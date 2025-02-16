"use client"
import { useSearchParams } from 'next/navigation'
import { useCallback } from "react";
import Talk from "talkjs";
import { Session, Chatbox } from "@talkjs/react";
import env from "@beam-australia/react-env";

export default function TalkJSChat() {
    const searchParams = useSearchParams()
    const conversationId = searchParams.get('conversation')

    const syncUser = useCallback(
        () => new Talk.User({
            id: searchParams.get('openid'),
            name: searchParams.get('alias'),
            // email: "nina@example.com",
            photoUrl: searchParams.get('avatar'),
            welcomeMessage: "Hi!",
            role: "saver"
        }), [searchParams]
    );

    const syncConversation = useCallback((talkjsSession) => {
        const conversation = talkjsSession.getOrCreateConversation(conversationId);
        conversation.setParticipant(talkjsSession.me);
        return conversation;
    }, [conversationId]);

    return (
        <Session appId={env("TALKJS_APP_ID")} syncUser={syncUser}>
            <Chatbox syncConversation={syncConversation} style={{ width: "100%", height: "86vh" }} />
        </Session>
    );
}