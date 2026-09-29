/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dispatch.talend.web.internal.process;

import com.liferay.dispatch.talend.archive.TalendArchive;
import com.liferay.petra.process.PathHolder;
import com.liferay.petra.process.ProcessConfig;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.util.AggregateClassLoader;
import com.liferay.portal.util.PortalClassPathUtil;

import java.io.File;

import java.net.URL;

import java.nio.file.Path;

import java.security.CodeSource;
import java.security.ProtectionDomain;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author Igor Beslic
 */
public class TalendProcess {

	public static final String ISO_8601_PATTERN = "yyyy-MM-dd'T'HH:mm:ssZ";

	public String[] getMainMethodArguments() {
		return _mainMethodArguments.toArray(new String[0]);
	}

	public ProcessConfig getProcessConfig() {
		return _processConfig;
	}

	@Override
	public String toString() {
		return StringBundler.concat(
			"{mainMethodArguments=", _mainMethodArguments, ", processConfig=",
			_processConfig, "}");
	}

	public static class Builder {

		public TalendProcess build() {
			return new TalendProcess(this);
		}

		public Builder companyId(long companyId) {
			_contextParams.add(
				"--context_param companyId=".concat(String.valueOf(companyId)));

			return this;
		}

		public Builder contextParam(String name, String value) {
			if (Objects.equals(name, "JAVA_OPTS")) {
				_jvmOptions = StringUtil.split(value, CharPool.SPACE);

				return this;
			}

			_contextParams.add(
				StringBundler.concat(
					"--context_param ", name, StringPool.EQUAL, value));

			return this;
		}

		public Builder lastRunStartDate(Date lastRunStartDate) {
			if (lastRunStartDate != null) {
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
					ISO_8601_PATTERN);

				_contextParams.add(
					"--context_param lastRunStartDate=".concat(
						simpleDateFormat.format(lastRunStartDate)));
			}

			return this;
		}

		public Builder talendArchive(TalendArchive talendArchive) {
			_talendArchive = talendArchive;

			_contextParams.add(
				"--context=".concat(_talendArchive.getContextName()));
			_contextParams.add(
				"--context_param jobWorkDirectory=".concat(
					_talendArchive.getJobDirectory()));

			return this;
		}

		private ProcessConfig _buildProcessConfig() {
			ProcessConfig.Builder processConfigBuilder =
				new ProcessConfig.Builder();

			List<String> arguments = new ArrayList<>();

			if (_jvmOptions != null) {
				arguments.addAll(_jvmOptions);
			}

			arguments.add("-Djava.security.manager=allow");

			ProcessConfig portalProcessConfig =
				PortalClassPathUtil.getPortalProcessConfig();

			return processConfigBuilder.setArguments(
				arguments
			).setBootstrapClassPath(
				_getBootstrapClassPath(
					portalProcessConfig.getBootstrapClassPathHolders())
			).setJavaExecutable(
				System.getProperty("java.home") + "/bin/java"
			).setProcessLogConsumer(
				portalProcessConfig.getProcessLogConsumer()
			).setReactClassLoader(
				AggregateClassLoader.getAggregateClassLoader(
					portalProcessConfig.getReactClassLoader(),
					TalendProcess.class.getClassLoader())
			).setRuntimeClassPath(
				StringBundler.concat(
					_talendArchive.getClassPath(), File.pathSeparator,
					_BUNDLE_FILE_PATH)
			).build();
		}

		private String _getBootstrapClassPath(PathHolder[] pathHolders) {
			StringBundler sb = new StringBundler();

			for (PathHolder pathHolder : pathHolders) {
				Path path = pathHolder.getPath();

				if (!_isRequired(path.getFileName())) {
					continue;
				}

				sb.append(path);
				sb.append(File.pathSeparatorChar);
			}

			sb.append(_talendArchive.getJobDirectory());

			return sb.toString();
		}

		private boolean _isRequired(Path path) {
			String pathString = path.toString();

			for (String artifact : _requiredPetraArtifacts) {
				if (pathString.contains(artifact)) {
					return true;
				}
			}

			return false;
		}

		private static final String _BUNDLE_FILE_PATH;

		private static final List<String> _requiredPetraArtifacts =
			new ArrayList<String>() {
				{
					add("petra.concurrent");
					add("petra.io");
					add("petra.lang");
					add("petra.process");
					add("petra.reflect");
					add("petra.string");
				}
			};

		static {
			ProtectionDomain protectionDomain =
				Builder.class.getProtectionDomain();

			CodeSource codeSource = protectionDomain.getCodeSource();

			URL url = codeSource.getLocation();

			_BUNDLE_FILE_PATH = url.getPath();
		}

		private final List<String> _contextParams = new ArrayList<>();
		private List<String> _jvmOptions;
		private TalendArchive _talendArchive;

	}

	private TalendProcess(Builder builder) {
		_mainMethodArguments = builder._contextParams;
		_processConfig = builder._buildProcessConfig();
	}

	private final List<String> _mainMethodArguments;
	private final ProcessConfig _processConfig;

}