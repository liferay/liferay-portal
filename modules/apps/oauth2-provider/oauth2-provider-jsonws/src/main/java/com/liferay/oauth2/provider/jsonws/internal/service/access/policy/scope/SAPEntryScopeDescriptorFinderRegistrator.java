/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.jsonws.internal.service.access.policy.scope;

import com.liferay.oauth2.provider.jsonws.internal.configuration.OAuth2JSONWSConfiguration;
import com.liferay.oauth2.provider.jsonws.internal.constants.OAuth2JSONWSConstants;
import com.liferay.oauth2.provider.scope.spi.scope.descriptor.ScopeDescriptor;
import com.liferay.oauth2.provider.scope.spi.scope.finder.ScopeFinder;
import com.liferay.osgi.util.ServiceTrackerFactory;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.security.service.access.policy.model.SAPEntry;
import com.liferay.portal.security.service.access.policy.service.SAPEntryLocalService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Tomas Polesovsky
 */
@Component(
	configurationPid = "com.liferay.oauth2.provider.jsonws.internal.configuration.OAuth2JSONWSConfiguration",
	service = SAPEntryScopeDescriptorFinderRegistrator.class
)
public class SAPEntryScopeDescriptorFinderRegistrator {

	public boolean contains(String jaxRsApplicationName) {
		return _jaxRsApplicationNames.contains(jaxRsApplicationName);
	}

	public List<SAPEntryScope> getRegisteredSAPEntryScopes(long companyId) {
		SAPEntryScopeDescriptorFinder sapEntryScopeDescriptorFinder =
			_registeredSAPEntryScopeDescriptorFinders.get(companyId);

		return new ArrayList<>(
			sapEntryScopeDescriptorFinder.getSAPEntryScopes());
	}

	public void register(long companyId) {
		try {
			SAPEntryScopeDescriptorFinder sapEntryScopeDescriptorFinder =
				new SAPEntryScopeDescriptorFinder(
					() -> _loadSAPEntryScopes(companyId),
					_defaultScopeDescriptor);

			_scopeDescriptorServiceRegistrations.compute(
				companyId,
				(key, serviceRegistration) -> {
					if (serviceRegistration != null) {
						serviceRegistration.unregister();
					}

					return _bundleContext.registerService(
						ScopeDescriptor.class, sapEntryScopeDescriptorFinder,
						_buildScopeDescriptorProperties(companyId));
				});

			Dictionary<String, Object> properties =
				HashMapDictionaryBuilder.<String, Object>put(
					"companyId", String.valueOf(companyId)
				).put(
					"osgi.jaxrs.name", OAuth2JSONWSConstants.APPLICATION_NAME
				).put(
					"sap.scope.finder", Boolean.TRUE
				).build();

			_scopeFinderServiceRegistrations.compute(
				companyId,
				(key, serviceRegistration) -> {
					if (serviceRegistration != null) {
						serviceRegistration.unregister();
					}

					serviceRegistration = _bundleContext.registerService(
						ScopeFinder.class, sapEntryScopeDescriptorFinder,
						properties);

					_registeredSAPEntryScopeDescriptorFinders.put(
						companyId, sapEntryScopeDescriptorFinder);

					return serviceRegistration;
				});
		}
		catch (Exception exception) {
			_log.error(
				"Unable to register SAP entry scope descriptor finder for " +
					"company " + companyId,
				exception);
		}
	}

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		_bundleContext = bundleContext;

		_serviceTracker = ServiceTrackerFactory.open(
			bundleContext,
			"(&(objectClass=" + ScopeFinder.class.getName() +
				")(osgi.jaxrs.name=*)(sap.scope.finder=true))",
			new ServiceTrackerCustomizer<ScopeFinder, ScopeFinder>() {

				@Override
				public ScopeFinder addingService(
					ServiceReference<ScopeFinder> serviceReference) {

					_jaxRsApplicationNames.add(
						GetterUtil.getString(
							serviceReference.getProperty("osgi.jaxrs.name")));

					_resetProperties();

					return bundleContext.getService(serviceReference);
				}

				@Override
				public void modifiedService(
					ServiceReference<ScopeFinder> serviceReference,
					ScopeFinder scopeFinder) {
				}

				@Override
				public void removedService(
					ServiceReference<ScopeFinder> serviceReference,
					ScopeFinder scopeFinder) {

					bundleContext.ungetService(serviceReference);

					_jaxRsApplicationNames.remove(
						GetterUtil.getString(
							serviceReference.getProperty("osgi.jaxrs.name")));

					_resetProperties();
				}

			});

		OAuth2JSONWSConfiguration oAuth2JSONWSConfiguration =
			ConfigurableUtil.createConfigurable(
				OAuth2JSONWSConfiguration.class, properties);

		_removeSAPEntryOAuth2Prefix =
			oAuth2JSONWSConfiguration.removeSAPEntryOAuth2Prefix();

		_sapEntryOAuth2Prefix =
			oAuth2JSONWSConfiguration.sapEntryOAuth2Prefix();

		_companyLocalService.forEachCompanyId(
			companyId -> register(companyId),
			ArrayUtil.toLongArray(_scopeFinderServiceRegistrations.keySet()));
	}

	@Deactivate
	protected void deactivate() {
		for (ServiceRegistration<ScopeFinder> serviceRegistration :
				_scopeFinderServiceRegistrations.values()) {

			serviceRegistration.unregister();
		}

		_scopeFinderServiceRegistrations.clear();

		for (ServiceRegistration<ScopeDescriptor> serviceRegistration :
				_scopeDescriptorServiceRegistrations.values()) {

			serviceRegistration.unregister();
		}

		_scopeDescriptorServiceRegistrations.clear();

		_serviceTracker.close();

		_jaxRsApplicationNames.clear();
	}

	private HashMapDictionary<String, Object> _buildScopeDescriptorProperties(
		long companyId) {

		return HashMapDictionaryBuilder.<String, Object>put(
			"companyId", String.valueOf(companyId)
		).put(
			"osgi.jaxrs.name", _jaxRsApplicationNames.toArray(new String[0])
		).build();
	}

	private boolean _isOAuth2ExportedSAPEntry(SAPEntry sapEntry) {
		return StringUtil.startsWith(sapEntry.getName(), _sapEntryOAuth2Prefix);
	}

	private List<SAPEntryScope> _loadSAPEntryScopes(long companyId) {
		List<SAPEntryScope> sapEntryScopes = new ArrayList<>();

		for (SAPEntry sapEntry :
				_sapEntryLocalService.getCompanySAPEntries(
					companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS)) {

			if (_isOAuth2ExportedSAPEntry(sapEntry)) {
				sapEntryScopes.add(
					new SAPEntryScope(sapEntry, _parseScope(sapEntry)));
			}
		}

		return sapEntryScopes;
	}

	private String _parseScope(SAPEntry sapEntry) {
		String sapEntryName = sapEntry.getName();

		if (!_removeSAPEntryOAuth2Prefix) {
			return sapEntryName;
		}

		return sapEntryName.substring(_sapEntryOAuth2Prefix.length());
	}

	private void _resetProperties() {
		for (Map.Entry<Long, ServiceRegistration<ScopeDescriptor>> entry :
				_scopeDescriptorServiceRegistrations.entrySet()) {

			ServiceRegistration<ScopeDescriptor> serviceRegistration =
				entry.getValue();

			try {
				serviceRegistration.setProperties(
					_buildScopeDescriptorProperties(entry.getKey()));
			}
			catch (IllegalStateException illegalStateException) {
				if (_log.isDebugEnabled()) {
					_log.debug(illegalStateException);
				}

				// Concurrent unregistration from register(long)

			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SAPEntryScopeDescriptorFinderRegistrator.class);

	private BundleContext _bundleContext;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference(target = "(default=true)")
	private ScopeDescriptor _defaultScopeDescriptor;

	private final Set<String> _jaxRsApplicationNames =
		Collections.newSetFromMap(new ConcurrentHashMap<>());
	private final Map<Long, SAPEntryScopeDescriptorFinder>
		_registeredSAPEntryScopeDescriptorFinders = new ConcurrentHashMap<>();
	private boolean _removeSAPEntryOAuth2Prefix = true;

	@Reference
	private SAPEntryLocalService _sapEntryLocalService;

	private String _sapEntryOAuth2Prefix = "OAUTH2_";
	private final Map<Long, ServiceRegistration<ScopeDescriptor>>
		_scopeDescriptorServiceRegistrations = new ConcurrentHashMap<>();
	private final Map<Long, ServiceRegistration<ScopeFinder>>
		_scopeFinderServiceRegistrations = new ConcurrentHashMap<>();
	private ServiceTracker<ScopeFinder, ScopeFinder> _serviceTracker;

}