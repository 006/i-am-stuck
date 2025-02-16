import GMap from "../components/gMap";
import { auth0 } from "@/lib/auth0";
import NewButton from "@/components/new-button";

const containerStyle = {
    width: '100%',
    height: '100vh'
}


export default async function Home() {
    const session = await auth0.getSession();


    return (
        <div className="">
            <main className="relative mlon-auto p-0 border-2 border-indigo-600" style={containerStyle}>
                <NewButton session={session} />
                <GMap session={session} />
            </main>
        </div>
    );
}