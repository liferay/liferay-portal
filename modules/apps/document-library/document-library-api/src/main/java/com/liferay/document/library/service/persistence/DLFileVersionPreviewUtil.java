/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.service.persistence;

import com.liferay.document.library.model.DLFileVersionPreview;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the dl file version preview service. This utility wraps <code>com.liferay.document.library.service.persistence.impl.DLFileVersionPreviewPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see DLFileVersionPreviewPersistence
 * @generated
 */
public class DLFileVersionPreviewUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(
		List<DLFileVersionPreview> dlFileVersionPreviews) {

		getPersistence().cacheResult(dlFileVersionPreviews);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(DLFileVersionPreview dlFileVersionPreview) {
		getPersistence().cacheResult(dlFileVersionPreview);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#clearCache()
	 */
	public static void clearCache() {
		getPersistence().clearCache();
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#clearCache(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void clearCache(DLFileVersionPreview dlFileVersionPreview) {
		getPersistence().clearCache(dlFileVersionPreview);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#countWithDynamicQuery(DynamicQuery)
	 */
	public static long countWithDynamicQuery(DynamicQuery dynamicQuery) {
		return getPersistence().countWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#fetchByPrimaryKeys(Set)
	 */
	public static Map<Serializable, DLFileVersionPreview> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<DLFileVersionPreview> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<DLFileVersionPreview> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<DLFileVersionPreview> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<DLFileVersionPreview> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static DLFileVersionPreview update(
		DLFileVersionPreview dlFileVersionPreview) {

		return getPersistence().update(dlFileVersionPreview);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static DLFileVersionPreview update(
		DLFileVersionPreview dlFileVersionPreview,
		ServiceContext serviceContext) {

		return getPersistence().update(dlFileVersionPreview, serviceContext);
	}

	/**
	 * Returns an ordered range of all the dl file version previews where fileEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.document.library.model.impl.DLFileVersionPreviewModelImpl</code>.
	 * </p>
	 *
	 * @param fileEntryId the file entry ID
	 * @param start the lower bound of the range of dl file version previews
	 * @param end the upper bound of the range of dl file version previews (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching dl file version previews
	 */
	public static List<DLFileVersionPreview> findByFileEntryId(
		long fileEntryId, int start, int end,
		OrderByComparator<DLFileVersionPreview> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByFileEntryId(
			fileEntryId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first dl file version preview in the ordered set where fileEntryId = &#63;.
	 *
	 * @param fileEntryId the file entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching dl file version preview
	 * @throws NoSuchFileVersionPreviewException if a matching dl file version preview could not be found
	 */
	public static DLFileVersionPreview findByFileEntryId_First(
			long fileEntryId,
			OrderByComparator<DLFileVersionPreview> orderByComparator)
		throws com.liferay.document.library.exception.
			NoSuchFileVersionPreviewException {

		return getPersistence().findByFileEntryId_First(
			fileEntryId, orderByComparator);
	}

	/**
	 * Returns the first dl file version preview in the ordered set where fileEntryId = &#63;.
	 *
	 * @param fileEntryId the file entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching dl file version preview, or <code>null</code> if a matching dl file version preview could not be found
	 */
	public static DLFileVersionPreview fetchByFileEntryId_First(
		long fileEntryId,
		OrderByComparator<DLFileVersionPreview> orderByComparator) {

		return getPersistence().fetchByFileEntryId_First(
			fileEntryId, orderByComparator);
	}

	/**
	 * Removes all the dl file version previews where fileEntryId = &#63; from the database.
	 *
	 * @param fileEntryId the file entry ID
	 */
	public static void removeByFileEntryId(long fileEntryId) {
		getPersistence().removeByFileEntryId(fileEntryId);
	}

	/**
	 * Returns the number of dl file version previews where fileEntryId = &#63;.
	 *
	 * @param fileEntryId the file entry ID
	 * @return the number of matching dl file version previews
	 */
	public static int countByFileEntryId(long fileEntryId) {
		return getPersistence().countByFileEntryId(fileEntryId);
	}

	/**
	 * Returns the dl file version preview where fileEntryId = &#63; and fileVersionId = &#63; or throws a <code>NoSuchFileVersionPreviewException</code> if it could not be found.
	 *
	 * @param fileEntryId the file entry ID
	 * @param fileVersionId the file version ID
	 * @return the matching dl file version preview
	 * @throws NoSuchFileVersionPreviewException if a matching dl file version preview could not be found
	 */
	public static DLFileVersionPreview findByF_F(
			long fileEntryId, long fileVersionId)
		throws com.liferay.document.library.exception.
			NoSuchFileVersionPreviewException {

		return getPersistence().findByF_F(fileEntryId, fileVersionId);
	}

	/**
	 * Returns the dl file version preview where fileEntryId = &#63; and fileVersionId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param fileEntryId the file entry ID
	 * @param fileVersionId the file version ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching dl file version preview, or <code>null</code> if a matching dl file version preview could not be found
	 */
	public static DLFileVersionPreview fetchByF_F(
		long fileEntryId, long fileVersionId, boolean useFinderCache) {

		return getPersistence().fetchByF_F(
			fileEntryId, fileVersionId, useFinderCache);
	}

	/**
	 * Removes the dl file version preview where fileEntryId = &#63; and fileVersionId = &#63; from the database.
	 *
	 * @param fileEntryId the file entry ID
	 * @param fileVersionId the file version ID
	 * @return the dl file version preview that was removed
	 */
	public static DLFileVersionPreview removeByF_F(
			long fileEntryId, long fileVersionId)
		throws com.liferay.document.library.exception.
			NoSuchFileVersionPreviewException {

		return getPersistence().removeByF_F(fileEntryId, fileVersionId);
	}

	/**
	 * Returns the number of dl file version previews where fileEntryId = &#63; and fileVersionId = &#63;.
	 *
	 * @param fileEntryId the file entry ID
	 * @param fileVersionId the file version ID
	 * @return the number of matching dl file version previews
	 */
	public static int countByF_F(long fileEntryId, long fileVersionId) {
		return getPersistence().countByF_F(fileEntryId, fileVersionId);
	}

	/**
	 * Returns the dl file version preview where fileEntryId = &#63; and fileVersionId = &#63; and previewStatus = &#63; or throws a <code>NoSuchFileVersionPreviewException</code> if it could not be found.
	 *
	 * @param fileEntryId the file entry ID
	 * @param fileVersionId the file version ID
	 * @param previewStatus the preview status
	 * @return the matching dl file version preview
	 * @throws NoSuchFileVersionPreviewException if a matching dl file version preview could not be found
	 */
	public static DLFileVersionPreview findByF_F_P(
			long fileEntryId, long fileVersionId, int previewStatus)
		throws com.liferay.document.library.exception.
			NoSuchFileVersionPreviewException {

		return getPersistence().findByF_F_P(
			fileEntryId, fileVersionId, previewStatus);
	}

	/**
	 * Returns the dl file version preview where fileEntryId = &#63; and fileVersionId = &#63; and previewStatus = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param fileEntryId the file entry ID
	 * @param fileVersionId the file version ID
	 * @param previewStatus the preview status
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching dl file version preview, or <code>null</code> if a matching dl file version preview could not be found
	 */
	public static DLFileVersionPreview fetchByF_F_P(
		long fileEntryId, long fileVersionId, int previewStatus,
		boolean useFinderCache) {

		return getPersistence().fetchByF_F_P(
			fileEntryId, fileVersionId, previewStatus, useFinderCache);
	}

	/**
	 * Removes the dl file version preview where fileEntryId = &#63; and fileVersionId = &#63; and previewStatus = &#63; from the database.
	 *
	 * @param fileEntryId the file entry ID
	 * @param fileVersionId the file version ID
	 * @param previewStatus the preview status
	 * @return the dl file version preview that was removed
	 */
	public static DLFileVersionPreview removeByF_F_P(
			long fileEntryId, long fileVersionId, int previewStatus)
		throws com.liferay.document.library.exception.
			NoSuchFileVersionPreviewException {

		return getPersistence().removeByF_F_P(
			fileEntryId, fileVersionId, previewStatus);
	}

	/**
	 * Returns the number of dl file version previews where fileEntryId = &#63; and fileVersionId = &#63; and previewStatus = &#63;.
	 *
	 * @param fileEntryId the file entry ID
	 * @param fileVersionId the file version ID
	 * @param previewStatus the preview status
	 * @return the number of matching dl file version previews
	 */
	public static int countByF_F_P(
		long fileEntryId, long fileVersionId, int previewStatus) {

		return getPersistence().countByF_F_P(
			fileEntryId, fileVersionId, previewStatus);
	}

	/**
	 * Creates a new dl file version preview with the primary key. Does not add the dl file version preview to the database.
	 *
	 * @param dlFileVersionPreviewId the primary key for the new dl file version preview
	 * @return the new dl file version preview
	 */
	public static DLFileVersionPreview create(long dlFileVersionPreviewId) {
		return getPersistence().create(dlFileVersionPreviewId);
	}

	/**
	 * Removes the dl file version preview with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param dlFileVersionPreviewId the primary key of the dl file version preview
	 * @return the dl file version preview that was removed
	 * @throws NoSuchFileVersionPreviewException if a dl file version preview with the primary key could not be found
	 */
	public static DLFileVersionPreview remove(long dlFileVersionPreviewId)
		throws com.liferay.document.library.exception.
			NoSuchFileVersionPreviewException {

		return getPersistence().remove(dlFileVersionPreviewId);
	}

	public static DLFileVersionPreview updateImpl(
		DLFileVersionPreview dlFileVersionPreview) {

		return getPersistence().updateImpl(dlFileVersionPreview);
	}

	/**
	 * Returns the dl file version preview with the primary key or throws a <code>NoSuchFileVersionPreviewException</code> if it could not be found.
	 *
	 * @param dlFileVersionPreviewId the primary key of the dl file version preview
	 * @return the dl file version preview
	 * @throws NoSuchFileVersionPreviewException if a dl file version preview with the primary key could not be found
	 */
	public static DLFileVersionPreview findByPrimaryKey(
			long dlFileVersionPreviewId)
		throws com.liferay.document.library.exception.
			NoSuchFileVersionPreviewException {

		return getPersistence().findByPrimaryKey(dlFileVersionPreviewId);
	}

	/**
	 * Returns the dl file version preview with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param dlFileVersionPreviewId the primary key of the dl file version preview
	 * @return the dl file version preview, or <code>null</code> if a dl file version preview with the primary key could not be found
	 */
	public static DLFileVersionPreview fetchByPrimaryKey(
		long dlFileVersionPreviewId) {

		return getPersistence().fetchByPrimaryKey(dlFileVersionPreviewId);
	}

	/**
	 * Returns the dl file version preview where fileEntryId = &#63; and fileVersionId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param fileEntryId the file entry ID
	 * @param fileVersionId the file version ID
	 * @return the matching dl file version preview, or <code>null</code> if a matching dl file version preview could not be found
	 */
	public static DLFileVersionPreview fetchByF_F(
		long fileEntryId, long fileVersionId) {

		return getPersistence().fetchByF_F(fileEntryId, fileVersionId);
	}

	/**
	 * Returns the dl file version preview where fileEntryId = &#63; and fileVersionId = &#63; and previewStatus = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param fileEntryId the file entry ID
	 * @param fileVersionId the file version ID
	 * @param previewStatus the preview status
	 * @return the matching dl file version preview, or <code>null</code> if a matching dl file version preview could not be found
	 */
	public static DLFileVersionPreview fetchByF_F_P(
		long fileEntryId, long fileVersionId, int previewStatus) {

		return getPersistence().fetchByF_F_P(
			fileEntryId, fileVersionId, previewStatus);
	}

	/**
	 * Returns all the dl file version previews where fileEntryId = &#63;.
	 *
	 * @param fileEntryId the file entry ID
	 * @return the matching dl file version previews
	 */
	public static List<DLFileVersionPreview> findByFileEntryId(
		long fileEntryId) {

		return getPersistence().findByFileEntryId(fileEntryId);
	}

	/**
	 * Returns a range of all the dl file version previews where fileEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.document.library.model.impl.DLFileVersionPreviewModelImpl</code>.
	 * </p>
	 *
	 * @param fileEntryId the file entry ID
	 * @param start the lower bound of the range of dl file version previews
	 * @param end the upper bound of the range of dl file version previews (not inclusive)
	 * @return the range of matching dl file version previews
	 */
	public static List<DLFileVersionPreview> findByFileEntryId(
		long fileEntryId, int start, int end) {

		return getPersistence().findByFileEntryId(fileEntryId, start, end);
	}

	/**
	 * Returns an ordered range of all the dl file version previews where fileEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.document.library.model.impl.DLFileVersionPreviewModelImpl</code>.
	 * </p>
	 *
	 * @param fileEntryId the file entry ID
	 * @param start the lower bound of the range of dl file version previews
	 * @param end the upper bound of the range of dl file version previews (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching dl file version previews
	 */
	public static List<DLFileVersionPreview> findByFileEntryId(
		long fileEntryId, int start, int end,
		OrderByComparator<DLFileVersionPreview> orderByComparator) {

		return getPersistence().findByFileEntryId(
			fileEntryId, start, end, orderByComparator);
	}

	public static DLFileVersionPreviewPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(
		DLFileVersionPreviewPersistence persistence) {

		_persistence = persistence;
	}

	private static volatile DLFileVersionPreviewPersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:749266777