/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dispatch.talend.archive;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.File;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * @author Igor Beslic
 */
public class TalendArchive {

	public String getClassPath() {
		return _classPath;
	}

	public String getContextName() {
		return _contextName;
	}

	public Properties getContextProperties() {
		return _contextProperties;
	}

	public String getJVMOptions() {
		return _jvmOptions;
	}

	public String getJobDirectory() {
		return _jobDirectory;
	}

	public String getJobJarParentDirectory() {
		return _jobJarParentDirectory;
	}

	public String getJobJarPath() {
		return _jobJarPath;
	}

	public String getJobMainClassFQN() {
		return _jobMainClassFQN;
	}

	public boolean hasJVMOptions() {
		return Validator.isNotNull(_jvmOptions);
	}

	public static class Builder {

		public TalendArchive build() {
			return new TalendArchive(this);
		}

		public Builder classPathEntries(List<String> classPathEntries) {
			_classPathEntries = classPathEntries;

			return this;
		}

		public Builder contextName(String contextName) {
			_contextName = contextName;

			return this;
		}

		public Builder contextProperties(Properties contextProperties) {
			_contextProperties = new Properties();

			for (String propertyName :
					contextProperties.stringPropertyNames()) {

				_contextProperties.put(
					propertyName, contextProperties.getProperty(propertyName));
			}

			return this;
		}

		public Builder jobDirectory(String jobDirectory) {
			_jobDirectory = jobDirectory;

			return this;
		}

		public Builder jobJarParentDirectory(String jobJarParentDirectory) {
			_jobJarParentDirectory = jobJarParentDirectory;

			return this;
		}

		public Builder jobJarPath(String jobJarPath) {
			_jobJarPath = jobJarPath;

			return this;
		}

		public Builder jobMainClassFQN(String jobMainClassFQN) {
			_jobMainClassFQN = jobMainClassFQN;

			return this;
		}

		public Builder jvmOptionsList(List<String> jvmOptionsList) {
			_jvmOptionsList = jvmOptionsList;

			return this;
		}

		private String _buildClassPath() {
			if (_classPathEntries == null) {
				return StringPool.BLANK;
			}

			StringBundler sb = new StringBundler(
				(_classPathEntries.size() * 2) + 1);

			for (String classPathEntry : _classPathEntries) {
				sb.append(classPathEntry);
				sb.append(File.pathSeparatorChar);
			}

			sb.append(_jobJarPath);

			return sb.toString();
		}

		private String _buildJVMOptions() {
			if (ListUtil.isEmpty(_jvmOptionsList)) {
				return null;
			}

			StringBundler sb = new StringBundler(
				(_jvmOptionsList.size() * 2) - 1);

			Iterator<String> iterator = _jvmOptionsList.iterator();

			while (iterator.hasNext()) {
				String jvmOption = iterator.next();

				sb.append(jvmOption);

				if (iterator.hasNext()) {
					sb.append(StringPool.SPACE);
				}
			}

			return sb.toString();
		}

		private List<String> _classPathEntries;
		private String _contextName;
		private Properties _contextProperties;
		private String _jobDirectory;
		private String _jobJarParentDirectory;
		private String _jobJarPath;
		private String _jobMainClassFQN;
		private List<String> _jvmOptionsList;

	}

	private TalendArchive(Builder builder) {
		_classPath = builder._buildClassPath();
		_contextName = builder._contextName;
		_contextProperties = builder._contextProperties;
		_jobDirectory = builder._jobDirectory;
		_jobJarParentDirectory = builder._jobJarParentDirectory;
		_jobJarPath = builder._jobJarPath;
		_jobMainClassFQN = builder._jobMainClassFQN;
		_jvmOptions = builder._buildJVMOptions();
	}

	private final String _classPath;
	private final String _contextName;
	private final Properties _contextProperties;
	private final String _jobDirectory;
	private final String _jobJarParentDirectory;
	private final String _jobJarPath;
	private final String _jobMainClassFQN;
	private final String _jvmOptions;

}