import geohash from 'ngeohash'
import Link from 'next/link';

export default function Geo({ lon, lat }) {
    const gh = geohash.encode(lat, lon);
    return (
        <div className="font-mono flex flex-row items-center">
            <div className="object-center">
                <span className="material-icons md-48 text-red-600">tag</span>
            </div>
            <div>
                <p className="text-xl font-semibold">Geohash:<Link className="text-red-600 hover:underline" target="_blank" href={"https://geohash.softeng.co/" + gh}>{gh}</Link></p>
                <p className="text-sm">({lat},{lon})</p>
            </div>
        </div>
    );
}