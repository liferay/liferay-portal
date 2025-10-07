/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.persistence.service;

import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link OAuthClientEntryService}.
 *
 * @author Brian Wing Shun Chan
 * @see OAuthClientEntryService
 * @generated
 */
public class OAuthClientEntryServiceWrapper
	implements OAuthClientEntryService,
			   ServiceWrapper<OAuthClientEntryService> {

	public OAuthClientEntryServiceWrapper() {
		this(null);
	}

	public OAuthClientEntryServiceWrapper(
		OAuthClientEntryService oAuthClientEntryService) {

		_oAuthClientEntryService = oAuthClientEntryService;
	}

	@Override
	public com.liferay.oauth.client.persistence.model.OAuthClientEntry
			addOAuthClientEntry(
				long userId, String authRequestParametersJSON,
				String authServerWellKnownURI, String customClaimsJSON,
				String infoJSON, long metadataCacheTime,
				String oidcUserInfoMapperJSON,
				String tokenRequestParametersJSON)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _oAuthClientEntryService.addOAuthClientEntry(
			userId, authRequestParametersJSON, authServerWellKnownURI,
			customClaimsJSON, infoJSON, metadataCacheTime,
			oidcUserInfoMapperJSON, tokenRequestParametersJSON);
	}

	@Override
	public com.liferay.oauth.client.persistence.model.OAuthClientEntry
			deleteOAuthClientEntry(long oAuthClientEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _oAuthClientEntryService.deleteOAuthClientEntry(
			oAuthClientEntryId);
	}

	@Override
	public com.liferay.oauth.client.persistence.model.OAuthClientEntry
			deleteOAuthClientEntry(
				long companyId, String authServerWellKnownURI, String clientId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _oAuthClientEntryService.deleteOAuthClientEntry(
			companyId, authServerWellKnownURI, clientId);
	}

	@Override
	public java.util.List
		<com.liferay.oauth.client.persistence.model.OAuthClientEntry>
				getAuthServerWellKnownURISuffixOAuthClientEntries(
					long companyId, String authServerWellKnownURISuffix)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _oAuthClientEntryService.
			getAuthServerWellKnownURISuffixOAuthClientEntries(
				companyId, authServerWellKnownURISuffix);
	}

	@Override
	public java.util.List
		<com.liferay.oauth.client.persistence.model.OAuthClientEntry>
			getCompanyOAuthClientEntries(long companyId) {

		return _oAuthClientEntryService.getCompanyOAuthClientEntries(companyId);
	}

	@Override
	public java.util.List
		<com.liferay.oauth.client.persistence.model.OAuthClientEntry>
			getCompanyOAuthClientEntries(long companyId, int start, int end) {

		return _oAuthClientEntryService.getCompanyOAuthClientEntries(
			companyId, start, end);
	}

	@Override
	public com.liferay.oauth.client.persistence.model.OAuthClientEntry
			getOAuthClientEntry(
				long companyId, String authServerWellKnownURI, String clientId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _oAuthClientEntryService.getOAuthClientEntry(
			companyId, authServerWellKnownURI, clientId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _oAuthClientEntryService.getOSGiServiceIdentifier();
	}

	@Override
	public java.util.List
		<com.liferay.oauth.client.persistence.model.OAuthClientEntry>
			getUserOAuthClientEntries(long userId) {

		return _oAuthClientEntryService.getUserOAuthClientEntries(userId);
	}

	@Override
	public java.util.List
		<com.liferay.oauth.client.persistence.model.OAuthClientEntry>
			getUserOAuthClientEntries(long userId, int start, int end) {

		return _oAuthClientEntryService.getUserOAuthClientEntries(
			userId, start, end);
	}

	@Override
	public com.liferay.oauth.client.persistence.model.OAuthClientEntry
			updateOAuthClientEntry(
				long oAuthClientEntryId, String authRequestParametersJSON,
				String authServerWellKnownURI, String customClaimsJSON,
				String infoJSON, long metadataCacheTime,
				String oidcUserInfoMapperJSON,
				String tokenRequestParametersJSON)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _oAuthClientEntryService.updateOAuthClientEntry(
			oAuthClientEntryId, authRequestParametersJSON,
			authServerWellKnownURI, customClaimsJSON, infoJSON,
			metadataCacheTime, oidcUserInfoMapperJSON,
			tokenRequestParametersJSON);
	}

	@Override
	public OAuthClientEntryService getWrappedService() {
		return _oAuthClientEntryService;
	}

	@Override
	public void setWrappedService(
		OAuthClientEntryService oAuthClientEntryService) {

		_oAuthClientEntryService = oAuthClientEntryService;
	}

	private OAuthClientEntryService _oAuthClientEntryService;

}