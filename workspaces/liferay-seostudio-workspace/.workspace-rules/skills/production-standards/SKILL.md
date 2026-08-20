---

description: Production guardrails applied when generating or modifying any code or configuration intended for a nonlocal environment (UAT, prod, customer facing deploys). Use whenever the agent's output may reach a higher environment — to enforce state integrity, secret management, and security preserving debugging practices.
name: production-standards

---

# Liferay Production Rules

## Configuration and State Integrity

- **State Integrity (Source of Truth)**: configuration is the source of truth for version control — never mutate the runtime directly. For Tomcat, this is `configs/local/portal-ext.properties`; copy to `bundles/portal-ext.properties` and restart to apply. For Docker with a prebuilt image, this is `liferay.env` (env vars); changes require a container restart. For Docker with a custom image, this is `configs/docker/portal-ext.properties`; changes require rebuilding the image.
- **No Silent Edits to Runtime**: never modify the live runtime state (e.g., `bundles/portal-ext.properties`) without explicitly announcing the change in your response. Edits that propagate from `configs/[env]/` via the normal sync flow are preferred.
- **No Security Disabling Debug Shortcuts**: never set `portal.security.manager.strategy=none`, disable CSRF enforcement, or otherwise weaken security as a debugging shortcut — even temporarily. If a 403 cannot be resolved through Roles, Permissions, or OAuth2 scope configuration, declare it a blocker and surface it to the user rather than disabling the protection.

## Production Deployment Guardrails

- **Environment Specific Configs**: mandate `configs/prod/` or `configs/uat/` for environment properties; these must be bundled into Docker images / Liferay Cloud builds, not hot deployed via Blade.
- **Secret Management**: explicitly forbid hardcoded credentials. Mandate Liferay's Secret Management or environment variables (e.g., `${env.SECRET_NAME}`).
- **Performance & Caching**: mandate proper caching headers for custom endpoints / Client Extensions, and use of `Liferay.Util.fetch` to route through authenticated caching layers.