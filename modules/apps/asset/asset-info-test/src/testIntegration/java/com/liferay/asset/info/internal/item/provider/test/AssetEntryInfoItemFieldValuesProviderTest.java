/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.info.internal.item.provider.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.display.page.constants.AssetDisplayPageConstants;
import com.liferay.asset.display.page.service.AssetDisplayPageEntryLocalService;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.info.field.InfoFieldValue;
import com.liferay.info.item.InfoItemFieldValues;
import com.liferay.info.item.provider.InfoItemFieldValuesProvider;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.test.util.DisplayPageTemplateTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.portlet.constants.FriendlyURLResolverConstants;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Balázs Sáfrány-Kovalik
 */
@RunWith(Arquillian.class)
public class AssetEntryInfoItemFieldValuesProviderTest {

	@ClassRule
	@Rule
	public static final TestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_classNameId = _classNameLocalService.getClassNameId(
			JournalArticle.class);
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testGetInfoItemFieldValuesReturnsDateForDateFields()
		throws Exception {

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		AssetEntry assetEntry = _assetEntryLocalService.fetchEntry(
			_classNameId, journalArticle.getResourcePrimKey());

		_setupServiceContext(_getThemeDisplay());

		InfoItemFieldValues infoItemFieldValues =
			_infoItemFieldValuesProvider.getInfoItemFieldValues(assetEntry);

		_assertDateFieldValue(
			assetEntry.getCreateDate(), infoItemFieldValues, "createDate");
		_assertDateFieldValue(
			assetEntry.getExpirationDate(), infoItemFieldValues,
			"expirationDate");
		_assertDateFieldValue(
			assetEntry.getModifiedDate(), infoItemFieldValues, "modifiedDate");
	}

	@Test
	public void testGetInfoItemFieldValuesWithAssetDisplayPageEntry()
		throws Exception {

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				_group.getGroupId(), _classNameId,
				journalArticle.getDDMStructureKey(), true,
				WorkflowConstants.STATUS_APPROVED);

		_assetDisplayPageEntryLocalService.addAssetDisplayPageEntry(
			TestPropsValues.getUserId(), _group.getGroupId(), _classNameId,
			journalArticle.getResourcePrimKey(),
			layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
			AssetDisplayPageConstants.TYPE_SPECIFIC, new ServiceContext());

		_testGetInfoItemFieldValues(
			journalArticle,
			FriendlyURLResolverConstants.URL_SEPARATOR_JOURNAL_ARTICLE);
	}

	@Test
	public void testGetInfoItemFieldValuesWithLayoutUuid() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		journalArticle.setLayoutUuid(layout.getUuid());

		journalArticle = _journalArticleLocalService.updateJournalArticle(
			journalArticle);

		_testGetInfoItemFieldValues(
			journalArticle, JournalArticleConstants.CANONICAL_URL_SEPARATOR);
	}

	private void _assertDateFieldValue(
		Date expectedDate, InfoItemFieldValues infoItemFieldValues,
		String fieldName) {

		InfoFieldValue<Object> infoFieldValue =
			infoItemFieldValues.getInfoFieldValue(fieldName);

		Assert.assertEquals(expectedDate, infoFieldValue.getValue());
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));

		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		themeDisplay.setLayout(layout);
		themeDisplay.setLayoutSet(layout.getLayoutSet());
		themeDisplay.setLayoutTypePortlet(
			(LayoutTypePortlet)layout.getLayoutType());

		themeDisplay.setRealUser(TestPropsValues.getUser());

		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		httpServletRequest.setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);

		themeDisplay.setRequest(httpServletRequest);

		themeDisplay.setLocale(LocaleUtil.getSiteDefault());
		themeDisplay.setPortalURL("http://localhost:8080");
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setServerName("localhost");
		themeDisplay.setServerPort(8080);
		themeDisplay.setSiteGroupId(_group.getGroupId());

		return themeDisplay;
	}

	private void _setupServiceContext(ThemeDisplay themeDisplay)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		serviceContext.setRequest(themeDisplay.getRequest());

		ServiceContextThreadLocal.pushServiceContext(serviceContext);
	}

	private void _testGetInfoItemFieldValues(
			JournalArticle journalArticle, String urlSeparator)
		throws Exception {

		AssetEntry assetEntry = _assetEntryLocalService.fetchEntry(
			_classNameId, journalArticle.getResourcePrimKey());

		ThemeDisplay themeDisplay = _getThemeDisplay();

		_setupServiceContext(themeDisplay);

		InfoItemFieldValues infoItemFieldValues =
			_infoItemFieldValuesProvider.getInfoItemFieldValues(assetEntry);

		InfoFieldValue<Object> displayPageURLInfoFieldValue =
			infoItemFieldValues.getInfoFieldValue("displayPageURL");

		String groupFriendlyURL = _portal.getGroupFriendlyURL(
			_group.getPublicLayoutSet(), themeDisplay, false, false);

		Assert.assertEquals(
			groupFriendlyURL + urlSeparator +
				journalArticle.getUrlTitle(LocaleUtil.getSiteDefault()),
			displayPageURLInfoFieldValue.getValue());
	}

	@Inject
	private AssetDisplayPageEntryLocalService
		_assetDisplayPageEntryLocalService;

	@Inject
	private AssetEntryLocalService _assetEntryLocalService;

	private long _classNameId;

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	private Group _group;

	@Inject(
		filter = "component.name=com.liferay.asset.info.internal.item.provider.AssetEntryInfoItemFieldValuesProvider"
	)
	private InfoItemFieldValuesProvider _infoItemFieldValuesProvider;

	@Inject
	private JournalArticleLocalService _journalArticleLocalService;

	@Inject
	private Portal _portal;

}