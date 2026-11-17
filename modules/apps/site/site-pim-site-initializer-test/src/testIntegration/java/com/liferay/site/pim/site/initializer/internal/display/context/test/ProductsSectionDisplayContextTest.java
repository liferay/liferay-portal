/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.display.context.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.model.DepotEntry;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.cms.site.initializer.contributor.CMSStructureObjectFolderContributor;
import com.liferay.site.pim.site.initializer.test.util.PIMTestUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.lang.reflect.Constructor;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Stefano Motta
 */
@FeatureFlags(featureFlags = @FeatureFlag("LPD-96666"))
@RunWith(Arquillian.class)
public class ProductsSectionDisplayContextTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_depotEntry = PIMTestUtil.addSpaceDepotEntry();

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay(mockHttpServletRequest));

		Class<?> cmsStructureObjectFolderContributorClass =
			_cmsStructureObjectFolderContributor.getClass();

		ClassLoader classLoader =
			cmsStructureObjectFolderContributorClass.getClassLoader();

		Class<?> displayContextClass = classLoader.loadClass(
			"com.liferay.site.pim.site.initializer.internal.display.context." +
				"ProductsSectionDisplayContext");

		Constructor<?> constructor = displayContextClass.getConstructor(
			HttpServletRequest.class);

		_displayContext = constructor.newInstance(mockHttpServletRequest);
	}

	@Test
	public void testGetCreationMenu() throws Exception {
		CreationMenu creationMenu = ReflectionTestUtil.invoke(
			_displayContext, "getCreationMenu", new Class<?>[0]);

		List<DropdownItem> dropdownItems = (List<DropdownItem>)creationMenu.get(
			"primaryItems");

		Assert.assertTrue(dropdownItems.size() > 1);

		DropdownItem dropdownItem = dropdownItems.get(0);

		Map<String, Object> data = (Map<String, Object>)dropdownItem.get(
			"data");

		Assert.assertEquals("createFolder", data.get("action"));
		Assert.assertEquals(
			ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_PRODUCTS,
			data.get("parentObjectEntryFolderExternalReferenceCode"));

		List<Long> groupIds = JSONUtil.toLongList(
			(JSONArray)data.get("assetLibraries"), "groupId");

		Assert.assertTrue(groupIds.contains(_depotEntry.getGroupId()));

		for (DropdownItem productTypeDropdownItem :
				dropdownItems.subList(1, dropdownItems.size())) {

			data = (Map<String, Object>)productTypeDropdownItem.get("data");

			Assert.assertEquals("createAsset", data.get("action"));
		}
	}

	@Test
	public void testGetFDSActionDropdownItems() {
		List<FDSActionDropdownItem> fdsActionDropdownItems =
			ReflectionTestUtil.invoke(
				_displayContext, "getFDSActionDropdownItems", new Class<?>[0]);

		Assert.assertEquals(
			List.of(
				"actionLinkFolder", "editFolder", "edit", "permissions",
				"delete"),
			TransformUtil.transform(
				fdsActionDropdownItems,
				fdsActionDropdownItem -> {
					Map<String, Object> data =
						(Map<String, Object>)fdsActionDropdownItem.get("data");

					return data.get("id");
				}));
	}

	private ThemeDisplay _getThemeDisplay(
			MockHttpServletRequest mockHttpServletRequest)
		throws Exception {

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));

		Group group = PIMTestUtil.getOrAddGroup();

		Layout layout = LayoutTestUtil.addTypeContentLayout(group);

		themeDisplay.setLayout(layout);
		themeDisplay.setLayoutSet(layout.getLayoutSet());

		themeDisplay.setLocale(LocaleUtil.getDefault());
		themeDisplay.setPathMain(_portal.getPathMain());
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setPortalURL(
			"http://localhost:" + PortalUtil.getPortalServerPort(false));
		themeDisplay.setRealUser(TestPropsValues.getUser());
		themeDisplay.setRequest(mockHttpServletRequest);
		themeDisplay.setScopeGroupId(group.getGroupId());
		themeDisplay.setSiteGroupId(group.getGroupId());
		themeDisplay.setURLCurrent(
			"http://localhost:" + PortalUtil.getPortalServerPort(false) +
				"/currentURL");
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	@Inject(
		filter = "component.name=com.liferay.site.pim.site.initializer.internal.contributor.ProductTypesCMSStructureObjectFolderContributor"
	)
	private CMSStructureObjectFolderContributor
		_cmsStructureObjectFolderContributor;

	@Inject
	private CompanyLocalService _companyLocalService;

	private DepotEntry _depotEntry;
	private Object _displayContext;

	@Inject
	private Portal _portal;

}