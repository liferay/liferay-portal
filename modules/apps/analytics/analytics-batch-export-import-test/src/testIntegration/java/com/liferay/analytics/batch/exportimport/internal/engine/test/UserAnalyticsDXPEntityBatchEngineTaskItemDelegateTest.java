/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.batch.exportimport.internal.engine.test;

import com.liferay.analytics.dxp.entity.rest.dto.v1_0.DXPEntity;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.batch.engine.BatchEngineTaskItemDelegate;
import com.liferay.batch.engine.pagination.Page;
import com.liferay.batch.engine.pagination.Pagination;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Collections;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Nilton Vieira
 */
@RunWith(Arquillian.class)
public class UserAnalyticsDXPEntityBatchEngineTaskItemDelegateTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		ReflectionTestUtil.setFieldValue(
			_batchEngineTaskItemDelegate, "contextCompany",
			CompanyLocalServiceUtil.getCompany(TestPropsValues.getCompanyId()));

		_permissionChecker = PermissionThreadLocal.getPermissionChecker();
		_user = UserTestUtil.addUser();
	}

	@After
	public void tearDown() {
		PermissionThreadLocal.setPermissionChecker(_permissionChecker);
	}

	@Test
	public void testRead() throws Exception {
		_testReadIfAdministratorUser();
		_testReadIfAnalyticsAdministratorUser();
		_testReadIfRegularUser();
	}

	private Page<DXPEntity> _read() throws Exception {
		return _batchEngineTaskItemDelegate.read(
			null, Pagination.of(1, 1), null, Collections.emptyMap(), null);
	}

	private void _testReadIfAdministratorUser() throws Exception {
		UserTestUtil.setUser(TestPropsValues.getUser());

		Assert.assertNotNull(_read());
	}

	private void _testReadIfAnalyticsAdministratorUser() throws Exception {
		Role role = _roleLocalService.getRole(
			_user.getCompanyId(), RoleConstants.ANALYTICS_ADMINISTRATOR);

		_userLocalService.addRoleUser(role.getRoleId(), _user.getUserId());

		UserTestUtil.setUser(_user);

		Assert.assertNotNull(_read());
	}

	private void _testReadIfRegularUser() throws Exception {
		UserTestUtil.setUser(_user);

		try {
			_read();

			Assert.fail();
		}
		catch (PrincipalException.MustHavePermission principalException) {
			Assert.assertNotNull(principalException);
		}
	}

	@Inject(
		filter = "component.name=com.liferay.analytics.batch.exportimport.internal.engine.UserAnalyticsDXPEntityBatchEngineTaskItemDelegate"
	)
	private BatchEngineTaskItemDelegate<DXPEntity> _batchEngineTaskItemDelegate;

	private PermissionChecker _permissionChecker;

	@Inject
	private RoleLocalService _roleLocalService;

	@DeleteAfterTestRun
	private User _user;

	@Inject
	private UserLocalService _userLocalService;

}