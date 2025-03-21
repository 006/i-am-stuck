import os
from pydantic_settings import BaseSettings, SettingsConfigDict
from pydantic import Field


class Settings(BaseSettings, case_sensitive=True):
    """
    Kubernets, use /config/stuck.properties
    Local Debug, use ./config/stuck.properties
    """
    model_config = SettingsConfigDict(
        env_file='/config/stuck.properties', extra='ignore', env_file_encoding='utf-8')

    # ConfigMap
    db_stuck: str
    db_stuck_host: str
    db_stuck_port: int
    db_stuck_user: str
    AUTH0_DOMAIN: str
    # Secret
    db_stuck_passwd: str = Field(alias="db_stuck_passwd") #os.environ.get('db_stuck_passwd')
    # AUTH0_CLIENT_SECRET: str
    # TOKEN_TEST: str


def get_settings():
    """TODO"""
    return Settings()
