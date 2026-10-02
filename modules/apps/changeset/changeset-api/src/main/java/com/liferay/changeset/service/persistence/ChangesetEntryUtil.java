/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.changeset.service.persistence;

import com.liferay.changeset.model.ChangesetEntry;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the changeset entry service. This utility wraps <code>com.liferay.changeset.service.persistence.impl.ChangesetEntryPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see ChangesetEntryPersistence
 * @generated
 */
public class ChangesetEntryUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(List<ChangesetEntry> changesetEntries) {
		getPersistence().cacheResult(changesetEntries);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(ChangesetEntry changesetEntry) {
		getPersistence().cacheResult(changesetEntry);
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
	public static void clearCache(ChangesetEntry changesetEntry) {
		getPersistence().clearCache(changesetEntry);
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
	public static Map<Serializable, ChangesetEntry> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<ChangesetEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<ChangesetEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<ChangesetEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<ChangesetEntry> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static ChangesetEntry update(ChangesetEntry changesetEntry) {
		return getPersistence().update(changesetEntry);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static ChangesetEntry update(
		ChangesetEntry changesetEntry, ServiceContext serviceContext) {

		return getPersistence().update(changesetEntry, serviceContext);
	}

	/**
	 * Returns an ordered range of all the changeset entries where changesetCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.changeset.model.impl.ChangesetEntryModelImpl</code>.
	 * </p>
	 *
	 * @param changesetCollectionId the changeset collection ID
	 * @param start the lower bound of the range of changeset entries
	 * @param end the upper bound of the range of changeset entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching changeset entries
	 */
	public static List<ChangesetEntry> findByChangesetCollectionId(
		long changesetCollectionId, int start, int end,
		OrderByComparator<ChangesetEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByChangesetCollectionId(
			changesetCollectionId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first changeset entry in the ordered set where changesetCollectionId = &#63;.
	 *
	 * @param changesetCollectionId the changeset collection ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching changeset entry
	 * @throws NoSuchEntryException if a matching changeset entry could not be found
	 */
	public static ChangesetEntry findByChangesetCollectionId_First(
			long changesetCollectionId,
			OrderByComparator<ChangesetEntry> orderByComparator)
		throws com.liferay.changeset.exception.NoSuchEntryException {

		return getPersistence().findByChangesetCollectionId_First(
			changesetCollectionId, orderByComparator);
	}

	/**
	 * Returns the first changeset entry in the ordered set where changesetCollectionId = &#63;.
	 *
	 * @param changesetCollectionId the changeset collection ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching changeset entry, or <code>null</code> if a matching changeset entry could not be found
	 */
	public static ChangesetEntry fetchByChangesetCollectionId_First(
		long changesetCollectionId,
		OrderByComparator<ChangesetEntry> orderByComparator) {

		return getPersistence().fetchByChangesetCollectionId_First(
			changesetCollectionId, orderByComparator);
	}

	/**
	 * Removes all the changeset entries where changesetCollectionId = &#63; from the database.
	 *
	 * @param changesetCollectionId the changeset collection ID
	 */
	public static void removeByChangesetCollectionId(
		long changesetCollectionId) {

		getPersistence().removeByChangesetCollectionId(changesetCollectionId);
	}

	/**
	 * Returns the number of changeset entries where changesetCollectionId = &#63;.
	 *
	 * @param changesetCollectionId the changeset collection ID
	 * @return the number of matching changeset entries
	 */
	public static int countByChangesetCollectionId(long changesetCollectionId) {
		return getPersistence().countByChangesetCollectionId(
			changesetCollectionId);
	}

	/**
	 * Returns an ordered range of all the changeset entries where changesetCollectionId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.changeset.model.impl.ChangesetEntryModelImpl</code>.
	 * </p>
	 *
	 * @param changesetCollectionId the changeset collection ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of changeset entries
	 * @param end the upper bound of the range of changeset entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching changeset entries
	 */
	public static List<ChangesetEntry> findByC_C(
		long changesetCollectionId, long classNameId, int start, int end,
		OrderByComparator<ChangesetEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_C(
			changesetCollectionId, classNameId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first changeset entry in the ordered set where changesetCollectionId = &#63; and classNameId = &#63;.
	 *
	 * @param changesetCollectionId the changeset collection ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching changeset entry
	 * @throws NoSuchEntryException if a matching changeset entry could not be found
	 */
	public static ChangesetEntry findByC_C_First(
			long changesetCollectionId, long classNameId,
			OrderByComparator<ChangesetEntry> orderByComparator)
		throws com.liferay.changeset.exception.NoSuchEntryException {

		return getPersistence().findByC_C_First(
			changesetCollectionId, classNameId, orderByComparator);
	}

	/**
	 * Returns the first changeset entry in the ordered set where changesetCollectionId = &#63; and classNameId = &#63;.
	 *
	 * @param changesetCollectionId the changeset collection ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching changeset entry, or <code>null</code> if a matching changeset entry could not be found
	 */
	public static ChangesetEntry fetchByC_C_First(
		long changesetCollectionId, long classNameId,
		OrderByComparator<ChangesetEntry> orderByComparator) {

		return getPersistence().fetchByC_C_First(
			changesetCollectionId, classNameId, orderByComparator);
	}

	/**
	 * Removes all the changeset entries where changesetCollectionId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param changesetCollectionId the changeset collection ID
	 * @param classNameId the class name ID
	 */
	public static void removeByC_C(
		long changesetCollectionId, long classNameId) {

		getPersistence().removeByC_C(changesetCollectionId, classNameId);
	}

	/**
	 * Returns the number of changeset entries where changesetCollectionId = &#63; and classNameId = &#63;.
	 *
	 * @param changesetCollectionId the changeset collection ID
	 * @param classNameId the class name ID
	 * @return the number of matching changeset entries
	 */
	public static int countByC_C(long changesetCollectionId, long classNameId) {
		return getPersistence().countByC_C(changesetCollectionId, classNameId);
	}

	/**
	 * Returns the changeset entry where changesetCollectionId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63; or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param changesetCollectionId the changeset collection ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @return the matching changeset entry
	 * @throws NoSuchEntryException if a matching changeset entry could not be found
	 */
	public static ChangesetEntry findByC_CERC_C(
			long changesetCollectionId, String classExternalReferenceCode,
			long classNameId)
		throws com.liferay.changeset.exception.NoSuchEntryException {

		return getPersistence().findByC_CERC_C(
			changesetCollectionId, classExternalReferenceCode, classNameId);
	}

	/**
	 * Returns the changeset entry where changesetCollectionId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param changesetCollectionId the changeset collection ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching changeset entry, or <code>null</code> if a matching changeset entry could not be found
	 */
	public static ChangesetEntry fetchByC_CERC_C(
		long changesetCollectionId, String classExternalReferenceCode,
		long classNameId, boolean useFinderCache) {

		return getPersistence().fetchByC_CERC_C(
			changesetCollectionId, classExternalReferenceCode, classNameId,
			useFinderCache);
	}

	/**
	 * Removes the changeset entry where changesetCollectionId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63; from the database.
	 *
	 * @param changesetCollectionId the changeset collection ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @return the changeset entry that was removed
	 */
	public static ChangesetEntry removeByC_CERC_C(
			long changesetCollectionId, String classExternalReferenceCode,
			long classNameId)
		throws com.liferay.changeset.exception.NoSuchEntryException {

		return getPersistence().removeByC_CERC_C(
			changesetCollectionId, classExternalReferenceCode, classNameId);
	}

	/**
	 * Returns the number of changeset entries where changesetCollectionId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63;.
	 *
	 * @param changesetCollectionId the changeset collection ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @return the number of matching changeset entries
	 */
	public static int countByC_CERC_C(
		long changesetCollectionId, String classExternalReferenceCode,
		long classNameId) {

		return getPersistence().countByC_CERC_C(
			changesetCollectionId, classExternalReferenceCode, classNameId);
	}

	/**
	 * Returns the changeset entry where changesetCollectionId = &#63; and classNameId = &#63; and classPK = &#63; or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param changesetCollectionId the changeset collection ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching changeset entry
	 * @throws NoSuchEntryException if a matching changeset entry could not be found
	 */
	public static ChangesetEntry findByC_C_C(
			long changesetCollectionId, long classNameId, long classPK)
		throws com.liferay.changeset.exception.NoSuchEntryException {

		return getPersistence().findByC_C_C(
			changesetCollectionId, classNameId, classPK);
	}

	/**
	 * Returns the changeset entry where changesetCollectionId = &#63; and classNameId = &#63; and classPK = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param changesetCollectionId the changeset collection ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching changeset entry, or <code>null</code> if a matching changeset entry could not be found
	 */
	public static ChangesetEntry fetchByC_C_C(
		long changesetCollectionId, long classNameId, long classPK,
		boolean useFinderCache) {

		return getPersistence().fetchByC_C_C(
			changesetCollectionId, classNameId, classPK, useFinderCache);
	}

	/**
	 * Removes the changeset entry where changesetCollectionId = &#63; and classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param changesetCollectionId the changeset collection ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the changeset entry that was removed
	 */
	public static ChangesetEntry removeByC_C_C(
			long changesetCollectionId, long classNameId, long classPK)
		throws com.liferay.changeset.exception.NoSuchEntryException {

		return getPersistence().removeByC_C_C(
			changesetCollectionId, classNameId, classPK);
	}

	/**
	 * Returns the number of changeset entries where changesetCollectionId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param changesetCollectionId the changeset collection ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching changeset entries
	 */
	public static int countByC_C_C(
		long changesetCollectionId, long classNameId, long classPK) {

		return getPersistence().countByC_C_C(
			changesetCollectionId, classNameId, classPK);
	}

	/**
	 * Creates a new changeset entry with the primary key. Does not add the changeset entry to the database.
	 *
	 * @param changesetEntryId the primary key for the new changeset entry
	 * @return the new changeset entry
	 */
	public static ChangesetEntry create(long changesetEntryId) {
		return getPersistence().create(changesetEntryId);
	}

	/**
	 * Removes the changeset entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param changesetEntryId the primary key of the changeset entry
	 * @return the changeset entry that was removed
	 * @throws NoSuchEntryException if a changeset entry with the primary key could not be found
	 */
	public static ChangesetEntry remove(long changesetEntryId)
		throws com.liferay.changeset.exception.NoSuchEntryException {

		return getPersistence().remove(changesetEntryId);
	}

	public static ChangesetEntry updateImpl(ChangesetEntry changesetEntry) {
		return getPersistence().updateImpl(changesetEntry);
	}

	/**
	 * Returns the changeset entry with the primary key or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param changesetEntryId the primary key of the changeset entry
	 * @return the changeset entry
	 * @throws NoSuchEntryException if a changeset entry with the primary key could not be found
	 */
	public static ChangesetEntry findByPrimaryKey(long changesetEntryId)
		throws com.liferay.changeset.exception.NoSuchEntryException {

		return getPersistence().findByPrimaryKey(changesetEntryId);
	}

	/**
	 * Returns the changeset entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param changesetEntryId the primary key of the changeset entry
	 * @return the changeset entry, or <code>null</code> if a changeset entry with the primary key could not be found
	 */
	public static ChangesetEntry fetchByPrimaryKey(long changesetEntryId) {
		return getPersistence().fetchByPrimaryKey(changesetEntryId);
	}

	/**
	 * Returns the changeset entry where changesetCollectionId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param changesetCollectionId the changeset collection ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @return the matching changeset entry, or <code>null</code> if a matching changeset entry could not be found
	 */
	public static ChangesetEntry fetchByC_CERC_C(
		long changesetCollectionId, String classExternalReferenceCode,
		long classNameId) {

		return getPersistence().fetchByC_CERC_C(
			changesetCollectionId, classExternalReferenceCode, classNameId);
	}

	/**
	 * Returns the changeset entry where changesetCollectionId = &#63; and classNameId = &#63; and classPK = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param changesetCollectionId the changeset collection ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching changeset entry, or <code>null</code> if a matching changeset entry could not be found
	 */
	public static ChangesetEntry fetchByC_C_C(
		long changesetCollectionId, long classNameId, long classPK) {

		return getPersistence().fetchByC_C_C(
			changesetCollectionId, classNameId, classPK);
	}

	/**
	 * Returns all the changeset entries where changesetCollectionId = &#63;.
	 *
	 * @param changesetCollectionId the changeset collection ID
	 * @return the matching changeset entries
	 */
	public static List<ChangesetEntry> findByChangesetCollectionId(
		long changesetCollectionId) {

		return getPersistence().findByChangesetCollectionId(
			changesetCollectionId);
	}

	/**
	 * Returns a range of all the changeset entries where changesetCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.changeset.model.impl.ChangesetEntryModelImpl</code>.
	 * </p>
	 *
	 * @param changesetCollectionId the changeset collection ID
	 * @param start the lower bound of the range of changeset entries
	 * @param end the upper bound of the range of changeset entries (not inclusive)
	 * @return the range of matching changeset entries
	 */
	public static List<ChangesetEntry> findByChangesetCollectionId(
		long changesetCollectionId, int start, int end) {

		return getPersistence().findByChangesetCollectionId(
			changesetCollectionId, start, end);
	}

	/**
	 * Returns an ordered range of all the changeset entries where changesetCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.changeset.model.impl.ChangesetEntryModelImpl</code>.
	 * </p>
	 *
	 * @param changesetCollectionId the changeset collection ID
	 * @param start the lower bound of the range of changeset entries
	 * @param end the upper bound of the range of changeset entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching changeset entries
	 */
	public static List<ChangesetEntry> findByChangesetCollectionId(
		long changesetCollectionId, int start, int end,
		OrderByComparator<ChangesetEntry> orderByComparator) {

		return getPersistence().findByChangesetCollectionId(
			changesetCollectionId, start, end, orderByComparator);
	}

	/**
	 * Returns all the changeset entries where changesetCollectionId = &#63; and classNameId = &#63;.
	 *
	 * @param changesetCollectionId the changeset collection ID
	 * @param classNameId the class name ID
	 * @return the matching changeset entries
	 */
	public static List<ChangesetEntry> findByC_C(
		long changesetCollectionId, long classNameId) {

		return getPersistence().findByC_C(changesetCollectionId, classNameId);
	}

	/**
	 * Returns a range of all the changeset entries where changesetCollectionId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.changeset.model.impl.ChangesetEntryModelImpl</code>.
	 * </p>
	 *
	 * @param changesetCollectionId the changeset collection ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of changeset entries
	 * @param end the upper bound of the range of changeset entries (not inclusive)
	 * @return the range of matching changeset entries
	 */
	public static List<ChangesetEntry> findByC_C(
		long changesetCollectionId, long classNameId, int start, int end) {

		return getPersistence().findByC_C(
			changesetCollectionId, classNameId, start, end);
	}

	/**
	 * Returns an ordered range of all the changeset entries where changesetCollectionId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.changeset.model.impl.ChangesetEntryModelImpl</code>.
	 * </p>
	 *
	 * @param changesetCollectionId the changeset collection ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of changeset entries
	 * @param end the upper bound of the range of changeset entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching changeset entries
	 */
	public static List<ChangesetEntry> findByC_C(
		long changesetCollectionId, long classNameId, int start, int end,
		OrderByComparator<ChangesetEntry> orderByComparator) {

		return getPersistence().findByC_C(
			changesetCollectionId, classNameId, start, end, orderByComparator);
	}

	public static ChangesetEntryPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(ChangesetEntryPersistence persistence) {
		_persistence = persistence;
	}

	private static volatile ChangesetEntryPersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:359638207