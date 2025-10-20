/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.instances.internal.instance.lifecycle;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.persistence.ReloadablePersistenceManager;
import com.liferay.portal.db.partition.util.DBPartitionUtil;
import com.liferay.portal.instance.lifecycle.BasePortalInstanceLifecycleListener;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.util.PortalInstances;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.felix.cm.file.ConfigurationHandler;

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
			 !PortalInstances.isCompanyInInsertionProcess())) {

			return;
		}

		if (PortalInstances.isCompanyInInsertionProcess()) {
			Map<String, String> configurations =
				DBPartitionUtil.getConfigurations(company.getCompanyId());

			for (String configurationId : configurations.keySet()) {
				_reloadablePersistenceManager.reload(configurationId);
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

			if (dictionary.get("service.factoryPid") != null) {
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

			if (configuration != null) {
				configuration.delete();
			}
		}

		_configurationMap.remove(company.getCompanyId());
	}

	private static final HashMap<Long, List<String>> _configurationMap =
		new HashMap<>();

	@Reference
	private ConfigurationAdmin _configurationAdmin;

	@Reference
	private ReloadablePersistenceManager _reloadablePersistenceManager;

}