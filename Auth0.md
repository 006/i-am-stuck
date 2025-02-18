# [Auth0](https://auth0.com/) Configration

## Create an APP (Regular Web Applications)

This part defines authentication related setting of you frontend application.

* **Settiing** > **Basic Information**
  * Domain (nextjs/.env.AUTH0_DOMAIN)
  * Client Id (nextjs/.env.AUTH0_CLIENT_ID)
  * Client Secret (nextjs/.env.AUTH0_CLIENT_SECRET)

* **App Secret** (nextjs/.env.AUTH0_SECRET)

```bash
openssl rand -hex 32
```

* **Settiing** > **Application Properties**

  * **Application Type**, set to **Regular Web Application**

* **Settiing** > **Application URIs**
  * Allowed Callback URLs

  ```text
  http://localhost:3000/auth/callback, http://your.domain/auth/callback
  ```

## Create an API

This part defines scopes, access token TTL for your resource server.

* **Identifier** (**audience** when request JWT access token)
* **Permission** (**scope** when request JWT access token)
* **Settiing** > **Access Token Settings**
  * Maximum Access Token Lifetime (xxx seconds until an access token issued for this API will expire.)
* **Settiing** > **Machine to Machine Applications**
Toggle **Authorized** on

## nextjs/.env.APP_BASE_URL

set to 'http://localhost:3000/' when develope, set 'http://your.domain/' when build to publish