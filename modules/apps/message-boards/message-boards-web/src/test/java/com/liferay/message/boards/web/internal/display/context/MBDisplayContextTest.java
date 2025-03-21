/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.web.internal.display.context;

import com.liferay.message.boards.model.MBMessage;
import com.liferay.portal.kernel.bean.BeanProperties;
import com.liferay.portal.kernel.bean.BeanPropertiesUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Jürgen Kappler
 */
public class MBDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		BeanPropertiesUtil beanPropertiesUtil = new BeanPropertiesUtil();

		beanPropertiesUtil.setBeanProperties(_beanProperties);

		_setUpLanguageUtil();
	}

	@Test
	public void testGetModifiedLabel() {
		HttpServletRequest httpServletRequest = Mockito.mock(
			HttpServletRequest.class);

		Mockito.when(
			httpServletRequest.getLocale()
		).thenReturn(
			LocaleUtil.US
		);

		MBDisplayContext mbDisplayContext = new MBDisplayContext(
			httpServletRequest);

		MBMessage mbMessage = Mockito.mock(MBMessage.class);

		Mockito.when(
			mbMessage.getModifiedDate()
		).thenReturn(
			new Date()
		);

		String userName = RandomTestUtil.randomString();

		Mockito.when(
			mbMessage.getUserName()
		).thenReturn(
			userName
		);

		String statusByUserName = RandomTestUtil.randomString();

		Mockito.when(
			mbMessage.getStatusByUserName()
		).thenReturn(
			statusByUserName
		);

		_testGetModifiedLabel(
			false, statusByUserName, mbDisplayContext, mbMessage);

		_testGetModifiedLabel(true, "anonymous", mbDisplayContext, mbMessage);
	}

	private void _setUpLanguageUtil() {
		LanguageUtil languageUtil = new LanguageUtil();

		Mockito.when(
			_language.format(
				Mockito.any(HttpServletRequest.class), Mockito.anyString(),
				(String[])Mockito.any())
		).thenAnswer(
			invocation -> {
				String[] values = invocation.getArgument(2);

				return values[0];
			}
		);

		Mockito.when(
			_language.getTimeDescription(
				Mockito.any(HttpServletRequest.class), Mockito.anyLong(),
				Mockito.anyBoolean())
		).thenReturn(
			RandomTestUtil.randomString()
		);

		languageUtil.setLanguage(_language);
	}

	private void _testGetModifiedLabel(
		boolean anonymous, String expectedModifiedLabel,
		MBDisplayContext mbDisplayContext, MBMessage mbMessage) {

		Mockito.when(
			mbMessage.isAnonymous()
		).thenReturn(
			anonymous
		);

		Assert.assertEquals(
			expectedModifiedLabel,
			mbDisplayContext.getModifiedLabel(mbMessage));
	}

	private final BeanProperties _beanProperties = Mockito.mock(
		BeanProperties.class);
	private final Language _language = Mockito.mock(Language.class);

}