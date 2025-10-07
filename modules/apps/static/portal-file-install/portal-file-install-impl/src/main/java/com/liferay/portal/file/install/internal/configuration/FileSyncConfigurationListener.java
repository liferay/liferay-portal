/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.file.install.internal.configuration;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.file.install.constants.FileInstallConstants;
import com.liferay.portal.file.install.internal.activator.FileInstallImplBundleActivator;
import com.liferay.portal.file.install.properties.ConfigurationProperties;
import com.liferay.portal.file.install.properties.ConfigurationPropertiesFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.PropsValues;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.osgi.framework.Constants;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationEvent;
import org.osgi.service.cm.ConfigurationListener;

/**
 * @author Shuyang Zhou
 */
public class FileSyncConfigurationListener implements ConfigurationListener {

	public FileSyncConfigurationListener(
		ConfigurationAdmin configurationAdmin,
		FileInstallImplBundleActivator fileInstallImplBundleActivator,
		String encoding) {

		_configurationAdmin = configurationAdmin;
		_fileInstallImplBundleActivator = fileInstallImplBundleActivator;
		_encoding = encoding;

		try {
			Configuration[] configurations =
				_configurationAdmin.listConfigurations(null);

			if (configurations != null) {
				for (Configuration configuration : configurations) {
					Dictionary<String, Object> dictionary =
						configuration.getProperties();

					String fileName = null;

					if (dictionary != null) {
						fileName = (String)dictionary.get(
							FileInstallConstants.FELIX_FILE_INSTALL_FILENAME);
					}

					if (fileName != null) {
						_pidToFile.put(configuration.getPid(), fileName);
					}
				}
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to initialize configurations", exception);
			}
		}
	}

	@Override
	public void configurationEvent(ConfigurationEvent configurationEvent) {
		int type = configurationEvent.getType();

		if (type == ConfigurationEvent.CM_UPDATED) {
			try {
				Configuration configuration =
					_configurationAdmin.getConfiguration(
						configurationEvent.getPid(), StringPool.QUESTION);

				Dictionary<String, Object> dictionary =
					configuration.getProperties();

				String fileName = null;

				if (dictionary != null) {
					fileName = (String)dictionary.get(
						FileInstallConstants.FELIX_FILE_INSTALL_FILENAME);
				}

				File file = null;

				if (fileName != null) {
					file = _fromConfigKey(fileName);
				}

				if ((file != null) && file.isFile()) {
					if (!file.canWrite()) {
						if (_log.isInfoEnabled()) {
							_log.info(
								StringBundler.concat(
									"Unable to save configuration because the ",
									"file ", file.getAbsolutePath(),
									" is not writable"));
						}

						_pidToFile.remove(configuration.getPid(), fileName);

						return;
					}

					_pidToFile.put(configuration.getPid(), fileName);

					ConfigurationProperties configurationProperties =
						ConfigurationPropertiesFactory.create(file, _encoding);

					List<String> toRemovePropertyKeys = new ArrayList<>();

					for (String key : configurationProperties.keySet()) {
						if ((dictionary.get(key) == null) &&
							!Objects.equals(Constants.SERVICE_PID, key) &&
							!Objects.equals(
								ConfigurationAdmin.SERVICE_FACTORYPID, key) &&
							!Objects.equals(
								FileInstallConstants.
									FELIX_FILE_INSTALL_FILENAME,
								key)) {

							toRemovePropertyKeys.add(key);
						}
					}

					Enumeration<String> enumeration = dictionary.keys();

					while (enumeration.hasMoreElements()) {
						String key = enumeration.nextElement();

						if (!Objects.equals(Constants.SERVICE_PID, key) &&
							!Objects.equals(
								ConfigurationAdmin.SERVICE_FACTORYPID, key) &&
							!Objects.equals(
								FileInstallConstants.
									FELIX_FILE_INSTALL_FILENAME,
								key)) {

							Object value = dictionary.get(key);

							configurationProperties.put(key, value);
						}
					}

					for (String key : toRemovePropertyKeys) {
						configurationProperties.remove(key);
					}

					try (OutputStream outputStream = new FileOutputStream(file);
						Writer writer = new OutputStreamWriter(
							outputStream, _encoding)) {

						configurationProperties.save(writer);
					}

					_fileInstallImplBundleActivator.updateChecksum(file);
				}
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn("Unable to save configuration", exception);
				}
			}
		}
		else if (type == ConfigurationEvent.CM_DELETED) {
			try {
				String fileName = _pidToFile.remove(
					configurationEvent.getPid());

				File file = null;

				if (fileName != null) {
					file = _fromConfigKey(fileName);
				}

				if ((file != null) && file.isFile() && !file.delete()) {
					throw new IOException("Unable to delete file " + file);
				}
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn("Unable to delete configuration file", exception);
				}
			}
		}
	}

	private File _fromConfigKey(String key) {
		return new File(PropsValues.MODULE_FRAMEWORK_CONFIGS_DIR, key);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FileSyncConfigurationListener.class);

	private final ConfigurationAdmin _configurationAdmin;
	private final String _encoding;
	private final FileInstallImplBundleActivator
		_fileInstallImplBundleActivator;
	private final Map<String, String> _pidToFile = new HashMap<>();

}