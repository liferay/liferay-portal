/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.instances.internal.operation;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.file.install.constants.FileInstallConstants;
import com.liferay.portal.kernel.cluster.ClusterMasterExecutor;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.Validator;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.Dictionary;
import java.util.Map;
import java.util.concurrent.Callable;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mariano Álvaro Sáiz
 */
public abstract class BasePortalInstanceOperation {

	public abstract String getOperationCompletedMessage(long companyId);

	public void onPortalInstance(
		Callable<Company> callable, Map<String, Object> properties) {

		try {
			if (!clusterMasterExecutor.isMaster()) {
				return;
			}

			Company company = callable.call();

			if (company != null) {
				_deleteConfiguration((String)properties.get("service.pid"));

				if (_log.isInfoEnabled()) {
					_log.info(
						getOperationCompletedMessage(company.getCompanyId()));
				}
			}
		}
		catch (Exception exception) {
			_log.error(
				"Unable to perform operation on portal instance", exception);
		}
		finally {
			_deleteConfiguration((String)properties.get("service.pid"));
		}
	}

	@Reference
	protected ClusterMasterExecutor clusterMasterExecutor;

	@Reference
	protected ConfigurationAdmin configurationAdmin;

	private void _deleteConfiguration(String pid) {
		try {
			Configuration configuration = configurationAdmin.getConfiguration(
				pid, StringPool.QUESTION);

			if (configuration == null) {
				return;
			}

			Dictionary<String, Object> properties =
				configuration.getProperties();

			if ((properties != null) &&
				Validator.isNotNull(
					properties.get(
						FileInstallConstants.FELIX_FILE_INSTALL_FILENAME))) {

				Files.deleteIfExists(
					Paths.get(
						PropsValues.MODULE_FRAMEWORK_CONFIGS_DIR,
						pid.concat(".config")));

				return;
			}

			configuration.delete();
		}
		catch (IOException ioException) {
			_log.error(ioException);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BasePortalInstanceOperation.class);

}