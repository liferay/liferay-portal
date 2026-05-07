/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.persistence.service;

import com.liferay.oauth.client.persistence.model.OAuthClientPRLocalMetadata;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for OAuthClientPRLocalMetadata. This utility wraps
 * <code>com.liferay.oauth.client.persistence.service.impl.OAuthClientPRLocalMetadataLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see OAuthClientPRLocalMetadataLocalService
 * @generated
 */
public class OAuthClientPRLocalMetadataLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.oauth.client.persistence.service.impl.OAuthClientPRLocalMetadataLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static OAuthClientPRLocalMetadata addOAuthClientPRLocalMetadata(
			long userId, String metadataJSON)
		throws PortalException {

		return getService().addOAuthClientPRLocalMetadata(userId, metadataJSON);
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
	public static OAuthClientPRLocalMetadata addOAuthClientPRLocalMetadata(
		OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata) {

		return getService().addOAuthClientPRLocalMetadata(
			oAuthClientPRLocalMetadata);
	}

	public static OAuthClientPRLocalMetadata addOAuthClientPRLocalMetadata(
			String externalReferenceCode, long userId,
			String[] authorizationServers, String[] bearerMethodsSupported,
			boolean localWellKnownEnabled, String resource, String resourceName,
			String[] scopesSupported)
		throws PortalException {

		return getService().addOAuthClientPRLocalMetadata(
			externalReferenceCode, userId, authorizationServers,
			bearerMethodsSupported, localWellKnownEnabled, resource,
			resourceName, scopesSupported);
	}

	/**
	 * Creates a new o auth client pr local metadata with the primary key. Does not add the o auth client pr local metadata to the database.
	 *
	 * @param oAuthClientPRLocalMetadataId the primary key for the new o auth client pr local metadata
	 * @return the new o auth client pr local metadata
	 */
	public static OAuthClientPRLocalMetadata createOAuthClientPRLocalMetadata(
		long oAuthClientPRLocalMetadataId) {

		return getService().createOAuthClientPRLocalMetadata(
			oAuthClientPRLocalMetadataId);
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
	public static OAuthClientPRLocalMetadata deleteOAuthClientPRLocalMetadata(
			long oAuthClientPRLocalMetadataId)
		throws PortalException {

		return getService().deleteOAuthClientPRLocalMetadata(
			oAuthClientPRLocalMetadataId);
	}

	public static OAuthClientPRLocalMetadata deleteOAuthClientPRLocalMetadata(
			long companyId, String localWellKnownURI)
		throws PortalException {

		return getService().deleteOAuthClientPRLocalMetadata(
			companyId, localWellKnownURI);
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
	 * @throws PortalException
	 */
	public static OAuthClientPRLocalMetadata deleteOAuthClientPRLocalMetadata(
			OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata)
		throws PortalException {

		return getService().deleteOAuthClientPRLocalMetadata(
			oAuthClientPRLocalMetadata);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.oauth.client.persistence.model.impl.OAuthClientPRLocalMetadataModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.oauth.client.persistence.model.impl.OAuthClientPRLocalMetadataModelImpl</code>.
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

	public static OAuthClientPRLocalMetadata fetchOAuthClientPRLocalMetadata(
		long oAuthClientPRLocalMetadataId) {

		return getService().fetchOAuthClientPRLocalMetadata(
			oAuthClientPRLocalMetadataId);
	}

	public static OAuthClientPRLocalMetadata fetchOAuthClientPRLocalMetadata(
		long companyId, boolean localWellKnownEnabled,
		OrderByComparator<OAuthClientPRLocalMetadata> orderByComparator) {

		return getService().fetchOAuthClientPRLocalMetadata(
			companyId, localWellKnownEnabled, orderByComparator);
	}

	public static OAuthClientPRLocalMetadata fetchOAuthClientPRLocalMetadata(
		long companyId, String resource) {

		return getService().fetchOAuthClientPRLocalMetadata(
			companyId, resource);
	}

	public static OAuthClientPRLocalMetadata
		fetchOAuthClientPRLocalMetadataByExternalReferenceCode(
			String externalReferenceCode, long companyId) {

		return getService().
			fetchOAuthClientPRLocalMetadataByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	public static OAuthClientPRLocalMetadata
		fetchOAuthClientPRLocalMetadataByLocalWellKnownURI(
			long companyId, String localWellKnownURI) {

		return getService().fetchOAuthClientPRLocalMetadataByLocalWellKnownURI(
			companyId, localWellKnownURI);
	}

	/**
	 * Returns the o auth client pr local metadata with the matching UUID and company.
	 *
	 * @param uuid the o auth client pr local metadata's UUID
	 * @param companyId the primary key of the company
	 * @return the matching o auth client pr local metadata, or <code>null</code> if a matching o auth client pr local metadata could not be found
	 */
	public static OAuthClientPRLocalMetadata
		fetchOAuthClientPRLocalMetadataByUuidAndCompanyId(
			String uuid, long companyId) {

		return getService().fetchOAuthClientPRLocalMetadataByUuidAndCompanyId(
			uuid, companyId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static List<OAuthClientPRLocalMetadata>
		getCompanyOAuthClientPRLocalMetadata(long companyId) {

		return getService().getCompanyOAuthClientPRLocalMetadata(companyId);
	}

	public static List<OAuthClientPRLocalMetadata>
		getCompanyOAuthClientPRLocalMetadata(
			long companyId, int start, int end) {

		return getService().getCompanyOAuthClientPRLocalMetadata(
			companyId, start, end);
	}

	public static com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return getService().getExportActionableDynamicQuery(portletDataContext);
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the o auth client pr local metadata with the primary key.
	 *
	 * @param oAuthClientPRLocalMetadataId the primary key of the o auth client pr local metadata
	 * @return the o auth client pr local metadata
	 * @throws PortalException if a o auth client pr local metadata with the primary key could not be found
	 */
	public static OAuthClientPRLocalMetadata getOAuthClientPRLocalMetadata(
			long oAuthClientPRLocalMetadataId)
		throws PortalException {

		return getService().getOAuthClientPRLocalMetadata(
			oAuthClientPRLocalMetadataId);
	}

	public static OAuthClientPRLocalMetadata getOAuthClientPRLocalMetadata(
			long companyId, String localWellKnownURI)
		throws PortalException {

		return getService().getOAuthClientPRLocalMetadata(
			companyId, localWellKnownURI);
	}

	public static OAuthClientPRLocalMetadata
			getOAuthClientPRLocalMetadataByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().
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
	public static OAuthClientPRLocalMetadata
			getOAuthClientPRLocalMetadataByUuidAndCompanyId(
				String uuid, long companyId)
		throws PortalException {

		return getService().getOAuthClientPRLocalMetadataByUuidAndCompanyId(
			uuid, companyId);
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
	public static List<OAuthClientPRLocalMetadata>
		getOAuthClientPRLocalMetadatas(int start, int end) {

		return getService().getOAuthClientPRLocalMetadatas(start, end);
	}

	/**
	 * Returns the number of o auth client pr local metadatas.
	 *
	 * @return the number of o auth client pr local metadatas
	 */
	public static int getOAuthClientPRLocalMetadatasCount() {
		return getService().getOAuthClientPRLocalMetadatasCount();
	}

	public static int getOAuthClientPRLocalMetadatasCount(long companyId) {
		return getService().getOAuthClientPRLocalMetadatasCount(companyId);
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

	public static List<OAuthClientPRLocalMetadata>
		getUserOAuthClientPRLocalMetadata(long userId) {

		return getService().getUserOAuthClientPRLocalMetadata(userId);
	}

	public static List<OAuthClientPRLocalMetadata>
		getUserOAuthClientPRLocalMetadata(long userId, int start, int end) {

		return getService().getUserOAuthClientPRLocalMetadata(
			userId, start, end);
	}

	public static OAuthClientPRLocalMetadata updateOAuthClientPRLocalMetadata(
			long oAuthClientPRLocalMetadataId, String metadataJSON)
		throws PortalException {

		return getService().updateOAuthClientPRLocalMetadata(
			oAuthClientPRLocalMetadataId, metadataJSON);
	}

	public static OAuthClientPRLocalMetadata updateOAuthClientPRLocalMetadata(
			long oAuthClientPRLocalMetadataId, String[] authorizationServers,
			String[] bearerMethodsSupported, boolean localWellKnownEnabled,
			String resource, String resourceName, String[] scopesSupported)
		throws PortalException {

		return getService().updateOAuthClientPRLocalMetadata(
			oAuthClientPRLocalMetadataId, authorizationServers,
			bearerMethodsSupported, localWellKnownEnabled, resource,
			resourceName, scopesSupported);
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
	public static OAuthClientPRLocalMetadata updateOAuthClientPRLocalMetadata(
		OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata) {

		return getService().updateOAuthClientPRLocalMetadata(
			oAuthClientPRLocalMetadata);
	}

	public static OAuthClientPRLocalMetadataLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<OAuthClientPRLocalMetadataLocalService>
		_serviceSnapshot = new Snapshot<>(
			OAuthClientPRLocalMetadataLocalServiceUtil.class,
			OAuthClientPRLocalMetadataLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-1672194168