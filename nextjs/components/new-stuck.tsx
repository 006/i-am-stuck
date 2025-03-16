import { useState } from 'react'
import Geo from './geo';

export default function NewStuckForm({ myPosition, handleFormSubmit, handleCloseDialog }) {
    const [formData, setFormData] = useState({
        lon: 0,
        lat: 0,
        vehicle_color: '',
        phone: '',
        description: '',
    });

    const handleChange = (event) => {
        const { name, value } = event.target;
        setFormData(prevState => ({ ...prevState, [name]: value }));
    };

    return (
        <div className="absolute w-full h-screen bg-black bg-opacity-50" style={{ top: 0, left: 0, display: 'flex', justifyContent: 'center', alignItems: 'center', zIndex: 1001 }}>
            <div className="relative dialog-content" style={{ backgroundColor: 'white', padding: '20px', borderRadius: '5px' }}>
                <button className="absolute  top-2 right-2 text-gray-500 hover:text-gray-700" onClick={handleCloseDialog}>
                    <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M6 18L18 6M6 6l12 12" />
                    </svg>
                </button>
                <h1 className="text-xl font-bold mb-2">Stuck, need help!</h1>
                {myPosition && (<Geo lon={myPosition.lon} lat={myPosition.lat} />)}
                <form className="mt-2" onSubmit={(e) => {
                    e.preventDefault();
                    formData.lon = myPosition.lon;
                    formData.lat = myPosition.lat;
                    console.log(formData);
                    handleFormSubmit(formData);
                }}>
                    <div className="md:flex md:items-center mb-6">
                        <div className="md:w-1/3">
                            <label className="block text-gray-500 font-bold md:text-right mb-1 md:mb-0 pr-4" htmlFor="vehicle-color">Vehicle Color</label>
                        </div>
                        <div className="md:w-2/3">
                            <input className="bg-gray-200 appearance-none border-2 border-gray-200 rounded-xs w-full py-2 px-4 text-gray-700 leading-tight focus:outline-hidden focus:bg-white focus:border-blue-700"
                                id="vehicle-color" name="vehicle_color" type="text" placeholder="Black" value={formData.vehicle_color} onChange={handleChange} />
                        </div>
                    </div>
                    <div className="md:flex md:items-center mb-6">
                        <div className="md:w-1/3">
                            <label className="block text-gray-500 font-bold md:text-right mb-1 md:mb-0 pr-4" htmlFor="phone-number">Phone</label>
                        </div>
                        <div className="md:w-2/3">
                            <input className="bg-gray-200 appearance-none border-2 border-gray-200 rounded-xs w-full py-2 px-4 text-gray-700 leading-tight focus:outline-hidden focus:bg-white focus:border-blue-700"
                                id="phone-number" name="phone" type="text" placeholder="123-123-1234" value={formData.phone} onChange={handleChange} />
                        </div>
                    </div>
                    <div className="md:flex md:items-center mb-6">
                        <div className="md:w-1/3">
                            <label className="block text-gray-500 font-bold md:text-right mb-1 md:mb-0 pr-4" htmlFor="description">Description</label>
                        </div>
                        <div className="md:w-2/3">
                            <textarea className="bg-gray-200 appearance-none border-2 border-gray-200 rounded-xs w-full py-2 px-4 text-gray-700 leading-tight focus:outline-hidden focus:bg-white focus:border-blue-700"
                                id="description" name="description" placeholder="Black F150 stuck on crack" value={formData.description} onChange={handleChange} />
                        </div>
                    </div>
                    <div className="flex flex-row">
                        <button type="submit" className="flex-1 block h-10  text-white bg-blue-700 rounded-md hover:bg-blue-800">Submit</button>
                        <button className="flex-1 ml-1 block h-10 text-white bg-blue-700 rounded-md hover:bg-blue-800" onClick={handleCloseDialog}>Cancel</button>
                    </div>
                </form>
            </div>
        </div >
    );
}