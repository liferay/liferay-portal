/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.ldap.internal.model.listener;

import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PasswordModificationThreadLocal;
import com.liferay.portal.kernel.security.ldap.LDAPSettings;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.transaction.TransactionCommitCallbackUtil;
import com.liferay.portal.security.exportimport.UserExporter;
import com.liferay.portal.security.ldap.internal.UserImportTransactionThreadLocal;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * @author Minhchau Dang
 */
public abstract class BaseLDAPExportModelListener<T extends BaseModel<T>>
	extends BaseModelListener<T> {

	protected void exportToLDAP(
			User user, UserExporter userExporter, LDAPSettings ldapSettings)
		throws Exception {

		if ((user == null) || user.isGuestUser() ||
			UserImportTransactionThreadLocal.isOriginatesFromImport()) {

			return;
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Callable<Void> callable = () -> {
			Map<String, Serializable> expandoBridgeAttributes = null;

			if (serviceContext != null) {
				expandoBridgeAttributes =
					serviceContext.getExpandoBridgeAttributes();
			}

			boolean oldPasswordModified =
				PasswordModificationThreadLocal.isPasswordModified();

			if (oldPasswordModified) {
				String lastPasswordUnencrypted = _lastPasswordUnencrypted.get();
				String newPasswordUnencrypted =
					PasswordModificationThreadLocal.getPasswordUnencrypted();

				boolean newPasswordModified = !Objects.equals(
					lastPasswordUnencrypted, newPasswordUnencrypted);

				_lastPasswordUnencrypted.set(newPasswordUnencrypted);

				PasswordModificationThreadLocal.setPasswordModified(
					newPasswordModified);
			}

			try {
				userExporter.exportUser(user, expandoBridgeAttributes);
			}
			finally {
				PasswordModificationThreadLocal.setPasswordModified(
					oldPasswordModified);
			}

			return null;
		};

		if (ldapSettings.isPasswordPolicyEnabled(user.getCompanyId()) &&
			PasswordModificationThreadLocal.isPasswordModified() &&
			(user.getLdapServerId() > 0)) {

			callable.call();
		}
		else {
			TransactionCommitCallbackUtil.registerCallback(callable);
		}
	}

	private static final CentralizedThreadLocal<String>
		_lastPasswordUnencrypted = new CentralizedThreadLocal<>(
			BaseLDAPExportModelListener.class.getName() +
				"._lastPasswordUnencrypted");

}