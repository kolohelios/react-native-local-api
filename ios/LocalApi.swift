import Foundation
import Alamofire

func sessionManagerFactory(timeout: Double, certForHost: String) -> SessionManager {
    let serverTrustPolicies: [String: ServerTrustPolicy] = [
        certForHost: .pinCertificates(
            certificates: ServerTrustPolicy.certificates(),
            validateCertificateChain: true,
            validateHost: false)
    ]

    var headers = SessionManager.defaultHTTPHeaders
    headers["Content-Type"] = "application/json"

    let configuration = URLSessionConfiguration.default
    configuration.timeoutIntervalForRequest = timeout
    configuration.httpShouldSetCookies = true
    configuration.httpAdditionalHeaders = headers

    let sessionManager = SessionManager(
        configuration: configuration,
        serverTrustPolicyManager: ServerTrustPolicyManager(policies: serverTrustPolicies)
    )

    return sessionManager
}

// Objective C converts booleans to ints by design.  If your API isn't expecting that, this can be a problem.  Use this function to reverse that for any params listed in the convertTheseKeysToBooleans array.
func convertBooleans(params: Dictionary<AnyHashable, Any>) -> Dictionary<AnyHashable, Any> {
	let convertTheseKeysToBooleans: [String] = ["instantActive", "enable", "auto"]
	func recurse(sourceObject: Dictionary<AnyHashable, Any>) -> Dictionary<AnyHashable, Any> {
		var newObject: Dictionary<AnyHashable, Any> = [:]
		for (key, value) in sourceObject {
			if convertTheseKeysToBooleans.contains(key as! String) {
				newObject[key] = value as! Int == 1 ? true : false
			} else if value is [Dictionary<AnyHashable, Any>] {
				newObject[key] = (value as! [Dictionary<AnyHashable, Any>]).map { recurse(sourceObject: $0) }
			} else {
				newObject[key] = value
			}
		}
		return newObject
	}
	return recurse(sourceObject: params)
}

@objc(LocalApi)
class LocalApi: NSObject {
    // 60 seconds is the default timeout in iOS
    var timeout = 60 as Double
    var localNetworkHost = "192.168.1.1"
    var sessionManager = sessionManagerFactory(timeout: 60, certForHost: "192.168.1.1")

    @objc func apiRequest(_ url: String, method: String, body: Dictionary<AnyHashable, Any>, setCookie: Bool,
        resolver resolve: @escaping RCTPromiseResolveBlock,
        rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {

        let url = NSURL(string: url as String)
        var request = URLRequest(url: url! as URL)
        request.httpMethod = method
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        if (method == "POST") {
            let data = try! JSONSerialization.data(withJSONObject: convertBooleans(params: body), options: JSONSerialization.WritingOptions.prettyPrinted)
            let json = NSString(data: data, encoding: String.Encoding.utf8.rawValue)
            request.httpBody = json!.data(using: String.Encoding.utf8.rawValue)
        }

        sessionManager.request(request).responseData { (responseObject) -> Void in
            guard case let .failure(error) = responseObject.result else { return }

            if let error = error as? AFError {
                switch error {
                case .invalidURL(let url):
                    reject("Invalid URL: \(url)", error.localizedDescription, error)
                case .parameterEncodingFailed(let reason):
                    reject("Parameter encoding failed with reason: \(reason)", error.localizedDescription, error)
                case .multipartEncodingFailed(let reason):
                    reject("Multipart encoding failed with reason: \(reason)", error.localizedDescription, error)
                case .responseValidationFailed(let reason):
                    reject("Response validation failed with reason: \(reason)", error.localizedDescription, error)
                case .responseSerializationFailed(let reason):
                    reject("Response serialization failed with reason: \(reason)", error.localizedDescription, error)
                }
            } else if let error = error as? URLError {
                reject("URLError occurred", error.localizedDescription, error)
            } else {
                reject("Unknown error", error.localizedDescription, error)
            }

        }.responseString { (responseString) -> Void in
            guard case .success = responseString.result else { return }

            if setCookie {
                self.sessionManager.session.configuration.httpCookieStorage?.setCookie(HTTPCookieStorage.shared.cookies![0])
            } else {
                self.sessionManager.session.configuration.httpShouldSetCookies = false
            }

            if (responseString.value != nil) {
                resolve(responseString.value)
            }
        }
    }

    @objc func clearCookies() -> Void {
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy/MM/dd HH:mm"
        let arbitraryEarlyDate = formatter.date(from: "1970/01/01 00:00")!
        sessionManager.session.configuration.httpCookieStorage?.removeCookies(since: arbitraryEarlyDate)
        HTTPCookieStorage.shared.cookies?.forEach(HTTPCookieStorage.shared.deleteCookie)
    }

    @objc func pinCertificate(_ hostname: String, publicKeys: [String], verificationURL: String, resolver resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        sessionManager = sessionManagerFactory(timeout: timeout, certForHost: hostname)
        // TODO add a check using apiRequest to ensure the pinning has been successful (or if it was unnecesary)
        resolve(true)
    }

    // TODO get this working by reassigning a new instance of sessionManager that includes new timeouts
    @objc func setTimeout(_ newTimeout: Double) -> Void {
        sessionManager = sessionManagerFactory(timeout: newTimeout, certForHost: localNetworkHost)
    }
}
