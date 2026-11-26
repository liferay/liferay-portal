/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.multi.factor.authentication.fido2.web.internal.util;

import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Regisson Aguiar
 */
public class MFAFIDO2AuditMessageUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		JSONFactoryUtil jsonFactoryUtil = new JSONFactoryUtil();

		jsonFactoryUtil.setJSONFactory(new JSONFactoryImpl());
	}

	@Test
	public void testBuildAuditMessages() throws Exception {
		AuditMessage auditMessage =
			MFAFIDO2AuditMessageUtil.
				buildNonexistentUserVerificationFailureAuditMessage(
					RandomTestUtil.randomLong(), RandomTestUtil.randomLong(),
					RandomTestUtil.randomString());

		Assert.assertEquals("mfa", auditMessage.getResourceType());

		JSONObject additionalInfoJSONObject = auditMessage.getAdditionalInfo();

		Assert.assertEquals(
			"fido2", additionalInfoJSONObject.getString("factor"));

		_assertAuditMessage(
			auditMessage, "verify_failure", "verification_failure");
		_assertAuditMessage(
			MFAFIDO2AuditMessageUtil.buildNotVerifiedAuditMessage(
				Mockito.mock(User.class), RandomTestUtil.randomString(),
				RandomTestUtil.randomString()),
			"verify_failure", "not_verified");
		_assertAuditMessage(
			MFAFIDO2AuditMessageUtil.
				buildUnconfiguredUserVerificationFailureAuditMessage(
					RandomTestUtil.randomLong(), Mockito.mock(User.class),
					RandomTestUtil.randomString()),
			"verify_failure", "verification_failure");
		_assertAuditMessage(
			MFAFIDO2AuditMessageUtil.buildVerificationFailureAuditMessage(
				Mockito.mock(User.class), RandomTestUtil.randomString(),
				RandomTestUtil.randomString()),
			"verify_failure", "verification_failure");
		_assertAuditMessage(
			MFAFIDO2AuditMessageUtil.buildVerifiedAuditMessage(
				Mockito.mock(User.class), RandomTestUtil.randomString()),
			"verify", "verified");
	}

	private void _assertAuditMessage(
		AuditMessage auditMessage, String expectedAction,
		String expectedOutcome) {

		Assert.assertEquals(expectedAction, auditMessage.getAction());

		JSONObject additionalInfoJSONObject = auditMessage.getAdditionalInfo();

		Assert.assertEquals(
			expectedOutcome, additionalInfoJSONObject.getString("outcome"));
	}

}