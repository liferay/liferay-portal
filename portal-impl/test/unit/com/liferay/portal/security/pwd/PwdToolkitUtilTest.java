/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.pwd;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.exception.UserPasswordException;
import com.liferay.portal.kernel.security.fips.FIPSModeValidator;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Rafael Praxedes
 */
public class PwdToolkitUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testValidate() throws Exception {
		_testValidate();
		_testValidateInFIPSMode();
	}

	private void _testValidate() throws Exception {
		String password = RandomTestUtil.randomString(13);
		long userId = RandomTestUtil.randomLong();

		PwdToolkitUtil.validate(userId, password, password, null);

		Assert.assertThrows(
			UserPasswordException.MustMatch.class,
			() -> PwdToolkitUtil.validate(
				userId, password, RandomTestUtil.randomString(), null));
	}

	private void _testValidateInFIPSMode() throws Exception {
		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"FIPS_ENABLED", true)) {

			String password = RandomTestUtil.randomString(13);
			long userId = RandomTestUtil.randomLong();

			UserPasswordException.MustBeLonger mustBeLonger =
				Assert.assertThrows(
					UserPasswordException.MustBeLonger.class,
					() -> PwdToolkitUtil.validate(
						userId, password, password, null));

			Assert.assertEquals(
				FIPSModeValidator.PASSWORDS_MIN_LENGTH, mustBeLonger.minLength);
			Assert.assertEquals(userId, mustBeLonger.userId);

			String fipsPassword = RandomTestUtil.randomString(14);

			PwdToolkitUtil.validate(userId, fipsPassword, fipsPassword, null);
		}
	}

}