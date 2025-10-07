/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.trash.service.persistence;

import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.trash.exception.NoSuchVersionException;
import com.liferay.trash.model.TrashVersion;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the trash version service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see TrashVersionUtil
 * @generated
 */
@ProviderType
public interface TrashVersionPersistence
	extends BasePersistence<TrashVersion>, CTPersistence<TrashVersion> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link TrashVersionUtil} to access the trash version persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns all the trash versions where entryId = &#63;.
	 *
	 * @param entryId the entry ID
	 * @return the matching trash versions
	 */
	public java.util.List<TrashVersion> findByEntryId(long entryId);

	/**
	 * Returns a range of all the trash versions where entryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TrashVersionModelImpl</code>.
	 * </p>
	 *
	 * @param entryId the entry ID
	 * @param start the lower bound of the range of trash versions
	 * @param end the upper bound of the range of trash versions (not inclusive)
	 * @return the range of matching trash versions
	 */
	public java.util.List<TrashVersion> findByEntryId(
		long entryId, int start, int end);

	/**
	 * Returns an ordered range of all the trash versions where entryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TrashVersionModelImpl</code>.
	 * </p>
	 *
	 * @param entryId the entry ID
	 * @param start the lower bound of the range of trash versions
	 * @param end the upper bound of the range of trash versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching trash versions
	 */
	public java.util.List<TrashVersion> findByEntryId(
		long entryId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<TrashVersion>
			orderByComparator);

	/**
	 * Returns an ordered range of all the trash versions where entryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TrashVersionModelImpl</code>.
	 * </p>
	 *
	 * @param entryId the entry ID
	 * @param start the lower bound of the range of trash versions
	 * @param end the upper bound of the range of trash versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching trash versions
	 */
	public java.util.List<TrashVersion> findByEntryId(
		long entryId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<TrashVersion>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first trash version in the ordered set where entryId = &#63;.
	 *
	 * @param entryId the entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching trash version
	 * @throws NoSuchVersionException if a matching trash version could not be found
	 */
	public TrashVersion findByEntryId_First(
			long entryId,
			com.liferay.portal.kernel.util.OrderByComparator<TrashVersion>
				orderByComparator)
		throws NoSuchVersionException;

	/**
	 * Returns the first trash version in the ordered set where entryId = &#63;.
	 *
	 * @param entryId the entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching trash version, or <code>null</code> if a matching trash version could not be found
	 */
	public TrashVersion fetchByEntryId_First(
		long entryId,
		com.liferay.portal.kernel.util.OrderByComparator<TrashVersion>
			orderByComparator);

	/**
	 * Returns the last trash version in the ordered set where entryId = &#63;.
	 *
	 * @param entryId the entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching trash version
	 * @throws NoSuchVersionException if a matching trash version could not be found
	 */
	public TrashVersion findByEntryId_Last(
			long entryId,
			com.liferay.portal.kernel.util.OrderByComparator<TrashVersion>
				orderByComparator)
		throws NoSuchVersionException;

	/**
	 * Returns the last trash version in the ordered set where entryId = &#63;.
	 *
	 * @param entryId the entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching trash version, or <code>null</code> if a matching trash version could not be found
	 */
	public TrashVersion fetchByEntryId_Last(
		long entryId,
		com.liferay.portal.kernel.util.OrderByComparator<TrashVersion>
			orderByComparator);

	/**
	 * Returns the trash versions before and after the current trash version in the ordered set where entryId = &#63;.
	 *
	 * @param versionId the primary key of the current trash version
	 * @param entryId the entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next trash version
	 * @throws NoSuchVersionException if a trash version with the primary key could not be found
	 */
	public TrashVersion[] findByEntryId_PrevAndNext(
			long versionId, long entryId,
			com.liferay.portal.kernel.util.OrderByComparator<TrashVersion>
				orderByComparator)
		throws NoSuchVersionException;

	/**
	 * Removes all the trash versions where entryId = &#63; from the database.
	 *
	 * @param entryId the entry ID
	 */
	public void removeByEntryId(long entryId);

	/**
	 * Returns the number of trash versions where entryId = &#63;.
	 *
	 * @param entryId the entry ID
	 * @return the number of matching trash versions
	 */
	public int countByEntryId(long entryId);

	/**
	 * Returns all the trash versions where entryId = &#63; and classNameId = &#63;.
	 *
	 * @param entryId the entry ID
	 * @param classNameId the class name ID
	 * @return the matching trash versions
	 */
	public java.util.List<TrashVersion> findByE_CN(
		long entryId, long classNameId);

	/**
	 * Returns a range of all the trash versions where entryId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TrashVersionModelImpl</code>.
	 * </p>
	 *
	 * @param entryId the entry ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of trash versions
	 * @param end the upper bound of the range of trash versions (not inclusive)
	 * @return the range of matching trash versions
	 */
	public java.util.List<TrashVersion> findByE_CN(
		long entryId, long classNameId, int start, int end);

	/**
	 * Returns an ordered range of all the trash versions where entryId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TrashVersionModelImpl</code>.
	 * </p>
	 *
	 * @param entryId the entry ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of trash versions
	 * @param end the upper bound of the range of trash versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching trash versions
	 */
	public java.util.List<TrashVersion> findByE_CN(
		long entryId, long classNameId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<TrashVersion>
			orderByComparator);

	/**
	 * Returns an ordered range of all the trash versions where entryId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TrashVersionModelImpl</code>.
	 * </p>
	 *
	 * @param entryId the entry ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of trash versions
	 * @param end the upper bound of the range of trash versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching trash versions
	 */
	public java.util.List<TrashVersion> findByE_CN(
		long entryId, long classNameId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<TrashVersion>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first trash version in the ordered set where entryId = &#63; and classNameId = &#63;.
	 *
	 * @param entryId the entry ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching trash version
	 * @throws NoSuchVersionException if a matching trash version could not be found
	 */
	public TrashVersion findByE_CN_First(
			long entryId, long classNameId,
			com.liferay.portal.kernel.util.OrderByComparator<TrashVersion>
				orderByComparator)
		throws NoSuchVersionException;

	/**
	 * Returns the first trash version in the ordered set where entryId = &#63; and classNameId = &#63;.
	 *
	 * @param entryId the entry ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching trash version, or <code>null</code> if a matching trash version could not be found
	 */
	public TrashVersion fetchByE_CN_First(
		long entryId, long classNameId,
		com.liferay.portal.kernel.util.OrderByComparator<TrashVersion>
			orderByComparator);

	/**
	 * Returns the last trash version in the ordered set where entryId = &#63; and classNameId = &#63;.
	 *
	 * @param entryId the entry ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching trash version
	 * @throws NoSuchVersionException if a matching trash version could not be found
	 */
	public TrashVersion findByE_CN_Last(
			long entryId, long classNameId,
			com.liferay.portal.kernel.util.OrderByComparator<TrashVersion>
				orderByComparator)
		throws NoSuchVersionException;

	/**
	 * Returns the last trash version in the ordered set where entryId = &#63; and classNameId = &#63;.
	 *
	 * @param entryId the entry ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching trash version, or <code>null</code> if a matching trash version could not be found
	 */
	public TrashVersion fetchByE_CN_Last(
		long entryId, long classNameId,
		com.liferay.portal.kernel.util.OrderByComparator<TrashVersion>
			orderByComparator);

	/**
	 * Returns the trash versions before and after the current trash version in the ordered set where entryId = &#63; and classNameId = &#63;.
	 *
	 * @param versionId the primary key of the current trash version
	 * @param entryId the entry ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next trash version
	 * @throws NoSuchVersionException if a trash version with the primary key could not be found
	 */
	public TrashVersion[] findByE_CN_PrevAndNext(
			long versionId, long entryId, long classNameId,
			com.liferay.portal.kernel.util.OrderByComparator<TrashVersion>
				orderByComparator)
		throws NoSuchVersionException;

	/**
	 * Removes all the trash versions where entryId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param entryId the entry ID
	 * @param classNameId the class name ID
	 */
	public void removeByE_CN(long entryId, long classNameId);

	/**
	 * Returns the number of trash versions where entryId = &#63; and classNameId = &#63;.
	 *
	 * @param entryId the entry ID
	 * @param classNameId the class name ID
	 * @return the number of matching trash versions
	 */
	public int countByE_CN(long entryId, long classNameId);

	/**
	 * Returns the trash version where classNameId = &#63; and classPK = &#63; or throws a <code>NoSuchVersionException</code> if it could not be found.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching trash version
	 * @throws NoSuchVersionException if a matching trash version could not be found
	 */
	public TrashVersion findByCN_CPK(long classNameId, long classPK)
		throws NoSuchVersionException;

	/**
	 * Returns the trash version where classNameId = &#63; and classPK = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching trash version, or <code>null</code> if a matching trash version could not be found
	 */
	public TrashVersion fetchByCN_CPK(long classNameId, long classPK);

	/**
	 * Returns the trash version where classNameId = &#63; and classPK = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching trash version, or <code>null</code> if a matching trash version could not be found
	 */
	public TrashVersion fetchByCN_CPK(
		long classNameId, long classPK, boolean useFinderCache);

	/**
	 * Removes the trash version where classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the trash version that was removed
	 */
	public TrashVersion removeByCN_CPK(long classNameId, long classPK)
		throws NoSuchVersionException;

	/**
	 * Returns the number of trash versions where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching trash versions
	 */
	public int countByCN_CPK(long classNameId, long classPK);

	/**
	 * Caches the trash version in the entity cache if it is enabled.
	 *
	 * @param trashVersion the trash version
	 */
	public void cacheResult(TrashVersion trashVersion);

	/**
	 * Caches the trash versions in the entity cache if it is enabled.
	 *
	 * @param trashVersions the trash versions
	 */
	public void cacheResult(java.util.List<TrashVersion> trashVersions);

	/**
	 * Creates a new trash version with the primary key. Does not add the trash version to the database.
	 *
	 * @param versionId the primary key for the new trash version
	 * @return the new trash version
	 */
	public TrashVersion create(long versionId);

	/**
	 * Removes the trash version with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param versionId the primary key of the trash version
	 * @return the trash version that was removed
	 * @throws NoSuchVersionException if a trash version with the primary key could not be found
	 */
	public TrashVersion remove(long versionId) throws NoSuchVersionException;

	public TrashVersion updateImpl(TrashVersion trashVersion);

	/**
	 * Returns the trash version with the primary key or throws a <code>NoSuchVersionException</code> if it could not be found.
	 *
	 * @param versionId the primary key of the trash version
	 * @return the trash version
	 * @throws NoSuchVersionException if a trash version with the primary key could not be found
	 */
	public TrashVersion findByPrimaryKey(long versionId)
		throws NoSuchVersionException;

	/**
	 * Returns the trash version with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param versionId the primary key of the trash version
	 * @return the trash version, or <code>null</code> if a trash version with the primary key could not be found
	 */
	public TrashVersion fetchByPrimaryKey(long versionId);

	/**
	 * Returns all the trash versions.
	 *
	 * @return the trash versions
	 */
	public java.util.List<TrashVersion> findAll();

	/**
	 * Returns a range of all the trash versions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TrashVersionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of trash versions
	 * @param end the upper bound of the range of trash versions (not inclusive)
	 * @return the range of trash versions
	 */
	public java.util.List<TrashVersion> findAll(int start, int end);

	/**
	 * Returns an ordered range of all the trash versions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TrashVersionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of trash versions
	 * @param end the upper bound of the range of trash versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of trash versions
	 */
	public java.util.List<TrashVersion> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<TrashVersion>
			orderByComparator);

	/**
	 * Returns an ordered range of all the trash versions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TrashVersionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of trash versions
	 * @param end the upper bound of the range of trash versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of trash versions
	 */
	public java.util.List<TrashVersion> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<TrashVersion>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the trash versions from the database.
	 */
	public void removeAll();

	/**
	 * Returns the number of trash versions.
	 *
	 * @return the number of trash versions
	 */
	public int countAll();

}