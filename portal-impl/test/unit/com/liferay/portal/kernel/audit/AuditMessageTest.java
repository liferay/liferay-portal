/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.audit;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.text.DateFormat;

import java.util.Date;

import org.junit.After;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Regisson Aguiar
 */
public class AuditMessageTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@After
	public void tearDown() {
		AuditRequestContextThreadLocal.removeAuditRequestContext();
	}

	@FeatureFlag("LPD-6417")
	@Test
	public void testConstructor() throws Exception {
		_testConstructor();
		_testConstructorWithClonedContext();
		_testConstructorWithSharedContext();
		_testConstructorWithSystemCompany();
	}

	@Test
	public void testConstructorDoesNotSetRequestIdWhenFeatureFlagIsDisabled()
		throws Exception {

		AuditRequestContext auditRequestContext =
			AuditRequestContextThreadLocal.getAuditRequestContext();

		Assert.assertNotNull(auditRequestContext.getRequestId());

		AuditMessage auditMessage = _createAuditMessage(
			RandomTestUtil.randomLong());

		Assert.assertNull(auditMessage.getRequestId());
		Assert.assertFalse(auditMessage.isRequestIdGenerated());
	}

	@Test
	public void testToJSONObject() throws Exception {
		long groupId = RandomTestUtil.randomLong();
		Date timestampDate = RandomTestUtil.nextDate();

		AuditMessage auditMessage = new AuditMessage(
			groupId, RandomTestUtil.randomLong(), RandomTestUtil.randomLong(),
			RandomTestUtil.randomString(), timestampDate,
			JSONFactoryUtil.createJSONObject(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString());

		String action = RandomTestUtil.randomString();

		auditMessage.setAction(action);

		JSONObject jsonObject = auditMessage.toJSONObject();

		DateFormat dateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
			"yyyyMMddkkmmssSSS");

		Assert.assertEquals(action, jsonObject.getString("action"));
		Assert.assertEquals(groupId, jsonObject.getLong("groupId"));
		Assert.assertEquals(
			dateFormat.format(timestampDate),
			jsonObject.getString("timestamp"));

		auditMessage.setTimestampDate(null);

		jsonObject = auditMessage.toJSONObject();

		Assert.assertNotNull(jsonObject.getString("timestamp"));

		auditMessage = new AuditMessage(jsonObject.toString());

		Assert.assertEquals(action, auditMessage.getAction());
		Assert.assertEquals(groupId, auditMessage.getGroupId());
		Assert.assertNotNull(auditMessage.getTimestampDate());
	}

	private AuditMessage _createAuditMessage(long companyId) {
		return new AuditMessage(
			RandomTestUtil.randomLong(), companyId, RandomTestUtil.randomLong(),
			RandomTestUtil.randomString(), RandomTestUtil.nextDate(),
			JSONFactoryUtil.createJSONObject(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString());
	}

	private void _testConstructor() throws Exception {
		AuditRequestContext auditRequestContext =
			AuditRequestContextThreadLocal.getAuditRequestContext();

		String correlationId = RandomTestUtil.randomString();

		auditRequestContext.setCorrelationId(correlationId);

		String requestId = RandomTestUtil.randomString();

		auditRequestContext.setRequestId(requestId);

		auditRequestContext.setRequestIdGenerated(true);

		AuditMessage auditMessage = _createAuditMessage(
			RandomTestUtil.randomLong());

		Assert.assertEquals(correlationId, auditMessage.getCorrelationId());
		Assert.assertEquals(requestId, auditMessage.getRequestId());
		Assert.assertTrue(auditMessage.isRequestIdGenerated());

		String action = RandomTestUtil.randomString();
		String resourceType = RandomTestUtil.randomString();

		auditMessage = new AuditMessage(
			RandomTestUtil.randomLong(), RandomTestUtil.randomLong(),
			RandomTestUtil.randomString(), action,
			JSONFactoryUtil.createJSONObject(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), resourceType);

		Assert.assertEquals(action, auditMessage.getAction());
		Assert.assertEquals(resourceType, auditMessage.getResourceType());

		AuditRequestContextThreadLocal.removeAuditRequestContext();
	}

	private void _testConstructorWithClonedContext() {
		long companyId = RandomTestUtil.randomLong();

		AuditMessage auditMessage1 = _createAuditMessage(companyId);

		String requestId = auditMessage1.getRequestId();

		Assert.assertNotNull(requestId);

		AuditRequestContext auditRequestContext =
			AuditRequestContextThreadLocal.getAuditRequestContext();

		try (SafeCloseable safeCloseable =
				AuditRequestContextThreadLocal.
					setAuditRequestContextWithSafeCloseable(
						auditRequestContext.clone())) {

			AuditMessage auditMessage2 = _createAuditMessage(companyId);

			Assert.assertEquals(requestId, auditMessage2.getRequestId());
			Assert.assertTrue(auditMessage2.isRequestIdGenerated());
		}

		AuditMessage auditMessage3 = _createAuditMessage(companyId);

		Assert.assertEquals(requestId, auditMessage3.getRequestId());

		AuditRequestContextThreadLocal.removeAuditRequestContext();
	}

	private void _testConstructorWithSharedContext() throws Exception {
		AuditMessage auditMessage1 = _createAuditMessage(
			RandomTestUtil.randomLong());

		Assert.assertTrue(auditMessage1.isRequestIdGenerated());

		String requestId = auditMessage1.getRequestId();

		Assert.assertNotNull(requestId);

		AuditMessage auditMessage2 = _createAuditMessage(
			RandomTestUtil.randomLong());

		Assert.assertTrue(auditMessage2.isRequestIdGenerated());

		Assert.assertEquals(requestId, auditMessage2.getRequestId());

		AuditRequestContextThreadLocal.removeAuditRequestContext();

		AuditMessage auditMessage3 = _createAuditMessage(
			RandomTestUtil.randomLong());

		Assert.assertNotNull(auditMessage3.getRequestId());
		Assert.assertNotEquals(requestId, auditMessage3.getRequestId());

		AuditRequestContextThreadLocal.removeAuditRequestContext();
	}

	private void _testConstructorWithSystemCompany() {
		AuditMessage auditMessage = _createAuditMessage(
			CompanyConstants.SYSTEM);

		Assert.assertNull(auditMessage.getRequestId());
		Assert.assertFalse(auditMessage.isRequestIdGenerated());

		AuditRequestContextThreadLocal.removeAuditRequestContext();
	}

}