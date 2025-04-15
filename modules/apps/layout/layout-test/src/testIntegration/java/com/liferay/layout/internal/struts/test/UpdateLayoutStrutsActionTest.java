/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.internal.struts.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.portlet.Portlet;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Lourdes Fernández Besada
 */
@RunWith(Arquillian.class)
public class UpdateLayoutStrutsActionTest {

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
	@TestInfo("LPD-49304")
	public void testExecute() throws Exception {
		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest();
		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		LayoutTypePortlet layoutTypePortlet =
			(LayoutTypePortlet)_layout.getLayoutType();

		List<String> portletIds = layoutTypePortlet.getPortletIds();

		int count = portletIds.size();

		for (String portletId : _ALLOWED_PORTLET_IDS) {
			mockHttpServletRequest.setParameter("p_p_id", portletId);

			_updateLayoutStrutsAction.execute(
				mockHttpServletRequest, mockHttpServletResponse);

			count++;

			portletIds = layoutTypePortlet.getPortletIds();

			Assert.assertEquals(
				portletIds.toString(), count, portletIds.size());
		}

		for (String portletId : _FORBIDDEN_PORTLET_IDS) {
			mockHttpServletRequest.setParameter("p_p_id", portletId);

			try {
				_updateLayoutStrutsAction.execute(
					mockHttpServletRequest, mockHttpServletResponse);

				Assert.fail();
			}
			catch (PrincipalException.MustHavePermission principalException) {
				if (_log.isDebugEnabled()) {
					_log.debug(principalException);
				}
			}

			portletIds = layoutTypePortlet.getPortletIds();

			Assert.assertEquals(
				portletIds.toString(), count, portletIds.size());
		}
	}

	@Test
	@TestInfo("LPD-52276")
	public void testExecuteAddPortletWithNestedPortletCategory()
		throws Exception {

		Bundle bundle = FrameworkUtil.getBundle(
			UpdateLayoutStrutsActionTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		String portletId = RandomTestUtil.randomString();

		ServiceRegistration<Portlet> portletServiceRegistration =
			bundleContext.registerService(
				Portlet.class, new MVCPortlet(),
				HashMapDictionaryBuilder.<String, Object>put(
					"com.liferay.portlet.display-category",
					"root//category.sample//category.nested"
				).put(
					"com.liferay.portlet.instanceable", true
				).put(
					"jakarta.portlet.name", portletId
				).build());

		try {
			MockHttpServletRequest mockHttpServletRequest =
				_getMockHttpServletRequest();
			MockHttpServletResponse mockHttpServletResponse =
				new MockHttpServletResponse();

			mockHttpServletRequest.setParameter("p_p_id", portletId);

			LayoutTypePortlet layoutTypePortlet =
				(LayoutTypePortlet)_layout.getLayoutType();

			List<String> portletIds = layoutTypePortlet.getPortletIds();

			int count = portletIds.size();

			_updateLayoutStrutsAction.execute(
				mockHttpServletRequest, mockHttpServletResponse);

			portletIds = layoutTypePortlet.getPortletIds();

			Assert.assertEquals(
				portletIds.toString(), count + 1, portletIds.size());
		}
		finally {
			portletServiceRegistration.unregister();
		}
	}

	private MockHttpServletRequest _getMockHttpServletRequest()
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(WebKeys.LAYOUT, _layout);

		ThemeDisplay themeDisplay = ContentLayoutTestUtil.getThemeDisplay(
			_companyLocalService.getCompany(_group.getCompanyId()), _group,
			_layout);

		themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(TestPropsValues.getUser()));

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockHttpServletRequest.setParameter(Constants.CMD, Constants.ADD);

		return mockHttpServletRequest;
	}

	private static final String[] _ALLOWED_PORTLET_IDS = {
		"com_liferay_asset_publisher_web_portlet_AssetPublisherPortlet",
		"com_liferay_asset_publisher_web_portlet_HighestRatedAssetsPortlet",
		"com_liferay_blogs_web_portlet_BlogsAgreggatorPortlet",
		"com_liferay_blogs_web_portlet_BlogsPortlet"
	};

	private static final String[] _FORBIDDEN_PORTLET_IDS = {
		"com_liferay_blogs_web_portlet_BlogsAdminPortlet",
		"com_liferay_layout_content_page_editor_web_internal_portlet_" +
			"ContentPageEditorPortlet",
		"com_liferay_layout_content_page_editor_web_internal_portlet_" +
			"ContentPageToolbarPortlet",
		"com_liferay_portal_instances_web_portlet_PortalInstancesPortlet"
	};

	private static final Log _log = LogFactoryUtil.getLog(
		UpdateLayoutStrutsActionTest.class);

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Inject
	private JSONFactory _jsonFactory;

	private Layout _layout;

	@Inject
	private PortletLocalService _portletLocalService;

	@Inject
	private PortletPreferencesLocalService _portletPreferencesLocalService;

	@Inject(filter = "path=/portal/update_layout")
	private StrutsAction _updateLayoutStrutsAction;

}