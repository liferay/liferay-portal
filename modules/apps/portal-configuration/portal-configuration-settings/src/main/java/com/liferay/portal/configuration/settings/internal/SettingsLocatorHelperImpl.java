/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.configuration.settings.internal;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.osgi.service.tracker.collections.map.ServiceReferenceMapperFactory;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.concurrent.DCLSingleton;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.configuration.metatype.definitions.ExtendedMetaTypeInformation;
import com.liferay.portal.configuration.metatype.definitions.ExtendedMetaTypeService;
import com.liferay.portal.configuration.settings.internal.scoped.configuration.admin.service.ScopedConfigurationManagedServiceFactory;
import com.liferay.portal.configuration.settings.internal.util.ConfigurationPidUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactory;
import com.liferay.portal.kernel.resource.manager.ClassLoaderResourceManager;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.settings.ConfigurationBeanSettings;
import com.liferay.portal.kernel.settings.LocationVariableResolver;
import com.liferay.portal.kernel.settings.PortletPreferencesSettings;
import com.liferay.portal.kernel.settings.PropertiesSettings;
import com.liferay.portal.kernel.settings.Settings;
import com.liferay.portal.kernel.settings.SettingsDescriptor;
import com.liferay.portal.kernel.settings.SettingsLocatorHelper;
import com.liferay.portal.kernel.settings.definition.ConfigurationPidMapping;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.PrefsProps;
import com.liferay.portal.kernel.util.Props;

import jakarta.portlet.PortletPreferences;

import java.io.Serializable;

import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.BundleTrackerCustomizer;

/**
 * @author Iván Zaera
 * @author Jorge Ferrer
 * @author Shuyang Zhou
 */
@Component(service = SettingsLocatorHelper.class)
public class SettingsLocatorHelperImpl implements SettingsLocatorHelper {

	@Override
	public Settings getCompanyConfigurationBeanSettings(
		long companyId, String configurationPid, Settings parentSettings) {

		return _getScopedConfigurationBeanSettings(
			ExtendedObjectClassDefinition.Scope.COMPANY, companyId,
			configurationPid, parentSettings);
	}

	@Override
	public Settings getCompanyPortletPreferencesSettings(
		long companyId, String settingsId, Settings parentSettings) {

		return new PortletPreferencesSettings(
			_portletPreferencesLocalService.getStrictPreferences(
				companyId, companyId, PortletKeys.PREFS_OWNER_TYPE_COMPANY, 0,
				settingsId),
			parentSettings);
	}

	@Override
	public Settings getConfigurationBeanSettings(String configurationPid) {
		_bundleTrackerDCLSingleton.getSingleton(this::_createBundleTracker);

		Settings configurationBeanSettings = _configurationBeanSettings.get(
			_toOCDPid(configurationPid));

		if (configurationBeanSettings == null) {
			return _portalPropertiesSettings;
		}

		return configurationBeanSettings;
	}

	@Override
	public ConfigurationPidMapping getConfigurationPidMapping(
		String configurationId) {

		_bundleTrackerDCLSingleton.getSingleton(this::_createBundleTracker);

		return _serviceTrackerMap.getService(configurationId);
	}

	@Override
	public Settings getGroupConfigurationBeanSettings(
		long groupId, String configurationPid, Settings parentSettings) {

		return _getScopedConfigurationBeanSettings(
			ExtendedObjectClassDefinition.Scope.GROUP, groupId,
			configurationPid, parentSettings);
	}

	@Override
	public Settings getGroupPortletPreferencesSettings(
		long groupId, String settingsId, Settings parentSettings) {

		try {
			Group group = _groupLocalService.getGroup(groupId);

			return new PortletPreferencesSettings(
				_portletPreferencesLocalService.getStrictPreferences(
					group.getCompanyId(), groupId,
					PortletKeys.PREFS_OWNER_TYPE_GROUP, 0, settingsId),
				parentSettings);
		}
		catch (PortalException portalException) {
			throw new SystemException(portalException);
		}
	}

	@Override
	public Settings getPortalPreferencesSettings(
		long companyId, Settings parentSettings) {

		return new PortletPreferencesSettings(
			_prefsProps.getPreferences(companyId), parentSettings);
	}

	@Override
	public Settings getPortletInstanceConfigurationBeanSettings(
		String portletId, String configurationPid, Settings parentSettings) {

		return _getScopedConfigurationBeanSettings(
			ExtendedObjectClassDefinition.Scope.PORTLET_INSTANCE, portletId,
			configurationPid, parentSettings);
	}

	@Override
	public Settings getPortletInstancePortletPreferencesSettings(
		long companyId, long ownerId, int ownerType, long plid,
		String portletId, Settings parentSettings) {

		return new PortletPreferencesSettings(
			_getPortletInstancePortletPreferences(
				companyId, ownerId, ownerType, plid, portletId),
			parentSettings);
	}

	@Override
	public Settings getPortletInstancePortletPreferencesSettings(
		long companyId, long plid, String portletId, Settings parentSettings) {

		return getPortletInstancePortletPreferencesSettings(
			companyId, PortletKeys.PREFS_OWNER_ID_DEFAULT,
			PortletKeys.PREFS_OWNER_TYPE_LAYOUT, plid, portletId,
			parentSettings);
	}

	@Override
	public Settings getServerSettings(String settingsId) {
		return getConfigurationBeanSettings(settingsId);
	}

	@Override
	public SettingsDescriptor getSettingsDescriptor(String settingsId) {
		settingsId = PortletIdCodec.decodePortletName(settingsId);

		ConfigurationPidMapping configurationPidMapping =
			getConfigurationPidMapping(settingsId);

		Class<?> clazz = configurationPidMapping.getConfigurationBeanClass();

		if (clazz.getAnnotation(Settings.Config.class) == null) {
			return new ConfigurationBeanClassSettingsDescriptor(clazz);
		}

		return new AnnotatedSettingsDescriptor(clazz);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_portalPropertiesSettings = new PropertiesSettings(
			new LocationVariableResolver(
				new ClassLoaderResourceManager(
					PortalClassLoaderUtil.getClassLoader()),
				this),
			_props.getProperties());

		_bundleContext = bundleContext;

		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, ConfigurationPidMapping.class, null,
			ServiceReferenceMapperFactory.createFromFunction(
				bundleContext, ConfigurationPidMapping::getConfigurationPid));
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();

		_bundleTrackerDCLSingleton.destroy(BundleTracker::close);
	}

	private BundleTracker<?> _createBundleTracker() {
		BundleTracker<?> bundleTracker = new BundleTracker<>(
			_bundleContext, Bundle.ACTIVE,
			new ConfigurationBeanClassBundleTrackerCustomizer());

		bundleTracker.open();

		return bundleTracker;
	}

	private PortletPreferences _getPortletInstancePortletPreferences(
		long companyId, long ownerId, int ownerType, long plid,
		String portletId) {

		if (plid != LayoutConstants.DEFAULT_PLID) {
			Layout layout = _layoutLocalService.fetchLayout(plid);

			if (layout != null) {
				return _portletPreferencesFactory.getStrictPortletSetup(
					layout, portletId);
			}
		}

		if (PortletIdCodec.hasUserId(portletId)) {
			ownerId = PortletIdCodec.decodeUserId(portletId);
			ownerType = PortletKeys.PREFS_OWNER_TYPE_USER;
		}

		return _portletPreferencesLocalService.getStrictPreferences(
			companyId, ownerId, ownerType, plid, portletId);
	}

	private Settings _getScopedConfigurationBeanSettings(
		ExtendedObjectClassDefinition.Scope scope, Serializable scopePK,
		String configurationPid, Settings parentSettings) {

		_bundleTrackerDCLSingleton.getSingleton(this::_createBundleTracker);

		ScopedConfigurationManagedServiceFactory
			scopedConfigurationManagedServiceFactory =
				_scopedConfigurationManagedServiceFactories.get(
					configurationPid);

		if (scopedConfigurationManagedServiceFactory == null) {
			return parentSettings;
		}

		Object configurationBean =
			scopedConfigurationManagedServiceFactory.getConfiguration(
				scope, scopePK);

		if (configurationBean == null) {
			return parentSettings;
		}

		return new ConfigurationBeanSettings(
			scopedConfigurationManagedServiceFactory.
				getLocationVariableResolver(),
			configurationBean, parentSettings);
	}

	private SafeCloseable _registerConfigurationBeanClass(
		Class<?> configurationBeanClass) {

		if (configurationBeanClass.getAnnotation(Meta.OCD.class) == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Skipping registration for class because Meta.OCD is " +
						"missing: " + configurationBeanClass.getName());
			}

			return null;
		}

		Set<String> requiredKeys = new HashSet<>();

		for (Method method : configurationBeanClass.getMethods()) {
			Meta.AD annotation = method.getAnnotation(Meta.AD.class);

			if (annotation == null) {
				continue;
			}

			if (annotation.required()) {
				requiredKeys.add(method.getName());
			}
		}

		String configurationPid = ConfigurationPidUtil.getConfigurationPid(
			configurationBeanClass);

		if (_configurationBeanSettings.containsKey(configurationPid)) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Skipping registration for class because it is already " +
						"registered: " + configurationPid);
			}

			return null;
		}

		CountDownLatch countDownLatch = new CountDownLatch(1);

		LocationVariableResolver locationVariableResolver =
			new LocationVariableResolver(
				new ClassLoaderResourceManager(
					configurationBeanClass.getClassLoader()),
				SettingsLocatorHelperImpl.this);

		ServiceRegistration<?> managedServiceServiceRegistration =
			_bundleContext.registerService(
				ManagedService.class,
				properties -> {
					if (properties == null) {
						properties = new HashMapDictionary<>();
					}

					Enumeration<String> enumeration = properties.keys();

					Set<String> localRequiredKeys = new HashSet<>(requiredKeys);

					while (enumeration.hasMoreElements()) {
						localRequiredKeys.remove(enumeration.nextElement());
					}

					if (localRequiredKeys.isEmpty()) {
						_configurationBeanSettings.put(
							configurationPid,
							new ConfigurationBeanSettings(
								locationVariableResolver,
								ConfigurableUtil.createConfigurable(
									configurationBeanClass, properties),
								_portalPropertiesSettings));
					}

					countDownLatch.countDown();
				},
				MapUtil.singletonDictionary(
					Constants.SERVICE_PID, configurationPid));

		try {
			countDownLatch.await();
		}
		catch (InterruptedException interruptedException) {
			_log.error(interruptedException);
		}

		ScopedConfigurationManagedServiceFactory
			scopedConfigurationManagedServiceFactory =
				new ScopedConfigurationManagedServiceFactory(
					configurationBeanClass, locationVariableResolver);

		ServiceRegistration<?> managedServiceFactoryServiceRegistration =
			_bundleContext.registerService(
				ManagedServiceFactory.class,
				scopedConfigurationManagedServiceFactory,
				MapUtil.singletonDictionary(
					Constants.SERVICE_PID, configurationPid + ".scoped"));

		_scopedConfigurationManagedServiceFactories.put(
			scopedConfigurationManagedServiceFactory.getName(),
			scopedConfigurationManagedServiceFactory);

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Registering configuration class: " +
					configurationBeanClass.getName());
		}

		ServiceRegistration<?> configurationPidMappingServiceRegistration =
			_bundleContext.registerService(
				ConfigurationPidMapping.class,
				new ConfigurationPidMapping() {

					@Override
					public Class<?> getConfigurationBeanClass() {
						return configurationBeanClass;
					}

					@Override
					public String getConfigurationPid() {
						return configurationPid;
					}

				},
				null);

		return () -> {
			configurationPidMappingServiceRegistration.unregister();

			_scopedConfigurationManagedServiceFactories.remove(
				configurationPid);

			managedServiceFactoryServiceRegistration.unregister();

			_configurationBeanSettings.remove(configurationPid);

			managedServiceServiceRegistration.unregister();
		};
	}

	private String _toOCDPid(String configurationPid) {
		ConfigurationPidMapping configurationPidMapping =
			getConfigurationPidMapping(configurationPid);

		if (configurationPidMapping == null) {
			return configurationPid;
		}

		Class<?> clazz = configurationPidMapping.getConfigurationBeanClass();

		if (clazz.getAnnotation(Settings.Config.class) != null) {
			return configurationPid;
		}

		return ConfigurationPidUtil.getConfigurationPid(clazz);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SettingsLocatorHelperImpl.class);

	private BundleContext _bundleContext;
	private final DCLSingleton<BundleTracker<?>> _bundleTrackerDCLSingleton =
		new DCLSingleton<>();
	private final Map<String, Settings> _configurationBeanSettings =
		new ConcurrentHashMap<>();

	@Reference
	private ExtendedMetaTypeService _extendedMetaTypeService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private LayoutLocalService _layoutLocalService;

	private Settings _portalPropertiesSettings;

	@Reference
	private PortletPreferencesFactory _portletPreferencesFactory;

	@Reference
	private PortletPreferencesLocalService _portletPreferencesLocalService;

	@Reference
	private PrefsProps _prefsProps;

	@Reference
	private Props _props;

	private final Map<String, ScopedConfigurationManagedServiceFactory>
		_scopedConfigurationManagedServiceFactories = new ConcurrentHashMap<>();
	private ServiceTrackerMap<String, ConfigurationPidMapping>
		_serviceTrackerMap;

	private class ConfigurationBeanClassBundleTrackerCustomizer
		implements BundleTrackerCustomizer<List<SafeCloseable>> {

		@Override
		public List<SafeCloseable> addingBundle(
			Bundle bundle, BundleEvent bundleEvent) {

			String bundleSymbolicName = bundle.getSymbolicName();

			if (bundleSymbolicName.endsWith(".test")) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						"Skipping bundle (do not check test modules): " +
							bundleSymbolicName);
				}

				return null;
			}

			ExtendedMetaTypeInformation metaTypeInformation =
				_extendedMetaTypeService.getMetaTypeInformation(bundle);

			if (metaTypeInformation == null) {
				return null;
			}

			List<SafeCloseable> autoCloseables = new ArrayList<>();

			for (String pid :
					ArrayUtil.append(
						metaTypeInformation.getPids(),
						metaTypeInformation.getFactoryPids())) {

				Class<?> configurationBeanClass;

				try {
					configurationBeanClass = bundle.loadClass(pid);
				}
				catch (ClassNotFoundException classNotFoundException) {
					if (_log.isDebugEnabled()) {
						_log.debug(
							"Class not found: " +
								classNotFoundException.getMessage());
					}

					continue;
				}

				SafeCloseable safeCloseable = _registerConfigurationBeanClass(
					configurationBeanClass);

				if (safeCloseable != null) {
					autoCloseables.add(safeCloseable);
				}
			}

			if (ListUtil.isEmpty(autoCloseables)) {
				return null;
			}

			return autoCloseables;
		}

		@Override
		public void modifiedBundle(
			Bundle bundle, BundleEvent bundleEvent,
			List<SafeCloseable> autoCloseables) {
		}

		@Override
		public void removedBundle(
			Bundle bundle, BundleEvent bundleEvent,
			List<SafeCloseable> safeCloseables) {

			if (ListUtil.isEmpty(safeCloseables)) {
				return;
			}

			if (_log.isDebugEnabled()) {
				_log.debug(
					"Un-registering configuration classes for bundle: " +
						bundle.getSymbolicName());
			}

			for (SafeCloseable safeCloseable : safeCloseables) {
				safeCloseable.close();
			}
		}

	}

}