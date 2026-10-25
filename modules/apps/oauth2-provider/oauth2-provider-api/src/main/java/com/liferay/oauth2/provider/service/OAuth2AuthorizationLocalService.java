/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.service;

import com.liferay.oauth2.provider.exception.NoSuchOAuth2AuthorizationException;
import com.liferay.oauth2.provider.model.OAuth2Authorization;
import com.liferay.oauth2.provider.model.OAuth2ScopeGrant;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Projection;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.BaseLocalService;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for OAuth2Authorization. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see OAuth2AuthorizationLocalServiceUtil
 * @generated
 */
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface OAuth2AuthorizationLocalService
	extends BaseLocalService, PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.oauth2.provider.service.impl.OAuth2AuthorizationLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the o auth2 authorization local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link OAuth2AuthorizationLocalServiceUtil} if injection and service tracking are not available.
	 */
	public OAuth2Authorization addOAuth2Authorization(
		long companyId, long userId, String userName, long oAuth2ApplicationId,
		long oAuth2ApplicationScopeAliasesId, String accessTokenContent,
		Date accessTokenCreateDate, Date accessTokenExpirationDate,
		List<String> audiencesList, String remoteHostInfo, String remoteIPInfo,
		String refreshTokenContent, Date refreshTokenCreateDate,
		Date refreshTokenExpirationDate);

	/**
	 * Adds the o auth2 authorization to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect OAuth2AuthorizationLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param oAuth2Authorization the o auth2 authorization
	 * @return the o auth2 authorization that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public OAuth2Authorization addOAuth2Authorization(
		OAuth2Authorization oAuth2Authorization);

	public boolean addOAuth2ScopeGrantOAuth2Authorization(
		long oAuth2ScopeGrantId, long oAuth2AuthorizationId);

	public boolean addOAuth2ScopeGrantOAuth2Authorization(
		long oAuth2ScopeGrantId, OAuth2Authorization oAuth2Authorization);

	public boolean addOAuth2ScopeGrantOAuth2Authorizations(
		long oAuth2ScopeGrantId,
		List<OAuth2Authorization> oAuth2Authorizations);

	public boolean addOAuth2ScopeGrantOAuth2Authorizations(
		long oAuth2ScopeGrantId, long[] oAuth2AuthorizationIds);

	public void clearOAuth2ScopeGrantOAuth2Authorizations(
		long oAuth2ScopeGrantId);

	/**
	 * Creates a new o auth2 authorization with the primary key. Does not add the o auth2 authorization to the database.
	 *
	 * @param oAuth2AuthorizationId the primary key for the new o auth2 authorization
	 * @return the new o auth2 authorization
	 */
	@Transactional(enabled = false)
	public OAuth2Authorization createOAuth2Authorization(
		long oAuth2AuthorizationId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	public void deleteExpiredOAuth2Authorizations() throws PortalException;

	/**
	 * Deletes the o auth2 authorization with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect OAuth2AuthorizationLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param oAuth2AuthorizationId the primary key of the o auth2 authorization
	 * @return the o auth2 authorization that was removed
	 * @throws PortalException if a o auth2 authorization with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public OAuth2Authorization deleteOAuth2Authorization(
			long oAuth2AuthorizationId)
		throws PortalException;

	/**
	 * Deletes the o auth2 authorization from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect OAuth2AuthorizationLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param oAuth2Authorization the o auth2 authorization
	 * @return the o auth2 authorization that was removed
	 */
	@Indexable(type = IndexableType.DELETE)
	public OAuth2Authorization deleteOAuth2Authorization(
		OAuth2Authorization oAuth2Authorization);

	public void deleteOAuth2Authorizations(
		long userId, long oAuth2ApplicationId);

	public void deleteOAuth2ScopeGrantOAuth2Authorization(
		long oAuth2ScopeGrantId, long oAuth2AuthorizationId);

	public void deleteOAuth2ScopeGrantOAuth2Authorization(
		long oAuth2ScopeGrantId, OAuth2Authorization oAuth2Authorization);

	public void deleteOAuth2ScopeGrantOAuth2Authorizations(
		long oAuth2ScopeGrantId,
		List<OAuth2Authorization> oAuth2Authorizations);

	public void deleteOAuth2ScopeGrantOAuth2Authorizations(
		long oAuth2ScopeGrantId, long[] oAuth2AuthorizationIds);

	/**
	 * @throws PortalException
	 */
	@Override
	public PersistedModel deletePersistedModel(PersistedModel persistedModel)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> T dslQuery(DSLQuery dslQuery);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int dslQueryCount(DSLQuery dslQuery);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DynamicQuery dynamicQuery();

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(DynamicQuery dynamicQuery);

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.oauth2.provider.model.impl.OAuth2AuthorizationModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @return the range of matching rows
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end);

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.oauth2.provider.model.impl.OAuth2AuthorizationModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching rows
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<T> orderByComparator);

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long dynamicQueryCount(DynamicQuery dynamicQuery);

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long dynamicQueryCount(
		DynamicQuery dynamicQuery, Projection projection);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public OAuth2Authorization fetchOAuth2Authorization(
		long oAuth2AuthorizationId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public OAuth2Authorization fetchOAuth2AuthorizationByAccessTokenContent(
		String accessTokenContent);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public OAuth2Authorization fetchOAuth2AuthorizationByRefreshTokenContent(
		String refreshTokenContent);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public OAuth2Authorization fetchOAuth2AuthorizationByRememberDeviceContent(
		long userId, long oAuth2ApplicationId, String rememberDeviceContent);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public IndexableActionableDynamicQuery getIndexableActionableDynamicQuery();

	/**
	 * Returns the o auth2 authorization with the primary key.
	 *
	 * @param oAuth2AuthorizationId the primary key of the o auth2 authorization
	 * @return the o auth2 authorization
	 * @throws PortalException if a o auth2 authorization with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public OAuth2Authorization getOAuth2Authorization(
			long oAuth2AuthorizationId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public OAuth2Authorization getOAuth2AuthorizationByAccessTokenContent(
			String accessTokenContent)
		throws NoSuchOAuth2AuthorizationException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public OAuth2Authorization getOAuth2AuthorizationByRefreshTokenContent(
			String refreshTokenContent)
		throws NoSuchOAuth2AuthorizationException;

	/**
	 * Returns a range of all the o auth2 authorizations.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.oauth2.provider.model.impl.OAuth2AuthorizationModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of o auth2 authorizations
	 * @param end the upper bound of the range of o auth2 authorizations (not inclusive)
	 * @return the range of o auth2 authorizations
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<OAuth2Authorization> getOAuth2Authorizations(
		int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<OAuth2Authorization> getOAuth2Authorizations(
		long oAuth2ApplicationId, int start, int end,
		OrderByComparator<OAuth2Authorization> orderByComparator);

	/**
	 * Returns the number of o auth2 authorizations.
	 *
	 * @return the number of o auth2 authorizations
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getOAuth2AuthorizationsCount();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getOAuth2AuthorizationsCount(long oAuth2ApplicationId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<OAuth2Authorization> getOAuth2ScopeGrantOAuth2Authorizations(
		long oAuth2ScopeGrantId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<OAuth2Authorization> getOAuth2ScopeGrantOAuth2Authorizations(
		long oAuth2ScopeGrantId, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<OAuth2Authorization> getOAuth2ScopeGrantOAuth2Authorizations(
		long oAuth2ScopeGrantId, int start, int end,
		OrderByComparator<OAuth2Authorization> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getOAuth2ScopeGrantOAuth2AuthorizationsCount(
		long oAuth2ScopeGrantId);

	/**
	 * Returns the oAuth2ScopeGrantIds of the o auth2 scope grants associated with the o auth2 authorization.
	 *
	 * @param oAuth2AuthorizationId the oAuth2AuthorizationId of the o auth2 authorization
	 * @return long[] the oAuth2ScopeGrantIds of o auth2 scope grants associated with the o auth2 authorization
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long[] getOAuth2ScopeGrantPrimaryKeys(long oAuth2AuthorizationId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Collection<OAuth2ScopeGrant> getOAuth2ScopeGrants(
		long oAuth2AuthorizationId);

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	/**
	 * @throws PortalException
	 */
	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<OAuth2Authorization> getUserOAuth2Authorizations(
		long userId, int start, int end,
		OrderByComparator<OAuth2Authorization> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getUserOAuth2AuthorizationsCount(long userId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasOAuth2ScopeGrantOAuth2Authorization(
		long oAuth2ScopeGrantId, long oAuth2AuthorizationId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasOAuth2ScopeGrantOAuth2Authorizations(
		long oAuth2ScopeGrantId);

	public void setOAuth2ScopeGrantOAuth2Authorizations(
		long oAuth2ScopeGrantId, long[] oAuth2AuthorizationIds);

	/**
	 * Updates the o auth2 authorization in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect OAuth2AuthorizationLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param oAuth2Authorization the o auth2 authorization
	 * @return the o auth2 authorization that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public OAuth2Authorization updateOAuth2Authorization(
		OAuth2Authorization oAuth2Authorization);

	public OAuth2Authorization updateRememberDeviceContent(
		String refreshTokenContent, String rememberDeviceContent);

}
// LIFERAY-SERVICE-BUILDER-HASH:-1370160621