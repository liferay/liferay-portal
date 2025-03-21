/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.portlet.preferences.processor;

import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.portlet.preferences.processor.ExportImportPortletPreferencesProcessorHelper;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletPreferences;

import java.util.function.Function;

import org.osgi.service.component.annotations.Component;

/**
 * @author Máté Thurzó
 */
@Component(service = ExportImportPortletPreferencesProcessorHelper.class)
public class ExportImportPortletPreferencesProcessorHelperImpl
	implements ExportImportPortletPreferencesProcessorHelper {

	@Override
	public void updateExportPortletPreferencesClassPKs(
			PortletDataContext portletDataContext, Portlet portlet,
			PortletPreferences portletPreferences, String key, String className,
			Function<String, String> exportPortletPreferencesNewValueFunction)
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

				String newPreferencesValue =
					exportPortletPreferencesNewValueFunction.apply(primaryKey);

				if (Validator.isNull(newPreferencesValue)) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							StringBundler.concat(
								"Unable to export portlet preferences value ",
								"for class ", className, " with primary key ",
								primaryKeyLong));
					}

					continue;
				}

				newValue = StringUtil.replace(
					newValue, primaryKey, newPreferencesValue);
			}

			newValues[i] = newValue;
		}

		portletPreferences.setValues(key, newValues);
	}

	@Override
	public void updateImportPortletPreferencesClassPKs(
			PortletDataContext portletDataContext,
			PortletPreferences portletPreferences, String key,
			long companyGroupId,
			Function<String, Long> importPortletPreferencesNewValueSupplier)
		throws Exception {

		String[] oldValues = portletPreferences.getValues(key, null);

		if (oldValues == null) {
			return;
		}

		String[] newValues = new String[oldValues.length];

		for (int i = 0; i < oldValues.length; i++) {
			String oldValue = oldValues[i];

			String newValue = oldValue;

			String[] portletPreferencesOldValues = StringUtil.split(oldValue);

			for (String portletPreferencesOldValue :
					portletPreferencesOldValues) {

				Long newPrimaryKey =
					importPortletPreferencesNewValueSupplier.apply(
						portletPreferencesOldValue);

				if (Validator.isNull(newPrimaryKey)) {
					if (_log.isInfoEnabled()) {
						_log.info(
							"Unable to import portlet preferences value " +
								portletPreferencesOldValue);
					}
				}
				else {
					newValue = StringUtil.replace(
						newValue, portletPreferencesOldValue,
						newPrimaryKey.toString());
				}
			}

			newValues[i] = newValue;
		}

		portletPreferences.setValues(key, newValues);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ExportImportPortletPreferencesProcessorHelperImpl.class);

}