docker pull repairnator/pipeline
docker pull repairnator/sequencer:2.0
docker pull antonw/activemq-jmx:latest
docker run -d --net=host antonw/activemq-jmx:latest
docker ps -a