/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.fragment.renderer.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.depot.service.DepotEntryPinLocalService;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.info.constants.InfoDisplayWebKeys;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.security.permission.SimplePermissionChecker;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.cms.site.initializer.test.util.CMSTestUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Adolfo Pérez
 * @author Roberto Díaz
 */
@RunWith(Arquillian.class)
public class BreadcrumbComponentSectionFragmentRendererTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		CMSTestUtil.getOrAddGroup(
			BreadcrumbComponentSectionFragmentRendererTest.class);

		_company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		_cmsAdministratorUser = UserTestUtil.addCompanyUser(
			_company, RoleConstants.CMS_ADMINISTRATOR);

		_depotEntry = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), StringUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), StringUtil.randomString()
			).build(),
			DepotConstants.TYPE_SPACE,
			ServiceContextTestUtil.getServiceContext());
	}

	@Test
	public void testGetProps() throws Exception {
		_testGetProps(
			ActionKeys.DELETE,
			jsonArray -> _assertLabelsEquals(jsonArray, "delete"));
		_testGetProps(
			ActionKeys.PERMISSIONS,
			jsonArray -> _assertLabelsEquals(
				jsonArray, "permissions", "default-permissions",
				"edit-and-propagate-default-permissions"));
		_testGetProps(
			ActionKeys.UPDATE,
			jsonArray -> _assertLabelsEquals(
				jsonArray, "pin-to-product-menu", "space-settings", "export",
				"import"));

		_depotEntryPinLocalService.addDepotEntryPin(
			_cmsAdministratorUser.getUserId(), _depotEntry.getDepotEntryId());

		_testGetProps(
			ActionKeys.UPDATE,
			jsonArray -> _assertLabelsEquals(
				jsonArray, "unpin-from-product-menu", "space-settings",
				"export", "import"));
		_testGetProps(
			ActionKeys.VIEW,
			jsonArray -> _assertLabelsEquals(
				jsonArray, "view-members", "view-connected-sites"));
	}

	private void _assertLabelsEquals(
		JSONArray jsonArray, String... expectedLabels) {

		Assert.assertEquals(expectedLabels.length, jsonArray.length());

		for (int i = 0; i < expectedLabels.length; i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			Assert.assertEquals(
				expectedLabels[i], jsonObject.getString("label"));
		}
	}

	private void _testGetProps(
			String allowedActionId,
			UnsafeConsumer<JSONArray, Exception> unsafeConsumer)
		throws Exception {

		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		httpServletRequest.setAttribute(
			InfoDisplayWebKeys.INFO_ITEM, _depotEntry);

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(_company);
		themeDisplay.setPermissionChecker(
			new SimplePermissionChecker() {

				@Override
				public boolean hasPermission(
					Group group, String name, String primKey, String actionId) {

					return allowedActionId.equals(actionId);
				}

			});
		themeDisplay.setSiteGroupId(_depotEntry.getGroupId());
		themeDisplay.setUser(_cmsAdministratorUser);

		httpServletRequest.setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);

		Map<String, Object> props = ReflectionTestUtil.invoke(
			_fragmentRenderer, "getProps",
			new Class<?>[] {
				FragmentRendererContext.class, HttpServletRequest.class
			},
			null, httpServletRequest);

		unsafeConsumer.accept((JSONArray)props.get("actionItems"));
	}

	private User _cmsAdministratorUser;
	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private DepotEntryPinLocalService _depotEntryPinLocalService;

	@Inject(
		filter = "component.name=com.liferay.site.cms.site.initializer.internal.fragment.renderer.BreadcrumbComponentSectionFragmentRenderer"
	)
	private FragmentRenderer _fragmentRenderer;

	@Inject
	private UserLocalService _userLocalService;

}