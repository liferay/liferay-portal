/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.service;

import com.liferay.oauth2.provider.exception.DuplicateOAuth2ScopeGrantException;
import com.liferay.oauth2.provider.model.OAuth2ScopeGrant;
import com.liferay.oauth2.provider.scope.liferay.LiferayOAuth2Scope;
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
import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for OAuth2ScopeGrant. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see OAuth2ScopeGrantLocalServiceUtil
 * @generated
 */
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface OAuth2ScopeGrantLocalService
	extends BaseLocalService, PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.oauth2.provider.service.impl.OAuth2ScopeGrantLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the o auth2 scope grant local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link OAuth2ScopeGrantLocalServiceUtil} if injection and service tracking are not available.
	 */
	public boolean addOAuth2AuthorizationOAuth2ScopeGrant(
		long oAuth2AuthorizationId, long oAuth2ScopeGrantId);

	public boolean addOAuth2AuthorizationOAuth2ScopeGrant(
		long oAuth2AuthorizationId, OAuth2ScopeGrant oAuth2ScopeGrant);

	public boolean addOAuth2AuthorizationOAuth2ScopeGrants(
		long oAuth2AuthorizationId, List<OAuth2ScopeGrant> oAuth2ScopeGrants);

	public boolean addOAuth2AuthorizationOAuth2ScopeGrants(
		long oAuth2AuthorizationId, long[] oAuth2ScopeGrantIds);

	/**
	 * Adds the o auth2 scope grant to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect OAuth2ScopeGrantLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param oAuth2ScopeGrant the o auth2 scope grant
	 * @return the o auth2 scope grant that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public OAuth2ScopeGrant addOAuth2ScopeGrant(
		OAuth2ScopeGrant oAuth2ScopeGrant);

	public void clearOAuth2AuthorizationOAuth2ScopeGrants(
		long oAuth2AuthorizationId);

	/**
	 * Creates a new o auth2 scope grant with the primary key. Does not add the o auth2 scope grant to the database.
	 *
	 * @param oAuth2ScopeGrantId the primary key for the new o auth2 scope grant
	 * @return the new o auth2 scope grant
	 */
	@Transactional(enabled = false)
	public OAuth2ScopeGrant createOAuth2ScopeGrant(long oAuth2ScopeGrantId);

	public OAuth2ScopeGrant createOAuth2ScopeGrant(
			long companyId, long oAuth2ApplicationScopeAliasesId,
			String applicationName, String bundleSymbolicName, String scope,
			List<String> scopeAliases)
		throws DuplicateOAuth2ScopeGrantException;

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	public void deleteOAuth2AuthorizationOAuth2ScopeGrant(
		long oAuth2AuthorizationId, long oAuth2ScopeGrantId);

	public void deleteOAuth2AuthorizationOAuth2ScopeGrant(
		long oAuth2AuthorizationId, OAuth2ScopeGrant oAuth2ScopeGrant);

	public void deleteOAuth2AuthorizationOAuth2ScopeGrants(
		long oAuth2AuthorizationId, List<OAuth2ScopeGrant> oAuth2ScopeGrants);

	public void deleteOAuth2AuthorizationOAuth2ScopeGrants(
		long oAuth2AuthorizationId, long[] oAuth2ScopeGrantIds);

	/**
	 * Deletes the o auth2 scope grant with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect OAuth2ScopeGrantLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param oAuth2ScopeGrantId the primary key of the o auth2 scope grant
	 * @return the o auth2 scope grant that was removed
	 * @throws PortalException if a o auth2 scope grant with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public OAuth2ScopeGrant deleteOAuth2ScopeGrant(long oAuth2ScopeGrantId)
		throws PortalException;

	/**
	 * Deletes the o auth2 scope grant from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect OAuth2ScopeGrantLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param oAuth2ScopeGrant the o auth2 scope grant
	 * @return the o auth2 scope grant that was removed
	 */
	@Indexable(type = IndexableType.DELETE)
	public OAuth2ScopeGrant deleteOAuth2ScopeGrant(
		OAuth2ScopeGrant oAuth2ScopeGrant);

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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.oauth2.provider.model.impl.OAuth2ScopeGrantModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.oauth2.provider.model.impl.OAuth2ScopeGrantModelImpl</code>.
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
	public OAuth2ScopeGrant fetchOAuth2ScopeGrant(long oAuth2ScopeGrantId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public IndexableActionableDynamicQuery getIndexableActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<OAuth2ScopeGrant> getOAuth2AuthorizationOAuth2ScopeGrants(
		long oAuth2AuthorizationId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<OAuth2ScopeGrant> getOAuth2AuthorizationOAuth2ScopeGrants(
		long oAuth2AuthorizationId, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<OAuth2ScopeGrant> getOAuth2AuthorizationOAuth2ScopeGrants(
		long oAuth2AuthorizationId, int start, int end,
		OrderByComparator<OAuth2ScopeGrant> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getOAuth2AuthorizationOAuth2ScopeGrantsCount(
		long oAuth2AuthorizationId);

	/**
	 * Returns the oAuth2AuthorizationIds of the o auth2 authorizations associated with the o auth2 scope grant.
	 *
	 * @param oAuth2ScopeGrantId the oAuth2ScopeGrantId of the o auth2 scope grant
	 * @return long[] the oAuth2AuthorizationIds of o auth2 authorizations associated with the o auth2 scope grant
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long[] getOAuth2AuthorizationPrimaryKeys(long oAuth2ScopeGrantId);

	/**
	 * Returns the o auth2 scope grant with the primary key.
	 *
	 * @param oAuth2ScopeGrantId the primary key of the o auth2 scope grant
	 * @return the o auth2 scope grant
	 * @throws PortalException if a o auth2 scope grant with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public OAuth2ScopeGrant getOAuth2ScopeGrant(long oAuth2ScopeGrantId)
		throws PortalException;

	/**
	 * Returns a range of all the o auth2 scope grants.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.oauth2.provider.model.impl.OAuth2ScopeGrantModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of o auth2 scope grants
	 * @param end the upper bound of the range of o auth2 scope grants (not inclusive)
	 * @return the range of o auth2 scope grants
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<OAuth2ScopeGrant> getOAuth2ScopeGrants(int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Collection<OAuth2ScopeGrant> getOAuth2ScopeGrants(
		long oAuth2ApplicationScopeAliasesId, int start, int end,
		OrderByComparator<OAuth2ScopeGrant> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Collection<OAuth2ScopeGrant> getOAuth2ScopeGrants(
		long companyId, String applicationName, String bundleSymbolicName,
		String accessTokenContent);

	/**
	 * Returns the number of o auth2 scope grants.
	 *
	 * @return the number of o auth2 scope grants
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getOAuth2ScopeGrantsCount();

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

	public Collection<OAuth2ScopeGrant> grantLiferayOAuth2Scopes(
			long oAuth2AuthorizationId,
			Collection<LiferayOAuth2Scope> liferayOAuth2Scopes)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasOAuth2AuthorizationOAuth2ScopeGrant(
		long oAuth2AuthorizationId, long oAuth2ScopeGrantId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasOAuth2AuthorizationOAuth2ScopeGrants(
		long oAuth2AuthorizationId);

	public void setOAuth2AuthorizationOAuth2ScopeGrants(
		long oAuth2AuthorizationId, long[] oAuth2ScopeGrantIds);

	/**
	 * Updates the o auth2 scope grant in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect OAuth2ScopeGrantLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param oAuth2ScopeGrant the o auth2 scope grant
	 * @return the o auth2 scope grant that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public OAuth2ScopeGrant updateOAuth2ScopeGrant(
		OAuth2ScopeGrant oAuth2ScopeGrant);

}
// LIFERAY-SERVICE-BUILDER-HASH:-537497951