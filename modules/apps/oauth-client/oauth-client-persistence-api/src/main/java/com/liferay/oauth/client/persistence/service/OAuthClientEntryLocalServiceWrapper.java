/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.persistence.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link OAuthClientEntryLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see OAuthClientEntryLocalService
 * @generated
 */
public class OAuthClientEntryLocalServiceWrapper
	implements OAuthClientEntryLocalService,
			   ServiceWrapper<OAuthClientEntryLocalService> {

	public OAuthClientEntryLocalServiceWrapper() {
		this(null);
	}

	public OAuthClientEntryLocalServiceWrapper(
		OAuthClientEntryLocalService oAuthClientEntryLocalService) {

		_oAuthClientEntryLocalService = oAuthClientEntryLocalService;
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

		return _oAuthClientEntryLocalService.addOAuthClientEntry(
			userId, authRequestParametersJSON, authServerWellKnownURI,
			customClaimsJSON, infoJSON, metadataCacheTime,
			oidcUserInfoMapperJSON, tokenRequestParametersJSON);
	}

	/**
	 * Adds the o auth client entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect OAuthClientEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param oAuthClientEntry the o auth client entry
	 * @return the o auth client entry that was added
	 */
	@Override
	public com.liferay.oauth.client.persistence.model.OAuthClientEntry
		addOAuthClientEntry(
			com.liferay.oauth.client.persistence.model.OAuthClientEntry
				oAuthClientEntry) {

		return _oAuthClientEntryLocalService.addOAuthClientEntry(
			oAuthClientEntry);
	}

	/**
	 * Creates a new o auth client entry with the primary key. Does not add the o auth client entry to the database.
	 *
	 * @param oAuthClientEntryId the primary key for the new o auth client entry
	 * @return the new o auth client entry
	 */
	@Override
	public com.liferay.oauth.client.persistence.model.OAuthClientEntry
		createOAuthClientEntry(long oAuthClientEntryId) {

		return _oAuthClientEntryLocalService.createOAuthClientEntry(
			oAuthClientEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _oAuthClientEntryLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the o auth client entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect OAuthClientEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param oAuthClientEntryId the primary key of the o auth client entry
	 * @return the o auth client entry that was removed
	 * @throws PortalException if a o auth client entry with the primary key could not be found
	 */
	@Override
	public com.liferay.oauth.client.persistence.model.OAuthClientEntry
			deleteOAuthClientEntry(long oAuthClientEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _oAuthClientEntryLocalService.deleteOAuthClientEntry(
			oAuthClientEntryId);
	}

	@Override
	public com.liferay.oauth.client.persistence.model.OAuthClientEntry
			deleteOAuthClientEntry(
				long companyId, String authServerWellKnownURI, String clientId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _oAuthClientEntryLocalService.deleteOAuthClientEntry(
			companyId, authServerWellKnownURI, clientId);
	}

	/**
	 * Deletes the o auth client entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect OAuthClientEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param oAuthClientEntry the o auth client entry
	 * @return the o auth client entry that was removed
	 * @throws PortalException
	 */
	@Override
	public com.liferay.oauth.client.persistence.model.OAuthClientEntry
			deleteOAuthClientEntry(
				com.liferay.oauth.client.persistence.model.OAuthClientEntry
					oAuthClientEntry)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _oAuthClientEntryLocalService.deleteOAuthClientEntry(
			oAuthClientEntry);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _oAuthClientEntryLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _oAuthClientEntryLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _oAuthClientEntryLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _oAuthClientEntryLocalService.dynamicQuery();
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

		return _oAuthClientEntryLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.oauth.client.persistence.model.impl.OAuthClientEntryModelImpl</code>.
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

		return _oAuthClientEntryLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.oauth.client.persistence.model.impl.OAuthClientEntryModelImpl</code>.
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

		return _oAuthClientEntryLocalService.dynamicQuery(
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

		return _oAuthClientEntryLocalService.dynamicQueryCount(dynamicQuery);
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

		return _oAuthClientEntryLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.oauth.client.persistence.model.OAuthClientEntry
		fetchOAuthClientEntry(long oAuthClientEntryId) {

		return _oAuthClientEntryLocalService.fetchOAuthClientEntry(
			oAuthClientEntryId);
	}

	@Override
	public com.liferay.oauth.client.persistence.model.OAuthClientEntry
		fetchOAuthClientEntry(
			long companyId, String authServerWellKnownURI, String clientId) {

		return _oAuthClientEntryLocalService.fetchOAuthClientEntry(
			companyId, authServerWellKnownURI, clientId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _oAuthClientEntryLocalService.getActionableDynamicQuery();
	}

	@Override
	public java.util.List
		<com.liferay.oauth.client.persistence.model.OAuthClientEntry>
			getAuthServerWellKnownURISuffixOAuthClientEntries(
				long companyId, String authServerWellKnownURISuffix) {

		return _oAuthClientEntryLocalService.
			getAuthServerWellKnownURISuffixOAuthClientEntries(
				companyId, authServerWellKnownURISuffix);
	}

	@Override
	public java.util.List
		<com.liferay.oauth.client.persistence.model.OAuthClientEntry>
			getCompanyOAuthClientEntries(long companyId) {

		return _oAuthClientEntryLocalService.getCompanyOAuthClientEntries(
			companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _oAuthClientEntryLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns a range of all the o auth client entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.oauth.client.persistence.model.impl.OAuthClientEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of o auth client entries
	 * @param end the upper bound of the range of o auth client entries (not inclusive)
	 * @return the range of o auth client entries
	 */
	@Override
	public java.util.List
		<com.liferay.oauth.client.persistence.model.OAuthClientEntry>
			getOAuthClientEntries(int start, int end) {

		return _oAuthClientEntryLocalService.getOAuthClientEntries(start, end);
	}

	/**
	 * Returns the number of o auth client entries.
	 *
	 * @return the number of o auth client entries
	 */
	@Override
	public int getOAuthClientEntriesCount() {
		return _oAuthClientEntryLocalService.getOAuthClientEntriesCount();
	}

	/**
	 * Returns the o auth client entry with the primary key.
	 *
	 * @param oAuthClientEntryId the primary key of the o auth client entry
	 * @return the o auth client entry
	 * @throws PortalException if a o auth client entry with the primary key could not be found
	 */
	@Override
	public com.liferay.oauth.client.persistence.model.OAuthClientEntry
			getOAuthClientEntry(long oAuthClientEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _oAuthClientEntryLocalService.getOAuthClientEntry(
			oAuthClientEntryId);
	}

	@Override
	public com.liferay.oauth.client.persistence.model.OAuthClientEntry
			getOAuthClientEntry(
				long companyId, String authServerWellKnownURI, String clientId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _oAuthClientEntryLocalService.getOAuthClientEntry(
			companyId, authServerWellKnownURI, clientId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _oAuthClientEntryLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _oAuthClientEntryLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public java.util.List
		<com.liferay.oauth.client.persistence.model.OAuthClientEntry>
			getUserOAuthClientEntries(long userId) {

		return _oAuthClientEntryLocalService.getUserOAuthClientEntries(userId);
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

		return _oAuthClientEntryLocalService.updateOAuthClientEntry(
			oAuthClientEntryId, authRequestParametersJSON,
			authServerWellKnownURI, customClaimsJSON, infoJSON,
			metadataCacheTime, oidcUserInfoMapperJSON,
			tokenRequestParametersJSON);
	}

	/**
	 * Updates the o auth client entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect OAuthClientEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param oAuthClientEntry the o auth client entry
	 * @return the o auth client entry that was updated
	 */
	@Override
	public com.liferay.oauth.client.persistence.model.OAuthClientEntry
		updateOAuthClientEntry(
			com.liferay.oauth.client.persistence.model.OAuthClientEntry
				oAuthClientEntry) {

		return _oAuthClientEntryLocalService.updateOAuthClientEntry(
			oAuthClientEntry);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _oAuthClientEntryLocalService.getBasePersistence();
	}

	@Override
	public OAuthClientEntryLocalService getWrappedService() {
		return _oAuthClientEntryLocalService;
	}

	@Override
	public void setWrappedService(
		OAuthClientEntryLocalService oAuthClientEntryLocalService) {

		_oAuthClientEntryLocalService = oAuthClientEntryLocalService;
	}

	private OAuthClientEntryLocalService _oAuthClientEntryLocalService;

}