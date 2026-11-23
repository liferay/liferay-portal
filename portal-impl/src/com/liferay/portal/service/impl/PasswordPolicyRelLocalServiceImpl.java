/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.impl;

import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.exception.NoSuchPasswordPolicyRelException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.PasswordPolicyRel;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.service.base.PasswordPolicyRelLocalServiceBaseImpl;

import java.util.List;

/**
 * @author Scott Lee
 * @author Shuyang Zhou
 */
public class PasswordPolicyRelLocalServiceImpl
	extends PasswordPolicyRelLocalServiceBaseImpl {

	@Override
	public PasswordPolicyRel addPasswordPolicyRel(
		long passwordPolicyId, String className, long classPK) {

		long classNameId = _classNameLocalService.getClassNameId(className);

		PasswordPolicyRel passwordPolicyRel =
			passwordPolicyRelPersistence.fetchByC_C(classNameId, classPK);

		if (passwordPolicyRel != null) {
			if (passwordPolicyRel.getPasswordPolicyId() == passwordPolicyId) {
				return null;
			}

			passwordPolicyRelPersistence.remove(passwordPolicyRel);
		}

		long passwordPolicyRelId = counterLocalService.increment();

		passwordPolicyRel = passwordPolicyRelPersistence.create(
			passwordPolicyRelId);

		passwordPolicyRel.setPasswordPolicyId(passwordPolicyId);
		passwordPolicyRel.setClassNameId(classNameId);
		passwordPolicyRel.setClassPK(classPK);

		return passwordPolicyRelPersistence.update(passwordPolicyRel);
	}

	@Override
	public void addPasswordPolicyRels(
		long passwordPolicyId, String className, long[] classPKs) {

		for (long classPK : classPKs) {
			addPasswordPolicyRel(passwordPolicyId, className, classPK);
		}
	}

	@Override
	public void deletePasswordPolicyRel(
		long passwordPolicyId, String className, long classPK) {

		PasswordPolicyRel passwordPolicyRel =
			passwordPolicyRelPersistence.fetchByC_C(
				_classNameLocalService.getClassNameId(className), classPK);

		if ((passwordPolicyRel != null) &&
			(passwordPolicyRel.getPasswordPolicyId() == passwordPolicyId)) {

			passwordPolicyRelPersistence.remove(passwordPolicyRel);
		}
	}

	@Override
	public void deletePasswordPolicyRel(String className, long classPK) {
		try {
			PasswordPolicyRel passwordPolicyRel =
				passwordPolicyRelPersistence.findByC_C(
					_classNameLocalService.getClassNameId(className), classPK);

			deletePasswordPolicyRel(passwordPolicyRel);
		}
		catch (NoSuchPasswordPolicyRelException
					noSuchPasswordPolicyRelException) {

			// LPS-52675

			if (_log.isDebugEnabled()) {
				_log.debug(noSuchPasswordPolicyRelException);
			}
		}
	}

	@Override
	public void deletePasswordPolicyRels(long passwordPolicyId) {
		List<PasswordPolicyRel> passwordPolicyRels =
			passwordPolicyRelPersistence.findByPasswordPolicyId(
				passwordPolicyId);

		for (PasswordPolicyRel passwordPolicyRel : passwordPolicyRels) {
			deletePasswordPolicyRel(passwordPolicyRel);
		}
	}

	@Override
	public void deletePasswordPolicyRels(
		long passwordPolicyId, String className, long[] classPKs) {

		for (long classPK : classPKs) {
			deletePasswordPolicyRel(passwordPolicyId, className, classPK);
		}
	}

	@Override
	public PasswordPolicyRel fetchPasswordPolicyRel(
		String className, long classPK) {

		return passwordPolicyRelPersistence.fetchByC_C(
			_classNameLocalService.getClassNameId(className), classPK);
	}

	@Override
	public boolean hasPasswordPolicyRel(
		long passwordPolicyId, String className, long classPK) {

		PasswordPolicyRel passwordPolicyRel =
			passwordPolicyRelPersistence.fetchByC_C(
				_classNameLocalService.getClassNameId(className), classPK);

		if ((passwordPolicyRel != null) &&
			(passwordPolicyRel.getPasswordPolicyId() == passwordPolicyId)) {

			return true;
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PasswordPolicyRelLocalServiceImpl.class);

	@BeanReference(type = ClassNameLocalService.class)
	private ClassNameLocalService _classNameLocalService;

}