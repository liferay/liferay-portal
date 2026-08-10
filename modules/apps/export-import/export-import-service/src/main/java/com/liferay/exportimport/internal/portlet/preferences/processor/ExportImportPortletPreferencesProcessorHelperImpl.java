/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.portlet.preferences.processor;

import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataException;
import com.liferay.exportimport.kernel.staging.StagingURLHelperUtil;
import com.liferay.exportimport.portlet.preferences.processor.ExportImportPortletPreferencesProcessorHelper;
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
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.service.http.GroupServiceHttp;

import jakarta.portlet.PortletPreferences;
import jakarta.portlet.ReadOnlyException;

import java.util.function.Function;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Máté Thurzó
 */
@Component(service = ExportImportPortletPreferencesProcessorHelper.class)
public class ExportImportPortletPreferencesProcessorHelperImpl
	implements ExportImportPortletPreferencesProcessorHelper {

	@Override
	public String getGroupExportPortletPreferencesExternalReferenceCode(
		long companyId, String externalReferenceCode) {

		if (!ExportImportThreadLocal.isStagingInProcess()) {
			return externalReferenceCode;
		}

		Group group = _groupLocalService.fetchGroupByExternalReferenceCode(
			externalReferenceCode, companyId);

		if (group == null) {
			return externalReferenceCode;
		}

		if (group.isStagedRemotely()) {
			String remoteGroupExternalReferenceCode =
				_getRemoteGroupExternalReferenceCode(group);

			if (Validator.isNotNull(remoteGroupExternalReferenceCode)) {
				externalReferenceCode = remoteGroupExternalReferenceCode;
			}
		}

		if (!group.isStagingGroup()) {
			return externalReferenceCode;
		}

		Group liveGroup = _groupLocalService.fetchGroup(group.getLiveGroupId());

		if (liveGroup == null) {
			return externalReferenceCode;
		}

		return liveGroup.getExternalReferenceCode();
	}

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
	public void updateGroupExportPortletPreferencesExternalReferenceCode(
			long companyId, String externalReferenceCodePreferenceKey,
			PortletPreferences portletPreferences)
		throws PortletDataException {

		String externalReferenceCode = portletPreferences.getValue(
			externalReferenceCodePreferenceKey, null);

		if (Validator.isBlank(externalReferenceCode)) {
			return;
		}

		try {
			portletPreferences.setValue(
				externalReferenceCodePreferenceKey,
				getGroupExportPortletPreferencesExternalReferenceCode(
					companyId, externalReferenceCode));
		}
		catch (ReadOnlyException readOnlyException) {
			throw new PortletDataException(readOnlyException);
		}
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

	private String _getRemoteGroupExternalReferenceCode(Group group) {
		UnicodeProperties typeSettingsUnicodeProperties =
			group.getTypeSettingsProperties();

		String remoteGroupExternalReferenceCode =
			typeSettingsUnicodeProperties.get(
				"remoteGroupExternalReferenceCode");

		if (Validator.isNotNull(remoteGroupExternalReferenceCode)) {
			return remoteGroupExternalReferenceCode;
		}

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

				Group remoteGroup = GroupServiceHttp.getGroup(
					httpPrincipal, remoteGroupId);

				return remoteGroup.getExternalReferenceCode();
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
		ExportImportPortletPreferencesProcessorHelperImpl.class);

	@Reference
	private GroupLocalService _groupLocalService;

}