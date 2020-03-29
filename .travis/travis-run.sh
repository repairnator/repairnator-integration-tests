set -e
export M2_HOME=/usr/local/maven

mvn clean test -B
