import Foundation
import Alamofire

func sessionManagerFactory(timeout: Double, certForHost: String) -> SessionManager {
    let serverTrustPolicies: [String: ServerTrustPolicy] = [
        certForHost: .pinCertificates(
            certificates: ServerTrustPolicy.certificates(),
            validateCertificateChain: true,
            validateHost: false)
    ]
    
    let configuration = URLSessionConfiguration.default
    configuration.timeoutIntervalForRequest = timeout
    configuration.httpShouldSetCookies = true
    
    let sessionManager = SessionManager(
        configuration: configuration,
        serverTrustPolicyManager: ServerTrustPolicyManager(policies: serverTrustPolicies)
    )
    
    return sessionManager
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
        sessionManager.request(url, method: HTTPMethod(rawValue: method)!, parameters: body as? Parameters).responseData { (responseObject) -> Void in
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
