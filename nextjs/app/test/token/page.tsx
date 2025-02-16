"use client"

import { getAccessToken } from "@auth0/nextjs-auth0"

export default function Component() {
    async function fetchData() {
        try {
            const token = await getAccessToken()
            console.log(token)
            // call external API with token...
        } catch (err) {
            // err will be an instance of AccessTokenError if an access token could not be obtained
            console.log(err)
        }
    }

    return (
        <main>
            <button onClick={fetchData}>Fetch Data</button>
        </main>
    )
}