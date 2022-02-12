vr=$(docker-compose ps | grep demo)

if [ ! -z "$vr" ];
then
    docker-compose down --remove-orphans;
fi

vr=$(docker images | grep demo)

if [ ! -z "$vr" ];
then
    docker image rm -f demoapi:0.0.1-SNAPSHOT;
fi

mvn package
docker build -t demoapi:0.0.1-SNAPSHOT .
docker-compose up -d
