---

description: Standards for building and deploying React based Custom Element Client Extensions. Use when the user wants to build a React powered widget, scaffold a React Custom Element CET, debug 404s on built assets, or troubleshoot an OSGi `Configuration deleted` loop. For nonReact Custom Element scaffolding or other CET types, use `scaffold-client-extension`.
name: react-custom-elements

---

# React Custom Elements

Standards for building and deploying React based Custom Element Client Extensions.

## When to Invoke

- "Build a React widget", "scaffold a React Custom Element CET"
- "Debug 404s on built assets"
- "Troubleshoot an OSGi `Configuration deleted` loop"
- For nonReact Custom Elements or other CET types, use `scaffold-client-extension`.

## Project Scaffold

Before generating any scaffold code, ask the user which build tool they are using (e.g., Vite, webpack, Create React App). Do not assume a build tool — the project structure, output paths, and `assemble` block configuration differ between bundlers.

For a reference starting point, consult the official [Liferay client-extension-samples](https://github.com/liferay/liferay-portal/tree/master/workspaces/liferay-sample-workspace/client-extensions/) which reflect the current recommended structure.

## `client-extension.yaml` — Assemble Block

The `assemble` block is mandatory. Without it, Liferay Workspace will not package the extension's assets, causing 404 errors on all static resources.

The `assemble` block must copy your build output directory into a target directory in the extension. The `urls` pattern must match your bundler's output filename format, including any content hashes:

```yaml
assemble:
    - from: <build-output-dir>
      into: static

my-react-app:
    name: My React App
    type: customElement
    friendlyURLMapping: my-react-app
    htmlElementName: my-react-app
    instanceable: true
    portletCategoryName: category.client-extensions
    urls:
        - <bundler-output-pattern>   # e.g. js/main.*.js for CRA, assets/index-*.js for Vite
    cssURLs:
        - <bundler-css-pattern>
    useESM: true
```

**Home Page URL**: for associated OAuth2 applications, `homePageURL` must include the full protocol (e.g., `http://localhost:${PORT}`) to prevent interpolation errors.

## Implementation Patterns

Wrap the React application in a standard Web Components Custom Element class. This pattern is build tool agnostic — it works regardless of bundler.

- **Web Component wrapper**: wrap the React application in a standard Custom Element class.
- **Lifecycle management**: use `connectedCallback` to initialize the React root and `disconnectedCallback` to properly unmount it, preventing memory leaks in single page navigation.
- **Global variables**: use `/* global Liferay */` to prevent ESLint errors when accessing platform utilities.

```javascript
/* global Liferay */
import React from 'react';
import { createRoot } from 'react-dom/client';
import App from './App';

class MyReactElement extends HTMLElement {
    connectedCallback() {
        if (!this.root) {
            this.root = createRoot(this);
        }
        this.root.render(<App />);
    }
    disconnectedCallback() {
        if (this.root) {
            this.root.unmount();
            this.root = null;
        }
    }
}

const ELEMENT_ID = 'my-react-app';
if (!customElements.get(ELEMENT_ID)) {
    customElements.define(ELEMENT_ID, MyReactElement);
}
```

## Patterns and Gotchas

- **404 on assets**: usually a mismatch between the `assemble` block `from` path and the actual build output directory. Verify the path matches your bundler's output location.
- **Multiple apps**: if deploying multiple React apps, ensure each has a unique `htmlElementName` and `friendlyURLMapping`.
- **OSGi "deleted" loop**: if deployment logs show a repeating `IllegalStateException: Configuration [id] deleted`, the OSGi registry is stuck on a corrupted previous registration. Fastest fix: rename the extension ID in `client-extension.yaml` and redeploy — this forces a clean registration under a new ID. Deep debugging of OSGi config state is rarely faster.

## Success Signal

TODO / inferred — verify against a running bundle. After `deploy-and-verify`, confirm the built React widget's assets return HTTP `200 OK` and the custom element renders on a page hosting it.