/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.internal.resource.v1_0;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryService;
import com.liferay.headless.admin.user.dto.v1_0.Account;
import com.liferay.headless.admin.user.dto.v1_0.Phone;
import com.liferay.headless.admin.user.dto.v1_0.UserAccount;
import com.liferay.headless.admin.user.internal.dto.v1_0.converter.constants.DTOConverterConstants;
import com.liferay.headless.admin.user.internal.dto.v1_0.util.PhoneUtil;
import com.liferay.headless.admin.user.resource.v1_0.PhoneResource;
import com.liferay.portal.kernel.model.Contact;
import com.liferay.portal.kernel.model.ListType;
import com.liferay.portal.kernel.model.ListTypeConstants;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ListTypeService;
import com.liferay.portal.kernel.service.PhoneService;
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
	properties = "OSGI-INF/liferay/rest/v1_0/phone.properties",
	scope = ServiceScope.PROTOTYPE, service = PhoneResource.class
)
public class PhoneResourceImpl extends BasePhoneResourceImpl {

	@Override
	public void deletePhone(Long phoneId) throws Exception {
		com.liferay.portal.kernel.model.Phone phone = _phoneService.getPhone(
			phoneId);

		_phoneService.deletePhone(phoneId);

		if (phone.isPrimary()) {
			_updatePrimaryPhone(phone.getClassName(), phone.getClassPK());
		}
	}

	@Override
	public void deletePhoneByExternalReferenceCode(String externalReferenceCode)
		throws Exception {

		com.liferay.portal.kernel.model.Phone phone =
			_phoneService.getPhoneByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		deletePhone(phone.getPhoneId());
	}

	@Override
	public Page<Phone> getAccountByExternalReferenceCodePhonesPage(
			String externalReferenceCode)
		throws Exception {

		return getAccountPhonesPage(
			DTOConverterUtil.getModelPrimaryKey(
				_accountResourceDTOConverter, externalReferenceCode));
	}

	@Override
	public Page<Phone> getAccountPhonesPage(Long accountId) throws Exception {
		AccountEntry accountEntry = _accountEntryService.getAccountEntry(
			accountId);

		return Page.of(
			transform(
				_phoneService.getPhones(
					accountEntry.getModelClassName(),
					accountEntry.getAccountEntryId()),
				PhoneUtil::toPhone));
	}

	@Override
	public Page<Phone> getOrganizationByExternalReferenceCodePhonesPage(
			String externalReferenceCode)
		throws Exception {

		return getOrganizationPhonesPage(
			String.valueOf(
				DTOConverterUtil.getModelPrimaryKey(
					_organizationResourceDTOConverter, externalReferenceCode)));
	}

	@Override
	public Page<Phone> getOrganizationPhonesPage(String organizationId)
		throws Exception {

		Organization organization = _organizationResourceDTOConverter.getObject(
			organizationId);

		return Page.of(
			transform(
				_phoneService.getPhones(
					organization.getModelClassName(),
					organization.getOrganizationId()),
				PhoneUtil::toPhone));
	}

	@Override
	public Phone getPhone(Long phoneId) throws Exception {
		return PhoneUtil.toPhone(_phoneService.getPhone(phoneId));
	}

	@Override
	public Phone getPhoneByExternalReferenceCode(String externalReferenceCode)
		throws Exception {

		com.liferay.portal.kernel.model.Phone phone =
			_phoneService.getPhoneByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		return getPhone(phone.getPhoneId());
	}

	@Override
	public Page<Phone> getUserAccountByExternalReferenceCodePhonesPage(
			String externalReferenceCode)
		throws Exception {

		return getUserAccountPhonesPage(
			DTOConverterUtil.getModelPrimaryKey(
				_userResourceDTOConverter, externalReferenceCode));
	}

	@Override
	public Page<Phone> getUserAccountPhonesPage(Long userAccountId)
		throws Exception {

		User user = _userService.getUserById(userAccountId);

		return Page.of(
			transform(
				_phoneService.getPhones(
					Contact.class.getName(), user.getContactId()),
				PhoneUtil::toPhone));
	}

	@Override
	public Phone patchPhone(Long phoneId, Phone phone) throws Exception {
		com.liferay.portal.kernel.model.Phone serviceBuilderPhone =
			_phoneService.getPhone(phoneId);

		boolean oldPrimary = serviceBuilderPhone.isPrimary();

		boolean newPrimary = GetterUtil.getBoolean(
			phone.getPrimary(), oldPrimary);

		serviceBuilderPhone = _phoneService.updatePhone(
			GetterUtil.getString(
				phone.getExternalReferenceCode(),
				serviceBuilderPhone.getExternalReferenceCode()),
			phoneId,
			GetterUtil.getString(
				phone.getPhoneNumber(), serviceBuilderPhone.getNumber()),
			GetterUtil.getString(
				phone.getExtension(), serviceBuilderPhone.getExtension()),
			GetterUtil.getLong(
				_getListTypeId(
					serviceBuilderPhone.getClassName(), phone.getPhoneType()),
				serviceBuilderPhone.getListTypeId()),
			newPrimary);

		if (!newPrimary && oldPrimary) {
			List<com.liferay.portal.kernel.model.Phone> serviceBuilderPhones =
				_phoneService.getPhones(
					serviceBuilderPhone.getClassName(),
					serviceBuilderPhone.getClassPK());

			for (com.liferay.portal.kernel.model.Phone
					currentServiceBuilderPhone : serviceBuilderPhones) {

				if ((serviceBuilderPhones.size() == 1) ||
					(currentServiceBuilderPhone.getPhoneId() !=
						serviceBuilderPhone.getPhoneId())) {

					_phoneService.updatePhone(
						currentServiceBuilderPhone.getExternalReferenceCode(),
						currentServiceBuilderPhone.getPhoneId(),
						currentServiceBuilderPhone.getNumber(),
						currentServiceBuilderPhone.getExtension(),
						currentServiceBuilderPhone.getListTypeId(), true);

					break;
				}
			}
		}

		return PhoneUtil.toPhone(serviceBuilderPhone);
	}

	@Override
	public Phone patchPhoneByExternalReferenceCode(
			String externalReferenceCode, Phone phone)
		throws Exception {

		com.liferay.portal.kernel.model.Phone serviceBuilderPhone =
			_phoneService.getPhoneByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		return patchPhone(serviceBuilderPhone.getPhoneId(), phone);
	}

	private Long _getListTypeId(String className, String name) {
		ListType listType = _listTypeService.fetchListType(
			contextCompany.getCompanyId(), name,
			className + ListTypeConstants.PHONE);

		if (listType == null) {
			return null;
		}

		return listType.getListTypeId();
	}

	private void _updatePrimaryPhone(String className, long contactId)
		throws Exception {

		List<com.liferay.portal.kernel.model.Phone> phones =
			_phoneService.getPhones(className, contactId);

		if (phones.isEmpty()) {
			return;
		}

		com.liferay.portal.kernel.model.Phone phone = phones.get(0);

		_phoneService.updatePhone(
			phone.getExternalReferenceCode(), phone.getPhoneId(),
			phone.getNumber(), phone.getExtension(), phone.getListTypeId(),
			true);
	}

	@Reference
	private AccountEntryService _accountEntryService;

	@Reference(target = DTOConverterConstants.ACCOUNT_RESOURCE_DTO_CONVERTER)
	private DTOConverter<AccountEntry, Account> _accountResourceDTOConverter;

	@Reference
	private ListTypeService _listTypeService;

	@Reference(
		target = DTOConverterConstants.ORGANIZATION_RESOURCE_DTO_CONVERTER
	)
	private DTOConverter
		<Organization, com.liferay.headless.admin.user.dto.v1_0.Organization>
			_organizationResourceDTOConverter;

	@Reference
	private PhoneService _phoneService;

	@Reference(target = DTOConverterConstants.USER_RESOURCE_DTO_CONVERTER)
	private DTOConverter<User, UserAccount> _userResourceDTOConverter;

	@Reference
	private UserService _userService;

}