---

alwaysApply: false
description: Beginner friendly mentor walkthrough for creating, deploying, and verifying a first Liferay Client Extension. Use when the user appears new to Liferay or Client Extensions, or when the user explicitly asks for a guided experience. For experienced developers and advanced workflows, use `scaffold-client-extension` instead.
globs: client-extensions/**
name: guided-client-extension

---

# Liferay Client Extension Mentor Protocol (Beginner Friendly)

## Preflight Check

- If the user appears to be new to Liferay or Client Extensions, ask if they would like a guided experience.
- If they decline, use this document only for technical reference.

## Guided One Shot Generation

- **Target directory**: create a new folder at `client-extensions/liferay-hello-world`.
- **Source of truth**: always reference official Liferay samples for the correct structure.
  - **Sample link**: [liferay-sample-custom-element-1](https://github.com/liferay/liferay-portal/tree/master/workspaces/liferay-sample-workspace/client-extensions/liferay-sample-custom-element-1)
- **Key files**:
  - `client-extension.yaml`: use the sample configuration as a base. Ensure the `type` matches the intended extension (e.g., `customElement`).
  - `assets/index.js`: standard entry point for JavaScript logic.
  - `assets/style.css`: standard entry point for component styling.
- **Autodetection**: remind the user that no `build.gradle` is needed in the extension folder; the Liferay workspace plugin detects everything in `client-extensions/` automatically by finding the `client-extension.yaml`.

## Deployment Workflow

- **Mechanism**: explain that Blade uses the Gradle Wrapper (`blade gw`) to package the code into a `.zip` file.
- **Prerequisites**:
  - **Tomcat**: verify that a `bundles/` folder exists. Ensure the server is running (`http://localhost:${PORT}`).
  - **Docker**: verify the Liferay container is running and healthy (`curl --fail http://localhost:${PORT}/c/portal/status`). Do not check for `bundles/` — it may not exist in a Docker workspace.
- **Execution**:
  - **Tomcat**: run `blade gw deploy`. This compiles the assets and copies the zip to `bundles/osgi/client-extensions/`.
  - **Docker**: run `./gradlew deploy "-Ddeploy.docker.container.id=$(docker compose ps --quiet liferay)"`. This deploys directly into the running container with no restart needed.

## Runtime Verification

- **Logs**: instruct the user (or search yourself) for the log entry: `STARTED [extension-id]`.
- **Significance**: explain that this log entry confirms Liferay has detected, registered, and started the extension in the OSGi registry.

## UI Placement and Publishing

Guide the user through these manual steps in the Liferay Portal UI:

1. Navigate to a Site Page and click the **Pencil (Edit)** icon.

1. Open the **Fragments and Widgets** sidebar (the **+** icon).

1. Select the **Widgets** tab and find the **Client Extensions** category.

1. Drag your new extension onto the page.

1. Click **Publish**.

## Next Steps

- Ask the user if they would like to explore other Client Extension types (e.g., Global CSS, IFrame, or Batch).
- Refer to the [official samples](https://github.com/liferay/liferay-portal/tree/master/workspaces/liferay-sample-workspace/client-extensions/) for more advanced patterns.
- For advanced CET scaffolding (microservices, site initializers, batch CXs), use `skills/scaffold-client-extension/SKILL.md`.