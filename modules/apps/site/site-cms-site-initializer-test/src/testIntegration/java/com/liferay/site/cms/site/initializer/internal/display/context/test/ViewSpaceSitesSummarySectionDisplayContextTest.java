/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.info.constants.InfoDisplayWebKeys;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
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

import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Alicia García
 */
@RunWith(Arquillian.class)
public class ViewSpaceSitesSummarySectionDisplayContextTest
	extends BaseDisplayContextTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_depotEntry = _addDepotEntry();

		Role role = _roleLocalService.getRole(
			_depotEntry.getCompanyId(),
			DepotRolesConstants.ASSET_LIBRARY_MEMBER);

		_originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		_user = UserTestUtil.addUser();

		_userGroupRoleLocalService.addUserGroupRoles(
			_user.getUserId(), _depotEntry.getGroupId(),
			new long[] {role.getRoleId()});
	}

	@After
	public void tearDown() throws Exception {
		PermissionThreadLocal.setPermissionChecker(_originalPermissionChecker);
	}

	@Test
	public void testGetCreationMenu() throws Exception {
		CreationMenu creationMenu = ReflectionTestUtil.invoke(
			_getViewSpaceSitesSummarySectionDisplayContext(
				_getHttpServletRequest(_depotEntry, TestPropsValues.getUser())),
			"getCreationMenu", new Class<?>[0]);

		Assert.assertFalse(creationMenu.isEmpty());

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_user));

		creationMenu = ReflectionTestUtil.invoke(
			_getViewSpaceSitesSummarySectionDisplayContext(
				_getHttpServletRequest(_depotEntry, _user)),
			"getCreationMenu", new Class<?>[0]);

		Assert.assertTrue(creationMenu.isEmpty());
	}

	@Test
	public void testGetFDSActionDropdownItems() throws Exception {
		List<FDSActionDropdownItem> fdsActionDropdownItems =
			ReflectionTestUtil.invoke(
				_getViewSpaceSitesSummarySectionDisplayContext(
					_getHttpServletRequest(
						_depotEntry, TestPropsValues.getUser())),
				"getFDSActionDropdownItems", new Class<?>[0]);

		Assert.assertFalse(fdsActionDropdownItems.isEmpty());

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_user));

		fdsActionDropdownItems = ReflectionTestUtil.invoke(
			_getViewSpaceSitesSummarySectionDisplayContext(
				_getHttpServletRequest(_depotEntry, _user)),
			"getFDSActionDropdownItems", new Class<?>[0]);

		Assert.assertTrue(fdsActionDropdownItems.isEmpty());
	}

	private DepotEntry _addDepotEntry() throws Exception {
		return _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			Collections.emptyMap(), DepotConstants.TYPE_SPACE,
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId(), TestPropsValues.getUserId()));
	}

	private HttpServletRequest _getHttpServletRequest(
			DepotEntry depotEntry, User user)
		throws Exception {

		HttpServletRequest httpServletRequest = getMockHttpServletRequest(user);

		httpServletRequest.setAttribute(
			InfoDisplayWebKeys.INFO_ITEM, depotEntry);

		return httpServletRequest;
	}

	private Object _getViewSpaceSitesSummarySectionDisplayContext(
			HttpServletRequest httpServletRequest)
		throws Exception {

		_fragmentRenderer.render(
			fragmentRendererContext, httpServletRequest,
			new MockHttpServletResponse());

		Object viewSpaceSitesSummarySectionDisplayContext =
			httpServletRequest.getAttribute(
				"com.liferay.site.cms.site.initializer.internal.display." +
					"context.ViewSpaceSitesSummarySectionDisplayContext");

		Assert.assertNotNull(viewSpaceSitesSummarySectionDisplayContext);

		return viewSpaceSitesSummarySectionDisplayContext;
	}

	@DeleteAfterTestRun
	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject(
		filter = "component.name=com.liferay.site.cms.site.initializer.internal.fragment.renderer.ViewSpaceSitesSummaryJSPSectionFragmentRenderer"
	)
	private FragmentRenderer _fragmentRenderer;

	private PermissionChecker _originalPermissionChecker;

	@Inject
	private RoleLocalService _roleLocalService;

	@DeleteAfterTestRun
	private User _user;

	@Inject
	private UserGroupRoleLocalService _userGroupRoleLocalService;

}