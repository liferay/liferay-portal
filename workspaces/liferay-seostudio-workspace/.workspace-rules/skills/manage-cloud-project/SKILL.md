---

description: Authenticate to and operate on a Liferay Cloud (LXC / DXP Cloud) project via the `lcp` CLI — deploy client extensions, view logs, restart services, list resources. Use when the user mentions lcp/LXC/liferay.cloud, when an LCP.json is present in the workspace, or when the user references a console.liferay.cloud URL.
name: manage-cloud-project

---

# Manage Cloud Project

Operate on a Liferay Cloud project from the `lcp` CLI.

## When to Invoke

- `lcp`, LXC, liferay.cloud, or DXP Cloud is mentioned.
- An `LCP.json` file exists in the workspace.
- The user references a `console.liferay.*` URL.

"Deploy to dev/uat/prd" alone is not a trigger. Those names also describe local environments under `configs/{env}/`. Require an explicit cloud signal.

## Establish Context

Resolve each before proceeding:

1. `lcp --version` — is the CLI installed?

1. `lcp remote` — is a default remote set?

1. `<project>-<env>` — what is the intended target?

## Workflow

1. **Identify the target.** Confirm `<project>-<env>` with the user. Use `lcp list` if uncertain.

1. **Deploy.** Run `lcp deploy`. Discover flags via `lcp deploy --help`.

1. **Verify runtime health.** Tail `lcp log` for the target service. Apply the runtime activation rule from `liferay-rules.md`: a successful command exit is not success.

For any other operation, discover available commands via `lcp --help`.

## Pitfalls

- `<project>-<env>` naming overlaps with local `configs/{env}/` directories. Confirm cloud target before assuming.
- `LCP.json` placement controls deploy scope: at repo root, `lcp deploy` deploys all services; from a service folder, it deploys only that one.

## Composition

- Counterpart to `deploy-and-verify`, which targets local Tomcat or Docker.
- Consumes the artifacts produced by `scaffold-client-extension`.
- Defer credential and secret handling to `production-standards`.

## References

- `lcp --help` and `lcp <cmd> --help`
- `https://learn.liferay.com/w/dxp/cloud/reference/command-line-tool`