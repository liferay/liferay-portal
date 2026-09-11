/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security.fips;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Jorge García Jiménez
 */
public class FIPSAuditEventFactoryTest {

	@Test
	public void testCreateFederationTokenRejected() {
		String receivingEndpoint = "/o/" + RandomTestUtil.randomString();
		String rejectedValue = RandomTestUtil.randomString();
		String tokenIssuer = RandomTestUtil.randomString();
		String tokenType = RandomTestUtil.randomString();

		FIPSAuditEvent fipsAuditEvent =
			FIPSAuditEventFactory.createFederationTokenRejected(
				receivingEndpoint, rejectedValue, tokenIssuer, tokenType);

		Assert.assertEquals(
			"federation-token-rejected", fipsAuditEvent.getEventType());
		Assert.assertEquals(
			FIPSAuditEvent.Severity.WARNING, fipsAuditEvent.getSeverity());

		_assertFields(
			fipsAuditEvent, receivingEndpoint, rejectedValue, tokenIssuer,
			tokenType);

		_assertFields(
			FIPSAuditEventFactory.createFederationTokenRejected(
				null, null, null, null),
			StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
			StringPool.BLANK);
	}

	private void _assertFields(
		FIPSAuditEvent fipsAuditEvent, String receivingEndpoint,
		String rejectedValue, String tokenIssuer, String tokenType) {

		Assert.assertEquals(
			HashMapBuilder.<String, Object>put(
				"receiving-endpoint", receivingEndpoint
			).put(
				"rejected-value", rejectedValue
			).put(
				"token-issuer", tokenIssuer
			).put(
				"token-type", tokenType
			).build(),
			fipsAuditEvent.getFields());
	}

}