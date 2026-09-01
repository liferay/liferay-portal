/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.verify;

import com.liferay.petra.concurrent.DCLSingleton;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.db.DBResourceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.model.ReleaseConstants;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.ReleaseLocalServiceUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsValues;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * @author Mariano Álvaro Sáiz
 */
public class PostupgradeVerifyDatabaseState extends VerifyProcess {

	@Override
	protected void doVerify() throws Exception {
		Map<String, String> tablesServletContextNames =
			_tablesServletContextNamesDCLSingleton.getSingleton(
				DBResourceUtil::getTablesServletContextNames);

		Set<String> expectedTableNames = _getCaseInsensitiveSet(
			tablesServletContextNames.keySet());

		CompanyLocalServiceUtil.forEachCompanyId(
			companyId -> {
				try {
					expectedTableNames.addAll(
						DBResourceUtil.getNonserviceBuilderTableNames(
							companyId));
				}
				catch (PortalException portalException) {
					_log.error(
						"Unable to get table names for company " + companyId,
						portalException);
				}
			});

		if (expectedTableNames.isEmpty()) {
			return;
		}

		DBInspector dbInspector = new DBInspector(connection);

		Set<String> expectedViewNames = new TreeSet<>(
			String.CASE_INSENSITIVE_ORDER);

		if (!CompanyThreadLocal.isDefaultCompany()) {
			expectedViewNames.addAll(
				dbInspector.getControlTableNames(expectedTableNames));

			expectedTableNames.removeAll(expectedViewNames);
		}

		Set<String> databaseTableNames = _getCaseInsensitiveSet(
			dbInspector.getTableNames(null));

		Set<String> missingTableNames = _asymmetricDifference(
			expectedTableNames, databaseTableNames);

		Set<String> databaseViewNames = _getCaseInsensitiveSet(
			dbInspector.getViewNames(null));

		Set<String> missingViewNames = _asymmetricDifference(
			expectedViewNames, databaseViewNames);

		Map<String, List<String>> messagesMap = new TreeMap<>();

		_addMessages(
			messagesMap, tablesServletContextNames,
			TransformUtil.transform(
				missingTableNames, dbInspector::normalizeName),
			"Missing tables were detected");
		_addMessages(
			messagesMap, tablesServletContextNames, missingViewNames,
			"Missing views were detected");

		Map<String, String> historicalTablesServletContextNames =
			_getHistoricalTablesServletContextNames();

		Set<String> customTableNames = _asymmetricDifference(
			databaseTableNames, expectedTableNames);

		Set<String> staleTableNames = _intersect(
			customTableNames, historicalTablesServletContextNames.keySet());

		customTableNames.removeAll(staleTableNames);

		Set<String> customViewNames = _asymmetricDifference(
			databaseViewNames, expectedViewNames);

		Set<String> staleViewNames = _intersect(
			customViewNames, historicalTablesServletContextNames.keySet());

		customViewNames.removeAll(staleViewNames);

		_addMessages(
			messagesMap, historicalTablesServletContextNames, staleTableNames,
			"Stale tables were detected");
		_addMessages(
			messagesMap, historicalTablesServletContextNames, staleViewNames,
			"Stale views were detected");

		for (Map.Entry<String, List<String>> entry : messagesMap.entrySet()) {
			String servletContextName = entry.getKey();

			if (!servletContextName.isEmpty()) {
				_log.error(_getModuleMessage(servletContextName));
			}

			for (String message : entry.getValue()) {
				_log.error(message);
			}
		}

		if (!_log.isInfoEnabled() ||
			(customTableNames.isEmpty() && customViewNames.isEmpty())) {

			return;
		}

		_log.info(
			StringBundler.concat(
				"Custom tables and views may include tables that an upgrade ",
				"dropped before 2025.Q3, because ServiceComponent history is ",
				"only reliable from that release"));

		if (!customTableNames.isEmpty()) {
			_log.info(
				_getMessage("Custom tables were detected", customTableNames));
		}

		if (!customViewNames.isEmpty()) {
			_log.info(
				_getMessage("Custom views were detected", customViewNames));
		}
	}

	private void _addMessages(
		Map<String, List<String>> messagesMap,
		Map<String, String> servletContextNames, Collection<String> names,
		String prefix) {

		Map<String, Set<String>> namesMap = new TreeMap<>();

		for (String name : names) {
			namesMap.computeIfAbsent(
				GetterUtil.getString(servletContextNames.get(name)),
				servletContextName -> new TreeSet<>()
			).add(
				name
			);
		}

		for (Map.Entry<String, Set<String>> entry : namesMap.entrySet()) {
			messagesMap.computeIfAbsent(
				entry.getKey(), servletContextName -> new ArrayList<>()
			).add(
				_getMessage(prefix, entry.getValue())
			);
		}
	}

	private Set<String> _asymmetricDifference(
		Collection<String> collection1, Collection<String> collection2) {

		Set<String> names = _getCaseInsensitiveSet(collection1);

		names.removeAll(collection2);

		return names;
	}

	private Set<String> _getCaseInsensitiveSet(Collection<String> names) {
		Set<String> caseInsensitiveSet = new TreeSet<>(
			String.CASE_INSENSITIVE_ORDER);

		caseInsensitiveSet.addAll(names);

		return caseInsensitiveSet;
	}

	private Map<String, String> _getHistoricalTablesServletContextNames() {
		return _historicalTablesServletContextNamesDCLSingleton.getSingleton(
			() -> {
				try {
					return DBResourceUtil.
						getHistoricalServiceComponentTablesServletContextNames(
							connection);
				}
				catch (Exception exception) {
					return ReflectionUtil.throwException(exception);
				}
			});
	}

	private String _getMessage(String prefix, Set<String> names) {
		if (PropsValues.DATABASE_PARTITION_ENABLED) {
			prefix = StringBundler.concat(
				prefix, " for company ",
				CompanyThreadLocal.getNonsystemCompanyId());
		}

		return StringBundler.concat(
			prefix, StringPool.COLON, StringPool.SPACE, names);
	}

	private String _getModuleMessage(String servletContextName) {
		String moduleMessage =
			"Module " + servletContextName + StringPool.COLON;

		Release release = ReleaseLocalServiceUtil.fetchRelease(
			servletContextName);

		if ((release == null) ||
			(release.getState() == ReleaseConstants.STATE_GOOD)) {

			return moduleMessage;
		}

		return StringBundler.concat(
			moduleMessage, " release state ",
			_getReleaseStateLabel(release.getState()), ", schema version ",
			release.getSchemaVersion(), ", last modified ",
			release.getModifiedDate());
	}

	private String _getReleaseStateLabel(int state) {
		if (state == ReleaseConstants.STATE_UPGRADE_FAILURE) {
			return "upgrade failure";
		}

		if (state == ReleaseConstants.STATE_VERIFY_FAILURE) {
			return "verify failure";
		}

		return String.valueOf(state);
	}

	private Set<String> _intersect(
		Collection<String> collection1, Collection<String> collection2) {

		Set<String> names = _getCaseInsensitiveSet(collection1);

		names.retainAll(collection2);

		return names;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PostupgradeVerifyDatabaseState.class);

	private static final DCLSingleton<Map<String, String>>
		_historicalTablesServletContextNamesDCLSingleton = new DCLSingleton<>();
	private static final DCLSingleton<Map<String, String>>
		_tablesServletContextNamesDCLSingleton = new DCLSingleton<>();

}