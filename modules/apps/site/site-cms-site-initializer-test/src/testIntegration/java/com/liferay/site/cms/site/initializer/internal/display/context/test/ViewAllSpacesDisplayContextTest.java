/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Alicia García
 */
@RunWith(Arquillian.class)
public class ViewAllSpacesDisplayContextTest
	extends BaseDisplayContextTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testGetAdditionalProps() throws Exception {
		Role role1 = _roleLocalService.addRole(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(), null, 0,
			RandomTestUtil.randomString(), null, null, RoleConstants.TYPE_DEPOT,
			null, null);
		Role role2 = _roleLocalService.addRole(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(), null, 0,
			RandomTestUtil.randomString(), null, null, RoleConstants.TYPE_DEPOT,
			DepotRolesConstants.SUBTYPE_PROJECT, null);
		Role role3 = _roleLocalService.addRole(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(), null, 0,
			RandomTestUtil.randomString(), null, null, RoleConstants.TYPE_DEPOT,
			DepotRolesConstants.SUBTYPE_SPACE, null);

		Map<String, Object> additionalProps = ReflectionTestUtil.invoke(
			_getViewAllSpacesDisplayContext(getMockHttpServletRequest()),
			"getAdditionalProps", new Class<?>[0]);

		Map<String, Object> defaultPermissionAdditionalProps =
			(Map<String, Object>)additionalProps.get(
				"defaultPermissionAdditionalProps");

		List<Map<String, Object>> roles = ListUtil.fromArray(
			(Map<String, Object>[])defaultPermissionAdditionalProps.get(
				"roles"));

		Assert.assertTrue(
			ListUtil.exists(
				roles,
				role -> Objects.equals(
					role1.getName(), MapUtil.getString(role, "key"))));
		Assert.assertFalse(
			ListUtil.exists(
				roles,
				role -> Objects.equals(
					role2.getName(), MapUtil.getString(role, "key"))));
		Assert.assertTrue(
			ListUtil.exists(
				roles,
				role -> Objects.equals(
					role3.getName(), MapUtil.getString(role, "key"))));
	}

	@Test
	public void testGetBreadcrumbProps() throws Exception {
		HttpServletRequest httpServletRequest = getMockHttpServletRequest();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		themeDisplay.setLayout(
			LayoutTestUtil.addTypeContentLayout(group, "test-name"));

		AssertUtils.assertEquals(
			HashMapBuilder.<String, Object>put(
				"breadcrumbItems",
				JSONUtil.putAll(
					JSONUtil.put(
						"active", false
					).put(
						"href", (String)null
					).put(
						"label", "test-name"
					))
			).put(
				"hideSpace", true
			).build(),
			_getBreadcrumbProps(httpServletRequest));
	}

	private Map<String, Object> _getBreadcrumbProps(
			HttpServletRequest httpServletRequest)
		throws Exception {

		return ReflectionTestUtil.invoke(
			_getViewAllSpacesDisplayContext(httpServletRequest),
			"getBreadcrumbProps", new Class<?>[0]);
	}

	private Object _getViewAllSpacesDisplayContext(
			HttpServletRequest httpServletRequest)
		throws Exception {

		_fragmentRenderer.render(
			fragmentRendererContext, httpServletRequest,
			new MockHttpServletResponse());

		Object viewAllSpacesDisplayContext = httpServletRequest.getAttribute(
			"com.liferay.site.cms.site.initializer.internal.display.context." +
				"ViewAllSpacesDisplayContext");

		Assert.assertNotNull(viewAllSpacesDisplayContext);

		return viewAllSpacesDisplayContext;
	}

	@Inject(
		filter = "component.name=com.liferay.site.cms.site.initializer.internal.fragment.renderer.ViewAllSpacesJSPSectionFragmentRenderer"
	)
	private FragmentRenderer _fragmentRenderer;

	@Inject
	private RoleLocalService _roleLocalService;

}