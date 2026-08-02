/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.model;

import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Brian I. Kim
 */
public class DSEnvelopeTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		JSONFactoryUtil jsonFactoryUtil = new JSONFactoryUtil();

		jsonFactoryUtil.setJSONFactory(new JSONFactoryImpl());
	}

	@Test
	public void testToJSONObject() {
		DSEnvelope dsEnvelope = new DSEnvelope();

		JSONObject jsonObject = dsEnvelope.toJSONObject();

		Assert.assertFalse(jsonObject.has("notification"));

		int expireAfter = RandomTestUtil.randomInt();

		dsEnvelope.setExpireAfter(expireAfter);

		int expireWarn = RandomTestUtil.randomInt();

		dsEnvelope.setExpireWarn(expireWarn);

		jsonObject = dsEnvelope.toJSONObject();

		JSONObject notificationJSONObject = jsonObject.getJSONObject(
			"notification");

		JSONObject expirationsJSONObject = notificationJSONObject.getJSONObject(
			"expirations");

		Assert.assertEquals(
			String.valueOf(expireAfter),
			expirationsJSONObject.getString("expireAfter"));
		Assert.assertEquals(
			"true", expirationsJSONObject.getString("expireEnabled"));
		Assert.assertEquals(
			String.valueOf(expireWarn),
			expirationsJSONObject.getString("expireWarn"));

		Assert.assertEquals(
			"false", notificationJSONObject.getString("useAccountDefaults"));
	}

}