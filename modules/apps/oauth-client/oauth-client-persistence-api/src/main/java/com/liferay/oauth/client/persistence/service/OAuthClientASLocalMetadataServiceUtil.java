/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.persistence.service;

import com.liferay.oauth.client.persistence.model.OAuthClientASLocalMetadata;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;

import java.util.List;

/**
 * Provides the remote service utility for OAuthClientASLocalMetadata. This utility wraps
 * <code>com.liferay.oauth.client.persistence.service.impl.OAuthClientASLocalMetadataServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see OAuthClientASLocalMetadataService
 * @generated
 */
public class OAuthClientASLocalMetadataServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.oauth.client.persistence.service.impl.OAuthClientASLocalMetadataServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static OAuthClientASLocalMetadata addOAuthClientASLocalMetadata(
			long userId, Boolean enabled, String issuerString, String jwksUri,
			String[] supportedGrantTypes, String[] supportedScopes,
			String[] supportedSubjectTypes, String tokenEndpointString,
			String userinfoEndpoint)
		throws PortalException {

		return getService().addOAuthClientASLocalMetadata(
			userId, enabled, issuerString, jwksUri, supportedGrantTypes,
			supportedScopes, supportedSubjectTypes, tokenEndpointString,
			userinfoEndpoint);
	}

	public static OAuthClientASLocalMetadata deleteOAuthClientASLocalMetadata(
			long oAuthClientASLocalMetadataId)
		throws PortalException {

		return getService().deleteOAuthClientASLocalMetadata(
			oAuthClientASLocalMetadataId);
	}

	public static OAuthClientASLocalMetadata deleteOAuthClientASLocalMetadata(
			String localWellKnownURI)
		throws PortalException {

		return getService().deleteOAuthClientASLocalMetadata(localWellKnownURI);
	}

	public static List<OAuthClientASLocalMetadata>
		getCompanyOAuthClientASLocalMetadata(long companyId) {

		return getService().getCompanyOAuthClientASLocalMetadata(companyId);
	}

	public static List<OAuthClientASLocalMetadata>
		getCompanyOAuthClientASLocalMetadata(
			long companyId, int start, int end) {

		return getService().getCompanyOAuthClientASLocalMetadata(
			companyId, start, end);
	}

	public static OAuthClientASLocalMetadata getOAuthClientASLocalMetadata(
			String localWellKnownURI)
		throws PortalException {

		return getService().getOAuthClientASLocalMetadata(localWellKnownURI);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static List<OAuthClientASLocalMetadata>
		getUserOAuthClientASLocalMetadata(long userId) {

		return getService().getUserOAuthClientASLocalMetadata(userId);
	}

	public static List<OAuthClientASLocalMetadata>
		getUserOAuthClientASLocalMetadata(long userId, int start, int end) {

		return getService().getUserOAuthClientASLocalMetadata(
			userId, start, end);
	}

	public static OAuthClientASLocalMetadata updateOAuthClientASLocalMetadata(
			long oAuthClientASLocalMetadataId, Boolean enabled,
			String issuerString, String jwksUri, String[] supportedGrantTypes,
			String[] supportedScopes, String[] supportedSubjectTypes,
			String tokenEndpointString, String userinfoEndpoint)
		throws PortalException {

		return getService().updateOAuthClientASLocalMetadata(
			oAuthClientASLocalMetadataId, enabled, issuerString, jwksUri,
			supportedGrantTypes, supportedScopes, supportedSubjectTypes,
			tokenEndpointString, userinfoEndpoint);
	}

	public static OAuthClientASLocalMetadataService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<OAuthClientASLocalMetadataService>
		_serviceSnapshot = new Snapshot<>(
			OAuthClientASLocalMetadataServiceUtil.class,
			OAuthClientASLocalMetadataService.class);

}