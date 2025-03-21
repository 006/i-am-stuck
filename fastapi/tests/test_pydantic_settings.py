from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings, case_sensitive=True):
    model_config = SettingsConfigDict(env_file_encoding='utf-8', extra='ignore',
                                      env_file=('../config/.env', '../config/stuck.properties'))
    db_stuck: str
    db_stuck_host: str
    db_stuck_port: int
    db_stuck_user: str
    db_stuck_passwd: str
    AUTH0_DOMAIN: str = 'xxx'
    AUTH0_CLIENT_SECRET: str
    TOKEN_TEST: str


def get_settings():
    return Settings()


if __name__ == '__main__':
    settings = get_settings()
    print(settings.db_stuck_host)
    print(settings.db_stuck_passwd)
    print(settings.AUTH0_DOMAIN)
    print(settings.AUTH0_CLIENT_SECRET)
    print(settings.db_stuck_passwd)
