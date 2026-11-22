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
import com.liferay.saml.persistence.model.SamlSpSession;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for SamlSpSession. This utility wraps
 * <code>com.liferay.saml.persistence.service.impl.SamlSpSessionLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Mika Koivisto
 * @see SamlSpSessionLocalService
 * @generated
 */
public class SamlSpSessionLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.saml.persistence.service.impl.SamlSpSessionLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the saml sp session to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SamlSpSessionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param samlSpSession the saml sp session
	 * @return the saml sp session that was added
	 */
	public static SamlSpSession addSamlSpSession(SamlSpSession samlSpSession) {
		return getService().addSamlSpSession(samlSpSession);
	}

	public static SamlSpSession addSamlSpSession(
			String assertionXml, String jSessionId, String nameIdFormat,
			String nameIdNameQualifier, String nameIdSPNameQualifier,
			String nameIdValue, String samlIdpEntityId, String samlSpSessionKey,
			String sessionIndex,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addSamlSpSession(
			assertionXml, jSessionId, nameIdFormat, nameIdNameQualifier,
			nameIdSPNameQualifier, nameIdValue, samlIdpEntityId,
			samlSpSessionKey, sessionIndex, serviceContext);
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
	 * Creates a new saml sp session with the primary key. Does not add the saml sp session to the database.
	 *
	 * @param samlSpSessionId the primary key for the new saml sp session
	 * @return the new saml sp session
	 */
	public static SamlSpSession createSamlSpSession(long samlSpSessionId) {
		return getService().createSamlSpSession(samlSpSessionId);
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
	 * Deletes the saml sp session with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SamlSpSessionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param samlSpSessionId the primary key of the saml sp session
	 * @return the saml sp session that was removed
	 * @throws PortalException if a saml sp session with the primary key could not be found
	 */
	public static SamlSpSession deleteSamlSpSession(long samlSpSessionId)
		throws PortalException {

		return getService().deleteSamlSpSession(samlSpSessionId);
	}

	/**
	 * Deletes the saml sp session from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SamlSpSessionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param samlSpSession the saml sp session
	 * @return the saml sp session that was removed
	 */
	public static SamlSpSession deleteSamlSpSession(
		SamlSpSession samlSpSession) {

		return getService().deleteSamlSpSession(samlSpSession);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.saml.persistence.model.impl.SamlSpSessionModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.saml.persistence.model.impl.SamlSpSessionModelImpl</code>.
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

	public static SamlSpSession fetchSamlSpSession(long samlSpSessionId) {
		return getService().fetchSamlSpSession(samlSpSessionId);
	}

	public static SamlSpSession fetchSamlSpSessionByJSessionId(
		String jSessionId) {

		return getService().fetchSamlSpSessionByJSessionId(jSessionId);
	}

	public static SamlSpSession fetchSamlSpSessionBySamlSpSessionKey(
		String samlSpSessionKey) {

		return getService().fetchSamlSpSessionBySamlSpSessionKey(
			samlSpSessionKey);
	}

	public static List<SamlSpSession> fetchSamlSpSessionsBySessionIndex(
		long companyId, String sessionIndex) {

		return getService().fetchSamlSpSessionsBySessionIndex(
			companyId, sessionIndex);
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
	 * Returns the saml sp session with the primary key.
	 *
	 * @param samlSpSessionId the primary key of the saml sp session
	 * @return the saml sp session
	 * @throws PortalException if a saml sp session with the primary key could not be found
	 */
	public static SamlSpSession getSamlSpSession(long samlSpSessionId)
		throws PortalException {

		return getService().getSamlSpSession(samlSpSessionId);
	}

	public static SamlSpSession getSamlSpSessionBySessionIndex(
			long companyId, String sessionIndex)
		throws PortalException {

		return getService().getSamlSpSessionBySessionIndex(
			companyId, sessionIndex);
	}

	/**
	 * Returns a range of all the saml sp sessions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.saml.persistence.model.impl.SamlSpSessionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of saml sp sessions
	 * @param end the upper bound of the range of saml sp sessions (not inclusive)
	 * @return the range of saml sp sessions
	 */
	public static List<SamlSpSession> getSamlSpSessions(int start, int end) {
		return getService().getSamlSpSessions(start, end);
	}

	public static List<SamlSpSession> getSamlSpSessions(
		long companyId, String nameIdFormat, String nameIdNameQualifier,
		String nameIdSPNameQualifier, String nameIdValue,
		String samlIdpEntityId) {

		return getService().getSamlSpSessions(
			companyId, nameIdFormat, nameIdNameQualifier, nameIdSPNameQualifier,
			nameIdValue, samlIdpEntityId);
	}

	/**
	 * Returns the number of saml sp sessions.
	 *
	 * @return the number of saml sp sessions
	 */
	public static int getSamlSpSessionsCount() {
		return getService().getSamlSpSessionsCount();
	}

	public static SamlSpSession updateSamlSpSession(
			long samlSpSessionId, String jSessionId)
		throws PortalException {

		return getService().updateSamlSpSession(samlSpSessionId, jSessionId);
	}

	public static SamlSpSession updateSamlSpSession(
			long samlSpSessionId, String assertionXml, String jSessionId,
			String nameIdFormat, String nameIdNameQualifier,
			String nameIdSPNameQualifier, String nameIdValue,
			String samlIdpEntityId, String samlSpSessionKey,
			String sessionIndex,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateSamlSpSession(
			samlSpSessionId, assertionXml, jSessionId, nameIdFormat,
			nameIdNameQualifier, nameIdSPNameQualifier, nameIdValue,
			samlIdpEntityId, samlSpSessionKey, sessionIndex, serviceContext);
	}

	/**
	 * Updates the saml sp session in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SamlSpSessionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param samlSpSession the saml sp session
	 * @return the saml sp session that was updated
	 */
	public static SamlSpSession updateSamlSpSession(
		SamlSpSession samlSpSession) {

		return getService().updateSamlSpSession(samlSpSession);
	}

	public static SamlSpSessionLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<SamlSpSessionLocalService> _serviceSnapshot =
		new Snapshot<>(
			SamlSpSessionLocalServiceUtil.class,
			SamlSpSessionLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:1913976465