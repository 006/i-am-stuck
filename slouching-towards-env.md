# Slouching

## Hardcoded

In the initial demo version, I hardcoded the Google Maps API key into the code and committed it to this public repository. I received an email from Google the following day stating, "We have detected a publicly accessible Google API key associated with the following Google Cloud Platform project: zzz."

I didn't directly use the JS version of the Google Maps SDK. Instead, I utilized a React library called "react-google-maps"[^1], which necessitates injecting my API key into the component before it initializes. This led me to another library, "beam-australia/react-env" [^2] (which took several hours to select). It includes a feature to "generate a **__ENV.js** file from multiple **.env** files that contains whitelisted environment variables that have a REACT_APP_ prefix" to enable client/browser access to these environment variables.

## Pack whole .env file to a secret

This step was straightforward and completed without any issues. The previous **.env** file is now located at the path: **/secret/.env**

## Command Not Found

The "beam-australia/react-env" [^2] library offers a way to generate the __ENV.js configuration file when the container starts, using the **Docker entrypoint**:

```bash
FROM node:20-alpine

ENTRYPOINT yarn react-env --env APP_ENV

CMD yarn start
```

This resulted in a **Command "react-env" not found** error in the frontend pod logs. The reason for this will be explained in the following section.

## No Fat Images

The official Next.js Dockerfile employs a multi-stage build process, dividing the packaging into different steps or layers. The final output is an image containing only the necessary code and resources for the application (without dependencies). This is the reason for the **Command "react-env" not found** error mentioned above.

While this issue could potentially be resolved by including those dependencies, it would significantly increase the size of the Docker image, from **198MB** to almost **800MB**.

I decided against pursuing this idea without even attempting it.

## Extra Code Snippet

Since I rejected the idea of creating a fat image, I explored whether there was a hook in **Next.js** that runs once when the server starts. I found one: [instrumentation.js](https://nextjs.org/docs/app/api-reference/file-conventions/instrumentation) which is used to integrate observability tools into applications.

I copied the code snippet from react-env that is responsible for generating the **__ENV.js** file. However, during the build stage, Webpack threw another error: **"Module not found: Can't resolve 'fs'"**.

## Webpack Error: Can't resolve 'fs' / 'path'

It appeared that I needed to modify the **next.config.js** settings to resolve this error [^3]. After changing my **next.config.js** to the following:

```javascript
import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  /* config options here */
  experimental: {
    // missingSuspenseWithCSRBailout: false,
    nodeMiddleware: true, // Enable Node.js middleware
  },
  output: "standalone",
  webpack: (config) => {
    config.resolve.fallback = { fs: false, path: false };
    return config;
  },
};

export default nextConfig;

```

The error disappeared, and I successfully built the Docker images. As is often the case, another error emerged upon deployment to the K8s cluster.

## Runtime Error: fs.readfilesync is not a function

This error was unexpected because the image was built based on **Node.js**, not **Edge** (Cloudflare Workers). However, I did observe the word **"edge"** in the pod logs. I then realized that this was likely due to the Auth0 middleware I had implemented to handle all authentication and authorization.

According to the Next.js documentation, Middleware functions default to using the **Edge** runtime, which does not support native Node.js APIs.

Fortunately, "As of v15.2 (canary), we have experimental support for using the Node.js runtime" [^4].

```JavaScript
export const config = {
    runtime: 'nodejs',
    // rest code of your middleware
};
```

After making all the necessary modifications and reconfiguring my project, the application finally ran without any errors. The following is a part of the log from the pod:

```bash
Writing runtime env to file:  /app/public/__ENV.js
```

Once again, things didn't go entirely smoothly. Google Maps ran in restricted mode because the API key was not correctly set.

Furthermore, I noticed a **404** error in the Chrome DevTools for the specific resource **__ENV.js**. What was going on?!

## File permissions

At this point, the thought occurred to me to check if the **__ENV.js** file was actually present on the file system.

```bash
kubectl exec -it stuck-nextjs-64c6f87898-ngl8d --ls /app/public
```

![FS](/assets/ls__ENV.png)

The output showed that the file **__ENV.js** belonged to **nogroup**, while other files belonged to **nodejs**. This difference in ownership might be the reason for the 404 error.

## A Empty Stub File

So, I decided to place an empty stub **__ENV.js** file inside the **public** folder before the build and package stage. This finally worked out.

This entire troubleshooting process was difficult and took a whole day, starting from the morning. The solution is somewhat inelegant but functional.

Moreover, I realized that the key to this solution is to **generate a downloadable JavaScript snippet from the mounted secret file**, including only the necessary information that the client/browser needs. I can write my own code to achieve this, which might be a future improvement.

[^1]: [react-google-maps](https://github.com/tomchentw/react-google-maps)
[^2]: [React Env - Runtime Environment Configuration](https://github.com/andrewmclagan/react-env#readme)
[^3]: [Module not found: Can't resolve 'fs' in Next.js application](https://stackoverflow.com/questions/64926174/module-not-found-cant-resolve-fs-in-next-js-application)
[^4]: [Middleware Runtime](https://nextjs.org/docs/app/building-your-application/routing/middleware#runtime)