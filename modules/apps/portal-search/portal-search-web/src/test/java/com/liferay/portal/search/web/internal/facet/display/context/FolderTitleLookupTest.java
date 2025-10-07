/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.facet.display.context;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.DocumentImpl;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.HitsImpl;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Locale;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Adam Brandizzi
 */
public class FolderTitleLookupTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetFolderTitle() throws SearchException {
		Hits hits = _getHitsWithDocument(
			_getTitleLocalizedFieldName(LocaleUtil.BRAZIL), "My Title");

		FolderTitleLookup folderTitleLookup = new FolderTitleLookupImpl(
			_mockFolderSearcher(hits), _mockHttpServletRequest(LocaleUtil.US));

		Assert.assertEquals(
			"My Title",
			folderTitleLookup.getFolderTitle(RandomTestUtil.randomLong()));
	}

	@Test
	public void testGetFolderTitleFromAnyLocalizedField()
		throws SearchException {

		Hits hits = _getHitsWithDocument(Field.TITLE, "My Title");

		FolderTitleLookup folderTitleLookup = new FolderTitleLookupImpl(
			_mockFolderSearcher(hits),
			_mockHttpServletRequest(LocaleUtil.BRAZIL));

		Assert.assertEquals(
			"My Title",
			folderTitleLookup.getFolderTitle(RandomTestUtil.randomLong()));
	}

	@Test
	public void testGetFolderTitleFromDisplayLocaleLocalizedField()
		throws SearchException {

		Hits hits = _getHitsWithDocument(
			_getTitleLocalizedFieldName(LocaleUtil.BRAZIL), "My Title");

		FolderTitleLookup folderTitleLookup = new FolderTitleLookupImpl(
			_mockFolderSearcher(hits),
			_mockHttpServletRequest(LocaleUtil.BRAZIL));

		Assert.assertEquals(
			"My Title",
			folderTitleLookup.getFolderTitle(RandomTestUtil.randomLong()));
	}

	private Hits _getHitsWithDocument(String fieldName, String value) {
		Document document = new DocumentImpl();

		document.addText(fieldName, value);

		Hits hits = new HitsImpl();

		hits.setLength(1);
		hits.setDocs(new Document[] {document});

		return hits;
	}

	private String _getTitleLocalizedFieldName(Locale locale) {
		return Field.TITLE + StringPool.UNDERLINE + locale;
	}

	private FolderSearcher _mockFolderSearcher(Hits hits)
		throws SearchException {

		FolderSearcher folderSearcher = Mockito.mock(FolderSearcher.class);

		Mockito.when(
			folderSearcher.search(Mockito.any())
		).thenReturn(
			hits
		);

		return folderSearcher;
	}

	private MockHttpServletRequest _mockHttpServletRequest(Locale locale) {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.when(
			themeDisplay.getLocale()
		).thenReturn(
			locale
		);

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		return mockHttpServletRequest;
	}

}