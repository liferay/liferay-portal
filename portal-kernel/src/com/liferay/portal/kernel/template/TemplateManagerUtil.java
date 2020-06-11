/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.kernel.template;

import com.liferay.portal.kernel.configuration.Filter;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.registry.ServiceReference;
import com.liferay.registry.ServiceTracker;
import com.liferay.registry.ServiceTrackerCustomizer;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Tina Tian
 * @author Raymond Augé
 */
public class TemplateManagerUtil {

	public static void destroy() {
		_instance._destroy();
	}

	public static void destroy(ClassLoader classLoader) {
		_instance._destroy(classLoader);
	}

	public static Set<String> getSupportedLanguageTypes(String propertyKey) {
		return _instance._getSupportedLanguageTypes(propertyKey);
	}

	public static Template getTemplate(
			String templateManagerName,
			List<TemplateResource> templateResources, boolean restricted)
		throws TemplateException {

		return _instance._getTemplate(
			templateManagerName, templateResources, restricted);
	}

	public static Template getTemplate(
			String templateManagerName,
			List<TemplateResource> templateResources,
			TemplateResource errorTemplateResource, boolean restricted)
		throws TemplateException {

		return _instance._getTemplate(
			templateManagerName, templateResources, errorTemplateResource,
			restricted);
	}

	public static Template getTemplate(
			String templateManagerName, TemplateResource templateResource,
			boolean restricted)
		throws TemplateException {

		return _instance._getTemplate(
			templateManagerName, templateResource, restricted);
	}

	public static Template getTemplate(
			String templateManagerName, TemplateResource templateResource,
			TemplateResource errorTemplateResource, boolean restricted)
		throws TemplateException {

		return _instance._getTemplate(
			templateManagerName, templateResource, errorTemplateResource,
			restricted);
	}

	public static TemplateManager getTemplateManager(
		String templateManagerName) {

		return _instance._getTemplateManager(templateManagerName);
	}

	public static Set<String> getTemplateManagerNames() {
		return _instance._getTemplateManagerNames();
	}

	public static Map<String, TemplateManager> getTemplateManagers() {
		return _instance._getTemplateManagers();
	}

	public static boolean hasTemplateManager(String templateManagerName) {
		return _instance._hasTemplateManager(templateManagerName);
	}

	private TemplateManagerUtil() {
		Registry registry = RegistryUtil.getRegistry();

		com.liferay.registry.Filter filter = registry.getFilter(
			"(&(language.type=*)(objectClass=" +
				TemplateManager.class.getName() + "))");

		_serviceTracker = registry.trackServices(
			filter, new TemplateManagerServiceTrackerCustomizer());

		_serviceTracker.open();
	}

	private void _destroy() {
		Map<String, TemplateManager> templateManagers = _getTemplateManagers();

		for (TemplateManager templateManager : templateManagers.values()) {
			templateManager.destroy();
		}

		templateManagers.clear();
	}

	private void _destroy(ClassLoader classLoader) {
		Map<String, TemplateManager> templateManagers = _getTemplateManagers();

		for (TemplateManager templateManager : templateManagers.values()) {
			templateManager.destroy(classLoader);
		}
	}

	private Set<String> _getSupportedLanguageTypes(String propertyKey) {
		Set<String> supportedLanguageTypes = _supportedLanguageTypes.get(
			propertyKey);

		if (supportedLanguageTypes != null) {
			return supportedLanguageTypes;
		}

		Map<String, TemplateManager> templateManagers = _getTemplateManagers();

		supportedLanguageTypes = new HashSet<>();

		for (String templateManagerName : templateManagers.keySet()) {
			String content = PropsUtil.get(
				propertyKey, new Filter(templateManagerName));

			if (Validator.isNotNull(content)) {
				supportedLanguageTypes.add(templateManagerName);
			}
		}

		supportedLanguageTypes = Collections.unmodifiableSet(
			supportedLanguageTypes);

		_supportedLanguageTypes.put(propertyKey, supportedLanguageTypes);

		return supportedLanguageTypes;
	}

	private Template _getTemplate(
			String templateManagerName,
			List<TemplateResource> templateResources, boolean restricted)
		throws TemplateException {

		TemplateManager templateManager = _getTemplateManagerChecked(
			templateManagerName);

		return templateManager.getTemplate(templateResources, restricted);
	}

	private Template _getTemplate(
			String templateManagerName,
			List<TemplateResource> templateResources,
			TemplateResource errorTemplateResource, boolean restricted)
		throws TemplateException {

		TemplateManager templateManager = _getTemplateManagerChecked(
			templateManagerName);

		return templateManager.getTemplate(
			templateResources, errorTemplateResource, restricted);
	}

	private Template _getTemplate(
			String templateManagerName, TemplateResource templateResource,
			boolean restricted)
		throws TemplateException {

		TemplateManager templateManager = _getTemplateManagerChecked(
			templateManagerName);

		return templateManager.getTemplate(templateResource, restricted);
	}

	private Template _getTemplate(
			String templateManagerName, TemplateResource templateResource,
			TemplateResource errorTemplateResource, boolean restricted)
		throws TemplateException {

		TemplateManager templateManager = _getTemplateManagerChecked(
			templateManagerName);

		return templateManager.getTemplate(
			templateResource, errorTemplateResource, restricted);
	}

	private TemplateManager _getTemplateManager(String templateManagerName) {
		Collection<TemplateManager> templateManagers =
			_templateManagers.values();

		for (TemplateManager templateManager : templateManagers) {
			if (templateManagerName.equals(templateManager.getName())) {
				return templateManager;
			}
		}

		return null;
	}

	private TemplateManager _getTemplateManagerChecked(
			String templateManagerName)
		throws TemplateException {

		Collection<TemplateManager> templateManagers =
			_templateManagers.values();

		for (TemplateManager templateManager : templateManagers) {
			if (templateManagerName.equals(templateManager.getName())) {
				return templateManager;
			}
		}

		throw new TemplateException(
			"Unsupported template manager " + templateManagerName);
	}

	private Set<String> _getTemplateManagerNames() {
		Map<String, TemplateManager> templateManagers = _getTemplateManagers();

		return templateManagers.keySet();
	}

	private Map<String, TemplateManager> _getTemplateManagers() {
		Map<String, TemplateManager> map = new HashMap<>();

		Collection<TemplateManager> templateManagers =
			_templateManagers.values();

		for (TemplateManager templateManager : templateManagers) {
			map.put(templateManager.getName(), templateManager);
		}

		return Collections.unmodifiableMap(map);
	}

	private boolean _hasTemplateManager(String templateManagerName) {
		Collection<TemplateManager> templateManagers =
			_templateManagers.values();

		for (TemplateManager templateManager : templateManagers) {
			if (templateManagerName.equals(templateManager.getName())) {
				return true;
			}
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		TemplateManagerUtil.class);

	private static final TemplateManagerUtil _instance =
		new TemplateManagerUtil();

	private final ServiceTracker<TemplateManager, TemplateManager>
		_serviceTracker;
	private final Map<String, Set<String>> _supportedLanguageTypes =
		new ConcurrentHashMap<>();
	private final Map<ServiceReference<TemplateManager>, TemplateManager>
		_templateManagers = new ConcurrentHashMap<>();

	private class TemplateManagerServiceTrackerCustomizer
		implements ServiceTrackerCustomizer<TemplateManager, TemplateManager> {

		@Override
		public TemplateManager addingService(
			ServiceReference<TemplateManager> serviceReference) {

			Registry registry = RegistryUtil.getRegistry();

			TemplateManager templateManager = registry.getService(
				serviceReference);

			String name = templateManager.getName();

			try {
				templateManager.init();

				_templateManagers.put(serviceReference, templateManager);
			}
			catch (TemplateException te) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"unable to init " + name + " Template Manager ", te);
				}
			}

			return templateManager;
		}

		@Override
		public void modifiedService(
			ServiceReference<TemplateManager> serviceReference,
			TemplateManager templateManager) {

			removedService(serviceReference, templateManager);

			addingService(serviceReference);
		}

		@Override
		public void removedService(
			ServiceReference<TemplateManager> serviceReference,
			TemplateManager templateManager) {

			Registry registry = RegistryUtil.getRegistry();

			registry.ungetService(serviceReference);

			_templateManagers.remove(serviceReference);

			templateManager.destroy();
		}

	}

}