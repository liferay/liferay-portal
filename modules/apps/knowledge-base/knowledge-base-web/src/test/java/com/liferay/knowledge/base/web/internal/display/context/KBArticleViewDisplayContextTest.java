/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.web.internal.display.context;

import com.liferay.knowledge.base.configuration.KBServiceConfigurationProvider;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderResponse;

import java.time.Instant;

import java.util.Date;
import java.util.TimeZone;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Brian Wulbern
 */
public class KBArticleViewDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		ReflectionTestUtil.setFieldValue(
			KBArticleViewDisplayContext.class,
			"_kbServiceConfigurationProviderSnapshot",
			new Snapshot<KBServiceConfigurationProvider>(
				KBArticleViewDisplayContext.class,
				KBServiceConfigurationProvider.class) {

				@Override
				public KBServiceConfigurationProvider get() {
					return _kbServiceConfigurationProvider;
				}

			});

		Mockito.when(
			_kbServiceConfigurationProvider.
				getExpirationDateNotificationDateWeeks()
		).thenReturn(
			1
		);
	}

	@After
	public void tearDown() {
		_dateUtilMockedStatic.close();
	}

	@Test
	@TestInfo("LPD-112106")
	public void testIsExpiringSoon() throws Exception {
		KBArticle kbArticle = Mockito.mock(KBArticle.class);

		Mockito.when(
			kbArticle.getExpirationDate()
		).thenReturn(
			Date.from(Instant.parse("2026-11-02T04:54:00Z"))
		);

		_mockDate("2026-10-26T03:50:00Z");

		KBArticleViewDisplayContext kbArticleViewDisplayContext =
			_getKBArticleViewDisplayContext(
				TimeZone.getTimeZone("America/Los_Angeles"));

		Assert.assertFalse(
			kbArticleViewDisplayContext.isExpiringSoon(kbArticle));

		_mockDate("2026-10-26T04:00:00Z");

		Assert.assertTrue(
			kbArticleViewDisplayContext.isExpiringSoon(kbArticle));
	}

	private KBArticleViewDisplayContext _getKBArticleViewDisplayContext(
		TimeZone timeZone) {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setTimeZone(timeZone);

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		LiferayPortletRequest liferayPortletRequest = Mockito.mock(
			LiferayPortletRequest.class);

		Mockito.when(
			liferayPortletRequest.getAttribute(
				Mockito.endsWith(WebKeys.CURRENT_PORTLET_URL))
		).thenReturn(
			Mockito.mock(PortletURL.class)
		);

		return new KBArticleViewDisplayContext(
			mockHttpServletRequest, liferayPortletRequest,
			Mockito.mock(LiferayPortletResponse.class),
			Mockito.mock(RenderResponse.class), null);
	}

	private void _mockDate(String text) {
		_dateUtilMockedStatic.when(
			DateUtil::newDate
		).thenReturn(
			Date.from(Instant.parse(text))
		);
	}

	private final MockedStatic<DateUtil> _dateUtilMockedStatic =
		Mockito.mockStatic(DateUtil.class);
	private final KBServiceConfigurationProvider
		_kbServiceConfigurationProvider = Mockito.mock(
			KBServiceConfigurationProvider.class);

}