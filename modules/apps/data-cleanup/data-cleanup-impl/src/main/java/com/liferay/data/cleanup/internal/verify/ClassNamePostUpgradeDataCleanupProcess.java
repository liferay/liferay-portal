/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.cleanup.internal.verify;

import com.liferay.data.cleanup.internal.verify.util.PostUpgradeDataCleanupProcessUtil;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.db.BaseDBProcess;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ClassName;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.upgrade.data.cleanup.util.DataCleanupLoggingUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicReference;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * @author Luis Ortiz
 */
public class ClassNamePostUpgradeDataCleanupProcess
	extends BaseDBProcess implements PostUpgradeDataCleanupProcess {

	public ClassNamePostUpgradeDataCleanupProcess(
		ClassNameLocalService classNameLocalService,
		CompanyLocalService companyLocalService, Connection connection,
		ObjectDefinitionLocalService objectDefinitionLocalService) {

		_classNameLocalService = classNameLocalService;
		_companyLocalService = companyLocalService;
		_objectDefinitionLocalService = objectDefinitionLocalService;

		this.connection = connection;
	}

	@Override
	public void cleanUp() throws Exception {
		if (!PostUpgradeDataCleanupProcessUtil.isEveryLiferayBundleResolved()) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						ClassNamePostUpgradeDataCleanupProcess.class.
							getSimpleName(),
						" cannot be executed because there are modules with ",
						"unsatisfied references"));
			}

			return;
		}

		BundleContext bundleContext = SystemBundleUtil.getBundleContext();
		List<ClassName> classNames = _classNameLocalService.getClassNames(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS);
		DBInspector dbInspector = new DBInspector(connection);
		Set<String> models = new HashSet<>(ModelHintsUtil.getModels());

		List<String> tableNames = new ArrayList<>();

		for (String tableName : dbInspector.getTableNames(null)) {
			if (!dbInspector.hasColumn(tableName, "classNameId") ||
				StringUtil.equalsIgnoreCase(tableName, "ClassName_")) {

				continue;
			}

			tableNames.add(tableName);
		}

		UnsafeConsumer<ClassName, Exception> unsafeConsumer = className -> {
			String value = className.getValue();

			if (!value.startsWith("com.liferay.")) {
				return;
			}

			if (StringUtil.startsWith(
					value,
					ObjectDefinitionConstants.
						CLASS_NAME_PREFIX_CUSTOM_OBJECT_DEFINITION)) {

				AtomicReference<ObjectDefinition> objectDefinition =
					new AtomicReference<>();

				String finalValue = value;

				_companyLocalService.forEachCompanyId(
					companyId -> {
						if (objectDefinition.get() != null) {
							return;
						}

						objectDefinition.set(
							_objectDefinitionLocalService.
								fetchObjectDefinitionByClassName(
									companyId, finalValue));
					});

				if (objectDefinition.get() != null) {
					return;
				}
			}

			int index = value.indexOf(StringPool.DASH);

			if ((index != -1) &&
				StringUtil.startsWith(value, Layout.class.getName())) {

				value = value.substring(0, index);
			}

			boolean missingClass = false;

			for (String currentValue : value.split("[-_]")) {
				if (models.contains(currentValue)) {
					continue;
				}

				Class<?> clazz = null;

				for (Bundle bundle : bundleContext.getBundles()) {
					try {
						clazz = bundle.loadClass(currentValue);

						break;
					}
					catch (ClassNotFoundException classNotFoundException) {
						if (_log.isDebugEnabled()) {
							_log.debug(classNotFoundException);
						}
					}
					catch (Exception exception) {
						_log.error(exception);

						return;
					}
				}

				if (clazz == null) {
					missingClass = true;

					break;
				}
			}

			if (!missingClass) {
				return;
			}

			Set<String> usedTableNames = new HashSet<>();

			for (String tableName : tableNames) {
				try (PreparedStatement preparedStatement =
						connection.prepareStatement(
							"select 1 from " + tableName +
								" where classNameId = ?")) {

					preparedStatement.setLong(1, className.getClassNameId());

					try (ResultSet resultSet =
							preparedStatement.executeQuery()) {

						if (resultSet.next()) {
							usedTableNames.add(tableName);
						}
					}
				}
			}

			if (usedTableNames.isEmpty()) {
				_classNameLocalService.deleteClassName(className);

				DataCleanupLoggingUtil.logDelete(
					_log, 1, dbInspector.normalizeName("ClassName_"),
					StringBundler.concat(
						"\"", value,
						"\" is not defined in any deployed module and is not ",
						"in use"));
			}
			else if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Class name ", value,
						" is not defined in any deployed module but is ",
						"referenced in the next tables: ",
						String.join(", ", new TreeSet<>(usedTableNames))));
			}
		};

		for (ClassName className : classNames) {
			unsafeConsumer.accept(className);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ClassNamePostUpgradeDataCleanupProcess.class);

	private final ClassNameLocalService _classNameLocalService;
	private final CompanyLocalService _companyLocalService;
	private final ObjectDefinitionLocalService _objectDefinitionLocalService;

}