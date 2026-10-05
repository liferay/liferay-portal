/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.audit;

import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.text.DateFormat;

import java.util.Date;

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

	@Test
	public void testConstructor() throws Exception {
		AuditRequestContext auditRequestContext =
			AuditRequestContextThreadLocal.getAuditRequestContext();

		String correlationId = RandomTestUtil.randomString();

		auditRequestContext.setCorrelationId(correlationId);

		String requestId = RandomTestUtil.randomString();

		auditRequestContext.setRequestId(requestId);

		auditRequestContext.setRequestIdGenerated(true);

		AuditMessage auditMessage = new AuditMessage(
			RandomTestUtil.randomLong(), RandomTestUtil.randomLong(),
			RandomTestUtil.randomLong(), RandomTestUtil.randomString(),
			RandomTestUtil.nextDate(), JSONFactoryUtil.createJSONObject(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

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

}