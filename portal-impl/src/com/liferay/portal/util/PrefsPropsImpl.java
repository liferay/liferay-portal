/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.util;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.PortalCacheHelperUtil;
import com.liferay.portal.kernel.cache.PortalCacheManagerNames;
import com.liferay.portal.kernel.cache.PortalCacheMapSynchronizeUtil;
import com.liferay.portal.kernel.cluster.ClusterExecutorUtil;
import com.liferay.portal.kernel.cluster.ClusterInvokeThreadLocal;
import com.liferay.portal.kernel.cluster.ClusterRequest;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.PortalPreferences;
import com.liferay.portal.kernel.module.util.ServiceLatch;
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
import com.liferay.portal.model.impl.PortalPreferenceValueCacheModel;
import com.liferay.portal.model.impl.PortalPreferenceValueImpl;
import com.liferay.portlet.PortalPreferencesImpl;
import com.liferay.portlet.PortalPreferencesWrapper;
import com.liferay.portlet.PortletPreferencesImpl;

import java.io.IOException;
import java.io.Serializable;

import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import javax.portlet.PortletPreferences;
import javax.portlet.ReadOnlyException;
import javax.portlet.ValidatorException;

/**
 * @author Brian Wing Shun Chan
 */
public class PrefsPropsImpl implements PrefsProps {

	public void afterPropertiesSet() {
		ServiceLatch serviceLatch = SystemBundleUtil.newServiceLatch();

		serviceLatch.waitFor(
			EntityCache.class,
			entityCache -> {
				PortalCache<?, ?> portalCache = entityCache.getPortalCache(
					PortalPreferenceValueImpl.class);

				PortalCacheMapSynchronizeUtil.synchronize(
					PortalCacheHelperUtil.getPortalCache(
						PortalCacheManagerNames.MULTI_VM,
						portalCache.getPortalCacheName(), portalCache.isMVCC(),
						portalCache.isSharded()),
					null, _synchronizer);
			});

		serviceLatch.openOn(
			() -> {
			});
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
			portletPreferences = new LazyPortletPreferences(
				_emptyPortletPreferences,
				() -> _portalPreferencesLocalService.getPreferences(
					companyId, PortletKeys.PREFS_OWNER_TYPE_COMPANY));
		}

		return portletPreferences;
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

			TransactionCommitCallbackUtil.registerCallback(
				() -> {
					ClusterRequest clusterRequest =
						ClusterRequest.createMulticastRequest(
							new MethodHandler(
								_removePortletPreferenceMethodKey, companyId),
							true);

					clusterRequest.setFireAndForget(true);

					ClusterExecutorUtil.execute(clusterRequest);

					return null;
				});
		}
	}

	private PortletPreferences _fetchPreferences() {
		return _fetchPreferences(PortletKeys.PREFS_OWNER_ID_DEFAULT);
	}

	private PortletPreferences _fetchPreferences(long companyId) {
		return _portletPreferences.computeIfAbsent(
			companyId,
			keyCompanyId -> {
				PortalPreferences portalPreferences =
					_portalPreferencesLocalService.fetchPortalPreferences(
						keyCompanyId, PortletKeys.PREFS_OWNER_TYPE_COMPANY);

				if (portalPreferences == null) {
					return _emptyPortletPreferences;
				}

				PortalPreferencesImpl portalPreferencesImpl =
					(PortalPreferencesImpl)
						_portalPreferenceValueLocalService.getPortalPreferences(
							portalPreferences, false);

				return new PortalPreferencesWrapper(portalPreferencesImpl);
			});
	}

	private static final Log _log = LogFactoryUtil.getLog(PrefsPropsImpl.class);

	private static final Map<Long, PortletPreferences> _portletPreferences =
		new ConcurrentHashMap<>();
	private static final MethodKey _removePortletPreferenceMethodKey =
		new MethodKey(
			PrefsPropsImpl.class, "_removePortletPreference", long.class);

	private final PortletPreferences _emptyPortletPreferences =
		new PortletPreferencesImpl();

	@BeanReference(type = PortalPreferencesLocalService.class)
	private PortalPreferencesLocalService _portalPreferencesLocalService;

	@BeanReference(type = PortalPreferenceValueLocalService.class)
	private PortalPreferenceValueLocalService
		_portalPreferenceValueLocalService;

	private final PortalCacheMapSynchronizeUtil.Synchronizer
		<Serializable, Serializable> _synchronizer =
			new PortalCacheMapSynchronizeUtil.Synchronizer
				<Serializable, Serializable>() {

				@Override
				public void onSynchronize(
					Map<? extends Serializable, ? extends Serializable> map,
					Serializable key, Serializable value, int timeToLive) {

					if (!(value instanceof PortalPreferenceValueCacheModel)) {
						return;
					}

					PortalPreferenceValueCacheModel
						portalPreferenceValueCacheModel =
							(PortalPreferenceValueCacheModel)value;

					try {
						PortalPreferences portalPreferences =
							_portalPreferencesLocalService.getPortalPreferences(
								portalPreferenceValueCacheModel.
									portalPreferencesId);

						if (portalPreferences.getOwnerType() ==
								PortletKeys.PREFS_OWNER_TYPE_COMPANY) {

							_removePortletPreference(
								portalPreferenceValueCacheModel.companyId);
						}
					}
					catch (PortalException portalException) {
						throw new ModelListenerException(portalException);
					}
				}

			};

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

}