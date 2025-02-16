# I Am Stuck (on the ice)

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT) [![Last Commit](https://img.shields.io/github/last-commit/006/i-am-stuck)](https://github.com/006/i-am-stuck)

## Overview

"I Am Stuck (on the ice)" is a full-stack web application inspired by "The Lake Winnipeg Report" Facebook group and my personal experience of getting stuck on the ice while ice fishing.  This project aims to improve the efficiency and safety of ice rescue efforts.  Currently, people rely on posting in the Facebook group, which can be slow, lack critical information, and expose personal details.  "I Am Stuck" provides a dedicated platform for reporting incidents, sharing location information, and connecting rescuers with those in need, all while preserving privacy.  This project demonstrates my proficiency in a wide range of frontend and backend technologies, as well as my ability to integrate various SaaS platforms.

## Features

* **Report a Stuck Case:**
  * Requests the user's current position using WGS84 coordinates.
  * Provides a form for submitting essential information about the incident.
  * Converts decimal WGS84 coordinates into Geohash for easy sharing via text or voice.
* **Browse cases on Google Maps:**
  * Displays reported incidents on a map using different icons to indicate the status (e.g., stuck, in talk, saved).
* **Private Chat:**
  * Facilitates direct communication between the stuck individual and rescuers via a private chat using the TalkJS SDK and RESTful API, protecting personal information.
* **Authentication and Authorization:**
  * Secure user authentication and authorization using Auth0.
  * Implements JWT (JSON Web Token) based access token generation and verification for secure API access.

## Technologies Used

* **Infrastructure:** RHEL, Kubernetes (on-premises), Docker/Podman, Helm, MetalLB, Nginx (ingress), PM2
* **Frontend:** React, Next.js, Tailwind CSS, CORS
* **Backend:** FastAPI (Python), Jetty, Jersey (Java)
* **Database/Caching:** MariaDB, Redis, SQLAlchemy/SQLModel, GraphQL
* **Packaging/Dependencies:** Webpack, Maven, pip, setuptools, Git, dnf, apt-get
* **Languages/Scripts:** Java, Python, JavaScript, CSS, SQL, Shell, YAML, Markdown
* **Standards**: OAuth 2 (RFC 6749), JWT (RFC 7519), Geohash, Restful
* **Cloud/SaaS:** Google Fonts, Google Maps API, Auth0, TalkJS, GitHub, Cloudflare

## Breakdown

Detailed build and deploy instructions are included in the *README.md* files within each module.

* **nextjs**: frontend (build on Nextjs)
  * Hides confidential configration by moving them from code to *.env* file.
  * Uses Google Fonts.
  * Uses Google Maps API.
  * Redircts to Auth0 for authentication and authorization.
* **gatsby**: frontend (build on Gatsby)
  * Uses GraphQL
  * Uses Google Maps API.
* **fastapi**: backend (Python implemented)
  * Supports CORS.
  * Decodes and verifies JWTs.
  * Performs MariaDB CRUD operations.
  * Encodes Geohashes.
  * Uses SQLModel for interacting with the RDBMS.
* **jetty-jersey**: backend (Java implemented)
  * Embedded Jetty Server.
  * Supports CORS.
  * Performs MariaDB CRUD operations.
  * Encodes Geohashes.
  * Uses Redis expiration to kick out old access tokens.
  * Decodes and verifies JWTs.

## Prerequisites

For information on setting up the infrastructure components (OS, RDBMS, Kubernetes, Redis), please refer to my other repository: [All On Premises](https://github.com/006/all-on-premises).

For details on OAuth 2 and Auth0 concepts and demonstrations, please see my other repository: [Authentication and Authorization with Auth0](https://github.com/006/aaa0).
