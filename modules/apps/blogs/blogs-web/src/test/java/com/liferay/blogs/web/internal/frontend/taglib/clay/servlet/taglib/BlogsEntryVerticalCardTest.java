/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.web.internal.frontend.taglib.clay.servlet.taglib;

import com.liferay.blogs.model.BlogsEntry;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.RenderRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Date;
import java.util.ResourceBundle;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

/**
 * @author David Reyes
 */
public class BlogsEntryVerticalCardTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_setUpLanguageUtil();
		_setUpPortalUtil();
	}

	@Test
	@TestInfo("LPD-111121")
	public void testGetSubtitle() {
		BlogsEntry escapedBlogsEntry = Mockito.mock(BlogsEntry.class);

		BlogsEntry blogsEntry = Mockito.mock(BlogsEntry.class);

		Mockito.when(
			blogsEntry.getStatusByUserName()
		).thenReturn(
			"Test & Co"
		);

		Mockito.when(
			escapedBlogsEntry.getModifiedDate()
		).thenReturn(
			new Date()
		);

		Mockito.when(
			escapedBlogsEntry.getStatusByUserName()
		).thenReturn(
			"Test &amp; Co"
		);

		Mockito.when(
			escapedBlogsEntry.toUnescapedModel()
		).thenReturn(
			blogsEntry
		);

		BlogsEntryVerticalCard blogsEntryVerticalCard =
			new BlogsEntryVerticalCard(
				escapedBlogsEntry, null, null, null, _renderRequest, null,
				Mockito.mock(ResourceBundle.class), null, null);

		Assert.assertEquals(
			"1 minute Test & Co", blogsEntryVerticalCard.getSubtitle());
	}

	private void _setUpLanguageUtil() {
		LanguageUtil languageUtil = new LanguageUtil();

		Language language = Mockito.mock(Language.class);

		Mockito.when(
			language.format(
				Mockito.any(ResourceBundle.class), Mockito.eq("x-ago-by-x"),
				Mockito.any(Object[].class))
		).thenAnswer(
			(Answer<String>)invocationOnMock -> StringUtil.merge(
				invocationOnMock.getArgument(2, Object[].class),
				StringPool.SPACE)
		);

		Mockito.when(
			language.getTimeDescription(
				Mockito.eq(_httpServletRequest), Mockito.anyLong(),
				Mockito.eq(true))
		).thenReturn(
			"1 minute"
		);

		languageUtil.setLanguage(language);
	}

	private void _setUpPortalUtil() {
		PortalUtil portalUtil = new PortalUtil();

		Portal portal = Mockito.mock(Portal.class);

		Mockito.when(
			portal.getHttpServletRequest(_renderRequest)
		).thenReturn(
			_httpServletRequest
		);

		portalUtil.setPortal(portal);
	}

	private final HttpServletRequest _httpServletRequest = Mockito.mock(
		HttpServletRequest.class);
	private final RenderRequest _renderRequest = Mockito.mock(
		RenderRequest.class);

}