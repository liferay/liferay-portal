/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.on.demand.admin.internal.search.spi.model.permission.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.on.demand.admin.manager.OnDemandAdminManager;
import com.liferay.on.demand.admin.test.util.OnDemandAdminTestUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.LinkedHashMap;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Pei-Jung Lan
 */
@RunWith(Arquillian.class)
public class UserModelPreFilterContributorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		_originalName = PrincipalThreadLocal.getName();

		PrincipalThreadLocal.setName(TestPropsValues.getUserId());
	}

	@AfterClass
	public static void tearDownClass() {
		PrincipalThreadLocal.setName(_originalName);
	}

	@Test
	public void testSearchUsers() throws Exception {
		Company company = CompanyTestUtil.addCompany();

		OnDemandAdminTestUtil.addOnDemandAdminUser(company);
		OnDemandAdminTestUtil.addOnDemandAdminUser(company);
		UserTestUtil.addUser(company);
		UserTestUtil.addUser(company);

		List<User> users = _userLocalService.search(
			company.getCompanyId(), null, WorkflowConstants.STATUS_APPROVED,
			new LinkedHashMap<String, Object>(), QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, (OrderByComparator<User>)null);

		Assert.assertEquals(
			users.toString(),
			UserTestUtil.getCompanyUsersCount(company.getCompanyId()) - 2,
			users.size());

		for (User user : users) {
			Assert.assertFalse(_onDemandAdminManager.isOnDemandAdminUser(user));
		}

		_companyLocalService.deleteCompany(company);
	}

	private static String _originalName;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private OnDemandAdminManager _onDemandAdminManager;

	@Inject
	private UserLocalService _userLocalService;

}