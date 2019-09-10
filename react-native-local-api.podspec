require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = "react-native-local-api"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.description  = <<-DESC
                  react-native-local-api
                   DESC
  s.homepage     = "https://github.com/kolohelios/react-native-local-api"
  s.license      = "MIT"
  s.license    = { :type => "MIT", :file => "LICENSE.md" }
  s.authors      = { "Jon Edwards" => "jkedwards@me.com" }
  s.platform     = :ios, "8.0"
  s.source       = { :git => "https://github.com/kolohelios/react-native-local-api.git", :tag => "#{s.version}" }

  s.source_files = "ios/**/*.{h,m,swift}"
  s.requires_arc = true

  s.dependency "React"
  s.dependency "Alamofire"
end

