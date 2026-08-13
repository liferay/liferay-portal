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
	public void testToJSONObject() throws Exception {
		long groupId = RandomTestUtil.randomLong();
		Date timestamp = RandomTestUtil.nextDate();

		AuditMessage auditMessage = new AuditMessage(
			groupId, RandomTestUtil.randomLong(), RandomTestUtil.randomLong(),
			RandomTestUtil.randomString(), timestamp,
			JSONFactoryUtil.createJSONObject(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString());

		JSONObject jsonObject = auditMessage.toJSONObject();

		DateFormat dateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
			"yyyyMMddkkmmssSSS");

		Assert.assertEquals(groupId, jsonObject.getLong("groupId"));
		Assert.assertEquals(
			dateFormat.format(timestamp), jsonObject.getString("timestamp"));

		auditMessage.setTimestamp(null);

		jsonObject = auditMessage.toJSONObject();

		Assert.assertNotNull(jsonObject.getString("timestamp"));

		auditMessage = new AuditMessage(jsonObject.toString());

		Assert.assertEquals(groupId, auditMessage.getGroupId());
		Assert.assertNotNull(auditMessage.getTimestamp());
	}

}