# JuriEdu AWS Lambda Serverless

## Dependencies

- docker
- serverless

## Build

```
$ mvn clean package

```

## Run Elasticsearch & kibana

```
$ docker-compose up -d
```

## Invoke locally

With JVM:
```
$ sls invoke local --docker --docker-arg='--network host' -f searchJvm --path search-payload.json
$ sls invoke local --docker --docker-arg='--network host' -f dictionaryJvm --path dictionary-payload.json
```
