/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.cache.CacheRegistryUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PortletConstants;
import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.BasePortletIdUpgradeProcess;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.sql.Connection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Raymond Augé
 */
@RunWith(Arquillian.class)
public class BaseUpgradePortletIdTest extends BasePortletIdUpgradeProcess {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws PortalException {
		for (String portletId : _PORTLET_IDS) {
			Portlet portlet = _portletLocalService.getPortletById(
				TestPropsValues.getCompanyId(), portletId);

			_portlets.add(portlet);

			_resourceActions.check(portletId);

			_portletLocalService.destroyPortlet(portlet);
		}
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		for (Portlet portlet : _portlets) {
			List<ResourceAction> portletResourceActions =
				_resourceActionLocalService.getResourceActions(
					portlet.getPortletName());

			for (ResourceAction portletResourceAction :
					portletResourceActions) {

				_resourceActionLocalService.deleteResourceAction(
					portletResourceAction);
			}

			if (portlet.isUndeployedPortlet()) {
				continue;
			}

			_portletLocalService.deployPortlet(portlet);
		}

		_portlets.clear();
	}

	@After
	public void tearDown() throws Exception {
		try (Connection connection = DataAccess.getConnection()) {
			this.connection = connection;

			String[][] renamePortletIdsArray = getRenamePortletIdsArray();

			for (String[] renamePortletIds : renamePortletIdsArray) {
				String oldRootPortletId = renamePortletIds[1];
				String newRootPortletId = renamePortletIds[0];

				updatePortlet(oldRootPortletId, newRootPortletId);
				updateLayoutRevisions(
					oldRootPortletId, newRootPortletId, false);
				updateLayouts(oldRootPortletId, newRootPortletId, false);
			}
		}
		finally {
			this.connection = null;
		}

		for (String portletId : _PORTLET_IDS) {
			runSQL(
				"delete from Portlet where portletId = '" + portletId +
					"_test'");

			runSQL(
				"delete from ResourceAction where name = '" + portletId +
					"_test'");

			runSQL(
				"delete from ResourcePermission where name = '" + portletId +
					"_test'");
		}
	}

	@Test
	public void testUpgradePortletId() throws Exception {
		doTestUpgrade();
	}

	@Test
	public void testUpgradeUninstanceablePortletId() throws Exception {
		_testInstanceable = false;

		try {
			doTestUpgrade();
		}
		finally {
			_testInstanceable = true;
		}
	}

	protected Layout addLayout() throws Exception {
		Group group = GroupTestUtil.addGroup();

		return LayoutTestUtil.addTypePortletLayout(group, false);
	}

	protected void addPortletPreferences(Layout layout, String portletId)
		throws Exception {

		_portletPreferencesLocalService.getPreferences(
			TestPropsValues.getCompanyId(), 0,
			PortletKeys.PREFS_OWNER_TYPE_LAYOUT, layout.getPlid(), portletId,
			PortletConstants.DEFAULT_PREFERENCES);
	}

	protected void doTestUpgrade() throws Exception {
		Layout layout = addLayout();

		LayoutTypePortlet layoutTypePortlet =
			(LayoutTypePortlet)layout.getLayoutType();

		Role role = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(), RoleConstants.USER);

		Map<Long, String[]> roleIdsToActionIds = HashMapBuilder.put(
			role.getRoleId(), new String[] {ActionKeys.CONFIGURATION}
		).build();

		Portlet portlet = null;
		String[][] renamePortletIdsArray = new String[_PORTLET_IDS.length][2];

		for (int i = 0; i < _PORTLET_IDS.length; i++) {
			String oldPortletId = _PORTLET_IDS[i];

			String portletId = getPortletId(oldPortletId);

			renamePortletIdsArray[i][0] = portletId;

			portlet = _portletLocalService.getPortletById(
				TestPropsValues.getCompanyId(), portletId);

			layoutTypePortlet.addPortletId(
				TestPropsValues.getUserId(), portletId, false);

			addPortletPreferences(layout, portletId);

			String portletPrimaryKey = PortletPermissionUtil.getPrimaryKey(
				layout.getPlid(), portletId);

			_resourcePermissionLocalService.setResourcePermissions(
				TestPropsValues.getCompanyId(), oldPortletId,
				ResourceConstants.SCOPE_INDIVIDUAL, portletPrimaryKey,
				roleIdsToActionIds);

			_portletLocalService.destroyPortlet(portlet);
		}

		_layoutLocalService.updateTypeSettings(
			layout.getGroupId(), layout.isPrivateLayout(), layout.getLayoutId(),
			layout.getTypeSettings());

		upgrade();

		CacheRegistryUtil.clear();

		layout = _layoutLocalService.getLayout(layout.getPlid());

		layoutTypePortlet = (LayoutTypePortlet)layout.getLayoutType();

		if (_testInstanceable) {
			String[][] renameRootPortletIdsArray = getRenamePortletIdsArray();

			for (int i = 0; i < renameRootPortletIdsArray.length; i++) {
				renamePortletIdsArray[i][1] = StringUtil.replace(
					renamePortletIdsArray[i][0],
					renameRootPortletIdsArray[i][0],
					renameRootPortletIdsArray[i][1]);
			}
		}
		else {
			for (int i = 0; i < _PORTLET_IDS.length; i++) {
				renamePortletIdsArray[i][1] = getNewPortletId(
					layoutTypePortlet, _PORTLET_IDS[i]);
			}
		}

		for (String[] renamePortletIds : renamePortletIdsArray) {
			String oldPortletId = renamePortletIds[0];

			String newPortletId = renamePortletIds[1];

			String newRootPortletId = PortletIdCodec.decodePortletName(
				newPortletId);

			portlet.setCompanyId(TestPropsValues.getCompanyId());
			portlet.setPortletId(newPortletId);

			List<String> portletActions =
				_resourceActions.getPortletResourceActions(newRootPortletId);

			_resourceActionLocalService.checkResourceActions(
				newRootPortletId, portletActions);

			_portletLocalService.checkPortlet(portlet);

			List<String> portletIds = layoutTypePortlet.getPortletIds();

			Assert.assertFalse(
				StringBundler.concat(
					oldPortletId, " still exists on page ", layout.getPlid(),
					": ", StringUtil.merge(layoutTypePortlet.getPortletIds())),
				portletIds.contains(oldPortletId));

			Assert.assertTrue(
				StringBundler.concat(
					newPortletId, " does not exist on page ", layout.getPlid(),
					": ", StringUtil.merge(layoutTypePortlet.getPortletIds())),
				portletIds.contains(newPortletId));

			String oldPortletPrimaryKey = PortletPermissionUtil.getPrimaryKey(
				layout.getPlid(), oldPortletId);
			String newPortletPrimaryKey = PortletPermissionUtil.getPrimaryKey(
				layout.getPlid(), newPortletId);

			ResourcePermission resourcePermission =
				_resourcePermissionLocalService.fetchResourcePermission(
					TestPropsValues.getCompanyId(), oldPortletId,
					ResourceConstants.SCOPE_INDIVIDUAL, oldPortletPrimaryKey,
					role.getRoleId());

			Assert.assertNull(
				StringBundler.concat(
					oldPortletId, " still has a resource permission on page ",
					layout.getPlid(), " via primKey ", oldPortletPrimaryKey),
				resourcePermission);

			Assert.assertNotNull(
				StringBundler.concat(
					newPortletId, " does not have a resource permission on ",
					"page ", layout.getPlid(), " via primKey ",
					newPortletPrimaryKey),
				_resourcePermissionLocalService.fetchResourcePermission(
					TestPropsValues.getCompanyId(), newRootPortletId,
					ResourceConstants.SCOPE_INDIVIDUAL, newPortletPrimaryKey,
					role.getRoleId()));

			boolean hasViewPermission =
				_resourcePermissionLocalService.hasResourcePermission(
					TestPropsValues.getCompanyId(), newRootPortletId,
					ResourceConstants.SCOPE_INDIVIDUAL, newPortletPrimaryKey,
					role.getRoleId(), ActionKeys.VIEW);

			Assert.assertFalse(hasViewPermission);

			boolean hasConfigurationPermission =
				_resourcePermissionLocalService.hasResourcePermission(
					TestPropsValues.getCompanyId(), newRootPortletId,
					ResourceConstants.SCOPE_INDIVIDUAL, newPortletPrimaryKey,
					role.getRoleId(), ActionKeys.CONFIGURATION);

			Assert.assertTrue(hasConfigurationPermission);
		}

		_groupLocalService.deleteGroup(layout.getGroup());
	}

	protected String getNewPortletId(
		LayoutTypePortlet layoutTypePortlet, String oldPortletId) {

		List<String> portletIds = layoutTypePortlet.getPortletIds();

		for (String portletId : portletIds) {
			if (portletId.startsWith(oldPortletId)) {
				return portletId;
			}
		}

		return oldPortletId;
	}

	protected String getPortletId(String portletId) {
		if (_testInstanceable) {
			return portletId + _INSTANCE_ID;
		}

		return portletId;
	}

	@Override
	protected String[][] getRenamePortletIdsArray() {
		if (_testInstanceable) {
			return new String[][] {
				{_PORTLET_IDS[0], _PORTLET_IDS[0] + "_test"},
				{_PORTLET_IDS[1], _PORTLET_IDS[1] + "_test"}
			};
		}

		return new String[0][0];
	}

	@Override
	protected String[] getUninstanceablePortletIds() {
		if (!_testInstanceable) {
			return _PORTLET_IDS;
		}

		return new String[0];
	}

	private static final String _INSTANCE_ID = "_INSTANCE_LhZwzy867qfr";

	private static final String[] _PORTLET_IDS = {
		"47", "com_liferay_test_portlet_TestPortlet"
	};

	@Inject
	private static PortletLocalService _portletLocalService;

	private static final List<Portlet> _portlets = new ArrayList<>();

	@Inject
	private static ResourceActionLocalService _resourceActionLocalService;

	@Inject
	private static ResourceActions _resourceActions;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private PortletPreferencesLocalService _portletPreferencesLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	private boolean _testInstanceable = true;

}