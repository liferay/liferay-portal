/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service.persistence;

import com.liferay.portal.kernel.exception.NoSuchImageException;
import com.liferay.portal.kernel.model.Image;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the image service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see ImageUtil
 * @generated
 */
@ProviderType
public interface ImagePersistence
	extends BasePersistence<Image>, CTPersistence<Image> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link ImageUtil} to access the image persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns all the images where size &lt; &#63;.
	 *
	 * @param size the size
	 * @return the matching images
	 */
	public java.util.List<Image> findByLtSize(int size);

	/**
	 * Returns a range of all the images where size &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ImageModelImpl</code>.
	 * </p>
	 *
	 * @param size the size
	 * @param start the lower bound of the range of images
	 * @param end the upper bound of the range of images (not inclusive)
	 * @return the range of matching images
	 */
	public java.util.List<Image> findByLtSize(int size, int start, int end);

	/**
	 * Returns an ordered range of all the images where size &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ImageModelImpl</code>.
	 * </p>
	 *
	 * @param size the size
	 * @param start the lower bound of the range of images
	 * @param end the upper bound of the range of images (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching images
	 */
	public java.util.List<Image> findByLtSize(
		int size, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Image>
			orderByComparator);

	/**
	 * Returns an ordered range of all the images where size &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ImageModelImpl</code>.
	 * </p>
	 *
	 * @param size the size
	 * @param start the lower bound of the range of images
	 * @param end the upper bound of the range of images (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching images
	 */
	public java.util.List<Image> findByLtSize(
		int size, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Image>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first image in the ordered set where size &lt; &#63;.
	 *
	 * @param size the size
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching image
	 * @throws NoSuchImageException if a matching image could not be found
	 */
	public Image findByLtSize_First(
			int size,
			com.liferay.portal.kernel.util.OrderByComparator<Image>
				orderByComparator)
		throws NoSuchImageException;

	/**
	 * Returns the first image in the ordered set where size &lt; &#63;.
	 *
	 * @param size the size
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching image, or <code>null</code> if a matching image could not be found
	 */
	public Image fetchByLtSize_First(
		int size,
		com.liferay.portal.kernel.util.OrderByComparator<Image>
			orderByComparator);

	/**
	 * Returns the last image in the ordered set where size &lt; &#63;.
	 *
	 * @param size the size
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching image
	 * @throws NoSuchImageException if a matching image could not be found
	 */
	public Image findByLtSize_Last(
			int size,
			com.liferay.portal.kernel.util.OrderByComparator<Image>
				orderByComparator)
		throws NoSuchImageException;

	/**
	 * Returns the last image in the ordered set where size &lt; &#63;.
	 *
	 * @param size the size
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching image, or <code>null</code> if a matching image could not be found
	 */
	public Image fetchByLtSize_Last(
		int size,
		com.liferay.portal.kernel.util.OrderByComparator<Image>
			orderByComparator);

	/**
	 * Returns the images before and after the current image in the ordered set where size &lt; &#63;.
	 *
	 * @param imageId the primary key of the current image
	 * @param size the size
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next image
	 * @throws NoSuchImageException if a image with the primary key could not be found
	 */
	public Image[] findByLtSize_PrevAndNext(
			long imageId, int size,
			com.liferay.portal.kernel.util.OrderByComparator<Image>
				orderByComparator)
		throws NoSuchImageException;

	/**
	 * Removes all the images where size &lt; &#63; from the database.
	 *
	 * @param size the size
	 */
	public void removeByLtSize(int size);

	/**
	 * Returns the number of images where size &lt; &#63;.
	 *
	 * @param size the size
	 * @return the number of matching images
	 */
	public int countByLtSize(int size);

	/**
	 * Caches the image in the entity cache if it is enabled.
	 *
	 * @param image the image
	 */
	public void cacheResult(Image image);

	/**
	 * Caches the images in the entity cache if it is enabled.
	 *
	 * @param images the images
	 */
	public void cacheResult(java.util.List<Image> images);

	/**
	 * Creates a new image with the primary key. Does not add the image to the database.
	 *
	 * @param imageId the primary key for the new image
	 * @return the new image
	 */
	public Image create(long imageId);

	/**
	 * Removes the image with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param imageId the primary key of the image
	 * @return the image that was removed
	 * @throws NoSuchImageException if a image with the primary key could not be found
	 */
	public Image remove(long imageId) throws NoSuchImageException;

	public Image updateImpl(Image image);

	/**
	 * Returns the image with the primary key or throws a <code>NoSuchImageException</code> if it could not be found.
	 *
	 * @param imageId the primary key of the image
	 * @return the image
	 * @throws NoSuchImageException if a image with the primary key could not be found
	 */
	public Image findByPrimaryKey(long imageId) throws NoSuchImageException;

	/**
	 * Returns the image with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param imageId the primary key of the image
	 * @return the image, or <code>null</code> if a image with the primary key could not be found
	 */
	public Image fetchByPrimaryKey(long imageId);

	/**
	 * Returns all the images.
	 *
	 * @return the images
	 */
	public java.util.List<Image> findAll();

	/**
	 * Returns a range of all the images.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ImageModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of images
	 * @param end the upper bound of the range of images (not inclusive)
	 * @return the range of images
	 */
	public java.util.List<Image> findAll(int start, int end);

	/**
	 * Returns an ordered range of all the images.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ImageModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of images
	 * @param end the upper bound of the range of images (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of images
	 */
	public java.util.List<Image> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Image>
			orderByComparator);

	/**
	 * Returns an ordered range of all the images.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ImageModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of images
	 * @param end the upper bound of the range of images (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of images
	 */
	public java.util.List<Image> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Image>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the images from the database.
	 */
	public void removeAll();

	/**
	 * Returns the number of images.
	 *
	 * @return the number of images
	 */
	public int countAll();

}