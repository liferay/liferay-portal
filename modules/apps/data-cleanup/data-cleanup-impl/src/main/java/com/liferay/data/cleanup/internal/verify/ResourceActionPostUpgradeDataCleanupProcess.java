/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.cleanup.internal.verify;

import com.liferay.data.cleanup.internal.verify.util.PostUpgradeDataCleanupProcessUtil;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectPortletKeys;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.upgrade.data.cleanup.util.DataCleanupLoggingUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Luis Ortiz
 */
public class ResourceActionPostUpgradeDataCleanupProcess
	implements PostUpgradeDataCleanupProcess {

	public ResourceActionPostUpgradeDataCleanupProcess(
		CompanyLocalService companyLocalService, Connection connection,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		ResourceActionLocalService resourceActionLocalService) {

		_companyLocalService = companyLocalService;
		_connection = connection;
		_objectDefinitionLocalService = objectDefinitionLocalService;
		_resourceActionLocalService = resourceActionLocalService;

		_dbInspector = new DBInspector(connection);
	}

	@Override
	public void cleanUp() throws Exception {
		if (!PostUpgradeDataCleanupProcessUtil.isEveryLiferayBundleActive()) {
			if (_log.isWarnEnabled() && CompanyThreadLocal.isDefaultCompany()) {
				_log.warn(
					StringBundler.concat(
						ResourceActionPostUpgradeDataCleanupProcess.class.
							getSimpleName(),
						" cannot be executed because there are modules that ",
						"are inactive"));
			}

			return;
		}

		Set<String> modelNames = new HashSet<>(
			ResourceActionsUtil.getModelNames());
		Set<String> portletNames = new HashSet<>(
			ResourceActionsUtil.getPortletNames());

		try (PreparedStatement preparedStatement1 =
				_connection.prepareStatement(
					"select distinct name from ResourceAction");
			PreparedStatement preparedStatement2 = _connection.prepareStatement(
				"select 1 from ResourcePermission where name = ?");
			ResultSet resultSet1 = preparedStatement1.executeQuery()) {

			while (resultSet1.next()) {
				String name = resultSet1.getString("name");

				if (Validator.isNull(name) ||
					(!name.startsWith("com.liferay.") &&
					 !name.startsWith("com_liferay_"))) {

					continue;
				}

				if (StringUtil.startsWith(
						name,
						ObjectDefinitionConstants.
							CLASS_NAME_PREFIX_CUSTOM_OBJECT_DEFINITION) ||
					StringUtil.startsWith(
						name,
						_RESOURCE_NAME_PREFIX_OBJECT_DEFINITION_PORTLET_ID) ||
					StringUtil.startsWith(
						name, _RESOURCE_NAME_PREFIX_OBJECT_DEFINITION)) {

					if (_hasObjectDefinition(name)) {
						continue;
					}
				}
				else if (modelNames.contains(name) ||
						 portletNames.contains(name)) {

					continue;
				}

				preparedStatement2.setString(1, name);

				try (ResultSet resultSet2 = preparedStatement2.executeQuery()) {
					if (resultSet2.next() && _log.isDebugEnabled()) {
						_log.debug(
							StringBundler.concat(
								"Resource action ", name,
								" is not defined in any deployed module but ",
								"is referenced in ",
								_dbInspector.normalizeName(
									"ResourcePermission"),
								" table"));

						continue;
					}
				}

				List<ResourceAction> resourceActions =
					_resourceActionLocalService.getResourceActions(name);

				for (ResourceAction resourceAction : resourceActions) {
					_resourceActionLocalService.deleteResourceAction(
						resourceAction);
				}

				DataCleanupLoggingUtil.logDelete(
					_log, resourceActions.size(),
					_dbInspector.normalizeName("ResourceAction"),
					StringBundler.concat(
						"'", name,
						"' is not defined in any deployed module and is not ",
						"in use"));
			}
		}
	}

	private boolean _hasObjectDefinition(String name) throws Exception {
		if (StringUtil.startsWith(
				name, _RESOURCE_NAME_PREFIX_OBJECT_DEFINITION)) {

			ObjectDefinition objectDefinition =
				_objectDefinitionLocalService.fetchObjectDefinition(
					GetterUtil.getLong(
						StringUtil.removeSubstring(
							name, _RESOURCE_NAME_PREFIX_OBJECT_DEFINITION)));

			if (objectDefinition != null) {
				return true;
			}

			return false;
		}

		String className = StringUtil.replaceFirst(
			name, _RESOURCE_NAME_PREFIX_OBJECT_DEFINITION_PORTLET_ID,
			ObjectDefinitionConstants.
				CLASS_NAME_PREFIX_CUSTOM_OBJECT_DEFINITION);

		AtomicReference<ObjectDefinition> objectDefinition =
			new AtomicReference<>();

		_companyLocalService.forEachCompanyId(
			companyId -> {
				if (objectDefinition.get() != null) {
					return;
				}

				objectDefinition.set(
					_objectDefinitionLocalService.
						fetchObjectDefinitionByClassName(companyId, className));
			});

		if (objectDefinition.get() != null) {
			return true;
		}

		return false;
	}

	private static final String _RESOURCE_NAME_PREFIX_OBJECT_DEFINITION =
		"com.liferay.object#";

	private static final String
		_RESOURCE_NAME_PREFIX_OBJECT_DEFINITION_PORTLET_ID =
			ObjectPortletKeys.OBJECT_DEFINITIONS + StringPool.UNDERLINE;

	private static final Log _log = LogFactoryUtil.getLog(
		ResourceActionPostUpgradeDataCleanupProcess.class);

	private final CompanyLocalService _companyLocalService;
	private final Connection _connection;
	private final DBInspector _dbInspector;
	private final ObjectDefinitionLocalService _objectDefinitionLocalService;
	private final ResourceActionLocalService _resourceActionLocalService;

}