/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.resource.v1_0.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountEntryOrganizationRel;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountEntryOrganizationRelLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.entry.rel.service.AssetEntryAssetCategoryRelLocalService;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.asset.test.util.AssetTestUtil;
import com.liferay.document.library.kernel.model.DLFolderConstants;
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
import com.liferay.headless.admin.user.client.dto.v1_0.Creator;
import com.liferay.headless.admin.user.client.dto.v1_0.EmailAddress;
import com.liferay.headless.admin.user.client.dto.v1_0.Organization;
import com.liferay.headless.admin.user.client.dto.v1_0.OrganizationContactInformation;
import com.liferay.headless.admin.user.client.dto.v1_0.Phone;
import com.liferay.headless.admin.user.client.dto.v1_0.PostalAddress;
import com.liferay.headless.admin.user.client.dto.v1_0.RoleBrief;
import com.liferay.headless.admin.user.client.dto.v1_0.TaxonomyCategoryBrief;
import com.liferay.headless.admin.user.client.dto.v1_0.TaxonomyCategoryReference;
import com.liferay.headless.admin.user.client.dto.v1_0.WebUrl;
import com.liferay.headless.admin.user.client.pagination.Page;
import com.liferay.headless.admin.user.client.pagination.Pagination;
import com.liferay.headless.admin.user.client.permission.Permission;
import com.liferay.headless.admin.user.client.resource.v1_0.OrganizationResource;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.repository.LocalRepository;
import com.liferay.portal.kernel.repository.RepositoryProviderUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.OrganizationTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.SynchronousMailTestRule;
import com.liferay.portal.vulcan.permission.PermissionUtil;

import java.io.InputStream;

import java.text.DateFormat;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Javier Gamarra
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class OrganizationResourceTest extends BaseOrganizationResourceTestCase {

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

		_accountEntry = _accountEntryLocalService.addOrUpdateAccountEntry(
			RandomTestUtil.randomString(20), TestPropsValues.getUserId(),
			AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT,
			RandomTestUtil.randomString(20), RandomTestUtil.randomString(20),
			null, null, null, null,
			AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS,
			WorkflowConstants.STATUS_APPROVED,
			ServiceContextTestUtil.getServiceContext());
		_user = UserTestUtil.addGroupAdminUser(testGroup);
	}

	@Override
	@Test
	public void testDeleteAccountByExternalReferenceCodeOrganization()
		throws Exception {

		Organization organization =
			testDeleteAccountByExternalReferenceCodeOrganization_addOrganization();

		assertHttpResponseStatusCode(
			204,
			organizationResource.
				deleteAccountByExternalReferenceCodeOrganizationHttpResponse(
					_accountEntry.getExternalReferenceCode(),
					organization.getId()));
	}

	@Override
	@Test
	public void testDeleteAccountOrganization() throws Exception {
		com.liferay.portal.kernel.model.Organization organization =
			OrganizationTestUtil.addOrganization();

		_accountEntryOrganizationRelLocalService.addAccountEntryOrganizationRel(
			_accountEntry.getAccountEntryId(),
			organization.getOrganizationId());

		Assert.assertNotNull(
			_accountEntryOrganizationRelLocalService.
				fetchAccountEntryOrganizationRel(
					_accountEntry.getAccountEntryId(),
					organization.getOrganizationId()));
	}

	@Override
	@Test
	public void testDeleteOrganizationByExternalReferenceCodeUserAccountByEmailAddress()
		throws Exception {

		Organization organization = _addOrganization(randomOrganization(), "0");
		User user = UserTestUtil.addUser();

		_organizationLocalService.addUserOrganization(
			user.getUserId(), GetterUtil.getLong(organization.getId()));

		Assert.assertTrue(
			_organizationLocalService.hasUserOrganization(
				user.getUserId(), GetterUtil.getLong(organization.getId())));

		organizationResource.
			deleteOrganizationByExternalReferenceCodeUserAccountByEmailAddress(
				organization.getExternalReferenceCode(),
				user.getEmailAddress());

		Assert.assertFalse(
			_organizationLocalService.hasUserOrganization(
				user.getUserId(), GetterUtil.getLong(organization.getId())));
	}

	@Override
	@Test
	public void testDeleteOrganizationByExternalReferenceCodeUserAccountsByEmailAddress()
		throws Exception {

		Organization organization = _addOrganization(randomOrganization(), "0");

		long organizationId = GetterUtil.getLong(organization.getId());

		List<User> users = Arrays.asList(
			UserTestUtil.addUser(), UserTestUtil.addUser(),
			UserTestUtil.addUser(), UserTestUtil.addUser());

		_userLocalService.addOrganizationUsers(organizationId, users);

		for (User user : users) {
			Assert.assertTrue(
				_userLocalService.hasOrganizationUser(
					organizationId, user.getUserId()));
		}

		List<User> deleteUsers = users.subList(0, 2);

		organizationResource.
			deleteOrganizationByExternalReferenceCodeUserAccountsByEmailAddress(
				organization.getExternalReferenceCode(),
				_toEmailAddresses(deleteUsers));

		for (User user : deleteUsers) {
			Assert.assertFalse(
				_userLocalService.hasOrganizationUser(
					organizationId, user.getUserId()));
		}

		List<User> keepUsers = users.subList(2, 4);

		for (User user : keepUsers) {
			Assert.assertTrue(
				_userLocalService.hasOrganizationUser(
					organizationId, user.getUserId()));
		}
	}

	@Override
	@Test
	public void testDeleteUserAccountByEmailAddress() throws Exception {
		Organization organization = _addOrganization(randomOrganization(), "0");
		User user = UserTestUtil.addUser();

		_organizationLocalService.addUserOrganization(
			user.getUserId(), GetterUtil.getLong(organization.getId()));

		Assert.assertTrue(
			_organizationLocalService.hasUserOrganization(
				user.getUserId(), GetterUtil.getLong(organization.getId())));

		organizationResource.deleteUserAccountByEmailAddress(
			organization.getId(), user.getEmailAddress());

		Assert.assertFalse(
			_organizationLocalService.hasUserOrganization(
				user.getUserId(), GetterUtil.getLong(organization.getId())));
	}

	@Override
	@Test
	public void testDeleteUserAccountsByEmailAddress() throws Exception {
		Organization organization = _addOrganization(randomOrganization(), "0");

		long organizationId = GetterUtil.getLong(organization.getId());

		List<User> users = Arrays.asList(
			UserTestUtil.addUser(), UserTestUtil.addUser(),
			UserTestUtil.addUser(), UserTestUtil.addUser());

		_userLocalService.addOrganizationUsers(organizationId, users);

		for (User user : users) {
			Assert.assertTrue(
				_userLocalService.hasOrganizationUser(
					organizationId, user.getUserId()));
		}

		List<User> deleteUsers = users.subList(0, 2);

		organizationResource.deleteUserAccountsByEmailAddress(
			organization.getId(), _toEmailAddresses(deleteUsers));

		for (User user : deleteUsers) {
			Assert.assertFalse(
				_userLocalService.hasOrganizationUser(
					organizationId, user.getUserId()));
		}

		List<User> keepUsers = users.subList(2, 4);

		for (User user : keepUsers) {
			Assert.assertTrue(
				_userLocalService.hasOrganizationUser(
					organizationId, user.getUserId()));
		}
	}

	@Override
	@Test
	public void testGetAccountByExternalReferenceCodeOrganization()
		throws Exception {

		testGetAccountOrganization();
	}

	@Override
	@Test
	public void testGetAccountOrganization() throws Exception {
		com.liferay.portal.kernel.model.Organization organization =
			OrganizationTestUtil.addOrganization();

		_accountEntryOrganizationRelLocalService.addAccountEntryOrganizationRel(
			_accountEntry.getAccountEntryId(),
			organization.getOrganizationId());

		Assert.assertNotNull(
			_accountEntryOrganizationRelLocalService.
				fetchAccountEntryOrganizationRel(
					_accountEntry.getAccountEntryId(),
					organization.getOrganizationId()));

		organizationResource.deleteAccountOrganization(
			_accountEntry.getAccountEntryId(),
			String.valueOf(organization.getOrganizationId()));

		Assert.assertNull(
			_accountEntryOrganizationRelLocalService.
				fetchAccountEntryOrganizationRel(
					_accountEntry.getAccountEntryId(),
					organization.getOrganizationId()));
	}

	@Override
	@Test
	public void testGetOrganization() throws Exception {
		super.testGetOrganization();

		_testGetOrganizationWithNestedFields();
	}

	@Override
	@Test
	public void testGetOrganizationsPage() throws Exception {
		super.testGetOrganizationsPage();

		_testGetOrganizationsPageWithFilter();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLDeleteAccountByExternalReferenceCodeOrganization()
		throws Exception {

		super.testGraphQLDeleteAccountByExternalReferenceCodeOrganization();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLDeleteAccountOrganization() throws Exception {
		super.testGraphQLDeleteAccountOrganization();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLDeleteOrganizationByExternalReferenceCodeUserAccountsByEmailAddress()
		throws Exception {

		super.
			testGraphQLDeleteOrganizationByExternalReferenceCodeUserAccountsByEmailAddress();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLDeleteUserAccountsByEmailAddress() throws Exception {
		super.testGraphQLDeleteUserAccountsByEmailAddress();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetAccountByExternalReferenceCodeOrganizationsPage()
		throws Exception {

		super.testGraphQLGetAccountByExternalReferenceCodeOrganizationsPage();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetAccountOrganizationsPage() throws Exception {
		super.testGraphQLGetAccountOrganizationsPage();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetOrganizationByExternalReferenceCodeChildOrganizationsPage()
		throws Exception {

		super.
			testGraphQLGetOrganizationByExternalReferenceCodeChildOrganizationsPage();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetOrganizationChildOrganizationsPage()
		throws Exception {

		super.testGraphQLGetOrganizationChildOrganizationsPage();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetOrganizationOrganizationsPage() throws Exception {
		super.testGraphQLGetOrganizationOrganizationsPage();
	}

	@Override
	@Test
	public void testPatchOrganization() throws Exception {
		super.testPatchOrganization();

		_testPatchOrganizationWithImageExternalReferenceCode();
		_testPatchOrganizationWithKeywords();
	}

	@Override
	@Test
	public void testPatchOrganizationByExternalReferenceCode()
		throws Exception {

		super.testPatchOrganizationByExternalReferenceCode();

		_testPatchOrganizationByExternalReferenceCodeWithImageExternalReferenceCode();
	}

	@Override
	@Test
	public void testPostAccountByExternalReferenceCodeOrganization()
		throws Exception {

		Organization organization =
			testPostAccountByExternalReferenceCodeOrganization_addOrganization();

		assertHttpResponseStatusCode(
			204,
			organizationResource.
				postAccountByExternalReferenceCodeOrganizationHttpResponse(
					_accountEntry.getExternalReferenceCode(),
					organization.getId()));

		assertHttpResponseStatusCode(
			404,
			organizationResource.
				postAccountByExternalReferenceCodeOrganizationHttpResponse(
					_accountEntry.getExternalReferenceCode(), "-"));
	}

	@Override
	@Test
	public void testPostAccountOrganization() throws Exception {
		Organization organization =
			testPostAccountOrganization_addOrganization();

		assertHttpResponseStatusCode(
			204,
			organizationResource.postAccountOrganizationHttpResponse(
				_accountEntry.getAccountEntryId(), organization.getId()));

		assertHttpResponseStatusCode(
			404,
			organizationResource.postAccountOrganizationHttpResponse(
				_accountEntry.getAccountEntryId(), "-"));
	}

	@FeatureFlag("LPD-35914")
	@LazyReferencing
	@Override
	@Test
	public void testPostOrganization() throws Exception {
		super.testPostOrganization();

		_testPostOrganizationBatch();
		_testPostOrganizationWithCustomFields();
		_testPostOrganizationWithCommentOverMaximumLength();
		_testPostOrganizationWithNameOverMaximumLength();
		_testPostOrganizationWithImageExternalReferenceCode();
	}

	@Override
	@Test
	public void testPostOrganizationByExternalReferenceCodeUserAccountByEmailAddress()
		throws Exception {

		Organization organization = _addOrganization(randomOrganization(), "0");
		User user = UserTestUtil.addUser();

		Assert.assertFalse(
			_organizationLocalService.hasUserOrganization(
				user.getUserId(), GetterUtil.getLong(organization.getId())));

		organizationResource.
			postOrganizationByExternalReferenceCodeUserAccountByEmailAddress(
				organization.getExternalReferenceCode(),
				user.getEmailAddress());

		Assert.assertTrue(
			_organizationLocalService.hasUserOrganization(
				user.getUserId(), GetterUtil.getLong(organization.getId())));
	}

	@Override
	@Test
	public void testPostOrganizationByExternalReferenceCodeUserAccountsByEmailAddress()
		throws Exception {

		Organization organization = _addOrganization(randomOrganization(), "0");

		long organizationId = GetterUtil.getLong(organization.getId());

		List<User> users = Arrays.asList(
			UserTestUtil.addUser(), UserTestUtil.addUser(),
			UserTestUtil.addUser(), UserTestUtil.addUser());

		for (User user : users) {
			Assert.assertFalse(
				_userLocalService.hasOrganizationUser(
					organizationId, user.getUserId()));
		}

		organizationResource.
			postOrganizationByExternalReferenceCodeUserAccountsByEmailAddress(
				organization.getExternalReferenceCode(), null,
				_toEmailAddresses(users));

		for (User user : users) {
			Assert.assertTrue(
				_userLocalService.hasOrganizationUser(
					organizationId, user.getUserId()));
		}
	}

	@Override
	@Test
	public void testPostUserAccountByEmailAddress() throws Exception {
		Organization organization = _addOrganization(randomOrganization(), "0");
		User user = UserTestUtil.addUser();

		Assert.assertFalse(
			_organizationLocalService.hasUserOrganization(
				user.getUserId(), GetterUtil.getLong(organization.getId())));

		organizationResource.postUserAccountByEmailAddress(
			organization.getId(), user.getEmailAddress());

		Assert.assertTrue(
			_organizationLocalService.hasUserOrganization(
				user.getUserId(), GetterUtil.getLong(organization.getId())));
	}

	@Override
	@Test
	public void testPostUserAccountsByEmailAddress() throws Exception {
		Organization organization = _addOrganization(randomOrganization(), "0");

		long organizationId = GetterUtil.getLong(organization.getId());

		List<User> users = Arrays.asList(
			UserTestUtil.addUser(), UserTestUtil.addUser(),
			UserTestUtil.addUser(), UserTestUtil.addUser());

		for (User user : users) {
			Assert.assertFalse(
				_userLocalService.hasOrganizationUser(
					organizationId, user.getUserId()));
		}

		organizationResource.postUserAccountsByEmailAddress(
			organization.getId(), null, _toEmailAddresses(users));

		for (User user : users) {
			Assert.assertTrue(
				_userLocalService.hasOrganizationUser(
					organizationId, user.getUserId()));
		}
	}

	@Override
	@Test
	public void testPutOrganization() throws Exception {
		super.testPutOrganization();

		_testPutOrganizationWithImageExternalReferenceCode();
	}

	@Override
	@Test
	public void testPutOrganizationByExternalReferenceCode() throws Exception {
		super.testPutOrganizationByExternalReferenceCode();

		_testPutOrganizationByExternalReferenceCodeKeepsExternalReferenceCode();
		_testPutOrganizationByExternalReferenceCodeWithImageExternalReferenceCode();
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"name", "externalReferenceCode"};
	}

	@Override
	protected Organization randomOrganization() throws Exception {
		Organization organization = super.randomOrganization();

		organization.setImageBase64(StringPool.BLANK);
		organization.setImageId(0L);

		return organization;
	}

	@Override
	protected Organization
			testDeleteAccountByExternalReferenceCodeOrganization_addOrganization()
		throws Exception {

		Organization organization =
			organizationResource.putOrganizationByExternalReferenceCode(
				RandomTestUtil.randomString(), randomOrganization());

		_accountEntryOrganizationRelLocalService.addAccountEntryOrganizationRel(
			_accountEntry.getAccountEntryId(),
			GetterUtil.getLong(organization.getId()));

		return organization;
	}

	@Override
	protected Organization testDeleteAccountOrganization_addOrganization()
		throws Exception {

		return testDeleteAccountByExternalReferenceCodeOrganization_addOrganization();
	}

	@Override
	protected Long testDeleteAccountOrganization_getAccountId()
		throws Exception {

		return _accountEntry.getAccountEntryId();
	}

	@Override
	protected Organization testDeleteOrganization_addOrganization()
		throws Exception {

		return _addOrganization(randomOrganization(), "0");
	}

	@Override
	protected Organization
			testDeleteOrganizationByExternalReferenceCode_addOrganization()
		throws Exception {

		return organizationResource.putOrganizationByExternalReferenceCode(
			StringUtil.toLowerCase(RandomTestUtil.randomString()),
			randomOrganization());
	}

	@Override
	protected Organization
			testGetAccountByExternalReferenceCodeOrganizationsPage_addOrganization(
				String externalReferenceCode, Organization organization)
		throws Exception {

		organization = organizationResource.postOrganization(organization);

		organizationResource.postAccountByExternalReferenceCodeOrganization(
			externalReferenceCode, organization.getId());

		return organization;
	}

	@Override
	protected String
			testGetAccountByExternalReferenceCodeOrganizationsPage_getExternalReferenceCode()
		throws Exception {

		return _accountEntry.getExternalReferenceCode();
	}

	@Override
	protected Organization testGetAccountOrganizationsPage_addOrganization(
			Long accountId, Organization organization)
		throws Exception {

		organization = organizationResource.postOrganization(organization);

		organizationResource.postAccountOrganization(
			accountId, organization.getId());

		return organization;
	}

	@Override
	protected Long testGetAccountOrganizationsPage_getAccountId()
		throws Exception {

		return _accountEntry.getAccountEntryId();
	}

	@Override
	protected Organization testGetOrganization_addOrganization()
		throws Exception {

		return _addOrganization(randomOrganization(), "0");
	}

	@Override
	protected Organization
			testGetOrganizationByExternalReferenceCode_addOrganization()
		throws Exception {

		return organizationResource.putOrganizationByExternalReferenceCode(
			StringUtil.toLowerCase(RandomTestUtil.randomString()),
			randomOrganization());
	}

	@Override
	protected Organization
			testGetOrganizationByExternalReferenceCodeChildOrganizationsPage_addOrganization(
				String externalReferenceCode, Organization organization)
		throws Exception {

		Organization parentOrganization =
			organizationResource.getOrganizationByExternalReferenceCode(
				externalReferenceCode);

		return _addOrganization(organization, parentOrganization.getId());
	}

	@Override
	protected String
			testGetOrganizationByExternalReferenceCodeChildOrganizationsPage_getExternalReferenceCode()
		throws Exception {

		Organization organization = organizationResource.postOrganization(
			randomOrganization());

		return organization.getExternalReferenceCode();
	}

	@Override
	protected Organization
			testGetOrganizationChildOrganizationsPage_addOrganization(
				String parentOrganizationId, Organization organization)
		throws Exception {

		return _addOrganization(organization, parentOrganizationId);
	}

	@Override
	protected String
			testGetOrganizationChildOrganizationsPage_getOrganizationId()
		throws Exception {

		Organization organization = organizationResource.postOrganization(
			randomOrganization());

		return String.valueOf(organization.getId());
	}

	@Override
	protected Organization testGetOrganizationOrganizationsPage_addOrganization(
			String parentOrganizationId, Organization organization)
		throws Exception {

		return _addOrganization(organization, parentOrganizationId);
	}

	@Override
	protected String
			testGetOrganizationOrganizationsPage_getParentOrganizationId()
		throws Exception {

		Organization organization = _addOrganization(randomOrganization(), "0");

		return String.valueOf(organization.getId());
	}

	@Override
	protected Organization testGetOrganizationsPage_addOrganization(
			Organization organization)
		throws Exception {

		return _addOrganization(organization, "0");
	}

	@Override
	protected String
			testGraphQLDeleteAccountByExternalReferenceCodeOrganization_getExternalReferenceCode(
				Organization organization)
		throws Exception {

		return _accountEntry.getExternalReferenceCode();
	}

	@Override
	protected Long testGraphQLDeleteAccountOrganization_getAccountId()
		throws Exception {

		return _accountEntry.getAccountEntryId();
	}

	@Override
	protected Organization
			testGraphQLDeleteOrganizationByExternalReferenceCodeUserAccountByEmailAddress_addOrganization()
		throws Exception {

		Organization organization = _addOrganization(randomOrganization(), "0");

		_organizationLocalService.addUserOrganization(
			_user.getUserId(), GetterUtil.getLong(organization.getId()));

		return organization;
	}

	@Override
	protected String
			testGraphQLDeleteOrganizationByExternalReferenceCodeUserAccountByEmailAddress_getEmailAddress()
		throws Exception {

		return _user.getEmailAddress();
	}

	@Override
	protected Organization
			testGraphQLDeleteUserAccountByEmailAddress_addOrganization()
		throws Exception {

		Organization organization = _addOrganization(randomOrganization(), "0");

		_organizationLocalService.addUserOrganization(
			_user.getUserId(), GetterUtil.getLong(organization.getId()));

		return organization;
	}

	@Override
	protected String
			testGraphQLDeleteUserAccountByEmailAddress_getEmailAddress()
		throws Exception {

		return _user.getEmailAddress();
	}

	@Override
	protected Organization
			testGraphQLGetAccountByExternalReferenceCodeOrganization_addOrganization()
		throws Exception {

		return testGraphQLGetAccountOrganization_addOrganization();
	}

	@Override
	protected String
			testGraphQLGetAccountByExternalReferenceCodeOrganization_getExternalReferenceCode(
				Organization organization)
		throws Exception {

		return _accountEntry.getExternalReferenceCode();
	}

	@Override
	protected Organization testGraphQLGetAccountOrganization_addOrganization()
		throws Exception {

		Organization organization = testGraphQLOrganization_addOrganization();

		_accountEntryOrganizationRelLocalService.addAccountEntryOrganizationRel(
			_accountEntry.getAccountEntryId(),
			GetterUtil.getLong(organization.getId()));

		return organization;
	}

	@Override
	protected Long testGraphQLGetAccountOrganization_getAccountId()
		throws Exception {

		return _accountEntry.getAccountEntryId();
	}

	@Override
	protected Organization testGraphQLOrganization_addOrganization()
		throws Exception {

		return _addOrganization(randomOrganization(), "0");
	}

	@Override
	protected Organization testPatchOrganization_addOrganization()
		throws Exception {

		return _addOrganization(randomOrganization(), "0");
	}

	@Override
	protected Organization
			testPatchOrganizationByExternalReferenceCode_addOrganization()
		throws Exception {

		return organizationResource.putOrganizationByExternalReferenceCode(
			StringUtil.toLowerCase(RandomTestUtil.randomString()),
			randomOrganization());
	}

	@Override
	protected Organization
			testPostAccountByExternalReferenceCodeOrganization_addOrganization()
		throws Exception {

		return organizationResource.putOrganizationByExternalReferenceCode(
			_accountEntry.getExternalReferenceCode(), randomOrganization());
	}

	@Override
	protected Organization testPostAccountOrganization_addOrganization()
		throws Exception {

		return organizationResource.putOrganizationByExternalReferenceCode(
			StringUtil.toLowerCase(RandomTestUtil.randomString()),
			randomOrganization());
	}

	@Override
	protected Organization testPostOrganization_addOrganization(
			Organization organization)
		throws Exception {

		return _addOrganization(organization, "0");
	}

	@Override
	protected Organization testPutOrganization_addOrganization()
		throws Exception {

		return _addOrganization(randomOrganization(), "0");
	}

	@Override
	protected Organization
			testPutOrganizationByExternalReferenceCode_addOrganization()
		throws Exception {

		return organizationResource.putOrganizationByExternalReferenceCode(
			StringUtil.toLowerCase(RandomTestUtil.randomString()),
			randomOrganization());
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

	private Organization _addOrganization(
			Organization organization, String parentOrganizationId)
		throws Exception {

		organization.setParentOrganization(
			() -> {
				if (Validator.isNull(parentOrganizationId)) {
					return null;
				}

				return new Organization() {
					{
						id = parentOrganizationId;
					}
				};
			});

		return organizationResource.postOrganization(organization);
	}

	private OrganizationContactInformation
		_getRandomOrganizationContactInformation() {

		return new OrganizationContactInformation() {
			{
				setEmailAddresses(
					() -> new EmailAddress[] {
						new EmailAddress() {
							{
								emailAddress =
									RandomTestUtil.randomString() +
										"@liferay.com";
							}
						}
					});
				setPostalAddresses(
					() -> new PostalAddress[] {
						new PostalAddress() {
							{
								addressCountry = "United States";
								addressLocality = RandomTestUtil.randomString();
								addressRegion = "California";
								addressType = "other";
								postalCode = String.valueOf(
									RandomTestUtil.randomInt());
								streetAddressLine1 =
									RandomTestUtil.randomString();
							}
						}
					});
				setTelephones(
					() -> new Phone[] {
						new Phone() {
							{
								extension = String.valueOf(
									RandomTestUtil.randomInt());
								phoneNumber = String.valueOf(
									RandomTestUtil.randomInt());
								phoneType = "fax";
							}
						}
					});
				setWebUrls(
					() -> new WebUrl[] {
						new WebUrl() {
							{
								primary = true;
								url =
									"https://" + RandomTestUtil.randomString() +
										".com";
								urlType = "personal";
							}
						}
					});
			}
		};
	}

	private void _testGetOrganizationsPageWithFilter() throws Exception {
		Page<Organization> page = organizationResource.getOrganizationsPage(
			null, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		// Sleep for 1 second to ensure that organization 1 and existing
		// organizations are created 1 second apart

		Thread.sleep(1000);

		Organization organization1 = testGetOrganizationsPage_addOrganization(
			randomOrganization());

		// Sleep for 1 second to ensure that organization 1 and organization 2
		// are created 1 second apart

		Thread.sleep(1000);

		Organization organization2 = testGetOrganizationsPage_addOrganization(
			randomOrganization());

		DateFormat dateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");

		page = organizationResource.getOrganizationsPage(
			null, null,
			"dateCreated lt " +
				dateFormat.format(organization1.getDateCreated()),
			Pagination.of(1, 2), null);

		Assert.assertEquals(totalCount, page.getTotalCount());

		page = organizationResource.getOrganizationsPage(
			null, null,
			"dateCreated ge " +
				dateFormat.format(organization1.getDateModified()),
			Pagination.of(1, 2), null);

		Assert.assertEquals(2, page.getTotalCount());

		// Sleep for 1 second to ensure that organization 1 and organization 2
		// are modified 1 second apart

		Thread.sleep(1000);

		organization1.setName(
			StringUtil.toLowerCase(RandomTestUtil.randomString()));

		organization1 = organizationResource.patchOrganization(
			organization1.getId(), organization1);

		page = organizationResource.getOrganizationsPage(
			null, null,
			"dateModified ge " +
				dateFormat.format(organization1.getDateModified()),
			Pagination.of(1, 2), null);

		Assert.assertEquals(1, page.getTotalCount());

		assertContains(organization1, (List<Organization>)page.getItems());

		page = organizationResource.getOrganizationsPage(
			null, null,
			"dateModified lt " +
				dateFormat.format(organization1.getDateModified()),
			Pagination.of(1, 2), null);

		Assert.assertEquals(totalCount + 1, page.getTotalCount());

		assertContains(organization2, (List<Organization>)page.getItems());
	}

	private void _testGetOrganizationWithNestedFields() throws Exception {
		Organization postOrganization = randomOrganization();

		postOrganization.setImageBase64(
			Base64.encode(
				FileUtil.getBytes(getClass(), "/images/liferay.png")));
		postOrganization.setKeywords(
			new String[] {RandomTestUtil.randomString()});
		postOrganization.setOrganizationContactInformation(
			_getRandomOrganizationContactInformation());

		postOrganization = _addOrganization(postOrganization, "0");

		AccountEntry accountEntry1 = _accountEntryLocalService.addAccountEntry(
			StringPool.BLANK, TestPropsValues.getUserId(),
			AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			null, null, null, AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS,
			WorkflowConstants.STATUS_APPROVED,
			ServiceContextTestUtil.getServiceContext());

		organizationResource.postAccountOrganization(
			accountEntry1.getAccountEntryId(), postOrganization.getId());

		AccountEntry accountEntry2 = _accountEntryLocalService.addAccountEntry(
			StringPool.BLANK, TestPropsValues.getUserId(),
			AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			null, null, null, AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS,
			WorkflowConstants.STATUS_APPROVED,
			ServiceContextTestUtil.getServiceContext());

		AssetEntry assetEntry = _assetEntryLocalService.fetchEntry(
			com.liferay.portal.kernel.model.Organization.class.getName(),
			GetterUtil.getLong(postOrganization.getId()));

		AssetTag assetTag = _assetTagLocalService.addTag(
			RandomTestUtil.randomString(), assetEntry.getUserId(),
			assetEntry.getGroupId(), RandomTestUtil.randomString(),
			new ServiceContext());

		_assetTagLocalService.addAssetEntryAssetTag(
			assetEntry.getEntryId(), assetTag);

		AssetVocabulary assetVocabulary = AssetTestUtil.addVocabulary(
			TestPropsValues.getGroupId());

		AssetCategory assetCategory = AssetTestUtil.addCategory(
			TestPropsValues.getGroupId(), assetVocabulary.getVocabularyId());

		_assetEntryAssetCategoryRelLocalService.addAssetEntryAssetCategoryRel(
			assetEntry.getEntryId(), assetCategory.getCategoryId());

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		com.liferay.portal.kernel.model.Organization organization =
			_organizationLocalService.getOrganization(
				GetterUtil.getLong(postOrganization.getId()));

		_roleLocalService.addGroupRole(
			organization.getGroupId(), role.getRoleId());

		_resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(),
			com.liferay.portal.kernel.model.Organization.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL, postOrganization.getId(),
			role.getRoleId(), new String[] {ActionKeys.DELETE});

		User user1 = UserTestUtil.addUser();

		organizationResource.postUserAccountsByEmailAddress(
			String.valueOf(organization.getOrganizationId()), null,
			new String[] {user1.getEmailAddress()});

		User user2 = UserTestUtil.addUser();

		OrganizationResource organizationResource =
			OrganizationResource.builder(
			).authentication(
				"test@liferay.com", PropsValues.DEFAULT_ADMIN_PASSWORD
			).locale(
				LocaleUtil.getDefault()
			).parameters(
				"nestedFields",
				"accountBriefs,creator,imageBase64,permissions,roleBriefs," +
					"taxonomyCategoryBriefs,userAccountBriefs"
			).build();

		Organization getOrganization = organizationResource.getOrganization(
			String.valueOf(organization.getOrganizationId()));

		Assert.assertTrue(
			ArrayUtil.exists(
				getOrganization.getAccountBriefs(),
				accountBrief ->
					accountBrief.getId() == accountEntry1.getAccountEntryId()));
		Assert.assertFalse(
			ArrayUtil.exists(
				getOrganization.getAccountBriefs(),
				accountBrief ->
					accountBrief.getId() == accountEntry2.getAccountEntryId()));

		Creator creator = getOrganization.getCreator();

		Assert.assertTrue(creator.getId() == TestPropsValues.getUserId());

		User user3 = TestPropsValues.getUser();

		Assert.assertTrue(
			Objects.equals(
				creator.getExternalReferenceCode(),
				user3.getExternalReferenceCode()));

		Assert.assertNotNull(getOrganization.getImageBase64());
		Assert.assertNotEquals(
			0, GetterUtil.getLong(getOrganization.getImageId()));
		Assert.assertTrue(
			ArrayUtil.exists(
				postOrganization.getKeywords(),
				keyword -> Objects.equals(
					keyword, getOrganization.getKeywords()[0])));
		Assert.assertTrue(
			Objects.equals(
				getOrganization.getOrganizationContactInformation(),
				postOrganization.getOrganizationContactInformation()));
		Assert.assertTrue(
			ArrayUtil.exists(
				getOrganization.getPermissions(),
				permission ->
					Objects.equals(permission.getRoleName(), role.getName()) &&
					(permission.getActionIds().length == 1) &&
					Objects.equals(permission.getActionIds()[0], "DELETE")));
		Assert.assertTrue(
			ArrayUtil.exists(
				getOrganization.getRoleBriefs(),
				groupRole -> groupRole.getId() == role.getRoleId()));
		Assert.assertTrue(
			ArrayUtil.exists(
				getOrganization.getTaxonomyCategoryBriefs(),
				taxonomyCategoryBrief -> Objects.equals(
					taxonomyCategoryBrief.getTaxonomyCategoryId(),
					assetCategory.getCategoryId())));
		Assert.assertTrue(
			ArrayUtil.exists(
				getOrganization.getUserAccountBriefs(),
				userAccountBrief ->
					userAccountBrief.getId() == user1.getUserId()));
		Assert.assertFalse(
			ArrayUtil.exists(
				getOrganization.getUserAccountBriefs(),
				userAccountBrief ->
					userAccountBrief.getId() == user2.getUserId()));
	}

	private void _testPatchOrganizationByExternalReferenceCodeWithImageExternalReferenceCode()
		throws Exception {

		Organization postOrganization =
			testPatchOrganizationByExternalReferenceCode_addOrganization();

		Organization randomPatchOrganization = randomPatchOrganization();

		FileEntry fileEntry = _addImageFileEntry();

		randomPatchOrganization.setImageExternalReferenceCode(
			fileEntry.getExternalReferenceCode());

		randomPatchOrganization.setImageId(0L);

		Organization patchOrganization =
			organizationResource.patchOrganizationByExternalReferenceCode(
				postOrganization.getExternalReferenceCode(),
				randomPatchOrganization);

		Assert.assertTrue(patchOrganization.getImageId() > 0);
	}

	private void _testPatchOrganizationWithImageExternalReferenceCode()
		throws Exception {

		Organization postOrganization = testPatchOrganization_addOrganization();

		Organization randomPatchOrganization = randomPatchOrganization();

		FileEntry fileEntry = _addImageFileEntry();

		randomPatchOrganization.setImageExternalReferenceCode(
			fileEntry.getExternalReferenceCode());

		randomPatchOrganization.setImageId(0L);

		Organization patchOrganization = organizationResource.patchOrganization(
			postOrganization.getId(), randomPatchOrganization);

		Assert.assertTrue(patchOrganization.getImageId() > 0);
	}

	private void _testPatchOrganizationWithKeywords() throws Exception {
		Organization postOrganization = testPatchOrganization_addOrganization();

		Organization randomPatchOrganization = randomPatchOrganization();

		String[] keywords = {
			RandomTestUtil.randomString(), RandomTestUtil.randomString()
		};

		randomPatchOrganization.setKeywords(keywords);

		Organization patchOrganization = organizationResource.patchOrganization(
			postOrganization.getId(), randomPatchOrganization);

		Assert.assertTrue(
			ArrayUtil.containsAll(patchOrganization.getKeywords(), keywords));
	}

	private void _testPostOrganizationBatch() throws Exception {
		Organization organization = randomOrganization();

		AccountEntry accountEntry1 = _accountEntryLocalService.addAccountEntry(
			StringPool.BLANK, _user.getUserId(),
			AccountConstants.PARENT_ACCOUNT_ENTRY_ID_DEFAULT,
			RandomTestUtil.randomString(), null, null,
			RandomTestUtil.randomString() + "@liferay.com", null, null,
			AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS,
			WorkflowConstants.STATUS_APPROVED,
			ServiceContextTestUtil.getServiceContext());

		AccountBrief accountBrief1 = new AccountBrief() {
			{
				externalReferenceCode =
					accountEntry1.getExternalReferenceCode();
				name = accountEntry1.getName();
				type = accountEntry1.getType();
			}
		};

		AccountBrief accountBrief2 = new AccountBrief() {
			{
				externalReferenceCode = RandomTestUtil.randomString();
				type = AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS;
			}
		};

		organization.setAccountBriefs(
			new AccountBrief[] {accountBrief1, accountBrief2});

		organization.setImageBase64(
			Base64.encode(
				FileUtil.getBytes(getClass(), "/images/liferay.png")));

		Organization parentOrganization = randomOrganization();

		organization.setParentOrganization(parentOrganization);

		Role role1 = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		Permission permission1 = new Permission() {
			{
				actionIds = new String[] {ActionKeys.VIEW};
				roleExternalReferenceCode = role1.getExternalReferenceCode();
				roleName = role1.getName();
				roleType = RoleConstants.getTypeLabel(role1.getType());
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

		organization.setPermissions(
			new Permission[] {permission1, permission2});

		Role role2 = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		RoleBrief roleBrief1 = new RoleBrief() {
			{
				externalReferenceCode = role2.getExternalReferenceCode();
				name = role2.getName();
				roleType = role2.getType();
			}
		};

		RoleBrief roleBrief2 = new RoleBrief() {
			{
				externalReferenceCode = RandomTestUtil.randomString();
				name = RandomTestUtil.randomString();
				roleType = RoleConstants.TYPE_ORGANIZATION;
			}
		};

		organization.setRoleBriefs(new RoleBrief[] {roleBrief1, roleBrief2});

		AssetVocabulary assetVocabulary = AssetTestUtil.addVocabulary(
			TestPropsValues.getGroupId());

		AssetCategory assetCategory1 = AssetTestUtil.addCategory(
			TestPropsValues.getGroupId(), assetVocabulary.getVocabularyId());

		Group group = _groupLocalService.getGroup(assetCategory1.getGroupId());

		TaxonomyCategoryReference taxonomyCategoryReference1 =
			new TaxonomyCategoryReference() {
				{
					externalReferenceCode =
						assetCategory1.getExternalReferenceCode();
					siteKey = group.getGroupKey();
				}
			};

		TaxonomyCategoryReference taxonomyCategoryReference2 =
			new TaxonomyCategoryReference() {
				{
					externalReferenceCode = RandomTestUtil.randomString();
					siteKey = group.getGroupKey();
				}
			};

		organization.setTaxonomyCategoryBriefs(
			new TaxonomyCategoryBrief[] {
				new TaxonomyCategoryBrief() {
					{
						taxonomyCategoryReference = taxonomyCategoryReference1;
					}
				},
				new TaxonomyCategoryBrief() {
					{
						taxonomyCategoryReference = taxonomyCategoryReference2;
					}
				}
			});

		waitForFinish(
			"COMPLETED",
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					"items",
					JSONUtil.put(
						_jsonFactory.createJSONObject(organization.toString()))
				).toString(),
				"headless-admin-user/v1.0/organizations/batch",
				Http.Method.POST));

		AccountEntry accountEntry2 =
			_accountEntryLocalService.fetchAccountEntryByExternalReferenceCode(
				accountBrief1.getExternalReferenceCode(),
				TestPropsValues.getCompanyId());

		Assert.assertEquals(
			accountEntry1.getAccountEntryId(),
			accountEntry2.getAccountEntryId());

		com.liferay.portal.kernel.model.Organization
			serviceBuilderOrganization =
				_organizationLocalService.
					fetchOrganizationByExternalReferenceCode(
						organization.getExternalReferenceCode(),
						TestPropsValues.getCompanyId());

		List<AccountEntryOrganizationRel> accountEntryOrganizationRels =
			_accountEntryOrganizationRelLocalService.
				getAccountEntryOrganizationRelsByOrganizationId(
					Long.valueOf(
						serviceBuilderOrganization.getOrganizationId()));

		Assert.assertTrue(
			ListUtil.exists(
				accountEntryOrganizationRels,
				accountEntryOrganizationRel ->
					accountEntryOrganizationRel.getAccountEntryId() ==
						accountEntry2.getAccountEntryId()));

		AccountEntry accountEntry3 =
			_accountEntryLocalService.fetchAccountEntryByExternalReferenceCode(
				accountBrief2.getExternalReferenceCode(),
				TestPropsValues.getCompanyId());

		Assert.assertTrue(
			ListUtil.exists(
				accountEntryOrganizationRels,
				accountEntryOrganizationRel ->
					accountEntryOrganizationRel.getAccountEntryId() ==
						accountEntry3.getAccountEntryId()));
		Assert.assertEquals(
			WorkflowConstants.STATUS_EMPTY, accountEntry3.getStatus());

		Assert.assertNotEquals(0, serviceBuilderOrganization.getLogoId());

		com.liferay.portal.kernel.model.Organization
			serviceBuilderParentOrganization =
				_organizationLocalService.
					fetchOrganizationByExternalReferenceCode(
						parentOrganization.getExternalReferenceCode(),
						TestPropsValues.getCompanyId());

		long parentOrganizationId = GetterUtil.getLong(
			serviceBuilderParentOrganization.getOrganizationId());

		Assert.assertTrue(
			parentOrganizationId ==
				serviceBuilderOrganization.getParentOrganizationId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_EMPTY,
			serviceBuilderParentOrganization.getStatus());

		Role role3 = _roleLocalService.fetchRoleByExternalReferenceCode(
			permission1.getRoleExternalReferenceCode(),
			TestPropsValues.getCompanyId());

		Assert.assertEquals(role1.getRoleId(), role3.getRoleId());

		List<com.liferay.portal.vulcan.permission.Permission> permissions =
			ListUtil.fromCollection(
				PermissionUtil.getPermissions(
					TestPropsValues.getCompanyId(),
					_resourceActionLocalService.getResourceActions(
						com.liferay.portal.kernel.model.Organization.class.
							getName()),
					serviceBuilderOrganization.getOrganizationId(),
					com.liferay.portal.kernel.model.Organization.class.
						getName(),
					null));

		Assert.assertTrue(
			ListUtil.exists(
				permissions,
				permission -> {
					String[] actionIds = permission.getActionIds();

					return (actionIds.length == 1) &&
						   Objects.equals(ActionKeys.VIEW, actionIds[0]) &&
						   Objects.equals(
							   role3.getExternalReferenceCode(),
							   permission.getRoleExternalReferenceCode());
				}));

		Role role4 = _roleLocalService.fetchRoleByExternalReferenceCode(
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
							   role4.getExternalReferenceCode(),
							   permission.getRoleExternalReferenceCode());
				}));

		Assert.assertEquals(permission2.getRoleName(), role4.getName());
		Assert.assertEquals(
			RoleConstants.getLabelType(permission2.getRoleType()),
			role4.getType());
		Assert.assertEquals(WorkflowConstants.STATUS_EMPTY, role4.getStatus());

		Role role5 = _roleLocalService.fetchRoleByExternalReferenceCode(
			roleBrief1.getExternalReferenceCode(),
			TestPropsValues.getCompanyId());

		Assert.assertEquals(role2.getRoleId(), role5.getRoleId());

		Assert.assertTrue(
			ArrayUtil.exists(
				organization.getRoleBriefs(),
				roleBrief -> Objects.equals(
					roleBrief.getExternalReferenceCode(),
					role5.getExternalReferenceCode())));

		Role role6 = _roleLocalService.fetchRoleByExternalReferenceCode(
			roleBrief2.getExternalReferenceCode(),
			TestPropsValues.getCompanyId());

		Assert.assertTrue(
			ArrayUtil.exists(
				organization.getRoleBriefs(),
				roleBrief -> Objects.equals(
					roleBrief.getExternalReferenceCode(),
					role6.getExternalReferenceCode())));
		Assert.assertEquals(WorkflowConstants.STATUS_EMPTY, role6.getStatus());

		AssetCategory assetCategory2 =
			_assetCategoryLocalService.
				fetchAssetCategoryByExternalReferenceCode(
					taxonomyCategoryReference1.getExternalReferenceCode(),
					group.getGroupId());

		Assert.assertEquals(
			assetCategory1.getCategoryId(), assetCategory2.getCategoryId());

		List<AssetCategory> assetCategories =
			_assetCategoryLocalService.getCategories(
				com.liferay.portal.kernel.model.Organization.class.getName(),
				serviceBuilderOrganization.getOrganizationId());

		Assert.assertTrue(
			ListUtil.exists(
				assetCategories,
				assetCategory ->
					assetCategory.getCategoryId() ==
						assetCategory2.getCategoryId()));

		AssetCategory assetCategory3 =
			_assetCategoryLocalService.
				fetchAssetCategoryByExternalReferenceCode(
					taxonomyCategoryReference2.getExternalReferenceCode(),
					group.getGroupId());

		Assert.assertTrue(
			ListUtil.exists(
				assetCategories,
				assetCategory ->
					assetCategory.getCategoryId() ==
						assetCategory3.getCategoryId()));
		Assert.assertEquals(
			WorkflowConstants.STATUS_EMPTY, assetCategory3.getStatus());
	}

	private void _testPostOrganizationWithCommentOverMaximumLength()
		throws Exception {

		Organization organization = randomOrganization();

		organization.setComment(RandomTestUtil.randomString(4001));

		assertHttpResponseStatusCode(
			400,
			organizationResource.postOrganizationHttpResponse(organization));
	}

	private void _testPostOrganizationWithCustomFields() throws Exception {
		ExpandoTable expandoTable = _expandoTableLocalService.addTable(
			testGroup.getCompanyId(),
			_classNameLocalService.getClassNameId(
				com.liferay.portal.kernel.model.Organization.class),
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

		Organization randomOrganization = randomOrganization();

		String value = RandomTestUtil.randomString();

		randomOrganization.setCustomFields(
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

		Organization postOrganization = testPostOrganization_addOrganization(
			randomOrganization);

		assertEquals(randomOrganization, postOrganization);
		assertValid(postOrganization);

		Assert.assertNotNull(postOrganization.getCustomFields());

		CustomField postOrganizationCustomField =
			postOrganization.getCustomFields()[0];

		Assert.assertEquals(
			expandoColumn.getName(), postOrganizationCustomField.getName());
	}

	private void _testPostOrganizationWithImageExternalReferenceCode()
		throws Exception {

		Organization randomOrganization = randomOrganization();

		FileEntry fileEntry = _addImageFileEntry();

		randomOrganization.setImageExternalReferenceCode(
			fileEntry.getExternalReferenceCode());

		randomOrganization.setImageId(0L);

		Organization postOrganization = organizationResource.postOrganization(
			randomOrganization);

		Assert.assertTrue(postOrganization.getImageId() > 0);
	}

	private void _testPostOrganizationWithNameOverMaximumLength()
		throws Exception {

		Organization organization = randomOrganization();

		organization.setName(RandomTestUtil.randomString(101));

		assertHttpResponseStatusCode(
			400,
			organizationResource.postOrganizationHttpResponse(organization));
	}

	private void _testPutOrganizationByExternalReferenceCodeKeepsExternalReferenceCode()
		throws Exception {

		String externalReferenceCode = RandomTestUtil.randomString();

		Organization putOrganization =
			organizationResource.putOrganizationByExternalReferenceCode(
				externalReferenceCode,
				new Organization() {
					{
						setName(RandomTestUtil.randomString());
					}
				});

		Assert.assertEquals(
			externalReferenceCode, putOrganization.getExternalReferenceCode());
	}

	private void _testPutOrganizationByExternalReferenceCodeWithImageExternalReferenceCode()
		throws Exception {

		Organization postOrganization =
			testPutOrganizationByExternalReferenceCode_addOrganization();

		Organization randomPutOrganization = randomOrganization();

		FileEntry fileEntry = _addImageFileEntry();

		randomPutOrganization.setImageExternalReferenceCode(
			fileEntry.getExternalReferenceCode());

		randomPutOrganization.setImageId(0L);

		Organization putOrganization =
			organizationResource.putOrganizationByExternalReferenceCode(
				postOrganization.getExternalReferenceCode(),
				randomPutOrganization);

		Assert.assertTrue(putOrganization.getImageId() > 0);
	}

	private void _testPutOrganizationWithImageExternalReferenceCode()
		throws Exception {

		Organization postOrganization = testPutOrganization_addOrganization();

		Organization randomPutOrganization = randomOrganization();

		FileEntry fileEntry = _addImageFileEntry();

		randomPutOrganization.setImageExternalReferenceCode(
			fileEntry.getExternalReferenceCode());

		randomPutOrganization.setImageId(0L);

		Organization putOrganization = organizationResource.putOrganization(
			postOrganization.getId(), randomPutOrganization);

		Assert.assertTrue(putOrganization.getImageId() > 0);
	}

	private String[] _toEmailAddresses(List<User> users) {
		return TransformUtil.transformToArray(
			users, User::getEmailAddress, String.class);
	}

	private AccountEntry _accountEntry;

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	@Inject
	private AccountEntryOrganizationRelLocalService
		_accountEntryOrganizationRelLocalService;

	@Inject
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Inject
	private AssetEntryAssetCategoryRelLocalService
		_assetEntryAssetCategoryRelLocalService;

	@Inject
	private AssetEntryLocalService _assetEntryLocalService;

	@Inject
	private AssetTagLocalService _assetTagLocalService;

	@Inject
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private ExpandoColumnLocalService _expandoColumnLocalService;

	@Inject
	private ExpandoTableLocalService _expandoTableLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private JSONFactory _jsonFactory;

	@Inject
	private OrganizationLocalService _organizationLocalService;

	@Inject
	private ResourceActionLocalService _resourceActionLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	private User _user;

	@Inject
	private UserLocalService _userLocalService;

}