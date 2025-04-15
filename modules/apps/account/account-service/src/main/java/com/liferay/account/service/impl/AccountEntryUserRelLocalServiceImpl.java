/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.service.impl;

import com.liferay.account.configuration.AccountEntryEmailConfiguration;
import com.liferay.account.constants.AccountConstants;
import com.liferay.account.constants.AccountPortletKeys;
import com.liferay.account.constants.AccountTicketConstants;
import com.liferay.account.exception.AccountEntryTypeException;
import com.liferay.account.exception.AccountEntryUserRelEmailAddressException;
import com.liferay.account.exception.DuplicateAccountEntryIdException;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountEntryUserRel;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountRoleLocalService;
import com.liferay.account.service.base.AccountEntryUserRelLocalServiceBaseImpl;
import com.liferay.account.validator.AccountEntryEmailAddressValidator;
import com.liferay.account.validator.AccountEntryEmailAddressValidatorFactory;
import com.liferay.mail.kernel.model.MailMessage;
import com.liferay.mail.kernel.service.MailService;
import com.liferay.mail.kernel.template.MailTemplate;
import com.liferay.mail.kernel.template.MailTemplateContext;
import com.liferay.mail.kernel.template.MailTemplateContextBuilder;
import com.liferay.mail.kernel.template.MailTemplateFactoryUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.exception.UserEmailAddressException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Ticket;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.TicketLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.settings.LocalizedValuesMap;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.EscapableObject;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.mail.internet.InternetAddress;

import jakarta.portlet.PortletMode;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.WindowState;

import java.time.Month;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = "model.class.name=com.liferay.account.model.AccountEntryUserRel",
	service = AopService.class
)
public class AccountEntryUserRelLocalServiceImpl
	extends AccountEntryUserRelLocalServiceBaseImpl {

	@Override
	public AccountEntryUserRel addAccountEntryUserRel(
			long accountEntryId, long accountUserId)
		throws PortalException {

		AccountEntryUserRel accountEntryUserRel =
			accountEntryUserRelPersistence.fetchByAEI_AUI(
				accountEntryId, accountUserId);

		if (accountEntryUserRel != null) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Account entry user relationship already exists for ",
						"account entry ", accountEntryId, " and user ",
						accountUserId));
			}

			return accountEntryUserRel;
		}

		if (accountEntryId != AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT) {
			_accountEntryLocalService.getAccountEntry(accountEntryId);
		}

		User accountUser = _userLocalService.getUser(accountUserId);

		_validateEmailAddress(
			_accountEntryEmailAddressValidatorFactory.create(
				accountUser.getCompanyId(), _getAccountDomains(accountEntryId)),
			accountUser.getEmailAddress());

		accountEntryUserRel = createAccountEntryUserRel(
			counterLocalService.increment());

		accountEntryUserRel.setAccountEntryId(accountEntryId);
		accountEntryUserRel.setAccountUserId(accountUserId);

		return addAccountEntryUserRel(accountEntryUserRel);
	}

	@Override
	public AccountEntryUserRel addAccountEntryUserRel(
			long accountEntryId, long creatorUserId, String screenName,
			String emailAddress, Locale locale, String firstName,
			String middleName, String lastName, long prefixListTypeId,
			long suffixListTypeId, String jobTitle,
			ServiceContext serviceContext)
		throws PortalException {

		long companyId = CompanyThreadLocal.getCompanyId();

		if (accountEntryId != AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT) {
			AccountEntry accountEntry =
				_accountEntryLocalService.getAccountEntry(accountEntryId);

			companyId = accountEntry.getCompanyId();
		}

		_validateEmailAddress(
			_accountEntryEmailAddressValidatorFactory.create(
				companyId, _getAccountDomains(accountEntryId)),
			emailAddress);

		boolean autoPassword = true;
		String password1 = null;
		String password2 = null;
		boolean autoScreenName = false;
		boolean male = true;
		int birthdayMonth = Month.JANUARY.getValue();
		int birthdayDay = 1;
		int birthdayYear = 1970;
		long[] groupIds = null;
		long[] organizationIds = null;
		long[] roleIds = null;
		long[] userGroupIds = null;
		boolean sendEmail = true;

		User user = _userLocalService.addUser(
			creatorUserId, companyId, autoPassword, password1, password2,
			autoScreenName, screenName, emailAddress, locale, firstName,
			middleName, lastName, prefixListTypeId, suffixListTypeId, male,
			birthdayMonth, birthdayDay, birthdayYear, jobTitle,
			UserConstants.TYPE_REGULAR, groupIds, organizationIds, roleIds,
			userGroupIds, sendEmail, serviceContext);

		return accountEntryUserRelLocalService.addAccountEntryUserRel(
			accountEntryId, user.getUserId());
	}

	@Override
	public AccountEntryUserRel addAccountEntryUserRelByEmailAddress(
			long accountEntryId, String emailAddress, long[] accountRoleIds,
			String userExternalReferenceCode, ServiceContext serviceContext)
		throws PortalException {

		User user = null;

		if (Validator.isNotNull(userExternalReferenceCode)) {
			user = _userLocalService.fetchUserByExternalReferenceCode(
				userExternalReferenceCode, serviceContext.getCompanyId());
		}

		if (user == null) {
			if (Validator.isNull(emailAddress)) {
				throw new AccountEntryUserRelEmailAddressException();
			}

			user = _userLocalService.fetchUserByEmailAddress(
				serviceContext.getCompanyId(), emailAddress);
		}

		if (user == null) {
			AccountEntry accountEntry =
				_accountEntryLocalService.getAccountEntry(accountEntryId);

			Group group = accountEntry.getAccountEntryGroup();

			long[] groupIds = {group.getGroupId()};

			if (serviceContext.getScopeGroupId() > 0) {
				groupIds = ArrayUtil.append(
					groupIds, serviceContext.getScopeGroupId());
			}

			user = _userLocalService.addUserWithWorkflow(
				serviceContext.getUserId(), serviceContext.getCompanyId(), true,
				StringPool.BLANK, StringPool.BLANK, true, StringPool.BLANK,
				emailAddress, serviceContext.getLocale(), emailAddress,
				StringPool.BLANK, emailAddress, 0, 0, true, 1, 1, 1970,
				StringPool.BLANK, UserConstants.TYPE_REGULAR, groupIds, null,
				null, null, true, serviceContext);

			user.setExternalReferenceCode(userExternalReferenceCode);

			user = _userLocalService.updateUser(user);
		}

		AccountEntryUserRel accountEntryUserRel =
			accountEntryUserRelLocalService.addAccountEntryUserRel(
				accountEntryId, user.getUserId());

		_updateRoles(accountEntryId, user.getUserId(), accountRoleIds);

		return accountEntryUserRel;
	}

	@Override
	public void addAccountEntryUserRels(
			long accountEntryId, long[] accountUserIds)
		throws PortalException {

		for (long accountUserId : accountUserIds) {
			addAccountEntryUserRel(accountEntryId, accountUserId);
		}
	}

	@Override
	public AccountEntryUserRel addPersonTypeAccountEntryUserRel(
			long accountEntryId, long creatorUserId, String screenName,
			String emailAddress, Locale locale, String firstName,
			String middleName, String lastName, long prefixListTypeId,
			long suffixListTypeId, String jobTitle,
			ServiceContext serviceContext)
		throws PortalException {

		AccountEntry accountEntry = _accountEntryLocalService.getAccountEntry(
			accountEntryId);

		if (!Objects.equals(
				AccountConstants.ACCOUNT_ENTRY_TYPE_PERSON,
				accountEntry.getType())) {

			throw new AccountEntryTypeException();
		}

		deleteAccountEntryUserRelsByAccountEntryId(accountEntryId);

		return accountEntryUserRelLocalService.addAccountEntryUserRel(
			accountEntryId, creatorUserId, screenName, emailAddress, locale,
			firstName, middleName, lastName, prefixListTypeId, suffixListTypeId,
			jobTitle, serviceContext);
	}

	@Override
	public void deleteAccountEntryUserRelByEmailAddress(
			long accountEntryId, String emailAddress)
		throws PortalException {

		AccountEntry accountEntry = _accountEntryLocalService.getAccountEntry(
			accountEntryId);

		User user = _userLocalService.getUserByEmailAddress(
			accountEntry.getCompanyId(), emailAddress);

		accountEntryUserRelPersistence.removeByAEI_AUI(
			accountEntry.getAccountEntryId(), user.getUserId());
	}

	@Override
	public void deleteAccountEntryUserRels(
			long accountEntryId, long[] accountUserIds)
		throws PortalException {

		for (long accountUserId : accountUserIds) {
			accountEntryUserRelPersistence.removeByAEI_AUI(
				accountEntryId, accountUserId);
		}
	}

	@Override
	public void deleteAccountEntryUserRelsByAccountEntryId(
		long accountEntryId) {

		for (AccountEntryUserRel accountEntryUserRel :
				getAccountEntryUserRelsByAccountEntryId(accountEntryId)) {

			deleteAccountEntryUserRel(accountEntryUserRel);
		}
	}

	@Override
	public void deleteAccountEntryUserRelsByAccountUserId(long accountUserId) {
		for (AccountEntryUserRel accountEntryUserRel :
				getAccountEntryUserRelsByAccountUserId(accountUserId)) {

			deleteAccountEntryUserRel(accountEntryUserRel);
		}
	}

	@Override
	public AccountEntryUserRel fetchAccountEntryUserRel(
		long accountEntryId, long accountUserId) {

		return accountEntryUserRelPersistence.fetchByAEI_AUI(
			accountEntryId, accountUserId);
	}

	@Override
	public AccountEntryUserRel getAccountEntryUserRel(
			long accountEntryId, long accountUserId)
		throws PortalException {

		return accountEntryUserRelPersistence.findByAEI_AUI(
			accountEntryId, accountUserId);
	}

	@Override
	public List<AccountEntryUserRel> getAccountEntryUserRelsByAccountEntryId(
		long accountEntryId) {

		return accountEntryUserRelPersistence.findByAccountEntryId(
			accountEntryId);
	}

	@Override
	public List<AccountEntryUserRel> getAccountEntryUserRelsByAccountEntryId(
		long accountEntryId, int start, int end) {

		return accountEntryUserRelPersistence.findByAccountEntryId(
			accountEntryId, start, end);
	}

	@Override
	public List<AccountEntryUserRel> getAccountEntryUserRelsByAccountUserId(
		long accountUserId) {

		return accountEntryUserRelPersistence.findByAccountUserId(
			accountUserId);
	}

	@Override
	public long getAccountEntryUserRelsCountByAccountEntryId(
		long accountEntryId) {

		return accountEntryUserRelPersistence.countByAccountEntryId(
			accountEntryId);
	}

	@Override
	public boolean hasAccountEntryUserRel(long accountEntryId, long userId) {
		AccountEntryUserRel accountEntryUserRel =
			accountEntryUserRelPersistence.fetchByAEI_AUI(
				accountEntryId, userId);

		if (accountEntryUserRel != null) {
			return true;
		}

		return false;
	}

	@Override
	public void inviteUser(
			long accountEntryId, long[] accountRoleIds, String emailAddress,
			User inviter, ServiceContext serviceContext)
		throws PortalException {

		User user = _userLocalService.fetchUserByEmailAddress(
			inviter.getCompanyId(), emailAddress);

		if (user != null) {
			addAccountEntryUserRel(accountEntryId, user.getUserId());

			_updateRoles(accountEntryId, user.getUserId(), accountRoleIds);
		}
		else {
			_sendEmail(
				accountEntryId, accountRoleIds, emailAddress, inviter,
				serviceContext);
		}
	}

	@Override
	public boolean isAccountEntryUser(long userId) {
		if (accountEntryUserRelPersistence.countByAccountUserId(userId) > 0) {
			return true;
		}

		return false;
	}

	@Override
	public void setAccountEntryUserRels(
			long accountEntryId, long[] accountUserIds)
		throws PortalException {

		if (accountUserIds == null) {
			return;
		}

		Set<Long> newAccountUserIds = SetUtil.fromArray(accountUserIds);

		Set<Long> oldAccountUserIds = SetUtil.fromCollection(
			ListUtil.toList(
				getAccountEntryUserRelsByAccountEntryId(accountEntryId),
				AccountEntryUserRel::getAccountUserId));

		Set<Long> removeAccountUserIds = new HashSet<>(oldAccountUserIds);

		removeAccountUserIds.removeAll(newAccountUserIds);

		deleteAccountEntryUserRels(
			accountEntryId, ArrayUtil.toLongArray(removeAccountUserIds));

		newAccountUserIds.removeAll(oldAccountUserIds);

		addAccountEntryUserRels(
			accountEntryId, ArrayUtil.toLongArray(newAccountUserIds));
	}

	@Override
	public void setPersonTypeAccountEntryUser(long accountEntryId, long userId)
		throws PortalException {

		AccountEntry accountEntry = _accountEntryLocalService.getAccountEntry(
			accountEntryId);

		if (!Objects.equals(
				AccountConstants.ACCOUNT_ENTRY_TYPE_PERSON,
				accountEntry.getType())) {

			throw new AccountEntryTypeException();
		}

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Updating user for person account entry: " + accountEntryId);
		}

		List<AccountEntryUserRel> removeAccountEntryUserRels = new ArrayList<>(
			getAccountEntryUserRelsByAccountEntryId(accountEntryId));

		boolean currentAccountUser = removeAccountEntryUserRels.removeIf(
			accountEntryUserRel ->
				accountEntryUserRel.getAccountUserId() == userId);

		removeAccountEntryUserRels.forEach(
			accountEntryUserRel -> {
				if (_log.isDebugEnabled()) {
					_log.debug(
						"Removing user: " +
							accountEntryUserRel.getAccountUserId());
				}

				deleteAccountEntryUserRel(accountEntryUserRel);
			});

		if ((userId > 0) && !currentAccountUser) {
			if (_log.isDebugEnabled()) {
				_log.debug("Adding user: " + userId);
			}

			addAccountEntryUserRel(accountEntryId, userId);
		}
	}

	@Override
	public void updateAccountEntryUserRels(
			long[] addAccountEntryIds, long[] deleteAccountEntryIds,
			long accountUserId)
		throws PortalException {

		Set<Long> set = SetUtil.intersect(
			addAccountEntryIds, deleteAccountEntryIds);

		if (SetUtil.isNotEmpty(set)) {
			throw new DuplicateAccountEntryIdException();
		}

		for (long addAccountEntryId : addAccountEntryIds) {
			if (!hasAccountEntryUserRel(addAccountEntryId, accountUserId)) {
				addAccountEntryUserRel(addAccountEntryId, accountUserId);
			}
		}

		for (long deleteAccountEntryId : deleteAccountEntryIds) {
			if (hasAccountEntryUserRel(deleteAccountEntryId, accountUserId)) {
				accountEntryUserRelPersistence.removeByAEI_AUI(
					deleteAccountEntryId, accountUserId);
			}
		}
	}

	private String[] _getAccountDomains(long accountEntryId) {
		AccountEntry accountEntry = _accountEntryLocalService.fetchAccountEntry(
			accountEntryId);

		if ((accountEntry == null) || !accountEntry.isRestrictMembership()) {
			return new String[0];
		}

		return accountEntry.getDomainsArray();
	}

	private void _sendEmail(
			long accountEntryId, long[] accountRoleIds, String emailAddress,
			User inviter, ServiceContext serviceContext)
		throws PortalException {

		_validateEmailAddress(
			_accountEntryEmailAddressValidatorFactory.create(
				inviter.getCompanyId(), _getAccountDomains(accountEntryId)),
			emailAddress);

		try {
			AccountEntryEmailConfiguration accountEntryEmailConfiguration =
				_configurationProvider.getCompanyConfiguration(
					AccountEntryEmailConfiguration.class,
					inviter.getCompanyId());

			int invitationTokenExpirationTime =
				accountEntryEmailConfiguration.invitationTokenExpirationTime();

			Ticket ticket = _ticketLocalService.addTicket(
				inviter.getCompanyId(), AccountEntry.class.getName(),
				accountEntryId, AccountTicketConstants.TYPE_USER_INVITATION,
				JSONUtil.put(
					"accountRoleIds", accountRoleIds
				).put(
					"emailAddress", emailAddress
				).toString(),
				new Date(
					System.currentTimeMillis() +
						TimeUnit.HOURS.toMillis(invitationTokenExpirationTime)),
				serviceContext);

			Group guestGroup = _groupLocalService.getGroup(
				inviter.getCompanyId(), GroupConstants.GUEST);

			String url = PortletURLBuilder.create(
				PortletURLFactoryUtil.create(
					serviceContext.getRequest(),
					AccountPortletKeys.ACCOUNT_USERS_REGISTRATION,
					_layoutLocalService.fetchDefaultLayout(
						guestGroup.getGroupId(), false),
					PortletRequest.RENDER_PHASE)
			).setMVCRenderCommandName(
				"/account_admin/create_account_user"
			).setParameter(
				"ticketKey", ticket.getKey()
			).setPortletMode(
				PortletMode.VIEW
			).setWindowState(
				WindowState.MAXIMIZED
			).buildString();

			MailTemplateContextBuilder mailTemplateContextBuilder =
				MailTemplateFactoryUtil.createMailTemplateContextBuilder();

			AccountEntry accountEntry =
				_accountEntryLocalService.getAccountEntry(accountEntryId);

			mailTemplateContextBuilder.put(
				"[$ACCOUNT_NAME$]",
				new EscapableObject<>(accountEntry.getName()));

			mailTemplateContextBuilder.put("[$CREATE_ACCOUNT_URL$]", url);

			String invitationEmailSenderName =
				accountEntryEmailConfiguration.invitationEmailSenderName();

			if (Validator.isNull(invitationEmailSenderName)) {
				invitationEmailSenderName = inviter.getFullName();
			}

			mailTemplateContextBuilder.put(
				"[$INVITE_SENDER_NAME$]",
				new EscapableObject<>(invitationEmailSenderName));

			MailTemplateContext mailTemplateContext =
				mailTemplateContextBuilder.build();

			LocalizedValuesMap subjectLocalizedValuesMap =
				accountEntryEmailConfiguration.invitationEmailSubject();

			MailTemplate subjectMailTemplate =
				MailTemplateFactoryUtil.createMailTemplate(
					subjectLocalizedValuesMap.get(inviter.getLocale()), false);

			LocalizedValuesMap bodyLocalizedValuesMap =
				accountEntryEmailConfiguration.invitationEmailBody();

			MailTemplate bodyMailTemplate =
				MailTemplateFactoryUtil.createMailTemplate(
					bodyLocalizedValuesMap.get(inviter.getLocale()), true);

			String invitationEmailSenderEmailAddress =
				accountEntryEmailConfiguration.
					invitationEmailSenderEmailAddress();

			if (Validator.isNull(invitationEmailSenderEmailAddress)) {
				invitationEmailSenderEmailAddress = inviter.getEmailAddress();
			}

			MailMessage mailMessage = new MailMessage(
				new InternetAddress(
					invitationEmailSenderEmailAddress,
					invitationEmailSenderName),
				new InternetAddress(emailAddress),
				subjectMailTemplate.renderAsString(
					inviter.getLocale(), mailTemplateContext),
				bodyMailTemplate.renderAsString(
					inviter.getLocale(), mailTemplateContext),
				true);

			_mailService.sendEmail(mailMessage);
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
	}

	private void _updateRoles(
			long accountEntryId, long userId, long[] accountRoleIds)
		throws PortalException {

		if (accountRoleIds == null) {
			return;
		}

		_accountRoleLocalService.associateUser(
			accountEntryId, accountRoleIds, userId);
	}

	private void _validateEmailAddress(
			AccountEntryEmailAddressValidator accountEntryEmailAddressValidator,
			String emailAddress)
		throws PortalException {

		if (accountEntryEmailAddressValidator.isBlockedDomain(emailAddress)) {
			throw new UserEmailAddressException.MustNotUseBlockedDomain(
				emailAddress,
				StringUtil.merge(
					accountEntryEmailAddressValidator.getBlockedDomains(),
					StringPool.COMMA_AND_SPACE));
		}

		if (!accountEntryEmailAddressValidator.isValidDomain(emailAddress)) {
			throw new UserEmailAddressException.MustHaveValidDomain(
				emailAddress,
				StringUtil.merge(
					accountEntryEmailAddressValidator.getValidDomains()));
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AccountEntryUserRelLocalServiceImpl.class);

	@Reference
	private AccountEntryEmailAddressValidatorFactory
		_accountEntryEmailAddressValidatorFactory;

	@Reference
	private AccountEntryLocalService _accountEntryLocalService;

	@Reference
	private AccountRoleLocalService _accountRoleLocalService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private MailService _mailService;

	@Reference
	private TicketLocalService _ticketLocalService;

	@Reference
	private UserLocalService _userLocalService;

}