/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service.persistence;

import com.liferay.dynamic.data.mapping.model.DDMFieldAttribute;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the ddm field attribute service. This utility wraps <code>com.liferay.dynamic.data.mapping.service.persistence.impl.DDMFieldAttributePersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see DDMFieldAttributePersistence
 * @generated
 */
public class DDMFieldAttributeUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(List<DDMFieldAttribute> ddmFieldAttributes) {
		getPersistence().cacheResult(ddmFieldAttributes);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(DDMFieldAttribute ddmFieldAttribute) {
		getPersistence().cacheResult(ddmFieldAttribute);
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
	public static void clearCache(DDMFieldAttribute ddmFieldAttribute) {
		getPersistence().clearCache(ddmFieldAttribute);
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
	public static Map<Serializable, DDMFieldAttribute> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<DDMFieldAttribute> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<DDMFieldAttribute> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<DDMFieldAttribute> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<DDMFieldAttribute> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static DDMFieldAttribute update(
		DDMFieldAttribute ddmFieldAttribute) {

		return getPersistence().update(ddmFieldAttribute);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static DDMFieldAttribute update(
		DDMFieldAttribute ddmFieldAttribute, ServiceContext serviceContext) {

		return getPersistence().update(ddmFieldAttribute, serviceContext);
	}

	/**
	 * Returns an ordered range of all the ddm field attributes where storageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFieldAttributeModelImpl</code>.
	 * </p>
	 *
	 * @param storageId the storage ID
	 * @param start the lower bound of the range of ddm field attributes
	 * @param end the upper bound of the range of ddm field attributes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm field attributes
	 */
	public static List<DDMFieldAttribute> findByStorageId(
		long storageId, int start, int end,
		OrderByComparator<DDMFieldAttribute> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByStorageId(
			storageId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ddm field attribute in the ordered set where storageId = &#63;.
	 *
	 * @param storageId the storage ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm field attribute
	 * @throws NoSuchFieldAttributeException if a matching ddm field attribute could not be found
	 */
	public static DDMFieldAttribute findByStorageId_First(
			long storageId,
			OrderByComparator<DDMFieldAttribute> orderByComparator)
		throws com.liferay.dynamic.data.mapping.exception.
			NoSuchFieldAttributeException {

		return getPersistence().findByStorageId_First(
			storageId, orderByComparator);
	}

	/**
	 * Returns the first ddm field attribute in the ordered set where storageId = &#63;.
	 *
	 * @param storageId the storage ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm field attribute, or <code>null</code> if a matching ddm field attribute could not be found
	 */
	public static DDMFieldAttribute fetchByStorageId_First(
		long storageId,
		OrderByComparator<DDMFieldAttribute> orderByComparator) {

		return getPersistence().fetchByStorageId_First(
			storageId, orderByComparator);
	}

	/**
	 * Removes all the ddm field attributes where storageId = &#63; from the database.
	 *
	 * @param storageId the storage ID
	 */
	public static void removeByStorageId(long storageId) {
		getPersistence().removeByStorageId(storageId);
	}

	/**
	 * Returns the number of ddm field attributes where storageId = &#63;.
	 *
	 * @param storageId the storage ID
	 * @return the number of matching ddm field attributes
	 */
	public static int countByStorageId(long storageId) {
		return getPersistence().countByStorageId(storageId);
	}

	/**
	 * Returns an ordered range of all the ddm field attributes where storageId = &#63; and attributeName = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFieldAttributeModelImpl</code>.
	 * </p>
	 *
	 * @param storageId the storage ID
	 * @param attributeName the attribute name
	 * @param start the lower bound of the range of ddm field attributes
	 * @param end the upper bound of the range of ddm field attributes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm field attributes
	 */
	public static List<DDMFieldAttribute> findByS_AN(
		long storageId, String attributeName, int start, int end,
		OrderByComparator<DDMFieldAttribute> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByS_AN(
			storageId, attributeName, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first ddm field attribute in the ordered set where storageId = &#63; and attributeName = &#63;.
	 *
	 * @param storageId the storage ID
	 * @param attributeName the attribute name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm field attribute
	 * @throws NoSuchFieldAttributeException if a matching ddm field attribute could not be found
	 */
	public static DDMFieldAttribute findByS_AN_First(
			long storageId, String attributeName,
			OrderByComparator<DDMFieldAttribute> orderByComparator)
		throws com.liferay.dynamic.data.mapping.exception.
			NoSuchFieldAttributeException {

		return getPersistence().findByS_AN_First(
			storageId, attributeName, orderByComparator);
	}

	/**
	 * Returns the first ddm field attribute in the ordered set where storageId = &#63; and attributeName = &#63;.
	 *
	 * @param storageId the storage ID
	 * @param attributeName the attribute name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm field attribute, or <code>null</code> if a matching ddm field attribute could not be found
	 */
	public static DDMFieldAttribute fetchByS_AN_First(
		long storageId, String attributeName,
		OrderByComparator<DDMFieldAttribute> orderByComparator) {

		return getPersistence().fetchByS_AN_First(
			storageId, attributeName, orderByComparator);
	}

	/**
	 * Removes all the ddm field attributes where storageId = &#63; and attributeName = &#63; from the database.
	 *
	 * @param storageId the storage ID
	 * @param attributeName the attribute name
	 */
	public static void removeByS_AN(long storageId, String attributeName) {
		getPersistence().removeByS_AN(storageId, attributeName);
	}

	/**
	 * Returns the number of ddm field attributes where storageId = &#63; and attributeName = &#63;.
	 *
	 * @param storageId the storage ID
	 * @param attributeName the attribute name
	 * @return the number of matching ddm field attributes
	 */
	public static int countByS_AN(long storageId, String attributeName) {
		return getPersistence().countByS_AN(storageId, attributeName);
	}

	/**
	 * Returns an ordered range of all the ddm field attributes where storageId = &#63; and languageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFieldAttributeModelImpl</code>.
	 * </p>
	 *
	 * @param storageId the storage ID
	 * @param languageId the language ID
	 * @param start the lower bound of the range of ddm field attributes
	 * @param end the upper bound of the range of ddm field attributes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm field attributes
	 */
	public static List<DDMFieldAttribute> findByS_L(
		long storageId, String languageId, int start, int end,
		OrderByComparator<DDMFieldAttribute> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByS_L(
			storageId, languageId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first ddm field attribute in the ordered set where storageId = &#63; and languageId = &#63;.
	 *
	 * @param storageId the storage ID
	 * @param languageId the language ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm field attribute
	 * @throws NoSuchFieldAttributeException if a matching ddm field attribute could not be found
	 */
	public static DDMFieldAttribute findByS_L_First(
			long storageId, String languageId,
			OrderByComparator<DDMFieldAttribute> orderByComparator)
		throws com.liferay.dynamic.data.mapping.exception.
			NoSuchFieldAttributeException {

		return getPersistence().findByS_L_First(
			storageId, languageId, orderByComparator);
	}

	/**
	 * Returns the first ddm field attribute in the ordered set where storageId = &#63; and languageId = &#63;.
	 *
	 * @param storageId the storage ID
	 * @param languageId the language ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm field attribute, or <code>null</code> if a matching ddm field attribute could not be found
	 */
	public static DDMFieldAttribute fetchByS_L_First(
		long storageId, String languageId,
		OrderByComparator<DDMFieldAttribute> orderByComparator) {

		return getPersistence().fetchByS_L_First(
			storageId, languageId, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the ddm field attributes where storageId = &#63; and languageId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFieldAttributeModelImpl</code>.
	 * </p>
	 *
	 * @param storageId the storage ID
	 * @param languageIds the language IDs
	 * @param start the lower bound of the range of ddm field attributes
	 * @param end the upper bound of the range of ddm field attributes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm field attributes
	 */
	public static List<DDMFieldAttribute> findByS_L(
		long storageId, String[] languageIds, int start, int end,
		OrderByComparator<DDMFieldAttribute> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByS_L(
			storageId, languageIds, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Removes all the ddm field attributes where storageId = &#63; and languageId = &#63; from the database.
	 *
	 * @param storageId the storage ID
	 * @param languageId the language ID
	 */
	public static void removeByS_L(long storageId, String languageId) {
		getPersistence().removeByS_L(storageId, languageId);
	}

	/**
	 * Returns the number of ddm field attributes where storageId = &#63; and languageId = &#63;.
	 *
	 * @param storageId the storage ID
	 * @param languageId the language ID
	 * @return the number of matching ddm field attributes
	 */
	public static int countByS_L(long storageId, String languageId) {
		return getPersistence().countByS_L(storageId, languageId);
	}

	/**
	 * Returns the number of ddm field attributes where storageId = &#63; and languageId = any &#63;.
	 *
	 * @param storageId the storage ID
	 * @param languageIds the language IDs
	 * @return the number of matching ddm field attributes
	 */
	public static int countByS_L(long storageId, String[] languageIds) {
		return getPersistence().countByS_L(storageId, languageIds);
	}

	/**
	 * Returns the ddm field attribute where fieldId = &#63; and attributeName = &#63; and languageId = &#63; or throws a <code>NoSuchFieldAttributeException</code> if it could not be found.
	 *
	 * @param fieldId the field ID
	 * @param attributeName the attribute name
	 * @param languageId the language ID
	 * @return the matching ddm field attribute
	 * @throws NoSuchFieldAttributeException if a matching ddm field attribute could not be found
	 */
	public static DDMFieldAttribute findByF_AN_L(
			long fieldId, String attributeName, String languageId)
		throws com.liferay.dynamic.data.mapping.exception.
			NoSuchFieldAttributeException {

		return getPersistence().findByF_AN_L(
			fieldId, attributeName, languageId);
	}

	/**
	 * Returns the ddm field attribute where fieldId = &#63; and attributeName = &#63; and languageId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param fieldId the field ID
	 * @param attributeName the attribute name
	 * @param languageId the language ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching ddm field attribute, or <code>null</code> if a matching ddm field attribute could not be found
	 */
	public static DDMFieldAttribute fetchByF_AN_L(
		long fieldId, String attributeName, String languageId,
		boolean useFinderCache) {

		return getPersistence().fetchByF_AN_L(
			fieldId, attributeName, languageId, useFinderCache);
	}

	/**
	 * Removes the ddm field attribute where fieldId = &#63; and attributeName = &#63; and languageId = &#63; from the database.
	 *
	 * @param fieldId the field ID
	 * @param attributeName the attribute name
	 * @param languageId the language ID
	 * @return the ddm field attribute that was removed
	 */
	public static DDMFieldAttribute removeByF_AN_L(
			long fieldId, String attributeName, String languageId)
		throws com.liferay.dynamic.data.mapping.exception.
			NoSuchFieldAttributeException {

		return getPersistence().removeByF_AN_L(
			fieldId, attributeName, languageId);
	}

	/**
	 * Returns the number of ddm field attributes where fieldId = &#63; and attributeName = &#63; and languageId = &#63;.
	 *
	 * @param fieldId the field ID
	 * @param attributeName the attribute name
	 * @param languageId the language ID
	 * @return the number of matching ddm field attributes
	 */
	public static int countByF_AN_L(
		long fieldId, String attributeName, String languageId) {

		return getPersistence().countByF_AN_L(
			fieldId, attributeName, languageId);
	}

	/**
	 * Creates a new ddm field attribute with the primary key. Does not add the ddm field attribute to the database.
	 *
	 * @param fieldAttributeId the primary key for the new ddm field attribute
	 * @return the new ddm field attribute
	 */
	public static DDMFieldAttribute create(long fieldAttributeId) {
		return getPersistence().create(fieldAttributeId);
	}

	/**
	 * Removes the ddm field attribute with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param fieldAttributeId the primary key of the ddm field attribute
	 * @return the ddm field attribute that was removed
	 * @throws NoSuchFieldAttributeException if a ddm field attribute with the primary key could not be found
	 */
	public static DDMFieldAttribute remove(long fieldAttributeId)
		throws com.liferay.dynamic.data.mapping.exception.
			NoSuchFieldAttributeException {

		return getPersistence().remove(fieldAttributeId);
	}

	public static DDMFieldAttribute updateImpl(
		DDMFieldAttribute ddmFieldAttribute) {

		return getPersistence().updateImpl(ddmFieldAttribute);
	}

	/**
	 * Returns the ddm field attribute with the primary key or throws a <code>NoSuchFieldAttributeException</code> if it could not be found.
	 *
	 * @param fieldAttributeId the primary key of the ddm field attribute
	 * @return the ddm field attribute
	 * @throws NoSuchFieldAttributeException if a ddm field attribute with the primary key could not be found
	 */
	public static DDMFieldAttribute findByPrimaryKey(long fieldAttributeId)
		throws com.liferay.dynamic.data.mapping.exception.
			NoSuchFieldAttributeException {

		return getPersistence().findByPrimaryKey(fieldAttributeId);
	}

	/**
	 * Returns the ddm field attribute with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param fieldAttributeId the primary key of the ddm field attribute
	 * @return the ddm field attribute, or <code>null</code> if a ddm field attribute with the primary key could not be found
	 */
	public static DDMFieldAttribute fetchByPrimaryKey(long fieldAttributeId) {
		return getPersistence().fetchByPrimaryKey(fieldAttributeId);
	}

	/**
	 * Returns the ddm field attribute where fieldId = &#63; and attributeName = &#63; and languageId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param fieldId the field ID
	 * @param attributeName the attribute name
	 * @param languageId the language ID
	 * @return the matching ddm field attribute, or <code>null</code> if a matching ddm field attribute could not be found
	 */
	public static DDMFieldAttribute fetchByF_AN_L(
		long fieldId, String attributeName, String languageId) {

		return getPersistence().fetchByF_AN_L(
			fieldId, attributeName, languageId);
	}

	/**
	 * Returns all the ddm field attributes where storageId = &#63;.
	 *
	 * @param storageId the storage ID
	 * @return the matching ddm field attributes
	 */
	public static List<DDMFieldAttribute> findByStorageId(long storageId) {
		return getPersistence().findByStorageId(storageId);
	}

	/**
	 * Returns a range of all the ddm field attributes where storageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFieldAttributeModelImpl</code>.
	 * </p>
	 *
	 * @param storageId the storage ID
	 * @param start the lower bound of the range of ddm field attributes
	 * @param end the upper bound of the range of ddm field attributes (not inclusive)
	 * @return the range of matching ddm field attributes
	 */
	public static List<DDMFieldAttribute> findByStorageId(
		long storageId, int start, int end) {

		return getPersistence().findByStorageId(storageId, start, end);
	}

	/**
	 * Returns an ordered range of all the ddm field attributes where storageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFieldAttributeModelImpl</code>.
	 * </p>
	 *
	 * @param storageId the storage ID
	 * @param start the lower bound of the range of ddm field attributes
	 * @param end the upper bound of the range of ddm field attributes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm field attributes
	 */
	public static List<DDMFieldAttribute> findByStorageId(
		long storageId, int start, int end,
		OrderByComparator<DDMFieldAttribute> orderByComparator) {

		return getPersistence().findByStorageId(
			storageId, start, end, orderByComparator);
	}

	/**
	 * Returns all the ddm field attributes where storageId = &#63; and attributeName = &#63;.
	 *
	 * @param storageId the storage ID
	 * @param attributeName the attribute name
	 * @return the matching ddm field attributes
	 */
	public static List<DDMFieldAttribute> findByS_AN(
		long storageId, String attributeName) {

		return getPersistence().findByS_AN(storageId, attributeName);
	}

	/**
	 * Returns a range of all the ddm field attributes where storageId = &#63; and attributeName = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFieldAttributeModelImpl</code>.
	 * </p>
	 *
	 * @param storageId the storage ID
	 * @param attributeName the attribute name
	 * @param start the lower bound of the range of ddm field attributes
	 * @param end the upper bound of the range of ddm field attributes (not inclusive)
	 * @return the range of matching ddm field attributes
	 */
	public static List<DDMFieldAttribute> findByS_AN(
		long storageId, String attributeName, int start, int end) {

		return getPersistence().findByS_AN(
			storageId, attributeName, start, end);
	}

	/**
	 * Returns an ordered range of all the ddm field attributes where storageId = &#63; and attributeName = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFieldAttributeModelImpl</code>.
	 * </p>
	 *
	 * @param storageId the storage ID
	 * @param attributeName the attribute name
	 * @param start the lower bound of the range of ddm field attributes
	 * @param end the upper bound of the range of ddm field attributes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm field attributes
	 */
	public static List<DDMFieldAttribute> findByS_AN(
		long storageId, String attributeName, int start, int end,
		OrderByComparator<DDMFieldAttribute> orderByComparator) {

		return getPersistence().findByS_AN(
			storageId, attributeName, start, end, orderByComparator);
	}

	/**
	 * Returns all the ddm field attributes where storageId = &#63; and languageId = &#63;.
	 *
	 * @param storageId the storage ID
	 * @param languageId the language ID
	 * @return the matching ddm field attributes
	 */
	public static List<DDMFieldAttribute> findByS_L(
		long storageId, String languageId) {

		return getPersistence().findByS_L(storageId, languageId);
	}

	/**
	 * Returns a range of all the ddm field attributes where storageId = &#63; and languageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFieldAttributeModelImpl</code>.
	 * </p>
	 *
	 * @param storageId the storage ID
	 * @param languageId the language ID
	 * @param start the lower bound of the range of ddm field attributes
	 * @param end the upper bound of the range of ddm field attributes (not inclusive)
	 * @return the range of matching ddm field attributes
	 */
	public static List<DDMFieldAttribute> findByS_L(
		long storageId, String languageId, int start, int end) {

		return getPersistence().findByS_L(storageId, languageId, start, end);
	}

	/**
	 * Returns an ordered range of all the ddm field attributes where storageId = &#63; and languageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFieldAttributeModelImpl</code>.
	 * </p>
	 *
	 * @param storageId the storage ID
	 * @param languageId the language ID
	 * @param start the lower bound of the range of ddm field attributes
	 * @param end the upper bound of the range of ddm field attributes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm field attributes
	 */
	public static List<DDMFieldAttribute> findByS_L(
		long storageId, String languageId, int start, int end,
		OrderByComparator<DDMFieldAttribute> orderByComparator) {

		return getPersistence().findByS_L(
			storageId, languageId, start, end, orderByComparator);
	}

	/**
	 * Returns all the ddm field attributes where storageId = &#63; and languageId = any &#63;.
	 *
	 * @param storageId the storage ID
	 * @param languageIds the language IDs
	 * @return the matching ddm field attributes
	 */
	public static List<DDMFieldAttribute> findByS_L(
		long storageId, String[] languageIds) {

		return getPersistence().findByS_L(storageId, languageIds);
	}

	/**
	 * Returns a range of all the ddm field attributes where storageId = &#63; and languageId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFieldAttributeModelImpl</code>.
	 * </p>
	 *
	 * @param storageId the storage ID
	 * @param languageIds the language IDs
	 * @param start the lower bound of the range of ddm field attributes
	 * @param end the upper bound of the range of ddm field attributes (not inclusive)
	 * @return the range of matching ddm field attributes
	 */
	public static List<DDMFieldAttribute> findByS_L(
		long storageId, String[] languageIds, int start, int end) {

		return getPersistence().findByS_L(storageId, languageIds, start, end);
	}

	/**
	 * Returns an ordered range of all the ddm field attributes where storageId = &#63; and languageId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFieldAttributeModelImpl</code>.
	 * </p>
	 *
	 * @param storageId the storage ID
	 * @param languageIds the language IDs
	 * @param start the lower bound of the range of ddm field attributes
	 * @param end the upper bound of the range of ddm field attributes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm field attributes
	 */
	public static List<DDMFieldAttribute> findByS_L(
		long storageId, String[] languageIds, int start, int end,
		OrderByComparator<DDMFieldAttribute> orderByComparator) {

		return getPersistence().findByS_L(
			storageId, languageIds, start, end, orderByComparator);
	}

	public static DDMFieldAttributePersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(
		DDMFieldAttributePersistence persistence) {

		_persistence = persistence;
	}

	private static volatile DDMFieldAttributePersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1897648859