/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service.persistence;

import com.liferay.dynamic.data.mapping.model.DDMField;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the ddm field service. This utility wraps <code>com.liferay.dynamic.data.mapping.service.persistence.impl.DDMFieldPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see DDMFieldPersistence
 * @generated
 */
public class DDMFieldUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(List<DDMField> ddmFields) {
		getPersistence().cacheResult(ddmFields);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(DDMField ddmField) {
		getPersistence().cacheResult(ddmField);
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
	public static void clearCache(DDMField ddmField) {
		getPersistence().clearCache(ddmField);
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
	public static Map<Serializable, DDMField> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<DDMField> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<DDMField> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<DDMField> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<DDMField> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static DDMField update(DDMField ddmField) {
		return getPersistence().update(ddmField);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static DDMField update(
		DDMField ddmField, ServiceContext serviceContext) {

		return getPersistence().update(ddmField, serviceContext);
	}

	/**
	 * Returns an ordered range of all the ddm fields where storageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFieldModelImpl</code>.
	 * </p>
	 *
	 * @param storageId the storage ID
	 * @param start the lower bound of the range of ddm fields
	 * @param end the upper bound of the range of ddm fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm fields
	 */
	public static List<DDMField> findByStorageId(
		long storageId, int start, int end,
		OrderByComparator<DDMField> orderByComparator, boolean useFinderCache) {

		return getPersistence().findByStorageId(
			storageId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ddm field in the ordered set where storageId = &#63;.
	 *
	 * @param storageId the storage ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm field
	 * @throws NoSuchFieldException if a matching ddm field could not be found
	 */
	public static DDMField findByStorageId_First(
			long storageId, OrderByComparator<DDMField> orderByComparator)
		throws com.liferay.dynamic.data.mapping.exception.NoSuchFieldException {

		return getPersistence().findByStorageId_First(
			storageId, orderByComparator);
	}

	/**
	 * Returns the first ddm field in the ordered set where storageId = &#63;.
	 *
	 * @param storageId the storage ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm field, or <code>null</code> if a matching ddm field could not be found
	 */
	public static DDMField fetchByStorageId_First(
		long storageId, OrderByComparator<DDMField> orderByComparator) {

		return getPersistence().fetchByStorageId_First(
			storageId, orderByComparator);
	}

	/**
	 * Removes all the ddm fields where storageId = &#63; from the database.
	 *
	 * @param storageId the storage ID
	 */
	public static void removeByStorageId(long storageId) {
		getPersistence().removeByStorageId(storageId);
	}

	/**
	 * Returns the number of ddm fields where storageId = &#63;.
	 *
	 * @param storageId the storage ID
	 * @return the number of matching ddm fields
	 */
	public static int countByStorageId(long storageId) {
		return getPersistence().countByStorageId(storageId);
	}

	/**
	 * Returns an ordered range of all the ddm fields where storageId = &#63; and fieldName = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFieldModelImpl</code>.
	 * </p>
	 *
	 * @param storageId the storage ID
	 * @param fieldName the field name
	 * @param start the lower bound of the range of ddm fields
	 * @param end the upper bound of the range of ddm fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm fields
	 */
	public static List<DDMField> findByS_F(
		long storageId, String fieldName, int start, int end,
		OrderByComparator<DDMField> orderByComparator, boolean useFinderCache) {

		return getPersistence().findByS_F(
			storageId, fieldName, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first ddm field in the ordered set where storageId = &#63; and fieldName = &#63;.
	 *
	 * @param storageId the storage ID
	 * @param fieldName the field name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm field
	 * @throws NoSuchFieldException if a matching ddm field could not be found
	 */
	public static DDMField findByS_F_First(
			long storageId, String fieldName,
			OrderByComparator<DDMField> orderByComparator)
		throws com.liferay.dynamic.data.mapping.exception.NoSuchFieldException {

		return getPersistence().findByS_F_First(
			storageId, fieldName, orderByComparator);
	}

	/**
	 * Returns the first ddm field in the ordered set where storageId = &#63; and fieldName = &#63;.
	 *
	 * @param storageId the storage ID
	 * @param fieldName the field name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm field, or <code>null</code> if a matching ddm field could not be found
	 */
	public static DDMField fetchByS_F_First(
		long storageId, String fieldName,
		OrderByComparator<DDMField> orderByComparator) {

		return getPersistence().fetchByS_F_First(
			storageId, fieldName, orderByComparator);
	}

	/**
	 * Removes all the ddm fields where storageId = &#63; and fieldName = &#63; from the database.
	 *
	 * @param storageId the storage ID
	 * @param fieldName the field name
	 */
	public static void removeByS_F(long storageId, String fieldName) {
		getPersistence().removeByS_F(storageId, fieldName);
	}

	/**
	 * Returns the number of ddm fields where storageId = &#63; and fieldName = &#63;.
	 *
	 * @param storageId the storage ID
	 * @param fieldName the field name
	 * @return the number of matching ddm fields
	 */
	public static int countByS_F(long storageId, String fieldName) {
		return getPersistence().countByS_F(storageId, fieldName);
	}

	/**
	 * Returns the ddm field where storageId = &#63; and instanceId = &#63; or throws a <code>NoSuchFieldException</code> if it could not be found.
	 *
	 * @param storageId the storage ID
	 * @param instanceId the instance ID
	 * @return the matching ddm field
	 * @throws NoSuchFieldException if a matching ddm field could not be found
	 */
	public static DDMField findByS_I(long storageId, String instanceId)
		throws com.liferay.dynamic.data.mapping.exception.NoSuchFieldException {

		return getPersistence().findByS_I(storageId, instanceId);
	}

	/**
	 * Returns the ddm field where storageId = &#63; and instanceId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param storageId the storage ID
	 * @param instanceId the instance ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching ddm field, or <code>null</code> if a matching ddm field could not be found
	 */
	public static DDMField fetchByS_I(
		long storageId, String instanceId, boolean useFinderCache) {

		return getPersistence().fetchByS_I(
			storageId, instanceId, useFinderCache);
	}

	/**
	 * Removes the ddm field where storageId = &#63; and instanceId = &#63; from the database.
	 *
	 * @param storageId the storage ID
	 * @param instanceId the instance ID
	 * @return the ddm field that was removed
	 */
	public static DDMField removeByS_I(long storageId, String instanceId)
		throws com.liferay.dynamic.data.mapping.exception.NoSuchFieldException {

		return getPersistence().removeByS_I(storageId, instanceId);
	}

	/**
	 * Returns the number of ddm fields where storageId = &#63; and instanceId = &#63;.
	 *
	 * @param storageId the storage ID
	 * @param instanceId the instance ID
	 * @return the number of matching ddm fields
	 */
	public static int countByS_I(long storageId, String instanceId) {
		return getPersistence().countByS_I(storageId, instanceId);
	}

	/**
	 * Creates a new ddm field with the primary key. Does not add the ddm field to the database.
	 *
	 * @param fieldId the primary key for the new ddm field
	 * @return the new ddm field
	 */
	public static DDMField create(long fieldId) {
		return getPersistence().create(fieldId);
	}

	/**
	 * Removes the ddm field with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param fieldId the primary key of the ddm field
	 * @return the ddm field that was removed
	 * @throws NoSuchFieldException if a ddm field with the primary key could not be found
	 */
	public static DDMField remove(long fieldId)
		throws com.liferay.dynamic.data.mapping.exception.NoSuchFieldException {

		return getPersistence().remove(fieldId);
	}

	public static DDMField updateImpl(DDMField ddmField) {
		return getPersistence().updateImpl(ddmField);
	}

	/**
	 * Returns the ddm field with the primary key or throws a <code>NoSuchFieldException</code> if it could not be found.
	 *
	 * @param fieldId the primary key of the ddm field
	 * @return the ddm field
	 * @throws NoSuchFieldException if a ddm field with the primary key could not be found
	 */
	public static DDMField findByPrimaryKey(long fieldId)
		throws com.liferay.dynamic.data.mapping.exception.NoSuchFieldException {

		return getPersistence().findByPrimaryKey(fieldId);
	}

	/**
	 * Returns the ddm field with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param fieldId the primary key of the ddm field
	 * @return the ddm field, or <code>null</code> if a ddm field with the primary key could not be found
	 */
	public static DDMField fetchByPrimaryKey(long fieldId) {
		return getPersistence().fetchByPrimaryKey(fieldId);
	}

	/**
	 * Returns the ddm field where storageId = &#63; and instanceId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param storageId the storage ID
	 * @param instanceId the instance ID
	 * @return the matching ddm field, or <code>null</code> if a matching ddm field could not be found
	 */
	public static DDMField fetchByS_I(long storageId, String instanceId) {
		return getPersistence().fetchByS_I(storageId, instanceId);
	}

	/**
	 * Returns all the ddm fields where storageId = &#63;.
	 *
	 * @param storageId the storage ID
	 * @return the matching ddm fields
	 */
	public static List<DDMField> findByStorageId(long storageId) {
		return getPersistence().findByStorageId(storageId);
	}

	/**
	 * Returns a range of all the ddm fields where storageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFieldModelImpl</code>.
	 * </p>
	 *
	 * @param storageId the storage ID
	 * @param start the lower bound of the range of ddm fields
	 * @param end the upper bound of the range of ddm fields (not inclusive)
	 * @return the range of matching ddm fields
	 */
	public static List<DDMField> findByStorageId(
		long storageId, int start, int end) {

		return getPersistence().findByStorageId(storageId, start, end);
	}

	/**
	 * Returns an ordered range of all the ddm fields where storageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFieldModelImpl</code>.
	 * </p>
	 *
	 * @param storageId the storage ID
	 * @param start the lower bound of the range of ddm fields
	 * @param end the upper bound of the range of ddm fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm fields
	 */
	public static List<DDMField> findByStorageId(
		long storageId, int start, int end,
		OrderByComparator<DDMField> orderByComparator) {

		return getPersistence().findByStorageId(
			storageId, start, end, orderByComparator);
	}

	/**
	 * Returns all the ddm fields where storageId = &#63; and fieldName = &#63;.
	 *
	 * @param storageId the storage ID
	 * @param fieldName the field name
	 * @return the matching ddm fields
	 */
	public static List<DDMField> findByS_F(long storageId, String fieldName) {
		return getPersistence().findByS_F(storageId, fieldName);
	}

	/**
	 * Returns a range of all the ddm fields where storageId = &#63; and fieldName = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFieldModelImpl</code>.
	 * </p>
	 *
	 * @param storageId the storage ID
	 * @param fieldName the field name
	 * @param start the lower bound of the range of ddm fields
	 * @param end the upper bound of the range of ddm fields (not inclusive)
	 * @return the range of matching ddm fields
	 */
	public static List<DDMField> findByS_F(
		long storageId, String fieldName, int start, int end) {

		return getPersistence().findByS_F(storageId, fieldName, start, end);
	}

	/**
	 * Returns an ordered range of all the ddm fields where storageId = &#63; and fieldName = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFieldModelImpl</code>.
	 * </p>
	 *
	 * @param storageId the storage ID
	 * @param fieldName the field name
	 * @param start the lower bound of the range of ddm fields
	 * @param end the upper bound of the range of ddm fields (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm fields
	 */
	public static List<DDMField> findByS_F(
		long storageId, String fieldName, int start, int end,
		OrderByComparator<DDMField> orderByComparator) {

		return getPersistence().findByS_F(
			storageId, fieldName, start, end, orderByComparator);
	}

	public static DDMFieldPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(DDMFieldPersistence persistence) {
		_persistence = persistence;
	}

	private static volatile DDMFieldPersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:-450786972