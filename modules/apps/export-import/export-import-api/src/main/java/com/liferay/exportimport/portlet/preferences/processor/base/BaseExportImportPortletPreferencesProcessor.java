/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.portlet.preferences.processor.base;

import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.portlet.preferences.processor.ExportImportPortletPreferencesProcessor;
import com.liferay.exportimport.portlet.preferences.processor.ExportImportPortletPreferencesProcessorHelper;
import com.liferay.osgi.util.ServiceTrackerFactory;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletPreferences;

import java.util.Map;
import java.util.Objects;

import org.osgi.annotation.versioning.ProviderType;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Máté Thurzó
 */
@ProviderType
public abstract class BaseExportImportPortletPreferencesProcessor
	implements ExportImportPortletPreferencesProcessor {

	protected String getExportPortletPreferencesExternalReferenceCode(
		PortletDataContext portletDataContext, Portlet portlet,
		String className, String externalReferenceCode) {

		return null;
	}

	protected abstract String getExportPortletPreferencesValue(
			PortletDataContext portletDataContext, Portlet portlet,
			String className, long primaryKeyLong)
		throws Exception;

	protected String getGroupExportPortletPreferencesExternalReferenceCode(
		PortletDataContext portletDataContext, String externalReferenceCode) {

		ExportImportPortletPreferencesProcessorHelper
			exportImportPortletPreferencesProcessorHelper =
				_serviceTracker.getService();

		return exportImportPortletPreferencesProcessorHelper.
			getGroupExportPortletPreferencesExternalReferenceCode(
				portletDataContext.getCompanyId(), externalReferenceCode);
	}

	protected String getImportPortletPreferencesNewExternalReferenceCode(
			PortletDataContext portletDataContext, Class<?> clazz,
			long companyGroupId, Map<String, String[]> primaryKeys,
			String portletPreferencesOldExternalReferenceCode)
		throws Exception {

		return null;
	}

	protected abstract Long getImportPortletPreferencesNewValue(
			PortletDataContext portletDataContext, Class<?> clazz,
			long companyGroupId, Map<Long, Long> primaryKeys,
			String portletPreferencesOldValue)
		throws Exception;

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

				String newPreferencesValue = getExportPortletPreferencesValue(
					portletDataContext, portlet, className, primaryKeyLong);

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

	protected void updateExportPortletPreferencesExternalReferenceCodes(
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

			String[] externalReferenceCodes = StringUtil.split(oldValue);

			for (String externalReferenceCode : externalReferenceCodes) {
				String newPreferencesValue = null;

				if (Objects.equals(className, Group.class.getName())) {
					newPreferencesValue =
						getGroupExportPortletPreferencesExternalReferenceCode(
							portletDataContext, externalReferenceCode);
				}
				else {
					newPreferencesValue =
						getExportPortletPreferencesExternalReferenceCode(
							portletDataContext, portlet, className,
							externalReferenceCode);
				}

				if (Validator.isNull(newPreferencesValue)) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							StringBundler.concat(
								"Unable to export portlet preferences value ",
								"for class ", className, " with external ",
								"reference code", externalReferenceCode));
					}

					continue;
				}

				newValue = StringUtil.replace(
					newValue, externalReferenceCode, newPreferencesValue);
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

			String[] portletPreferencesOldValues = StringUtil.split(oldValue);

			for (String portletPreferencesOldValue :
					portletPreferencesOldValues) {

				Long newPrimaryKey = getImportPortletPreferencesNewValue(
					portletDataContext, clazz, companyGroupId, primaryKeys,
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

	protected void updateImportPortletPreferencesExternalReferenceCodes(
			PortletDataContext portletDataContext,
			PortletPreferences portletPreferences, String key, Class<?> clazz,
			long companyGroupId)
		throws Exception {

		String[] oldValues = portletPreferences.getValues(key, null);

		if (oldValues == null) {
			return;
		}

		Map<String, String[]> parameterMap =
			portletDataContext.getParameterMap();

		String[] newValues = new String[oldValues.length];

		for (int i = 0; i < oldValues.length; i++) {
			String oldValue = oldValues[i];

			String newValue = oldValue;

			String[] portletPreferencesOldValues = StringUtil.split(oldValue);

			for (String portletPreferencesOldValue :
					portletPreferencesOldValues) {

				String newExternalReferenceCode =
					getImportPortletPreferencesNewExternalReferenceCode(
						portletDataContext, clazz, companyGroupId, parameterMap,
						portletPreferencesOldValue);

				if (Validator.isNull(newExternalReferenceCode)) {
					if (_log.isInfoEnabled()) {
						_log.info(
							"Unable to import portlet preferences value " +
								portletPreferencesOldValue);
					}
				}
				else {
					newValue = StringUtil.replace(
						newValue, portletPreferencesOldValue,
						newExternalReferenceCode);
				}
			}

			newValues[i] = newValue;
		}

		portletPreferences.setValues(key, newValues);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BaseExportImportPortletPreferencesProcessor.class);

	private static final ServiceTracker
		<ExportImportPortletPreferencesProcessorHelper,
		 ExportImportPortletPreferencesProcessorHelper> _serviceTracker;

	static {
		Bundle bundle = FrameworkUtil.getBundle(
			BaseExportImportPortletPreferencesProcessor.class);

		_serviceTracker = ServiceTrackerFactory.open(
			bundle, ExportImportPortletPreferencesProcessorHelper.class);
	}

}