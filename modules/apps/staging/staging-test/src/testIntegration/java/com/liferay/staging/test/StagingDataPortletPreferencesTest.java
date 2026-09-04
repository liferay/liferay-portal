/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.staging.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationParameterMapFactoryUtil;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.service.StagingLocalServiceUtil;
import com.liferay.exportimport.kernel.staging.StagingUtil;
import com.liferay.exportimport.test.util.ExportImportTestUtil;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.journal.constants.JournalContentPortletKeys;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.site.navigation.constants.SiteNavigationMenuPortletKeys;
import com.liferay.site.navigation.model.SiteNavigationMenu;
import com.liferay.site.navigation.service.SiteNavigationMenuLocalService;

import jakarta.portlet.PortletPreferences;

import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Tamas Molnar
 */
@RunWith(Arquillian.class)
public class StagingDataPortletPreferencesTest
	extends BaseLocalStagingTestCase {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testJournalContentDataPortletPreferences() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			stagingGroup.getGroupId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString());

		Map<String, String[]> preferenceMap = HashMapBuilder.put(
			"articleExternalReferenceCode",
			new String[] {journalArticle.getExternalReferenceCode()}
		).put(
			"groupExternalReferenceCode",
			new String[] {stagingGroup.getExternalReferenceCode()}
		).build();

		String portletId = publishLayoutWithDisplayPortlet(
			JournalContentPortletKeys.JOURNAL_CONTENT, preferenceMap, true);

		Assert.assertEquals(
			journalArticle.getExternalReferenceCode(),
			livePortletPreferences.getValue(
				"articleExternalReferenceCode", StringPool.BLANK));

		publishPortlet(JournalPortletKeys.JOURNAL);

		publishLayoutWithDisplayPortlet(portletId, preferenceMap, false);

		JournalArticle liveJournalArticle =
			_journalArticleLocalService.getJournalArticleByUuidAndGroupId(
				journalArticle.getUuid(), liveGroup.getGroupId());

		Assert.assertEquals(
			liveJournalArticle.getExternalReferenceCode(),
			livePortletPreferences.getValue(
				"articleExternalReferenceCode", StringPool.BLANK));
	}

	@Test
	@TestInfo("LPS-130051")
	public void testSiteNavigationMenuDisplayStylePortletPreferences()
		throws Exception {

		Layout stagingLayout = LayoutTestUtil.addTypeContentLayout(
			stagingGroup);

		StagingLocalServiceUtil.enableLocalStaging(
			TestPropsValues.getUserId(), liveGroup, true, false,
			ServiceContextTestUtil.getServiceContext(liveGroup.getGroupId()));

		Layout draftStagingLayout = stagingLayout.fetchDraftLayout();

		JSONObject jsonObject = ContentLayoutTestUtil.addPortletToLayout(
			draftStagingLayout,
			SiteNavigationMenuPortletKeys.SITE_NAVIGATION_MENU);

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
				jsonObject.getJSONObject(
					"fragmentEntryLink"
				).getLong(
					"fragmentEntryLinkId"
				));

		String encodedPortletId = PortletIdCodec.encode(
			SiteNavigationMenuPortletKeys.SITE_NAVIGATION_MENU,
			fragmentEntryLink.getEditableValuesJSONObject(
			).getString(
				"instanceId"
			));

		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		Group companyGroup = company.getGroup();

		LayoutTestUtil.updateLayoutPortletPreferences(
			draftStagingLayout, encodedPortletId,
			HashMapBuilder.put(
				"displayStyle", "ddmTemplate_list-menu-ftl"
			).put(
				"displayStyleGroupExternalReferenceCode",
				companyGroup.getExternalReferenceCode()
			).put(
				"displayStyleGroupId", String.valueOf(companyGroup.getGroupId())
			).build());

		ContentLayoutTestUtil.publishLayout(draftStagingLayout, stagingLayout);

		long backgroundTaskId = StagingUtil.publishLayouts(
			TestPropsValues.getUserId(), stagingGroup.getGroupId(),
			liveGroup.getGroupId(), false,
			new long[] {stagingLayout.getLayoutId()},
			ExportImportConfigurationParameterMapFactoryUtil.
				buildFullPublishParameterMap());

		ExportImportTestUtil.assertBackgroundTaskSuccessful(backgroundTaskId);

		Layout liveLayout = _layoutLocalService.getLayoutByUuidAndGroupId(
			stagingLayout.getUuid(), liveGroup.getGroupId(), false);

		PortletPreferences liveLayoutPortletPreferences =
			PortletPreferencesFactoryUtil.getPortletSetup(
				liveLayout, encodedPortletId, null);

		Assert.assertEquals(
			"ddmTemplate_list-menu-ftl",
			liveLayoutPortletPreferences.getValue(
				"displayStyle", StringPool.BLANK));
	}

	@Test
	public void testSiteNavigationMenuPortletPreferences() throws Exception {
		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		Group companyGroup = company.getGroup();

		String portletId = publishLayoutWithDisplayPortlet(
			SiteNavigationMenuPortletKeys.SITE_NAVIGATION_MENU,
			HashMapBuilder.put(
				"displayDepth", new String[] {String.valueOf(0)}
			).put(
				"displayStyle", new String[] {"ddmTemplate_NAVBAR-BLANK-FTL"}
			).put(
				"displayStyleGroupExternalReferenceCode",
				new String[] {companyGroup.getExternalReferenceCode()}
			).put(
				"displayStyleGroupId",
				new String[] {String.valueOf(companyGroup.getGroupId())}
			).put(
				"displayStyleGroupKey",
				new String[] {String.valueOf(company.getCompanyId())}
			).put(
				"expandedLevels", new String[] {"auto"}
			).put(
				"rootMenuItemId", new String[0]
			).put(
				"rootMenuItemLevel", new String[] {String.valueOf(0)}
			).put(
				"rootMenuItemType", new String[] {"absolute"}
			).put(
				"siteNavigationMenuId", new String[] {String.valueOf(0)}
			).put(
				"siteNavigationMenuType", new String[] {String.valueOf(-1)}
			).build(),
			true);

		Assert.assertEquals(
			String.valueOf(0),
			livePortletPreferences.getValue("displayDepth", StringPool.BLANK));

		Assert.assertEquals(
			"ddmTemplate_NAVBAR-BLANK-FTL",
			livePortletPreferences.getValue("displayStyle", StringPool.BLANK));

		Assert.assertEquals(
			companyGroup.getExternalReferenceCode(),
			livePortletPreferences.getValue(
				"displayStyleGroupExternalReferenceCode", StringPool.BLANK));

		Assert.assertEquals(
			String.valueOf(companyGroup.getGroupId()),
			livePortletPreferences.getValue(
				"displayStyleGroupId", StringPool.BLANK));

		Assert.assertEquals(
			String.valueOf(company.getCompanyId()),
			livePortletPreferences.getValue(
				"displayStyleGroupKey", StringPool.BLANK));

		Assert.assertEquals(
			"auto",
			livePortletPreferences.getValue(
				"expandedLevels", StringPool.BLANK));

		Assert.assertEquals(
			StringPool.BLANK,
			livePortletPreferences.getValue(
				"rootMenuItemId", StringPool.BLANK));

		Assert.assertEquals(
			String.valueOf(0),
			livePortletPreferences.getValue(
				"rootMenuItemLevel", StringPool.BLANK));

		Assert.assertEquals(
			"absolute",
			livePortletPreferences.getValue(
				"rootMenuItemType", StringPool.BLANK));

		Assert.assertEquals(
			String.valueOf(0),
			livePortletPreferences.getValue(
				"siteNavigationMenuId", StringPool.BLANK));

		Assert.assertEquals(
			String.valueOf(-1),
			livePortletPreferences.getValue(
				"siteNavigationMenuType", StringPool.BLANK));

		SiteNavigationMenu siteNavigationMenu =
			_siteNavigationMenuLocalService.addSiteNavigationMenu(
				null, TestPropsValues.getUserId(), stagingGroup.getGroupId(),
				RandomTestUtil.randomString(),
				ServiceContextTestUtil.getServiceContext(
					stagingGroup.getGroupId()));

		LayoutTestUtil.updateLayoutPortletPreferences(
			stagingLayout, portletId,
			HashMapBuilder.put(
				"siteNavigationMenuExternalReferenceCode",
				siteNavigationMenu.getExternalReferenceCode()
			).put(
				"siteNavigationMenuId",
				String.valueOf(siteNavigationMenu.getSiteNavigationMenuId())
			).build());

		publishLayoutWithDisplayPortlet(portletId, null, false);

		Assert.assertEquals(
			siteNavigationMenu.getExternalReferenceCode(),
			livePortletPreferences.getValue(
				"siteNavigationMenuExternalReferenceCode", StringPool.BLANK));
	}

	protected String publishLayoutWithDisplayPortlet(
			String portletId, Map<String, String[]> preferenceMap,
			boolean addPortlet)
		throws Exception {

		if (addPortlet) {
			portletId = LayoutTestUtil.addPortletToLayout(
				stagingLayout, portletId, preferenceMap);
		}

		Map<String, String[]> parameterMap =
			ExportImportConfigurationParameterMapFactoryUtil.
				buildParameterMap();

		parameterMap.put(
			PortletDataHandlerKeys.PORTLET_DATA,
			new String[] {Boolean.TRUE.toString()});

		parameterMap.put(
			PortletDataHandlerKeys.PORTLET_DATA_ALL,
			new String[] {Boolean.TRUE.toString()});

		publishLayouts(parameterMap);

		livePortletPreferences = LayoutTestUtil.getPortletPreferences(
			liveLayout, portletId);

		return portletId;
	}

	protected PortletPreferences livePortletPreferences;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Inject
	private JournalArticleLocalService _journalArticleLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private SiteNavigationMenuLocalService _siteNavigationMenuLocalService;

}