from fastapi.testclient import TestClient

from src.stuck_fastapi.main import app
from src.stuck_fastapi.config import get_settings

client = TestClient(app)
config = get_settings()


def test_create_report():
    response = client.post(
        "/spot/ECF33406590B45B1A7C4287F523B31C4/report",
        headers={"Authorization": f"Bearer {config.TOKEN_TEST}"},
        json={"bites": 2, "bph": 1, "cph": 0, "datime_last": "2025-02-02 11:13:25"},
    )
    assert response.status_code == 200
    assert response.json() == {
        'ok': True
    }
