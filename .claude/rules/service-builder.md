---

paths:
  - "modules/**/*-api/bnd.bnd"
  - "modules/**/*-api/src/main/java/**/service/*Service.java"
  - "modules/**/*-api/src/main/java/**/service/*ServiceUtil.java"
  - "modules/**/*-api/src/main/java/**/service/*ServiceWrapper.java"
  - "modules/**/*-api/src/main/resources/**/packageinfo"
  - "modules/**/*-service/service.xml"
  - "modules/**/*-service/src/main/java/**/service/impl/*Impl.java"
  - "modules/**/*-test/src/testIntegration/java/**/service/persistence/test/*PersistenceTest.java"

---

# Service Builder

All Service Builder API artifacts — `*LocalService.java`, `*Service.java`, `*LocalServiceUtil.java`, `*ServiceUtil.java`, `*LocalServiceWrapper.java`, `*ServiceWrapper.java`, `packageinfo`, and the `bnd.bnd` version bump in the `*-api` module — are generated. Do not hand-edit them. Add or change methods on the `*LocalServiceImpl` or `*ServiceImpl` class in the `*-service` module and let `buildService` regenerate the API. Entity, column, and finder definitions live in `service.xml`; changing them regenerates models, persistence, and base service classes the same way.

## Adding or Updating Entities

1. Edit `modules/.../<module>-service/service.xml`.

1. Commit the `service.xml` edit.

1. Run `<gradlew> buildService` from `modules/.../<module>-service` to regenerate the model, persistence, base service, and API artifacts.

1. Commit the generated files.

## Adding or Updating Service Methods

1. Edit the impl in `modules/.../<module>-service/src/main/java/.../service/impl/<Entity>LocalServiceImpl.java` or `<Entity>ServiceImpl.java`.

1. Commit the impl edit.

1. Run `<gradlew> buildService` from `modules/.../<module>-service` to regenerate the API interface, `*Util`, `*Wrapper`, `packageinfo`, and `bnd.bnd`.

1. Commit the generated files.