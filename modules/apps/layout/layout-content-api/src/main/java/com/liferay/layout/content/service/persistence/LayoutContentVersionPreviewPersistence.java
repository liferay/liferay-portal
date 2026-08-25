/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.service.persistence;

import com.liferay.layout.content.exception.NoSuchLayoutContentVersionPreviewException;
import com.liferay.layout.content.model.LayoutContentVersionPreview;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the layout content version preview service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Lourdes Fernández Besada
 * @see LayoutContentVersionPreviewUtil
 * @generated
 */
@ProviderType
public interface LayoutContentVersionPreviewPersistence
	extends BasePersistence<LayoutContentVersionPreview> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link LayoutContentVersionPreviewUtil} to access the layout content version preview persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns an ordered range of all the layout content version previews where layoutContentVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.content.model.impl.LayoutContentVersionPreviewModelImpl</code>.
	 * </p>
	 *
	 * @param layoutContentVersionId the layout content version ID
	 * @param start the lower bound of the range of layout content version previews
	 * @param end the upper bound of the range of layout content version previews (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout content version previews
	 */
	public java.util.List<LayoutContentVersionPreview>
		findByLayoutContentVersionId(
			long layoutContentVersionId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutContentVersionPreview> orderByComparator,
			boolean useFinderCache);

	/**
	 * Returns the first layout content version preview in the ordered set where layoutContentVersionId = &#63;.
	 *
	 * @param layoutContentVersionId the layout content version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout content version preview
	 * @throws NoSuchLayoutContentVersionPreviewException if a matching layout content version preview could not be found
	 */
	public LayoutContentVersionPreview findByLayoutContentVersionId_First(
			long layoutContentVersionId,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutContentVersionPreview> orderByComparator)
		throws NoSuchLayoutContentVersionPreviewException;

	/**
	 * Returns the first layout content version preview in the ordered set where layoutContentVersionId = &#63;.
	 *
	 * @param layoutContentVersionId the layout content version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout content version preview, or <code>null</code> if a matching layout content version preview could not be found
	 */
	public LayoutContentVersionPreview fetchByLayoutContentVersionId_First(
		long layoutContentVersionId,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutContentVersionPreview> orderByComparator);

	/**
	 * Removes all the layout content version previews where layoutContentVersionId = &#63; from the database.
	 *
	 * @param layoutContentVersionId the layout content version ID
	 */
	public void removeByLayoutContentVersionId(long layoutContentVersionId);

	/**
	 * Returns the number of layout content version previews where layoutContentVersionId = &#63;.
	 *
	 * @param layoutContentVersionId the layout content version ID
	 * @return the number of matching layout content version previews
	 */
	public int countByLayoutContentVersionId(long layoutContentVersionId);

	/**
	 * Returns an ordered range of all the layout content version previews where layoutContentVersionId = &#63; and segmentsExperienceERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.content.model.impl.LayoutContentVersionPreviewModelImpl</code>.
	 * </p>
	 *
	 * @param layoutContentVersionId the layout content version ID
	 * @param segmentsExperienceERC the segments experience erc
	 * @param start the lower bound of the range of layout content version previews
	 * @param end the upper bound of the range of layout content version previews (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout content version previews
	 */
	public java.util.List<LayoutContentVersionPreview> findByLCVI_SEERC(
		long layoutContentVersionId, String segmentsExperienceERC, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutContentVersionPreview> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first layout content version preview in the ordered set where layoutContentVersionId = &#63; and segmentsExperienceERC = &#63;.
	 *
	 * @param layoutContentVersionId the layout content version ID
	 * @param segmentsExperienceERC the segments experience erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout content version preview
	 * @throws NoSuchLayoutContentVersionPreviewException if a matching layout content version preview could not be found
	 */
	public LayoutContentVersionPreview findByLCVI_SEERC_First(
			long layoutContentVersionId, String segmentsExperienceERC,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutContentVersionPreview> orderByComparator)
		throws NoSuchLayoutContentVersionPreviewException;

	/**
	 * Returns the first layout content version preview in the ordered set where layoutContentVersionId = &#63; and segmentsExperienceERC = &#63;.
	 *
	 * @param layoutContentVersionId the layout content version ID
	 * @param segmentsExperienceERC the segments experience erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout content version preview, or <code>null</code> if a matching layout content version preview could not be found
	 */
	public LayoutContentVersionPreview fetchByLCVI_SEERC_First(
		long layoutContentVersionId, String segmentsExperienceERC,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutContentVersionPreview> orderByComparator);

	/**
	 * Removes all the layout content version previews where layoutContentVersionId = &#63; and segmentsExperienceERC = &#63; from the database.
	 *
	 * @param layoutContentVersionId the layout content version ID
	 * @param segmentsExperienceERC the segments experience erc
	 */
	public void removeByLCVI_SEERC(
		long layoutContentVersionId, String segmentsExperienceERC);

	/**
	 * Returns the number of layout content version previews where layoutContentVersionId = &#63; and segmentsExperienceERC = &#63;.
	 *
	 * @param layoutContentVersionId the layout content version ID
	 * @param segmentsExperienceERC the segments experience erc
	 * @return the number of matching layout content version previews
	 */
	public int countByLCVI_SEERC(
		long layoutContentVersionId, String segmentsExperienceERC);

	/**
	 * Returns the layout content version preview where layoutContentVersionId = &#63; and languageId = &#63; and segmentsExperienceERC = &#63; or throws a <code>NoSuchLayoutContentVersionPreviewException</code> if it could not be found.
	 *
	 * @param layoutContentVersionId the layout content version ID
	 * @param languageId the language ID
	 * @param segmentsExperienceERC the segments experience erc
	 * @return the matching layout content version preview
	 * @throws NoSuchLayoutContentVersionPreviewException if a matching layout content version preview could not be found
	 */
	public LayoutContentVersionPreview findByLCVI_L_SEERC(
			long layoutContentVersionId, String languageId,
			String segmentsExperienceERC)
		throws NoSuchLayoutContentVersionPreviewException;

	/**
	 * Returns the layout content version preview where layoutContentVersionId = &#63; and languageId = &#63; and segmentsExperienceERC = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param layoutContentVersionId the layout content version ID
	 * @param languageId the language ID
	 * @param segmentsExperienceERC the segments experience erc
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout content version preview, or <code>null</code> if a matching layout content version preview could not be found
	 */
	public LayoutContentVersionPreview fetchByLCVI_L_SEERC(
		long layoutContentVersionId, String languageId,
		String segmentsExperienceERC, boolean useFinderCache);

	/**
	 * Removes the layout content version preview where layoutContentVersionId = &#63; and languageId = &#63; and segmentsExperienceERC = &#63; from the database.
	 *
	 * @param layoutContentVersionId the layout content version ID
	 * @param languageId the language ID
	 * @param segmentsExperienceERC the segments experience erc
	 * @return the layout content version preview that was removed
	 */
	public LayoutContentVersionPreview removeByLCVI_L_SEERC(
			long layoutContentVersionId, String languageId,
			String segmentsExperienceERC)
		throws NoSuchLayoutContentVersionPreviewException;

	/**
	 * Returns the number of layout content version previews where layoutContentVersionId = &#63; and languageId = &#63; and segmentsExperienceERC = &#63;.
	 *
	 * @param layoutContentVersionId the layout content version ID
	 * @param languageId the language ID
	 * @param segmentsExperienceERC the segments experience erc
	 * @return the number of matching layout content version previews
	 */
	public int countByLCVI_L_SEERC(
		long layoutContentVersionId, String languageId,
		String segmentsExperienceERC);

	/**
	 * Creates a new layout content version preview with the primary key. Does not add the layout content version preview to the database.
	 *
	 * @param layoutContentVersionPreviewId the primary key for the new layout content version preview
	 * @return the new layout content version preview
	 */
	public LayoutContentVersionPreview create(
		long layoutContentVersionPreviewId);

	/**
	 * Removes the layout content version preview with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param layoutContentVersionPreviewId the primary key of the layout content version preview
	 * @return the layout content version preview that was removed
	 * @throws NoSuchLayoutContentVersionPreviewException if a layout content version preview with the primary key could not be found
	 */
	public LayoutContentVersionPreview remove(
			long layoutContentVersionPreviewId)
		throws NoSuchLayoutContentVersionPreviewException;

	public LayoutContentVersionPreview updateImpl(
		LayoutContentVersionPreview layoutContentVersionPreview);

	/**
	 * Returns the layout content version preview with the primary key or throws a <code>NoSuchLayoutContentVersionPreviewException</code> if it could not be found.
	 *
	 * @param layoutContentVersionPreviewId the primary key of the layout content version preview
	 * @return the layout content version preview
	 * @throws NoSuchLayoutContentVersionPreviewException if a layout content version preview with the primary key could not be found
	 */
	public LayoutContentVersionPreview findByPrimaryKey(
			long layoutContentVersionPreviewId)
		throws NoSuchLayoutContentVersionPreviewException;

	/**
	 * Returns the layout content version preview with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param layoutContentVersionPreviewId the primary key of the layout content version preview
	 * @return the layout content version preview, or <code>null</code> if a layout content version preview with the primary key could not be found
	 */
	public LayoutContentVersionPreview fetchByPrimaryKey(
		long layoutContentVersionPreviewId);

	/**
	 * Returns the layout content version preview where layoutContentVersionId = &#63; and languageId = &#63; and segmentsExperienceERC = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param layoutContentVersionId the layout content version ID
	 * @param languageId the language ID
	 * @param segmentsExperienceERC the segments experience erc
	 * @return the matching layout content version preview, or <code>null</code> if a matching layout content version preview could not be found
	 */
	public default LayoutContentVersionPreview fetchByLCVI_L_SEERC(
		long layoutContentVersionId, String languageId,
		String segmentsExperienceERC) {

		return fetchByLCVI_L_SEERC(
			layoutContentVersionId, languageId, segmentsExperienceERC, true);
	}

	/**
	 * Returns all the layout content version previews where layoutContentVersionId = &#63;.
	 *
	 * @param layoutContentVersionId the layout content version ID
	 * @return the matching layout content version previews
	 */
	public default java.util.List<LayoutContentVersionPreview>
		findByLayoutContentVersionId(long layoutContentVersionId) {

		return findByLayoutContentVersionId(
			layoutContentVersionId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the layout content version previews where layoutContentVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.content.model.impl.LayoutContentVersionPreviewModelImpl</code>.
	 * </p>
	 *
	 * @param layoutContentVersionId the layout content version ID
	 * @param start the lower bound of the range of layout content version previews
	 * @param end the upper bound of the range of layout content version previews (not inclusive)
	 * @return the range of matching layout content version previews
	 */
	public default java.util.List<LayoutContentVersionPreview>
		findByLayoutContentVersionId(
			long layoutContentVersionId, int start, int end) {

		return findByLayoutContentVersionId(
			layoutContentVersionId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the layout content version previews where layoutContentVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.content.model.impl.LayoutContentVersionPreviewModelImpl</code>.
	 * </p>
	 *
	 * @param layoutContentVersionId the layout content version ID
	 * @param start the lower bound of the range of layout content version previews
	 * @param end the upper bound of the range of layout content version previews (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout content version previews
	 */
	public default java.util.List<LayoutContentVersionPreview>
		findByLayoutContentVersionId(
			long layoutContentVersionId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutContentVersionPreview> orderByComparator) {

		return findByLayoutContentVersionId(
			layoutContentVersionId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the layout content version previews where layoutContentVersionId = &#63; and segmentsExperienceERC = &#63;.
	 *
	 * @param layoutContentVersionId the layout content version ID
	 * @param segmentsExperienceERC the segments experience erc
	 * @return the matching layout content version previews
	 */
	public default java.util.List<LayoutContentVersionPreview> findByLCVI_SEERC(
		long layoutContentVersionId, String segmentsExperienceERC) {

		return findByLCVI_SEERC(
			layoutContentVersionId, segmentsExperienceERC,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the layout content version previews where layoutContentVersionId = &#63; and segmentsExperienceERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.content.model.impl.LayoutContentVersionPreviewModelImpl</code>.
	 * </p>
	 *
	 * @param layoutContentVersionId the layout content version ID
	 * @param segmentsExperienceERC the segments experience erc
	 * @param start the lower bound of the range of layout content version previews
	 * @param end the upper bound of the range of layout content version previews (not inclusive)
	 * @return the range of matching layout content version previews
	 */
	public default java.util.List<LayoutContentVersionPreview> findByLCVI_SEERC(
		long layoutContentVersionId, String segmentsExperienceERC, int start,
		int end) {

		return findByLCVI_SEERC(
			layoutContentVersionId, segmentsExperienceERC, start, end, null,
			true);
	}

	/**
	 * Returns an ordered range of all the layout content version previews where layoutContentVersionId = &#63; and segmentsExperienceERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.content.model.impl.LayoutContentVersionPreviewModelImpl</code>.
	 * </p>
	 *
	 * @param layoutContentVersionId the layout content version ID
	 * @param segmentsExperienceERC the segments experience erc
	 * @param start the lower bound of the range of layout content version previews
	 * @param end the upper bound of the range of layout content version previews (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout content version previews
	 */
	public default java.util.List<LayoutContentVersionPreview> findByLCVI_SEERC(
		long layoutContentVersionId, String segmentsExperienceERC, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutContentVersionPreview> orderByComparator) {

		return findByLCVI_SEERC(
			layoutContentVersionId, segmentsExperienceERC, start, end,
			orderByComparator, true);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:7059803