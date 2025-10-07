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
import com.liferay.saml.persistence.model.SamlIdpSsoSession;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for SamlIdpSsoSession. This utility wraps
 * <code>com.liferay.saml.persistence.service.impl.SamlIdpSsoSessionLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Mika Koivisto
 * @see SamlIdpSsoSessionLocalService
 * @generated
 */
public class SamlIdpSsoSessionLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.saml.persistence.service.impl.SamlIdpSsoSessionLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the saml idp sso session to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SamlIdpSsoSessionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param samlIdpSsoSession the saml idp sso session
	 * @return the saml idp sso session that was added
	 */
	public static SamlIdpSsoSession addSamlIdpSsoSession(
		SamlIdpSsoSession samlIdpSsoSession) {

		return getService().addSamlIdpSsoSession(samlIdpSsoSession);
	}

	public static SamlIdpSsoSession addSamlIdpSsoSession(
			String samlIdpSsoSessionKey,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addSamlIdpSsoSession(
			samlIdpSsoSessionKey, serviceContext);
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
	 * Creates a new saml idp sso session with the primary key. Does not add the saml idp sso session to the database.
	 *
	 * @param samlIdpSsoSessionId the primary key for the new saml idp sso session
	 * @return the new saml idp sso session
	 */
	public static SamlIdpSsoSession createSamlIdpSsoSession(
		long samlIdpSsoSessionId) {

		return getService().createSamlIdpSsoSession(samlIdpSsoSessionId);
	}

	public static void deleteExpiredSamlIdpSsoSessions() {
		getService().deleteExpiredSamlIdpSsoSessions();
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
	 * Deletes the saml idp sso session with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SamlIdpSsoSessionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param samlIdpSsoSessionId the primary key of the saml idp sso session
	 * @return the saml idp sso session that was removed
	 * @throws PortalException if a saml idp sso session with the primary key could not be found
	 */
	public static SamlIdpSsoSession deleteSamlIdpSsoSession(
			long samlIdpSsoSessionId)
		throws PortalException {

		return getService().deleteSamlIdpSsoSession(samlIdpSsoSessionId);
	}

	/**
	 * Deletes the saml idp sso session from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SamlIdpSsoSessionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param samlIdpSsoSession the saml idp sso session
	 * @return the saml idp sso session that was removed
	 */
	public static SamlIdpSsoSession deleteSamlIdpSsoSession(
		SamlIdpSsoSession samlIdpSsoSession) {

		return getService().deleteSamlIdpSsoSession(samlIdpSsoSession);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.saml.persistence.model.impl.SamlIdpSsoSessionModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.saml.persistence.model.impl.SamlIdpSsoSessionModelImpl</code>.
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

	public static SamlIdpSsoSession fetchSamlIdpSso(
		String samlIdpSsoSessionKey) {

		return getService().fetchSamlIdpSso(samlIdpSsoSessionKey);
	}

	public static SamlIdpSsoSession fetchSamlIdpSsoByUserId(long userId) {
		return getService().fetchSamlIdpSsoByUserId(userId);
	}

	public static SamlIdpSsoSession fetchSamlIdpSsoSession(
		long samlIdpSsoSessionId) {

		return getService().fetchSamlIdpSsoSession(samlIdpSsoSessionId);
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

	public static SamlIdpSsoSession getSamlIdpSso(String samlIdpSsoSessionKey)
		throws PortalException {

		return getService().getSamlIdpSso(samlIdpSsoSessionKey);
	}

	/**
	 * Returns the saml idp sso session with the primary key.
	 *
	 * @param samlIdpSsoSessionId the primary key of the saml idp sso session
	 * @return the saml idp sso session
	 * @throws PortalException if a saml idp sso session with the primary key could not be found
	 */
	public static SamlIdpSsoSession getSamlIdpSsoSession(
			long samlIdpSsoSessionId)
		throws PortalException {

		return getService().getSamlIdpSsoSession(samlIdpSsoSessionId);
	}

	/**
	 * Returns a range of all the saml idp sso sessions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.saml.persistence.model.impl.SamlIdpSsoSessionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of saml idp sso sessions
	 * @param end the upper bound of the range of saml idp sso sessions (not inclusive)
	 * @return the range of saml idp sso sessions
	 */
	public static List<SamlIdpSsoSession> getSamlIdpSsoSessions(
		int start, int end) {

		return getService().getSamlIdpSsoSessions(start, end);
	}

	/**
	 * Returns the number of saml idp sso sessions.
	 *
	 * @return the number of saml idp sso sessions
	 */
	public static int getSamlIdpSsoSessionsCount() {
		return getService().getSamlIdpSsoSessionsCount();
	}

	public static SamlIdpSsoSession updateModifiedDate(
			String samlIdpSsoSessionKey)
		throws PortalException {

		return getService().updateModifiedDate(samlIdpSsoSessionKey);
	}

	/**
	 * Updates the saml idp sso session in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SamlIdpSsoSessionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param samlIdpSsoSession the saml idp sso session
	 * @return the saml idp sso session that was updated
	 */
	public static SamlIdpSsoSession updateSamlIdpSsoSession(
		SamlIdpSsoSession samlIdpSsoSession) {

		return getService().updateSamlIdpSsoSession(samlIdpSsoSession);
	}

	public static SamlIdpSsoSessionLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<SamlIdpSsoSessionLocalService>
		_serviceSnapshot = new Snapshot<>(
			SamlIdpSsoSessionLocalServiceUtil.class,
			SamlIdpSsoSessionLocalService.class);

}