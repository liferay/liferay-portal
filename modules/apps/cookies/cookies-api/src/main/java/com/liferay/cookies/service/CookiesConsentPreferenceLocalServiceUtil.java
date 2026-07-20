/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.cookies.service;

import com.liferay.cookies.model.CookiesConsentPreference;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for CookiesConsentPreference. This utility wraps
 * <code>com.liferay.cookies.service.impl.CookiesConsentPreferenceLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Christopher Kian
 * @see CookiesConsentPreferenceLocalService
 * @generated
 */
public class CookiesConsentPreferenceLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.cookies.service.impl.CookiesConsentPreferenceLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the cookies consent preference to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CookiesConsentPreferenceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cookiesConsentPreference the cookies consent preference
	 * @return the cookies consent preference that was added
	 */
	public static CookiesConsentPreference addCookiesConsentPreference(
		CookiesConsentPreference cookiesConsentPreference) {

		return getService().addCookiesConsentPreference(
			cookiesConsentPreference);
	}

	public static CookiesConsentPreference addCookiesConsentPreference(
			long userId, String domain, java.util.Date expirationDate,
			String name, String value)
		throws PortalException {

		return getService().addCookiesConsentPreference(
			userId, domain, expirationDate, name, value);
	}

	/**
	 * Creates a new cookies consent preference with the primary key. Does not add the cookies consent preference to the database.
	 *
	 * @param cookiesConsentPreferenceId the primary key for the new cookies consent preference
	 * @return the new cookies consent preference
	 */
	public static CookiesConsentPreference createCookiesConsentPreference(
		long cookiesConsentPreferenceId) {

		return getService().createCookiesConsentPreference(
			cookiesConsentPreferenceId);
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
	 * Deletes the cookies consent preference from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CookiesConsentPreferenceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cookiesConsentPreference the cookies consent preference
	 * @return the cookies consent preference that was removed
	 */
	public static CookiesConsentPreference deleteCookiesConsentPreference(
		CookiesConsentPreference cookiesConsentPreference) {

		return getService().deleteCookiesConsentPreference(
			cookiesConsentPreference);
	}

	/**
	 * Deletes the cookies consent preference with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CookiesConsentPreferenceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cookiesConsentPreferenceId the primary key of the cookies consent preference
	 * @return the cookies consent preference that was removed
	 * @throws PortalException if a cookies consent preference with the primary key could not be found
	 */
	public static CookiesConsentPreference deleteCookiesConsentPreference(
			long cookiesConsentPreferenceId)
		throws PortalException {

		return getService().deleteCookiesConsentPreference(
			cookiesConsentPreferenceId);
	}

	public static void deleteCookiesConsentPreference(
			long userId, String domain, String name)
		throws PortalException {

		getService().deleteCookiesConsentPreference(userId, domain, name);
	}

	public static void deleteCookiesConsentPreferences(long userId) {
		getService().deleteCookiesConsentPreferences(userId);
	}

	public static void deleteCookiesConsentPreferences(
		long userId, String domain) {

		getService().deleteCookiesConsentPreferences(userId, domain);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.cookies.model.impl.CookiesConsentPreferenceModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.cookies.model.impl.CookiesConsentPreferenceModelImpl</code>.
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

	public static CookiesConsentPreference fetchCookiesConsentPreference(
		long cookiesConsentPreferenceId) {

		return getService().fetchCookiesConsentPreference(
			cookiesConsentPreferenceId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the cookies consent preference with the primary key.
	 *
	 * @param cookiesConsentPreferenceId the primary key of the cookies consent preference
	 * @return the cookies consent preference
	 * @throws PortalException if a cookies consent preference with the primary key could not be found
	 */
	public static CookiesConsentPreference getCookiesConsentPreference(
			long cookiesConsentPreferenceId)
		throws PortalException {

		return getService().getCookiesConsentPreference(
			cookiesConsentPreferenceId);
	}

	public static CookiesConsentPreference getCookiesConsentPreference(
			long userId, String domain, String name)
		throws PortalException {

		return getService().getCookiesConsentPreference(userId, domain, name);
	}

	/**
	 * Returns a range of all the cookies consent preferences.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.cookies.model.impl.CookiesConsentPreferenceModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cookies consent preferences
	 * @param end the upper bound of the range of cookies consent preferences (not inclusive)
	 * @return the range of cookies consent preferences
	 */
	public static List<CookiesConsentPreference> getCookiesConsentPreferences(
		int start, int end) {

		return getService().getCookiesConsentPreferences(start, end);
	}

	public static List<CookiesConsentPreference> getCookiesConsentPreferences(
		long userId, String domain) {

		return getService().getCookiesConsentPreferences(userId, domain);
	}

	/**
	 * Returns the number of cookies consent preferences.
	 *
	 * @return the number of cookies consent preferences
	 */
	public static int getCookiesConsentPreferencesCount() {
		return getService().getCookiesConsentPreferencesCount();
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
	 * Updates the cookies consent preference in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CookiesConsentPreferenceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cookiesConsentPreference the cookies consent preference
	 * @return the cookies consent preference that was updated
	 */
	public static CookiesConsentPreference updateCookiesConsentPreference(
		CookiesConsentPreference cookiesConsentPreference) {

		return getService().updateCookiesConsentPreference(
			cookiesConsentPreference);
	}

	public static CookiesConsentPreferenceLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CookiesConsentPreferenceLocalService>
		_serviceSnapshot = new Snapshot<>(
			CookiesConsentPreferenceLocalServiceUtil.class,
			CookiesConsentPreferenceLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-1988590670