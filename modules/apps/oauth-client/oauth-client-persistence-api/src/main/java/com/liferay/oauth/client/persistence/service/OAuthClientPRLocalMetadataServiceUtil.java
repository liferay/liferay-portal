/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.persistence.service;

import com.liferay.oauth.client.persistence.model.OAuthClientPRLocalMetadata;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;

/**
 * Provides the remote service utility for OAuthClientPRLocalMetadata. This utility wraps
 * <code>com.liferay.oauth.client.persistence.service.impl.OAuthClientPRLocalMetadataServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see OAuthClientPRLocalMetadataService
 * @generated
 */
public class OAuthClientPRLocalMetadataServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.oauth.client.persistence.service.impl.OAuthClientPRLocalMetadataServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static OAuthClientPRLocalMetadata addOAuthClientPRLocalMetadata(
			String metadataJSON)
		throws PortalException {

		return getService().addOAuthClientPRLocalMetadata(metadataJSON);
	}

	public static OAuthClientPRLocalMetadata addOAuthClientPRLocalMetadata(
			String externalReferenceCode, String[] authorizationServers,
			String[] bearerMethodsSupported, boolean localWellKnownEnabled,
			String resource, String resourceName, String[] scopesSupported)
		throws PortalException {

		return getService().addOAuthClientPRLocalMetadata(
			externalReferenceCode, authorizationServers, bearerMethodsSupported,
			localWellKnownEnabled, resource, resourceName, scopesSupported);
	}

	public static OAuthClientPRLocalMetadata deleteOAuthClientPRLocalMetadata(
			long oAuthClientPRLocalMetadataId)
		throws PortalException {

		return getService().deleteOAuthClientPRLocalMetadata(
			oAuthClientPRLocalMetadataId);
	}

	public static OAuthClientPRLocalMetadata deleteOAuthClientPRLocalMetadata(
			long companyId, String localWellKnownURI)
		throws PortalException {

		return getService().deleteOAuthClientPRLocalMetadata(
			companyId, localWellKnownURI);
	}

	public static OAuthClientPRLocalMetadata fetchOAuthClientPRLocalMetadata(
			long oAuthClientPRLocalMetadataId)
		throws PortalException {

		return getService().fetchOAuthClientPRLocalMetadata(
			oAuthClientPRLocalMetadataId);
	}

	public static OAuthClientPRLocalMetadata fetchOAuthClientPRLocalMetadata(
			long companyId, String resource)
		throws PortalException {

		return getService().fetchOAuthClientPRLocalMetadata(
			companyId, resource);
	}

	public static OAuthClientPRLocalMetadata
			fetchOAuthClientPRLocalMetadataByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().
			fetchOAuthClientPRLocalMetadataByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	public static List<OAuthClientPRLocalMetadata>
		getCompanyOAuthClientPRLocalMetadata(long companyId) {

		return getService().getCompanyOAuthClientPRLocalMetadata(companyId);
	}

	public static List<OAuthClientPRLocalMetadata>
		getCompanyOAuthClientPRLocalMetadata(
			long companyId, int start, int end) {

		return getService().getCompanyOAuthClientPRLocalMetadata(
			companyId, start, end);
	}

	public static OAuthClientPRLocalMetadata getOAuthClientPRLocalMetadata(
			long companyId, boolean localWellKnownEnabled,
			OrderByComparator<OAuthClientPRLocalMetadata> orderByComparator)
		throws PortalException {

		return getService().getOAuthClientPRLocalMetadata(
			companyId, localWellKnownEnabled, orderByComparator);
	}

	public static OAuthClientPRLocalMetadata getOAuthClientPRLocalMetadata(
			long companyId, String resource)
		throws PortalException {

		return getService().getOAuthClientPRLocalMetadata(companyId, resource);
	}

	public static OAuthClientPRLocalMetadata
			getOAuthClientPRLocalMetadataByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().
			getOAuthClientPRLocalMetadataByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	public static OAuthClientPRLocalMetadata
			getOAuthClientPRLocalMetadataByLocalWellKnownURI(
				long companyId, String localWellKnownURI)
		throws PortalException {

		return getService().getOAuthClientPRLocalMetadataByLocalWellKnownURI(
			companyId, localWellKnownURI);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static List<OAuthClientPRLocalMetadata>
		getUserOAuthClientPRLocalMetadata(long userId) {

		return getService().getUserOAuthClientPRLocalMetadata(userId);
	}

	public static List<OAuthClientPRLocalMetadata>
		getUserOAuthClientPRLocalMetadata(long userId, int start, int end) {

		return getService().getUserOAuthClientPRLocalMetadata(
			userId, start, end);
	}

	public static OAuthClientPRLocalMetadata updateOAuthClientPRLocalMetadata(
			long oAuthClientPRLocalMetadataId, String metadataJSON)
		throws PortalException {

		return getService().updateOAuthClientPRLocalMetadata(
			oAuthClientPRLocalMetadataId, metadataJSON);
	}

	public static OAuthClientPRLocalMetadata updateOAuthClientPRLocalMetadata(
			long oAuthClientPRLocalMetadataId, String[] authorizationServers,
			String[] bearerMethodsSupported, boolean localWellKnownEnabled,
			String resource, String resourceName, String[] scopesSupported)
		throws PortalException {

		return getService().updateOAuthClientPRLocalMetadata(
			oAuthClientPRLocalMetadataId, authorizationServers,
			bearerMethodsSupported, localWellKnownEnabled, resource,
			resourceName, scopesSupported);
	}

	public static OAuthClientPRLocalMetadataService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<OAuthClientPRLocalMetadataService>
		_serviceSnapshot = new Snapshot<>(
			OAuthClientPRLocalMetadataServiceUtil.class,
			OAuthClientPRLocalMetadataService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-2115996976