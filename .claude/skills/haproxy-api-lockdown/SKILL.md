---
name: haproxy-api-lockdown
description: "Restrict an API endpoint in HAProxy to only the calls actually made by browser JS. Full workflow: analyze source → design ACLs → deploy → browser-test loop until clean."
---

# /haproxy-api-lockdown

Locks down an API endpoint in HAProxy by allowlisting only calls made by browser-side JavaScript. Blocks everything else with deny-reason logging for diagnostics.

## Project paths

Concrete paths for this Arena project are in `DESIGN.md` under "HAProxy `/api/jsonws` Lockdown":
- **Edit**: `/opt/projects/arena-install/develop/arena-install/stage/haproxy/arena/arena.cfg`
- **Live**: `/etc/haproxy/arena/arena.cfg`
- **Log**: `/var/log/haproxy.log`
- **JS source**: `modules/apps/foundation/frontend-js/` in this repo

Deploy one-liner: `cp <source> /etc/haproxy/arena/arena.cfg && systemctl reload haproxy`

## Usage

```
/haproxy-api-lockdown                        # start grilling session to design the allowlist
/haproxy-api-lockdown <endpoint>             # e.g. /api/jsonws
```

## Workflow

### Phase 1 — Analyze: find all JS calls to the endpoint

Search the JS source tree for every call to the target endpoint.

**For Liferay JSONWS (`/api/jsonws/invoke`):**
- Search for `Liferay.Service(` and `Liferay.Service.bind(`
- All calls POST to `/api/jsonws/invoke` as `application/x-www-form-urlencoded` with `cmd=<json>&p_auth=<token>`
- Two call shapes:
  - **Plain**: `{""/service/method"": {...}}` — key starts with `"/`
  - **Object-graph**: `{""$varname = /service"": {..., ""$nested = /service2"": {...}}}` — key starts with `"$`

**Critical encoding pitfall (Liferay/jQuery):** jQuery encodes spaces as `+` in form bodies. HAProxy's `url_dec` converter only decodes `%XX` — it does NOT convert `+` to space. So an object-graph key `"$var = /service"` arrives in the raw body as `"$var+=+/service"`. Write regexes matching `\+=\+` not ` = `.

### Phase 2 — Design ACLs

**Frontend (captures — must be in frontend, not backend):**
```haproxy
declare capture response len 30
acl fe-<endpoint> path <endpoint-path>
http-request wait-for-body time 5s if fe-<endpoint> { method POST }
http-request capture req.body_param(cmd),url_dec len 300 if fe-<endpoint> { method POST }
http-response capture res.hdr(X-<Endpoint>-Deny-Reason) id 0
```

**Backend ACL skeleton:**
```haproxy
acl is-ep        path_beg <endpoint>
acl ep-internal  src -f /etc/haproxy/<whitelist>.txt
acl ep-invoke    path <endpoint>/invoke
acl ep-post      method POST

# Buffer body before ACL evaluation (scoped to POST invoke only)
http-request wait-for-body time 5s if ep-invoke ep-post

# Anti-pollution / anti-batch guards
acl ep-dup-cmd       req.body -m reg '(^|&)cmd=.*&cmd='
acl ep-multi-service req.body_param(cmd),url_dec -m reg '\}[[:space:]]*,[[:space:]]*"[/$]'

# Allowlist — plain calls (service name at key start, no batch array)
acl ep-allowed-service req.body_param(cmd),url_dec -m reg '^\{"/(?:service1|service2)":'

# Allowlist — object-graph calls (note \+=\+ for spaces around =)
acl ep-allowed-graph req.body_param(cmd),url_dec -m reg '^\{"\$[a-zA-Z0-9_]+\+=\+/(?:service1|service2)":'

# Allow rules first, then deny-with-reason, then catch-all
http-request allow if is-ep ep-internal
http-request allow if ep-invoke ep-post ep-allowed-service !ep-dup-cmd !ep-multi-service
http-request allow if ep-invoke ep-post ep-allowed-graph !ep-dup-cmd !ep-multi-service
http-request deny deny_status 403 hdr X-<Endpoint>-Deny-Reason "dup-cmd"       if is-ep ep-dup-cmd
http-request deny deny_status 403 hdr X-<Endpoint>-Deny-Reason "multi-service" if is-ep ep-multi-service
http-request deny deny_status 403 hdr X-<Endpoint>-Deny-Reason "not-allowed"   if is-ep
```

**HAProxy syntax notes:**
- `declare capture response len N` + `http-response capture ... id 0` — the `id 0` is required; `len N` inline does not work for response captures
- `http-request capture` is frontend-only; body ACLs can be in backend
- `wait-for-body` must precede any ACL that reads the body
- `is-jsonws jsonws-invoke` is redundant — `jsonws-invoke` (exact path match) is already a subset of `is-jsonws` (path_beg); use the more specific one

### Phase 3 — Deploy and verify loop

```bash
# 1. Copy to live location
cp <source-cfg> /etc/haproxy/<path>/<file>.cfg

# 2. Validate config
haproxy -c -f /etc/haproxy/haproxy.cfg -f /etc/haproxy/<path>/<file>.cfg

# 3. Reload
systemctl reload haproxy   # or: service haproxy reload

# 4. Browser-test with Claude in Chrome (open the feature that triggers the call)

# 5. On 403: check deny reason
grep "403" /var/log/haproxy.log | grep "<endpoint>" | tail -10

# 6. Check captured cmd body in log to see what actually arrived
#    e.g. log shows: {#7B#22$vocabularies+=+/assetvo  (hex-encoded { = #7B, " = #22)

# 7. Adjust ACL, repeat from step 1
```

Loop (steps 1–7) until no 403s appear after full feature exercise.

### Diagnosing regex mismatches from logs

HAProxy captures the first 300 chars of `url_dec(cmd)` in the request log. If the regex doesn't match:
1. Read the captured prefix in the log — it shows the actual decoded value
2. Check for `+=+` where you expected ` = ` (jQuery `+` encoding)
3. Check for `%XX` sequences that `url_dec` did not decode (shouldn't happen, but verify)
4. Test the regex locally: `echo 'body' | grep -P 'your-pattern'`

### Security constraints

- Never match on `cmd` value alone without anchoring — always use `^` anchor
- Never allow batch arrays: the `cmd` value must start with `{` not `[`
- Guard every `allow` with `!dup-cmd` to prevent HTTP parameter pollution
- For object-graph calls, only allowlist the **top-level** service — nested calls in the graph follow from the allowed entry point; adding nested services to the regex expands attack surface unnecessarily
