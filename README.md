# JuriEdu AWS Lambda Serverless

## Dependencies

- docker
- serverless

## Build

```
$ mvn clean package -Dnative=true -Dnative-image.docker-build=true
```

## Run Elasticsearch

```
$ cd docker/elasticsearch
$ docker-compose up -d
```

## Invoke locally

With JVM:
```
$ sls invoke local --docker --docker-arg='--network host' -f searchJvm --path search-payload.json
$ sls invoke local --docker --docker-arg='--network host' -f dictionaryJvm --path dictionary-payload.json
```
