/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.manager.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.digital.signature.model.DSEnvelope;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Brian Kim
 */
@RunWith(Arquillian.class)
public class DSEnvelopeExpirationTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testToJSONObjectWithExpiration() {
		DSEnvelope dsEnvelope = new DSEnvelope();

		dsEnvelope.setExpireAfter(30);
		dsEnvelope.setExpireWarn(3);

		JSONObject jsonObject = dsEnvelope.toJSONObject();

		JSONObject notificationJSONObject = jsonObject.getJSONObject(
			"notification");

		Assert.assertNotNull(notificationJSONObject);
		Assert.assertEquals(
			"false", notificationJSONObject.getString("useAccountDefaults"));

		JSONObject expirationsJSONObject = notificationJSONObject.getJSONObject(
			"expirations");

		Assert.assertEquals(
			"30", expirationsJSONObject.getString("expireAfter"));
		Assert.assertEquals(
			"true", expirationsJSONObject.getString("expireEnabled"));
		Assert.assertEquals("3", expirationsJSONObject.getString("expireWarn"));
	}

	@Test
	public void testToJSONObjectWithoutExpiration() {
		DSEnvelope dsEnvelope = new DSEnvelope();

		JSONObject jsonObject = dsEnvelope.toJSONObject();

		Assert.assertFalse(jsonObject.has("notification"));
	}

}