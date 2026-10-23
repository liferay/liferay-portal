/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service.persistence;

import com.liferay.portal.kernel.exception.NoSuchPluginSettingException;
import com.liferay.portal.kernel.model.PluginSetting;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the plugin setting service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see PluginSettingUtil
 * @generated
 */
@ProviderType
public interface PluginSettingPersistence
	extends BasePersistence<PluginSetting> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link PluginSettingUtil} to access the plugin setting persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns the plugin setting where companyId = &#63; and pluginId = &#63; and pluginType = &#63; or throws a <code>NoSuchPluginSettingException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param pluginId the plugin ID
	 * @param pluginType the plugin type
	 * @return the matching plugin setting
	 * @throws NoSuchPluginSettingException if a matching plugin setting could not be found
	 */
	public PluginSetting findByC_P_P(
			long companyId, String pluginId, String pluginType)
		throws NoSuchPluginSettingException;

	/**
	 * Returns the plugin setting where companyId = &#63; and pluginId = &#63; and pluginType = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param pluginId the plugin ID
	 * @param pluginType the plugin type
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching plugin setting, or <code>null</code> if a matching plugin setting could not be found
	 */
	public PluginSetting fetchByC_P_P(
		long companyId, String pluginId, String pluginType,
		boolean useFinderCache);

	/**
	 * Removes the plugin setting where companyId = &#63; and pluginId = &#63; and pluginType = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param pluginId the plugin ID
	 * @param pluginType the plugin type
	 * @return the plugin setting that was removed
	 */
	public PluginSetting removeByC_P_P(
			long companyId, String pluginId, String pluginType)
		throws NoSuchPluginSettingException;

	/**
	 * Returns the number of plugin settings where companyId = &#63; and pluginId = &#63; and pluginType = &#63;.
	 *
	 * @param companyId the company ID
	 * @param pluginId the plugin ID
	 * @param pluginType the plugin type
	 * @return the number of matching plugin settings
	 */
	public int countByC_P_P(long companyId, String pluginId, String pluginType);

	/**
	 * Creates a new plugin setting with the primary key. Does not add the plugin setting to the database.
	 *
	 * @param pluginSettingId the primary key for the new plugin setting
	 * @return the new plugin setting
	 */
	public PluginSetting create(long pluginSettingId);

	/**
	 * Removes the plugin setting with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param pluginSettingId the primary key of the plugin setting
	 * @return the plugin setting that was removed
	 * @throws NoSuchPluginSettingException if a plugin setting with the primary key could not be found
	 */
	public PluginSetting remove(long pluginSettingId)
		throws NoSuchPluginSettingException;

	public PluginSetting updateImpl(PluginSetting pluginSetting);

	/**
	 * Returns the plugin setting with the primary key or throws a <code>NoSuchPluginSettingException</code> if it could not be found.
	 *
	 * @param pluginSettingId the primary key of the plugin setting
	 * @return the plugin setting
	 * @throws NoSuchPluginSettingException if a plugin setting with the primary key could not be found
	 */
	public PluginSetting findByPrimaryKey(long pluginSettingId)
		throws NoSuchPluginSettingException;

	/**
	 * Returns the plugin setting with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param pluginSettingId the primary key of the plugin setting
	 * @return the plugin setting, or <code>null</code> if a plugin setting with the primary key could not be found
	 */
	public PluginSetting fetchByPrimaryKey(long pluginSettingId);

	/**
	 * Returns the plugin setting where companyId = &#63; and pluginId = &#63; and pluginType = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param companyId the company ID
	 * @param pluginId the plugin ID
	 * @param pluginType the plugin type
	 * @return the matching plugin setting, or <code>null</code> if a matching plugin setting could not be found
	 */
	public default PluginSetting fetchByC_P_P(
		long companyId, String pluginId, String pluginType) {

		return fetchByC_P_P(companyId, pluginId, pluginType, true);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1867847752