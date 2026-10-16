/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.instances.internal.instance.lifecycle;

import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.persistence.ReloadablePersistenceManager;
import com.liferay.portal.db.partition.util.DBPartitionUtil;
import com.liferay.portal.instance.lifecycle.BasePortalInstanceLifecycleListener;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.util.PortalInstances;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.felix.cm.file.ConfigurationHandler;

import org.osgi.framework.Constants;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luis Ortiz
 */
@Component(service = PortalInstanceLifecycleListener.class)
public class ConfigurationPortalInstanceLifecycleListener
	extends BasePortalInstanceLifecycleListener {

	@Override
	public void portalInstancePreunregistered(Company company)
		throws Exception {

		if (!PropsValues.DATABASE_PARTITION_ENABLED) {
			return;
		}

		_configurationMap.remove(company.getCompanyId());

		List<String> pids = DBPartitionUtil.getConfigurationPids(
			company.getCompanyId());

		_configurationMap.put(company.getCompanyId(), pids);
	}

	@Override
	public void portalInstanceRegistered(Company company) throws Exception {
		if (!PropsValues.DATABASE_PARTITION_ENABLED ||
			(!PortalInstances.isCompanyInCopyProcess() &&
			 !PortalInstances.isCompanyInImportProcess())) {

			return;
		}

		if (PortalInstances.isCompanyInImportProcess()) {
			Map<String, String> configurations =
				DBPartitionUtil.getConfigurations(company.getCompanyId());

			for (String configurationId : configurations.keySet()) {
				try {
					_reloadConfiguration(
						company.getCompanyId(), configurationId);
				}
				catch (Exception exception) {
					_log.error(
						"Unable to reload configuration " + configurationId,
						exception);
				}
			}

			return;
		}

		Map<String, String> configurations = DBPartitionUtil.getConfigurations(
			PortalInstances.getCopyInProcessCompanyId());

		for (Map.Entry<String, String> entry : configurations.entrySet()) {
			String dictionaryString = entry.getValue();

			Dictionary<String, Object> dictionary = ConfigurationHandler.read(
				new UnsyncByteArrayInputStream(
					dictionaryString.getBytes(StringPool.UTF8)));

			if (dictionary.get("service.factoryPid") == null) {
				continue;
			}

			if (dictionary.get("companyId") != null) {
				dictionary.put("companyId", company.getCompanyId());
			}

			Configuration configuration =
				_configurationAdmin.createFactoryConfiguration(
					(String)dictionary.get("service.factoryPid"),
					StringPool.QUESTION);

			dictionary.put("service.pid", configuration.getPid());

			configuration.update(dictionary);
		}
	}

	@Override
	public void portalInstanceUnregistered(Company company) throws Exception {
		if (!PropsValues.DATABASE_PARTITION_ENABLED ||
			MapUtil.isEmpty(_configurationMap)) {

			return;
		}

		for (String pid : _configurationMap.get(company.getCompanyId())) {
			Configuration configuration = _configurationAdmin.getConfiguration(
				pid, "?");

			if (configuration == null) {
				continue;
			}

			configuration.delete();
		}

		_configurationMap.remove(company.getCompanyId());
	}

	private void _reloadConfiguration(long companyId, String pid)
		throws Exception {

		Dictionary<String, ?> dictionary =
			(Dictionary<String, ?>)_reloadablePersistenceManager.reload(pid);

		if ((dictionary == null) ||
			(GetterUtil.getLong(dictionary.get("companyId")) != companyId)) {

			return;
		}

		Configuration[] configurations = _configurationAdmin.listConfigurations(
			StringBundler.concat("(", Constants.SERVICE_PID, "=", pid, ")"));

		if (configurations == null) {
			return;
		}

		for (Configuration configuration : configurations) {
			Set<Configuration.ConfigurationAttribute> configurationAttributes =
				configuration.getAttributes();

			boolean readOnly = configurationAttributes.contains(
				Configuration.ConfigurationAttribute.READ_ONLY);

			if (readOnly) {
				configuration.removeAttributes(
					Configuration.ConfigurationAttribute.READ_ONLY);
			}

			try {
				configuration.update(dictionary);
			}
			finally {
				if (readOnly) {
					configuration.addAttributes(
						Configuration.ConfigurationAttribute.READ_ONLY);
				}
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ConfigurationPortalInstanceLifecycleListener.class);

	private static final HashMap<Long, List<String>> _configurationMap =
		new HashMap<>();

	@Reference
	private ConfigurationAdmin _configurationAdmin;

	@Reference
	private ReloadablePersistenceManager _reloadablePersistenceManager;

}