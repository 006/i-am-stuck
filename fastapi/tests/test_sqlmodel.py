from jproperties import Properties
from sqlalchemy import URL
from sqlmodel import Field, Session, SQLModel, create_engine, select


class Zzz(SQLModel, table=True):
    id: int | None = Field(default=None, primary_key=True)
    x: int | None = None
    y: int | None = None


billions = './stuck.properties'
prop = Properties()
with open(billions, 'rb') as config_file:        prop.load(config_file)

conn_str = URL.create(
    "mariadb+mariadbconnector",
    username=prop.get("db.stuck.user").data,
    password=prop.get("db.stuck.passwd").data,
    host=prop.get("db.stuck.host").data,
    database=prop.get("db.stuck").data,
)
engine = create_engine(conn_str)


def select_heroes():
    with Session(engine) as session:
        statement = select(Zzz)
        results = session.exec(statement)
        for hero in results:
            print(hero)


def main():
    select_heroes()


if __name__ == '__main__':
    main()
