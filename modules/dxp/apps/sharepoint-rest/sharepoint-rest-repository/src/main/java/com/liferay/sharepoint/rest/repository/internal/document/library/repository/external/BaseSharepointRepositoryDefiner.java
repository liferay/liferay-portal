/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharepoint.rest.repository.internal.document.library.repository.external;

import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.document.library.kernel.service.DLAppHelperLocalService;
import com.liferay.document.library.kernel.service.DLFolderLocalService;
import com.liferay.document.library.repository.authorization.capability.AuthorizationCapability;
import com.liferay.document.library.repository.authorization.oauth2.TokenStore;
import com.liferay.document.library.repository.external.MinorVersionCapability;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.SingleVMPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.repository.BaseRepository;
import com.liferay.portal.kernel.repository.DocumentRepository;
import com.liferay.portal.kernel.repository.LocalRepository;
import com.liferay.portal.kernel.repository.Repository;
import com.liferay.portal.kernel.repository.RepositoryConfiguration;
import com.liferay.portal.kernel.repository.RepositoryConfigurationBuilder;
import com.liferay.portal.kernel.repository.RepositoryFactory;
import com.liferay.portal.kernel.repository.capabilities.PortalCapabilityLocator;
import com.liferay.portal.kernel.repository.capabilities.ProcessorCapability;
import com.liferay.portal.kernel.repository.registry.CapabilityRegistry;
import com.liferay.portal.kernel.repository.registry.RepositoryDefiner;
import com.liferay.portal.kernel.repository.registry.RepositoryEventRegistry;
import com.liferay.portal.kernel.repository.registry.RepositoryFactoryRegistry;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoaderUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.RepositoryEntryLocalService;
import com.liferay.portal.kernel.service.RepositoryLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.Time;
import com.liferay.sharepoint.rest.repository.internal.configuration.SharepointRepositoryConfiguration;
import com.liferay.sharepoint.rest.repository.internal.document.library.repository.authorization.capability.SharepointRepositoryAuthorizationCapability;
import com.liferay.sharepoint.rest.repository.internal.document.library.repository.authorization.oauth2.util.SharepointRepositoryTokenBrokerFactoryUtil;
import com.liferay.sharepoint.rest.repository.internal.util.SharepointRepositoryClassNameUtil;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
public abstract class BaseSharepointRepositoryDefiner
	implements RepositoryDefiner {

	@Override
	public String getClassName() {
		return SharepointRepositoryClassNameUtil.getClassName(
			_sharepointRepositoryConfiguration.name());
	}

	@Override
	public RepositoryConfiguration getRepositoryConfiguration() {
		RepositoryConfigurationBuilder repositoryConfigurationBuilder =
			new RepositoryConfigurationBuilder(
				ResourceBundleLoaderUtil.getPortalResourceBundleLoader());

		repositoryConfigurationBuilder.addParameter("library-path");
		repositoryConfigurationBuilder.addParameter("site-absolute-url");

		return repositoryConfigurationBuilder.build();
	}

	@Override
	public String getRepositoryTypeLabel(Locale locale) {
		String label = language.get(locale, "sharepoint");

		return String.format(
			"%s (%s)", label, _sharepointRepositoryConfiguration.name());
	}

	@Override
	public boolean isExternalRepository() {
		return true;
	}

	@Override
	public void registerCapabilities(
		CapabilityRegistry<DocumentRepository> capabilityRegistry) {

		DocumentRepository documentRepository = capabilityRegistry.getTarget();

		capabilityRegistry.addSupportedCapability(
			ProcessorCapability.class,
			portalCapabilityLocator.getProcessorCapability(
				documentRepository,
				ProcessorCapability.ResourceGenerationStrategy.
					ALWAYS_GENERATE));

		capabilityRegistry.addExportedCapability(
			AuthorizationCapability.class,
			new SharepointRepositoryAuthorizationCapability(
				documentRepository, tokenStore,
				_sharepointRepositoryConfiguration,
				SharepointRepositoryTokenBrokerFactoryUtil.create(
					_properties, _sharepointRepositoryConfiguration)));

		capabilityRegistry.addExportedCapability(
			MinorVersionCapability.class,
			() -> _isMinorVersionEnabled(documentRepository.getRepositoryId()));
	}

	@Override
	public void registerRepositoryEventListeners(
		RepositoryEventRegistry repositoryEventRegistry) {
	}

	@Override
	public void registerRepositoryFactory(
		RepositoryFactoryRegistry repositoryFactoryRegistry) {

		repositoryFactoryRegistry.setRepositoryFactory(
			new SharepointRepositoryFactory(
				_sharepointRepositoryConfiguration));
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		Class<?> clazz = getClass();

		_portalCache = (PortalCache<Long, Boolean>)singleVMPool.getPortalCache(
			clazz.getName());

		_properties = properties;
		_sharepointRepositoryConfiguration =
			ConfigurableUtil.createConfigurable(
				SharepointRepositoryConfiguration.class, properties);
	}

	@Deactivate
	protected void deactivate() {
		_portalCache.removeAll();
	}

	@Reference
	protected AssetEntryLocalService assetEntryLocalService;

	@Reference
	protected CompanyLocalService companyLocalService;

	@Reference
	protected DLAppHelperLocalService dlAppHelperLocalService;

	@Reference
	protected DLFolderLocalService dlFolderLocalService;

	@Reference
	protected Language language;

	@Reference
	protected PortalCapabilityLocator portalCapabilityLocator;

	@Reference
	protected RepositoryEntryLocalService repositoryEntryLocalService;

	@Reference
	protected RepositoryLocalService repositoryLocalService;

	@Reference
	protected SingleVMPool singleVMPool;

	@Reference
	protected TokenStore tokenStore;

	@Reference
	protected UserLocalService userLocalService;

	private boolean _isMinorVersionEnabled(long repositoryId)
		throws PortalException {

		Boolean minorVersionEnabled = _portalCache.get(repositoryId);

		if (minorVersionEnabled != null) {
			return minorVersionEnabled;
		}

		com.liferay.portal.kernel.model.Repository repository =
			repositoryLocalService.getRepository(repositoryId);
		SharepointExtRepository sharepointExtRepository =
			new SharepointExtRepository(
				_properties, _sharepointRepositoryConfiguration, tokenStore);

		sharepointExtRepository.initRepository(
			repository.getTypeSettingsProperties(), null);

		minorVersionEnabled = sharepointExtRepository.isMinorVersionEnabled();

		_portalCache.put(
			repositoryId, minorVersionEnabled,
			(int)(5 * Time.MINUTE / Time.SECOND));

		return minorVersionEnabled;
	}

	private PortalCache<Long, Boolean> _portalCache;
	private Map<String, Object> _properties;
	private SharepointRepositoryConfiguration
		_sharepointRepositoryConfiguration;

	private class SharepointRepositoryFactory implements RepositoryFactory {

		public SharepointRepositoryFactory(
			SharepointRepositoryConfiguration
				sharepointRepositoryConfiguration) {

			_sharepointRepositoryConfiguration =
				sharepointRepositoryConfiguration;
		}

		@Override
		public LocalRepository createLocalRepository(long repositoryId)
			throws PortalException {

			BaseRepository baseRepository = _createBaseRepository(repositoryId);

			return baseRepository.getLocalRepository();
		}

		@Override
		public Repository createRepository(long repositoryId)
			throws PortalException {

			return _createBaseRepository(repositoryId);
		}

		private BaseRepository _createBaseRepository(long repositoryId)
			throws PortalException {

			SharepointExtRepositoryAdapter sharepointExtRepositoryAdapter =
				new SharepointExtRepositoryAdapter(
					new SharepointCachingExtRepository(
						new SharepointExtRepository(
							_properties, _sharepointRepositoryConfiguration,
							tokenStore)));

			sharepointExtRepositoryAdapter.setAssetEntryLocalService(
				assetEntryLocalService);

			com.liferay.portal.kernel.model.Repository repository =
				repositoryLocalService.getRepository(repositoryId);

			sharepointExtRepositoryAdapter.setCompanyId(
				repository.getCompanyId());

			sharepointExtRepositoryAdapter.setCompanyLocalService(
				companyLocalService);
			sharepointExtRepositoryAdapter.setDLAppHelperLocalService(
				dlAppHelperLocalService);
			sharepointExtRepositoryAdapter.setDLFolderLocalService(
				dlFolderLocalService);
			sharepointExtRepositoryAdapter.setGroupId(repository.getGroupId());
			sharepointExtRepositoryAdapter.setRepositoryId(
				repository.getRepositoryId());
			sharepointExtRepositoryAdapter.setRepositoryEntryLocalService(
				repositoryEntryLocalService);
			sharepointExtRepositoryAdapter.setUserLocalService(
				userLocalService);
			sharepointExtRepositoryAdapter.setTypeSettingsProperties(
				repository.getTypeSettingsProperties());

			sharepointExtRepositoryAdapter.initRepository();

			return sharepointExtRepositoryAdapter;
		}

		private final SharepointRepositoryConfiguration
			_sharepointRepositoryConfiguration;

	}

}