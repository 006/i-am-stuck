docker build -t registry.tsst.xyz:5000/onme/stuck-fastapi:0.1.0 .
docker push registry.tsst.xyz:5000/onme/stuck-fastapi:0.1.0

docker run -p 8000:8000 registry.tsst.xyz:5000/onme/stuck-fastapi:0.1.0