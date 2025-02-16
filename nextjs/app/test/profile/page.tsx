"use client"

import { useUser } from "@auth0/nextjs-auth0"

export default function Profile() {
    const { user, isLoading, error } = useUser()

    if (isLoading) return <div>Loading...</div>

    return (
        <main>
            <h1>Profile</h1>
            <div>
                <pre>{JSON.stringify(user, null, 2)}</pre>
            </div>
        </main>
    )
}