/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.monitor;

import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.InfrastructureUtil;
import com.liferay.portal.kernel.util.NamedThreadFactory;
import com.liferay.portal.kernel.util.PropsValues;

import java.sql.Connection;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

/**
 * @author Jorge Avalos
 */
public final class UpgradeQueryMonitor {

	public static synchronized void start() {
		if (!PropsValues.UPGRADE_QUERY_MONITOR_ENABLED ||
			(_scheduledExecutorService != null)) {

			return;
		}

		ScheduledExecutorService scheduledExecutorService =
			Executors.newSingleThreadScheduledExecutor(
				new NamedThreadFactory(
					"Liferay Upgrade Query Monitor", Thread.NORM_PRIORITY,
					null));

		scheduledExecutorService.scheduleWithFixedDelay(
			() -> _poll(scheduledExecutorService), _POLLING_INTERVAL_SECONDS,
			_POLLING_INTERVAL_SECONDS, TimeUnit.SECONDS);

		_scheduledExecutorService = scheduledExecutorService;
	}

	public static synchronized void stop() {
		if (_scheduledExecutorService == null) {
			return;
		}

		_scheduledExecutorService.shutdown();

		try {
			if (!_scheduledExecutorService.awaitTermination(
					_SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {

				if (_log.isWarnEnabled()) {
					_log.warn(
						StringBundler.concat(
							"Unable to terminate upgrade query monitor within ",
							_SHUTDOWN_TIMEOUT_SECONDS, " seconds"));
				}
			}
		}
		catch (InterruptedException interruptedException) {
			if (_log.isDebugEnabled()) {
				_log.debug(interruptedException);
			}

			Thread currentThread = Thread.currentThread();

			currentThread.interrupt();
		}

		_scheduledExecutorService = null;
	}

	private static String _getSchemaClause(
		String defaultSchema, String schema) {

		if ((schema == null) || schema.equals(defaultSchema)) {
			return "";
		}

		return StringBundler.concat(" in schema \"", schema, "\"");
	}

	private static void _poll(
		ScheduledExecutorService scheduledExecutorService) {

		DataSource dataSource = InfrastructureUtil.getDataSource();

		if (dataSource == null) {
			return;
		}

		try (Connection connection = dataSource.getConnection()) {
			DB db = DBManagerUtil.getDB();

			String defaultSchema = connection.getCatalog();

			if (defaultSchema == null) {
				defaultSchema = connection.getSchema();
			}

			List<DB.QueryInfo> lockedQueryInfos = db.getLockedQueryInfos(
				connection);

			for (DB.QueryInfo lockedQueryInfo : lockedQueryInfos) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						StringBundler.concat(
							"Locked query \"", lockedQueryInfo.getQuery(),
							"\" with ID ", lockedQueryInfo.getId(),
							_getSchemaClause(
								defaultSchema, lockedQueryInfo.getSchema()),
							" has been running for ",
							TimeUnit.MILLISECONDS.toSeconds(
								lockedQueryInfo.getDuration()),
							" seconds"));
				}
			}

			if (!_log.isInfoEnabled()) {
				return;
			}

			List<DB.QueryInfo> longRunningQueryInfos =
				db.getLongRunningQueryInfos(connection);

			for (DB.QueryInfo longRunningQueryInfo : longRunningQueryInfos) {
				_log.info(
					StringBundler.concat(
						"Long running query \"",
						longRunningQueryInfo.getQuery(), "\" with ID ",
						longRunningQueryInfo.getId(),
						_getSchemaClause(
							defaultSchema, longRunningQueryInfo.getSchema()),
						" has been running for ",
						TimeUnit.MILLISECONDS.toSeconds(
							longRunningQueryInfo.getDuration()),
						" seconds"));
			}
		}
		catch (Exception exception) {
			if (scheduledExecutorService.isShutdown()) {
				return;
			}

			if (_log.isWarnEnabled()) {
				_log.warn(
					"Upgrade query monitoring is disabled: " +
						exception.getMessage());
			}

			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			ReflectionUtil.throwException(exception);
		}
	}

	private UpgradeQueryMonitor() {
	}

	private static final long _POLLING_INTERVAL_SECONDS = 60;

	private static final long _SHUTDOWN_TIMEOUT_SECONDS = 5;

	private static final Log _log = LogFactoryUtil.getLog(
		UpgradeQueryMonitor.class);

	private static ScheduledExecutorService _scheduledExecutorService;

}