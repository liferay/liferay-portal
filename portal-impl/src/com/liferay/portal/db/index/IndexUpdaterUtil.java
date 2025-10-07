/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.db.index;

import com.liferay.petra.concurrent.DCLSingleton;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.db.DBResourceUtil;
import com.liferay.portal.events.StartupHelperUtil;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DuplicateUniqueFinderRowsCleaner;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.dependency.manager.DependencyManagerSyncUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.model.ReleaseConstants;
import com.liferay.portal.kernel.module.util.BundleUtil;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.service.ReleaseLocalServiceUtil;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.sql.Connection;
import java.sql.SQLException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.BundleTrackerCustomizer;

/**
 * @author Ricardo Couso
 */
public class IndexUpdaterUtil {

	public static void updateAllIndexes() {
		LoggingTimer loggingTimer = new LoggingTimer(
			"Updating database indexes");

		if (!_processedServletContextNames.contains("portal")) {
			try {
				_addUpdateIndexesFutures(
					"portal", DBResourceUtil.getPortalTablesSQL(),
					DBResourceUtil.getPortalIndexesSQL());
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(exception);
				}
			}
		}

		BundleTracker<Void> bundleTracker = new BundleTracker<>(
			SystemBundleUtil.getBundleContext(), Bundle.ACTIVE,
			new BundleTrackerCustomizer<Void>() {

				@Override
				public Void addingBundle(
					Bundle bundle, BundleEvent bundleEvent) {

					if (BundleUtil.isLiferayRequireSchemaVersionBundle(
							bundle) ||
						BundleUtil.isLiferayServiceBundle(bundle)) {

						try {
							if (!_processedServletContextNames.contains(
									bundle.getSymbolicName()) &&
								!_isSkipUpdateIndexes(
									bundle.getSymbolicName())) {

								_addUpdateIndexesFutures(
									bundle.getSymbolicName(),
									DBResourceUtil.getModuleTablesSQL(bundle),
									DBResourceUtil.getModuleIndexesSQL(bundle));
							}
						}
						catch (Exception exception) {
							_log.error(exception);
						}
					}

					return null;
				}

				@Override
				public void modifiedBundle(
					Bundle bundle, BundleEvent bundleEvent, Void tracked) {
				}

				@Override
				public void removedBundle(
					Bundle bundle, BundleEvent bundleEvent, Void tracked) {
				}

			});

		DependencyManagerSyncUtil.registerSyncFutureTask(
			new FutureTask<>(
				() -> {
					try {
						PrimaryKeyUpdaterUtil.updateAllPrimaryKeys();
					}
					catch (Exception exception) {
						_log.error(exception);
					}

					bundleTracker.open();

					DependencyManagerSyncUtil.registerSyncCallable(
						() -> {
							bundleTracker.close();

							_clearProcessedServletContextNames();

							_awaitFuturesTermination();

							loggingTimer.close();

							return null;
						});

					return null;
				}),
			IndexUpdaterUtil.class.getName() + "-BundleTrackerOpener");
	}

	public static void updateIndexes(Bundle bundle) {
		try (LoggingTimer loggingTimer = new LoggingTimer(
				"Updating database indexes for " + bundle.getSymbolicName())) {

			_addUpdateIndexesFutures(
				bundle.getSymbolicName(),
				DBResourceUtil.getModuleTablesSQL(bundle),
				DBResourceUtil.getModuleIndexesSQL(bundle));

			_awaitFuturesTermination();
		}
	}

	public static void updatePortalIndexes() {
		LoggingTimer loggingTimer = new LoggingTimer(
			"Updating database indexes for portal");

		try {
			_addUpdateIndexesFutures(
				"portal", DBResourceUtil.getPortalTablesSQL(),
				DBResourceUtil.getPortalIndexesSQL());
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}
		}
		finally {
			_awaitFuturesTermination();

			loggingTimer.close();
		}
	}

	private static void _addUpdateIndexesFutures(
		String servletContextName, String tablesSQL, String indexesSQL) {

		_processedServletContextNames.add(servletContextName);

		if ((indexesSQL == null) || (tablesSQL == null)) {
			return;
		}

		ExecutorService executorService = _getExecutorService();

		Map<String, String> tableIndexesSQLMap = _getTableIndexesSQLMap(
			tablesSQL, indexesSQL);

		for (Map.Entry<String, String> entry : tableIndexesSQLMap.entrySet()) {
			_futures.add(
				executorService.submit(
					() -> {
						try {
							_updateIndexes(entry.getKey(), entry.getValue());
						}
						catch (Exception exception) {
							throw new RuntimeException(exception);
						}
					}));
		}
	}

	private static void _awaitFuturesTermination() {
		for (Future<?> future : _futures) {
			try {
				future.get();
			}
			catch (Exception exception) {
				_log.error(exception);
			}
		}

		_futures.clear();
	}

	private static void _clearProcessedServletContextNames() {
		_processedServletContextNames.clear();
	}

	private static void _deleteDuplicates(
			Connection connection, DB db, String tableName, String indexesSQL)
		throws Exception {

		Matcher matcher = _uniqueIndexPattern.matcher(indexesSQL);

		DBInspector dbInspector = new DBInspector(connection);

		while (matcher.find()) {
			if (dbInspector.hasIndex(tableName, matcher.group(1))) {
				continue;
			}

			String indexColumns = matcher.group(2);
			String orderByColumns = StringUtil.merge(
				db.getPrimaryKeyColumnNames(connection, tableName),
				StringPool.COMMA_AND_SPACE);

			DuplicateUniqueFinderRowsCleaner duplicateUniqueFinderRowsCleaner =
				new DuplicateUniqueFinderRowsCleaner(
					connection, tableName,
					StringUtil.split(
						indexColumns.replaceAll(
							"\\[\\$COLUMN_LENGTH:(\\d+)\\$\\]",
							StringPool.BLANK),
						StringPool.COMMA_AND_SPACE),
					orderByColumns + " asc");

			duplicateUniqueFinderRowsCleaner.deleteDuplicates();
		}
	}

	private static ExecutorService _getExecutorService() {
		return _executorServiceDCLSingleton.getSingleton(
			() -> {
				Runtime runtime = Runtime.getRuntime();

				return Executors.newFixedThreadPool(
					runtime.availableProcessors());
			});
	}

	private static Map<String, String> _getTableIndexesSQLMap(
		String tablesSQL, String indexesSQL) {

		Map<String, String> indexesSQLMap = new LinkedHashMap<>();

		String[] indexesSQLArray = StringUtil.split(indexesSQL, "\n\n");

		for (String element : indexesSQLArray) {
			String tableName = element.substring(
				element.indexOf("on ") + 3, element.indexOf(" ("));

			indexesSQLMap.put(tableName, element);
		}

		String[] tablesSQLArray = StringUtil.split(tablesSQL, "\n\n");

		for (String element : tablesSQLArray) {
			String tableName = element.substring(
				element.indexOf("create table ") + 13, element.indexOf(" ("));

			if (!indexesSQLMap.containsKey(tableName)) {
				indexesSQLMap.put(tableName, StringPool.BLANK);
			}
		}

		return indexesSQLMap;
	}

	private static boolean _isSkipUpdateIndexes(String bundleSymbolicName) {
		Release release = ReleaseLocalServiceUtil.fetchRelease(
			bundleSymbolicName);

		if ((release != null) &&
			(release.getState() == ReleaseConstants.STATE_GOOD)) {

			return false;
		}

		if (_log.isInfoEnabled()) {
			_log.info(
				"Skipped updating database indexes for " + bundleSymbolicName +
					" since it is not upgraded");
		}

		return true;
	}

	private static void _updateIndexes(String tableName, String indexesSQL)
		throws Exception {

		DB db = DBManagerUtil.getDB();

		db.process(
			companyId -> {
				try (Connection connection = DataAccess.getConnection()) {
					try {
						db.updateIndexes(
							connection, tableName, indexesSQL, true);
					}
					catch (SQLException sqlException) {
						if (!StartupHelperUtil.isUpgrading() ||
							!indexesSQL.contains("unique index")) {

							throw sqlException;
						}

						_deleteDuplicates(
							connection, db, tableName, indexesSQL);

						db.updateIndexes(
							connection, tableName, indexesSQL, true);
					}
				}
				catch (Exception exception) {
					String message = new String(
						"Unable to update database indexes for " + tableName);

					if (Validator.isNotNull(companyId)) {
						message += " and company " + companyId;
					}

					_log.error(message + " due to " + exception.getMessage());
				}
			});
	}

	private static final Log _log = LogFactoryUtil.getLog(
		IndexUpdaterUtil.class);

	private static final DCLSingleton<ExecutorService>
		_executorServiceDCLSingleton = new DCLSingleton<>();
	private static final List<Future<?>> _futures =
		new CopyOnWriteArrayList<>();
	private static final Set<String> _processedServletContextNames =
		ConcurrentHashMap.newKeySet();
	private static final Pattern _uniqueIndexPattern = Pattern.compile(
		"create\\s+unique\\s+index\\s+(\\w+)\\s+on\\s+\\w+\\s*\\(([^)]+)\\)");

}