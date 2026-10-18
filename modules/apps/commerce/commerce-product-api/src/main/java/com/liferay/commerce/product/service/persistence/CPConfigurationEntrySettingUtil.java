/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.persistence;

import com.liferay.commerce.product.model.CPConfigurationEntrySetting;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the cp configuration entry setting service. This utility wraps <code>com.liferay.commerce.product.service.persistence.impl.CPConfigurationEntrySettingPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @see CPConfigurationEntrySettingPersistence
 * @generated
 */
public class CPConfigurationEntrySettingUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(
		List<CPConfigurationEntrySetting> cpConfigurationEntrySettings) {

		getPersistence().cacheResult(cpConfigurationEntrySettings);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(
		CPConfigurationEntrySetting cpConfigurationEntrySetting) {

		getPersistence().cacheResult(cpConfigurationEntrySetting);
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
		CPConfigurationEntrySetting cpConfigurationEntrySetting) {

		getPersistence().clearCache(cpConfigurationEntrySetting);
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
	public static Map<Serializable, CPConfigurationEntrySetting>
		fetchByPrimaryKeys(Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<CPConfigurationEntrySetting> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<CPConfigurationEntrySetting> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<CPConfigurationEntrySetting> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<CPConfigurationEntrySetting> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static CPConfigurationEntrySetting update(
		CPConfigurationEntrySetting cpConfigurationEntrySetting) {

		return getPersistence().update(cpConfigurationEntrySetting);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static CPConfigurationEntrySetting update(
		CPConfigurationEntrySetting cpConfigurationEntrySetting,
		ServiceContext serviceContext) {

		return getPersistence().update(
			cpConfigurationEntrySetting, serviceContext);
	}

	/**
	 * Returns an ordered range of all the cp configuration entry settings where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPConfigurationEntrySettingModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of cp configuration entry settings
	 * @param end the upper bound of the range of cp configuration entry settings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp configuration entry settings
	 */
	public static List<CPConfigurationEntrySetting> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CPConfigurationEntrySetting> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid(
			uuid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp configuration entry setting in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp configuration entry setting
	 * @throws NoSuchCPConfigurationEntrySettingException if a matching cp configuration entry setting could not be found
	 */
	public static CPConfigurationEntrySetting findByUuid_First(
			String uuid,
			OrderByComparator<CPConfigurationEntrySetting> orderByComparator)
		throws com.liferay.commerce.product.exception.
			NoSuchCPConfigurationEntrySettingException {

		return getPersistence().findByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the first cp configuration entry setting in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp configuration entry setting, or <code>null</code> if a matching cp configuration entry setting could not be found
	 */
	public static CPConfigurationEntrySetting fetchByUuid_First(
		String uuid,
		OrderByComparator<CPConfigurationEntrySetting> orderByComparator) {

		return getPersistence().fetchByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Removes all the cp configuration entry settings where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public static void removeByUuid(String uuid) {
		getPersistence().removeByUuid(uuid);
	}

	/**
	 * Returns the number of cp configuration entry settings where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching cp configuration entry settings
	 */
	public static int countByUuid(String uuid) {
		return getPersistence().countByUuid(uuid);
	}

	/**
	 * Returns the cp configuration entry setting where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchCPConfigurationEntrySettingException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching cp configuration entry setting
	 * @throws NoSuchCPConfigurationEntrySettingException if a matching cp configuration entry setting could not be found
	 */
	public static CPConfigurationEntrySetting findByUUID_G(
			String uuid, long groupId)
		throws com.liferay.commerce.product.exception.
			NoSuchCPConfigurationEntrySettingException {

		return getPersistence().findByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the cp configuration entry setting where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp configuration entry setting, or <code>null</code> if a matching cp configuration entry setting could not be found
	 */
	public static CPConfigurationEntrySetting fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return getPersistence().fetchByUUID_G(uuid, groupId, useFinderCache);
	}

	/**
	 * Removes the cp configuration entry setting where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the cp configuration entry setting that was removed
	 */
	public static CPConfigurationEntrySetting removeByUUID_G(
			String uuid, long groupId)
		throws com.liferay.commerce.product.exception.
			NoSuchCPConfigurationEntrySettingException {

		return getPersistence().removeByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the number of cp configuration entry settings where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching cp configuration entry settings
	 */
	public static int countByUUID_G(String uuid, long groupId) {
		return getPersistence().countByUUID_G(uuid, groupId);
	}

	/**
	 * Returns an ordered range of all the cp configuration entry settings where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPConfigurationEntrySettingModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp configuration entry settings
	 * @param end the upper bound of the range of cp configuration entry settings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp configuration entry settings
	 */
	public static List<CPConfigurationEntrySetting> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CPConfigurationEntrySetting> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp configuration entry setting in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp configuration entry setting
	 * @throws NoSuchCPConfigurationEntrySettingException if a matching cp configuration entry setting could not be found
	 */
	public static CPConfigurationEntrySetting findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<CPConfigurationEntrySetting> orderByComparator)
		throws com.liferay.commerce.product.exception.
			NoSuchCPConfigurationEntrySettingException {

		return getPersistence().findByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the first cp configuration entry setting in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp configuration entry setting, or <code>null</code> if a matching cp configuration entry setting could not be found
	 */
	public static CPConfigurationEntrySetting fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<CPConfigurationEntrySetting> orderByComparator) {

		return getPersistence().fetchByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Removes all the cp configuration entry settings where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public static void removeByUuid_C(String uuid, long companyId) {
		getPersistence().removeByUuid_C(uuid, companyId);
	}

	/**
	 * Returns the number of cp configuration entry settings where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching cp configuration entry settings
	 */
	public static int countByUuid_C(String uuid, long companyId) {
		return getPersistence().countByUuid_C(uuid, companyId);
	}

	/**
	 * Returns the cp configuration entry setting where CPConfigurationEntryId = &#63; and type = &#63; or throws a <code>NoSuchCPConfigurationEntrySettingException</code> if it could not be found.
	 *
	 * @param CPConfigurationEntryId the cp configuration entry ID
	 * @param type the type
	 * @return the matching cp configuration entry setting
	 * @throws NoSuchCPConfigurationEntrySettingException if a matching cp configuration entry setting could not be found
	 */
	public static CPConfigurationEntrySetting findByC_T(
			long CPConfigurationEntryId, int type)
		throws com.liferay.commerce.product.exception.
			NoSuchCPConfigurationEntrySettingException {

		return getPersistence().findByC_T(CPConfigurationEntryId, type);
	}

	/**
	 * Returns the cp configuration entry setting where CPConfigurationEntryId = &#63; and type = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param CPConfigurationEntryId the cp configuration entry ID
	 * @param type the type
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp configuration entry setting, or <code>null</code> if a matching cp configuration entry setting could not be found
	 */
	public static CPConfigurationEntrySetting fetchByC_T(
		long CPConfigurationEntryId, int type, boolean useFinderCache) {

		return getPersistence().fetchByC_T(
			CPConfigurationEntryId, type, useFinderCache);
	}

	/**
	 * Removes the cp configuration entry setting where CPConfigurationEntryId = &#63; and type = &#63; from the database.
	 *
	 * @param CPConfigurationEntryId the cp configuration entry ID
	 * @param type the type
	 * @return the cp configuration entry setting that was removed
	 */
	public static CPConfigurationEntrySetting removeByC_T(
			long CPConfigurationEntryId, int type)
		throws com.liferay.commerce.product.exception.
			NoSuchCPConfigurationEntrySettingException {

		return getPersistence().removeByC_T(CPConfigurationEntryId, type);
	}

	/**
	 * Returns the number of cp configuration entry settings where CPConfigurationEntryId = &#63; and type = &#63;.
	 *
	 * @param CPConfigurationEntryId the cp configuration entry ID
	 * @param type the type
	 * @return the number of matching cp configuration entry settings
	 */
	public static int countByC_T(long CPConfigurationEntryId, int type) {
		return getPersistence().countByC_T(CPConfigurationEntryId, type);
	}

	/**
	 * Creates a new cp configuration entry setting with the primary key. Does not add the cp configuration entry setting to the database.
	 *
	 * @param CPConfigurationEntrySettingId the primary key for the new cp configuration entry setting
	 * @return the new cp configuration entry setting
	 */
	public static CPConfigurationEntrySetting create(
		long CPConfigurationEntrySettingId) {

		return getPersistence().create(CPConfigurationEntrySettingId);
	}

	/**
	 * Removes the cp configuration entry setting with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param CPConfigurationEntrySettingId the primary key of the cp configuration entry setting
	 * @return the cp configuration entry setting that was removed
	 * @throws NoSuchCPConfigurationEntrySettingException if a cp configuration entry setting with the primary key could not be found
	 */
	public static CPConfigurationEntrySetting remove(
			long CPConfigurationEntrySettingId)
		throws com.liferay.commerce.product.exception.
			NoSuchCPConfigurationEntrySettingException {

		return getPersistence().remove(CPConfigurationEntrySettingId);
	}

	public static CPConfigurationEntrySetting updateImpl(
		CPConfigurationEntrySetting cpConfigurationEntrySetting) {

		return getPersistence().updateImpl(cpConfigurationEntrySetting);
	}

	/**
	 * Returns the cp configuration entry setting with the primary key or throws a <code>NoSuchCPConfigurationEntrySettingException</code> if it could not be found.
	 *
	 * @param CPConfigurationEntrySettingId the primary key of the cp configuration entry setting
	 * @return the cp configuration entry setting
	 * @throws NoSuchCPConfigurationEntrySettingException if a cp configuration entry setting with the primary key could not be found
	 */
	public static CPConfigurationEntrySetting findByPrimaryKey(
			long CPConfigurationEntrySettingId)
		throws com.liferay.commerce.product.exception.
			NoSuchCPConfigurationEntrySettingException {

		return getPersistence().findByPrimaryKey(CPConfigurationEntrySettingId);
	}

	/**
	 * Returns the cp configuration entry setting with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param CPConfigurationEntrySettingId the primary key of the cp configuration entry setting
	 * @return the cp configuration entry setting, or <code>null</code> if a cp configuration entry setting with the primary key could not be found
	 */
	public static CPConfigurationEntrySetting fetchByPrimaryKey(
		long CPConfigurationEntrySettingId) {

		return getPersistence().fetchByPrimaryKey(
			CPConfigurationEntrySettingId);
	}

	/**
	 * Returns the cp configuration entry setting where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching cp configuration entry setting, or <code>null</code> if a matching cp configuration entry setting could not be found
	 */
	public static CPConfigurationEntrySetting fetchByUUID_G(
		String uuid, long groupId) {

		return getPersistence().fetchByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the cp configuration entry setting where CPConfigurationEntryId = &#63; and type = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param CPConfigurationEntryId the cp configuration entry ID
	 * @param type the type
	 * @return the matching cp configuration entry setting, or <code>null</code> if a matching cp configuration entry setting could not be found
	 */
	public static CPConfigurationEntrySetting fetchByC_T(
		long CPConfigurationEntryId, int type) {

		return getPersistence().fetchByC_T(CPConfigurationEntryId, type);
	}

	/**
	 * Returns all the cp configuration entry settings where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching cp configuration entry settings
	 */
	public static List<CPConfigurationEntrySetting> findByUuid(String uuid) {
		return getPersistence().findByUuid(uuid);
	}

	/**
	 * Returns a range of all the cp configuration entry settings where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPConfigurationEntrySettingModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of cp configuration entry settings
	 * @param end the upper bound of the range of cp configuration entry settings (not inclusive)
	 * @return the range of matching cp configuration entry settings
	 */
	public static List<CPConfigurationEntrySetting> findByUuid(
		String uuid, int start, int end) {

		return getPersistence().findByUuid(uuid, start, end);
	}

	/**
	 * Returns an ordered range of all the cp configuration entry settings where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPConfigurationEntrySettingModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of cp configuration entry settings
	 * @param end the upper bound of the range of cp configuration entry settings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp configuration entry settings
	 */
	public static List<CPConfigurationEntrySetting> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CPConfigurationEntrySetting> orderByComparator) {

		return getPersistence().findByUuid(uuid, start, end, orderByComparator);
	}

	/**
	 * Returns all the cp configuration entry settings where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching cp configuration entry settings
	 */
	public static List<CPConfigurationEntrySetting> findByUuid_C(
		String uuid, long companyId) {

		return getPersistence().findByUuid_C(uuid, companyId);
	}

	/**
	 * Returns a range of all the cp configuration entry settings where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPConfigurationEntrySettingModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp configuration entry settings
	 * @param end the upper bound of the range of cp configuration entry settings (not inclusive)
	 * @return the range of matching cp configuration entry settings
	 */
	public static List<CPConfigurationEntrySetting> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return getPersistence().findByUuid_C(uuid, companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the cp configuration entry settings where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPConfigurationEntrySettingModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp configuration entry settings
	 * @param end the upper bound of the range of cp configuration entry settings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp configuration entry settings
	 */
	public static List<CPConfigurationEntrySetting> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CPConfigurationEntrySetting> orderByComparator) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator);
	}

	public static CPConfigurationEntrySettingPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(
		CPConfigurationEntrySettingPersistence persistence) {

		_persistence = persistence;
	}

	private static volatile CPConfigurationEntrySettingPersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:724765559