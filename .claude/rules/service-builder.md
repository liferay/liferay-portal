# Service Builder

Use these procedures whenever a task requires modifying a Liferay Service Builder module bundle.

## Module Bundle Layout

A Service Builder feature lives in three sibling modules under `modules/apps/<area>`:

- `<name>-api` — generated public API.
- `<name>-service` — implementation. Hand-written `service.xml` and the `*Impl` classes drive everything else.
- `<name>-test` — integration tests.

Portal core follows the same split, with `portal-impl/service.xml` driving the API in `portal-kernel`.

## Files To Leave Alone

Every generated Java file is tagged `@generated` in its Javadoc — do not hand-edit anything carrying that tag; `buildService` rewrites it on each run. The same applies to the generated resources under `<name>-service/src/main/resources/META-INF`: `module-hbm.xml`, `portlet-model-hints.xml`, and the `sql` directory.

## Editing a Service

Hand edits are confined to two inputs in the `<name>-service` module:

- `service.xml` — entity, column, and finder definitions. Changing them regenerates the model, persistence, and base service classes.
- The `*Impl` classes `buildService` scaffolds on first run and preserves on every subsequent run — `model/impl/<Entity>Impl.java`, `service/impl/<Entity>LocalServiceImpl.java`, and `service/impl/<Entity>ServiceImpl.java`. Add or change methods here and let the tool regenerate the API interface, `*Util`, and `*Wrapper`.

Every edit must be followed by a regeneration and committed before any further work continues.

### Workflow

Run every step without asking for confirmation, including the commits.

1. Commit the hand-written `service.xml` or `*Impl` edit.

1. Run Service Builder.

1. Commit the changes the tool produces or rewrites, titled `<TICKET> buildService`. Keep the regenerated output on its own, separate from the edit that caused it.

1. Continue with the work.

## Running Service Builder

### A Single Module

Run `<gradlew> buildService` from the `<name>-service` module to regenerate that module alone.

### Every Module

To regenerate every Service Builder module in one pass, run `ant build-services` from `portal-impl`:

```bash
(cd "${REPO_ROOT}/portal-impl" && ant build-services)
```