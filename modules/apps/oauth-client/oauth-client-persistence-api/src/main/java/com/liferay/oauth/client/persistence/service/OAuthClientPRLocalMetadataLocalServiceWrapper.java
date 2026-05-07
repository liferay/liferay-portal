/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.persistence.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link OAuthClientPRLocalMetadataLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see OAuthClientPRLocalMetadataLocalService
 * @generated
 */
public class OAuthClientPRLocalMetadataLocalServiceWrapper
	implements OAuthClientPRLocalMetadataLocalService,
			   ServiceWrapper<OAuthClientPRLocalMetadataLocalService> {

	public OAuthClientPRLocalMetadataLocalServiceWrapper() {
		this(null);
	}

	public OAuthClientPRLocalMetadataLocalServiceWrapper(
		OAuthClientPRLocalMetadataLocalService
			oAuthClientPRLocalMetadataLocalService) {

		_oAuthClientPRLocalMetadataLocalService =
			oAuthClientPRLocalMetadataLocalService;
	}

	/**
	 * Adds the o auth client pr local metadata to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect OAuthClientPRLocalMetadataLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param oAuthClientPRLocalMetadata the o auth client pr local metadata
	 * @return the o auth client pr local metadata that was added
	 */
	@Override
	public com.liferay.oauth.client.persistence.model.OAuthClientPRLocalMetadata
		addOAuthClientPRLocalMetadata(
			com.liferay.oauth.client.persistence.model.
				OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata) {

		return _oAuthClientPRLocalMetadataLocalService.
			addOAuthClientPRLocalMetadata(oAuthClientPRLocalMetadata);
	}

	/**
	 * Creates a new o auth client pr local metadata with the primary key. Does not add the o auth client pr local metadata to the database.
	 *
	 * @param oAuthClientPRLocalMetadataId the primary key for the new o auth client pr local metadata
	 * @return the new o auth client pr local metadata
	 */
	@Override
	public com.liferay.oauth.client.persistence.model.OAuthClientPRLocalMetadata
		createOAuthClientPRLocalMetadata(long oAuthClientPRLocalMetadataId) {

		return _oAuthClientPRLocalMetadataLocalService.
			createOAuthClientPRLocalMetadata(oAuthClientPRLocalMetadataId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _oAuthClientPRLocalMetadataLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the o auth client pr local metadata with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect OAuthClientPRLocalMetadataLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param oAuthClientPRLocalMetadataId the primary key of the o auth client pr local metadata
	 * @return the o auth client pr local metadata that was removed
	 * @throws PortalException if a o auth client pr local metadata with the primary key could not be found
	 */
	@Override
	public com.liferay.oauth.client.persistence.model.OAuthClientPRLocalMetadata
			deleteOAuthClientPRLocalMetadata(long oAuthClientPRLocalMetadataId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _oAuthClientPRLocalMetadataLocalService.
			deleteOAuthClientPRLocalMetadata(oAuthClientPRLocalMetadataId);
	}

	/**
	 * Deletes the o auth client pr local metadata from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect OAuthClientPRLocalMetadataLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param oAuthClientPRLocalMetadata the o auth client pr local metadata
	 * @return the o auth client pr local metadata that was removed
	 */
	@Override
	public com.liferay.oauth.client.persistence.model.OAuthClientPRLocalMetadata
		deleteOAuthClientPRLocalMetadata(
			com.liferay.oauth.client.persistence.model.
				OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata) {

		return _oAuthClientPRLocalMetadataLocalService.
			deleteOAuthClientPRLocalMetadata(oAuthClientPRLocalMetadata);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _oAuthClientPRLocalMetadataLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _oAuthClientPRLocalMetadataLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _oAuthClientPRLocalMetadataLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _oAuthClientPRLocalMetadataLocalService.dynamicQuery();
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

		return _oAuthClientPRLocalMetadataLocalService.dynamicQuery(
			dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.oauth.client.persistence.model.impl.OAuthClientPRLocalMetadataModelImpl</code>.
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

		return _oAuthClientPRLocalMetadataLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.oauth.client.persistence.model.impl.OAuthClientPRLocalMetadataModelImpl</code>.
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

		return _oAuthClientPRLocalMetadataLocalService.dynamicQuery(
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

		return _oAuthClientPRLocalMetadataLocalService.dynamicQueryCount(
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

		return _oAuthClientPRLocalMetadataLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.oauth.client.persistence.model.OAuthClientPRLocalMetadata
		fetchOAuthClientPRLocalMetadata(long oAuthClientPRLocalMetadataId) {

		return _oAuthClientPRLocalMetadataLocalService.
			fetchOAuthClientPRLocalMetadata(oAuthClientPRLocalMetadataId);
	}

	@Override
	public com.liferay.oauth.client.persistence.model.OAuthClientPRLocalMetadata
		fetchOAuthClientPRLocalMetadataByExternalReferenceCode(
			String externalReferenceCode, long companyId) {

		return _oAuthClientPRLocalMetadataLocalService.
			fetchOAuthClientPRLocalMetadataByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	/**
	 * Returns the o auth client pr local metadata with the matching UUID and company.
	 *
	 * @param uuid the o auth client pr local metadata's UUID
	 * @param companyId the primary key of the company
	 * @return the matching o auth client pr local metadata, or <code>null</code> if a matching o auth client pr local metadata could not be found
	 */
	@Override
	public com.liferay.oauth.client.persistence.model.OAuthClientPRLocalMetadata
		fetchOAuthClientPRLocalMetadataByUuidAndCompanyId(
			String uuid, long companyId) {

		return _oAuthClientPRLocalMetadataLocalService.
			fetchOAuthClientPRLocalMetadataByUuidAndCompanyId(uuid, companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _oAuthClientPRLocalMetadataLocalService.
			getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _oAuthClientPRLocalMetadataLocalService.
			getExportActionableDynamicQuery(portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _oAuthClientPRLocalMetadataLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the o auth client pr local metadata with the primary key.
	 *
	 * @param oAuthClientPRLocalMetadataId the primary key of the o auth client pr local metadata
	 * @return the o auth client pr local metadata
	 * @throws PortalException if a o auth client pr local metadata with the primary key could not be found
	 */
	@Override
	public com.liferay.oauth.client.persistence.model.OAuthClientPRLocalMetadata
			getOAuthClientPRLocalMetadata(long oAuthClientPRLocalMetadataId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _oAuthClientPRLocalMetadataLocalService.
			getOAuthClientPRLocalMetadata(oAuthClientPRLocalMetadataId);
	}

	@Override
	public com.liferay.oauth.client.persistence.model.OAuthClientPRLocalMetadata
			getOAuthClientPRLocalMetadataByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _oAuthClientPRLocalMetadataLocalService.
			getOAuthClientPRLocalMetadataByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	/**
	 * Returns the o auth client pr local metadata with the matching UUID and company.
	 *
	 * @param uuid the o auth client pr local metadata's UUID
	 * @param companyId the primary key of the company
	 * @return the matching o auth client pr local metadata
	 * @throws PortalException if a matching o auth client pr local metadata could not be found
	 */
	@Override
	public com.liferay.oauth.client.persistence.model.OAuthClientPRLocalMetadata
			getOAuthClientPRLocalMetadataByUuidAndCompanyId(
				String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _oAuthClientPRLocalMetadataLocalService.
			getOAuthClientPRLocalMetadataByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of all the o auth client pr local metadatas.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.oauth.client.persistence.model.impl.OAuthClientPRLocalMetadataModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of o auth client pr local metadatas
	 * @param end the upper bound of the range of o auth client pr local metadatas (not inclusive)
	 * @return the range of o auth client pr local metadatas
	 */
	@Override
	public java.util.List
		<com.liferay.oauth.client.persistence.model.OAuthClientPRLocalMetadata>
			getOAuthClientPRLocalMetadatas(int start, int end) {

		return _oAuthClientPRLocalMetadataLocalService.
			getOAuthClientPRLocalMetadatas(start, end);
	}

	/**
	 * Returns the number of o auth client pr local metadatas.
	 *
	 * @return the number of o auth client pr local metadatas
	 */
	@Override
	public int getOAuthClientPRLocalMetadatasCount() {
		return _oAuthClientPRLocalMetadataLocalService.
			getOAuthClientPRLocalMetadatasCount();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _oAuthClientPRLocalMetadataLocalService.
			getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _oAuthClientPRLocalMetadataLocalService.getPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Updates the o auth client pr local metadata in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect OAuthClientPRLocalMetadataLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param oAuthClientPRLocalMetadata the o auth client pr local metadata
	 * @return the o auth client pr local metadata that was updated
	 */
	@Override
	public com.liferay.oauth.client.persistence.model.OAuthClientPRLocalMetadata
		updateOAuthClientPRLocalMetadata(
			com.liferay.oauth.client.persistence.model.
				OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata) {

		return _oAuthClientPRLocalMetadataLocalService.
			updateOAuthClientPRLocalMetadata(oAuthClientPRLocalMetadata);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _oAuthClientPRLocalMetadataLocalService.getBasePersistence();
	}

	@Override
	public OAuthClientPRLocalMetadataLocalService getWrappedService() {
		return _oAuthClientPRLocalMetadataLocalService;
	}

	@Override
	public void setWrappedService(
		OAuthClientPRLocalMetadataLocalService
			oAuthClientPRLocalMetadataLocalService) {

		_oAuthClientPRLocalMetadataLocalService =
			oAuthClientPRLocalMetadataLocalService;
	}

	private OAuthClientPRLocalMetadataLocalService
		_oAuthClientPRLocalMetadataLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:1545713799