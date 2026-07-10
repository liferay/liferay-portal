/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service;

import com.liferay.commerce.product.model.CPAttachmentFileEntry;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link CPAttachmentFileEntryLocalService}.
 *
 * @author Marco Leo
 * @see CPAttachmentFileEntryLocalService
 * @generated
 */
public class CPAttachmentFileEntryLocalServiceWrapper
	implements CPAttachmentFileEntryLocalService,
			   ServiceWrapper<CPAttachmentFileEntryLocalService> {

	public CPAttachmentFileEntryLocalServiceWrapper() {
		this(null);
	}

	public CPAttachmentFileEntryLocalServiceWrapper(
		CPAttachmentFileEntryLocalService cpAttachmentFileEntryLocalService) {

		_cpAttachmentFileEntryLocalService = cpAttachmentFileEntryLocalService;
	}

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
	@Override
	public CPAttachmentFileEntry addCPAttachmentFileEntry(
		CPAttachmentFileEntry cpAttachmentFileEntry) {

		return _cpAttachmentFileEntryLocalService.addCPAttachmentFileEntry(
			cpAttachmentFileEntry);
	}

	@Override
	public CPAttachmentFileEntry addCPAttachmentFileEntry(
			String externalReferenceCode, long userId, long groupId,
			long classNameId, long classPK, long fileEntryId,
			boolean cdnEnabled, String cdnURL, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire, boolean galleryEnabled,
			java.util.Map<java.util.Locale, String> titleMap, String json,
			double priority, int type,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpAttachmentFileEntryLocalService.addCPAttachmentFileEntry(
			externalReferenceCode, userId, groupId, classNameId, classPK,
			fileEntryId, cdnEnabled, cdnURL, displayDateMonth, displayDateDay,
			displayDateYear, displayDateHour, displayDateMinute,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, neverExpire,
			galleryEnabled, titleMap, json, priority, type, serviceContext);
	}

	@Override
	public CPAttachmentFileEntry addOrUpdateCPAttachmentFileEntry(
			String externalReferenceCode, long userId, long groupId,
			long classNameId, long classPK, long cpAttachmentFileEntryId,
			long fileEntryId, boolean cdnEnabled, String cdnURL,
			int displayDateMonth, int displayDateDay, int displayDateYear,
			int displayDateHour, int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire, boolean galleryEnabled,
			java.util.Map<java.util.Locale, String> titleMap, String json,
			double priority, int type,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpAttachmentFileEntryLocalService.
			addOrUpdateCPAttachmentFileEntry(
				externalReferenceCode, userId, groupId, classNameId, classPK,
				cpAttachmentFileEntryId, fileEntryId, cdnEnabled, cdnURL,
				displayDateMonth, displayDateDay, displayDateYear,
				displayDateHour, displayDateMinute, expirationDateMonth,
				expirationDateDay, expirationDateYear, expirationDateHour,
				expirationDateMinute, neverExpire, galleryEnabled, titleMap,
				json, priority, type, serviceContext);
	}

	@Override
	public void checkCPAttachmentFileEntries()
		throws com.liferay.portal.kernel.exception.PortalException {

		_cpAttachmentFileEntryLocalService.checkCPAttachmentFileEntries();
	}

	@Override
	public void checkCPAttachmentFileEntriesByDisplayDate(
			long classNameId, long classPK)
		throws com.liferay.portal.kernel.exception.PortalException {

		_cpAttachmentFileEntryLocalService.
			checkCPAttachmentFileEntriesByDisplayDate(classNameId, classPK);
	}

	/**
	 * Creates a new cp attachment file entry with the primary key. Does not add the cp attachment file entry to the database.
	 *
	 * @param CPAttachmentFileEntryId the primary key for the new cp attachment file entry
	 * @return the new cp attachment file entry
	 */
	@Override
	public CPAttachmentFileEntry createCPAttachmentFileEntry(
		long CPAttachmentFileEntryId) {

		return _cpAttachmentFileEntryLocalService.createCPAttachmentFileEntry(
			CPAttachmentFileEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpAttachmentFileEntryLocalService.createPersistedModel(
			primaryKeyObj);
	}

	@Override
	public void deleteCPAttachmentFileEntries(long fileEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_cpAttachmentFileEntryLocalService.deleteCPAttachmentFileEntries(
			fileEntryId);
	}

	@Override
	public void deleteCPAttachmentFileEntries(String className, long classPK)
		throws com.liferay.portal.kernel.exception.PortalException {

		_cpAttachmentFileEntryLocalService.deleteCPAttachmentFileEntries(
			className, classPK);
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
	@Override
	public CPAttachmentFileEntry deleteCPAttachmentFileEntry(
			CPAttachmentFileEntry cpAttachmentFileEntry)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpAttachmentFileEntryLocalService.deleteCPAttachmentFileEntry(
			cpAttachmentFileEntry);
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
	@Override
	public CPAttachmentFileEntry deleteCPAttachmentFileEntry(
			long CPAttachmentFileEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpAttachmentFileEntryLocalService.deleteCPAttachmentFileEntry(
			CPAttachmentFileEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpAttachmentFileEntryLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _cpAttachmentFileEntryLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _cpAttachmentFileEntryLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _cpAttachmentFileEntryLocalService.dynamicQuery();
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

		return _cpAttachmentFileEntryLocalService.dynamicQuery(dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _cpAttachmentFileEntryLocalService.dynamicQuery(
			dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _cpAttachmentFileEntryLocalService.dynamicQuery(
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

		return _cpAttachmentFileEntryLocalService.dynamicQueryCount(
			dynamicQuery);
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

		return _cpAttachmentFileEntryLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public CPAttachmentFileEntry fetchCPAttachmentFileEntry(
		long CPAttachmentFileEntryId) {

		return _cpAttachmentFileEntryLocalService.fetchCPAttachmentFileEntry(
			CPAttachmentFileEntryId);
	}

	@Override
	public CPAttachmentFileEntry
		fetchCPAttachmentFileEntryByExternalReferenceCode(
			String externalReferenceCode, long companyId) {

		return _cpAttachmentFileEntryLocalService.
			fetchCPAttachmentFileEntryByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	/**
	 * Returns the cp attachment file entry matching the UUID and group.
	 *
	 * @param uuid the cp attachment file entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching cp attachment file entry, or <code>null</code> if a matching cp attachment file entry could not be found
	 */
	@Override
	public CPAttachmentFileEntry fetchCPAttachmentFileEntryByUuidAndGroupId(
		String uuid, long groupId) {

		return _cpAttachmentFileEntryLocalService.
			fetchCPAttachmentFileEntryByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _cpAttachmentFileEntryLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.repository.model.Folder
			getAttachmentsFolder(
				long userId, long groupId, String className, long classPK)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpAttachmentFileEntryLocalService.getAttachmentsFolder(
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
	@Override
	public java.util.List<CPAttachmentFileEntry> getCPAttachmentFileEntries(
		int start, int end) {

		return _cpAttachmentFileEntryLocalService.getCPAttachmentFileEntries(
			start, end);
	}

	@Override
	public java.util.List<CPAttachmentFileEntry> getCPAttachmentFileEntries(
			long cpDefinitionId, Boolean galleryEnabled,
			String serializedDDMFormValues, int type, int start, int end)
		throws Exception {

		return _cpAttachmentFileEntryLocalService.getCPAttachmentFileEntries(
			cpDefinitionId, galleryEnabled, serializedDDMFormValues, type,
			start, end);
	}

	@Override
	public java.util.List<CPAttachmentFileEntry> getCPAttachmentFileEntries(
			long classNameId, long classPK, boolean galleryEnabled, int type,
			int status, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpAttachmentFileEntryLocalService.getCPAttachmentFileEntries(
			classNameId, classPK, galleryEnabled, type, status, start, end);
	}

	@Override
	public java.util.List<CPAttachmentFileEntry> getCPAttachmentFileEntries(
			long classNameId, long classPK, int type, int status, int start,
			int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpAttachmentFileEntryLocalService.getCPAttachmentFileEntries(
			classNameId, classPK, type, status, start, end);
	}

	@Override
	public java.util.List<CPAttachmentFileEntry> getCPAttachmentFileEntries(
			long classNameId, long classPK, int type, int status, int start,
			int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<CPAttachmentFileEntry> orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpAttachmentFileEntryLocalService.getCPAttachmentFileEntries(
			classNameId, classPK, type, status, start, end, orderByComparator);
	}

	@Override
	public java.util.List<CPAttachmentFileEntry> getCPAttachmentFileEntries(
			long classNameId, long classPK, String keywords, int type,
			int status, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpAttachmentFileEntryLocalService.getCPAttachmentFileEntries(
			classNameId, classPK, keywords, type, status, start, end);
	}

	/**
	 * Returns all the cp attachment file entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the cp attachment file entries
	 * @param companyId the primary key of the company
	 * @return the matching cp attachment file entries, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<CPAttachmentFileEntry>
		getCPAttachmentFileEntriesByUuidAndCompanyId(
			String uuid, long companyId) {

		return _cpAttachmentFileEntryLocalService.
			getCPAttachmentFileEntriesByUuidAndCompanyId(uuid, companyId);
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
	@Override
	public java.util.List<CPAttachmentFileEntry>
		getCPAttachmentFileEntriesByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<CPAttachmentFileEntry> orderByComparator) {

		return _cpAttachmentFileEntryLocalService.
			getCPAttachmentFileEntriesByUuidAndCompanyId(
				uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of cp attachment file entries.
	 *
	 * @return the number of cp attachment file entries
	 */
	@Override
	public int getCPAttachmentFileEntriesCount() {
		return _cpAttachmentFileEntryLocalService.
			getCPAttachmentFileEntriesCount();
	}

	@Override
	public int getCPAttachmentFileEntriesCount(
		long classNameId, long classPK, int type, int status) {

		return _cpAttachmentFileEntryLocalService.
			getCPAttachmentFileEntriesCount(classNameId, classPK, type, status);
	}

	@Override
	public int getCPAttachmentFileEntriesCount(
			long classNameId, long classPK, String keywords, int type,
			int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpAttachmentFileEntryLocalService.
			getCPAttachmentFileEntriesCount(
				classNameId, classPK, keywords, type, status);
	}

	/**
	 * Returns the cp attachment file entry with the primary key.
	 *
	 * @param CPAttachmentFileEntryId the primary key of the cp attachment file entry
	 * @return the cp attachment file entry
	 * @throws PortalException if a cp attachment file entry with the primary key could not be found
	 */
	@Override
	public CPAttachmentFileEntry getCPAttachmentFileEntry(
			long CPAttachmentFileEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpAttachmentFileEntryLocalService.getCPAttachmentFileEntry(
			CPAttachmentFileEntryId);
	}

	@Override
	public CPAttachmentFileEntry
			getCPAttachmentFileEntryByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpAttachmentFileEntryLocalService.
			getCPAttachmentFileEntryByExternalReferenceCode(
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
	@Override
	public CPAttachmentFileEntry getCPAttachmentFileEntryByUuidAndGroupId(
			String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpAttachmentFileEntryLocalService.
			getCPAttachmentFileEntryByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _cpAttachmentFileEntryLocalService.
			getExportActionableDynamicQuery(portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _cpAttachmentFileEntryLocalService.
			getIndexableActionableDynamicQuery();
	}

	@Override
	public CPAttachmentFileEntry getOrAddEmptyCPAttachmentFileEntry(
			String externalReferenceCode, long companyId, long userId,
			long groupId, long classNameId, long classPK)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpAttachmentFileEntryLocalService.
			getOrAddEmptyCPAttachmentFileEntry(
				externalReferenceCode, companyId, userId, groupId, classNameId,
				classPK);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _cpAttachmentFileEntryLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpAttachmentFileEntryLocalService.getPersistedModel(
			primaryKeyObj);
	}

	@Override
	public void updateAsset(
			long userId, CPAttachmentFileEntry cpAttachmentFileEntry,
			long[] assetCategoryIds, String[] assetTagNames,
			long[] assetLinkEntryIds, Double priority)
		throws com.liferay.portal.kernel.exception.PortalException {

		_cpAttachmentFileEntryLocalService.updateAsset(
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
	@Override
	public CPAttachmentFileEntry updateCPAttachmentFileEntry(
		CPAttachmentFileEntry cpAttachmentFileEntry) {

		return _cpAttachmentFileEntryLocalService.updateCPAttachmentFileEntry(
			cpAttachmentFileEntry);
	}

	@Override
	public CPAttachmentFileEntry updateCPAttachmentFileEntry(
			long userId, long cpAttachmentFileEntryId, long fileEntryId,
			boolean cdnEnabled, String cdnURL, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire, boolean galleryEnabled,
			java.util.Map<java.util.Locale, String> titleMap, String json,
			double priority, int type,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpAttachmentFileEntryLocalService.updateCPAttachmentFileEntry(
			userId, cpAttachmentFileEntryId, fileEntryId, cdnEnabled, cdnURL,
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, expirationDateMonth, expirationDateDay,
			expirationDateYear, expirationDateHour, expirationDateMinute,
			neverExpire, galleryEnabled, titleMap, json, priority, type,
			serviceContext);
	}

	@Override
	public CPAttachmentFileEntry updateStatus(
			long userId, long cpAttachmentFileEntryId, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext,
			java.util.Map<String, java.io.Serializable> workflowContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpAttachmentFileEntryLocalService.updateStatus(
			userId, cpAttachmentFileEntryId, status, serviceContext,
			workflowContext);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _cpAttachmentFileEntryLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<CPAttachmentFileEntry> getCTPersistence() {
		return _cpAttachmentFileEntryLocalService.getCTPersistence();
	}

	@Override
	public Class<CPAttachmentFileEntry> getModelClass() {
		return _cpAttachmentFileEntryLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<CPAttachmentFileEntry>, R, E>
				updateUnsafeFunction)
		throws E {

		return _cpAttachmentFileEntryLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public CPAttachmentFileEntryLocalService getWrappedService() {
		return _cpAttachmentFileEntryLocalService;
	}

	@Override
	public void setWrappedService(
		CPAttachmentFileEntryLocalService cpAttachmentFileEntryLocalService) {

		_cpAttachmentFileEntryLocalService = cpAttachmentFileEntryLocalService;
	}

	private CPAttachmentFileEntryLocalService
		_cpAttachmentFileEntryLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:576170114