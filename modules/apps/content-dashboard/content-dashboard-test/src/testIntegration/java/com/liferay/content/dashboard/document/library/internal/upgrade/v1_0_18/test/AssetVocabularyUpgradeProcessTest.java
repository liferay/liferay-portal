/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.dashboard.document.library.internal.upgrade.v1_0_18.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.exportimport.kernel.service.StagingLocalService;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.PortletPreferencesIds;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TextFormatter;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import jakarta.portlet.PortletPreferences;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Balázs Sáfrány-Kovalik
 */
@RunWith(Arquillian.class)
public class AssetVocabularyUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule liferayIntegrationTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testUpgrade() throws Exception {
		Company company = CompanyTestUtil.addCompany();

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					company.getCompanyId())) {

			Group companyGroup = _groupLocalService.getCompanyGroup(
				company.getCompanyId());

			String topicExternalReferenceCode = _setRandomExternalReferenceCode(
				companyGroup.getGroupId(), "topic");

			_setRandomExternalReferenceCode(
				companyGroup.getGroupId(), "audience");
			_setRandomExternalReferenceCode(companyGroup.getGroupId(), "stage");

			User user = UserTestUtil.getAdminUser(company.getCompanyId());

			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(user));

			PrincipalThreadLocal.setName(user.getUserId());

			try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
					"com.liferay.batch.engine.internal." +
						"BatchEngineImportTaskExecutorImpl",
					LoggerTestUtil.OFF)) {

				_stagingLocalService.enableLocalStaging(
					user.getUserId(), companyGroup, false, false,
					ServiceContextTestUtil.getServiceContext(
						companyGroup.getGroupId()));
			}

			Group stagingGroup = companyGroup.getStagingGroup();

			Group group = GroupTestUtil.addGroupToCompany(
				company.getCompanyId());

			Layout layout = LayoutTestUtil.addTypePortletLayout(group);

			String portletId1 = LayoutTestUtil.addPortletToLayout(
				layout,
				"com_liferay_asset_categories_navigation_web_portlet_" +
					"AssetCategoriesNavigationPortlet");

			String defaultPreferences = StringBundler.concat(
				"<portlet-preferences><preference><name>",
				"assetVocabularyExternalReferenceCodes_L_GLOBAL</name><value>",
				topicExternalReferenceCode,
				"</value></preference></portlet-preferences>");

			_portletPreferencesLocalService.addPortletPreferences(
				group.getCompanyId(), PortletKeys.PREFS_OWNER_ID_DEFAULT,
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, layout.getPlid(),
				portletId1,
				_portletLocalService.fetchPortletById(
					group.getCompanyId(), portletId1),
				defaultPreferences);

			String portletId2 = LayoutTestUtil.addPortletToLayout(
				layout,
				"com_liferay_portal_search_web_category_facet_portlet_" +
					"CategoryFacetPortlet");

			defaultPreferences = StringBundler.concat(
				"<portlet-preferences><preference><name>",
				"groupVocabularyExternalReferenceCodes</name><value>L_GLOBAL",
				"&amp;&amp;", topicExternalReferenceCode,
				"</value></preference></portlet-preferences>");

			_portletPreferencesLocalService.addPortletPreferences(
				group.getCompanyId(), PortletKeys.PREFS_OWNER_ID_DEFAULT,
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, layout.getPlid(),
				portletId2,
				_portletLocalService.fetchPortletById(
					group.getCompanyId(), portletId2),
				defaultPreferences);

			_runUpgrade();

			_assertExternalReferenceCodeUpdated(
				companyGroup.getGroupId(), "audience");
			_assertExternalReferenceCodeUpdated(
				companyGroup.getGroupId(), "stage");
			_assertExternalReferenceCodeUpdated(
				companyGroup.getGroupId(), "topic");
			_assertExternalReferenceCodeUpdated(
				stagingGroup.getGroupId(), "audience");
			_assertExternalReferenceCodeUpdated(
				stagingGroup.getGroupId(), "stage");
			_assertExternalReferenceCodeUpdated(
				stagingGroup.getGroupId(), "topic");

			PortletPreferencesIds portletPreferencesIds =
				new PortletPreferencesIds(
					group.getCompanyId(), PortletKeys.PREFS_OWNER_ID_DEFAULT,
					PortletKeys.PREFS_OWNER_TYPE_LAYOUT, layout.getPlid(),
					portletId1);

			PortletPreferences jxPortletPreferences =
				_portletPreferencesLocalService.getPreferences(
					portletPreferencesIds);

			Assert.assertEquals(
				"L_TOPIC",
				jxPortletPreferences.getValue(
					"assetVocabularyExternalReferenceCodes_L_GLOBAL",
					StringPool.BLANK));

			portletPreferencesIds = new PortletPreferencesIds(
				group.getCompanyId(), PortletKeys.PREFS_OWNER_ID_DEFAULT,
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, layout.getPlid(),
				portletId2);

			jxPortletPreferences =
				_portletPreferencesLocalService.getPreferences(
					portletPreferencesIds);

			Assert.assertEquals(
				"L_GLOBAL&&L_TOPIC",
				jxPortletPreferences.getValue(
					"groupVocabularyExternalReferenceCodes", StringPool.BLANK));
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(TestPropsValues.getUser()));

			PrincipalThreadLocal.setName(TestPropsValues.getUserId());
		}
	}

	private void _assertExternalReferenceCodeUpdated(
		long groupId, String name) {

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.
				fetchAssetVocabularyByExternalReferenceCode(
					"L_" + TextFormatter.format(name, TextFormatter.A),
					groupId);

		Assert.assertNotNull(assetVocabulary);
	}

	private void _runUpgrade() throws Exception {
		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator, _CLASS_NAME);

		upgradeProcess.upgrade();
	}

	private String _setRandomExternalReferenceCode(long groupId, String name)
		throws Exception {

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.getGroupVocabulary(groupId, name);

		String externalReferenceCode = StringUtil.randomString();

		assetVocabulary.setExternalReferenceCode(externalReferenceCode);

		_assetVocabularyLocalService.updateAssetVocabulary(assetVocabulary);

		return externalReferenceCode;
	}

	private static final String _CLASS_NAME =
		"com.liferay.content.dashboard.document.library.internal.upgrade." +
			"v1_0_18.AssetVocabularyUpgradeProcess";

	@Inject
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private PortletLocalService _portletLocalService;

	@Inject
	private PortletPreferencesLocalService _portletPreferencesLocalService;

	@Inject
	private StagingLocalService _stagingLocalService;

	@Inject(
		filter = "(&(component.name=com.liferay.content.dashboard.document.library.internal.upgrade.registry.ContentDashboardDocumentLibraryImplUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}