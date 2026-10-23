/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service.persistence;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.model.Image;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the image service. This utility wraps <code>com.liferay.portal.service.persistence.impl.ImagePersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see ImagePersistence
 * @generated
 */
public class ImageUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(List<Image> images) {
		getPersistence().cacheResult(images);
	}

	/**
	 * @see BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(Image image) {
		getPersistence().cacheResult(image);
	}

	/**
	 * @see BasePersistence#clearCache()
	 */
	public static void clearCache() {
		getPersistence().clearCache();
	}

	/**
	 * @see BasePersistence#clearCache(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void clearCache(Image image) {
		getPersistence().clearCache(image);
	}

	/**
	 * @see BasePersistence#countWithDynamicQuery(DynamicQuery)
	 */
	public static long countWithDynamicQuery(DynamicQuery dynamicQuery) {
		return getPersistence().countWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see BasePersistence#fetchByPrimaryKeys(Set)
	 */
	public static Map<Serializable, Image> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<Image> findWithDynamicQuery(DynamicQuery dynamicQuery) {
		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<Image> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<Image> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<Image> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static Image update(Image image) {
		return getPersistence().update(image);
	}

	/**
	 * @see BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static Image update(Image image, ServiceContext serviceContext) {
		return getPersistence().update(image, serviceContext);
	}

	/**
	 * Creates a new image with the primary key. Does not add the image to the database.
	 *
	 * @param imageId the primary key for the new image
	 * @return the new image
	 */
	public static Image create(long imageId) {
		return getPersistence().create(imageId);
	}

	/**
	 * Removes the image with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param imageId the primary key of the image
	 * @return the image that was removed
	 * @throws NoSuchImageException if a image with the primary key could not be found
	 */
	public static Image remove(long imageId)
		throws com.liferay.portal.kernel.exception.NoSuchImageException {

		return getPersistence().remove(imageId);
	}

	public static Image updateImpl(Image image) {
		return getPersistence().updateImpl(image);
	}

	/**
	 * Returns the image with the primary key or throws a <code>NoSuchImageException</code> if it could not be found.
	 *
	 * @param imageId the primary key of the image
	 * @return the image
	 * @throws NoSuchImageException if a image with the primary key could not be found
	 */
	public static Image findByPrimaryKey(long imageId)
		throws com.liferay.portal.kernel.exception.NoSuchImageException {

		return getPersistence().findByPrimaryKey(imageId);
	}

	/**
	 * Returns the image with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param imageId the primary key of the image
	 * @return the image, or <code>null</code> if a image with the primary key could not be found
	 */
	public static Image fetchByPrimaryKey(long imageId) {
		return getPersistence().fetchByPrimaryKey(imageId);
	}

	public static ImagePersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(ImagePersistence persistence) {
		_persistence = persistence;
	}

	private static volatile ImagePersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1303780095