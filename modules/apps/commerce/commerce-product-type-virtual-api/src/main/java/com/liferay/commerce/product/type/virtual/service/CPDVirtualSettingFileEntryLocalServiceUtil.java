/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.type.virtual.service;

import com.liferay.commerce.product.type.virtual.model.CPDVirtualSettingFileEntry;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.InputStream;
import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for CPDVirtualSettingFileEntry. This utility wraps
 * <code>com.liferay.commerce.product.type.virtual.service.impl.CPDVirtualSettingFileEntryLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Marco Leo
 * @see CPDVirtualSettingFileEntryLocalService
 * @generated
 */
public class CPDVirtualSettingFileEntryLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.product.type.virtual.service.impl.CPDVirtualSettingFileEntryLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the cpd virtual setting file entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPDVirtualSettingFileEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpdVirtualSettingFileEntry the cpd virtual setting file entry
	 * @return the cpd virtual setting file entry that was added
	 */
	public static CPDVirtualSettingFileEntry addCPDVirtualSettingFileEntry(
		CPDVirtualSettingFileEntry cpdVirtualSettingFileEntry) {

		return getService().addCPDVirtualSettingFileEntry(
			cpdVirtualSettingFileEntry);
	}

	public static CPDVirtualSettingFileEntry addCPDVirtualSettingFileEntry(
			long userId, long groupId, long cpDefinitionVirtualSettingId,
			long fileEntryId, String url, String version)
		throws PortalException {

		return getService().addCPDVirtualSettingFileEntry(
			userId, groupId, cpDefinitionVirtualSettingId, fileEntryId, url,
			version);
	}

	public static com.liferay.portal.kernel.repository.model.FileEntry
			addFileEntry(
				long userId, long groupId, String className, long classPK,
				String serviceName, long folderId, InputStream inputStream,
				String fileName, String mimeType)
		throws PortalException {

		return getService().addFileEntry(
			userId, groupId, className, classPK, serviceName, folderId,
			inputStream, fileName, mimeType);
	}

	/**
	 * Creates a new cpd virtual setting file entry with the primary key. Does not add the cpd virtual setting file entry to the database.
	 *
	 * @param CPDefinitionVirtualSettingFileEntryId the primary key for the new cpd virtual setting file entry
	 * @return the new cpd virtual setting file entry
	 */
	public static CPDVirtualSettingFileEntry createCPDVirtualSettingFileEntry(
		long CPDefinitionVirtualSettingFileEntryId) {

		return getService().createCPDVirtualSettingFileEntry(
			CPDefinitionVirtualSettingFileEntryId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	public static void deleteCPDVirtualSettingFileEntries(
			long cpDefinitionVirtualSettingId)
		throws PortalException {

		getService().deleteCPDVirtualSettingFileEntries(
			cpDefinitionVirtualSettingId);
	}

	/**
	 * Deletes the cpd virtual setting file entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPDVirtualSettingFileEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpdVirtualSettingFileEntry the cpd virtual setting file entry
	 * @return the cpd virtual setting file entry that was removed
	 */
	public static CPDVirtualSettingFileEntry deleteCPDVirtualSettingFileEntry(
		CPDVirtualSettingFileEntry cpdVirtualSettingFileEntry) {

		return getService().deleteCPDVirtualSettingFileEntry(
			cpdVirtualSettingFileEntry);
	}

	/**
	 * Deletes the cpd virtual setting file entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPDVirtualSettingFileEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param CPDefinitionVirtualSettingFileEntryId the primary key of the cpd virtual setting file entry
	 * @return the cpd virtual setting file entry that was removed
	 * @throws PortalException if a cpd virtual setting file entry with the primary key could not be found
	 */
	public static CPDVirtualSettingFileEntry deleteCPDVirtualSettingFileEntry(
			long CPDefinitionVirtualSettingFileEntryId)
		throws PortalException {

		return getService().deleteCPDVirtualSettingFileEntry(
			CPDefinitionVirtualSettingFileEntryId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.type.virtual.model.impl.CPDVirtualSettingFileEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.type.virtual.model.impl.CPDVirtualSettingFileEntryModelImpl</code>.
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

	public static CPDVirtualSettingFileEntry fetchCPDVirtualSettingFileEntry(
		long CPDefinitionVirtualSettingFileEntryId) {

		return getService().fetchCPDVirtualSettingFileEntry(
			CPDefinitionVirtualSettingFileEntryId);
	}

	/**
	 * Returns the cpd virtual setting file entry matching the UUID and group.
	 *
	 * @param uuid the cpd virtual setting file entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching cpd virtual setting file entry, or <code>null</code> if a matching cpd virtual setting file entry could not be found
	 */
	public static CPDVirtualSettingFileEntry
		fetchCPDVirtualSettingFileEntryByUuidAndGroupId(
			String uuid, long groupId) {

		return getService().fetchCPDVirtualSettingFileEntryByUuidAndGroupId(
			uuid, groupId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns a range of all the cpd virtual setting file entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.type.virtual.model.impl.CPDVirtualSettingFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cpd virtual setting file entries
	 * @param end the upper bound of the range of cpd virtual setting file entries (not inclusive)
	 * @return the range of cpd virtual setting file entries
	 */
	public static List<CPDVirtualSettingFileEntry>
		getCPDVirtualSettingFileEntries(int start, int end) {

		return getService().getCPDVirtualSettingFileEntries(start, end);
	}

	public static List<CPDVirtualSettingFileEntry>
		getCPDVirtualSettingFileEntries(long cpDefinitionVirtualSettingId) {

		return getService().getCPDVirtualSettingFileEntries(
			cpDefinitionVirtualSettingId);
	}

	public static List<CPDVirtualSettingFileEntry>
		getCPDVirtualSettingFileEntries(
			long cpDefinitionVirtualSettingId, int start, int end) {

		return getService().getCPDVirtualSettingFileEntries(
			cpDefinitionVirtualSettingId, start, end);
	}

	/**
	 * Returns all the cpd virtual setting file entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the cpd virtual setting file entries
	 * @param companyId the primary key of the company
	 * @return the matching cpd virtual setting file entries, or an empty list if no matches were found
	 */
	public static List<CPDVirtualSettingFileEntry>
		getCPDVirtualSettingFileEntriesByUuidAndCompanyId(
			String uuid, long companyId) {

		return getService().getCPDVirtualSettingFileEntriesByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of cpd virtual setting file entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the cpd virtual setting file entries
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of cpd virtual setting file entries
	 * @param end the upper bound of the range of cpd virtual setting file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching cpd virtual setting file entries, or an empty list if no matches were found
	 */
	public static List<CPDVirtualSettingFileEntry>
		getCPDVirtualSettingFileEntriesByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			OrderByComparator<CPDVirtualSettingFileEntry> orderByComparator) {

		return getService().getCPDVirtualSettingFileEntriesByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of cpd virtual setting file entries.
	 *
	 * @return the number of cpd virtual setting file entries
	 */
	public static int getCPDVirtualSettingFileEntriesCount() {
		return getService().getCPDVirtualSettingFileEntriesCount();
	}

	public static int getCPDVirtualSettingFileEntriesCount(
		long cpDefinitionVirtualSettingId) {

		return getService().getCPDVirtualSettingFileEntriesCount(
			cpDefinitionVirtualSettingId);
	}

	/**
	 * Returns the cpd virtual setting file entry with the primary key.
	 *
	 * @param CPDefinitionVirtualSettingFileEntryId the primary key of the cpd virtual setting file entry
	 * @return the cpd virtual setting file entry
	 * @throws PortalException if a cpd virtual setting file entry with the primary key could not be found
	 */
	public static CPDVirtualSettingFileEntry getCPDVirtualSettingFileEntry(
			long CPDefinitionVirtualSettingFileEntryId)
		throws PortalException {

		return getService().getCPDVirtualSettingFileEntry(
			CPDefinitionVirtualSettingFileEntryId);
	}

	/**
	 * Returns the cpd virtual setting file entry matching the UUID and group.
	 *
	 * @param uuid the cpd virtual setting file entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching cpd virtual setting file entry
	 * @throws PortalException if a matching cpd virtual setting file entry could not be found
	 */
	public static CPDVirtualSettingFileEntry
			getCPDVirtualSettingFileEntryByUuidAndGroupId(
				String uuid, long groupId)
		throws PortalException {

		return getService().getCPDVirtualSettingFileEntryByUuidAndGroupId(
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
	 * Updates the cpd virtual setting file entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPDVirtualSettingFileEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpdVirtualSettingFileEntry the cpd virtual setting file entry
	 * @return the cpd virtual setting file entry that was updated
	 */
	public static CPDVirtualSettingFileEntry updateCPDVirtualSettingFileEntry(
		CPDVirtualSettingFileEntry cpdVirtualSettingFileEntry) {

		return getService().updateCPDVirtualSettingFileEntry(
			cpdVirtualSettingFileEntry);
	}

	public static CPDVirtualSettingFileEntry updateCPDVirtualSettingFileEntry(
			long cpdVirtualSettingFileEntryId, long fileEntryId, String url,
			String version)
		throws PortalException {

		return getService().updateCPDVirtualSettingFileEntry(
			cpdVirtualSettingFileEntryId, fileEntryId, url, version);
	}

	public static CPDVirtualSettingFileEntryLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CPDVirtualSettingFileEntryLocalService>
		_serviceSnapshot = new Snapshot<>(
			CPDVirtualSettingFileEntryLocalServiceUtil.class,
			CPDVirtualSettingFileEntryLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-1766830834