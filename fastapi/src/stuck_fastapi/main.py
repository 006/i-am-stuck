import uuid
from typing import Annotated, Union, Dict, Any

from fastapi import Depends, FastAPI, Form, Security, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from sqlalchemy import URL
from sqlmodel import Field, Session, SQLModel, create_engine

from .config import get_settings
from .utils import VerifyToken, Geohash


class SpotLog(SQLModel, table=True):
    __tablename__ = "SK_LOG_SPOT"
    open_id: str | None = Field(default=None, primary_key=True)
    lon: float | None
    lat: float | None
    geo_hash: str | None
    id_spot: int = None
    content: str = None


class Spot(SQLModel, table=True):
    __tablename__ = "SK_SPOT"
    aiid: int | None
    unid: str | None = Field(default=None, primary_key=True)
    lon: float | None
    lat: float | None
    geo_hash: str | None
    open_id_stucker: str | None
    open_id_saver: str | None
    cellphone: str | None
    color_vehicle: str | None
    datime_last: str | None
    id_cvsn: str | None
    content: str | None


class SpotUpdate(SQLModel):
    datime_last: str | None
    bites: int | None
    bph: int | None
    cph: int | None


class SpotReport(SQLModel, table=True):
    __tablename__ = "NB_LOG_SPOT_REPORT"
    aiid: int | None = Field(default=None, primary_key=True)
    lon: float | None
    lat: float | None
    id_spot: int | None
    id_angler: int | None
    bites: int | None
    bph: int | None
    cph: int | None
    content: str | None


config = get_settings()

# billions = './stuck.properties'
# prop = Properties()
# with open(billions, 'rb') as config_file: prop.load(config_file)

conn_str = URL.create(
    "mariadb+mariadbconnector",
    host=config.db_stuck_host,
    username=config.db_stuck_user,
    password=config.db_stuck_passwd,
    database=config.db_stuck,
)
# engine = create_engine(conn_str) # turn off sql log
engine = create_engine(conn_str, echo=True)


def get_session():
    with Session(engine) as session:
        yield session


SessionDep = Annotated[Session, Depends(get_session)]

app = FastAPI()
auth = VerifyToken()

origins = [
    "https://192.168.6.*",
    "http://localhost:3000",
    "http://127.0.0.1",
    "http://localhost:8000",
]

app.add_middleware(
    CORSMiddleware,
    allow_origins=origins,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


@app.get("/")
def read_root():
    """TODO"""
    return {"Hello": "World"}


@app.get("/spot/{spot_unid}")
# def get_spot(spot_unid: str, session: SessionDep) -> Spot:
def get_spot(spot_unid: str, session: SessionDep) -> Dict[str, Any]:
    """get spot with unid"""
    spot = session.get(Spot, spot_unid)
    if not spot:
        raise HTTPException(status_code=404, detail="Spot not found")
    # return spot
    return {"maker":"fastapi", "unid": spot.unid, "lon": spot.lon, "lat": spot.lat, "openid_stucker": spot.open_id_stucker,
            "openid_saver": spot.open_id_saver, "geohash": spot.geo_hash, "phone": spot.cellphone,
            "conversation_id": spot.id_cvsn, "vehicle_color": spot.color_vehicle}


@app.put("/spot/{spot_id}")
def modify_spot(spot_id: int, q: Union[str, None] = None):
    """TODO"""
    return {"spot_id": spot_id, "q": q}


@app.post("/spot")
def create_stuck(openid: Annotated[str, Form()], lon: Annotated[float, Form()], lat: Annotated[float, Form()],
                 vehicle_color: Annotated[str, Form()], phone: Annotated[str, Form()],
                 description: Annotated[str, Form()], session: SessionDep,
                 payload: str = Security(auth.verify, scopes=["spot"])) -> Dict[str, Any]:
    """Report a new stuck"""
    # print(payload)
    print(openid, lon, lat, vehicle_color, phone, description)

    geohash = Geohash.encode(lat, lon, precision=9)

    spot = Spot()
    spot.unid = uuid.uuid4().hex.upper()
    spot.open_id_stucker = openid
    spot.lon = lon
    spot.lat = lat
    spot.geo_hash = geohash
    spot.color_vehicle = vehicle_color
    spot.cellphone = phone
    spot.content = description

    session.add(spot)
    session.commit()
    # why does the DATIME_LAST have a actual value? not '0000-00-00 00:00:00'
    session.refresh(spot)
    print("AIID: ", spot.aiid)

    # FIXME log to SK_LOG_SPOT
    log = SpotLog()
    log.open_id = openid
    log.lon = lon
    log.lat = lat
    log.geo_hash = geohash
    log.id_spot = spot.aiid
    log.content = "Report stuck at: " + geohash
    session.add(log)
    session.commit()

    return {"ok": True}


def create_spot_report(spot_id: str, patch: SpotUpdate, session: SessionDep,
                       payload: str = Security(auth.verify, scopes=["spot"])):
    """Zzz"""


@app.post("/spot/{spot_id}/report")
def create_spot_report(spot_id: str, patch: SpotUpdate, session: SessionDep,
                       payload: str = Security(auth.verify, scopes=["spot"])):
    """A valid access token is required to access this route"""
    print(payload)
    spot = session.get(Spot, spot_id)
    if not spot:
        raise HTTPException(status_code=404, detail="Spot not found")
    spot_data = patch.model_dump(exclude_unset=True)
    spot.sqlmodel_update(spot_data)
    session.add(spot)
    session.commit()
    session.refresh(spot)
    # FIXME use server time instead of client time to update DB
    # FIXME add new record to table NB_LOG_SPOT_REPORT
    # return {"spot_id": spot_id}
    return {"ok": True}
