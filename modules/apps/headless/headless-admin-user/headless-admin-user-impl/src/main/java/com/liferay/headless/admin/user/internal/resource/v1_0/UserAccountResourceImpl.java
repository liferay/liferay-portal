/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.internal.resource.v1_0;

import com.liferay.account.constants.AccountWebKeys;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountEntryUserRel;
import com.liferay.account.service.AccountEntryService;
import com.liferay.account.service.AccountEntryUserRelLocalService;
import com.liferay.account.service.AccountEntryUserRelService;
import com.liferay.account.service.AccountRoleLocalService;
import com.liferay.announcements.kernel.service.AnnouncementsDeliveryLocalService;
import com.liferay.captcha.util.CaptchaUtil;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.expando.kernel.service.ExpandoColumnLocalService;
import com.liferay.expando.kernel.service.ExpandoTableLocalService;
import com.liferay.headless.admin.user.dto.v1_0.Account;
import com.liferay.headless.admin.user.dto.v1_0.AccountBrief;
import com.liferay.headless.admin.user.dto.v1_0.EmailAddress;
import com.liferay.headless.admin.user.dto.v1_0.OrganizationBrief;
import com.liferay.headless.admin.user.dto.v1_0.Phone;
import com.liferay.headless.admin.user.dto.v1_0.PostalAddress;
import com.liferay.headless.admin.user.dto.v1_0.RoleBrief;
import com.liferay.headless.admin.user.dto.v1_0.UserAccount;
import com.liferay.headless.admin.user.dto.v1_0.UserAccountContactInformation;
import com.liferay.headless.admin.user.dto.v1_0.WebUrl;
import com.liferay.headless.admin.user.internal.dto.v1_0.converter.constants.DTOConverterConstants;
import com.liferay.headless.admin.user.internal.dto.v1_0.util.ServiceBuilderAddressUtil;
import com.liferay.headless.admin.user.internal.dto.v1_0.util.ServiceBuilderEmailAddressUtil;
import com.liferay.headless.admin.user.internal.dto.v1_0.util.ServiceBuilderListTypeUtil;
import com.liferay.headless.admin.user.internal.dto.v1_0.util.ServiceBuilderPhoneUtil;
import com.liferay.headless.admin.user.internal.dto.v1_0.util.ServiceBuilderWebsiteUtil;
import com.liferay.headless.admin.user.internal.odata.entity.v1_0.UserAccountEntityModel;
import com.liferay.headless.admin.user.resource.v1_0.AccountRoleResource;
import com.liferay.headless.admin.user.resource.v1_0.UserAccountResource;
import com.liferay.headless.common.spi.odata.entity.EntityFieldsUtil;
import com.liferay.headless.common.spi.service.context.ServiceContextBuilder;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.captcha.CaptchaSettings;
import com.liferay.portal.kernel.cookies.CookiesManagerUtil;
import com.liferay.portal.kernel.cookies.constants.CookiesConstants;
import com.liferay.portal.kernel.exception.UserLockoutException;
import com.liferay.portal.kernel.exception.UserPasswordException;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.Contact;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ListTypeConstants;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.PasswordPolicy;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.model.Website;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactory;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.search.filter.TermFilter;
import com.liferay.portal.kernel.security.auth.Authenticator;
import com.liferay.portal.kernel.security.ldap.LDAPSettingsUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.CompanyService;
import com.liferay.portal.kernel.service.ContactLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.GroupService;
import com.liferay.portal.kernel.service.OrganizationService;
import com.liferay.portal.kernel.service.PortalPreferencesLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserGroupService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.UserService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.File;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TextFormatter;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.search.expando.ExpandoBridgeIndexer;
import com.liferay.portal.security.auth.session.AuthenticatedSessionManagerUtil;
import com.liferay.portal.vulcan.custom.field.CustomFieldsUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.dto.converter.util.DTOConverterUtil;
import com.liferay.portal.vulcan.fields.NestedField;
import com.liferay.portal.vulcan.fields.NestedFieldId;
import com.liferay.portal.vulcan.multipart.MultipartBody;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.SearchUtil;
import com.liferay.portlet.usersadmin.util.UsersAdminUtil;
import com.liferay.user.associated.data.anonymizer.UADAnonymousUserProvider;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Javier Gamarra
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/user-account.properties",
	property = "nested.field.support=true", scope = ServiceScope.PROTOTYPE,
	service = UserAccountResource.class
)
public class UserAccountResourceImpl extends BaseUserAccountResourceImpl {

	@Override
	public void
			deleteAccountByExternalReferenceCodeUserAccountByExternalReferenceCode(
				String accountExternalReferenceCode,
				String externalReferenceCode)
		throws Exception {

		User user = _userLocalService.getUserByExternalReferenceCode(
			externalReferenceCode, contextCompany.getCompanyId());

		_accountEntryUserRelService.deleteAccountEntryUserRelByEmailAddress(
			DTOConverterUtil.getModelPrimaryKey(
				_accountResourceDTOConverter, accountExternalReferenceCode),
			user.getEmailAddress());
	}

	@Override
	public void deleteAccountUserAccount(Long accountId, Long userAccountId)
		throws Exception {

		User user = _userLocalService.getUserById(
			contextCompany.getCompanyId(), userAccountId);

		deleteAccountUserAccountByEmailAddress(
			accountId, user.getEmailAddress());
	}

	@Override
	public void deleteAccountUserAccountByEmailAddress(
			Long accountId, String emailAddress)
		throws Exception {

		_accountEntryUserRelService.deleteAccountEntryUserRelByEmailAddress(
			accountId, emailAddress);
	}

	@Override
	public void deleteAccountUserAccountByExternalReferenceCodeByEmailAddress(
			String externalReferenceCode, String emailAddress)
		throws Exception {

		deleteAccountUserAccountByEmailAddress(
			DTOConverterUtil.getModelPrimaryKey(
				_accountResourceDTOConverter, externalReferenceCode),
			emailAddress);
	}

	@Override
	public void deleteAccountUserAccountsByEmailAddress(
			Long accountId, String[] emailAddresses)
		throws Exception {

		for (String emailAddress : emailAddresses) {
			deleteAccountUserAccountByEmailAddress(accountId, emailAddress);
		}
	}

	@Override
	public void deleteAccountUserAccountsByExternalReferenceCodeByEmailAddress(
			String externalReferenceCode, String[] emailAddresses)
		throws Exception {

		for (String emailAddress : emailAddresses) {
			deleteAccountUserAccountByExternalReferenceCodeByEmailAddress(
				externalReferenceCode, emailAddress);
		}
	}

	@Override
	public void deleteUserAccount(Long userAccountId) throws Exception {
		_userService.deleteUser(userAccountId);
	}

	@Override
	public void deleteUserAccountByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		deleteUserAccount(
			DTOConverterUtil.getModelPrimaryKey(
				_userResourceDTOConverter, externalReferenceCode));
	}

	@Override
	public UserAccount
			getAccountByExternalReferenceCodeUserAccountByExternalReferenceCode(
				String accountExternalReferenceCode,
				String externalReferenceCode)
		throws Exception {

		AccountEntryUserRel accountEntryUserRel =
			_accountEntryUserRelLocalService.getAccountEntryUserRel(
				DTOConverterUtil.getModelPrimaryKey(
					_accountResourceDTOConverter, accountExternalReferenceCode),
				DTOConverterUtil.getModelPrimaryKey(
					_userResourceDTOConverter, externalReferenceCode));

		return _toUserAccount(
			_userService.getUserById(accountEntryUserRel.getAccountUserId()));
	}

	@Override
	public UserAccount getAccountUserAccount(Long accountId, Long userAccountId)
		throws Exception {

		AccountEntryUserRel accountEntryUserRel =
			_accountEntryUserRelLocalService.getAccountEntryUserRel(
				accountId, userAccountId);

		return _toUserAccount(
			_userService.getUserById(accountEntryUserRel.getAccountUserId()));
	}

	@Override
	public Page<UserAccount> getAccountUserAccountsByExternalReferenceCodePage(
			String externalReferenceCode, String search, Filter filter,
			Pagination pagination, Sort[] sorts)
		throws Exception {

		return getAccountUserAccountsPage(
			DTOConverterUtil.getModelPrimaryKey(
				_accountResourceDTOConverter, externalReferenceCode),
			search, filter, pagination, sorts);
	}

	@NestedField(parentClass = Account.class, value = "accountUserAccounts")
	@Override
	public Page<UserAccount> getAccountUserAccountsPage(
			@NestedFieldId(value = "id") Long accountId, String search,
			Filter filter, Pagination pagination, Sort[] sorts)
		throws Exception {

		Map<String, Map<String, String>> actions = _getModelActions(
			Collections.singletonMap(
				ActionKeys.MANAGE_USERS,
				new String[] {
					"deleteAccountUserAccountByEmailAddress",
					"deleteAccountUserAccountByExternalReferenceCodeBy" +
						"EmailAddress",
					"deleteAccountUserAccountsByEmailAddress",
					"deleteAccountUserAccountsByExternalReferenceCodeBy" +
						"EmailAddress",
					"getAccountUserAccountsByExternalReferenceCodePage",
					"getAccountUserAccountsPage", "postAccountUserAccount",
					"postAccountUserAccountBatch",
					"postAccountUserAccountByEmailAddress",
					"postAccountUserAccountByExternalReferenceCode",
					"postAccountUserAccountByExternalReferenceCodeBy" +
						"EmailAddress",
					"postAccountUserAccountsByEmailAddress",
					"postAccountUserAccountsByExternalReferenceCodeBy" +
						"EmailAddress"
				}),
			accountId, _accountEntryModelResourcePermission);

		return SearchUtil.search(
			actions,
			booleanQuery -> {
				BooleanFilter booleanFilter =
					booleanQuery.getPreBooleanFilter();

				booleanFilter.add(
					new TermFilter(
						"accountEntryIds", String.valueOf(accountId)),
					BooleanClauseOccur.MUST);
			},
			filter, User.class.getName(), search, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> searchContext.setCompanyId(
				contextCompany.getCompanyId()),
			sorts,
			document -> _toUserAccount(
				actions,
				GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK))));
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return new UserAccountEntityModel(
			EntityFieldsUtil.getEntityFields(
				_portal.getClassNameId(User.class.getName()),
				contextCompany.getCompanyId(), _expandoBridgeIndexer,
				_expandoColumnLocalService, _expandoTableLocalService));
	}

	@Override
	public UserAccount getMyUserAccount() throws Exception {
		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		return _toUserAccount(
			_userService.getUserById(permissionChecker.getUserId()));
	}

	@Override
	public Page<UserAccount>
			getOrganizationByExternalReferenceCodeUserAccountsPage(
				String externalReferenceCode, String search, Filter filter,
				Pagination pagination, Sort[] sorts)
		throws Exception {

		Organization organization =
			_organizationService.getOrganizationByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		return getOrganizationUserAccountsPage(
			String.valueOf(organization.getOrganizationId()), search, filter,
			pagination, sorts);
	}

	@NestedField(
		parentClass = com.liferay.headless.admin.user.dto.v1_0.Organization.class,
		value = "userAccounts"
	)
	@Override
	public Page<UserAccount> getOrganizationUserAccountsPage(
			@NestedFieldId(value = "id") String organizationId, String search,
			Filter filter, Pagination pagination, Sort[] sorts)
		throws Exception {

		return _getUserAccountsPage(
			_getModelActions(
				Collections.singletonMap(
					ActionKeys.MANAGE_USERS,
					new String[] {"getOrganizationUserAccountsPage"}),
				DTOConverterUtil.getModelPrimaryKey(
					_organizationOrganizationDTOConverter, organizationId),
				_organizationModelResourcePermission),
			booleanQuery -> {
				BooleanFilter booleanFilter =
					booleanQuery.getPreBooleanFilter();

				booleanFilter.add(
					new TermFilter(
						"organizationIds",
						String.valueOf(
							DTOConverterUtil.getModelPrimaryKey(
								_organizationOrganizationDTOConverter,
								organizationId))),
					BooleanClauseOccur.MUST);
			},
			filter, search, pagination, sorts, null);
	}

	@Override
	public Boolean getSiteAccountUserAccountSelected(
			Long siteId, Long accountId, Long userAccountId)
		throws Exception {

		AccountEntryUserRel accountEntryUserRel =
			_accountEntryUserRelService.getAccountEntryUserRel(
				accountId, userAccountId);
		Group group = _groupService.getGroup(siteId);

		PortalPreferences portalPreferences =
			_portletPreferencesFactory.getPortalPreferences(
				accountEntryUserRel.getAccountUserId(), false);

		long currentAccountEntryId = GetterUtil.getLong(
			portalPreferences.getValue(
				AccountEntry.class.getName(),
				AccountWebKeys.CURRENT_ACCOUNT_ENTRY_ID + group.getGroupId()));

		return accountEntryUserRel.getAccountEntryId() == currentAccountEntryId;
	}

	@Override
	public Boolean
			getSiteByFriendlyUrlPathAccountByExternalReferenceCodeAccountExternalReferenceCodeUserAccountByExternalReferenceCodeUserAccountExternalReferenceCodeSelected(
				String friendlyUrlPath, String accountExternalReferenceCode,
				String userAccountExternalReferenceCode)
		throws Exception {

		Group group = _groupLocalService.getFriendlyURLGroup(
			contextCompany.getCompanyId(), "/" + friendlyUrlPath);
		AccountEntry accountEntry =
			_accountEntryService.getAccountEntryByExternalReferenceCode(
				accountExternalReferenceCode, contextCompany.getCompanyId());
		UserAccount userAccount = getUserAccountByExternalReferenceCode(
			userAccountExternalReferenceCode);

		return getSiteAccountUserAccountSelected(
			group.getGroupId(), accountEntry.getAccountEntryId(),
			userAccount.getId());
	}

	@Override
	public Page<UserAccount> getSiteUserAccountsPage(
			Long siteId, String search, Filter filter, Pagination pagination,
			Sort[] sorts)
		throws Exception {

		return _getUserAccountsPage(
			Collections.singletonMap(
				_formatActionMapKey("getSiteUserAccountsPage"),
				addAction(
					_formatActionMapKey("getSiteUserAccountsPage"),
					"getSiteUserAccountsPage", User.class.getName(), siteId)),
			booleanQuery -> {
				BooleanFilter booleanFilter =
					booleanQuery.getPreBooleanFilter();

				booleanFilter.add(
					new TermFilter("groupId", String.valueOf(siteId)),
					BooleanClauseOccur.MUST);
			},
			filter, search, pagination, sorts, null);
	}

	@Override
	public UserAccount getUserAccount(Long userAccountId) throws Exception {
		return _toUserAccount(_userService.getUserById(userAccountId));
	}

	@Override
	public UserAccount getUserAccountByEmailAddress(String emailAddress)
		throws Exception {

		return _toUserAccount(
			_userService.getUserByEmailAddress(
				contextCompany.getCompanyId(), emailAddress));
	}

	@Override
	public UserAccount getUserAccountByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		return _toUserAccount(
			_userService.getUserByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId()));
	}

	@Override
	public Page<UserAccount> getUserAccountsByStatusPage(
			String status, String search, Filter filter, Pagination pagination,
			Sort[] sorts)
		throws Exception {

		Integer statusInteger = null;

		if (StringUtil.equalsIgnoreCase(
				UserAccount.Status.ACTIVE.getValue(), status)) {

			statusInteger = WorkflowConstants.STATUS_APPROVED;
		}
		else if (StringUtil.equalsIgnoreCase(
					UserAccount.Status.INACTIVE.getValue(), status)) {

			statusInteger = WorkflowConstants.STATUS_INACTIVE;
		}
		else {
			throw new BadRequestException("Invalid status: " + status);
		}

		return _getUserAccountsPage(
			HashMapBuilder.putAll(
				_getCompanyScopeActions(
					ActionKeys.VIEW, new String[] {"getUserAccountsPage"},
					User.class.getName())
			).putAll(
				_getCompanyScopeActions(
					ActionKeys.ADD_USER,
					new String[] {
						"postUserAccount",
						"putUserAccountByExternalReferenceCode"
					},
					PortletKeys.PORTAL)
			).build(),
			booleanQuery -> {
				BooleanFilter booleanFilter =
					booleanQuery.getPreBooleanFilter();

				User user = _uadAnonymousUserProvider.getAnonymousUser(
					contextCompany.getCompanyId());

				if (user != null) {
					booleanFilter.add(
						new TermFilter("screenName", user.getScreenName()),
						BooleanClauseOccur.MUST_NOT);
				}

				booleanFilter.add(
					new TermFilter("userName", StringPool.BLANK),
					BooleanClauseOccur.MUST_NOT);
			},
			filter, search, pagination, sorts, statusInteger);
	}

	@Override
	public Page<UserAccount> getUserAccountsPage(
			String search, Filter filter, Pagination pagination, Sort[] sorts)
		throws Exception {

		return _getUserAccountsPage(
			HashMapBuilder.<String, Map<String, String>>putAll(
				_getCompanyScopeActions(
					ActionKeys.VIEW, new String[] {"getUserAccountsPage"},
					User.class.getName())
			).putAll(
				_getCompanyScopeActions(
					ActionKeys.ADD_USER,
					new String[] {
						"postUserAccount",
						"putUserAccountByExternalReferenceCode"
					},
					PortletKeys.PORTAL)
			).build(),
			booleanQuery -> {
				BooleanFilter booleanFilter =
					booleanQuery.getPreBooleanFilter();

				booleanFilter.add(
					new TermFilter("userName", StringPool.BLANK),
					BooleanClauseOccur.MUST_NOT);
			},
			filter, search, pagination, sorts, null);
	}

	@Override
	public Page<UserAccount> getUserGroupByExternalReferenceCodeUsersPage(
			String externalReferenceCode, String search, Filter filter,
			Pagination pagination, Sort[] sorts)
		throws Exception {

		UserGroup userGroup =
			_userGroupService.getUserGroupByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		return getUserGroupUsersPage(
			userGroup.getUserGroupId(), search, filter, pagination, sorts);
	}

	@Override
	public Page<UserAccount> getUserGroupUsersPage(
			Long userGroupId, String search, Filter filter,
			Pagination pagination, Sort[] sorts)
		throws Exception {

		UserGroup userGroup = _userGroupService.getUserGroup(userGroupId);

		return SearchUtil.search(
			Collections.emptyMap(),
			booleanQuery -> {
				BooleanFilter booleanFilter =
					booleanQuery.getPreBooleanFilter();

				booleanFilter.add(
					new TermFilter(
						"userGroupIds",
						String.valueOf(userGroup.getUserGroupId())),
					BooleanClauseOccur.MUST);
			},
			filter, User.class.getName(), search, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> searchContext.setCompanyId(
				contextCompany.getCompanyId()),
			sorts,
			document -> _toUserAccount(
				Collections.emptyMap(),
				GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK))));
	}

	@Override
	public void patchSiteAccountUserAccountSelected(
			Long siteId, Long accountId, Long userAccountId)
		throws Exception {

		AccountEntryUserRel accountEntryUserRel =
			_accountEntryUserRelService.getAccountEntryUserRel(
				accountId, userAccountId);
		Group group = _groupService.getGroup(siteId);

		PortalPreferences portalPreferences =
			_portletPreferencesFactory.getPortalPreferences(
				accountEntryUserRel.getAccountUserId(), false);

		portalPreferences.setValue(
			AccountEntry.class.getName(),
			AccountWebKeys.CURRENT_ACCOUNT_ENTRY_ID + group.getGroupId(),
			String.valueOf(accountId));

		_portalPreferencesLocalService.updatePreferences(
			accountEntryUserRel.getAccountUserId(),
			PortletKeys.PREFS_OWNER_TYPE_USER, portalPreferences);
	}

	@Override
	public void
			patchSiteByFriendlyUrlPathAccountByExternalReferenceCodeAccountExternalReferenceCodeUserAccountByExternalReferenceCodeUserAccountExternalReferenceCodeSelected(
				String friendlyUrlPath, String accountExternalReferenceCode,
				String userAccountExternalReferenceCode)
		throws Exception {

		Group group = _groupLocalService.getFriendlyURLGroup(
			contextCompany.getCompanyId(), "/" + friendlyUrlPath);
		AccountEntry accountEntry =
			_accountEntryService.getAccountEntryByExternalReferenceCode(
				accountExternalReferenceCode, contextCompany.getCompanyId());
		UserAccount userAccount = getUserAccountByExternalReferenceCode(
			userAccountExternalReferenceCode);

		patchSiteAccountUserAccountSelected(
			group.getGroupId(), accountEntry.getAccountEntryId(),
			userAccount.getId());
	}

	@Override
	public UserAccount patchUserAccount(
			Long userAccountId, UserAccount userAccount)
		throws Exception {

		User user = _userService.getUserById(userAccountId);

		if (user.getStatus() == WorkflowConstants.STATUS_PENDING) {
			throw new BadRequestException(
				"Unable to patch pending user account " + user.getUserId());
		}

		Contact contact = user.getContact();

		String sms = contact.getSmsSn();
		String facebook = contact.getFacebookSn();
		String jabber = contact.getJabberSn();
		String skype = contact.getSkypeSn();
		String twitter = contact.getTwitterSn();

		UserAccountContactInformation userAccountContactInformation =
			userAccount.getUserAccountContactInformation();

		if (userAccountContactInformation != null) {
			sms = GetterUtil.getString(
				userAccountContactInformation.getSms(), sms);
			facebook = GetterUtil.getString(
				userAccountContactInformation.getFacebook(), facebook);
			jabber = GetterUtil.getString(
				userAccountContactInformation.getJabber(), jabber);
			skype = GetterUtil.getString(
				userAccountContactInformation.getSkype(), skype);
			twitter = GetterUtil.getString(
				userAccountContactInformation.getTwitter(), twitter);
		}

		long[] organizationIds = user.getOrganizationIds();

		OrganizationBrief[] organizationBriefs =
			userAccount.getOrganizationBriefs();

		if (organizationBriefs != null) {
			organizationIds = transformToLongArray(
				Arrays.asList(organizationBriefs), OrganizationBrief::getId);
		}

		long[] roleIds = user.getRoleIds();

		RoleBrief[] roleBriefs = userAccount.getRoleBriefs();

		if (roleBriefs != null) {
			roleIds = transformToLongArray(
				Arrays.asList(roleBriefs), RoleBrief::getId);
		}

		ServiceContext serviceContext = _createServiceContext(userAccount);

		user = _userService.updateUser(
			userAccountId, null, null, null, false, null, null,
			GetterUtil.getString(
				userAccount.getAlternateName(), user.getScreenName()),
			GetterUtil.getString(
				userAccount.getEmailAddress(), user.getEmailAddress()),
			_hasPortrait(user, userAccount),
			_getPortraitBytes(true, user, userAccount),
			GetterUtil.getString(
				userAccount.getLanguageId(), user.getLanguageId()),
			user.getTimeZoneId(), user.getGreeting(), user.getComments(),
			GetterUtil.getString(
				userAccount.getGivenName(), user.getFirstName()),
			GetterUtil.getString(
				userAccount.getAdditionalName(), user.getMiddleName()),
			GetterUtil.getString(
				userAccount.getFamilyName(), user.getLastName()),
			_getPrefixId(contact, userAccount),
			_getSuffixId(contact, userAccount),
			_isMale(contact.isMale(), userAccount.getGender()),
			_getBirthdayMonth(
				_getCalendarFieldValue(Calendar.MONTH, Calendar.JANUARY, user),
				userAccount),
			_getBirthdayDay(
				_getCalendarFieldValue(Calendar.DAY_OF_MONTH, 1, user),
				userAccount),
			_getBirthdayYear(
				_getCalendarFieldValue(Calendar.YEAR, 1977, user), userAccount),
			sms, facebook, jabber, skype, twitter,
			GetterUtil.getString(userAccount.getJobTitle(), user.getJobTitle()),
			user.getGroupIds(), organizationIds, roleIds,
			_userGroupRoleLocalService.getUserGroupRoles(userAccountId),
			user.getUserGroupIds(), _getAddresses(user, userAccount),
			_getServiceBuilderEmailAddresses(user, userAccount),
			_getServiceBuilderPhones(user, userAccount),
			_getWebsites(user, userAccount),
			_announcementsDeliveryLocalService.getUserDeliveries(userAccountId),
			serviceContext);

		user = _userService.updateExternalReferenceCode(
			user,
			GetterUtil.getString(
				userAccount.getExternalReferenceCode(),
				user.getExternalReferenceCode()));
		user = _updatePassword(
			user, userAccount.getCurrentPassword(), userAccount.getPassword());
		user = _updateStatus(serviceContext, user, userAccount);

		AccountBrief[] accountBriefs = userAccount.getAccountBriefs();

		if (accountBriefs != null) {
			_accountEntryUserRelLocalService.
				deleteAccountEntryUserRelsByAccountUserId(userAccountId);

			for (AccountBrief accountBrief : accountBriefs) {
				_accountEntryUserRelLocalService.addAccountEntryUserRel(
					accountBrief.getId(), userAccountId);

				RoleBrief[] accountRoleBriefs = accountBrief.getRoleBriefs();

				if (accountRoleBriefs != null) {
					for (RoleBrief roleBrief : accountRoleBriefs) {
						_accountRoleLocalService.associateUser(
							accountBrief.getId(), roleBrief.getId(),
							userAccountId);
					}
				}
			}
		}

		return _toUserAccount(user);
	}

	@Override
	public UserAccount patchUserAccountByExternalReferenceCode(
			String externalReferenceCode, UserAccount userAccount)
		throws Exception {

		User user = _userService.getUserByExternalReferenceCode(
			externalReferenceCode, contextCompany.getCompanyId());

		return patchUserAccount(user.getUserId(), userAccount);
	}

	@Override
	public void
			postAccountByExternalReferenceCodeUserAccountByExternalReferenceCode(
				String accountExternalReferenceCode,
				String externalReferenceCode)
		throws Exception {

		User user = _userLocalService.getUserByExternalReferenceCode(
			externalReferenceCode, contextCompany.getCompanyId());

		_accountEntryUserRelService.addAccountEntryUserRelByEmailAddress(
			DTOConverterUtil.getModelPrimaryKey(
				_accountResourceDTOConverter, accountExternalReferenceCode),
			user.getEmailAddress(), new long[0], null,
			new ServiceContext() {
				{
					setCompanyId(contextCompany.getCompanyId());
					setLanguageId(
						contextAcceptLanguage.getPreferredLanguageId());
					setUserId(contextUser.getUserId());
				}
			});
	}

	@Override
	public UserAccount postAccountUserAccount(
			Long accountId, UserAccount userAccount)
		throws Exception {

		AccountEntryUserRel accountEntryUserRel =
			_accountEntryUserRelService.addAccountEntryUserRel(
				accountId, contextUser.getUserId(),
				userAccount.getAlternateName(), userAccount.getEmailAddress(),
				_getLocale(userAccount), userAccount.getGivenName(),
				userAccount.getAdditionalName(), userAccount.getFamilyName(),
				_getPrefixId(null, userAccount),
				_getSuffixId(null, userAccount), userAccount.getJobTitle(),
				ServiceContextFactory.getInstance(contextHttpServletRequest));

		User user = accountEntryUserRel.getUser();

		Contact contact = user.getContact();

		String sms = null;
		String facebook = null;
		String jabber = null;
		String skype = null;
		String twitter = null;

		UserAccountContactInformation userAccountContactInformation =
			userAccount.getUserAccountContactInformation();

		if (userAccountContactInformation != null) {
			sms = userAccountContactInformation.getSms();
			facebook = userAccountContactInformation.getFacebook();
			jabber = userAccountContactInformation.getJabber();
			skype = userAccountContactInformation.getSkype();
			twitter = userAccountContactInformation.getTwitter();
		}

		user = _userLocalService.updateUser(
			user.getUserId(), null, null, null, false,
			user.getReminderQueryQuestion(), user.getReminderQueryAnswer(),
			user.getScreenName(), user.getEmailAddress(),
			_hasPortrait(null, userAccount),
			_getPortraitBytes(false, user, userAccount), user.getLanguageId(),
			user.getTimeZoneId(), user.getGreeting(), user.getComments(),
			user.getFirstName(), user.getMiddleName(), user.getLastName(),
			contact.getPrefixListTypeId(), contact.getSuffixListTypeId(),
			user.isMale(), _getBirthdayMonth(Calendar.JANUARY, userAccount),
			_getBirthdayDay(1, userAccount),
			_getBirthdayYear(1977, userAccount), sms, facebook, jabber, skype,
			twitter, user.getJobTitle(), user.getGroupIds(),
			user.getOrganizationIds(), user.getRoleIds(), null,
			user.getUserGroupIds(), _createServiceContext(userAccount));

		UsersAdminUtil.updateAddresses(
			Contact.class.getName(), user.getContactId(),
			_getAddresses(null, userAccount));
		UsersAdminUtil.updateEmailAddresses(
			Contact.class.getName(), user.getContactId(),
			_getServiceBuilderEmailAddresses(null, userAccount));
		UsersAdminUtil.updatePhones(
			Contact.class.getName(), user.getContactId(),
			_getServiceBuilderPhones(null, userAccount));
		UsersAdminUtil.updateWebsites(
			Contact.class.getName(), user.getContactId(),
			_getWebsites(null, userAccount));

		return _toUserAccount(user);
	}

	@Override
	public UserAccount postAccountUserAccountByEmailAddress(
			Long accountId, String emailAddress)
		throws Exception {

		AccountEntryUserRel accountEntryUserRel =
			_accountEntryUserRelService.addAccountEntryUserRelByEmailAddress(
				accountId, emailAddress, new long[0], null,
				new ServiceContext() {
					{
						setCompanyId(contextCompany.getCompanyId());
						setLanguageId(
							contextAcceptLanguage.getPreferredLanguageId());
						setUserId(contextUser.getUserId());
					}
				});

		return _toUserAccount(
			_userLocalService.getUser(accountEntryUserRel.getAccountUserId()));
	}

	@Override
	public UserAccount postAccountUserAccountByExternalReferenceCode(
			String externalReferenceCode, UserAccount userAccount)
		throws Exception {

		return postAccountUserAccount(
			DTOConverterUtil.getModelPrimaryKey(
				_accountResourceDTOConverter, externalReferenceCode),
			userAccount);
	}

	@Override
	public UserAccount
			postAccountUserAccountByExternalReferenceCodeByEmailAddress(
				String externalReferenceCode, String emailAddress)
		throws Exception {

		return postAccountUserAccountByEmailAddress(
			DTOConverterUtil.getModelPrimaryKey(
				_accountResourceDTOConverter, externalReferenceCode),
			emailAddress);
	}

	@Override
	public Page<UserAccount> postAccountUserAccountsByEmailAddress(
			Long accountId, String accountRoleIds, String[] emailAddresses)
		throws Exception {

		List<UserAccount> userAccounts = transformToList(
			emailAddresses,
			emailAddress -> postAccountUserAccountByEmailAddress(
				accountId, emailAddress));

		if (Validator.isNull(accountRoleIds)) {
			return Page.of(userAccounts);
		}

		String[] accountRoleIdsArray = StringUtil.split(
			accountRoleIds, CharPool.COMMA);

		for (UserAccount userAccount : userAccounts) {
			for (String accountRoleId : accountRoleIdsArray) {
				_accountRoleResource.
					postAccountAccountRoleUserAccountAssociation(
						accountId, GetterUtil.getLong(accountRoleId),
						userAccount.getId());
			}
		}

		return Page.of(
			transform(
				userAccounts,
				userAccount -> _toUserAccount(
					_userService.getUserByEmailAddress(
						contextCompany.getCompanyId(),
						userAccount.getEmailAddress()))));
	}

	@Override
	public Page<UserAccount>
			postAccountUserAccountsByExternalReferenceCodeByEmailAddress(
				String externalReferenceCode, String accountRoleIds,
				String[] emailAddresses)
		throws Exception {

		AccountEntry accountEntry =
			_accountEntryService.getAccountEntryByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		return postAccountUserAccountsByEmailAddress(
			accountEntry.getAccountEntryId(), accountRoleIds, emailAddresses);
	}

	@Override
	public UserAccount postUserAccount(UserAccount userAccount)
		throws Exception {

		User user = null;

		boolean autoPassword = false;
		String password = userAccount.getPassword();

		if (Validator.isNull(password)) {
			autoPassword = true;
		}

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			contextHttpServletRequest);

		serviceContext.setExpandoBridgeAttributes(
			CustomFieldsUtil.toMap(
				User.class.getName(), contextCompany.getCompanyId(),
				userAccount.getCustomFields(),
				contextAcceptLanguage.getPreferredLocale()));

		if (Validator.isNull(serviceContext.getPathMain()) ||
			Validator.isNull(serviceContext.getPortalURL())) {

			Company company = _companyService.getCompanyById(
				contextCompany.getCompanyId());

			serviceContext.setPathMain(Portal.PATH_MAIN);
			serviceContext.setPortalURL(company.getPortalURL(0));
		}

		if (contextUser.isGuestUser()) {
			if (_captchaSettings.isCreateAccountCaptchaEnabled()) {
				CaptchaUtil.check(contextHttpServletRequest);
			}

			user = _userService.addUserWithWorkflow(
				contextCompany.getCompanyId(), autoPassword, password, password,
				false, userAccount.getAlternateName(),
				userAccount.getEmailAddress(), _getLocale(userAccount),
				userAccount.getGivenName(), userAccount.getAdditionalName(),
				userAccount.getFamilyName(), _getPrefixId(null, userAccount),
				_getSuffixId(null, userAccount),
				_isMale(true, userAccount.getGender()),
				_getBirthdayMonth(Calendar.JANUARY, userAccount),
				_getBirthdayDay(1, userAccount),
				_getBirthdayYear(1977, userAccount), userAccount.getJobTitle(),
				new long[0], new long[0], new long[0], new long[0], true,
				serviceContext);

			PermissionThreadLocal.setPermissionChecker(
				_permissionCheckerFactory.create(user));

			UsersAdminUtil.updateAddresses(
				Contact.class.getName(), user.getContactId(),
				_getAddresses(null, userAccount));
			UsersAdminUtil.updateEmailAddresses(
				Contact.class.getName(), user.getContactId(),
				_getServiceBuilderEmailAddresses(null, userAccount));
			UsersAdminUtil.updatePhones(
				Contact.class.getName(), user.getContactId(),
				_getServiceBuilderPhones(null, userAccount));
			UsersAdminUtil.updateWebsites(
				Contact.class.getName(), user.getContactId(),
				_getWebsites(null, userAccount));
		}
		else {
			user = _userService.addUserWithWorkflow(
				contextCompany.getCompanyId(), autoPassword, password, password,
				false, userAccount.getAlternateName(),
				userAccount.getEmailAddress(), _getLocale(userAccount),
				userAccount.getGivenName(), userAccount.getAdditionalName(),
				userAccount.getFamilyName(), _getPrefixId(null, userAccount),
				_getSuffixId(null, userAccount),
				_isMale(true, userAccount.getGender()),
				_getBirthdayMonth(Calendar.JANUARY, userAccount),
				_getBirthdayDay(1, userAccount),
				_getBirthdayYear(1977, userAccount), userAccount.getJobTitle(),
				new long[0], new long[0], new long[0], new long[0],
				_getAddresses(null, userAccount),
				_getServiceBuilderEmailAddresses(null, userAccount),
				_getServiceBuilderPhones(null, userAccount),
				_getWebsites(null, userAccount), Collections.emptyList(), true,
				serviceContext);
		}

		user = _userService.updateExternalReferenceCode(
			user, userAccount.getExternalReferenceCode());
		user = _userService.updatePortrait(
			user.getUserId(), _getPortraitBytes(false, null, userAccount));

		UserAccountContactInformation userAccountContactInformation =
			userAccount.getUserAccountContactInformation();

		if (userAccountContactInformation != null) {
			Contact contact = user.getContact();

			contact.setSmsSn(userAccountContactInformation.getSms());
			contact.setFacebookSn(userAccountContactInformation.getFacebook());
			contact.setJabberSn(userAccountContactInformation.getJabber());
			contact.setSkypeSn(userAccountContactInformation.getSkype());
			contact.setTwitterSn(userAccountContactInformation.getTwitter());

			_contactLocalService.updateContact(contact);

			user = _userService.getUserById(user.getUserId());
		}

		return _toUserAccount(user);
	}

	@Override
	public Response postUserAccountImage(
			Long userAccountId, MultipartBody multipartBody)
		throws Exception {

		_userService.updatePortrait(
			userAccountId, multipartBody.getBinaryFileAsBytes("image"));

		Response.ResponseBuilder responseBuilder = Response.noContent();

		return responseBuilder.build();
	}

	@Override
	public UserAccount putUserAccount(
			Long userAccountId, UserAccount userAccount)
		throws Exception {

		User user = _userService.getUserById(userAccountId);

		if (user.getStatus() == WorkflowConstants.STATUS_PENDING) {
			throw new BadRequestException(
				"Unable to put pending user account " + user.getUserId());
		}

		String sms = null;
		String facebook = null;
		String jabber = null;
		String skype = null;
		String twitter = null;

		UserAccountContactInformation userAccountContactInformation =
			userAccount.getUserAccountContactInformation();

		if (userAccountContactInformation != null) {
			sms = userAccountContactInformation.getSms();
			facebook = userAccountContactInformation.getFacebook();
			jabber = userAccountContactInformation.getJabber();
			skype = userAccountContactInformation.getSkype();
			twitter = userAccountContactInformation.getTwitter();
		}

		long[] organizationIds = user.getOrganizationIds();

		OrganizationBrief[] organizationBriefs =
			userAccount.getOrganizationBriefs();

		if (organizationBriefs != null) {
			Long[] ids = transform(
				organizationBriefs,
				organizationBrief -> organizationBrief.getId(), Long.class);

			organizationIds = ArrayUtil.toArray(ids);
		}

		ServiceContext serviceContext = _createServiceContext(userAccount);

		user = _userService.updateUser(
			userAccountId, null, null, null, false, null, null,
			userAccount.getAlternateName(), userAccount.getEmailAddress(),
			_hasPortrait(null, userAccount),
			_getPortraitBytes(false, user, userAccount),
			GetterUtil.getString(
				userAccount.getLanguageId(), user.getLanguageId()),
			user.getTimeZoneId(), user.getGreeting(), user.getComments(),
			userAccount.getGivenName(), userAccount.getAdditionalName(),
			userAccount.getFamilyName(), _getPrefixId(null, userAccount),
			_getSuffixId(null, userAccount),
			_isMale(true, userAccount.getGender()),
			_getBirthdayMonth(Calendar.JANUARY, userAccount),
			_getBirthdayDay(1, userAccount),
			_getBirthdayYear(1977, userAccount), sms, facebook, jabber, skype,
			twitter, userAccount.getJobTitle(), user.getGroupIds(),
			organizationIds, user.getRoleIds(),
			_userGroupRoleLocalService.getUserGroupRoles(userAccountId),
			user.getUserGroupIds(), _getAddresses(null, userAccount),
			_getServiceBuilderEmailAddresses(null, userAccount),
			_getServiceBuilderPhones(null, userAccount),
			_getWebsites(null, userAccount),
			_announcementsDeliveryLocalService.getUserDeliveries(userAccountId),
			serviceContext);

		user = _userService.updateExternalReferenceCode(
			user,
			GetterUtil.getString(
				userAccount.getExternalReferenceCode(),
				user.getExternalReferenceCode()));
		user = _updatePassword(
			user, userAccount.getCurrentPassword(), userAccount.getPassword());
		user = _updateStatus(serviceContext, user, userAccount);

		AccountBrief[] accountBriefs = userAccount.getAccountBriefs();

		if (accountBriefs != null) {
			_accountEntryUserRelLocalService.
				deleteAccountEntryUserRelsByAccountUserId(userAccountId);

			for (AccountBrief accountBrief : accountBriefs) {
				_accountEntryUserRelLocalService.addAccountEntryUserRel(
					accountBrief.getId(), userAccountId);
			}
		}

		return _toUserAccount(user);
	}

	@Override
	public UserAccount putUserAccountByExternalReferenceCode(
			String externalReferenceCode, UserAccount userAccount)
		throws Exception {

		User user = _userService.fetchUserByExternalReferenceCode(
			externalReferenceCode, contextCompany.getCompanyId());

		if (user == null) {
			return postUserAccount(userAccount);
		}

		return putUserAccount(user.getUserId(), userAccount);
	}

	@Override
	protected void preparePatch(
		UserAccount userAccount, UserAccount existingUserAccount) {

		AccountBrief[] accountBriefs = userAccount.getAccountBriefs();

		if (accountBriefs != null) {
			existingUserAccount.setAccountBriefs(() -> accountBriefs);
		}

		OrganizationBrief[] organizationBriefs =
			userAccount.getOrganizationBriefs();

		if (organizationBriefs != null) {
			existingUserAccount.setOrganizationBriefs(() -> organizationBriefs);
		}

		UserAccountContactInformation userAccountContactInformation =
			userAccount.getUserAccountContactInformation();

		if (userAccountContactInformation != null) {
			UserAccountContactInformation
				existingUserAccountContactInformation =
					existingUserAccount.getUserAccountContactInformation();

			EmailAddress[] emailAddresses =
				userAccountContactInformation.getEmailAddresses();

			if (emailAddresses != null) {
				existingUserAccountContactInformation.setEmailAddresses(
					() -> emailAddresses);
			}

			String facebook = userAccountContactInformation.getFacebook();

			if (facebook != null) {
				existingUserAccountContactInformation.setFacebook(
					() -> facebook);
			}

			String jabber = userAccountContactInformation.getJabber();

			if (jabber != null) {
				existingUserAccountContactInformation.setJabber(() -> jabber);
			}

			PostalAddress[] postalAddresses =
				userAccountContactInformation.getPostalAddresses();

			if (postalAddresses != null) {
				existingUserAccountContactInformation.setPostalAddresses(
					() -> postalAddresses);
			}

			String skype = userAccountContactInformation.getSkype();

			if (skype != null) {
				existingUserAccountContactInformation.setSkype(() -> skype);
			}

			String sms = userAccountContactInformation.getSms();

			if (sms != null) {
				existingUserAccountContactInformation.setSms(() -> sms);
			}

			Phone[] telephones = userAccountContactInformation.getTelephones();

			if (telephones != null) {
				existingUserAccountContactInformation.setTelephones(
					() -> telephones);
			}

			String twitter = userAccountContactInformation.getTwitter();

			if (twitter != null) {
				existingUserAccountContactInformation.setTwitter(() -> twitter);
			}

			WebUrl[] webUrls = userAccountContactInformation.getWebUrls();

			if (webUrls != null) {
				existingUserAccountContactInformation.setWebUrls(() -> webUrls);
			}
		}
	}

	private void _checkCurrentPassword(User user, String currentPassword)
		throws Exception {

		if ((user == null) || (contextUser.getUserId() != user.getUserId())) {
			return;
		}

		if (Validator.isNull(currentPassword)) {
			throw new UserPasswordException.MustMatchCurrentPassword(
				user.getUserId());
		}

		int authResult = _userLocalService.authenticateByUserId(
			contextCompany.getCompanyId(), user.getUserId(), currentPassword,
			new HashMap<>(), new HashMap<>(), new HashMap<>());

		if (authResult == Authenticator.FAILURE) {
			if (user.isLockout()) {
				HttpServletRequest originalHttpServletRequest =
					_portal.getOriginalServletRequest(
						contextHttpServletRequest);
				HttpServletResponse httpServletResponse =
					contextHttpServletResponse;

				AuthenticatedSessionManagerUtil.logout(
					originalHttpServletRequest, httpServletResponse);

				throw new UserLockoutException.PasswordPolicyLockout(
					user, user.getPasswordPolicy());
			}

			throw new UserPasswordException.MustMatchCurrentPassword(
				user.getUserId());
		}
	}

	private ServiceContext _createServiceContext(UserAccount userAccount)
		throws Exception {

		return ServiceContextBuilder.create(
			contextCompany.getGroupId(), contextHttpServletRequest, null
		).expandoBridgeAttributes(
			CustomFieldsUtil.toMap(
				User.class.getName(), contextCompany.getCompanyId(),
				userAccount.getCustomFields(),
				contextAcceptLanguage.getPreferredLocale())
		).build();
	}

	private String _formatActionMapKey(String methodName) {
		return TextFormatter.format(methodName, TextFormatter.K);
	}

	private List<Address> _getAddresses(User user, UserAccount userAccount) {
		UserAccountContactInformation userAccountContactInformation =
			userAccount.getUserAccountContactInformation();

		if (userAccountContactInformation == null) {
			if (user != null) {
				return user.getAddresses();
			}

			return Collections.emptyList();
		}

		PostalAddress[] postalAddresses =
			userAccountContactInformation.getPostalAddresses();

		if (postalAddresses == null) {
			if (user != null) {
				return user.getAddresses();
			}

			return Collections.emptyList();
		}

		return ListUtil.filter(
			transformToList(
				postalAddresses,
				_postalAddress ->
					ServiceBuilderAddressUtil.toServiceBuilderAddress(
						contextCompany.getCompanyId(), _postalAddress,
						ListTypeConstants.CONTACT_ADDRESS)),
			Objects::nonNull);
	}

	private int _getBirthdayDay(int defaultValue, UserAccount userAccount) {
		return _getCalendarFieldValue(
			Calendar.DAY_OF_MONTH, defaultValue, userAccount);
	}

	private int _getBirthdayMonth(int defaultValue, UserAccount userAccount) {
		return _getCalendarFieldValue(
			Calendar.MONTH, defaultValue, userAccount);
	}

	private int _getBirthdayYear(int defaultValue, UserAccount userAccount) {
		return _getCalendarFieldValue(Calendar.YEAR, defaultValue, userAccount);
	}

	private int _getCalendarFieldValue(
			int calendarField, int defaultValue, User user)
		throws Exception {

		Date date = user.getBirthday();

		if (date == null) {
			return defaultValue;
		}

		Calendar calendar = CalendarFactoryUtil.getCalendar();

		calendar.setTime(date);

		return calendar.get(calendarField);
	}

	private int _getCalendarFieldValue(
		int calendarField, int defaultValue, UserAccount userAccount) {

		Date date = userAccount.getBirthDate();

		if (date == null) {
			return defaultValue;
		}

		Calendar calendar = CalendarFactoryUtil.getCalendar();

		calendar.setTime(date);

		return calendar.get(calendarField);
	}

	private Map<String, Map<String, String>> _getCompanyScopeActions(
		String actionName, String[] methodNames, String resourceName) {

		Map<String, Map<String, String>> actions = new HashMap<>();

		for (String methodName : methodNames) {
			actions.put(
				_formatActionMapKey(methodName),
				addAction(actionName, methodName, resourceName, 0L));
		}

		return actions;
	}

	private DTOConverterContext _getDTOConverterContext(long userId) {
		return new DefaultDTOConverterContext(
			contextAcceptLanguage.isAcceptAllLanguages(),
			_getModelActions(
				HashMapBuilder.put(
					ActionKeys.DELETE,
					new String[] {
						"deleteUserAccount",
						"deleteUserAccountByExternalReferenceCode"
					}
				).put(
					ActionKeys.UPDATE,
					new String[] {
						"putUserAccount",
						"putUserAccountByExternalReferenceCode",
						"patchUserAccount"
					}
				).put(
					ActionKeys.VIEW,
					new String[] {
						"getMyUserAccount", "getUserAccount",
						"getUserAccountByExternalReferenceCode"
					}
				).build(),
				userId, _userModelResourcePermission),
			null, contextHttpServletRequest, userId,
			contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
			contextUser);
	}

	private Locale _getLocale(UserAccount userAccount) {
		String languageId = userAccount.getLanguageId();

		if (Validator.isNull(languageId)) {
			return contextAcceptLanguage.getPreferredLocale();
		}

		return LocaleUtil.fromLanguageId(languageId);
	}

	private Map<String, Map<String, String>> _getModelActions(
		Map<String, String[]> actionMethodMap, long id,
		ModelResourcePermission<?> modelResourcePermission) {

		Map<String, Map<String, String>> actions = new HashMap<>();

		for (Map.Entry<String, String[]> entry : actionMethodMap.entrySet()) {
			for (String methodName : entry.getValue()) {
				actions.put(
					_formatActionMapKey(methodName),
					addAction(
						entry.getKey(), id, methodName,
						modelResourcePermission));
			}
		}

		return actions;
	}

	private byte[] _getPortraitBytes(
			boolean useUserDefault, User user, UserAccount userAccount)
		throws Exception {

		long imageId = GetterUtil.getLong(userAccount.getImageId());

		if (imageId == 0) {
			FileEntry fileEntry =
				_dlAppLocalService.fetchFileEntryByExternalReferenceCode(
					contextCompany.getGroupId(),
					userAccount.getImageExternalReferenceCode());

			if (fileEntry != null) {
				imageId = fileEntry.getFileEntryId();
			}
			else if ((user != null) && useUserDefault) {
				imageId = user.getPortraitId();
			}
		}

		if ((imageId <= 0) ||
			((user != null) && (user.getPortraitId() == imageId))) {

			return null;
		}

		FileEntry fileEntry = _dlAppLocalService.getFileEntry(imageId);

		return _file.getBytes(fileEntry.getContentStream());
	}

	private long _getPrefixId(Contact contact, UserAccount userAccount) {
		String prefix = userAccount.getHonorificPrefix();

		if (prefix == null) {
			if (contact != null) {
				return contact.getPrefixListTypeId();
			}

			return 0;
		}

		return ServiceBuilderListTypeUtil.getServiceBuilderListTypeId(
			contextCompany.getCompanyId(), ListTypeConstants.CONTACT_PREFIX,
			prefix);
	}

	private List<com.liferay.portal.kernel.model.EmailAddress>
		_getServiceBuilderEmailAddresses(User user, UserAccount userAccount) {

		UserAccountContactInformation userAccountContactInformation =
			userAccount.getUserAccountContactInformation();

		if (userAccountContactInformation == null) {
			if (user != null) {
				return user.getEmailAddresses();
			}

			return Collections.emptyList();
		}

		EmailAddress[] emailAddresses =
			userAccountContactInformation.getEmailAddresses();

		if (emailAddresses == null) {
			if (user != null) {
				return user.getEmailAddresses();
			}

			return Collections.emptyList();
		}

		return ListUtil.filter(
			transformToList(
				emailAddresses,
				emailAddress ->
					ServiceBuilderEmailAddressUtil.toServiceBuilderEmailAddress(
						contextCompany.getCompanyId(), emailAddress,
						ListTypeConstants.CONTACT_EMAIL_ADDRESS)),
			Objects::nonNull);
	}

	private List<com.liferay.portal.kernel.model.Phone>
		_getServiceBuilderPhones(User user, UserAccount userAccount) {

		UserAccountContactInformation userAccountContactInformation =
			userAccount.getUserAccountContactInformation();

		if (userAccountContactInformation == null) {
			if (user != null) {
				return user.getPhones();
			}

			return Collections.emptyList();
		}

		Phone[] phones = userAccountContactInformation.getTelephones();

		if (phones == null) {
			if (user != null) {
				return user.getPhones();
			}

			return Collections.emptyList();
		}

		return ListUtil.filter(
			transformToList(
				phones,
				telephone -> ServiceBuilderPhoneUtil.toServiceBuilderPhone(
					contextCompany.getCompanyId(), telephone,
					ListTypeConstants.CONTACT_PHONE)),
			Objects::nonNull);
	}

	private long _getSuffixId(Contact contact, UserAccount userAccount) {
		String honorificSuffix = userAccount.getHonorificSuffix();

		if (honorificSuffix == null) {
			if (contact != null) {
				return contact.getSuffixListTypeId();
			}

			return 0;
		}

		return ServiceBuilderListTypeUtil.getServiceBuilderListTypeId(
			contextCompany.getCompanyId(), ListTypeConstants.CONTACT_SUFFIX,
			honorificSuffix);
	}

	private Page<UserAccount> _getUserAccountsPage(
			Map<String, Map<String, String>> actions,
			UnsafeConsumer<BooleanQuery, Exception> booleanQueryUnsafeConsumer,
			Filter filter, String search, Pagination pagination, Sort[] sorts,
			Integer status)
		throws Exception {

		return SearchUtil.search(
			actions, booleanQueryUnsafeConsumer, filter, User.class.getName(),
			search, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> {
				Integer searchContextStatus = status;

				if ((searchContextStatus == null) && (filter != null) &&
					StringUtil.containsIgnoreCase(
						filter.toString(), "field=status", StringPool.BLANK)) {

					searchContextStatus = WorkflowConstants.STATUS_ANY;
				}

				searchContext.setAttribute(Field.STATUS, searchContextStatus);

				searchContext.setCompanyId(contextCompany.getCompanyId());
			},
			sorts,
			document -> _toUserAccount(
				actions,
				GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK))));
	}

	private List<Website> _getWebsites(User user, UserAccount userAccount) {
		UserAccountContactInformation userAccountContactInformation =
			userAccount.getUserAccountContactInformation();

		if (userAccountContactInformation == null) {
			if (user != null) {
				return user.getWebsites();
			}

			return Collections.emptyList();
		}

		WebUrl[] webUrls = userAccountContactInformation.getWebUrls();

		if (webUrls == null) {
			if (user != null) {
				return user.getWebsites();
			}

			return Collections.emptyList();
		}

		return ListUtil.filter(
			transformToList(
				webUrls,
				webUrl -> ServiceBuilderWebsiteUtil.toServiceBuilderWebsite(
					contextCompany.getCompanyId(),
					ListTypeConstants.CONTACT_WEBSITE, webUrl)),
			Objects::nonNull);
	}

	private boolean _hasPortrait(User user, UserAccount userAccount)
		throws Exception {

		long imageId = GetterUtil.getLong(userAccount.getImageId());

		if (imageId == 0) {
			FileEntry fileEntry =
				_dlAppLocalService.fetchFileEntryByExternalReferenceCode(
					contextCompany.getGroupId(),
					userAccount.getImageExternalReferenceCode());

			if (fileEntry != null) {
				imageId = fileEntry.getFileEntryId();
			}
			else if (user != null) {
				imageId = user.getPortraitId();
			}
		}

		if (imageId == 0) {
			return false;
		}

		return true;
	}

	private boolean _isMale(boolean defaultValue, UserAccount.Gender gender) {
		if ((gender == null) ||
			!PrefsPropsUtil.getBoolean(
				contextCompany.getCompanyId(),
				PropsKeys.
					FIELD_ENABLE_COM_LIFERAY_PORTAL_KERNEL_MODEL_CONTACT_MALE)) {

			return defaultValue;
		}

		return Objects.equals(UserAccount.Gender.MALE, gender);
	}

	private boolean _isPasswordResetRequired(User user) throws Exception {
		PasswordPolicy passwordPolicy = user.getPasswordPolicy();

		boolean ldapPasswordPolicyEnabled =
			LDAPSettingsUtil.isPasswordPolicyEnabled(user.getCompanyId());

		if ((user.getLastLoginDate() == null) &&
			(((passwordPolicy == null) && !ldapPasswordPolicyEnabled) ||
			 ((passwordPolicy != null) && passwordPolicy.isChangeable() &&
			  passwordPolicy.isChangeRequired()))) {

			return true;
		}

		return false;
	}

	private UserAccount _toUserAccount(
			Map<String, Map<String, String>> actions, long userId)
		throws Exception {

		DTOConverterContext dtoConverterContext = _getDTOConverterContext(
			userId);

		Map<String, Map<String, String>> actionsMap = new HashMap<>();

		if (!actions.isEmpty()) {
			actionsMap.putAll(actions);
		}

		actionsMap.putAll(dtoConverterContext.getActions());

		return _userResourceDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				contextAcceptLanguage.isAcceptAllLanguages(), actionsMap,
				_dtoConverterRegistry, userId,
				contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
				contextUser));
	}

	private UserAccount _toUserAccount(User user) throws Exception {
		return _userResourceDTOConverter.toDTO(
			_getDTOConverterContext(user.getUserId()), user);
	}

	private User _updatePassword(
			User user, String currentPassword, String password)
		throws Exception {

		if ((user == null) || Validator.isNull(password)) {
			return user;
		}

		_checkCurrentPassword(user, currentPassword);

		user = _userService.updatePassword(
			user.getUserId(), password, password,
			_isPasswordResetRequired(user));

		String cookie = CookiesManagerUtil.getCookieValue(
			CookiesConstants.NAME_JSESSIONID, contextHttpServletRequest, false);

		if ((contextUser.getUserId() == user.getUserId()) && (cookie != null)) {
			String login = null;

			String authType = contextCompany.getAuthType();

			if (authType.equals(CompanyConstants.AUTH_TYPE_EA)) {
				login = user.getEmailAddress();
			}
			else if (authType.equals(CompanyConstants.AUTH_TYPE_SN)) {
				login = user.getScreenName();
			}
			else if (authType.equals(CompanyConstants.AUTH_TYPE_ID)) {
				login = String.valueOf(user.getUserId());
			}

			AuthenticatedSessionManagerUtil.login(
				contextHttpServletRequest, contextHttpServletResponse, login,
				password, false, null);
		}

		return user;
	}

	private User _updateStatus(
			ServiceContext serviceContext, int status, User user)
		throws Exception {

		return _userService.updateStatus(user, status, serviceContext);
	}

	private User _updateStatus(
			ServiceContext serviceContext, User user, UserAccount userAccount)
		throws Exception {

		if (StringUtil.equalsIgnoreCase(
				UserAccount.Status.ACTIVE.getValue(),
				userAccount.getStatusAsString())) {

			return _updateStatus(
				serviceContext, WorkflowConstants.STATUS_APPROVED, user);
		}
		else if (StringUtil.equalsIgnoreCase(
					UserAccount.Status.INACTIVE.getValue(),
					userAccount.getStatusAsString())) {

			return _updateStatus(
				serviceContext, WorkflowConstants.STATUS_INACTIVE, user);
		}

		return user;
	}

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		target = "(model.class.name=com.liferay.account.model.AccountEntry)"
	)
	private volatile ModelResourcePermission<AccountEntry>
		_accountEntryModelResourcePermission;

	@Reference
	private AccountEntryService _accountEntryService;

	@Reference
	private AccountEntryUserRelLocalService _accountEntryUserRelLocalService;

	@Reference
	private AccountEntryUserRelService _accountEntryUserRelService;

	@Reference(target = DTOConverterConstants.ACCOUNT_RESOURCE_DTO_CONVERTER)
	private DTOConverter<AccountEntry, Account> _accountResourceDTOConverter;

	@Reference
	private AccountRoleLocalService _accountRoleLocalService;

	@Reference
	private AccountRoleResource _accountRoleResource;

	@Reference
	private AnnouncementsDeliveryLocalService
		_announcementsDeliveryLocalService;

	@Reference
	private CaptchaSettings _captchaSettings;

	@Reference
	private CompanyService _companyService;

	@Reference
	private ContactLocalService _contactLocalService;

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private ExpandoBridgeIndexer _expandoBridgeIndexer;

	@Reference
	private ExpandoColumnLocalService _expandoColumnLocalService;

	@Reference
	private ExpandoTableLocalService _expandoTableLocalService;

	@Reference
	private File _file;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private GroupService _groupService;

	@Reference(
		target = "(model.class.name=com.liferay.portal.kernel.model.Organization)"
	)
	private ModelResourcePermission<Organization>
		_organizationModelResourcePermission;

	@Reference(
		target = DTOConverterConstants.ORGANIZATION_RESOURCE_DTO_CONVERTER
	)
	private DTOConverter
		<Organization, com.liferay.headless.admin.user.dto.v1_0.Organization>
			_organizationOrganizationDTOConverter;

	@Reference
	private OrganizationService _organizationService;

	@Reference
	private PermissionCheckerFactory _permissionCheckerFactory;

	@Reference
	private Portal _portal;

	@Reference
	private PortalPreferencesLocalService _portalPreferencesLocalService;

	@Reference
	private PortletPreferencesFactory _portletPreferencesFactory;

	@Reference
	private UADAnonymousUserProvider _uadAnonymousUserProvider;

	@Reference
	private UserGroupRoleLocalService _userGroupRoleLocalService;

	@Reference
	private UserGroupService _userGroupService;

	@Reference
	private UserLocalService _userLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.portal.kernel.model.User)"
	)
	private ModelResourcePermission<User> _userModelResourcePermission;

	@Reference(target = DTOConverterConstants.USER_RESOURCE_DTO_CONVERTER)
	private DTOConverter<User, UserAccount> _userResourceDTOConverter;

	@Reference
	private UserService _userService;

}