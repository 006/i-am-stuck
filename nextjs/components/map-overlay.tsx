import Image from "next/image";
import { format } from 'timeago.js';
import { useCallback, useState } from 'react'
import { redirect, useRouter } from 'next/navigation'
import { SessionData } from "@auth0/nextjs-auth0/types";
import Geo from './geo';
import { getStatusIcon, getStatusText } from "@/lib/miscellaneous";
import env from "@beam-australia/react-env";


export default function Overlay({ spot, changeIsShown, session }: { spot: any, changeIsShown: boolean, session: SessionData }) {
    const router = useRouter();
    const joinConversation = useCallback(async () => {
        if (!session)
            redirect('/auth/login')
        const openId = session.user.sub.replace("auth0|", "");
        console.log("exist conversation", spot.conversation_id)
        fetch(env("JERSEY_URL") + "/talkjs/" + spot.conversation_id, {
            mode: 'cors',
            method: 'PUT',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                'Accept': 'application/json',
                'Authorization': 'Bearer ' + session.tokenSet.accessToken
            },
            body: new URLSearchParams({
                spot_unid: spot.unid,
                // geohash: spot.geohash,
                openid: openId,
                alias: session.user.nickname,
                avatar: session.user.picture,
                email: session.user.email,
            }).toString()
        }).then(response => {
            const body = response.json();
            // console.log("new conversation", body)
            return body;
        }).then((data) => {
            // console.log("new conversation", data)
            router.push('/talkjs?' + new URLSearchParams({
                conversation: data.context.value,
                openid: openId,
                alias: session.user.nickname,
                avatar: session.user.picture,
            }).toString())
        }).catch((err) => {
            console.log(err)
        });
    }, [spot, session, router])

    const newConversation = useCallback(async () => {
        if (!session)
            redirect('/auth/login')
        const openId = session.user.sub.replace("auth0|", "");
        fetch(env("JERSEY_URL") + "/talkjs", {
            mode: 'cors',
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                'Accept': 'application/json',
                'Authorization': 'Bearer ' + session.tokenSet.accessToken
            },
            body: new URLSearchParams({
                spot_unid: spot.unid,
                geohash: spot.geohash,
                openid: openId,
                alias: session.user.nickname,
                avatar: session.user.picture,
                email: session.user.email,
            }).toString()
        }).then(response => {
            const body = response.json();
            // console.log("new conversation", body)
            return body;
        }).then((data) => {
            // console.log("new conversation", data)
            router.push('/talkjs?' + new URLSearchParams({
                conversation: data.context.value,
                openid: openId,
                alias: session.user.nickname,
                avatar: session.user.picture,
            }).toString())
        }).catch((err) => {
            console.log(err)
        });
    }, [spot, session, router])


    function formatPhoneNumber(phoneNumberString) {
        const cleaned = ('' + phoneNumberString).replace(/\D/g, '');
        const match = cleaned.match(/^(\d{3})(\d{3})(\d{4})$/);
        if (match)
            return '(' + match[1] + ') ' + match[2] + '-' + match[3];
        return null;
    }

    return (
        <div className="bg-white p-6 border-0 border-black rounded-md" >
            <button className="absolute top-2 right-2 text-gray-500 hover:text-gray-700" onClick={() => changeIsShown(spot)}>
                <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M6 18L18 6M6 6l12 12" />
                </svg>
            </button>
            <h1 className="text-lg text-center">{spot.name}</h1>
            <Geo lon={spot.lon} lat={spot.lat} />
            <div className="flex-grow border-t border-gray-400" />
            <div className="flex flex-row">
                <div className="content-center">
                    <Image src={getStatusIcon(spot.state)} alt="status" width="48" height="48" />
                </div>
                <div className="ml-2 content-center">
                    <p className="text-lg align-middle">{getStatusText(spot.state)}</p>
                </div>
            </div>
            <div>
                <p className="text-base">Phone: {formatPhoneNumber(spot.phone)}</p>
            </div>
            <div className="flex-grow border-t border-gray-400" />
            <div className="flex items-center my-1">
                <span className="material-icons">update</span>
                <span className="text-sm">&nbsp;{format(spot.epoch_last)}&nbsp;by&nbsp;</span>
                <span className="text-sm font-medium">Gavin Belson</span>
            </div>
            <div className="flex flex-row">
                {spot.conversation_id === undefined ?
                    (<button className="flex-1 block h-10 text-lg text-white bg-blue-700 rounded-md hover:bg-blue-800" onClick={newConversation}>New chat</button>) :
                    (<button className="flex-1 block h-10 text-lg text-white bg-blue-700 rounded-md hover:bg-blue-800" onClick={joinConversation}>Join chat</button>)
                }
            </div>
        </div>
    );
}