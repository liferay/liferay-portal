/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service.persistence;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.model.PluginSetting;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the plugin setting service. This utility wraps <code>com.liferay.portal.service.persistence.impl.PluginSettingPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see PluginSettingPersistence
 * @generated
 */
public class PluginSettingUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(List<PluginSetting> pluginSettings) {
		getPersistence().cacheResult(pluginSettings);
	}

	/**
	 * @see BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(PluginSetting pluginSetting) {
		getPersistence().cacheResult(pluginSetting);
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
	public static void clearCache(PluginSetting pluginSetting) {
		getPersistence().clearCache(pluginSetting);
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
	public static Map<Serializable, PluginSetting> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<PluginSetting> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<PluginSetting> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<PluginSetting> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<PluginSetting> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static PluginSetting update(PluginSetting pluginSetting) {
		return getPersistence().update(pluginSetting);
	}

	/**
	 * @see BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static PluginSetting update(
		PluginSetting pluginSetting, ServiceContext serviceContext) {

		return getPersistence().update(pluginSetting, serviceContext);
	}

	/**
	 * Returns the plugin setting where companyId = &#63; and pluginId = &#63; and pluginType = &#63; or throws a <code>NoSuchPluginSettingException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param pluginId the plugin ID
	 * @param pluginType the plugin type
	 * @return the matching plugin setting
	 * @throws NoSuchPluginSettingException if a matching plugin setting could not be found
	 */
	public static PluginSetting findByC_P_P(
			long companyId, String pluginId, String pluginType)
		throws com.liferay.portal.kernel.exception.
			NoSuchPluginSettingException {

		return getPersistence().findByC_P_P(companyId, pluginId, pluginType);
	}

	/**
	 * Returns the plugin setting where companyId = &#63; and pluginId = &#63; and pluginType = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param pluginId the plugin ID
	 * @param pluginType the plugin type
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching plugin setting, or <code>null</code> if a matching plugin setting could not be found
	 */
	public static PluginSetting fetchByC_P_P(
		long companyId, String pluginId, String pluginType,
		boolean useFinderCache) {

		return getPersistence().fetchByC_P_P(
			companyId, pluginId, pluginType, useFinderCache);
	}

	/**
	 * Removes the plugin setting where companyId = &#63; and pluginId = &#63; and pluginType = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param pluginId the plugin ID
	 * @param pluginType the plugin type
	 * @return the plugin setting that was removed
	 */
	public static PluginSetting removeByC_P_P(
			long companyId, String pluginId, String pluginType)
		throws com.liferay.portal.kernel.exception.
			NoSuchPluginSettingException {

		return getPersistence().removeByC_P_P(companyId, pluginId, pluginType);
	}

	/**
	 * Returns the number of plugin settings where companyId = &#63; and pluginId = &#63; and pluginType = &#63;.
	 *
	 * @param companyId the company ID
	 * @param pluginId the plugin ID
	 * @param pluginType the plugin type
	 * @return the number of matching plugin settings
	 */
	public static int countByC_P_P(
		long companyId, String pluginId, String pluginType) {

		return getPersistence().countByC_P_P(companyId, pluginId, pluginType);
	}

	/**
	 * Creates a new plugin setting with the primary key. Does not add the plugin setting to the database.
	 *
	 * @param pluginSettingId the primary key for the new plugin setting
	 * @return the new plugin setting
	 */
	public static PluginSetting create(long pluginSettingId) {
		return getPersistence().create(pluginSettingId);
	}

	/**
	 * Removes the plugin setting with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param pluginSettingId the primary key of the plugin setting
	 * @return the plugin setting that was removed
	 * @throws NoSuchPluginSettingException if a plugin setting with the primary key could not be found
	 */
	public static PluginSetting remove(long pluginSettingId)
		throws com.liferay.portal.kernel.exception.
			NoSuchPluginSettingException {

		return getPersistence().remove(pluginSettingId);
	}

	public static PluginSetting updateImpl(PluginSetting pluginSetting) {
		return getPersistence().updateImpl(pluginSetting);
	}

	/**
	 * Returns the plugin setting with the primary key or throws a <code>NoSuchPluginSettingException</code> if it could not be found.
	 *
	 * @param pluginSettingId the primary key of the plugin setting
	 * @return the plugin setting
	 * @throws NoSuchPluginSettingException if a plugin setting with the primary key could not be found
	 */
	public static PluginSetting findByPrimaryKey(long pluginSettingId)
		throws com.liferay.portal.kernel.exception.
			NoSuchPluginSettingException {

		return getPersistence().findByPrimaryKey(pluginSettingId);
	}

	/**
	 * Returns the plugin setting with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param pluginSettingId the primary key of the plugin setting
	 * @return the plugin setting, or <code>null</code> if a plugin setting with the primary key could not be found
	 */
	public static PluginSetting fetchByPrimaryKey(long pluginSettingId) {
		return getPersistence().fetchByPrimaryKey(pluginSettingId);
	}

	/**
	 * Returns the plugin setting where companyId = &#63; and pluginId = &#63; and pluginType = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param companyId the company ID
	 * @param pluginId the plugin ID
	 * @param pluginType the plugin type
	 * @return the matching plugin setting, or <code>null</code> if a matching plugin setting could not be found
	 */
	public static PluginSetting fetchByC_P_P(
		long companyId, String pluginId, String pluginType) {

		return getPersistence().fetchByC_P_P(companyId, pluginId, pluginType);
	}

	public static PluginSettingPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(PluginSettingPersistence persistence) {
		_persistence = persistence;
	}

	private static volatile PluginSettingPersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:-576605692