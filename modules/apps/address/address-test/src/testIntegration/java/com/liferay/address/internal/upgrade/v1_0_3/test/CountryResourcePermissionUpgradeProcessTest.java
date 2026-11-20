/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.address.internal.upgrade.v1_0_3.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.cache.CacheRegistryUtil;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.CountryLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.CountryTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.model.impl.ResourceActionImpl;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Tancredi Covioli
 */
@RunWith(Arquillian.class)
public class CountryResourcePermissionUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		Group group = GroupTestUtil.addGroup();
		User user = UserTestUtil.addUser();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			group.getCompanyId(), group.getGroupId(), user.getUserId());

		_country = _countryLocalService.addCountry(
			null, CountryTestUtil.randomA2(_serviceContext.getCompanyId()),
			CountryTestUtil.randomA3(_serviceContext.getCompanyId()), true,
			true, RandomTestUtil.randomString(3), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), 0L, true, true, true,
			_serviceContext);

		_role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);
	}

	@Test
	public void testDoUpgrade() throws Exception {
		_resourceActionLocalService.checkResourceActions(
			PortletKeys.PORTAL, Collections.singletonList("MANAGE_COUNTRIES"));

		_resourcePermissionLocalService.addResourcePermission(
			_serviceContext.getCompanyId(), PortletKeys.PORTAL,
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(_serviceContext.getCompanyId()), _role.getRoleId(),
			"MANAGE_COUNTRIES");

		Assert.assertEquals(
			1,
			_resourcePermissionLocalService.getResourcePermissionsCount(
				_country.getCompanyId(), Country.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(_country.getCountryId())));

		_resourceLocalService.deleteResource(
			_country, ResourceConstants.SCOPE_INDIVIDUAL);

		_runUpgrade();

		CacheRegistryUtil.clear();

		_entityCache.clearCache();
		_finderCache.clearCache(ResourceActionImpl.class);
		_multiVMPool.clear();

		Assert.assertNull(
			_resourceActionLocalService.fetchResourceAction(
				PortletKeys.PORTAL, "MANAGE_COUNTRIES"));
		Assert.assertEquals(
			1,
			_resourcePermissionLocalService.getResourcePermissionsCount(
				_country.getCompanyId(), Country.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(_country.getCountryId())));
		Assert.assertTrue(
			_resourcePermissionLocalService.hasResourcePermission(
				_serviceContext.getCompanyId(), Country.class.getName(),
				ResourceConstants.SCOPE_COMPANY,
				String.valueOf(_serviceContext.getCompanyId()),
				_role.getRoleId(), ActionKeys.DELETE));
		Assert.assertTrue(
			_resourcePermissionLocalService.hasResourcePermission(
				_serviceContext.getCompanyId(), Country.class.getName(),
				ResourceConstants.SCOPE_COMPANY,
				String.valueOf(_serviceContext.getCompanyId()),
				_role.getRoleId(), ActionKeys.PERMISSIONS));
		Assert.assertTrue(
			_resourcePermissionLocalService.hasResourcePermission(
				_serviceContext.getCompanyId(), Country.class.getName(),
				ResourceConstants.SCOPE_COMPANY,
				String.valueOf(_serviceContext.getCompanyId()),
				_role.getRoleId(), ActionKeys.UPDATE));
		Assert.assertTrue(
			_resourcePermissionLocalService.hasResourcePermission(
				_serviceContext.getCompanyId(), PortletKeys.PORTAL,
				ResourceConstants.SCOPE_COMPANY,
				String.valueOf(_serviceContext.getCompanyId()),
				_role.getRoleId(), ActionKeys.ADD_COUNTRY));
	}

	private void _runUpgrade() throws Exception {
		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator, _CLASS_NAME);

		upgradeProcess.upgrade();
	}

	private static final String _CLASS_NAME =
		"com.liferay.address.internal.upgrade.v1_0_3." +
			"CountryResourcePermissionUpgradeProcess";

	private Country _country;

	@Inject
	private CountryLocalService _countryLocalService;

	@Inject
	private EntityCache _entityCache;

	@Inject
	private FinderCache _finderCache;

	@Inject
	private MultiVMPool _multiVMPool;

	@Inject
	private ResourceActionLocalService _resourceActionLocalService;

	@Inject
	private ResourceLocalService _resourceLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	private Role _role;
	private ServiceContext _serviceContext;

	@Inject(
		filter = "(&(component.name=com.liferay.address.internal.upgrade.registry.AddressUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}