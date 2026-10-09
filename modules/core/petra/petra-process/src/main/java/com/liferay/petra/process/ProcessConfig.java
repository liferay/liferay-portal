/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.petra.process;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringUtil;

import java.io.File;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Shuyang Zhou
 */
public class ProcessConfig implements Serializable {

	public List<String> getArguments() {
		return _arguments;
	}

	public String getBootstrapClassPath() {
		return _merge(getBootstrapClassPathHolders());
	}

	public PathHolder[] getBootstrapClassPathHolders() {
		return _bootstrapClassPathHolders;
	}

	public Map<String, String> getEnvironment() {
		return _environment;
	}

	public String getJavaExecutable() {
		return _javaExecutable;
	}

	public Consumer<ProcessLog> getProcessLogConsumer() {
		return _processLogConsumer;
	}

	public ClassLoader getReactClassLoader() {
		return _reactClassLoader;
	}

	public String getRuntimeClassPath() {
		return _merge(getRuntimeClassPathHolders());
	}

	public PathHolder[] getRuntimeClassPathHolders() {
		return _runtimeClassPathHolders;
	}

	public static class Builder {

		public Builder() {
		}

		public Builder(ProcessConfig processConfig) {
			List<String> arguments = processConfig.getArguments();

			if (!arguments.isEmpty()) {
				_arguments = new ArrayList<>(arguments);
			}

			_bootstrapClassPath = processConfig.getBootstrapClassPath();

			Map<String, String> environment = processConfig.getEnvironment();

			if (environment != null) {
				_environment = new HashMap<>(environment);
			}

			_javaExecutable = processConfig.getJavaExecutable();

			_processLogConsumer = processConfig.getProcessLogConsumer();

			_reactClassLoader = processConfig.getReactClassLoader();

			_runtimeClassPath = processConfig.getRuntimeClassPath();
		}

		public ProcessConfig build() {
			return new ProcessConfig(this);
		}

		public List<String> getArguments() {
			return _arguments;
		}

		public Map<String, String> getEnvironment() {
			return _environment;
		}

		public Builder setArguments(List<String> arguments) {
			_arguments = arguments;

			return this;
		}

		public Builder setBootstrapClassPath(String bootstrapClassPath) {
			_bootstrapClassPath = bootstrapClassPath;

			return this;
		}

		public Builder setEnvironment(Map<String, String> environment) {
			_environment = environment;

			return this;
		}

		public Builder setJavaExecutable(String javaExecutable) {
			_javaExecutable = javaExecutable;

			return this;
		}

		public Builder setProcessLogConsumer(
			Consumer<ProcessLog> processLogConsumer) {

			_processLogConsumer = processLogConsumer;

			return this;
		}

		public Builder setReactClassLoader(ClassLoader reactClassLoader) {
			_reactClassLoader = reactClassLoader;

			return this;
		}

		public Builder setRuntimeClassPath(String runtimeClassPath) {
			_runtimeClassPath = runtimeClassPath;

			return this;
		}

		private List<String> _arguments = Collections.emptyList();
		private String _bootstrapClassPath = System.getProperty(
			"java.class.path");
		private Map<String, String> _environment;
		private String _javaExecutable = "java";
		private Consumer<ProcessLog> _processLogConsumer = processLog -> {
		};
		private ClassLoader _reactClassLoader =
			ProcessConfig.class.getClassLoader();
		private String _runtimeClassPath = _bootstrapClassPath;

	}

	private ProcessConfig(Builder builder) {
		_arguments = builder._arguments;
		_bootstrapClassPathHolders = _toPathHolders(
			builder._bootstrapClassPath);
		_environment = builder._environment;
		_javaExecutable = builder._javaExecutable;
		_processLogConsumer = builder._processLogConsumer;
		_reactClassLoader = builder._reactClassLoader;

		_runtimeClassPathHolders = _toPathHolders(builder._runtimeClassPath);
	}

	private String _merge(PathHolder[] pathHolders) {
		StringBundler sb = new StringBundler((2 * pathHolders.length) - 1);

		for (int i = 0; i < pathHolders.length; i++) {
			sb.append(pathHolders[i]);

			if ((pathHolders.length - 1) == i) {
				continue;
			}

			sb.append(File.pathSeparator);
		}

		return sb.toString();
	}

	private PathHolder[] _toPathHolders(String classPath) {
		List<String> classPathElements = StringUtil.split(
			classPath, File.pathSeparatorChar);

		PathHolder[] classPathHolders =
			new PathHolder[classPathElements.size()];

		for (int i = 0; i < classPathElements.size(); i++) {
			classPathHolders[i] = new PathHolder(classPathElements.get(i));
		}

		return classPathHolders;
	}

	private static final long serialVersionUID = 1L;

	private final List<String> _arguments;
	private final PathHolder[] _bootstrapClassPathHolders;
	private final Map<String, String> _environment;
	private final String _javaExecutable;
	private final transient Consumer<ProcessLog> _processLogConsumer;
	private final transient ClassLoader _reactClassLoader;
	private final PathHolder[] _runtimeClassPathHolders;

}