/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.publisher.frontend.taglib.form.navigator.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.publisher.constants.AssetPublisherConstants;
import com.liferay.asset.publisher.constants.AssetPublisherPortletKeys;
import com.liferay.frontend.taglib.form.navigator.FormNavigatorCategory;
import com.liferay.frontend.taglib.form.navigator.FormNavigatorCategoryProvider;
import com.liferay.frontend.taglib.form.navigator.FormNavigatorEntry;
import com.liferay.frontend.taglib.form.navigator.FormNavigatorEntryProvider;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Eudaldo Alonso
 */
@RunWith(Arquillian.class)
public class AssetPublisherConfigurationFormNavigatorTest {

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

		_portletId = LayoutTestUtil.addPortletToLayout(
			_layout, AssetPublisherPortletKeys.ASSET_PUBLISHER,
			Collections.singletonMap(
				"selectionStyle", new String[] {"dynamic"}));

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		serviceContext.setRequest(_getHttpServletRequest());

		ServiceContextThreadLocal.pushServiceContext(serviceContext);
	}

	@After
	public void tearDown() {
		ServiceContextThreadLocal.popServiceContext();
	}

	@FeatureFlags(enable = false, value = "LPD-13311")
	@Test
	public void testViewOrderOfFieldsetsInAssetSelectionWhenSelectionStyleIsDynamic1()
		throws PortalException {

		_assertFormNavigatorEntryKeys(
			new String[] {
				"asset-selection", "scope", "source", "filter", "ordering",
				"create-asset-list"
			});
	}

	@FeatureFlags("LPD-13311")
	@Test
	public void testViewOrderOfFieldsetsInAssetSelectionWhenSelectionStyleIsDynamic2()
		throws PortalException {

		_assertFormNavigatorEntryKeys(
			new String[] {
				"asset-selection", "scope", "source", "filter",
				"custom-user-attributes", "ordering", "create-asset-list"
			});
	}

	private void _assertFormNavigatorEntryKeys(String[] formNavigatorEntryKeys)
		throws PortalException {

		List<FormNavigatorCategory> formNavigatorCategories =
			_formNavigatorCategoryProvider.getFormNavigatorCategories(
				AssetPublisherConstants.FORM_NAVIGATOR_ID_CONFIGURATION);

		FormNavigatorCategory formNavigatorCategory =
			formNavigatorCategories.get(0);

		List<FormNavigatorEntry<Object>> formNavigatorEntries =
			_formNavigatorEntryProvider.getFormNavigatorEntries(
				AssetPublisherConstants.FORM_NAVIGATOR_ID_CONFIGURATION,
				formNavigatorCategory.getKey(), TestPropsValues.getUser(),
				null);

		for (int i = 0; i < formNavigatorEntries.size(); i++) {
			FormNavigatorEntry<Object> formNavigatorEntry =
				formNavigatorEntries.get(i);

			Assert.assertEquals(
				formNavigatorEntryKeys[i], formNavigatorEntry.getKey());
		}
	}

	private HttpServletRequest _getHttpServletRequest() throws Exception {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setLayout(_layout);
		themeDisplay.setRealUser(TestPropsValues.getUser());
		themeDisplay.setRequest(mockHttpServletRequest);
		themeDisplay.setResponse(new MockHttpServletResponse());
		themeDisplay.setUser(TestPropsValues.getUser());

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		portletDisplay.setPortletResource(_portletId);

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		return mockHttpServletRequest;
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private FormNavigatorCategoryProvider _formNavigatorCategoryProvider;

	@Inject
	private FormNavigatorEntryProvider _formNavigatorEntryProvider;

	@DeleteAfterTestRun
	private Group _group;

	private Layout _layout;
	private String _portletId;

}