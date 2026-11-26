import httpx

from config import API_BASE_URL


async def get_json(path: str):
    async with httpx.AsyncClient(timeout=10.0) as client:
        response = await client.get(f"{API_BASE_URL}{path}")
        response.raise_for_status()
        return response.json()


async def upload_pdf(filename: str, content: bytes) -> None:
    async with httpx.AsyncClient(timeout=30.0) as client:
        response = await client.post(
            f"{API_BASE_URL}/api/files",
            files={"file": (filename, content, "application/pdf")},
        )
        response.raise_for_status()


async def change_document(document_id: int, method: str, payload: dict | None = None) -> None:
    async with httpx.AsyncClient(timeout=10.0) as client:
        response = await client.request(
            method,
            f"{API_BASE_URL}/api/files/{document_id}",
            json=payload,
        )
        response.raise_for_status()


async def add_tag(document_id: int, name: str) -> dict:
    async with httpx.AsyncClient(timeout=10.0) as client:
        response = await client.post(f"{API_BASE_URL}/api/files/{document_id}/tags", json={"name": name})
        response.raise_for_status()
        return response.json()


async def remove_tag(document_id: int, tag_id: int) -> None:
    async with httpx.AsyncClient(timeout=10.0) as client:
        response = await client.delete(f"{API_BASE_URL}/api/files/{document_id}/tags/{tag_id}")
        response.raise_for_status()
