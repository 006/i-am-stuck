import { Suspense } from "react";
import TalkJSChatBox from "@/components/chat-box";

export default function TalkJSChat() {
    return (
        <Suspense>
            <TalkJSChatBox />
        </Suspense>
    );
}