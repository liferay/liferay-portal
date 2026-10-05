/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.rss.web.internal.portlet.validator;

import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.PortletPreferences;
import jakarta.portlet.ValidatorException;

import java.util.Collections;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Alberto Chaparro
 */
public class RSSPreferencesValidatorTest {

	@ClassRule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	@TestInfo("LPD-106200")
	public void testValidate() {
		_testValidate("file:///" + RandomTestUtil.randomString(), false);
		_testValidate("ftp://" + RandomTestUtil.randomString() + ".com", false);
		_testValidate("http://" + RandomTestUtil.randomString() + ".com", true);
		_testValidate(
			"https://" + RandomTestUtil.randomString() + ".com", true);
		_testValidate(RandomTestUtil.randomString(), false);
	}

	private void _testValidate(String url, boolean valid) {
		PortletPreferences portletPreferences = Mockito.mock(
			PortletPreferences.class);

		Mockito.when(
			portletPreferences.getValues(Mockito.eq("urls"), Mockito.any())
		).thenReturn(
			new String[] {url}
		);

		try {
			_rssPreferencesValidator.validate(portletPreferences);

			Assert.assertTrue(valid);
		}
		catch (ValidatorException validatorException) {
			Assert.assertFalse(valid);

			Assert.assertArrayEquals(
				new String[] {url},
				ArrayUtil.toStringArray(
					Collections.list(validatorException.getFailedKeys())));
		}
	}

	private final RSSPreferencesValidator _rssPreferencesValidator =
		new RSSPreferencesValidator();

}