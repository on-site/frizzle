require "open3"

def maven_version
  output, status = Open3.capture2e("git describe --abbrev=0 --tags")
  return "0.0.1-SNAPSHOT" unless status.success?
  match = /\Av(\d.*)$/.match(output)
  return "0.0.1-SNAPSHOT" unless match
  version = match[1]
  output, status = Open3.capture2e("git describe --exact-match --tags HEAD")
  return "#{version}-SNAPSHOT" unless status.success?
  return "#{version}-SNAPSHOT" unless output.strip == "v#{version}"
  return version
end
