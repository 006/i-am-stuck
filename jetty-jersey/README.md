# APIs writen in Java (Jetty and Jersy)

## Installation

### Build a jar with maven

```bash
mvn -DskipTests package -U
```

### Prepare necessary files for docker image

```bash
zzz/
├── Dockerfile
├── io.onme.stuck-jetty-jersey-0.1.0-SNAPSHOT-jar-with-dependencies.jar
├── stuck.properties
```

##  Dockerize

### Perform a [maven build](#build-a-jar-with-maven) first

### Build a docker image

```bash
cp ./k8s/Dockerfile ./

docker build -t [your.private.registry]:5000/[zzz]/stuck-jetty-jersey:0.1.0 .

docker images

docker run -p 8000:8000 [your.private.registry]:5000/[zzz]/stuck-jetty-jersey:0.1.0

```

### Push to private docker registry for K8S use

 ```bash
 docker push [your.private.registry]:5000/[zzz]/stuck-jetty-jersey:0.1.0
 ```

### Copy k8s yaml to k8s cluster and deploy

 ```bash
 kubectl create -f stuck-jetty-jersey.yaml
 ```
