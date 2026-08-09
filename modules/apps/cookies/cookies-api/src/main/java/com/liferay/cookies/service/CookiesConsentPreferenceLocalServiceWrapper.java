/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.cookies.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link CookiesConsentPreferenceLocalService}.
 *
 * @author Christopher Kian
 * @see CookiesConsentPreferenceLocalService
 * @generated
 */
public class CookiesConsentPreferenceLocalServiceWrapper
	implements CookiesConsentPreferenceLocalService,
			   ServiceWrapper<CookiesConsentPreferenceLocalService> {

	public CookiesConsentPreferenceLocalServiceWrapper() {
		this(null);
	}

	public CookiesConsentPreferenceLocalServiceWrapper(
		CookiesConsentPreferenceLocalService
			cookiesConsentPreferenceLocalService) {

		_cookiesConsentPreferenceLocalService =
			cookiesConsentPreferenceLocalService;
	}

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
	@Override
	public com.liferay.cookies.model.CookiesConsentPreference
		addCookiesConsentPreference(
			com.liferay.cookies.model.CookiesConsentPreference
				cookiesConsentPreference) {

		return _cookiesConsentPreferenceLocalService.
			addCookiesConsentPreference(cookiesConsentPreference);
	}

	@Override
	public com.liferay.cookies.model.CookiesConsentPreference
			addCookiesConsentPreference(
				long userId, String domain, java.util.Date expirationDate,
				String name, String value)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cookiesConsentPreferenceLocalService.
			addCookiesConsentPreference(
				userId, domain, expirationDate, name, value);
	}

	/**
	 * Creates a new cookies consent preference with the primary key. Does not add the cookies consent preference to the database.
	 *
	 * @param cookiesConsentPreferenceId the primary key for the new cookies consent preference
	 * @return the new cookies consent preference
	 */
	@Override
	public com.liferay.cookies.model.CookiesConsentPreference
		createCookiesConsentPreference(long cookiesConsentPreferenceId) {

		return _cookiesConsentPreferenceLocalService.
			createCookiesConsentPreference(cookiesConsentPreferenceId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cookiesConsentPreferenceLocalService.createPersistedModel(
			primaryKeyObj);
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
	@Override
	public com.liferay.cookies.model.CookiesConsentPreference
		deleteCookiesConsentPreference(
			com.liferay.cookies.model.CookiesConsentPreference
				cookiesConsentPreference) {

		return _cookiesConsentPreferenceLocalService.
			deleteCookiesConsentPreference(cookiesConsentPreference);
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
	@Override
	public com.liferay.cookies.model.CookiesConsentPreference
			deleteCookiesConsentPreference(long cookiesConsentPreferenceId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cookiesConsentPreferenceLocalService.
			deleteCookiesConsentPreference(cookiesConsentPreferenceId);
	}

	@Override
	public void deleteCookiesConsentPreference(
			long userId, String domain, String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		_cookiesConsentPreferenceLocalService.deleteCookiesConsentPreference(
			userId, domain, name);
	}

	@Override
	public void deleteCookiesConsentPreferences(long userId) {
		_cookiesConsentPreferenceLocalService.deleteCookiesConsentPreferences(
			userId);
	}

	@Override
	public void deleteCookiesConsentPreferences(long userId, String domain) {
		_cookiesConsentPreferenceLocalService.deleteCookiesConsentPreferences(
			userId, domain);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cookiesConsentPreferenceLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _cookiesConsentPreferenceLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _cookiesConsentPreferenceLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _cookiesConsentPreferenceLocalService.dynamicQuery();
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

		return _cookiesConsentPreferenceLocalService.dynamicQuery(dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _cookiesConsentPreferenceLocalService.dynamicQuery(
			dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _cookiesConsentPreferenceLocalService.dynamicQuery(
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

		return _cookiesConsentPreferenceLocalService.dynamicQueryCount(
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

		return _cookiesConsentPreferenceLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.cookies.model.CookiesConsentPreference
		fetchCookiesConsentPreference(long cookiesConsentPreferenceId) {

		return _cookiesConsentPreferenceLocalService.
			fetchCookiesConsentPreference(cookiesConsentPreferenceId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _cookiesConsentPreferenceLocalService.
			getActionableDynamicQuery();
	}

	/**
	 * Returns the cookies consent preference with the primary key.
	 *
	 * @param cookiesConsentPreferenceId the primary key of the cookies consent preference
	 * @return the cookies consent preference
	 * @throws PortalException if a cookies consent preference with the primary key could not be found
	 */
	@Override
	public com.liferay.cookies.model.CookiesConsentPreference
			getCookiesConsentPreference(long cookiesConsentPreferenceId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cookiesConsentPreferenceLocalService.
			getCookiesConsentPreference(cookiesConsentPreferenceId);
	}

	@Override
	public com.liferay.cookies.model.CookiesConsentPreference
			getCookiesConsentPreference(long userId, String domain, String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cookiesConsentPreferenceLocalService.
			getCookiesConsentPreference(userId, domain, name);
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
	@Override
	public java.util.List<com.liferay.cookies.model.CookiesConsentPreference>
		getCookiesConsentPreferences(int start, int end) {

		return _cookiesConsentPreferenceLocalService.
			getCookiesConsentPreferences(start, end);
	}

	@Override
	public java.util.List<com.liferay.cookies.model.CookiesConsentPreference>
		getCookiesConsentPreferences(long userId, String domain) {

		return _cookiesConsentPreferenceLocalService.
			getCookiesConsentPreferences(userId, domain);
	}

	/**
	 * Returns the number of cookies consent preferences.
	 *
	 * @return the number of cookies consent preferences
	 */
	@Override
	public int getCookiesConsentPreferencesCount() {
		return _cookiesConsentPreferenceLocalService.
			getCookiesConsentPreferencesCount();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _cookiesConsentPreferenceLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _cookiesConsentPreferenceLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cookiesConsentPreferenceLocalService.getPersistedModel(
			primaryKeyObj);
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
	@Override
	public com.liferay.cookies.model.CookiesConsentPreference
		updateCookiesConsentPreference(
			com.liferay.cookies.model.CookiesConsentPreference
				cookiesConsentPreference) {

		return _cookiesConsentPreferenceLocalService.
			updateCookiesConsentPreference(cookiesConsentPreference);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _cookiesConsentPreferenceLocalService.getBasePersistence();
	}

	@Override
	public CookiesConsentPreferenceLocalService getWrappedService() {
		return _cookiesConsentPreferenceLocalService;
	}

	@Override
	public void setWrappedService(
		CookiesConsentPreferenceLocalService
			cookiesConsentPreferenceLocalService) {

		_cookiesConsentPreferenceLocalService =
			cookiesConsentPreferenceLocalService;
	}

	private CookiesConsentPreferenceLocalService
		_cookiesConsentPreferenceLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-2076735159