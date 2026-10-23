/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.multi.factor.authentication.fido2.web.internal.audit;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.util.FeatureFlagTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
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
public class MFAFIDO2AuditMessageBuilderTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		JSONFactoryUtil jsonFactoryUtil = new JSONFactoryUtil();

		jsonFactoryUtil.setJSONFactory(new JSONFactoryImpl());
	}

	@FeatureFlag("LPD-6417")
	@Test
	public void testBuildAuditMessages() throws Exception {
		MFAFIDO2AuditMessageBuilder mfaFIDO2AuditMessageBuilder =
			new MFAFIDO2AuditMessageBuilder();

		AuditMessage auditMessage =
			mfaFIDO2AuditMessageBuilder.
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
			mfaFIDO2AuditMessageBuilder.buildNotVerifiedAuditMessage(
				_getUser(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString()),
			"verify_failure", "not_verified");
		_assertAuditMessage(
			mfaFIDO2AuditMessageBuilder.
				buildUnconfiguredUserVerificationFailureAuditMessage(
					RandomTestUtil.randomLong(), _getUser(),
					RandomTestUtil.randomString()),
			"verify_failure", "verification_failure");
		_assertAuditMessage(
			mfaFIDO2AuditMessageBuilder.buildVerificationFailureAuditMessage(
				_getUser(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString()),
			"verify_failure", "verification_failure");
		_assertAuditMessage(
			mfaFIDO2AuditMessageBuilder.buildVerifiedAuditMessage(
				_getUser(), RandomTestUtil.randomString()),
			"verify", "verified");

		_testBuildNotVerifiedAuditMessageWhenFeatureFlagIsDisabled();
	}

	private void _assertAuditMessage(
		AuditMessage auditMessage, String expectedAction,
		String expectedOutcome) {

		Assert.assertEquals(expectedAction, auditMessage.getAction());

		JSONObject additionalInfoJSONObject = auditMessage.getAdditionalInfo();

		Assert.assertEquals(
			expectedOutcome, additionalInfoJSONObject.getString("outcome"));
	}

	private User _getUser() {
		User user = Mockito.mock(User.class);

		Mockito.when(
			user.getCompanyId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		return user;
	}

	private void _testBuildNotVerifiedAuditMessageWhenFeatureFlagIsDisabled()
		throws Exception {

		try (SafeCloseable safeCloseable =
				FeatureFlagTestUtil.setFeatureFlagsWithSafeCloseable(
					false, "LPD-6417")) {

			String reason = RandomTestUtil.randomString();

			MFAFIDO2AuditMessageBuilder mfaFIDO2AuditMessageBuilder =
				new MFAFIDO2AuditMessageBuilder();

			AuditMessage auditMessage =
				mfaFIDO2AuditMessageBuilder.buildNotVerifiedAuditMessage(
					_getUser(), RandomTestUtil.randomString(), reason);

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();

			Assert.assertFalse(additionalInfoJSONObject.has("factor"));
			Assert.assertFalse(additionalInfoJSONObject.has("outcome"));
			Assert.assertEquals(
				reason, additionalInfoJSONObject.getString("reason"));
		}
	}

}