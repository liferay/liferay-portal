/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.configuration.settings.internal.scoped.configuration.admin.service;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.configuration.settings.internal.util.ConfigurationPidUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.settings.LocationVariableResolver;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.Serializable;

import java.util.Dictionary;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.SequencedMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;

/**
 * @author Drew Brokke
 */
public class ScopedConfigurationManagedServiceFactory
	implements ManagedServiceFactory {

	public ScopedConfigurationManagedServiceFactory(
		Class<?> configurationBeanClass,
		LocationVariableResolver locationVariableResolver) {

		_configurationBeanClass = configurationBeanClass;
		_locationVariableResolver = locationVariableResolver;

		_factoryPid = ConfigurationPidUtil.getConfigurationPid(
			configurationBeanClass);
	}

	@Override
	public void deleted(String pid) {
		_removePidConfigurations(pid);
	}

	public Object getConfiguration(
		long companyId, ExtendedObjectClassDefinition.Scope scope,
		Serializable scopePK) {

		ScopeConfigurationBeans scopeConfigurationBeans =
			_configurationBeans.get(_getScopeKey(companyId, scope, scopePK));

		if (scopeConfigurationBeans == null) {
			return null;
		}

		return scopeConfigurationBeans.getLatestConfigurationBean();
	}

	public LocationVariableResolver getLocationVariableResolver() {
		return _locationVariableResolver;
	}

	@Override
	public String getName() {
		return _factoryPid;
	}

	@Override
	public void updated(String pid, Dictionary<String, ?> properties)
		throws ConfigurationException {

		long companyId = GetterUtil.getLong(
			properties.get(
				ExtendedObjectClassDefinition.Scope.COMPANY.getPropertyKey()),
			CompanyConstants.SYSTEM);

		long groupId = GetterUtil.getLong(
			properties.get(
				ExtendedObjectClassDefinition.Scope.GROUP.getPropertyKey()),
			GroupConstants.ANY_PARENT_GROUP_ID);

		if ((companyId != CompanyConstants.SYSTEM) &&
			(groupId == GroupConstants.ANY_PARENT_GROUP_ID)) {

			_updateEntries(
				pid, companyId, ExtendedObjectClassDefinition.Scope.COMPANY,
				properties);
		}

		if (groupId != GroupConstants.ANY_PARENT_GROUP_ID) {
			_updateEntries(
				pid, groupId, ExtendedObjectClassDefinition.Scope.GROUP,
				properties);
		}

		String portletInstanceId = GetterUtil.getString(
			properties.get(
				ExtendedObjectClassDefinition.Scope.PORTLET_INSTANCE.
					getPropertyKey()));

		if (Validator.isNotNull(portletInstanceId)) {
			_updateEntries(
				pid, portletInstanceId,
				ExtendedObjectClassDefinition.Scope.PORTLET_INSTANCE,
				properties);
		}
	}

	private ScopeKey _getScopeKey(
		long companyId, ExtendedObjectClassDefinition.Scope scope,
		Serializable scopePK) {

		ScopeKey scopeKey = new ScopeKey(scopePK, scope);

		if (scope.equals(ExtendedObjectClassDefinition.Scope.GROUP)) {
			scopeKey = new ScopeKey(companyId + _SEPARATOR + scopePK, scope);
		}

		return scopeKey;
	}

	private void _removePidConfigurations(String pid) {
		_pidScopeKeys.computeIfPresent(
			pid,
			(key, scopeKeys) -> {
				for (ScopeKey scopeKey : scopeKeys) {
					_removeScopePidConfigurations(pid, scopeKey);
				}

				return null;
			});
	}

	private void _removeScopePidConfigurations(String pid, ScopeKey scopeKey) {
		_configurationBeans.computeIfPresent(
			scopeKey,
			(key, scopeConfigurationBeans) -> {
				SequencedMap<String, Object> configurationBeans =
					new LinkedHashMap<>(
						scopeConfigurationBeans.getConfigurationBeans());

				configurationBeans.remove(pid);

				if (!configurationBeans.isEmpty()) {
					return new ScopeConfigurationBeans(configurationBeans);
				}

				return null;
			});
	}

	private void _updateEntries(
		String pid, Serializable scopePK,
		ExtendedObjectClassDefinition.Scope scope,
		Dictionary<String, ?> properties) {

		ScopeKey scopeKey = _getScopeKey(
			GetterUtil.getLong(
				properties.get(
					ExtendedObjectClassDefinition.Scope.COMPANY.
						getPropertyKey())),
			scope, scopePK);

		_pidScopeKeys.compute(
			pid,
			(key, scopeKeys) -> {
				if (scopeKeys == null) {
					scopeKeys = new HashSet<>();
				}

				scopeKeys.add(scopeKey);

				return scopeKeys;
			});

		_configurationBeans.compute(
			scopeKey,
			(key, scopeConfigurationBeans) -> {
				SequencedMap<String, Object> configurationBeans =
					new LinkedHashMap<>();

				if (scopeConfigurationBeans != null) {
					configurationBeans.putAll(
						scopeConfigurationBeans.getConfigurationBeans());
				}

				configurationBeans.putLast(
					pid,
					ConfigurableUtil.createConfigurable(
						_configurationBeanClass, properties));

				return new ScopeConfigurationBeans(configurationBeans);
			});
	}

	private static final String _SEPARATOR = "--";

	private final Class<?> _configurationBeanClass;
	private final Map<ScopeKey, ScopeConfigurationBeans> _configurationBeans =
		new ConcurrentHashMap<>();
	private final String _factoryPid;
	private final LocationVariableResolver _locationVariableResolver;
	private final Map<String, Set<ScopeKey>> _pidScopeKeys =
		new ConcurrentHashMap<>();

	private static class ScopeConfigurationBeans {

		public Map<String, Object> getConfigurationBeans() {
			return _configurationBeans;
		}

		public Object getLatestConfigurationBean() {
			return _latestConfigurationBean;
		}

		private ScopeConfigurationBeans(
			SequencedMap<String, Object> configurationBeans) {

			_configurationBeans = configurationBeans;

			Map.Entry<String, Object> entry = configurationBeans.lastEntry();

			_latestConfigurationBean = entry.getValue();
		}

		private final SequencedMap<String, Object> _configurationBeans;
		private final Object _latestConfigurationBean;

	}

	private class ScopeKey {

		@Override
		public boolean equals(Object object) {
			if (!(object instanceof ScopeKey)) {
				return false;
			}

			ScopeKey scopeKey = (ScopeKey)object;

			if (Objects.equals(_scope, scopeKey.getScope()) &&
				Objects.equals(_scopePK, scopeKey.getScopePK())) {

				return true;
			}

			return false;
		}

		public ExtendedObjectClassDefinition.Scope getScope() {
			return _scope;
		}

		public Serializable getScopePK() {
			return _scopePK;
		}

		@Override
		public int hashCode() {
			return Objects.hash(_scope.getValue(), _scopePK);
		}

		private ScopeKey(
			Serializable scopePK, ExtendedObjectClassDefinition.Scope scope) {

			_scopePK = scopePK;
			_scope = scope;
		}

		private final ExtendedObjectClassDefinition.Scope _scope;
		private final Serializable _scopePK;

	}

}