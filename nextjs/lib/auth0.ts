import { Auth0Client } from "@auth0/nextjs-auth0/server";

export const auth0 = new Auth0Client({
    routes: {
        login: "/auth/login",
        logout: "/auth/logout",
        callback: "/auth/callback",
        backChannelLogout: "/auth/backchannel-logout",
    },
    authorizationParameters: {
        scope: "openid profile email spot report chat",// APIs Permissions tab
        audience: "io.onme.stuck",//APIs identifier field
    },
    async beforeSessionSaved(session, idToken) {
        return {
            ...session,
            user: {
                ...session.user,
                idToken: idToken,
            },
        }
    },
});