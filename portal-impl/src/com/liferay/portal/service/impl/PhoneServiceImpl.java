/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.impl;

import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Phone;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.service.base.PhoneServiceBaseImpl;
import com.liferay.portal.service.permission.CommonPermissionUtil;

import java.util.List;
import java.util.Objects;

/**
 * @author Brian Wing Shun Chan
 */
public class PhoneServiceImpl extends PhoneServiceBaseImpl {

	@Override
	public Phone addPhone(
			String externalReferenceCode, String className, long classPK,
			String number, String extension, long typeId, boolean primary,
			ServiceContext serviceContext)
		throws PortalException {

		String actionId = ActionKeys.UPDATE;

		if (Objects.equals(
				className, "com.liferay.account.model.AccountEntry")) {

			actionId = "MANAGE_ADDRESSES";
		}

		CommonPermissionUtil.check(
			getPermissionChecker(), className, classPK, actionId);

		return phoneLocalService.addPhone(
			externalReferenceCode, getUserId(), className, classPK, number,
			extension, typeId, primary, serviceContext);
	}

	@Override
	public void deletePhone(long phoneId) throws PortalException {
		Phone phone = phonePersistence.findByPrimaryKey(phoneId);

		String actionId = ActionKeys.UPDATE;

		if (Objects.equals(
				phone.getClassName(),
				"com.liferay.account.model.AccountEntry")) {

			actionId = "MANAGE_ADDRESSES";
		}

		CommonPermissionUtil.check(
			getPermissionChecker(), phone.getClassNameId(), phone.getClassPK(),
			actionId);

		phoneLocalService.deletePhone(phone);
	}

	@Override
	public Phone fetchPhoneByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		Phone phone = phonePersistence.fetchByERC_C(
			externalReferenceCode, companyId);

		if (phone != null) {
			CommonPermissionUtil.check(
				getPermissionChecker(), phone.getClassNameId(),
				phone.getClassPK(), ActionKeys.VIEW);
		}

		return phone;
	}

	@Override
	public Phone getPhone(long phoneId) throws PortalException {
		Phone phone = phonePersistence.findByPrimaryKey(phoneId);

		CommonPermissionUtil.check(
			getPermissionChecker(), phone.getClassNameId(), phone.getClassPK(),
			ActionKeys.VIEW);

		return phone;
	}

	@Override
	public Phone getPhoneByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		Phone phone = phonePersistence.findByERC_C(
			externalReferenceCode, companyId);

		CommonPermissionUtil.check(
			getPermissionChecker(), phone.getClassNameId(), phone.getClassPK(),
			ActionKeys.VIEW);

		return phone;
	}

	@Override
	public List<Phone> getPhones(String className, long classPK)
		throws PortalException {

		CommonPermissionUtil.check(
			getPermissionChecker(), className, classPK, ActionKeys.VIEW);

		User user = getUser();

		return phonePersistence.findByC_C_C(
			user.getCompanyId(),
			_classNameLocalService.getClassNameId(className), classPK);
	}

	@Override
	public Phone updatePhone(
			String externalReferenceCode, long phoneId, String number,
			String extension, long typeId, boolean primary)
		throws PortalException {

		Phone phone = phonePersistence.findByPrimaryKey(phoneId);

		String actionId = ActionKeys.UPDATE;

		if (Objects.equals(
				phone.getClassName(),
				"com.liferay.account.model.AccountEntry")) {

			actionId = "MANAGE_ADDRESSES";
		}

		CommonPermissionUtil.check(
			getPermissionChecker(), phone.getClassNameId(), phone.getClassPK(),
			actionId);

		return phoneLocalService.updatePhone(
			externalReferenceCode, phoneId, number, extension, typeId, primary);
	}

	@BeanReference(type = ClassNameLocalService.class)
	private ClassNameLocalService _classNameLocalService;

}