/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.service.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountEntryUserRelLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
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
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.util.PortalInstances;
import com.liferay.portal.workflow.configuration.WorkflowDefinitionConfiguration;
import com.liferay.portal.workflow.constants.WorkflowDefinitionConstants;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinition;
import com.liferay.portal.workflow.kaleo.service.KaleoDefinitionLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoDefinitionService;
import com.liferay.portal.workflow.kaleo.service.KaleoDefinitionVersionLocalService;

import java.io.InputStream;

import java.util.Collections;

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
 * @author Feliphe Marinho
 * @author Nathaly Gomes
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class KaleoDefinitionServiceImplTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		_company1 = CompanyTestUtil.addCompany();

		PortalInstances.initCompany(_company1);

		_company1AdminUser = UserTestUtil.addCompanyAdminUser(_company1);

		_company2 = CompanyTestUtil.addCompany();

		PortalInstances.initCompany(_company2);

		_company2AdminUser = UserTestUtil.addCompanyAdminUser(_company2);

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

		_setCompanyAdministratorCanPublish(false);
	}

	@Test
	public void testActivateKaleoDefinition() throws Exception {
		_testActivateKaleoDefinition();
		_testActivateKaleoDefinitionFromAnotherCompany();
		_testActivateKaleoDefinitionWithGroupId();
	}

	@Test
	public void testAddKaleoDefinition() throws Exception {
		_testAddKaleoDefinition();
		_testAddKaleoDefinitionFromAnotherCompany();
		_testAddKaleoDefinitionWithGroupId();
	}

	@Test
	public void testDeactivateKaleoDefinition() throws Exception {
		_testDeactivateKaleoDefinition();
		_testDeactivateKaleoDefinitionFromAnotherCompany();
		_testDeactivateKaleoDefinitionWithGroupId();
	}

	@Test
	public void testDeleteKaleoDefinition() throws Exception {
		_testDeleteKaleoDefinition();
		_testDeleteKaleoDefinitionFromAnotherCompany();
		_testDeleteKaleoDefinitionWithGroupId();
	}

	@Test
	public void testGetKaleoDefinition() throws Exception {
		_testGetKaleoDefinition();
		_testGetKaleoDefinitionWithGroupId();
	}

	@Test
	public void testGetScopeKaleoDefinitions() throws Exception {
		User user = _addUser();

		AssertUtils.assertFailure(
			PrincipalException.MustBeCompanyAdmin.class,
			StringBundler.concat(
				"User ", user.getUserId(), " must be the company ",
				"administrator to perform the action"),
			() -> _kaleoDefinitionService.getScopeKaleoDefinitions(
				WorkflowDefinitionConstants.SCOPE_ALL, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null, _serviceContext));
		AssertUtils.assertFailure(
			PrincipalException.MustBeCompanyAdmin.class,
			StringBundler.concat(
				"User ", user.getUserId(), " must be the company ",
				"administrator to perform the action"),
			() -> _kaleoDefinitionService.getScopeKaleoDefinitions(
				WorkflowDefinitionConstants.SCOPE_ALL, true, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null, _serviceContext));

		_setUpPermissionThreadLocal(_company1AdminUser);

		KaleoDefinition singleApprover =
			_kaleoDefinitionLocalService.getKaleoDefinition(
				"Single Approver", _serviceContext);

		Assert.assertEquals(
			Collections.singletonList(singleApprover),
			_kaleoDefinitionService.getScopeKaleoDefinitions(
				WorkflowDefinitionConstants.SCOPE_ALL, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null, _serviceContext));
		Assert.assertEquals(
			Collections.singletonList(singleApprover),
			_kaleoDefinitionService.getScopeKaleoDefinitions(
				WorkflowDefinitionConstants.SCOPE_ALL, true, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null, _serviceContext));
	}

	@Test
	public void testUpdateKaleoDefinition() throws Exception {
		_testUpdateKaleoDefinition();
		_testUpdateKaleoDefinitionFromAnotherCompany();
		_testUpdateKaleoDefinitionWithDifferentGroupId();
		_testUpdateKaleoDefinitionWithGroupId();
	}

	private AccountEntry _addAccountEntry() throws Exception {
		return _addAccountEntry(TestPropsValues.getUserId());
	}

	private AccountEntry _addAccountEntry(long userId) throws Exception {
		return _accountEntryLocalService.addAccountEntry(
			StringPool.BLANK, userId, 0, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), null, null, null,
			RandomTestUtil.randomString(),
			AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS,
			WorkflowConstants.STATUS_APPROVED,
			ServiceContextTestUtil.getServiceContext());
	}

	private KaleoDefinition _addKaleoDefinition() throws Exception {
		return _addKaleoDefinition(
			WorkflowDefinitionConstants.SCOPE_ALL, _serviceContext);
	}

	private KaleoDefinition _addKaleoDefinition(ServiceContext serviceContext)
		throws Exception {

		return _addKaleoDefinition(
			WorkflowDefinitionConstants.SCOPE_AI, serviceContext);
	}

	private KaleoDefinition _addKaleoDefinition(
			String scope, ServiceContext serviceContext)
		throws Exception {

		return _kaleoDefinitionService.addKaleoDefinition(
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			_read(), scope, false, 1, serviceContext);
	}

	private User _addUser() throws Exception {
		return _addUser(_company1.getCompanyId());
	}

	private User _addUser(long companyId) throws Exception {
		User user = UserTestUtil.addUser(
			companyId, TestPropsValues.getUserId(),
			RandomTestUtil.randomString(
				NumericStringRandomizerBumper.INSTANCE,
				UniqueStringRandomizerBumper.INSTANCE),
			LocaleUtil.getDefault(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), new long[0], _serviceContext);

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));

		PrincipalThreadLocal.setName(user.getUserId());

		return user;
	}

	private ServiceContext _getCompany1ServiceContext() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		serviceContext.setCompanyId(_company1.getCompanyId());
		serviceContext.setUserId(_company1AdminUser.getUserId());

		return serviceContext;
	}

	private String _read() throws Exception {
		ClassLoader classLoader =
			BaseKaleoLocalServiceTestCase.class.getClassLoader();

		try (InputStream inputStream = classLoader.getResourceAsStream(
				"com/liferay/portal/workflow/kaleo/dependencies" +
					"/legal-marketing-workflow-definition.xml")) {

			return StringUtil.read(inputStream);
		}
	}

	private void _setCompanyAdministratorCanPublish(boolean canPublish)
		throws Exception {

		ConfigurationTestUtil.saveConfiguration(
			_configuration,
			HashMapDictionaryBuilder.<String, Object>put(
				"company.administrator.can.publish", canPublish
			).build());
	}

	private void _setUpPermissionThreadLocal(User user) {
		PrincipalThreadLocal.setName(user.getUserId());

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));
	}

	private void _testActivateKaleoDefinition() throws Exception {
		_setUpPermissionThreadLocal(_company1AdminUser);

		_setCompanyAdministratorCanPublish(true);

		ServiceContext serviceContext = _getCompany1ServiceContext();

		KaleoDefinition kaleoDefinition = _addKaleoDefinition(
			WorkflowDefinitionConstants.SCOPE_ALL, serviceContext);

		_setCompanyAdministratorCanPublish(false);

		User user = _addUser();

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", user.getUserId(), " must have ",
				ActionKeys.ADD_DEFINITION, " permission for ",
				WorkflowConstants.RESOURCE_NAME, StringPool.SPACE),
			() -> _kaleoDefinitionService.activateKaleoDefinition(
				kaleoDefinition.getName(), kaleoDefinition.getVersion(),
				serviceContext));

		_setUpPermissionThreadLocal(_company1AdminUser);

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", _company1AdminUser.getUserId(), " must have ",
				ActionKeys.ADD_DEFINITION, " permission for ",
				WorkflowConstants.RESOURCE_NAME, StringPool.SPACE),
			() -> _kaleoDefinitionService.activateKaleoDefinition(
				kaleoDefinition.getName(), kaleoDefinition.getVersion(),
				serviceContext));

		_setCompanyAdministratorCanPublish(true);

		KaleoDefinition activeKaleoDefinition =
			_kaleoDefinitionService.activateKaleoDefinition(
				kaleoDefinition.getName(), kaleoDefinition.getVersion(),
				serviceContext);

		Assert.assertTrue(activeKaleoDefinition.isActive());
	}

	private void _testActivateKaleoDefinitionFromAnotherCompany()
		throws Exception {

		_setCompanyAdministratorCanPublish(true);

		_setUpPermissionThreadLocal(TestPropsValues.getUser());

		KaleoDefinition kaleoDefinition = _addKaleoDefinition();

		_setUpPermissionThreadLocal(_company2AdminUser);

		AssertUtils.assertFailure(
			PrincipalException.class,
			"User is not an omniadmin and does not belong to the company",
			() -> _kaleoDefinitionService.activateKaleoDefinition(
				kaleoDefinition.getName(), kaleoDefinition.getVersion(),
				_serviceContext));
	}

	private void _testActivateKaleoDefinitionWithGroupId() throws Exception {
		_setUpPermissionThreadLocal(TestPropsValues.getUser());

		AccountEntry accountEntry = _addAccountEntry();

		KaleoDefinition kaleoDefinition1 = _addKaleoDefinition(
			ServiceContextTestUtil.getServiceContext(
				accountEntry.getAccountEntryGroupId(),
				TestPropsValues.getUserId()));

		User user = _addUser(TestPropsValues.getCompanyId());

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", user.getUserId(), " must have ",
				ActionKeys.ADD_DEFINITION, " permission for ",
				WorkflowConstants.RESOURCE_NAME, StringPool.SPACE),
			() -> _kaleoDefinitionService.activateKaleoDefinition(
				kaleoDefinition1.getName(), kaleoDefinition1.getVersion(),
				_serviceContext));

		_accountEntryUserRelLocalService.addAccountEntryUserRel(
			accountEntry.getAccountEntryId(), user.getUserId());

		KaleoDefinition activeKaleoDefinition =
			_kaleoDefinitionService.activateKaleoDefinition(
				kaleoDefinition1.getName(), kaleoDefinition1.getVersion(),
				_serviceContext);

		Assert.assertTrue(activeKaleoDefinition.isActive());

		KaleoDefinition kaleoDefinition2 =
			_kaleoDefinitionLocalService.getKaleoDefinition(
				"Single Approver", _serviceContext);

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", user.getUserId(), " must have ",
				ActionKeys.ADD_DEFINITION, " permission for ",
				WorkflowConstants.RESOURCE_NAME, StringPool.SPACE),
			() -> _kaleoDefinitionService.activateKaleoDefinition(
				kaleoDefinition2.getName(), kaleoDefinition2.getVersion(),
				_serviceContext));
	}

	private void _testAddKaleoDefinition() throws Exception {

		// A user with the "checkPermission" attribute set to false

		User user = _addUser(TestPropsValues.getCompanyId());

		ServiceContext serviceContext1 =
			ServiceContextTestUtil.getServiceContext();

		serviceContext1.setAttribute("checkPermission", Boolean.FALSE);

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", user.getUserId(), " must have ",
				ActionKeys.ADD_DEFINITION, " permission for ",
				WorkflowConstants.RESOURCE_NAME, StringPool.SPACE),
			() -> _addKaleoDefinition(
				WorkflowDefinitionConstants.SCOPE_ALL, serviceContext1));

		// Administrator with "company.administrator.can.publish" disabled

		_setUpPermissionThreadLocal(_company1AdminUser);

		ServiceContext serviceContext2 = _getCompany1ServiceContext();

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", _company1AdminUser.getUserId(), " must have ",
				ActionKeys.ADD_DEFINITION, " permission for ",
				WorkflowConstants.RESOURCE_NAME, StringPool.SPACE),
			() -> _addKaleoDefinition(
				WorkflowDefinitionConstants.SCOPE_ALL, serviceContext2));

		// Administrator with "company.administrator.can.publish" enabled

		_setCompanyAdministratorCanPublish(true);

		Assert.assertNotNull(
			_addKaleoDefinition(
				WorkflowDefinitionConstants.SCOPE_ALL, serviceContext2));
	}

	private void _testAddKaleoDefinitionFromAnotherCompany() throws Exception {
		_setCompanyAdministratorCanPublish(true);

		_setUpPermissionThreadLocal(_company2AdminUser);

		AssertUtils.assertFailure(
			PrincipalException.class,
			"User is not an omniadmin and does not belong to the company",
			this::_addKaleoDefinition);
	}

	private void _testAddKaleoDefinitionWithGroupId() throws Exception {
		AccountEntry accountEntry = _addAccountEntry();

		User user = _addUser(TestPropsValues.getCompanyId());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				accountEntry.getAccountEntryGroupId(), user.getUserId());

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", user.getUserId(), " must have ",
				ActionKeys.ADD_DEFINITION, " permission for ",
				WorkflowConstants.RESOURCE_NAME, StringPool.SPACE),
			() -> _addKaleoDefinition(serviceContext));

		_accountEntryUserRelLocalService.addAccountEntryUserRel(
			accountEntry.getAccountEntryId(), user.getUserId());

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", user.getUserId(), " must have ",
				ActionKeys.ADD_DEFINITION, " permission for ",
				WorkflowConstants.RESOURCE_NAME, StringPool.SPACE),
			() -> _addKaleoDefinition(
				WorkflowDefinitionConstants.SCOPE_ALL, serviceContext));

		KaleoDefinition kaleoDefinition = _addKaleoDefinition(serviceContext);

		Assert.assertEquals(
			accountEntry.getAccountEntryGroupId(),
			kaleoDefinition.getGroupId());
	}

	private void _testDeactivateKaleoDefinition() throws Exception {
		_setUpPermissionThreadLocal(_company1AdminUser);

		_setCompanyAdministratorCanPublish(true);

		ServiceContext serviceContext = _getCompany1ServiceContext();

		KaleoDefinition kaleoDefinition = _addKaleoDefinition(
			WorkflowDefinitionConstants.SCOPE_ALL, serviceContext);

		_kaleoDefinitionLocalService.activateKaleoDefinition(
			kaleoDefinition.getKaleoDefinitionId(), serviceContext);

		_setCompanyAdministratorCanPublish(false);

		User user = _addUser();

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", user.getUserId(), " must have ",
				ActionKeys.ADD_DEFINITION, " permission for ",
				WorkflowConstants.RESOURCE_NAME, StringPool.SPACE),
			() -> _kaleoDefinitionService.deactivateKaleoDefinition(
				kaleoDefinition.getName(), kaleoDefinition.getVersion(),
				serviceContext));

		_setUpPermissionThreadLocal(_company1AdminUser);

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", _company1AdminUser.getUserId(), " must have ",
				ActionKeys.ADD_DEFINITION, " permission for ",
				WorkflowConstants.RESOURCE_NAME, StringPool.SPACE),
			() -> _kaleoDefinitionService.deactivateKaleoDefinition(
				kaleoDefinition.getName(), kaleoDefinition.getVersion(),
				serviceContext));

		_setCompanyAdministratorCanPublish(true);

		KaleoDefinition inactiveKaleoDefinition =
			_kaleoDefinitionService.deactivateKaleoDefinition(
				kaleoDefinition.getName(), kaleoDefinition.getVersion(),
				serviceContext);

		Assert.assertFalse(inactiveKaleoDefinition.isActive());
	}

	private void _testDeactivateKaleoDefinitionFromAnotherCompany()
		throws Exception {

		_setCompanyAdministratorCanPublish(true);

		_setUpPermissionThreadLocal(TestPropsValues.getUser());

		KaleoDefinition kaleoDefinition = _addKaleoDefinition();

		_kaleoDefinitionLocalService.activateKaleoDefinition(
			kaleoDefinition.getKaleoDefinitionId(), _serviceContext);

		_setUpPermissionThreadLocal(_company2AdminUser);

		AssertUtils.assertFailure(
			PrincipalException.class,
			"User is not an omniadmin and does not belong to the company",
			() -> _kaleoDefinitionService.deactivateKaleoDefinition(
				kaleoDefinition.getName(), kaleoDefinition.getVersion(),
				_serviceContext));
	}

	private void _testDeactivateKaleoDefinitionWithGroupId() throws Exception {
		_setUpPermissionThreadLocal(TestPropsValues.getUser());

		AccountEntry accountEntry = _addAccountEntry();

		KaleoDefinition kaleoDefinition1 = _addKaleoDefinition(
			ServiceContextTestUtil.getServiceContext(
				accountEntry.getAccountEntryGroupId(),
				TestPropsValues.getUserId()));

		_kaleoDefinitionLocalService.activateKaleoDefinition(
			kaleoDefinition1.getKaleoDefinitionId(), _serviceContext);

		User user = _addUser(TestPropsValues.getCompanyId());

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", user.getUserId(), " must have ",
				ActionKeys.ADD_DEFINITION, " permission for ",
				WorkflowConstants.RESOURCE_NAME, StringPool.SPACE),
			() -> _kaleoDefinitionService.deactivateKaleoDefinition(
				kaleoDefinition1.getName(), kaleoDefinition1.getVersion(),
				_serviceContext));

		_accountEntryUserRelLocalService.addAccountEntryUserRel(
			accountEntry.getAccountEntryId(), user.getUserId());

		KaleoDefinition inactiveKaleoDefinition =
			_kaleoDefinitionService.deactivateKaleoDefinition(
				kaleoDefinition1.getName(), kaleoDefinition1.getVersion(),
				_serviceContext);

		Assert.assertFalse(inactiveKaleoDefinition.isActive());

		KaleoDefinition kaleoDefinition2 =
			_kaleoDefinitionLocalService.getKaleoDefinition(
				"Single Approver", _serviceContext);

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", user.getUserId(), " must have ",
				ActionKeys.ADD_DEFINITION, " permission for ",
				WorkflowConstants.RESOURCE_NAME, StringPool.SPACE),
			() -> _kaleoDefinitionService.deactivateKaleoDefinition(
				kaleoDefinition2.getName(), kaleoDefinition2.getVersion(),
				_serviceContext));
	}

	private void _testDeleteKaleoDefinition() throws Exception {
		_setUpPermissionThreadLocal(_company1AdminUser);

		_setCompanyAdministratorCanPublish(true);

		ServiceContext serviceContext = _getCompany1ServiceContext();

		KaleoDefinition kaleoDefinition = _addKaleoDefinition(
			WorkflowDefinitionConstants.SCOPE_ALL, serviceContext);

		_setCompanyAdministratorCanPublish(false);

		User user = _addUser();

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", user.getUserId(), " must have ", ActionKeys.DELETE,
				" permission for ", WorkflowConstants.RESOURCE_NAME,
				StringPool.SPACE),
			() -> _kaleoDefinitionService.deleteKaleoDefinition(
				kaleoDefinition.getName(), serviceContext));

		_setUpPermissionThreadLocal(_company1AdminUser);

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", _company1AdminUser.getUserId(), " must have ",
				ActionKeys.DELETE, " permission for ",
				WorkflowConstants.RESOURCE_NAME, StringPool.SPACE),
			() -> _kaleoDefinitionService.deleteKaleoDefinition(
				kaleoDefinition.getName(), serviceContext));

		_setCompanyAdministratorCanPublish(true);

		_kaleoDefinitionService.deleteKaleoDefinition(
			kaleoDefinition.getName(), serviceContext);

		Assert.assertNull(
			_kaleoDefinitionLocalService.fetchKaleoDefinition(
				kaleoDefinition.getName(), serviceContext));
	}

	private void _testDeleteKaleoDefinitionFromAnotherCompany()
		throws Exception {

		_setCompanyAdministratorCanPublish(true);

		_setUpPermissionThreadLocal(TestPropsValues.getUser());

		KaleoDefinition kaleoDefinition = _addKaleoDefinition();

		_setUpPermissionThreadLocal(_company2AdminUser);

		AssertUtils.assertFailure(
			PrincipalException.class,
			"User is not an omniadmin and does not belong to the company",
			() -> _kaleoDefinitionService.deleteKaleoDefinition(
				kaleoDefinition.getName(), _serviceContext));
	}

	private void _testDeleteKaleoDefinitionWithGroupId() throws Exception {
		_setUpPermissionThreadLocal(TestPropsValues.getUser());

		AccountEntry accountEntry = _addAccountEntry();

		KaleoDefinition kaleoDefinition1 = _addKaleoDefinition(
			ServiceContextTestUtil.getServiceContext(
				accountEntry.getAccountEntryGroupId(),
				TestPropsValues.getUserId()));

		User user = _addUser(TestPropsValues.getCompanyId());

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", user.getUserId(), " must have ", ActionKeys.DELETE,
				" permission for ", WorkflowConstants.RESOURCE_NAME,
				StringPool.SPACE),
			() -> _kaleoDefinitionService.deleteKaleoDefinition(
				kaleoDefinition1.getName(), _serviceContext));

		_accountEntryUserRelLocalService.addAccountEntryUserRel(
			accountEntry.getAccountEntryId(), user.getUserId());

		_kaleoDefinitionService.deleteKaleoDefinition(
			kaleoDefinition1.getName(), _serviceContext);

		Assert.assertNull(
			_kaleoDefinitionLocalService.fetchKaleoDefinition(
				kaleoDefinition1.getName(), _serviceContext));

		KaleoDefinition kaleoDefinition2 =
			_kaleoDefinitionLocalService.getKaleoDefinition(
				"Single Approver", _serviceContext);

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", user.getUserId(), " must have ", ActionKeys.DELETE,
				" permission for ", WorkflowConstants.RESOURCE_NAME,
				StringPool.SPACE),
			() -> _kaleoDefinitionService.deleteKaleoDefinition(
				kaleoDefinition2.getName(), _serviceContext));
	}

	private void _testGetKaleoDefinition() throws Exception {
		KaleoDefinition kaleoDefinition = _addKaleoDefinition();

		User user = _addUser();

		AssertUtils.assertFailure(
			PrincipalException.MustBeCompanyAdmin.class,
			StringBundler.concat(
				"User ", user.getUserId(), " must be the company ",
				"administrator to perform the action"),
			() -> _kaleoDefinitionService.getKaleoDefinition(
				kaleoDefinition.getKaleoDefinitionId()));

		_setUpPermissionThreadLocal(_company1AdminUser);

		Assert.assertEquals(
			kaleoDefinition,
			_kaleoDefinitionService.getKaleoDefinition(
				kaleoDefinition.getKaleoDefinitionId()));
	}

	private void _testGetKaleoDefinitionWithGroupId() throws Exception {
		_setUpPermissionThreadLocal(TestPropsValues.getUser());

		_setCompanyAdministratorCanPublish(true);

		AccountEntry accountEntry = _addAccountEntry();

		KaleoDefinition kaleoDefinition = _addKaleoDefinition(
			ServiceContextTestUtil.getServiceContext(
				accountEntry.getAccountEntryGroupId(),
				TestPropsValues.getUserId()));

		Assert.assertEquals(
			accountEntry.getAccountEntryGroupId(),
			kaleoDefinition.getGroupId());

		User user = _addUser(TestPropsValues.getCompanyId());

		AssertUtils.assertFailure(
			PrincipalException.MustBeCompanyAdmin.class,
			StringBundler.concat(
				"User ", user.getUserId(), " must be the company ",
				"administrator to perform the action"),
			() -> _kaleoDefinitionService.getKaleoDefinition(
				kaleoDefinition.getKaleoDefinitionId()));

		_accountEntryUserRelLocalService.addAccountEntryUserRel(
			accountEntry.getAccountEntryId(), user.getUserId());

		Assert.assertNotNull(
			_kaleoDefinitionService.getKaleoDefinition(
				kaleoDefinition.getKaleoDefinitionId()));
	}

	private void _testUpdateKaleoDefinition() throws Exception {
		_setUpPermissionThreadLocal(_company1AdminUser);

		_setCompanyAdministratorCanPublish(true);

		ServiceContext serviceContext1 = _getCompany1ServiceContext();

		KaleoDefinition kaleoDefinition = _addKaleoDefinition(
			WorkflowDefinitionConstants.SCOPE_ALL, serviceContext1);

		_setCompanyAdministratorCanPublish(false);

		// A user with the "checkPermission" attribute set to false

		User user = _addUser();

		ServiceContext serviceContext2 =
			ServiceContextTestUtil.getServiceContext();

		serviceContext2.setAttribute("checkPermission", Boolean.FALSE);
		serviceContext2.setCompanyId(_company1.getCompanyId());

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", user.getUserId(), " must have ",
				ActionKeys.ADD_DEFINITION, " permission for ",
				WorkflowConstants.RESOURCE_NAME, StringPool.SPACE),
			() -> _kaleoDefinitionService.updateKaleoDefinition(
				kaleoDefinition.getExternalReferenceCode(),
				kaleoDefinition.getKaleoDefinitionId(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				kaleoDefinition.getContent(), false, serviceContext2));

		// Administrator with "company.administrator.can.publish" disabled

		_setUpPermissionThreadLocal(_company1AdminUser);

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", _company1AdminUser.getUserId(), " must have ",
				ActionKeys.ADD_DEFINITION, " permission for ",
				WorkflowConstants.RESOURCE_NAME, StringPool.SPACE),
			() -> _kaleoDefinitionService.updateKaleoDefinition(
				kaleoDefinition.getExternalReferenceCode(),
				kaleoDefinition.getKaleoDefinitionId(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				kaleoDefinition.getContent(), false, serviceContext1));

		// Administrator with "company.administrator.can.publish" enabled

		_setCompanyAdministratorCanPublish(true);

		Assert.assertNotNull(
			_kaleoDefinitionService.updateKaleoDefinition(
				kaleoDefinition.getExternalReferenceCode(),
				kaleoDefinition.getKaleoDefinitionId(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				kaleoDefinition.getContent(), false, serviceContext1));
	}

	private void _testUpdateKaleoDefinitionFromAnotherCompany()
		throws Exception {

		_setCompanyAdministratorCanPublish(true);

		_setUpPermissionThreadLocal(TestPropsValues.getUser());

		KaleoDefinition kaleoDefinition = _addKaleoDefinition();

		_setUpPermissionThreadLocal(_company2AdminUser);

		AssertUtils.assertFailure(
			PrincipalException.class,
			"User is not an omniadmin and does not belong to the company",
			() -> _kaleoDefinitionService.updateKaleoDefinition(
				kaleoDefinition.getExternalReferenceCode(),
				kaleoDefinition.getKaleoDefinitionId(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				kaleoDefinition.getContent(), false, _serviceContext));
	}

	private void _testUpdateKaleoDefinitionWithDifferentGroupId()
		throws Exception {

		_setUpPermissionThreadLocal(_company1AdminUser);

		_setCompanyAdministratorCanPublish(true);

		AccountEntry accountEntry1 = _addAccountEntry(
			_company1AdminUser.getUserId());

		KaleoDefinition kaleoDefinition = _addKaleoDefinition(
			ServiceContextTestUtil.getServiceContext(
				accountEntry1.getAccountEntryGroupId(),
				_company1AdminUser.getUserId()));

		AccountEntry accountEntry2 = _addAccountEntry(
			_company1AdminUser.getUserId());

		User user = _addUser();

		_accountEntryUserRelLocalService.addAccountEntryUserRel(
			accountEntry2.getAccountEntryId(), user.getUserId());

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", user.getUserId(), " must have ",
				ActionKeys.ADD_DEFINITION, " permission for ",
				WorkflowConstants.RESOURCE_NAME, StringPool.SPACE),
			() -> _kaleoDefinitionService.updateKaleoDefinition(
				kaleoDefinition.getExternalReferenceCode(),
				kaleoDefinition.getKaleoDefinitionId(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				kaleoDefinition.getContent(), false,
				ServiceContextTestUtil.getServiceContext(
					accountEntry2.getAccountEntryGroupId(), user.getUserId())));
	}

	private void _testUpdateKaleoDefinitionWithGroupId() throws Exception {
		_setUpPermissionThreadLocal(_company1AdminUser);

		_setCompanyAdministratorCanPublish(true);

		AccountEntry accountEntry = _addAccountEntry(
			_company1AdminUser.getUserId());

		KaleoDefinition kaleoDefinition1 = _addKaleoDefinition(
			ServiceContextTestUtil.getServiceContext(
				accountEntry.getAccountEntryGroupId(),
				_company1AdminUser.getUserId()));

		Assert.assertEquals(
			accountEntry.getAccountEntryGroupId(),
			kaleoDefinition1.getGroupId());

		KaleoDefinition kaleoDefinition2 = _addKaleoDefinition(
			WorkflowDefinitionConstants.SCOPE_ALL,
			_getCompany1ServiceContext());

		User user = _addUser();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				accountEntry.getAccountEntryGroupId(), user.getUserId());

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", user.getUserId(), " must have ",
				ActionKeys.ADD_DEFINITION, " permission for ",
				WorkflowConstants.RESOURCE_NAME, StringPool.SPACE),
			() -> _kaleoDefinitionService.updateKaleoDefinition(
				kaleoDefinition1.getExternalReferenceCode(),
				kaleoDefinition1.getKaleoDefinitionId(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				kaleoDefinition1.getContent(), false, serviceContext));

		_accountEntryUserRelLocalService.addAccountEntryUserRel(
			accountEntry.getAccountEntryId(), user.getUserId());

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", user.getUserId(), " must have ",
				ActionKeys.ADD_DEFINITION, " permission for ",
				WorkflowConstants.RESOURCE_NAME, StringPool.SPACE),
			() -> _kaleoDefinitionService.updateKaleoDefinition(
				kaleoDefinition2.getExternalReferenceCode(),
				kaleoDefinition2.getKaleoDefinitionId(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				kaleoDefinition2.getContent(), false, serviceContext));

		Assert.assertNotNull(
			_kaleoDefinitionService.updateKaleoDefinition(
				kaleoDefinition1.getExternalReferenceCode(),
				kaleoDefinition1.getKaleoDefinitionId(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				kaleoDefinition1.getContent(), false, serviceContext));
	}

	private static Company _company1;
	private static User _company1AdminUser;
	private static Company _company2;
	private static User _company2AdminUser;
	private static Configuration _configuration;

	@Inject
	private static ConfigurationAdmin _configurationAdmin;

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	@Inject
	private AccountEntryUserRelLocalService _accountEntryUserRelLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private KaleoDefinitionLocalService _kaleoDefinitionLocalService;

	@Inject
	private KaleoDefinitionService _kaleoDefinitionService;

	@Inject
	private KaleoDefinitionVersionLocalService
		_kaleoDefinitionVersionLocalService;

	private String _originalName;
	private PermissionChecker _originalPermissionChecker;
	private ServiceContext _serviceContext;

}