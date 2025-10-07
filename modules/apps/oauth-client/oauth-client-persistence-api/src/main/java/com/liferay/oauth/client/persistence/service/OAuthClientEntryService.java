/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.persistence.service;

import com.liferay.oauth.client.persistence.model.OAuthClientEntry;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.jsonwebservice.JSONWebService;
import com.liferay.portal.kernel.security.access.control.AccessControlled;
import com.liferay.portal.kernel.service.BaseService;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the remote service interface for OAuthClientEntry. Methods of this
 * service are expected to have security checks based on the propagated JAAS
 * credentials because this service can be accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see OAuthClientEntryServiceUtil
 * @generated
 */
@AccessControlled
@JSONWebService
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface OAuthClientEntryService extends BaseService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.oauth.client.persistence.service.impl.OAuthClientEntryServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the o auth client entry remote service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link OAuthClientEntryServiceUtil} if injection and service tracking are not available.
	 */
	public OAuthClientEntry addOAuthClientEntry(
			long userId, String authRequestParametersJSON,
			String authServerWellKnownURI, String customClaimsJSON,
			String infoJSON, long metadataCacheTime,
			String oidcUserInfoMapperJSON, String tokenRequestParametersJSON)
		throws PortalException;

	public OAuthClientEntry deleteOAuthClientEntry(long oAuthClientEntryId)
		throws PortalException;

	public OAuthClientEntry deleteOAuthClientEntry(
			long companyId, String authServerWellKnownURI, String clientId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<OAuthClientEntry>
			getAuthServerWellKnownURISuffixOAuthClientEntries(
				long companyId, String authServerWellKnownURISuffix)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<OAuthClientEntry> getCompanyOAuthClientEntries(long companyId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<OAuthClientEntry> getCompanyOAuthClientEntries(
		long companyId, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public OAuthClientEntry getOAuthClientEntry(
			long companyId, String authServerWellKnownURI, String clientId)
		throws PortalException;

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<OAuthClientEntry> getUserOAuthClientEntries(long userId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<OAuthClientEntry> getUserOAuthClientEntries(
		long userId, int start, int end);

	public OAuthClientEntry updateOAuthClientEntry(
			long oAuthClientEntryId, String authRequestParametersJSON,
			String authServerWellKnownURI, String customClaimsJSON,
			String infoJSON, long metadataCacheTime,
			String oidcUserInfoMapperJSON, String tokenRequestParametersJSON)
		throws PortalException;

}