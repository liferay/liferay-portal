/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.cleanup.internal.verify;

import com.liferay.data.cleanup.internal.verify.util.PostUpgradeDataCleanupProcessUtil;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.petra.string.CharPool;
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
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.upgrade.data.cleanup.util.DataCleanupLoggingUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicReference;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleWiring;

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
			if (_log.isWarnEnabled() && CompanyThreadLocal.isDefaultCompany()) {
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
		DBInspector dbInspector = new DBInspector(connection);
		_packageNameBundlesMap = _getPackageNameBundlesMap();

		StringBundler sb = new StringBundler();
		List<String> tableNames = new ArrayList<>();

		for (String tableName : dbInspector.getTableNames(null)) {
			if (!dbInspector.hasColumn(tableName, "classNameId") ||
				StringUtil.equalsIgnoreCase(tableName, "ClassName_")) {

				continue;
			}

			if (!tableNames.isEmpty()) {
				sb.append(" union all ");
			}

			tableNames.add(tableName);

			sb.append("select distinct '");
			sb.append(tableName);
			sb.append("' from ");
			sb.append(tableName);
			sb.append(" where classNameId = ?");
		}

		String usedTableNamesSQL = sb.toString();

		List<ClassName> classNames = _classNameLocalService.getClassNames(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS);
		Map<String, Boolean> definedClasses = new HashMap<>();
		Set<String> models = new HashSet<>(ModelHintsUtil.getModels());

		for (ClassName className : classNames) {
			String value = className.getValue();

			if (!value.startsWith("com.liferay.")) {
				continue;
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
					continue;
				}
			}

			int index = value.indexOf(StringPool.DASH);

			if ((index != -1) &&
				StringUtil.startsWith(value, Layout.class.getName())) {

				value = value.substring(0, index);
			}

			if (_isClassDefined(
					bundleContext, definedClasses, models,
					_packageNameBundlesMap, value)) {

				continue;
			}

			Set<String> usedTableNames = new HashSet<>();

			try (PreparedStatement preparedStatement =
					connection.prepareStatement(usedTableNamesSQL)) {

				for (int i = 1; i <= tableNames.size(); i++) {
					preparedStatement.setLong(i, className.getClassNameId());
				}

				try (ResultSet resultSet = preparedStatement.executeQuery()) {
					while (resultSet.next()) {
						usedTableNames.add(
							StringUtil.trim(resultSet.getString(1)));
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
		}
	}

	private Map<String, List<Bundle>> _getPackageNameBundlesMap() {
		if (!CompanyThreadLocal.isDefaultCompany()) {
			return _packageNameBundlesMap;
		}

		Map<String, List<Bundle>> packageNameBundlesMap = new HashMap<>();

		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		for (Bundle bundle : bundleContext.getBundles()) {
			BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);

			if (bundleWiring == null) {
				continue;
			}

			for (BundleCapability bundleCapability :
					bundleWiring.getCapabilities(
						BundleRevision.PACKAGE_NAMESPACE)) {

				Map<String, Object> attributes =
					bundleCapability.getAttributes();

				Object packageName = attributes.get(
					BundleRevision.PACKAGE_NAMESPACE);

				if (packageName == null) {
					continue;
				}

				List<Bundle> bundles = packageNameBundlesMap.computeIfAbsent(
					packageName.toString(), key -> new ArrayList<>());

				bundles.add(bundle);
			}
		}

		return packageNameBundlesMap;
	}

	private boolean _isClassDefined(
		BundleContext bundleContext, Map<String, Boolean> definedClasses,
		Set<String> models, Map<String, List<Bundle>> packageNameBundlesMap,
		String value) {

		for (String currentValue : value.split("[-_]")) {
			if (models.contains(currentValue)) {
				continue;
			}

			Boolean defined = definedClasses.get(currentValue);

			if (defined != null) {
				if (defined) {
					continue;
				}

				return false;
			}

			Set<Bundle> candidateBundles = new LinkedHashSet<>();

			int index = currentValue.lastIndexOf(CharPool.PERIOD);

			if (index != -1) {
				List<Bundle> exportingBundles = packageNameBundlesMap.get(
					currentValue.substring(0, index));

				if (exportingBundles != null) {
					candidateBundles.addAll(exportingBundles);
				}
			}

			Collections.addAll(candidateBundles, bundleContext.getBundles());

			defined = false;

			for (Bundle bundle : candidateBundles) {
				try {
					bundle.loadClass(currentValue);

					defined = true;

					break;
				}
				catch (ClassNotFoundException classNotFoundException) {
					if (_log.isDebugEnabled()) {
						_log.debug(classNotFoundException);
					}
				}
				catch (Exception exception) {
					_log.error(exception);

					return true;
				}
			}

			definedClasses.put(currentValue, defined);

			if (!defined) {
				return false;
			}
		}

		return true;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ClassNamePostUpgradeDataCleanupProcess.class);

	private static Map<String, List<Bundle>> _packageNameBundlesMap;

	private final ClassNameLocalService _classNameLocalService;
	private final CompanyLocalService _companyLocalService;
	private final ObjectDefinitionLocalService _objectDefinitionLocalService;

}