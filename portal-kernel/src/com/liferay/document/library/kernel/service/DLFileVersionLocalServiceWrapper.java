/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.kernel.service;

import com.liferay.document.library.kernel.model.DLFileVersion;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link DLFileVersionLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see DLFileVersionLocalService
 * @generated
 */
public class DLFileVersionLocalServiceWrapper
	implements DLFileVersionLocalService,
			   ServiceWrapper<DLFileVersionLocalService> {

	public DLFileVersionLocalServiceWrapper() {
		this(null);
	}

	public DLFileVersionLocalServiceWrapper(
		DLFileVersionLocalService dlFileVersionLocalService) {

		_dlFileVersionLocalService = dlFileVersionLocalService;
	}

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
	@Override
	public DLFileVersion addDLFileVersion(DLFileVersion dlFileVersion) {
		return _dlFileVersionLocalService.addDLFileVersion(dlFileVersion);
	}

	/**
	 * Creates a new document library file version with the primary key. Does not add the document library file version to the database.
	 *
	 * @param fileVersionId the primary key for the new document library file version
	 * @return the new document library file version
	 */
	@Override
	public DLFileVersion createDLFileVersion(long fileVersionId) {
		return _dlFileVersionLocalService.createDLFileVersion(fileVersionId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileVersionLocalService.createPersistedModel(primaryKeyObj);
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
	@Override
	public DLFileVersion deleteDLFileVersion(DLFileVersion dlFileVersion) {
		return _dlFileVersionLocalService.deleteDLFileVersion(dlFileVersion);
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
	@Override
	public DLFileVersion deleteDLFileVersion(long fileVersionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileVersionLocalService.deleteDLFileVersion(fileVersionId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileVersionLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _dlFileVersionLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _dlFileVersionLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _dlFileVersionLocalService.dynamicQuery();
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

		return _dlFileVersionLocalService.dynamicQuery(dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _dlFileVersionLocalService.dynamicQuery(
			dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _dlFileVersionLocalService.dynamicQuery(
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

		return _dlFileVersionLocalService.dynamicQueryCount(dynamicQuery);
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

		return _dlFileVersionLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public DLFileVersion fetchDLFileVersion(long fileVersionId) {
		return _dlFileVersionLocalService.fetchDLFileVersion(fileVersionId);
	}

	/**
	 * Returns the document library file version matching the UUID and group.
	 *
	 * @param uuid the document library file version's UUID
	 * @param groupId the primary key of the group
	 * @return the matching document library file version, or <code>null</code> if a matching document library file version could not be found
	 */
	@Override
	public DLFileVersion fetchDLFileVersionByUuidAndGroupId(
		String uuid, long groupId) {

		return _dlFileVersionLocalService.fetchDLFileVersionByUuidAndGroupId(
			uuid, groupId);
	}

	@Override
	public DLFileVersion fetchLatestFileVersion(
		long fileEntryId, boolean excludeWorkingCopy) {

		return _dlFileVersionLocalService.fetchLatestFileVersion(
			fileEntryId, excludeWorkingCopy);
	}

	@Override
	public DLFileVersion fetchLatestFileVersion(
		long fileEntryId, boolean excludeWorkingCopy, int status) {

		return _dlFileVersionLocalService.fetchLatestFileVersion(
			fileEntryId, excludeWorkingCopy, status);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _dlFileVersionLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns the document library file version with the primary key.
	 *
	 * @param fileVersionId the primary key of the document library file version
	 * @return the document library file version
	 * @throws PortalException if a document library file version with the primary key could not be found
	 */
	@Override
	public DLFileVersion getDLFileVersion(long fileVersionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileVersionLocalService.getDLFileVersion(fileVersionId);
	}

	/**
	 * Returns the document library file version matching the UUID and group.
	 *
	 * @param uuid the document library file version's UUID
	 * @param groupId the primary key of the group
	 * @return the matching document library file version
	 * @throws PortalException if a matching document library file version could not be found
	 */
	@Override
	public DLFileVersion getDLFileVersionByUuidAndGroupId(
			String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileVersionLocalService.getDLFileVersionByUuidAndGroupId(
			uuid, groupId);
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
	@Override
	public java.util.List<DLFileVersion> getDLFileVersions(int start, int end) {
		return _dlFileVersionLocalService.getDLFileVersions(start, end);
	}

	/**
	 * Returns all the document library file versions matching the UUID and company.
	 *
	 * @param uuid the UUID of the document library file versions
	 * @param companyId the primary key of the company
	 * @return the matching document library file versions, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<DLFileVersion> getDLFileVersionsByUuidAndCompanyId(
		String uuid, long companyId) {

		return _dlFileVersionLocalService.getDLFileVersionsByUuidAndCompanyId(
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
	@Override
	public java.util.List<DLFileVersion> getDLFileVersionsByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFileVersion>
			orderByComparator) {

		return _dlFileVersionLocalService.getDLFileVersionsByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of document library file versions.
	 *
	 * @return the number of document library file versions
	 */
	@Override
	public int getDLFileVersionsCount() {
		return _dlFileVersionLocalService.getDLFileVersionsCount();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _dlFileVersionLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public DLFileVersion getFileVersion(long fileVersionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileVersionLocalService.getFileVersion(fileVersionId);
	}

	@Override
	public DLFileVersion getFileVersion(long fileEntryId, String version)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileVersionLocalService.getFileVersion(fileEntryId, version);
	}

	@Override
	public DLFileVersion getFileVersionByUuidAndGroupId(
			String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileVersionLocalService.getFileVersionByUuidAndGroupId(
			uuid, groupId);
	}

	@Override
	public java.util.List<DLFileVersion> getFileVersions(
		long fileEntryId, int status) {

		return _dlFileVersionLocalService.getFileVersions(fileEntryId, status);
	}

	@Override
	public java.util.List<DLFileVersion> getFileVersions(
		long fileEntryId, int status, int start, int end) {

		return _dlFileVersionLocalService.getFileVersions(
			fileEntryId, status, start, end);
	}

	@Override
	public int getFileVersionsCount(long fileEntryId, int status) {
		return _dlFileVersionLocalService.getFileVersionsCount(
			fileEntryId, status);
	}

	@Override
	public int getFileVersionsCount(long companyId, String storeUUID) {
		return _dlFileVersionLocalService.getFileVersionsCount(
			companyId, storeUUID);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _dlFileVersionLocalService.getIndexableActionableDynamicQuery();
	}

	@Override
	public DLFileVersion getLatestFileVersion(
			long fileEntryId, boolean excludeWorkingCopy)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileVersionLocalService.getLatestFileVersion(
			fileEntryId, excludeWorkingCopy);
	}

	@Override
	public DLFileVersion getLatestFileVersion(long userId, long fileEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileVersionLocalService.getLatestFileVersion(
			userId, fileEntryId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _dlFileVersionLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileVersionLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public void rebuildTree(long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_dlFileVersionLocalService.rebuildTree(companyId);
	}

	@Override
	public void setTreePaths(long folderId, String treePath)
		throws com.liferay.portal.kernel.exception.PortalException {

		_dlFileVersionLocalService.setTreePaths(folderId, treePath);
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
	@Override
	public DLFileVersion updateDLFileVersion(DLFileVersion dlFileVersion) {
		return _dlFileVersionLocalService.updateDLFileVersion(dlFileVersion);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _dlFileVersionLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<DLFileVersion> getCTPersistence() {
		return _dlFileVersionLocalService.getCTPersistence();
	}

	@Override
	public Class<DLFileVersion> getModelClass() {
		return _dlFileVersionLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<DLFileVersion>, R, E>
				updateUnsafeFunction)
		throws E {

		return _dlFileVersionLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public DLFileVersionLocalService getWrappedService() {
		return _dlFileVersionLocalService;
	}

	@Override
	public void setWrappedService(
		DLFileVersionLocalService dlFileVersionLocalService) {

		_dlFileVersionLocalService = dlFileVersionLocalService;
	}

	private DLFileVersionLocalService _dlFileVersionLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:1398958783