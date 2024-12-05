/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.WorkflowDefinitionLink;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkService;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.randomizerbumpers.NumericStringRandomizerBumper;
import com.liferay.portal.kernel.test.randomizerbumpers.UniqueStringRandomizerBumper;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.util.PortalInstances;
import com.liferay.portal.workflow.configuration.WorkflowDefinitionConfiguration;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinition;
import com.liferay.portal.workflow.kaleo.service.KaleoDefinitionLocalService;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Jhosseph Gonzalez
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class WorkflowDefinitionLinkServiceImplTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		_company = CompanyTestUtil.addCompany();

		PortalInstances.initCompany(_company);

		_companyAdminUser = UserTestUtil.addCompanyAdminUser(_company);

		_configuration = _configurationAdmin.getConfiguration(
			WorkflowDefinitionConfiguration.class.getName(),
			StringPool.QUESTION);
	}

	@Before
	public void setUp() throws Exception {
		_originalName = PrincipalThreadLocal.getName();
		_originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();
		_serviceContext = ServiceContextTestUtil.getServiceContext();
	}

	@After
	public void tearDown() throws Exception {
		PermissionThreadLocal.setPermissionChecker(_originalPermissionChecker);
		PrincipalThreadLocal.setName(_originalName);

		ConfigurationTestUtil.saveConfiguration(
			_configuration,
			HashMapDictionaryBuilder.<String, Object>put(
				"company.administrator.can.publish", false
			).build());
	}

	@Test
	public void testAddWorkflowDefinitionLink() throws Exception {
		KaleoDefinition kaleoDefinition = _addKaleoDefinition();

		_kaleoDefinitionLocalService.activateKaleoDefinition(
			kaleoDefinition.getKaleoDefinitionId(), _serviceContext);

		_setUpPermissionThreadLocal(_companyAdminUser);

		AssertUtils.assertFailure(
			PrincipalException.MustBeCompanyAdmin.class,
			StringBundler.concat(
				"User ", _companyAdminUser.getUserId(), " must be the company ",
				"administrator to perform the action"),
			() -> _workflowDefinitionLinkService.addWorkflowDefinitionLink(
				_companyAdminUser.getUserId(), TestPropsValues.getCompanyId(),
				TestPropsValues.getGroupId(), BlogsEntry.class.getName(), 0, 0,
				kaleoDefinition.getName(), 1));

		ConfigurationTestUtil.saveConfiguration(
			_configuration,
			HashMapDictionaryBuilder.<String, Object>put(
				"company.administrator.can.publish", true
			).build());

		Assert.assertNotNull(
			_addWorkflowDefinitionLink(
				kaleoDefinition.getName(), BlogsEntry.class.getName()));
	}

	@Test
	public void testGetWorkflowDefinitionLinks() throws Exception {
		_setUpPermissionThreadLocal(_companyAdminUser);

		ConfigurationTestUtil.saveConfiguration(
			_configuration,
			HashMapDictionaryBuilder.<String, Object>put(
				"company.administrator.can.publish", true
			).build());

		WorkflowDefinitionLink workflowDefinitionLink1 =
			_addWorkflowDefinitionLink(
				"Single Approver", BlogsEntry.class.getName());

		User user = _addUser();

		_setUpPermissionThreadLocal(user);

		AssertUtils.assertFailure(
			PrincipalException.MustBeCompanyAdmin.class,
			StringBundler.concat(
				"User ", user.getUserId(), " must be the company ",
				"administrator to perform the action"),
			() -> _workflowDefinitionLinkService.getWorkflowDefinitionLinks(
				TestPropsValues.getCompanyId(), "Single Approver", 1));

		_setUpPermissionThreadLocal(_companyAdminUser);

		List<WorkflowDefinitionLink> workflowDefinitionLinks =
			_workflowDefinitionLinkService.getWorkflowDefinitionLinks(
				TestPropsValues.getCompanyId(), "Single Approver", 1);

		WorkflowDefinitionLink workflowDefinitionLink2 =
			workflowDefinitionLinks.get(0);

		Assert.assertEquals(
			workflowDefinitionLink1.getClassName(),
			workflowDefinitionLink2.getClassName());
		Assert.assertEquals(
			workflowDefinitionLink1.getWorkflowDefinitionName(),
			workflowDefinitionLink2.getWorkflowDefinitionName());
	}

	@Test
	public void testUpdateWorkflowDefinitionLink() throws Exception {
		_setUpPermissionThreadLocal(_companyAdminUser);

		ConfigurationTestUtil.saveConfiguration(
			_configuration,
			HashMapDictionaryBuilder.<String, Object>put(
				"company.administrator.can.publish", true
			).build());

		KaleoDefinition kaleoDefinition = _addKaleoDefinition();

		WorkflowDefinitionLink workflowDefinitionLink1 =
			_addWorkflowDefinitionLink(
				"Single Approver", BlogsEntry.class.getName());

		User user = _addUser();

		_setUpPermissionThreadLocal(user);

		AssertUtils.assertFailure(
			PrincipalException.MustBeCompanyAdmin.class,
			StringBundler.concat(
				"User ", user.getUserId(), " must be the company ",
				"administrator to perform the action"),
			() -> _workflowDefinitionLinkService.updateWorkflowDefinitionLink(
				workflowDefinitionLink1.getExternalReferenceCode(),
				TestPropsValues.getUserId(), TestPropsValues.getCompanyId(),
				workflowDefinitionLink1.getGroupId(),
				"com.liferay.account.model.AccountEntry", 0, 0,
				kaleoDefinition.getName(),
				workflowDefinitionLink1.getWorkflowDefinitionVersion()));

		_setUpPermissionThreadLocal(_companyAdminUser);

		WorkflowDefinitionLink workflowDefinitionLink2 =
			_workflowDefinitionLinkService.updateWorkflowDefinitionLink(
				workflowDefinitionLink1.getExternalReferenceCode(),
				TestPropsValues.getUserId(), TestPropsValues.getCompanyId(),
				workflowDefinitionLink1.getGroupId(),
				"com.liferay.account.model.AccountEntry", 0, 0,
				kaleoDefinition.getName(),
				workflowDefinitionLink1.getWorkflowDefinitionVersion());

		Assert.assertEquals(
			"com.liferay.account.model.AccountEntry",
			workflowDefinitionLink2.getClassName());

		Assert.assertEquals(
			kaleoDefinition.getName(),
			workflowDefinitionLink2.getWorkflowDefinitionName());
	}

	private KaleoDefinition _addKaleoDefinition() throws Exception {
		return _kaleoDefinitionLocalService.addKaleoDefinition(
			null, RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), null, StringPool.BLANK, 1,
			_serviceContext);
	}

	private User _addUser() throws Exception {
		return UserTestUtil.addUser(
			_company.getCompanyId(), TestPropsValues.getUserId(),
			RandomTestUtil.randomString(
				NumericStringRandomizerBumper.INSTANCE,
				UniqueStringRandomizerBumper.INSTANCE),
			LocaleUtil.getDefault(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), new long[0], _serviceContext);
	}

	private WorkflowDefinitionLink _addWorkflowDefinitionLink(
			String kaleoDefinitionName, String className)
		throws Exception {

		return _workflowDefinitionLinkService.addWorkflowDefinitionLink(
			_companyAdminUser.getUserId(), TestPropsValues.getCompanyId(),
			TestPropsValues.getGroupId(), className, 0, 0, kaleoDefinitionName,
			1);
	}

	private void _setUpPermissionThreadLocal(User user) {
		PrincipalThreadLocal.setName(user.getUserId());

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));
	}

	private static Company _company;
	private static User _companyAdminUser;
	private static Configuration _configuration;

	@Inject
	private static ConfigurationAdmin _configurationAdmin;

	@Inject
	private KaleoDefinitionLocalService _kaleoDefinitionLocalService;

	private String _originalName;
	private PermissionChecker _originalPermissionChecker;
	private ServiceContext _serviceContext;

	@Inject
	private WorkflowDefinitionLinkService _workflowDefinitionLinkService;

}