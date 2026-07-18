/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.impl;

import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.EmailAddress;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.service.base.EmailAddressServiceBaseImpl;
import com.liferay.portal.service.permission.CommonPermissionUtil;

import java.util.List;
import java.util.Objects;

/**
 * @author Brian Wing Shun Chan
 * @author Alexander Chow
 */
public class EmailAddressServiceImpl extends EmailAddressServiceBaseImpl {

	@Override
	public EmailAddress addEmailAddress(
			String externalReferenceCode, String className, long classPK,
			String address, long typeId, boolean primary,
			ServiceContext serviceContext)
		throws PortalException {

		String actionId = ActionKeys.UPDATE;

		if (Objects.equals(
				className, "com.liferay.account.model.AccountEntry")) {

			actionId = "MANAGE_ADDRESSES";
		}

		CommonPermissionUtil.check(
			getPermissionChecker(), className, classPK, actionId);

		return emailAddressLocalService.addEmailAddress(
			externalReferenceCode, getUserId(), className, classPK, address,
			typeId, primary, serviceContext);
	}

	@Override
	public void deleteEmailAddress(long emailAddressId) throws PortalException {
		EmailAddress emailAddress = emailAddressPersistence.findByPrimaryKey(
			emailAddressId);

		String actionId = ActionKeys.UPDATE;

		if (Objects.equals(
				emailAddress.getClassName(),
				"com.liferay.account.model.AccountEntry")) {

			actionId = "MANAGE_ADDRESSES";
		}

		CommonPermissionUtil.check(
			getPermissionChecker(), emailAddress.getClassNameId(),
			emailAddress.getClassPK(), actionId);

		emailAddressLocalService.deleteEmailAddress(emailAddress);
	}

	/**
	 * Returns the email address with the primary key.
	 *
	 * @param  emailAddressId the primary key of the email address
	 * @return the email address with the primary key, or <code>null</code> if
	 *         an email address with the primary key could not be found or if
	 *         the user did not have permission to view the email address
	 */
	@Override
	public EmailAddress fetchEmailAddress(long emailAddressId)
		throws PortalException {

		EmailAddress emailAddress = emailAddressPersistence.fetchByPrimaryKey(
			emailAddressId);

		if (emailAddress != null) {
			CommonPermissionUtil.check(
				getPermissionChecker(), emailAddress.getClassNameId(),
				emailAddress.getClassPK(), ActionKeys.VIEW);
		}

		return emailAddress;
	}

	@Override
	public EmailAddress fetchEmailAddressByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		EmailAddress emailAddress = emailAddressPersistence.fetchByERC_C(
			externalReferenceCode, companyId);

		if (emailAddress != null) {
			CommonPermissionUtil.check(
				getPermissionChecker(), emailAddress.getClassNameId(),
				emailAddress.getClassPK(), ActionKeys.VIEW);
		}

		return emailAddress;
	}

	@Override
	public EmailAddress getEmailAddress(long emailAddressId)
		throws PortalException {

		EmailAddress emailAddress = emailAddressPersistence.findByPrimaryKey(
			emailAddressId);

		CommonPermissionUtil.check(
			getPermissionChecker(), emailAddress.getClassNameId(),
			emailAddress.getClassPK(), ActionKeys.VIEW);

		return emailAddress;
	}

	@Override
	public List<EmailAddress> getEmailAddresses(String className, long classPK)
		throws PortalException {

		CommonPermissionUtil.check(
			getPermissionChecker(), className, classPK, ActionKeys.VIEW);

		User user = getUser();

		return emailAddressPersistence.findByC_C_C(
			user.getCompanyId(),
			_classNameLocalService.getClassNameId(className), classPK);
	}

	@Override
	public EmailAddress updateEmailAddress(
			String externalReferenceCode, long emailAddressId, String address,
			long typeId, boolean primary)
		throws PortalException {

		EmailAddress emailAddress = emailAddressPersistence.findByPrimaryKey(
			emailAddressId);

		String actionId = ActionKeys.UPDATE;

		if (Objects.equals(
				emailAddress.getClassName(),
				"com.liferay.account.model.AccountEntry")) {

			actionId = "MANAGE_ADDRESSES";
		}

		CommonPermissionUtil.check(
			getPermissionChecker(), emailAddress.getClassNameId(),
			emailAddress.getClassPK(), actionId);

		return emailAddressLocalService.updateEmailAddress(
			externalReferenceCode, emailAddressId, address, typeId, primary);
	}

	@BeanReference(type = ClassNameLocalService.class)
	private ClassNameLocalService _classNameLocalService;

}