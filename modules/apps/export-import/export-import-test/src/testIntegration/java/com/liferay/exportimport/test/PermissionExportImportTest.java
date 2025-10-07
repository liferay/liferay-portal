/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.lar.PermissionImporter;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.model.Resource;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.LayoutSetPrototypeLocalServiceUtil;
import com.liferay.portal.kernel.service.ResourceLocalServiceUtil;
import com.liferay.portal.kernel.service.ResourcePermissionServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.ResourcePermissionUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Máté Thurzó
 */
@RunWith(Arquillian.class)
public class PermissionExportImportTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		UserTestUtil.setUser(TestPropsValues.getUser());
	}

	@Test
	public void testCustomRoleWithEmptyPermissionsNotExported()
		throws Exception {

		// Export

		LayoutSetPrototype exportLayoutSetPrototype =
			LayoutTestUtil.addLayoutSetPrototype(RandomTestUtil.randomString());

		Group exportGroup = exportLayoutSetPrototype.getGroup();

		Layout exportLayout = LayoutTestUtil.addTypePortletLayout(
			exportGroup, true);

		String exportResourcePrimKey = PortletPermissionUtil.getPrimaryKey(
			exportLayout.getPlid(), _PORTLET_ID);

		Role roleWithPermissions = RoleTestUtil.addRole(
			"roleWithPermissions", RoleConstants.TYPE_REGULAR);

		addPortletPermissions(
			exportGroup, roleWithPermissions, exportResourcePrimKey);

		Role roleWithoutPermissions = RoleTestUtil.addRole(
			"roleWithoutPermissions", RoleConstants.TYPE_REGULAR);

		// Add a resource permission with no action IDs. First, add a resource
		// permission with default action IDs since resource permissions cannot
		// be created with no action IDs. Then, set the resource permission to
		// have no action IDs.

		addPortletPermissions(
			exportGroup, roleWithoutPermissions, exportResourcePrimKey);

		addPortletPermissions(
			exportGroup, roleWithoutPermissions, exportResourcePrimKey,
			new String[0]);

		Element portletElement = exportPortletPermissions(
			exportGroup, exportLayout);

		RoleLocalServiceUtil.deleteRole(roleWithPermissions);

		RoleLocalServiceUtil.deleteRole(roleWithoutPermissions);

		// Import

		LayoutSetPrototype importLayoutSetPrototype =
			LayoutTestUtil.addLayoutSetPrototype(RandomTestUtil.randomString());

		Group importGroup = importLayoutSetPrototype.getGroup();

		Layout importLayout = LayoutTestUtil.addTypePortletLayout(
			importGroup, true);

		importPortletPermissions(importGroup, importLayout, portletElement);

		Assert.assertNotNull(
			RoleLocalServiceUtil.fetchRole(
				importGroup.getCompanyId(), "roleWithPermissions"));

		Assert.assertNull(
			RoleLocalServiceUtil.fetchRole(
				importGroup.getCompanyId(), "roleWithoutPermissions"));

		LayoutSetPrototypeLocalServiceUtil.deleteLayoutSetPrototype(
			exportLayoutSetPrototype);
		LayoutSetPrototypeLocalServiceUtil.deleteLayoutSetPrototype(
			importLayoutSetPrototype);
	}

	@Test
	public void testPortletGuestPermissionsExportImportToGroup()
		throws Exception {

		LayoutSetPrototype exportLayoutSetPrototype =
			LayoutTestUtil.addLayoutSetPrototype(RandomTestUtil.randomString());
		Group importGroup = GroupTestUtil.addGroup();

		try {
			_testPortletGuestPermissionsExportImport(
				exportLayoutSetPrototype.getGroup(), importGroup);
		}
		finally {
			LayoutSetPrototypeLocalServiceUtil.deleteLayoutSetPrototype(
				exportLayoutSetPrototype);
			GroupTestUtil.deleteGroup(importGroup);
		}
	}

	@Test
	public void testPortletGuestPermissionsExportImportToLayoutSetPrototype()
		throws Exception {

		LayoutSetPrototype exportLayoutSetPrototype =
			LayoutTestUtil.addLayoutSetPrototype(RandomTestUtil.randomString());
		LayoutSetPrototype importLayoutSetPrototype =
			LayoutTestUtil.addLayoutSetPrototype(RandomTestUtil.randomString());

		try {
			_testPortletGuestPermissionsExportImport(
				exportLayoutSetPrototype.getGroup(),
				importLayoutSetPrototype.getGroup());
		}
		finally {
			LayoutSetPrototypeLocalServiceUtil.deleteLayoutSetPrototype(
				exportLayoutSetPrototype);
			LayoutSetPrototypeLocalServiceUtil.deleteLayoutSetPrototype(
				importLayoutSetPrototype);
		}
	}

	protected void addPortletPermissions(
			Group exportGroup, Role role, String exportResourcePrimKey)
		throws Exception {

		addPortletPermissions(
			exportGroup, role, exportResourcePrimKey, _ACTION_IDS);
	}

	protected void addPortletPermissions(
			Group exportGroup, Role role, String exportResourcePrimKey,
			String[] actionIds)
		throws Exception {

		ResourcePermissionServiceUtil.setIndividualResourcePermissions(
			exportGroup.getGroupId(), TestPropsValues.getCompanyId(),
			_PORTLET_ID, exportResourcePrimKey,
			HashMapBuilder.put(
				role.getRoleId(), actionIds
			).build());
	}

	protected Element exportPortletPermissions(
			final Group exportGroup, Layout exportLayout)
		throws Exception {

		final Method getCompanyIdMethod = ReflectionTestUtil.getMethod(
			PortletDataContext.class, "getCompanyId");
		final Method getGroupIdMethod = ReflectionTestUtil.getMethod(
			PortletDataContext.class, "getGroupId");

		PortletDataContext portletDataContext =
			(PortletDataContext)ProxyUtil.newProxyInstance(
				PortletDataContext.class.getClassLoader(),
				new Class<?>[] {PortletDataContext.class},
				new InvocationHandler() {

					@Override
					public Object invoke(
							Object proxy, Method method, Object[] args)
						throws PortalException {

						if (method.equals(getCompanyIdMethod)) {
							return TestPropsValues.getCompanyId();
						}

						if (method.equals(getGroupIdMethod)) {
							return exportGroup.getGroupId();
						}

						throw new UnsupportedOperationException();
					}

				});

		Element portletElement = SAXReaderUtil.createElement("portlet");

		Class<?> clazz = _permissionImporter.getClass();

		ClassLoader classLoader = clazz.getClassLoader();

		clazz = classLoader.loadClass(
			"com.liferay.exportimport.internal.lar.PermissionExporter");

		Field field = clazz.getDeclaredField("_permissionExporter");

		field.setAccessible(true);

		ReflectionTestUtil.invoke(
			field.get(null), "exportPortletPermissions",
			new Class<?>[] {
				PortletDataContext.class, String.class, Layout.class,
				Element.class
			},
			portletDataContext, _PORTLET_ID, exportLayout, portletElement);

		return portletElement;
	}

	protected void importPortletPermissions(
			Group importGroup, Layout importLayout, Element portletElement)
		throws Exception {

		ReflectionTestUtil.invoke(
			_permissionImporter, "importPortletPermissions",
			new Class<?>[] {
				long.class, long.class, long.class, Layout.class, Element.class,
				String.class
			},
			TestPropsValues.getCompanyId(), importGroup.getGroupId(),
			TestPropsValues.getUserId(), importLayout, portletElement,
			_PORTLET_ID);
	}

	protected void validateImportedPortletPermissions(
			Group importGroup, Role role, String importResourcePrimKey)
		throws Exception {

		List<String> resourceActions = ResourceActionsUtil.getResourceActions(
			_PORTLET_ID, null);

		Resource resource = ResourceLocalServiceUtil.getResource(
			TestPropsValues.getCompanyId(), _PORTLET_ID,
			ResourceConstants.SCOPE_INDIVIDUAL, importResourcePrimKey);

		List<String> currentIndividualActions = new ArrayList<>();

		ResourcePermissionUtil.populateResourcePermissionActionIds(
			importGroup.getGroupId(), role, resource, resourceActions,
			currentIndividualActions, new ArrayList<String>(),
			new ArrayList<String>(), new ArrayList<String>());

		Assert.assertEquals(
			currentIndividualActions.toString(), _ACTION_IDS.length,
			currentIndividualActions.size());

		for (String action : currentIndividualActions) {
			boolean foundActionId = false;

			for (String actionId : _ACTION_IDS) {
				if (action.equals(actionId)) {
					foundActionId = true;

					break;
				}
			}

			Assert.assertTrue("Unable to import permissions", foundActionId);
		}
	}

	private void _testPortletGuestPermissionsExportImport(
			Group exportGroup, Group importGroup)
		throws Exception {

		// Export

		Layout exportLayout = LayoutTestUtil.addTypePortletLayout(
			exportGroup, true);

		String exportResourcePrimKey = PortletPermissionUtil.getPrimaryKey(
			exportLayout.getPlid(), _PORTLET_ID);

		Role role = RoleLocalServiceUtil.getRole(
			TestPropsValues.getCompanyId(), RoleConstants.GUEST);

		addPortletPermissions(exportGroup, role, exportResourcePrimKey);

		Element portletElement = exportPortletPermissions(
			exportGroup, exportLayout);

		// Import

		Layout importLayout = LayoutTestUtil.addTypePortletLayout(
			importGroup, true);

		String importResourcePrimKey = PortletPermissionUtil.getPrimaryKey(
			importLayout.getPlid(), _PORTLET_ID);

		importPortletPermissions(importGroup, importLayout, portletElement);

		validateImportedPortletPermissions(
			importGroup, role, importResourcePrimKey);
	}

	private static final String[] _ACTION_IDS = {
		ActionKeys.ADD_TO_PAGE, ActionKeys.VIEW
	};

	private static final String _PORTLET_ID = PortletKeys.EXPORT_IMPORT;

	@Inject
	private PermissionImporter _permissionImporter;

}