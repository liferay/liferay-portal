/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.type.grouped.service;

import com.liferay.commerce.product.type.grouped.model.CPDefinitionGroupedEntry;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for CPDefinitionGroupedEntry. This utility wraps
 * <code>com.liferay.commerce.product.type.grouped.service.impl.CPDefinitionGroupedEntryLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Andrea Di Giorgi
 * @see CPDefinitionGroupedEntryLocalService
 * @generated
 */
public class CPDefinitionGroupedEntryLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.product.type.grouped.service.impl.CPDefinitionGroupedEntryLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static void addCPDefinitionGroupedEntries(
			long cpDefinitionId, long[] entryCProductIds,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		getService().addCPDefinitionGroupedEntries(
			cpDefinitionId, entryCProductIds, serviceContext);
	}

	/**
	 * Adds the cp definition grouped entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPDefinitionGroupedEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpDefinitionGroupedEntry the cp definition grouped entry
	 * @return the cp definition grouped entry that was added
	 */
	public static CPDefinitionGroupedEntry addCPDefinitionGroupedEntry(
		CPDefinitionGroupedEntry cpDefinitionGroupedEntry) {

		return getService().addCPDefinitionGroupedEntry(
			cpDefinitionGroupedEntry);
	}

	public static CPDefinitionGroupedEntry addCPDefinitionGroupedEntry(
			long cpDefinitionId, long entryCProductId, double priority,
			int quantity,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCPDefinitionGroupedEntry(
			cpDefinitionId, entryCProductId, priority, quantity,
			serviceContext);
	}

	public static void cloneCPDefinitionGroupedEntries(
		long cpDefinitionId, long newCPDefinitionId) {

		getService().cloneCPDefinitionGroupedEntries(
			cpDefinitionId, newCPDefinitionId);
	}

	/**
	 * Creates a new cp definition grouped entry with the primary key. Does not add the cp definition grouped entry to the database.
	 *
	 * @param CPDefinitionGroupedEntryId the primary key for the new cp definition grouped entry
	 * @return the new cp definition grouped entry
	 */
	public static CPDefinitionGroupedEntry createCPDefinitionGroupedEntry(
		long CPDefinitionGroupedEntryId) {

		return getService().createCPDefinitionGroupedEntry(
			CPDefinitionGroupedEntryId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	public static void deleteCPDefinitionGroupedEntries(long cpDefinitionId) {
		getService().deleteCPDefinitionGroupedEntries(cpDefinitionId);
	}

	public static void deleteCPDefinitionGroupedEntriesByEntryCProductId(
		long entryCProductId) {

		getService().deleteCPDefinitionGroupedEntriesByEntryCProductId(
			entryCProductId);
	}

	/**
	 * Deletes the cp definition grouped entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPDefinitionGroupedEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpDefinitionGroupedEntry the cp definition grouped entry
	 * @return the cp definition grouped entry that was removed
	 */
	public static CPDefinitionGroupedEntry deleteCPDefinitionGroupedEntry(
		CPDefinitionGroupedEntry cpDefinitionGroupedEntry) {

		return getService().deleteCPDefinitionGroupedEntry(
			cpDefinitionGroupedEntry);
	}

	/**
	 * Deletes the cp definition grouped entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPDefinitionGroupedEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param CPDefinitionGroupedEntryId the primary key of the cp definition grouped entry
	 * @return the cp definition grouped entry that was removed
	 * @throws PortalException if a cp definition grouped entry with the primary key could not be found
	 */
	public static CPDefinitionGroupedEntry deleteCPDefinitionGroupedEntry(
			long CPDefinitionGroupedEntryId)
		throws PortalException {

		return getService().deleteCPDefinitionGroupedEntry(
			CPDefinitionGroupedEntryId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.type.grouped.model.impl.CPDefinitionGroupedEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.type.grouped.model.impl.CPDefinitionGroupedEntryModelImpl</code>.
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

	public static CPDefinitionGroupedEntry fetchCPDefinitionGroupedEntry(
		long CPDefinitionGroupedEntryId) {

		return getService().fetchCPDefinitionGroupedEntry(
			CPDefinitionGroupedEntryId);
	}

	public static CPDefinitionGroupedEntry fetchCPDefinitionGroupedEntry(
		long cpDefinitionId, long entryCProductId) {

		return getService().fetchCPDefinitionGroupedEntry(
			cpDefinitionId, entryCProductId);
	}

	/**
	 * Returns the cp definition grouped entry matching the UUID and group.
	 *
	 * @param uuid the cp definition grouped entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching cp definition grouped entry, or <code>null</code> if a matching cp definition grouped entry could not be found
	 */
	public static CPDefinitionGroupedEntry
		fetchCPDefinitionGroupedEntryByUuidAndGroupId(
			String uuid, long groupId) {

		return getService().fetchCPDefinitionGroupedEntryByUuidAndGroupId(
			uuid, groupId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns a range of all the cp definition grouped entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.type.grouped.model.impl.CPDefinitionGroupedEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cp definition grouped entries
	 * @param end the upper bound of the range of cp definition grouped entries (not inclusive)
	 * @return the range of cp definition grouped entries
	 */
	public static List<CPDefinitionGroupedEntry> getCPDefinitionGroupedEntries(
		int start, int end) {

		return getService().getCPDefinitionGroupedEntries(start, end);
	}

	public static List<CPDefinitionGroupedEntry> getCPDefinitionGroupedEntries(
		long cpDefinitionId) {

		return getService().getCPDefinitionGroupedEntries(cpDefinitionId);
	}

	public static List<CPDefinitionGroupedEntry> getCPDefinitionGroupedEntries(
		long cpDefinitionId, int start, int end,
		OrderByComparator<CPDefinitionGroupedEntry> orderByComparator) {

		return getService().getCPDefinitionGroupedEntries(
			cpDefinitionId, start, end, orderByComparator);
	}

	public static List<CPDefinitionGroupedEntry> getCPDefinitionGroupedEntries(
			long companyId, long cpDefinitionId, String keywords, int start,
			int end, com.liferay.portal.kernel.search.Sort sort)
		throws PortalException {

		return getService().getCPDefinitionGroupedEntries(
			companyId, cpDefinitionId, keywords, start, end, sort);
	}

	/**
	 * Returns all the cp definition grouped entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the cp definition grouped entries
	 * @param companyId the primary key of the company
	 * @return the matching cp definition grouped entries, or an empty list if no matches were found
	 */
	public static List<CPDefinitionGroupedEntry>
		getCPDefinitionGroupedEntriesByUuidAndCompanyId(
			String uuid, long companyId) {

		return getService().getCPDefinitionGroupedEntriesByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of cp definition grouped entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the cp definition grouped entries
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of cp definition grouped entries
	 * @param end the upper bound of the range of cp definition grouped entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching cp definition grouped entries, or an empty list if no matches were found
	 */
	public static List<CPDefinitionGroupedEntry>
		getCPDefinitionGroupedEntriesByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			OrderByComparator<CPDefinitionGroupedEntry> orderByComparator) {

		return getService().getCPDefinitionGroupedEntriesByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of cp definition grouped entries.
	 *
	 * @return the number of cp definition grouped entries
	 */
	public static int getCPDefinitionGroupedEntriesCount() {
		return getService().getCPDefinitionGroupedEntriesCount();
	}

	public static int getCPDefinitionGroupedEntriesCount(
			long companyId, long cpDefinitionId, String keywords)
		throws PortalException {

		return getService().getCPDefinitionGroupedEntriesCount(
			companyId, cpDefinitionId, keywords);
	}

	/**
	 * Returns the cp definition grouped entry with the primary key.
	 *
	 * @param CPDefinitionGroupedEntryId the primary key of the cp definition grouped entry
	 * @return the cp definition grouped entry
	 * @throws PortalException if a cp definition grouped entry with the primary key could not be found
	 */
	public static CPDefinitionGroupedEntry getCPDefinitionGroupedEntry(
			long CPDefinitionGroupedEntryId)
		throws PortalException {

		return getService().getCPDefinitionGroupedEntry(
			CPDefinitionGroupedEntryId);
	}

	/**
	 * Returns the cp definition grouped entry matching the UUID and group.
	 *
	 * @param uuid the cp definition grouped entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching cp definition grouped entry
	 * @throws PortalException if a matching cp definition grouped entry could not be found
	 */
	public static CPDefinitionGroupedEntry
			getCPDefinitionGroupedEntryByUuidAndGroupId(
				String uuid, long groupId)
		throws PortalException {

		return getService().getCPDefinitionGroupedEntryByUuidAndGroupId(
			uuid, groupId);
	}

	public static List<CPDefinitionGroupedEntry>
			getEntryCProductCPDefinitionGroupedEntries(
				long entryCProductId, int start, int end,
				OrderByComparator<CPDefinitionGroupedEntry> orderByComparator)
		throws PortalException {

		return getService().getEntryCProductCPDefinitionGroupedEntries(
			entryCProductId, start, end, orderByComparator);
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

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<CPDefinitionGroupedEntry> searchCPDefinitionGroupedEntries(
				com.liferay.portal.kernel.search.SearchContext searchContext)
			throws PortalException {

		return getService().searchCPDefinitionGroupedEntries(searchContext);
	}

	public static int searchCPDefinitionGroupedEntriesCount(
			com.liferay.portal.kernel.search.SearchContext searchContext)
		throws PortalException {

		return getService().searchCPDefinitionGroupedEntriesCount(
			searchContext);
	}

	/**
	 * Updates the cp definition grouped entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPDefinitionGroupedEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpDefinitionGroupedEntry the cp definition grouped entry
	 * @return the cp definition grouped entry that was updated
	 */
	public static CPDefinitionGroupedEntry updateCPDefinitionGroupedEntry(
		CPDefinitionGroupedEntry cpDefinitionGroupedEntry) {

		return getService().updateCPDefinitionGroupedEntry(
			cpDefinitionGroupedEntry);
	}

	public static CPDefinitionGroupedEntry updateCPDefinitionGroupedEntry(
			long cpDefinitionGroupedEntryId, double priority, int quantity)
		throws PortalException {

		return getService().updateCPDefinitionGroupedEntry(
			cpDefinitionGroupedEntryId, priority, quantity);
	}

	public static CPDefinitionGroupedEntryLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CPDefinitionGroupedEntryLocalService>
		_serviceSnapshot = new Snapshot<>(
			CPDefinitionGroupedEntryLocalServiceUtil.class,
			CPDefinitionGroupedEntryLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-1006030590