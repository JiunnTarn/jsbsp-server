# JSBSP Server

This repository contains the backend code for the JSBSP project.

## Deployment

JSBSP Server supports self-hosting using Docker Compose.

1. Clone this repository:
   
   ```sh
   git clone https://github.com/JiunnTarn/jsbsp-server.git
   cd jsbsp-server
   ```

2. Configure the environment variables:
   
   ```sh
   mv .env.example .env
   vi .env
   ```
   
   Edit the `.env` file to set necessary environment variables.
   
   - `JWT_SECRET`: Secret key for JWT generation.
   - `AES_SALT`: Salt for AES encryption of passwords.
   - `DB_PASSWORD`: Password for the database.

3. Start the server:
   
   ```sh
   docker compose up -d
   ```

The server should now be running and listening on port `57277`.
