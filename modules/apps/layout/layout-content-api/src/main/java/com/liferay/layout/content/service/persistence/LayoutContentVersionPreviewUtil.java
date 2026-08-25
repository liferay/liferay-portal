/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.service.persistence;

import com.liferay.layout.content.model.LayoutContentVersionPreview;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the layout content version preview service. This utility wraps <code>com.liferay.layout.content.service.persistence.impl.LayoutContentVersionPreviewPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Lourdes Fernández Besada
 * @see LayoutContentVersionPreviewPersistence
 * @generated
 */
public class LayoutContentVersionPreviewUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(
		List<LayoutContentVersionPreview> layoutContentVersionPreviews) {

		getPersistence().cacheResult(layoutContentVersionPreviews);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(
		LayoutContentVersionPreview layoutContentVersionPreview) {

		getPersistence().cacheResult(layoutContentVersionPreview);
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
	public static void clearCache(
		LayoutContentVersionPreview layoutContentVersionPreview) {

		getPersistence().clearCache(layoutContentVersionPreview);
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
	public static Map<Serializable, LayoutContentVersionPreview>
		fetchByPrimaryKeys(Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<LayoutContentVersionPreview> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<LayoutContentVersionPreview> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<LayoutContentVersionPreview> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<LayoutContentVersionPreview> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static LayoutContentVersionPreview update(
		LayoutContentVersionPreview layoutContentVersionPreview) {

		return getPersistence().update(layoutContentVersionPreview);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static LayoutContentVersionPreview update(
		LayoutContentVersionPreview layoutContentVersionPreview,
		ServiceContext serviceContext) {

		return getPersistence().update(
			layoutContentVersionPreview, serviceContext);
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
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout content version previews
	 */
	public static List<LayoutContentVersionPreview>
		findByLayoutContentVersionId(
			long layoutContentVersionId, int start, int end,
			OrderByComparator<LayoutContentVersionPreview> orderByComparator,
			boolean useFinderCache) {

		return getPersistence().findByLayoutContentVersionId(
			layoutContentVersionId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first layout content version preview in the ordered set where layoutContentVersionId = &#63;.
	 *
	 * @param layoutContentVersionId the layout content version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout content version preview
	 * @throws NoSuchLayoutContentVersionPreviewException if a matching layout content version preview could not be found
	 */
	public static LayoutContentVersionPreview
			findByLayoutContentVersionId_First(
				long layoutContentVersionId,
				OrderByComparator<LayoutContentVersionPreview>
					orderByComparator)
		throws com.liferay.layout.content.exception.
			NoSuchLayoutContentVersionPreviewException {

		return getPersistence().findByLayoutContentVersionId_First(
			layoutContentVersionId, orderByComparator);
	}

	/**
	 * Returns the first layout content version preview in the ordered set where layoutContentVersionId = &#63;.
	 *
	 * @param layoutContentVersionId the layout content version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout content version preview, or <code>null</code> if a matching layout content version preview could not be found
	 */
	public static LayoutContentVersionPreview
		fetchByLayoutContentVersionId_First(
			long layoutContentVersionId,
			OrderByComparator<LayoutContentVersionPreview> orderByComparator) {

		return getPersistence().fetchByLayoutContentVersionId_First(
			layoutContentVersionId, orderByComparator);
	}

	/**
	 * Removes all the layout content version previews where layoutContentVersionId = &#63; from the database.
	 *
	 * @param layoutContentVersionId the layout content version ID
	 */
	public static void removeByLayoutContentVersionId(
		long layoutContentVersionId) {

		getPersistence().removeByLayoutContentVersionId(layoutContentVersionId);
	}

	/**
	 * Returns the number of layout content version previews where layoutContentVersionId = &#63;.
	 *
	 * @param layoutContentVersionId the layout content version ID
	 * @return the number of matching layout content version previews
	 */
	public static int countByLayoutContentVersionId(
		long layoutContentVersionId) {

		return getPersistence().countByLayoutContentVersionId(
			layoutContentVersionId);
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
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout content version previews
	 */
	public static List<LayoutContentVersionPreview> findByLCVI_SEERC(
		long layoutContentVersionId, String segmentsExperienceERC, int start,
		int end,
		OrderByComparator<LayoutContentVersionPreview> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByLCVI_SEERC(
			layoutContentVersionId, segmentsExperienceERC, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout content version preview in the ordered set where layoutContentVersionId = &#63; and segmentsExperienceERC = &#63;.
	 *
	 * @param layoutContentVersionId the layout content version ID
	 * @param segmentsExperienceERC the segments experience erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout content version preview
	 * @throws NoSuchLayoutContentVersionPreviewException if a matching layout content version preview could not be found
	 */
	public static LayoutContentVersionPreview findByLCVI_SEERC_First(
			long layoutContentVersionId, String segmentsExperienceERC,
			OrderByComparator<LayoutContentVersionPreview> orderByComparator)
		throws com.liferay.layout.content.exception.
			NoSuchLayoutContentVersionPreviewException {

		return getPersistence().findByLCVI_SEERC_First(
			layoutContentVersionId, segmentsExperienceERC, orderByComparator);
	}

	/**
	 * Returns the first layout content version preview in the ordered set where layoutContentVersionId = &#63; and segmentsExperienceERC = &#63;.
	 *
	 * @param layoutContentVersionId the layout content version ID
	 * @param segmentsExperienceERC the segments experience erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout content version preview, or <code>null</code> if a matching layout content version preview could not be found
	 */
	public static LayoutContentVersionPreview fetchByLCVI_SEERC_First(
		long layoutContentVersionId, String segmentsExperienceERC,
		OrderByComparator<LayoutContentVersionPreview> orderByComparator) {

		return getPersistence().fetchByLCVI_SEERC_First(
			layoutContentVersionId, segmentsExperienceERC, orderByComparator);
	}

	/**
	 * Removes all the layout content version previews where layoutContentVersionId = &#63; and segmentsExperienceERC = &#63; from the database.
	 *
	 * @param layoutContentVersionId the layout content version ID
	 * @param segmentsExperienceERC the segments experience erc
	 */
	public static void removeByLCVI_SEERC(
		long layoutContentVersionId, String segmentsExperienceERC) {

		getPersistence().removeByLCVI_SEERC(
			layoutContentVersionId, segmentsExperienceERC);
	}

	/**
	 * Returns the number of layout content version previews where layoutContentVersionId = &#63; and segmentsExperienceERC = &#63;.
	 *
	 * @param layoutContentVersionId the layout content version ID
	 * @param segmentsExperienceERC the segments experience erc
	 * @return the number of matching layout content version previews
	 */
	public static int countByLCVI_SEERC(
		long layoutContentVersionId, String segmentsExperienceERC) {

		return getPersistence().countByLCVI_SEERC(
			layoutContentVersionId, segmentsExperienceERC);
	}

	/**
	 * Returns the layout content version preview where layoutContentVersionId = &#63; and languageId = &#63; and segmentsExperienceERC = &#63; or throws a <code>NoSuchLayoutContentVersionPreviewException</code> if it could not be found.
	 *
	 * @param layoutContentVersionId the layout content version ID
	 * @param languageId the language ID
	 * @param segmentsExperienceERC the segments experience erc
	 * @return the matching layout content version preview
	 * @throws NoSuchLayoutContentVersionPreviewException if a matching layout content version preview could not be found
	 */
	public static LayoutContentVersionPreview findByLCVI_L_SEERC(
			long layoutContentVersionId, String languageId,
			String segmentsExperienceERC)
		throws com.liferay.layout.content.exception.
			NoSuchLayoutContentVersionPreviewException {

		return getPersistence().findByLCVI_L_SEERC(
			layoutContentVersionId, languageId, segmentsExperienceERC);
	}

	/**
	 * Returns the layout content version preview where layoutContentVersionId = &#63; and languageId = &#63; and segmentsExperienceERC = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param layoutContentVersionId the layout content version ID
	 * @param languageId the language ID
	 * @param segmentsExperienceERC the segments experience erc
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout content version preview, or <code>null</code> if a matching layout content version preview could not be found
	 */
	public static LayoutContentVersionPreview fetchByLCVI_L_SEERC(
		long layoutContentVersionId, String languageId,
		String segmentsExperienceERC, boolean useFinderCache) {

		return getPersistence().fetchByLCVI_L_SEERC(
			layoutContentVersionId, languageId, segmentsExperienceERC,
			useFinderCache);
	}

	/**
	 * Removes the layout content version preview where layoutContentVersionId = &#63; and languageId = &#63; and segmentsExperienceERC = &#63; from the database.
	 *
	 * @param layoutContentVersionId the layout content version ID
	 * @param languageId the language ID
	 * @param segmentsExperienceERC the segments experience erc
	 * @return the layout content version preview that was removed
	 */
	public static LayoutContentVersionPreview removeByLCVI_L_SEERC(
			long layoutContentVersionId, String languageId,
			String segmentsExperienceERC)
		throws com.liferay.layout.content.exception.
			NoSuchLayoutContentVersionPreviewException {

		return getPersistence().removeByLCVI_L_SEERC(
			layoutContentVersionId, languageId, segmentsExperienceERC);
	}

	/**
	 * Returns the number of layout content version previews where layoutContentVersionId = &#63; and languageId = &#63; and segmentsExperienceERC = &#63;.
	 *
	 * @param layoutContentVersionId the layout content version ID
	 * @param languageId the language ID
	 * @param segmentsExperienceERC the segments experience erc
	 * @return the number of matching layout content version previews
	 */
	public static int countByLCVI_L_SEERC(
		long layoutContentVersionId, String languageId,
		String segmentsExperienceERC) {

		return getPersistence().countByLCVI_L_SEERC(
			layoutContentVersionId, languageId, segmentsExperienceERC);
	}

	/**
	 * Creates a new layout content version preview with the primary key. Does not add the layout content version preview to the database.
	 *
	 * @param layoutContentVersionPreviewId the primary key for the new layout content version preview
	 * @return the new layout content version preview
	 */
	public static LayoutContentVersionPreview create(
		long layoutContentVersionPreviewId) {

		return getPersistence().create(layoutContentVersionPreviewId);
	}

	/**
	 * Removes the layout content version preview with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param layoutContentVersionPreviewId the primary key of the layout content version preview
	 * @return the layout content version preview that was removed
	 * @throws NoSuchLayoutContentVersionPreviewException if a layout content version preview with the primary key could not be found
	 */
	public static LayoutContentVersionPreview remove(
			long layoutContentVersionPreviewId)
		throws com.liferay.layout.content.exception.
			NoSuchLayoutContentVersionPreviewException {

		return getPersistence().remove(layoutContentVersionPreviewId);
	}

	public static LayoutContentVersionPreview updateImpl(
		LayoutContentVersionPreview layoutContentVersionPreview) {

		return getPersistence().updateImpl(layoutContentVersionPreview);
	}

	/**
	 * Returns the layout content version preview with the primary key or throws a <code>NoSuchLayoutContentVersionPreviewException</code> if it could not be found.
	 *
	 * @param layoutContentVersionPreviewId the primary key of the layout content version preview
	 * @return the layout content version preview
	 * @throws NoSuchLayoutContentVersionPreviewException if a layout content version preview with the primary key could not be found
	 */
	public static LayoutContentVersionPreview findByPrimaryKey(
			long layoutContentVersionPreviewId)
		throws com.liferay.layout.content.exception.
			NoSuchLayoutContentVersionPreviewException {

		return getPersistence().findByPrimaryKey(layoutContentVersionPreviewId);
	}

	/**
	 * Returns the layout content version preview with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param layoutContentVersionPreviewId the primary key of the layout content version preview
	 * @return the layout content version preview, or <code>null</code> if a layout content version preview with the primary key could not be found
	 */
	public static LayoutContentVersionPreview fetchByPrimaryKey(
		long layoutContentVersionPreviewId) {

		return getPersistence().fetchByPrimaryKey(
			layoutContentVersionPreviewId);
	}

	/**
	 * Returns the layout content version preview where layoutContentVersionId = &#63; and languageId = &#63; and segmentsExperienceERC = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param layoutContentVersionId the layout content version ID
	 * @param languageId the language ID
	 * @param segmentsExperienceERC the segments experience erc
	 * @return the matching layout content version preview, or <code>null</code> if a matching layout content version preview could not be found
	 */
	public static LayoutContentVersionPreview fetchByLCVI_L_SEERC(
		long layoutContentVersionId, String languageId,
		String segmentsExperienceERC) {

		return getPersistence().fetchByLCVI_L_SEERC(
			layoutContentVersionId, languageId, segmentsExperienceERC);
	}

	/**
	 * Returns all the layout content version previews where layoutContentVersionId = &#63;.
	 *
	 * @param layoutContentVersionId the layout content version ID
	 * @return the matching layout content version previews
	 */
	public static List<LayoutContentVersionPreview>
		findByLayoutContentVersionId(long layoutContentVersionId) {

		return getPersistence().findByLayoutContentVersionId(
			layoutContentVersionId);
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
	public static List<LayoutContentVersionPreview>
		findByLayoutContentVersionId(
			long layoutContentVersionId, int start, int end) {

		return getPersistence().findByLayoutContentVersionId(
			layoutContentVersionId, start, end);
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
	public static List<LayoutContentVersionPreview>
		findByLayoutContentVersionId(
			long layoutContentVersionId, int start, int end,
			OrderByComparator<LayoutContentVersionPreview> orderByComparator) {

		return getPersistence().findByLayoutContentVersionId(
			layoutContentVersionId, start, end, orderByComparator);
	}

	/**
	 * Returns all the layout content version previews where layoutContentVersionId = &#63; and segmentsExperienceERC = &#63;.
	 *
	 * @param layoutContentVersionId the layout content version ID
	 * @param segmentsExperienceERC the segments experience erc
	 * @return the matching layout content version previews
	 */
	public static List<LayoutContentVersionPreview> findByLCVI_SEERC(
		long layoutContentVersionId, String segmentsExperienceERC) {

		return getPersistence().findByLCVI_SEERC(
			layoutContentVersionId, segmentsExperienceERC);
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
	public static List<LayoutContentVersionPreview> findByLCVI_SEERC(
		long layoutContentVersionId, String segmentsExperienceERC, int start,
		int end) {

		return getPersistence().findByLCVI_SEERC(
			layoutContentVersionId, segmentsExperienceERC, start, end);
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
	public static List<LayoutContentVersionPreview> findByLCVI_SEERC(
		long layoutContentVersionId, String segmentsExperienceERC, int start,
		int end,
		OrderByComparator<LayoutContentVersionPreview> orderByComparator) {

		return getPersistence().findByLCVI_SEERC(
			layoutContentVersionId, segmentsExperienceERC, start, end,
			orderByComparator);
	}

	public static LayoutContentVersionPreviewPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(
		LayoutContentVersionPreviewPersistence persistence) {

		_persistence = persistence;
	}

	private static volatile LayoutContentVersionPreviewPersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1113226171