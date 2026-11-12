/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.reports.engine.console.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.service.permission.ModelPermissionsFactory;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.reports.engine.console.model.Source;
import com.liferay.portal.reports.engine.console.service.SourceLocalServiceUtil;
import com.liferay.portal.reports.engine.console.service.SourceServiceUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Inácio Nery
 */
@RunWith(Arquillian.class)
public class SourceServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		setUpPermissionChecker();
		setUpSource();
	}

	@After
	public void tearDown() throws PortalException {
		PermissionThreadLocal.setPermissionChecker(_originalPermissionChecker);
	}

	@Test
	public void testGetSourcesAsAdminUser() throws Exception {
		List<Source> sources = getSources(
			_adminPermissionChecker, QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(sources.toString(), 10, sources.size());
	}

	@Test
	public void testGetSourcesAsGuestUser() throws Exception {
		List<Source> sources = getSources(
			_guestPermissionChecker, QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(sources.toString(), 5, sources.size());
	}

	@Test
	public void testGetSourcesCountAsAdminUser() throws Exception {
		Assert.assertEquals(10, getSourcesCount(_adminPermissionChecker));
	}

	@Test
	public void testGetSourcesCountAsGuestUser() throws Exception {
		Assert.assertEquals(5, getSourcesCount(_guestPermissionChecker));
	}

	@Test
	public void testGetSourcesLimitedAsAdminUser() throws Exception {
		List<Source> sources = getSources(_adminPermissionChecker, 0, 5);

		Assert.assertEquals(sources.toString(), 5, sources.size());
	}

	@Test
	public void testGetSourcesLimitedAsGuestUser() throws Exception {
		List<Source> sources = getSources(_guestPermissionChecker, 0, 5);

		Assert.assertEquals(sources.toString(), 5, sources.size());
	}

	protected List<Source> getSources(
			PermissionChecker permissionChecker, int start, int end)
		throws Exception {

		PermissionThreadLocal.setPermissionChecker(permissionChecker);

		return SourceServiceUtil.getSources(
			_group.getGroupId(), null, _URL, false, start, end, null);
	}

	protected int getSourcesCount(PermissionChecker permissionChecker)
		throws Exception {

		PermissionThreadLocal.setPermissionChecker(permissionChecker);

		return SourceServiceUtil.getSourcesCount(
			_group.getGroupId(), null, _URL, false);
	}

	protected User getUser(String roleName) throws Exception {
		Role role = RoleLocalServiceUtil.getRole(
			TestPropsValues.getCompanyId(), roleName);

		List<User> users = UserLocalServiceUtil.getRoleUsers(
			role.getRoleId(), 0, 1);

		return users.get(0);
	}

	protected void setUpPermissionChecker() throws Exception {
		User adminUser = getUser(RoleConstants.ADMINISTRATOR);

		_adminPermissionChecker = PermissionCheckerFactoryUtil.create(
			adminUser);

		User guestUser = getUser(RoleConstants.GUEST);

		_guestPermissionChecker = PermissionCheckerFactoryUtil.create(
			guestUser);
	}

	protected void setUpSource() throws Exception {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.spring.hibernate.DialectUtil",
				LoggerTestUtil.OFF)) {

			ServiceContext serviceContext =
				ServiceContextTestUtil.getServiceContext();

			serviceContext.setModelPermissions(
				ModelPermissionsFactory.create(
					_SOURCE_GROUP_PERMISSIONS, null, Source.class.getName()));

			for (int i = 0; i < 5; i++) {
				SourceLocalServiceUtil.addSource(
					TestPropsValues.getUserId(), _group.getGroupId(),
					HashMapBuilder.put(
						LocaleUtil.US, RandomTestUtil.randomString()
					).build(),
					_DRIVER_CLASS_NAME, _URL, _USER_NAME, _PASSWORD,
					serviceContext);
			}

			serviceContext.setModelPermissions(
				ModelPermissionsFactory.create(
					_SOURCE_GROUP_PERMISSIONS, new String[] {"VIEW"},
					Source.class.getName()));

			for (int i = 0; i < 5; i++) {
				SourceLocalServiceUtil.addSource(
					TestPropsValues.getUserId(), _group.getGroupId(),
					HashMapBuilder.put(
						LocaleUtil.US, RandomTestUtil.randomString()
					).build(),
					_DRIVER_CLASS_NAME, _URL, _USER_NAME, _PASSWORD,
					serviceContext);
			}
		}
	}

	private static final String _DRIVER_CLASS_NAME =
		"org.hsqldb.jdbc.JDBCDriver";

	private static final String _PASSWORD = StringPool.BLANK;

	private static final String[] _SOURCE_GROUP_PERMISSIONS = {
		"DELETE", "PERMISSIONS", "UPDATE", "VIEW"
	};

	private static final String _URL = "jdbc:hsqldb:mem:testDB;shutdown=true";

	private static final String _USER_NAME = "sa";

	private PermissionChecker _adminPermissionChecker;

	@DeleteAfterTestRun
	private Group _group;

	private PermissionChecker _guestPermissionChecker;
	private PermissionChecker _originalPermissionChecker;

}