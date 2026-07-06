/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.ldap.internal.model.listener;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.transaction.TransactionCallbackUtil;
import com.liferay.portal.security.exportimport.UserExporter;
import com.liferay.portal.security.exportimport.UserOperation;
import com.liferay.portal.security.ldap.internal.UserImportTransactionThreadLocal;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Marcellus Tavares
 */
@Component(service = ModelListener.class)
public class UserGroupModelListener extends BaseModelListener<UserGroup> {

	@Override
	public void onAfterAddAssociation(
		Object userGroupId, String associationClassName,
		Object associationClassPK) {

		try {
			if (associationClassName.equals(User.class.getName())) {
				exportToLDAP(
					(Long)associationClassPK, (Long)userGroupId,
					UserOperation.ADD);
			}
		}
		catch (Exception exception) {
			_log.error(
				"Unable to export user group with user ID " +
					associationClassPK + " to LDAP on after add association",
				exception);
		}
	}

	@Override
	public void onAfterRemoveAssociation(
		Object userGroupId, String associationClassName,
		Object associationClassPK) {

		try {
			if (associationClassName.equals(User.class.getName())) {
				exportToLDAP(
					(Long)associationClassPK, (Long)userGroupId,
					UserOperation.REMOVE);
			}
		}
		catch (Exception exception) {
			_log.error(
				"Unable to export user group with user ID " +
					associationClassPK + " to LDAP on after remove association",
				exception);
		}
	}

	protected void exportToLDAP(
		long userId, long userGroupId, UserOperation userOperation) {

		if (UserImportTransactionThreadLocal.isOriginatesFromImport()) {
			return;
		}

		TransactionCallbackUtil.registerCommitCallback(
			CallableUtil.getCallable(
				expandoBridgeAttributes -> {
					if ((_userLocalService.fetchUser(userId) == null) ||
						(_userGroupLocalService.fetchUserGroup(userGroupId) ==
							null)) {

						return;
					}

					try {
						_userExporter.exportUser(
							userId, userGroupId, userOperation);
					}
					catch (Exception exception) {
						if (_log.isWarnEnabled()) {
							_log.warn(exception);
						}
					}

					if (_log.isDebugEnabled()) {
						if (_log.isDebugEnabled()) {
							StringBundler.concat(
								"Exporting user ", userId, " to user group ",
								userGroupId, " with user operation ",
								userOperation.name());
						}
					}
				}));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UserGroupModelListener.class);

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY
	)
	private volatile UserExporter _userExporter;

	@Reference
	private UserGroupLocalService _userGroupLocalService;

	@Reference
	private UserLocalService _userLocalService;

}