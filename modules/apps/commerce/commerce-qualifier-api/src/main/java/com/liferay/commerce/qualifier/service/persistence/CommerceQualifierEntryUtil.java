/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.qualifier.service.persistence;

import com.liferay.commerce.qualifier.model.CommerceQualifierEntry;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the commerce qualifier entry service. This utility wraps <code>com.liferay.commerce.qualifier.service.persistence.impl.CommerceQualifierEntryPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Riccardo Alberti
 * @see CommerceQualifierEntryPersistence
 * @generated
 */
public class CommerceQualifierEntryUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(
		List<CommerceQualifierEntry> commerceQualifierEntries) {

		getPersistence().cacheResult(commerceQualifierEntries);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(
		CommerceQualifierEntry commerceQualifierEntry) {

		getPersistence().cacheResult(commerceQualifierEntry);
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
		CommerceQualifierEntry commerceQualifierEntry) {

		getPersistence().clearCache(commerceQualifierEntry);
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
	public static Map<Serializable, CommerceQualifierEntry> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<CommerceQualifierEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<CommerceQualifierEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<CommerceQualifierEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<CommerceQualifierEntry> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static CommerceQualifierEntry update(
		CommerceQualifierEntry commerceQualifierEntry) {

		return getPersistence().update(commerceQualifierEntry);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static CommerceQualifierEntry update(
		CommerceQualifierEntry commerceQualifierEntry,
		ServiceContext serviceContext) {

		return getPersistence().update(commerceQualifierEntry, serviceContext);
	}

	/**
	 * Returns an ordered range of all the commerce qualifier entries where sourceClassNameId = &#63; and sourceClassPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.qualifier.model.impl.CommerceQualifierEntryModelImpl</code>.
	 * </p>
	 *
	 * @param sourceClassNameId the source class name ID
	 * @param sourceClassPK the source class pk
	 * @param start the lower bound of the range of commerce qualifier entries
	 * @param end the upper bound of the range of commerce qualifier entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce qualifier entries
	 */
	public static List<CommerceQualifierEntry> findByS_S(
		long sourceClassNameId, long sourceClassPK, int start, int end,
		OrderByComparator<CommerceQualifierEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByS_S(
			sourceClassNameId, sourceClassPK, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first commerce qualifier entry in the ordered set where sourceClassNameId = &#63; and sourceClassPK = &#63;.
	 *
	 * @param sourceClassNameId the source class name ID
	 * @param sourceClassPK the source class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce qualifier entry
	 * @throws NoSuchCommerceQualifierEntryException if a matching commerce qualifier entry could not be found
	 */
	public static CommerceQualifierEntry findByS_S_First(
			long sourceClassNameId, long sourceClassPK,
			OrderByComparator<CommerceQualifierEntry> orderByComparator)
		throws com.liferay.commerce.qualifier.exception.
			NoSuchCommerceQualifierEntryException {

		return getPersistence().findByS_S_First(
			sourceClassNameId, sourceClassPK, orderByComparator);
	}

	/**
	 * Returns the first commerce qualifier entry in the ordered set where sourceClassNameId = &#63; and sourceClassPK = &#63;.
	 *
	 * @param sourceClassNameId the source class name ID
	 * @param sourceClassPK the source class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce qualifier entry, or <code>null</code> if a matching commerce qualifier entry could not be found
	 */
	public static CommerceQualifierEntry fetchByS_S_First(
		long sourceClassNameId, long sourceClassPK,
		OrderByComparator<CommerceQualifierEntry> orderByComparator) {

		return getPersistence().fetchByS_S_First(
			sourceClassNameId, sourceClassPK, orderByComparator);
	}

	/**
	 * Removes all the commerce qualifier entries where sourceClassNameId = &#63; and sourceClassPK = &#63; from the database.
	 *
	 * @param sourceClassNameId the source class name ID
	 * @param sourceClassPK the source class pk
	 */
	public static void removeByS_S(long sourceClassNameId, long sourceClassPK) {
		getPersistence().removeByS_S(sourceClassNameId, sourceClassPK);
	}

	/**
	 * Returns the number of commerce qualifier entries where sourceClassNameId = &#63; and sourceClassPK = &#63;.
	 *
	 * @param sourceClassNameId the source class name ID
	 * @param sourceClassPK the source class pk
	 * @return the number of matching commerce qualifier entries
	 */
	public static int countByS_S(long sourceClassNameId, long sourceClassPK) {
		return getPersistence().countByS_S(sourceClassNameId, sourceClassPK);
	}

	/**
	 * Returns an ordered range of all the commerce qualifier entries where targetClassNameId = &#63; and targetClassPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.qualifier.model.impl.CommerceQualifierEntryModelImpl</code>.
	 * </p>
	 *
	 * @param targetClassNameId the target class name ID
	 * @param targetClassPK the target class pk
	 * @param start the lower bound of the range of commerce qualifier entries
	 * @param end the upper bound of the range of commerce qualifier entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce qualifier entries
	 */
	public static List<CommerceQualifierEntry> findByT_T(
		long targetClassNameId, long targetClassPK, int start, int end,
		OrderByComparator<CommerceQualifierEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByT_T(
			targetClassNameId, targetClassPK, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first commerce qualifier entry in the ordered set where targetClassNameId = &#63; and targetClassPK = &#63;.
	 *
	 * @param targetClassNameId the target class name ID
	 * @param targetClassPK the target class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce qualifier entry
	 * @throws NoSuchCommerceQualifierEntryException if a matching commerce qualifier entry could not be found
	 */
	public static CommerceQualifierEntry findByT_T_First(
			long targetClassNameId, long targetClassPK,
			OrderByComparator<CommerceQualifierEntry> orderByComparator)
		throws com.liferay.commerce.qualifier.exception.
			NoSuchCommerceQualifierEntryException {

		return getPersistence().findByT_T_First(
			targetClassNameId, targetClassPK, orderByComparator);
	}

	/**
	 * Returns the first commerce qualifier entry in the ordered set where targetClassNameId = &#63; and targetClassPK = &#63;.
	 *
	 * @param targetClassNameId the target class name ID
	 * @param targetClassPK the target class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce qualifier entry, or <code>null</code> if a matching commerce qualifier entry could not be found
	 */
	public static CommerceQualifierEntry fetchByT_T_First(
		long targetClassNameId, long targetClassPK,
		OrderByComparator<CommerceQualifierEntry> orderByComparator) {

		return getPersistence().fetchByT_T_First(
			targetClassNameId, targetClassPK, orderByComparator);
	}

	/**
	 * Removes all the commerce qualifier entries where targetClassNameId = &#63; and targetClassPK = &#63; from the database.
	 *
	 * @param targetClassNameId the target class name ID
	 * @param targetClassPK the target class pk
	 */
	public static void removeByT_T(long targetClassNameId, long targetClassPK) {
		getPersistence().removeByT_T(targetClassNameId, targetClassPK);
	}

	/**
	 * Returns the number of commerce qualifier entries where targetClassNameId = &#63; and targetClassPK = &#63;.
	 *
	 * @param targetClassNameId the target class name ID
	 * @param targetClassPK the target class pk
	 * @return the number of matching commerce qualifier entries
	 */
	public static int countByT_T(long targetClassNameId, long targetClassPK) {
		return getPersistence().countByT_T(targetClassNameId, targetClassPK);
	}

	/**
	 * Returns the commerce qualifier entry where sourceClassNameId = &#63; and sourceClassPK = &#63; and targetClassNameId = &#63; and targetClassPK = &#63; or throws a <code>NoSuchCommerceQualifierEntryException</code> if it could not be found.
	 *
	 * @param sourceClassNameId the source class name ID
	 * @param sourceClassPK the source class pk
	 * @param targetClassNameId the target class name ID
	 * @param targetClassPK the target class pk
	 * @return the matching commerce qualifier entry
	 * @throws NoSuchCommerceQualifierEntryException if a matching commerce qualifier entry could not be found
	 */
	public static CommerceQualifierEntry findByS_S_T_T(
			long sourceClassNameId, long sourceClassPK, long targetClassNameId,
			long targetClassPK)
		throws com.liferay.commerce.qualifier.exception.
			NoSuchCommerceQualifierEntryException {

		return getPersistence().findByS_S_T_T(
			sourceClassNameId, sourceClassPK, targetClassNameId, targetClassPK);
	}

	/**
	 * Returns the commerce qualifier entry where sourceClassNameId = &#63; and sourceClassPK = &#63; and targetClassNameId = &#63; and targetClassPK = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param sourceClassNameId the source class name ID
	 * @param sourceClassPK the source class pk
	 * @param targetClassNameId the target class name ID
	 * @param targetClassPK the target class pk
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce qualifier entry, or <code>null</code> if a matching commerce qualifier entry could not be found
	 */
	public static CommerceQualifierEntry fetchByS_S_T_T(
		long sourceClassNameId, long sourceClassPK, long targetClassNameId,
		long targetClassPK, boolean useFinderCache) {

		return getPersistence().fetchByS_S_T_T(
			sourceClassNameId, sourceClassPK, targetClassNameId, targetClassPK,
			useFinderCache);
	}

	/**
	 * Removes the commerce qualifier entry where sourceClassNameId = &#63; and sourceClassPK = &#63; and targetClassNameId = &#63; and targetClassPK = &#63; from the database.
	 *
	 * @param sourceClassNameId the source class name ID
	 * @param sourceClassPK the source class pk
	 * @param targetClassNameId the target class name ID
	 * @param targetClassPK the target class pk
	 * @return the commerce qualifier entry that was removed
	 */
	public static CommerceQualifierEntry removeByS_S_T_T(
			long sourceClassNameId, long sourceClassPK, long targetClassNameId,
			long targetClassPK)
		throws com.liferay.commerce.qualifier.exception.
			NoSuchCommerceQualifierEntryException {

		return getPersistence().removeByS_S_T_T(
			sourceClassNameId, sourceClassPK, targetClassNameId, targetClassPK);
	}

	/**
	 * Returns the number of commerce qualifier entries where sourceClassNameId = &#63; and sourceClassPK = &#63; and targetClassNameId = &#63; and targetClassPK = &#63;.
	 *
	 * @param sourceClassNameId the source class name ID
	 * @param sourceClassPK the source class pk
	 * @param targetClassNameId the target class name ID
	 * @param targetClassPK the target class pk
	 * @return the number of matching commerce qualifier entries
	 */
	public static int countByS_S_T_T(
		long sourceClassNameId, long sourceClassPK, long targetClassNameId,
		long targetClassPK) {

		return getPersistence().countByS_S_T_T(
			sourceClassNameId, sourceClassPK, targetClassNameId, targetClassPK);
	}

	/**
	 * Creates a new commerce qualifier entry with the primary key. Does not add the commerce qualifier entry to the database.
	 *
	 * @param commerceQualifierEntryId the primary key for the new commerce qualifier entry
	 * @return the new commerce qualifier entry
	 */
	public static CommerceQualifierEntry create(long commerceQualifierEntryId) {
		return getPersistence().create(commerceQualifierEntryId);
	}

	/**
	 * Removes the commerce qualifier entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commerceQualifierEntryId the primary key of the commerce qualifier entry
	 * @return the commerce qualifier entry that was removed
	 * @throws NoSuchCommerceQualifierEntryException if a commerce qualifier entry with the primary key could not be found
	 */
	public static CommerceQualifierEntry remove(long commerceQualifierEntryId)
		throws com.liferay.commerce.qualifier.exception.
			NoSuchCommerceQualifierEntryException {

		return getPersistence().remove(commerceQualifierEntryId);
	}

	public static CommerceQualifierEntry updateImpl(
		CommerceQualifierEntry commerceQualifierEntry) {

		return getPersistence().updateImpl(commerceQualifierEntry);
	}

	/**
	 * Returns the commerce qualifier entry with the primary key or throws a <code>NoSuchCommerceQualifierEntryException</code> if it could not be found.
	 *
	 * @param commerceQualifierEntryId the primary key of the commerce qualifier entry
	 * @return the commerce qualifier entry
	 * @throws NoSuchCommerceQualifierEntryException if a commerce qualifier entry with the primary key could not be found
	 */
	public static CommerceQualifierEntry findByPrimaryKey(
			long commerceQualifierEntryId)
		throws com.liferay.commerce.qualifier.exception.
			NoSuchCommerceQualifierEntryException {

		return getPersistence().findByPrimaryKey(commerceQualifierEntryId);
	}

	/**
	 * Returns the commerce qualifier entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commerceQualifierEntryId the primary key of the commerce qualifier entry
	 * @return the commerce qualifier entry, or <code>null</code> if a commerce qualifier entry with the primary key could not be found
	 */
	public static CommerceQualifierEntry fetchByPrimaryKey(
		long commerceQualifierEntryId) {

		return getPersistence().fetchByPrimaryKey(commerceQualifierEntryId);
	}

	/**
	 * Returns the commerce qualifier entry where sourceClassNameId = &#63; and sourceClassPK = &#63; and targetClassNameId = &#63; and targetClassPK = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param sourceClassNameId the source class name ID
	 * @param sourceClassPK the source class pk
	 * @param targetClassNameId the target class name ID
	 * @param targetClassPK the target class pk
	 * @return the matching commerce qualifier entry, or <code>null</code> if a matching commerce qualifier entry could not be found
	 */
	public static CommerceQualifierEntry fetchByS_S_T_T(
		long sourceClassNameId, long sourceClassPK, long targetClassNameId,
		long targetClassPK) {

		return getPersistence().fetchByS_S_T_T(
			sourceClassNameId, sourceClassPK, targetClassNameId, targetClassPK);
	}

	/**
	 * Returns all the commerce qualifier entries where sourceClassNameId = &#63; and sourceClassPK = &#63;.
	 *
	 * @param sourceClassNameId the source class name ID
	 * @param sourceClassPK the source class pk
	 * @return the matching commerce qualifier entries
	 */
	public static List<CommerceQualifierEntry> findByS_S(
		long sourceClassNameId, long sourceClassPK) {

		return getPersistence().findByS_S(sourceClassNameId, sourceClassPK);
	}

	/**
	 * Returns a range of all the commerce qualifier entries where sourceClassNameId = &#63; and sourceClassPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.qualifier.model.impl.CommerceQualifierEntryModelImpl</code>.
	 * </p>
	 *
	 * @param sourceClassNameId the source class name ID
	 * @param sourceClassPK the source class pk
	 * @param start the lower bound of the range of commerce qualifier entries
	 * @param end the upper bound of the range of commerce qualifier entries (not inclusive)
	 * @return the range of matching commerce qualifier entries
	 */
	public static List<CommerceQualifierEntry> findByS_S(
		long sourceClassNameId, long sourceClassPK, int start, int end) {

		return getPersistence().findByS_S(
			sourceClassNameId, sourceClassPK, start, end);
	}

	/**
	 * Returns an ordered range of all the commerce qualifier entries where sourceClassNameId = &#63; and sourceClassPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.qualifier.model.impl.CommerceQualifierEntryModelImpl</code>.
	 * </p>
	 *
	 * @param sourceClassNameId the source class name ID
	 * @param sourceClassPK the source class pk
	 * @param start the lower bound of the range of commerce qualifier entries
	 * @param end the upper bound of the range of commerce qualifier entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce qualifier entries
	 */
	public static List<CommerceQualifierEntry> findByS_S(
		long sourceClassNameId, long sourceClassPK, int start, int end,
		OrderByComparator<CommerceQualifierEntry> orderByComparator) {

		return getPersistence().findByS_S(
			sourceClassNameId, sourceClassPK, start, end, orderByComparator);
	}

	/**
	 * Returns all the commerce qualifier entries where targetClassNameId = &#63; and targetClassPK = &#63;.
	 *
	 * @param targetClassNameId the target class name ID
	 * @param targetClassPK the target class pk
	 * @return the matching commerce qualifier entries
	 */
	public static List<CommerceQualifierEntry> findByT_T(
		long targetClassNameId, long targetClassPK) {

		return getPersistence().findByT_T(targetClassNameId, targetClassPK);
	}

	/**
	 * Returns a range of all the commerce qualifier entries where targetClassNameId = &#63; and targetClassPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.qualifier.model.impl.CommerceQualifierEntryModelImpl</code>.
	 * </p>
	 *
	 * @param targetClassNameId the target class name ID
	 * @param targetClassPK the target class pk
	 * @param start the lower bound of the range of commerce qualifier entries
	 * @param end the upper bound of the range of commerce qualifier entries (not inclusive)
	 * @return the range of matching commerce qualifier entries
	 */
	public static List<CommerceQualifierEntry> findByT_T(
		long targetClassNameId, long targetClassPK, int start, int end) {

		return getPersistence().findByT_T(
			targetClassNameId, targetClassPK, start, end);
	}

	/**
	 * Returns an ordered range of all the commerce qualifier entries where targetClassNameId = &#63; and targetClassPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.qualifier.model.impl.CommerceQualifierEntryModelImpl</code>.
	 * </p>
	 *
	 * @param targetClassNameId the target class name ID
	 * @param targetClassPK the target class pk
	 * @param start the lower bound of the range of commerce qualifier entries
	 * @param end the upper bound of the range of commerce qualifier entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce qualifier entries
	 */
	public static List<CommerceQualifierEntry> findByT_T(
		long targetClassNameId, long targetClassPK, int start, int end,
		OrderByComparator<CommerceQualifierEntry> orderByComparator) {

		return getPersistence().findByT_T(
			targetClassNameId, targetClassPK, start, end, orderByComparator);
	}

	public static CommerceQualifierEntryPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(
		CommerceQualifierEntryPersistence persistence) {

		_persistence = persistence;
	}

	private static volatile CommerceQualifierEntryPersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1753891407