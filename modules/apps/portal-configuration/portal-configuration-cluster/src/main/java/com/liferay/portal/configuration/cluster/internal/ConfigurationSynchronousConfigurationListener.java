/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.configuration.cluster.internal;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.persistence.InMemoryOnlyConfigurationThreadLocal;
import com.liferay.portal.configuration.persistence.ReloadablePersistenceManager;
import com.liferay.portal.kernel.cluster.ClusterExecutor;
import com.liferay.portal.kernel.cluster.ClusterRequest;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;

import java.util.Dictionary;
import java.util.Set;

import org.osgi.framework.Constants;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationEvent;
import org.osgi.service.cm.SynchronousConfigurationListener;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Raymond Augé
 */
@Component(enabled = false, service = SynchronousConfigurationListener.class)
public class ConfigurationSynchronousConfigurationListener
	implements SynchronousConfigurationListener {

	@Override
	public void configurationEvent(ConfigurationEvent configurationEvent) {
		try {
			if (ConfigurationThreadLocal.isLocalUpdate() ||
				InMemoryOnlyConfigurationThreadLocal.isInMemoryOnly() ||
				_reloadablePersistenceManager.isEphemeral(
					configurationEvent.getPid())) {

				return;
			}
		}
		catch (Exception exception) {
			_log.error(
				"Unable to determine if configuration is ephemeral for " +
					configurationEvent.getPid(),
				exception);

			return;
		}

		try {
			MethodHandler methodHandler = new MethodHandler(
				_onNotifyMethodKey, CompanyThreadLocal.getNonsystemCompanyId(),
				configurationEvent.getPid(), configurationEvent.getType());

			ClusterRequest clusterRequest =
				ClusterRequest.createMulticastRequest(methodHandler, true);

			clusterRequest.setFireAndForget(true);

			_clusterExecutor.execute(clusterRequest);
		}
		catch (Throwable throwable) {
			_log.error(throwable);
		}
	}

	private static void _onNotify(long companyId, String pid, int type)
		throws Exception {

		SynchronousConfigurationListener synchronousConfigurationListener =
			_snapshot.get();

		if (synchronousConfigurationListener == null) {
			return;
		}

		ConfigurationSynchronousConfigurationListener
			configurationSynchronousConfigurationListener =
				(ConfigurationSynchronousConfigurationListener)
					synchronousConfigurationListener;

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(companyId)) {

			configurationSynchronousConfigurationListener._reloadConfiguration(
				pid, type);
		}
	}

	private void _reloadConfiguration(String pid, int type) throws Exception {
		_reloadablePersistenceManager.reload(pid);

		Dictionary<String, ?> dictionary = _reloadablePersistenceManager.load(
			pid);

		try {
			ConfigurationThreadLocal.setLocalUpdate(true);

			Configuration[] configurations =
				_configurationAdmin.listConfigurations(
					StringBundler.concat(
						"(", Constants.SERVICE_PID, "=", pid, ")"));

			if (configurations == null) {
				return;
			}

			for (Configuration configuration : configurations) {
				Set<Configuration.ConfigurationAttribute>
					configurationAttributes = configuration.getAttributes();
				boolean readOnly = false;

				if (configurationAttributes.contains(
						Configuration.ConfigurationAttribute.READ_ONLY)) {

					configuration.removeAttributes(
						Configuration.ConfigurationAttribute.READ_ONLY);
					readOnly = true;
				}

				if (type == ConfigurationEvent.CM_DELETED) {
					configuration.delete();
				}
				else {
					if (dictionary == null) {
						configuration.update();
					}
					else {
						configuration.update(dictionary);
					}

					if (readOnly) {
						configuration.addAttributes(
							Configuration.ConfigurationAttribute.READ_ONLY);
					}
				}
			}
		}
		finally {
			ConfigurationThreadLocal.setLocalUpdate(false);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ConfigurationSynchronousConfigurationListener.class.getName());

	private static final MethodKey _onNotifyMethodKey = new MethodKey(
		ConfigurationSynchronousConfigurationListener.class, "_onNotify",
		long.class, String.class, int.class);
	private static final Snapshot<SynchronousConfigurationListener> _snapshot =
		new Snapshot<>(
			ConfigurationSynchronousConfigurationListener.class,
			SynchronousConfigurationListener.class,
			"(component.name=com.liferay.portal.configuration.cluster." +
				"internal.ConfigurationSynchronousConfigurationListener)",
			true);

	@Reference
	private ClusterExecutor _clusterExecutor;

	@Reference
	private ConfigurationAdmin _configurationAdmin;

	@Reference
	private ReloadablePersistenceManager _reloadablePersistenceManager;

}