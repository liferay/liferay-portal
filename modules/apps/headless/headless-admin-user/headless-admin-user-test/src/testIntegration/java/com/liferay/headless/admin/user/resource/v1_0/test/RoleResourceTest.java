/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.resource.v1_0.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountRole;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountRoleLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.exportimport.test.rule.LazyReferencing;
import com.liferay.exportimport.test.rule.LazyReferencingTestRule;
import com.liferay.headless.admin.user.client.dto.v1_0.Role;
import com.liferay.headless.admin.user.client.dto.v1_0.RolePermission;
import com.liferay.headless.admin.user.client.http.HttpInvoker;
import com.liferay.headless.admin.user.client.pagination.Page;
import com.liferay.headless.admin.user.client.pagination.Pagination;
import com.liferay.headless.admin.user.client.permission.Permission;
import com.liferay.headless.admin.user.client.problem.Problem;
import com.liferay.headless.admin.user.client.resource.v1_0.RoleResource;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.OrganizationTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.SynchronousMailTestRule;
import com.liferay.portal.vulcan.permission.PermissionUtil;

import java.text.DateFormat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Javier Gamarra
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class RoleResourceTest extends BaseRoleResourceTestCase {

	@ClassRule
	@Rule
	public static final LazyReferencingTestRule lazyReferencingTestRule =
		LazyReferencingTestRule.INSTANCE;

	@ClassRule
	@Rule
	public static final SynchronousMailTestRule synchronousMailTestRule =
		SynchronousMailTestRule.INSTANCE;

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_organization = OrganizationTestUtil.addOrganization();
		_user = UserTestUtil.addGroupAdminUser(testGroup);
	}

	@Override
	@Test
	public void testGetRole() throws Exception {
		super.testGetRole();

		_testGetRoleVisibility();
		_testGetRoleWithNestedFields();
		_testGetRoleWithRolePermissions();
	}

	@Override
	@Test
	public void testGetRolesPage() throws Exception {
		_testGetRolesPage();
		_testGetRolesPageAsGuest();
		_testGetRolesPageWithFilter();
		_testGetRolesPageWithRolePermissions();
		_testGetRolesPageWithType();
	}

	@Override
	@Test
	public void testGetRolesPageWithPagination() throws Exception {
		Page<Role> rolesPage = roleResource.getRolesPage(
			null, null, null, null);

		testGetRolesPage_addRole(randomRole());
		testGetRolesPage_addRole(randomRole());
		testGetRolesPage_addRole(randomRole());

		Page<Role> page1 = roleResource.getRolesPage(
			null, null, null, Pagination.of(1, 2));

		List<Role> roles1 = (List<Role>)page1.getItems();

		Assert.assertEquals(roles1.toString(), 2, roles1.size());

		Page<Role> page2 = roleResource.getRolesPage(
			null, null, null, Pagination.of(2, 2));

		Assert.assertEquals(
			rolesPage.getTotalCount() + 3, page2.getTotalCount());
	}

	@Override
	@Test
	public void testGraphQLGetRolesPage() throws Exception {
		GraphQLField graphQLField = new GraphQLField(
			"roles",
			(HashMap)HashMapBuilder.put(
				"page", 1
			).put(
				"pageSize", 2
			).build(),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		int totalCount = JSONUtil.getValueAsInt(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/roles", "Object/totalCount");

		testGraphQLRole_addRole();
		testGraphQLRole_addRole();

		Assert.assertEquals(
			totalCount + 2,
			JSONUtil.getValueAsInt(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/roles", "Object/totalCount"));
	}

	@Override
	@Test
	public void testPatchRole() throws Exception {
		super.testPatchRole();

		_testPatchRoleWithPermissions();
		_testPatchRoleWithRolePermissions();
	}

	@Override
	@Test
	public void testPostOrganizationRoleByExternalReferenceCodeUserAccountAssociation()
		throws Exception {

		Role role =
			testPostOrganizationRoleByExternalReferenceCodeUserAccountAssociation_addRole();

		assertHttpResponseStatusCode(
			204,
			roleResource.
				postOrganizationRoleByExternalReferenceCodeUserAccountAssociationHttpResponse(
					role.getExternalReferenceCode(), _user.getUserId(),
					_organization.getOrganizationId()));

		assertHttpResponseStatusCode(
			404,
			roleResource.
				postOrganizationRoleByExternalReferenceCodeUserAccountAssociationHttpResponse(
					RandomTestUtil.randomString(), _user.getUserId(),
					_organization.getOrganizationId()));

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME_EXCEPTION_MAPPER, LoggerTestUtil.ERROR)) {

			assertHttpResponseStatusCode(
				500,
				roleResource.
					postOrganizationRoleByExternalReferenceCodeUserAccountAssociationHttpResponse(
						_getRoleExternalReferenceCode(
							_addRole(true, RoleConstants.TYPE_REGULAR)),
						_user.getUserId(), _organization.getOrganizationId()));
			assertHttpResponseStatusCode(
				500,
				roleResource.
					postOrganizationRoleByExternalReferenceCodeUserAccountAssociationHttpResponse(
						_getRoleExternalReferenceCode(
							_addRole(true, RoleConstants.TYPE_SITE)),
						_user.getUserId(), _organization.getOrganizationId()));
		}
	}

	@Override
	@Test
	public void testPostOrganizationRoleUserAccountAssociation()
		throws Exception {

		Role role = testPostOrganizationRoleUserAccountAssociation_addRole();

		assertHttpResponseStatusCode(
			204,
			roleResource.postOrganizationRoleUserAccountAssociationHttpResponse(
				role.getId(), _user.getUserId(),
				_organization.getOrganizationId()));

		assertHttpResponseStatusCode(
			404,
			roleResource.postOrganizationRoleUserAccountAssociationHttpResponse(
				0L, _user.getUserId(), _organization.getOrganizationId()));

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME_EXCEPTION_MAPPER, LoggerTestUtil.ERROR)) {

			assertHttpResponseStatusCode(
				500,
				roleResource.
					postOrganizationRoleUserAccountAssociationHttpResponse(
						_getRoleId(_addRole(true, RoleConstants.TYPE_REGULAR)),
						_user.getUserId(), _organization.getOrganizationId()));
			assertHttpResponseStatusCode(
				500,
				roleResource.
					postOrganizationRoleUserAccountAssociationHttpResponse(
						_getRoleId(_addRole(true, RoleConstants.TYPE_SITE)),
						_user.getUserId(), _organization.getOrganizationId()));
		}
	}

	@LazyReferencing
	@Override
	@Test
	public void testPostRole() throws Exception {
		super.testPostRole();

		_testPostRoleBatch();
		_testPostRoleWithInvalidPrimaryKey();
		_testPostRoleWithPermissions();
		_testPostRoleWithRolePermissions();
		_testPostRoleWithSubtype();
	}

	@Override
	@Test
	public void testPostRoleByExternalReferenceCodeUserAccountAssociation()
		throws Exception {

		Role role =
			testPostRoleByExternalReferenceCodeUserAccountAssociation_addRole();

		Assert.assertEquals(
			0, _roleLocalService.getAssigneesTotal(role.getId()));

		assertHttpResponseStatusCode(
			204,
			roleResource.
				postRoleByExternalReferenceCodeUserAccountAssociationHttpResponse(
					role.getExternalReferenceCode(), _user.getUserId()));

		Assert.assertEquals(
			1, _roleLocalService.getAssigneesTotal(role.getId()));

		assertHttpResponseStatusCode(
			404,
			roleResource.
				postRoleByExternalReferenceCodeUserAccountAssociationHttpResponse(
					RandomTestUtil.randomString(), _user.getUserId()));

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME_EXCEPTION_MAPPER, LoggerTestUtil.ERROR)) {

			assertHttpResponseStatusCode(
				500,
				roleResource.
					postRoleByExternalReferenceCodeUserAccountAssociationHttpResponse(
						_getRoleExternalReferenceCode(
							_addRole(true, RoleConstants.TYPE_ORGANIZATION)),
						_user.getUserId()));
			assertHttpResponseStatusCode(
				500,
				roleResource.
					postRoleByExternalReferenceCodeUserAccountAssociationHttpResponse(
						_getRoleExternalReferenceCode(
							_addRole(true, RoleConstants.TYPE_SITE)),
						_user.getUserId()));
		}
	}

	@Override
	@Test
	public void testPostRoleUserAccountAssociation() throws Exception {
		Role role = testPostRoleUserAccountAssociation_addRole();

		Assert.assertEquals(
			0, _roleLocalService.getAssigneesTotal(role.getId()));

		assertHttpResponseStatusCode(
			204,
			roleResource.postRoleUserAccountAssociationHttpResponse(
				role.getId(), _user.getUserId()));

		Assert.assertEquals(
			1, _roleLocalService.getAssigneesTotal(role.getId()));

		assertHttpResponseStatusCode(
			404,
			roleResource.postRoleUserAccountAssociationHttpResponse(
				0L, _user.getUserId()));

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME_EXCEPTION_MAPPER, LoggerTestUtil.ERROR)) {

			assertHttpResponseStatusCode(
				500,
				roleResource.postRoleUserAccountAssociationHttpResponse(
					_getRoleId(_addRole(true, RoleConstants.TYPE_ORGANIZATION)),
					_user.getUserId()));
			assertHttpResponseStatusCode(
				500,
				roleResource.postRoleUserAccountAssociationHttpResponse(
					_getRoleId(_addRole(true, RoleConstants.TYPE_SITE)),
					_user.getUserId()));
		}
	}

	@Override
	@Test
	public void testPostSiteRoleByExternalReferenceCodeUserAccountAssociation()
		throws Exception {

		Role role =
			testPostSiteRoleByExternalReferenceCodeUserAccountAssociation_addRole();

		assertHttpResponseStatusCode(
			204,
			roleResource.
				postSiteRoleByExternalReferenceCodeUserAccountAssociationHttpResponse(
					role.getExternalReferenceCode(), _user.getUserId(),
					testGroup.getGroupId()));

		assertHttpResponseStatusCode(
			404,
			roleResource.
				postSiteRoleByExternalReferenceCodeUserAccountAssociationHttpResponse(
					RandomTestUtil.randomString(), _user.getUserId(),
					testGroup.getGroupId()));

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME_EXCEPTION_MAPPER, LoggerTestUtil.ERROR)) {

			assertHttpResponseStatusCode(
				500,
				roleResource.
					postSiteRoleByExternalReferenceCodeUserAccountAssociationHttpResponse(
						_getRoleExternalReferenceCode(
							_addRole(true, RoleConstants.TYPE_REGULAR)),
						_user.getUserId(), testGroup.getGroupId()));
			assertHttpResponseStatusCode(
				500,
				roleResource.
					postSiteRoleByExternalReferenceCodeUserAccountAssociationHttpResponse(
						_getRoleExternalReferenceCode(
							_addRole(true, RoleConstants.TYPE_ORGANIZATION)),
						_user.getUserId(), testGroup.getGroupId()));
		}
	}

	@Override
	@Test
	public void testPostSiteRoleUserAccountAssociation() throws Exception {
		Role role = testPostSiteRoleUserAccountAssociation_addRole();

		assertHttpResponseStatusCode(
			204,
			roleResource.postSiteRoleUserAccountAssociationHttpResponse(
				role.getId(), _user.getUserId(), testGroup.getGroupId()));

		assertHttpResponseStatusCode(
			404,
			roleResource.postSiteRoleUserAccountAssociationHttpResponse(
				0L, _user.getUserId(), testGroup.getGroupId()));

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME_EXCEPTION_MAPPER, LoggerTestUtil.ERROR)) {

			assertHttpResponseStatusCode(
				500,
				roleResource.postSiteRoleUserAccountAssociationHttpResponse(
					_getRoleId(_addRole(true, RoleConstants.TYPE_REGULAR)),
					_user.getUserId(), testGroup.getGroupId()));
			assertHttpResponseStatusCode(
				500,
				roleResource.postSiteRoleUserAccountAssociationHttpResponse(
					_getRoleId(_addRole(true, RoleConstants.TYPE_ORGANIZATION)),
					_user.getUserId(), testGroup.getGroupId()));
		}
	}

	@Override
	@Test
	public void testPutRole() throws Exception {
		super.testPutRole();

		_testPutRoleWithPermissions();
		_testPutRoleWithRolePermissions();
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"externalReferenceCode", "name", "subtype"};
	}

	@Override
	protected String[] getIgnoredEntityFieldNames() {
		return new String[] {"description", "title"};
	}

	@Override
	protected Role randomRole() throws Exception {
		Role role = super.randomRole();

		role.setRoleType(
			RoleConstants.getTypeLabel(
				RoleConstants.TYPES_ORGANIZATION_AND_REGULAR_AND_SITE
					[RandomTestUtil.randomInt(0, 2)]));

		return role;
	}

	@Override
	protected Role
			testDeleteOrganizationRoleByExternalReferenceCodeUserAccountAssociation_addRole()
		throws Exception {

		return _addRole(true, RoleConstants.TYPE_ORGANIZATION);
	}

	@Override
	protected Long
			testDeleteOrganizationRoleByExternalReferenceCodeUserAccountAssociation_getOrganizationId()
		throws Exception {

		return _organization.getOrganizationId();
	}

	@Override
	protected Long
			testDeleteOrganizationRoleByExternalReferenceCodeUserAccountAssociation_getUserAccountId()
		throws Exception {

		return _user.getUserId();
	}

	@Override
	protected Role testDeleteOrganizationRoleUserAccountAssociation_addRole()
		throws Exception {

		return _addRole(true, RoleConstants.TYPE_ORGANIZATION);
	}

	@Override
	protected Long
			testDeleteOrganizationRoleUserAccountAssociation_getOrganizationId()
		throws Exception {

		return _organization.getOrganizationId();
	}

	@Override
	protected Long
			testDeleteOrganizationRoleUserAccountAssociation_getUserAccountId()
		throws Exception {

		return _user.getUserId();
	}

	@Override
	protected Role testDeleteRole_addRole() throws Exception {
		return _addRole(true, RoleConstants.TYPE_REGULAR);
	}

	@Override
	protected Role testDeleteRoleByExternalReferenceCode_addRole()
		throws Exception {

		return _addRole(true, RoleConstants.TYPE_REGULAR);
	}

	@Override
	protected Role
			testDeleteRoleByExternalReferenceCodeUserAccountAssociation_addRole()
		throws Exception {

		return _addRole(true, RoleConstants.TYPE_REGULAR);
	}

	@Override
	protected Long
		testDeleteRoleByExternalReferenceCodeUserAccountAssociation_getUserAccountId() {

		return _user.getUserId();
	}

	@Override
	protected Role testDeleteRoleUserAccountAssociation_addRole()
		throws Exception {

		return _addRole(true, RoleConstants.TYPE_REGULAR);
	}

	@Override
	protected Long testDeleteRoleUserAccountAssociation_getUserAccountId()
		throws Exception {

		return _user.getUserId();
	}

	@Override
	protected Role
			testDeleteSiteRoleByExternalReferenceCodeUserAccountAssociation_addRole()
		throws Exception {

		return _addRole(true, RoleConstants.TYPE_SITE);
	}

	@Override
	protected Long
			testDeleteSiteRoleByExternalReferenceCodeUserAccountAssociation_getUserAccountId()
		throws Exception {

		return _user.getUserId();
	}

	@Override
	protected Role testDeleteSiteRoleUserAccountAssociation_addRole()
		throws Exception {

		return _addRole(true, RoleConstants.TYPE_SITE);
	}

	@Override
	protected Long testDeleteSiteRoleUserAccountAssociation_getUserAccountId()
		throws Exception {

		return _user.getUserId();
	}

	@Override
	protected Role testGetRole_addRole() throws Exception {
		return _addRole(true, randomRole());
	}

	@Override
	protected Role testGetRoleByExternalReferenceCode_addRole()
		throws Exception {

		return _addRole(true, RoleConstants.TYPE_REGULAR);
	}

	@Override
	protected Role testGetRolesPage_addRole(Role role) throws Exception {
		return _addRole(true, role);
	}

	@Override
	protected Long
			testGraphQLDeleteOrganizationRoleByExternalReferenceCodeUserAccountAssociation_getOrganizationId()
		throws Exception {

		return _organization.getOrganizationId();
	}

	@Override
	protected Long
			testGraphQLDeleteOrganizationRoleByExternalReferenceCodeUserAccountAssociation_getUserAccountId()
		throws Exception {

		return _user.getUserId();
	}

	@Override
	protected Long
			testGraphQLDeleteOrganizationRoleUserAccountAssociation_getOrganizationId()
		throws Exception {

		return _organization.getOrganizationId();
	}

	@Override
	protected Long
			testGraphQLDeleteOrganizationRoleUserAccountAssociation_getUserAccountId()
		throws Exception {

		return _user.getUserId();
	}

	@Override
	protected Long
		testGraphQLDeleteRoleByExternalReferenceCodeUserAccountAssociation_getUserAccountId() {

		return _user.getUserId();
	}

	@Override
	protected Long
			testGraphQLDeleteRoleUserAccountAssociation_getUserAccountId()
		throws Exception {

		return _user.getUserId();
	}

	@Override
	protected Role
			testGraphQLDeleteSiteRoleByExternalReferenceCodeUserAccountAssociation_addRole()
		throws Exception {

		return _addRole(true, RoleConstants.TYPE_SITE);
	}

	@Override
	protected Long
			testGraphQLDeleteSiteRoleByExternalReferenceCodeUserAccountAssociation_getUserAccountId()
		throws Exception {

		return _user.getUserId();
	}

	@Override
	protected Role testGraphQLDeleteSiteRoleUserAccountAssociation_addRole()
		throws Exception {

		return _addRole(true, RoleConstants.TYPE_SITE);
	}

	@Override
	protected Long
			testGraphQLDeleteSiteRoleUserAccountAssociation_getUserAccountId()
		throws Exception {

		return _user.getUserId();
	}

	@Override
	protected Role testGraphQLRole_addRole() throws Exception {
		return _addRole(true, RoleConstants.TYPE_ORGANIZATION);
	}

	@Override
	protected Role testPatchRole_addRole() throws Exception {
		return _addRole(true, RoleConstants.TYPE_REGULAR);
	}

	@Override
	protected Role testPatchRoleByExternalReferenceCode_addRole()
		throws Exception {

		return _addRole(true, RoleConstants.TYPE_REGULAR);
	}

	@Override
	protected Role
			testPostOrganizationRoleByExternalReferenceCodeUserAccountAssociation_addRole()
		throws Exception {

		return _addRole(true, RoleConstants.TYPE_ORGANIZATION);
	}

	@Override
	protected Role testPostOrganizationRoleUserAccountAssociation_addRole()
		throws Exception {

		return _addRole(true, RoleConstants.TYPE_ORGANIZATION);
	}

	@Override
	protected Role testPostRole_addRole(Role role) throws Exception {
		role.setRoleType(
			RoleConstants.getTypeLabel(RoleConstants.TYPE_REGULAR));

		return _addRole(true, role);
	}

	@Override
	protected Role
			testPostRoleByExternalReferenceCodeUserAccountAssociation_addRole()
		throws Exception {

		return _addRole(false, RoleConstants.TYPE_REGULAR);
	}

	@Override
	protected Role testPostRoleUserAccountAssociation_addRole()
		throws Exception {

		return _addRole(false, RoleConstants.TYPE_REGULAR);
	}

	@Override
	protected Role
			testPostSiteRoleByExternalReferenceCodeUserAccountAssociation_addRole()
		throws Exception {

		return _addRole(true, RoleConstants.TYPE_SITE);
	}

	@Override
	protected Role testPostSiteRoleUserAccountAssociation_addRole()
		throws Exception {

		return _addRole(true, RoleConstants.TYPE_SITE);
	}

	@Override
	protected Role testPutRole_addRole() throws Exception {
		return _addRole(true, RoleConstants.TYPE_REGULAR);
	}

	@Override
	protected Role testPutRoleByExternalReferenceCode_addRole()
		throws Exception {

		return _addRole(true, RoleConstants.TYPE_REGULAR);
	}

	private Role _addRole(boolean associateUser, int type) throws Exception {
		Role role = randomRole();

		role.setRoleType(RoleConstants.getTypeLabel(type));

		return _addRole(associateUser, role);
	}

	private Role _addRole(boolean associateUser, Role role) throws Exception {
		_roleLocalService.deleteUserRole(
			_user.getUserId(),
			_roleLocalService.getRole(
				testGroup.getCompanyId(), RoleConstants.USER));

		com.liferay.portal.kernel.model.Role serviceBuilderRole =
			_roleLocalService.addRole(
				role.getExternalReferenceCode(), _user.getUserId(), null, 0,
				role.getName(), null, null,
				RoleConstants.getLabelType(role.getRoleType()),
				role.getSubtype(),
				new ServiceContext() {
					{
						setCompanyId(testCompany.getCompanyId());
						setUserId(_user.getUserId());
					}
				});

		if (associateUser) {
			_roleLocalService.addUserRole(
				_user.getUserId(), serviceBuilderRole);
		}

		return _toRole(serviceBuilderRole.getName(), serviceBuilderRole);
	}

	private User _addUser(
			com.liferay.portal.kernel.model.Role serviceBuilderRole1,
			String... actionIds)
		throws Exception {

		com.liferay.portal.kernel.model.Role serviceBuilderRole2 =
			RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);
		User user = UserTestUtil.addUser(testCompany, _PASSWORD);

		_roleLocalService.addUserRole(
			user.getUserId(), serviceBuilderRole2.getRoleId());

		_resourcePermissionLocalService.setResourcePermissions(
			testCompany.getCompanyId(), PortletKeys.PORTAL,
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(testCompany.getCompanyId()),
			serviceBuilderRole2.getRoleId(),
			new String[] {ActionKeys.ADD_ROLE});
		_resourcePermissionLocalService.setResourcePermissions(
			testCompany.getCompanyId(),
			com.liferay.portal.kernel.model.Role.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(serviceBuilderRole1.getRoleId()),
			serviceBuilderRole2.getRoleId(), actionIds);

		return user;
	}

	private void _assertProblemException(
			UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		try {
			unsafeRunnable.run();

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("FORBIDDEN", problem.getStatus());
		}
	}

	private String _getRoleExternalReferenceCode(Role role) {
		return role.getExternalReferenceCode();
	}

	private long _getRoleId(Role role) {
		return role.getId();
	}

	private RoleResource _getRoleResource(String password, User user) {
		return RoleResource.builder(
		).authentication(
			user.getEmailAddress(), password
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).locale(
			LocaleUtil.getDefault()
		).build();
	}

	private long _getUserRoleId() throws Exception {
		com.liferay.portal.kernel.model.Role serviceBuilderRole =
			_roleLocalService.getRole(
				testCompany.getCompanyId(), RoleConstants.USER);

		return serviceBuilderRole.getRoleId();
	}

	private boolean _hasResourcePermission(
			String actionId, String name, String primKey, long roleId,
			int scope)
		throws Exception {

		return _resourcePermissionLocalService.hasResourcePermission(
			testCompany.getCompanyId(), name, scope, primKey, roleId, actionId);
	}

	private void _setResourcePermissions(
			com.liferay.portal.kernel.model.Role serviceBuilderRole1,
			com.liferay.portal.kernel.model.Role serviceBuilderRole2,
			String... actionIds)
		throws Exception {

		_resourcePermissionLocalService.setResourcePermissions(
			testCompany.getCompanyId(),
			com.liferay.portal.kernel.model.Role.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(serviceBuilderRole1.getRoleId()),
			serviceBuilderRole2.getRoleId(), actionIds);
	}

	private void _testGetRolesPage() throws Exception {
		Page<Role> page = roleResource.getRolesPage(
			null, null, null, Pagination.of(1, 100));

		List<Role> roles = new ArrayList<>(page.getItems());

		Role role1 = _addRole(true, randomRole());

		roles.add(role1);

		Role role2 = _addRole(true, randomRole());

		roles.add(role2);

		page = roleResource.getRolesPage(
			null, null, null, Pagination.of(1, roles.size()));

		Assert.assertEquals(roles.size(), page.getTotalCount());

		assertEqualsIgnoringOrder(roles, (List<Role>)page.getItems());
		assertValid(page);

		page = roleResource.getRolesPage(
			role1.getName(), null, null, Pagination.of(1, roles.size()));

		roles = (List<Role>)page.getItems();

		assertEquals(role1, roles.get(0));
	}

	private void _testGetRolesPageAsGuest() throws Exception {
		RoleResource roleResource = RoleResource.builder(
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).locale(
			LocaleUtil.getDefault()
		).build();

		assertHttpResponseStatusCode(
			403,
			roleResource.getRolesPageHttpResponse(
				null, null, null, Pagination.of(1, 100)));
	}

	private void _testGetRolesPageWithFilter() throws Exception {
		Page<Role> page = roleResource.getRolesPage(
			null, null, null, Pagination.of(1, 100));

		long totalCount = page.getTotalCount();

		// Sleep for 1 second to ensure that role 1 and existing roles are
		// created 1 second apart

		Thread.sleep(1000);

		Role role1 = _addRole(false, randomRole());

		// Sleep for 1 second to ensure that role 1 and role 2 are created 1
		// second apart

		Thread.sleep(1000);

		Role role2 = _addRole(false, randomRole());

		DateFormat dateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");

		page = roleResource.getRolesPage(
			null, null,
			"dateCreated lt " + dateFormat.format(role1.getDateCreated()),
			Pagination.of(1, 2));

		Assert.assertEquals(totalCount, page.getTotalCount());

		page = roleResource.getRolesPage(
			null, null,
			"dateCreated ge " + dateFormat.format(role1.getDateCreated()),
			Pagination.of(1, 2));

		Assert.assertEquals(2, page.getTotalCount());

		// Sleep for 1 second to ensure that role 1 and role 2 are modified 1
		// second apart

		Thread.sleep(1000);

		role1.setDescription(
			StringUtil.toLowerCase(RandomTestUtil.randomString()));

		role1 = roleResource.patchRole(role1.getId(), role1);

		page = roleResource.getRolesPage(
			null, null,
			"dateModified ge " + dateFormat.format(role1.getDateModified()),
			Pagination.of(1, 2));

		Assert.assertEquals(1, page.getTotalCount());

		assertContains(role1, (List<Role>)page.getItems());

		page = roleResource.getRolesPage(
			null, null,
			"dateModified lt " + dateFormat.format(role1.getDateModified()),
			Pagination.of(1, 100));

		Assert.assertEquals(totalCount + 1, page.getTotalCount());

		assertContains(role2, (List<Role>)page.getItems());
	}

	private void _testGetRolesPageWithRolePermissions() throws Exception {
		User user = UserTestUtil.addUser(testCompany, _PASSWORD);

		RoleResource roleResource = _getRoleResource(_PASSWORD, user);

		com.liferay.portal.kernel.model.Role serviceBuilderRole1 =
			RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_resourcePermissionLocalService.setResourcePermissions(
			testCompany.getCompanyId(), Organization.class.getName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(testCompany.getCompanyId()),
			serviceBuilderRole1.getRoleId(), new String[] {ActionKeys.VIEW});

		com.liferay.portal.kernel.model.Role serviceBuilderRole2 =
			RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_setResourcePermissions(
			serviceBuilderRole1, serviceBuilderRole2, ActionKeys.VIEW);

		_roleLocalService.addUserRole(
			user.getUserId(), serviceBuilderRole2.getRoleId());

		Page<Role> page = roleResource.getRolesPage(
			serviceBuilderRole1.getName(), null, null, Pagination.of(1, 100));

		Role role = page.fetchFirstItem();

		Assert.assertTrue(ArrayUtil.isEmpty(role.getRolePermissions()));

		_setResourcePermissions(
			serviceBuilderRole1, serviceBuilderRole2, ActionKeys.VIEW,
			ActionKeys.PERMISSIONS);

		page = roleResource.getRolesPage(
			serviceBuilderRole1.getName(), null, null, Pagination.of(1, 100));

		role = page.fetchFirstItem();

		Assert.assertTrue(ArrayUtil.isEmpty(role.getRolePermissions()));

		_setResourcePermissions(
			serviceBuilderRole1, serviceBuilderRole2, ActionKeys.VIEW,
			ActionKeys.PERMISSIONS, ActionKeys.UPDATE);

		page = roleResource.getRolesPage(
			serviceBuilderRole1.getName(), null, null, Pagination.of(1, 100));

		role = page.fetchFirstItem();

		Assert.assertTrue(ArrayUtil.isEmpty(role.getRolePermissions()));

		_setResourcePermissions(
			serviceBuilderRole1, serviceBuilderRole2, ActionKeys.VIEW,
			ActionKeys.PERMISSIONS, ActionKeys.UPDATE,
			ActionKeys.DEFINE_PERMISSIONS);

		page = roleResource.getRolesPage(
			serviceBuilderRole1.getName(), null, null, Pagination.of(1, 100));

		role = page.fetchFirstItem();

		Assert.assertFalse(ArrayUtil.isEmpty(role.getRolePermissions()));
	}

	private void _testGetRolesPageWithType() throws Exception {
		String prefix = RandomTestUtil.randomString();

		AccountEntry accountEntry = _accountEntryLocalService.addAccountEntry(
			StringPool.BLANK, _user.getUserId(),
			AccountConstants.PARENT_ACCOUNT_ENTRY_ID_DEFAULT,
			RandomTestUtil.randomString(), null, null,
			RandomTestUtil.randomString() + "@liferay.com", null, null,
			AccountConstants.ACCOUNT_ENTRY_TYPE_GUEST,
			WorkflowConstants.STATUS_APPROVED,
			ServiceContextTestUtil.getServiceContext(
				testCompany.getCompanyId(), testGroup.getGroupId(),
				_user.getUserId()));

		_accountRoleLocalService.addAccountRole(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			accountEntry.getAccountEntryId(),
			prefix + " " + RandomTestUtil.randomString(), null, null);

		AccountRole accountRole = _accountRoleLocalService.addAccountRole(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT,
			prefix + " " + RandomTestUtil.randomString(), null, null);

		com.liferay.portal.kernel.model.Role serviceBuilderRole =
			_roleLocalService.addRole(
				RandomTestUtil.randomString(), _user.getUserId(), null, 0,
				prefix + " " + RandomTestUtil.randomString(), null, null,
				RoleConstants.TYPE_REGULAR, null,
				ServiceContextTestUtil.getServiceContext(
					testCompany.getCompanyId(), testGroup.getGroupId(),
					_user.getUserId()));

		Page<Role> page = roleResource.getRolesPage(
			null, null, "contains(name,'" + prefix + "')",
			Pagination.of(1, 10));

		Assert.assertEquals(1, page.getTotalCount());
		Assert.assertTrue(
			ListUtil.exists(
				(List<Role>)page.getItems(),
				role -> role.getId() == serviceBuilderRole.getRoleId()));

		page = roleResource.getRolesPage(
			null, new Integer[] {6}, "contains(name,'" + prefix + "')",
			Pagination.of(1, 10));

		Assert.assertEquals(1, page.getTotalCount());
		Assert.assertTrue(
			ListUtil.exists(
				(List<Role>)page.getItems(),
				role -> role.getId() == accountRole.getRoleId()));

		page = roleResource.getRolesPage(
			null, new Integer[] {1, 6}, "contains(name,'" + prefix + "')",
			Pagination.of(1, 10));

		Assert.assertEquals(2, page.getTotalCount());
		Assert.assertTrue(
			ListUtil.exists(
				(List<Role>)page.getItems(),
				role -> role.getId() == accountRole.getRoleId()));
		Assert.assertTrue(
			ListUtil.exists(
				(List<Role>)page.getItems(),
				role -> role.getId() == serviceBuilderRole.getRoleId()));
	}

	private void _testGetRoleVisibility() throws Exception {
		RoleResource roleResource = _getRoleResource(
			_PASSWORD, UserTestUtil.addUser(testCompany, _PASSWORD));

		com.liferay.portal.kernel.model.Role serviceBuilderRole1 =
			_roleLocalService.getRole(
				testCompany.getCompanyId(), RoleConstants.GUEST);

		Assert.assertNotNull(
			roleResource.getRole(serviceBuilderRole1.getRoleId()));

		com.liferay.portal.kernel.model.Role serviceBuilderRole2 =
			RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		assertHttpResponseStatusCode(
			404,
			roleResource.getRoleHttpResponse(serviceBuilderRole2.getRoleId()));
	}

	private void _testGetRoleWithNestedFields() throws Exception {
		Role postRole = testGetRole_addRole();

		com.liferay.portal.kernel.model.Role role = RoleTestUtil.addRole(
			RoleConstants.TYPE_REGULAR);

		_resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(),
			com.liferay.portal.kernel.model.Role.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(postRole.getId()), role.getRoleId(),
			new String[] {ActionKeys.DELETE});

		RoleResource roleResource = RoleResource.builder(
		).authentication(
			"test@liferay.com", PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).locale(
			LocaleUtil.getDefault()
		).parameters(
			"nestedFields", "permissions"
		).build();

		Role getRole = roleResource.getRole(postRole.getId());

		Assert.assertTrue(
			ArrayUtil.exists(
				getRole.getPermissions(),
				permission ->
					Objects.equals(permission.getRoleName(), role.getName()) &&
					(permission.getActionIds().length == 1) &&
					Objects.equals(permission.getActionIds()[0], "DELETE")));
	}

	private void _testGetRoleWithRolePermissions() throws Exception {
		User user = UserTestUtil.addUser(testCompany, _PASSWORD);

		RoleResource roleResource = _getRoleResource(_PASSWORD, user);

		com.liferay.portal.kernel.model.Role serviceBuilderRole1 =
			RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_resourcePermissionLocalService.setResourcePermissions(
			testCompany.getCompanyId(), Organization.class.getName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(testCompany.getCompanyId()),
			serviceBuilderRole1.getRoleId(), new String[] {ActionKeys.VIEW});

		com.liferay.portal.kernel.model.Role serviceBuilderRole2 =
			RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_setResourcePermissions(
			serviceBuilderRole1, serviceBuilderRole2, ActionKeys.VIEW);

		_roleLocalService.addUserRole(
			user.getUserId(), serviceBuilderRole2.getRoleId());

		Role role = roleResource.getRole(serviceBuilderRole1.getRoleId());

		Assert.assertTrue(ArrayUtil.isEmpty(role.getRolePermissions()));

		_setResourcePermissions(
			serviceBuilderRole1, serviceBuilderRole2, ActionKeys.VIEW,
			ActionKeys.PERMISSIONS);

		role = roleResource.getRole(serviceBuilderRole1.getRoleId());

		Assert.assertTrue(ArrayUtil.isEmpty(role.getRolePermissions()));

		_setResourcePermissions(
			serviceBuilderRole1, serviceBuilderRole2, ActionKeys.VIEW,
			ActionKeys.PERMISSIONS, ActionKeys.UPDATE);

		role = roleResource.getRole(serviceBuilderRole1.getRoleId());

		Assert.assertTrue(ArrayUtil.isEmpty(role.getRolePermissions()));

		_setResourcePermissions(
			serviceBuilderRole1, serviceBuilderRole2, ActionKeys.VIEW,
			ActionKeys.PERMISSIONS, ActionKeys.UPDATE,
			ActionKeys.DEFINE_PERMISSIONS);

		role = roleResource.getRole(serviceBuilderRole1.getRoleId());

		Assert.assertFalse(ArrayUtil.isEmpty(role.getRolePermissions()));
	}

	private void _testPatchRoleWithPermissions() throws Exception {
		com.liferay.portal.kernel.model.Role serviceBuilderRole1 =
			RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		RoleResource roleResource1 = _getRoleResource(
			_PASSWORD,
			_addUser(serviceBuilderRole1, ActionKeys.UPDATE, ActionKeys.VIEW));

		com.liferay.portal.kernel.model.Role serviceBuilderRole2 =
			RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_resourcePermissionLocalService.setResourcePermissions(
			testCompany.getCompanyId(),
			com.liferay.portal.kernel.model.Role.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(serviceBuilderRole2.getRoleId()), _getUserRoleId(),
			new String[] {ActionKeys.VIEW});

		Role role = _toRole(serviceBuilderRole1.getName(), serviceBuilderRole1);

		role.setPermissions(
			new Permission[] {_toPermission(serviceBuilderRole2)});

		_assertProblemException(
			() -> roleResource1.patchRole(
				serviceBuilderRole1.getRoleId(), role));

		RoleResource roleResource2 = _getRoleResource(
			_PASSWORD,
			_addUser(
				serviceBuilderRole1, ActionKeys.DEFINE_PERMISSIONS,
				ActionKeys.UPDATE, ActionKeys.VIEW));

		_assertProblemException(
			() -> roleResource2.patchRole(
				serviceBuilderRole1.getRoleId(), role));

		Assert.assertFalse(
			_hasResourcePermission(
				ActionKeys.DEFINE_PERMISSIONS,
				com.liferay.portal.kernel.model.Role.class.getName(),
				String.valueOf(serviceBuilderRole1.getRoleId()),
				serviceBuilderRole2.getRoleId(),
				ResourceConstants.SCOPE_INDIVIDUAL));

		RoleResource roleResource3 = _getRoleResource(
			_PASSWORD,
			_addUser(
				serviceBuilderRole1, ActionKeys.PERMISSIONS, ActionKeys.UPDATE,
				ActionKeys.VIEW));

		roleResource3.patchRole(serviceBuilderRole1.getRoleId(), role);

		Assert.assertTrue(
			_hasResourcePermission(
				ActionKeys.DEFINE_PERMISSIONS,
				com.liferay.portal.kernel.model.Role.class.getName(),
				String.valueOf(serviceBuilderRole1.getRoleId()),
				serviceBuilderRole2.getRoleId(),
				ResourceConstants.SCOPE_INDIVIDUAL));
	}

	private void _testPatchRoleWithRolePermissions() throws Exception {
		com.liferay.portal.kernel.model.Role serviceBuilderRole =
			RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		RoleResource roleResource1 = _getRoleResource(
			_PASSWORD,
			_addUser(serviceBuilderRole, ActionKeys.UPDATE, ActionKeys.VIEW));

		Role role = _toRole(serviceBuilderRole.getName(), serviceBuilderRole);

		role.setRolePermissions(new RolePermission[] {_toRolePermission()});

		_assertProblemException(
			() -> roleResource1.patchRole(
				serviceBuilderRole.getRoleId(), role));

		RoleResource roleResource2 = _getRoleResource(
			_PASSWORD,
			_addUser(
				serviceBuilderRole, ActionKeys.DEFINE_PERMISSIONS,
				ActionKeys.UPDATE, ActionKeys.VIEW));

		_assertProblemException(
			() -> roleResource2.patchRole(
				serviceBuilderRole.getRoleId(), role));

		Assert.assertFalse(
			_hasResourcePermission(
				ActionKeys.VIEW, Layout.class.getName(),
				String.valueOf(testCompany.getCompanyId()),
				serviceBuilderRole.getRoleId(),
				ResourceConstants.SCOPE_COMPANY));

		RoleResource roleResource3 = _getRoleResource(
			_PASSWORD,
			_addUser(
				serviceBuilderRole, ActionKeys.PERMISSIONS, ActionKeys.UPDATE,
				ActionKeys.VIEW));

		roleResource3.patchRole(serviceBuilderRole.getRoleId(), role);

		Assert.assertTrue(
			_hasResourcePermission(
				ActionKeys.VIEW, Layout.class.getName(),
				String.valueOf(testCompany.getCompanyId()),
				serviceBuilderRole.getRoleId(),
				ResourceConstants.SCOPE_COMPANY));
	}

	private void _testPostRoleBatch() throws Exception {
		Role role = randomRole();

		com.liferay.portal.kernel.model.Role serviceBuilderRole1 =
			RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		Permission permission1 = new Permission() {
			{
				actionIds = new String[] {ActionKeys.VIEW};
				roleExternalReferenceCode =
					serviceBuilderRole1.getExternalReferenceCode();
				roleName = serviceBuilderRole1.getName();
				roleType = RoleConstants.getTypeLabel(
					serviceBuilderRole1.getType());
			}
		};

		Permission permission2 = new Permission() {
			{
				actionIds = new String[] {ActionKeys.UPDATE};
				roleExternalReferenceCode = RandomTestUtil.randomString();
				roleName = RandomTestUtil.randomString();
				roleType = RoleConstants.getTypeLabel(
					RoleConstants.TYPE_REGULAR);
			}
		};

		role.setPermissions(new Permission[] {permission1, permission2});

		waitForFinish(
			"COMPLETED",
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					"items",
					JSONUtil.put(_jsonFactory.createJSONObject(role.toString()))
				).toString(),
				"headless-admin-user/v1.0/roles/batch", Http.Method.POST));

		com.liferay.portal.kernel.model.Role serviceBuilderRole2 =
			_roleLocalService.fetchRoleByExternalReferenceCode(
				permission1.getRoleExternalReferenceCode(),
				TestPropsValues.getCompanyId());

		com.liferay.portal.kernel.model.Role createdRole =
			_roleLocalService.fetchRoleByExternalReferenceCode(
				role.getExternalReferenceCode(),
				TestPropsValues.getCompanyId());

		List<com.liferay.portal.vulcan.permission.Permission> permissions =
			ListUtil.fromCollection(
				PermissionUtil.getPermissions(
					TestPropsValues.getCompanyId(),
					_resourceActionLocalService.getResourceActions(
						com.liferay.portal.kernel.model.Role.class.getName()),
					createdRole.getRoleId(),
					com.liferay.portal.kernel.model.Role.class.getName(),
					null));

		Assert.assertTrue(
			ListUtil.exists(
				permissions,
				permission -> {
					String[] actionIds = permission.getActionIds();

					return (actionIds.length == 1) &&
						   Objects.equals(ActionKeys.VIEW, actionIds[0]) &&
						   Objects.equals(
							   serviceBuilderRole2.getExternalReferenceCode(),
							   permission.getRoleExternalReferenceCode());
				}));

		Assert.assertEquals(
			serviceBuilderRole1.getRoleId(), serviceBuilderRole2.getRoleId());

		com.liferay.portal.kernel.model.Role serviceBuilderRole3 =
			_roleLocalService.fetchRoleByExternalReferenceCode(
				permission2.getRoleExternalReferenceCode(),
				TestPropsValues.getCompanyId());

		Assert.assertTrue(
			ListUtil.exists(
				permissions,
				permission -> {
					String[] actionIds = permission.getActionIds();

					return (actionIds.length == 1) &&
						   Objects.equals(ActionKeys.UPDATE, actionIds[0]) &&
						   Objects.equals(
							   serviceBuilderRole3.getExternalReferenceCode(),
							   permission.getRoleExternalReferenceCode());
				}));
		Assert.assertEquals(
			permission2.getRoleName(), serviceBuilderRole3.getName());
		Assert.assertEquals(
			RoleConstants.getLabelType(permission2.getRoleType()),
			serviceBuilderRole3.getType());
		Assert.assertEquals(
			WorkflowConstants.STATUS_EMPTY, serviceBuilderRole3.getStatus());
	}

	private void _testPostRoleWithInvalidPrimaryKey() throws Exception {
		Role role = randomRole();

		long primKey = RandomTestUtil.randomLong();

		role.setRolePermissions(
			new RolePermission[] {
				new RolePermission() {
					{
						setActionIds(new String[] {ActionKeys.VIEW});
						setPrimaryKey(String.valueOf(primKey));
						setResourceName(
							com.liferay.portal.kernel.model.Role.class.
								getName());
						setScope((long)ResourceConstants.SCOPE_COMPANY);
					}
				}
			});

		role.setRoleType(
			RoleConstants.getTypeLabel(RoleConstants.TYPE_REGULAR));

		role = roleResource.postRole(role);

		Assert.assertTrue(
			_resourcePermissionLocalService.hasResourcePermission(
				testCompany.getCompanyId(),
				com.liferay.portal.kernel.model.Role.class.getName(),
				ResourceConstants.SCOPE_COMPANY,
				String.valueOf(testCompany.getCompanyId()), role.getId(),
				ActionKeys.VIEW));

		Assert.assertEquals(
			0,
			_resourcePermissionLocalService.getResourcePermissionsCount(
				testCompany.getCompanyId(),
				com.liferay.portal.kernel.model.Role.class.getName(),
				ResourceConstants.SCOPE_COMPANY, String.valueOf(primKey)));
	}

	private void _testPostRoleWithPermissions() throws Exception {
		com.liferay.portal.kernel.model.Role serviceBuilderRole1 =
			RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		RoleResource roleResource1 = _getRoleResource(
			_PASSWORD,
			_addUser(serviceBuilderRole1, ActionKeys.UPDATE, ActionKeys.VIEW));

		com.liferay.portal.kernel.model.Role serviceBuilderRole2 =
			RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_resourcePermissionLocalService.setResourcePermissions(
			testCompany.getCompanyId(),
			com.liferay.portal.kernel.model.Role.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(serviceBuilderRole2.getRoleId()), _getUserRoleId(),
			new String[] {ActionKeys.VIEW});

		Role role1 = randomRole();

		role1.setPermissions(
			new Permission[] {_toPermission(serviceBuilderRole2)});

		_assertProblemException(() -> roleResource1.postRole(role1));

		RoleResource roleResource2 = _getRoleResource(
			_PASSWORD,
			_addUser(
				serviceBuilderRole1, ActionKeys.DEFINE_PERMISSIONS,
				ActionKeys.VIEW));

		_assertProblemException(() -> roleResource2.postRole(role1));

		RoleResource roleResource3 = _getRoleResource(
			_PASSWORD,
			_addUser(
				serviceBuilderRole1, ActionKeys.PERMISSIONS, ActionKeys.VIEW));

		_assertProblemException(() -> roleResource3.postRole(role1));

		Role role2 = roleResource.postRole(role1);

		Assert.assertTrue(
			_hasResourcePermission(
				ActionKeys.DEFINE_PERMISSIONS,
				com.liferay.portal.kernel.model.Role.class.getName(),
				String.valueOf(role2.getId()), serviceBuilderRole2.getRoleId(),
				ResourceConstants.SCOPE_INDIVIDUAL));
	}

	private void _testPostRoleWithRolePermissions() throws Exception {
		com.liferay.portal.kernel.model.Role serviceBuilderRole =
			RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		RoleResource roleResource1 = _getRoleResource(
			_PASSWORD,
			_addUser(serviceBuilderRole, ActionKeys.UPDATE, ActionKeys.VIEW));

		Role role1 = randomRole();

		role1.setRolePermissions(new RolePermission[] {_toRolePermission()});

		_assertProblemException(() -> roleResource1.postRole(role1));

		RoleResource roleResource2 = _getRoleResource(
			_PASSWORD,
			_addUser(
				serviceBuilderRole, ActionKeys.DEFINE_PERMISSIONS,
				ActionKeys.VIEW));

		_assertProblemException(() -> roleResource2.postRole(role1));

		RoleResource roleResource3 = _getRoleResource(
			_PASSWORD,
			_addUser(
				serviceBuilderRole, ActionKeys.PERMISSIONS, ActionKeys.VIEW));

		_assertProblemException(() -> roleResource3.postRole(role1));

		Role role2 = roleResource.postRole(role1);

		Assert.assertTrue(
			_hasResourcePermission(
				ActionKeys.VIEW, Layout.class.getName(),
				String.valueOf(testCompany.getCompanyId()),
				_roleLocalService.fetchRole(
					role2.getId()
				).getRoleId(),
				ResourceConstants.SCOPE_COMPANY));
	}

	private void _testPostRoleWithSubtype() throws Exception {
		Role randomRole = randomRole();

		randomRole.setRoleType(RoleConstants.TYPE_DEPOT_LABEL);
		randomRole.setSubtype(RandomTestUtil.randomString());

		HttpInvoker.HttpResponse httpResponse =
			roleResource.postRoleHttpResponse(randomRole);

		assertHttpResponseStatusCode(400, httpResponse);

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			httpResponse.getContent());

		Assert.assertEquals(
			"The role subtype is invalid", jsonObject.getString("title"));

		randomRole.setSubtype(DepotRolesConstants.SUBTYPE_SPACE);

		Role postRole = testPostRole_addRole(randomRole);

		assertEquals(randomRole, postRole);
		assertValid(postRole);
	}

	private void _testPutRoleWithPermissions() throws Exception {
		com.liferay.portal.kernel.model.Role serviceBuilderRole1 =
			RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		RoleResource roleResource1 = _getRoleResource(
			_PASSWORD,
			_addUser(serviceBuilderRole1, ActionKeys.UPDATE, ActionKeys.VIEW));

		com.liferay.portal.kernel.model.Role serviceBuilderRole2 =
			RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_resourcePermissionLocalService.setResourcePermissions(
			testCompany.getCompanyId(),
			com.liferay.portal.kernel.model.Role.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(serviceBuilderRole2.getRoleId()), _getUserRoleId(),
			new String[] {ActionKeys.VIEW});

		Role role = _toRole(serviceBuilderRole1.getName(), serviceBuilderRole1);

		role.setPermissions(
			new Permission[] {_toPermission(serviceBuilderRole2)});

		_assertProblemException(
			() -> roleResource1.putRole(serviceBuilderRole1.getRoleId(), role));

		RoleResource roleResource2 = _getRoleResource(
			_PASSWORD,
			_addUser(
				serviceBuilderRole1, ActionKeys.DEFINE_PERMISSIONS,
				ActionKeys.UPDATE, ActionKeys.VIEW));

		_assertProblemException(
			() -> roleResource2.putRole(serviceBuilderRole1.getRoleId(), role));

		Assert.assertFalse(
			_hasResourcePermission(
				ActionKeys.DEFINE_PERMISSIONS,
				com.liferay.portal.kernel.model.Role.class.getName(),
				String.valueOf(serviceBuilderRole1.getRoleId()),
				serviceBuilderRole2.getRoleId(),
				ResourceConstants.SCOPE_INDIVIDUAL));

		RoleResource roleResource3 = _getRoleResource(
			_PASSWORD,
			_addUser(
				serviceBuilderRole1, ActionKeys.PERMISSIONS, ActionKeys.UPDATE,
				ActionKeys.VIEW));

		roleResource3.putRole(serviceBuilderRole1.getRoleId(), role);

		Assert.assertTrue(
			_hasResourcePermission(
				ActionKeys.DEFINE_PERMISSIONS,
				com.liferay.portal.kernel.model.Role.class.getName(),
				String.valueOf(serviceBuilderRole1.getRoleId()),
				serviceBuilderRole2.getRoleId(),
				ResourceConstants.SCOPE_INDIVIDUAL));
	}

	private void _testPutRoleWithRolePermissions() throws Exception {
		com.liferay.portal.kernel.model.Role serviceBuilderRole =
			RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		RoleResource roleResource1 = _getRoleResource(
			_PASSWORD,
			_addUser(serviceBuilderRole, ActionKeys.UPDATE, ActionKeys.VIEW));

		Role role = _toRole(serviceBuilderRole.getName(), serviceBuilderRole);

		role.setRolePermissions(new RolePermission[] {_toRolePermission()});

		_assertProblemException(
			() -> roleResource1.putRole(serviceBuilderRole.getRoleId(), role));

		RoleResource roleResource2 = _getRoleResource(
			_PASSWORD,
			_addUser(
				serviceBuilderRole, ActionKeys.DEFINE_PERMISSIONS,
				ActionKeys.UPDATE, ActionKeys.VIEW));

		_assertProblemException(
			() -> roleResource2.putRole(serviceBuilderRole.getRoleId(), role));

		Assert.assertFalse(
			_hasResourcePermission(
				ActionKeys.VIEW, Layout.class.getName(),
				String.valueOf(testCompany.getCompanyId()),
				serviceBuilderRole.getRoleId(),
				ResourceConstants.SCOPE_COMPANY));

		RoleResource roleResource3 = _getRoleResource(
			_PASSWORD,
			_addUser(
				serviceBuilderRole, ActionKeys.PERMISSIONS, ActionKeys.UPDATE,
				ActionKeys.VIEW));

		roleResource3.putRole(serviceBuilderRole.getRoleId(), role);

		Assert.assertTrue(
			_hasResourcePermission(
				ActionKeys.VIEW, Layout.class.getName(),
				String.valueOf(testCompany.getCompanyId()),
				serviceBuilderRole.getRoleId(),
				ResourceConstants.SCOPE_COMPANY));
	}

	private Permission _toPermission(
		com.liferay.portal.kernel.model.Role serviceBuilderRole) {

		return new Permission() {
			{
				actionIds = new String[] {ActionKeys.DEFINE_PERMISSIONS};
				roleExternalReferenceCode =
					serviceBuilderRole.getExternalReferenceCode();
				roleName = serviceBuilderRole.getName();
				roleType = RoleConstants.TYPE_REGULAR_LABEL;
			}
		};
	}

	private Role _toRole(
		String roleName,
		com.liferay.portal.kernel.model.Role serviceBuilderRole) {

		return new Role() {
			{
				dateCreated = serviceBuilderRole.getCreateDate();
				dateModified = serviceBuilderRole.getModifiedDate();
				description = serviceBuilderRole.getDescription();
				externalReferenceCode =
					serviceBuilderRole.getExternalReferenceCode();
				id = serviceBuilderRole.getRoleId();
				name = roleName;
				roleType = RoleConstants.getTypeLabel(
					serviceBuilderRole.getType());
				subtype = serviceBuilderRole.getSubtype();
			}
		};
	}

	private RolePermission _toRolePermission() {
		return new RolePermission() {
			{
				actionIds = new String[] {ActionKeys.VIEW};
				resourceName = Layout.class.getName();
				scope = Long.valueOf(ResourceConstants.SCOPE_COMPANY);
			}
		};
	}

	private static final String _CLASS_NAME_EXCEPTION_MAPPER =
		"com.liferay.portal.vulcan.internal.jaxrs.exception.mapper." +
			"ExceptionMapper";

	private static final String _PASSWORD = RandomTestUtil.randomString();

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	@Inject
	private AccountRoleLocalService _accountRoleLocalService;

	@Inject
	private JSONFactory _jsonFactory;

	private Organization _organization;

	@Inject
	private ResourceActionLocalService _resourceActionLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	private User _user;

}