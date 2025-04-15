/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.util;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.cluster.ClusterExecutorUtil;
import com.liferay.portal.kernel.cluster.ClusterInvokeThreadLocal;
import com.liferay.portal.kernel.cluster.ClusterRequest;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.PortalPreferenceValue;
import com.liferay.portal.kernel.model.PortalPreferences;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.service.PortalPreferenceValueLocalService;
import com.liferay.portal.kernel.service.PortalPreferencesLocalService;
import com.liferay.portal.kernel.transaction.TransactionCommitCallbackUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.PrefsProps;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portlet.PortalPreferencesImpl;
import com.liferay.portlet.PortalPreferencesWrapper;
import com.liferay.portlet.PortletPreferencesImpl;

import jakarta.portlet.PortletPreferences;
import jakarta.portlet.ReadOnlyException;
import jakarta.portlet.ValidatorException;

import java.io.IOException;

import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Brian Wing Shun Chan
 */
public class PrefsPropsImpl implements PrefsProps {

	public void afterPropertiesSet() {
		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		_serviceRegistration = bundleContext.registerService(
			ModelListener.class, new PortalPreferenceValueModelListener(),
			null);
	}

	public void destroy() {
		_serviceRegistration.unregister();
	}

	@Override
	public boolean getBoolean(long companyId, String name) {
		return getBoolean(_fetchPreferences(companyId), name);
	}

	@Override
	public boolean getBoolean(
		long companyId, String name, boolean defaultValue) {

		return getBoolean(_fetchPreferences(companyId), name, defaultValue);
	}

	@Override
	public boolean getBoolean(
		PortletPreferences portletPreferences, String name) {

		return GetterUtil.getBoolean(getString(portletPreferences, name));
	}

	@Override
	public boolean getBoolean(
		PortletPreferences portletPreferences, String name,
		boolean defaultValue) {

		return GetterUtil.getBoolean(
			getString(portletPreferences, name, defaultValue));
	}

	@Override
	public boolean getBoolean(String name) {
		return getBoolean(_fetchPreferences(), name);
	}

	@Override
	public boolean getBoolean(String name, boolean defaultValue) {
		return getBoolean(_fetchPreferences(), name, defaultValue);
	}

	@Override
	public String getContent(long companyId, String name) {
		return getContent(_fetchPreferences(companyId), name);
	}

	@Override
	public String getContent(
		PortletPreferences portletPreferences, String name) {

		String value = portletPreferences.getValue(name, StringPool.BLANK);

		if (Validator.isNotNull(value)) {
			return value;
		}

		try {
			return StringUtil.read(
				PrefsPropsImpl.class.getClassLoader(), PropsUtil.get(name));
		}
		catch (IOException ioException) {
			_log.error(
				"Unable to read the content for " + PropsUtil.get(name),
				ioException);

			return null;
		}
	}

	@Override
	public String getContent(String name) {
		return getContent(_fetchPreferences(), name);
	}

	@Override
	public double getDouble(long companyId, String name) {
		return getDouble(_fetchPreferences(companyId), name);
	}

	@Override
	public double getDouble(long companyId, String name, double defaultValue) {
		return getDouble(_fetchPreferences(companyId), name, defaultValue);
	}

	@Override
	public double getDouble(
		PortletPreferences portletPreferences, String name) {

		return GetterUtil.getDouble(getString(portletPreferences, name));
	}

	@Override
	public double getDouble(
		PortletPreferences portletPreferences, String name,
		double defaultValue) {

		return GetterUtil.getDouble(
			getString(portletPreferences, name, defaultValue));
	}

	@Override
	public double getDouble(String name) {
		return getDouble(_fetchPreferences(), name);
	}

	@Override
	public double getDouble(String name, double defaultValue) {
		return getDouble(_fetchPreferences(), name, defaultValue);
	}

	@Override
	public int getInteger(long companyId, String name) {
		return getInteger(_fetchPreferences(companyId), name);
	}

	@Override
	public int getInteger(long companyId, String name, int defaultValue) {
		return getInteger(_fetchPreferences(companyId), name, defaultValue);
	}

	@Override
	public int getInteger(PortletPreferences portletPreferences, String name) {
		return GetterUtil.getInteger(getString(portletPreferences, name));
	}

	@Override
	public int getInteger(
		PortletPreferences portletPreferences, String name, int defaultValue) {

		return GetterUtil.getInteger(
			getString(portletPreferences, name, defaultValue));
	}

	@Override
	public int getInteger(String name) {
		return getInteger(_fetchPreferences(), name);
	}

	@Override
	public int getInteger(String name, int defaultValue) {
		return getInteger(_fetchPreferences(), name, defaultValue);
	}

	@Override
	public long getLong(long companyId, String name) {
		return getLong(_fetchPreferences(companyId), name);
	}

	@Override
	public long getLong(long companyId, String name, long defaultValue) {
		return getLong(_fetchPreferences(companyId), name, defaultValue);
	}

	@Override
	public long getLong(PortletPreferences portletPreferences, String name) {
		return GetterUtil.getLong(getString(portletPreferences, name));
	}

	@Override
	public long getLong(
		PortletPreferences portletPreferences, String name, long defaultValue) {

		return GetterUtil.getLong(
			getString(portletPreferences, name, defaultValue));
	}

	@Override
	public long getLong(String name) {
		return getLong(_fetchPreferences(), name);
	}

	@Override
	public long getLong(String name, long defaultValue) {
		return getLong(_fetchPreferences(), name, defaultValue);
	}

	@Override
	public PortletPreferences getPreferences() {
		return getPreferences(PortletKeys.PREFS_OWNER_ID_DEFAULT);
	}

	@Override
	public PortletPreferences getPreferences(long companyId) {
		PortletPreferences portletPreferences = _fetchPreferences(companyId);

		if (portletPreferences == _emptyPortletPreferences) {
			return new LazyPortletPreferences(
				_emptyPortletPreferences,
				() -> _portalPreferencesLocalService.getPreferences(
					companyId, PortletKeys.PREFS_OWNER_TYPE_COMPANY));
		}

		PortalPreferencesWrapper portalPreferencesWrapper =
			(PortalPreferencesWrapper)portletPreferences;

		return portalPreferencesWrapper.clone();
	}

	@Override
	public Properties getProperties(
		PortletPreferences portletPreferences, String prefix,
		boolean removePrefix) {

		Properties newProperties = new Properties();

		Enumeration<String> enumeration = portletPreferences.getNames();

		while (enumeration.hasMoreElements()) {
			String key = enumeration.nextElement();

			if (key.startsWith(prefix)) {
				String value = portletPreferences.getValue(
					key, StringPool.BLANK);

				if (removePrefix) {
					key = key.substring(prefix.length());
				}

				newProperties.setProperty(key, value);
			}
		}

		return newProperties;
	}

	@Override
	public Properties getProperties(String prefix, boolean removePrefix) {
		return getProperties(_fetchPreferences(), prefix, removePrefix);
	}

	@Override
	public short getShort(long companyId, String name) {
		return getShort(_fetchPreferences(companyId), name);
	}

	@Override
	public short getShort(long companyId, String name, short defaultValue) {
		return getShort(_fetchPreferences(companyId), name, defaultValue);
	}

	@Override
	public short getShort(PortletPreferences portletPreferences, String name) {
		return GetterUtil.getShort(getString(portletPreferences, name));
	}

	@Override
	public short getShort(
		PortletPreferences portletPreferences, String name,
		short defaultValue) {

		return GetterUtil.getShort(
			getString(portletPreferences, name, defaultValue));
	}

	@Override
	public short getShort(String name) {
		return getShort(_fetchPreferences(), name);
	}

	@Override
	public short getShort(String name, short defaultValue) {
		return getShort(_fetchPreferences(), name, defaultValue);
	}

	@Override
	public String getString(long companyId, String name) {
		return getString(_fetchPreferences(companyId), name);
	}

	@Override
	public String getString(long companyId, String name, String defaultValue) {
		return getString(_fetchPreferences(companyId), name, defaultValue);
	}

	@Override
	public String getString(
		PortletPreferences portletPreferences, String name) {

		String value = PropsUtil.get(name);

		return portletPreferences.getValue(name, value);
	}

	@Override
	public String getString(
		PortletPreferences portletPreferences, String name,
		boolean defaultValue) {

		return portletPreferences.getValue(name, String.valueOf(defaultValue));
	}

	@Override
	public String getString(
		PortletPreferences portletPreferences, String name,
		double defaultValue) {

		return portletPreferences.getValue(name, String.valueOf(defaultValue));
	}

	@Override
	public String getString(
		PortletPreferences portletPreferences, String name, int defaultValue) {

		return portletPreferences.getValue(name, String.valueOf(defaultValue));
	}

	@Override
	public String getString(
		PortletPreferences portletPreferences, String name, long defaultValue) {

		return portletPreferences.getValue(name, String.valueOf(defaultValue));
	}

	@Override
	public String getString(
		PortletPreferences portletPreferences, String name,
		short defaultValue) {

		return portletPreferences.getValue(name, String.valueOf(defaultValue));
	}

	@Override
	public String getString(
		PortletPreferences portletPreferences, String name,
		String defaultValue) {

		return portletPreferences.getValue(name, defaultValue);
	}

	@Override
	public String getString(String name) {
		return getString(_fetchPreferences(), name);
	}

	@Override
	public String getString(String name, String defaultValue) {
		return getString(_fetchPreferences(), name, defaultValue);
	}

	@Override
	public String[] getStringArray(
		long companyId, String name, String delimiter) {

		return getStringArray(_fetchPreferences(companyId), name, delimiter);
	}

	@Override
	public String[] getStringArray(
		long companyId, String name, String delimiter, String[] defaultValue) {

		return getStringArray(
			_fetchPreferences(companyId), name, delimiter, defaultValue);
	}

	@Override
	public String[] getStringArray(
		PortletPreferences portletPreferences, String name, String delimiter) {

		String value = PropsUtil.get(name);

		return StringUtil.split(
			portletPreferences.getValue(name, value), delimiter);
	}

	@Override
	public String[] getStringArray(
		PortletPreferences portletPreferences, String name, String delimiter,
		String[] defaultValue) {

		String value = portletPreferences.getValue(name, null);

		if (value == null) {
			return defaultValue;
		}

		return StringUtil.split(value, delimiter);
	}

	@Override
	public String[] getStringArray(String name, String delimiter) {
		return getStringArray(_fetchPreferences(), name, delimiter);
	}

	@Override
	public String[] getStringArray(
		String name, String delimiter, String[] defaultValue) {

		return getStringArray(
			_fetchPreferences(), name, delimiter, defaultValue);
	}

	@Override
	public String getStringFromNames(long companyId, String... names) {
		for (String name : names) {
			String value = getString(companyId, name);

			if (Validator.isNotNull(value)) {
				return value;
			}
		}

		return null;
	}

	private static void _removePortletPreference(long companyId) {
		_portletPreferences.remove(companyId);

		if (ClusterExecutorUtil.isEnabled() &&
			ClusterInvokeThreadLocal.isEnabled()) {

			ClusterRequest clusterRequest =
				ClusterRequest.createMulticastRequest(
					new MethodHandler(
						_removePortletPreferenceMethodKey, companyId),
					true);

			clusterRequest.setFireAndForget(true);

			ClusterExecutorUtil.execute(clusterRequest);
		}
	}

	private PortletPreferences _fetchPreferences() {
		return _fetchPreferences(PortletKeys.PREFS_OWNER_ID_DEFAULT);
	}

	private PortletPreferences _fetchPreferences(long companyId) {
		if (_skipCacheThreadLocal.get()) {
			return _getPortletPreferences(companyId);
		}

		return _portletPreferences.computeIfAbsent(
			companyId, this::_getPortletPreferences);
	}

	private PortletPreferences _getPortletPreferences(long companyId) {
		PortalPreferences portalPreferences =
			_portalPreferencesLocalService.fetchPortalPreferences(
				companyId, PortletKeys.PREFS_OWNER_TYPE_COMPANY);

		if (portalPreferences == null) {
			return _emptyPortletPreferences;
		}

		PortalPreferencesImpl portalPreferencesImpl =
			(PortalPreferencesImpl)
				_portalPreferenceValueLocalService.getPortalPreferences(
					portalPreferences, false);

		return new PortalPreferencesWrapper(portalPreferencesImpl);
	}

	private static final Log _log = LogFactoryUtil.getLog(PrefsPropsImpl.class);

	private static final Map<Long, PortletPreferences> _portletPreferences =
		new ConcurrentHashMap<>();
	private static final MethodKey _removePortletPreferenceMethodKey =
		new MethodKey(
			PrefsPropsImpl.class, "_removePortletPreference", long.class);
	private static final ThreadLocal<Boolean> _skipCacheThreadLocal =
		ThreadLocal.withInitial(() -> false);

	private final PortletPreferences _emptyPortletPreferences =
		new PortletPreferencesImpl();

	@BeanReference(type = PortalPreferencesLocalService.class)
	private PortalPreferencesLocalService _portalPreferencesLocalService;

	@BeanReference(type = PortalPreferenceValueLocalService.class)
	private PortalPreferenceValueLocalService
		_portalPreferenceValueLocalService;

	private ServiceRegistration<?> _serviceRegistration;

	private static class LazyPortletPreferences implements PortletPreferences {

		@Override
		public Map<String, String[]> getMap() {
			return _portletPreferences.getMap();
		}

		@Override
		public Enumeration<String> getNames() {
			return _portletPreferences.getNames();
		}

		@Override
		public String getValue(String key, String def) {
			return _portletPreferences.getValue(key, def);
		}

		@Override
		public String[] getValues(String key, String[] def) {
			return _portletPreferences.getValues(key, def);
		}

		@Override
		public boolean isReadOnly(String key) {
			return _portletPreferences.isReadOnly(key);
		}

		@Override
		public void reset(String key) throws ReadOnlyException {
			_ensureLoaded();

			_portletPreferences.reset(key);
		}

		@Override
		public void setValue(String key, String value)
			throws ReadOnlyException {

			_ensureLoaded();

			_portletPreferences.setValue(key, value);
		}

		@Override
		public void setValues(String key, String... values)
			throws ReadOnlyException {

			_ensureLoaded();

			_portletPreferences.setValues(key, values);
		}

		@Override
		public void store() throws IOException, ValidatorException {
			_ensureLoaded();

			_portletPreferences.store();
		}

		private LazyPortletPreferences(
			PortletPreferences portletPreferences,
			Supplier<PortletPreferences> writePortletPreferencesSupplier) {

			_portletPreferences = portletPreferences;
			_writePortletPreferencesSupplier = writePortletPreferencesSupplier;
		}

		private void _ensureLoaded() {
			if (!_loaded) {
				_portletPreferences = _writePortletPreferencesSupplier.get();

				_loaded = true;
			}
		}

		private boolean _loaded;
		private PortletPreferences _portletPreferences;
		private final Supplier<PortletPreferences>
			_writePortletPreferencesSupplier;

	}

	private class PortalPreferenceValueModelListener
		extends BaseModelListener<PortalPreferenceValue> {

		@Override
		public Class<?> getModelClass() {
			return PortalPreferenceValue.class;
		}

		@Override
		public void onBeforeCreate(PortalPreferenceValue portalPreferenceValue)
			throws ModelListenerException {

			_clearPortletPreferencce(portalPreferenceValue);
		}

		@Override
		public void onBeforeRemove(PortalPreferenceValue portalPreferenceValue)
			throws ModelListenerException {

			_clearPortletPreferencce(portalPreferenceValue);
		}

		@Override
		public void onBeforeUpdate(
			PortalPreferenceValue originalPortalPreferenceValue,
			PortalPreferenceValue portalPreferenceValue) {

			_clearPortletPreferencce(portalPreferenceValue);
		}

		private void _clearPortletPreferencce(
			PortalPreferenceValue portalPreferenceValue) {

			if (_skipCacheThreadLocal.get()) {
				return;
			}

			try {
				PortalPreferences portalPreferences =
					_portalPreferencesLocalService.getPortalPreferences(
						portalPreferenceValue.getPortalPreferencesId());

				if (portalPreferences.getOwnerType() ==
						PortletKeys.PREFS_OWNER_TYPE_COMPANY) {

					_skipCacheThreadLocal.set(true);

					TransactionCommitCallbackUtil.registerCallback(
						() -> {
							_removePortletPreference(
								portalPreferenceValue.getCompanyId());

							_skipCacheThreadLocal.set(false);

							return null;
						});
				}
			}
			catch (PortalException portalException) {
				throw new ModelListenerException(portalException);
			}
		}

	}

}