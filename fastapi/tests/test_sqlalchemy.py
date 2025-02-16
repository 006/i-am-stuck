from jproperties import Properties
from sqlalchemy import create_engine, URL, text

if __name__ == '__main__':
    billions = './stuck.properties'
    prop = Properties()
    with open(billions, 'rb') as config_file:
        prop.load(config_file)

    conn_str = URL.create(
        "mariadb+mariadbconnector",
        username=prop.get("db.stuck.user").data,
        password=prop.get("db.stuck.passwd").data,
        host=prop.get("db.stuck.host").data,
        database=prop.get("db.stuck").data,
    )
    engine = create_engine(conn_str)

    with engine.connect() as conn:
        result = conn.execute(text("SELECT id, x, y FROM test.zzz"))
        for row in result:
            print(f"id: {row.id}, x: {row.x}  y: {row.y}")
