/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.persistence.service;

import com.liferay.oauth.client.persistence.model.OAuthClientEntry;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;

import java.util.List;

/**
 * Provides the remote service utility for OAuthClientEntry. This utility wraps
 * <code>com.liferay.oauth.client.persistence.service.impl.OAuthClientEntryServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see OAuthClientEntryService
 * @generated
 */
public class OAuthClientEntryServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.oauth.client.persistence.service.impl.OAuthClientEntryServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static OAuthClientEntry addOAuthClientEntry(
			long userId, String authRequestParametersJSON,
			String authServerWellKnownURI, String customClaimsJSON,
			String infoJSON, long metadataCacheTime,
			String oidcUserInfoMapperJSON, String tokenRequestParametersJSON)
		throws PortalException {

		return getService().addOAuthClientEntry(
			userId, authRequestParametersJSON, authServerWellKnownURI,
			customClaimsJSON, infoJSON, metadataCacheTime,
			oidcUserInfoMapperJSON, tokenRequestParametersJSON);
	}

	public static OAuthClientEntry deleteOAuthClientEntry(
			long oAuthClientEntryId)
		throws PortalException {

		return getService().deleteOAuthClientEntry(oAuthClientEntryId);
	}

	public static OAuthClientEntry deleteOAuthClientEntry(
			long companyId, String authServerWellKnownURI, String clientId)
		throws PortalException {

		return getService().deleteOAuthClientEntry(
			companyId, authServerWellKnownURI, clientId);
	}

	public static List<OAuthClientEntry>
			getAuthServerWellKnownURISuffixOAuthClientEntries(
				long companyId, String authServerWellKnownURISuffix)
		throws PortalException {

		return getService().getAuthServerWellKnownURISuffixOAuthClientEntries(
			companyId, authServerWellKnownURISuffix);
	}

	public static List<OAuthClientEntry> getCompanyOAuthClientEntries(
		long companyId) {

		return getService().getCompanyOAuthClientEntries(companyId);
	}

	public static List<OAuthClientEntry> getCompanyOAuthClientEntries(
		long companyId, int start, int end) {

		return getService().getCompanyOAuthClientEntries(companyId, start, end);
	}

	public static OAuthClientEntry getOAuthClientEntry(
			long companyId, String authServerWellKnownURI, String clientId)
		throws PortalException {

		return getService().getOAuthClientEntry(
			companyId, authServerWellKnownURI, clientId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static List<OAuthClientEntry> getUserOAuthClientEntries(
		long userId) {

		return getService().getUserOAuthClientEntries(userId);
	}

	public static List<OAuthClientEntry> getUserOAuthClientEntries(
		long userId, int start, int end) {

		return getService().getUserOAuthClientEntries(userId, start, end);
	}

	public static OAuthClientEntry updateOAuthClientEntry(
			long oAuthClientEntryId, String authRequestParametersJSON,
			String authServerWellKnownURI, String customClaimsJSON,
			String infoJSON, long metadataCacheTime,
			String oidcUserInfoMapperJSON, String tokenRequestParametersJSON)
		throws PortalException {

		return getService().updateOAuthClientEntry(
			oAuthClientEntryId, authRequestParametersJSON,
			authServerWellKnownURI, customClaimsJSON, infoJSON,
			metadataCacheTime, oidcUserInfoMapperJSON,
			tokenRequestParametersJSON);
	}

	public static OAuthClientEntryService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<OAuthClientEntryService> _serviceSnapshot =
		new Snapshot<>(
			OAuthClientEntryServiceUtil.class, OAuthClientEntryService.class);

}