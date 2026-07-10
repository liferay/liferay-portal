/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service;

import com.liferay.commerce.product.model.CPAttachmentFileEntry;
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
 * Provides the local service utility for CPAttachmentFileEntry. This utility wraps
 * <code>com.liferay.commerce.product.service.impl.CPAttachmentFileEntryLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Marco Leo
 * @see CPAttachmentFileEntryLocalService
 * @generated
 */
public class CPAttachmentFileEntryLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.product.service.impl.CPAttachmentFileEntryLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the cp attachment file entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPAttachmentFileEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpAttachmentFileEntry the cp attachment file entry
	 * @return the cp attachment file entry that was added
	 */
	public static CPAttachmentFileEntry addCPAttachmentFileEntry(
		CPAttachmentFileEntry cpAttachmentFileEntry) {

		return getService().addCPAttachmentFileEntry(cpAttachmentFileEntry);
	}

	public static CPAttachmentFileEntry addCPAttachmentFileEntry(
			String externalReferenceCode, long userId, long groupId,
			long classNameId, long classPK, long fileEntryId,
			boolean cdnEnabled, String cdnURL, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire, boolean galleryEnabled,
			Map<java.util.Locale, String> titleMap, String json,
			double priority, int type,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCPAttachmentFileEntry(
			externalReferenceCode, userId, groupId, classNameId, classPK,
			fileEntryId, cdnEnabled, cdnURL, displayDateMonth, displayDateDay,
			displayDateYear, displayDateHour, displayDateMinute,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, neverExpire,
			galleryEnabled, titleMap, json, priority, type, serviceContext);
	}

	public static CPAttachmentFileEntry addOrUpdateCPAttachmentFileEntry(
			String externalReferenceCode, long userId, long groupId,
			long classNameId, long classPK, long cpAttachmentFileEntryId,
			long fileEntryId, boolean cdnEnabled, String cdnURL,
			int displayDateMonth, int displayDateDay, int displayDateYear,
			int displayDateHour, int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire, boolean galleryEnabled,
			Map<java.util.Locale, String> titleMap, String json,
			double priority, int type,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addOrUpdateCPAttachmentFileEntry(
			externalReferenceCode, userId, groupId, classNameId, classPK,
			cpAttachmentFileEntryId, fileEntryId, cdnEnabled, cdnURL,
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, expirationDateMonth, expirationDateDay,
			expirationDateYear, expirationDateHour, expirationDateMinute,
			neverExpire, galleryEnabled, titleMap, json, priority, type,
			serviceContext);
	}

	public static void checkCPAttachmentFileEntries() throws PortalException {
		getService().checkCPAttachmentFileEntries();
	}

	public static void checkCPAttachmentFileEntriesByDisplayDate(
			long classNameId, long classPK)
		throws PortalException {

		getService().checkCPAttachmentFileEntriesByDisplayDate(
			classNameId, classPK);
	}

	/**
	 * Creates a new cp attachment file entry with the primary key. Does not add the cp attachment file entry to the database.
	 *
	 * @param CPAttachmentFileEntryId the primary key for the new cp attachment file entry
	 * @return the new cp attachment file entry
	 */
	public static CPAttachmentFileEntry createCPAttachmentFileEntry(
		long CPAttachmentFileEntryId) {

		return getService().createCPAttachmentFileEntry(
			CPAttachmentFileEntryId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	public static void deleteCPAttachmentFileEntries(long fileEntryId)
		throws PortalException {

		getService().deleteCPAttachmentFileEntries(fileEntryId);
	}

	public static void deleteCPAttachmentFileEntries(
			String className, long classPK)
		throws PortalException {

		getService().deleteCPAttachmentFileEntries(className, classPK);
	}

	/**
	 * Deletes the cp attachment file entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPAttachmentFileEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpAttachmentFileEntry the cp attachment file entry
	 * @return the cp attachment file entry that was removed
	 * @throws PortalException
	 */
	public static CPAttachmentFileEntry deleteCPAttachmentFileEntry(
			CPAttachmentFileEntry cpAttachmentFileEntry)
		throws PortalException {

		return getService().deleteCPAttachmentFileEntry(cpAttachmentFileEntry);
	}

	/**
	 * Deletes the cp attachment file entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPAttachmentFileEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param CPAttachmentFileEntryId the primary key of the cp attachment file entry
	 * @return the cp attachment file entry that was removed
	 * @throws PortalException if a cp attachment file entry with the primary key could not be found
	 */
	public static CPAttachmentFileEntry deleteCPAttachmentFileEntry(
			long CPAttachmentFileEntryId)
		throws PortalException {

		return getService().deleteCPAttachmentFileEntry(
			CPAttachmentFileEntryId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPAttachmentFileEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPAttachmentFileEntryModelImpl</code>.
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

	public static CPAttachmentFileEntry fetchCPAttachmentFileEntry(
		long CPAttachmentFileEntryId) {

		return getService().fetchCPAttachmentFileEntry(CPAttachmentFileEntryId);
	}

	public static CPAttachmentFileEntry
		fetchCPAttachmentFileEntryByExternalReferenceCode(
			String externalReferenceCode, long companyId) {

		return getService().fetchCPAttachmentFileEntryByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the cp attachment file entry matching the UUID and group.
	 *
	 * @param uuid the cp attachment file entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching cp attachment file entry, or <code>null</code> if a matching cp attachment file entry could not be found
	 */
	public static CPAttachmentFileEntry
		fetchCPAttachmentFileEntryByUuidAndGroupId(String uuid, long groupId) {

		return getService().fetchCPAttachmentFileEntryByUuidAndGroupId(
			uuid, groupId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static com.liferay.portal.kernel.repository.model.Folder
			getAttachmentsFolder(
				long userId, long groupId, String className, long classPK)
		throws PortalException {

		return getService().getAttachmentsFolder(
			userId, groupId, className, classPK);
	}

	/**
	 * Returns a range of all the cp attachment file entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPAttachmentFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cp attachment file entries
	 * @param end the upper bound of the range of cp attachment file entries (not inclusive)
	 * @return the range of cp attachment file entries
	 */
	public static List<CPAttachmentFileEntry> getCPAttachmentFileEntries(
		int start, int end) {

		return getService().getCPAttachmentFileEntries(start, end);
	}

	public static List<CPAttachmentFileEntry> getCPAttachmentFileEntries(
			long cpDefinitionId, Boolean galleryEnabled,
			String serializedDDMFormValues, int type, int start, int end)
		throws Exception {

		return getService().getCPAttachmentFileEntries(
			cpDefinitionId, galleryEnabled, serializedDDMFormValues, type,
			start, end);
	}

	public static List<CPAttachmentFileEntry> getCPAttachmentFileEntries(
			long classNameId, long classPK, boolean galleryEnabled, int type,
			int status, int start, int end)
		throws PortalException {

		return getService().getCPAttachmentFileEntries(
			classNameId, classPK, galleryEnabled, type, status, start, end);
	}

	public static List<CPAttachmentFileEntry> getCPAttachmentFileEntries(
			long classNameId, long classPK, int type, int status, int start,
			int end)
		throws PortalException {

		return getService().getCPAttachmentFileEntries(
			classNameId, classPK, type, status, start, end);
	}

	public static List<CPAttachmentFileEntry> getCPAttachmentFileEntries(
			long classNameId, long classPK, int type, int status, int start,
			int end, OrderByComparator<CPAttachmentFileEntry> orderByComparator)
		throws PortalException {

		return getService().getCPAttachmentFileEntries(
			classNameId, classPK, type, status, start, end, orderByComparator);
	}

	public static List<CPAttachmentFileEntry> getCPAttachmentFileEntries(
			long classNameId, long classPK, String keywords, int type,
			int status, int start, int end)
		throws PortalException {

		return getService().getCPAttachmentFileEntries(
			classNameId, classPK, keywords, type, status, start, end);
	}

	/**
	 * Returns all the cp attachment file entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the cp attachment file entries
	 * @param companyId the primary key of the company
	 * @return the matching cp attachment file entries, or an empty list if no matches were found
	 */
	public static List<CPAttachmentFileEntry>
		getCPAttachmentFileEntriesByUuidAndCompanyId(
			String uuid, long companyId) {

		return getService().getCPAttachmentFileEntriesByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of cp attachment file entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the cp attachment file entries
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of cp attachment file entries
	 * @param end the upper bound of the range of cp attachment file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching cp attachment file entries, or an empty list if no matches were found
	 */
	public static List<CPAttachmentFileEntry>
		getCPAttachmentFileEntriesByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			OrderByComparator<CPAttachmentFileEntry> orderByComparator) {

		return getService().getCPAttachmentFileEntriesByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of cp attachment file entries.
	 *
	 * @return the number of cp attachment file entries
	 */
	public static int getCPAttachmentFileEntriesCount() {
		return getService().getCPAttachmentFileEntriesCount();
	}

	public static int getCPAttachmentFileEntriesCount(
		long classNameId, long classPK, int type, int status) {

		return getService().getCPAttachmentFileEntriesCount(
			classNameId, classPK, type, status);
	}

	public static int getCPAttachmentFileEntriesCount(
			long classNameId, long classPK, String keywords, int type,
			int status)
		throws PortalException {

		return getService().getCPAttachmentFileEntriesCount(
			classNameId, classPK, keywords, type, status);
	}

	/**
	 * Returns the cp attachment file entry with the primary key.
	 *
	 * @param CPAttachmentFileEntryId the primary key of the cp attachment file entry
	 * @return the cp attachment file entry
	 * @throws PortalException if a cp attachment file entry with the primary key could not be found
	 */
	public static CPAttachmentFileEntry getCPAttachmentFileEntry(
			long CPAttachmentFileEntryId)
		throws PortalException {

		return getService().getCPAttachmentFileEntry(CPAttachmentFileEntryId);
	}

	public static CPAttachmentFileEntry
			getCPAttachmentFileEntryByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().getCPAttachmentFileEntryByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the cp attachment file entry matching the UUID and group.
	 *
	 * @param uuid the cp attachment file entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching cp attachment file entry
	 * @throws PortalException if a matching cp attachment file entry could not be found
	 */
	public static CPAttachmentFileEntry
			getCPAttachmentFileEntryByUuidAndGroupId(String uuid, long groupId)
		throws PortalException {

		return getService().getCPAttachmentFileEntryByUuidAndGroupId(
			uuid, groupId);
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

	public static CPAttachmentFileEntry getOrAddEmptyCPAttachmentFileEntry(
			String externalReferenceCode, long companyId, long userId,
			long groupId, long classNameId, long classPK)
		throws PortalException {

		return getService().getOrAddEmptyCPAttachmentFileEntry(
			externalReferenceCode, companyId, userId, groupId, classNameId,
			classPK);
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

	public static void updateAsset(
			long userId, CPAttachmentFileEntry cpAttachmentFileEntry,
			long[] assetCategoryIds, String[] assetTagNames,
			long[] assetLinkEntryIds, Double priority)
		throws PortalException {

		getService().updateAsset(
			userId, cpAttachmentFileEntry, assetCategoryIds, assetTagNames,
			assetLinkEntryIds, priority);
	}

	/**
	 * Updates the cp attachment file entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPAttachmentFileEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpAttachmentFileEntry the cp attachment file entry
	 * @return the cp attachment file entry that was updated
	 */
	public static CPAttachmentFileEntry updateCPAttachmentFileEntry(
		CPAttachmentFileEntry cpAttachmentFileEntry) {

		return getService().updateCPAttachmentFileEntry(cpAttachmentFileEntry);
	}

	public static CPAttachmentFileEntry updateCPAttachmentFileEntry(
			long userId, long cpAttachmentFileEntryId, long fileEntryId,
			boolean cdnEnabled, String cdnURL, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire, boolean galleryEnabled,
			Map<java.util.Locale, String> titleMap, String json,
			double priority, int type,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateCPAttachmentFileEntry(
			userId, cpAttachmentFileEntryId, fileEntryId, cdnEnabled, cdnURL,
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, expirationDateMonth, expirationDateDay,
			expirationDateYear, expirationDateHour, expirationDateMinute,
			neverExpire, galleryEnabled, titleMap, json, priority, type,
			serviceContext);
	}

	public static CPAttachmentFileEntry updateStatus(
			long userId, long cpAttachmentFileEntryId, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext,
			Map<String, Serializable> workflowContext)
		throws PortalException {

		return getService().updateStatus(
			userId, cpAttachmentFileEntryId, status, serviceContext,
			workflowContext);
	}

	public static CPAttachmentFileEntryLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CPAttachmentFileEntryLocalService>
		_serviceSnapshot = new Snapshot<>(
			CPAttachmentFileEntryLocalServiceUtil.class,
			CPAttachmentFileEntryLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:677714773