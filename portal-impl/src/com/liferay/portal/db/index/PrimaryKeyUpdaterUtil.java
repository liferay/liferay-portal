/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.db.index;

import com.liferay.petra.concurrent.DCLSingleton;
import com.liferay.portal.db.DBResourceUtil;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.framework.ThrowableCollector;
import com.liferay.portal.kernel.module.util.BundleUtil;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Validator;

import java.sql.Connection;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * @author Mariano Álvaro Sáiz
 */
public class PrimaryKeyUpdaterUtil {

	public static void updateAllPrimaryKeys() {
		try (LoggingTimer loggingTimer = new LoggingTimer(
				"Updating database primary keys")) {

			_throwableCollector = new ThrowableCollector();

			_addUpdatePrimaryKeysFutures(
				DBResourceUtil.getPortalTablesPrimaryKeyColumnNames());

			BundleContext bundleContext = SystemBundleUtil.getBundleContext();

			for (Bundle bundle : bundleContext.getBundles()) {
				if (BundleUtil.isLiferayRequireSchemaVersionBundle(bundle) ||
					BundleUtil.isLiferayServiceBundle(bundle)) {

					try {
						_addUpdatePrimaryKeysFutures(
							DBResourceUtil.getModuleTablesPrimaryKeyColumnNames(
								bundle));
					}
					catch (Exception exception) {
						_log.error(exception);
					}
				}
			}

			_awaitFuturesTermination();

			_throwableCollector.rethrow();
		}
	}

	private static void _addUpdatePrimaryKeysFutures(
		Map<String, String[]> tablesPrimaryKeysColumnNames) {

		if (MapUtil.isEmpty(tablesPrimaryKeysColumnNames)) {
			return;
		}

		ExecutorService executorService = _getExecutorService();

		for (Map.Entry<String, String[]> entry :
				tablesPrimaryKeysColumnNames.entrySet()) {

			_futures.add(
				executorService.submit(
					() -> {
						try {
							_updatePrimaryKey(entry.getKey(), entry.getValue());
						}
						catch (Exception exception) {
							_throwableCollector.collect(exception);

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

	private static ExecutorService _getExecutorService() {
		return _executorServiceDCLSingleton.getSingleton(
			() -> {
				Runtime runtime = Runtime.getRuntime();

				return Executors.newFixedThreadPool(
					runtime.availableProcessors());
			});
	}

	private static void _updatePrimaryKey(
			String tableName, String[] primaryKeyColumnNames)
		throws Exception {

		DB db = DBManagerUtil.getDB();

		db.process(
			companyId -> {
				try {
					try (Connection connection = DataAccess.getConnection()) {
						db.updatePrimaryKey(
							connection, tableName, primaryKeyColumnNames);
					}
				}
				catch (Exception exception) {
					String message = new String(
						"Unable to update database primary key for " +
							tableName);

					if (Validator.isNotNull(companyId)) {
						message += " and company " + companyId;
					}

					_log.error(message + " due to " + exception.getMessage());
				}
			});
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PrimaryKeyUpdaterUtil.class);

	private static final DCLSingleton<ExecutorService>
		_executorServiceDCLSingleton = new DCLSingleton<>();
	private static final List<Future<?>> _futures =
		new CopyOnWriteArrayList<>();
	private static ThrowableCollector _throwableCollector;

}