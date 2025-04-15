/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.portlet.preferences.processor.base;

import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.staging.StagingURLHelperUtil;
import com.liferay.exportimport.portlet.preferences.processor.ExportImportPortletPreferencesProcessor;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.lang.ThreadContextClassLoaderUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.service.http.GroupServiceHttp;

import jakarta.portlet.PortletPreferences;

import java.util.Map;
import java.util.Objects;

import org.osgi.annotation.versioning.ProviderType;

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

		Group group = GroupLocalServiceUtil.fetchGroupByExternalReferenceCode(
			externalReferenceCode, portletDataContext.getCompanyId());

		if (group == null) {
			return externalReferenceCode;
		}

		if (ExportImportThreadLocal.isStagingInProcess() &&
			group.isStagedRemotely()) {

			UnicodeProperties typeSettingsUnicodeProperties =
				group.getTypeSettingsProperties();

			String remoteGroupExternalReferenceCode =
				typeSettingsUnicodeProperties.get(
					"remoteGroupExternalReferenceCode");

			if (Validator.isNull(remoteGroupExternalReferenceCode)) {
				remoteGroupExternalReferenceCode =
					_getRemoteGroupExternalReferenceCode(
						typeSettingsUnicodeProperties);
			}

			if (Validator.isNotNull(remoteGroupExternalReferenceCode)) {
				externalReferenceCode = remoteGroupExternalReferenceCode;
			}
		}

		if (!group.isStagingGroup()) {
			return externalReferenceCode;
		}

		Group liveGroup = GroupLocalServiceUtil.fetchGroup(
			group.getLiveGroupId());

		if (liveGroup == null) {
			return externalReferenceCode;
		}

		return liveGroup.getExternalReferenceCode();
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

	private String _getRemoteGroupExternalReferenceCode(
		UnicodeProperties typeSettingsUnicodeProperties) {

		String remoteAddress = GetterUtil.getString(
			typeSettingsUnicodeProperties.get("remoteAddress"));
		long remoteGroupId = GetterUtil.getLong(
			typeSettingsUnicodeProperties.get("remoteGroupId"));

		if (Validator.isNull(remoteAddress) || (remoteGroupId <= 0)) {
			return null;
		}

		int remotePort = GetterUtil.getInteger(
			typeSettingsUnicodeProperties.get("remotePort"));
		String remotePathContext = GetterUtil.getString(
			typeSettingsUnicodeProperties.get("remotePathContext"));
		boolean secureConnection = GetterUtil.getBoolean(
			typeSettingsUnicodeProperties.get("secureConnection"));

		String remoteURL = StagingURLHelperUtil.buildRemoteURL(
			remoteAddress, remotePort, remotePathContext, secureConnection);

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		User user = permissionChecker.getUser();

		try {
			HttpPrincipal httpPrincipal = new HttpPrincipal(
				remoteURL, user.getLogin(), user.getPassword(),
				user.isPasswordEncrypted());

			try (SafeCloseable safeCloseable =
					ThreadContextClassLoaderUtil.swap(
						PortalClassLoaderUtil.getClassLoader())) {

				Group group = GroupServiceHttp.getGroup(
					httpPrincipal, remoteGroupId);

				return group.getExternalReferenceCode();
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BaseExportImportPortletPreferencesProcessor.class);

}