/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.sidecar;

import com.liferay.petra.concurrent.NoticeableFuture;
import com.liferay.petra.io.Deserializer;
import com.liferay.petra.process.ProcessCallable;
import com.liferay.petra.process.ProcessChannel;
import com.liferay.petra.process.ProcessConfig;
import com.liferay.petra.process.ProcessException;
import com.liferay.petra.process.ProcessExecutor;
import com.liferay.petra.process.ProcessLog;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import java.lang.reflect.Constructor;

import java.net.URL;
import java.net.URLClassLoader;

import java.nio.ByteBuffer;
import java.nio.file.Files;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tina Tian
 */
public class PersistedProcessUtil {

	public static <T extends Serializable> ProcessChannel<T> start(
		PersistedProcess persistedProcess, ProcessExecutor processExecutor) {

		String[] processCallableClassNames =
			persistedProcess.getProcessCallableClassNames();

		if (processCallableClassNames.length == 0) {
			throw new IllegalArgumentException(
				"No process callable is defined");
		}

		ClassLoader reactClassLoader = new URLClassLoader(
			new URL[] {persistedProcess.getBundleURL()},
			PortalClassLoaderUtil.getClassLoader());

		List<ProcessCallable<?>> processCallables = new ArrayList<>();

		for (String processCallableClassName : processCallableClassNames) {
			processCallables.add(
				(ProcessCallable<?>)_getInstance(
					reactClassLoader, processCallableClassName));
		}

		ProcessConfig persistedProcessConfig =
			persistedProcess.getProcessConfig();

		ProcessConfig.Builder builder = new ProcessConfig.Builder(
			persistedProcessConfig);

		ProcessConfig processConfig = builder.setEnvironment(
			HashMapBuilder.putAll(
				System.getenv()
			).putAll(
				persistedProcessConfig.getEnvironment()
			).build()
		).setProcessLogConsumer(
			PersistedProcessUtil::_consumeProcessLog
		).setReactClassLoader(
			reactClassLoader
		).build();

		ProcessChannel<?> processChannel = null;

		for (ProcessCallable<?> processCallable : processCallables) {
			if (processChannel == null) {
				try {
					processChannel = processExecutor.execute(
						processConfig, processCallable);
				}
				catch (ProcessException processException) {
					throw new RuntimeException(
						"Unable to start process " +
							persistedProcess.getProcessName(),
						processException);
				}
			}
			else {
				NoticeableFuture<?> noticeableFuture = processChannel.write(
					processCallable);

				try {
					noticeableFuture.get();
				}
				catch (Exception exception) {
					NoticeableFuture<?> processNoticeableFuture =
						processChannel.getProcessNoticeableFuture();

					processNoticeableFuture.cancel(true);

					throw new RuntimeException(
						"Unable to execute process callable", exception);
				}
			}
		}

		return (ProcessChannel<T>)processChannel;
	}

	public static <T extends Serializable>
		ObjectValuePair<ProcessChannel<T>, byte[]> start(
			ProcessExecutor processExecutor, File processFile) {

		if (!processFile.exists()) {
			throw new IllegalArgumentException(
				"Unable to find persisted process file " + processFile);
		}

		byte[] bytes = null;

		try {
			bytes = Files.readAllBytes(processFile.toPath());
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				"Unable to read persisted process file " + processFile,
				ioException);
		}

		Deserializer deserializer = new Deserializer(ByteBuffer.wrap(bytes));

		PersistedProcess persistedProcess = null;

		try {
			persistedProcess = deserializer.readObject();
		}
		catch (ClassNotFoundException classNotFoundException) {
			throw new RuntimeException(
				"Unable to deserialize persisted process file " + processFile,
				classNotFoundException);
		}

		return new ObjectValuePair<>(
			start(persistedProcess, processExecutor), bytes);
	}

	private static void _consumeProcessLog(ProcessLog processLog) {
		if (ProcessLog.Level.DEBUG == processLog.getLevel()) {
			if (_log.isDebugEnabled()) {
				_log.debug(processLog.getMessage(), processLog.getThrowable());
			}
		}
		else if (ProcessLog.Level.INFO == processLog.getLevel()) {
			if (_log.isInfoEnabled()) {
				_log.info(processLog.getMessage(), processLog.getThrowable());
			}
		}
		else if (ProcessLog.Level.WARN == processLog.getLevel()) {
			if (_log.isWarnEnabled()) {
				_log.warn(processLog.getMessage(), processLog.getThrowable());
			}
		}
		else {
			_log.error(processLog.getMessage(), processLog.getThrowable());
		}
	}

	private static Object _getInstance(
		ClassLoader classLoader, String className) {

		try {
			Class<?> clazz = classLoader.loadClass(className);

			Constructor<?> constructor = clazz.getConstructor();

			return constructor.newInstance();
		}
		catch (Exception exception) {
			throw new IllegalStateException(
				"Unable to create instance for " + className, exception);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PersistedProcessUtil.class);

}