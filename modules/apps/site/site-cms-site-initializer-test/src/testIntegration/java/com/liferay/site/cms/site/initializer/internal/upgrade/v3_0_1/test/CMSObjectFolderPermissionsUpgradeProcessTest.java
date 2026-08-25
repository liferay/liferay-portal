/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.upgrade.v3_0_1.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.constants.ObjectActionKeys;
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.model.ObjectFolder;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;
import com.liferay.site.cms.site.initializer.util.RoleUtil;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Víctor Galán
 */
@RunWith(Arquillian.class)
public class CMSObjectFolderPermissionsUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_objectFolder =
			_objectFolderLocalService.fetchObjectFolderByExternalReferenceCode(
				ObjectFolderConstants.
					EXTERNAL_REFERENCE_CODE_STRUCTURE_REPEATABLE_GROUPS,
				TestPropsValues.getCompanyId());
		_role = RoleUtil.getOrAddCMSAdministratorRole(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId());
	}

	@Test
	public void testUpgrade() throws Exception {
		_resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(), ObjectFolder.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(_objectFolder.getObjectFolderId()),
			_role.getRoleId(), new String[0]);

		for (String actionId : _ACTION_IDS) {
			Assert.assertFalse(actionId, _hasResourcePermission(actionId));
		}

		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator, _CLASS_NAME);

		upgradeProcess.upgrade();

		for (String actionId : _ACTION_IDS) {
			Assert.assertTrue(actionId, _hasResourcePermission(actionId));
		}
	}

	private boolean _hasResourcePermission(String actionId) throws Exception {
		return _resourcePermissionLocalService.hasResourcePermission(
			TestPropsValues.getCompanyId(), ObjectFolder.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(_objectFolder.getObjectFolderId()),
			_role.getRoleId(), actionId);
	}

	private static final String[] _ACTION_IDS = {
		ObjectActionKeys.ADD_OBJECT_DEFINITION, ActionKeys.DELETE,
		ActionKeys.PERMISSIONS, ActionKeys.UPDATE, ActionKeys.VIEW
	};

	private static final String _CLASS_NAME =
		"com.liferay.site.cms.site.initializer.internal.upgrade.v3_0_1." +
			"CMSObjectFolderPermissionsUpgradeProcess";

	private ObjectFolder _objectFolder;

	@Inject
	private ObjectFolderLocalService _objectFolderLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	private Role _role;

	@Inject(
		filter = "component.name=com.liferay.site.cms.site.initializer.internal.upgrade.registry.SiteCMSSiteInitializerUpgradeStepRegistrator"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}