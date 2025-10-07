/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.search;

import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.journal.service.JournalFolderLocalServiceUtil;
import com.liferay.journal.web.internal.security.permission.resource.JournalArticlePermission;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Jan Brychta
 */
public class EntriesCheckerTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@AfterClass
	public static void tearDownClass() {
		_journalArticleLocalServiceUtilMockedStatic.close();
		_journalArticlePermissionMockedStatic.close();
		_journalFolderLocalServiceUtilMockedStatic.close();

		_languageUtilMockedStatic.close();
	}

	@Test
	public void testGetRowCheckBox() {
		EntriesChecker entriesChecker = new EntriesChecker(
			_getLiferayPortletRequest(),
			Mockito.mock(LiferayPortletResponse.class));

		JournalArticle journalArticle = Mockito.mock(JournalArticle.class);

		String journalArticleId = String.valueOf(RandomTestUtil.randomLong());

		Mockito.when(
			journalArticle.getArticleId()
		).thenReturn(
			journalArticleId
		);

		_journalArticleLocalServiceUtilMockedStatic.when(
			() -> JournalArticleLocalServiceUtil.fetchArticle(
				Mockito.anyLong(), Mockito.anyString())
		).thenReturn(
			journalArticle
		);

		_journalArticlePermissionMockedStatic.when(
			() -> JournalArticlePermission.contains(
				Mockito.any(PermissionChecker.class),
				Mockito.any(JournalArticle.class), Mockito.anyString())
		).thenReturn(
			false
		);

		JournalFolder journalFolder = Mockito.mock(JournalFolder.class);

		_journalFolderLocalServiceUtilMockedStatic.when(
			() -> JournalFolderLocalServiceUtil.fetchFolder(Mockito.anyLong())
		).thenReturn(
			journalFolder
		);

		_languageUtilMockedStatic.when(
			() -> LanguageUtil.get(
				Mockito.any(Locale.class), Mockito.eq("select"))
		).thenReturn(
			"Select (mocked)"
		);

		String rowCheckBox = entriesChecker.getRowCheckBox(
			_getHttpServletRequest(), false, false, journalArticleId);

		Assert.assertFalse(rowCheckBox.isEmpty());
	}

	private HttpServletRequest _getHttpServletRequest() {
		HttpServletRequest httpServletRequest = Mockito.mock(
			HttpServletRequest.class);

		Mockito.when(
			httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			_getThemeDisplay()
		);

		return httpServletRequest;
	}

	private LiferayPortletRequest _getLiferayPortletRequest() {
		LiferayPortletRequest liferayPortletRequest = Mockito.mock(
			LiferayPortletRequest.class);

		ThemeDisplay themeDisplay = _getThemeDisplay();

		Mockito.when(
			liferayPortletRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			themeDisplay
		);

		return liferayPortletRequest;
	}

	private ThemeDisplay _getThemeDisplay() {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setPermissionChecker(
			Mockito.mock(PermissionChecker.class));

		return themeDisplay;
	}

	private static final MockedStatic<JournalArticleLocalServiceUtil>
		_journalArticleLocalServiceUtilMockedStatic = Mockito.mockStatic(
			JournalArticleLocalServiceUtil.class);
	private static final MockedStatic<JournalArticlePermission>
		_journalArticlePermissionMockedStatic = Mockito.mockStatic(
			JournalArticlePermission.class);
	private static final MockedStatic<JournalFolderLocalServiceUtil>
		_journalFolderLocalServiceUtilMockedStatic = Mockito.mockStatic(
			JournalFolderLocalServiceUtil.class);
	private static final MockedStatic<LanguageUtil> _languageUtilMockedStatic =
		Mockito.mockStatic(LanguageUtil.class);

}