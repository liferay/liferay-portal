/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.auth.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.DefaultScreenNameGenerator;
import com.liferay.portal.kernel.security.auth.ScreenNameGenerator;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Riccardo Ferrari
 * @author Daniel Reuther
 */
@RunWith(Arquillian.class)
public class DefaultScreenNameGeneratorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testGenerate() throws Exception {
		Assert.assertEquals(
			"user123",
			_screenNameGenerator.generate(
				TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
				"user123@liferay.com"));
	}

	@Test
	public void testGenerateDuplicateScreenName() throws Exception {
		User user = TestPropsValues.getUser();

		String screenName = _screenNameGenerator.generate(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			user.getScreenName() + "@liferay.com");

		Assert.assertNotSame(user.getScreenName(), screenName);
		Assert.assertEquals(user.getScreenName() + ".1", screenName);
	}

	@Test
	public void testGenerateNoEmailAddress() throws Exception {
		String screenName = _screenNameGenerator.generate(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(), null);

		if (PropsValues.USERS_SCREEN_NAME_ALLOW_NUMERIC) {
			Assert.assertEquals(
				String.valueOf(TestPropsValues.getUserId()), screenName);
		}
		else {
			Assert.assertEquals(
				"user." + TestPropsValues.getUserId(), screenName);
		}
	}

	@Test
	public void testGenerateNumericEmailAddress() throws Exception {
		String screenName = _screenNameGenerator.generate(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			"123@liferay.com");

		if (PropsValues.USERS_SCREEN_NAME_ALLOW_NUMERIC) {
			Assert.assertNotSame("user.123", screenName);
			Assert.assertEquals("123", screenName);
		}
		else {
			Assert.assertNotSame("123", screenName);
			Assert.assertEquals("user.123", screenName);
		}
	}

	@Test
	public void testGeneratePostfixEmailAddress() throws Exception {
		Assert.assertEquals(
			"postfix." + TestPropsValues.getUserId(),
			_screenNameGenerator.generate(
				TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
				"postfix@liferay.com"));
	}

	private final ScreenNameGenerator _screenNameGenerator =
		new DefaultScreenNameGenerator();

}