/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.type.controller.asset.display.internal.portlet.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.display.page.constants.AssetDisplayPageConstants;
import com.liferay.asset.display.page.model.AssetDisplayPageEntry;
import com.liferay.asset.display.page.portlet.AssetDisplayPageFriendlyURLProvider;
import com.liferay.asset.display.page.service.AssetDisplayPageEntryLocalService;
import com.liferay.info.item.InfoItemReference;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.test.util.DisplayPageTemplateTestUtil;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.constants.FriendlyURLResolverConstants;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Mikel Lorza
 */
@RunWith(Arquillian.class)
public class AssetDisplayPageFriendlyURLProviderImplTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		_journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, StringPool.BLANK,
			true, RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(), null,
			LocaleUtil.getSiteDefault(), null, false, false, serviceContext);

		long classNameId = _portal.getClassNameId(
			JournalArticle.class.getName());

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				_group.getGroupId(), classNameId,
				_journalArticle.getDDMStructureId(), true,
				WorkflowConstants.STATUS_APPROVED);

		_assetDisplayPageEntry =
			_assetDisplayPageEntryLocalService.addAssetDisplayPageEntry(
				TestPropsValues.getUserId(), _group.getGroupId(), classNameId,
				_journalArticle.getResourcePrimKey(),
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
				AssetDisplayPageConstants.TYPE_SPECIFIC, serviceContext);
	}

	@After
	public void tearDown() {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testGetFriendlyURL() throws Exception {
		_setUpThemeDisplay(
			_layoutLocalService.getLayout(_assetDisplayPageEntry.getPlid()));

		_assertGetFriendlyURL(
			FriendlyURLResolverConstants.URL_SEPARATOR_JOURNAL_ARTICLE);
	}

	@Test
	public void testGetFriendlyURLPreventsCrossSiteCircularCall()
		throws Exception {

		Group group = GroupTestUtil.addGroup();

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		DisplayPageTemplateTestUtil.addDisplayPageTemplate(
			_group.getGroupId(),
			_portal.getClassNameId(JournalArticle.class.getName()),
			journalArticle.getDDMStructureId(), true,
			WorkflowConstants.STATUS_APPROVED);

		_setUpThemeDisplay(
			_layoutLocalService.getLayout(_assetDisplayPageEntry.getPlid()));

		Assert.assertNull(
			_assetDisplayPageFriendlyURLProvider.getFriendlyURL(
				new InfoItemReference(
					JournalArticle.class.getName(),
					journalArticle.getResourcePrimKey()),
				LocaleUtil.getSiteDefault(), _themeDisplay));
	}

	@Test
	public void testGetFriendlyURLWithLayoutUUIDBasedAssetDisplayPage()
		throws Exception {

		Layout layout = LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId());

		_setUpThemeDisplay(layout);

		_assertGetFriendlyURL(
			JournalArticleConstants.CANONICAL_URL_SEPARATOR,
			JournalTestUtil.addArticle(
				_group.getGroupId(),
				JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				JournalArticleConstants.CLASS_NAME_ID_DEFAULT, StringPool.BLANK,
				true, RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomLocaleStringMap(), layout.getUuid(),
				LocaleUtil.getSiteDefault(), null, false, false,
				ServiceContextThreadLocal.getServiceContext()));
	}

	private void _assertGetFriendlyURL(String urlSeparator) throws Exception {
		Assert.assertEquals(
			StringBundler.concat(
				_portal.getGroupFriendlyURL(
					_group.getPublicLayoutSet(), _themeDisplay, false, false),
				urlSeparator,
				_journalArticle.getUrlTitle(LocaleUtil.getSiteDefault())),
			_assetDisplayPageFriendlyURLProvider.getFriendlyURL(
				new InfoItemReference(
					JournalArticle.class.getName(),
					_journalArticle.getResourcePrimKey()),
				LocaleUtil.getSiteDefault(), _themeDisplay));
	}

	private void _assertGetFriendlyURL(
			String urlSeparator, JournalArticle journalArticle)
		throws Exception {

		Assert.assertEquals(
			StringBundler.concat(
				_portal.getGroupFriendlyURL(
					_group.getPublicLayoutSet(), _themeDisplay, false, false),
				urlSeparator,
				journalArticle.getUrlTitle(LocaleUtil.getSiteDefault())),
			_assetDisplayPageFriendlyURLProvider.getFriendlyURL(
				new InfoItemReference(
					JournalArticle.class.getName(),
					journalArticle.getResourcePrimKey()),
				LocaleUtil.getSiteDefault(), _themeDisplay));
	}

	private void _setUpThemeDisplay(Layout layout) throws Exception {
		_themeDisplay = ContentLayoutTestUtil.getThemeDisplay(
			_companyLocalService.getCompany(_group.getCompanyId()), _group,
			layout);

		_themeDisplay.setPortalURL("http://localhost:8080");
		_themeDisplay.setServerName("localhost");
		_themeDisplay.setServerPort(8080);
	}

	private AssetDisplayPageEntry _assetDisplayPageEntry;

	@Inject
	private AssetDisplayPageEntryLocalService
		_assetDisplayPageEntryLocalService;

	@Inject(
		filter = "component.name=com.liferay.layout.type.controller.asset.display.internal.portlet.AssetDisplayPageFriendlyURLProviderImpl"
	)
	private AssetDisplayPageFriendlyURLProvider
		_assetDisplayPageFriendlyURLProvider;

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private JournalArticle _journalArticle;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private Portal _portal;

	private ThemeDisplay _themeDisplay;

}