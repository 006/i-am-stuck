"use client"
import { useState } from 'react';
import { redirect } from 'next/navigation'
import NewStuckForm from './new-stuck'
import env from "@beam-australia/react-env";

export default function NewButton({ session }) {
    const [isDialogOpen, setIsDialogOpen] = useState(false);
    const [myPosition, SetMyPosition] = useState({ lon: 0, lat: 0 });

    async function newStuck() {
        if (!session)
            redirect('/auth/login?screen_hint=signup')
        console.log(session.user.sub)//auth0|678fc69e258a386d6ac21cf2
        navigator.geolocation.getCurrentPosition(async (position) => {
            SetMyPosition({ lon: position.coords.longitude, lat: position.coords.latitude })
            setIsDialogOpen(true);
        });
    }

    const handleCloseDialog = () => {
        setIsDialogOpen(false);
        // setClickedMarker(null);
    };

    /**
      * localhost:8000 for Jetty-Jersy (Java) Restful API
      * localhost:8000 for FastAPI (Python) Restful API
      * localhost:8000 for Gin (Golang) Restful API
      * .env/REACT_APP_MIXED_URL=>Mixed service load balance to all above 3 implements
      */
    const handleFormSubmit = (formData) => {
        console.log("Form Data Submitted:", formData);
        fetch(env("MIXED_URL") + "/spot", {
            mode: 'cors',
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                'Authorization': 'Bearer ' + session.tokenSet.accessToken
            },
            body: new URLSearchParams({
                ...formData,
                openid: session.user.sub.replace("auth0|", "")
            }).toString()
        }).then(response => {
            const body = response.json();
            console.log("new stuck", body)
        }
        ).catch((err) => {
            console.log(err)
        });
        handleCloseDialog();
    };

    return (
        <div className="flex flex-col items-center">
            <button className="absolute z-10 bottom-1 h-12 w-12 rounded-full bg-black" onClick={newStuck}>
                <span className="material-icons md-48 green600">add</span>
            </button>
            {isDialogOpen && <NewStuckForm myPosition={myPosition} handleFormSubmit={handleFormSubmit} handleCloseDialog={handleCloseDialog} />}
        </div >
    );
}