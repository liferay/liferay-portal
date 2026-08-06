/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.asset.library.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalServiceUtil;
import com.liferay.headless.asset.library.client.dto.v1_0.UserAccount;
import com.liferay.headless.asset.library.client.pagination.Page;
import com.liferay.headless.asset.library.client.pagination.Pagination;
import com.liferay.headless.asset.library.client.problem.Problem;
import com.liferay.headless.asset.library.client.resource.v1_0.UserAccountResource;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.IdEntityField;
import com.liferay.portal.odata.entity.StringEntityField;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Roberto Díaz
 */
@RunWith(Arquillian.class)
public class UserAccountResourceTest extends BaseUserAccountResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_spaceDepotEntry = DepotEntryLocalServiceUtil.addDepotEntry(
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			null, DepotConstants.TYPE_SPACE,
			new ServiceContext() {
				{
					setCompanyId(testCompany.getCompanyId());
					setUserId(TestPropsValues.getUserId());
				}
			});
		_testUser = UserTestUtil.addUser();
	}

	@Ignore
	@Override
	@Test
	public void testBatchEngineDeleteImportTask() {
	}

	@Override
	@Test
	public void testDeleteAssetLibraryUserAccount() throws Exception {
		super.testDeleteAssetLibraryUserAccount();

		_testDeleteAssetLibraryUserAccountWithViewAndAssignMembersPermission();
	}

	@Override
	@Test
	public void testGetAssetLibraryUserAccount() throws Exception {
		super.testGetAssetLibraryUserAccount();

		_testGetAssetLibraryUserAccount(
			testDepotEntryGroup, _getAssetLibraryMemberUserAccountResource(""));

		UserAccountResource cmsAdministratorUserAccountResource =
			_getCMSAdministratorUserAccountResource();

		_assertFailure(
			() -> _testGetAssetLibraryUserAccount(
				testDepotEntryGroup, cmsAdministratorUserAccountResource));

		_testGetAssetLibraryUserAccount(
			_spaceDepotEntry.getGroup(), cmsAdministratorUserAccountResource);
	}

	@Override
	@Test
	public void testGetAssetLibraryUserAccountsPage() throws Exception {
		super.testGetAssetLibraryUserAccountsPage();

		_testGetAssetLibraryUserAccountsPage(
			testDepotEntryGroup, _getAssetLibraryMemberUserAccountResource(""));
		_testGetAssetLibraryUserAccountsPage(
			testDepotEntryGroup,
			_getAssetLibraryMemberUserAccountResource("roles"));

		UserAccountResource cmsAdministratorUserAccountResource =
			_getCMSAdministratorUserAccountResource();

		_assertFailure(
			() -> _testGetAssetLibraryUserAccountsPage(
				testDepotEntryGroup, cmsAdministratorUserAccountResource));

		_testGetAssetLibraryUserAccountsPage(
			_spaceDepotEntry.getGroup(), cmsAdministratorUserAccountResource);

		_testGetAssetLibraryUserAccountsPageWithSortId();
	}

	@Override
	@Test
	public void testPutAssetLibraryUserAccount() throws Exception {
		super.testPutAssetLibraryUserAccount();

		_testPutAssetLibraryUserAccountWithoutAssignMembersPermission();
		_testPutAssetLibraryUserAccountWithViewAndAssignMembersPermission();
	}

	@Override
	protected Collection<EntityField> getEntityFields() {
		return List.of(
			new StringEntityField(
				"externalReferenceCode", locale -> "externalReferenceCode"),
			new StringEntityField("name", locale -> "name"),
			new IdEntityField("id", locale -> "id", locale -> "id"));
	}

	@Override
	protected String[] getIgnoredEntityFieldNames() {
		return new String[] {"externalReferenceCode", "name"};
	}

	@Override
	protected UserAccount randomUserAccount() throws Exception {
		User user = UserTestUtil.addUser();

		return new UserAccount() {
			{
				externalReferenceCode = user.getExternalReferenceCode();
				id = user.getUserId();
				name = user.getFullName();
			}
		};
	}

	@Override
	protected UserAccount testDeleteAssetLibraryUserAccount_addUserAccount()
		throws Exception {

		return _addUserAccount();
	}

	@Override
	protected String
			testDeleteAssetLibraryUserAccount_getAssetLibraryExternalReferenceCode()
		throws Exception {

		return _getGroupExternalReferenceCode();
	}

	@Override
	protected UserAccount testGetAssetLibraryUserAccount_addUserAccount()
		throws Exception {

		return _addUserAccount();
	}

	@Override
	protected UserAccount testGetAssetLibraryUserAccountsPage_addUserAccount(
			String assetLibraryExternalReferenceCode, UserAccount userAccount)
		throws Exception {

		return userAccountResource.putAssetLibraryUserAccount(
			assetLibraryExternalReferenceCode,
			userAccount.getExternalReferenceCode());
	}

	@Override
	protected UserAccount testPutAssetLibraryUserAccount_addUserAccount()
		throws Exception {

		return _addUserAccount();
	}

	@Override
	protected String
			testPutAssetLibraryUserAccount_getAssetLibraryExternalReferenceCode()
		throws Exception {

		return _getGroupExternalReferenceCode();
	}

	private UserAccount _addUserAccount() throws Exception {
		return userAccountResource.putAssetLibraryUserAccount(
			testDepotEntryGroup.getExternalReferenceCode(),
			_testUser.getExternalReferenceCode());
	}

	private void _assertFailure(
		String message, UnsafeRunnable<Exception> unsafeRunnable) {

		AssertUtils.assertFailure(
			Problem.ProblemException.class, message, unsafeRunnable);
	}

	private void _assertFailure(UnsafeRunnable<Exception> unsafeRunnable) {
		_assertFailure(null, unsafeRunnable);
	}

	private UserAccountResource _getAssetLibraryMemberUserAccountResource(
			String nestedFields)
		throws Exception {

		String password = RandomTestUtil.randomString();

		User user = UserTestUtil.addUser(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			password, RandomTestUtil.randomString() + "@liferay.com",
			RandomTestUtil.randomString(), LocaleUtil.getDefault(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			new long[] {testDepotEntry.getGroupId()},
			ServiceContextTestUtil.getServiceContext());

		return UserAccountResource.builder(
		).authentication(
			user.getEmailAddress(), password
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).locale(
			LocaleUtil.getDefault()
		).parameter(
			"nestedFields", nestedFields
		).build();
	}

	private UserAccountResource _getCMSAdministratorUserAccountResource()
		throws Exception {

		String password = RandomTestUtil.randomString();

		User user = UserTestUtil.addUser(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			password, RandomTestUtil.randomString() + "@liferay.com",
			RandomTestUtil.randomString(), LocaleUtil.getDefault(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			ServiceContextTestUtil.getServiceContext());

		Role serviceBuilderRole = RoleTestUtil.addRole(
			RoleConstants.CMS_ADMINISTRATOR, RoleConstants.TYPE_REGULAR);

		_userLocalService.addRoleUser(serviceBuilderRole.getRoleId(), user);

		return UserAccountResource.builder(
		).authentication(
			user.getEmailAddress(), password
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).locale(
			LocaleUtil.getDefault()
		).build();
	}

	private UserAccountResource _getDepotEntryUserAccountResource(
			List<String> actionIds)
		throws Exception {

		String password = RandomTestUtil.randomString();

		User user = UserTestUtil.addUser(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			password, RandomTestUtil.randomString() + "@liferay.com",
			RandomTestUtil.randomString(), LocaleUtil.getDefault(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			ServiceContextTestUtil.getServiceContext());

		Role serviceBuilderRole = RoleTestUtil.addRole(
			RoleConstants.TYPE_REGULAR);

		_userLocalService.addRoleUser(serviceBuilderRole.getRoleId(), user);

		for (String actionId : actionIds) {
			RoleTestUtil.addResourcePermission(
				serviceBuilderRole, DepotEntry.class.getName(),
				ResourceConstants.SCOPE_GROUP,
				String.valueOf(testDepotEntry.getGroupId()), actionId);
		}

		RoleTestUtil.addResourcePermission(
			serviceBuilderRole, User.class.getName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), ActionKeys.VIEW);

		return UserAccountResource.builder(
		).authentication(
			user.getEmailAddress(), password
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).locale(
			LocaleUtil.getDefault()
		).build();
	}

	private String _getGroupExternalReferenceCode() throws Exception {
		Group group = testDepotEntry.getGroup();

		return group.getExternalReferenceCode();
	}

	private UserAccountResource _getViewAndAssignMembersUserAccountResource()
		throws Exception {

		return _getDepotEntryUserAccountResource(
			List.of(ActionKeys.ASSIGN_MEMBERS, ActionKeys.VIEW));
	}

	private UserAccountResource
			_getWithoutAssignMembersPermissionUserAccountResource()
		throws Exception {

		List<String> actionIds = _resourceActions.getModelResourceActions(
			DepotEntry.class.getName());

		actionIds.remove(ActionKeys.ASSIGN_MEMBERS);

		return _getDepotEntryUserAccountResource(actionIds);
	}

	private void _testDeleteAssetLibraryUserAccountWithViewAndAssignMembersPermission()
		throws Exception {

		UserAccount userAccount = randomUserAccount();

		userAccountResource.putAssetLibraryUserAccount(
			testDepotEntryGroup.getExternalReferenceCode(),
			userAccount.getExternalReferenceCode());

		UserAccountResource viewAndAssignMembersUserAccountResource =
			_getViewAndAssignMembersUserAccountResource();

		viewAndAssignMembersUserAccountResource.deleteAssetLibraryUserAccount(
			testDepotEntryGroup.getExternalReferenceCode(),
			userAccount.getExternalReferenceCode());

		assertHttpResponseStatusCode(
			404,
			userAccountResource.getAssetLibraryUserAccountHttpResponse(
				testDepotEntryGroup.getExternalReferenceCode(),
				userAccount.getExternalReferenceCode()));
	}

	private void _testGetAssetLibraryUserAccount(
			Group group, UserAccountResource getUserAccountResource)
		throws Exception {

		UserAccount postUserAccount =
			userAccountResource.putAssetLibraryUserAccount(
				group.getExternalReferenceCode(),
				_testUser.getExternalReferenceCode());

		UserAccount getUserAccount =
			getUserAccountResource.getAssetLibraryUserAccount(
				group.getExternalReferenceCode(),
				postUserAccount.getExternalReferenceCode());

		assertEquals(postUserAccount, getUserAccount);
		assertValid(getUserAccount);
	}

	private void _testGetAssetLibraryUserAccountsPage(
			Group group, UserAccountResource getUserAccountResource)
		throws Exception {

		Page<UserAccount> page =
			getUserAccountResource.getAssetLibraryUserAccountsPage(
				group.getExternalReferenceCode(), null, null,
				Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		UserAccount randomUserAccount = randomUserAccount();

		UserAccount userAccount1 =
			userAccountResource.putAssetLibraryUserAccount(
				group.getExternalReferenceCode(),
				randomUserAccount.getExternalReferenceCode());

		randomUserAccount = randomUserAccount();

		UserAccount userAccount2 =
			userAccountResource.putAssetLibraryUserAccount(
				group.getExternalReferenceCode(),
				randomUserAccount.getExternalReferenceCode());

		page = getUserAccountResource.getAssetLibraryUserAccountsPage(
			group.getExternalReferenceCode(), null, null, Pagination.of(1, 10),
			null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(userAccount1, (List<UserAccount>)page.getItems());
		assertContains(userAccount2, (List<UserAccount>)page.getItems());
		assertValid(
			page,
			testGetAssetLibraryUserAccountsPage_getExpectedActions(
				group.getExternalReferenceCode()));
	}

	private void _testGetAssetLibraryUserAccountsPageWithSortId()
		throws Exception {

		testGetAssetLibraryUserAccountsPageWithSort(
			EntityField.Type.ID,
			(entityField, userAccount1, userAccount2) -> {
			});
	}

	private void _testPutAssetLibraryUserAccountWithoutAssignMembersPermission()
		throws Exception {

		UserAccountResource withoutAssignMembersPermissionUserAccountResource =
			_getWithoutAssignMembersPermissionUserAccountResource();

		UserAccount userAccount = randomUserAccount();

		_assertFailure(
			"Forbidden",
			() ->
				withoutAssignMembersPermissionUserAccountResource.
					putAssetLibraryUserAccount(
						testDepotEntryGroup.getExternalReferenceCode(),
						userAccount.getExternalReferenceCode()));
	}

	private void _testPutAssetLibraryUserAccountWithViewAndAssignMembersPermission()
		throws Exception {

		UserAccountResource viewAndAssignMembersUserAccountResource =
			_getViewAndAssignMembersUserAccountResource();

		UserAccount userAccount = randomUserAccount();

		UserAccount putUserAccount =
			viewAndAssignMembersUserAccountResource.putAssetLibraryUserAccount(
				testDepotEntryGroup.getExternalReferenceCode(),
				userAccount.getExternalReferenceCode());

		assertValid(putUserAccount);
	}

	@Inject
	private ResourceActions _resourceActions;

	@Inject
	private RoleLocalService _roleLocalService;

	private DepotEntry _spaceDepotEntry;
	private User _testUser;

	@Inject
	private UserLocalService _userLocalService;

}