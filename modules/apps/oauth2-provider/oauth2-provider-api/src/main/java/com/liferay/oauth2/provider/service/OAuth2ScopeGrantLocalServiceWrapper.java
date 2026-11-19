/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link OAuth2ScopeGrantLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see OAuth2ScopeGrantLocalService
 * @generated
 */
public class OAuth2ScopeGrantLocalServiceWrapper
	implements OAuth2ScopeGrantLocalService,
			   ServiceWrapper<OAuth2ScopeGrantLocalService> {

	public OAuth2ScopeGrantLocalServiceWrapper() {
		this(null);
	}

	public OAuth2ScopeGrantLocalServiceWrapper(
		OAuth2ScopeGrantLocalService oAuth2ScopeGrantLocalService) {

		_oAuth2ScopeGrantLocalService = oAuth2ScopeGrantLocalService;
	}

	@Override
	public boolean addOAuth2AuthorizationOAuth2ScopeGrant(
		long oAuth2AuthorizationId, long oAuth2ScopeGrantId) {

		return _oAuth2ScopeGrantLocalService.
			addOAuth2AuthorizationOAuth2ScopeGrant(
				oAuth2AuthorizationId, oAuth2ScopeGrantId);
	}

	@Override
	public boolean addOAuth2AuthorizationOAuth2ScopeGrant(
		long oAuth2AuthorizationId,
		com.liferay.oauth2.provider.model.OAuth2ScopeGrant oAuth2ScopeGrant) {

		return _oAuth2ScopeGrantLocalService.
			addOAuth2AuthorizationOAuth2ScopeGrant(
				oAuth2AuthorizationId, oAuth2ScopeGrant);
	}

	@Override
	public boolean addOAuth2AuthorizationOAuth2ScopeGrants(
		long oAuth2AuthorizationId,
		java.util.List<com.liferay.oauth2.provider.model.OAuth2ScopeGrant>
			oAuth2ScopeGrants) {

		return _oAuth2ScopeGrantLocalService.
			addOAuth2AuthorizationOAuth2ScopeGrants(
				oAuth2AuthorizationId, oAuth2ScopeGrants);
	}

	@Override
	public boolean addOAuth2AuthorizationOAuth2ScopeGrants(
		long oAuth2AuthorizationId, long[] oAuth2ScopeGrantIds) {

		return _oAuth2ScopeGrantLocalService.
			addOAuth2AuthorizationOAuth2ScopeGrants(
				oAuth2AuthorizationId, oAuth2ScopeGrantIds);
	}

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
	@Override
	public com.liferay.oauth2.provider.model.OAuth2ScopeGrant
		addOAuth2ScopeGrant(
			com.liferay.oauth2.provider.model.OAuth2ScopeGrant
				oAuth2ScopeGrant) {

		return _oAuth2ScopeGrantLocalService.addOAuth2ScopeGrant(
			oAuth2ScopeGrant);
	}

	@Override
	public void clearOAuth2AuthorizationOAuth2ScopeGrants(
		long oAuth2AuthorizationId) {

		_oAuth2ScopeGrantLocalService.clearOAuth2AuthorizationOAuth2ScopeGrants(
			oAuth2AuthorizationId);
	}

	/**
	 * Creates a new o auth2 scope grant with the primary key. Does not add the o auth2 scope grant to the database.
	 *
	 * @param oAuth2ScopeGrantId the primary key for the new o auth2 scope grant
	 * @return the new o auth2 scope grant
	 */
	@Override
	public com.liferay.oauth2.provider.model.OAuth2ScopeGrant
		createOAuth2ScopeGrant(long oAuth2ScopeGrantId) {

		return _oAuth2ScopeGrantLocalService.createOAuth2ScopeGrant(
			oAuth2ScopeGrantId);
	}

	@Override
	public com.liferay.oauth2.provider.model.OAuth2ScopeGrant
			createOAuth2ScopeGrant(
				long companyId, long oAuth2ApplicationScopeAliasesId,
				String applicationName, String bundleSymbolicName, String scope)
		throws com.liferay.oauth2.provider.exception.
			DuplicateOAuth2ScopeGrantException {

		return _oAuth2ScopeGrantLocalService.createOAuth2ScopeGrant(
			companyId, oAuth2ApplicationScopeAliasesId, applicationName,
			bundleSymbolicName, scope);
	}

	@Override
	public com.liferay.oauth2.provider.model.OAuth2ScopeGrant
			createOAuth2ScopeGrant(
				long companyId, long oAuth2ApplicationScopeAliasesId,
				String applicationName, String bundleSymbolicName, String scope,
				java.util.List<String> scopeAliases)
		throws com.liferay.oauth2.provider.exception.
			DuplicateOAuth2ScopeGrantException {

		return _oAuth2ScopeGrantLocalService.createOAuth2ScopeGrant(
			companyId, oAuth2ApplicationScopeAliasesId, applicationName,
			bundleSymbolicName, scope, scopeAliases);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _oAuth2ScopeGrantLocalService.createPersistedModel(
			primaryKeyObj);
	}

	@Override
	public void deleteOAuth2AuthorizationOAuth2ScopeGrant(
		long oAuth2AuthorizationId, long oAuth2ScopeGrantId) {

		_oAuth2ScopeGrantLocalService.deleteOAuth2AuthorizationOAuth2ScopeGrant(
			oAuth2AuthorizationId, oAuth2ScopeGrantId);
	}

	@Override
	public void deleteOAuth2AuthorizationOAuth2ScopeGrant(
		long oAuth2AuthorizationId,
		com.liferay.oauth2.provider.model.OAuth2ScopeGrant oAuth2ScopeGrant) {

		_oAuth2ScopeGrantLocalService.deleteOAuth2AuthorizationOAuth2ScopeGrant(
			oAuth2AuthorizationId, oAuth2ScopeGrant);
	}

	@Override
	public void deleteOAuth2AuthorizationOAuth2ScopeGrants(
		long oAuth2AuthorizationId,
		java.util.List<com.liferay.oauth2.provider.model.OAuth2ScopeGrant>
			oAuth2ScopeGrants) {

		_oAuth2ScopeGrantLocalService.
			deleteOAuth2AuthorizationOAuth2ScopeGrants(
				oAuth2AuthorizationId, oAuth2ScopeGrants);
	}

	@Override
	public void deleteOAuth2AuthorizationOAuth2ScopeGrants(
		long oAuth2AuthorizationId, long[] oAuth2ScopeGrantIds) {

		_oAuth2ScopeGrantLocalService.
			deleteOAuth2AuthorizationOAuth2ScopeGrants(
				oAuth2AuthorizationId, oAuth2ScopeGrantIds);
	}

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
	@Override
	public com.liferay.oauth2.provider.model.OAuth2ScopeGrant
			deleteOAuth2ScopeGrant(long oAuth2ScopeGrantId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _oAuth2ScopeGrantLocalService.deleteOAuth2ScopeGrant(
			oAuth2ScopeGrantId);
	}

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
	@Override
	public com.liferay.oauth2.provider.model.OAuth2ScopeGrant
		deleteOAuth2ScopeGrant(
			com.liferay.oauth2.provider.model.OAuth2ScopeGrant
				oAuth2ScopeGrant) {

		return _oAuth2ScopeGrantLocalService.deleteOAuth2ScopeGrant(
			oAuth2ScopeGrant);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _oAuth2ScopeGrantLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _oAuth2ScopeGrantLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _oAuth2ScopeGrantLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _oAuth2ScopeGrantLocalService.dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _oAuth2ScopeGrantLocalService.dynamicQuery(dynamicQuery);
	}

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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _oAuth2ScopeGrantLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _oAuth2ScopeGrantLocalService.dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _oAuth2ScopeGrantLocalService.dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return _oAuth2ScopeGrantLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.oauth2.provider.model.OAuth2ScopeGrant
		fetchOAuth2ScopeGrant(long oAuth2ScopeGrantId) {

		return _oAuth2ScopeGrantLocalService.fetchOAuth2ScopeGrant(
			oAuth2ScopeGrantId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _oAuth2ScopeGrantLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _oAuth2ScopeGrantLocalService.
			getIndexableActionableDynamicQuery();
	}

	@Override
	public java.util.List<com.liferay.oauth2.provider.model.OAuth2ScopeGrant>
		getOAuth2AuthorizationOAuth2ScopeGrants(long oAuth2AuthorizationId) {

		return _oAuth2ScopeGrantLocalService.
			getOAuth2AuthorizationOAuth2ScopeGrants(oAuth2AuthorizationId);
	}

	@Override
	public java.util.List<com.liferay.oauth2.provider.model.OAuth2ScopeGrant>
		getOAuth2AuthorizationOAuth2ScopeGrants(
			long oAuth2AuthorizationId, int start, int end) {

		return _oAuth2ScopeGrantLocalService.
			getOAuth2AuthorizationOAuth2ScopeGrants(
				oAuth2AuthorizationId, start, end);
	}

	@Override
	public java.util.List<com.liferay.oauth2.provider.model.OAuth2ScopeGrant>
		getOAuth2AuthorizationOAuth2ScopeGrants(
			long oAuth2AuthorizationId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.oauth2.provider.model.OAuth2ScopeGrant>
					orderByComparator) {

		return _oAuth2ScopeGrantLocalService.
			getOAuth2AuthorizationOAuth2ScopeGrants(
				oAuth2AuthorizationId, start, end, orderByComparator);
	}

	@Override
	public int getOAuth2AuthorizationOAuth2ScopeGrantsCount(
		long oAuth2AuthorizationId) {

		return _oAuth2ScopeGrantLocalService.
			getOAuth2AuthorizationOAuth2ScopeGrantsCount(oAuth2AuthorizationId);
	}

	/**
	 * Returns the oAuth2AuthorizationIds of the o auth2 authorizations associated with the o auth2 scope grant.
	 *
	 * @param oAuth2ScopeGrantId the oAuth2ScopeGrantId of the o auth2 scope grant
	 * @return long[] the oAuth2AuthorizationIds of o auth2 authorizations associated with the o auth2 scope grant
	 */
	@Override
	public long[] getOAuth2AuthorizationPrimaryKeys(long oAuth2ScopeGrantId) {
		return _oAuth2ScopeGrantLocalService.getOAuth2AuthorizationPrimaryKeys(
			oAuth2ScopeGrantId);
	}

	/**
	 * Returns the o auth2 scope grant with the primary key.
	 *
	 * @param oAuth2ScopeGrantId the primary key of the o auth2 scope grant
	 * @return the o auth2 scope grant
	 * @throws PortalException if a o auth2 scope grant with the primary key could not be found
	 */
	@Override
	public com.liferay.oauth2.provider.model.OAuth2ScopeGrant
			getOAuth2ScopeGrant(long oAuth2ScopeGrantId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _oAuth2ScopeGrantLocalService.getOAuth2ScopeGrant(
			oAuth2ScopeGrantId);
	}

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
	@Override
	public java.util.List<com.liferay.oauth2.provider.model.OAuth2ScopeGrant>
		getOAuth2ScopeGrants(int start, int end) {

		return _oAuth2ScopeGrantLocalService.getOAuth2ScopeGrants(start, end);
	}

	@Override
	public java.util.Collection
		<com.liferay.oauth2.provider.model.OAuth2ScopeGrant>
			getOAuth2ScopeGrants(
				long oAuth2ApplicationScopeAliasesId, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.oauth2.provider.model.OAuth2ScopeGrant>
						orderByComparator) {

		return _oAuth2ScopeGrantLocalService.getOAuth2ScopeGrants(
			oAuth2ApplicationScopeAliasesId, start, end, orderByComparator);
	}

	@Override
	public java.util.Collection
		<com.liferay.oauth2.provider.model.OAuth2ScopeGrant>
			getOAuth2ScopeGrants(
				long companyId, String applicationName,
				String bundleSymbolicName, String accessTokenContent) {

		return _oAuth2ScopeGrantLocalService.getOAuth2ScopeGrants(
			companyId, applicationName, bundleSymbolicName, accessTokenContent);
	}

	/**
	 * Returns the number of o auth2 scope grants.
	 *
	 * @return the number of o auth2 scope grants
	 */
	@Override
	public int getOAuth2ScopeGrantsCount() {
		return _oAuth2ScopeGrantLocalService.getOAuth2ScopeGrantsCount();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _oAuth2ScopeGrantLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _oAuth2ScopeGrantLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public java.util.Collection
		<com.liferay.oauth2.provider.model.OAuth2ScopeGrant>
				grantLiferayOAuth2Scopes(
					long oAuth2AuthorizationId,
					java.util.Collection
						<com.liferay.oauth2.provider.scope.liferay.
							LiferayOAuth2Scope> liferayOAuth2Scopes)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _oAuth2ScopeGrantLocalService.grantLiferayOAuth2Scopes(
			oAuth2AuthorizationId, liferayOAuth2Scopes);
	}

	@Override
	public boolean hasOAuth2AuthorizationOAuth2ScopeGrant(
		long oAuth2AuthorizationId, long oAuth2ScopeGrantId) {

		return _oAuth2ScopeGrantLocalService.
			hasOAuth2AuthorizationOAuth2ScopeGrant(
				oAuth2AuthorizationId, oAuth2ScopeGrantId);
	}

	@Override
	public boolean hasOAuth2AuthorizationOAuth2ScopeGrants(
		long oAuth2AuthorizationId) {

		return _oAuth2ScopeGrantLocalService.
			hasOAuth2AuthorizationOAuth2ScopeGrants(oAuth2AuthorizationId);
	}

	@Override
	public void setOAuth2AuthorizationOAuth2ScopeGrants(
		long oAuth2AuthorizationId, long[] oAuth2ScopeGrantIds) {

		_oAuth2ScopeGrantLocalService.setOAuth2AuthorizationOAuth2ScopeGrants(
			oAuth2AuthorizationId, oAuth2ScopeGrantIds);
	}

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
	@Override
	public com.liferay.oauth2.provider.model.OAuth2ScopeGrant
		updateOAuth2ScopeGrant(
			com.liferay.oauth2.provider.model.OAuth2ScopeGrant
				oAuth2ScopeGrant) {

		return _oAuth2ScopeGrantLocalService.updateOAuth2ScopeGrant(
			oAuth2ScopeGrant);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _oAuth2ScopeGrantLocalService.getBasePersistence();
	}

	@Override
	public OAuth2ScopeGrantLocalService getWrappedService() {
		return _oAuth2ScopeGrantLocalService;
	}

	@Override
	public void setWrappedService(
		OAuth2ScopeGrantLocalService oAuth2ScopeGrantLocalService) {

		_oAuth2ScopeGrantLocalService = oAuth2ScopeGrantLocalService;
	}

	private OAuth2ScopeGrantLocalService _oAuth2ScopeGrantLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:522117174