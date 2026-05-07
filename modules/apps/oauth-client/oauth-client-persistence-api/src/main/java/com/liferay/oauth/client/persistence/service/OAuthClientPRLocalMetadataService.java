/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.persistence.service;

import com.liferay.oauth.client.persistence.model.OAuthClientPRLocalMetadata;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.jsonwebservice.JSONWebService;
import com.liferay.portal.kernel.security.access.control.AccessControlled;
import com.liferay.portal.kernel.service.BaseService;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the remote service interface for OAuthClientPRLocalMetadata. Methods of this
 * service are expected to have security checks based on the propagated JAAS
 * credentials because this service can be accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see OAuthClientPRLocalMetadataServiceUtil
 * @generated
 */
@AccessControlled
@JSONWebService
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface OAuthClientPRLocalMetadataService extends BaseService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.oauth.client.persistence.service.impl.OAuthClientPRLocalMetadataServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the o auth client pr local metadata remote service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link OAuthClientPRLocalMetadataServiceUtil} if injection and service tracking are not available.
	 */
	public OAuthClientPRLocalMetadata addOAuthClientPRLocalMetadata(
			String metadataJSON)
		throws PortalException;

	public OAuthClientPRLocalMetadata addOAuthClientPRLocalMetadata(
			String externalReferenceCode, String[] authorizationServers,
			String[] bearerMethodsSupported, boolean localWellKnownEnabled,
			String resource, String resourceName, String[] scopesSupported)
		throws PortalException;

	public OAuthClientPRLocalMetadata deleteOAuthClientPRLocalMetadata(
			long oAuthClientPRLocalMetadataId)
		throws PortalException;

	public OAuthClientPRLocalMetadata deleteOAuthClientPRLocalMetadata(
			long companyId, String localWellKnownURI)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public OAuthClientPRLocalMetadata fetchOAuthClientPRLocalMetadata(
			long oAuthClientPRLocalMetadataId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public OAuthClientPRLocalMetadata fetchOAuthClientPRLocalMetadata(
			long companyId, String resource)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public OAuthClientPRLocalMetadata
			fetchOAuthClientPRLocalMetadataByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<OAuthClientPRLocalMetadata>
		getCompanyOAuthClientPRLocalMetadata(long companyId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<OAuthClientPRLocalMetadata>
		getCompanyOAuthClientPRLocalMetadata(
			long companyId, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public OAuthClientPRLocalMetadata getOAuthClientPRLocalMetadata(
			long companyId, boolean localWellKnownEnabled,
			OrderByComparator<OAuthClientPRLocalMetadata> orderByComparator)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public OAuthClientPRLocalMetadata getOAuthClientPRLocalMetadata(
			long companyId, String resource)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public OAuthClientPRLocalMetadata
			getOAuthClientPRLocalMetadataByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public OAuthClientPRLocalMetadata
			getOAuthClientPRLocalMetadataByLocalWellKnownURI(
				long companyId, String localWellKnownURI)
		throws PortalException;

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<OAuthClientPRLocalMetadata> getUserOAuthClientPRLocalMetadata(
		long userId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<OAuthClientPRLocalMetadata> getUserOAuthClientPRLocalMetadata(
		long userId, int start, int end);

	public OAuthClientPRLocalMetadata updateOAuthClientPRLocalMetadata(
			long oAuthClientPRLocalMetadataId, String metadataJSON)
		throws PortalException;

	public OAuthClientPRLocalMetadata updateOAuthClientPRLocalMetadata(
			long oAuthClientPRLocalMetadataId, String[] authorizationServers,
			String[] bearerMethodsSupported, boolean localWellKnownEnabled,
			String resource, String resourceName, String[] scopesSupported)
		throws PortalException;

}
// LIFERAY-SERVICE-BUILDER-HASH:-606556035