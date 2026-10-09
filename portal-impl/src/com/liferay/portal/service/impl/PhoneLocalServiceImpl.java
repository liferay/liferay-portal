/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.impl;

import com.liferay.portal.format.util.PhoneNumberFormatUtil;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.exception.PhoneNumberException;
import com.liferay.portal.kernel.exception.PhoneNumberExtensionException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Contact;
import com.liferay.portal.kernel.model.ListTypeConstants;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.Phone;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ListTypeLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.persistence.UserPersistence;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.service.base.PhoneLocalServiceBaseImpl;

import java.util.List;

/**
 * @author Brian Wing Shun Chan
 */
public class PhoneLocalServiceImpl extends PhoneLocalServiceBaseImpl {

	@Override
	public Phone addPhone(
			String externalReferenceCode, long userId, String className,
			long classPK, String number, String extension, long listTypeId,
			boolean primary, ServiceContext serviceContext)
		throws PortalException {

		User user = _userPersistence.findByPrimaryKey(userId);
		long classNameId = _classNameLocalService.getClassNameId(className);

		validate(
			0, user.getCompanyId(), classNameId, classPK, number, extension,
			listTypeId, primary);

		long phoneId = counterLocalService.increment();

		Phone phone = phonePersistence.create(phoneId);

		phone.setUuid(serviceContext.getUuid());
		phone.setExternalReferenceCode(externalReferenceCode);
		phone.setCompanyId(user.getCompanyId());
		phone.setUserId(user.getUserId());
		phone.setUserName(user.getFullName());
		phone.setClassNameId(classNameId);
		phone.setClassPK(classPK);
		phone.setNumber(number);
		phone.setExtension(extension);
		phone.setListTypeId(listTypeId);
		phone.setPrimary(primary);

		return phonePersistence.update(phone);
	}

	@Override
	public Phone deletePhone(long phoneId) throws PortalException {
		Phone phone = phonePersistence.findByPrimaryKey(phoneId);

		return phoneLocalService.deletePhone(phone);
	}

	@Override
	@SystemEvent(
		action = SystemEventConstants.ACTION_SKIP,
		type = SystemEventConstants.TYPE_DELETE
	)
	public Phone deletePhone(Phone phone) {
		phonePersistence.remove(phone);

		return phone;
	}

	@Override
	public void deletePhones(long companyId, String className, long classPK) {
		List<Phone> phones = phonePersistence.findByC_C_C(
			companyId, _classNameLocalService.getClassNameId(className),
			classPK);

		for (Phone phone : phones) {
			phoneLocalService.deletePhone(phone);
		}
	}

	@Override
	public List<Phone> getPhones() {
		return phonePersistence.findAll();
	}

	@Override
	public List<Phone> getPhones(
		long companyId, String className, long classPK) {

		return phonePersistence.findByC_C_C(
			companyId, _classNameLocalService.getClassNameId(className),
			classPK);
	}

	@Override
	public Phone updatePhone(
			String externalReferenceCode, long phoneId, String number,
			String extension, long listTypeId, boolean primary)
		throws PortalException {

		validate(phoneId, 0, 0, 0, number, extension, listTypeId, primary);

		Phone phone = phonePersistence.findByPrimaryKey(phoneId);

		phone.setExternalReferenceCode(externalReferenceCode);
		phone.setNumber(number);
		phone.setExtension(extension);
		phone.setListTypeId(listTypeId);
		phone.setPrimary(primary);

		return phonePersistence.update(phone);
	}

	protected void validate(
		long phoneId, long companyId, long classNameId, long classPK,
		boolean primary) {

		// Check to make sure there isn't another phone with the same company
		// id, class name, and class pk that also has primary set to true

		if (primary) {
			List<Phone> phones = phonePersistence.findByC_C_C_P(
				companyId, classNameId, classPK, primary);

			for (Phone phone : phones) {
				if ((phoneId > 0) && (phone.getPhoneId() == phoneId)) {
					continue;
				}

				phone.setPrimary(false);

				phonePersistence.update(phone);
			}
		}
	}

	protected void validate(
			long phoneId, long companyId, long classNameId, long classPK,
			String number, String extension, long listTypeId, boolean primary)
		throws PortalException {

		if (!PhoneNumberFormatUtil.validate(number)) {
			throw new PhoneNumberException();
		}

		if (Validator.isNotNull(extension)) {
			for (int i = 0; i < extension.length(); i++) {
				if (!Character.isDigit(extension.charAt(i))) {
					throw new PhoneNumberExtensionException();
				}
			}
		}

		if (phoneId > 0) {
			Phone phone = phonePersistence.findByPrimaryKey(phoneId);

			companyId = phone.getCompanyId();
			classNameId = phone.getClassNameId();
			classPK = phone.getClassPK();
		}

		if ((classNameId == _classNameLocalService.getClassNameId(
				Company.class)) ||
			(classNameId == _classNameLocalService.getClassNameId(
				Contact.class)) ||
			(classNameId == _classNameLocalService.getClassNameId(
				Organization.class))) {

			_listTypeLocalService.validate(
				listTypeId, classNameId, ListTypeConstants.PHONE);
		}

		validate(phoneId, companyId, classNameId, classPK, primary);
	}

	@BeanReference(type = ClassNameLocalService.class)
	private ClassNameLocalService _classNameLocalService;

	@BeanReference(type = ListTypeLocalService.class)
	private ListTypeLocalService _listTypeLocalService;

	@BeanReference(type = UserPersistence.class)
	private UserPersistence _userPersistence;

}