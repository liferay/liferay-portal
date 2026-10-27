/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.dispatch.executor;

import com.liferay.dispatch.executor.BaseDispatchTaskExecutor;
import com.liferay.dispatch.executor.DispatchTaskExecutor;
import com.liferay.dispatch.executor.DispatchTaskExecutorOutput;
import com.liferay.dispatch.model.DispatchTrigger;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.site.pim.site.initializer.connector.PIMConnector;
import com.liferay.site.pim.site.initializer.connector.PIMConnectorRegistry;
import com.liferay.site.pim.site.initializer.exception.PIMConnectorException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stefano Motta
 */
@Component(
	property = {
		"dispatch.task.executor.name=" + PIMConnectorDispatchTaskExecutor.KEY,
		"dispatch.task.executor.type=" + PIMConnectorDispatchTaskExecutor.KEY
	},
	service = DispatchTaskExecutor.class
)
public class PIMConnectorDispatchTaskExecutor extends BaseDispatchTaskExecutor {

	public static final String KEY = "pim-connector";

	@Override
	public void doExecute(
			DispatchTrigger dispatchTrigger,
			DispatchTaskExecutorOutput dispatchTaskExecutorOutput)
		throws Exception {

		String originalName = PrincipalThreadLocal.getName();
		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		User user = _userLocalService.getUser(dispatchTrigger.getUserId());

		try {
			PermissionThreadLocal.setPermissionChecker(
				_permissionCheckerFactory.create(user));
			PrincipalThreadLocal.setName(user.getUserId());

			UnicodeProperties unicodeProperties =
				dispatchTrigger.getDispatchTaskSettingsUnicodeProperties();

			ObjectEntry objectEntry = _objectEntryLocalService.getObjectEntry(
				GetterUtil.getLong(
					unicodeProperties.getProperty(
						"pimConnectorObjectEntryId")));

			if (!MapUtil.getBoolean(objectEntry.getValues(), "active")) {
				dispatchTaskExecutorOutput.setOutput(
					"PIM connector is inactive");

				return;
			}

			PIMConnector pimConnector = _pimConnectorRegistry.getPIMConnector(
				MapUtil.getString(objectEntry.getValues(), "key"));

			if (pimConnector == null) {
				throw new PIMConnectorException(
					"unable-to-get-a-pim-connector-with-the-given-key");
			}

			dispatchTaskExecutorOutput.setOutput(
				pimConnector.execute(objectEntry));
		}
		catch (PIMConnectorException pimConnectorException) {
			if (Validator.isNotNull(pimConnectorException.getOutput())) {
				dispatchTaskExecutorOutput.setOutput(
					pimConnectorException.getOutput());
			}

			dispatchTaskExecutorOutput.setError(
				_language.get(
					user.getLocale(), pimConnectorException.getMessage()));

			throw pimConnectorException;
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(
				originalPermissionChecker);
			PrincipalThreadLocal.setName(originalName);
		}
	}

	@Override
	public String getName() {
		return KEY;
	}

	@Override
	public boolean isClusterModeSingle() {
		return true;
	}

	@Override
	public boolean isHiddenInUI() {
		return true;
	}

	@Reference
	private Language _language;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private PermissionCheckerFactory _permissionCheckerFactory;

	@Reference
	private PIMConnectorRegistry _pimConnectorRegistry;

	@Reference
	private UserLocalService _userLocalService;

}