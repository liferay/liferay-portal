/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ratings.kernel.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.ratings.kernel.model.RatingsEntry;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

/**
 * Provides the local service utility for RatingsEntry. This utility wraps
 * <code>com.liferay.portlet.ratings.service.impl.RatingsEntryLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see RatingsEntryLocalService
 * @generated
 */
public class RatingsEntryLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portlet.ratings.service.impl.RatingsEntryLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the ratings entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect RatingsEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ratingsEntry the ratings entry
	 * @return the ratings entry that was added
	 */
	public static RatingsEntry addRatingsEntry(RatingsEntry ratingsEntry) {
		return getService().addRatingsEntry(ratingsEntry);
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
	 * Creates a new ratings entry with the primary key. Does not add the ratings entry to the database.
	 *
	 * @param entryId the primary key for the new ratings entry
	 * @return the new ratings entry
	 */
	public static RatingsEntry createRatingsEntry(long entryId) {
		return getService().createRatingsEntry(entryId);
	}

	public static void deleteEntry(long userId, String className, long classPK)
		throws PortalException {

		getService().deleteEntry(userId, className, classPK);
	}

	public static void deleteEntry(
			RatingsEntry entry, long userId, String className, long classPK)
		throws PortalException {

		getService().deleteEntry(entry, userId, className, classPK);
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
	 * Deletes the ratings entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect RatingsEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param entryId the primary key of the ratings entry
	 * @return the ratings entry that was removed
	 * @throws PortalException if a ratings entry with the primary key could not be found
	 */
	public static RatingsEntry deleteRatingsEntry(long entryId)
		throws PortalException {

		return getService().deleteRatingsEntry(entryId);
	}

	/**
	 * Deletes the ratings entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect RatingsEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ratingsEntry the ratings entry
	 * @return the ratings entry that was removed
	 */
	public static RatingsEntry deleteRatingsEntry(RatingsEntry ratingsEntry) {
		return getService().deleteRatingsEntry(ratingsEntry);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.ratings.model.impl.RatingsEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.ratings.model.impl.RatingsEntryModelImpl</code>.
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

	public static RatingsEntry fetchEntry(
		long userId, String className, long classPK) {

		return getService().fetchEntry(userId, className, classPK);
	}

	public static RatingsEntry fetchRatingsEntry(long entryId) {
		return getService().fetchRatingsEntry(entryId);
	}

	/**
	 * Returns the ratings entry with the matching UUID and company.
	 *
	 * @param uuid the ratings entry's UUID
	 * @param companyId the primary key of the company
	 * @return the matching ratings entry, or <code>null</code> if a matching ratings entry could not be found
	 */
	public static RatingsEntry fetchRatingsEntryByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().fetchRatingsEntryByUuidAndCompanyId(
			uuid, companyId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static Map<Long, RatingsEntry> getEntries(
		long userId, String className, long[] classPKs) {

		return getService().getEntries(userId, className, classPKs);
	}

	public static List<RatingsEntry> getEntries(
		String className, long classPK) {

		return getService().getEntries(className, classPK);
	}

	public static RatingsEntry getEntry(
			long userId, String className, long classPK)
		throws PortalException {

		return getService().getEntry(userId, className, classPK);
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
	 * Returns a range of all the ratings entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.ratings.model.impl.RatingsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of ratings entries
	 * @param end the upper bound of the range of ratings entries (not inclusive)
	 * @return the range of ratings entries
	 */
	public static List<RatingsEntry> getRatingsEntries(int start, int end) {
		return getService().getRatingsEntries(start, end);
	}

	/**
	 * Returns the number of ratings entries.
	 *
	 * @return the number of ratings entries
	 */
	public static int getRatingsEntriesCount() {
		return getService().getRatingsEntriesCount();
	}

	/**
	 * Returns the ratings entry with the primary key.
	 *
	 * @param entryId the primary key of the ratings entry
	 * @return the ratings entry
	 * @throws PortalException if a ratings entry with the primary key could not be found
	 */
	public static RatingsEntry getRatingsEntry(long entryId)
		throws PortalException {

		return getService().getRatingsEntry(entryId);
	}

	/**
	 * Returns the ratings entry with the matching UUID and company.
	 *
	 * @param uuid the ratings entry's UUID
	 * @param companyId the primary key of the company
	 * @return the matching ratings entry
	 * @throws PortalException if a matching ratings entry could not be found
	 */
	public static RatingsEntry getRatingsEntryByUuidAndCompanyId(
			String uuid, long companyId)
		throws PortalException {

		return getService().getRatingsEntryByUuidAndCompanyId(uuid, companyId);
	}

	public static RatingsEntry updateEntry(
			long userId, String className, long classPK, double score,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateEntry(
			userId, className, classPK, score, serviceContext);
	}

	/**
	 * Updates the ratings entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect RatingsEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ratingsEntry the ratings entry
	 * @return the ratings entry that was updated
	 */
	public static RatingsEntry updateRatingsEntry(RatingsEntry ratingsEntry) {
		return getService().updateRatingsEntry(ratingsEntry);
	}

	public static RatingsEntryLocalService getService() {
		return _service;
	}

	public static void setService(RatingsEntryLocalService service) {
		_service = service;
	}

	private static volatile RatingsEntryLocalService _service;

}
// LIFERAY-SERVICE-BUILDER-HASH:587787003