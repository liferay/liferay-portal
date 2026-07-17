/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.search.spi.model.index.contributor;

import com.liferay.exportimport.kernel.lar.ExportImportHelper;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.search.spi.model.index.contributor.ModelDocumentContributor;

import java.io.Serializable;

import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Máté Thurzó
 * @author Akos Thurzo
 * @author Luan Maoski
 */
@Component(
	property = "indexer.class.name=com.liferay.exportimport.kernel.model.ExportImportConfiguration",
	service = ModelDocumentContributor.class
)
public class ExportImportConfigurationModelDocumentContributor
	implements ModelDocumentContributor<ExportImportConfiguration> {

	@Override
	public void contribute(
		Document document,
		ExportImportConfiguration exportImportConfiguration) {

		document.addText(
			Field.DESCRIPTION, exportImportConfiguration.getDescription());
		document.addText(Field.NAME, exportImportConfiguration.getName());
		document.addKeyword(Field.TYPE, exportImportConfiguration.getType());
		document.addNumber(
			"exportImportConfigurationId",
			exportImportConfiguration.getExportImportConfigurationId());

		Map<String, Serializable> settingsMap =
			exportImportConfiguration.getSettingsMap();

		_populateDates(document, settingsMap);
		_populateLayoutIds(document, settingsMap);
		_populateLocale(document, settingsMap);
		_populateParameterMap(document, settingsMap);
		_populateSiteInformation(document, settingsMap);
		_populateTimeZone(document, settingsMap);

		document.addKeyword(
			_PREFIX_SETTING + Field.USER_ID,
			MapUtil.getLong(settingsMap, "userId"));
	}

	private void _populateDates(
		Document document, Map<String, Serializable> settingsMap) {

		if (settingsMap.get("endDate") instanceof Date endDate) {
			document.addDate(_PREFIX_SETTING + "endDate", endDate);
		}

		if (settingsMap.get("startDate") instanceof Date startDate) {
			document.addDate(_PREFIX_SETTING + "startDate", startDate);
		}
	}

	private void _populateLayoutIds(
		Document document, Map<String, Serializable> settingsMap) {

		Serializable layoutIdMap = settingsMap.get("layoutIdMap");
		Serializable layoutIdsSetting = settingsMap.get("layoutIds");

		if ((layoutIdMap == null) && (layoutIdsSetting == null)) {
			return;
		}

		long[] layoutIds = GetterUtil.getLongValues(layoutIdsSetting);

		if (ArrayUtil.isEmpty(layoutIds)) {
			try {
				layoutIds = _exportImportHelper.getLayoutIds(
					(Map<Long, Boolean>)layoutIdMap);
			}
			catch (PortalException portalException) {

				// LPS-52675

				if (_log.isDebugEnabled()) {
					_log.debug(portalException);
				}
			}
		}

		document.addKeyword("layoutIds", layoutIds);
	}

	private void _populateLocale(
		Document document, Map<String, Serializable> settingsMap) {

		Locale locale = (Locale)settingsMap.get("locale");

		document.addText(_PREFIX_SETTING + "locale", locale.toString());
	}

	private void _populateParameterMap(
		Document document, Map<String, Serializable> settingsMap) {

		if (!(settingsMap.get("parameterMap") instanceof Map<?, ?> map)) {
			return;
		}

		Map<String, String[]> parameterMap = (Map<String, String[]>)map;

		for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
			String parameterName = entry.getKey();

			if (!Field.validateFieldName(parameterName)) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Skipping invalid parameter name: " + parameterName);
				}

				continue;
			}

			String[] parameterValues = ArrayUtil.clone(entry.getValue());

			for (int i = 0; i < parameterValues.length; i++) {
				String parameterValue = parameterValues[i];

				if (parameterValue.equals(StringPool.TRUE)) {
					parameterValues[i] = "on";
				}
				else if (parameterValue.equals(StringPool.FALSE)) {
					parameterValues[i] = "off";
				}
			}

			document.addKeyword(
				_PREFIX_PARAMETER + entry.getKey(), parameterValues);
		}
	}

	private void _populateSiteInformation(
		Document document, Map<String, Serializable> settingsMap) {

		document.addKeyword(
			_PREFIX_SETTING + "privateLayout",
			MapUtil.getBoolean(settingsMap, "privateLayout"));
		document.addKeyword(
			_PREFIX_SETTING + "sourceGroupId",
			MapUtil.getLong(settingsMap, "sourceGroupId"));
		document.addKeyword(
			_PREFIX_SETTING + "targetGroupId",
			MapUtil.getLong(settingsMap, "targetGroupId"));
	}

	private void _populateTimeZone(
		Document document, Map<String, Serializable> settingsMap) {

		TimeZone timeZone = (TimeZone)settingsMap.get("timeZone");

		if (timeZone != null) {
			document.addKeyword(
				_PREFIX_SETTING + "timeZone", timeZone.getDisplayName());
		}
	}

	private static final String _PREFIX_PARAMETER = "parameter_";

	private static final String _PREFIX_SETTING = "setting_";

	private static final Log _log = LogFactoryUtil.getLog(
		ExportImportConfigurationModelDocumentContributor.class);

	@Reference
	private ExportImportHelper _exportImportHelper;

}