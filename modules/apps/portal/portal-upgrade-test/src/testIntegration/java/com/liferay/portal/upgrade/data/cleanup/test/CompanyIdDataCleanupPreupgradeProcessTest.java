/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.data.cleanup.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.model.ClassName;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.data.cleanup.CompanyIdDataCleanupPreupgradeProcess;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Luis Ortiz
 */
@DataGuard(autoDelete = false, scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class CompanyIdDataCleanupPreupgradeProcessTest
	extends CompanyIdDataCleanupPreupgradeProcess {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_classNames = _classNameLocalService.getClassNames(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		_resourceActions = _resourceActionLocalService.getResourceActions(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	@After
	public void tearDown() throws Exception {
		List<ClassName> classNames = ListUtil.remove(
			_classNameLocalService.getClassNames(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS),
			_classNames);

		for (ClassName className : classNames) {
			_classNameLocalService.deleteClassName(className);
		}

		List<ResourceAction> resourceActions = ListUtil.remove(
			_resourceActionLocalService.getResourceActions(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS),
			_resourceActions);

		for (ResourceAction resourceAction : resourceActions) {
			_resourceActionLocalService.deleteResourceAction(resourceAction);
		}
	}

	@Test
	public void testUpgrade() throws Exception {
		String webId = RandomTestUtil.randomString() + "test.com";

		Company company = _companyLocalService.addCompany(
			null, webId, webId, "test.com", 0, true, true, null, null, null,
			null, null, null);

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					PortalInstancePool.getDefaultCompanyId())) {

			runSQL(
				"delete from Company where companyId = " +
					company.getCompanyId());
		}

		upgrade();
	}

	private static List<ClassName> _classNames;
	private static List<ResourceAction> _resourceActions;

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private MultiVMPool _multiVMPool;

	@Inject
	private ResourceActionLocalService _resourceActionLocalService;

}