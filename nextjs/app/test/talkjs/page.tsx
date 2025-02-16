"use client"

import { useCallback } from "react";
import Talk from "talkjs";
import { Session, Chatbox } from "@talkjs/react";
import env from "@beam-australia/react-env";

function TalkJSChat() {
  const syncUser = useCallback(
    () =>
      new Talk.User({
        id: "nina",
        name: "Nina",
        email: "nina@example.com",
        photoUrl: "https://talkjs.com/new-web/avatar-7.jpg",
        welcomeMessage: "Hi!",
      }),
    []
  );

  const syncConversation = useCallback((session) => {
    // JavaScript SDK code here
    const conversation = session.getOrCreateConversation("cbfgvdtj1_ttron57");
    const other = new Talk.User({
      id: "frank",
      name: "Frank",
      email: "frank@example.com",
      photoUrl: "https://talkjs.com/new-web/avatar-8.jpg",
      welcomeMessage: "Hey, how can I help?",
    });
    conversation.setParticipant(session.me);
    conversation.setParticipant(other);
    return conversation;
  }, []);

  console.log(env("TALKJS_APP_ID"))
  return (
    <Session appId={env("TALKJS_APP_ID")} syncUser={syncUser}>
      <Chatbox syncConversation={syncConversation}
        style={{ width: "100%", height: "86vh" }}
      />
    </Session>
  );
}

export default TalkJSChat;