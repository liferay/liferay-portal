/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.persistence.service;

import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link OAuthClientPRLocalMetadataService}.
 *
 * @author Brian Wing Shun Chan
 * @see OAuthClientPRLocalMetadataService
 * @generated
 */
public class OAuthClientPRLocalMetadataServiceWrapper
	implements OAuthClientPRLocalMetadataService,
			   ServiceWrapper<OAuthClientPRLocalMetadataService> {

	public OAuthClientPRLocalMetadataServiceWrapper() {
		this(null);
	}

	public OAuthClientPRLocalMetadataServiceWrapper(
		OAuthClientPRLocalMetadataService oAuthClientPRLocalMetadataService) {

		_oAuthClientPRLocalMetadataService = oAuthClientPRLocalMetadataService;
	}

	@Override
	public com.liferay.oauth.client.persistence.model.OAuthClientPRLocalMetadata
			addOAuthClientPRLocalMetadata(String metadataJSON)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _oAuthClientPRLocalMetadataService.addOAuthClientPRLocalMetadata(
			metadataJSON);
	}

	@Override
	public com.liferay.oauth.client.persistence.model.OAuthClientPRLocalMetadata
			addOAuthClientPRLocalMetadata(
				String externalReferenceCode, String[] authorizationServers,
				String[] bearerMethodsSupported, boolean localWellKnownEnabled,
				String resource, String resourceName, String[] scopesSupported)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _oAuthClientPRLocalMetadataService.addOAuthClientPRLocalMetadata(
			externalReferenceCode, authorizationServers, bearerMethodsSupported,
			localWellKnownEnabled, resource, resourceName, scopesSupported);
	}

	@Override
	public com.liferay.oauth.client.persistence.model.OAuthClientPRLocalMetadata
			deleteOAuthClientPRLocalMetadata(long oAuthClientPRLocalMetadataId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _oAuthClientPRLocalMetadataService.
			deleteOAuthClientPRLocalMetadata(oAuthClientPRLocalMetadataId);
	}

	@Override
	public com.liferay.oauth.client.persistence.model.OAuthClientPRLocalMetadata
			deleteOAuthClientPRLocalMetadata(
				long companyId, String localWellKnownURI)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _oAuthClientPRLocalMetadataService.
			deleteOAuthClientPRLocalMetadata(companyId, localWellKnownURI);
	}

	@Override
	public com.liferay.oauth.client.persistence.model.OAuthClientPRLocalMetadata
			fetchOAuthClientPRLocalMetadata(long oAuthClientPRLocalMetadataId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _oAuthClientPRLocalMetadataService.
			fetchOAuthClientPRLocalMetadata(oAuthClientPRLocalMetadataId);
	}

	@Override
	public com.liferay.oauth.client.persistence.model.OAuthClientPRLocalMetadata
			fetchOAuthClientPRLocalMetadata(long companyId, String resource)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _oAuthClientPRLocalMetadataService.
			fetchOAuthClientPRLocalMetadata(companyId, resource);
	}

	@Override
	public com.liferay.oauth.client.persistence.model.OAuthClientPRLocalMetadata
			fetchOAuthClientPRLocalMetadataByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _oAuthClientPRLocalMetadataService.
			fetchOAuthClientPRLocalMetadataByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	@Override
	public java.util.List
		<com.liferay.oauth.client.persistence.model.OAuthClientPRLocalMetadata>
			getCompanyOAuthClientPRLocalMetadata(long companyId) {

		return _oAuthClientPRLocalMetadataService.
			getCompanyOAuthClientPRLocalMetadata(companyId);
	}

	@Override
	public java.util.List
		<com.liferay.oauth.client.persistence.model.OAuthClientPRLocalMetadata>
			getCompanyOAuthClientPRLocalMetadata(
				long companyId, int start, int end) {

		return _oAuthClientPRLocalMetadataService.
			getCompanyOAuthClientPRLocalMetadata(companyId, start, end);
	}

	@Override
	public com.liferay.oauth.client.persistence.model.OAuthClientPRLocalMetadata
			getOAuthClientPRLocalMetadata(
				long companyId, boolean localWellKnownEnabled,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.oauth.client.persistence.model.
						OAuthClientPRLocalMetadata> orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _oAuthClientPRLocalMetadataService.getOAuthClientPRLocalMetadata(
			companyId, localWellKnownEnabled, orderByComparator);
	}

	@Override
	public com.liferay.oauth.client.persistence.model.OAuthClientPRLocalMetadata
			getOAuthClientPRLocalMetadata(long companyId, String resource)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _oAuthClientPRLocalMetadataService.getOAuthClientPRLocalMetadata(
			companyId, resource);
	}

	@Override
	public com.liferay.oauth.client.persistence.model.OAuthClientPRLocalMetadata
			getOAuthClientPRLocalMetadataByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _oAuthClientPRLocalMetadataService.
			getOAuthClientPRLocalMetadataByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	@Override
	public com.liferay.oauth.client.persistence.model.OAuthClientPRLocalMetadata
			getOAuthClientPRLocalMetadataByLocalWellKnownURI(
				long companyId, String localWellKnownURI)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _oAuthClientPRLocalMetadataService.
			getOAuthClientPRLocalMetadataByLocalWellKnownURI(
				companyId, localWellKnownURI);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _oAuthClientPRLocalMetadataService.getOSGiServiceIdentifier();
	}

	@Override
	public java.util.List
		<com.liferay.oauth.client.persistence.model.OAuthClientPRLocalMetadata>
			getUserOAuthClientPRLocalMetadata(long userId) {

		return _oAuthClientPRLocalMetadataService.
			getUserOAuthClientPRLocalMetadata(userId);
	}

	@Override
	public java.util.List
		<com.liferay.oauth.client.persistence.model.OAuthClientPRLocalMetadata>
			getUserOAuthClientPRLocalMetadata(long userId, int start, int end) {

		return _oAuthClientPRLocalMetadataService.
			getUserOAuthClientPRLocalMetadata(userId, start, end);
	}

	@Override
	public com.liferay.oauth.client.persistence.model.OAuthClientPRLocalMetadata
			updateOAuthClientPRLocalMetadata(
				long oAuthClientPRLocalMetadataId, String metadataJSON)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _oAuthClientPRLocalMetadataService.
			updateOAuthClientPRLocalMetadata(
				oAuthClientPRLocalMetadataId, metadataJSON);
	}

	@Override
	public com.liferay.oauth.client.persistence.model.OAuthClientPRLocalMetadata
			updateOAuthClientPRLocalMetadata(
				long oAuthClientPRLocalMetadataId,
				String[] authorizationServers, String[] bearerMethodsSupported,
				boolean localWellKnownEnabled, String resource,
				String resourceName, String[] scopesSupported)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _oAuthClientPRLocalMetadataService.
			updateOAuthClientPRLocalMetadata(
				oAuthClientPRLocalMetadataId, authorizationServers,
				bearerMethodsSupported, localWellKnownEnabled, resource,
				resourceName, scopesSupported);
	}

	@Override
	public OAuthClientPRLocalMetadataService getWrappedService() {
		return _oAuthClientPRLocalMetadataService;
	}

	@Override
	public void setWrappedService(
		OAuthClientPRLocalMetadataService oAuthClientPRLocalMetadataService) {

		_oAuthClientPRLocalMetadataService = oAuthClientPRLocalMetadataService;
	}

	private OAuthClientPRLocalMetadataService
		_oAuthClientPRLocalMetadataService;

}
// LIFERAY-SERVICE-BUILDER-HASH:762240207