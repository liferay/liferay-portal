/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.permission.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.Resource;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.model.impl.PortletImpl;
import com.liferay.portal.model.impl.ResourceImpl;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Manuel de la Peña
 */
@RunWith(Arquillian.class)
public class ResourcePermissionLocalServiceTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testResourceActionsDefaultsWithEmptyDefaultActions()
		throws Exception {

		_resourceActions.populateModelResources(
			ResourcePermissionLocalServiceTest.class.getClassLoader(),
			"com/liferay/portal/security/permission/test/dependencies" +
				"/resource-actions-models.xml");

		String portletNamePrefix = StringUtil.replace(
			ResourcePermissionLocalServiceTest.class.getName(), CharPool.PERIOD,
			CharPool.UNDERLINE);

		String portletName = portletNamePrefix + "_ResourceActionsPortlet";

		Portlet portlet = new PortletImpl(
			TestPropsValues.getCompanyId(), portletName);

		_resourceActions.populatePortletResource(
			portlet, ResourcePermissionLocalServiceTest.class.getClassLoader(),
			"com/liferay/portal/security/permission/test/dependencies" +
				"/resource-actions-portlets.xml");

		_portletLocalService.checkPortlet(portlet);

		_assertResourceActionsDefaults(
			Collections.emptyList(), Collections.emptyList(),
			Collections.emptyList(), _RESOURCE_ACTIONS_MODEL_NAME,
			_RESOURCE_ACTIONS_MODEL_NAME,
			_resourceActions.getModelResourceActions(
				_RESOURCE_ACTIONS_MODEL_NAME));
		_assertResourceActionsDefaults(
			Collections.emptyList(), Collections.emptyList(),
			Collections.emptyList(), portletName, portletName,
			_resourceActions.getPortletResourceActions(portletName));
	}

	@Test
	public void testSetResourcePermissionsWithEmptyRoleIdsToActionIds()
		throws Exception {

		_group = GroupTestUtil.addGroup();

		Role role = _roleLocalService.getRole(
			_group.getCompanyId(), RoleConstants.SITE_MEMBER);

		String primKey = String.valueOf(_group.getGroupId());

		_resourcePermissionLocalService.setResourcePermissions(
			_group.getCompanyId(), Group.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL, primKey, role.getRoleId(),
			new String[] {ActionKeys.VIEW});

		Map<Long, Long> roleIdsToActionIds = _getRoleIdsToActionIds(primKey);

		Assert.assertFalse(
			roleIdsToActionIds.toString(), roleIdsToActionIds.isEmpty());

		_resourcePermissionLocalService.setResourcePermissions(
			_group.getCompanyId(), Group.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL, primKey,
			Collections.emptyMap());

		Assert.assertEquals(
			roleIdsToActionIds, _getRoleIdsToActionIds(primKey));
	}

	@Test
	public void testShouldFailIfFirstResourceIsNotIndividual()
		throws Exception {

		_testResources(
			"The first resource must be an individual scope",
			Arrays.asList(
				_createResource(ResourceConstants.SCOPE_GROUP),
				_createResource(ResourceConstants.SCOPE_COMPANY)));
	}

	@Test
	public void testShouldFailIfLastResourceIsNotCompany() throws Exception {
		_testResources(
			"The last resource must be a company scope",
			Arrays.asList(
				_createResource(ResourceConstants.SCOPE_INDIVIDUAL),
				_createResource(ResourceConstants.SCOPE_GROUP)));
	}

	@Test
	public void testShouldFailIfResourcesIsLessThanTwo() throws Exception {
		_testResources(
			"The list of resources must contain at least two values",
			Arrays.asList(new ResourceImpl()));
	}

	private void _assertResourceActionsDefaults(
			List<String> expectedGuestDefaultActions,
			List<String> expectedOwnerDefaultActions,
			List<String> expectedSiteMemberDefaultActions, String primKey,
			String resourceName, List<String> supportActionIds)
		throws Exception {

		Role guestRole = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(), RoleConstants.GUEST);

		List<String> actualGuestActionIds =
			_resourcePermissionLocalService.
				getAvailableResourcePermissionActionIds(
					TestPropsValues.getCompanyId(), resourceName,
					ResourceConstants.SCOPE_INDIVIDUAL, String.valueOf(primKey),
					guestRole.getRoleId(), supportActionIds);

		Collections.sort(actualGuestActionIds);

		Assert.assertEquals(expectedGuestDefaultActions, actualGuestActionIds);

		Role ownerRole = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(), RoleConstants.OWNER);

		List<String> actualOwnerActionIds =
			_resourcePermissionLocalService.
				getAvailableResourcePermissionActionIds(
					TestPropsValues.getCompanyId(), resourceName,
					ResourceConstants.SCOPE_INDIVIDUAL, String.valueOf(primKey),
					ownerRole.getRoleId(), supportActionIds);

		Collections.sort(actualOwnerActionIds);

		Assert.assertEquals(expectedOwnerDefaultActions, actualOwnerActionIds);

		Role siteMemberRole = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(), RoleConstants.SITE_MEMBER);

		List<String> actualSiteMemberActionIds =
			_resourcePermissionLocalService.
				getAvailableResourcePermissionActionIds(
					TestPropsValues.getCompanyId(), resourceName,
					ResourceConstants.SCOPE_INDIVIDUAL, String.valueOf(primKey),
					siteMemberRole.getRoleId(), supportActionIds);

		Collections.sort(actualSiteMemberActionIds);

		Assert.assertEquals(
			expectedSiteMemberDefaultActions, actualSiteMemberActionIds);
	}

	private Resource _createResource(int scope) {
		Resource resource = new ResourceImpl();

		resource.setScope(scope);

		return resource;
	}

	private Map<Long, Long> _getRoleIdsToActionIds(String primKey) {
		Map<Long, Long> roleIdsToActionIds = new HashMap<>();

		for (ResourcePermission resourcePermission :
				_resourcePermissionLocalService.getResourcePermissions(
					_group.getCompanyId(), Group.class.getName(),
					ResourceConstants.SCOPE_INDIVIDUAL, primKey)) {

			roleIdsToActionIds.put(
				resourcePermission.getRoleId(),
				resourcePermission.getActionIds());
		}

		return roleIdsToActionIds;
	}

	private void _testResources(
			String expectedMessage, List<Resource> resources)
		throws Exception {

		_group = GroupTestUtil.addGroup();

		Role guestRole = _roleLocalService.getRole(
			_group.getCompanyId(), RoleConstants.GUEST);

		try {
			_resourcePermissionLocalService.hasResourcePermission(
				resources, new long[] {guestRole.getRoleId()}, ActionKeys.VIEW);
		}
		catch (IllegalArgumentException illegalArgumentException) {
			Assert.assertEquals(
				expectedMessage, illegalArgumentException.getMessage());
		}
	}

	private static final String _RESOURCE_ACTIONS_MODEL_NAME =
		"com.liferay.portal.security.permission.test.ResourceActions";

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private PortletLocalService _portletLocalService;

	@Inject
	private ResourceActions _resourceActions;

	@Inject
	private ResourceLocalService _resourceLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

}