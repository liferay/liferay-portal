/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharepoint.soap.repository;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.repository.DocumentRepository;
import com.liferay.portal.kernel.repository.RepositoryConfiguration;
import com.liferay.portal.kernel.repository.RepositoryConfigurationBuilder;
import com.liferay.portal.kernel.repository.RepositoryFactory;
import com.liferay.portal.kernel.repository.capabilities.PortalCapabilityLocator;
import com.liferay.portal.kernel.repository.capabilities.ProcessorCapability;
import com.liferay.portal.kernel.repository.registry.BaseRepositoryDefiner;
import com.liferay.portal.kernel.repository.registry.CapabilityRegistry;
import com.liferay.portal.kernel.repository.registry.RepositoryDefiner;
import com.liferay.portal.kernel.repository.registry.RepositoryFactoryRegistry;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoader;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.sharepoint.soap.repository.constants.SharepointWSConstants;

import java.util.Locale;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cristina González
 */
@Component(enabled = false, service = RepositoryDefiner.class)
public class SharepointWSRepositoryDefiner extends BaseRepositoryDefiner {

	@Override
	public String getClassName() {
		return SharepointWSRepository.class.getName();
	}

	@Override
	public RepositoryConfiguration getRepositoryConfiguration() {
		return _repositoryConfiguration;
	}

	@Override
	public String getRepositoryTypeLabel(Locale locale) {
		return StringBundler.concat(
			super.getRepositoryTypeLabel(locale), " (",
			_language.get(locale, "deprecated"), StringPool.CLOSE_PARENTHESIS);
	}

	@Override
	public boolean isExternalRepository() {
		return true;
	}

	@Override
	public void registerCapabilities(
		CapabilityRegistry<DocumentRepository> capabilityRegistry) {

		capabilityRegistry.addSupportedCapability(
			ProcessorCapability.class,
			_portalCapabilityLocator.getProcessorCapability(
				capabilityRegistry.getTarget(),
				ProcessorCapability.ResourceGenerationStrategy.
					ALWAYS_GENERATE));
	}

	@Override
	public void registerRepositoryFactory(
		RepositoryFactoryRegistry repositoryFactoryRegistry) {

		repositoryFactoryRegistry.setRepositoryFactory(_repositoryFactory);
	}

	@Activate
	protected void activate() {
		ResourceBundleLoader resourceBundleLoader =
			locale -> ResourceBundleUtil.getBundle(locale, getClass());

		RepositoryConfigurationBuilder repositoryConfigurationBuilder =
			new RepositoryConfigurationBuilder(
				resourceBundleLoader,
				SharepointWSConstants.SHAREPOINT_LIBRARY_NAME,
				SharepointWSConstants.SHAREPOINT_LIBRARY_PATH,
				SharepointWSConstants.SHAREPOINT_SERVER_VERSION,
				SharepointWSConstants.SHAREPOINT_SITE_URL);

		_repositoryConfiguration = repositoryConfigurationBuilder.build();
	}

	@Deactivate
	protected void deactivate() {
		_repositoryConfiguration = null;
	}

	@Reference
	private Language _language;

	@Reference
	private PortalCapabilityLocator _portalCapabilityLocator;

	private RepositoryConfiguration _repositoryConfiguration;

	@Reference(
		target = "(repository.target.class.name=com.liferay.sharepoint.soap.repository.SharepointWSRepository)"
	)
	private RepositoryFactory _repositoryFactory;

}