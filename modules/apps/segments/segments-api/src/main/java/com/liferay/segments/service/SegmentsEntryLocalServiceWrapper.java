/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.service;

import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.segments.model.SegmentsEntry;

/**
 * Provides a wrapper for {@link SegmentsEntryLocalService}.
 *
 * @author Eduardo Garcia
 * @see SegmentsEntryLocalService
 * @generated
 */
public class SegmentsEntryLocalServiceWrapper
	implements SegmentsEntryLocalService,
			   ServiceWrapper<SegmentsEntryLocalService> {

	public SegmentsEntryLocalServiceWrapper() {
		this(null);
	}

	public SegmentsEntryLocalServiceWrapper(
		SegmentsEntryLocalService segmentsEntryLocalService) {

		_segmentsEntryLocalService = segmentsEntryLocalService;
	}

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
	@Override
	public SegmentsEntry addSegmentsEntry(SegmentsEntry segmentsEntry) {
		return _segmentsEntryLocalService.addSegmentsEntry(segmentsEntry);
	}

	@Override
	public SegmentsEntry addSegmentsEntry(
			String externalReferenceCode, String segmentsEntryKey,
			java.util.Map<java.util.Locale, String> nameMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			boolean active, String criteria, String source,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _segmentsEntryLocalService.addSegmentsEntry(
			externalReferenceCode, segmentsEntryKey, nameMap, descriptionMap,
			active, criteria, source, serviceContext);
	}

	@Override
	public void addSegmentsEntryClassPKs(
			long segmentsEntryId, long[] classPKs,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		_segmentsEntryLocalService.addSegmentsEntryClassPKs(
			segmentsEntryId, classPKs, serviceContext);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _segmentsEntryLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Creates a new segments entry with the primary key. Does not add the segments entry to the database.
	 *
	 * @param segmentsEntryId the primary key for the new segments entry
	 * @return the new segments entry
	 */
	@Override
	public SegmentsEntry createSegmentsEntry(long segmentsEntryId) {
		return _segmentsEntryLocalService.createSegmentsEntry(segmentsEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _segmentsEntryLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public void deleteSegmentsEntries(long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_segmentsEntryLocalService.deleteSegmentsEntries(groupId);
	}

	@Override
	public void deleteSegmentsEntries(String source)
		throws com.liferay.portal.kernel.exception.PortalException {

		_segmentsEntryLocalService.deleteSegmentsEntries(source);
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
	@Override
	public SegmentsEntry deleteSegmentsEntry(long segmentsEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _segmentsEntryLocalService.deleteSegmentsEntry(segmentsEntryId);
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
	@Override
	public SegmentsEntry deleteSegmentsEntry(SegmentsEntry segmentsEntry)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _segmentsEntryLocalService.deleteSegmentsEntry(segmentsEntry);
	}

	@Override
	public void deleteSegmentsEntryClassPKs(
			long segmentsEntryId, long[] classPKs)
		throws com.liferay.portal.kernel.exception.PortalException {

		_segmentsEntryLocalService.deleteSegmentsEntryClassPKs(
			segmentsEntryId, classPKs);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _segmentsEntryLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _segmentsEntryLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _segmentsEntryLocalService.dynamicQuery();
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

		return _segmentsEntryLocalService.dynamicQuery(dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _segmentsEntryLocalService.dynamicQuery(
			dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _segmentsEntryLocalService.dynamicQuery(
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

		return _segmentsEntryLocalService.dynamicQueryCount(dynamicQuery);
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

		return _segmentsEntryLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public SegmentsEntry fetchSegmentsEntry(long segmentsEntryId) {
		return _segmentsEntryLocalService.fetchSegmentsEntry(segmentsEntryId);
	}

	@Override
	public SegmentsEntry fetchSegmentsEntry(
		long groupId, String segmentsEntryKey) {

		return _segmentsEntryLocalService.fetchSegmentsEntry(
			groupId, segmentsEntryKey);
	}

	@Override
	public SegmentsEntry fetchSegmentsEntryByExternalReferenceCode(
		String externalReferenceCode, long groupId) {

		return _segmentsEntryLocalService.
			fetchSegmentsEntryByExternalReferenceCode(
				externalReferenceCode, groupId);
	}

	/**
	 * Returns the segments entry matching the UUID and group.
	 *
	 * @param uuid the segments entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching segments entry, or <code>null</code> if a matching segments entry could not be found
	 */
	@Override
	public SegmentsEntry fetchSegmentsEntryByUuidAndGroupId(
		String uuid, long groupId) {

		return _segmentsEntryLocalService.fetchSegmentsEntryByUuidAndGroupId(
			uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _segmentsEntryLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _segmentsEntryLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _segmentsEntryLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _segmentsEntryLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _segmentsEntryLocalService.getPersistedModel(primaryKeyObj);
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
	@Override
	public java.util.List<SegmentsEntry> getSegmentsEntries(
		int start, int end) {

		return _segmentsEntryLocalService.getSegmentsEntries(start, end);
	}

	@Override
	public java.util.List<SegmentsEntry> getSegmentsEntries(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SegmentsEntry>
			orderByComparator) {

		return _segmentsEntryLocalService.getSegmentsEntries(
			groupId, start, end, orderByComparator);
	}

	@Override
	public java.util.List<SegmentsEntry> getSegmentsEntries(
		long groupId, String[] sources, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SegmentsEntry>
			orderByComparator) {

		return _segmentsEntryLocalService.getSegmentsEntries(
			groupId, sources, start, end, orderByComparator);
	}

	@Override
	public java.util.List<SegmentsEntry> getSegmentsEntries(
		long[] groupIds, boolean active, String[] sources) {

		return _segmentsEntryLocalService.getSegmentsEntries(
			groupIds, active, sources);
	}

	@Override
	public java.util.List<SegmentsEntry> getSegmentsEntries(
		long[] segmentsEntryIds, int start, int end) {

		return _segmentsEntryLocalService.getSegmentsEntries(
			segmentsEntryIds, start, end);
	}

	@Override
	public java.util.List<SegmentsEntry> getSegmentsEntriesBySource(
		long companyId, String source, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SegmentsEntry>
			orderByComparator) {

		return _segmentsEntryLocalService.getSegmentsEntriesBySource(
			companyId, source, start, end, orderByComparator);
	}

	@Override
	public java.util.List<SegmentsEntry> getSegmentsEntriesBySource(
		String source, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SegmentsEntry>
			orderByComparator) {

		return _segmentsEntryLocalService.getSegmentsEntriesBySource(
			source, start, end, orderByComparator);
	}

	/**
	 * Returns all the segments entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the segments entries
	 * @param companyId the primary key of the company
	 * @return the matching segments entries, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<SegmentsEntry> getSegmentsEntriesByUuidAndCompanyId(
		String uuid, long companyId) {

		return _segmentsEntryLocalService.getSegmentsEntriesByUuidAndCompanyId(
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
	@Override
	public java.util.List<SegmentsEntry> getSegmentsEntriesByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SegmentsEntry>
			orderByComparator) {

		return _segmentsEntryLocalService.getSegmentsEntriesByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of segments entries.
	 *
	 * @return the number of segments entries
	 */
	@Override
	public int getSegmentsEntriesCount() {
		return _segmentsEntryLocalService.getSegmentsEntriesCount();
	}

	@Override
	public int getSegmentsEntriesCount(long groupId) {
		return _segmentsEntryLocalService.getSegmentsEntriesCount(groupId);
	}

	@Override
	public int getSegmentsEntriesCount(long groupId, String[] sources) {
		return _segmentsEntryLocalService.getSegmentsEntriesCount(
			groupId, sources);
	}

	/**
	 * Returns the segments entry with the primary key.
	 *
	 * @param segmentsEntryId the primary key of the segments entry
	 * @return the segments entry
	 * @throws PortalException if a segments entry with the primary key could not be found
	 */
	@Override
	public SegmentsEntry getSegmentsEntry(long segmentsEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _segmentsEntryLocalService.getSegmentsEntry(segmentsEntryId);
	}

	@Override
	public SegmentsEntry getSegmentsEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _segmentsEntryLocalService.
			getSegmentsEntryByExternalReferenceCode(
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
	@Override
	public SegmentsEntry getSegmentsEntryByUuidAndGroupId(
			String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _segmentsEntryLocalService.getSegmentsEntryByUuidAndGroupId(
			uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult<SegmentsEntry>
			searchSegmentsEntries(
				long companyId, long groupId, String keywords,
				java.util.LinkedHashMap<String, Object> params, int start,
				int end, com.liferay.portal.kernel.search.Sort sort)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _segmentsEntryLocalService.searchSegmentsEntries(
			companyId, groupId, keywords, params, start, end, sort);
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult<SegmentsEntry>
			searchSegmentsEntries(
				com.liferay.portal.kernel.search.SearchContext searchContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _segmentsEntryLocalService.searchSegmentsEntries(searchContext);
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
	@Override
	public SegmentsEntry updateSegmentsEntry(SegmentsEntry segmentsEntry) {
		return _segmentsEntryLocalService.updateSegmentsEntry(segmentsEntry);
	}

	@Override
	public SegmentsEntry updateSegmentsEntry(
			String externalReferenceCode, long segmentsEntryId,
			String segmentsEntryKey,
			java.util.Map<java.util.Locale, String> nameMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			boolean active, String criteria,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _segmentsEntryLocalService.updateSegmentsEntry(
			externalReferenceCode, segmentsEntryId, segmentsEntryKey, nameMap,
			descriptionMap, active, criteria, serviceContext);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _segmentsEntryLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<SegmentsEntry> getCTPersistence() {
		return _segmentsEntryLocalService.getCTPersistence();
	}

	@Override
	public Class<SegmentsEntry> getModelClass() {
		return _segmentsEntryLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<SegmentsEntry>, R, E>
				updateUnsafeFunction)
		throws E {

		return _segmentsEntryLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public SegmentsEntryLocalService getWrappedService() {
		return _segmentsEntryLocalService;
	}

	@Override
	public void setWrappedService(
		SegmentsEntryLocalService segmentsEntryLocalService) {

		_segmentsEntryLocalService = segmentsEntryLocalService;
	}

	private SegmentsEntryLocalService _segmentsEntryLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:701262167