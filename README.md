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

## Run local SAM server

```
$ sam local start-api --docker-network host
```
