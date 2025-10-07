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
import com.liferay.saml.persistence.model.SamlSpIdpConnection;

import java.io.InputStream;
import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for SamlSpIdpConnection. This utility wraps
 * <code>com.liferay.saml.persistence.service.impl.SamlSpIdpConnectionLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Mika Koivisto
 * @see SamlSpIdpConnectionLocalService
 * @generated
 */
public class SamlSpIdpConnectionLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.saml.persistence.service.impl.SamlSpIdpConnectionLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the saml sp idp connection to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SamlSpIdpConnectionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param samlSpIdpConnection the saml sp idp connection
	 * @return the saml sp idp connection that was added
	 */
	public static SamlSpIdpConnection addSamlSpIdpConnection(
		SamlSpIdpConnection samlSpIdpConnection) {

		return getService().addSamlSpIdpConnection(samlSpIdpConnection);
	}

	public static SamlSpIdpConnection addSamlSpIdpConnection(
			String samlIdpEntityId, boolean assertionSignatureRequired,
			long clockSkew, boolean enabled, boolean forceAuthn,
			boolean ldapImportEnabled, String metadataUrl,
			InputStream metadataXmlInputStream, String name,
			String nameIdFormat, boolean signAuthnRequest,
			boolean unknownUsersAreStrangers, String userAttributeMappings,
			String userIdentifierExpression,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addSamlSpIdpConnection(
			samlIdpEntityId, assertionSignatureRequired, clockSkew, enabled,
			forceAuthn, ldapImportEnabled, metadataUrl, metadataXmlInputStream,
			name, nameIdFormat, signAuthnRequest, unknownUsersAreStrangers,
			userAttributeMappings, userIdentifierExpression, serviceContext);
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
	 * Creates a new saml sp idp connection with the primary key. Does not add the saml sp idp connection to the database.
	 *
	 * @param samlSpIdpConnectionId the primary key for the new saml sp idp connection
	 * @return the new saml sp idp connection
	 */
	public static SamlSpIdpConnection createSamlSpIdpConnection(
		long samlSpIdpConnectionId) {

		return getService().createSamlSpIdpConnection(samlSpIdpConnectionId);
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
	 * Deletes the saml sp idp connection with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SamlSpIdpConnectionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param samlSpIdpConnectionId the primary key of the saml sp idp connection
	 * @return the saml sp idp connection that was removed
	 * @throws PortalException if a saml sp idp connection with the primary key could not be found
	 */
	public static SamlSpIdpConnection deleteSamlSpIdpConnection(
			long samlSpIdpConnectionId)
		throws PortalException {

		return getService().deleteSamlSpIdpConnection(samlSpIdpConnectionId);
	}

	/**
	 * Deletes the saml sp idp connection from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SamlSpIdpConnectionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param samlSpIdpConnection the saml sp idp connection
	 * @return the saml sp idp connection that was removed
	 */
	public static SamlSpIdpConnection deleteSamlSpIdpConnection(
		SamlSpIdpConnection samlSpIdpConnection) {

		return getService().deleteSamlSpIdpConnection(samlSpIdpConnection);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.saml.persistence.model.impl.SamlSpIdpConnectionModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.saml.persistence.model.impl.SamlSpIdpConnectionModelImpl</code>.
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

	public static SamlSpIdpConnection fetchSamlSpIdpConnection(
		long samlSpIdpConnectionId) {

		return getService().fetchSamlSpIdpConnection(samlSpIdpConnectionId);
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
	 * Returns the saml sp idp connection with the primary key.
	 *
	 * @param samlSpIdpConnectionId the primary key of the saml sp idp connection
	 * @return the saml sp idp connection
	 * @throws PortalException if a saml sp idp connection with the primary key could not be found
	 */
	public static SamlSpIdpConnection getSamlSpIdpConnection(
			long samlSpIdpConnectionId)
		throws PortalException {

		return getService().getSamlSpIdpConnection(samlSpIdpConnectionId);
	}

	public static SamlSpIdpConnection getSamlSpIdpConnection(
			long companyId, String samlIdpEntityId)
		throws PortalException {

		return getService().getSamlSpIdpConnection(companyId, samlIdpEntityId);
	}

	/**
	 * Returns a range of all the saml sp idp connections.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.saml.persistence.model.impl.SamlSpIdpConnectionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of saml sp idp connections
	 * @param end the upper bound of the range of saml sp idp connections (not inclusive)
	 * @return the range of saml sp idp connections
	 */
	public static List<SamlSpIdpConnection> getSamlSpIdpConnections(
		int start, int end) {

		return getService().getSamlSpIdpConnections(start, end);
	}

	public static List<SamlSpIdpConnection> getSamlSpIdpConnections(
		long companyId) {

		return getService().getSamlSpIdpConnections(companyId);
	}

	public static List<SamlSpIdpConnection> getSamlSpIdpConnections(
		long companyId, int start, int end) {

		return getService().getSamlSpIdpConnections(companyId, start, end);
	}

	public static List<SamlSpIdpConnection> getSamlSpIdpConnections(
		long companyId, int start, int end,
		OrderByComparator<SamlSpIdpConnection> orderByComparator) {

		return getService().getSamlSpIdpConnections(
			companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of saml sp idp connections.
	 *
	 * @return the number of saml sp idp connections
	 */
	public static int getSamlSpIdpConnectionsCount() {
		return getService().getSamlSpIdpConnectionsCount();
	}

	public static int getSamlSpIdpConnectionsCount(long companyId) {
		return getService().getSamlSpIdpConnectionsCount(companyId);
	}

	public static void updateMetadata(long samlSpIdpConnectionId)
		throws PortalException {

		getService().updateMetadata(samlSpIdpConnectionId);
	}

	public static SamlSpIdpConnection updateSamlSpIdpConnection(
			long samlSpIdpConnectionId, String samlIdpEntityId,
			boolean assertionSignatureRequired, long clockSkew, boolean enabled,
			boolean forceAuthn, boolean ldapImportEnabled, String metadataUrl,
			InputStream metadataXmlInputStream, String name,
			String nameIdFormat, boolean signAuthnRequest,
			boolean unknownUsersAreStrangers, String userAttributeMappings,
			String userIdentifierExpression,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateSamlSpIdpConnection(
			samlSpIdpConnectionId, samlIdpEntityId, assertionSignatureRequired,
			clockSkew, enabled, forceAuthn, ldapImportEnabled, metadataUrl,
			metadataXmlInputStream, name, nameIdFormat, signAuthnRequest,
			unknownUsersAreStrangers, userAttributeMappings,
			userIdentifierExpression, serviceContext);
	}

	/**
	 * Updates the saml sp idp connection in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SamlSpIdpConnectionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param samlSpIdpConnection the saml sp idp connection
	 * @return the saml sp idp connection that was updated
	 */
	public static SamlSpIdpConnection updateSamlSpIdpConnection(
		SamlSpIdpConnection samlSpIdpConnection) {

		return getService().updateSamlSpIdpConnection(samlSpIdpConnection);
	}

	public static SamlSpIdpConnectionLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<SamlSpIdpConnectionLocalService>
		_serviceSnapshot = new Snapshot<>(
			SamlSpIdpConnectionLocalServiceUtil.class,
			SamlSpIdpConnectionLocalService.class);

}