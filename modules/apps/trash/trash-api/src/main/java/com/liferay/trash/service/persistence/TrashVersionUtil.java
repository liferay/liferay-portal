/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.trash.service.persistence;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.trash.model.TrashVersion;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the trash version service. This utility wraps <code>com.liferay.trash.service.persistence.impl.TrashVersionPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see TrashVersionPersistence
 * @generated
 */
public class TrashVersionUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#clearCache()
	 */
	public static void clearCache() {
		getPersistence().clearCache();
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#clearCache(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void clearCache(TrashVersion trashVersion) {
		getPersistence().clearCache(trashVersion);
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
	public static Map<Serializable, TrashVersion> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<TrashVersion> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<TrashVersion> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<TrashVersion> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<TrashVersion> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static TrashVersion update(TrashVersion trashVersion) {
		return getPersistence().update(trashVersion);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static TrashVersion update(
		TrashVersion trashVersion, ServiceContext serviceContext) {

		return getPersistence().update(trashVersion, serviceContext);
	}

	/**
	 * Returns all the trash versions where entryId = &#63;.
	 *
	 * @param entryId the entry ID
	 * @return the matching trash versions
	 */
	public static List<TrashVersion> findByEntryId(long entryId) {
		return getPersistence().findByEntryId(entryId);
	}

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
	public static List<TrashVersion> findByEntryId(
		long entryId, int start, int end) {

		return getPersistence().findByEntryId(entryId, start, end);
	}

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
	public static List<TrashVersion> findByEntryId(
		long entryId, int start, int end,
		OrderByComparator<TrashVersion> orderByComparator) {

		return getPersistence().findByEntryId(
			entryId, start, end, orderByComparator);
	}

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
	public static List<TrashVersion> findByEntryId(
		long entryId, int start, int end,
		OrderByComparator<TrashVersion> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByEntryId(
			entryId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first trash version in the ordered set where entryId = &#63;.
	 *
	 * @param entryId the entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching trash version
	 * @throws NoSuchVersionException if a matching trash version could not be found
	 */
	public static TrashVersion findByEntryId_First(
			long entryId, OrderByComparator<TrashVersion> orderByComparator)
		throws com.liferay.trash.exception.NoSuchVersionException {

		return getPersistence().findByEntryId_First(entryId, orderByComparator);
	}

	/**
	 * Returns the first trash version in the ordered set where entryId = &#63;.
	 *
	 * @param entryId the entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching trash version, or <code>null</code> if a matching trash version could not be found
	 */
	public static TrashVersion fetchByEntryId_First(
		long entryId, OrderByComparator<TrashVersion> orderByComparator) {

		return getPersistence().fetchByEntryId_First(
			entryId, orderByComparator);
	}

	/**
	 * Returns the last trash version in the ordered set where entryId = &#63;.
	 *
	 * @param entryId the entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching trash version
	 * @throws NoSuchVersionException if a matching trash version could not be found
	 */
	public static TrashVersion findByEntryId_Last(
			long entryId, OrderByComparator<TrashVersion> orderByComparator)
		throws com.liferay.trash.exception.NoSuchVersionException {

		return getPersistence().findByEntryId_Last(entryId, orderByComparator);
	}

	/**
	 * Returns the last trash version in the ordered set where entryId = &#63;.
	 *
	 * @param entryId the entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching trash version, or <code>null</code> if a matching trash version could not be found
	 */
	public static TrashVersion fetchByEntryId_Last(
		long entryId, OrderByComparator<TrashVersion> orderByComparator) {

		return getPersistence().fetchByEntryId_Last(entryId, orderByComparator);
	}

	/**
	 * Returns the trash versions before and after the current trash version in the ordered set where entryId = &#63;.
	 *
	 * @param versionId the primary key of the current trash version
	 * @param entryId the entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next trash version
	 * @throws NoSuchVersionException if a trash version with the primary key could not be found
	 */
	public static TrashVersion[] findByEntryId_PrevAndNext(
			long versionId, long entryId,
			OrderByComparator<TrashVersion> orderByComparator)
		throws com.liferay.trash.exception.NoSuchVersionException {

		return getPersistence().findByEntryId_PrevAndNext(
			versionId, entryId, orderByComparator);
	}

	/**
	 * Removes all the trash versions where entryId = &#63; from the database.
	 *
	 * @param entryId the entry ID
	 */
	public static void removeByEntryId(long entryId) {
		getPersistence().removeByEntryId(entryId);
	}

	/**
	 * Returns the number of trash versions where entryId = &#63;.
	 *
	 * @param entryId the entry ID
	 * @return the number of matching trash versions
	 */
	public static int countByEntryId(long entryId) {
		return getPersistence().countByEntryId(entryId);
	}

	/**
	 * Returns all the trash versions where entryId = &#63; and classNameId = &#63;.
	 *
	 * @param entryId the entry ID
	 * @param classNameId the class name ID
	 * @return the matching trash versions
	 */
	public static List<TrashVersion> findByE_CN(
		long entryId, long classNameId) {

		return getPersistence().findByE_CN(entryId, classNameId);
	}

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
	public static List<TrashVersion> findByE_CN(
		long entryId, long classNameId, int start, int end) {

		return getPersistence().findByE_CN(entryId, classNameId, start, end);
	}

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
	public static List<TrashVersion> findByE_CN(
		long entryId, long classNameId, int start, int end,
		OrderByComparator<TrashVersion> orderByComparator) {

		return getPersistence().findByE_CN(
			entryId, classNameId, start, end, orderByComparator);
	}

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
	public static List<TrashVersion> findByE_CN(
		long entryId, long classNameId, int start, int end,
		OrderByComparator<TrashVersion> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByE_CN(
			entryId, classNameId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first trash version in the ordered set where entryId = &#63; and classNameId = &#63;.
	 *
	 * @param entryId the entry ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching trash version
	 * @throws NoSuchVersionException if a matching trash version could not be found
	 */
	public static TrashVersion findByE_CN_First(
			long entryId, long classNameId,
			OrderByComparator<TrashVersion> orderByComparator)
		throws com.liferay.trash.exception.NoSuchVersionException {

		return getPersistence().findByE_CN_First(
			entryId, classNameId, orderByComparator);
	}

	/**
	 * Returns the first trash version in the ordered set where entryId = &#63; and classNameId = &#63;.
	 *
	 * @param entryId the entry ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching trash version, or <code>null</code> if a matching trash version could not be found
	 */
	public static TrashVersion fetchByE_CN_First(
		long entryId, long classNameId,
		OrderByComparator<TrashVersion> orderByComparator) {

		return getPersistence().fetchByE_CN_First(
			entryId, classNameId, orderByComparator);
	}

	/**
	 * Returns the last trash version in the ordered set where entryId = &#63; and classNameId = &#63;.
	 *
	 * @param entryId the entry ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching trash version
	 * @throws NoSuchVersionException if a matching trash version could not be found
	 */
	public static TrashVersion findByE_CN_Last(
			long entryId, long classNameId,
			OrderByComparator<TrashVersion> orderByComparator)
		throws com.liferay.trash.exception.NoSuchVersionException {

		return getPersistence().findByE_CN_Last(
			entryId, classNameId, orderByComparator);
	}

	/**
	 * Returns the last trash version in the ordered set where entryId = &#63; and classNameId = &#63;.
	 *
	 * @param entryId the entry ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching trash version, or <code>null</code> if a matching trash version could not be found
	 */
	public static TrashVersion fetchByE_CN_Last(
		long entryId, long classNameId,
		OrderByComparator<TrashVersion> orderByComparator) {

		return getPersistence().fetchByE_CN_Last(
			entryId, classNameId, orderByComparator);
	}

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
	public static TrashVersion[] findByE_CN_PrevAndNext(
			long versionId, long entryId, long classNameId,
			OrderByComparator<TrashVersion> orderByComparator)
		throws com.liferay.trash.exception.NoSuchVersionException {

		return getPersistence().findByE_CN_PrevAndNext(
			versionId, entryId, classNameId, orderByComparator);
	}

	/**
	 * Removes all the trash versions where entryId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param entryId the entry ID
	 * @param classNameId the class name ID
	 */
	public static void removeByE_CN(long entryId, long classNameId) {
		getPersistence().removeByE_CN(entryId, classNameId);
	}

	/**
	 * Returns the number of trash versions where entryId = &#63; and classNameId = &#63;.
	 *
	 * @param entryId the entry ID
	 * @param classNameId the class name ID
	 * @return the number of matching trash versions
	 */
	public static int countByE_CN(long entryId, long classNameId) {
		return getPersistence().countByE_CN(entryId, classNameId);
	}

	/**
	 * Returns the trash version where classNameId = &#63; and classPK = &#63; or throws a <code>NoSuchVersionException</code> if it could not be found.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching trash version
	 * @throws NoSuchVersionException if a matching trash version could not be found
	 */
	public static TrashVersion findByCN_CPK(long classNameId, long classPK)
		throws com.liferay.trash.exception.NoSuchVersionException {

		return getPersistence().findByCN_CPK(classNameId, classPK);
	}

	/**
	 * Returns the trash version where classNameId = &#63; and classPK = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching trash version, or <code>null</code> if a matching trash version could not be found
	 */
	public static TrashVersion fetchByCN_CPK(long classNameId, long classPK) {
		return getPersistence().fetchByCN_CPK(classNameId, classPK);
	}

	/**
	 * Returns the trash version where classNameId = &#63; and classPK = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching trash version, or <code>null</code> if a matching trash version could not be found
	 */
	public static TrashVersion fetchByCN_CPK(
		long classNameId, long classPK, boolean useFinderCache) {

		return getPersistence().fetchByCN_CPK(
			classNameId, classPK, useFinderCache);
	}

	/**
	 * Removes the trash version where classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the trash version that was removed
	 */
	public static TrashVersion removeByCN_CPK(long classNameId, long classPK)
		throws com.liferay.trash.exception.NoSuchVersionException {

		return getPersistence().removeByCN_CPK(classNameId, classPK);
	}

	/**
	 * Returns the number of trash versions where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching trash versions
	 */
	public static int countByCN_CPK(long classNameId, long classPK) {
		return getPersistence().countByCN_CPK(classNameId, classPK);
	}

	/**
	 * Caches the trash version in the entity cache if it is enabled.
	 *
	 * @param trashVersion the trash version
	 */
	public static void cacheResult(TrashVersion trashVersion) {
		getPersistence().cacheResult(trashVersion);
	}

	/**
	 * Caches the trash versions in the entity cache if it is enabled.
	 *
	 * @param trashVersions the trash versions
	 */
	public static void cacheResult(List<TrashVersion> trashVersions) {
		getPersistence().cacheResult(trashVersions);
	}

	/**
	 * Creates a new trash version with the primary key. Does not add the trash version to the database.
	 *
	 * @param versionId the primary key for the new trash version
	 * @return the new trash version
	 */
	public static TrashVersion create(long versionId) {
		return getPersistence().create(versionId);
	}

	/**
	 * Removes the trash version with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param versionId the primary key of the trash version
	 * @return the trash version that was removed
	 * @throws NoSuchVersionException if a trash version with the primary key could not be found
	 */
	public static TrashVersion remove(long versionId)
		throws com.liferay.trash.exception.NoSuchVersionException {

		return getPersistence().remove(versionId);
	}

	public static TrashVersion updateImpl(TrashVersion trashVersion) {
		return getPersistence().updateImpl(trashVersion);
	}

	/**
	 * Returns the trash version with the primary key or throws a <code>NoSuchVersionException</code> if it could not be found.
	 *
	 * @param versionId the primary key of the trash version
	 * @return the trash version
	 * @throws NoSuchVersionException if a trash version with the primary key could not be found
	 */
	public static TrashVersion findByPrimaryKey(long versionId)
		throws com.liferay.trash.exception.NoSuchVersionException {

		return getPersistence().findByPrimaryKey(versionId);
	}

	/**
	 * Returns the trash version with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param versionId the primary key of the trash version
	 * @return the trash version, or <code>null</code> if a trash version with the primary key could not be found
	 */
	public static TrashVersion fetchByPrimaryKey(long versionId) {
		return getPersistence().fetchByPrimaryKey(versionId);
	}

	/**
	 * Returns all the trash versions.
	 *
	 * @return the trash versions
	 */
	public static List<TrashVersion> findAll() {
		return getPersistence().findAll();
	}

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
	public static List<TrashVersion> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
	}

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
	public static List<TrashVersion> findAll(
		int start, int end, OrderByComparator<TrashVersion> orderByComparator) {

		return getPersistence().findAll(start, end, orderByComparator);
	}

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
	public static List<TrashVersion> findAll(
		int start, int end, OrderByComparator<TrashVersion> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findAll(
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the trash versions from the database.
	 */
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	 * Returns the number of trash versions.
	 *
	 * @return the number of trash versions
	 */
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static TrashVersionPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(TrashVersionPersistence persistence) {
		_persistence = persistence;
	}

	private static volatile TrashVersionPersistence _persistence;

}