# App1 — Project Setup Guide

This guide walks you through cloning, configuring, and running the App1 application from scratch.

---

## Table of Contents

- [Prerequisites](#prerequisites)
- [Cloning the Repository](#cloning-the-repository)
- [Project Structure](#project-structure)
- [Installing Dependencies](#installing-dependencies)
- [Environment Variables](#environment-variables)
- [Running the Development Server](#running-the-development-server)
- [Building for Production](#building-for-production)
- [Running Tests](#running-tests)
- [Linting and Formatting](#linting-and-formatting)
- [Docker (Optional)](#docker-optional)
- [Troubleshooting](#troubleshooting)
- [Contributing](#contributing)

---

## Prerequisites

Ensure the following tools are installed on your machine before proceeding:

| Tool | Minimum Version | Installation |
|------|----------------|--------------|
| **Git** | 2.30+ | [git-scm.com](https://git-scm.com/) |
| **Node.js** | 18 LTS+ | [nodejs.org](https://nodejs.org/) (or use [nvm](https://github.com/nvm-sh/nvm)) |
| **npm** | 9+ | Bundled with Node.js |
| **Docker** *(optional)* | 24+ | [docs.docker.com](https://docs.docker.com/get-docker/) |

> **Tip:** To verify installed versions, run:
> ```bash
> git --version
> node --version
> npm --version
> ```

---

## Cloning the Repository

1. **Clone the repo:**

   ```bash
   git clone https://github.com/gregetmaxs/App1.git
   ```

2. **Navigate into the project directory:**

   ```bash
   cd App1
   ```

3. **Verify the clone:**

   ```bash
   git status
   ```

   You should see `On branch main` with a clean working tree.

---

## Project Structure

```
App1/
├── README.md          # Project overview
├── SETUP.md           # This setup guide
├── .env.example       # Example environment variables (copy to .env)
├── package.json       # Dependencies and scripts (when added)
├── src/               # Application source code (when added)
│   ├── index.js       # Application entry point
│   └── ...
├── tests/             # Test files (when added)
│   └── ...
└── docs/              # Additional documentation (when added)
```

> **Note:** The project is in its early stages. Directories listed above will be created as the application is developed. Refer back to this guide as the structure evolves.

---

## Installing Dependencies

Once a `package.json` is added to the project, install dependencies with:

```bash
npm install
```

If the project uses a lockfile (`package-lock.json`), prefer a clean install to ensure reproducible builds:

```bash
npm ci
```

### Common Issues

- **Permission errors:** Avoid using `sudo` with npm. If you encounter `EACCES` errors, fix npm's directory permissions:
  ```bash
  mkdir -p ~/.npm-global
  npm config set prefix '~/.npm-global'
  export PATH="$HOME/.npm-global/bin:$PATH"
  ```
  Add the `export` line to your `~/.bashrc` or `~/.zshrc` for persistence.

- **Node version mismatch:** If the project specifies an engine requirement, use `nvm` to switch:
  ```bash
  nvm install
  nvm use
  ```

---

## Environment Variables

Environment variables keep secrets and configuration out of source control.

### Setup

1. **Copy the example file:**

   ```bash
   cp .env.example .env
   ```

2. **Edit `.env`** with your values:

   ```bash
   # .env
   #
   # Application
   NODE_ENV=development
   PORT=3000
   #
   # Database (example)
   # DATABASE_URL=postgresql://user:password@localhost:5432/app1_dev
   #
   # API Keys (example)
   # API_KEY=your-api-key-here
   # API_SECRET=your-api-secret-here
   ```

3. **Never commit `.env` to version control.** Ensure `.env` is listed in `.gitignore`:

   ```
   # .gitignore
   .env
   .env.local
   .env.*.local
   ```

### Required Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `NODE_ENV` | Application environment (`development`, `production`, `test`) | `development` |
| `PORT` | Port the server listens on | `3000` |

> **Note:** Additional variables will be documented here as features are added.

---

## Running the Development Server

Once the application code and dependencies are in place, start the development server:

```bash
npm run dev
```

The server will start on the port specified by the `PORT` environment variable (default: `3000`). Open your browser and navigate to:

```
http://localhost:3000
```

### Hot Reloading

If the project uses a tool like [nodemon](https://nodemon.io/) or a framework with built-in hot reload (e.g., Next.js, Vite), file changes will automatically restart the server or refresh the browser.

---

## Building for Production

To create an optimized production build:

```bash
npm run build
```

To run the production build locally:

```bash
npm start
```

---

## Running Tests

Execute the test suite with:

```bash
npm test
```

For watch mode during development:

```bash
npm run test:watch
```

For coverage reports:

```bash
npm run test:coverage
```

---

## Linting and Formatting

Keep the codebase consistent by running the linter and formatter:

```bash
# Lint
npm run lint

# Auto-fix lint issues
npm run lint:fix

# Format code (if Prettier or similar is configured)
npm run format
```

### Pre-commit Hooks

If the project uses [Husky](https://typicode.github.io/husky/) or [pre-commit](https://pre-commit.com/) for Git hooks, they will be installed automatically when you run `npm install`. These hooks run linting and formatting checks before each commit to catch issues early.

---

## Docker (Optional)

If a `Dockerfile` is added to the project, you can build and run the application inside a container:

### Build the Image

```bash
docker build -t app1 .
```

### Run the Container

```bash
docker run -p 3000:3000 --env-file .env app1
```

### Docker Compose

If a `docker-compose.yml` is provided for multi-service setups (e.g., app + database):

```bash
docker compose up -d
```

To stop all services:

```bash
docker compose down
```

---

## Troubleshooting

| Problem | Solution |
|---------|----------|
| `npm install` fails with network errors | Check your internet connection or try `npm install --registry https://registry.npmmirror.com` |
| Port already in use | Kill the process occupying the port: `lsof -ti :3000 \| xargs kill -9`, or change `PORT` in `.env` |
| Environment variable not loading | Ensure you copied `.env.example` to `.env` and restarted the server |
| Git clone fails with permission denied | Verify your SSH key is added to GitHub: `ssh -T git@github.com` |
| Docker container won't start | Check logs: `docker logs <container_id>` |

---

## Contributing

1. **Fork the repository** and create your branch from `main`:
   ```bash
   git checkout -b feature/your-feature-name
   ```

2. **Make your changes** and ensure they pass all checks:
   ```bash
   npm run lint
   npm test
   ```

3. **Commit your changes** with a clear message:
   ```bash
   git commit -m "feat: add your feature description"
   ```

   Follow the [Conventional Commits](https://www.conventionalcommits.org/) specification when possible.

4. **Push to your branch:**
   ```bash
   git push origin feature/your-feature-name
   ```

5. **Open a Pull Request** against `main` and describe your changes.

---

## Additional Resources

- [Git Documentation](https://git-scm.com/doc)
- [Node.js Documentation](https://nodejs.org/docs/latest/api/)
- [npm Documentation](https://docs.npmjs.com/)
- [Docker Documentation](https://docs.docker.com/)

---

*This guide will be updated as the project evolves. If you find any issues or have suggestions, please open an issue or submit a PR.*
