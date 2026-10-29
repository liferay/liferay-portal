/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.asset.auto.tagger.tensorflow.internal.util;

import com.liferay.document.library.asset.auto.tagger.tensorflow.internal.osgi.commands.TensorFlowAssetAutoTagProviderOSGiCommands;
import com.liferay.document.library.asset.auto.tagger.tensorflow.internal.petra.process.InitializeProcessCallable;
import com.liferay.document.library.asset.auto.tagger.tensorflow.internal.petra.process.TensorFlowDaemonProcessCallable;
import com.liferay.petra.concurrent.DCLSingleton;
import com.liferay.petra.process.ProcessCallable;
import com.liferay.petra.process.ProcessChannel;
import com.liferay.petra.process.ProcessConfig;
import com.liferay.petra.process.ProcessExecutor;
import com.liferay.petra.process.ProcessLog;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.util.PortalClassPathUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import java.net.URL;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import java.security.CodeSource;
import java.security.ProtectionDomain;

import java.util.Dictionary;
import java.util.concurrent.Future;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;

/**
 * @author Shuyang Zhou
 */
public class TensorFlowProcessHolder {

	public static void resetCounter() {
		_relanuchCounter = 0;
	}

	public TensorFlowProcessHolder(
			Bundle bundle, ProcessExecutor processExecutor,
			TensorFlowDownloadHelper tensorFlowDownloadHelper)
		throws Exception {

		_bundle = bundle;
		_processExecutor = processExecutor;
		_tensorFlowDownloadHelper = tensorFlowDownloadHelper;

		_tensorFlowWorkDir = bundle.getDataFile("tensorflow-workdir");

		_tensorFlowWorkDir.mkdirs();
	}

	public void destroy() {
		_destroyed = true;

		_stop();

		FileUtil.deltree(_tensorFlowWorkDir);
	}

	public <T extends Serializable> T execute(
		ProcessCallable<T> processCallable, int maxRelaunch, long timeout) {

		ProcessChannel<String> processChannel =
			_processChannelDCLSingleton.getSingleton(
				() -> _startProcess(_processExecutor, maxRelaunch, timeout));

		Future<T> future = processChannel.write(processCallable);

		try {
			return future.get();
		}
		catch (Exception exception) {
			_stop();

			return ReflectionUtil.throwException(exception);
		}
	}

	private String _createClassPath(Bundle bundle, Path tempPath)
		throws Exception {

		StringBundler sb = new StringBundler();

		Dictionary<String, String> headers = bundle.getHeaders(
			StringPool.BLANK);

		for (String pathString :
				StringUtil.split(headers.get(Constants.BUNDLE_CLASSPATH))) {

			if (pathString.equals(StringPool.PERIOD)) {
				continue;
			}

			URL url = bundle.getEntry(pathString);

			Path path = Paths.get(url.getFile());

			try (InputStream inputStream = url.openStream()) {
				Path targetPath = tempPath.resolve(path.getFileName());

				sb.append(targetPath);

				sb.append(File.pathSeparator);

				Files.copy(
					inputStream, targetPath,
					StandardCopyOption.REPLACE_EXISTING);
			}
		}

		try (InputStream inputStream =
				_tensorFlowDownloadHelper.getNativeLibraryInputStream()) {

			Path targetPath = tempPath.resolve(
				TensorFlowDownloadHelper.NATIVE_LIBRARY_FILE_NAME);

			sb.append(targetPath);

			sb.append(File.pathSeparator);

			Files.copy(
				inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
		}

		ProtectionDomain protectionDomain =
			TensorFlowProcessHolder.class.getProtectionDomain();

		CodeSource codeSource = protectionDomain.getCodeSource();

		URL url = codeSource.getLocation();

		sb.append(url.getPath());

		sb.append(File.pathSeparator);

		ProcessConfig portalProcessConfig =
			PortalClassPathUtil.getPortalProcessConfig();

		sb.append(portalProcessConfig.getBootstrapClassPath());

		return sb.toString();
	}

	private ProcessConfig _createProcessConfig(Bundle bundle, Path tempPath)
		throws Exception {

		ProcessConfig.Builder builder = new ProcessConfig.Builder();

		String classPath = _createClassPath(bundle, tempPath);

		builder.setBootstrapClassPath(classPath);

		builder.setProcessLogConsumer(
			processLog -> {
				if (ProcessLog.Level.DEBUG == processLog.getLevel()) {
					if (_log.isDebugEnabled()) {
						_log.debug(
							processLog.getMessage(), processLog.getThrowable());
					}
				}
				else if (ProcessLog.Level.INFO == processLog.getLevel()) {
					if (_log.isInfoEnabled()) {
						_log.info(
							processLog.getMessage(), processLog.getThrowable());
					}
				}
				else if (ProcessLog.Level.WARN == processLog.getLevel()) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							processLog.getMessage(), processLog.getThrowable());
					}
				}
				else if (_destroyed &&
						 (processLog.getThrowable() instanceof IOException)) {

					if (_log.isDebugEnabled()) {
						_log.debug(
							processLog.getMessage(), processLog.getThrowable());
					}
				}
				else {
					_log.error(
						processLog.getMessage(), processLog.getThrowable());
				}
			});
		builder.setReactClassLoader(
			TensorFlowProcessHolder.class.getClassLoader());
		builder.setRuntimeClassPath(classPath);

		return builder.build();
	}

	private ProcessChannel<String> _startProcess(
		ProcessExecutor processExecutor, int maxRelaunch, long timeout) {

		try {
			if ((System.currentTimeMillis() - _lastLaunchTime) > timeout) {
				_relanuchCounter = 0;
			}

			if (_relanuchCounter >= maxRelaunch) {
				throw new SystemException(
					StringBundler.concat(
						"The TensorFlow process has crashed more than ",
						maxRelaunch,
						" times. It is now disabled. To enable it again ",
						"please open the Gogo shell and run ",
						TensorFlowAssetAutoTagProviderOSGiCommands.SCOPE,
						StringPool.COLON,
						TensorFlowAssetAutoTagProviderOSGiCommands.
							RESET_PROCESS_COUNTER));
			}

			_relanuchCounter++;

			if (_processConfig == null) {
				_processConfig = _createProcessConfig(
					_bundle, _tensorFlowWorkDir.toPath());
			}

			ProcessChannel<String> processChannel = processExecutor.execute(
				_processConfig, new TensorFlowDaemonProcessCallable());

			_lastLaunchTime = System.currentTimeMillis();

			Future<String> future = processChannel.write(
				new InitializeProcessCallable(
					_tensorFlowDownloadHelper.getGraphBytes()));

			future.get();

			return processChannel;
		}
		catch (Exception exception) {
			return ReflectionUtil.throwException(exception);
		}
	}

	private void _stop() {
		_processChannelDCLSingleton.destroy(
			processChannel -> {
				Future<?> future = processChannel.getProcessNoticeableFuture();

				future.cancel(true);
			});
	}

	private static final Log _log = LogFactoryUtil.getLog(
		TensorFlowProcessHolder.class);

	private static long _lastLaunchTime;
	private static volatile int _relanuchCounter;

	private final Bundle _bundle;
	private volatile boolean _destroyed;
	private final DCLSingleton<ProcessChannel<String>>
		_processChannelDCLSingleton = new DCLSingleton<>();
	private ProcessConfig _processConfig;
	private final ProcessExecutor _processExecutor;
	private final TensorFlowDownloadHelper _tensorFlowDownloadHelper;
	private final File _tensorFlowWorkDir;

}