/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.multi.factor.authentication.timebased.otp.web.internal.checker;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
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
public class TimeBasedOTPBrowserSetupMFACheckerTest {

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
		AuditMessage auditMessage = _buildAuditMessage(
			"buildNonexistentUserVerificationFailureAuditMessage",
			new Class<?>[] {long.class, long.class, String.class},
			RandomTestUtil.randomLong(), RandomTestUtil.randomLong(),
			RandomTestUtil.randomString());

		Assert.assertEquals("mfa", auditMessage.getResourceType());

		JSONObject additionalInfoJSONObject = auditMessage.getAdditionalInfo();

		Assert.assertEquals(
			"timebased_otp", additionalInfoJSONObject.getString("factor"));

		_assertAuditMessage(
			_buildAuditMessage(
				"buildNotVerifiedAuditMessage",
				new Class<?>[] {User.class, String.class, String.class},
				_getUser(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString()),
			"verify_failure", "not_verified");
		_assertAuditMessage(
			_buildAuditMessage(
				"buildUnconfiguredUserVerificationFailureAuditMessage",
				new Class<?>[] {long.class, User.class, String.class},
				RandomTestUtil.randomLong(), _getUser(),
				RandomTestUtil.randomString()),
			"verify_failure", "verification_failure");
		_assertAuditMessage(
			_buildAuditMessage(
				"buildVerificationFailureAuditMessage",
				new Class<?>[] {User.class, String.class, String.class},
				_getUser(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString()),
			"verify_failure", "verification_failure");
		_assertAuditMessage(
			_buildAuditMessage(
				"buildVerificationSuccessAuditMessage",
				new Class<?>[] {User.class, String.class}, _getUser(),
				RandomTestUtil.randomString()),
			"verify", "verification_success");
		_assertAuditMessage(
			_buildAuditMessage(
				"buildVerifiedAuditMessage",
				new Class<?>[] {User.class, String.class}, _getUser(),
				RandomTestUtil.randomString()),
			"verify", "verified");
		_assertAuditMessage(
			auditMessage, "verify_failure", "verification_failure");

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

	private AuditMessage _buildAuditMessage(
		String methodName, Class<?>[] parameterTypes, Object... arguments) {

		Object mfaTimeBasedOTPAuditMessageBuilder =
			ReflectionTestUtil.getFieldValue(
				new TimeBasedOTPBrowserSetupMFAChecker(),
				"_mfaTimeBasedOTPAuditMessageBuilder");

		return ReflectionTestUtil.invoke(
			mfaTimeBasedOTPAuditMessageBuilder, methodName, parameterTypes,
			arguments);
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

			AuditMessage auditMessage = _buildAuditMessage(
				"buildNotVerifiedAuditMessage",
				new Class<?>[] {User.class, String.class, String.class},
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