/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.persistence.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link SamlIdpSpConnectionLocalService}.
 *
 * @author Mika Koivisto
 * @see SamlIdpSpConnectionLocalService
 * @generated
 */
public class SamlIdpSpConnectionLocalServiceWrapper
	implements SamlIdpSpConnectionLocalService,
			   ServiceWrapper<SamlIdpSpConnectionLocalService> {

	public SamlIdpSpConnectionLocalServiceWrapper() {
		this(null);
	}

	public SamlIdpSpConnectionLocalServiceWrapper(
		SamlIdpSpConnectionLocalService samlIdpSpConnectionLocalService) {

		_samlIdpSpConnectionLocalService = samlIdpSpConnectionLocalService;
	}

	/**
	 * Adds the saml idp sp connection to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SamlIdpSpConnectionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param samlIdpSpConnection the saml idp sp connection
	 * @return the saml idp sp connection that was added
	 */
	@Override
	public com.liferay.saml.persistence.model.SamlIdpSpConnection
		addSamlIdpSpConnection(
			com.liferay.saml.persistence.model.SamlIdpSpConnection
				samlIdpSpConnection) {

		return _samlIdpSpConnectionLocalService.addSamlIdpSpConnection(
			samlIdpSpConnection);
	}

	@Override
	public com.liferay.saml.persistence.model.SamlIdpSpConnection
			addSamlIdpSpConnection(
				String samlSpEntityId, int assertionLifetime,
				String attributeNames, boolean attributesEnabled,
				boolean attributesNamespaceEnabled, boolean enabled,
				boolean encryptionForced, String metadataUrl,
				java.io.InputStream metadataXmlInputStream, String name,
				String nameIdAttribute, String nameIdFormat,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _samlIdpSpConnectionLocalService.addSamlIdpSpConnection(
			samlSpEntityId, assertionLifetime, attributeNames,
			attributesEnabled, attributesNamespaceEnabled, enabled,
			encryptionForced, metadataUrl, metadataXmlInputStream, name,
			nameIdAttribute, nameIdFormat, serviceContext);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _samlIdpSpConnectionLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Creates a new saml idp sp connection with the primary key. Does not add the saml idp sp connection to the database.
	 *
	 * @param samlIdpSpConnectionId the primary key for the new saml idp sp connection
	 * @return the new saml idp sp connection
	 */
	@Override
	public com.liferay.saml.persistence.model.SamlIdpSpConnection
		createSamlIdpSpConnection(long samlIdpSpConnectionId) {

		return _samlIdpSpConnectionLocalService.createSamlIdpSpConnection(
			samlIdpSpConnectionId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _samlIdpSpConnectionLocalService.deletePersistedModel(
			persistedModel);
	}

	/**
	 * Deletes the saml idp sp connection with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SamlIdpSpConnectionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param samlIdpSpConnectionId the primary key of the saml idp sp connection
	 * @return the saml idp sp connection that was removed
	 * @throws PortalException if a saml idp sp connection with the primary key could not be found
	 */
	@Override
	public com.liferay.saml.persistence.model.SamlIdpSpConnection
			deleteSamlIdpSpConnection(long samlIdpSpConnectionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _samlIdpSpConnectionLocalService.deleteSamlIdpSpConnection(
			samlIdpSpConnectionId);
	}

	/**
	 * Deletes the saml idp sp connection from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SamlIdpSpConnectionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param samlIdpSpConnection the saml idp sp connection
	 * @return the saml idp sp connection that was removed
	 */
	@Override
	public com.liferay.saml.persistence.model.SamlIdpSpConnection
		deleteSamlIdpSpConnection(
			com.liferay.saml.persistence.model.SamlIdpSpConnection
				samlIdpSpConnection) {

		return _samlIdpSpConnectionLocalService.deleteSamlIdpSpConnection(
			samlIdpSpConnection);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _samlIdpSpConnectionLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _samlIdpSpConnectionLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _samlIdpSpConnectionLocalService.dynamicQuery();
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

		return _samlIdpSpConnectionLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.saml.persistence.model.impl.SamlIdpSpConnectionModelImpl</code>.
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

		return _samlIdpSpConnectionLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.saml.persistence.model.impl.SamlIdpSpConnectionModelImpl</code>.
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

		return _samlIdpSpConnectionLocalService.dynamicQuery(
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

		return _samlIdpSpConnectionLocalService.dynamicQueryCount(dynamicQuery);
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

		return _samlIdpSpConnectionLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.saml.persistence.model.SamlIdpSpConnection
		fetchSamlIdpSpConnection(long samlIdpSpConnectionId) {

		return _samlIdpSpConnectionLocalService.fetchSamlIdpSpConnection(
			samlIdpSpConnectionId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _samlIdpSpConnectionLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _samlIdpSpConnectionLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _samlIdpSpConnectionLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _samlIdpSpConnectionLocalService.getPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Returns the saml idp sp connection with the primary key.
	 *
	 * @param samlIdpSpConnectionId the primary key of the saml idp sp connection
	 * @return the saml idp sp connection
	 * @throws PortalException if a saml idp sp connection with the primary key could not be found
	 */
	@Override
	public com.liferay.saml.persistence.model.SamlIdpSpConnection
			getSamlIdpSpConnection(long samlIdpSpConnectionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _samlIdpSpConnectionLocalService.getSamlIdpSpConnection(
			samlIdpSpConnectionId);
	}

	@Override
	public com.liferay.saml.persistence.model.SamlIdpSpConnection
			getSamlIdpSpConnection(long companyId, String samlSpEntityId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _samlIdpSpConnectionLocalService.getSamlIdpSpConnection(
			companyId, samlSpEntityId);
	}

	/**
	 * Returns a range of all the saml idp sp connections.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.saml.persistence.model.impl.SamlIdpSpConnectionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of saml idp sp connections
	 * @param end the upper bound of the range of saml idp sp connections (not inclusive)
	 * @return the range of saml idp sp connections
	 */
	@Override
	public java.util.List
		<com.liferay.saml.persistence.model.SamlIdpSpConnection>
			getSamlIdpSpConnections(int start, int end) {

		return _samlIdpSpConnectionLocalService.getSamlIdpSpConnections(
			start, end);
	}

	@Override
	public java.util.List
		<com.liferay.saml.persistence.model.SamlIdpSpConnection>
			getSamlIdpSpConnections(long companyId) {

		return _samlIdpSpConnectionLocalService.getSamlIdpSpConnections(
			companyId);
	}

	@Override
	public java.util.List
		<com.liferay.saml.persistence.model.SamlIdpSpConnection>
			getSamlIdpSpConnections(long companyId, int start, int end) {

		return _samlIdpSpConnectionLocalService.getSamlIdpSpConnections(
			companyId, start, end);
	}

	@Override
	public java.util.List
		<com.liferay.saml.persistence.model.SamlIdpSpConnection>
			getSamlIdpSpConnections(
				long companyId, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.saml.persistence.model.SamlIdpSpConnection>
						orderByComparator) {

		return _samlIdpSpConnectionLocalService.getSamlIdpSpConnections(
			companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of saml idp sp connections.
	 *
	 * @return the number of saml idp sp connections
	 */
	@Override
	public int getSamlIdpSpConnectionsCount() {
		return _samlIdpSpConnectionLocalService.getSamlIdpSpConnectionsCount();
	}

	@Override
	public int getSamlIdpSpConnectionsCount(long companyId) {
		return _samlIdpSpConnectionLocalService.getSamlIdpSpConnectionsCount(
			companyId);
	}

	@Override
	public void updateMetadata(long samlIdpSpConnectionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_samlIdpSpConnectionLocalService.updateMetadata(samlIdpSpConnectionId);
	}

	@Override
	public com.liferay.saml.persistence.model.SamlIdpSpConnection
			updateSamlIdpSpConnection(
				long samlIdpSpConnectionId, String samlSpEntityId,
				int assertionLifetime, String attributeNames,
				boolean attributesEnabled, boolean attributesNamespaceEnabled,
				boolean enabled, boolean encryptionForced, String metadataUrl,
				java.io.InputStream metadataXmlInputStream, String name,
				String nameIdAttribute, String nameIdFormat,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _samlIdpSpConnectionLocalService.updateSamlIdpSpConnection(
			samlIdpSpConnectionId, samlSpEntityId, assertionLifetime,
			attributeNames, attributesEnabled, attributesNamespaceEnabled,
			enabled, encryptionForced, metadataUrl, metadataXmlInputStream,
			name, nameIdAttribute, nameIdFormat, serviceContext);
	}

	/**
	 * Updates the saml idp sp connection in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SamlIdpSpConnectionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param samlIdpSpConnection the saml idp sp connection
	 * @return the saml idp sp connection that was updated
	 */
	@Override
	public com.liferay.saml.persistence.model.SamlIdpSpConnection
		updateSamlIdpSpConnection(
			com.liferay.saml.persistence.model.SamlIdpSpConnection
				samlIdpSpConnection) {

		return _samlIdpSpConnectionLocalService.updateSamlIdpSpConnection(
			samlIdpSpConnection);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _samlIdpSpConnectionLocalService.getBasePersistence();
	}

	@Override
	public SamlIdpSpConnectionLocalService getWrappedService() {
		return _samlIdpSpConnectionLocalService;
	}

	@Override
	public void setWrappedService(
		SamlIdpSpConnectionLocalService samlIdpSpConnectionLocalService) {

		_samlIdpSpConnectionLocalService = samlIdpSpConnectionLocalService;
	}

	private SamlIdpSpConnectionLocalService _samlIdpSpConnectionLocalService;

}