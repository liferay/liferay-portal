/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.tools.service.builder.test.model.FilterPrimaryEntry;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for FilterPrimaryEntry. This utility wraps
 * <code>com.liferay.portal.tools.service.builder.test.service.impl.FilterPrimaryEntryLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see FilterPrimaryEntryLocalService
 * @generated
 */
public class FilterPrimaryEntryLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portal.tools.service.builder.test.service.impl.FilterPrimaryEntryLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the filter primary entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect FilterPrimaryEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param filterPrimaryEntry the filter primary entry
	 * @return the filter primary entry that was added
	 */
	public static FilterPrimaryEntry addFilterPrimaryEntry(
		FilterPrimaryEntry filterPrimaryEntry) {

		return getService().addFilterPrimaryEntry(filterPrimaryEntry);
	}

	public static FilterPrimaryEntry addFilterPrimaryEntry(
			long companyId, long groupId, long resourcePrimKey, long userId)
		throws PortalException {

		return getService().addFilterPrimaryEntry(
			companyId, groupId, resourcePrimKey, userId);
	}

	/**
	 * Creates a new filter primary entry with the primary key. Does not add the filter primary entry to the database.
	 *
	 * @param filterPrimaryEntryId the primary key for the new filter primary entry
	 * @return the new filter primary entry
	 */
	public static FilterPrimaryEntry createFilterPrimaryEntry(
		long filterPrimaryEntryId) {

		return getService().createFilterPrimaryEntry(filterPrimaryEntryId);
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
	 * Deletes the filter primary entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect FilterPrimaryEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param filterPrimaryEntry the filter primary entry
	 * @return the filter primary entry that was removed
	 * @throws PortalException
	 */
	public static FilterPrimaryEntry deleteFilterPrimaryEntry(
			FilterPrimaryEntry filterPrimaryEntry)
		throws PortalException {

		return getService().deleteFilterPrimaryEntry(filterPrimaryEntry);
	}

	/**
	 * Deletes the filter primary entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect FilterPrimaryEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param filterPrimaryEntryId the primary key of the filter primary entry
	 * @return the filter primary entry that was removed
	 * @throws PortalException if a filter primary entry with the primary key could not be found
	 */
	public static FilterPrimaryEntry deleteFilterPrimaryEntry(
			long filterPrimaryEntryId)
		throws PortalException {

		return getService().deleteFilterPrimaryEntry(filterPrimaryEntryId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.FilterPrimaryEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.FilterPrimaryEntryModelImpl</code>.
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

	public static FilterPrimaryEntry fetchFilterPrimaryEntry(
		long filterPrimaryEntryId) {

		return getService().fetchFilterPrimaryEntry(filterPrimaryEntryId);
	}

	public static int filterCountByGroupId(long groupId) {
		return getService().filterCountByGroupId(groupId);
	}

	public static List<FilterPrimaryEntry> filterFindByGroupId(long groupId) {
		return getService().filterFindByGroupId(groupId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns a range of all the filter primary entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.FilterPrimaryEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of filter primary entries
	 * @param end the upper bound of the range of filter primary entries (not inclusive)
	 * @return the range of filter primary entries
	 */
	public static List<FilterPrimaryEntry> getFilterPrimaryEntries(
		int start, int end) {

		return getService().getFilterPrimaryEntries(start, end);
	}

	/**
	 * Returns the number of filter primary entries.
	 *
	 * @return the number of filter primary entries
	 */
	public static int getFilterPrimaryEntriesCount() {
		return getService().getFilterPrimaryEntriesCount();
	}

	/**
	 * Returns the filter primary entry with the primary key.
	 *
	 * @param filterPrimaryEntryId the primary key of the filter primary entry
	 * @return the filter primary entry
	 * @throws PortalException if a filter primary entry with the primary key could not be found
	 */
	public static FilterPrimaryEntry getFilterPrimaryEntry(
			long filterPrimaryEntryId)
		throws PortalException {

		return getService().getFilterPrimaryEntry(filterPrimaryEntryId);
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

	public static List<? extends PersistedModel> getPersistedModel(
			long resourcePrimKey)
		throws PortalException {

		return getService().getPersistedModel(resourcePrimKey);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException {

		return getService().getPersistedModel(primaryKeyObj);
	}

	/**
	 * Updates the filter primary entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect FilterPrimaryEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param filterPrimaryEntry the filter primary entry
	 * @return the filter primary entry that was updated
	 */
	public static FilterPrimaryEntry updateFilterPrimaryEntry(
		FilterPrimaryEntry filterPrimaryEntry) {

		return getService().updateFilterPrimaryEntry(filterPrimaryEntry);
	}

	public static FilterPrimaryEntryLocalService getService() {
		return _service;
	}

	public static void setService(FilterPrimaryEntryLocalService service) {
		_service = service;
	}

	private static volatile FilterPrimaryEntryLocalService _service;

}
// LIFERAY-SERVICE-BUILDER-HASH:1391553098