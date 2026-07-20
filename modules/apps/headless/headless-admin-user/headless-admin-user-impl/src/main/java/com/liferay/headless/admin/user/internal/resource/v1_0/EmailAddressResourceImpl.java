/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.internal.resource.v1_0;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryService;
import com.liferay.headless.admin.user.dto.v1_0.Account;
import com.liferay.headless.admin.user.dto.v1_0.EmailAddress;
import com.liferay.headless.admin.user.dto.v1_0.UserAccount;
import com.liferay.headless.admin.user.internal.dto.v1_0.converter.constants.DTOConverterConstants;
import com.liferay.headless.admin.user.internal.dto.v1_0.util.EmailAddressUtil;
import com.liferay.headless.admin.user.resource.v1_0.EmailAddressResource;
import com.liferay.portal.kernel.exception.NoSuchEmailAddressException;
import com.liferay.portal.kernel.model.Contact;
import com.liferay.portal.kernel.model.ListType;
import com.liferay.portal.kernel.model.ListTypeConstants;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.EmailAddressService;
import com.liferay.portal.kernel.service.ListTypeService;
import com.liferay.portal.kernel.service.UserService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.util.DTOConverterUtil;
import com.liferay.portal.vulcan.pagination.Page;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Javier Gamarra
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/email-address.properties",
	scope = ServiceScope.PROTOTYPE, service = EmailAddressResource.class
)
public class EmailAddressResourceImpl extends BaseEmailAddressResourceImpl {

	@Override
	public void deleteEmailAddress(Long emailAddressId) throws Exception {
		com.liferay.portal.kernel.model.EmailAddress emailAddress =
			_emailAddressService.getEmailAddress(emailAddressId);

		_emailAddressService.deleteEmailAddress(emailAddressId);

		if (emailAddress.isPrimary()) {
			_updatePrimaryEmailAddress(
				emailAddress.getClassName(), emailAddress.getClassPK());
		}
	}

	@Override
	public void deleteEmailAddressByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		com.liferay.portal.kernel.model.EmailAddress serviceEmailAddress =
			_emailAddressService.fetchEmailAddressByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		if (serviceEmailAddress == null) {
			throw new NoSuchEmailAddressException();
		}

		deleteEmailAddress(serviceEmailAddress.getEmailAddressId());
	}

	@Override
	public Page<EmailAddress>
			getAccountByExternalReferenceCodeEmailAddressesPage(
				String externalReferenceCode)
		throws Exception {

		return getAccountEmailAddressesPage(
			DTOConverterUtil.getModelPrimaryKey(
				_accountResourceDTOConverter, externalReferenceCode));
	}

	@Override
	public Page<EmailAddress> getAccountEmailAddressesPage(Long accountId)
		throws Exception {

		AccountEntry accountEntry = _accountEntryService.getAccountEntry(
			accountId);

		return Page.of(
			transform(
				_emailAddressService.getEmailAddresses(
					accountEntry.getModelClassName(),
					accountEntry.getAccountEntryId()),
				EmailAddressUtil::toEmailAddress));
	}

	@Override
	public EmailAddress getEmailAddress(Long emailAddressId) throws Exception {
		return EmailAddressUtil.toEmailAddress(
			_emailAddressService.getEmailAddress(emailAddressId));
	}

	@Override
	public EmailAddress getEmailAddressByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		com.liferay.portal.kernel.model.EmailAddress
			serviceBuilderEmailAddress =
				_emailAddressService.fetchEmailAddressByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		if (serviceBuilderEmailAddress == null) {
			throw new NoSuchEmailAddressException();
		}

		return getEmailAddress(serviceBuilderEmailAddress.getEmailAddressId());
	}

	@Override
	public Page<EmailAddress>
			getOrganizationByExternalReferenceCodeEmailAddressesPage(
				String externalReferenceCode)
		throws Exception {

		return getOrganizationEmailAddressesPage(
			String.valueOf(
				DTOConverterUtil.getModelPrimaryKey(
					_organizationResourceDTOConverter, externalReferenceCode)));
	}

	@Override
	public Page<EmailAddress> getOrganizationEmailAddressesPage(
			String organizationId)
		throws Exception {

		Organization organization = _organizationResourceDTOConverter.getObject(
			organizationId);

		return Page.of(
			transform(
				_emailAddressService.getEmailAddresses(
					organization.getModelClassName(),
					organization.getOrganizationId()),
				EmailAddressUtil::toEmailAddress));
	}

	@Override
	public Page<EmailAddress>
			getUserAccountByExternalReferenceCodeEmailAddressesPage(
				String externalReferenceCode)
		throws Exception {

		return getUserAccountEmailAddressesPage(
			DTOConverterUtil.getModelPrimaryKey(
				_userResourceDTOConverter, externalReferenceCode));
	}

	@Override
	public Page<EmailAddress> getUserAccountEmailAddressesPage(
			Long userAccountId)
		throws Exception {

		User user = _userService.getUserById(userAccountId);

		return Page.of(
			transform(
				_emailAddressService.getEmailAddresses(
					Contact.class.getName(), user.getContactId()),
				EmailAddressUtil::toEmailAddress));
	}

	@Override
	public EmailAddress patchEmailAddress(
			Long emailAddressId, EmailAddress emailAddress)
		throws Exception {

		com.liferay.portal.kernel.model.EmailAddress
			serviceBuilderEmailAddress = _emailAddressService.getEmailAddress(
				emailAddressId);

		return _updateEmailAddress(emailAddress, serviceBuilderEmailAddress);
	}

	@Override
	public EmailAddress patchEmailAddressByExternalReferenceCode(
			String externalReferenceCode, EmailAddress emailAddress)
		throws Exception {

		com.liferay.portal.kernel.model.EmailAddress
			serviceBuilderEmailAddress =
				_emailAddressService.fetchEmailAddressByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		if (serviceBuilderEmailAddress == null) {
			throw new NoSuchEmailAddressException();
		}

		return _updateEmailAddress(emailAddress, serviceBuilderEmailAddress);
	}

	private Long _getListTypeId(String className, String name) {
		ListType listType = _listTypeService.fetchListType(
			contextCompany.getCompanyId(), name,
			className + ListTypeConstants.EMAIL_ADDRESS);

		if (listType == null) {
			return null;
		}

		return listType.getListTypeId();
	}

	private EmailAddress _updateEmailAddress(
			EmailAddress emailAddress,
			com.liferay.portal.kernel.model.EmailAddress
				serviceBuilderEmailAddress)
		throws Exception {

		boolean oldPrimary = serviceBuilderEmailAddress.isPrimary();

		boolean newPrimary = GetterUtil.getBoolean(
			emailAddress.getPrimary(), oldPrimary);

		serviceBuilderEmailAddress = _emailAddressService.updateEmailAddress(
			GetterUtil.getString(
				emailAddress.getExternalReferenceCode(),
				serviceBuilderEmailAddress.getExternalReferenceCode()),
			serviceBuilderEmailAddress.getEmailAddressId(),
			GetterUtil.getString(
				emailAddress.getEmailAddress(),
				serviceBuilderEmailAddress.getAddress()),
			GetterUtil.getLong(
				_getListTypeId(
					serviceBuilderEmailAddress.getClassName(),
					emailAddress.getType()),
				serviceBuilderEmailAddress.getListTypeId()),
			newPrimary);

		if (!newPrimary && oldPrimary) {
			List<com.liferay.portal.kernel.model.EmailAddress>
				serviceBuilderEmailAddresses =
					_emailAddressService.getEmailAddresses(
						serviceBuilderEmailAddress.getClassName(),
						serviceBuilderEmailAddress.getClassPK());

			for (com.liferay.portal.kernel.model.EmailAddress
					currentServiceBuilderEmailAddress :
						serviceBuilderEmailAddresses) {

				if ((serviceBuilderEmailAddresses.size() == 1) ||
					(currentServiceBuilderEmailAddress.getEmailAddressId() !=
						serviceBuilderEmailAddress.getEmailAddressId())) {

					_emailAddressService.updateEmailAddress(
						currentServiceBuilderEmailAddress.
							getExternalReferenceCode(),
						currentServiceBuilderEmailAddress.getEmailAddressId(),
						currentServiceBuilderEmailAddress.getAddress(),
						currentServiceBuilderEmailAddress.getListTypeId(),
						true);

					break;
				}
			}
		}

		return EmailAddressUtil.toEmailAddress(serviceBuilderEmailAddress);
	}

	private void _updatePrimaryEmailAddress(String className, long contactId)
		throws Exception {

		List<com.liferay.portal.kernel.model.EmailAddress> emailAddresses =
			_emailAddressService.getEmailAddresses(className, contactId);

		if (emailAddresses.isEmpty()) {
			return;
		}

		com.liferay.portal.kernel.model.EmailAddress emailAddress =
			emailAddresses.get(0);

		_emailAddressService.updateEmailAddress(
			emailAddress.getExternalReferenceCode(),
			emailAddress.getEmailAddressId(), emailAddress.getAddress(),
			emailAddress.getListTypeId(), true);
	}

	@Reference
	private AccountEntryService _accountEntryService;

	@Reference(target = DTOConverterConstants.ACCOUNT_RESOURCE_DTO_CONVERTER)
	private DTOConverter<AccountEntry, Account> _accountResourceDTOConverter;

	@Reference
	private EmailAddressService _emailAddressService;

	@Reference
	private ListTypeService _listTypeService;

	@Reference(
		target = DTOConverterConstants.ORGANIZATION_RESOURCE_DTO_CONVERTER
	)
	private DTOConverter
		<Organization, com.liferay.headless.admin.user.dto.v1_0.Organization>
			_organizationResourceDTOConverter;

	@Reference(target = DTOConverterConstants.USER_RESOURCE_DTO_CONVERTER)
	private DTOConverter<User, UserAccount> _userResourceDTOConverter;

	@Reference
	private UserService _userService;

}