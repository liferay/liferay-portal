/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.configuration.persistence.internal.upgrade;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.persistence.ReloadablePersistenceManager;
import com.liferay.portal.configuration.persistence.upgrade.ConfigurationUpgradeStepFactory;
import com.liferay.portal.file.install.constants.FileInstallConstants;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.upgrade.UpgradeException;
import com.liferay.portal.kernel.upgrade.UpgradeStep;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.File;
import java.io.IOException;

import java.nio.file.Files;

import java.util.Dictionary;
import java.util.Enumeration;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Preston Crary
 */
@Component(service = ConfigurationUpgradeStepFactory.class)
public class ConfigurationUpgradeStepFactoryImpl
	implements ConfigurationUpgradeStepFactory {

	@Override
	public UpgradeStep createUpgradeStep(String oldPid, String newPid) {
		return () -> {
			try {
				Enumeration<Dictionary<String, String>> enumeration =
					_reloadablePersistenceManager.getDictionaries();

				while (enumeration.hasMoreElements()) {
					Dictionary<String, String> dictionary =
						enumeration.nextElement();

					String oldServicePid = dictionary.get("service.pid");

					if (!oldPid.equals(oldServicePid)) {
						if (!oldPid.equals(
								dictionary.get("service.factoryPid"))) {

							continue;
						}

						dictionary.put("service.factoryPid", newPid);
					}

					String newServicePid = StringUtil.replace(
						oldServicePid, oldPid, newPid);

					dictionary.put("service.pid", newServicePid);

					String fileName = dictionary.get(
						FileInstallConstants.FELIX_FILE_INSTALL_FILENAME);

					if (fileName != null) {
						dictionary.put(
							FileInstallConstants.FELIX_FILE_INSTALL_FILENAME,
							StringUtil.replace(fileName, oldPid, newPid));
					}

					_reloadablePersistenceManager.store(
						newServicePid, dictionary);

					_reloadablePersistenceManager.delete(oldServicePid);

					Configuration configuration =
						_configurationAdmin.getConfiguration(
							newServicePid, StringPool.QUESTION);

					configuration.update(dictionary);
				}

				File configResourcesDir = new File(
					PropsValues.MODULE_FRAMEWORK_CONFIGS_DIR);

				for (File file : configResourcesDir.listFiles()) {
					String fileName = file.getName();

					if (!fileName.equals(oldPid.concat(".cfg")) &&
						!fileName.equals(oldPid.concat(".config")) &&
						!fileName.startsWith(oldPid.concat(StringPool.DASH))) {

						continue;
					}

					File newFile = new File(
						StringUtil.replace(file.getPath(), oldPid, newPid));

					if (newFile.exists()) {
						if (_log.isWarnEnabled()) {
							_log.warn(
								StringBundler.concat(
									"Unable to rename ", file.getAbsolutePath(),
									" to ", newFile.getAbsolutePath(),
									" because the file already exists"));
						}

						continue;
					}

					Files.move(file.toPath(), newFile.toPath());
				}
			}
			catch (IOException ioException) {
				throw new UpgradeException(ioException);
			}
		};
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ConfigurationUpgradeStepFactoryImpl.class);

	@Reference
	private ConfigurationAdmin _configurationAdmin;

	@Reference
	private ReloadablePersistenceManager _reloadablePersistenceManager;

}