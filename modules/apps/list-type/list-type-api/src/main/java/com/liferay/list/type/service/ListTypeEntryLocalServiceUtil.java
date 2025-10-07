/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.list.type.service;

import com.liferay.list.type.model.ListTypeEntry;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

/**
 * Provides the local service utility for ListTypeEntry. This utility wraps
 * <code>com.liferay.list.type.service.impl.ListTypeEntryLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Gabriel Albuquerque
 * @see ListTypeEntryLocalService
 * @generated
 */
public class ListTypeEntryLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.list.type.service.impl.ListTypeEntryLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the list type entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ListTypeEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param listTypeEntry the list type entry
	 * @return the list type entry that was added
	 */
	public static ListTypeEntry addListTypeEntry(ListTypeEntry listTypeEntry) {
		return getService().addListTypeEntry(listTypeEntry);
	}

	public static ListTypeEntry addListTypeEntry(
			String externalReferenceCode, long userId,
			long listTypeDefinitionId, String key,
			Map<java.util.Locale, String> nameMap, boolean system)
		throws PortalException {

		return getService().addListTypeEntry(
			externalReferenceCode, userId, listTypeDefinitionId, key, nameMap,
			system);
	}

	/**
	 * Creates a new list type entry with the primary key. Does not add the list type entry to the database.
	 *
	 * @param listTypeEntryId the primary key for the new list type entry
	 * @return the new list type entry
	 */
	public static ListTypeEntry createListTypeEntry(long listTypeEntryId) {
		return getService().createListTypeEntry(listTypeEntryId);
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
	 * Deletes the list type entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ListTypeEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param listTypeEntry the list type entry
	 * @return the list type entry that was removed
	 * @throws PortalException
	 */
	public static ListTypeEntry deleteListTypeEntry(ListTypeEntry listTypeEntry)
		throws PortalException {

		return getService().deleteListTypeEntry(listTypeEntry);
	}

	/**
	 * Deletes the list type entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ListTypeEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param listTypeEntryId the primary key of the list type entry
	 * @return the list type entry that was removed
	 * @throws PortalException if a list type entry with the primary key could not be found
	 */
	public static ListTypeEntry deleteListTypeEntry(long listTypeEntryId)
		throws PortalException {

		return getService().deleteListTypeEntry(listTypeEntryId);
	}

	public static void deleteListTypeEntryByListTypeDefinitionId(
			long listTypeDefinitionId)
		throws PortalException {

		getService().deleteListTypeEntryByListTypeDefinitionId(
			listTypeDefinitionId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.list.type.model.impl.ListTypeEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.list.type.model.impl.ListTypeEntryModelImpl</code>.
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

	public static ListTypeEntry fetchListTypeEntry(long listTypeEntryId) {
		return getService().fetchListTypeEntry(listTypeEntryId);
	}

	public static ListTypeEntry fetchListTypeEntry(
		long listTypeDefinitionId, String key) {

		return getService().fetchListTypeEntry(listTypeDefinitionId, key);
	}

	public static ListTypeEntry fetchListTypeEntryByExternalReferenceCode(
		String externalReferenceCode, long companyId,
		long listTypeDefinitionId) {

		return getService().fetchListTypeEntryByExternalReferenceCode(
			externalReferenceCode, companyId, listTypeDefinitionId);
	}

	/**
	 * Returns the list type entry with the matching UUID and company.
	 *
	 * @param uuid the list type entry's UUID
	 * @param companyId the primary key of the company
	 * @return the matching list type entry, or <code>null</code> if a matching list type entry could not be found
	 */
	public static ListTypeEntry fetchListTypeEntryByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().fetchListTypeEntryByUuidAndCompanyId(
			uuid, companyId);
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
	 * Returns a range of all the list type entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.list.type.model.impl.ListTypeEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of list type entries
	 * @param end the upper bound of the range of list type entries (not inclusive)
	 * @return the range of list type entries
	 */
	public static List<ListTypeEntry> getListTypeEntries(int start, int end) {
		return getService().getListTypeEntries(start, end);
	}

	public static List<ListTypeEntry> getListTypeEntries(
		long listTypeDefinitionId) {

		return getService().getListTypeEntries(listTypeDefinitionId);
	}

	public static List<ListTypeEntry> getListTypeEntries(
		long listTypeDefinitionId, int start, int end) {

		return getService().getListTypeEntries(
			listTypeDefinitionId, start, end);
	}

	public static List<ListTypeEntry> getListTypeEntries(
		long listTypeDefinitionId, int start, int end,
		OrderByComparator<ListTypeEntry> orderByComparator) {

		return getService().getListTypeEntries(
			listTypeDefinitionId, start, end, orderByComparator);
	}

	public static List<ListTypeEntry> getListTypeEntries(
		long[] listTypeDefinitionIds) {

		return getService().getListTypeEntries(listTypeDefinitionIds);
	}

	/**
	 * Returns the number of list type entries.
	 *
	 * @return the number of list type entries
	 */
	public static int getListTypeEntriesCount() {
		return getService().getListTypeEntriesCount();
	}

	public static int getListTypeEntriesCount(long listTypeDefinitionId) {
		return getService().getListTypeEntriesCount(listTypeDefinitionId);
	}

	/**
	 * Returns the list type entry with the primary key.
	 *
	 * @param listTypeEntryId the primary key of the list type entry
	 * @return the list type entry
	 * @throws PortalException if a list type entry with the primary key could not be found
	 */
	public static ListTypeEntry getListTypeEntry(long listTypeEntryId)
		throws PortalException {

		return getService().getListTypeEntry(listTypeEntryId);
	}

	public static ListTypeEntry getListTypeEntry(
			long listTypeDefinitionId, String key)
		throws PortalException {

		return getService().getListTypeEntry(listTypeDefinitionId, key);
	}

	public static ListTypeEntry getListTypeEntryByExternalReferenceCode(
			String externalReferenceCode, long companyId,
			long listTypeDefinitionId)
		throws PortalException {

		return getService().getListTypeEntryByExternalReferenceCode(
			externalReferenceCode, companyId, listTypeDefinitionId);
	}

	/**
	 * Returns the list type entry with the matching UUID and company.
	 *
	 * @param uuid the list type entry's UUID
	 * @param companyId the primary key of the company
	 * @return the matching list type entry
	 * @throws PortalException if a matching list type entry could not be found
	 */
	public static ListTypeEntry getListTypeEntryByUuidAndCompanyId(
			String uuid, long companyId)
		throws PortalException {

		return getService().getListTypeEntryByUuidAndCompanyId(uuid, companyId);
	}

	public static ListTypeEntry getOrAddEmptyListTypeEntry(
			long userId, long listTypeDefinitionId, String key)
		throws PortalException {

		return getService().getOrAddEmptyListTypeEntry(
			userId, listTypeDefinitionId, key);
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
	 * Updates the list type entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ListTypeEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param listTypeEntry the list type entry
	 * @return the list type entry that was updated
	 */
	public static ListTypeEntry updateListTypeEntry(
		ListTypeEntry listTypeEntry) {

		return getService().updateListTypeEntry(listTypeEntry);
	}

	public static ListTypeEntry updateListTypeEntry(
			String externalReferenceCode, long listTypeEntryId,
			Map<java.util.Locale, String> nameMap)
		throws PortalException {

		return getService().updateListTypeEntry(
			externalReferenceCode, listTypeEntryId, nameMap);
	}

	public static void updateUserId(
			long companyId, long oldUserId, long newUserId)
		throws PortalException {

		getService().updateUserId(companyId, oldUserId, newUserId);
	}

	public static ListTypeEntryLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<ListTypeEntryLocalService> _serviceSnapshot =
		new Snapshot<>(
			ListTypeEntryLocalServiceUtil.class,
			ListTypeEntryLocalService.class);

}