/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.db;

import com.liferay.petra.concurrent.DCLSingleton;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.NamedThreadFactory;
import com.liferay.portal.kernel.util.PropsUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Mariano Álvaro Sáiz
 */
public class UpgradeExecutorServiceUtil {

	public static ExecutorService getDataExecutorService() {
		return _dataExecutorServiceDCLSingleton.getSingleton(
			() -> _getExecutorService(
				"UpgradeDataExecutor", getDataExecutorServicePoolSize()));
	}

	public static int getDataExecutorServicePoolSize() {
		int jdbcDefaultMaximumPoolSize = GetterUtil.getInteger(
			PropsUtil.get("jdbc.default.maximumPoolSize"));

		return Math.max(
			1, (jdbcDefaultMaximumPoolSize - _RESERVED_CONNECTION_COUNT) / 2);
	}

	public static ExecutorService getSchemaExecutorService() {
		return _schemaExecutorServiceDCLSingleton.getSingleton(
			() -> {
				Runtime runtime = Runtime.getRuntime();

				return _getExecutorService(
					"UpgradeSchemaExecutor", runtime.availableProcessors());
			});
	}

	public static void shutdown() {
		_dataExecutorServiceDCLSingleton.destroy(
			executorService -> executorService.shutdownNow());
		_schemaExecutorServiceDCLSingleton.destroy(
			executorService -> executorService.shutdownNow());
	}

	private static ExecutorService _getExecutorService(
		String name, int maximumPoolSize) {

		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
			maximumPoolSize, maximumPoolSize, 60, TimeUnit.SECONDS,
			new LinkedBlockingQueue<>(),
			new NamedThreadFactory(name, Thread.NORM_PRIORITY, null));

		threadPoolExecutor.allowCoreThreadTimeOut(true);

		return threadPoolExecutor;
	}

	private static final DCLSingleton<ExecutorService>
		_dataExecutorServiceDCLSingleton = new DCLSingleton<>();
	private static final int _RESERVED_CONNECTION_COUNT = 4;
	private static final DCLSingleton<ExecutorService>
		_schemaExecutorServiceDCLSingleton = new DCLSingleton<>();

}