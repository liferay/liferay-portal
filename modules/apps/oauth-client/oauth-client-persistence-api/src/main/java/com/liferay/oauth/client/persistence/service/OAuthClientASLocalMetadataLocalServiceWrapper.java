/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.persistence.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link OAuthClientASLocalMetadataLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see OAuthClientASLocalMetadataLocalService
 * @generated
 */
public class OAuthClientASLocalMetadataLocalServiceWrapper
	implements OAuthClientASLocalMetadataLocalService,
			   ServiceWrapper<OAuthClientASLocalMetadataLocalService> {

	public OAuthClientASLocalMetadataLocalServiceWrapper() {
		this(null);
	}

	public OAuthClientASLocalMetadataLocalServiceWrapper(
		OAuthClientASLocalMetadataLocalService
			oAuthClientASLocalMetadataLocalService) {

		_oAuthClientASLocalMetadataLocalService =
			oAuthClientASLocalMetadataLocalService;
	}

	@Override
	public com.liferay.oauth.client.persistence.model.OAuthClientASLocalMetadata
			addOAuthClientASLocalMetadata(
				long userId, Boolean enabled, String issuerString,
				String jwksUri, String[] supportedGrantTypes,
				String[] supportedScopes, String[] supportedSubjectTypes,
				String tokenEndpointString, String userinfoEndpoint)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _oAuthClientASLocalMetadataLocalService.
			addOAuthClientASLocalMetadata(
				userId, enabled, issuerString, jwksUri, supportedGrantTypes,
				supportedScopes, supportedSubjectTypes, tokenEndpointString,
				userinfoEndpoint);
	}

	/**
	 * Adds the o auth client as local metadata to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect OAuthClientASLocalMetadataLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param oAuthClientASLocalMetadata the o auth client as local metadata
	 * @return the o auth client as local metadata that was added
	 */
	@Override
	public com.liferay.oauth.client.persistence.model.OAuthClientASLocalMetadata
		addOAuthClientASLocalMetadata(
			com.liferay.oauth.client.persistence.model.
				OAuthClientASLocalMetadata oAuthClientASLocalMetadata) {

		return _oAuthClientASLocalMetadataLocalService.
			addOAuthClientASLocalMetadata(oAuthClientASLocalMetadata);
	}

	/**
	 * Creates a new o auth client as local metadata with the primary key. Does not add the o auth client as local metadata to the database.
	 *
	 * @param oAuthClientASLocalMetadataId the primary key for the new o auth client as local metadata
	 * @return the new o auth client as local metadata
	 */
	@Override
	public com.liferay.oauth.client.persistence.model.OAuthClientASLocalMetadata
		createOAuthClientASLocalMetadata(long oAuthClientASLocalMetadataId) {

		return _oAuthClientASLocalMetadataLocalService.
			createOAuthClientASLocalMetadata(oAuthClientASLocalMetadataId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _oAuthClientASLocalMetadataLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the o auth client as local metadata with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect OAuthClientASLocalMetadataLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param oAuthClientASLocalMetadataId the primary key of the o auth client as local metadata
	 * @return the o auth client as local metadata that was removed
	 * @throws PortalException if a o auth client as local metadata with the primary key could not be found
	 */
	@Override
	public com.liferay.oauth.client.persistence.model.OAuthClientASLocalMetadata
			deleteOAuthClientASLocalMetadata(long oAuthClientASLocalMetadataId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _oAuthClientASLocalMetadataLocalService.
			deleteOAuthClientASLocalMetadata(oAuthClientASLocalMetadataId);
	}

	/**
	 * Deletes the o auth client as local metadata from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect OAuthClientASLocalMetadataLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param oAuthClientASLocalMetadata the o auth client as local metadata
	 * @return the o auth client as local metadata that was removed
	 * @throws PortalException
	 */
	@Override
	public com.liferay.oauth.client.persistence.model.OAuthClientASLocalMetadata
			deleteOAuthClientASLocalMetadata(
				com.liferay.oauth.client.persistence.model.
					OAuthClientASLocalMetadata oAuthClientASLocalMetadata)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _oAuthClientASLocalMetadataLocalService.
			deleteOAuthClientASLocalMetadata(oAuthClientASLocalMetadata);
	}

	@Override
	public com.liferay.oauth.client.persistence.model.OAuthClientASLocalMetadata
			deleteOAuthClientASLocalMetadata(String localWellKnownURI)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _oAuthClientASLocalMetadataLocalService.
			deleteOAuthClientASLocalMetadata(localWellKnownURI);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _oAuthClientASLocalMetadataLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _oAuthClientASLocalMetadataLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _oAuthClientASLocalMetadataLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _oAuthClientASLocalMetadataLocalService.dynamicQuery();
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

		return _oAuthClientASLocalMetadataLocalService.dynamicQuery(
			dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.oauth.client.persistence.model.impl.OAuthClientASLocalMetadataModelImpl</code>.
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

		return _oAuthClientASLocalMetadataLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.oauth.client.persistence.model.impl.OAuthClientASLocalMetadataModelImpl</code>.
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

		return _oAuthClientASLocalMetadataLocalService.dynamicQuery(
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

		return _oAuthClientASLocalMetadataLocalService.dynamicQueryCount(
			dynamicQuery);
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

		return _oAuthClientASLocalMetadataLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.oauth.client.persistence.model.OAuthClientASLocalMetadata
		fetchOAuthClientASLocalMetadata(long oAuthClientASLocalMetadataId) {

		return _oAuthClientASLocalMetadataLocalService.
			fetchOAuthClientASLocalMetadata(oAuthClientASLocalMetadataId);
	}

	@Override
	public com.liferay.oauth.client.persistence.model.OAuthClientASLocalMetadata
		fetchOAuthClientASLocalMetadata(String localWellKnownURI) {

		return _oAuthClientASLocalMetadataLocalService.
			fetchOAuthClientASLocalMetadata(localWellKnownURI);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _oAuthClientASLocalMetadataLocalService.
			getActionableDynamicQuery();
	}

	@Override
	public java.util.List
		<com.liferay.oauth.client.persistence.model.OAuthClientASLocalMetadata>
			getCompanyOAuthClientASLocalMetadata(long companyId) {

		return _oAuthClientASLocalMetadataLocalService.
			getCompanyOAuthClientASLocalMetadata(companyId);
	}

	@Override
	public java.util.List
		<com.liferay.oauth.client.persistence.model.OAuthClientASLocalMetadata>
			getCompanyOAuthClientASLocalMetadata(
				long companyId, int start, int end) {

		return _oAuthClientASLocalMetadataLocalService.
			getCompanyOAuthClientASLocalMetadata(companyId, start, end);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _oAuthClientASLocalMetadataLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the o auth client as local metadata with the primary key.
	 *
	 * @param oAuthClientASLocalMetadataId the primary key of the o auth client as local metadata
	 * @return the o auth client as local metadata
	 * @throws PortalException if a o auth client as local metadata with the primary key could not be found
	 */
	@Override
	public com.liferay.oauth.client.persistence.model.OAuthClientASLocalMetadata
			getOAuthClientASLocalMetadata(long oAuthClientASLocalMetadataId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _oAuthClientASLocalMetadataLocalService.
			getOAuthClientASLocalMetadata(oAuthClientASLocalMetadataId);
	}

	@Override
	public com.liferay.oauth.client.persistence.model.OAuthClientASLocalMetadata
			getOAuthClientASLocalMetadata(String localWellKnownURI)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _oAuthClientASLocalMetadataLocalService.
			getOAuthClientASLocalMetadata(localWellKnownURI);
	}

	/**
	 * Returns a range of all the o auth client as local metadatas.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.oauth.client.persistence.model.impl.OAuthClientASLocalMetadataModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of o auth client as local metadatas
	 * @param end the upper bound of the range of o auth client as local metadatas (not inclusive)
	 * @return the range of o auth client as local metadatas
	 */
	@Override
	public java.util.List
		<com.liferay.oauth.client.persistence.model.OAuthClientASLocalMetadata>
			getOAuthClientASLocalMetadatas(int start, int end) {

		return _oAuthClientASLocalMetadataLocalService.
			getOAuthClientASLocalMetadatas(start, end);
	}

	/**
	 * Returns the number of o auth client as local metadatas.
	 *
	 * @return the number of o auth client as local metadatas
	 */
	@Override
	public int getOAuthClientASLocalMetadatasCount() {
		return _oAuthClientASLocalMetadataLocalService.
			getOAuthClientASLocalMetadatasCount();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _oAuthClientASLocalMetadataLocalService.
			getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _oAuthClientASLocalMetadataLocalService.getPersistedModel(
			primaryKeyObj);
	}

	@Override
	public java.util.List
		<com.liferay.oauth.client.persistence.model.OAuthClientASLocalMetadata>
			getUserOAuthClientASLocalMetadata(long userId) {

		return _oAuthClientASLocalMetadataLocalService.
			getUserOAuthClientASLocalMetadata(userId);
	}

	@Override
	public java.util.List
		<com.liferay.oauth.client.persistence.model.OAuthClientASLocalMetadata>
			getUserOAuthClientASLocalMetadata(long userId, int start, int end) {

		return _oAuthClientASLocalMetadataLocalService.
			getUserOAuthClientASLocalMetadata(userId, start, end);
	}

	@Override
	public com.liferay.oauth.client.persistence.model.OAuthClientASLocalMetadata
			updateOAuthClientASLocalMetadata(
				long oAuthClientASLocalMetadataId, Boolean enabled,
				String issuerString, String jwksUri,
				String[] supportedGrantTypes, String[] supportedScopes,
				String[] supportedSubjectTypes, String tokenEndpointString,
				String userinfoEndpoint)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _oAuthClientASLocalMetadataLocalService.
			updateOAuthClientASLocalMetadata(
				oAuthClientASLocalMetadataId, enabled, issuerString, jwksUri,
				supportedGrantTypes, supportedScopes, supportedSubjectTypes,
				tokenEndpointString, userinfoEndpoint);
	}

	/**
	 * Updates the o auth client as local metadata in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect OAuthClientASLocalMetadataLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param oAuthClientASLocalMetadata the o auth client as local metadata
	 * @return the o auth client as local metadata that was updated
	 */
	@Override
	public com.liferay.oauth.client.persistence.model.OAuthClientASLocalMetadata
		updateOAuthClientASLocalMetadata(
			com.liferay.oauth.client.persistence.model.
				OAuthClientASLocalMetadata oAuthClientASLocalMetadata) {

		return _oAuthClientASLocalMetadataLocalService.
			updateOAuthClientASLocalMetadata(oAuthClientASLocalMetadata);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _oAuthClientASLocalMetadataLocalService.getBasePersistence();
	}

	@Override
	public OAuthClientASLocalMetadataLocalService getWrappedService() {
		return _oAuthClientASLocalMetadataLocalService;
	}

	@Override
	public void setWrappedService(
		OAuthClientASLocalMetadataLocalService
			oAuthClientASLocalMetadataLocalService) {

		_oAuthClientASLocalMetadataLocalService =
			oAuthClientASLocalMetadataLocalService;
	}

	private OAuthClientASLocalMetadataLocalService
		_oAuthClientASLocalMetadataLocalService;

}