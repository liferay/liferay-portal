/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.redirect.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.redirect.model.RedirectNotFoundEntry;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for RedirectNotFoundEntry. This utility wraps
 * <code>com.liferay.redirect.service.impl.RedirectNotFoundEntryLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see RedirectNotFoundEntryLocalService
 * @generated
 */
public class RedirectNotFoundEntryLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.redirect.service.impl.RedirectNotFoundEntryLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static RedirectNotFoundEntry addOrUpdateRedirectNotFoundEntry(
		com.liferay.portal.kernel.model.Group group, String url) {

		return getService().addOrUpdateRedirectNotFoundEntry(group, url);
	}

	/**
	 * Adds the redirect not found entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect RedirectNotFoundEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param redirectNotFoundEntry the redirect not found entry
	 * @return the redirect not found entry that was added
	 */
	public static RedirectNotFoundEntry addRedirectNotFoundEntry(
		RedirectNotFoundEntry redirectNotFoundEntry) {

		return getService().addRedirectNotFoundEntry(redirectNotFoundEntry);
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
	 * Creates a new redirect not found entry with the primary key. Does not add the redirect not found entry to the database.
	 *
	 * @param redirectNotFoundEntryId the primary key for the new redirect not found entry
	 * @return the new redirect not found entry
	 */
	public static RedirectNotFoundEntry createRedirectNotFoundEntry(
		long redirectNotFoundEntryId) {

		return getService().createRedirectNotFoundEntry(
			redirectNotFoundEntryId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	public static void deleteRedirectNotFoundEntries(long groupId)
		throws PortalException {

		getService().deleteRedirectNotFoundEntries(groupId);
	}

	/**
	 * Deletes the redirect not found entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect RedirectNotFoundEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param redirectNotFoundEntryId the primary key of the redirect not found entry
	 * @return the redirect not found entry that was removed
	 * @throws PortalException if a redirect not found entry with the primary key could not be found
	 */
	public static RedirectNotFoundEntry deleteRedirectNotFoundEntry(
			long redirectNotFoundEntryId)
		throws PortalException {

		return getService().deleteRedirectNotFoundEntry(
			redirectNotFoundEntryId);
	}

	/**
	 * Deletes the redirect not found entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect RedirectNotFoundEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param redirectNotFoundEntry the redirect not found entry
	 * @return the redirect not found entry that was removed
	 * @throws PortalException
	 */
	public static RedirectNotFoundEntry deleteRedirectNotFoundEntry(
			RedirectNotFoundEntry redirectNotFoundEntry)
		throws PortalException {

		return getService().deleteRedirectNotFoundEntry(redirectNotFoundEntry);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.redirect.model.impl.RedirectNotFoundEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.redirect.model.impl.RedirectNotFoundEntryModelImpl</code>.
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

	public static RedirectNotFoundEntry fetchRedirectNotFoundEntry(
		long redirectNotFoundEntryId) {

		return getService().fetchRedirectNotFoundEntry(redirectNotFoundEntryId);
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
	 * Returns a range of all the redirect not found entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.redirect.model.impl.RedirectNotFoundEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of redirect not found entries
	 * @param end the upper bound of the range of redirect not found entries (not inclusive)
	 * @return the range of redirect not found entries
	 */
	public static List<RedirectNotFoundEntry> getRedirectNotFoundEntries(
		int start, int end) {

		return getService().getRedirectNotFoundEntries(start, end);
	}

	public static List<RedirectNotFoundEntry> getRedirectNotFoundEntries(
		long groupId, Boolean ignored, java.util.Date minModifiedDate,
		int start, int end,
		OrderByComparator<RedirectNotFoundEntry> orderByComparator) {

		return getService().getRedirectNotFoundEntries(
			groupId, ignored, minModifiedDate, start, end, orderByComparator);
	}

	public static List<RedirectNotFoundEntry> getRedirectNotFoundEntries(
		long groupId, java.util.Date minModifiedDate, int start, int end,
		OrderByComparator<RedirectNotFoundEntry> orderByComparator) {

		return getService().getRedirectNotFoundEntries(
			groupId, minModifiedDate, start, end, orderByComparator);
	}

	/**
	 * Returns the number of redirect not found entries.
	 *
	 * @return the number of redirect not found entries
	 */
	public static int getRedirectNotFoundEntriesCount() {
		return getService().getRedirectNotFoundEntriesCount();
	}

	public static int getRedirectNotFoundEntriesCount(
		long groupId, Boolean ignored, java.util.Date minModifiedDate) {

		return getService().getRedirectNotFoundEntriesCount(
			groupId, ignored, minModifiedDate);
	}

	public static int getRedirectNotFoundEntriesCount(
		long groupId, java.util.Date minModifiedDate) {

		return getService().getRedirectNotFoundEntriesCount(
			groupId, minModifiedDate);
	}

	/**
	 * Returns the redirect not found entry with the primary key.
	 *
	 * @param redirectNotFoundEntryId the primary key of the redirect not found entry
	 * @return the redirect not found entry
	 * @throws PortalException if a redirect not found entry with the primary key could not be found
	 */
	public static RedirectNotFoundEntry getRedirectNotFoundEntry(
			long redirectNotFoundEntryId)
		throws PortalException {

		return getService().getRedirectNotFoundEntry(redirectNotFoundEntryId);
	}

	public static RedirectNotFoundEntry updateRedirectNotFoundEntry(
			long redirectNotFoundEntryId, boolean ignored)
		throws PortalException {

		return getService().updateRedirectNotFoundEntry(
			redirectNotFoundEntryId, ignored);
	}

	/**
	 * Updates the redirect not found entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect RedirectNotFoundEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param redirectNotFoundEntry the redirect not found entry
	 * @return the redirect not found entry that was updated
	 */
	public static RedirectNotFoundEntry updateRedirectNotFoundEntry(
		RedirectNotFoundEntry redirectNotFoundEntry) {

		return getService().updateRedirectNotFoundEntry(redirectNotFoundEntry);
	}

	public static RedirectNotFoundEntryLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<RedirectNotFoundEntryLocalService>
		_serviceSnapshot = new Snapshot<>(
			RedirectNotFoundEntryLocalServiceUtil.class,
			RedirectNotFoundEntryLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-1227992874