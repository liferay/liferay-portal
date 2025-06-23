/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.resource.v1_0.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountEntryModel;
import com.liferay.account.model.AccountEntryOrganizationRel;
import com.liferay.account.model.AccountGroup;
import com.liferay.account.model.AccountGroupRel;
import com.liferay.account.model.AccountRole;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountEntryOrganizationRelLocalService;
import com.liferay.account.service.AccountEntryUserRelLocalService;
import com.liferay.account.service.AccountGroupLocalService;
import com.liferay.account.service.AccountGroupRelLocalService;
import com.liferay.account.service.AccountRoleLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.entry.rel.service.AssetEntryAssetCategoryRelLocalService;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.asset.test.util.AssetTestUtil;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.expando.kernel.exception.NoSuchValueException;
import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoColumnConstants;
import com.liferay.expando.kernel.model.ExpandoTable;
import com.liferay.expando.kernel.service.ExpandoColumnLocalService;
import com.liferay.expando.kernel.service.ExpandoTableLocalService;
import com.liferay.expando.test.util.ExpandoTestUtil;
import com.liferay.headless.admin.user.client.custom.field.CustomField;
import com.liferay.headless.admin.user.client.custom.field.CustomValue;
import com.liferay.headless.admin.user.client.dto.v1_0.Account;
import com.liferay.headless.admin.user.client.dto.v1_0.AccountContactInformation;
import com.liferay.headless.admin.user.client.dto.v1_0.AccountGroupBrief;
import com.liferay.headless.admin.user.client.dto.v1_0.Creator;
import com.liferay.headless.admin.user.client.dto.v1_0.EmailAddress;
import com.liferay.headless.admin.user.client.dto.v1_0.Phone;
import com.liferay.headless.admin.user.client.dto.v1_0.PostalAddress;
import com.liferay.headless.admin.user.client.dto.v1_0.TaxonomyCategoryBrief;
import com.liferay.headless.admin.user.client.dto.v1_0.TaxonomyCategoryReference;
import com.liferay.headless.admin.user.client.dto.v1_0.WebUrl;
import com.liferay.headless.admin.user.client.pagination.Page;
import com.liferay.headless.admin.user.client.pagination.Pagination;
import com.liferay.headless.admin.user.client.permission.Permission;
import com.liferay.headless.admin.user.client.problem.Problem;
import com.liferay.headless.admin.user.client.resource.v1_0.AccountResource;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.repository.LocalRepository;
import com.liferay.portal.kernel.repository.RepositoryProviderUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.AddressLocalService;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.OrganizationLocalService;
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
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.util.PropsValues;
import com.liferay.portal.vulcan.permission.PermissionUtil;

import java.io.InputStream;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Drew Brokke
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class AccountResourceTest extends BaseAccountResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_accountGroup = _accountGroupLocalService.addAccountGroup(
			StringPool.BLANK, TestPropsValues.getUserId(),
			StringUtil.randomString(), StringUtil.randomString(),
			new ServiceContext());
	}

	@After
	@Override
	public void tearDown() throws Exception {
	}

	@Override
	@Test
	public void testDeleteOrganizationAccounts() throws Exception {
		List<AccountEntry> accountEntries = Arrays.asList(
			_addAccountEntry(), _addAccountEntry(), _addAccountEntry());

		Organization organization = OrganizationTestUtil.addOrganization();

		for (AccountEntry accountEntry : accountEntries) {
			_accountEntryOrganizationRelLocalService.
				addAccountEntryOrganizationRel(
					accountEntry.getAccountEntryId(),
					organization.getOrganizationId());
		}

		Assert.assertEquals(
			3,
			_accountEntryOrganizationRelLocalService.
				getAccountEntryOrganizationRelsCountByOrganizationId(
					organization.getOrganizationId()));

		Long[] accountEntryIds = ListUtil.toArray(
			accountEntries.subList(1, accountEntries.size()),
			AccountEntry.ACCOUNT_ENTRY_ID_ACCESSOR);

		accountResource.deleteOrganizationAccounts(
			organization.getOrganizationId(), accountEntryIds);

		Assert.assertEquals(
			1,
			_accountEntryOrganizationRelLocalService.
				getAccountEntryOrganizationRelsCountByOrganizationId(
					organization.getOrganizationId()));

		AccountEntry accountEntry = accountEntries.get(0);

		Assert.assertTrue(
			_accountEntryOrganizationRelLocalService.
				hasAccountEntryOrganizationRel(
					accountEntry.getAccountEntryId(),
					organization.getOrganizationId()));
	}

	@Override
	@Test
	public void testDeleteOrganizationAccountsByExternalReferenceCode()
		throws Exception {

		List<AccountEntry> accountEntries = Arrays.asList(
			_addAccountEntry(), _addAccountEntry(), _addAccountEntry());

		Organization organization = OrganizationTestUtil.addOrganization();

		for (AccountEntry accountEntry : accountEntries) {
			_accountEntryOrganizationRelLocalService.
				addAccountEntryOrganizationRel(
					accountEntry.getAccountEntryId(),
					organization.getOrganizationId());
		}

		Assert.assertEquals(
			3,
			_accountEntryOrganizationRelLocalService.
				getAccountEntryOrganizationRelsCountByOrganizationId(
					organization.getOrganizationId()));

		String[] externalReferenceCodes = TransformUtil.transformToArray(
			accountEntries.subList(1, accountEntries.size()),
			AccountEntryModel::getExternalReferenceCode, String.class);

		accountResource.deleteOrganizationAccountsByExternalReferenceCode(
			organization.getOrganizationId(), externalReferenceCodes);

		Assert.assertEquals(
			1,
			_accountEntryOrganizationRelLocalService.
				getAccountEntryOrganizationRelsCountByOrganizationId(
					organization.getOrganizationId()));

		AccountEntry accountEntry = accountEntries.get(0);

		Assert.assertTrue(
			_accountEntryOrganizationRelLocalService.
				hasAccountEntryOrganizationRel(
					accountEntry.getAccountEntryId(),
					organization.getOrganizationId()));
	}

	@Override
	@Test
	public void testDeleteOrganizationByExternalReferenceCodeAccounts()
		throws Exception {

		List<AccountEntry> accountEntries = Arrays.asList(
			_addAccountEntry(), _addAccountEntry(), _addAccountEntry());

		Organization organization = OrganizationTestUtil.addOrganization();

		for (AccountEntry accountEntry : accountEntries) {
			_accountEntryOrganizationRelLocalService.
				addAccountEntryOrganizationRel(
					accountEntry.getAccountEntryId(),
					organization.getOrganizationId());
		}

		Assert.assertEquals(
			3,
			_accountEntryOrganizationRelLocalService.
				getAccountEntryOrganizationRelsCountByOrganizationId(
					organization.getOrganizationId()));

		Long[] accountEntryIds = ListUtil.toArray(
			accountEntries.subList(1, accountEntries.size()),
			AccountEntry.ACCOUNT_ENTRY_ID_ACCESSOR);

		accountResource.deleteOrganizationByExternalReferenceCodeAccounts(
			organization.getExternalReferenceCode(), accountEntryIds);

		Assert.assertEquals(
			1,
			_accountEntryOrganizationRelLocalService.
				getAccountEntryOrganizationRelsCountByOrganizationId(
					organization.getOrganizationId()));

		AccountEntry accountEntry = accountEntries.get(0);

		Assert.assertTrue(
			_accountEntryOrganizationRelLocalService.
				hasAccountEntryOrganizationRel(
					accountEntry.getAccountEntryId(),
					organization.getOrganizationId()));
	}

	@Override
	@Test
	public void testDeleteOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountByExternalReferenceCode()
		throws Exception {

		List<AccountEntry> accountEntries = Arrays.asList(
			_addAccountEntry(), _addAccountEntry(), _addAccountEntry());

		Organization organization = OrganizationTestUtil.addOrganization();

		for (AccountEntry accountEntry : accountEntries) {
			_accountEntryOrganizationRelLocalService.
				addAccountEntryOrganizationRel(
					accountEntry.getAccountEntryId(),
					organization.getOrganizationId());
		}

		Assert.assertEquals(
			3,
			_accountEntryOrganizationRelLocalService.
				getAccountEntryOrganizationRelsCountByOrganizationId(
					organization.getOrganizationId()));

		String[] externalReferenceCodes = TransformUtil.transformToArray(
			accountEntries.subList(1, accountEntries.size()),
			AccountEntryModel::getExternalReferenceCode, String.class);

		accountResource.
			deleteOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountByExternalReferenceCode(
				organization.getExternalReferenceCode(),
				externalReferenceCodes);

		Assert.assertEquals(
			1,
			_accountEntryOrganizationRelLocalService.
				getAccountEntryOrganizationRelsCountByOrganizationId(
					organization.getOrganizationId()));

		AccountEntry accountEntry = accountEntries.get(0);

		Assert.assertTrue(
			_accountEntryOrganizationRelLocalService.
				hasAccountEntryOrganizationRel(
					accountEntry.getAccountEntryId(),
					organization.getOrganizationId()));
	}

	@Override
	@Test
	public void testGetAccount() throws Exception {
		super.testGetAccount();

		_testGetAccountWithNestedFields();
	}

	@Override
	@Test
	public void testGetAccountsPage() throws Exception {
		super.testGetAccountsPage();

		AccountEntry accountEntry1 = _addAccountEntry();
		AccountEntry accountEntry2 = _addAccountEntry();
		Organization organization = OrganizationTestUtil.addOrganization();

		_addAccountEntry();

		_testGetAccountsPage(Arrays.asList(accountEntry1, accountEntry2), null);
		_testGetAccountsPage(
			Collections.emptyList(), organization.getOrganizationId());

		_accountEntryOrganizationRelLocalService.addAccountEntryOrganizationRel(
			accountEntry1.getAccountEntryId(),
			organization.getOrganizationId());

		_testGetAccountsPage(
			Collections.singletonList(accountEntry1),
			organization.getOrganizationId());
		_testGetAccountsPageWithCustomFields();
		_testGetAccountsPageWithNestedFields();
	}

	@Override
	@Test
	public void testPatchAccount() throws Exception {
		super.testPatchAccount();

		_testPatchAccountWithContactInformation();
		_testPatchAccountWithEmptyOrganizationExternalReferenceCodes();
		_testPatchAccountWithEmptyOrganizationIds();
		_testPatchAccountWithMoreExternalReferenceCodes();
		_testPatchAccountWithPostalAddressPhoneNumber();
		_testPatchAccountWithoutName();
	}

	@Override
	@Test
	public void testPatchAccountByExternalReferenceCode() throws Exception {
		super.testPatchAccountByExternalReferenceCode();

		_testPatchAccountByExternalReferenceCodeWithMoreExternalReferenceCodes();
		_testPatchAccountByExternalReferenceCodeWithoutName();
	}

	@Override
	@Test
	public void testPatchOrganizationMoveAccounts() throws Exception {
		List<AccountEntry> accountEntries = Arrays.asList(
			_addAccountEntry(), _addAccountEntry(), _addAccountEntry());

		Organization organization1 = OrganizationTestUtil.addOrganization();

		for (AccountEntry accountEntry : accountEntries) {
			_accountEntryOrganizationRelLocalService.
				addAccountEntryOrganizationRel(
					accountEntry.getAccountEntryId(),
					organization1.getOrganizationId());
		}

		Assert.assertEquals(
			3,
			_accountEntryOrganizationRelLocalService.
				getAccountEntryOrganizationRelsCountByOrganizationId(
					organization1.getOrganizationId()));

		Organization organization2 = OrganizationTestUtil.addOrganization();

		Assert.assertEquals(
			0,
			_accountEntryOrganizationRelLocalService.
				getAccountEntryOrganizationRelsCountByOrganizationId(
					organization2.getOrganizationId()));

		accountResource.patchOrganizationMoveAccounts(
			organization1.getOrganizationId(),
			organization2.getOrganizationId(),
			ListUtil.toArray(
				accountEntries, AccountEntry.ACCOUNT_ENTRY_ID_ACCESSOR));

		Assert.assertEquals(
			0,
			_accountEntryOrganizationRelLocalService.
				getAccountEntryOrganizationRelsCountByOrganizationId(
					organization1.getOrganizationId()));

		Assert.assertEquals(
			3,
			_accountEntryOrganizationRelLocalService.
				getAccountEntryOrganizationRelsCountByOrganizationId(
					organization2.getOrganizationId()));
	}

	@Override
	@Test
	public void testPatchOrganizationMoveAccountsByExternalReferenceCode()
		throws Exception {

		List<AccountEntry> accountEntries = Arrays.asList(
			_addAccountEntry(), _addAccountEntry(), _addAccountEntry());

		Organization organization1 = OrganizationTestUtil.addOrganization();

		for (AccountEntry accountEntry : accountEntries) {
			_accountEntryOrganizationRelLocalService.
				addAccountEntryOrganizationRel(
					accountEntry.getAccountEntryId(),
					organization1.getOrganizationId());
		}

		Assert.assertEquals(
			3,
			_accountEntryOrganizationRelLocalService.
				getAccountEntryOrganizationRelsCountByOrganizationId(
					organization1.getOrganizationId()));

		Organization organization2 = OrganizationTestUtil.addOrganization();

		Assert.assertEquals(
			0,
			_accountEntryOrganizationRelLocalService.
				getAccountEntryOrganizationRelsCountByOrganizationId(
					organization2.getOrganizationId()));

		String[] externalReferenceCodes = TransformUtil.transformToArray(
			accountEntries, AccountEntryModel::getExternalReferenceCode,
			String.class);

		accountResource.patchOrganizationMoveAccountsByExternalReferenceCode(
			organization1.getOrganizationId(),
			organization2.getOrganizationId(), externalReferenceCodes);

		Assert.assertEquals(
			0,
			_accountEntryOrganizationRelLocalService.
				getAccountEntryOrganizationRelsCountByOrganizationId(
					organization1.getOrganizationId()));

		Assert.assertEquals(
			3,
			_accountEntryOrganizationRelLocalService.
				getAccountEntryOrganizationRelsCountByOrganizationId(
					organization2.getOrganizationId()));
	}

	@FeatureFlag("LPD-47858")
	@Override
	@Test
	public void testPostAccount() throws Exception {
		super.testPostAccount();

		_testPostAccountBatch();
		_testPostAccountDuplicateExternalReferenceCode();
		_testPostAccountWithContactInformation();
		_testPostAccountWithMoreExternalReferenceCodes();
		_testPostAccountWithPostalAddressPhoneNumber();
	}

	@Override
	@Test
	public void testPostOrganizationAccounts() throws Exception {
		Organization organization = OrganizationTestUtil.addOrganization();

		Assert.assertEquals(
			0,
			_accountEntryOrganizationRelLocalService.
				getAccountEntryOrganizationRelsCountByOrganizationId(
					organization.getOrganizationId()));

		List<AccountEntry> accountEntries = Arrays.asList(
			_addAccountEntry(), _addAccountEntry(), _addAccountEntry());

		Long[] accountEntryIds = ListUtil.toArray(
			accountEntries, AccountEntry.ACCOUNT_ENTRY_ID_ACCESSOR);

		accountResource.postOrganizationAccounts(
			organization.getOrganizationId(), accountEntryIds);

		for (Long accountEntryId : accountEntryIds) {
			Assert.assertTrue(
				_accountEntryOrganizationRelLocalService.
					hasAccountEntryOrganizationRel(
						accountEntryId, organization.getOrganizationId()));
		}
	}

	@Override
	@Test
	public void testPostOrganizationAccountsByExternalReferenceCode()
		throws Exception {

		Organization organization = OrganizationTestUtil.addOrganization();

		Assert.assertEquals(
			0,
			_accountEntryOrganizationRelLocalService.
				getAccountEntryOrganizationRelsCountByOrganizationId(
					organization.getOrganizationId()));

		List<AccountEntry> accountEntries = Arrays.asList(
			_addAccountEntry(), _addAccountEntry(), _addAccountEntry());

		String[] externalReferenceCodes = TransformUtil.transformToArray(
			accountEntries, AccountEntryModel::getExternalReferenceCode,
			String.class);

		accountResource.postOrganizationAccountsByExternalReferenceCode(
			organization.getOrganizationId(), externalReferenceCodes);

		for (AccountEntry accountEntry : accountEntries) {
			Assert.assertTrue(
				_accountEntryOrganizationRelLocalService.
					hasAccountEntryOrganizationRel(
						accountEntry.getAccountEntryId(),
						organization.getOrganizationId()));
		}
	}

	@Override
	@Test
	public void testPostOrganizationByExternalReferenceCodeAccounts()
		throws Exception {

		Organization organization = OrganizationTestUtil.addOrganization();

		Assert.assertEquals(
			0,
			_accountEntryOrganizationRelLocalService.
				getAccountEntryOrganizationRelsCountByOrganizationId(
					organization.getOrganizationId()));

		List<AccountEntry> accountEntries = Arrays.asList(
			_addAccountEntry(), _addAccountEntry(), _addAccountEntry());

		Long[] accountEntryIds = ListUtil.toArray(
			accountEntries, AccountEntry.ACCOUNT_ENTRY_ID_ACCESSOR);

		accountResource.postOrganizationByExternalReferenceCodeAccounts(
			organization.getExternalReferenceCode(), accountEntryIds);

		for (Long accountEntryId : accountEntryIds) {
			Assert.assertTrue(
				_accountEntryOrganizationRelLocalService.
					hasAccountEntryOrganizationRel(
						accountEntryId, organization.getOrganizationId()));
		}
	}

	@Override
	@Test
	public void testPostOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountByExternalReferenceCode()
		throws Exception {

		Organization organization = OrganizationTestUtil.addOrganization();

		Assert.assertEquals(
			0,
			_accountEntryOrganizationRelLocalService.
				getAccountEntryOrganizationRelsCountByOrganizationId(
					organization.getOrganizationId()));

		List<AccountEntry> accountEntries = Arrays.asList(
			_addAccountEntry(), _addAccountEntry(), _addAccountEntry());

		String[] externalReferenceCodes = TransformUtil.transformToArray(
			accountEntries, AccountEntryModel::getExternalReferenceCode,
			String.class);

		accountResource.
			postOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountByExternalReferenceCode(
				organization.getExternalReferenceCode(),
				externalReferenceCodes);

		for (AccountEntry accountEntry : accountEntries) {
			Assert.assertTrue(
				_accountEntryOrganizationRelLocalService.
					hasAccountEntryOrganizationRel(
						accountEntry.getAccountEntryId(),
						organization.getOrganizationId()));
		}
	}

	@Override
	@Test
	public void testPutAccount() throws Exception {
		super.testPutAccount();

		_testPutAccountWithContactInformation();
		_testPutAccountWithEmptyOrganizationExternalReferenceCodes();
		_testPutAccountWithEmptyOrganizationIds();
		_testPutAccountWithMoreExternalReferenceCodes();
		_testPutAccountWithPostalAddressPhoneNumber();
		_testPutAccountWithoutName();
	}

	@Override
	@Test
	public void testPutAccountByExternalReferenceCode() throws Exception {
		super.testPutAccountByExternalReferenceCode();

		_testPutAccountByExternalReferenceCodeWithContactInformation();
		_testPutAccountByExternalReferenceCodeWithMoreExternalReferenceCodes();
		_testPutAccountByExternalReferenceCodeWithoutName();
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"name", "type"};
	}

	@Override
	protected String[] getIgnoredEntityFieldNames() {
		return new String[] {"type"};
	}

	@Override
	protected Account randomAccount() throws Exception {
		Account account = super.randomAccount();

		account.setDefaultBillingAddressExternalReferenceCode(StringPool.BLANK);
		account.setDefaultShippingAddressExternalReferenceCode(
			StringPool.BLANK);
		account.setLogoBase64(StringPool.BLANK);
		account.setLogoId(0L);
		account.setParentAccountExternalReferenceCode(StringPool.BLANK);
		account.setParentAccountId(AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT);
		account.setStatus(WorkflowConstants.STATUS_APPROVED);
		account.setType(
			Account.Type.create(AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS));

		return account;
	}

	@Override
	protected Account testDeleteAccount_addAccount() throws Exception {
		return _postAccount();
	}

	@Override
	protected Account testDeleteAccountByExternalReferenceCode_addAccount()
		throws Exception {

		return accountResource.putAccountByExternalReferenceCode(
			StringUtil.toLowerCase(RandomTestUtil.randomString()),
			randomAccount());
	}

	@Override
	protected Account testGetAccount_addAccount() throws Exception {
		return _postAccount();
	}

	@Override
	protected Account testGetAccountByExternalReferenceCode_addAccount()
		throws Exception {

		return accountResource.putAccountByExternalReferenceCode(
			StringUtil.toLowerCase(RandomTestUtil.randomString()),
			randomAccount());
	}

	@Override
	protected Account testGetAccountGroupAccountsPage_addAccount(
			Long accountGroupId, Account account)
		throws Exception {

		account = _postAccount(account);

		_accountGroupRelLocalService.addAccountGroupRel(
			accountGroupId, AccountEntry.class.getName(), account.getId());

		return account;
	}

	@Override
	protected Long testGetAccountGroupAccountsPage_getAccountGroupId()
		throws Exception {

		return _accountGroup.getAccountGroupId();
	}

	@Override
	protected Account
			testGetAccountGroupByExternalReferenceCodeAccountsPage_addAccount(
				String accountGroupExternalReferenceCode, Account account)
		throws Exception {

		account = _postAccount(account);

		_accountGroupRelLocalService.addAccountGroupRel(
			_accountGroup.getAccountGroupId(), AccountEntry.class.getName(),
			account.getId());

		return account;
	}

	@Override
	protected String
			testGetAccountGroupByExternalReferenceCodeAccountsPage_getAccountGroupExternalReferenceCode()
		throws Exception {

		return _accountGroup.getExternalReferenceCode();
	}

	@Override
	protected Account testGetAccountsPage_addAccount(Account account)
		throws Exception {

		return _postAccount(account);
	}

	@Override
	protected Account testGetOrganizationAccountsPage_addAccount(
			String organizationId, Account account)
		throws Exception {

		Account putAccount = accountResource.putAccountByExternalReferenceCode(
			account.getExternalReferenceCode(), account);

		accountResource.postOrganizationAccounts(
			Long.valueOf(organizationId), new Long[] {putAccount.getId()});

		return putAccount;
	}

	@Override
	protected String testGetOrganizationAccountsPage_getOrganizationId()
		throws Exception {

		Organization organization = OrganizationTestUtil.addOrganization();

		return String.valueOf(organization.getOrganizationId());
	}

	@Override
	protected Account
			testGetOrganizationByExternalReferenceCodeAccountsPage_addAccount(
				String externalReferenceCode, Account account)
		throws Exception {

		Account postAccount = accountResource.postAccount(account);

		accountResource.postOrganizationByExternalReferenceCodeAccounts(
			externalReferenceCode, new Long[] {postAccount.getId()});

		return postAccount;
	}

	@Override
	protected String
			testGetOrganizationByExternalReferenceCodeAccountsPage_getExternalReferenceCode()
		throws Exception {

		Organization organization = OrganizationTestUtil.addOrganization();

		return organization.getExternalReferenceCode();
	}

	@Override
	protected Account
			testGetOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodePage_addAccount(
				String externalReferenceCode, Account account)
		throws Exception {

		Account putAccount = accountResource.putAccountByExternalReferenceCode(
			account.getExternalReferenceCode(), account);

		accountResource.
			postOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountByExternalReferenceCode(
				externalReferenceCode,
				new String[] {putAccount.getExternalReferenceCode()});

		return putAccount;
	}

	@Override
	protected String
			testGetOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodePage_getOrganizationExternalReferenceCode()
		throws Exception {

		Organization organization = OrganizationTestUtil.addOrganization();

		return organization.getExternalReferenceCode();
	}

	@Override
	protected Account testGraphQLAccount_addAccount() throws Exception {
		return _postAccount();
	}

	@Override
	protected Account testPatchAccount_addAccount() throws Exception {
		return _postAccount();
	}

	@Override
	protected Account testPatchAccountByExternalReferenceCode_addAccount()
		throws Exception {

		return accountResource.putAccountByExternalReferenceCode(
			StringUtil.toLowerCase(RandomTestUtil.randomString()),
			randomAccount());
	}

	@Override
	protected Account testPostAccount_addAccount(Account account)
		throws Exception {

		return _postAccount(account);
	}

	@Override
	protected Account testPutAccount_addAccount() throws Exception {
		return _postAccount();
	}

	@Override
	protected Account testPutAccountByExternalReferenceCode_addAccount()
		throws Exception {

		return accountResource.putAccountByExternalReferenceCode(
			StringUtil.toLowerCase(RandomTestUtil.randomString()),
			randomAccount());
	}

	private AccountEntry _addAccountEntry() throws Exception {
		AccountEntry accountEntry = _accountEntryLocalService.addAccountEntry(
			StringPool.BLANK, TestPropsValues.getUserId(),
			AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			null, null, null, AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS,
			WorkflowConstants.STATUS_APPROVED,
			ServiceContextTestUtil.getServiceContext());

		accountEntry.setExternalReferenceCode(RandomTestUtil.randomString());

		return _accountEntryLocalService.updateAccountEntry(accountEntry);
	}

	private ExpandoColumn _addExpandoColumn(
			Object defaultData, ExpandoTable expandoTable, int type,
			Map<String, String> typeSettingsProperties)
		throws Exception {

		ExpandoColumn expandoColumn = ExpandoTestUtil.addColumn(
			expandoTable, "A" + RandomTestUtil.randomString(), type,
			defaultData);

		UnicodeProperties unicodeProperties =
			expandoColumn.getTypeSettingsProperties();

		unicodeProperties.putAll(typeSettingsProperties);

		expandoColumn.setTypeSettingsProperties(unicodeProperties);

		return _expandoColumnLocalService.updateExpandoColumn(expandoColumn);
	}

	private FileEntry _addImageFileEntry() throws Exception {
		Group group = _groupLocalService.getCompanyGroup(
			_accountGroup.getCompanyId());

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

	private void _assertEquals(
		AccountContactInformation accountContactInformation1,
		AccountContactInformation accountContactInformation2) {

		_assertEquals(
			accountContactInformation1.getEmailAddresses(),
			accountContactInformation2.getEmailAddresses());
		Assert.assertEquals(
			accountContactInformation1.getFacebook(),
			accountContactInformation2.getFacebook());
		Assert.assertEquals(
			accountContactInformation1.getJabber(),
			accountContactInformation2.getJabber());
		_assertEquals(
			accountContactInformation1.getPostalAddresses(),
			accountContactInformation2.getPostalAddresses());
		Assert.assertEquals(
			accountContactInformation1.getSkype(),
			accountContactInformation2.getSkype());
		Assert.assertEquals(
			accountContactInformation1.getSms(),
			accountContactInformation2.getSms());
		_assertEquals(
			accountContactInformation1.getTelephones(),
			accountContactInformation2.getTelephones());
		Assert.assertEquals(
			accountContactInformation1.getTwitter(),
			accountContactInformation2.getTwitter());
		_assertEquals(
			accountContactInformation1.getWebUrls(),
			accountContactInformation2.getWebUrls());
	}

	private void _assertEquals(
		EmailAddress[] emailAddresses1, EmailAddress[] emailAddresses2) {

		Assert.assertEquals(
			Arrays.toString(emailAddresses1) + " does not equal " +
				Arrays.toString(emailAddresses2),
			emailAddresses1.length, emailAddresses2.length);

		Arrays.sort(
			emailAddresses1,
			Comparator.comparing(EmailAddress::getEmailAddress));
		Arrays.sort(
			emailAddresses2,
			Comparator.comparing(EmailAddress::getEmailAddress));

		for (int i = 0; i < emailAddresses1.length; i++) {
			Assert.assertTrue(
				emailAddresses1[i] + " does not equal " + emailAddresses2[i],
				_equals(emailAddresses1[i], emailAddresses2[i]));
		}
	}

	private void _assertEquals(Phone[] phones1, Phone[] phones2) {
		Assert.assertEquals(
			Arrays.toString(phones1) + " does not equal " +
				Arrays.toString(phones2),
			phones1.length, phones2.length);

		Arrays.sort(phones1, Comparator.comparing(Phone::getPhoneNumber));
		Arrays.sort(phones2, Comparator.comparing(Phone::getPhoneNumber));

		for (int i = 0; i < phones1.length; i++) {
			Assert.assertTrue(
				phones1[i] + " does not equal " + phones2[i],
				_equals(phones1[i], phones2[i]));
		}
	}

	private void _assertEquals(
		PostalAddress[] postalAddresses1, PostalAddress[] postalAddresses2) {

		Assert.assertEquals(
			Arrays.toString(postalAddresses1) + " does not equal " +
				Arrays.toString(postalAddresses2),
			postalAddresses1.length, postalAddresses2.length);

		Arrays.sort(
			postalAddresses1,
			Comparator.comparing(PostalAddress::getAddressLocality));
		Arrays.sort(
			postalAddresses2,
			Comparator.comparing(PostalAddress::getAddressLocality));

		for (int i = 0; i < postalAddresses1.length; i++) {
			Assert.assertTrue(
				postalAddresses1[i] + " does not equal " + postalAddresses2[i],
				_equals(postalAddresses1[i], postalAddresses2[i]));
		}
	}

	private void _assertEquals(WebUrl[] webUrls1, WebUrl[] webUrls2) {
		Assert.assertEquals(
			Arrays.toString(webUrls1) + " does not equal " +
				Arrays.toString(webUrls2),
			webUrls1.length, webUrls2.length);

		Arrays.sort(webUrls1, Comparator.comparing(WebUrl::getUrl));
		Arrays.sort(webUrls2, Comparator.comparing(WebUrl::getUrl));

		for (int i = 0; i < webUrls1.length; i++) {
			Assert.assertTrue(
				webUrls1[i] + " does not equal " + webUrls2[i],
				_equals(webUrls1[i], webUrls2[i]));
		}
	}

	private boolean _equals(
		EmailAddress emailAddress1, EmailAddress emailAddress2) {

		if (emailAddress1 == emailAddress2) {
			return true;
		}

		if (!Objects.equals(
				emailAddress1.getEmailAddress(),
				emailAddress2.getEmailAddress()) ||
			!Objects.equals(emailAddress1.getType(), emailAddress2.getType())) {

			return false;
		}

		return true;
	}

	private boolean _equals(Phone phone1, Phone phone2) {
		if (phone1 == phone2) {
			return true;
		}

		if (!Objects.equals(phone1.getExtension(), phone2.getExtension()) ||
			!Objects.equals(phone1.getPhoneNumber(), phone2.getPhoneNumber()) ||
			!Objects.equals(phone1.getPhoneType(), phone2.getPhoneType())) {

			return false;
		}

		return true;
	}

	private boolean _equals(
		PostalAddress postalAddress1, PostalAddress postalAddress2) {

		if (postalAddress1 == postalAddress2) {
			return true;
		}

		if (!Objects.equals(
				postalAddress1.getAddressCountry(),
				postalAddress2.getAddressCountry()) ||
			!Objects.equals(
				postalAddress1.getAddressLocality(),
				postalAddress2.getAddressLocality()) ||
			!Objects.equals(
				postalAddress1.getAddressRegion(),
				postalAddress2.getAddressRegion()) ||
			!Objects.equals(
				postalAddress1.getAddressType(),
				postalAddress2.getAddressType()) ||
			!Objects.equals(
				postalAddress1.getPostalCode(),
				postalAddress2.getPostalCode()) ||
			!Objects.equals(
				postalAddress1.getStreetAddressLine1(),
				postalAddress2.getStreetAddressLine1())) {

			return false;
		}

		return true;
	}

	private boolean _equals(WebUrl webUrl1, WebUrl webUrl2) {
		if (webUrl1 == webUrl2) {
			return true;
		}

		if (!Objects.equals(webUrl1.getUrl(), webUrl2.getUrl()) ||
			!Objects.equals(webUrl1.getUrlType(), webUrl2.getUrlType())) {

			return false;
		}

		return true;
	}

	private Object _getCustomFieldCustomValueData(Account account, String name)
		throws Exception {

		for (CustomField customField : account.getCustomFields()) {
			if (StringUtil.equals(customField.getName(), name)) {
				CustomValue customValue = customField.getCustomValue();

				return customValue.getData();
			}
		}

		throw new NoSuchValueException();
	}

	private Account _postAccount() throws Exception {
		return _postAccount(randomAccount());
	}

	private Account _postAccount(Account account) throws Exception {
		return accountResource.postAccount(account);
	}

	private AccountContactInformation _randomAccountContactInformation() {
		return new AccountContactInformation() {
			{
				emailAddresses = new EmailAddress[] {
					_randomEmailAddress(), _randomEmailAddress()
				};
				facebook = RandomTestUtil.randomString();
				jabber = RandomTestUtil.randomString();
				postalAddresses = new PostalAddress[] {
					_randomPostalAddress(), _randomPostalAddress()
				};
				skype = RandomTestUtil.randomString();
				sms = RandomTestUtil.randomString();
				telephones = new Phone[] {_randomPhone(), _randomPhone()};
				twitter = RandomTestUtil.randomString();
				webUrls = new WebUrl[] {_randomWebUrl(), _randomWebUrl()};
			}
		};
	}

	private EmailAddress _randomEmailAddress() {
		return new EmailAddress() {
			{
				emailAddress = RandomTestUtil.randomString() + "@liferay.com";
				type = "email-address";
			}
		};
	}

	private Phone _randomPhone() {
		return new Phone() {
			{
				extension = String.valueOf(RandomTestUtil.randomInt());
				phoneNumber = String.valueOf(RandomTestUtil.randomInt());
				phoneType = "fax";
			}
		};
	}

	private PostalAddress _randomPostalAddress() {
		return new PostalAddress() {
			{
				addressCountry = "United States";
				addressLocality = RandomTestUtil.randomString();
				addressRegion = "California";
				addressType = "other";
				postalCode = String.valueOf(RandomTestUtil.randomInt());
				streetAddressLine1 = RandomTestUtil.randomString();
			}
		};
	}

	private WebUrl _randomWebUrl() {
		return new WebUrl() {
			{
				url = "https://liferay" + StringUtil.randomString() + ".com";
				urlType = "intranet";
			}
		};
	}

	private void _testGetAccountsPage(
			List<AccountEntry> expectedAccountEntries, Long organizationId)
		throws Exception {

		StringBundler sb = new StringBundler();

		for (AccountEntry accountEntry : expectedAccountEntries) {
			if (sb.length() != 0) {
				sb.append(" or ");
			}

			sb.append(
				String.format("contains(name, '%s')", accountEntry.getName()));
		}

		if (organizationId != null) {
			if (sb.length() != 0) {
				sb.append(" and ");
			}

			sb.append(String.format("organizationIds eq '%s'", organizationId));
		}

		Page<Account> accountsPage = accountResource.getAccountsPage(
			null, sb.toString(), null, null);

		Assert.assertEquals(
			expectedAccountEntries.size(), accountsPage.getTotalCount());

		for (Account account : accountsPage.getItems()) {
			expectedAccountEntries.contains(
				_accountEntryLocalService.getAccountEntry(account.getId()));
		}
	}

	private void _testGetAccountsPageWithCustomFields() throws Exception {
		ExpandoTable expandoTable = ExpandoTestUtil.addTable(
			_classNameLocalService.getClassNameId(AccountEntry.class),
			"CUSTOM_FIELDS");

		ExpandoColumn expandoColumn = _addExpandoColumn(
			null, expandoTable, ExpandoColumnConstants.STRING,
			HashMapBuilder.put(
				ExpandoColumnConstants.INDEX_TYPE,
				String.valueOf(ExpandoColumnConstants.INDEX_TYPE_KEYWORD)
			).build());

		double randomDouble = RandomTestUtil.randomDouble();

		ExpandoColumn doubleArrayExpandoColumn1 = _addExpandoColumn(
			new double[] {
				randomDouble, RandomTestUtil.randomDouble(),
				RandomTestUtil.randomDouble()
			},
			expandoTable, ExpandoColumnConstants.DOUBLE_ARRAY,
			HashMapBuilder.put(
				ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE,
				ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE_RADIO
			).build());
		ExpandoColumn doubleArrayExpandoColumn2 = _addExpandoColumn(
			new double[] {
				randomDouble, RandomTestUtil.randomDouble(),
				RandomTestUtil.randomDouble()
			},
			expandoTable, ExpandoColumnConstants.DOUBLE_ARRAY,
			HashMapBuilder.put(
				ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE,
				ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE_SELECTION_LIST
			).build());

		long randomLong = RandomTestUtil.randomLong();

		ExpandoColumn longArrayExpandoColumn1 = _addExpandoColumn(
			new long[] {
				randomLong, RandomTestUtil.randomLong(),
				RandomTestUtil.randomLong()
			},
			expandoTable, ExpandoColumnConstants.LONG_ARRAY,
			HashMapBuilder.put(
				ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE,
				ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE_RADIO
			).build());
		ExpandoColumn longArrayExpandoColumn2 = _addExpandoColumn(
			new long[] {
				randomLong, RandomTestUtil.randomLong(),
				RandomTestUtil.randomLong()
			},
			expandoTable, ExpandoColumnConstants.LONG_ARRAY,
			HashMapBuilder.put(
				ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE,
				ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE_SELECTION_LIST
			).build());

		String randomString = RandomTestUtil.randomString();

		ExpandoColumn stringArrayExpandoColumn1 = _addExpandoColumn(
			new String[] {
				randomString, RandomTestUtil.randomString(),
				RandomTestUtil.randomString()
			},
			expandoTable, ExpandoColumnConstants.STRING_ARRAY,
			HashMapBuilder.put(
				ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE,
				ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE_RADIO
			).build());
		ExpandoColumn stringArrayExpandoColumn2 = _addExpandoColumn(
			new String[] {
				randomString, RandomTestUtil.randomString(),
				RandomTestUtil.randomString()
			},
			expandoTable, ExpandoColumnConstants.STRING_ARRAY,
			HashMapBuilder.put(
				ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE,
				ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE_SELECTION_LIST
			).build());

		Account account = randomAccount();

		String value = RandomTestUtil.randomString();

		account.setCustomFields(
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

		account = testGetAccountsPage_addAccount(account);

		Page<Account> page = accountResource.getAccountsPage(
			null,
			StringBundler.concat(
				"(customFields/", expandoColumn.getName(), " eq '",
				RandomTestUtil.randomString(), "')"),
			Pagination.of(1, 2), null);

		Assert.assertEquals(0, page.getTotalCount());

		page = accountResource.getAccountsPage(
			null,
			StringBundler.concat(
				"(customFields/", expandoColumn.getName(), " eq '", value,
				"')"),
			Pagination.of(1, 2), null);

		Assert.assertEquals(1, page.getTotalCount());

		List<Account> accounts = (List<Account>)page.getItems();

		assertEquals(Collections.singletonList(account), accounts);

		Account actualAccount = accounts.get(0);

		Assert.assertEquals(
			Arrays.toString(new double[] {0.0}),
			Arrays.toString(
				(Object[])_getCustomFieldCustomValueData(
					actualAccount, doubleArrayExpandoColumn1.getName())));
		Assert.assertEquals(
			Arrays.toString(new double[] {randomDouble}),
			Arrays.toString(
				(Object[])_getCustomFieldCustomValueData(
					actualAccount, doubleArrayExpandoColumn2.getName())));
		Assert.assertEquals(
			Arrays.toString(new long[] {0}),
			Arrays.toString(
				(Object[])_getCustomFieldCustomValueData(
					actualAccount, longArrayExpandoColumn1.getName())));
		Assert.assertEquals(
			Arrays.toString(new long[] {randomLong}),
			Arrays.toString(
				(Object[])_getCustomFieldCustomValueData(
					actualAccount, longArrayExpandoColumn2.getName())));
		Assert.assertEquals(
			Arrays.toString(new String[] {"false"}),
			Arrays.toString(
				(Object[])_getCustomFieldCustomValueData(
					actualAccount, stringArrayExpandoColumn1.getName())));
		Assert.assertEquals(
			Arrays.toString(new String[] {randomString}),
			Arrays.toString(
				(Object[])_getCustomFieldCustomValueData(
					actualAccount, stringArrayExpandoColumn2.getName())));
	}

	private void _testGetAccountsPageWithNestedFields() throws Exception {
		Account postAccount = _postAccount(randomAccount());

		User user = UserTestUtil.addUser();

		_accountEntryUserRelLocalService.addAccountEntryUserRel(
			postAccount.getId(), user.getUserId());

		AccountResource accountResource = AccountResource.builder(
		).authentication(
			"test@liferay.com", PropsValues.DEFAULT_ADMIN_PASSWORD
		).locale(
			LocaleUtil.getDefault()
		).parameters(
			"nestedFields", "accountUserAccounts"
		).build();

		Page<Account> accountsPage = accountResource.getAccountsPage(
			null, String.format("contains(name, '%s')", postAccount.getName()),
			null, null);

		Assert.assertEquals(1, accountsPage.getTotalCount());

		List<Account> accounts = (List<Account>)accountsPage.getItems();

		assertEquals(Collections.singletonList(postAccount), accounts);

		Account account = accounts.get(0);

		Assert.assertTrue(
			ArrayUtil.exists(
				account.getAccountUserAccounts(),
				userAccount -> userAccount.getId() == user.getUserId()));
	}

	private void _testGetAccountWithNestedFields() throws Exception {
		Account randomAccount = randomAccount();

		randomAccount.setKeywords(new String[] {RandomTestUtil.randomString()});
		randomAccount.setLogoBase64(
			Base64.encode(
				FileUtil.getBytes(getClass(), "/images/liferay.png")));

		Account postAccount = _postAccount(randomAccount);

		User user = UserTestUtil.addUser();

		_accountEntryUserRelLocalService.addAccountEntryUserRel(
			postAccount.getId(), user.getUserId());

		AccountGroup accountGroup = _accountGroupLocalService.addAccountGroup(
			StringPool.BLANK, TestPropsValues.getUserId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext());

		_accountGroupRelLocalService.addAccountGroupRel(
			accountGroup.getAccountGroupId(), AccountEntry.class.getName(),
			postAccount.getId());

		AccountRole accountRole = _accountRoleLocalService.addAccountRole(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			postAccount.getId(), RandomTestUtil.randomString(), null, null);

		_resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(), AccountEntry.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(postAccount.getId()), accountRole.getRoleId(),
			new String[] {ActionKeys.DELETE});

		AssetEntry assetEntry = _assetEntryLocalService.fetchEntry(
			_classNameLocalService.getClassNameId(AccountEntry.class),
			postAccount.getId());

		AssetVocabulary assetVocabulary = AssetTestUtil.addVocabulary(
			TestPropsValues.getGroupId());

		AssetCategory assetCategory = AssetTestUtil.addCategory(
			TestPropsValues.getGroupId(), assetVocabulary.getVocabularyId());

		_assetEntryAssetCategoryRelLocalService.addAssetEntryAssetCategoryRel(
			assetEntry.getEntryId(), assetCategory.getCategoryId());

		AccountResource accountResource = AccountResource.builder(
		).authentication(
			"test@liferay.com", PropsValues.DEFAULT_ADMIN_PASSWORD
		).locale(
			LocaleUtil.getDefault()
		).parameters(
			"nestedFields",
			"accountGroupBriefs,accountRoles,accountUserAccounts,creator," +
				"keywords,logoBase64,permissions,taxonomyCategoryBriefs"
		).build();

		Account getAccount = accountResource.getAccount(postAccount.getId());

		Assert.assertNotNull(getAccount.getCreator());

		Creator creator = getAccount.getCreator();

		Assert.assertTrue(creator.getId() == TestPropsValues.getUserId());

		Assert.assertTrue(
			ArrayUtil.exists(
				getAccount.getAccountGroupBriefs(),
				accountGroupBrief ->
					Objects.equals(
						accountGroupBrief.getId(),
						accountGroup.getAccountGroupId()) &&
					Objects.equals(
						accountGroupBrief.getName(), accountGroup.getName())));
		Assert.assertTrue(
			ArrayUtil.exists(
				getAccount.getAccountRoles(),
				innerAccountRole ->
					innerAccountRole.getId() == accountRole.getRoleId()));
		Assert.assertTrue(
			ArrayUtil.exists(
				getAccount.getAccountUserAccounts(),
				userAccount -> userAccount.getId() == user.getUserId()));
		Assert.assertTrue(
			ArrayUtil.exists(
				getAccount.getKeywords(),
				keyword -> Objects.equals(
					keyword, randomAccount.getKeywords()[0])));
		Assert.assertNotNull(getAccount.getLogoBase64());
		Assert.assertNotEquals(0, GetterUtil.getLong(getAccount.getLogoId()));

		Role role = accountRole.getRole();

		Assert.assertTrue(
			ArrayUtil.exists(
				getAccount.getPermissions(),
				permission ->
					Objects.equals(permission.getRoleName(), role.getName()) &&
					(permission.getActionIds().length == 1) &&
					Objects.equals(permission.getActionIds()[0], "DELETE")));

		Assert.assertTrue(
			ArrayUtil.exists(
				getAccount.getTaxonomyCategoryBriefs(),
				taxonomyCategoryBrief -> Objects.equals(
					taxonomyCategoryBrief.getTaxonomyCategoryId(),
					assetCategory.getCategoryId())));
	}

	private void _testPatchAccountByExternalReferenceCodeWithMoreExternalReferenceCodes()
		throws Exception {

		Account postAccount =
			testPatchAccountByExternalReferenceCode_addAccount();

		Account randomPatchAccount = randomPatchAccount();

		Organization organization1 = OrganizationTestUtil.addOrganization();

		Address billingAddress = OrganizationTestUtil.addAddress(organization1);

		randomPatchAccount.setDefaultBillingAddressExternalReferenceCode(
			billingAddress.getExternalReferenceCode());

		randomPatchAccount.setDefaultBillingAddressId(0L);

		Organization organization2 = OrganizationTestUtil.addOrganization();

		Address shippingAddress = OrganizationTestUtil.addAddress(
			organization2);

		randomPatchAccount.setDefaultShippingAddressExternalReferenceCode(
			shippingAddress.getExternalReferenceCode());

		randomPatchAccount.setDefaultShippingAddressId(0L);

		FileEntry fileEntry = _addImageFileEntry();

		randomPatchAccount.setLogoExternalReferenceCode(
			fileEntry.getExternalReferenceCode());

		randomPatchAccount.setLogoId(0L);
		randomPatchAccount.setOrganizationExternalReferenceCodes(
			new String[] {
				organization1.getExternalReferenceCode(),
				organization2.getExternalReferenceCode()
			});
		randomPatchAccount.setOrganizationIds(new Long[0]);

		Account parentAccount = randomAccount();

		Account postParentAccount = testPostAccount_addAccount(parentAccount);

		randomPatchAccount.setParentAccountExternalReferenceCode(
			postParentAccount.getExternalReferenceCode());

		randomPatchAccount.setParentAccountId(0L);

		Account patchAccount =
			accountResource.patchAccountByExternalReferenceCode(
				postAccount.getExternalReferenceCode(), randomPatchAccount);

		Arrays.sort(patchAccount.getOrganizationIds());

		Assert.assertArrayEquals(
			new Long[] {
				organization1.getOrganizationId(),
				organization2.getOrganizationId()
			},
			patchAccount.getOrganizationIds());
		Assert.assertEquals(
			billingAddress.getAddressId(),
			GetterUtil.getLong(patchAccount.getDefaultBillingAddressId()));
		Assert.assertEquals(
			shippingAddress.getAddressId(),
			GetterUtil.getLong(patchAccount.getDefaultShippingAddressId()));
		Assert.assertEquals(
			postParentAccount.getId(), patchAccount.getParentAccountId());
		Assert.assertTrue(patchAccount.getLogoId() > 0);
	}

	private void _testPatchAccountByExternalReferenceCodeWithoutName()
		throws Exception {

		Account postAccount =
			testPatchAccountByExternalReferenceCode_addAccount();

		Account randomPatchAccount = randomPatchAccount();

		randomPatchAccount.setName(() -> null);

		Account patchAccount =
			accountResource.patchAccountByExternalReferenceCode(
				postAccount.getExternalReferenceCode(), randomPatchAccount);

		Assert.assertEquals(postAccount.getName(), patchAccount.getName());
	}

	private void _testPatchAccountWithContactInformation() throws Exception {
		Account postAccount = testPatchAccount_addAccount();

		Account randomPatchAccount = randomPatchAccount();

		AccountContactInformation accountContactInformation =
			_randomAccountContactInformation();

		randomPatchAccount.setAccountContactInformation(
			accountContactInformation);

		Account patchAccount = accountResource.patchAccount(
			postAccount.getId(), randomPatchAccount);

		_assertEquals(
			accountContactInformation,
			patchAccount.getAccountContactInformation());
	}

	private void _testPatchAccountWithEmptyOrganizationExternalReferenceCodes()
		throws Exception {

		Account randomAccount = randomAccount();

		Organization organization1 = OrganizationTestUtil.addOrganization();
		Organization organization2 = OrganizationTestUtil.addOrganization();

		String[] organizationExternalReferenceCodes = {
			organization1.getExternalReferenceCode(),
			organization2.getExternalReferenceCode()
		};

		Arrays.sort(organizationExternalReferenceCodes);

		randomAccount.setOrganizationExternalReferenceCodes(
			organizationExternalReferenceCodes);

		Account postAccount = _postAccount(randomAccount);

		Arrays.sort(postAccount.getOrganizationExternalReferenceCodes());

		Assert.assertArrayEquals(
			organizationExternalReferenceCodes,
			postAccount.getOrganizationExternalReferenceCodes());

		postAccount.setOrganizationExternalReferenceCodes(() -> null);
		postAccount.setOrganizationIds(() -> null);

		Account patchAccount = accountResource.patchAccount(
			postAccount.getId(), postAccount);

		Arrays.sort(patchAccount.getOrganizationExternalReferenceCodes());

		Assert.assertArrayEquals(
			organizationExternalReferenceCodes,
			patchAccount.getOrganizationExternalReferenceCodes());

		postAccount.setOrganizationExternalReferenceCodes(new String[0]);
		postAccount.setOrganizationIds(new Long[0]);

		patchAccount = accountResource.patchAccount(
			postAccount.getId(), postAccount);

		Assert.assertArrayEquals(
			new String[0],
			patchAccount.getOrganizationExternalReferenceCodes());
	}

	private void _testPatchAccountWithEmptyOrganizationIds() throws Exception {
		Account randomAccount = randomAccount();

		Organization organization1 = OrganizationTestUtil.addOrganization();
		Organization organization2 = OrganizationTestUtil.addOrganization();

		Long[] organizationIds = {
			organization1.getOrganizationId(), organization2.getOrganizationId()
		};

		Arrays.sort(organizationIds);

		randomAccount.setOrganizationIds(organizationIds);

		Account postAccount = _postAccount(randomAccount);

		Arrays.sort(postAccount.getOrganizationIds());

		Assert.assertArrayEquals(
			organizationIds, postAccount.getOrganizationIds());

		postAccount.setOrganizationExternalReferenceCodes(() -> null);
		postAccount.setOrganizationIds(() -> null);

		Account patchAccount = accountResource.patchAccount(
			postAccount.getId(), postAccount);

		Arrays.sort(patchAccount.getOrganizationIds());

		Assert.assertArrayEquals(
			organizationIds, patchAccount.getOrganizationIds());

		postAccount.setOrganizationExternalReferenceCodes(new String[0]);
		postAccount.setOrganizationIds(new Long[0]);

		patchAccount = accountResource.patchAccount(
			postAccount.getId(), postAccount);

		Assert.assertArrayEquals(
			new Long[0], patchAccount.getOrganizationIds());
	}

	private void _testPatchAccountWithMoreExternalReferenceCodes()
		throws Exception {

		Account postAccount = testPatchAccount_addAccount();

		Account randomPatchAccount = randomPatchAccount();

		Organization organization1 = OrganizationTestUtil.addOrganization();

		Address billingAddress = OrganizationTestUtil.addAddress(organization1);

		randomPatchAccount.setDefaultBillingAddressExternalReferenceCode(
			billingAddress.getExternalReferenceCode());

		randomPatchAccount.setDefaultBillingAddressId(0L);

		Organization organization2 = OrganizationTestUtil.addOrganization();

		Address shippingAddress = OrganizationTestUtil.addAddress(
			organization2);

		randomPatchAccount.setDefaultShippingAddressExternalReferenceCode(
			shippingAddress.getExternalReferenceCode());

		randomPatchAccount.setDefaultShippingAddressId(0L);

		FileEntry fileEntry = _addImageFileEntry();

		randomPatchAccount.setLogoExternalReferenceCode(
			fileEntry.getExternalReferenceCode());

		randomPatchAccount.setLogoId(0L);
		randomPatchAccount.setOrganizationExternalReferenceCodes(
			new String[] {
				organization1.getExternalReferenceCode(),
				organization2.getExternalReferenceCode()
			});
		randomPatchAccount.setOrganizationIds(new Long[0]);

		Account parentAccount = randomAccount();

		Account postParentAccount = testPostAccount_addAccount(parentAccount);

		randomPatchAccount.setParentAccountExternalReferenceCode(
			postParentAccount.getExternalReferenceCode());

		randomPatchAccount.setParentAccountId(0L);

		Account patchAccount = accountResource.patchAccount(
			postAccount.getId(), randomPatchAccount);

		Arrays.sort(patchAccount.getOrganizationIds());

		Assert.assertArrayEquals(
			new Long[] {
				organization1.getOrganizationId(),
				organization2.getOrganizationId()
			},
			patchAccount.getOrganizationIds());
		Assert.assertEquals(
			billingAddress.getAddressId(),
			GetterUtil.getLong(patchAccount.getDefaultBillingAddressId()));
		Assert.assertEquals(
			shippingAddress.getAddressId(),
			GetterUtil.getLong(patchAccount.getDefaultShippingAddressId()));
		Assert.assertEquals(
			postParentAccount.getId(), patchAccount.getParentAccountId());
		Assert.assertTrue(patchAccount.getLogoId() > 0);
	}

	private void _testPatchAccountWithoutName() throws Exception {
		Account postAccount = testPatchAccount_addAccount();

		Account randomPatchAccount = randomPatchAccount();

		randomPatchAccount.setName(() -> null);

		Account patchAccount = accountResource.patchAccount(
			postAccount.getId(), randomPatchAccount);

		Assert.assertEquals(postAccount.getName(), patchAccount.getName());
	}

	private void _testPatchAccountWithPostalAddressPhoneNumber()
		throws Exception {

		Account postAccount = testPatchAccount_addAccount();

		Account randomPatchAccount = randomPatchAccount();

		PostalAddress postalAddress = _randomPostalAddress();

		postalAddress.setPhoneNumber(RandomTestUtil.randomString());

		randomPatchAccount.setPostalAddresses(
			new PostalAddress[] {postalAddress});

		Account patchAccount = accountResource.patchAccount(
			postAccount.getId(), randomPatchAccount);

		List<Address> addresses = _addressLocalService.getAddresses(
			TestPropsValues.getCompanyId(), AccountEntry.class.getName(),
			patchAccount.getId());

		Address address = addresses.get(0);

		Assert.assertEquals(
			postalAddress.getPhoneNumber(), address.getPhoneNumber());
	}

	private void _testPostAccountBatch() throws Exception {
		Account account = randomAccount();

		AccountGroup accountGroup1 = _accountGroupLocalService.addAccountGroup(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			new ServiceContext());

		AccountGroupBrief accountGroupBrief1 = new AccountGroupBrief() {
			{
				externalReferenceCode =
					accountGroup1.getExternalReferenceCode();
				name = accountGroup1.getName();
			}
		};
		AccountGroupBrief accountGroupBrief2 = new AccountGroupBrief() {
			{
				externalReferenceCode = RandomTestUtil.randomString();
			}
		};

		account.setAccountGroupBriefs(
			new AccountGroupBrief[] {accountGroupBrief1, accountGroupBrief2});

		com.liferay.headless.admin.user.client.dto.v1_0.AccountRole
			accountRole =
				new com.liferay.headless.admin.user.client.dto.v1_0.
					AccountRole() {

					{
						externalReferenceCode = RandomTestUtil.randomString();
						name = RandomTestUtil.randomString();
					}
				};

		account.setAccountRoles(
			new com.liferay.headless.admin.user.client.dto.v1_0.AccountRole[] {
				accountRole
			});

		account.setDefaultBillingAddressExternalReferenceCode(
			RandomTestUtil.randomString());
		account.setDefaultShippingAddressExternalReferenceCode(
			RandomTestUtil.randomString());
		account.setLogoBase64(
			Base64.encode(
				FileUtil.getBytes(getClass(), "/images/liferay.png")));

		Organization organization1 = _organizationLocalService.addOrganization(
			TestPropsValues.getUserId(), 0, RandomTestUtil.randomString(),
			false);

		account.setOrganizationExternalReferenceCodes(
			new String[] {
				organization1.getExternalReferenceCode(),
				RandomTestUtil.randomString()
			});

		account.setParentAccountExternalReferenceCode(
			RandomTestUtil.randomString());

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

		account.setPermissions(new Permission[] {permission1, permission2});

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

		account.setTaxonomyCategoryBriefs(
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
						_jsonFactory.createJSONObject(account.toString()))
				).toString(),
				"headless-admin-user/v1.0/accounts/batch", Http.Method.POST));

		AccountGroup accountGroup2 =
			_accountGroupLocalService.fetchAccountGroupByExternalReferenceCode(
				accountGroupBrief1.getExternalReferenceCode(),
				TestPropsValues.getCompanyId());

		Assert.assertEquals(
			accountGroup1.getAccountGroupId(),
			accountGroup2.getAccountGroupId());

		AccountEntry accountEntry =
			_accountEntryLocalService.fetchAccountEntryByExternalReferenceCode(
				account.getExternalReferenceCode(),
				TestPropsValues.getCompanyId());

		List<AccountGroupRel> accountGroupRels =
			_accountGroupRelLocalService.getAccountGroupRels(
				AccountEntry.class.getName(), accountEntry.getAccountEntryId());

		Assert.assertTrue(
			ListUtil.exists(
				accountGroupRels,
				accountGroupRel ->
					accountGroupRel.getAccountGroupId() ==
						accountGroup2.getAccountGroupId()));

		AccountGroup accountGroup3 =
			_accountGroupLocalService.fetchAccountGroupByExternalReferenceCode(
				accountGroupBrief2.getExternalReferenceCode(),
				TestPropsValues.getCompanyId());

		Assert.assertTrue(
			ListUtil.exists(
				accountGroupRels,
				accountGroupRel ->
					accountGroupRel.getAccountGroupId() ==
						accountGroup3.getAccountGroupId()));
		Assert.assertEquals(
			WorkflowConstants.STATUS_INCOMPLETE, accountGroup3.getStatus());

		AccountRole serviceBuilderAccountRole =
			_accountRoleLocalService.fetchAccountRoleByExternalReferenceCode(
				accountRole.getExternalReferenceCode(),
				TestPropsValues.getCompanyId());

		Assert.assertEquals(
			accountRole.getName(), serviceBuilderAccountRole.getRoleName());

		Address address1 =
			_addressLocalService.fetchAddressByExternalReferenceCode(
				account.getDefaultBillingAddressExternalReferenceCode(),
				TestPropsValues.getCompanyId());

		Assert.assertEquals(
			accountEntry.getDefaultBillingAddressId(), address1.getAddressId());
		Assert.assertEquals(
			WorkflowConstants.STATUS_INCOMPLETE, address1.getStatus());

		Address address2 =
			_addressLocalService.fetchAddressByExternalReferenceCode(
				account.getDefaultShippingAddressExternalReferenceCode(),
				TestPropsValues.getCompanyId());

		Assert.assertEquals(
			accountEntry.getDefaultShippingAddressId(),
			address2.getAddressId());
		Assert.assertEquals(
			WorkflowConstants.STATUS_INCOMPLETE, address2.getStatus());

		Assert.assertNotEquals(0, accountEntry.getLogoId());

		Organization organization2 =
			_organizationLocalService.fetchOrganizationByExternalReferenceCode(
				account.getOrganizationExternalReferenceCodes()[0],
				TestPropsValues.getCompanyId());

		Assert.assertEquals(
			organization1.getOrganizationId(),
			organization2.getOrganizationId());

		List<AccountEntryOrganizationRel> accountEntryOrganizationRels =
			_accountEntryOrganizationRelLocalService.
				getAccountEntryOrganizationRels(
					accountEntry.getAccountEntryId());

		Assert.assertTrue(
			ListUtil.exists(
				accountEntryOrganizationRels,
				accountEntryOrganizationRel ->
					accountEntryOrganizationRel.getOrganizationId() ==
						organization2.getOrganizationId()));

		Organization organization3 =
			_organizationLocalService.fetchOrganizationByExternalReferenceCode(
				account.getOrganizationExternalReferenceCodes()[1],
				TestPropsValues.getCompanyId());

		Assert.assertTrue(
			ListUtil.exists(
				accountEntryOrganizationRels,
				accountEntryOrganizationRel ->
					accountEntryOrganizationRel.getOrganizationId() ==
						organization3.getOrganizationId()));
		Assert.assertEquals(
			WorkflowConstants.STATUS_INCOMPLETE, organization3.getStatus());

		AccountEntry parentAccountEntry =
			_accountEntryLocalService.fetchAccountEntryByExternalReferenceCode(
				account.getParentAccountExternalReferenceCode(),
				TestPropsValues.getCompanyId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_INCOMPLETE,
			parentAccountEntry.getStatus());

		Role serviceBuilderRole2 =
			_roleLocalService.fetchRoleByExternalReferenceCode(
				permission1.getRoleExternalReferenceCode(),
				TestPropsValues.getCompanyId());

		List<com.liferay.portal.vulcan.permission.Permission> permissions =
			ListUtil.fromCollection(
				PermissionUtil.getPermissions(
					TestPropsValues.getCompanyId(),
					_resourceActionLocalService.getResourceActions(
						AccountEntry.class.getName()),
					accountEntry.getAccountEntryId(),
					AccountEntry.class.getName(), null));

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
			WorkflowConstants.STATUS_INCOMPLETE,
			serviceBuilderRole3.getStatus());

		AssetCategory assetCategory2 =
			_assetCategoryLocalService.
				fetchAssetCategoryByExternalReferenceCode(
					taxonomyCategoryReference1.getExternalReferenceCode(),
					group.getGroupId());

		Assert.assertEquals(
			assetCategory1.getCategoryId(), assetCategory2.getCategoryId());

		List<AssetCategory> assetCategories =
			_assetCategoryLocalService.getCategories(
				AccountEntry.class.getName(), accountEntry.getAccountEntryId());

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
			WorkflowConstants.STATUS_INCOMPLETE, assetCategory3.getStatus());
	}

	private void _testPostAccountDuplicateExternalReferenceCode()
		throws Exception {

		Account account1 = randomAccount();

		Account postAccount = accountResource.postAccount(account1);

		Assert.assertEquals(
			account1.getExternalReferenceCode(),
			postAccount.getExternalReferenceCode());

		try {
			Account account2 = randomAccount();

			account2.setExternalReferenceCode(
				postAccount.getExternalReferenceCode());

			_postAccount(account2);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals(
				problem.getTitle(),
				"An account already exists with the same external reference " +
					"code");
		}
	}

	private void _testPostAccountWithContactInformation() throws Exception {
		Account account = randomAccount();

		AccountContactInformation accountContactInformation =
			_randomAccountContactInformation();

		account.setAccountContactInformation(accountContactInformation);

		Account postAccount = accountResource.postAccount(account);

		_assertEquals(
			accountContactInformation,
			postAccount.getAccountContactInformation());
	}

	private void _testPostAccountWithMoreExternalReferenceCodes()
		throws Exception {

		Account randomAccount = randomAccount();

		Organization organization1 = OrganizationTestUtil.addOrganization();

		Address billingAddress = OrganizationTestUtil.addAddress(organization1);

		randomAccount.setDefaultBillingAddressExternalReferenceCode(
			billingAddress.getExternalReferenceCode());

		randomAccount.setDefaultBillingAddressId(0L);

		Organization organization2 = OrganizationTestUtil.addOrganization();

		Address shippingAddress = OrganizationTestUtil.addAddress(
			organization2);

		randomAccount.setDefaultShippingAddressExternalReferenceCode(
			shippingAddress.getExternalReferenceCode());

		randomAccount.setDefaultShippingAddressId(0L);

		FileEntry fileEntry = _addImageFileEntry();

		randomAccount.setLogoExternalReferenceCode(
			fileEntry.getExternalReferenceCode());

		randomAccount.setLogoId(0L);
		randomAccount.setOrganizationExternalReferenceCodes(
			new String[] {
				organization1.getExternalReferenceCode(),
				organization2.getExternalReferenceCode()
			});
		randomAccount.setOrganizationIds(new Long[0]);

		Account parentAccount = randomAccount();

		Account postParentAccount = testPostAccount_addAccount(parentAccount);

		randomAccount.setParentAccountExternalReferenceCode(
			postParentAccount.getExternalReferenceCode());

		randomAccount.setParentAccountId(0L);

		Account postAccount = testPostAccount_addAccount(randomAccount);

		Arrays.sort(postAccount.getOrganizationIds());

		Assert.assertArrayEquals(
			new Long[] {
				Long.valueOf(organization1.getOrganizationId()),
				Long.valueOf(organization2.getOrganizationId())
			},
			postAccount.getOrganizationIds());
		Assert.assertEquals(
			Long.valueOf(billingAddress.getAddressId()),
			postAccount.getDefaultBillingAddressId());
		Assert.assertEquals(
			Long.valueOf(shippingAddress.getAddressId()),
			postAccount.getDefaultShippingAddressId());
		Assert.assertEquals(
			postParentAccount.getId(), postAccount.getParentAccountId());
		Assert.assertTrue(postAccount.getLogoId() > 0);
	}

	private void _testPostAccountWithPostalAddressPhoneNumber()
		throws Exception {

		Account account = randomAccount();

		PostalAddress postalAddress = _randomPostalAddress();

		postalAddress.setPhoneNumber(RandomTestUtil.randomString());

		account.setPostalAddresses(new PostalAddress[] {postalAddress});

		Account postAccount = accountResource.postAccount(account);

		List<Address> addresses = _addressLocalService.getAddresses(
			TestPropsValues.getCompanyId(), AccountEntry.class.getName(),
			postAccount.getId());

		Address address = addresses.get(0);

		Assert.assertEquals(
			postalAddress.getPhoneNumber(), address.getPhoneNumber());
	}

	private void _testPutAccountByExternalReferenceCodeWithContactInformation()
		throws Exception {

		Account postAccount =
			testPutAccountByExternalReferenceCode_addAccount();

		Account randomAccount = randomAccount();

		AccountContactInformation accountContactInformation =
			_randomAccountContactInformation();

		randomAccount.setAccountContactInformation(accountContactInformation);

		Account putAccount = accountResource.putAccountByExternalReferenceCode(
			postAccount.getExternalReferenceCode(), randomAccount);

		_assertEquals(
			accountContactInformation,
			putAccount.getAccountContactInformation());
	}

	private void _testPutAccountByExternalReferenceCodeWithMoreExternalReferenceCodes()
		throws Exception {

		Account postAccount =
			testPutAccountByExternalReferenceCode_addAccount();

		Account randomPutAccount = randomAccount();

		Organization organization1 = OrganizationTestUtil.addOrganization();

		Address billingAddress = OrganizationTestUtil.addAddress(organization1);

		randomPutAccount.setDefaultBillingAddressExternalReferenceCode(
			billingAddress.getExternalReferenceCode());

		randomPutAccount.setDefaultBillingAddressId(0L);

		Organization organization2 = OrganizationTestUtil.addOrganization();

		Address shippingAddress = OrganizationTestUtil.addAddress(
			organization2);

		randomPutAccount.setDefaultShippingAddressExternalReferenceCode(
			shippingAddress.getExternalReferenceCode());

		randomPutAccount.setDefaultShippingAddressId(0L);

		FileEntry fileEntry = _addImageFileEntry();

		randomPutAccount.setLogoExternalReferenceCode(
			fileEntry.getExternalReferenceCode());

		randomPutAccount.setLogoId(0L);
		randomPutAccount.setOrganizationExternalReferenceCodes(
			new String[] {
				organization1.getExternalReferenceCode(),
				organization2.getExternalReferenceCode()
			});
		randomPutAccount.setOrganizationIds(new Long[0]);

		Account parentAccount = randomAccount();

		Account postParentAccount = testPostAccount_addAccount(parentAccount);

		randomPutAccount.setParentAccountExternalReferenceCode(
			postParentAccount.getExternalReferenceCode());

		randomPutAccount.setParentAccountId(0L);

		Account putAccount = accountResource.putAccountByExternalReferenceCode(
			postAccount.getExternalReferenceCode(), randomPutAccount);

		Arrays.sort(putAccount.getOrganizationIds());

		Assert.assertArrayEquals(
			new Long[] {
				organization1.getOrganizationId(),
				organization2.getOrganizationId()
			},
			putAccount.getOrganizationIds());
		Assert.assertEquals(
			billingAddress.getAddressId(),
			GetterUtil.getLong(putAccount.getDefaultBillingAddressId()));
		Assert.assertEquals(
			shippingAddress.getAddressId(),
			GetterUtil.getLong(putAccount.getDefaultShippingAddressId()));
		Assert.assertEquals(
			postParentAccount.getId(), putAccount.getParentAccountId());
		Assert.assertTrue(putAccount.getLogoId() > 0);
	}

	private void _testPutAccountByExternalReferenceCodeWithoutName()
		throws Exception {

		Account postAccount =
			testPutAccountByExternalReferenceCode_addAccount();

		Account randomAccount = randomAccount();

		randomAccount.setName(() -> null);

		try {
			accountResource.putAccountByExternalReferenceCode(
				postAccount.getExternalReferenceCode(), randomAccount);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			String errorMessage = problem.getTitle();

			Assert.assertTrue(
				errorMessage.contains("The account name is invalid"));
		}
	}

	private void _testPutAccountWithContactInformation() throws Exception {
		Account postAccount = testPutAccount_addAccount();

		Account randomAccount = randomAccount();

		AccountContactInformation accountContactInformation =
			_randomAccountContactInformation();

		randomAccount.setAccountContactInformation(accountContactInformation);

		Account putAccount = accountResource.putAccount(
			postAccount.getId(), randomAccount);

		_assertEquals(
			accountContactInformation,
			putAccount.getAccountContactInformation());
	}

	private void _testPutAccountWithEmptyOrganizationExternalReferenceCodes()
		throws Exception {

		Account randomAccount = randomAccount();

		Organization organization1 = OrganizationTestUtil.addOrganization();
		Organization organization2 = OrganizationTestUtil.addOrganization();

		String[] organizationExternalReferenceCodes = {
			organization1.getExternalReferenceCode(),
			organization2.getExternalReferenceCode()
		};

		Arrays.sort(organizationExternalReferenceCodes);

		randomAccount.setOrganizationExternalReferenceCodes(
			organizationExternalReferenceCodes);

		Account postAccount = _postAccount(randomAccount);

		Arrays.sort(postAccount.getOrganizationExternalReferenceCodes());

		Assert.assertArrayEquals(
			organizationExternalReferenceCodes,
			postAccount.getOrganizationExternalReferenceCodes());

		postAccount.setOrganizationExternalReferenceCodes(new String[0]);
		postAccount.setOrganizationIds(new Long[0]);

		Account putAccount = accountResource.putAccount(
			postAccount.getId(), postAccount);

		Assert.assertArrayEquals(
			new String[0], putAccount.getOrganizationExternalReferenceCodes());
	}

	private void _testPutAccountWithEmptyOrganizationIds() throws Exception {
		Account randomAccount = randomAccount();

		Organization organization1 = OrganizationTestUtil.addOrganization();
		Organization organization2 = OrganizationTestUtil.addOrganization();

		Long[] organizationIds = {
			organization1.getOrganizationId(), organization2.getOrganizationId()
		};

		Arrays.sort(organizationIds);

		randomAccount.setOrganizationIds(organizationIds);

		Account postAccount = _postAccount(randomAccount);

		Arrays.sort(postAccount.getOrganizationIds());

		Assert.assertArrayEquals(
			organizationIds, postAccount.getOrganizationIds());

		postAccount.setOrganizationExternalReferenceCodes(new String[0]);
		postAccount.setOrganizationIds(new Long[0]);

		Account putAccount = accountResource.putAccount(
			postAccount.getId(), postAccount);

		Assert.assertArrayEquals(new Long[0], putAccount.getOrganizationIds());
	}

	private void _testPutAccountWithMoreExternalReferenceCodes()
		throws Exception {

		Account postAccount = testPutAccount_addAccount();

		Account randomPutAccount = randomAccount();

		Organization organization1 = OrganizationTestUtil.addOrganization();

		Address billingAddress = OrganizationTestUtil.addAddress(organization1);

		randomPutAccount.setDefaultBillingAddressExternalReferenceCode(
			billingAddress.getExternalReferenceCode());

		randomPutAccount.setDefaultBillingAddressId(0L);

		Organization organization2 = OrganizationTestUtil.addOrganization();

		Address shippingAddress = OrganizationTestUtil.addAddress(
			organization2);

		randomPutAccount.setDefaultShippingAddressExternalReferenceCode(
			shippingAddress.getExternalReferenceCode());

		randomPutAccount.setDefaultShippingAddressId(0L);

		FileEntry fileEntry = _addImageFileEntry();

		randomPutAccount.setLogoExternalReferenceCode(
			fileEntry.getExternalReferenceCode());

		randomPutAccount.setLogoId(0L);
		randomPutAccount.setOrganizationExternalReferenceCodes(
			new String[] {
				organization1.getExternalReferenceCode(),
				organization2.getExternalReferenceCode()
			});
		randomPutAccount.setOrganizationIds(new Long[0]);

		Account parentAccount = randomAccount();

		Account postParentAccount = testPostAccount_addAccount(parentAccount);

		randomPutAccount.setParentAccountExternalReferenceCode(
			postParentAccount.getExternalReferenceCode());

		randomPutAccount.setParentAccountId(0L);

		Account putAccount = accountResource.putAccount(
			postAccount.getId(), randomPutAccount);

		Arrays.sort(putAccount.getOrganizationIds());

		Assert.assertArrayEquals(
			new Long[] {
				organization1.getOrganizationId(),
				organization2.getOrganizationId()
			},
			putAccount.getOrganizationIds());
		Assert.assertEquals(
			billingAddress.getAddressId(),
			GetterUtil.getLong(putAccount.getDefaultBillingAddressId()));
		Assert.assertEquals(
			shippingAddress.getAddressId(),
			GetterUtil.getLong(putAccount.getDefaultShippingAddressId()));
		Assert.assertEquals(
			postParentAccount.getId(), putAccount.getParentAccountId());
		Assert.assertTrue(putAccount.getLogoId() > 0);
	}

	private void _testPutAccountWithoutName() throws Exception {
		Account postAccount = testPutAccount_addAccount();

		Account randomAccount = randomAccount();

		randomAccount.setName(() -> null);

		try {
			accountResource.putAccount(postAccount.getId(), randomAccount);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			String errorMessage = problem.getTitle();

			Assert.assertTrue(
				errorMessage.contains("The account name is invalid"));
		}
	}

	private void _testPutAccountWithPostalAddressPhoneNumber()
		throws Exception {

		Account postAccount = testPutAccount_addAccount();

		Account randomAccount = randomAccount();

		PostalAddress postalAddress = _randomPostalAddress();

		postalAddress.setPhoneNumber(RandomTestUtil.randomString());

		randomAccount.setPostalAddresses(new PostalAddress[] {postalAddress});

		Account putAccount = accountResource.putAccount(
			postAccount.getId(), randomAccount);

		List<Address> addresses = _addressLocalService.getAddresses(
			TestPropsValues.getCompanyId(), AccountEntry.class.getName(),
			putAccount.getId());

		Address address = addresses.get(0);

		Assert.assertEquals(
			postalAddress.getPhoneNumber(), address.getPhoneNumber());
	}

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	@Inject
	private AccountEntryOrganizationRelLocalService
		_accountEntryOrganizationRelLocalService;

	@Inject
	private AccountEntryUserRelLocalService _accountEntryUserRelLocalService;

	private AccountGroup _accountGroup;

	@Inject
	private AccountGroupLocalService _accountGroupLocalService;

	@Inject
	private AccountGroupRelLocalService _accountGroupRelLocalService;

	@Inject
	private AccountRoleLocalService _accountRoleLocalService;

	@Inject
	private AddressLocalService _addressLocalService;

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
	private ClassNameLocalService _classNameLocalService;

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

}