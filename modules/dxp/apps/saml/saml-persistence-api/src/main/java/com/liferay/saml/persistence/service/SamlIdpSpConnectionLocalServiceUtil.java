/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.persistence.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.saml.persistence.model.SamlIdpSpConnection;

import java.io.InputStream;
import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for SamlIdpSpConnection. This utility wraps
 * <code>com.liferay.saml.persistence.service.impl.SamlIdpSpConnectionLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Mika Koivisto
 * @see SamlIdpSpConnectionLocalService
 * @generated
 */
public class SamlIdpSpConnectionLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.saml.persistence.service.impl.SamlIdpSpConnectionLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

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
	public static SamlIdpSpConnection addSamlIdpSpConnection(
		SamlIdpSpConnection samlIdpSpConnection) {

		return getService().addSamlIdpSpConnection(samlIdpSpConnection);
	}

	public static SamlIdpSpConnection addSamlIdpSpConnection(
			String samlSpEntityId, int assertionLifetime, String attributeNames,
			boolean attributesEnabled, boolean attributesNamespaceEnabled,
			boolean enabled, boolean encryptionForced, String metadataUrl,
			InputStream metadataXmlInputStream, String name,
			String nameIdAttribute, String nameIdFormat,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addSamlIdpSpConnection(
			samlSpEntityId, assertionLifetime, attributeNames,
			attributesEnabled, attributesNamespaceEnabled, enabled,
			encryptionForced, metadataUrl, metadataXmlInputStream, name,
			nameIdAttribute, nameIdFormat, serviceContext);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	/**
	 * Creates a new saml idp sp connection with the primary key. Does not add the saml idp sp connection to the database.
	 *
	 * @param samlIdpSpConnectionId the primary key for the new saml idp sp connection
	 * @return the new saml idp sp connection
	 */
	public static SamlIdpSpConnection createSamlIdpSpConnection(
		long samlIdpSpConnectionId) {

		return getService().createSamlIdpSpConnection(samlIdpSpConnectionId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
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
	public static SamlIdpSpConnection deleteSamlIdpSpConnection(
			long samlIdpSpConnectionId)
		throws PortalException {

		return getService().deleteSamlIdpSpConnection(samlIdpSpConnectionId);
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
	public static SamlIdpSpConnection deleteSamlIdpSpConnection(
		SamlIdpSpConnection samlIdpSpConnection) {

		return getService().deleteSamlIdpSpConnection(samlIdpSpConnection);
	}

	public static <T> T dslQuery(DSLQuery dslQuery) {
		return getService().dslQuery(dslQuery);
	}

	public static int dslQueryCount(DSLQuery dslQuery) {
		return getService().dslQueryCount(dslQuery);
	}

	public static DynamicQuery dynamicQuery() {
		return getService().dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	public static <T> List<T> dynamicQuery(DynamicQuery dynamicQuery) {
		return getService().dynamicQuery(dynamicQuery);
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
	public static <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getService().dynamicQuery(dynamicQuery, start, end);
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
	public static <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<T> orderByComparator) {

		return getService().dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(DynamicQuery dynamicQuery) {
		return getService().dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(
		DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return getService().dynamicQueryCount(dynamicQuery, projection);
	}

	public static SamlIdpSpConnection fetchSamlIdpSpConnection(
		long samlIdpSpConnectionId) {

		return getService().fetchSamlIdpSpConnection(samlIdpSpConnectionId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException {

		return getService().getPersistedModel(primaryKeyObj);
	}

	/**
	 * Returns the saml idp sp connection with the primary key.
	 *
	 * @param samlIdpSpConnectionId the primary key of the saml idp sp connection
	 * @return the saml idp sp connection
	 * @throws PortalException if a saml idp sp connection with the primary key could not be found
	 */
	public static SamlIdpSpConnection getSamlIdpSpConnection(
			long samlIdpSpConnectionId)
		throws PortalException {

		return getService().getSamlIdpSpConnection(samlIdpSpConnectionId);
	}

	public static SamlIdpSpConnection getSamlIdpSpConnection(
			long companyId, String samlSpEntityId)
		throws PortalException {

		return getService().getSamlIdpSpConnection(companyId, samlSpEntityId);
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
	public static List<SamlIdpSpConnection> getSamlIdpSpConnections(
		int start, int end) {

		return getService().getSamlIdpSpConnections(start, end);
	}

	public static List<SamlIdpSpConnection> getSamlIdpSpConnections(
		long companyId) {

		return getService().getSamlIdpSpConnections(companyId);
	}

	public static List<SamlIdpSpConnection> getSamlIdpSpConnections(
		long companyId, int start, int end) {

		return getService().getSamlIdpSpConnections(companyId, start, end);
	}

	public static List<SamlIdpSpConnection> getSamlIdpSpConnections(
		long companyId, int start, int end,
		OrderByComparator<SamlIdpSpConnection> orderByComparator) {

		return getService().getSamlIdpSpConnections(
			companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of saml idp sp connections.
	 *
	 * @return the number of saml idp sp connections
	 */
	public static int getSamlIdpSpConnectionsCount() {
		return getService().getSamlIdpSpConnectionsCount();
	}

	public static int getSamlIdpSpConnectionsCount(long companyId) {
		return getService().getSamlIdpSpConnectionsCount(companyId);
	}

	public static void updateMetadata(long samlIdpSpConnectionId)
		throws PortalException {

		getService().updateMetadata(samlIdpSpConnectionId);
	}

	public static SamlIdpSpConnection updateSamlIdpSpConnection(
			long samlIdpSpConnectionId, String samlSpEntityId,
			int assertionLifetime, String attributeNames,
			boolean attributesEnabled, boolean attributesNamespaceEnabled,
			boolean enabled, boolean encryptionForced, String metadataUrl,
			InputStream metadataXmlInputStream, String name,
			String nameIdAttribute, String nameIdFormat,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateSamlIdpSpConnection(
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
	public static SamlIdpSpConnection updateSamlIdpSpConnection(
		SamlIdpSpConnection samlIdpSpConnection) {

		return getService().updateSamlIdpSpConnection(samlIdpSpConnection);
	}

	public static SamlIdpSpConnectionLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<SamlIdpSpConnectionLocalService>
		_serviceSnapshot = new Snapshot<>(
			SamlIdpSpConnectionLocalServiceUtil.class,
			SamlIdpSpConnectionLocalService.class);

}