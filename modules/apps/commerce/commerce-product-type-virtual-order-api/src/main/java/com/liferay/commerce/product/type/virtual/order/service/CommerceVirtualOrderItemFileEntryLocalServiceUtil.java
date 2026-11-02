/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.type.virtual.order.service;

import com.liferay.commerce.product.type.virtual.order.model.CommerceVirtualOrderItemFileEntry;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for CommerceVirtualOrderItemFileEntry. This utility wraps
 * <code>com.liferay.commerce.product.type.virtual.order.service.impl.CommerceVirtualOrderItemFileEntryLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Alessio Antonio Rendina
 * @see CommerceVirtualOrderItemFileEntryLocalService
 * @generated
 */
public class CommerceVirtualOrderItemFileEntryLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.product.type.virtual.order.service.impl.CommerceVirtualOrderItemFileEntryLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the commerce virtual order item file entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceVirtualOrderItemFileEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceVirtualOrderItemFileEntry the commerce virtual order item file entry
	 * @return the commerce virtual order item file entry that was added
	 */
	public static CommerceVirtualOrderItemFileEntry
		addCommerceVirtualOrderItemFileEntry(
			CommerceVirtualOrderItemFileEntry
				commerceVirtualOrderItemFileEntry) {

		return getService().addCommerceVirtualOrderItemFileEntry(
			commerceVirtualOrderItemFileEntry);
	}

	public static CommerceVirtualOrderItemFileEntry
			addCommerceVirtualOrderItemFileEntry(
				long userId, long groupId, long commerceOrderItemId,
				long fileEntryId, String url, int usages, String version)
		throws PortalException {

		return getService().addCommerceVirtualOrderItemFileEntry(
			userId, groupId, commerceOrderItemId, fileEntryId, url, usages,
			version);
	}

	/**
	 * Creates a new commerce virtual order item file entry with the primary key. Does not add the commerce virtual order item file entry to the database.
	 *
	 * @param commerceVirtualOrderItemFileEntryId the primary key for the new commerce virtual order item file entry
	 * @return the new commerce virtual order item file entry
	 */
	public static CommerceVirtualOrderItemFileEntry
		createCommerceVirtualOrderItemFileEntry(
			long commerceVirtualOrderItemFileEntryId) {

		return getService().createCommerceVirtualOrderItemFileEntry(
			commerceVirtualOrderItemFileEntryId);
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
	 * Deletes the commerce virtual order item file entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceVirtualOrderItemFileEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceVirtualOrderItemFileEntry the commerce virtual order item file entry
	 * @return the commerce virtual order item file entry that was removed
	 */
	public static CommerceVirtualOrderItemFileEntry
		deleteCommerceVirtualOrderItemFileEntry(
			CommerceVirtualOrderItemFileEntry
				commerceVirtualOrderItemFileEntry) {

		return getService().deleteCommerceVirtualOrderItemFileEntry(
			commerceVirtualOrderItemFileEntry);
	}

	/**
	 * Deletes the commerce virtual order item file entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceVirtualOrderItemFileEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceVirtualOrderItemFileEntryId the primary key of the commerce virtual order item file entry
	 * @return the commerce virtual order item file entry that was removed
	 * @throws PortalException if a commerce virtual order item file entry with the primary key could not be found
	 */
	public static CommerceVirtualOrderItemFileEntry
			deleteCommerceVirtualOrderItemFileEntry(
				long commerceVirtualOrderItemFileEntryId)
		throws PortalException {

		return getService().deleteCommerceVirtualOrderItemFileEntry(
			commerceVirtualOrderItemFileEntryId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.type.virtual.order.model.impl.CommerceVirtualOrderItemFileEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.type.virtual.order.model.impl.CommerceVirtualOrderItemFileEntryModelImpl</code>.
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

	public static CommerceVirtualOrderItemFileEntry
		fetchCommerceVirtualOrderItemFileEntry(
			long commerceVirtualOrderItemFileEntryId) {

		return getService().fetchCommerceVirtualOrderItemFileEntry(
			commerceVirtualOrderItemFileEntryId);
	}

	public static CommerceVirtualOrderItemFileEntry
		fetchCommerceVirtualOrderItemFileEntry(
			long commerceVirtualOrderItemId, long fileEntryId) {

		return getService().fetchCommerceVirtualOrderItemFileEntry(
			commerceVirtualOrderItemId, fileEntryId);
	}

	/**
	 * Returns the commerce virtual order item file entry matching the UUID and group.
	 *
	 * @param uuid the commerce virtual order item file entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching commerce virtual order item file entry, or <code>null</code> if a matching commerce virtual order item file entry could not be found
	 */
	public static CommerceVirtualOrderItemFileEntry
		fetchCommerceVirtualOrderItemFileEntryByUuidAndGroupId(
			String uuid, long groupId) {

		return getService().
			fetchCommerceVirtualOrderItemFileEntryByUuidAndGroupId(
				uuid, groupId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns a range of all the commerce virtual order item file entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.type.virtual.order.model.impl.CommerceVirtualOrderItemFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce virtual order item file entries
	 * @param end the upper bound of the range of commerce virtual order item file entries (not inclusive)
	 * @return the range of commerce virtual order item file entries
	 */
	public static List<CommerceVirtualOrderItemFileEntry>
		getCommerceVirtualOrderItemFileEntries(int start, int end) {

		return getService().getCommerceVirtualOrderItemFileEntries(start, end);
	}

	public static List<CommerceVirtualOrderItemFileEntry>
		getCommerceVirtualOrderItemFileEntries(
			long commerceVirtualOrderItemId) {

		return getService().getCommerceVirtualOrderItemFileEntries(
			commerceVirtualOrderItemId);
	}

	/**
	 * Returns all the commerce virtual order item file entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the commerce virtual order item file entries
	 * @param companyId the primary key of the company
	 * @return the matching commerce virtual order item file entries, or an empty list if no matches were found
	 */
	public static List<CommerceVirtualOrderItemFileEntry>
		getCommerceVirtualOrderItemFileEntriesByUuidAndCompanyId(
			String uuid, long companyId) {

		return getService().
			getCommerceVirtualOrderItemFileEntriesByUuidAndCompanyId(
				uuid, companyId);
	}

	/**
	 * Returns a range of commerce virtual order item file entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the commerce virtual order item file entries
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of commerce virtual order item file entries
	 * @param end the upper bound of the range of commerce virtual order item file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching commerce virtual order item file entries, or an empty list if no matches were found
	 */
	public static List<CommerceVirtualOrderItemFileEntry>
		getCommerceVirtualOrderItemFileEntriesByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			OrderByComparator<CommerceVirtualOrderItemFileEntry>
				orderByComparator) {

		return getService().
			getCommerceVirtualOrderItemFileEntriesByUuidAndCompanyId(
				uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of commerce virtual order item file entries.
	 *
	 * @return the number of commerce virtual order item file entries
	 */
	public static int getCommerceVirtualOrderItemFileEntriesCount() {
		return getService().getCommerceVirtualOrderItemFileEntriesCount();
	}

	public static int getCommerceVirtualOrderItemFileEntriesCount(
		long commerceVirtualOrderItemId) {

		return getService().getCommerceVirtualOrderItemFileEntriesCount(
			commerceVirtualOrderItemId);
	}

	/**
	 * Returns the commerce virtual order item file entry with the primary key.
	 *
	 * @param commerceVirtualOrderItemFileEntryId the primary key of the commerce virtual order item file entry
	 * @return the commerce virtual order item file entry
	 * @throws PortalException if a commerce virtual order item file entry with the primary key could not be found
	 */
	public static CommerceVirtualOrderItemFileEntry
			getCommerceVirtualOrderItemFileEntry(
				long commerceVirtualOrderItemFileEntryId)
		throws PortalException {

		return getService().getCommerceVirtualOrderItemFileEntry(
			commerceVirtualOrderItemFileEntryId);
	}

	/**
	 * Returns the commerce virtual order item file entry matching the UUID and group.
	 *
	 * @param uuid the commerce virtual order item file entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching commerce virtual order item file entry
	 * @throws PortalException if a matching commerce virtual order item file entry could not be found
	 */
	public static CommerceVirtualOrderItemFileEntry
			getCommerceVirtualOrderItemFileEntryByUuidAndGroupId(
				String uuid, long groupId)
		throws PortalException {

		return getService().
			getCommerceVirtualOrderItemFileEntryByUuidAndGroupId(uuid, groupId);
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

	public static CommerceVirtualOrderItemFileEntry incrementUsages(
			long commerceVirtualOrderItemFileEntryId)
		throws PortalException {

		return getService().incrementUsages(
			commerceVirtualOrderItemFileEntryId);
	}

	/**
	 * Updates the commerce virtual order item file entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceVirtualOrderItemFileEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceVirtualOrderItemFileEntry the commerce virtual order item file entry
	 * @return the commerce virtual order item file entry that was updated
	 */
	public static CommerceVirtualOrderItemFileEntry
		updateCommerceVirtualOrderItemFileEntry(
			CommerceVirtualOrderItemFileEntry
				commerceVirtualOrderItemFileEntry) {

		return getService().updateCommerceVirtualOrderItemFileEntry(
			commerceVirtualOrderItemFileEntry);
	}

	public static CommerceVirtualOrderItemFileEntry
			updateCommerceVirtualOrderItemFileEntry(
				long commerceVirtualOrderItemFileEntryId, long fileEntryId,
				String url, int usages, String version)
		throws PortalException {

		return getService().updateCommerceVirtualOrderItemFileEntry(
			commerceVirtualOrderItemFileEntryId, fileEntryId, url, usages,
			version);
	}

	public static CommerceVirtualOrderItemFileEntryLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CommerceVirtualOrderItemFileEntryLocalService>
		_serviceSnapshot = new Snapshot<>(
			CommerceVirtualOrderItemFileEntryLocalServiceUtil.class,
			CommerceVirtualOrderItemFileEntryLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:707018150