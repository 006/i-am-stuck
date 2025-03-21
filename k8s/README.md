# Use ConfigMap and Secrets decouple with Docker Images

## Create ConfigMap

## Create Secret

All value of any secret key shoud be encoded to **Base64** string, before put into K8S as secret

```bash
echo -n 'tyvVAWHh' |base64
# dHl2VkFXSGg=

```


## Debug with [Kuard](https://github.com/kubernetes-up-and-running/kuard)

Kuard is a very interesting application I found when I read the book "Kubernetes Up and Running"[^1]

```bash
kubectl create -f kuard-for-debug.yml

kubectl port-forward -n onme kuard-stuck 8080 --address='0.0.0.0'

```

Open a browser and access **http://IP:8080**, You can see Server Env and File system.
Base on your pod manifest, ConfigMap and Secret would mounted to the path you pointed

[^1]: [Kubernetes Up and Running](https://www.oreilly.com/library/view/kubernetes-up-and/9781491935668/)
