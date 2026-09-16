/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.publisher.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.model.AssetListEntrySegmentsEntryRel;
import com.liferay.asset.list.service.AssetListEntryLocalService;
import com.liferay.asset.list.service.AssetListEntrySegmentsEntryRelLocalService;
import com.liferay.asset.publisher.constants.AssetPublisherPortletKeys;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.test.util.LayoutPageTemplateTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.constants.SegmentsEntryConstants;

import jakarta.portlet.PortletPreferences;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Eudaldo Alonso
 */
@RunWith(Arquillian.class)
public class AddAssetListMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group1 = GroupTestUtil.addGroup();

		_group1Layout = LayoutTestUtil.addTypePortletLayout(_group1);

		_group2 = GroupTestUtil.addGroup();

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateTestUtil.addLayoutPageTemplateEntry(
				_group2.getGroupId(),
				LayoutPageTemplateEntryTypeConstants.WIDGET_PAGE,
				WorkflowConstants.STATUS_APPROVED);

		_group2Layout = _layoutLocalService.getLayout(
			layoutPageTemplateEntry.getPlid());
	}

	@Test
	public void testAddAssetListFromDynamicCollection() throws Exception {
		_testAddAssetListFromDynamicCollection(_group1, _group1Layout, _group1);
		_testAddAssetListFromDynamicCollection(_group2, _group2Layout, _group1);
	}

	@Test
	public void testAddAssetListFromManualCollection() throws Exception {
		_testAddAssetListFromManualCollection(_group1, _group1Layout, _group1);
		_testAddAssetListFromManualCollection(_group2, _group2Layout, _group1);
	}

	private ThemeDisplay _getThemeDisplay(Group group, Layout layout)
		throws Exception {

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.fetchCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setLayout(layout);
		themeDisplay.setLayoutSet(layout.getLayoutSet());
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setScopeGroupId(group.getGroupId());
		themeDisplay.setSiteGroupId(group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	private void _testAddAssetListFromDynamicCollection(
			Group expectedGroup, Layout layout, Group themeDisplayGroup)
		throws Exception {

		String portletId = LayoutTestUtil.addPortletToLayout(
			layout, AssetPublisherPortletKeys.ASSET_PUBLISHER,
			HashMapBuilder.put(
				"anyAssetType", new String[] {_CLASS_NAME_ID_NONEXISTENT}
			).put(
				"classNameIds", new String[] {_CLASS_NAME_IDS}
			).put(
				"selectionStyle", new String[] {"dynamic"}
			).build());

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.addParameter(
			"title", RandomTestUtil.randomString());
		mockLiferayPortletActionRequest.addParameter(
			"portletResource", portletId);

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay(themeDisplayGroup, layout));

		_mvcActionCommand.processAction(
			mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse());

		PortletPreferences portletPreferences =
			PortletPreferencesFactoryUtil.getExistingPortletSetup(
				layout, portletId);

		Assert.assertEquals(
			"asset-list", portletPreferences.getValue("selectionStyle", null));

		AssetListEntry assetListEntry =
			_assetListEntryLocalService.
				fetchAssetListEntryByExternalReferenceCode(
					portletPreferences.getValue(
						"assetListEntryExternalReferenceCode", null),
					expectedGroup.getGroupId());

		AssetListEntrySegmentsEntryRel assetListEntrySegmentsEntryRel =
			_assetListEntrySegmentsEntryRelLocalService.
				fetchAssetListEntrySegmentsEntryRel(
					assetListEntry.getAssetListEntryId(),
					SegmentsEntryConstants.ID_DEFAULT);

		UnicodeProperties unicodeProperties = UnicodePropertiesBuilder.load(
			assetListEntrySegmentsEntryRel.getTypeSettings()
		).build();

		Assert.assertEquals(
			_CLASS_NAME_ID_NONEXISTENT,
			unicodeProperties.getProperty("anyAssetType", null));
		Assert.assertEquals(
			_CLASS_NAME_IDS,
			unicodeProperties.getProperty("classNameIds", null));
	}

	private void _testAddAssetListFromManualCollection(
			Group expectedGroup, Layout layout, Group themeDisplayGroup)
		throws Exception {

		String portletId = LayoutTestUtil.addPortletToLayout(
			layout, AssetPublisherPortletKeys.ASSET_PUBLISHER,
			Collections.singletonMap(
				"selectionStyle", new String[] {"manual"}));

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.addParameter(
			"title", RandomTestUtil.randomString());
		mockLiferayPortletActionRequest.addParameter(
			"portletResource", portletId);
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay(themeDisplayGroup, layout));

		_mvcActionCommand.processAction(
			mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse());

		PortletPreferences portletPreferences =
			PortletPreferencesFactoryUtil.getExistingPortletSetup(
				layout, portletId);

		String externalReferenceCode = portletPreferences.getValue(
			"assetListEntryExternalReferenceCode", null);

		Assert.assertNotNull(
			_assetListEntryLocalService.
				fetchAssetListEntryByExternalReferenceCode(
					externalReferenceCode, expectedGroup.getGroupId()));

		Assert.assertEquals(
			"asset-list", portletPreferences.getValue("selectionStyle", null));
	}

	private static final String _CLASS_NAME_ID_NONEXISTENT = String.valueOf(
		RandomTestUtil.nextLong());

	private static final String _CLASS_NAME_IDS =
		PortalUtil.getClassNameId(Layout.class) + StringPool.COMMA +
			RandomTestUtil.nextLong();

	@Inject
	private AssetListEntryLocalService _assetListEntryLocalService;

	@Inject
	private AssetListEntrySegmentsEntryRelLocalService
		_assetListEntrySegmentsEntryRelLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group1;

	private Layout _group1Layout;

	@DeleteAfterTestRun
	private Group _group2;

	private Layout _group2Layout;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject(filter = "mvc.command.name=/asset_publisher/add_asset_list")
	private MVCActionCommand _mvcActionCommand;

}