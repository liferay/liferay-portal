/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.service.persistence;

import com.liferay.document.library.exception.NoSuchFileVersionPreviewException;
import com.liferay.document.library.model.DLFileVersionPreview;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the dl file version preview service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see DLFileVersionPreviewUtil
 * @generated
 */
@ProviderType
public interface DLFileVersionPreviewPersistence
	extends BasePersistence<DLFileVersionPreview>,
			CTPersistence<DLFileVersionPreview> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link DLFileVersionPreviewUtil} to access the dl file version preview persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

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
	public java.util.List<DLFileVersionPreview> findByFileEntryId(
		long fileEntryId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFileVersionPreview>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first dl file version preview in the ordered set where fileEntryId = &#63;.
	 *
	 * @param fileEntryId the file entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching dl file version preview
	 * @throws NoSuchFileVersionPreviewException if a matching dl file version preview could not be found
	 */
	public DLFileVersionPreview findByFileEntryId_First(
			long fileEntryId,
			com.liferay.portal.kernel.util.OrderByComparator
				<DLFileVersionPreview> orderByComparator)
		throws NoSuchFileVersionPreviewException;

	/**
	 * Returns the first dl file version preview in the ordered set where fileEntryId = &#63;.
	 *
	 * @param fileEntryId the file entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching dl file version preview, or <code>null</code> if a matching dl file version preview could not be found
	 */
	public DLFileVersionPreview fetchByFileEntryId_First(
		long fileEntryId,
		com.liferay.portal.kernel.util.OrderByComparator<DLFileVersionPreview>
			orderByComparator);

	/**
	 * Removes all the dl file version previews where fileEntryId = &#63; from the database.
	 *
	 * @param fileEntryId the file entry ID
	 */
	public void removeByFileEntryId(long fileEntryId);

	/**
	 * Returns the number of dl file version previews where fileEntryId = &#63;.
	 *
	 * @param fileEntryId the file entry ID
	 * @return the number of matching dl file version previews
	 */
	public int countByFileEntryId(long fileEntryId);

	/**
	 * Returns the dl file version preview where fileEntryId = &#63; and fileVersionId = &#63; or throws a <code>NoSuchFileVersionPreviewException</code> if it could not be found.
	 *
	 * @param fileEntryId the file entry ID
	 * @param fileVersionId the file version ID
	 * @return the matching dl file version preview
	 * @throws NoSuchFileVersionPreviewException if a matching dl file version preview could not be found
	 */
	public DLFileVersionPreview findByF_F(long fileEntryId, long fileVersionId)
		throws NoSuchFileVersionPreviewException;

	/**
	 * Returns the dl file version preview where fileEntryId = &#63; and fileVersionId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param fileEntryId the file entry ID
	 * @param fileVersionId the file version ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching dl file version preview, or <code>null</code> if a matching dl file version preview could not be found
	 */
	public DLFileVersionPreview fetchByF_F(
		long fileEntryId, long fileVersionId, boolean useFinderCache);

	/**
	 * Removes the dl file version preview where fileEntryId = &#63; and fileVersionId = &#63; from the database.
	 *
	 * @param fileEntryId the file entry ID
	 * @param fileVersionId the file version ID
	 * @return the dl file version preview that was removed
	 */
	public DLFileVersionPreview removeByF_F(
			long fileEntryId, long fileVersionId)
		throws NoSuchFileVersionPreviewException;

	/**
	 * Returns the number of dl file version previews where fileEntryId = &#63; and fileVersionId = &#63;.
	 *
	 * @param fileEntryId the file entry ID
	 * @param fileVersionId the file version ID
	 * @return the number of matching dl file version previews
	 */
	public int countByF_F(long fileEntryId, long fileVersionId);

	/**
	 * Returns the dl file version preview where fileEntryId = &#63; and fileVersionId = &#63; and previewStatus = &#63; or throws a <code>NoSuchFileVersionPreviewException</code> if it could not be found.
	 *
	 * @param fileEntryId the file entry ID
	 * @param fileVersionId the file version ID
	 * @param previewStatus the preview status
	 * @return the matching dl file version preview
	 * @throws NoSuchFileVersionPreviewException if a matching dl file version preview could not be found
	 */
	public DLFileVersionPreview findByF_F_P(
			long fileEntryId, long fileVersionId, int previewStatus)
		throws NoSuchFileVersionPreviewException;

	/**
	 * Returns the dl file version preview where fileEntryId = &#63; and fileVersionId = &#63; and previewStatus = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param fileEntryId the file entry ID
	 * @param fileVersionId the file version ID
	 * @param previewStatus the preview status
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching dl file version preview, or <code>null</code> if a matching dl file version preview could not be found
	 */
	public DLFileVersionPreview fetchByF_F_P(
		long fileEntryId, long fileVersionId, int previewStatus,
		boolean useFinderCache);

	/**
	 * Removes the dl file version preview where fileEntryId = &#63; and fileVersionId = &#63; and previewStatus = &#63; from the database.
	 *
	 * @param fileEntryId the file entry ID
	 * @param fileVersionId the file version ID
	 * @param previewStatus the preview status
	 * @return the dl file version preview that was removed
	 */
	public DLFileVersionPreview removeByF_F_P(
			long fileEntryId, long fileVersionId, int previewStatus)
		throws NoSuchFileVersionPreviewException;

	/**
	 * Returns the number of dl file version previews where fileEntryId = &#63; and fileVersionId = &#63; and previewStatus = &#63;.
	 *
	 * @param fileEntryId the file entry ID
	 * @param fileVersionId the file version ID
	 * @param previewStatus the preview status
	 * @return the number of matching dl file version previews
	 */
	public int countByF_F_P(
		long fileEntryId, long fileVersionId, int previewStatus);

	/**
	 * Creates a new dl file version preview with the primary key. Does not add the dl file version preview to the database.
	 *
	 * @param dlFileVersionPreviewId the primary key for the new dl file version preview
	 * @return the new dl file version preview
	 */
	public DLFileVersionPreview create(long dlFileVersionPreviewId);

	/**
	 * Removes the dl file version preview with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param dlFileVersionPreviewId the primary key of the dl file version preview
	 * @return the dl file version preview that was removed
	 * @throws NoSuchFileVersionPreviewException if a dl file version preview with the primary key could not be found
	 */
	public DLFileVersionPreview remove(long dlFileVersionPreviewId)
		throws NoSuchFileVersionPreviewException;

	public DLFileVersionPreview updateImpl(
		DLFileVersionPreview dlFileVersionPreview);

	/**
	 * Returns the dl file version preview with the primary key or throws a <code>NoSuchFileVersionPreviewException</code> if it could not be found.
	 *
	 * @param dlFileVersionPreviewId the primary key of the dl file version preview
	 * @return the dl file version preview
	 * @throws NoSuchFileVersionPreviewException if a dl file version preview with the primary key could not be found
	 */
	public DLFileVersionPreview findByPrimaryKey(long dlFileVersionPreviewId)
		throws NoSuchFileVersionPreviewException;

	/**
	 * Returns the dl file version preview with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param dlFileVersionPreviewId the primary key of the dl file version preview
	 * @return the dl file version preview, or <code>null</code> if a dl file version preview with the primary key could not be found
	 */
	public DLFileVersionPreview fetchByPrimaryKey(long dlFileVersionPreviewId);

	/**
	 * Returns the dl file version preview where fileEntryId = &#63; and fileVersionId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param fileEntryId the file entry ID
	 * @param fileVersionId the file version ID
	 * @return the matching dl file version preview, or <code>null</code> if a matching dl file version preview could not be found
	 */
	public default DLFileVersionPreview fetchByF_F(
		long fileEntryId, long fileVersionId) {

		return fetchByF_F(fileEntryId, fileVersionId, true);
	}

	/**
	 * Returns the dl file version preview where fileEntryId = &#63; and fileVersionId = &#63; and previewStatus = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param fileEntryId the file entry ID
	 * @param fileVersionId the file version ID
	 * @param previewStatus the preview status
	 * @return the matching dl file version preview, or <code>null</code> if a matching dl file version preview could not be found
	 */
	public default DLFileVersionPreview fetchByF_F_P(
		long fileEntryId, long fileVersionId, int previewStatus) {

		return fetchByF_F_P(fileEntryId, fileVersionId, previewStatus, true);
	}

	/**
	 * Returns all the dl file version previews where fileEntryId = &#63;.
	 *
	 * @param fileEntryId the file entry ID
	 * @return the matching dl file version previews
	 */
	public default java.util.List<DLFileVersionPreview> findByFileEntryId(
		long fileEntryId) {

		return findByFileEntryId(
			fileEntryId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
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
	public default java.util.List<DLFileVersionPreview> findByFileEntryId(
		long fileEntryId, int start, int end) {

		return findByFileEntryId(fileEntryId, start, end, null, true);
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
	public default java.util.List<DLFileVersionPreview> findByFileEntryId(
		long fileEntryId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFileVersionPreview>
			orderByComparator) {

		return findByFileEntryId(
			fileEntryId, start, end, orderByComparator, true);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1591086499