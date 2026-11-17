/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.content.web.internal.frontend.taglib.clay.servlet.taglib;

import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.journal.model.JournalArticle;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.RenderRequest;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author David Reyes
 */
public class JournalArticleVerticalCardTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_groupLocalServiceUtilMockedStatic = Mockito.mockStatic(
			GroupLocalServiceUtil.class);
	}

	@After
	public void tearDown() {
		_groupLocalServiceUtilMockedStatic.close();
	}

	@Test
	@TestInfo("LPD-111121")
	public void testGetTitle() throws Exception {
		RenderRequest renderRequest = Mockito.mock(RenderRequest.class);

		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.when(
			themeDisplay.getLocale()
		).thenReturn(
			LocaleUtil.US
		);

		Mockito.when(
			renderRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			themeDisplay
		);

		AssetRenderer<JournalArticle> assetRenderer = Mockito.mock(
			AssetRenderer.class);

		Mockito.when(
			assetRenderer.getTitle(LocaleUtil.US)
		).thenReturn(
			"Article"
		);

		JournalArticle article = Mockito.mock(JournalArticle.class);

		Mockito.when(
			article.getGroupId()
		).thenReturn(
			_GROUP_ID
		);

		Group group = Mockito.mock(Group.class);

		Mockito.when(
			group.getDescriptiveName(LocaleUtil.US)
		).thenReturn(
			"Test & Co"
		);

		_groupLocalServiceUtilMockedStatic.when(
			() -> GroupLocalServiceUtil.fetchGroup(_GROUP_ID)
		).thenReturn(
			group
		);

		JournalArticleVerticalCard journalArticleVerticalCard =
			new JournalArticleVerticalCard(
				article, assetRenderer, renderRequest);

		Assert.assertEquals(
			"Article (Test & Co)", journalArticleVerticalCard.getTitle());
	}

	private static final long _GROUP_ID = RandomTestUtil.randomLong();

	private MockedStatic<GroupLocalServiceUtil>
		_groupLocalServiceUtilMockedStatic;

}