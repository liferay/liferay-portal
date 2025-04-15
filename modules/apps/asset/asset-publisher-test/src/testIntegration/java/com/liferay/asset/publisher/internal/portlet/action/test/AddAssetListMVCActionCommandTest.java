/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.publisher.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.list.service.AssetListEntryLocalService;
import com.liferay.asset.publisher.constants.AssetPublisherPortletKeys;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

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
		_group = GroupTestUtil.addGroup();

		_layout = LayoutTestUtil.addTypePortletLayout(_group);
	}

	@Test
	public void testAddAssetListFromDynamicCollection() throws Exception {
		String portletId = LayoutTestUtil.addPortletToLayout(
			_layout, AssetPublisherPortletKeys.ASSET_PUBLISHER,
			Collections.singletonMap(
				"selectionStyle", new String[] {"dynamic"}));

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.addParameter(
			"title", RandomTestUtil.randomString());
		mockLiferayPortletActionRequest.addParameter(
			"portletResource", portletId);

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		_mvcActionCommand.processAction(
			mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse());

		PortletPreferences portletPreferences =
			PortletPreferencesFactoryUtil.getExistingPortletSetup(
				_layout, portletId);

		String externalReferenceCode = portletPreferences.getValue(
			"assetListEntryExternalReferenceCode", null);

		Assert.assertNotNull(
			_assetListEntryLocalService.
				fetchAssetListEntryByExternalReferenceCode(
					externalReferenceCode, _group.getGroupId()));

		Assert.assertEquals(
			"asset-list", portletPreferences.getValue("selectionStyle", null));
	}

	@Test
	public void testAddAssetListFromManualCollection() throws Exception {
		String portletId = LayoutTestUtil.addPortletToLayout(
			_layout, AssetPublisherPortletKeys.ASSET_PUBLISHER,
			Collections.singletonMap(
				"selectionStyle", new String[] {"manual"}));

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.addParameter(
			"title", RandomTestUtil.randomString());
		mockLiferayPortletActionRequest.addParameter(
			"portletResource", portletId);
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		_mvcActionCommand.processAction(
			mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse());

		PortletPreferences portletPreferences =
			PortletPreferencesFactoryUtil.getExistingPortletSetup(
				_layout, portletId);

		String externalReferenceCode = portletPreferences.getValue(
			"assetListEntryExternalReferenceCode", null);

		Assert.assertNotNull(
			_assetListEntryLocalService.
				fetchAssetListEntryByExternalReferenceCode(
					externalReferenceCode, _group.getGroupId()));

		Assert.assertEquals(
			"asset-list", portletPreferences.getValue("selectionStyle", null));
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.fetchCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setLayout(_layout);
		themeDisplay.setLayoutSet(_layout.getLayoutSet());
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	@Inject
	private AssetListEntryLocalService _assetListEntryLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private Layout _layout;

	@Inject(filter = "mvc.command.name=/asset_publisher/add_asset_list")
	private MVCActionCommand _mvcActionCommand;

}