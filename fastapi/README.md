# FastAPI

## Dockerfile highlignts
* Install MariaDB Connector/C

```
RUN apt-get update && apt-get install -y libmariadb3 libmariadb-dev gcc
```

* Run code from wheel package instead of xxx.py

```
RUN pip install --no-cache-dir /app/*.whl
CMD ["uvicorn", "stuck_fastapi.main:app", "--host", "0.0.0.0", "--port", "8000"]
```

## Installation

* Build a wheel package

```
python -m build
```

* Prepare necessary files for docker image

```
zzz/
├── Dockerfile
├── requirements.txt
├── stuck.properties
├── dist/
│   └── stuck_fastapi-0.0.1-py3-none-any.whl
```

* Build a docker image

```
docker build -t [your.private.registry]:5000/[zzz]/stuck-fastapi:0.1.0 .
docker run -p 8000:8000 [your.private.registry]:5000/[zzz]/stuck-fastapi:0.1.0
```

* Push to private docker registry for K8S use

 ```
 docker push [your.private.registry]:5000/[zzz]/stuck-fastapi:0.1.0
 ```

* Copy k8s yaml to k8s cluster and deploy

 ```
 kubectl create -f stuck-fastapi.yaml
 ```
