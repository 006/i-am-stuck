# Decouple environment-specific configuration from container images

## Use ConfigMaps store non-confidential data

A ConfigMap is an API object used to store non-confidential data in key-value pairs. Pods can consume ConfigMaps as environment variables, command-line arguments, or as configuration files in a volume.

## Use Secrets store confidential data

A Secret is an object that contains a small amount of sensitive data such as a password, a token, or a key.
