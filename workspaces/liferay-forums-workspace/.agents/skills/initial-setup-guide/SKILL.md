---

description: Gentle first run setup walkthrough for a user brand new to a Liferay Workspace. Use when a first time user needs step by step help creating a workspace, initializing the bundle, and starting the server for the first time. For diagnosing or repairing an existing workspace, use workspace-init.
name: initial-setup-guide

---

# Initial Setup Guide

A gentle, step by step first run for a brand new Liferay Workspace: verify the workspace, initialize the bundle, and start the server. For diagnosing or repairing an existing workspace, use `workspace-init`.

## When to Invoke

- A first time user needs help creating their first workspace and starting the server.
- The friendly entry point before `workspace-init`'s deeper setup and diagnosis.

## Workflow

For a first time user, follow this sequence:

### Workspace Verification

- Check for `gradle.properties` and `settings.gradle` in the root directory.
    - If missing, instruct the user to run `blade init -v [version]`.
    - Explain that Liferay Workspace is a generated set of folders and Gradle scripts that manage your SDK and server in one place.
    - If the files exist, skip to **Starting the Server**.

### Bundle Initialization

- Instruct the user to run `blade server init`.
- Explain that this downloads Liferay Portal (Tomcat bundle) into the `/bundles` folder.
- Confirm the `/bundles` folder exists before proceeding.

### Starting the Server

- Instruct the user to run `blade server start`.
    - Direct the user to watch the logs at `bundles/tomcat/logs/catalina.out`.
    - Inform the user there are different variations depending on their use case.
        - `blade server start --tail` starts the server and automatically tails the logs (catalina.out).
        - `blade server run` starts the server in the foreground. Closing the terminal stops the server.
        - `blade server start --debug` starts the server in debug mode (default port 8000).
    - Do not proceed to development tasks until the user confirms two things:
        - "Server startup in [X] ms" appears in the log.
        - The user can log in at `http://localhost:8080`.
- Instruct the user to use `test@liferay.com` to log in with `test` as the default password.

## Troubleshooting

If the server fails to start or behaves unexpectedly, use `web_search` to query Liferay Learn documentation:
- Search: `site:learn.liferay.com [error message or topic]`
- Common issues are documented in `/w/dxp/self-hosted-installation-and-upgrades` within the Liferay Learn website.

## Success Signal

The log shows "Server startup in [X] ms" and the user can sign in at `http://localhost:8080` with `test@liferay.com` / `test`.