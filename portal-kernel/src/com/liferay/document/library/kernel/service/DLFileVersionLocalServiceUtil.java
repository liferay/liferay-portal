/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.kernel.service;

import com.liferay.document.library.kernel.model.DLFileVersion;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for DLFileVersion. This utility wraps
 * <code>com.liferay.portlet.documentlibrary.service.impl.DLFileVersionLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see DLFileVersionLocalService
 * @generated
 */
public class DLFileVersionLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portlet.documentlibrary.service.impl.DLFileVersionLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the document library file version to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DLFileVersionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param dlFileVersion the document library file version
	 * @return the document library file version that was added
	 */
	public static DLFileVersion addDLFileVersion(DLFileVersion dlFileVersion) {
		return getService().addDLFileVersion(dlFileVersion);
	}

	/**
	 * Creates a new document library file version with the primary key. Does not add the document library file version to the database.
	 *
	 * @param fileVersionId the primary key for the new document library file version
	 * @return the new document library file version
	 */
	public static DLFileVersion createDLFileVersion(long fileVersionId) {
		return getService().createDLFileVersion(fileVersionId);
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
	 * Deletes the document library file version from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DLFileVersionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param dlFileVersion the document library file version
	 * @return the document library file version that was removed
	 */
	public static DLFileVersion deleteDLFileVersion(
		DLFileVersion dlFileVersion) {

		return getService().deleteDLFileVersion(dlFileVersion);
	}

	/**
	 * Deletes the document library file version with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DLFileVersionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param fileVersionId the primary key of the document library file version
	 * @return the document library file version that was removed
	 * @throws PortalException if a document library file version with the primary key could not be found
	 */
	public static DLFileVersion deleteDLFileVersion(long fileVersionId)
		throws PortalException {

		return getService().deleteDLFileVersion(fileVersionId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileVersionModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileVersionModelImpl</code>.
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

	public static DLFileVersion fetchDLFileVersion(long fileVersionId) {
		return getService().fetchDLFileVersion(fileVersionId);
	}

	/**
	 * Returns the document library file version matching the UUID and group.
	 *
	 * @param uuid the document library file version's UUID
	 * @param groupId the primary key of the group
	 * @return the matching document library file version, or <code>null</code> if a matching document library file version could not be found
	 */
	public static DLFileVersion fetchDLFileVersionByUuidAndGroupId(
		String uuid, long groupId) {

		return getService().fetchDLFileVersionByUuidAndGroupId(uuid, groupId);
	}

	public static DLFileVersion fetchLatestFileVersion(
		long fileEntryId, boolean excludeWorkingCopy) {

		return getService().fetchLatestFileVersion(
			fileEntryId, excludeWorkingCopy);
	}

	public static DLFileVersion fetchLatestFileVersion(
		long fileEntryId, boolean excludeWorkingCopy, int status) {

		return getService().fetchLatestFileVersion(
			fileEntryId, excludeWorkingCopy, status);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the document library file version with the primary key.
	 *
	 * @param fileVersionId the primary key of the document library file version
	 * @return the document library file version
	 * @throws PortalException if a document library file version with the primary key could not be found
	 */
	public static DLFileVersion getDLFileVersion(long fileVersionId)
		throws PortalException {

		return getService().getDLFileVersion(fileVersionId);
	}

	/**
	 * Returns the document library file version matching the UUID and group.
	 *
	 * @param uuid the document library file version's UUID
	 * @param groupId the primary key of the group
	 * @return the matching document library file version
	 * @throws PortalException if a matching document library file version could not be found
	 */
	public static DLFileVersion getDLFileVersionByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException {

		return getService().getDLFileVersionByUuidAndGroupId(uuid, groupId);
	}

	/**
	 * Returns a range of all the document library file versions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileVersionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of document library file versions
	 * @param end the upper bound of the range of document library file versions (not inclusive)
	 * @return the range of document library file versions
	 */
	public static List<DLFileVersion> getDLFileVersions(int start, int end) {
		return getService().getDLFileVersions(start, end);
	}

	/**
	 * Returns all the document library file versions matching the UUID and company.
	 *
	 * @param uuid the UUID of the document library file versions
	 * @param companyId the primary key of the company
	 * @return the matching document library file versions, or an empty list if no matches were found
	 */
	public static List<DLFileVersion> getDLFileVersionsByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().getDLFileVersionsByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of document library file versions matching the UUID and company.
	 *
	 * @param uuid the UUID of the document library file versions
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of document library file versions
	 * @param end the upper bound of the range of document library file versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching document library file versions, or an empty list if no matches were found
	 */
	public static List<DLFileVersion> getDLFileVersionsByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<DLFileVersion> orderByComparator) {

		return getService().getDLFileVersionsByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of document library file versions.
	 *
	 * @return the number of document library file versions
	 */
	public static int getDLFileVersionsCount() {
		return getService().getDLFileVersionsCount();
	}

	public static com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return getService().getExportActionableDynamicQuery(portletDataContext);
	}

	public static DLFileVersion getFileVersion(long fileVersionId)
		throws PortalException {

		return getService().getFileVersion(fileVersionId);
	}

	public static DLFileVersion getFileVersion(long fileEntryId, String version)
		throws PortalException {

		return getService().getFileVersion(fileEntryId, version);
	}

	public static DLFileVersion getFileVersionByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException {

		return getService().getFileVersionByUuidAndGroupId(uuid, groupId);
	}

	public static List<DLFileVersion> getFileVersions(
		long fileEntryId, int status) {

		return getService().getFileVersions(fileEntryId, status);
	}

	public static List<DLFileVersion> getFileVersions(
		long fileEntryId, int status, int start, int end) {

		return getService().getFileVersions(fileEntryId, status, start, end);
	}

	public static int getFileVersionsCount(long fileEntryId, int status) {
		return getService().getFileVersionsCount(fileEntryId, status);
	}

	public static int getFileVersionsCount(long companyId, String storeUUID) {
		return getService().getFileVersionsCount(companyId, storeUUID);
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	public static DLFileVersion getLatestFileVersion(
			long fileEntryId, boolean excludeWorkingCopy)
		throws PortalException {

		return getService().getLatestFileVersion(
			fileEntryId, excludeWorkingCopy);
	}

	public static DLFileVersion getLatestFileVersion(
			long userId, long fileEntryId)
		throws PortalException {

		return getService().getLatestFileVersion(userId, fileEntryId);
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

	public static void rebuildTree(long companyId) throws PortalException {
		getService().rebuildTree(companyId);
	}

	public static void setTreePaths(long folderId, String treePath)
		throws PortalException {

		getService().setTreePaths(folderId, treePath);
	}

	/**
	 * Updates the document library file version in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DLFileVersionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param dlFileVersion the document library file version
	 * @return the document library file version that was updated
	 */
	public static DLFileVersion updateDLFileVersion(
		DLFileVersion dlFileVersion) {

		return getService().updateDLFileVersion(dlFileVersion);
	}

	public static DLFileVersionLocalService getService() {
		return _service;
	}

	public static void setService(DLFileVersionLocalService service) {
		_service = service;
	}

	private static volatile DLFileVersionLocalService _service;

}
// LIFERAY-SERVICE-BUILDER-HASH:1156495179