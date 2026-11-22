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
import com.liferay.saml.persistence.model.SamlSpAuthRequest;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for SamlSpAuthRequest. This utility wraps
 * <code>com.liferay.saml.persistence.service.impl.SamlSpAuthRequestLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Mika Koivisto
 * @see SamlSpAuthRequestLocalService
 * @generated
 */
public class SamlSpAuthRequestLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.saml.persistence.service.impl.SamlSpAuthRequestLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the saml sp auth request to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SamlSpAuthRequestLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param samlSpAuthRequest the saml sp auth request
	 * @return the saml sp auth request that was added
	 */
	public static SamlSpAuthRequest addSamlSpAuthRequest(
		SamlSpAuthRequest samlSpAuthRequest) {

		return getService().addSamlSpAuthRequest(samlSpAuthRequest);
	}

	public static SamlSpAuthRequest addSamlSpAuthRequest(
		String samlIdpEntityId, String samlRelayState,
		String samlSpAuthRequestKey,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return getService().addSamlSpAuthRequest(
			samlIdpEntityId, samlRelayState, samlSpAuthRequestKey,
			serviceContext);
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
	 * Creates a new saml sp auth request with the primary key. Does not add the saml sp auth request to the database.
	 *
	 * @param samlSpAuthnRequestId the primary key for the new saml sp auth request
	 * @return the new saml sp auth request
	 */
	public static SamlSpAuthRequest createSamlSpAuthRequest(
		long samlSpAuthnRequestId) {

		return getService().createSamlSpAuthRequest(samlSpAuthnRequestId);
	}

	public static void deleteExpiredSamlSpAuthRequests() {
		getService().deleteExpiredSamlSpAuthRequests();
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
	 * Deletes the saml sp auth request with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SamlSpAuthRequestLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param samlSpAuthnRequestId the primary key of the saml sp auth request
	 * @return the saml sp auth request that was removed
	 * @throws PortalException if a saml sp auth request with the primary key could not be found
	 */
	public static SamlSpAuthRequest deleteSamlSpAuthRequest(
			long samlSpAuthnRequestId)
		throws PortalException {

		return getService().deleteSamlSpAuthRequest(samlSpAuthnRequestId);
	}

	/**
	 * Deletes the saml sp auth request from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SamlSpAuthRequestLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param samlSpAuthRequest the saml sp auth request
	 * @return the saml sp auth request that was removed
	 */
	public static SamlSpAuthRequest deleteSamlSpAuthRequest(
		SamlSpAuthRequest samlSpAuthRequest) {

		return getService().deleteSamlSpAuthRequest(samlSpAuthRequest);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.saml.persistence.model.impl.SamlSpAuthRequestModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.saml.persistence.model.impl.SamlSpAuthRequestModelImpl</code>.
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

	public static SamlSpAuthRequest fetchSamlSpAuthRequest(
		long samlSpAuthnRequestId) {

		return getService().fetchSamlSpAuthRequest(samlSpAuthnRequestId);
	}

	public static SamlSpAuthRequest fetchSamlSpAuthRequest(
		String samlIdpEntityId, String samlSpAuthRequestKey) {

		return getService().fetchSamlSpAuthRequest(
			samlIdpEntityId, samlSpAuthRequestKey);
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
	 * Returns the saml sp auth request with the primary key.
	 *
	 * @param samlSpAuthnRequestId the primary key of the saml sp auth request
	 * @return the saml sp auth request
	 * @throws PortalException if a saml sp auth request with the primary key could not be found
	 */
	public static SamlSpAuthRequest getSamlSpAuthRequest(
			long samlSpAuthnRequestId)
		throws PortalException {

		return getService().getSamlSpAuthRequest(samlSpAuthnRequestId);
	}

	/**
	 * Returns a range of all the saml sp auth requests.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.saml.persistence.model.impl.SamlSpAuthRequestModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of saml sp auth requests
	 * @param end the upper bound of the range of saml sp auth requests (not inclusive)
	 * @return the range of saml sp auth requests
	 */
	public static List<SamlSpAuthRequest> getSamlSpAuthRequests(
		int start, int end) {

		return getService().getSamlSpAuthRequests(start, end);
	}

	/**
	 * Returns the number of saml sp auth requests.
	 *
	 * @return the number of saml sp auth requests
	 */
	public static int getSamlSpAuthRequestsCount() {
		return getService().getSamlSpAuthRequestsCount();
	}

	/**
	 * Updates the saml sp auth request in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SamlSpAuthRequestLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param samlSpAuthRequest the saml sp auth request
	 * @return the saml sp auth request that was updated
	 */
	public static SamlSpAuthRequest updateSamlSpAuthRequest(
		SamlSpAuthRequest samlSpAuthRequest) {

		return getService().updateSamlSpAuthRequest(samlSpAuthRequest);
	}

	public static SamlSpAuthRequestLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<SamlSpAuthRequestLocalService>
		_serviceSnapshot = new Snapshot<>(
			SamlSpAuthRequestLocalServiceUtil.class,
			SamlSpAuthRequestLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-1541856422