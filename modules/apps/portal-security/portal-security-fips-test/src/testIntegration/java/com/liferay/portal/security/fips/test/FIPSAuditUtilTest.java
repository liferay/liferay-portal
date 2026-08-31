/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.fips.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.security.fips.FIPSAuditEvent;
import com.liferay.portal.kernel.security.fips.FIPSAuditUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermissions;

import java.security.Provider;
import java.security.Security;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jorge García Jiménez
 * @author Rafael Praxedes
 */
@RunWith(Arquillian.class)
public class FIPSAuditUtilTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() {
		Assume.assumeTrue(PropsValues.FIPS_ENABLED);
	}

	@Test
	public void testWrite() throws Exception {
		long lastEventSequence = _getLastEventSequence();

		String eventType = RandomTestUtil.randomString();

		FIPSAuditEvent fipsAuditEvent = new FIPSAuditEvent(
			eventType, FIPSAuditEvent.Severity.INFO);

		String reason = RandomTestUtil.randomString();

		fipsAuditEvent.put("reason", reason);

		FIPSAuditUtil.write(fipsAuditEvent);

		List<JSONObject> jsonObjects = _getJSONObjects();

		JSONObject jsonObject = _getJSONObject(eventType, jsonObjects);

		Assert.assertEquals(
			PropsValues.FIPS_AUDIT_PROVIDER_CMVP_CERTIFICATE_ID,
			jsonObject.getString("cmvp-certificate-id"));

		Assert.assertTrue(
			Validator.isNotNull(
				jsonObject.getString("deployment-instance-id")));

		Assert.assertEquals(
			"1.0", jsonObject.getString("event-schema-version"));
		Assert.assertEquals(
			lastEventSequence + 1, jsonObject.getLong("event-sequence"));

		JSONObject fieldsJSONObject = jsonObject.getJSONObject("fields");

		Assert.assertEquals(reason, fieldsJSONObject.getString("reason"));

		Provider provider = Security.getProviders()[0];

		Assert.assertEquals(
			provider.getName(), jsonObject.getString("provider-name"));
		Assert.assertEquals(
			provider.getVersionStr(), jsonObject.getString("provider-version"));

		Assert.assertEquals("INFO", jsonObject.getString("severity"));

		Instant instant = Instant.parse(jsonObject.getString("timestamp"));

		Assert.assertTrue(
			Math.abs(System.currentTimeMillis() - instant.toEpochMilli()) <
				Time.MINUTE);

		_assertBootStateTransitions(jsonObjects);
		_assertCanonicalTimestamps(jsonObjects);
		_assertIncrementingEventSequences(jsonObjects);
		_assertOneDeploymentInstanceId(jsonObjects);
	}

	@Test
	public void testWriteWithAFieldTimestampWithoutATimeZone()
		throws Exception {

		String eventType = RandomTestUtil.randomString();

		FIPSAuditEvent fipsAuditEvent = new FIPSAuditEvent(
			eventType, FIPSAuditEvent.Severity.INFO);

		fipsAuditEvent.put(
			"provider-timestamp",
			LocalDateTime.ofEpochSecond(
				RandomTestUtil.randomInt(), 0, ZoneOffset.UTC));

		Assert.assertThrows(
			IllegalArgumentException.class,
			() -> FIPSAuditUtil.write(fipsAuditEvent));

		for (JSONObject jsonObject : _getJSONObjects()) {
			Assert.assertNotEquals(
				eventType, jsonObject.getString("event-type"));
		}
	}

	@Test
	public void testWriteWithASpoofedProviderName() throws Exception {
		String eventType = RandomTestUtil.randomString();

		FIPSAuditEvent fipsAuditEvent = new FIPSAuditEvent(
			eventType, FIPSAuditEvent.Severity.INFO);

		String spoofedProviderName = RandomTestUtil.randomString();

		fipsAuditEvent.put("provider-name", spoofedProviderName);

		FIPSAuditUtil.write(fipsAuditEvent);

		JSONObject jsonObject = _getJSONObject(eventType);

		Provider provider = Security.getProviders()[0];

		Assert.assertEquals(
			provider.getName(), jsonObject.getString("provider-name"));

		JSONObject fieldsJSONObject = jsonObject.getJSONObject("fields");

		Assert.assertEquals(
			spoofedProviderName, fieldsJSONObject.getString("provider-name"));
	}

	@Test
	public void testWriteWithCriticalSeverity() throws Exception {
		String eventType = RandomTestUtil.randomString();

		FIPSAuditUtil.write(
			new FIPSAuditEvent(eventType, FIPSAuditEvent.Severity.CRITICAL));

		JSONObject jsonObject = _getJSONObject(eventType);

		Assert.assertEquals("CRITICAL", jsonObject.getString("severity"));
	}

	@Test
	public void testWriteWithFieldTimestamps() throws Exception {
		String eventType = RandomTestUtil.randomString();

		FIPSAuditEvent fipsAuditEvent = new FIPSAuditEvent(
			eventType, FIPSAuditEvent.Severity.INFO);

		fipsAuditEvent.put(
			"completed-date",
			Date.from(Instant.parse("2025-05-06T14:19:23.471Z")));
		fipsAuditEvent.put(
			"completed-instant",
			Instant.parse("2026-05-06T14:19:23.471999999Z"));
		fipsAuditEvent.put(
			"completed-offset-date-time",
			OffsetDateTime.parse("2026-05-06T16:19:23.471+02:00"));
		fipsAuditEvent.put(
			"provider-self-test",
			Collections.singletonMap(
				"completed", Instant.parse("2026-05-06T14:19:23.471Z")));
		fipsAuditEvent.put(
			"provider-timestamps",
			Arrays.asList(Instant.parse("2026-05-06T14:19:23Z")));

		FIPSAuditUtil.write(fipsAuditEvent);

		JSONObject jsonObject = _getJSONObject(eventType);

		JSONObject fieldsJSONObject = jsonObject.getJSONObject("fields");

		Assert.assertEquals(
			"2025-05-06T14:19:23.471Z",
			fieldsJSONObject.getString("completed-date"));
		Assert.assertEquals(
			"2026-05-06T14:19:23.471Z",
			fieldsJSONObject.getString("completed-instant"));
		Assert.assertEquals(
			"2026-05-06T14:19:23.471Z",
			fieldsJSONObject.getString("completed-offset-date-time"));

		JSONObject providerSelfTestJSONObject = fieldsJSONObject.getJSONObject(
			"provider-self-test");

		Assert.assertEquals(
			"2026-05-06T14:19:23.471Z",
			providerSelfTestJSONObject.getString("completed"));

		JSONArray providerTimestampsJSONArray = fieldsJSONObject.getJSONArray(
			"provider-timestamps");

		Assert.assertEquals(
			"2026-05-06T14:19:23.000Z",
			providerTimestampsJSONArray.getString(0));
	}

	@Test
	public void testWriteWithoutADeploymentInstanceId() throws Exception {
		Assume.assumeTrue(
			Validator.isNull(PropsValues.FIPS_AUDIT_DEPLOYMENT_INSTANCE_ID));

		String eventType = RandomTestUtil.randomString();

		FIPSAuditUtil.write(
			new FIPSAuditEvent(eventType, FIPSAuditEvent.Severity.INFO));

		Path path = Paths.get(
			PropsValues.LIFERAY_HOME, "data",
			"fips-audit-deployment-instance-id");

		String deploymentInstanceId = new String(
			Files.readAllBytes(path), StandardCharsets.UTF_8);

		JSONObject jsonObject = _getJSONObject(eventType);

		Assert.assertEquals(
			deploymentInstanceId.trim(),
			jsonObject.getString("deployment-instance-id"));
	}

	@Test
	public void testWriteWithPosixFileSystem() throws Exception {
		FIPSAuditUtil.write(
			new FIPSAuditEvent(
				RandomTestUtil.randomString(), FIPSAuditEvent.Severity.INFO));

		Path path = _getFIPSAuditLogPath();

		FileSystem fileSystem = path.getFileSystem();

		Set<String> supportedFileAttributeViews =
			fileSystem.supportedFileAttributeViews();

		Assume.assumeTrue(supportedFileAttributeViews.contains("posix"));

		Assert.assertEquals(
			PosixFilePermissions.fromString("rw-------"),
			Files.getPosixFilePermissions(path));
	}

	private void _assertBootStateTransitions(List<JSONObject> jsonObjects) {
		List<String> transitions = TransformUtil.transform(
			jsonObjects,
			jsonObject -> {
				String eventType = jsonObject.getString("event-type");

				if (!eventType.equals("fips-state-transition")) {
					return null;
				}

				JSONObject fieldsJSONObject = jsonObject.getJSONObject(
					"fields");

				return StringBundler.concat(
					fieldsJSONObject.getString("from-state"), " to ",
					fieldsJSONObject.getString("to-state"));
			});

		Assert.assertTrue(transitions.contains("INITIALIZING to SELF_TEST"));
		Assert.assertTrue(transitions.contains("SELF_TEST to OPERATIONAL"));
	}

	private void _assertCanonicalTimestamps(List<JSONObject> jsonObjects) {
		for (JSONObject jsonObject : jsonObjects) {
			String timestamp = jsonObject.getString("timestamp");

			Assert.assertTrue(
				timestamp.matches(
					"\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}Z"));
		}
	}

	private void _assertIncrementingEventSequences(
		List<JSONObject> jsonObjects) {

		long previousEventSequence = 0;

		for (JSONObject jsonObject : jsonObjects) {
			long eventSequence = jsonObject.getLong("event-sequence");

			if (eventSequence > previousEventSequence) {
				Assert.assertEquals(previousEventSequence + 1, eventSequence);
			}

			previousEventSequence = eventSequence;
		}
	}

	private void _assertOneDeploymentInstanceId(List<JSONObject> jsonObjects) {
		JSONObject firstJSONObject = jsonObjects.get(0);

		String deploymentInstanceId = firstJSONObject.getString(
			"deployment-instance-id");

		for (JSONObject jsonObject : jsonObjects) {
			Assert.assertEquals(
				deploymentInstanceId,
				jsonObject.getString("deployment-instance-id"));
		}
	}

	private Path _getFIPSAuditLogPath() {
		LocalDate localDate = LocalDate.now(ZoneOffset.UTC);

		return Paths.get(
			PropsValues.LIFERAY_HOME, "logs",
			StringBundler.concat("fips-audit.", localDate, ".ndjson"));
	}

	private JSONObject _getJSONObject(String eventType) throws Exception {
		return _getJSONObject(eventType, _getJSONObjects());
	}

	private JSONObject _getJSONObject(
		String eventType, List<JSONObject> jsonObjects) {

		List<JSONObject> eventTypeJSONObjects = ListUtil.filter(
			jsonObjects,
			jsonObject -> Objects.equals(
				eventType, jsonObject.getString("event-type")));

		Assert.assertEquals(
			eventTypeJSONObjects.toString(), 1, eventTypeJSONObjects.size());

		return eventTypeJSONObjects.get(0);
	}

	private List<JSONObject> _getJSONObjects() throws Exception {
		return TransformUtil.unsafeTransform(
			Files.readAllLines(_getFIPSAuditLogPath()),
			JSONFactoryUtil::createJSONObject);
	}

	private long _getLastEventSequence() throws Exception {
		List<JSONObject> jsonObjects = _getJSONObjects();

		JSONObject jsonObject = jsonObjects.get(jsonObjects.size() - 1);

		return jsonObject.getLong("event-sequence");
	}

}