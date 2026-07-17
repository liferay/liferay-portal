/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.segments.model.SegmentsEntry;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

/**
 * Provides the local service utility for SegmentsEntry. This utility wraps
 * <code>com.liferay.segments.service.impl.SegmentsEntryLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Eduardo Garcia
 * @see SegmentsEntryLocalService
 * @generated
 */
public class SegmentsEntryLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.segments.service.impl.SegmentsEntryLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the segments entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SegmentsEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param segmentsEntry the segments entry
	 * @return the segments entry that was added
	 */
	public static SegmentsEntry addSegmentsEntry(SegmentsEntry segmentsEntry) {
		return getService().addSegmentsEntry(segmentsEntry);
	}

	public static SegmentsEntry addSegmentsEntry(
			String segmentsEntryKey, Map<java.util.Locale, String> nameMap,
			Map<java.util.Locale, String> descriptionMap, boolean active,
			String criteria,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addSegmentsEntry(
			segmentsEntryKey, nameMap, descriptionMap, active, criteria,
			serviceContext);
	}

	public static SegmentsEntry addSegmentsEntry(
			String segmentsEntryKey, Map<java.util.Locale, String> nameMap,
			Map<java.util.Locale, String> descriptionMap, boolean active,
			String criteria, String source,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addSegmentsEntry(
			segmentsEntryKey, nameMap, descriptionMap, active, criteria, source,
			serviceContext);
	}

	public static void addSegmentsEntryClassPKs(
			long segmentsEntryId, long[] classPKs,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		getService().addSegmentsEntryClassPKs(
			segmentsEntryId, classPKs, serviceContext);
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
	 * Creates a new segments entry with the primary key. Does not add the segments entry to the database.
	 *
	 * @param segmentsEntryId the primary key for the new segments entry
	 * @return the new segments entry
	 */
	public static SegmentsEntry createSegmentsEntry(long segmentsEntryId) {
		return getService().createSegmentsEntry(segmentsEntryId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	public static void deleteSegmentsEntries(long groupId)
		throws PortalException {

		getService().deleteSegmentsEntries(groupId);
	}

	public static void deleteSegmentsEntries(String source)
		throws PortalException {

		getService().deleteSegmentsEntries(source);
	}

	/**
	 * Deletes the segments entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SegmentsEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param segmentsEntryId the primary key of the segments entry
	 * @return the segments entry that was removed
	 * @throws PortalException if a segments entry with the primary key could not be found
	 */
	public static SegmentsEntry deleteSegmentsEntry(long segmentsEntryId)
		throws PortalException {

		return getService().deleteSegmentsEntry(segmentsEntryId);
	}

	/**
	 * Deletes the segments entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SegmentsEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param segmentsEntry the segments entry
	 * @return the segments entry that was removed
	 * @throws PortalException
	 */
	public static SegmentsEntry deleteSegmentsEntry(SegmentsEntry segmentsEntry)
		throws PortalException {

		return getService().deleteSegmentsEntry(segmentsEntry);
	}

	public static void deleteSegmentsEntryClassPKs(
			long segmentsEntryId, long[] classPKs)
		throws PortalException {

		getService().deleteSegmentsEntryClassPKs(segmentsEntryId, classPKs);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsEntryModelImpl</code>.
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

	public static SegmentsEntry fetchSegmentsEntry(long segmentsEntryId) {
		return getService().fetchSegmentsEntry(segmentsEntryId);
	}

	public static SegmentsEntry fetchSegmentsEntry(
		long groupId, String segmentsEntryKey) {

		return getService().fetchSegmentsEntry(groupId, segmentsEntryKey);
	}

	public static SegmentsEntry fetchSegmentsEntryByExternalReferenceCode(
		String externalReferenceCode, long groupId) {

		return getService().fetchSegmentsEntryByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	/**
	 * Returns the segments entry matching the UUID and group.
	 *
	 * @param uuid the segments entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching segments entry, or <code>null</code> if a matching segments entry could not be found
	 */
	public static SegmentsEntry fetchSegmentsEntryByUuidAndGroupId(
		String uuid, long groupId) {

		return getService().fetchSegmentsEntryByUuidAndGroupId(uuid, groupId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
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
	 * Returns a range of all the segments entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of segments entries
	 * @param end the upper bound of the range of segments entries (not inclusive)
	 * @return the range of segments entries
	 */
	public static List<SegmentsEntry> getSegmentsEntries(int start, int end) {
		return getService().getSegmentsEntries(start, end);
	}

	public static List<SegmentsEntry> getSegmentsEntries(
		long groupId, int start, int end,
		OrderByComparator<SegmentsEntry> orderByComparator) {

		return getService().getSegmentsEntries(
			groupId, start, end, orderByComparator);
	}

	public static List<SegmentsEntry> getSegmentsEntries(
		long groupId, String[] sources, int start, int end,
		OrderByComparator<SegmentsEntry> orderByComparator) {

		return getService().getSegmentsEntries(
			groupId, sources, start, end, orderByComparator);
	}

	public static List<SegmentsEntry> getSegmentsEntries(
		long[] groupIds, boolean active, String[] sources) {

		return getService().getSegmentsEntries(groupIds, active, sources);
	}

	public static List<SegmentsEntry> getSegmentsEntries(
		long[] segmentsEntryIds, int start, int end) {

		return getService().getSegmentsEntries(segmentsEntryIds, start, end);
	}

	public static List<SegmentsEntry> getSegmentsEntriesBySource(
		long companyId, String source, int start, int end,
		OrderByComparator<SegmentsEntry> orderByComparator) {

		return getService().getSegmentsEntriesBySource(
			companyId, source, start, end, orderByComparator);
	}

	public static List<SegmentsEntry> getSegmentsEntriesBySource(
		String source, int start, int end,
		OrderByComparator<SegmentsEntry> orderByComparator) {

		return getService().getSegmentsEntriesBySource(
			source, start, end, orderByComparator);
	}

	/**
	 * Returns all the segments entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the segments entries
	 * @param companyId the primary key of the company
	 * @return the matching segments entries, or an empty list if no matches were found
	 */
	public static List<SegmentsEntry> getSegmentsEntriesByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().getSegmentsEntriesByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of segments entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the segments entries
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of segments entries
	 * @param end the upper bound of the range of segments entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching segments entries, or an empty list if no matches were found
	 */
	public static List<SegmentsEntry> getSegmentsEntriesByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<SegmentsEntry> orderByComparator) {

		return getService().getSegmentsEntriesByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of segments entries.
	 *
	 * @return the number of segments entries
	 */
	public static int getSegmentsEntriesCount() {
		return getService().getSegmentsEntriesCount();
	}

	public static int getSegmentsEntriesCount(long groupId) {
		return getService().getSegmentsEntriesCount(groupId);
	}

	public static int getSegmentsEntriesCount(long groupId, String[] sources) {
		return getService().getSegmentsEntriesCount(groupId, sources);
	}

	/**
	 * Returns the segments entry with the primary key.
	 *
	 * @param segmentsEntryId the primary key of the segments entry
	 * @return the segments entry
	 * @throws PortalException if a segments entry with the primary key could not be found
	 */
	public static SegmentsEntry getSegmentsEntry(long segmentsEntryId)
		throws PortalException {

		return getService().getSegmentsEntry(segmentsEntryId);
	}

	public static SegmentsEntry getSegmentsEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		return getService().getSegmentsEntryByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	/**
	 * Returns the segments entry matching the UUID and group.
	 *
	 * @param uuid the segments entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching segments entry
	 * @throws PortalException if a matching segments entry could not be found
	 */
	public static SegmentsEntry getSegmentsEntryByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException {

		return getService().getSegmentsEntryByUuidAndGroupId(uuid, groupId);
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<SegmentsEntry> searchSegmentsEntries(
				long companyId, long groupId, String keywords,
				java.util.LinkedHashMap<String, Object> params, int start,
				int end, com.liferay.portal.kernel.search.Sort sort)
			throws PortalException {

		return getService().searchSegmentsEntries(
			companyId, groupId, keywords, params, start, end, sort);
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<SegmentsEntry> searchSegmentsEntries(
				com.liferay.portal.kernel.search.SearchContext searchContext)
			throws PortalException {

		return getService().searchSegmentsEntries(searchContext);
	}

	public static SegmentsEntry updateSegmentsEntry(
			long segmentsEntryId, String segmentsEntryKey,
			Map<java.util.Locale, String> nameMap,
			Map<java.util.Locale, String> descriptionMap, boolean active,
			String criteria,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateSegmentsEntry(
			segmentsEntryId, segmentsEntryKey, nameMap, descriptionMap, active,
			criteria, serviceContext);
	}

	/**
	 * Updates the segments entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SegmentsEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param segmentsEntry the segments entry
	 * @return the segments entry that was updated
	 */
	public static SegmentsEntry updateSegmentsEntry(
		SegmentsEntry segmentsEntry) {

		return getService().updateSegmentsEntry(segmentsEntry);
	}

	public static SegmentsEntryLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<SegmentsEntryLocalService> _serviceSnapshot =
		new Snapshot<>(
			SegmentsEntryLocalServiceUtil.class,
			SegmentsEntryLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-1759649487