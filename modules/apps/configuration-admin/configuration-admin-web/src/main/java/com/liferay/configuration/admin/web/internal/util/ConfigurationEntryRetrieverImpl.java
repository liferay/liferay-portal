/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.configuration.admin.web.internal.util;

import com.liferay.configuration.admin.category.ConfigurationCategory;
import com.liferay.configuration.admin.category.ConfigurationCategoryShowFilter;
import com.liferay.configuration.admin.display.ConfigurationScreen;
import com.liferay.configuration.admin.web.internal.display.ConfigurationCategoryDisplay;
import com.liferay.configuration.admin.web.internal.display.ConfigurationCategoryMenuDisplay;
import com.liferay.configuration.admin.web.internal.display.ConfigurationCategorySectionDisplay;
import com.liferay.configuration.admin.web.internal.display.ConfigurationEntry;
import com.liferay.configuration.admin.web.internal.display.ConfigurationModelConfigurationEntry;
import com.liferay.configuration.admin.web.internal.display.ConfigurationScreenConfigurationEntry;
import com.liferay.configuration.admin.web.internal.model.ConfigurationModel;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.search.capabilities.SearchCapabilities;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jorge Ferrer
 * @author Michael C. Han
 */
@Component(service = ConfigurationEntryRetriever.class)
public class ConfigurationEntryRetrieverImpl
	implements ConfigurationEntryRetriever {

	@Override
	public Collection<ConfigurationScreen> getAllConfigurationScreens() {
		return _configurationScreenServiceTrackerMap.values();
	}

	@Override
	public ConfigurationCategory getConfigurationCategory(
		String configurationCategoryKey) {

		return _configurationCategoryServiceTrackerMap.getService(
			configurationCategoryKey);
	}

	@Override
	public ConfigurationCategoryMenuDisplay getConfigurationCategoryMenuDisplay(
		String configurationCategory, String languageId,
		ExtendedObjectClassDefinition.Scope scope, Serializable scopePK) {

		ConfigurationCategoryDisplay configurationCategoryDisplay =
			new ConfigurationCategoryDisplay(
				getConfigurationCategory(configurationCategory));

		return new ConfigurationCategoryMenuDisplay(
			configurationCategoryDisplay,
			getConfigurationEntries(
				configurationCategory, languageId, scope, scopePK),
			scope);
	}

	@Override
	public List<ConfigurationCategorySectionDisplay>
		getConfigurationCategorySectionDisplays(
			ExtendedObjectClassDefinition.Scope scope, Serializable scopePK) {

		Locale locale = LocaleThreadLocal.getThemeDisplayLocale();

		Map<String, ConfigurationModel> configurationModelsMap =
			_configurationModelRetriever.getConfigurationModels(
				locale.getLanguage(), scope, scopePK);

		Map<String, Set<ConfigurationModel>> categorizedConfigurationModels =
			_configurationModelRetriever.categorizeConfigurationModels(
				configurationModelsMap);

		Map<String, ConfigurationCategorySectionDisplay>
			configurationCategorySectionDisplaysMap = new HashMap<>();

		for (String curConfigurationCategoryKey :
				categorizedConfigurationModels.keySet()) {

			_populateConfigurationCategorySectionDisplay(
				configurationCategorySectionDisplaysMap,
				curConfigurationCategoryKey);
		}

		for (ConfigurationScreen configurationScreen :
				_configurationScreenServiceTrackerMap.values()) {

			if (!scope.equals(configurationScreen.getScope()) ||
				!configurationScreen.isVisible()) {

				continue;
			}

			_populateConfigurationCategorySectionDisplay(
				configurationCategorySectionDisplaysMap,
				configurationScreen.getCategoryKey());
		}

		Set<ConfigurationCategorySectionDisplay>
			configurationCategorySectionDisplays = new TreeSet<>(
				new ConfigurationCategorySectionDisplayComparator());

		configurationCategorySectionDisplays.addAll(
			configurationCategorySectionDisplaysMap.values());

		return new ArrayList<>(configurationCategorySectionDisplays);
	}

	@Override
	public Set<ConfigurationEntry> getConfigurationEntries(
		String configurationCategory, String languageId,
		ExtendedObjectClassDefinition.Scope scope, Serializable scopePK) {

		Set<ConfigurationEntry> configurationEntries = new TreeSet<>(
			_getConfigurationEntryComparator());

		Locale locale = LocaleUtil.fromLanguageId(languageId);

		List<ConfigurationScreen> configurationScreens =
			_configurationScreensServiceTrackerMap.getService(
				configurationCategory);

		if (configurationScreens != null) {
			for (ConfigurationScreen configurationScreen :
					configurationScreens) {

				if (!scope.equals(configurationScreen.getScope()) ||
					!configurationScreen.isVisible()) {

					continue;
				}

				ConfigurationEntry configurationEntry =
					new ConfigurationScreenConfigurationEntry(
						configurationScreen, locale);

				configurationEntries.add(configurationEntry);
			}
		}

		Set<ConfigurationModel> configurationModels =
			_configurationModelRetriever.getConfigurationModels(
				configurationCategory, languageId, scope, scopePK);

		for (ConfigurationModel configurationModel : configurationModels) {
			if (configurationModel.isGenerateUI()) {
				ConfigurationEntry configurationEntry =
					new ConfigurationModelConfigurationEntry(
						configurationModel, locale);

				configurationEntries.add(configurationEntry);
			}
		}

		return configurationEntries;
	}

	@Override
	public ConfigurationScreen getConfigurationScreen(
		String configurationScreenKey) {

		return _configurationScreenServiceTrackerMap.getService(
			configurationScreenKey);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;

		_configurationCategoryServiceTrackerMap =
			ServiceTrackerMapFactory.openSingleValueMap(
				bundleContext, ConfigurationCategory.class, null,
				(serviceReference, emitter) -> {
					ConfigurationCategory configurationCategory =
						bundleContext.getService(serviceReference);

					emitter.emit(configurationCategory.getCategoryKey());
				});

		_configurationCategoryShowFilterServiceTrackerList =
			ServiceTrackerListFactory.open(
				bundleContext, ConfigurationCategoryShowFilter.class);

		_configurationScreenServiceTrackerMap =
			ServiceTrackerMapFactory.openSingleValueMap(
				bundleContext, ConfigurationScreen.class, null,
				(serviceReference, emitter) -> {
					ConfigurationScreen configurationScreen =
						bundleContext.getService(serviceReference);

					emitter.emit(configurationScreen.getKey());
				});
		_configurationScreensServiceTrackerMap =
			ServiceTrackerMapFactory.openMultiValueMap(
				bundleContext, ConfigurationScreen.class, null,
				(serviceReference, emitter) -> {
					ConfigurationScreen configurationScreen =
						bundleContext.getService(serviceReference);

					emitter.emit(configurationScreen.getCategoryKey());
				});
	}

	@Deactivate
	protected void deactivate() {
		_configurationCategoryServiceTrackerMap.close();
		_configurationCategoryShowFilterServiceTrackerList.close();
		_configurationScreenServiceTrackerMap.close();
		_configurationScreensServiceTrackerMap.close();

		_configurationCategoryServiceRegistrations.forEach(
			configurationCategoryServiceRegistration ->
				configurationCategoryServiceRegistration.unregister());
	}

	private Comparator<ConfigurationEntry> _getConfigurationEntryComparator() {
		return new ConfigurationEntryComparator();
	}

	private boolean _isCategorySectionEnabled(String categorySection) {
		if (!_searchCapabilities.isCommerceSupported() &&
			Objects.equals(categorySection, "commerce")) {

			return false;
		}

		return true;
	}

	private boolean _isShow(ConfigurationCategory configurationCategory) {
		for (ConfigurationCategoryShowFilter configurationCategoryShowFilter :
				_configurationCategoryShowFilterServiceTrackerList) {

			if (!configurationCategoryShowFilter.isShow(
					configurationCategory)) {

				return false;
			}
		}

		return true;
	}

	private void _populateConfigurationCategorySectionDisplay(
		Map<String, ConfigurationCategorySectionDisplay>
			configurationCategorySectionDisplaysMap,
		String curConfigurationCategoryKey) {

		ConfigurationCategory curConfigurationCategory =
			_configurationCategoryServiceTrackerMap.getService(
				curConfigurationCategoryKey);

		if (curConfigurationCategory == null) {
			curConfigurationCategory = new AdhocConfigurationCategory(
				curConfigurationCategoryKey);

			_registerConfigurationCategory(curConfigurationCategory);
		}

		if (!_isCategorySectionEnabled(
				curConfigurationCategory.getCategorySection()) ||
			!_isShow(curConfigurationCategory)) {

			return;
		}

		ConfigurationCategorySectionDisplay
			configurationCategorySectionDisplay =
				configurationCategorySectionDisplaysMap.get(
					curConfigurationCategory.getCategorySection());

		if (configurationCategorySectionDisplay == null) {
			configurationCategorySectionDisplay =
				new ConfigurationCategorySectionDisplay(
					curConfigurationCategory.getCategorySection());

			configurationCategorySectionDisplaysMap.put(
				curConfigurationCategory.getCategorySection(),
				configurationCategorySectionDisplay);
		}

		ConfigurationCategoryDisplay configurationCategoryDisplay =
			new ConfigurationCategoryDisplay(curConfigurationCategory);

		configurationCategorySectionDisplay.add(configurationCategoryDisplay);
	}

	private void _registerConfigurationCategory(
		ConfigurationCategory configurationCategory) {

		ServiceRegistration<ConfigurationCategory> serviceRegistration =
			_bundleContext.registerService(
				ConfigurationCategory.class, configurationCategory,
				new HashMapDictionary<>());

		_configurationCategoryServiceRegistrations.add(serviceRegistration);
	}

	private BundleContext _bundleContext;
	private final Set<ServiceRegistration<ConfigurationCategory>>
		_configurationCategoryServiceRegistrations = new HashSet<>();
	private ServiceTrackerMap<String, ConfigurationCategory>
		_configurationCategoryServiceTrackerMap;
	private ServiceTrackerList<ConfigurationCategoryShowFilter>
		_configurationCategoryShowFilterServiceTrackerList;

	@Reference(target = "(filter.visibility=true)")
	private ConfigurationModelRetriever _configurationModelRetriever;

	private ServiceTrackerMap<String, ConfigurationScreen>
		_configurationScreenServiceTrackerMap;
	private ServiceTrackerMap<String, List<ConfigurationScreen>>
		_configurationScreensServiceTrackerMap;

	@Reference
	private SearchCapabilities _searchCapabilities;

	private static class ConfigurationCategorySectionDisplayComparator
		implements Comparator<ConfigurationCategorySectionDisplay> {

		@Override
		public int compare(
			ConfigurationCategorySectionDisplay
				configurationCategorySectionDisplay1,
			ConfigurationCategorySectionDisplay
				configurationCategorySectionDisplay2) {

			String configurationCategorySection1 =
				configurationCategorySectionDisplay1.
					getConfigurationCategorySection();
			String configurationCategorySection2 =
				configurationCategorySectionDisplay2.
					getConfigurationCategorySection();

			int index1 = _orderedConfigurationCategorySections.indexOf(
				configurationCategorySection1);
			int index2 = _orderedConfigurationCategorySections.indexOf(
				configurationCategorySection2);

			if ((index1 == -1) && (index2 == -1)) {
				return configurationCategorySection1.compareTo(
					configurationCategorySection2);
			}
			else if (index1 == -1) {
				return 1;
			}
			else if (index2 == -1) {
				return -1;
			}
			else if (index1 > index2) {
				return 1;
			}
			else if (index2 > index1) {
				return -1;
			}

			return configurationCategorySection1.compareTo(
				configurationCategorySection2);
		}

		private final List<String> _orderedConfigurationCategorySections =
			ListUtil.fromArray(
				"content", "social", "commerce", "platform", "security");

	}

	private static class ConfigurationEntryComparator
		implements Comparator<ConfigurationEntry> {

		@Override
		public int compare(
			ConfigurationEntry configurationEntry1,
			ConfigurationEntry configurationEntry2) {

			String key1 = configurationEntry1.getKey();
			String key2 = configurationEntry2.getKey();

			return key1.compareTo(key2);
		}

	}

}