/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.reports.journal.internal.info.item.test;

import com.liferay.analytics.reports.info.item.AnalyticsReportsInfoItem;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.display.page.constants.AssetDisplayPageConstants;
import com.liferay.asset.display.page.model.AssetDisplayPageEntry;
import com.liferay.asset.display.page.service.AssetDisplayPageEntryLocalService;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.test.util.DisplayPageTemplateTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Cristina González
 */
@RunWith(Arquillian.class)
public class JournalArticleAnalyticsReportsInfoItemTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testGetAuthorName() throws Exception {
		User user = TestPropsValues.getUser();

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			user.getUserId(), _group.getGroupId(), 0);

		Assert.assertEquals(
			user.getFullName(),
			_analyticsReportsInfoItem.getAuthorName(journalArticle));
	}

	@Test
	public void testGetAuthorNameWithDeletedUser() throws Exception {
		User user = UserTestUtil.addUser(_group.getGroupId());

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			user.getUserId(), _group.getGroupId(), 0);

		_userLocalService.deleteUser(user);

		Assert.assertEquals(
			StringPool.BLANK,
			_analyticsReportsInfoItem.getAuthorName(journalArticle));
	}

	@Test
	public void testGetAuthorUserId() throws Exception {
		User user = TestPropsValues.getUser();

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			user.getUserId(), _group.getGroupId(), 0);

		Assert.assertEquals(
			user.getUserId(),
			_analyticsReportsInfoItem.getAuthorUserId(journalArticle));
	}

	@Test
	public void testGetAuthorUserIdWithDeletedUser() throws Exception {
		User user = UserTestUtil.addUser(_group.getGroupId());

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			user.getUserId(), _group.getGroupId(), 0);

		_userLocalService.deleteUser(user);

		Assert.assertEquals(
			0L, _analyticsReportsInfoItem.getAuthorUserId(journalArticle));
	}

	@Test
	public void testGetAvailableLocales() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(), 0,
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, _getLocalizedMap(),
			_getLocalizedMap(), _getLocalizedMap(), LocaleUtil.SPAIN, false,
			false,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));

		GroupTestUtil.updateDisplaySettings(
			_group.getGroupId(),
			Arrays.asList(LocaleUtil.BRAZIL, LocaleUtil.SPAIN),
			LocaleUtil.SPAIN);

		Assert.assertEquals(
			Arrays.asList(LocaleUtil.BRAZIL, LocaleUtil.SPAIN),
			_analyticsReportsInfoItem.getAvailableLocales(journalArticle));
	}

	@Test
	public void testGetCanonicalURL() throws Exception {
		User user = TestPropsValues.getUser();

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			user.getUserId(), _group.getGroupId(), 0);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				_group.getGroupId(),
				_portal.getClassNameId(JournalArticle.class.getName()),
				journalArticle.getDDMStructureId(),
				journalArticle.getDDMStructureKey(), true,
				WorkflowConstants.STATUS_APPROVED);

		_assetDisplayPageEntryLocalService.addAssetDisplayPageEntry(
			TestPropsValues.getUserId(), _group.getGroupId(),
			_portal.getClassNameId(JournalArticle.class.getName()),
			journalArticle.getResourcePrimKey(),
			layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
			AssetDisplayPageConstants.TYPE_SPECIFIC,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		try {
			ServiceContext serviceContext =
				ServiceContextTestUtil.getServiceContext(_group.getGroupId());

			MockHttpServletRequest mockHttpServletRequest =
				new MockHttpServletRequest();

			mockHttpServletRequest.setAttribute(
				WebKeys.CURRENT_COMPLETE_URL, StringPool.BLANK);

			ThemeDisplay themeDisplay = new ThemeDisplay();

			themeDisplay.setCompany(
				_companyLocalService.fetchCompany(
					TestPropsValues.getCompanyId()));

			Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

			themeDisplay.setLayoutSet(layout.getLayoutSet());

			themeDisplay.setRequest(mockHttpServletRequest);
			themeDisplay.setScopeGroupId(_group.getGroupId());
			themeDisplay.setSiteGroupId(_group.getGroupId());

			mockHttpServletRequest.setAttribute(
				WebKeys.THEME_DISPLAY, themeDisplay);

			serviceContext.setRequest(mockHttpServletRequest);

			ServiceContextThreadLocal.pushServiceContext(serviceContext);

			String canonicalURL = _analyticsReportsInfoItem.getCanonicalURL(
				journalArticle, LocaleUtil.US);

			Assert.assertTrue(
				canonicalURL.contains(
					journalArticle.getUrlTitle(LocaleUtil.US)));
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	@Test
	public void testGetDefaultLocale() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(), 0,
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, _getLocalizedMap(),
			_getLocalizedMap(), _getLocalizedMap(), LocaleUtil.SPAIN, false,
			false,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));

		GroupTestUtil.updateDisplaySettings(
			_group.getGroupId(),
			Arrays.asList(LocaleUtil.BRAZIL, LocaleUtil.SPAIN),
			LocaleUtil.SPAIN);

		Assert.assertEquals(
			LocaleUtil.SPAIN,
			_analyticsReportsInfoItem.getDefaultLocale(journalArticle));
	}

	@Test
	public void testGetPublishDate() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString());

		Assert.assertEquals(
			journalArticle.getCreateDate(),
			_analyticsReportsInfoItem.getPublishDate(journalArticle));
	}

	@Test
	public void testGetPublishDateWithDisplayPage() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString());

		DDMStructure ddmStructure = journalArticle.getDDMStructure();

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				_group.getGroupId(),
				_portal.getClassNameId(JournalArticle.class.getName()),
				ddmStructure.getStructureId(), ddmStructure.getStructureKey(),
				true, WorkflowConstants.STATUS_APPROVED);

		AssetDisplayPageEntry assetDisplayPageEntry =
			_assetDisplayPageEntryLocalService.addAssetDisplayPageEntry(
				TestPropsValues.getUserId(), _group.getGroupId(),
				_portal.getClassNameId(JournalArticle.class.getName()),
				journalArticle.getResourcePrimKey(),
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
				AssetDisplayPageConstants.TYPE_SPECIFIC,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Assert.assertEquals(
			assetDisplayPageEntry.getCreateDate(),
			_analyticsReportsInfoItem.getPublishDate(journalArticle));
	}

	@Test
	public void testGetTitle() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString());

		Assert.assertEquals(
			journalArticle.getTitle(LocaleUtil.US),
			_analyticsReportsInfoItem.getTitle(journalArticle, LocaleUtil.US));
	}

	private Map<Locale, String> _getLocalizedMap() {
		return HashMapBuilder.put(
			LocaleUtil.SPAIN, RandomTestUtil.randomString()
		).put(
			LocaleUtil.US, RandomTestUtil.randomString()
		).build();
	}

	@Inject(
		filter = "component.name=com.liferay.analytics.reports.journal.internal.info.item.JournalArticleAnalyticsReportsInfoItem"
	)
	private AnalyticsReportsInfoItem<JournalArticle> _analyticsReportsInfoItem;

	@Inject
	private AssetDisplayPageEntryLocalService
		_assetDisplayPageEntryLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private Portal _portal;

	@Inject
	private UserLocalService _userLocalService;

}