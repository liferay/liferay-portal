# Liferay Object Actions: Scheduling, Error Handling & Resilience

> Analysis of `modules/apps/object/` — how Object Actions are triggered, executed, and how failures are managed.

---

## Table of Contents

1. [Architecture Overview](#architecture-overview)
2. [Trigger System](#trigger-system)
3. [Execution Flow](#execution-flow)
4. [Executors](#executors)
5. [Error Handling](#error-handling)
6. [Retry Logic](#retry-logic)
7. [Resilience Patterns](#resilience-patterns)
8. [Key File Reference](#key-file-reference)

---

## Architecture Overview

Object Actions are implemented as an **event-driven, message-passing system** built on Liferay's OSGi messaging infrastructure. The main components are:

```
Trigger Event
     │
     ▼
MessageListener (ObjectActionTriggerMessageListener)
     │  [Liferay Messaging API — async delivery]
     ▼
ObjectActionEngine.executeObjectActions()
     │
     ├─ Validate user/company
     ├─ Evaluate condition expression (DDM)
     ├─ Check for duplicate execution (ThreadLocal guard)
     ├─ Scope-check executor (company / object definition)
     │
     ▼
ObjectActionExecutor.execute()
     │  [wrapped in TransactionCommitCallback]
     ▼
doExecute() — actual work (webhook, script, function, etc.)
     │
     ▼
Status updated: SUCCESS or FAILED
```

---

## Trigger System

### Built-in Trigger Keys

Defined in `ObjectActionTriggerConstants`:

| Constant | Key | When it fires |
|---|---|---|
| `KEY_ON_AFTER_ADD` | `onAfterAdd` | After an object entry is created |
| `KEY_ON_AFTER_UPDATE` | `onAfterUpdate` | After an object entry is updated |
| `KEY_ON_AFTER_DELETE` | `onAfterDelete` | After an object entry is deleted |
| `KEY_ON_AFTER_ROOT_UPDATE` | `onAfterRootUpdate` | After the root-level entry in a hierarchy is updated |
| `KEY_ON_AFTER_ATTACHMENT_DOWNLOAD` | `onAfterAttachmentDownload` | After a file attachment is downloaded |
| `KEY_ON_AFTER_LOGIN` | `onAfterLogin` | After a user logs in |
| `KEY_STANDALONE` | `standalone` | Manually invoked (not event-driven) |

Source: `object-api/.../constants/ObjectActionTriggerConstants.java`

### Dynamic Trigger Registration (OSGi)

`ObjectActionTriggerRegistryImpl` uses an OSGi `ServiceTracker` to watch for `Destination` services tagged with `object.action.trigger.class.name`. When one appears, it automatically:

1. Registers a `MessageListener` bound to that destination
2. Registers an `ObjectActionTrigger` for that class name

This allows external modules to introduce custom triggers by publishing a `Destination` OSGi service with the appropriate property — no core changes required.

```java
// ObjectActionTriggerRegistryImpl.java
_serviceTracker = new ServiceTracker<>(
    bundleContext,
    bundleContext.createFilter(
        "(&(object.action.trigger.class.name=*)(objectClass=" +
            Destination.class.getName() + "))"),
    new ServiceTrackerCustomizer<...>() {
        public List<ServiceRegistration<?>> addingService(...) {
            // Registers MessageListener + ObjectActionTrigger automatically
        }
    });
```

Source: `object-service/.../action/trigger/ObjectActionTriggerRegistryImpl.java`

### Message Listener

`ObjectActionTriggerMessageListener` bridges incoming messages to the engine. It resolves `userId` from either `principalName` or a `PermissionChecker` embedded in the message:

```java
// ObjectActionTriggerMessageListener.java
protected void doReceive(Message message) {
    _objectActionEngine.executeObjectActions(
        _className,
        GetterUtil.getLong(message.get("companyId")),
        _objectActionTriggerKey,
        () -> (JSONObject)message.getPayload(),
        _getUserId(message));
}
```

Source: `object-service/.../action/trigger/messaging/ObjectActionTriggerMessageListener.java`

### Periodic Scheduler (Entry Housekeeping)

Separate from action triggers, a periodic cron job runs housekeeping on object entries and their versions. This is **not** an action trigger itself, but it can indirectly cause triggers to fire via entry updates.

```java
// CheckObjectEntrySchedulerJobConfiguration.java
@Component(
    configurationPid = "com.liferay.object.configuration.ObjectEntryScheduleConfiguration",
    service = SchedulerJobConfiguration.class
)
public class CheckObjectEntrySchedulerJobConfiguration
    implements SchedulerJobConfiguration {

    public TriggerConfiguration getTriggerConfiguration() {
        return TriggerConfiguration.createTriggerConfiguration(
            _objectEntryScheduleConfiguration.checkInterval(), TimeUnit.MINUTE);
    }

    public UnsafeRunnable<Exception> getJobExecutorUnsafeRunnable() {
        return () -> _companyLocalService.forEachCompanyId(companyId -> {
            _objectEntryLocalService.checkObjectEntries(companyId);
            _objectEntryVersionLocalService.checkObjectEntryVersions(companyId);
        });
    }
}
```

The `checkInterval` is configurable in minutes via OSGi configuration PID `com.liferay.object.configuration.ObjectEntryScheduleConfiguration`.

Source: `object-web/.../scheduler/CheckObjectEntrySchedulerJobConfiguration.java`

---

## Execution Flow

### Engine Entry Points

`ObjectActionEngineImpl` exposes two methods:

| Method | Purpose |
|---|---|
| `executeObjectAction(name, triggerKey, definitionId, payload, userId)` | Execute a single named action directly |
| `executeObjectActions(className, companyId, triggerKey, payloadSupplier, userId)` | Execute all active actions for a class+trigger combination |

### Guard Rails Before Execution

`executeObjectActions()` performs these checks before doing any work:

1. **Null guard** — exits immediately if `companyId == 0` or `userId == 0`
2. **User validation** — fetches the user; aborts if not found or company mismatch
3. **Object definition lookup** — fetches by `className`; aborts if not found
4. **Action list check** — fetches active actions for the trigger; returns early if empty

### Security Context Setup

Before looping over actions, the engine elevates privileges and saves the original context:

```java
// ObjectActionEngineImpl.java:127-169
String name = PrincipalThreadLocal.getName();
PermissionChecker permissionChecker = PermissionThreadLocal.getPermissionChecker();
boolean skipReadOnlyObjectFieldsValidation =
    ObjectEntryThreadLocal.isSkipReadOnlyObjectFieldsValidation();

try {
    ObjectEntryThreadLocal.setSkipObjectEntryResourcePermission(true);
    ObjectEntryThreadLocal.setSkipReadOnlyObjectFieldsValidation(true);
    PrincipalThreadLocal.setName(userId);
    PermissionThreadLocal.setPermissionChecker(_permissionCheckerFactory.create(user));

    // ... execute actions ...
}
finally {
    // Always restore original context
    ObjectEntryThreadLocal.setSkipObjectEntryResourcePermission(false);
    ObjectEntryThreadLocal.setSkipReadOnlyObjectFieldsValidation(skipReadOnlyObjectFieldsValidation);
    PrincipalThreadLocal.setName(name);
    PermissionThreadLocal.setPermissionChecker(permissionChecker);
}
```

The `finally` block ensures context is always restored, even if an exception is thrown.

### Per-Action Execution: `_executeObjectAction()`

For each individual action:

1. **Duplicate guard** — checks `ObjectActionThreadLocal` to skip already-executed actions (see [Resilience Patterns](#resilience-patterns))
2. **Condition evaluation** — evaluates a DDM expression; skips the action if it returns `false`
3. **Track execution** — records this `objectEntryId` in thread-local before proceeding (prevents re-entry)
4. **Executor lookup** — retrieves the right `ObjectActionExecutor` from the registry
5. **Scope validation** — checks company and object-definition allowlists
6. **Execute** — calls `objectActionExecutor.execute(...)`
7. **Status update** — marks the action `STATUS_SUCCESS` or `STATUS_FAILED`

---

## Executors

### Base Class: Post-Transaction Execution

All standard executors extend `BaseObjectActionExecutor`, which wraps `doExecute()` in a `TransactionCommitCallbackUtil` callback. This guarantees the executor runs **only after the triggering database transaction commits**, ensuring external systems see consistent data:

```java
// BaseObjectActionExecutor.java
public void execute(...) throws Exception {
    TransactionCommitCallbackUtil.registerCallback(() -> {
        doExecute(companyId, objectActionId, parametersUnicodeProperties,
                  payloadJSONObject, userId);
        return null;
    });
}
```

Source: `object-api/.../action/executor/BaseObjectActionExecutor.java`

> **Note:** `WebhookObjectActionExecutorImpl` does **not** extend `BaseObjectActionExecutor` — it implements `ObjectActionExecutor` directly, so it executes **synchronously within the transaction**, not after commit.

### Available Executors

| Key | Class | Notes |
|---|---|---|
| `webhook` | `WebhookObjectActionExecutorImpl` | HTTP POST; synchronous; no retry; no `BaseObjectActionExecutor` |
| `groovy` | `GroovyObjectActionExecutorImpl` | Executes a Groovy script; post-transaction via base class |
| `function#<erc>` | `FunctionObjectActionExecutorImpl` | Calls an external function via `PortalCatapult`; company+definition scoped; post-transaction |
| `add-object-entry` | `AddObjectEntryObjectActionExecutorImpl` | Creates a new entry; post-transaction |
| `update-object-entry` | `UpdateObjectEntryObjectActionExecutorImpl` | Updates an existing entry; post-transaction |
| `notification-template` | `NotificationTemplateObjectActionExecutorImpl` | Sends a notification; post-transaction |

### Webhook Executor (Synchronous, Fire-and-Forget)

```java
// WebhookObjectActionExecutorImpl.java
public void execute(...) throws Exception {
    Http.Options options = new Http.Options();
    options.addHeader(HttpHeaders.CONTENT_TYPE, ContentTypes.APPLICATION_JSON);
    options.addHeader("x-api-key", parametersUnicodeProperties.get("secret"));
    options.setBody(payloadJSONObject.toString(), ContentTypes.APPLICATION_JSON, StringPool.UTF8);
    options.setLocation(parametersUnicodeProperties.get("url"));
    options.setPost(true);

    _http.URLtoString(options);  // Fire-and-forget; any exception propagates up
}
```

Key observations:
- No retry logic
- No timeout configuration at this layer
- Any HTTP error or network failure propagates as an exception, causing `STATUS_FAILED`

### Function Executor (Company+Definition Scoped)

`FunctionObjectActionExecutorImpl` implements both `CompanyScoped` and `ObjectDefinitionScoped`, restricting which companies and object definitions may use it. The executor key incorporates the OSGi configuration's external reference code: `function#<erc>`.

---

## Error Handling

### Status Tracking

```java
// ObjectActionConstants.java
public static final int STATUS_NEVER_RAN = 0;
public static final int STATUS_SUCCESS   = 1;
public static final int STATUS_FAILED    = 2;
```

Status is persisted on the `ObjectAction` entity after every execution attempt.

### Per-Action Try/Catch in `_executeObjectAction()`

Each action wraps its logic in try/catch. On failure: set `STATUS_FAILED`, then **re-throw** the exception:

```java
// ObjectActionEngineImpl.java:232-294
try {
    // ... evaluate condition, validate scope, execute ...
    _updateObjectActionStatus(objectAction, ObjectActionConstants.STATUS_SUCCESS);
}
catch (Exception exception) {
    _updateObjectActionStatus(objectAction, ObjectActionConstants.STATUS_FAILED);
    throw exception;  // propagates to the multi-action loop
}
```

### Multi-Action Fail-Safe Loop

The outer loop in `executeObjectActions()` catches exceptions from each action and **logs them without stopping the remaining actions**:

```java
// ObjectActionEngineImpl.java:150-159
for (ObjectAction objectAction : objectActions) {
    try {
        _executeObjectAction(objectAction, objectDefinition,
                             payloadJSONObject, userId, variables);
    }
    catch (Exception exception) {
        _log.error(exception);  // logged; loop continues
    }
}
```

This is a **fail-safe / best-effort** pattern: one action failing does not block subsequent actions for the same trigger event.

### Locked Action Exception

When updating status, a `LockedObjectActionException` is silently swallowed. All other `PortalException` types are re-thrown:

```java
// ObjectActionEngineImpl.java:319-336
private void _updateObjectActionStatus(ObjectAction objectAction, int status)
    throws PortalException {

    if (objectAction.getStatus() == status) {
        return;  // idempotent; skip if already correct
    }

    try {
        _objectActionLocalService.updateStatus(objectAction.getObjectActionId(), status);
    }
    catch (PortalException portalException) {
        if (!(portalException instanceof LockedObjectActionException)) {
            throw portalException;
        }
        // LockedObjectActionException is silently ignored
    }
}
```

### Exception Types

| Exception | Meaning |
|---|---|
| `ObjectActionSystemException` | System-level failure during execution |
| `ObjectActionErrorMessageException` | User-facing error message from an action |
| `ObjectActionExecutorKeyException` | Invalid executor key or scope violation |
| `ObjectActionTriggerKeyException` | Invalid trigger key |
| `ObjectActionConditionExpressionException` | DDM condition expression parse/eval error |
| `ObjectActionActiveException` | Attempting to activate an action that cannot be activated |
| `ObjectActionParametersException` | Invalid or missing executor parameters |
| `LockedObjectActionException` | Concurrent status update conflict (silently ignored) |
| `NoSuchObjectActionException` | Action not found by name+trigger+definition |
| `DuplicateObjectActionExternalReferenceCodeException` | ERC collision on create/update |

Source: `object-api/.../exception/`

---

## Retry Logic

**There is no built-in retry mechanism** in the Object Actions system. The design is fire-and-forget with status tracking:

| Aspect | Behavior |
|---|---|
| Network failure (webhook) | Exception propagates → `STATUS_FAILED`, no retry |
| Script exception (Groovy) | Exception propagates → `STATUS_FAILED`, no retry |
| External function failure | Exception propagates → `STATUS_FAILED`, no retry |
| `STATUS_FAILED` actions | Remain failed; no background job re-executes them |
| Manual re-trigger | Only possible by re-firing the original event or using `standalone` trigger manually |

### Implications for Integrators

- Webhook endpoints that may be temporarily unavailable will cause permanent `STATUS_FAILED` with no recovery
- Function executors via `PortalCatapult` inherit whatever resilience is built into that service, but there is no wrapper retry at the Object Actions layer
- For critical integrations, implement retry logic in the receiving endpoint (e.g., idempotent webhook consumers with a queue)

---

## Resilience Patterns

### 1. Duplicate Execution Prevention (Thread-Local Guard)

`ObjectActionThreadLocal` maintains a `Map<Long objectActionId, Set<Long objectEntryId>>` per thread. Before executing an action, the engine checks this map:

- **Non-update triggers**: if the action ID is already in the map at all, skip execution entirely
- **`onAfterUpdate` trigger**: skip only if this specific `(actionId, entryId)` pair has already been processed

```java
// ObjectActionEngineImpl.java:204-229
Map<Long, Set<Long>> objectEntryIdsMap = ObjectActionThreadLocal.getObjectEntryIdsMap();

if (!StringUtil.equals(objectAction.getObjectActionTriggerKey(),
        ObjectActionTriggerConstants.KEY_ON_AFTER_UPDATE) &&
    objectEntryIdsMap.containsKey(objectAction.getObjectActionId())) {
    return;  // already ran; skip
}
// For onAfterUpdate, also check the specific entry ID...
```

This prevents **infinite loops** when an action (e.g., `update-object-entry`) triggers the same trigger it was launched from.

`ObjectActionThreadLocal` also exposes `isSkipObjectActionExecution()` as a global bypass flag for callers that need to suppress all action execution (e.g., bulk imports).

Source: `object-api/.../action/util/ObjectActionThreadLocal.java`

### 2. Post-Transaction Execution (Data Consistency)

`BaseObjectActionExecutor.execute()` registers work via `TransactionCommitCallbackUtil`. This means:

- Actions run **after** the triggering transaction commits successfully
- If the transaction rolls back, the action does not execute
- External systems (webhooks, functions) see committed, consistent data

### 3. Security Context Save/Restore

The `finally` block in `executeObjectActions()` guarantees the caller's security context (`PrincipalThreadLocal`, `PermissionThreadLocal`, `ObjectEntryThreadLocal`) is always restored, preventing context contamination across threads or request boundaries.

### 4. Scope-Based Execution Allowlists

Before delegating to an executor, the engine checks:

- **`CompanyScoped`** executors: verifies the action's company matches the executor's allowed company
- **`ObjectDefinitionScoped`** executors: verifies the object definition name is in the executor's allowlist

Violations throw `ObjectActionExecutorKeyException` immediately, preventing cross-tenant or cross-definition execution.

```java
// ObjectActionEngineImpl.java:247-278
if (objectActionExecutor instanceof CompanyScoped) {
    if (!((CompanyScoped)objectActionExecutor).isAllowedCompany(companyId)) {
        throw new ObjectActionExecutorKeyException(...);
    }
}
if (objectActionExecutor instanceof ObjectDefinitionScoped) {
    if (!((ObjectDefinitionScoped)objectActionExecutor).isAllowedObjectDefinition(name)) {
        throw new ObjectActionExecutorKeyException(...);
    }
}
```

### 5. Conditional Execution (DDM Expressions)

Actions can carry a `conditionExpression` evaluated by `DDMExpressionFactory`. If it returns `false`, the action is silently skipped — no status change, no error. This reduces unnecessary executions and avoids side effects from unwanted triggers.

Both the current model state (`baseModel`) and the original pre-update state (`originalBaseModel`) are available as expression variables, enabling delta-based conditions like "fire only if status changed to X".

### 6. Fail-Safe Multi-Action Loop

When multiple actions share a trigger, a failure in one action is logged and swallowed so subsequent actions still run. This means a broken webhook executor won't block a notification executor registered for the same event.

---

## Key File Reference

| File | Role |
|---|---|
| `object-api/.../constants/ObjectActionTriggerConstants.java` | Trigger key constants |
| `object-api/.../constants/ObjectActionConstants.java` | Status constants (`NEVER_RAN`, `SUCCESS`, `FAILED`) |
| `object-api/.../constants/ObjectActionExecutorConstants.java` | Executor key constants |
| `object-api/.../action/engine/ObjectActionEngine.java` | Engine interface |
| `object-api/.../action/executor/ObjectActionExecutor.java` | Executor interface |
| `object-api/.../action/executor/BaseObjectActionExecutor.java` | Post-transaction callback base class |
| `object-api/.../action/util/ObjectActionThreadLocal.java` | Thread-local duplicate-execution guard |
| `object-service/.../action/engine/ObjectActionEngineImpl.java` | Core orchestration: validation, condition eval, loop, error handling, status update |
| `object-service/.../action/executor/WebhookObjectActionExecutorImpl.java` | HTTP POST executor (synchronous, no retry) |
| `object-service/.../action/executor/FunctionObjectActionExecutorImpl.java` | PortalCatapult executor (company+definition scoped) |
| `object-service/.../action/executor/GroovyObjectActionExecutorImpl.java` | Groovy script executor |
| `object-service/.../action/executor/AddObjectEntryObjectActionExecutorImpl.java` | Creates object entries |
| `object-service/.../action/executor/UpdateObjectEntryObjectActionExecutorImpl.java` | Updates object entries |
| `object-service/.../action/executor/NotificationTemplateObjectActionExecutorImpl.java` | Sends notifications |
| `object-service/.../action/trigger/ObjectActionTriggerRegistryImpl.java` | OSGi ServiceTracker — dynamic trigger registration |
| `object-service/.../action/trigger/messaging/ObjectActionTriggerMessageListener.java` | Bridges messages to engine |
| `object-web/.../scheduler/CheckObjectEntrySchedulerJobConfiguration.java` | Periodic housekeeping scheduler (configurable interval) |
| `object-api/.../exception/` | All ObjectAction-specific exception types |
