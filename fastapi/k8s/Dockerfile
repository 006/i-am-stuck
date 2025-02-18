#Deriving the latest base image
FROM python:3.12-slim

WORKDIR /app

# Copy pre-compiled dependencies (if any)
COPY dist/*.whl /app/

RUN pip install --upgrade pip

# Install the wheel
RUN pip install --no-cache-dir /app/*.whl

# Install any remaining dependencies from requirements.txt (if applicable)
COPY requirements.txt /app/

# Install MariaDB Connector/C, which is a dependency.
RUN apt-get update && apt-get install -y libmariadb3 libmariadb-dev gcc

RUN pip install --no-cache-dir -r requirements.txt

# Copy your application files (if any additional files are needed)
# COPY . /app
COPY stuck.properties /app/

# Debugging steps:
# RUN ls -l /app  # List files in /app

# Set the entrypoint (command to run when the container starts)
#CMD ["python", "-m", "stuck_fastapi.main"]
#CMD ["fastapi", "run", "stuck_fastapi.main", "--port", "8000"]
CMD ["uvicorn", "stuck_fastapi.main:app", "--host", "0.0.0.0", "--port", "8000"]