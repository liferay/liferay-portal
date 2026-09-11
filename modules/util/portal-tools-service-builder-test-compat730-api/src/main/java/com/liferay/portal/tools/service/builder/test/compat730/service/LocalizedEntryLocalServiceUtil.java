/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.compat730.service;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.tools.service.builder.test.compat730.model.LocalizedEntry;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

/**
 * Provides the local service utility for LocalizedEntry. This utility wraps
 * <code>com.liferay.portal.tools.service.builder.test.compat730.service.impl.LocalizedEntryLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see LocalizedEntryLocalService
 * @generated
 */
public class LocalizedEntryLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portal.tools.service.builder.test.compat730.service.impl.LocalizedEntryLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the localized entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LocalizedEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param localizedEntry the localized entry
	 * @return the localized entry that was added
	 */
	public static LocalizedEntry addLocalizedEntry(
		LocalizedEntry localizedEntry) {

		return getService().addLocalizedEntry(localizedEntry);
	}

	public static com.liferay.portal.tools.service.builder.test.compat730.model.
		LocalizedEntryLocalization addLocalizedEntryLocalization(
				LocalizedEntry localizedEntry, String languageId, String title,
				String content)
			throws PortalException {

		return getService().addLocalizedEntryLocalization(
			localizedEntry, languageId, title, content);
	}

	/**
	 * Creates a new localized entry with the primary key. Does not add the localized entry to the database.
	 *
	 * @param localizedEntryId the primary key for the new localized entry
	 * @return the new localized entry
	 */
	public static LocalizedEntry createLocalizedEntry(long localizedEntryId) {
		return getService().createLocalizedEntry(localizedEntryId);
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
	 * Deletes the localized entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LocalizedEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param localizedEntry the localized entry
	 * @return the localized entry that was removed
	 */
	public static LocalizedEntry deleteLocalizedEntry(
		LocalizedEntry localizedEntry) {

		return getService().deleteLocalizedEntry(localizedEntry);
	}

	/**
	 * Deletes the localized entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LocalizedEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param localizedEntryId the primary key of the localized entry
	 * @return the localized entry that was removed
	 * @throws PortalException if a localized entry with the primary key could not be found
	 */
	public static LocalizedEntry deleteLocalizedEntry(long localizedEntryId)
		throws PortalException {

		return getService().deleteLocalizedEntry(localizedEntryId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.compat730.model.impl.LocalizedEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.compat730.model.impl.LocalizedEntryModelImpl</code>.
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

	public static LocalizedEntry fetchLocalizedEntry(long localizedEntryId) {
		return getService().fetchLocalizedEntry(localizedEntryId);
	}

	public static com.liferay.portal.tools.service.builder.test.compat730.model.
		LocalizedEntryLocalization fetchLocalizedEntryLocalization(
			long localizedEntryId, String languageId) {

		return getService().fetchLocalizedEntryLocalization(
			localizedEntryId, languageId);
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
	 * Returns a range of all the localized entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.compat730.model.impl.LocalizedEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of localized entries
	 * @param end the upper bound of the range of localized entries (not inclusive)
	 * @return the range of localized entries
	 */
	public static List<LocalizedEntry> getLocalizedEntries(int start, int end) {
		return getService().getLocalizedEntries(start, end);
	}

	/**
	 * Returns the number of localized entries.
	 *
	 * @return the number of localized entries
	 */
	public static int getLocalizedEntriesCount() {
		return getService().getLocalizedEntriesCount();
	}

	/**
	 * Returns the localized entry with the primary key.
	 *
	 * @param localizedEntryId the primary key of the localized entry
	 * @return the localized entry
	 * @throws PortalException if a localized entry with the primary key could not be found
	 */
	public static LocalizedEntry getLocalizedEntry(long localizedEntryId)
		throws PortalException {

		return getService().getLocalizedEntry(localizedEntryId);
	}

	public static com.liferay.portal.tools.service.builder.test.compat730.model.
		LocalizedEntryLocalization getLocalizedEntryLocalization(
				long localizedEntryId, String languageId)
			throws PortalException {

		return getService().getLocalizedEntryLocalization(
			localizedEntryId, languageId);
	}

	public static List
		<com.liferay.portal.tools.service.builder.test.compat730.model.
			LocalizedEntryLocalization> getLocalizedEntryLocalizations(
				long localizedEntryId) {

		return getService().getLocalizedEntryLocalizations(localizedEntryId);
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
	 * Updates the localized entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LocalizedEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param localizedEntry the localized entry
	 * @return the localized entry that was updated
	 */
	public static LocalizedEntry updateLocalizedEntry(
		LocalizedEntry localizedEntry) {

		return getService().updateLocalizedEntry(localizedEntry);
	}

	public static com.liferay.portal.tools.service.builder.test.compat730.model.
		LocalizedEntryLocalization updateLocalizedEntryLocalization(
				LocalizedEntry localizedEntry, String languageId, String title,
				String content)
			throws PortalException {

		return getService().updateLocalizedEntryLocalization(
			localizedEntry, languageId, title, content);
	}

	public static List
		<com.liferay.portal.tools.service.builder.test.compat730.model.
			LocalizedEntryLocalization> updateLocalizedEntryLocalizations(
					LocalizedEntry localizedEntry, Map<String, String> titleMap,
					Map<String, String> contentMap)
				throws PortalException {

		return getService().updateLocalizedEntryLocalizations(
			localizedEntry, titleMap, contentMap);
	}

	public static LocalizedEntryLocalService getService() {
		return _service;
	}

	public static void setService(LocalizedEntryLocalService service) {
		_service = service;
	}

	private static volatile LocalizedEntryLocalService _service;

}
// LIFERAY-SERVICE-BUILDER-HASH:1400831888