/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.resource.v1_0.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountRole;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountEntryUserRelLocalService;
import com.liferay.account.service.AccountRoleLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.entry.rel.service.AssetEntryAssetCategoryRelLocalService;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.test.util.AssetTestUtil;
import com.liferay.captcha.rest.client.dto.v1_0.Captcha;
import com.liferay.captcha.rest.client.resource.v1_0.CaptchaResource;
import com.liferay.captcha.simplecaptcha.SimpleCaptchaImpl;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoColumnConstants;
import com.liferay.expando.kernel.model.ExpandoTable;
import com.liferay.expando.kernel.model.ExpandoTableConstants;
import com.liferay.expando.kernel.service.ExpandoColumnLocalService;
import com.liferay.expando.kernel.service.ExpandoTableLocalService;
import com.liferay.headless.admin.user.client.custom.field.CustomField;
import com.liferay.headless.admin.user.client.custom.field.CustomValue;
import com.liferay.headless.admin.user.client.dto.v1_0.Creator;
import com.liferay.headless.admin.user.client.dto.v1_0.EmailAddress;
import com.liferay.headless.admin.user.client.dto.v1_0.OrganizationBrief;
import com.liferay.headless.admin.user.client.dto.v1_0.Phone;
import com.liferay.headless.admin.user.client.dto.v1_0.PostalAddress;
import com.liferay.headless.admin.user.client.dto.v1_0.RoleBrief;
import com.liferay.headless.admin.user.client.dto.v1_0.SiteBrief;
import com.liferay.headless.admin.user.client.dto.v1_0.UserAccount;
import com.liferay.headless.admin.user.client.dto.v1_0.UserAccountContactInformation;
import com.liferay.headless.admin.user.client.dto.v1_0.WebUrl;
import com.liferay.headless.admin.user.client.http.HttpInvoker;
import com.liferay.headless.admin.user.client.pagination.Page;
import com.liferay.headless.admin.user.client.pagination.Pagination;
import com.liferay.headless.admin.user.client.problem.Problem;
import com.liferay.headless.admin.user.client.resource.v1_0.UserAccountResource;
import com.liferay.headless.admin.user.client.serdes.v1_0.UserAccountSerDes;
import com.liferay.object.constants.ObjectValidationRuleConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectValidationRule;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectValidationRuleLocalService;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.kernel.captcha.CaptchaException;
import com.liferay.portal.kernel.encryptor.EncryptorUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.model.UserGroupRole;
import com.liferay.portal.kernel.model.WorkflowDefinitionLink;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.repository.LocalRepository;
import com.liferay.portal.kernel.repository.RepositoryProviderUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.security.auth.Authenticator;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.UserService;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.OrganizationTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserGroupTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.security.service.access.policy.model.SAPEntry;
import com.liferay.portal.security.service.access.policy.service.SAPEntryLocalService;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.mail.MailMessage;
import com.liferay.portal.test.mail.MailServiceTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.SynchronousMailTestRule;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.BaseExceptionMapper;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import jakarta.portlet.PortletPreferences;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.ws.rs.core.Response;

import java.io.InputStream;

import java.text.DateFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 * @author Javier Gamarra
 * @author Matyas Wollner
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class UserAccountResourceTest extends BaseUserAccountResourceTestCase {

	@ClassRule
	@Rule
	public static final SynchronousMailTestRule synchronousMailTestRule =
		SynchronousMailTestRule.INSTANCE;

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_accountEntry = _addAccountEntry();
		_organization = OrganizationTestUtil.addOrganization();

		_role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_resourcePermissionLocalService.addResourcePermission(
			TestPropsValues.getCompanyId(), User.class.getName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), _role.getRoleId(),
			ActionKeys.VIEW);

		_testUser = _userLocalService.getUserByEmailAddress(
			testGroup.getCompanyId(), "test@liferay.com");

		_userLocalService.deleteGroupUser(
			testGroup.getGroupId(), _testUser.getUserId());

		Indexer<User> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
			_testUser.getModelClassName());

		indexer.reindex(_testUser);

		_userGroup = UserGroupTestUtil.addUserGroup();
	}

	@Override
	@Test
	public void testDeleteAccountUserAccountByEmailAddress() throws Exception {
		super.testDeleteAccountUserAccountByEmailAddress();

		User user = UserTestUtil.addUser();

		_accountEntryUserRelLocalService.addAccountEntryUserRel(
			_accountEntry.getAccountEntryId(), user.getUserId());

		Assert.assertNotNull(
			_accountEntryUserRelLocalService.fetchAccountEntryUserRel(
				_accountEntry.getAccountEntryId(), user.getUserId()));

		userAccountResource.deleteAccountUserAccountByEmailAddress(
			_accountEntry.getAccountEntryId(), user.getEmailAddress());

		Assert.assertNull(
			_accountEntryUserRelLocalService.fetchAccountEntryUserRel(
				_accountEntry.getAccountEntryId(), user.getUserId()));
	}

	@Override
	@Test
	public void testDeleteAccountUserAccountByExternalReferenceCodeByEmailAddress()
		throws Exception {

		super.
			testDeleteAccountUserAccountByExternalReferenceCodeByEmailAddress();

		User user = UserTestUtil.addUser();

		_accountEntryUserRelLocalService.addAccountEntryUserRel(
			_accountEntry.getAccountEntryId(), user.getUserId());

		Assert.assertNotNull(
			_accountEntryUserRelLocalService.fetchAccountEntryUserRel(
				_accountEntry.getAccountEntryId(), user.getUserId()));

		userAccountResource.
			deleteAccountUserAccountByExternalReferenceCodeByEmailAddress(
				_accountEntry.getExternalReferenceCode(),
				user.getEmailAddress());

		Assert.assertNull(
			_accountEntryUserRelLocalService.fetchAccountEntryUserRel(
				_accountEntry.getAccountEntryId(), user.getUserId()));
	}

	@Override
	@Test
	public void testDeleteAccountUserAccountsByEmailAddress() throws Exception {
		List<User> users = Arrays.asList(
			UserTestUtil.addUser(), UserTestUtil.addUser(),
			UserTestUtil.addUser(), UserTestUtil.addUser());

		_accountEntryUserRelLocalService.addAccountEntryUserRels(
			_accountEntry.getAccountEntryId(), _toUserIds(users));

		for (User user : users) {
			Assert.assertNotNull(
				_accountEntryUserRelLocalService.fetchAccountEntryUserRel(
					_accountEntry.getAccountEntryId(), user.getUserId()));
		}

		List<User> removeUsers = users.subList(0, 2);

		userAccountResource.deleteAccountUserAccountsByEmailAddress(
			_accountEntry.getAccountEntryId(), _toEmailAddresses(removeUsers));

		for (User user : removeUsers) {
			Assert.assertNull(
				_accountEntryUserRelLocalService.fetchAccountEntryUserRel(
					_accountEntry.getAccountEntryId(), user.getUserId()));
		}

		List<User> keepUsers = users.subList(2, 4);

		for (User user : keepUsers) {
			Assert.assertNotNull(
				_accountEntryUserRelLocalService.fetchAccountEntryUserRel(
					_accountEntry.getAccountEntryId(), user.getUserId()));
		}
	}

	@Override
	@Test
	public void testDeleteAccountUserAccountsByExternalReferenceCodeByEmailAddress()
		throws Exception {

		List<User> users = Arrays.asList(
			UserTestUtil.addUser(), UserTestUtil.addUser(),
			UserTestUtil.addUser(), UserTestUtil.addUser());

		_accountEntryUserRelLocalService.addAccountEntryUserRels(
			_accountEntry.getAccountEntryId(), _toUserIds(users));

		for (User user : users) {
			Assert.assertNotNull(
				_accountEntryUserRelLocalService.fetchAccountEntryUserRel(
					_accountEntry.getAccountEntryId(), user.getUserId()));
		}

		List<User> removeUsers = users.subList(0, 2);

		userAccountResource.
			deleteAccountUserAccountsByExternalReferenceCodeByEmailAddress(
				_accountEntry.getExternalReferenceCode(),
				_toEmailAddresses(removeUsers));

		for (User user : removeUsers) {
			Assert.assertNull(
				_accountEntryUserRelLocalService.fetchAccountEntryUserRel(
					_accountEntry.getAccountEntryId(), user.getUserId()));
		}

		List<User> keepUsers = users.subList(2, 4);

		for (User user : keepUsers) {
			Assert.assertNotNull(
				_accountEntryUserRelLocalService.fetchAccountEntryUserRel(
					_accountEntry.getAccountEntryId(), user.getUserId()));
		}
	}

	@Override
	@Test
	public void testGetSiteAccountUserAccountSelected() throws Exception {
		AccountEntry accountEntry1 = _addAccountEntry();
		User user = UserTestUtil.addUser();

		_accountEntryUserRelLocalService.addAccountEntryUserRel(
			accountEntry1.getAccountEntryId(), user.getUserId());

		AccountEntry accountEntry2 = _addAccountEntry();

		_accountEntryUserRelLocalService.addAccountEntryUserRel(
			accountEntry2.getAccountEntryId(), user.getUserId());

		Assert.assertFalse(
			userAccountResource.getSiteAccountUserAccountSelected(
				testGroup.getGroupId(), accountEntry1.getAccountEntryId(),
				user.getUserId()));
		Assert.assertFalse(
			userAccountResource.getSiteAccountUserAccountSelected(
				testGroup.getGroupId(), accountEntry2.getAccountEntryId(),
				user.getUserId()));

		userAccountResource.patchSiteAccountUserAccountSelected(
			testGroup.getGroupId(), accountEntry1.getAccountEntryId(),
			user.getUserId());

		Assert.assertTrue(
			userAccountResource.getSiteAccountUserAccountSelected(
				testGroup.getGroupId(), accountEntry1.getAccountEntryId(),
				user.getUserId()));
		Assert.assertFalse(
			userAccountResource.getSiteAccountUserAccountSelected(
				testGroup.getGroupId(), accountEntry2.getAccountEntryId(),
				user.getUserId()));

		userAccountResource.patchSiteAccountUserAccountSelected(
			testGroup.getGroupId(), accountEntry2.getAccountEntryId(),
			user.getUserId());

		Assert.assertFalse(
			userAccountResource.getSiteAccountUserAccountSelected(
				testGroup.getGroupId(), accountEntry1.getAccountEntryId(),
				user.getUserId()));
		Assert.assertTrue(
			userAccountResource.getSiteAccountUserAccountSelected(
				testGroup.getGroupId(), accountEntry2.getAccountEntryId(),
				user.getUserId()));

		AccountEntry accountEntry3 = _addAccountEntry();

		assertHttpResponseStatusCode(
			404,
			userAccountResource.getSiteAccountUserAccountSelectedHttpResponse(
				testGroup.getGroupId(), accountEntry3.getAccountEntryId(),
				user.getUserId()));
	}

	@Override
	@Test
	public void testGetSiteByFriendlyUrlPathAccountByExternalReferenceCodeAccountExternalReferenceCodeUserAccountByExternalReferenceCodeUserAccountExternalReferenceCodeSelected()
		throws Exception {

		AccountEntry accountEntry1 = _addAccountEntry();
		User user = UserTestUtil.addUser();

		_accountEntryUserRelLocalService.addAccountEntryUserRel(
			accountEntry1.getAccountEntryId(), user.getUserId());

		AccountEntry accountEntry2 = _addAccountEntry();

		_accountEntryUserRelLocalService.addAccountEntryUserRel(
			accountEntry2.getAccountEntryId(), user.getUserId());

		Assert.assertFalse(
			userAccountResource.
				getSiteByFriendlyUrlPathAccountByExternalReferenceCodeAccountExternalReferenceCodeUserAccountByExternalReferenceCodeUserAccountExternalReferenceCodeSelected(
					testGroup.getFriendlyURL(),
					accountEntry1.getExternalReferenceCode(),
					user.getExternalReferenceCode()));
		Assert.assertFalse(
			userAccountResource.
				getSiteByFriendlyUrlPathAccountByExternalReferenceCodeAccountExternalReferenceCodeUserAccountByExternalReferenceCodeUserAccountExternalReferenceCodeSelected(
					testGroup.getFriendlyURL(),
					accountEntry2.getExternalReferenceCode(),
					user.getExternalReferenceCode()));

		userAccountResource.
			patchSiteByFriendlyUrlPathAccountByExternalReferenceCodeAccountExternalReferenceCodeUserAccountByExternalReferenceCodeUserAccountExternalReferenceCodeSelected(
				testGroup.getFriendlyURL(),
				accountEntry1.getExternalReferenceCode(),
				user.getExternalReferenceCode());

		Assert.assertTrue(
			userAccountResource.
				getSiteByFriendlyUrlPathAccountByExternalReferenceCodeAccountExternalReferenceCodeUserAccountByExternalReferenceCodeUserAccountExternalReferenceCodeSelected(
					testGroup.getFriendlyURL(),
					accountEntry1.getExternalReferenceCode(),
					user.getExternalReferenceCode()));
		Assert.assertFalse(
			userAccountResource.
				getSiteByFriendlyUrlPathAccountByExternalReferenceCodeAccountExternalReferenceCodeUserAccountByExternalReferenceCodeUserAccountExternalReferenceCodeSelected(
					testGroup.getFriendlyURL(),
					accountEntry2.getExternalReferenceCode(),
					user.getExternalReferenceCode()));

		userAccountResource.
			patchSiteByFriendlyUrlPathAccountByExternalReferenceCodeAccountExternalReferenceCodeUserAccountByExternalReferenceCodeUserAccountExternalReferenceCodeSelected(
				testGroup.getFriendlyURL(),
				accountEntry2.getExternalReferenceCode(),
				user.getExternalReferenceCode());

		Assert.assertFalse(
			userAccountResource.
				getSiteByFriendlyUrlPathAccountByExternalReferenceCodeAccountExternalReferenceCodeUserAccountByExternalReferenceCodeUserAccountExternalReferenceCodeSelected(
					testGroup.getFriendlyURL(),
					accountEntry1.getExternalReferenceCode(),
					user.getExternalReferenceCode()));
		Assert.assertTrue(
			userAccountResource.
				getSiteByFriendlyUrlPathAccountByExternalReferenceCodeAccountExternalReferenceCodeUserAccountByExternalReferenceCodeUserAccountExternalReferenceCodeSelected(
					testGroup.getFriendlyURL(),
					accountEntry2.getExternalReferenceCode(),
					user.getExternalReferenceCode()));

		AccountEntry accountEntry3 = _addAccountEntry();

		assertHttpResponseStatusCode(
			404,
			userAccountResource.
				getSiteByFriendlyUrlPathAccountByExternalReferenceCodeAccountExternalReferenceCodeUserAccountByExternalReferenceCodeUserAccountExternalReferenceCodeSelectedHttpResponse(
					testGroup.getFriendlyURL(),
					accountEntry3.getExternalReferenceCode(),
					user.getExternalReferenceCode()));
	}

	@Override
	@Test
	public void testGetSiteUserAccountsPage() throws Exception {
		Page<UserAccount> page = userAccountResource.getSiteUserAccountsPage(
			testGetSiteUserAccountsPage_getSiteId(),
			RandomTestUtil.randomString(), null, Pagination.of(1, 2), null);

		Assert.assertEquals(0, page.getTotalCount());

		Long siteId = testGetSiteUserAccountsPage_getSiteId();

		UserAccount userAccount1 = testGetSiteUserAccountsPage_addUserAccount(
			siteId, randomUserAccount());
		UserAccount userAccount2 = testGetSiteUserAccountsPage_addUserAccount(
			siteId, randomUserAccount());

		page = userAccountResource.getSiteUserAccountsPage(
			siteId, null, null, Pagination.of(1, 2), null);

		Assert.assertEquals(2, page.getTotalCount());

		assertEqualsIgnoringOrder(
			Arrays.asList(userAccount1, userAccount2),
			(List<UserAccount>)page.getItems());
		assertValid(page);
	}

	@Override
	@Test
	public void testGetUserAccount() throws Exception {
		super.testGetUserAccount();

		Group group = GroupTestUtil.addGroup();

		Role groupRole = RoleTestUtil.addRole(
			"Test Site Role", RoleConstants.TYPE_SITE);

		User groupUser = UserTestUtil.addGroupUser(group, "Test Site Role");

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			"headless-admin-user/v1.0/user-accounts/" + groupUser.getUserId(),
			Http.Method.GET);

		JSONAssert.assertEquals(
			JSONUtil.put(
				JSONUtil.put(
					"name", group.getGroupKey()
				).put(
					"roleBriefs",
					JSONUtil.put(
						JSONUtil.put(
							"id", groupRole.getRoleId()
						).put(
							"name", groupRole.getName()
						))
				)
			).toString(),
			String.valueOf(jsonObject.getJSONArray("siteBriefs")),
			JSONCompareMode.LENIENT);

		User user = UserTestUtil.addUser();

		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(_testUser));

			_testGetUserAccountWithRoles(
				group,
				() -> _groupLocalService.addUserGroup(user.getUserId(), group),
				user);

			Organization organization = OrganizationTestUtil.addOrganization();

			_testGetUserAccountWithRoles(
				organization.getGroup(),
				() -> _organizationLocalService.addUserOrganization(
					user.getUserId(), organization),
				user);

			UserGroup userGroup = UserGroupTestUtil.addUserGroup();

			_testGetUserAccountWithRoles(
				userGroup.getGroup(),
				() -> _userGroupLocalService.addUserUserGroup(
					user.getUserId(), userGroup),
				user);
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(
				originalPermissionChecker);
		}

		_testGetUserAccountWithGender();
		_testGetUserAccountWithLoginDate();
		_testGetUserAccountWithMoreExternalReferenceCodes();
		_testGetUserAccountWithNestedFields();
	}

	@Override
	@Test
	public void testGetUserAccountByEmailAddress() throws Exception {
		super.testGetUserAccountByEmailAddress();

		UserAccount postUserAccount =
			testGetUserAccountsByStatusPage_addUserAccount(
				com.liferay.headless.admin.user.dto.v1_0.UserAccount.Status.
					INACTIVE.toString(),
				randomUserAccount());

		UserAccount getUserAccount =
			userAccountResource.getUserAccountByEmailAddress(
				postUserAccount.getEmailAddress());

		assertEquals(postUserAccount, getUserAccount);
		assertValid(getUserAccount);
	}

	@Override
	@Test
	public void testGetUserAccountsByStatusPage() throws Exception {
		super.testGetUserAccountsByStatusPage();

		Page<UserAccount> page =
			userAccountResource.getUserAccountsByStatusPage(
				testGetUserAccountsByStatusPage_getStatus(), null,
				"status eq 0", Pagination.of(1, 2), null);

		Assert.assertEquals(0, page.getTotalCount());
	}

	@Override
	@Test
	public void testGetUserAccountsPage() throws Exception {
		UserAccount userAccount1 = testGetUserAccountsPage_addUserAccount(
			randomUserAccount());
		UserAccount userAccount2 = testGetUserAccountsPage_addUserAccount(
			randomUserAccount());
		UserAccount userAccount3 = userAccountResource.getUserAccount(
			_testUser.getUserId());

		String idFilterString = String.format(
			"id in ('%s','%s','%s')", userAccount1.getId(),
			userAccount2.getId(), userAccount3.getId());

		_testGetUserAccountsPage(
			idFilterString, userAccount1, userAccount2, userAccount3);

		_userLocalService.updateLastLogin(userAccount2.getId(), null);
		_userLocalService.updateLastLogin(userAccount3.getId(), null);

		_testGetUserAccountsPage(
			String.format("%s and %s", idFilterString, "hasLoginDate ne false"),
			userAccount2, userAccount3);
		_testGetUserAccountsPage(
			String.format("%s and %s", idFilterString, "hasLoginDate eq false"),
			userAccount1);

		_testGetUserAccountsPage(
			String.format("name eq '%s'", userAccount1.getName()),
			userAccount1);

		String familyName = RandomTestUtil.randomString();

		UserAccount userAccount4 = randomUserAccount();

		userAccount4.setFamilyName(familyName);

		userAccount4 = testGetUserAccountsPage_addUserAccount(userAccount4);

		UserAccount userAccount5 = randomUserAccount();

		userAccount5.setFamilyName(familyName);

		userAccount5 = testGetUserAccountsPage_addUserAccount(userAccount5);

		_testGetUserAccountsPage(
			String.format("contains(name, '%s')", familyName), userAccount4,
			userAccount5);

		String roleName = "Test role " + RandomTestUtil.randomString();

		Role role = RoleTestUtil.addRole(roleName, RoleConstants.TYPE_REGULAR);

		_userLocalService.addRoleUser(role.getRoleId(), userAccount1.getId());

		_testGetUserAccountsPage(
			String.format("roleNames/any(f:f eq '%s')", roleName),
			userAccount1);
		_testGetUserAccountsPage(
			"roleNames/any(f:contains(f, 'Test role '))", userAccount1);
		_testGetUserAccountsPage("roleNames/any(f:f eq 'Test Role')");

		String groupRoleName =
			"Test group role " + RandomTestUtil.randomString();

		Role groupRole = RoleTestUtil.addRole(
			groupRoleName, RoleConstants.TYPE_SITE);

		_userGroupRoleLocalService.addUserGroupRole(
			userAccount2.getId(), TestPropsValues.getGroupId(),
			groupRole.getRoleId());

		_testGetUserAccountsPage(
			String.format("userGroupRoleNames/any(f:f eq '%s')", groupRoleName),
			userAccount2);
		_testGetUserAccountsPage(
			"userGroupRoleNames/any(f:contains(f, 'Test group role '))",
			userAccount2);
		_testGetUserAccountsPage("userGroupRoleNames/any(f:f eq 'Test Role')");

		UserAccount userAccount6 =
			testGetUserAccountsByStatusPage_addUserAccount(
				"inactive", randomUserAccount());

		_testGetUserAccountsPage("status eq 5", userAccount6);

		idFilterString = String.format(
			"id in ('%s','%s','%s','%s')", userAccount1.getId(),
			userAccount2.getId(), userAccount3.getId(), userAccount6.getId());

		_testGetUserAccountsPage(
			String.format(
				"%s and %s", idFilterString,
				"((status eq 0) or (status eq 5))"),
			userAccount1, userAccount2, userAccount3, userAccount6);

		_testGetUserAccountsPageWithBirthDateFilter();
		_testGetUserAccountsPageWithCustomFields();
		_testGetUserAccountsPageWithSortCustomField();
		_testGetUserAccountsPageWithSortFullName();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLDeleteAccountByExternalReferenceCodeUserAccountByExternalReferenceCode()
		throws Exception {

		super.
			testGraphQLDeleteAccountByExternalReferenceCodeUserAccountByExternalReferenceCode();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLDeleteAccountUserAccount() throws Exception {
		super.testGraphQLDeleteAccountUserAccount();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLDeleteAccountUserAccountByEmailAddress()
		throws Exception {

		super.testGraphQLDeleteAccountUserAccountByEmailAddress();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLDeleteAccountUserAccountByExternalReferenceCodeByEmailAddress()
		throws Exception {

		super.
			testGraphQLDeleteAccountUserAccountByExternalReferenceCodeByEmailAddress();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLDeleteAccountUserAccountsByEmailAddress()
		throws Exception {

		super.testGraphQLDeleteAccountUserAccountsByEmailAddress();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLDeleteAccountUserAccountsByExternalReferenceCodeByEmailAddress()
		throws Exception {

		super.
			testGraphQLDeleteAccountUserAccountsByExternalReferenceCodeByEmailAddress();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetAccountUserAccountsByExternalReferenceCodePage()
		throws Exception {

		super.testGraphQLGetAccountUserAccountsByExternalReferenceCodePage();
	}

	@Override
	@Test
	public void testGraphQLGetMyUserAccount() throws Exception {
		Assert.assertTrue(
			equals(
				userAccountResource.getUserAccount(_testUser.getUserId()),
				UserAccountSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"myUserAccount", getGraphQLFields())),
						"JSONObject/data", "JSONObject/myUserAccount"))));
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetUserAccountsByStatusPage() throws Exception {
		super.testGraphQLGetUserAccountsByStatusPage();
	}

	@Override
	@Test
	public void testGraphQLGetUserAccountsPage() throws Exception {
		GraphQLField graphQLField = new GraphQLField(
			"userAccounts",
			new HashMap<String, Object>() {
				{
					put("page", 1);
					put("pageSize", 10);
					put("search", null);
					put("sort", "\"dateCreated:desc\"");
				}
			},
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		// No namespace

		JSONObject userAccountsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/userAccounts");

		long totalCount = userAccountsJSONObject.getLong("totalCount");

		UserAccount userAccount1 = testGraphQLUserAccount_addUserAccount(
			randomUserAccount());

		UserAccount userAccount2 = testGraphQLUserAccount_addUserAccount(
			randomUserAccount());

		userAccountsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/userAccounts");

		Assert.assertEquals(
			totalCount + 2, userAccountsJSONObject.getLong("totalCount"));

		assertContains(
			userAccount1,
			Arrays.asList(
				UserAccountSerDes.toDTOs(
					userAccountsJSONObject.getString("items"))));
		assertContains(
			userAccount2,
			Arrays.asList(
				UserAccountSerDes.toDTOs(
					userAccountsJSONObject.getString("items"))));

		// Using the namespace headlessAdminUser_v1_0

		userAccountsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField("headlessAdminUser_v1_0", graphQLField)),
			"JSONObject/data", "JSONObject/headlessAdminUser_v1_0",
			"JSONObject/userAccounts");

		Assert.assertEquals(
			totalCount + 2, userAccountsJSONObject.getLong("totalCount"));

		assertContains(
			userAccount1,
			Arrays.asList(
				UserAccountSerDes.toDTOs(
					userAccountsJSONObject.getString("items"))));
		assertContains(
			userAccount2,
			Arrays.asList(
				UserAccountSerDes.toDTOs(
					userAccountsJSONObject.getString("items"))));
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLPostAccountUserAccount() throws Exception {
		super.testGraphQLPostAccountUserAccount();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLPostAccountUserAccountByExternalReferenceCode()
		throws Exception {

		super.testGraphQLPostAccountUserAccountByExternalReferenceCode();
	}

	@Override
	@Test
	public void testPatchSiteAccountUserAccountSelected() throws Exception {
		AccountEntry accountEntry1 = _addAccountEntry();
		User user = UserTestUtil.addUser();

		_accountEntryUserRelLocalService.addAccountEntryUserRel(
			accountEntry1.getAccountEntryId(), user.getUserId());

		Assert.assertFalse(
			userAccountResource.getSiteAccountUserAccountSelected(
				testGroup.getGroupId(), accountEntry1.getAccountEntryId(),
				user.getUserId()));

		userAccountResource.patchSiteAccountUserAccountSelected(
			testGroup.getGroupId(), accountEntry1.getAccountEntryId(),
			user.getUserId());

		Assert.assertTrue(
			userAccountResource.getSiteAccountUserAccountSelected(
				testGroup.getGroupId(), accountEntry1.getAccountEntryId(),
				user.getUserId()));

		AccountEntry accountEntry2 = _addAccountEntry();

		assertHttpResponseStatusCode(
			404,
			userAccountResource.patchSiteAccountUserAccountSelectedHttpResponse(
				testGroup.getGroupId(), accountEntry2.getAccountEntryId(),
				user.getUserId()));
	}

	@Override
	@Test
	public void testPatchSiteByFriendlyUrlPathAccountByExternalReferenceCodeAccountExternalReferenceCodeUserAccountByExternalReferenceCodeUserAccountExternalReferenceCodeSelected()
		throws Exception {

		AccountEntry accountEntry1 = _addAccountEntry();
		User user = UserTestUtil.addUser();

		_accountEntryUserRelLocalService.addAccountEntryUserRel(
			accountEntry1.getAccountEntryId(), user.getUserId());

		Assert.assertFalse(
			userAccountResource.
				getSiteByFriendlyUrlPathAccountByExternalReferenceCodeAccountExternalReferenceCodeUserAccountByExternalReferenceCodeUserAccountExternalReferenceCodeSelected(
					testGroup.getFriendlyURL(),
					accountEntry1.getExternalReferenceCode(),
					user.getExternalReferenceCode()));

		userAccountResource.
			patchSiteByFriendlyUrlPathAccountByExternalReferenceCodeAccountExternalReferenceCodeUserAccountByExternalReferenceCodeUserAccountExternalReferenceCodeSelected(
				testGroup.getFriendlyURL(),
				accountEntry1.getExternalReferenceCode(),
				user.getExternalReferenceCode());

		Assert.assertTrue(
			userAccountResource.
				getSiteByFriendlyUrlPathAccountByExternalReferenceCodeAccountExternalReferenceCodeUserAccountByExternalReferenceCodeUserAccountExternalReferenceCodeSelected(
					testGroup.getFriendlyURL(),
					accountEntry1.getExternalReferenceCode(),
					user.getExternalReferenceCode()));

		AccountEntry accountEntry2 = _addAccountEntry();

		assertHttpResponseStatusCode(
			404,
			userAccountResource.
				patchSiteByFriendlyUrlPathAccountByExternalReferenceCodeAccountExternalReferenceCodeUserAccountByExternalReferenceCodeUserAccountExternalReferenceCodeSelectedHttpResponse(
					testGroup.getFriendlyURL(),
					accountEntry2.getExternalReferenceCode(),
					user.getExternalReferenceCode()));
	}

	@Override
	@Test
	public void testPatchUserAccount() throws Exception {
		super.testPatchUserAccount();

		User user = UserTestUtil.addUser();

		long portraitId = RandomTestUtil.randomLong();

		user.setPortraitId(portraitId);

		user = _userLocalService.updateUser(user);

		UserAccount userAccount = new UserAccount();

		userAccount.setJobTitle(RandomTestUtil.randomString());

		userAccount = userAccountResource.patchUserAccount(
			user.getUserId(), userAccount);

		user = _userLocalService.getUser(userAccount.getId());

		Assert.assertEquals(portraitId, user.getPortraitId());

		String newPassword = RandomTestUtil.randomString();
		UserAccount patchUserAccount = testPatchUserAccount_addUserAccount();

		_assertAuthenticationResult(
			Authenticator.FAILURE, patchUserAccount.getEmailAddress(),
			newPassword);

		userAccountResource.patchUserAccount(
			patchUserAccount.getId(),
			new UserAccount() {
				{
					password = newPassword;
				}
			});

		_assertAuthenticationResult(
			Authenticator.SUCCESS, patchUserAccount.getEmailAddress(),
			newPassword);

		_setUpTestUserAccountResource();

		_assertProblem(
			"The user account password is invalid",
			() -> _regularUserAccountResource.patchUserAccountHttpResponse(
				_regularUserAccount.getId(),
				new UserAccount() {
					{
						password = newPassword;
					}
				}));

		_assertAuthenticationResult(
			Authenticator.FAILURE, _regularUserAccount.getEmailAddress(),
			newPassword);

		_regularUserAccountResource.patchUserAccount(
			_regularUserAccount.getId(),
			new UserAccount() {
				{
					currentPassword = _regularUserAccountCurrentPassword;
					password = newPassword;
				}
			});

		_assertAuthenticationResult(
			Authenticator.SUCCESS, _regularUserAccount.getEmailAddress(),
			newPassword);

		_setUpTestUserAccountResource();

		_regularUserAccountResource.patchUserAccount(
			_regularUserAccount.getId(),
			new UserAccount() {
				{
					givenName = RandomTestUtil.randomString();
				}
			});

		_testPatchUserAccountWithGender();
		_testPatchUserAccountWithImageExternalReferenceCode();
	}

	@Override
	@Test
	public void testPostAccountByExternalReferenceCodeUserAccountByExternalReferenceCode()
		throws Exception {

		UserAccount userAccount =
			userAccountResource.putUserAccountByExternalReferenceCode(
				StringUtil.toLowerCase(RandomTestUtil.randomString()),
				randomUserAccount());

		Assert.assertNull(
			_accountEntryUserRelLocalService.fetchAccountEntryUserRel(
				_accountEntry.getAccountEntryId(), userAccount.getId()));

		userAccountResource.
			postAccountByExternalReferenceCodeUserAccountByExternalReferenceCodeHttpResponse(
				_accountEntry.getExternalReferenceCode(),
				userAccount.getExternalReferenceCode());

		Assert.assertNotNull(
			_accountEntryUserRelLocalService.fetchAccountEntryUserRel(
				_accountEntry.getAccountEntryId(), userAccount.getId()));
	}

	@Override
	@Test
	public void testPostAccountUserAccount() throws Exception {
		super.testPostAccountUserAccount();

		UserAccount randomUserAccount = randomUserAccount();

		Assert.assertNull(
			_userLocalService.fetchUserByEmailAddress(
				TestPropsValues.getCompanyId(),
				randomUserAccount.getEmailAddress()));

		randomUserAccount = testPostAccountUserAccount_addUserAccount(
			randomUserAccount);

		Assert.assertNotNull(
			_userLocalService.fetchUserByEmailAddress(
				TestPropsValues.getCompanyId(),
				randomUserAccount.getEmailAddress()));

		Assert.assertNotNull(
			_accountEntryUserRelLocalService.fetchAccountEntryUserRel(
				_getAccountEntryId(), randomUserAccount.getId()));
	}

	@Override
	@Test
	public void testPostAccountUserAccountByEmailAddress() throws Exception {
		super.testPostAccountUserAccountByEmailAddress();

		User user = UserTestUtil.addUser();

		Assert.assertNull(
			_accountEntryUserRelLocalService.fetchAccountEntryUserRel(
				_accountEntry.getAccountEntryId(), user.getUserId()));

		userAccountResource.postAccountUserAccountByEmailAddress(
			_accountEntry.getAccountEntryId(), user.getEmailAddress());

		Assert.assertNotNull(
			_accountEntryUserRelLocalService.fetchAccountEntryUserRel(
				_accountEntry.getAccountEntryId(), user.getUserId()));
	}

	@Override
	@Test
	public void testPostAccountUserAccountByExternalReferenceCodeByEmailAddress()
		throws Exception {

		User user = UserTestUtil.addUser();

		Assert.assertNull(
			_accountEntryUserRelLocalService.fetchAccountEntryUserRel(
				_accountEntry.getAccountEntryId(), user.getUserId()));

		userAccountResource.
			postAccountUserAccountByExternalReferenceCodeByEmailAddress(
				_accountEntry.getExternalReferenceCode(),
				user.getEmailAddress());

		Assert.assertNotNull(
			_accountEntryUserRelLocalService.fetchAccountEntryUserRel(
				_accountEntry.getAccountEntryId(), user.getUserId()));
	}

	@Override
	@Test
	public void testPostAccountUserAccountsByEmailAddress() throws Exception {
		List<User> users = Arrays.asList(
			UserTestUtil.addUser(), UserTestUtil.addUser(),
			UserTestUtil.addUser(), UserTestUtil.addUser());

		for (User user : users) {
			Assert.assertNull(
				_accountEntryUserRelLocalService.fetchAccountEntryUserRel(
					_accountEntry.getAccountEntryId(), user.getUserId()));
		}

		userAccountResource.postAccountUserAccountsByEmailAddress(
			_accountEntry.getAccountEntryId(), null, _toEmailAddresses(users));

		for (User user : users) {
			Assert.assertNotNull(
				_accountEntryUserRelLocalService.fetchAccountEntryUserRel(
					_accountEntry.getAccountEntryId(), user.getUserId()));
		}
	}

	@Override
	@Test
	public void testPostAccountUserAccountsByExternalReferenceCodeByEmailAddress()
		throws Exception {

		List<User> users = Arrays.asList(
			UserTestUtil.addUser(), UserTestUtil.addUser(),
			UserTestUtil.addUser(), UserTestUtil.addUser());

		for (User user : users) {
			Assert.assertNull(
				_accountEntryUserRelLocalService.fetchAccountEntryUserRel(
					_accountEntry.getAccountEntryId(), user.getUserId()));
		}

		userAccountResource.
			postAccountUserAccountsByExternalReferenceCodeByEmailAddress(
				_accountEntry.getExternalReferenceCode(), null,
				_toEmailAddresses(users));

		for (User user : users) {
			Assert.assertNotNull(
				_accountEntryUserRelLocalService.fetchAccountEntryUserRel(
					_accountEntry.getAccountEntryId(), user.getUserId()));
		}

		_testPostAccountUserAccountsByExternalReferenceCodeByEmailAddressWithRoleId();
	}

	@Override
	@Test
	public void testPostUserAccount() throws Exception {
		super.testPostUserAccount();

		_testPostUserAccountBatch();
		_testPostUserAccountWithApprovalWorkflow();
		_testPostUserAccountWithCaptcha();
		_testPostUserAccountWithGender();
		_testPostUserAccountWithImageExternalReferenceCode();
		_testPostUserAccountWithObjectValidationRule();
	}

	@Override
	@Test
	public void testPostUserAccountImage() throws Exception {
		UserAccount postUserAccount = userAccountResource.postUserAccount(
			null, null, randomUserAccount());

		Assert.assertNull(postUserAccount.getImage());

		userAccountResource.postUserAccountImage(
			postUserAccount.getId(), postUserAccount,
			Collections.singletonMap(
				"image",
				FileUtil.createTempFile(
					FileUtil.getBytes(getClass(), "/images/liferay.png"))));

		postUserAccount = userAccountResource.getUserAccount(
			postUserAccount.getId());

		Assert.assertNotNull(postUserAccount.getImage());
	}

	@Override
	@Test
	public void testPutUserAccount() throws Exception {
		super.testPutUserAccount();

		String newPassword = RandomTestUtil.randomString();
		UserAccount putUserAccount = testPutUserAccount_addUserAccount();

		_assertAuthenticationResult(
			Authenticator.FAILURE, putUserAccount.getEmailAddress(),
			newPassword);

		putUserAccount.setPassword(newPassword);

		userAccountResource.putUserAccount(
			putUserAccount.getId(), putUserAccount);

		_assertAuthenticationResult(
			Authenticator.SUCCESS, putUserAccount.getEmailAddress(),
			newPassword);

		_setUpTestUserAccountResource();

		_regularUserAccount.setPassword(newPassword);

		_assertProblem(
			"The user account password is invalid",
			() -> _regularUserAccountResource.putUserAccountHttpResponse(
				_regularUserAccount.getId(), _regularUserAccount));

		_assertAuthenticationResult(
			Authenticator.FAILURE, _regularUserAccount.getEmailAddress(),
			newPassword);

		_regularUserAccount.setCurrentPassword(
			_regularUserAccountCurrentPassword);

		_regularUserAccountResource.putUserAccount(
			_regularUserAccount.getId(), _regularUserAccount);

		_assertAuthenticationResult(
			Authenticator.SUCCESS, _regularUserAccount.getEmailAddress(),
			newPassword);

		_setUpTestUserAccountResource();

		_regularUserAccountResource.putUserAccount(
			_regularUserAccount.getId(),
			_randomUserAccount(
				userAccount -> {
					userAccount.setCurrentPassword(() -> null);
					userAccount.setPassword(() -> null);
				}));

		_testPutUserAccountWithImageExternalReferenceCode();
	}

	@Override
	@Test
	public void testPutUserAccountByExternalReferenceCode() throws Exception {
		super.testPutUserAccountByExternalReferenceCode();

		String newPassword = RandomTestUtil.randomString();
		UserAccount putUserAccount =
			testPutUserAccountByExternalReferenceCode_addUserAccount();

		_assertAuthenticationResult(
			Authenticator.FAILURE, putUserAccount.getEmailAddress(),
			newPassword);

		putUserAccount.setPassword(newPassword);

		userAccountResource.putUserAccountByExternalReferenceCode(
			putUserAccount.getExternalReferenceCode(), putUserAccount);

		_assertAuthenticationResult(
			Authenticator.SUCCESS, putUserAccount.getEmailAddress(),
			newPassword);

		_setUpTestUserAccountResource();

		_regularUserAccount.setPassword(newPassword);

		_assertProblem(
			"The user account password is invalid",
			() ->
				_regularUserAccountResource.
					putUserAccountByExternalReferenceCodeHttpResponse(
						_regularUserAccount.getExternalReferenceCode(),
						_regularUserAccount));

		_assertAuthenticationResult(
			Authenticator.FAILURE, _regularUserAccount.getEmailAddress(),
			newPassword);

		_regularUserAccount.setCurrentPassword(
			_regularUserAccountCurrentPassword);

		_regularUserAccountResource.putUserAccountByExternalReferenceCode(
			_regularUserAccount.getExternalReferenceCode(),
			_regularUserAccount);

		_assertAuthenticationResult(
			Authenticator.SUCCESS, _regularUserAccount.getEmailAddress(),
			newPassword);

		_setUpTestUserAccountResource();

		_regularUserAccountResource.putUserAccountByExternalReferenceCode(
			_regularUserAccount.getExternalReferenceCode(),
			_randomUserAccount(
				userAccount -> {
					userAccount.setCurrentPassword(() -> null);
					userAccount.setPassword(() -> null);
				}));

		_testPutUserAccountByExternalReferenceCodeWithImageExternalReferenceCode();
	}

	@Override
	protected void assertEquals(
		UserAccount userAccount1, UserAccount userAccount2) {

		super.assertEquals(userAccount1, userAccount2);

		UserAccountContactInformation userAccountContactInformation1 =
			userAccount1.getUserAccountContactInformation();
		UserAccountContactInformation userAccountContactInformation2 =
			userAccount2.getUserAccountContactInformation();

		Assert.assertEquals(
			StringUtil.lowerCase(userAccountContactInformation1.getFacebook()),
			StringUtil.lowerCase(userAccountContactInformation2.getFacebook()));
		Assert.assertEquals(
			StringUtil.lowerCase(userAccountContactInformation1.getJabber()),
			StringUtil.lowerCase(userAccountContactInformation2.getJabber()));
		Assert.assertEquals(
			StringUtil.lowerCase(userAccountContactInformation1.getSkype()),
			StringUtil.lowerCase(userAccountContactInformation2.getSkype()));
		Assert.assertEquals(
			StringUtil.lowerCase(userAccountContactInformation1.getSms()),
			StringUtil.lowerCase(userAccountContactInformation2.getSms()));
		Assert.assertEquals(
			StringUtil.lowerCase(userAccountContactInformation1.getTwitter()),
			StringUtil.lowerCase(userAccountContactInformation2.getTwitter()));

		_assertUserAccountContactInformation(
			userAccountContactInformation1, userAccountContactInformation2,
			"emailAddresses", "emailAddress");
		_assertUserAccountContactInformation(
			userAccountContactInformation1, userAccountContactInformation2,
			"postalAddresses", "streetAddressLine1");
		_assertUserAccountContactInformation(
			userAccountContactInformation1, userAccountContactInformation2,
			"telephones", "phoneNumber");
		_assertUserAccountContactInformation(
			userAccountContactInformation1, userAccountContactInformation2,
			"webUrls", "url");
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"alternateName", "familyName", "givenName"};
	}

	@Override
	protected String[] getIgnoredEntityFieldNames() {
		return new String[] {
			"alternateName", "emailAddress", "hasLoginDate", "name", "status"
		};
	}

	@Override
	protected UserAccount randomUserAccount() throws Exception {
		UserAccount userAccount = super.randomUserAccount();

		userAccount.setBirthDate(
			() -> {
				Calendar calendar = CalendarFactoryUtil.getCalendar();

				calendar.setTime(RandomTestUtil.nextDate());
				calendar.set(Calendar.HOUR_OF_DAY, 0);
				calendar.set(Calendar.MINUTE, 0);
				calendar.set(Calendar.SECOND, 0);
				calendar.set(Calendar.MILLISECOND, 0);

				return calendar.getTime();
			});
		userAccount.setImageId(0L);
		userAccount.setUserAccountContactInformation(
			_randomUserAccountContactInformation());

		return userAccount;
	}

	@Override
	protected UserAccount
			testDeleteAccountByExternalReferenceCodeUserAccountByExternalReferenceCode_addUserAccount()
		throws Exception {

		return _addUserAccount(
			testGroup.getGroupId(), _accountEntry, randomUserAccount());
	}

	@Override
	protected String
			testDeleteAccountByExternalReferenceCodeUserAccountByExternalReferenceCode_getAccountExternalReferenceCode()
		throws Exception {

		return _accountEntry.getExternalReferenceCode();
	}

	@Override
	protected UserAccount testDeleteAccountUserAccount_addUserAccount()
		throws Exception {

		return _addUserAccount(
			testGroup.getGroupId(), _accountEntry, randomUserAccount());
	}

	@Override
	protected Long testDeleteAccountUserAccount_getAccountId()
		throws Exception {

		return _accountEntry.getAccountEntryId();
	}

	@Override
	protected UserAccount
			testDeleteAccountUserAccountByEmailAddress_addUserAccount()
		throws Exception {

		return _addUserAccount(
			testGroup.getGroupId(), _accountEntry, randomUserAccount());
	}

	@Override
	protected Long testDeleteAccountUserAccountByEmailAddress_getAccountId()
		throws Exception {

		return _accountEntry.getAccountEntryId();
	}

	@Override
	protected UserAccount
			testDeleteAccountUserAccountByExternalReferenceCodeByEmailAddress_addUserAccount()
		throws Exception {

		return _addUserAccount(
			testGroup.getGroupId(), _accountEntry, randomUserAccount());
	}

	@Override
	protected String
			testDeleteAccountUserAccountByExternalReferenceCodeByEmailAddress_getExternalReferenceCode(
				UserAccount userAccount)
		throws Exception {

		return _accountEntry.getExternalReferenceCode();
	}

	@Override
	protected UserAccount
			testDeleteAccountUserAccountsByEmailAddress_addUserAccount()
		throws Exception {

		return _addUserAccount(testGroup.getGroupId(), randomUserAccount());
	}

	@Override
	protected UserAccount testDeleteUserAccount_addUserAccount()
		throws Exception {

		return _addUserAccount(testGroup.getGroupId(), randomUserAccount());
	}

	@Override
	protected UserAccount
			testDeleteUserAccountByExternalReferenceCode_addUserAccount()
		throws Exception {

		return userAccountResource.putUserAccountByExternalReferenceCode(
			StringUtil.toLowerCase(RandomTestUtil.randomString()),
			randomUserAccount());
	}

	@Override
	protected UserAccount
			testGetAccountByExternalReferenceCodeUserAccountByExternalReferenceCode_addUserAccount()
		throws Exception {

		return _addAccountUserAccount(
			_getAccountEntryId(), randomUserAccount());
	}

	@Override
	protected String
			testGetAccountByExternalReferenceCodeUserAccountByExternalReferenceCode_getAccountExternalReferenceCode()
		throws Exception {

		return _accountEntry.getExternalReferenceCode();
	}

	@Override
	protected UserAccount testGetAccountUserAccount_addUserAccount()
		throws Exception {

		return _addAccountUserAccount(
			_getAccountEntryId(), randomUserAccount());
	}

	@Override
	protected Long testGetAccountUserAccount_getAccountId() throws Exception {
		return _accountEntry.getAccountEntryId();
	}

	@Override
	protected UserAccount
			testGetAccountUserAccountsByExternalReferenceCodePage_addUserAccount(
				String externalReferenceCode, UserAccount userAccount)
		throws Exception {

		return userAccountResource.
			postAccountUserAccountByExternalReferenceCode(
				externalReferenceCode, userAccount);
	}

	@Override
	protected String
		testGetAccountUserAccountsByExternalReferenceCodePage_getExternalReferenceCode() {

		return _accountEntry.getExternalReferenceCode();
	}

	@Override
	protected UserAccount testGetAccountUserAccountsPage_addUserAccount(
			Long accountId, UserAccount userAccount)
		throws Exception {

		return userAccountResource.postAccountUserAccount(
			accountId, userAccount);
	}

	@Override
	protected Long testGetAccountUserAccountsPage_getAccountId() {
		return _getAccountEntryId();
	}

	@Override
	protected Map<String, Map<String, String>>
			testGetAccountUserAccountsPage_getExpectedActions(Long accountId)
		throws Exception {

		return Collections.emptyMap();
	}

	@Override
	protected UserAccount testGetMyUserAccount_addUserAccount()
		throws Exception {

		return userAccountResource.getUserAccount(_testUser.getUserId());
	}

	@Override
	protected UserAccount
			testGetOrganizationByExternalReferenceCodeUserAccountsPage_addUserAccount(
				String externalReferenceCode, UserAccount userAccount)
		throws Exception {

		userAccount = _addUserAccount(
			testGetSiteUserAccountsPage_getSiteId(), userAccount);

		_userLocalService.addOrganizationUser(
			_organization.getOrganizationId(), userAccount.getId());

		return userAccount;
	}

	@Override
	protected String
			testGetOrganizationByExternalReferenceCodeUserAccountsPage_getExternalReferenceCode()
		throws Exception {

		return _organization.getExternalReferenceCode();
	}

	@Override
	protected UserAccount testGetOrganizationUserAccountsPage_addUserAccount(
			String organizationId, UserAccount userAccount)
		throws Exception {

		userAccount = _addUserAccount(
			testGetSiteUserAccountsPage_getSiteId(), userAccount);

		_userLocalService.addOrganizationUser(
			GetterUtil.getLong(organizationId), userAccount.getId());

		return userAccount;
	}

	@Override
	protected String testGetOrganizationUserAccountsPage_getOrganizationId() {
		return String.valueOf(_organization.getOrganizationId());
	}

	@Override
	protected UserAccount testGetSiteUserAccountsPage_addUserAccount(
			Long siteId, UserAccount userAccount)
		throws Exception {

		return _addUserAccount(siteId, userAccount);
	}

	@Override
	protected Long testGetSiteUserAccountsPage_getSiteId() {
		return testGroup.getGroupId();
	}

	@Override
	protected UserAccount testGetUserAccount_addUserAccount() throws Exception {
		return _addUserAccount(
			testGetSiteUserAccountsPage_getSiteId(), randomUserAccount());
	}

	@Override
	protected UserAccount testGetUserAccountByEmailAddress_addUserAccount()
		throws Exception {

		return userAccountResource.postUserAccount(
			null, null, randomUserAccount());
	}

	@Override
	protected UserAccount
			testGetUserAccountByExternalReferenceCode_addUserAccount()
		throws Exception {

		return userAccountResource.putUserAccountByExternalReferenceCode(
			StringUtil.toLowerCase(RandomTestUtil.randomString()),
			randomUserAccount());
	}

	@Override
	protected UserAccount testGetUserAccountsByStatusPage_addUserAccount(
			String status, UserAccount userAccount)
		throws Exception {

		UserAccount postUserAccount = testPostAccountUserAccount_addUserAccount(
			userAccount);

		if (StringUtil.equalsIgnoreCase(
				status,
				com.liferay.headless.admin.user.dto.v1_0.UserAccount.Status.
					INACTIVE.toString())) {

			PermissionChecker originalPermissionChecker =
				PermissionThreadLocal.getPermissionChecker();

			String originalName = PrincipalThreadLocal.getName();

			try {
				PermissionThreadLocal.setPermissionChecker(
					PermissionCheckerFactoryUtil.create(
						TestPropsValues.getUser()));

				PrincipalThreadLocal.setName(TestPropsValues.getUserId());

				_userService.updateStatus(
					postUserAccount.getId(), WorkflowConstants.STATUS_INACTIVE,
					ServiceContextTestUtil.getServiceContext(
						testCompany.getCompanyId(), testGroup.getGroupId(),
						_testUser.getUserId()));
			}
			finally {
				PermissionThreadLocal.setPermissionChecker(
					originalPermissionChecker);

				PrincipalThreadLocal.setName(originalName);
			}
		}

		return postUserAccount;
	}

	@Override
	protected String testGetUserAccountsByStatusPage_getStatus()
		throws Exception {

		return com.liferay.headless.admin.user.dto.v1_0.UserAccount.Status.
			INACTIVE.toString();
	}

	@Override
	protected UserAccount testGetUserAccountsPage_addUserAccount(
			UserAccount userAccount)
		throws Exception {

		return _addUserAccount(testGroup.getGroupId(), userAccount);
	}

	@Override
	protected UserAccount
			testGetUserGroupByExternalReferenceCodeUsersPage_addUserAccount(
				String externalReferenceCode, UserAccount userAccount)
		throws Exception {

		userAccount = _addUserAccount(testGroup.getGroupId(), userAccount);

		_userLocalService.addUserGroupUser(
			_userGroup.getUserGroupId(), userAccount.getId());

		return userAccount;
	}

	@Override
	protected String
			testGetUserGroupByExternalReferenceCodeUsersPage_getExternalReferenceCode()
		throws Exception {

		return _userGroup.getExternalReferenceCode();
	}

	@Override
	protected UserAccount testGetUserGroupUsersPage_addUserAccount(
			Long userGroupId, UserAccount userAccount)
		throws Exception {

		userAccount = _addUserAccount(testGroup.getGroupId(), userAccount);

		_userLocalService.addUserGroupUser(userGroupId, userAccount.getId());

		return userAccount;
	}

	@Override
	protected Long testGetUserGroupUsersPage_getUserGroupId() throws Exception {
		return _userGroup.getUserGroupId();
	}

	@Override
	protected String
			testGraphQLDeleteAccountByExternalReferenceCodeUserAccountByExternalReferenceCode_getAccountExternalReferenceCode()
		throws Exception {

		return _accountEntry.getExternalReferenceCode();
	}

	@Override
	protected Long testGraphQLDeleteAccountUserAccount_getAccountId()
		throws Exception {

		return _accountEntry.getAccountEntryId();
	}

	@Override
	protected Long
			testGraphQLDeleteAccountUserAccountByEmailAddress_getAccountId()
		throws Exception {

		return _accountEntry.getAccountEntryId();
	}

	@Override
	protected String
			testGraphQLDeleteAccountUserAccountByExternalReferenceCodeByEmailAddress_getExternalReferenceCode(
				UserAccount userAccount)
		throws Exception {

		return _accountEntry.getExternalReferenceCode();
	}

	@Override
	protected Long
			testGraphQLDeleteAccountUserAccountsByEmailAddress_getAccountId()
		throws Exception {

		return _accountEntry.getAccountEntryId();
	}

	@Override
	protected String
			testGraphQLDeleteAccountUserAccountsByExternalReferenceCodeByEmailAddress_getExternalReferenceCode(
				UserAccount userAccount)
		throws Exception {

		return _accountEntry.getExternalReferenceCode();
	}

	@Override
	protected UserAccount
			testGraphQLGetAccountByExternalReferenceCodeUserAccountByExternalReferenceCode_addUserAccount()
		throws Exception {

		return _addUserAccount(
			testGroup.getGroupId(), _accountEntry, randomUserAccount());
	}

	@Override
	protected String
			testGraphQLGetAccountByExternalReferenceCodeUserAccountByExternalReferenceCode_getAccountExternalReferenceCode()
		throws Exception {

		return _accountEntry.getExternalReferenceCode();
	}

	@Override
	protected UserAccount testGraphQLGetAccountUserAccount_addUserAccount()
		throws Exception {

		return _addUserAccount(
			testGroup.getGroupId(), _accountEntry, randomUserAccount());
	}

	@Override
	protected Long testGraphQLGetAccountUserAccount_getAccountId()
		throws Exception {

		return _accountEntry.getAccountEntryId();
	}

	@Override
	protected UserAccount testGraphQLUserAccount_addUserAccount()
		throws Exception {

		return _addUserAccount(
			testGetSiteUserAccountsPage_getSiteId(), randomUserAccount());
	}

	@Override
	protected UserAccount testPatchUserAccount_addUserAccount()
		throws Exception {

		return _addUserAccount(testGroup.getGroupId(), randomUserAccount());
	}

	@Override
	protected UserAccount
			testPatchUserAccountByExternalReferenceCode_addUserAccount()
		throws Exception {

		return _addUserAccount(testGroup.getGroupId(), randomUserAccount());
	}

	@Override
	protected UserAccount testPostAccountUserAccount_addUserAccount(
			UserAccount userAccount)
		throws Exception {

		return _addAccountUserAccount(_getAccountEntryId(), userAccount);
	}

	@Override
	protected UserAccount
			testPostAccountUserAccountByEmailAddress_addUserAccount(
				UserAccount userAccount)
		throws Exception {

		return _addUserAccount(testGroup.getGroupId(), userAccount);
	}

	@Override
	protected UserAccount
			testPostAccountUserAccountByExternalReferenceCode_addUserAccount(
				UserAccount userAccount)
		throws Exception {

		return userAccountResource.
			postAccountUserAccountByExternalReferenceCode(
				_accountEntry.getExternalReferenceCode(), userAccount);
	}

	@Override
	protected UserAccount testPostUserAccount_addUserAccount(
			UserAccount userAccount)
		throws Exception {

		return _addUserAccount(testGroup.getGroupId(), userAccount);
	}

	@Override
	protected UserAccount testPutUserAccount_addUserAccount() throws Exception {
		return _addUserAccount(testGroup.getGroupId(), randomUserAccount());
	}

	@Override
	protected UserAccount
			testPutUserAccountByExternalReferenceCode_addUserAccount()
		throws Exception {

		return userAccountResource.putUserAccountByExternalReferenceCode(
			StringUtil.toLowerCase(RandomTestUtil.randomString()),
			randomUserAccount());
	}

	private AccountEntry _addAccountEntry() throws Exception {
		return _accountEntryLocalService.addAccountEntry(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT,
			RandomTestUtil.randomString(20), RandomTestUtil.randomString(20),
			null, null, null, null,
			AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS,
			WorkflowConstants.STATUS_APPROVED,
			ServiceContextTestUtil.getServiceContext());
	}

	private UserAccount _addAccountUserAccount(
			Long accountId, UserAccount userAccount)
		throws Exception {

		return userAccountResource.postAccountUserAccount(
			accountId, userAccount);
	}

	private ExpandoColumn _addExpandoColumn(
			int expandoColumnType, ExpandoTable expandoTable)
		throws Exception {

		ExpandoColumn expandoColumn = _expandoColumnLocalService.addColumn(
			expandoTable.getTableId(), "A" + RandomTestUtil.randomString(),
			expandoColumnType);

		UnicodeProperties unicodeProperties =
			expandoColumn.getTypeSettingsProperties();

		unicodeProperties.setProperty(
			ExpandoColumnConstants.INDEX_TYPE,
			String.valueOf(ExpandoColumnConstants.INDEX_TYPE_KEYWORD));

		expandoColumn.setTypeSettingsProperties(unicodeProperties);

		return _expandoColumnLocalService.updateExpandoColumn(expandoColumn);
	}

	private FileEntry _addImageFileEntry() throws Exception {
		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		Group group = company.getGroup();

		LocalRepository localRepository =
			RepositoryProviderUtil.getLocalRepository(group.getGroupId());

		byte[] bytes = FileUtil.getBytes(getClass(), "/images/liferay.png");

		InputStream inputStream = new UnsyncByteArrayInputStream(bytes);

		return localRepository.addFileEntry(
			null, TestPropsValues.getUserId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), ContentTypes.IMAGE_PNG,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			StringPool.BLANK, StringPool.BLANK, inputStream, bytes.length, null,
			null, null,
			ServiceContextTestUtil.getServiceContext(group.getGroupId()));
	}

	private UserAccount _addUserAccount(
			long siteId, AccountEntry accountEntry, UserAccount userAccount)
		throws Exception {

		userAccount = _addUserAccount(siteId, userAccount);

		_accountEntryUserRelLocalService.addAccountEntryUserRel(
			accountEntry.getAccountEntryId(), userAccount.getId());

		return userAccount;
	}

	private UserAccount _addUserAccount(long siteId, UserAccount userAccount)
		throws Exception {

		userAccount = userAccountResource.postUserAccount(
			null, null, userAccount);

		_userLocalService.addGroupUser(siteId, userAccount.getId());

		return userAccount;
	}

	private void _assertAuthenticationResult(
			int authenticatorResult, String emailAddress, String password)
		throws Exception {

		Assert.assertEquals(
			authenticatorResult,
			_userLocalService.authenticateByEmailAddress(
				testCompany.getCompanyId(), emailAddress, password,
				Collections.emptyMap(), Collections.emptyMap(),
				new HashMap<>()));
	}

	private <T extends Exception> void _assertProblem(
			String errorMessage,
			UnsafeSupplier<HttpInvoker.HttpResponse, Exception>
				httpResponseUnsafeSupplier)
		throws Exception {

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				BaseExceptionMapper.class.getName(), LoggerTestUtil.OFF)) {

			HttpInvoker.HttpResponse httpResponse =
				httpResponseUnsafeSupplier.get();

			Assert.assertEquals(
				Response.Status.BAD_REQUEST.getStatusCode(),
				httpResponse.getStatusCode());

			if (Validator.isNotNull(errorMessage)) {
				JSONObject jsonObject = _jsonFactory.createJSONObject(
					httpResponse.getContent());

				String title = jsonObject.getString("title");

				Assert.assertEquals(errorMessage, title);
			}
		}
	}

	private void _assertUserAccountContactInformation(
		UserAccountContactInformation userAccountContactInformation1,
		UserAccountContactInformation userAccountContactInformation2,
		String fieldName, String subfieldName) {

		try {
			Object[] objects1 = ReflectionTestUtil.getFieldValue(
				userAccountContactInformation1, fieldName);
			Object[] objects2 = ReflectionTestUtil.getFieldValue(
				userAccountContactInformation2, fieldName);

			Assert.assertEquals(
				Arrays.toString(objects1), objects1.length, objects2.length);

			Comparator<Object> comparator = Comparator.comparing(
				object -> ReflectionTestUtil.getFieldValue(
					object, subfieldName));

			Arrays.sort(objects1, comparator);
			Arrays.sort(objects2, comparator);

			for (int i = 0; i < objects1.length; i++) {
				Assert.assertEquals(
					(String)ReflectionTestUtil.getFieldValue(
						objects1[i], subfieldName),
					(String)ReflectionTestUtil.getFieldValue(
						objects2[i], subfieldName));
			}
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	private Long _getAccountEntryId() {
		return _accountEntry.getAccountEntryId();
	}

	private RoleBrief[] _getUserAccountRoleBriefs(UserAccount userAccount) {
		RoleBrief[] roleBriefs = userAccount.getRoleBriefs();

		for (OrganizationBrief organizationBrief :
				userAccount.getOrganizationBriefs()) {

			roleBriefs = ArrayUtil.append(
				roleBriefs, organizationBrief.getRoleBriefs());
		}

		for (SiteBrief siteBrief : userAccount.getSiteBriefs()) {
			roleBriefs = ArrayUtil.append(
				roleBriefs, siteBrief.getRoleBriefs());
		}

		return roleBriefs;
	}

	private boolean _hasRole(Role role, RoleBrief[] roleBriefs) {
		for (RoleBrief roleBrief : roleBriefs) {
			if (Objects.equals(role.getRoleId(), roleBrief.getId())) {
				return true;
			}
		}

		return false;
	}

	private boolean _hasRole(Role role, User user) throws Exception {
		return _hasRole(
			role,
			_getUserAccountRoleBriefs(
				userAccountResource.getUserAccount(user.getUserId())));
	}

	private EmailAddress _randomEmailAddress() throws Exception {
		return new EmailAddress() {
			{
				emailAddress = RandomTestUtil.randomString() + "@liferay.com";
				primary = true;
				type = "email-address";
			}
		};
	}

	private Phone _randomPhone() throws Exception {
		return new Phone() {
			{
				extension = String.valueOf(RandomTestUtil.randomInt());
				phoneNumber = String.valueOf(RandomTestUtil.randomInt());
				phoneType = "personal";
				primary = true;
			}
		};
	}

	private PostalAddress _randomPostalAddress() throws Exception {
		return new PostalAddress() {
			{
				addressCountry = "united-states";
				addressLocality = "Diamond Bar";
				addressRegion = "California";
				addressType = "personal";
				postalCode = "91765";
				primary = true;
				streetAddressLine1 = RandomTestUtil.randomString();
				streetAddressLine2 = RandomTestUtil.randomString();
				streetAddressLine3 = RandomTestUtil.randomString();
			}
		};
	}

	private UserAccount _randomUserAccount(
			Consumer<UserAccount> userAccountConsumer)
		throws Exception {

		UserAccount randomUserAccount = randomUserAccount();

		userAccountConsumer.accept(randomUserAccount);

		return randomUserAccount;
	}

	private UserAccountContactInformation _randomUserAccountContactInformation()
		throws Exception {

		return new UserAccountContactInformation() {
			{
				emailAddresses = new EmailAddress[] {_randomEmailAddress()};
				facebook = RandomTestUtil.randomString();
				jabber = RandomTestUtil.randomString();
				postalAddresses = new PostalAddress[] {_randomPostalAddress()};
				skype = RandomTestUtil.randomString();
				sms = RandomTestUtil.randomString() + "@liferay.com";
				telephones = new Phone[] {_randomPhone()};
				twitter = RandomTestUtil.randomString();
				webUrls = new WebUrl[] {_randomWebUrl()};
			}
		};
	}

	private WebUrl _randomWebUrl() throws Exception {
		return new WebUrl() {
			{
				primary = true;
				url = "https://" + RandomTestUtil.randomString() + ".com";
				urlType = "personal";
			}
		};
	}

	private void _setUpTestUserAccountResource() throws Exception {
		_regularUserAccountCurrentPassword = RandomTestUtil.randomString();

		_regularUserAccount = _addUserAccount(
			testGroup.getGroupId(), randomUserAccount());

		_userLocalService.updatePassword(
			_regularUserAccount.getId(), _regularUserAccountCurrentPassword,
			_regularUserAccountCurrentPassword, false, true);

		UserAccountResource.Builder builder = UserAccountResource.builder();

		_regularUserAccountResource = builder.authentication(
			_regularUserAccount.getEmailAddress(),
			_regularUserAccountCurrentPassword
		).locale(
			LocaleUtil.getDefault()
		).build();
	}

	private void _testGetUserAccountsPage(
			String filterString, UserAccount... expectedUserAccounts)
		throws Exception {

		Page<UserAccount> page = userAccountResource.getUserAccountsPage(
			null, filterString,
			Pagination.of(1, expectedUserAccounts.length + 1), null);

		Assert.assertEquals(expectedUserAccounts.length, page.getTotalCount());

		assertEqualsIgnoringOrder(
			Arrays.asList(expectedUserAccounts),
			(List<UserAccount>)page.getItems());

		if (expectedUserAccounts.length > 0) {
			assertValid(page);
		}
	}

	private void _testGetUserAccountsPageWithBirthDateFilter()
		throws Exception {

		UserAccount userAccount1 = randomUserAccount();

		Calendar calendar = CalendarFactoryUtil.getCalendar();

		calendar.set(Calendar.YEAR, 1990);

		userAccount1.setBirthDate(calendar.getTime());

		userAccount1 = testGetUserAccountsPage_addUserAccount(userAccount1);

		testGetUserAccountsPage_addUserAccount(randomUserAccount());

		DateFormat dateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
			"yyyy-MM-dd");

		_testGetUserAccountsPage("(birthDate eq 1979-01-01)");
		_testGetUserAccountsPage(
			StringBundler.concat(
				"(birthDate eq ", dateFormat.format(calendar.getTime()), ")"),
			userAccount1);
	}

	private void _testGetUserAccountsPageWithCustomFields() throws Exception {
		ExpandoTable expandoTable = _expandoTableLocalService.addTable(
			testGroup.getCompanyId(),
			_classNameLocalService.getClassNameId(User.class),
			ExpandoTableConstants.DEFAULT_TABLE_NAME);

		Function<Number, List<String>> function = number -> Arrays.asList(
			number.toString(), "0" + number, "00" + number, number + "0",
			number + "00");

		_testGetUserAccountsPageWithCustomFields(
			ExpandoColumnConstants.DOUBLE, expandoTable, function,
			RandomTestUtil::randomDouble);
		_testGetUserAccountsPageWithCustomFields(
			ExpandoColumnConstants.FLOAT, expandoTable, function,
			RandomTestUtil::randomFloat);

		_testGetUserAccountsPageWithCustomFields(
			ExpandoColumnConstants.STRING, expandoTable,
			value -> List.of(StringUtil.quote(value)),
			RandomTestUtil::randomString);
	}

	private <T> void _testGetUserAccountsPageWithCustomFields(
			int expandoColumnType, ExpandoTable expandoTable,
			Function<T, List<String>> function, Supplier<T> supplier)
		throws Exception {

		ExpandoColumn expandoColumn = _addExpandoColumn(
			expandoColumnType, expandoTable);

		UserAccount userAccount = randomUserAccount();

		T value = supplier.get();

		userAccount.setCustomFields(
			() -> new CustomField[] {
				new CustomField() {
					{
						customValue = new CustomValue() {
							{
								data = value;
							}
						};
						name = expandoColumn.getName();
					}
				}
			});

		userAccount = testGetUserAccountsPage_addUserAccount(userAccount);

		for (String filterString : function.apply(value)) {
			_testGetUserAccountsPage(
				StringBundler.concat(
					"(customFields/", expandoColumn.getName(), " eq ",
					filterString, ")"),
				userAccount);
		}

		for (String filterString : function.apply(supplier.get())) {
			_testGetUserAccountsPage(
				StringBundler.concat(
					"(customFields/", expandoColumn.getName(), " eq ",
					filterString, ")"));
		}
	}

	private void _testGetUserAccountsPageWithSortCustomField()
		throws Exception {

		ExpandoTable expandoTable = _expandoTableLocalService.addTable(
			testGroup.getCompanyId(),
			_classNameLocalService.getClassNameId(User.class),
			ExpandoTableConstants.DEFAULT_TABLE_NAME);

		_testGetUserAccountsPageWithSortCustomField(
			expandoTable, ExpandoColumnConstants.DATE,
			Arrays.asList(
				"2000-07-27T00:00:00Z", "2000-07-27T10:00:00Z",
				"2000-07-28T00:00:00Z"));
		_testGetUserAccountsPageWithSortCustomField(
			expandoTable, ExpandoColumnConstants.DOUBLE,
			Arrays.asList(1.001, 01.01, 001.1));
		_testGetUserAccountsPageWithSortCustomField(
			expandoTable, ExpandoColumnConstants.FLOAT,
			Arrays.asList(1.001F, 01.01F, 001.1F));
	}

	private void _testGetUserAccountsPageWithSortCustomField(
			ExpandoTable expandoTable, int expandoColumnType,
			List<Object> values)
		throws Exception {

		String domainName = StringUtil.randomString() + ".com";
		ExpandoColumn expandoColumn = _addExpandoColumn(
			expandoColumnType, expandoTable);

		List<UserAccount> userAccounts = TransformUtil.transform(
			values,
			value -> userAccountResource.postUserAccount(
				null, null,
				_randomUserAccount(
					userAccount -> {
						userAccount.setCustomFields(
							() -> new CustomField[] {
								new CustomField() {
									{
										customValue = new CustomValue() {
											{
												data = value;
											}
										};
										name = expandoColumn.getName();
									}
								}
							});

						userAccount.setEmailAddress(
							RandomTestUtil.randomString() + '@' + domainName);
					})));

		Page<UserAccount> page = userAccountResource.getUserAccountsPage(
			domainName, null, Pagination.of(1, 10),
			"customFields/" + expandoColumn.getName() + ":asc");

		assertEquals(userAccounts, (List<UserAccount>)page.getItems());

		page = userAccountResource.getUserAccountsPage(
			domainName, null, Pagination.of(1, 10),
			"customFields/" + expandoColumn.getName() + ":desc");

		Collections.reverse(userAccounts);

		assertEquals(userAccounts, (List<UserAccount>)page.getItems());
	}

	private void _testGetUserAccountsPageWithSortFullName() throws Exception {
		String domainName = StringUtil.randomString() + ".com";
		List<UserAccount> userAccounts = new ArrayList<>();

		userAccounts.add(
			userAccountResource.postUserAccount(
				null, null,
				_randomUserAccount(
					userAccount -> {
						userAccount.setGivenName("aaa");
						userAccount.setEmailAddress("aaa@" + domainName);
					})));
		userAccounts.add(
			userAccountResource.postUserAccount(
				null, null,
				_randomUserAccount(
					userAccount -> {
						userAccount.setGivenName("bbb");
						userAccount.setEmailAddress("bbb@" + domainName);
					})));

		Page<UserAccount> page = userAccountResource.getUserAccountsPage(
			domainName, null, Pagination.of(1, 10), "name:asc");

		assertEquals(userAccounts, (List<UserAccount>)page.getItems());

		Collections.reverse(userAccounts);

		page = userAccountResource.getUserAccountsPage(
			domainName, null, Pagination.of(1, 10), "name:desc");

		assertEquals(userAccounts, (List<UserAccount>)page.getItems());
	}

	private void _testGetUserAccountWithGender() throws Exception {
		PortletPreferences portletPreferences = PrefsPropsUtil.getPreferences(
			testCompany.getCompanyId());

		try {
			UserAccount randomUserAccount = randomUserAccount();

			randomUserAccount.setGender(UserAccount.Gender.FEMALE);

			UserAccount postUserAccount = _addUserAccount(
				testGroup.getGroupId(), randomUserAccount);

			UserAccount getUserAccount = userAccountResource.getUserAccount(
				postUserAccount.getId());

			assertEquals(postUserAccount, getUserAccount);
			assertValid(getUserAccount);

			Assert.assertNull(getUserAccount.getGender());

			portletPreferences.setValue(
				PropsKeys.
					FIELD_ENABLE_COM_LIFERAY_PORTAL_KERNEL_MODEL_CONTACT_MALE,
				Boolean.TRUE.toString());

			portletPreferences.store();

			randomUserAccount = randomUserAccount();

			randomUserAccount.setGender(UserAccount.Gender.FEMALE);

			postUserAccount = _addUserAccount(
				testGroup.getGroupId(), randomUserAccount);

			getUserAccount = userAccountResource.getUserAccount(
				postUserAccount.getId());

			Assert.assertEquals(
				UserAccount.Gender.FEMALE, getUserAccount.getGender());
		}
		finally {
			portletPreferences.setValue(
				PropsKeys.
					FIELD_ENABLE_COM_LIFERAY_PORTAL_KERNEL_MODEL_CONTACT_MALE,
				Boolean.FALSE.toString());

			portletPreferences.store();
		}
	}

	private void _testGetUserAccountWithLoginDate() throws Exception {
		UserAccount postUserAccount = testGetUserAccount_addUserAccount();

		_userLocalService.updateLastLogin(postUserAccount.getId(), null);

		UserAccount getUserAccount = userAccountResource.getUserAccount(
			postUserAccount.getId());

		Assert.assertNotNull(getUserAccount.getLoginDate());
	}

	private void _testGetUserAccountWithMoreExternalReferenceCodes()
		throws Exception {

		User user = UserTestUtil.addUser();

		_userLocalService.addGroupUser(
			testGroup.getGroupId(), user.getUserId());

		_userLocalService.addOrganizationUser(
			_organization.getOrganizationId(), user.getUserId());

		_userLocalService.addRoleUser(_role.getRoleId(), user.getUserId());

		_userLocalService.addUserGroupUser(
			_userGroup.getUserGroupId(), user.getUserId());

		UserAccount userAccount = userAccountResource.getUserAccount(
			user.getUserId());

		Assert.assertTrue(
			ArrayUtil.exists(
				userAccount.getOrganizationBriefs(),
				organizationBrief -> Objects.equals(
					organizationBrief.getExternalReferenceCode(),
					_organization.getExternalReferenceCode())));
		Assert.assertTrue(
			ArrayUtil.exists(
				userAccount.getRoleBriefs(),
				roleBrief -> Objects.equals(
					roleBrief.getExternalReferenceCode(),
					_role.getExternalReferenceCode())));
		Assert.assertTrue(
			ArrayUtil.exists(
				userAccount.getSiteBriefs(),
				siteBrief -> Objects.equals(
					siteBrief.getExternalReferenceCode(),
					testGroup.getExternalReferenceCode())));
		Assert.assertTrue(
			ArrayUtil.exists(
				userAccount.getUserGroupBriefs(),
				userGroupBrief -> Objects.equals(
					userGroupBrief.getExternalReferenceCode(),
					_userGroup.getExternalReferenceCode())));
	}

	private void _testGetUserAccountWithNestedFields() throws Exception {
		User user = UserTestUtil.addUser();

		AssetEntry assetEntry = _assetEntryLocalService.fetchEntry(
			_classNameLocalService.getClassNameId(User.class),
			user.getUserId());

		AssetVocabulary assetVocabulary = AssetTestUtil.addVocabulary(
			TestPropsValues.getGroupId());

		AssetCategory assetCategory = AssetTestUtil.addCategory(
			TestPropsValues.getGroupId(), assetVocabulary.getVocabularyId());

		_assetEntryAssetCategoryRelLocalService.addAssetEntryAssetCategoryRel(
			assetEntry.getEntryId(), assetCategory.getCategoryId());

		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			new HashMap<>(), DepotConstants.TYPE_ASSET_LIBRARY,
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId(), user.getUserId()));

		_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
			depotEntry.getDepotEntryId(), user.getGroupId());

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(), User.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(user.getUserId()), role.getRoleId(),
			new String[] {ActionKeys.DELETE});

		UserAccountResource userAccountResource = UserAccountResource.builder(
		).authentication(
			"test@liferay.com", PropsValues.DEFAULT_ADMIN_PASSWORD
		).locale(
			LocaleUtil.getDefault()
		).parameters(
			"nestedFields",
			"assetLibraryBriefs,creator,permissions,taxonomyCategoryBriefs"
		).build();

		UserAccount userAccount = userAccountResource.getUserAccount(
			user.getUserId());

		Assert.assertTrue(
			ArrayUtil.exists(
				userAccount.getAssetLibraryBriefs(),
				assetLibraryBrief -> Objects.equals(
					assetLibraryBrief.getGroupId(), depotEntry.getGroupId())));
		Assert.assertNotNull(userAccount.getCreator());

		Creator creator = userAccount.getCreator();

		Assert.assertTrue(creator.getId() == TestPropsValues.getUserId());

		Assert.assertTrue(
			ArrayUtil.exists(
				userAccount.getPermissions(),
				permission ->
					Objects.equals(permission.getRoleName(), role.getName()) &&
					(permission.getActionIds().length == 1) &&
					Objects.equals(permission.getActionIds()[0], "DELETE")));
		Assert.assertTrue(
			ArrayUtil.exists(
				userAccount.getTaxonomyCategoryBriefs(),
				taxonomyCategoryBrief -> Objects.equals(
					taxonomyCategoryBrief.getTaxonomyCategoryId(),
					assetCategory.getCategoryId())));
	}

	private void _testGetUserAccountWithRoles(
			Group group, UnsafeRunnable<Exception> unsafeRunnable, User user)
		throws Exception {

		Role inheritedRole = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_roleLocalService.addGroupRole(group.getGroupId(), inheritedRole);

		Assert.assertFalse(_hasRole(inheritedRole, user));

		unsafeRunnable.run();

		Assert.assertTrue(_hasRole(inheritedRole, user));

		if (group.isUserGroup()) {
			_testGetUserAccountWithRolesWithNoPermission(user, inheritedRole);

			return;
		}

		int groupRoleType = RoleConstants.TYPE_SITE;

		if (group.isOrganization()) {
			groupRoleType = RoleConstants.TYPE_ORGANIZATION;
		}

		Role groupRole = RoleTestUtil.addRole(groupRoleType);

		Assert.assertFalse(_hasRole(groupRole, user));

		_userGroupRoleLocalService.addUserGroupRole(
			user.getUserId(), group.getGroupId(), groupRole.getRoleId());

		Assert.assertTrue(_hasRole(groupRole, user));

		_testGetUserAccountWithRolesWithNoPermission(
			user, inheritedRole, groupRole);
	}

	private void _testGetUserAccountWithRolesWithNoPermission(
			User user, Role... roles)
		throws Exception {

		User otherUser = UserTestUtil.addUser(false);

		otherUser = _userLocalService.updatePassword(
			otherUser.getUserId(), "test", "test", false, true);

		_userLocalService.addRoleUser(_role.getRoleId(), otherUser);

		UserAccountResource.Builder builder = UserAccountResource.builder();

		UserAccountResource otherUserAccountResource = builder.authentication(
			otherUser.getEmailAddress(), "test"
		).locale(
			LocaleUtil.getDefault()
		).build();

		RoleBrief[] roleBriefs = _getUserAccountRoleBriefs(
			otherUserAccountResource.getUserAccount(user.getUserId()));

		for (Role role : roles) {
			Assert.assertFalse(_hasRole(role, roleBriefs));
		}
	}

	private void _testPatchUserAccountWithGender() throws Exception {
		PortletPreferences portletPreferences = PrefsPropsUtil.getPreferences(
			testCompany.getCompanyId());

		try {
			UserAccount randomUserAccount = randomUserAccount();

			UserAccount postUserAccount = testPostUserAccount_addUserAccount(
				randomUserAccount);

			Assert.assertNull(postUserAccount.getGender());

			UserAccount userAccount = new UserAccount();

			userAccount.setGender(UserAccount.Gender.FEMALE);

			UserAccount patchUserAccount = userAccountResource.patchUserAccount(
				postUserAccount.getId(), userAccount);

			assertEquals(randomUserAccount, patchUserAccount);
			assertValid(patchUserAccount);

			Assert.assertNull(patchUserAccount.getGender());

			User user = _userLocalService.getUser(patchUserAccount.getId());

			Assert.assertFalse(user.getFemale());

			portletPreferences.setValue(
				PropsKeys.
					FIELD_ENABLE_COM_LIFERAY_PORTAL_KERNEL_MODEL_CONTACT_MALE,
				Boolean.TRUE.toString());

			portletPreferences.store();

			patchUserAccount = userAccountResource.patchUserAccount(
				postUserAccount.getId(), userAccount);

			assertEquals(randomUserAccount, patchUserAccount);
			assertValid(patchUserAccount);

			Assert.assertEquals(
				UserAccount.Gender.FEMALE, patchUserAccount.getGender());
		}
		finally {
			portletPreferences.setValue(
				PropsKeys.
					FIELD_ENABLE_COM_LIFERAY_PORTAL_KERNEL_MODEL_CONTACT_MALE,
				Boolean.FALSE.toString());

			portletPreferences.store();
		}
	}

	private void _testPatchUserAccountWithImageExternalReferenceCode()
		throws Exception {

		UserAccount postUserAccount = testPatchUserAccount_addUserAccount();

		UserAccount randomPatchUserAccount = randomPatchUserAccount();

		FileEntry fileEntry = _addImageFileEntry();

		randomPatchUserAccount.setImageExternalReferenceCode(
			fileEntry.getExternalReferenceCode());

		randomPatchUserAccount.setImageId(0L);

		UserAccount patchUserAccount = userAccountResource.patchUserAccount(
			postUserAccount.getId(), randomPatchUserAccount);

		Assert.assertTrue(patchUserAccount.getImageId() > 0);
	}

	private void _testPostAccountUserAccountsByExternalReferenceCodeByEmailAddressWithRoleId()
		throws Exception {

		List<User> users = Arrays.asList(
			UserTestUtil.addUser(), UserTestUtil.addUser(),
			UserTestUtil.addUser(), UserTestUtil.addUser());

		for (User user : users) {
			Assert.assertNull(
				_accountEntryUserRelLocalService.fetchAccountEntryUserRel(
					_accountEntry.getAccountEntryId(), user.getUserId()));
		}

		AccountRole accountRole = _accountRoleLocalService.addAccountRole(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT,
			RandomTestUtil.randomString(), null,
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()));

		userAccountResource.
			postAccountUserAccountsByExternalReferenceCodeByEmailAddress(
				_accountEntry.getExternalReferenceCode(),
				String.valueOf(accountRole.getAccountRoleId()),
				_toEmailAddresses(users));

		for (User user : users) {
			Assert.assertNotNull(
				_accountEntryUserRelLocalService.fetchAccountEntryUserRel(
					_accountEntry.getAccountEntryId(), user.getUserId()));

			UserGroupRole userGroupRole =
				_userGroupRoleLocalService.fetchUserGroupRole(
					user.getUserId(), _accountEntry.getAccountEntryGroupId(),
					accountRole.getRoleId());

			Assert.assertNotNull(userGroupRole);
		}
	}

	private void _testPostUserAccountBatch() throws Exception {
		UserAccount randomUserAccount = _randomUserAccount(
			userAccount -> userAccount.setPassword(StringPool.BLANK));

		_waitForFinish(
			"COMPLETED", true,
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					"items",
					JSONUtil.put(
						_jsonFactory.createJSONObject(
							randomUserAccount.toString()))
				).toString(),
				"headless-admin-user/v1.0/user-accounts/batch",
				Http.Method.POST));

		MailMessage mailMessage = MailServiceTestUtil.getLastMailMessage();

		String body = mailMessage.getBody();

		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		Assert.assertTrue(
			body,
			body.contains(
				StringBundler.concat(
					company.getPortalURL(0), Portal.PATH_MAIN,
					"/portal/update_password")));
	}

	private void _testPostUserAccountWithApprovalWorkflow() throws Exception {
		UserAccount postUserAccount = userAccountResource.postUserAccount(
			null, null, randomUserAccount());

		User user = _userLocalService.getUser(postUserAccount.getId());

		Assert.assertEquals(
			user.getStatus(), WorkflowConstants.STATUS_APPROVED);

		WorkflowDefinitionLink workflowDefinitionLink =
			_workflowDefinitionLinkLocalService.addWorkflowDefinitionLink(
				null, TestPropsValues.getUserId(),
				TestPropsValues.getCompanyId(),
				GroupConstants.DEFAULT_LIVE_GROUP_ID, User.class.getName(), 0,
				0, "Single Approver", 1);

		postUserAccount = userAccountResource.postUserAccount(
			null, null, randomUserAccount());

		user = _userLocalService.getUser(postUserAccount.getId());

		Assert.assertEquals(user.getStatus(), WorkflowConstants.STATUS_PENDING);

		_workflowDefinitionLinkLocalService.deleteWorkflowDefinitionLink(
			workflowDefinitionLink);
	}

	private void _testPostUserAccountWithCaptcha() throws Exception {
		SAPEntry sapEntry = _sapEntryLocalService.addSAPEntry(
			TestPropsValues.getUserId(),
			"com.liferay.headless.admin.user.internal.resource.v1_0." +
				"UserAccountResourceImpl#postUserAccount",
			true, true, "Guest",
			HashMapBuilder.put(
				LocaleUtil.getDefault(), "Guest"
			).build(),
			ServiceContextTestUtil.getServiceContext());

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					"com.liferay.captcha.configuration.CaptchaConfiguration",
					HashMapDictionaryBuilder.<String, Object>put(
						"createAccountCaptchaEnabled", true
					).build())) {

			CaptchaResource captchaResource = CaptchaResource.builder(
			).endpoint(
				testCompany.getVirtualHostname(),
				PortalUtil.getPortalServerPort(false), "http"
			).build();

			Captcha captcha = captchaResource.getCaptchaChallenge();

			UserAccountResource.Builder builder = UserAccountResource.builder();

			UserAccountResource userAccountResourceGuestUser = builder.locale(
				LocaleUtil.getDefault()
			).build();

			UserAccount userAccount = randomUserAccount();

			try {
				userAccountResourceGuestUser.postUserAccount(
					RandomTestUtil.randomString(), captcha.getToken(),
					userAccount);

				Assert.fail();
			}
			catch (Problem.ProblemException problemException) {
				Problem problem = problemException.getProblem();

				Assert.assertEquals(
					"The captcha value is invalid", problem.getTitle());
			}

			captcha = captchaResource.getCaptchaChallenge();

			JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
				EncryptorUtil.decrypt(
					testCompany.getKeyObj(), captcha.getToken()));

			userAccountResourceGuestUser.postUserAccount(
				jsonObject.getString("answer"), captcha.getToken(),
				userAccount);

			Assert.assertNotNull(
				_userLocalService.fetchUserByEmailAddress(
					TestPropsValues.getCompanyId(),
					userAccount.getEmailAddress()));
		}
		finally {
			_sapEntryLocalService.deleteSAPEntry(sapEntry);
		}
	}

	private void _testPostUserAccountWithGender() throws Exception {
		PortletPreferences portletPreferences = PrefsPropsUtil.getPreferences(
			testCompany.getCompanyId());

		try {
			UserAccount randomUserAccount = randomUserAccount();

			randomUserAccount.setGender(UserAccount.Gender.FEMALE);

			UserAccount postUserAccount = testPostUserAccount_addUserAccount(
				randomUserAccount);

			assertEquals(randomUserAccount, postUserAccount);
			assertValid(postUserAccount);

			Assert.assertNull(postUserAccount.getGender());

			User user = _userLocalService.getUser(postUserAccount.getId());

			Assert.assertFalse(user.getFemale());

			portletPreferences.setValue(
				PropsKeys.
					FIELD_ENABLE_COM_LIFERAY_PORTAL_KERNEL_MODEL_CONTACT_MALE,
				Boolean.TRUE.toString());

			portletPreferences.store();

			randomUserAccount = randomUserAccount();

			randomUserAccount.setGender(UserAccount.Gender.FEMALE);

			postUserAccount = testPostUserAccount_addUserAccount(
				randomUserAccount);

			assertEquals(randomUserAccount, postUserAccount);
			assertValid(postUserAccount);

			Assert.assertEquals(
				UserAccount.Gender.FEMALE, postUserAccount.getGender());
		}
		finally {
			portletPreferences.setValue(
				PropsKeys.
					FIELD_ENABLE_COM_LIFERAY_PORTAL_KERNEL_MODEL_CONTACT_MALE,
				Boolean.FALSE.toString());

			portletPreferences.store();
		}
	}

	private void _testPostUserAccountWithImageExternalReferenceCode()
		throws Exception {

		UserAccount randomUserAccount = randomUserAccount();

		FileEntry fileEntry = _addImageFileEntry();

		randomUserAccount.setImageExternalReferenceCode(
			fileEntry.getExternalReferenceCode());

		randomUserAccount.setImageId(0L);

		UserAccount postUserAccount = userAccountResource.postUserAccount(
			null, null, randomUserAccount);

		Assert.assertTrue(postUserAccount.getImageId() > 0);
	}

	private void _testPostUserAccountWithObjectValidationRule()
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_USER", testCompany.getCompanyId());

		ObjectValidationRule objectValidationRule =
			_objectValidationRuleLocalService.addObjectValidationRule(
				StringPool.BLANK, _testUser.getUserId(),
				objectDefinition.getObjectDefinitionId(), true,
				ObjectValidationRuleConstants.ENGINE_TYPE_DDM,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectValidationRuleConstants.OUTPUT_TYPE_FULL_VALIDATION,
				"NOT(isEmpty(alternateName))", true, Collections.emptyList());

		UserAccount postUserAccount = userAccountResource.postUserAccount(
			null, null,
			_randomUserAccount(
				userAccount -> userAccount.setUserAccountContactInformation(
					() -> null)));

		Assert.assertNotNull(postUserAccount);

		_objectValidationRuleLocalService.deleteObjectValidationRule(
			objectValidationRule);
	}

	private void _testPutUserAccountByExternalReferenceCodeWithImageExternalReferenceCode()
		throws Exception {

		UserAccount postUserAccount =
			testPutUserAccountByExternalReferenceCode_addUserAccount();

		UserAccount randomPutUserAccount = randomUserAccount();

		FileEntry fileEntry = _addImageFileEntry();

		randomPutUserAccount.setImageExternalReferenceCode(
			fileEntry.getExternalReferenceCode());

		randomPutUserAccount.setImageId(0L);

		UserAccount putUserAccount =
			userAccountResource.putUserAccountByExternalReferenceCode(
				postUserAccount.getExternalReferenceCode(),
				randomPutUserAccount);

		Assert.assertTrue(putUserAccount.getImageId() > 0);
	}

	private void _testPutUserAccountWithImageExternalReferenceCode()
		throws Exception {

		UserAccount postUserAccount = testPutUserAccount_addUserAccount();

		UserAccount randomPutUserAccount = randomUserAccount();

		FileEntry fileEntry = _addImageFileEntry();

		randomPutUserAccount.setImageExternalReferenceCode(
			fileEntry.getExternalReferenceCode());

		randomPutUserAccount.setImageId(0L);

		UserAccount putUserAccount = userAccountResource.putUserAccount(
			postUserAccount.getId(), randomPutUserAccount);

		Assert.assertTrue(putUserAccount.getImageId() > 0);
	}

	private String[] _toEmailAddresses(List<User> users) {
		return TransformUtil.transformToArray(
			users, User::getEmailAddress, String.class);
	}

	private long[] _toUserIds(List<User> users) {
		return ListUtil.toLongArray(users, User.USER_ID_ACCESSOR);
	}

	private JSONObject _waitForFinish(
			String expectedExecuteStatus, boolean importTask,
			JSONObject jsonObject)
		throws Exception {

		String endpoint = StringBundler.concat(
			"headless-batch-engine/v1.0/",
			importTask ? "import-task" : "export-task",
			"/by-external-reference-code/");

		while (true) {
			jsonObject = HTTPTestUtil.invokeToJSONObject(
				null, endpoint + jsonObject.getString("externalReferenceCode"),
				Http.Method.GET);

			String executeStatus = jsonObject.getString("executeStatus");

			if (StringUtil.equals(executeStatus, "COMPLETED") ||
				StringUtil.equals(executeStatus, "FAILED")) {

				Assert.assertEquals(expectedExecuteStatus, executeStatus);

				return jsonObject;
			}
		}
	}

	private AccountEntry _accountEntry;

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	@Inject
	private AccountEntryUserRelLocalService _accountEntryUserRelLocalService;

	@Inject
	private AccountRoleLocalService _accountRoleLocalService;

	@Inject
	private AssetEntryAssetCategoryRelLocalService
		_assetEntryAssetCategoryRelLocalService;

	@Inject
	private AssetEntryLocalService _assetEntryLocalService;

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DepotEntryGroupRelLocalService _depotEntryGroupRelLocalService;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private ExpandoColumnLocalService _expandoColumnLocalService;

	@Inject
	private ExpandoTableLocalService _expandoTableLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private JSONFactory _jsonFactory;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectValidationRuleLocalService _objectValidationRuleLocalService;

	private Organization _organization;

	@Inject
	private OrganizationLocalService _organizationLocalService;

	private UserAccount _regularUserAccount;
	private String _regularUserAccountCurrentPassword;
	private UserAccountResource _regularUserAccountResource;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	private Role _role;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private SAPEntryLocalService _sapEntryLocalService;

	private User _testUser;
	private UserGroup _userGroup;

	@Inject
	private UserGroupLocalService _userGroupLocalService;

	@Inject
	private UserGroupRoleLocalService _userGroupRoleLocalService;

	@Inject
	private UserLocalService _userLocalService;

	@Inject
	private UserService _userService;

	@Inject
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

	private class TestSimpleCaptchaImpl extends SimpleCaptchaImpl {

		public TestSimpleCaptchaImpl(
			UnsafeRunnable<CaptchaException> unsafeRunnable) {

			_unsafeRunnable = unsafeRunnable;
		}

		@Override
		public void check(HttpServletRequest httpServletRequest)
			throws CaptchaException {

			_unsafeRunnable.run();
		}

		private final UnsafeRunnable<CaptchaException> _unsafeRunnable;

	}

}