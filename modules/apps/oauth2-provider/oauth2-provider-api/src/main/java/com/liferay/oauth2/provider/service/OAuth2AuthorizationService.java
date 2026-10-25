/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.service;

import com.liferay.oauth2.provider.model.OAuth2Authorization;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.security.access.control.AccessControlled;
import com.liferay.portal.kernel.service.BaseService;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the remote service interface for OAuth2Authorization. Methods of this
 * service are expected to have security checks based on the propagated JAAS
 * credentials because this service can be accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see OAuth2AuthorizationServiceUtil
 * @generated
 */
@AccessControlled
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface OAuth2AuthorizationService extends BaseService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.oauth2.provider.service.impl.OAuth2AuthorizationServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the o auth2 authorization remote service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link OAuth2AuthorizationServiceUtil} if injection and service tracking are not available.
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<OAuth2Authorization> getApplicationOAuth2Authorizations(
			long oAuth2ApplicationId, int start, int end,
			OrderByComparator<OAuth2Authorization> orderByComparator)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getApplicationOAuth2AuthorizationsCount(long oAuth2ApplicationId)
		throws PortalException;

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<OAuth2Authorization> getUserOAuth2Authorizations(
			int start, int end,
			OrderByComparator<OAuth2Authorization> orderByComparator)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getUserOAuth2AuthorizationsCount() throws PortalException;

	public void revokeAllOAuth2Authorizations(long oAuth2ApplicationId)
		throws PortalException;

	public void revokeOAuth2Authorization(long oAuth2AuthorizationId)
		throws PortalException;

}
// LIFERAY-SERVICE-BUILDER-HASH:1448441766