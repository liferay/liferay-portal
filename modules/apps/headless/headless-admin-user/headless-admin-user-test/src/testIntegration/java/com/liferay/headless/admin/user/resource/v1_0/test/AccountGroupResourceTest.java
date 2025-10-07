/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.resource.v1_0.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountGroupRel;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountGroupLocalService;
import com.liferay.account.service.AccountGroupRelLocalService;
import com.liferay.account.service.AccountGroupRelLocalServiceUtil;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoColumnConstants;
import com.liferay.expando.kernel.model.ExpandoTable;
import com.liferay.expando.kernel.service.ExpandoColumnLocalService;
import com.liferay.expando.kernel.service.ExpandoTableLocalService;
import com.liferay.exportimport.test.rule.LazyReferencing;
import com.liferay.exportimport.test.rule.LazyReferencingTestRule;
import com.liferay.headless.admin.user.client.custom.field.CustomField;
import com.liferay.headless.admin.user.client.custom.field.CustomValue;
import com.liferay.headless.admin.user.client.dto.v1_0.AccountBrief;
import com.liferay.headless.admin.user.client.dto.v1_0.AccountGroup;
import com.liferay.headless.admin.user.client.dto.v1_0.Creator;
import com.liferay.headless.admin.user.client.pagination.Page;
import com.liferay.headless.admin.user.client.pagination.Pagination;
import com.liferay.headless.admin.user.client.permission.Permission;
import com.liferay.headless.admin.user.client.problem.Problem;
import com.liferay.headless.admin.user.client.resource.v1_0.AccountGroupResource;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.vulcan.permission.PermissionUtil;

import java.text.DateFormat;

import java.util.Collections;
import java.util.Date;
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
public class AccountGroupResourceTest extends BaseAccountGroupResourceTestCase {

	@ClassRule
	@Rule
	public static final LazyReferencingTestRule lazyReferencingTestRule =
		LazyReferencingTestRule.INSTANCE;

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_serviceContext = ServiceContextTestUtil.getServiceContext();

		_accountEntry = _addAccountEntry();
	}

	@Override
	@Test
	public void testGetAccountGroup() throws Exception {
		super.testGetAccountGroup();

		_testGetAccountGroupWithNestedFields();
	}

	@Override
	@Test
	public void testGetAccountGroupsPage() throws Exception {
		super.testGetAccountGroupsPage();

		_testGetAccountGroupsPageWithCustomFields();
		_testGetAccountGroupsPageWithFilter();
	}

	@Override
	@Test
	public void testPatchAccountGroup() throws Exception {
		super.testPatchAccountGroup();

		_testPatchAccountGroupWithoutName();
	}

	@Override
	@Test
	public void testPatchAccountGroupByExternalReferenceCode()
		throws Exception {

		super.testPatchAccountGroupByExternalReferenceCode();

		_testPatchAccountGroupByExternalReferenceCodeWithoutName();
	}

	@FeatureFlag("LPD-35914")
	@LazyReferencing
	@Override
	@Test
	public void testPostAccountGroup() throws Exception {
		super.testPostAccountGroup();

		_testPostAccountGroupBatch();
	}

	@Override
	@Test
	public void testPostAccountGroupByExternalReferenceCodeAccountByExternalReferenceCode()
		throws Exception {

		AccountGroup accountGroup = _postAccountGroup(randomAccountGroup());

		assertHttpResponseStatusCode(
			204,
			accountGroupResource.
				postAccountGroupByExternalReferenceCodeAccountByExternalReferenceCodeHttpResponse(
					_accountEntry.getExternalReferenceCode(),
					accountGroup.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			accountGroupResource.
				postAccountGroupByExternalReferenceCodeAccountByExternalReferenceCodeHttpResponse(
					RandomTestUtil.randomString(),
					accountGroup.getExternalReferenceCode()));
	}

	@Override
	@Test
	public void testPutAccountGroup() throws Exception {
		super.testPutAccountGroup();

		_testPutAccountGroupWithoutName();
	}

	@Override
	@Test
	public void testPutAccountGroupByExternalReferenceCode() throws Exception {
		super.testPutAccountGroupByExternalReferenceCode();

		_testPutAccountGroupByExternalReferenceWithoutName();
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"description", "externalReferenceCode", "name"};
	}

	@Override
	protected AccountGroup testDeleteAccountGroup_addAccountGroup()
		throws Exception {

		return _postAccountGroup(randomAccountGroup());
	}

	@Override
	protected AccountGroup
			testDeleteAccountGroupByExternalReferenceCode_addAccountGroup()
		throws Exception {

		return _postAccountGroup(randomAccountGroup());
	}

	@Override
	protected AccountGroup
			testDeleteAccountGroupByExternalReferenceCodeAccountByExternalReferenceCode_addAccountGroup()
		throws Exception {

		return _postAccountGroup(randomAccountGroup());
	}

	@Override
	protected String
			testDeleteAccountGroupByExternalReferenceCodeAccountByExternalReferenceCode_getAccountExternalReferenceCode()
		throws Exception {

		return _accountEntry.getExternalReferenceCode();
	}

	@Override
	protected AccountGroup testGetAccountAccountGroupsPage_addAccountGroup(
			Long accountId, AccountGroup accountGroup)
		throws Exception {

		AccountGroup randomAccountGroup = _postAccountGroup(accountGroup);

		AccountGroupRelLocalServiceUtil.addAccountGroupRel(
			randomAccountGroup.getId(), AccountEntry.class.getName(),
			accountId);

		return randomAccountGroup;
	}

	@Override
	protected Long testGetAccountAccountGroupsPage_getAccountId()
		throws Exception {

		return _accountEntry.getAccountEntryId();
	}

	@Override
	protected AccountGroup
			testGetAccountByExternalReferenceCodeAccountExternalReferenceCodeAccountGroupsPage_addAccountGroup(
				String accountExternalReferenceCode, AccountGroup accountGroup)
		throws Exception {

		AccountGroup randomAccountGroup = _postAccountGroup(accountGroup);

		AccountGroupRelLocalServiceUtil.addAccountGroupRel(
			randomAccountGroup.getId(), AccountEntry.class.getName(),
			_accountEntry.getAccountEntryId());

		return randomAccountGroup;
	}

	@Override
	protected String
			testGetAccountByExternalReferenceCodeAccountExternalReferenceCodeAccountGroupsPage_getAccountExternalReferenceCode()
		throws Exception {

		return _accountEntry.getExternalReferenceCode();
	}

	@Override
	protected AccountGroup testGetAccountGroup_addAccountGroup()
		throws Exception {

		return _postAccountGroup(randomAccountGroup());
	}

	@Override
	protected AccountGroup
			testGetAccountGroupByExternalReferenceCode_addAccountGroup()
		throws Exception {

		return _postAccountGroup(randomAccountGroup());
	}

	@Override
	protected AccountGroup testGetAccountGroupsPage_addAccountGroup(
			AccountGroup accountGroup)
		throws Exception {

		return _postAccountGroup(accountGroup);
	}

	@Override
	protected AccountGroup testGraphQLAccountGroup_addAccountGroup()
		throws Exception {

		return _postAccountGroup(randomAccountGroup());
	}

	@Override
	protected String
			testGraphQLDeleteAccountGroupByExternalReferenceCodeAccountByExternalReferenceCode_getAccountExternalReferenceCode()
		throws Exception {

		return _accountEntry.getExternalReferenceCode();
	}

	@Override
	protected AccountGroup testPatchAccountGroup_addAccountGroup()
		throws Exception {

		return _postAccountGroup(randomAccountGroup());
	}

	@Override
	protected AccountGroup
			testPatchAccountGroupByExternalReferenceCode_addAccountGroup()
		throws Exception {

		return _postAccountGroup(randomAccountGroup());
	}

	@Override
	protected AccountGroup testPostAccountGroup_addAccountGroup(
			AccountGroup accountGroup)
		throws Exception {

		return _postAccountGroup(accountGroup);
	}

	@Override
	protected AccountGroup
			testPostAccountGroupByExternalReferenceCodeAccountByExternalReferenceCode_addAccountGroup()
		throws Exception {

		return _postAccountGroup(randomAccountGroup());
	}

	@Override
	protected AccountGroup testPutAccountGroup_addAccountGroup()
		throws Exception {

		return _postAccountGroup(randomAccountGroup());
	}

	@Override
	protected AccountGroup
			testPutAccountGroupByExternalReferenceCode_addAccountGroup()
		throws Exception {

		return _postAccountGroup(randomAccountGroup());
	}

	private AccountEntry _addAccountEntry() throws Exception {
		return _accountEntryLocalService.addAccountEntry(
			StringPool.BLANK, _serviceContext.getUserId(),
			AccountConstants.PARENT_ACCOUNT_ENTRY_ID_DEFAULT,
			RandomTestUtil.randomString(), null, null,
			RandomTestUtil.randomString() + "@liferay.com", null, null,
			AccountConstants.ACCOUNT_ENTRY_TYPE_GUEST,
			WorkflowConstants.STATUS_APPROVED, _serviceContext);
	}

	private AccountGroup _postAccountGroup(AccountGroup accountGroup)
		throws Exception {

		return accountGroupResource.postAccountGroup(accountGroup);
	}

	private void _testGetAccountGroupsPageWithCustomFields() throws Exception {
		ExpandoTable expandoTable = _expandoTableLocalService.addTable(
			testGroup.getCompanyId(),
			_classNameLocalService.getClassNameId(
				com.liferay.account.model.AccountGroup.class),
			"CUSTOM_FIELDS");

		ExpandoColumn expandoColumn = _expandoColumnLocalService.addColumn(
			expandoTable.getTableId(), "A" + RandomTestUtil.randomString(),
			ExpandoColumnConstants.STRING);

		UnicodeProperties unicodeProperties =
			expandoColumn.getTypeSettingsProperties();

		unicodeProperties.setProperty(
			ExpandoColumnConstants.INDEX_TYPE,
			String.valueOf(ExpandoColumnConstants.INDEX_TYPE_KEYWORD));

		expandoColumn.setTypeSettingsProperties(unicodeProperties);

		_expandoColumnLocalService.updateExpandoColumn(expandoColumn);

		AccountGroup accountGroup = randomAccountGroup();

		String value = RandomTestUtil.randomString();

		accountGroup.setCustomFields(
			() -> new CustomField[] {
				new CustomField() {
					{
						customValue = new CustomValue() {
							{
								data = value;
							}
						};
						dataType = "Text";
						name = expandoColumn.getName();
					}
				}
			});

		accountGroup = testGetAccountGroupsPage_addAccountGroup(accountGroup);

		Page<AccountGroup> page = accountGroupResource.getAccountGroupsPage(
			null,
			StringBundler.concat(
				"(customFields/", expandoColumn.getName(), " eq '",
				RandomTestUtil.randomString(), "')"),
			Pagination.of(1, 2), null);

		Assert.assertEquals(0, page.getTotalCount());

		page = accountGroupResource.getAccountGroupsPage(
			null,
			StringBundler.concat(
				"(customFields/", expandoColumn.getName(), " eq '", value,
				"')"),
			Pagination.of(1, 2), null);

		Assert.assertEquals(1, page.getTotalCount());

		assertEquals(
			Collections.singletonList(accountGroup),
			(List<AccountGroup>)page.getItems());
	}

	private void _testGetAccountGroupsPageWithFilter() throws Exception {
		Page<AccountGroup> page = accountGroupResource.getAccountGroupsPage(
			null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		// Sleep for 1 second to ensure that account group 1 and existing
		// account groups are created 1 second apart

		Thread.sleep(1000);

		AccountGroup accountGroup1 = testGetAccountGroupsPage_addAccountGroup(
			randomAccountGroup());

		// Sleep for 1 second to ensure that account group 1 and account
		// group 2 are created 1 second apart

		Thread.sleep(1000);

		AccountGroup accountGroup2 = testGetAccountGroupsPage_addAccountGroup(
			randomAccountGroup());

		Date date = accountGroup1.getDateCreated();

		DateFormat dateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");

		page = accountGroupResource.getAccountGroupsPage(
			null, "dateCreated lt " + dateFormat.format(date.getTime()),
			Pagination.of(1, 2), null);

		Assert.assertEquals(totalCount, page.getTotalCount());

		page = accountGroupResource.getAccountGroupsPage(
			null, "dateCreated ge " + dateFormat.format(date.getTime()),
			Pagination.of(1, 2), null);

		Assert.assertEquals(2, page.getTotalCount());

		// Sleep for 1 second to ensure that account group 1 and account
		// group 2 are modified 1 second apart

		Thread.sleep(1000);

		accountGroup1.setDescription(
			StringUtil.toLowerCase(RandomTestUtil.randomString()));

		accountGroup1 = accountGroupResource.patchAccountGroup(
			accountGroup1.getId(), accountGroup1);

		date = accountGroup1.getDateModified();

		page = accountGroupResource.getAccountGroupsPage(
			null, "dateModified ge " + dateFormat.format(date.getTime()),
			Pagination.of(1, 2), null);

		Assert.assertEquals(1, page.getTotalCount());

		assertContains(accountGroup1, (List<AccountGroup>)page.getItems());

		page = accountGroupResource.getAccountGroupsPage(
			null, "dateModified lt " + dateFormat.format(date.getTime()),
			Pagination.of(1, 2), null);

		Assert.assertEquals(totalCount + 1, page.getTotalCount());

		assertContains(accountGroup2, (List<AccountGroup>)page.getItems());
	}

	private void _testGetAccountGroupWithNestedFields() throws Exception {
		AccountGroup postAccountGroup = testGetAccountGroup_addAccountGroup();

		AccountEntry accountEntry1 = _addAccountEntry();
		AccountEntry accountEntry2 = _addAccountEntry();
		AccountEntry accountEntry3 = _addAccountEntry();

		_accountGroupRelLocalService.addAccountGroupRels(
			postAccountGroup.getId(), AccountEntry.class.getName(),
			new long[] {
				accountEntry1.getAccountEntryId(),
				accountEntry2.getAccountEntryId()
			});

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(),
			com.liferay.account.model.AccountGroup.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(postAccountGroup.getId()), role.getRoleId(),
			new String[] {ActionKeys.DELETE});

		AccountGroupResource accountGroupResource =
			AccountGroupResource.builder(
			).authentication(
				"test@liferay.com", PropsValues.DEFAULT_ADMIN_PASSWORD
			).locale(
				LocaleUtil.getDefault()
			).parameters(
				"nestedFields", "accountBriefs,creator,permissions"
			).build();

		AccountGroup getAccountGroup = accountGroupResource.getAccountGroup(
			postAccountGroup.getId());

		Assert.assertTrue(
			ArrayUtil.exists(
				getAccountGroup.getAccountBriefs(),
				accountBrief ->
					accountBrief.getId() == accountEntry1.getAccountEntryId()));
		Assert.assertTrue(
			ArrayUtil.exists(
				getAccountGroup.getAccountBriefs(),
				accountBrief ->
					accountBrief.getId() == accountEntry2.getAccountEntryId()));
		Assert.assertFalse(
			ArrayUtil.exists(
				getAccountGroup.getAccountBriefs(),
				accountBrief ->
					accountBrief.getId() == accountEntry3.getAccountEntryId()));

		Creator creator = getAccountGroup.getCreator();

		Assert.assertTrue(creator.getId() == TestPropsValues.getUserId());

		User user = TestPropsValues.getUser();

		Assert.assertTrue(
			Objects.equals(
				creator.getExternalReferenceCode(),
				user.getExternalReferenceCode()));

		Assert.assertTrue(
			ArrayUtil.exists(
				getAccountGroup.getPermissions(),
				permission ->
					Objects.equals(permission.getRoleName(), role.getName()) &&
					(permission.getActionIds().length == 1) &&
					Objects.equals(permission.getActionIds()[0], "DELETE")));
	}

	private void _testPatchAccountGroupByExternalReferenceCodeWithoutName()
		throws Exception {

		AccountGroup postAccountGroup = _postAccountGroup(randomAccountGroup());

		AccountGroup randomPatchAccountGroup = randomPatchAccountGroup();

		randomPatchAccountGroup.setName(() -> null);

		AccountGroup patchAccountGroup =
			accountGroupResource.patchAccountGroupByExternalReferenceCode(
				postAccountGroup.getExternalReferenceCode(),
				randomPatchAccountGroup);

		AccountGroup expectedPatchAccountGroup = postAccountGroup.clone();

		BeanTestUtil.copyProperties(
			randomPatchAccountGroup, expectedPatchAccountGroup);

		expectedPatchAccountGroup.setName(postAccountGroup.getName());

		AccountGroup getAccountGroup = accountGroupResource.getAccountGroup(
			patchAccountGroup.getId());

		assertEquals(expectedPatchAccountGroup, getAccountGroup);
		assertValid(getAccountGroup);
	}

	private void _testPatchAccountGroupWithoutName() throws Exception {
		AccountGroup postAccountGroup = _postAccountGroup(randomAccountGroup());

		AccountGroup randomPatchAccountGroup = randomPatchAccountGroup();

		randomPatchAccountGroup.setName(() -> null);

		AccountGroup patchAccountGroup = accountGroupResource.patchAccountGroup(
			postAccountGroup.getId(), randomPatchAccountGroup);

		AccountGroup expectedPatchAccountGroup = postAccountGroup.clone();

		BeanTestUtil.copyProperties(
			randomPatchAccountGroup, expectedPatchAccountGroup);

		expectedPatchAccountGroup.setName(postAccountGroup.getName());

		AccountGroup getAccountGroup = accountGroupResource.getAccountGroup(
			patchAccountGroup.getId());

		assertEquals(expectedPatchAccountGroup, getAccountGroup);
		assertValid(getAccountGroup);
	}

	private void _testPostAccountGroupBatch() throws Exception {
		AccountGroup randomAccountGroup = randomAccountGroup();

		AccountEntry serviceBuilderAccountEntry1 =
			_accountEntryLocalService.addAccountEntry(
				StringPool.BLANK, TestPropsValues.getUserId(),
				AccountConstants.PARENT_ACCOUNT_ENTRY_ID_DEFAULT,
				RandomTestUtil.randomString(), null, new String[0], null, null,
				null, AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS,
				WorkflowConstants.STATUS_APPROVED, null);

		AccountBrief accountBrief1 = new AccountBrief() {
			{
				externalReferenceCode =
					serviceBuilderAccountEntry1.getExternalReferenceCode();
				name = serviceBuilderAccountEntry1.getName();
				type = serviceBuilderAccountEntry1.getType();
			}
		};

		AccountBrief accountBrief2 = new AccountBrief() {
			{
				externalReferenceCode = RandomTestUtil.randomString();
				name = RandomTestUtil.randomString();
				type = AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS;
			}
		};

		randomAccountGroup.setAccountBriefs(
			new AccountBrief[] {accountBrief1, accountBrief2});

		Role serviceBuilderRole1 = RoleTestUtil.addRole(
			RoleConstants.TYPE_REGULAR);

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

		randomAccountGroup.setPermissions(
			new Permission[] {permission1, permission2});

		waitForFinish(
			"COMPLETED",
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					"items",
					JSONUtil.put(
						_jsonFactory.createJSONObject(
							randomAccountGroup.toString()))
				).toString(),
				"headless-admin-user/v1.0/account-groups/batch",
				Http.Method.POST));

		AccountEntry serviceBuilderAccountEntry2 =
			_accountEntryLocalService.fetchAccountEntryByExternalReferenceCode(
				accountBrief1.getExternalReferenceCode(),
				TestPropsValues.getCompanyId());

		Assert.assertEquals(
			serviceBuilderAccountEntry1.getAccountEntryId(),
			serviceBuilderAccountEntry2.getAccountEntryId());

		com.liferay.account.model.AccountGroup serviceBuilderAccountGroup =
			_accountGroupLocalService.fetchAccountGroupByExternalReferenceCode(
				randomAccountGroup.getExternalReferenceCode(),
				TestPropsValues.getCompanyId());

		List<AccountGroupRel> serviceBuilderAccountGroupRels =
			_accountGroupRelLocalService.getAccountGroupRels(
				serviceBuilderAccountGroup.getAccountGroupId(),
				AccountEntry.class.getName());

		Assert.assertTrue(
			ListUtil.exists(
				serviceBuilderAccountGroupRels,
				serviceBuilderAccountGroupRel ->
					serviceBuilderAccountGroupRel.getClassPK() ==
						serviceBuilderAccountEntry2.getAccountEntryId()));

		AccountEntry serviceBuilderAccountEntry3 =
			_accountEntryLocalService.fetchAccountEntryByExternalReferenceCode(
				accountBrief2.getExternalReferenceCode(),
				TestPropsValues.getCompanyId());

		Assert.assertTrue(
			ListUtil.exists(
				serviceBuilderAccountGroupRels,
				serviceBuilderAccountGroupRel ->
					serviceBuilderAccountGroupRel.getClassPK() ==
						serviceBuilderAccountEntry3.getAccountEntryId()));
		Assert.assertEquals(
			accountBrief2.getName(), serviceBuilderAccountEntry3.getName());
		Assert.assertEquals(
			accountBrief2.getType(), serviceBuilderAccountEntry3.getType());
		Assert.assertEquals(
			WorkflowConstants.STATUS_EMPTY,
			serviceBuilderAccountEntry3.getStatus());

		Role serviceBuilderRole2 =
			_roleLocalService.fetchRoleByExternalReferenceCode(
				permission1.getRoleExternalReferenceCode(),
				TestPropsValues.getCompanyId());

		List<com.liferay.portal.vulcan.permission.Permission> permissions =
			ListUtil.fromCollection(
				PermissionUtil.getPermissions(
					TestPropsValues.getCompanyId(),
					_resourceActionLocalService.getResourceActions(
						com.liferay.account.model.AccountGroup.class.getName()),
					serviceBuilderAccountGroup.getAccountGroupId(),
					com.liferay.account.model.AccountGroup.class.getName(),
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

		Role serviceBuilderRole3 =
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

	private void _testPutAccountGroupByExternalReferenceWithoutName()
		throws Exception {

		AccountGroup postAccountGroup = _postAccountGroup(randomAccountGroup());

		AccountGroup randomAccountGroup = randomAccountGroup();

		randomAccountGroup.setName(() -> null);

		try {
			accountGroupResource.putAccountGroupByExternalReferenceCode(
				postAccountGroup.getExternalReferenceCode(),
				randomAccountGroup);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals(
				"The account group name is invalid", problem.getTitle());
		}
	}

	private void _testPutAccountGroupWithoutName() throws Exception {
		AccountGroup postAccountGroup = _postAccountGroup(randomAccountGroup());

		AccountGroup randomAccountGroup = randomAccountGroup();

		randomAccountGroup.setName(() -> null);

		try {
			accountGroupResource.putAccountGroup(
				postAccountGroup.getId(), randomAccountGroup);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals(
				"The account group name is invalid", problem.getTitle());
		}
	}

	private AccountEntry _accountEntry;

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	@Inject
	private AccountGroupLocalService _accountGroupLocalService;

	@Inject
	private AccountGroupRelLocalService _accountGroupRelLocalService;

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private ExpandoColumnLocalService _expandoColumnLocalService;

	@Inject
	private ExpandoTableLocalService _expandoTableLocalService;

	@Inject
	private JSONFactory _jsonFactory;

	@Inject
	private ResourceActionLocalService _resourceActionLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	private ServiceContext _serviceContext;

}