/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.portal.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.constants.CTConstants;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTEntryLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.action.UpdateLanguageAction;
import com.liferay.portal.kernel.change.tracking.CTCollectionPreviewThreadLocal;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LanguageIds;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.servlet.http.HttpSession;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Pei-Jung Lan
 */
@LanguageIds(
	availableLanguageIds = {"de_DE", "en_US"}, defaultLanguageId = "en_US"
)
@RunWith(Arquillian.class)
public class UpdateLanguageActionCTTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testExecute() throws Exception {
		CTCollection ctCollection = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, RandomTestUtil.randomString(), RandomTestUtil.randomString());

		Assert.assertTrue(
			ListUtil.isEmpty(
				_ctEntryLocalService.getCTEntries(
					ctCollection.getCtCollectionId(),
					_portal.getClassNameId(User.class))));

		UpdateLanguageAction updateLanguageAction = new UpdateLanguageAction();

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));

		Layout layout = _layoutLocalService.getLayout(
			TestPropsValues.getPlid());

		themeDisplay.setLayout(layout);
		themeDisplay.setLayoutSet(layout.getLayoutSet());

		themeDisplay.setSignedIn(true);
		themeDisplay.setSiteGroupId(TestPropsValues.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockHttpServletRequest.setParameter(
			"languageId", LocaleUtil.toLanguageId(LocaleUtil.GERMANY));

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollection.getCtCollectionId())) {

			updateLanguageAction.execute(
				null, mockHttpServletRequest, new MockHttpServletResponse());
		}

		Assert.assertTrue(
			ListUtil.isEmpty(
				_ctEntryLocalService.getCTEntries(
					ctCollection.getCtCollectionId(),
					_portal.getClassNameId(User.class))));
	}

	@Test
	public void testGetRedirectURL() throws Exception {
		CTCollection ctCollection = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, RandomTestUtil.randomString(), RandomTestUtil.randomString());

		CTCollectionPreviewThreadLocal.setCTCollectionId(
			ctCollection.getCtCollectionId());

		Layout layout = _layoutLocalService.getLayout(
			TestPropsValues.getPlid());

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setLayout(layout);
		themeDisplay.setLayoutSet(layout.getLayoutSet());

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setSignedIn(true);
		themeDisplay.setSiteGroupId(TestPropsValues.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		String url = HttpComponentsUtil.addParameter(
			_portal.getLayoutFriendlyURL(layout, themeDisplay), "p_l_mode",
			"preview");

		url = HttpComponentsUtil.addParameter(
			url, "previewCTCollectionId", ctCollection.getCtCollectionId());

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setContextPath("");

		HttpSession httpSession = mockHttpServletRequest.getSession();

		httpSession.setAttribute(WebKeys.LOCALE, LocaleUtil.ENGLISH);

		mockHttpServletRequest.setParameter("redirect", url);

		UpdateLanguageAction updateLanguageAction = new UpdateLanguageAction();

		PropsUtil.set(PropsKeys.LOCALE_PREPEND_FRIENDLY_URL_STYLE, "0");

		Assert.assertEquals(
			url,
			updateLanguageAction.getRedirect(
				mockHttpServletRequest, themeDisplay, LocaleUtil.ENGLISH));

		url = HttpComponentsUtil.setParameter(
			url, "previewCTCollectionId",
			CTConstants.CT_COLLECTION_ID_PRODUCTION);

		mockHttpServletRequest.setParameter("redirect", url);

		Assert.assertEquals(
			url,
			updateLanguageAction.getRedirect(
				mockHttpServletRequest, themeDisplay, LocaleUtil.ENGLISH));
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private CTCollectionLocalService _ctCollectionLocalService;

	@Inject
	private CTEntryLocalService _ctEntryLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private Portal _portal;

}