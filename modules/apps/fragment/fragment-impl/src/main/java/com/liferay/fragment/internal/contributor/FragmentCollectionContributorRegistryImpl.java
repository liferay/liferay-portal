/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.internal.contributor;

import com.liferay.fragment.configuration.FragmentServiceConfiguration;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.contributor.FragmentCollectionContributor;
import com.liferay.fragment.contributor.FragmentCollectionContributorRegistry;
import com.liferay.fragment.model.FragmentComposition;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.processor.FragmentEntryProcessorRegistry;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.fragment.validator.FragmentEntryValidator;
import com.liferay.layout.util.LayoutServiceContextHelper;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.resource.bundle.AggregateResourceBundleLoader;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoader;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.PortalPreferencesLocalService;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PortletKeys;

import jakarta.portlet.PortletPreferences;

import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Jürgen Kappler
 */
@Component(service = FragmentCollectionContributorRegistry.class)
public class FragmentCollectionContributorRegistryImpl
	implements FragmentCollectionContributorRegistry {

	@Override
	public FragmentCollectionContributor getFragmentCollectionContributor(
		String fragmentCollectionKey) {

		FragmentCollectionBag fragmentCollectionBag =
			_serviceTrackerMap.getService(fragmentCollectionKey);

		if (fragmentCollectionBag == null) {
			return null;
		}

		FragmentCollectionContributor fragmentCollectionContributor =
			fragmentCollectionBag._fragmentCollectionContributor;

		if (!fragmentCollectionContributor.isEnabled()) {
			return null;
		}

		return fragmentCollectionContributor;
	}

	@Override
	public List<FragmentCollectionContributor>
		getFragmentCollectionContributors() {

		List<FragmentCollectionContributor> fragmentCollectionContributors =
			new ArrayList<>();

		for (FragmentCollectionBag fragmentCollectionBag :
				_serviceTrackerMap.values()) {

			FragmentCollectionContributor fragmentCollectionContributor =
				fragmentCollectionBag._fragmentCollectionContributor;

			if (!fragmentCollectionContributor.isEnabled()) {
				continue;
			}

			if (MapUtil.isNotEmpty(fragmentCollectionContributor.getNames())) {
				fragmentCollectionContributors.add(
					fragmentCollectionContributor);
			}
		}

		return fragmentCollectionContributors;
	}

	@Override
	public FragmentComposition getFragmentComposition(
		String fragmentCompositionKey) {

		int index = fragmentCompositionKey.indexOf("-composition-");

		if (index == -1) {
			return null;
		}

		FragmentCollectionBag fragmentCollectionBag =
			_serviceTrackerMap.getService(
				fragmentCompositionKey.substring(0, index));

		if (fragmentCollectionBag == null) {
			return null;
		}

		FragmentCollectionContributor fragmentCollectionContributor =
			fragmentCollectionBag._fragmentCollectionContributor;

		if (!fragmentCollectionContributor.isEnabled()) {
			return null;
		}

		Map<String, FragmentComposition> fragmentCompostions =
			fragmentCollectionBag._fragmentCompostions;

		return fragmentCompostions.get(fragmentCompositionKey);
	}

	@Override
	public Map<String, FragmentEntry> getFragmentEntries() {
		Map<String, FragmentEntry> fragmentEntries = new HashMap<>();

		for (FragmentCollectionBag fragmentCollectionBag :
				_serviceTrackerMap.values()) {

			FragmentCollectionContributor fragmentCollectionContributor =
				fragmentCollectionBag._fragmentCollectionContributor;

			if (!fragmentCollectionContributor.isEnabled()) {
				continue;
			}

			fragmentEntries.putAll(fragmentCollectionBag._fragmentEntries);
		}

		return fragmentEntries;
	}

	@Override
	public Map<String, FragmentEntry> getFragmentEntries(Locale locale) {
		Map<String, FragmentEntry> fragmentEntries = new HashMap<>();

		for (FragmentCollectionBag fragmentCollectionBag :
				_serviceTrackerMap.values()) {

			FragmentCollectionContributor fragmentCollectionContributor =
				fragmentCollectionBag._fragmentCollectionContributor;

			if (!fragmentCollectionContributor.isEnabled()) {
				continue;
			}

			for (FragmentEntry fragmentEntry :
					fragmentCollectionContributor.getFragmentEntries(
						_SUPPORTED_FRAGMENT_TYPES, locale)) {

				fragmentEntries.put(
					fragmentEntry.getFragmentEntryKey(), fragmentEntry);
			}
		}

		return fragmentEntries;
	}

	@Override
	public FragmentEntry getFragmentEntry(String fragmentEntryKey) {
		if (fragmentEntryKey == null) {
			return null;
		}

		int index = fragmentEntryKey.indexOf(CharPool.DASH);

		if (index == -1) {
			return null;
		}

		FragmentCollectionBag fragmentCollectionBag =
			_serviceTrackerMap.getService(fragmentEntryKey.substring(0, index));

		if (fragmentCollectionBag == null) {
			return null;
		}

		FragmentCollectionContributor fragmentCollectionContributor =
			fragmentCollectionBag._fragmentCollectionContributor;

		if (!fragmentCollectionContributor.isEnabled()) {
			return null;
		}

		Map<String, FragmentEntry> fragmentEntries =
			fragmentCollectionBag._fragmentEntries;

		return fragmentEntries.get(fragmentEntryKey);
	}

	@Override
	public ResourceBundleLoader getResourceBundleLoader() {
		List<ResourceBundleLoader> resourceBundleLoaders = new ArrayList<>();

		for (FragmentCollectionBag fragmentCollectionBag :
				_serviceTrackerMap.values()) {

			FragmentCollectionContributor fragmentCollectionContributor =
				fragmentCollectionBag._fragmentCollectionContributor;

			ResourceBundleLoader resourceBundleLoader =
				fragmentCollectionContributor.getResourceBundleLoader();

			if (resourceBundleLoader != null) {
				resourceBundleLoaders.add(resourceBundleLoader);
			}
		}

		return new AggregateResourceBundleLoader(
			resourceBundleLoaders.toArray(new ResourceBundleLoader[0]));
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, FragmentCollectionContributor.class,
			"fragment.collection.key",
			new FragmentCollectionContributorServiceTrackerCustomizer(
				bundleContext));
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	@Reference
	protected FragmentEntryProcessorRegistry fragmentEntryProcessorRegistry;

	@Reference
	protected FragmentEntryValidator fragmentEntryValidator;

	private Configuration _getFragmentServiceCompanyConfiguration(
			long companyId)
		throws ConfigurationException {

		try {
			String filterString = StringBundler.concat(
				"(&(", ConfigurationAdmin.SERVICE_FACTORYPID, StringPool.EQUAL,
				FragmentServiceConfiguration.class.getName(), ".scoped",
				")(companyId=", companyId, "))");

			Configuration[] configurations =
				_configurationAdmin.listConfigurations(filterString);

			if (configurations != null) {
				return configurations[0];
			}

			return null;
		}
		catch (InvalidSyntaxException | IOException exception) {
			throw new ConfigurationException(exception);
		}
	}

	private boolean _isPropagateContributedFragmentChanges(long companyId)
		throws ConfigurationException {

		if (_getFragmentServiceCompanyConfiguration(companyId) != null) {
			FragmentServiceConfiguration companyFragmentServiceConfiguration =
				_configurationProvider.getCompanyConfiguration(
					FragmentServiceConfiguration.class, companyId);

			return companyFragmentServiceConfiguration.
				propagateContributedFragmentChanges();
		}

		FragmentServiceConfiguration systemFragmentServiceConfiguration =
			_configurationProvider.getSystemConfiguration(
				FragmentServiceConfiguration.class);

		return systemFragmentServiceConfiguration.
			propagateContributedFragmentChanges();
	}

	private void _updateFragmentEntryLinks(
		Map<String, FragmentEntry> fragmentEntries) {

		_companyLocalService.forEachCompany(
			company -> {
				try {
					if (!_isPropagateContributedFragmentChanges(
							company.getCompanyId())) {

						PortletPreferences portletPreferences =
							_portalPreferencesLocalService.getPreferences(
								company.getCompanyId(),
								PortletKeys.PREFS_OWNER_TYPE_COMPANY);

						portletPreferences.setValue(
							"alreadyPropagateContributedFragmentChanges",
							Boolean.FALSE.toString());

						portletPreferences.store();

						return;
					}
				}
				catch (Exception exception) {
					_log.error(exception);

					return;
				}

				try (AutoCloseable autoCloseable =
						_layoutServiceContextHelper.
							getServiceContextAutoCloseable(company)) {

					Set<String> keys = fragmentEntries.keySet();

					List<FragmentEntryLink> fragmentEntryLinks =
						_fragmentEntryLinkLocalService.getFragmentEntryLinks(
							company.getCompanyId(),
							keys.toArray(new String[0]));

					for (FragmentEntryLink fragmentEntryLink :
							fragmentEntryLinks) {

						FragmentEntry fragmentEntry = fragmentEntries.get(
							fragmentEntryLink.getRendererKey());

						if (fragmentEntry == null) {
							continue;
						}

						try {
							_fragmentEntryLinkLocalService.updateLatestChanges(
								fragmentEntry, fragmentEntryLink);
						}
						catch (PortalException portalException) {
							_log.error(portalException);
						}
					}
				}
				catch (Exception exception) {
					_log.error(exception);
				}
			});
	}

	private boolean _validateFragmentEntry(FragmentEntry fragmentEntry) {
		JSONObject configurationJSONObject = _jsonFactory.safeCreateJSONObject(
			fragmentEntry.getConfiguration(), true);

		try {
			fragmentEntryValidator.validateConfiguration(
				configurationJSONObject);
			fragmentEntryValidator.validateTypeOptions(
				fragmentEntry.getType(), fragmentEntry.getTypeOptions());

			fragmentEntryProcessorRegistry.validateFragmentEntryHTML(
				fragmentEntry.getHtml(), configurationJSONObject);

			return true;
		}
		catch (PortalException portalException) {
			_log.error("Unable to validate fragment entry", portalException);
		}

		return false;
	}

	private static final int[] _SUPPORTED_FRAGMENT_TYPES = {
		FragmentConstants.TYPE_COMPONENT, FragmentConstants.TYPE_INPUT,
		FragmentConstants.TYPE_SECTION
	};

	private static final Log _log = LogFactoryUtil.getLog(
		FragmentCollectionContributorRegistryImpl.class);

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private ConfigurationAdmin _configurationAdmin;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private LayoutServiceContextHelper _layoutServiceContextHelper;

	@Reference
	private PortalPreferencesLocalService _portalPreferencesLocalService;

	private ServiceTrackerMap<String, FragmentCollectionBag> _serviceTrackerMap;

	private static class FragmentCollectionBag {

		private FragmentCollectionBag(
			FragmentCollectionContributor fragmentCollectionContributor,
			Map<String, FragmentComposition> fragmentCompostions,
			Map<String, FragmentEntry> fragmentEntries) {

			_fragmentCollectionContributor = fragmentCollectionContributor;
			_fragmentCompostions = fragmentCompostions;
			_fragmentEntries = fragmentEntries;
		}

		private final FragmentCollectionContributor
			_fragmentCollectionContributor;
		private final Map<String, FragmentComposition> _fragmentCompostions;
		private final Map<String, FragmentEntry> _fragmentEntries;

	}

	private class FragmentCollectionContributorServiceTrackerCustomizer
		implements ServiceTrackerCustomizer
			<FragmentCollectionContributor, FragmentCollectionBag> {

		@Override
		public FragmentCollectionBag addingService(
			ServiceReference<FragmentCollectionContributor> serviceReference) {

			FragmentCollectionContributor fragmentCollectionContributor =
				_bundleContext.getService(serviceReference);

			Map<String, FragmentComposition> fragmentCompositions =
				new HashMap<>();
			Map<String, FragmentEntry> fragmentEntries = new HashMap<>();

			for (FragmentComposition fragmentComposition :
					fragmentCollectionContributor.getFragmentCompositions()) {

				fragmentCompositions.put(
					fragmentComposition.getFragmentCompositionKey(),
					fragmentComposition);
			}

			for (FragmentEntry fragmentEntry :
					fragmentCollectionContributor.getFragmentEntries(
						_SUPPORTED_FRAGMENT_TYPES)) {

				if (!_validateFragmentEntry(fragmentEntry)) {
					continue;
				}

				fragmentEntries.put(
					fragmentEntry.getFragmentEntryKey(), fragmentEntry);
			}

			_updateFragmentEntryLinks(fragmentEntries);

			return new FragmentCollectionBag(
				fragmentCollectionContributor, fragmentCompositions,
				fragmentEntries);
		}

		@Override
		public void modifiedService(
			ServiceReference<FragmentCollectionContributor> serviceReference,
			FragmentCollectionBag fragmentCollectionBag) {
		}

		@Override
		public void removedService(
			ServiceReference<FragmentCollectionContributor> serviceReference,
			FragmentCollectionBag fragmentCollectionBag) {

			_bundleContext.ungetService(serviceReference);
		}

		private FragmentCollectionContributorServiceTrackerCustomizer(
			BundleContext bundleContext) {

			_bundleContext = bundleContext;
		}

		private final BundleContext _bundleContext;

	}

}