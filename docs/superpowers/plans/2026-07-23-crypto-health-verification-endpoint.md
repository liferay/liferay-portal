# Crypto Health Verification Endpoint Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Expose an authenticated REST endpoint that re-runs the validated FIPS provider's self-tests on demand and reports the outcome, with no runtime side effects.

**Architecture:** A side-effect-free `FIPSModeValidator.runSelfTests()` in `portal-kernel` drives the provider self-tests through a reflective executor and returns an immutable `FIPSHealthCheckResult`. A REST Builder module cluster (`portal-security-fips-rest-{api,impl,client,test}`) exposes `POST /o/crypto-health/v1.0/health-verifications`, gates it on Crypto Officer role membership, and maps the result to HTTP 200 / 409 / 503.

**Tech Stack:** Java, portal-kernel (Ant), OSGi + REST Builder + Vulcan (Gradle), JUnit 4, Mockito 5.4.0, Arquillian integration tests.

## Global Constraints

- Every commit title is prefixed `LPD-93272`.
- Tests live in their own commits, separate from production code.
- Language keys go in the global `modules/apps/portal-language/portal-language-lang/src/main/resources/content/Language.properties` (alphabetical); generated `Language_<locale>.properties` files land in a separate `LPD-93272 buildLang` commit.
- Do NOT hand-edit `@Generated`/`@generated` files; `buildREST` owns them.
- No Error State, no crypto halt, no audit event — those are out of scope (split story, depends on LPD-93276).
- `<gradlew>` from an impl/api/client/test module dir is `../../../../gradlew`.
- Kernel license header on every new Java file:
  ```java
  /**
   * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
   * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
   */
  ```

## File Structure

Kernel (`portal-kernel`):
- `src/com/liferay/portal/kernel/security/fips/FIPSHealthCheckResult.java` — immutable result value type.
- `src/com/liferay/portal/kernel/security/fips/FIPSSelfTestException.java` — self-test failure carrier.
- `src/com/liferay/portal/kernel/security/fips/FIPSSelfTestExecutor.java` — executor seam interface.
- `src/com/liferay/portal/kernel/security/fips/ReflectionFIPSSelfTestExecutor.java` — reflective BCFIPS/Corretto executor.
- `src/com/liferay/portal/kernel/security/fips/FIPSModeValidator.java` — MODIFY: add `runSelfTests()` + two fields.
- `test/unit/.../fips/FIPSHealthCheckResultTest.java`, `.../fips/FIPSModeValidatorTest.java` (MODIFY).

REST cluster (`modules/apps/portal-security`):
- `portal-security-fips-rest-api` — generated API (hand-authored bnd/build/markers only).
- `portal-security-fips-rest-impl` — hand-authored `rest-config.yaml`, `rest-openapi.yaml`, `FIPSActionKeys`, `CryptoOfficerRolePortalInstanceLifecycleListener`, `HealthVerificationResourceImpl`, unit test; generated base resource/application.
- `portal-security-fips-rest-client` — generated client.
- `portal-security-fips-rest-test` — integration test.

---

### Task 1: Kernel value types

**Files:**
- Create: `portal-kernel/src/com/liferay/portal/kernel/security/fips/FIPSHealthCheckResult.java`
- Create: `portal-kernel/src/com/liferay/portal/kernel/security/fips/FIPSSelfTestException.java`
- Create: `portal-kernel/src/com/liferay/portal/kernel/security/fips/FIPSSelfTestExecutor.java`
- Test: `portal-kernel/test/unit/com/liferay/portal/kernel/security/fips/FIPSHealthCheckResultTest.java`

**Interfaces:**
- Produces: `FIPSHealthCheckResult` with `enum Status { FAILED, HEALTHY, NOT_APPLICABLE }`; factories `healthy(String providerName)`, `failed(String providerName, String failedTest, String fipsState, String providerMessage)`, `notApplicable()`; getters `getStatus()`, `getProviderName()`, `getFailedTest()`, `getFipsState()`, `getProviderMessage()`.
- Produces: `FIPSSelfTestException(String providerName, String failedTest, String fipsState, String providerMessage)` extends `Exception`, same getters.
- Produces: `FIPSSelfTestExecutor` with `String execute() throws Exception`.

- [ ] **Step 1: Write the failing test**

Create `FIPSHealthCheckResultTest.java`:

```java
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security.fips;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Lucas Miranda
 */
public class FIPSHealthCheckResultTest {

	@Test
	public void testFailed() {
		FIPSHealthCheckResult result = FIPSHealthCheckResult.failed(
			"BCFIPS", "AES-KAT", "ERROR", "boom");

		Assert.assertEquals(
			FIPSHealthCheckResult.Status.FAILED, result.getStatus());
		Assert.assertEquals("BCFIPS", result.getProviderName());
		Assert.assertEquals("AES-KAT", result.getFailedTest());
		Assert.assertEquals("ERROR", result.getFipsState());
		Assert.assertEquals("boom", result.getProviderMessage());
	}

	@Test
	public void testHealthy() {
		FIPSHealthCheckResult result = FIPSHealthCheckResult.healthy("BCFIPS");

		Assert.assertEquals(
			FIPSHealthCheckResult.Status.HEALTHY, result.getStatus());
		Assert.assertEquals("BCFIPS", result.getProviderName());
		Assert.assertNull(result.getFailedTest());
	}

	@Test
	public void testNotApplicable() {
		FIPSHealthCheckResult result = FIPSHealthCheckResult.notApplicable();

		Assert.assertEquals(
			FIPSHealthCheckResult.Status.NOT_APPLICABLE, result.getStatus());
	}

}
```

- [ ] **Step 2: Run test to verify it fails**

Run from `portal-kernel`:
```bash
ant test-class -Dtest.class=FIPSHealthCheckResultTest
```
Expected: compilation failure — `FIPSHealthCheckResult` does not exist.

- [ ] **Step 3: Write the value types**

Create `FIPSHealthCheckResult.java`:

```java
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security.fips;

/**
 * @author Lucas Miranda
 */
public class FIPSHealthCheckResult {

	public static FIPSHealthCheckResult failed(
		String providerName, String failedTest, String fipsState,
		String providerMessage) {

		return new FIPSHealthCheckResult(
			Status.FAILED, providerName, failedTest, fipsState,
			providerMessage);
	}

	public static FIPSHealthCheckResult healthy(String providerName) {
		return new FIPSHealthCheckResult(
			Status.HEALTHY, providerName, null, null, null);
	}

	public static FIPSHealthCheckResult notApplicable() {
		return new FIPSHealthCheckResult(
			Status.NOT_APPLICABLE, null, null, null, null);
	}

	public String getFailedTest() {
		return _failedTest;
	}

	public String getFipsState() {
		return _fipsState;
	}

	public String getProviderMessage() {
		return _providerMessage;
	}

	public String getProviderName() {
		return _providerName;
	}

	public Status getStatus() {
		return _status;
	}

	public enum Status {

		FAILED, HEALTHY, NOT_APPLICABLE

	}

	private FIPSHealthCheckResult(
		Status status, String providerName, String failedTest, String fipsState,
		String providerMessage) {

		_status = status;
		_providerName = providerName;
		_failedTest = failedTest;
		_fipsState = fipsState;
		_providerMessage = providerMessage;
	}

	private final String _failedTest;
	private final String _fipsState;
	private final String _providerMessage;
	private final String _providerName;
	private final Status _status;

}
```

Create `FIPSSelfTestException.java`:

```java
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security.fips;

/**
 * @author Lucas Miranda
 */
public class FIPSSelfTestException extends Exception {

	public FIPSSelfTestException(
		String providerName, String failedTest, String fipsState,
		String providerMessage) {

		super(providerMessage);

		_providerName = providerName;
		_failedTest = failedTest;
		_fipsState = fipsState;
		_providerMessage = providerMessage;
	}

	public String getFailedTest() {
		return _failedTest;
	}

	public String getFipsState() {
		return _fipsState;
	}

	public String getProviderMessage() {
		return _providerMessage;
	}

	public String getProviderName() {
		return _providerName;
	}

	private final String _failedTest;
	private final String _fipsState;
	private final String _providerMessage;
	private final String _providerName;

}
```

Create `FIPSSelfTestExecutor.java`:

```java
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security.fips;

/**
 * @author Lucas Miranda
 */
public interface FIPSSelfTestExecutor {

	/**
	 * Forces the validated provider to re-run its self-tests and re-verifies
	 * approved mode. Returns the provider name on success. Throws {@link
	 * FIPSSelfTestException} on a detected self-test failure; any other
	 * exception signals an unverifiable state and is treated as failure
	 * (fail-closed) by the caller.
	 */
	public String execute() throws Exception;

}
```

- [ ] **Step 4: Run test to verify it passes**

Run from `portal-kernel`:
```bash
ant test-class -Dtest.class=FIPSHealthCheckResultTest
```
Expected: BUILD SUCCESSFUL, 3 tests pass.

- [ ] **Step 5: Commit**

```bash
git add portal-kernel/src/com/liferay/portal/kernel/security/fips/FIPSHealthCheckResult.java \
        portal-kernel/src/com/liferay/portal/kernel/security/fips/FIPSSelfTestException.java \
        portal-kernel/src/com/liferay/portal/kernel/security/fips/FIPSSelfTestExecutor.java
git commit -m "LPD-93272 Add FIPS self-test result, exception, and executor seam"
git add portal-kernel/test/unit/com/liferay/portal/kernel/security/fips/FIPSHealthCheckResultTest.java
git commit -m "LPD-93272 Add unit tests for the FIPS self-test result"
```

---

### Task 2: Reflective self-test executor

**Files:**
- Create: `portal-kernel/src/com/liferay/portal/kernel/security/fips/ReflectionFIPSSelfTestExecutor.java`

**Interfaces:**
- Consumes: `FIPSSelfTestExecutor`, `FIPSSelfTestException` (Task 1).
- Produces: `ReflectionFIPSSelfTestExecutor implements FIPSSelfTestExecutor` (default no-arg constructor).

This executor has no standalone unit test; it is exercised through `FIPSModeValidator.runSelfTests()` in Task 3 (the tests swap in a fake executor). Committing it here keeps production code and its consumer separate.

- [ ] **Step 1: Create the executor**

Create `ReflectionFIPSSelfTestExecutor.java`:

```java
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security.fips;

import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;

import java.lang.reflect.Method;

import java.security.Provider;
import java.security.Security;

import java.util.Objects;

/**
 * Re-runs the validated provider's self-tests on demand. BCFIPS and Amazon
 * Corretto are driven reflectively so portal-kernel keeps no compile-time
 * dependency on the provider jars.
 *
 * @author Lucas Miranda
 */
public class ReflectionFIPSSelfTestExecutor implements FIPSSelfTestExecutor {

	@Override
	public String execute() throws Exception {
		Provider[] providers = Security.getProviders();

		if (ArrayUtil.isEmpty(providers)) {
			throw new FIPSSelfTestException(
				null, "provider-presence", null,
				"There are no security providers");
		}

		Provider provider = providers[0];

		String name = provider.getName();

		if (Objects.equals(name, "AmazonCorrettoCryptoProvider")) {
			_reverifyAmazonCorretto(provider);
		}
		else if (Objects.equals(name, "BCFIPS")) {
			_reverifyBCFIPS(provider);
		}
		else {
			throw new FIPSSelfTestException(
				name, "provider-identity", null,
				"The first security provider is not an allowed FIPS provider");
		}

		return name;
	}

	private void _reverifyAmazonCorretto(Provider provider) throws Exception {
		try {
			Class<?> providerClass = provider.getClass();

			Method assertHealthyMethod = ReflectionUtil.getDeclaredMethod(
				providerClass, "assertHealthy");

			assertHealthyMethod.invoke(provider);
		}
		catch (Exception exception) {
			throw new FIPSSelfTestException(
				"AmazonCorrettoCryptoProvider", "assertHealthy", null,
				_rootMessage(exception));
		}
	}

	private void _reverifyBCFIPS(Provider provider) throws Exception {
		try {
			ClassLoader classLoader = provider.getClass(
			).getClassLoader();

			Class<?> cryptoServicesRegistrarClass = Class.forName(
				"org.bouncycastle.crypto.CryptoServicesRegistrar", true,
				classLoader);

			Class<?> fipsStatusClass = Class.forName(
				"org.bouncycastle.crypto.fips.FipsStatus", true, classLoader);

			// Force the module self-tests (KATs) to run again. This reflective
			// symbol is unverified against a live bc-fips jar (see LPD-93272 /
			// LPD-80674); a missing method fails closed into a FAILED result.

			Method runSelfTestsMethod = ReflectionUtil.getDeclaredMethod(
				fipsStatusClass, "runSelfTests");

			runSelfTestsMethod.invoke(null);

			Method isInApprovedOnlyModeMethod =
				ReflectionUtil.getDeclaredMethod(
					cryptoServicesRegistrarClass, "isInApprovedOnlyMode");

			if (!GetterUtil.getBoolean(
					isInApprovedOnlyModeMethod.invoke(null))) {

				throw new FIPSSelfTestException(
					"BCFIPS", "approved-only-mode", "NOT_APPROVED",
					"BCFIPS is not in approved only mode");
			}

			Method isReadyMethod = ReflectionUtil.getDeclaredMethod(
				fipsStatusClass, "isReady");

			if (!GetterUtil.getBoolean(isReadyMethod.invoke(null))) {
				Method getStatusMessageMethod =
					ReflectionUtil.getDeclaredMethod(
						fipsStatusClass, "getStatusMessage");

				throw new FIPSSelfTestException(
					"BCFIPS", "integrity-self-test",
					String.valueOf(getStatusMessageMethod.invoke(null)),
					"BCFIPS integrity self test failed");
			}
		}
		catch (FIPSSelfTestException fipsSelfTestException) {
			throw fipsSelfTestException;
		}
		catch (Exception exception) {
			throw new FIPSSelfTestException(
				"BCFIPS", "self-test-invocation", null,
				_rootMessage(exception));
		}
	}

	private String _rootMessage(Throwable throwable) {
		Throwable causeThrowable = throwable.getCause();

		if (causeThrowable == null) {
			causeThrowable = throwable;
		}

		String message = causeThrowable.getMessage();

		if (message == null) {
			return causeThrowable.toString();
		}

		return message;
	}

}
```

- [ ] **Step 2: Verify it compiles**

Run from `portal-kernel`:
```bash
ant compile
```
Expected: BUILD SUCCESSFUL.

- [ ] **Step 3: Commit**

```bash
git add portal-kernel/src/com/liferay/portal/kernel/security/fips/ReflectionFIPSSelfTestExecutor.java
git commit -m "LPD-93272 Add reflective FIPS self-test executor"
```

---

### Task 3: Side-effect-free `runSelfTests()` on `FIPSModeValidator`

**Files:**
- Modify: `portal-kernel/src/com/liferay/portal/kernel/security/fips/FIPSModeValidator.java`
- Test: `portal-kernel/test/unit/com/liferay/portal/kernel/security/fips/FIPSModeValidatorTest.java` (MODIFY — add tests)

**Interfaces:**
- Consumes: `FIPSHealthCheckResult`, `FIPSSelfTestExecutor`, `FIPSSelfTestException`, `ReflectionFIPSSelfTestExecutor` (Tasks 1-2).
- Produces: `public static FIPSHealthCheckResult FIPSModeValidator.runSelfTests()`.

Note: `runSelfTests()` has NO error-state side effects. It does not set any flag, does not affect `validateAlgorithm`/`validateKey`, and there is no `isInErrorState()` method. On failure it returns a `FAILED` result and nothing else changes.

- [ ] **Step 1: Write the failing tests**

The existing `FIPSModeValidatorTest.java` (author `Caio Farias`) already tests `validateAlgorithm`, `validateKey`, etc. Add these four methods to the class body, and add the two private helpers at the bottom. Add `import com.liferay.petra.lang.SafeCloseable;`, `import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;`, and `import org.junit.After;` if not present.

```java
	@After
	public void tearDown() {
		ReflectionTestUtil.setFieldValue(
			FIPSModeValidator.class, "_fipsSelfTestExecutor",
			new ReflectionFIPSSelfTestExecutor());
	}

	@Test
	public void testRunSelfTestsFailsClosedOnUnexpectedError() {
		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"FIPS_ENABLED", true)) {

			_swapExecutor(
				() -> {
					throw new RuntimeException("reflection blew up");
				});

			FIPSHealthCheckResult result = FIPSModeValidator.runSelfTests();

			Assert.assertEquals(
				FIPSHealthCheckResult.Status.FAILED, result.getStatus());
		}
	}

	@Test
	public void testRunSelfTestsFailure() {
		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"FIPS_ENABLED", true)) {

			_swapExecutor(
				() -> {
					throw new FIPSSelfTestException(
						"BCFIPS", "AES-KAT", "ERROR", "integrity failure");
				});

			FIPSHealthCheckResult result = FIPSModeValidator.runSelfTests();

			Assert.assertEquals(
				FIPSHealthCheckResult.Status.FAILED, result.getStatus());
			Assert.assertEquals("AES-KAT", result.getFailedTest());

			// Running the self-tests must not halt crypto operations in this
			// story; validateAlgorithm still works after a failure.

			FIPSModeValidator.validateAlgorithm("AES");
		}
	}

	@Test
	public void testRunSelfTestsNotApplicableWhenFIPSDisabled() {
		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"FIPS_ENABLED", false)) {

			FIPSHealthCheckResult result = FIPSModeValidator.runSelfTests();

			Assert.assertEquals(
				FIPSHealthCheckResult.Status.NOT_APPLICABLE,
				result.getStatus());
		}
	}

	@Test
	public void testRunSelfTestsSuccess() {
		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"FIPS_ENABLED", true)) {

			_swapExecutor(() -> "BCFIPS");

			FIPSHealthCheckResult result = FIPSModeValidator.runSelfTests();

			Assert.assertEquals(
				FIPSHealthCheckResult.Status.HEALTHY, result.getStatus());
			Assert.assertEquals("BCFIPS", result.getProviderName());
		}
	}
```

Add these private helpers next to the existing `_assertSecurityException`/`_createProvider` helpers:

```java
	private void _swapExecutor(FIPSSelfTestExecutor fipsSelfTestExecutor) {
		ReflectionTestUtil.setFieldValue(
			FIPSModeValidator.class, "_fipsSelfTestExecutor",
			fipsSelfTestExecutor);
	}
```

- [ ] **Step 2: Run tests to verify they fail**

Run from `portal-kernel`:
```bash
ant test-class -Dtest.class=FIPSModeValidatorTest
```
Expected: compilation failure — `runSelfTests` and `_fipsSelfTestExecutor` do not exist yet.

- [ ] **Step 3: Add `runSelfTests()` and its fields**

In `FIPSModeValidator.java`, add these imports:
```java
import com.liferay.portal.kernel.util.PropsValues;
```
(`PropsValues` is likely already imported — add only if missing.)

Add the public method (place it before `validate()`):

```java
	public static FIPSHealthCheckResult runSelfTests() {
		if (!PropsValues.FIPS_ENABLED) {
			return FIPSHealthCheckResult.notApplicable();
		}

		synchronized (_selfTestLock) {
			try {
				String providerName = _fipsSelfTestExecutor.execute();

				return FIPSHealthCheckResult.healthy(providerName);
			}
			catch (FIPSSelfTestException fipsSelfTestException) {
				return FIPSHealthCheckResult.failed(
					fipsSelfTestException.getProviderName(),
					fipsSelfTestException.getFailedTest(),
					fipsSelfTestException.getFipsState(),
					fipsSelfTestException.getProviderMessage());
			}
			catch (Exception exception) {
				return FIPSHealthCheckResult.failed(
					null, "self-test-execution", null, exception.getMessage());
			}
		}
	}
```

Add these two fields to the private static field block at the bottom of the class (alphabetical among the `_` fields):

```java
	private static final FIPSSelfTestExecutor _fipsSelfTestExecutor =
		new ReflectionFIPSSelfTestExecutor();
```
```java
	private static final Object _selfTestLock = new Object();
```

Do NOT add `_fipsErrorState`, `_checkErrorState()`, `isInErrorState()`, or any guard call in `validateAlgorithm`/`validateKey`.

- [ ] **Step 4: Run tests to verify they pass**

Run from `portal-kernel`:
```bash
ant test-class -Dtest.class=FIPSModeValidatorTest
```
Expected: BUILD SUCCESSFUL; the four new tests plus the pre-existing ones pass.

- [ ] **Step 5: Commit**

```bash
git add portal-kernel/src/com/liferay/portal/kernel/security/fips/FIPSModeValidator.java
git commit -m "LPD-93272 Add on-demand FIPS self-test"
git add portal-kernel/test/unit/com/liferay/portal/kernel/security/fips/FIPSModeValidatorTest.java
git commit -m "LPD-93272 Add unit tests for the on-demand FIPS self-test"
```

- [ ] **Step 6: Deploy the kernel** (needed before the integration test in Task 7)

```bash
cd portal-kernel && ant deploy install-portal-snapshot
```
This requires a server restart to take effect (do it before running Task 7).

---

### Task 4: Scaffold and generate the REST module cluster

**Files (hand-authored):**
- Create markers: `portal-security-fips-rest-{api,impl,client}/.lfrbuild-portal` (empty)
- Create: `portal-security-fips-rest-api/{bnd.bnd,build.gradle}`
- Create: `portal-security-fips-rest-impl/{bnd.bnd,build.gradle,rest-config.yaml,rest-openapi.yaml}`
- Create: `portal-security-fips-rest-client/{bnd.bnd,build.gradle}`
- Create: `portal-security-fips-rest-test/{bnd.bnd,build.gradle}`

**Interfaces:**
- Produces (after `buildREST`): DTO `com.liferay.portal.security.fips.rest.dto.v1_0.HealthVerification` with setters `setDate(UnsafeSupplier)`, `setFailedTest(...)`, `setFipsState(...)`, `setProviderMessage(...)`, `setProviderName(...)`, `setStatus(...)` and nested `HealthVerification.Status.create(String)`; base class `BaseHealthVerificationResourceImpl` with abstract `HealthVerification postHealthVerification()`; interface `com.liferay.portal.security.fips.rest.resource.v1_0.HealthVerificationResource`.

Base path for all files: `modules/apps/portal-security/`.

- [ ] **Step 1: Create the marker and hand-authored files**

`portal-security-fips-rest-api/bnd.bnd`:
```
Bundle-Name: Liferay Crypto Health REST API
Bundle-SymbolicName: com.liferay.portal.security.fips.rest.api
Bundle-Version: 1.0.0
Export-Package:\
	com.liferay.portal.security.fips.rest.dto.v1_0,\
	com.liferay.portal.security.fips.rest.resource.v1_0
```

`portal-security-fips-rest-api/build.gradle`:
```
dependencies {
	compileOnly group: "com.fasterxml.jackson.core", name: "jackson-annotations", version: "2.18.6"
	compileOnly group: "com.liferay", name: "jakarta.ws.rs", version: "3.1.0.LIFERAY-PATCHED-1"
	compileOnly group: "com.liferay.portal", name: "com.liferay.portal.kernel", version: "default"
	compileOnly group: "io.swagger.core.v3", name: "swagger-annotations-jakarta", version: "2.2.28"
	compileOnly group: "jakarta.annotation", name: "jakarta.annotation-api", version: "2.1.1"
	compileOnly group: "jakarta.servlet", name: "jakarta.servlet-api", version: "6.0.0"
	compileOnly group: "jakarta.validation", name: "jakarta.validation-api", version: "3.1.0"
	compileOnly group: "jakarta.xml.bind", name: "jakarta.xml.bind-api", version: "4.0.2"
	compileOnly group: "org.osgi", name: "org.osgi.annotation.versioning", version: "1.1.0"
	compileOnly project(":apps:portal-odata:portal-odata-api")
	compileOnly project(":apps:portal-vulcan:portal-vulcan-api")
	compileOnly project(":core:petra:petra-function")
	compileOnly project(":core:petra:petra-string")
}
```

`portal-security-fips-rest-impl/bnd.bnd`:
```
Bundle-Name: Liferay Crypto Health REST Implementation
Bundle-SymbolicName: com.liferay.portal.security.fips.rest.impl
Bundle-Version: 1.0.0
```

`portal-security-fips-rest-impl/build.gradle`:
```
dependencies {
	compileOnly group: "com.liferay", name: "jakarta.ws.rs", version: "3.1.0.LIFERAY-PATCHED-1"
	compileOnly group: "com.liferay.portal", name: "com.liferay.portal.kernel", version: "default"
	compileOnly group: "io.swagger.core.v3", name: "swagger-annotations-jakarta", version: "2.2.28"
	compileOnly group: "jakarta.annotation", name: "jakarta.annotation-api", version: "2.1.1"
	compileOnly group: "jakarta.servlet", name: "jakarta.servlet-api", version: "6.0.0"
	compileOnly group: "org.osgi", name: "org.osgi.service.component", version: "1.4.0"
	compileOnly group: "org.osgi", name: "org.osgi.service.component.annotations", version: "1.4.0"
	compileOnly group: "org.osgi", name: "osgi.core", version: "6.0.0"
	compileOnly project(":apps:portal-odata:portal-odata-api")
	compileOnly project(":apps:portal-security:portal-security-fips-rest-api")
	compileOnly project(":apps:portal-vulcan:portal-vulcan-api")
	compileOnly project(":apps:portal:portal-instance-lifecycle-api")
	compileOnly project(":core:petra:petra-function")
	testImplementation group: "com.liferay.portal", name: "com.liferay.portal.test", version: "default"
	testImplementation group: "org.mockito", name: "mockito-core", version: "5.4.0"
}
```

`portal-security-fips-rest-impl/rest-config.yaml`:
```yaml
apiDir: ../portal-security-fips-rest-api/src/main/java
apiPackagePath: com.liferay.portal.security.fips.rest
application:
    baseURI: /crypto-health
    className: CryptoHealthApplication
    name: Liferay.Crypto.Health.REST
author: Lucas Miranda
clientDir: ../portal-security-fips-rest-client/src/main/java
compatibilityVersion: 15
forcePredictableOperationId: true
javaEEPackage: jakarta
testDir: ../portal-security-fips-rest-test/src/testIntegration/java
```

`portal-security-fips-rest-impl/rest-openapi.yaml`:
```yaml
components:
    schemas:
        HealthVerification:
            description:
                "Result of an on-demand cryptographic provider self-test re-verification."
            properties:
                date:
                    description:
                        "When the verification ran."
                    format: date-time
                    type: string
                failedTest:
                    description:
                        "The self-test that failed, if any."
                    type: string
                fipsState:
                    description:
                        "The provider FIPS state at the time of failure."
                    type: string
                providerMessage:
                    description:
                        "The provider exception message on failure."
                    type: string
                providerName:
                    description:
                        "The active FIPS provider name."
                    type: string
                status:
                    description:
                        "The verification outcome."
                    enum: ["HEALTHY", "FAILED", "NOT_APPLICABLE"]
                    type: string
            type: object
info:
    description:
        "Periodic cryptographic health verification.. A Java client JAR is available for use with the group ID 'com.liferay', artifact ID 'com.liferay.portal.security.fips.rest.client', and version '1.0.0'."
    license:
        name: Apache 2.0
        url: http://www.apache.org/licenses/LICENSE-2.0.html
    title: Crypto Health
    version: v1.0
openapi: 3.0.1
paths:
    /health-verifications:
        post:
            description:
                "Forces the validated FIPS provider to re-run its self-tests on demand."
            operationId: postHealthVerification
            responses:
                200:
                    content:
                        application/json:
                            schema:
                                $ref: "#/components/schemas/HealthVerification"
                        application/xml:
                            schema:
                                $ref: "#/components/schemas/HealthVerification"
            tags: ["HealthVerification"]
tags:
    -   name: HealthVerification
```

`portal-security-fips-rest-client/bnd.bnd`:
```
Bundle-Name: Liferay Crypto Health REST Client
Bundle-SymbolicName: com.liferay.portal.security.fips.rest.client
Bundle-Version: 1.0.0
Export-Package:\
	com.liferay.portal.security.fips.rest.client.dto.v1_0,\
	com.liferay.portal.security.fips.rest.client.function
```

`portal-security-fips-rest-client/build.gradle`:
```
dependencies {
	compileOnly group: "jakarta.annotation", name: "jakarta.annotation-api", version: "2.1.1"
}
```

`portal-security-fips-rest-test/bnd.bnd`:
```
Bundle-Name: Liferay Crypto Health REST Test
Bundle-SymbolicName: com.liferay.portal.security.fips.rest.test
Bundle-Version: 1.0.0
-includeresource: com.liferay.portal.security.fips.rest.client.jar=com.liferay.portal.security.fips.rest.client-[0-9.]*.jar;lib:=true
```

`portal-security-fips-rest-test/build.gradle`:
```
dependencies {
	testIntegrationImplementation group: "com.fasterxml.jackson.core", name: "jackson-databind", version: "2.18.6"
	testIntegrationImplementation group: "com.liferay", name: "jakarta.ws.rs", version: "3.1.0.LIFERAY-PATCHED-1"
	testIntegrationImplementation group: "jakarta.annotation", name: "jakarta.annotation-api", version: "2.1.1"
	testIntegrationImplementation project(":apps:portal-odata:portal-odata-api")
	testIntegrationImplementation project(":apps:portal-security:portal-security-fips-rest-api")
	testIntegrationImplementation project(":apps:portal-security:portal-security-fips-rest-client")
	testIntegrationImplementation project(":apps:portal-vulcan:portal-vulcan-api")
	testIntegrationImplementation project(":test:arquillian-extension-junit-bridge")
}
```

Create empty marker files:
```bash
cd modules/apps/portal-security
touch portal-security-fips-rest-api/.lfrbuild-portal \
      portal-security-fips-rest-impl/.lfrbuild-portal \
      portal-security-fips-rest-client/.lfrbuild-portal
```

- [ ] **Step 2: Commit the hand-authored files**

```bash
git add modules/apps/portal-security/portal-security-fips-rest-api \
        modules/apps/portal-security/portal-security-fips-rest-impl \
        modules/apps/portal-security/portal-security-fips-rest-client \
        modules/apps/portal-security/portal-security-fips-rest-test
git commit -m "LPD-93272 Add crypto health REST module scaffolding"
```

- [ ] **Step 3: Run REST Builder**

```bash
cd modules/apps/portal-security/portal-security-fips-rest-impl
../../../../gradlew buildREST
```
Expected: generates the API DTO/resource, client, and `internal/.../BaseHealthVerificationResourceImpl.java`, `CryptoHealthApplication`, GraphQL, OpenAPI resource, and a scaffold `HealthVerificationResourceImpl.java`.

- [ ] **Step 4: Commit generated output**

```bash
git add modules/apps/portal-security
git commit -m "LPD-93272 Generate crypto health REST API, client, and base resource"
```

---

### Task 5: Crypto Officer role and action keys

**Files:**
- Create: `.../portal-security-fips-rest-impl/src/main/java/com/liferay/portal/security/fips/rest/internal/constants/FIPSActionKeys.java`
- Create: `.../internal/instance/lifecycle/CryptoOfficerRolePortalInstanceLifecycleListener.java`
- Modify: `modules/apps/portal-language/portal-language-lang/src/main/resources/content/Language.properties`

**Interfaces:**
- Produces: `FIPSActionKeys.CRYPTO_OFFICER_ROLE_NAME` = `"Crypto Officer"`, `FIPSActionKeys.TRIGGER_HEALTH_VERIFICATION`.
- Produces: a per-company regular role named "Crypto Officer" created at portal instance registration.

- [ ] **Step 1: Add the language key**

In `Language.properties`, add in alphabetical position:
```
crypto-officer=Crypto Officer
```

- [ ] **Step 2: Commit the source language edit**

```bash
git add modules/apps/portal-language/portal-language-lang/src/main/resources/content/Language.properties
git commit -m "LPD-93272 Add the Crypto Officer language key"
```

- [ ] **Step 3: Generate translations**

```bash
cd modules/apps/portal-language/portal-language-lang
../../../../gradlew buildLang
```

- [ ] **Step 4: Commit the generated language files**

```bash
git add modules/apps/portal-language/portal-language-lang/src/main/resources/content/Language_*.properties
git commit -m "LPD-93272 buildLang"
```

- [ ] **Step 5: Create the constants class**

`FIPSActionKeys.java`:
```java
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.fips.rest.internal.constants;

/**
 * @author Lucas Miranda
 */
public class FIPSActionKeys {

	public static final String CRYPTO_OFFICER_ROLE_NAME = "Crypto Officer";

	public static final String TRIGGER_HEALTH_VERIFICATION =
		"TRIGGER_HEALTH_VERIFICATION";

}
```

- [ ] **Step 6: Create the role lifecycle listener**

`CryptoOfficerRolePortalInstanceLifecycleListener.java`:
```java
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.fips.rest.internal.instance.lifecycle;

import com.liferay.portal.instance.lifecycle.BasePortalInstanceLifecycleListener;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.security.fips.rest.internal.constants.FIPSActionKeys;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lucas Miranda
 */
@Component(service = PortalInstanceLifecycleListener.class)
public class CryptoOfficerRolePortalInstanceLifecycleListener
	extends BasePortalInstanceLifecycleListener {

	@Override
	public void portalInstanceRegistered(Company company) throws Exception {
		Role role = _roleLocalService.fetchRole(
			company.getCompanyId(), FIPSActionKeys.CRYPTO_OFFICER_ROLE_NAME);

		if (role != null) {
			return;
		}

		User guestUser = company.getGuestUser();

		_roleLocalService.addRole(
			null, guestUser.getUserId(), null, 0,
			FIPSActionKeys.CRYPTO_OFFICER_ROLE_NAME, null,
			HashMapBuilder.put(
				company.getLocale(),
				_language.get(company.getLocale(), "crypto-officer")
			).build(),
			RoleConstants.TYPE_REGULAR, null, null);
	}

	@Reference
	private Language _language;

	@Reference
	private RoleLocalService _roleLocalService;

}
```

- [ ] **Step 7: Commit**

```bash
git add modules/apps/portal-security/portal-security-fips-rest-impl/src/main/java/com/liferay/portal/security/fips/rest/internal/constants/FIPSActionKeys.java \
        modules/apps/portal-security/portal-security-fips-rest-impl/src/main/java/com/liferay/portal/security/fips/rest/internal/instance/lifecycle/CryptoOfficerRolePortalInstanceLifecycleListener.java
git commit -m "LPD-93272 Add Crypto Officer role and health verification action keys"
```

---

### Task 6: Resource implementation + unit test

**Files:**
- Modify (overwrite the buildREST scaffold): `.../internal/resource/v1_0/HealthVerificationResourceImpl.java`
- Test: `.../portal-security-fips-rest-impl/src/test/java/com/liferay/portal/security/fips/rest/internal/resource/v1_0/HealthVerificationResourceImplTest.java`

**Interfaces:**
- Consumes: `FIPSModeValidator.runSelfTests()` (Task 3); `FIPSHealthCheckResult` (Task 1); `FIPSActionKeys` (Task 5); generated `HealthVerification`, `BaseHealthVerificationResourceImpl` (Task 4).
- Produces: `HealthVerification postHealthVerification()` returning 200 (HEALTHY) / 409 (NOT_APPLICABLE) / 503 (FAILED), gated on Crypto Officer role.

- [ ] **Step 1: Write the failing unit test**

`HealthVerificationResourceImplTest.java`:
```java
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.fips.rest.internal.resource.v1_0;

import com.liferay.portal.kernel.security.fips.FIPSHealthCheckResult;
import com.liferay.portal.kernel.security.fips.FIPSModeValidator;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.security.fips.rest.dto.v1_0.HealthVerification;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.RuntimeDelegate;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Lucas Miranda
 */
public class HealthVerificationResourceImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_healthVerificationResourceImpl = new HealthVerificationResourceImpl();

		ReflectionTestUtil.setFieldValue(
			_healthVerificationResourceImpl, "_roleLocalService",
			Mockito.mock(RoleLocalService.class));

		_permissionChecker = Mockito.mock(PermissionChecker.class);

		Mockito.when(
			_permissionChecker.isOmniadmin()
		).thenReturn(
			true
		);

		// A JAX-RS RuntimeDelegate is required to build a Response outside the
		// server runtime.

		_responseBuilder = Mockito.mock(
			Response.ResponseBuilder.class, Mockito.RETURNS_SELF);

		Response response = Mockito.mock(Response.class);

		Mockito.when(
			response.getStatusInfo()
		).thenReturn(
			Response.Status.SERVICE_UNAVAILABLE
		);

		Mockito.when(
			_responseBuilder.build()
		).thenReturn(
			response
		);

		RuntimeDelegate runtimeDelegate = Mockito.mock(RuntimeDelegate.class);

		Mockito.when(
			runtimeDelegate.createResponseBuilder()
		).thenReturn(
			_responseBuilder
		);

		RuntimeDelegate.setInstance(runtimeDelegate);
	}

	@After
	public void tearDown() {
		RuntimeDelegate.setInstance(null);
	}

	@Test
	public void testPostHealthVerificationFailedReturns503() throws Exception {
		try (MockedStatic<PermissionThreadLocal>
				permissionThreadLocalMockedStatic = Mockito.mockStatic(
					PermissionThreadLocal.class);
			MockedStatic<FIPSModeValidator> fipsModeValidatorMockedStatic =
				Mockito.mockStatic(FIPSModeValidator.class)) {

			permissionThreadLocalMockedStatic.when(
				PermissionThreadLocal::getPermissionChecker
			).thenReturn(
				_permissionChecker
			);

			fipsModeValidatorMockedStatic.when(
				FIPSModeValidator::runSelfTests
			).thenReturn(
				FIPSHealthCheckResult.failed("BCFIPS", "AES-KAT", "ERROR", "boom")
			);

			Assert.assertThrows(
				WebApplicationException.class,
				_healthVerificationResourceImpl::postHealthVerification);

			Mockito.verify(
				_responseBuilder
			).status(
				(Response.StatusType)Response.Status.SERVICE_UNAVAILABLE
			);
		}
	}

	@Test
	public void testPostHealthVerificationHealthyReturns200() throws Exception {
		try (MockedStatic<PermissionThreadLocal>
				permissionThreadLocalMockedStatic = Mockito.mockStatic(
					PermissionThreadLocal.class);
			MockedStatic<FIPSModeValidator> fipsModeValidatorMockedStatic =
				Mockito.mockStatic(FIPSModeValidator.class)) {

			permissionThreadLocalMockedStatic.when(
				PermissionThreadLocal::getPermissionChecker
			).thenReturn(
				_permissionChecker
			);

			fipsModeValidatorMockedStatic.when(
				FIPSModeValidator::runSelfTests
			).thenReturn(
				FIPSHealthCheckResult.healthy("BCFIPS")
			);

			HealthVerification healthVerification =
				_healthVerificationResourceImpl.postHealthVerification();

			Assert.assertEquals(
				HealthVerification.Status.HEALTHY,
				healthVerification.getStatus());
		}
	}

	private HealthVerificationResourceImpl _healthVerificationResourceImpl;
	private PermissionChecker _permissionChecker;
	private Response.ResponseBuilder _responseBuilder;

}
```

- [ ] **Step 2: Run the test to verify it fails**

```bash
cd modules/apps/portal-security/portal-security-fips-rest-impl
../../../../gradlew test --tests HealthVerificationResourceImplTest
```
Expected: FAIL — the scaffold `postHealthVerification` throws the default "not implemented" error / returns null.

- [ ] **Step 3: Implement the resource**

Overwrite `HealthVerificationResourceImpl.java` with:
```java
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.fips.rest.internal.resource.v1_0;

import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.fips.FIPSHealthCheckResult;
import com.liferay.portal.kernel.security.fips.FIPSModeValidator;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.security.fips.rest.dto.v1_0.HealthVerification;
import com.liferay.portal.security.fips.rest.internal.constants.FIPSActionKeys;
import com.liferay.portal.security.fips.rest.resource.v1_0.HealthVerificationResource;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import java.util.Date;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Lucas Miranda
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/health-verification.properties",
	scope = ServiceScope.PROTOTYPE, service = HealthVerificationResource.class
)
public class HealthVerificationResourceImpl
	extends BaseHealthVerificationResourceImpl {

	@Override
	public HealthVerification postHealthVerification() throws Exception {
		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if (!permissionChecker.isOmniadmin() &&
			!_roleLocalService.hasUserRole(
				permissionChecker.getUserId(), permissionChecker.getCompanyId(),
				FIPSActionKeys.CRYPTO_OFFICER_ROLE_NAME, true)) {

			throw new PrincipalException.MustHavePermission(
				permissionChecker, FIPSActionKeys.TRIGGER_HEALTH_VERIFICATION);
		}

		FIPSHealthCheckResult result = FIPSModeValidator.runSelfTests();

		HealthVerification healthVerification = _toHealthVerification(result);

		if (result.getStatus() == FIPSHealthCheckResult.Status.NOT_APPLICABLE) {
			throw new WebApplicationException(
				Response.status(
					Response.Status.CONFLICT
				).entity(
					healthVerification
				).build());
		}

		if (result.getStatus() == FIPSHealthCheckResult.Status.FAILED) {
			throw new WebApplicationException(
				Response.status(
					Response.Status.SERVICE_UNAVAILABLE
				).entity(
					healthVerification
				).build());
		}

		return healthVerification;
	}

	private HealthVerification _toHealthVerification(
		FIPSHealthCheckResult result) {

		HealthVerification healthVerification = new HealthVerification();

		healthVerification.setDate(() -> new Date());
		healthVerification.setFailedTest(result::getFailedTest);
		healthVerification.setFipsState(result::getFipsState);
		healthVerification.setProviderMessage(result::getProviderMessage);
		healthVerification.setProviderName(result::getProviderName);
		healthVerification.setStatus(
			() -> HealthVerification.Status.create(
				result.getStatus(
				).name()));

		return healthVerification;
	}

	@Reference
	private RoleLocalService _roleLocalService;

}
```

- [ ] **Step 4: Run the test to verify it passes**

```bash
cd modules/apps/portal-security/portal-security-fips-rest-impl
../../../../gradlew test --tests HealthVerificationResourceImplTest
```
Expected: PASS (2 tests).

- [ ] **Step 5: Commit**

```bash
git add modules/apps/portal-security/portal-security-fips-rest-impl/src/main/java/com/liferay/portal/security/fips/rest/internal/resource/v1_0/HealthVerificationResourceImpl.java
git commit -m "LPD-93272 Implement crypto health verification endpoint"
git add modules/apps/portal-security/portal-security-fips-rest-impl/src/test/java/com/liferay/portal/security/fips/rest/internal/resource/v1_0/HealthVerificationResourceImplTest.java
git commit -m "LPD-93272 Add unit tests for crypto health resource status mapping"
```

---

### Task 7: Integration tests

**Files:**
- Test: `.../portal-security-fips-rest-test/src/testIntegration/java/com/liferay/portal/security/fips/rest/resource/v1_0/test/HealthVerificationResourceTest.java`

**Interfaces:**
- Consumes: the deployed endpoint `POST /o/crypto-health/v1.0/health-verifications`; the "Crypto Officer" role (Task 5).

Prerequisite: the kernel snapshot from Task 3 Step 6 is installed and the server restarted; deploy the impl module (`../../../../gradlew deploy` from the impl dir) so the endpoint and role listener are live before running the test.

- [ ] **Step 1: Write the integration test**

`HealthVerificationResourceTest.java`:
```java
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.fips.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.net.HttpURLConnection;
import java.net.URL;

import java.util.Base64;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Lucas Miranda
 */
@RunWith(Arquillian.class)
public class HealthVerificationResourceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_companyId = TestPropsValues.getCompanyId();
	}

	@Test
	public void testAuthorizedCallerGets409() throws Exception {
		User user = _addUserWithKnownPassword();

		Role role = RoleLocalServiceUtil.getRole(_companyId, "Crypto Officer");

		RoleLocalServiceUtil.addUserRole(user.getUserId(), role.getRoleId());

		int responseCode = _invoke(user.getEmailAddress(), _PASSWORD);

		// FIPS is disabled on a normal bundle, so the endpoint reports
		// NOT_APPLICABLE as HTTP 409.

		Assert.assertEquals(HttpURLConnection.HTTP_CONFLICT, responseCode);
	}

	@Test
	public void testUnauthorizedCallerGets403() throws Exception {
		User user = _addUserWithKnownPassword();

		int responseCode = _invoke(user.getEmailAddress(), _PASSWORD);

		Assert.assertEquals(HttpURLConnection.HTTP_FORBIDDEN, responseCode);
	}

	private User _addUserWithKnownPassword() throws Exception {
		return UserTestUtil.addUser(
			CompanyLocalServiceUtil.getCompany(_companyId), _PASSWORD);
	}

	private int _invoke(String emailAddress, String password) throws Exception {
		URL url = new URL(
			"http://localhost:" + PortalUtil.getPortalServerPort(false) +
				"/o/crypto-health/v1.0/health-verifications");

		HttpURLConnection httpURLConnection =
			(HttpURLConnection)url.openConnection();

		httpURLConnection.setRequestMethod("POST");

		Base64.Encoder encoder = Base64.getEncoder();

		String credentials = emailAddress + ":" + password;

		String encodedCredentials = encoder.encodeToString(
			credentials.getBytes());

		httpURLConnection.setRequestProperty(
			"Authorization", "Basic " + encodedCredentials);

		httpURLConnection.setRequestProperty("Accept", "application/json");

		return httpURLConnection.getResponseCode();
	}

	private static final String _PASSWORD = "test";

	private long _companyId;

}
```

- [ ] **Step 2: Run the integration test**

```bash
cd modules/apps/portal-security/portal-security-fips-rest-test
../../../../gradlew testIntegration --tests HealthVerificationResourceTest
```
Expected: PASS (2 tests) — 403 for the unauthorized caller, 409 for the authorized caller.

- [ ] **Step 3: Commit**

```bash
git add modules/apps/portal-security/portal-security-fips-rest-test/src/testIntegration/java/com/liferay/portal/security/fips/rest/resource/v1_0/test/HealthVerificationResourceTest.java
git commit -m "LPD-93272 Add integration tests for crypto health endpoint auth"
```

---

## Final Step: Branch-wide format

- [ ] Run the formatter over the branch and amend any fallout into the relevant commit:

```bash
cd portal-impl && ANT_OPTS="-Xmx2560m" ant format-source-current-branch
```
Fix anything it reports, then re-run until clean.
