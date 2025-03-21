/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.language;

import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoader;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoaderUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.AggregateResourceBundle;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

/**
 * @author Shuyang Zhou
 * @author Kamesh Sampath
 */
public class LanguageResources {

	public static ResourceBundleLoader PORTAL_RESOURCE_BUNDLE_LOADER =
		new ResourceBundleLoader() {

			@Override
			public ResourceBundle loadResourceBundle(Locale locale) {
				return LanguageResources.getResourceBundle(locale);
			}

		};

	public static void clearResourceBundles() {
		_resourceBundles.clear();
	}

	public static String getMessage(Locale locale, String key) {
		if (locale == null) {
			return null;
		}

		ResourceBundle overrideResourceBundle = _getOverrideResourceBundle(
			locale);

		if (overrideResourceBundle != null) {
			String overrideValue = ResourceBundleUtil.getString(
				overrideResourceBundle, key);

			if (overrideValue != null) {
				return overrideValue;
			}
		}

		MapHolder mapHolder = _getMapHolder(locale);

		Map<String, String> languageMap = mapHolder.getMap();

		String value = languageMap.get(key);

		if (value == null) {
			return getMessage(getSuperLocale(locale), key);
		}

		return value;
	}

	public static ResourceBundle getResourceBundle(Locale locale) {
		return _resourceBundles.computeIfAbsent(
			locale,
			key -> new AggregateResourceBundle(
				new DynamicOverrideResourceBundle(key),
				new LanguageResourcesBundle(key)));
	}

	public static Locale getSuperLocale(Locale locale) {
		Long companyId = CompanyThreadLocal.getCompanyId();

		Map<Locale, Locale> superLocales = _superLocalesMap.get(companyId);

		if (superLocales == null) {
			superLocales = new ConcurrentHashMap<>();

			Map<Locale, Locale> previousSuperLocales =
				_superLocalesMap.putIfAbsent(companyId, superLocales);

			if (previousSuperLocales != null) {
				superLocales = previousSuperLocales;
			}
		}

		Locale superLocale = superLocales.get(locale);

		if (superLocale != null) {
			if (superLocale == _nullLocale) {
				return null;
			}

			return superLocale;
		}

		superLocale = _getSuperLocale(locale);

		if (superLocale == null) {
			superLocales.put(locale, _nullLocale);
		}
		else {
			superLocales.put(locale, superLocale);
		}

		return superLocale;
	}

	public void afterPropertiesSet() {
		ResourceBundleLoaderUtil.setPortalResourceBundleLoader(
			PORTAL_RESOURCE_BUNDLE_LOADER);
	}

	public void destroy() {
		for (MapHolder mapHolder : _mapHolders.values()) {
			mapHolder.close();
		}

		_mapHolders.clear();
	}

	private static MapHolder _getMapHolder(Locale locale) {
		MapHolder mapHolder = _mapHolders.get(locale);

		if (mapHolder == null) {
			mapHolder = new MapHolder(locale);

			MapHolder previousMapHolder = _mapHolders.putIfAbsent(
				locale, mapHolder);

			if (previousMapHolder != null) {
				mapHolder.close();

				mapHolder = previousMapHolder;
			}
		}

		return mapHolder;
	}

	private static ResourceBundle _getOverrideResourceBundle(Locale locale) {
		LanguageOverrideProvider languageOverrideProvider =
			_languageOverrideProviderSnapshot.get();

		if (languageOverrideProvider == null) {
			return null;
		}

		return languageOverrideProvider.getOverrideResourceBundle(locale);
	}

	private static Locale _getSuperLocale(Locale locale) {
		String variant = locale.getVariant();

		if (variant.length() > 0) {
			return new Locale(locale.getLanguage(), locale.getCountry());
		}

		String country = locale.getCountry();

		if (country.length() > 0) {
			Locale priorityLocale = LanguageUtil.getLocale(
				locale.getLanguage());

			if (priorityLocale != null) {
				variant = priorityLocale.getVariant();
			}

			if ((priorityLocale != null) && !locale.equals(priorityLocale) &&
				(variant.length() <= 0)) {

				return new Locale(
					priorityLocale.getLanguage(), priorityLocale.getCountry());
			}

			return LocaleUtil.fromLanguageId(locale.getLanguage(), false, true);
		}

		String language = locale.getLanguage();

		if (language.length() > 0) {
			return _blankLocale;
		}

		return null;
	}

	private static final Locale _blankLocale = new Locale(StringPool.BLANK);
	private static final BundleContext _bundleContext =
		SystemBundleUtil.getBundleContext();
	private static final Snapshot<LanguageOverrideProvider>
		_languageOverrideProviderSnapshot = new Snapshot<>(
			LanguageResources.class, LanguageOverrideProvider.class);
	private static final Map<Locale, MapHolder> _mapHolders =
		new ConcurrentHashMap<>();
	private static final Locale _nullLocale = new Locale(StringPool.BLANK);
	private static final Map<Locale, ResourceBundle> _resourceBundles =
		new ConcurrentHashMap<>();
	private static final Map<Long, Map<Locale, Locale>> _superLocalesMap =
		new ConcurrentHashMap<>();

	private static class DynamicOverrideResourceBundle extends ResourceBundle {

		@Override
		public Enumeration<String> getKeys() {
			ResourceBundle overrideResourceBundle = _getOverrideResourceBundle(
				_locale);

			if (overrideResourceBundle != null) {
				return overrideResourceBundle.getKeys();
			}

			return Collections.emptyEnumeration();
		}

		@Override
		public Locale getLocale() {
			return _locale;
		}

		@Override
		protected Object handleGetObject(String key) {
			ResourceBundle overrideResourceBundle = _getOverrideResourceBundle(
				_locale);

			if (overrideResourceBundle != null) {
				try {
					return _handleGetObjectMethodHandle.invokeExact(
						overrideResourceBundle, key);
				}
				catch (Throwable throwable) {
					ReflectionUtil.throwException(throwable);
				}
			}

			return null;
		}

		@Override
		protected Set<String> handleKeySet() {
			ResourceBundle overrideResourceBundle = _getOverrideResourceBundle(
				_locale);

			if (overrideResourceBundle != null) {
				try {
					return (Set<String>)_handleKeySetMethodHandle.invokeExact(
						overrideResourceBundle);
				}
				catch (Throwable throwable) {
					ReflectionUtil.throwException(throwable);
				}
			}

			return Collections.emptySet();
		}

		private DynamicOverrideResourceBundle(Locale locale) {
			_locale = locale;
		}

		private static final MethodHandle _handleGetObjectMethodHandle;
		private static final MethodHandle _handleKeySetMethodHandle;

		static {
			try {
				MethodHandles.Lookup lookup = ReflectionUtil.getImplLookup();

				_handleGetObjectMethodHandle = lookup.findVirtual(
					ResourceBundle.class, "handleGetObject",
					MethodType.methodType(Object.class, String.class));
				_handleKeySetMethodHandle = lookup.findVirtual(
					ResourceBundle.class, "handleKeySet",
					MethodType.methodType(Set.class));
			}
			catch (ReflectiveOperationException reflectiveOperationException) {
				throw new ExceptionInInitializerError(
					reflectiveOperationException);
			}
		}

		private final Locale _locale;

	}

	private static class LanguageResourcesBundle extends ResourceBundle {

		@Override
		public Enumeration<String> getKeys() {
			MapHolder mapHolder = _getMapHolder(_locale);

			Map<String, String> languageMap = mapHolder.getMap();

			Set<String> keySet = languageMap.keySet();

			if (parent == null) {
				return Collections.enumeration(keySet);
			}

			return new ResourceBundleEnumeration(keySet, parent.getKeys());
		}

		@Override
		public Locale getLocale() {
			return _locale;
		}

		@Override
		protected Object handleGetObject(String key) {
			MapHolder mapHolder = _getMapHolder(_locale);

			Map<String, String> languageMap = mapHolder.getMap();

			return languageMap.get(key);
		}

		@Override
		protected Set<String> handleKeySet() {
			MapHolder mapHolder = _getMapHolder(_locale);

			Map<String, String> languageMap = mapHolder.getMap();

			return languageMap.keySet();
		}

		private LanguageResourcesBundle(Locale locale) {
			_locale = locale;

			Locale superLocale = getSuperLocale(locale);

			if (superLocale != null) {
				ResourceBundle superResourceBundle =
					new LanguageResourcesBundle(superLocale);

				ResourceBundle superLocaleOverrideResourceBundle =
					_getOverrideResourceBundle(superLocale);

				if (superLocaleOverrideResourceBundle != null) {
					superResourceBundle = new AggregateResourceBundle(
						superLocaleOverrideResourceBundle, superResourceBundle);
				}

				setParent(superResourceBundle);
			}
		}

		private final Locale _locale;

	}

	private static class MapHolder {

		public void close() {
			for (ServiceReference<ResourceBundle> serviceReference :
					_serviceReferences) {

				_bundleContext.ungetService(serviceReference);
			}
		}

		public Map<String, String> getMap() {
			return _map;
		}

		private MapHolder(Locale locale) {
			try {
				_serviceReferences = _bundleContext.getServiceReferences(
					ResourceBundle.class,
					"(&(!(jakarta.portlet.name=*))(language.id=" +
						LocaleUtil.toLanguageId(locale) + "))");
			}
			catch (InvalidSyntaxException invalidSyntaxException) {
				throw new RuntimeException(invalidSyntaxException);
			}

			for (ServiceReference<ResourceBundle> serviceReference :
					_serviceReferences) {

				ResourceBundle resourceBundle = _bundleContext.getService(
					serviceReference);

				Enumeration<String> enumeration = resourceBundle.getKeys();

				while (enumeration.hasMoreElements()) {
					String key = enumeration.nextElement();

					_map.putIfAbsent(
						key, ResourceBundleUtil.getString(resourceBundle, key));
				}
			}
		}

		private final Map<String, String> _map = new HashMap<>();
		private final Collection<ServiceReference<ResourceBundle>>
			_serviceReferences;

		static {
			try {
				_bundleContext.addServiceListener(
					new ServiceListener() {

						@Override
						public void serviceChanged(ServiceEvent serviceEvent) {
							ServiceReference<?> serviceReference =
								serviceEvent.getServiceReference();

							MapHolder mapHolder = _mapHolders.remove(
								LocaleUtil.fromLanguageId(
									String.valueOf(
										serviceReference.getProperty(
											"language.id")),
									false));

							if (mapHolder != null) {
								mapHolder.close();
							}
						}

					},
					"(&(!(jakarta.portlet.name=*))(language.id=*))");
			}
			catch (InvalidSyntaxException invalidSyntaxException) {
				throw new ExceptionInInitializerError(invalidSyntaxException);
			}
		}

	}

}