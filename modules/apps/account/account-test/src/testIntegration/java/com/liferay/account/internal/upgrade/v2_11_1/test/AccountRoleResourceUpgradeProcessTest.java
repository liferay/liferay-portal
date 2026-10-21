/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.internal.upgrade.v2_11_1.test;

import com.liferay.account.constants.AccountActionKeys;
import com.liferay.account.model.AccountEntry;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.cache.CacheRegistryUtil;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.model.impl.ResourceActionImpl;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Lianne Louie
 */
@DataGuard(scope = DataGuard.Scope.NONE)
@RunWith(Arquillian.class)
public class AccountRoleResourceUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testAccountRoleResourceUpgradeProcess() throws Exception {
		_replaceResourceAction(
			AccountActionKeys.UPDATE_ORGANIZATIONS,
			AccountEntry.class.getName(), "EDIT_ORGANIZATIONS");
		_replaceResourceAction(
			ActionKeys.UPDATE_SUBORGANIZATIONS, Organization.class.getName(),
			"EDIT_SUBORGANIZATIONS");
		_replaceResourceAction(
			AccountActionKeys.UPDATE_SUBORGANIZATIONS_ACCOUNTS,
			Organization.class.getName(), "EDIT_SUBORGANIZATIONS_ACCOUNTS");

		_runUpgrade();

		CacheRegistryUtil.clear();

		_entityCache.clearCache(ResourceActionImpl.class);
		_finderCache.clearCache(ResourceActionImpl.class);

		_resourceActionLocalService.checkResourceActions();

		List<ResourceAction> resourceActions =
			_resourceActionLocalService.getResourceActions(
				"EDIT_ORGANIZATIONS");

		Assert.assertEquals(
			resourceActions.toString(), 0, resourceActions.size());

		Assert.assertNotNull(
			_resourceActionLocalService.fetchResourceAction(
				AccountEntry.class.getName(),
				AccountActionKeys.UPDATE_ORGANIZATIONS));

		resourceActions = _resourceActionLocalService.getResourceActions(
			"EDIT_SUBORGANIZATIONS");

		Assert.assertEquals(
			resourceActions.toString(), 0, resourceActions.size());

		Assert.assertNotNull(
			_resourceActionLocalService.fetchResourceAction(
				Organization.class.getName(),
				ActionKeys.UPDATE_SUBORGANIZATIONS));

		resourceActions = _resourceActionLocalService.getResourceActions(
			"EDIT_SUBORGANIZATIONS_ACCOUNTS");

		Assert.assertEquals(
			resourceActions.toString(), 0, resourceActions.size());

		Assert.assertNotNull(
			_resourceActionLocalService.fetchResourceAction(
				Organization.class.getName(),
				AccountActionKeys.UPDATE_SUBORGANIZATIONS_ACCOUNTS));
	}

	private void _replaceResourceAction(
			String actionId, String className, String oldActionId)
		throws Exception {

		ResourceAction resourceAction =
			_resourceActionLocalService.getResourceAction(className, actionId);

		_resourceActionLocalService.deleteResourceAction(resourceAction);

		_resourceActionLocalService.addResourceAction(
			className, oldActionId, resourceAction.getBitwiseValue());
	}

	private void _runUpgrade() throws Exception {
		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator, _CLASS_NAME);

		upgradeProcess.upgrade();
	}

	private static final String _CLASS_NAME =
		"com.liferay.account.internal.upgrade.v2_11_1." +
			"AccountRoleResourceUpgradeProcess";

	@Inject
	private EntityCache _entityCache;

	@Inject
	private FinderCache _finderCache;

	@Inject
	private ResourceActionLocalService _resourceActionLocalService;

	@Inject(
		filter = "(&(component.name=com.liferay.account.internal.upgrade.registry.AccountServiceUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}