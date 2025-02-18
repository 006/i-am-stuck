"use client"

import React, { useCallback, useState } from 'react'
import { GoogleMap, useJsApiLoader, MarkerF, OverlayViewF, OVERLAY_MOUSE_TARGET } from '@react-google-maps/api'
import env from "@beam-australia/react-env";
import Overlay from '@/components/map-overlay';
import { getStatusIcon } from "@/lib/miscellaneous";
import { Spot } from '@/lib/types';

const containerStyle = {
  width: "100%",
  height: "100%"
}

const center = {
  lat: 49.890506,
  lng: -97.142026,
}


function MyComponent({ session }) {
  const { isLoaded } = useJsApiLoader({ id: 'google-map-script', googleMapsApiKey: env("GOOGLE_MAP_API_KEY") })

  const [map, setMap] = useState(null)

  const [currentSpot, setCurrentSpot] = useState(null)

  const [spots, setSpots] = useState(null)

  const [overlayPane, setOverlayPane] = useState(OVERLAY_MOUSE_TARGET)

  const [isShown, setIsShown] = useState(false)

  const changeIsShown = useCallback((spot: Spot) => {
    setCurrentSpot(spot)
    setIsShown(!isShown)
  }, [isShown])

  async function fetchSpots(bounds, limit) {
    const northEast = bounds.getNorthEast();
    const southWest = bounds.getSouthWest();
    const result = await fetch(env("JERSEY_URL") + "/spot?" + new URLSearchParams({
      west: southWest.lng(),
      east: northEast.lng(),
      north: northEast.lat(),
      south: southWest.lat(),
      limit: limit ?? 10
    }).toString(), {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        // 'X-Shopify-Storefront-Access-Token': key,
      }
    });

    const body = await result.json();

    if (body.errors) {
      throw body.errors[0];
    }
    // console.log(body)
    setSpots(body)
  }

  const onLoad = useCallback(function callback(map: google.maps.Map) {
    setMap(map)
  }, [])

  const onUnmount = useCallback(function callback(map: google.maps.Map) { setMap(null) }, [])

  const boundsChanged = useCallback(function callback() {
    if (map) {
      const bounds = map.getBounds();
      fetchSpots(bounds, 20)
    }
  }, [map])

  function centerOverlayView(width: number, height: number): { x: number, y: number } {
    return { x: -(width / 2), y: -(height / 2), }
  }

  return isLoaded ? (
    <GoogleMap
      mapContainerStyle={containerStyle}
      center={center}
      zoom={15}
      onLoad={onLoad}
      onUnmount={onUnmount}
      onDragEnd={boundsChanged}
      onZoomChanged={boundsChanged}
    >
      { /* Child components, such as markers, info windows, etc. */}
      {spots?.map((spot) => (
        <MarkerF key={spot.unid} position={{ lat: spot.lat, lng: spot.lon }} icon={getStatusIcon(spot.state)} onClick={() => changeIsShown(spot)} />
      ))}

      {isShown ? (
        <OverlayViewF
          position={{ lat: currentSpot.lat, lng: currentSpot.lon }}
          mapPaneName={overlayPane}
          // onLoad={loadCallback}
          // onUnmount={unmountCallback}
          getPixelPositionOffset={centerOverlayView}
        >
          <Overlay spot={currentSpot} changeIsShown={changeIsShown} session={session} />
        </OverlayViewF>
      ) : null}
      <></>
    </GoogleMap>
  ) : <></>
}

const GMap = React.memo(MyComponent)

export default GMap