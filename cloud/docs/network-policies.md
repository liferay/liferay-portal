# Ingress Network Policies

Every CNE platform namespace denies ingress by default and reopens only the
paths below. This file records *why* each allow exists, because the rendered
YAML cannot say it and at least one of these rules is wrong in the obvious
reading.

## Control Plane Traffic Does Not Come From The Control Plane CIDR

On GKE the API server does not connect to in-cluster webhooks directly. It
tunnels through `konnectivity-agent` pods in `kube-system`, so an admission
webhook call arrives carrying an **agent pod IP** from the cluster pod CIDR,
never `master_ipv4_cidr_block`.

An `ipBlock` on the control plane CIDR therefore matches nothing, and a policy
allowing only that drops the API server's own traffic. This was verified on a
live cluster: applying such a policy stopped the ECK webhook from being called
at all.

Both entries are kept:

```yaml
from:
  - ipBlock:
      cidr: <master CIDR>          # clusters that connect directly
  - namespaceSelector:
      matchLabels:
        kubernetes.io/metadata.name: kube-system
    podSelector:
      matchLabels:
        k8s-app: konnectivity-agent   # clusters that tunnel (GKE default)
```

The `ipBlock` entry looks redundant on a konnectivity cluster. Do not remove
it: it is what matches on a cluster that connects directly.

The two selectors must stay in the **same** list entry. Split into two entries
they mean "anything in kube-system, or any konnectivity-agent pod anywhere".

This rule is written out at each webhook policy rather than shared. Keeping it
inline makes the allow-list visible where it is read, which matters for a
security rule; the duplication is guarded by a test per policy that requires the
konnectivity source. Revisit once a second consumer exists in either stack.

## Matrix

`observability` below is `var.observability_config.namespace`.

### argocd-system

| Allowed in | From | Port | Why |
| --- | --- | --- | --- |
| metrics, 7 components | `observability` | `metrics` | Prometheus scrape. Gated on `observability_config.enabled`, so the rules vanish when nothing scrapes. |
| dex, redis, repo-server | `observability` | `grpc`, `redis`, `repo-server` | Component-to-component paths the chart needs. |
| argocd-server | `gateway_namespace` | `server` | Envoy Gateway fronts the UI. |
| everything else | — | — | `default-deny-ingress` |

### argo-workflows-system

| Allowed in | From | Port | Why |
| --- | --- | --- | --- |
| metrics | `observability` | `metrics` | Prometheus scrape. Not gated, unlike argocd. |
| everything else | — | — | `default-deny-ingress` |

### crossplane-system

| Allowed in | From | Port | Why |
| --- | --- | --- | --- |
| function runtimes | crossplane core pods | `grpc` | Core calls function pods over gRPC. |
| metrics | `observability` | `metrics` | Empty `podSelector`: core, RBAC manager, functions and providers all expose metrics and share no single label. |
| webhook | control plane CIDR + konnectivity agents | 9443 | API server calls it. Port by number: core names it `webhooks`, providers name it `webhook`. |
| everything else | — | — | `default-deny-ingress` |

### elastic-system

| Allowed in | From | Port | Why |
| --- | --- | --- | --- |
| operator webhook | control plane CIDR + konnectivity agents | `https-webhook` | API server calls it. `failurePolicy: Ignore`, so a block is **silent** — see below. |
| everything else | — | — | `default-deny-ingress` |

## Verifying A Change

Rendered-YAML tests cannot tell whether a rule matches real traffic. After
changing any policy, run the smoke test against a cluster:

```bash
cloud/scripts/tests/run_netpol_smoke.sh <kube-context>
```

It asserts the paths that must stay open. The assertion differs by
`failurePolicy`:

| failurePolicy | A blocked webhook looks like | So the check must assert |
| --- | --- | --- |
| `Fail` (ESO) | admission errors | a valid resource is **admitted** |
| `Fail` (crossplane) | `failed calling webhook` on an in-use delete | the delete is denied **for being in use**, not for a webhook error |
| `Ignore` (ECK) | nothing at all — validation silently stops | an invalid resource is **rejected** |

Crossplane's check has to create its own fixture: its webhook only fires on
deletes of objects labelled `crossplane.io/in-use=true`, so the script makes a
throwaway `ConfigMap` and a `Usage`, exercises the delete, and removes both.
Note that `kubectl delete usage` resolves to the deprecated cluster-scoped
`apiextensions.crossplane.io` kind — the namespaced
`usages.protection.crossplane.io` must be named in full.

The `Ignore` case is why "it worked" is not evidence. A blocked ECK webhook
returns success and admits resources it should have refused, with nothing in
any log.

## Status

`external-secrets-system` is not listed: its policies are still in review.
`envoy-gateway-system`, `liferay` and `cluster-bootstrap-system` are not yet
done (LCD-51451).