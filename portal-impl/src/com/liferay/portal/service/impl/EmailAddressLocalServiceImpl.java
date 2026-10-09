/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.impl;

import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.exception.EmailAddressException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.EmailAddress;
import com.liferay.portal.kernel.model.ListTypeConstants;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ListTypeLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.persistence.UserPersistence;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.service.base.EmailAddressLocalServiceBaseImpl;

import java.util.List;

/**
 * @author Brian Wing Shun Chan
 * @author Alexander Chow
 */
public class EmailAddressLocalServiceImpl
	extends EmailAddressLocalServiceBaseImpl {

	@Override
	public EmailAddress addEmailAddress(
			String externalReferenceCode, long userId, String className,
			long classPK, String address, long listTypeId, boolean primary,
			ServiceContext serviceContext)
		throws PortalException {

		User user = _userPersistence.findByPrimaryKey(userId);
		long classNameId = _classNameLocalService.getClassNameId(className);

		validate(
			0, user.getCompanyId(), classNameId, classPK, address, listTypeId,
			primary);

		long emailAddressId = counterLocalService.increment();

		EmailAddress emailAddress = emailAddressPersistence.create(
			emailAddressId);

		emailAddress.setUuid(serviceContext.getUuid());
		emailAddress.setExternalReferenceCode(externalReferenceCode);
		emailAddress.setCompanyId(user.getCompanyId());
		emailAddress.setUserId(user.getUserId());
		emailAddress.setUserName(user.getFullName());
		emailAddress.setClassNameId(classNameId);
		emailAddress.setClassPK(classPK);
		emailAddress.setAddress(address);
		emailAddress.setListTypeId(listTypeId);
		emailAddress.setPrimary(primary);

		return emailAddressPersistence.update(emailAddress);
	}

	@Override
	@SystemEvent(
		action = SystemEventConstants.ACTION_SKIP,
		type = SystemEventConstants.TYPE_DELETE
	)
	public EmailAddress deleteEmailAddress(EmailAddress emailAddress) {
		emailAddressPersistence.remove(emailAddress);

		return emailAddress;
	}

	@Override
	public EmailAddress deleteEmailAddress(long emailAddressId)
		throws PortalException {

		EmailAddress emailAddress = emailAddressPersistence.findByPrimaryKey(
			emailAddressId);

		return emailAddressLocalService.deleteEmailAddress(emailAddress);
	}

	@Override
	public void deleteEmailAddresses(
		long companyId, String className, long classPK) {

		List<EmailAddress> emailAddresses = emailAddressPersistence.findByC_C_C(
			companyId, _classNameLocalService.getClassNameId(className),
			classPK);

		for (EmailAddress emailAddress : emailAddresses) {
			emailAddressLocalService.deleteEmailAddress(emailAddress);
		}
	}

	@Override
	public List<EmailAddress> getEmailAddresses() {
		return emailAddressPersistence.findAll();
	}

	@Override
	public List<EmailAddress> getEmailAddresses(
		long companyId, String className, long classPK) {

		return emailAddressPersistence.findByC_C_C(
			companyId, _classNameLocalService.getClassNameId(className),
			classPK);
	}

	@Override
	public EmailAddress updateEmailAddress(
			String externalReferenceCode, long emailAddressId, String address,
			long listTypeId, boolean primary)
		throws PortalException {

		validate(emailAddressId, 0, 0, 0, address, listTypeId, primary);

		EmailAddress emailAddress = emailAddressPersistence.findByPrimaryKey(
			emailAddressId);

		emailAddress.setExternalReferenceCode(externalReferenceCode);
		emailAddress.setAddress(address);
		emailAddress.setListTypeId(listTypeId);
		emailAddress.setPrimary(primary);

		return emailAddressPersistence.update(emailAddress);
	}

	protected void validate(
		long emailAddressId, long companyId, long classNameId, long classPK,
		boolean primary) {

		// Check to make sure there isn't another emailAddress with the same
		// company id, class name, and class pk that also has primary set to
		// true

		if (primary) {
			List<EmailAddress> emailAddresses =
				emailAddressPersistence.findByC_C_C_P(
					companyId, classNameId, classPK, primary);

			for (EmailAddress emailAddress : emailAddresses) {
				if ((emailAddressId > 0) &&
					(emailAddress.getEmailAddressId() == emailAddressId)) {

					continue;
				}

				emailAddress.setPrimary(false);

				emailAddressPersistence.update(emailAddress);
			}
		}
	}

	protected void validate(
			long emailAddressId, long companyId, long classNameId, long classPK,
			String address, long listTypeId, boolean primary)
		throws PortalException {

		if (!Validator.isEmailAddress(address)) {
			throw new EmailAddressException();
		}

		if (emailAddressId > 0) {
			EmailAddress emailAddress =
				emailAddressPersistence.findByPrimaryKey(emailAddressId);

			companyId = emailAddress.getCompanyId();
			classNameId = emailAddress.getClassNameId();
			classPK = emailAddress.getClassPK();
		}

		_listTypeLocalService.validate(
			listTypeId, classNameId, ListTypeConstants.EMAIL_ADDRESS);

		validate(emailAddressId, companyId, classNameId, classPK, primary);
	}

	@BeanReference(type = ClassNameLocalService.class)
	private ClassNameLocalService _classNameLocalService;

	@BeanReference(type = ListTypeLocalService.class)
	private ListTypeLocalService _listTypeLocalService;

	@BeanReference(type = UserPersistence.class)
	private UserPersistence _userPersistence;

}