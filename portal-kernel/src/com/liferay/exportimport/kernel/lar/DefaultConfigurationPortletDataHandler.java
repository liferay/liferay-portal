/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.kernel.lar;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletPreferences;

import java.util.Map;

/**
 * @author Eduardo García
 */
public class DefaultConfigurationPortletDataHandler
	extends BasePortletDataHandler {

	public DefaultConfigurationPortletDataHandler() {
		setDataLevel(DataLevel.PORTLET_INSTANCE);
	}

	@Override
	public PortletPreferences deleteData(
		PortletDataContext portletDataContext, String portletId,
		PortletPreferences portletPreferences) {

		return null;
	}

	@Override
	public String exportData(
		PortletDataContext portletDataContext, String portletId,
		PortletPreferences portletPreferences) {

		return null;
	}

	@Override
	public long getExportModelCount(ManifestSummary manifestSummary) {
		return 0;
	}

	@Override
	public PortletPreferences importData(
		PortletDataContext portletDataContext, String portletId,
		PortletPreferences portletPreferences, String data) {

		return null;
	}

	protected String getExportPortletPreferencesUuid(
			PortletDataContext portletDataContext, Portlet portlet,
			String className, long primaryKeyLong)
		throws Exception {

		return StringPool.BLANK;
	}

	protected Long getImportPortletPreferencesNewPrimaryKey(
			PortletDataContext portletDataContext, Class<?> clazz,
			long companyGroupId, Map<Long, Long> primaryKeys, String uuid)
		throws Exception {

		return Long.valueOf(0L);
	}

	protected void updateExportPortletPreferencesClassPKs(
			PortletDataContext portletDataContext, Portlet portlet,
			PortletPreferences portletPreferences, String key, String className)
		throws Exception {

		String[] oldValues = portletPreferences.getValues(key, null);

		if (oldValues == null) {
			return;
		}

		String[] newValues = new String[oldValues.length];

		for (int i = 0; i < oldValues.length; i++) {
			String oldValue = oldValues[i];

			String newValue = oldValue;

			String[] primaryKeys = StringUtil.split(oldValue);

			for (String primaryKey : primaryKeys) {
				if (!Validator.isNumber(primaryKey)) {
					break;
				}

				long primaryKeyLong = GetterUtil.getLong(primaryKey);

				String uuid = getExportPortletPreferencesUuid(
					portletDataContext, portlet, className, primaryKeyLong);

				if (Validator.isNull(uuid)) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							StringBundler.concat(
								"Unable to get UUID for class ", className,
								" with primary key ", primaryKeyLong));
					}

					continue;
				}

				newValue = StringUtil.replace(newValue, primaryKey, uuid);
			}

			newValues[i] = newValue;
		}

		portletPreferences.setValues(key, newValues);
	}

	protected void updateImportPortletPreferencesClassPKs(
			PortletDataContext portletDataContext,
			PortletPreferences portletPreferences, String key, Class<?> clazz,
			long companyGroupId)
		throws Exception {

		String[] oldValues = portletPreferences.getValues(key, null);

		if (oldValues == null) {
			return;
		}

		Map<Long, Long> primaryKeys =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(clazz);

		String[] newValues = new String[oldValues.length];

		for (int i = 0; i < oldValues.length; i++) {
			String oldValue = oldValues[i];

			String newValue = oldValue;

			String[] uuids = StringUtil.split(oldValue);

			for (String uuid : uuids) {
				Long newPrimaryKey = getImportPortletPreferencesNewPrimaryKey(
					portletDataContext, clazz, companyGroupId, primaryKeys,
					uuid);

				if (Validator.isNull(newPrimaryKey)) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							StringBundler.concat(
								"Unable to get primary key for ", clazz,
								" with UUID ", uuid, " in company group ",
								companyGroupId, " or in group ",
								portletDataContext.getScopeGroupId()));
					}
				}
				else {
					newValue = StringUtil.replace(
						newValue, uuid, newPrimaryKey.toString());
				}
			}

			newValues[i] = newValue;
		}

		portletPreferences.setValues(key, newValues);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DefaultConfigurationPortletDataHandler.class);

}