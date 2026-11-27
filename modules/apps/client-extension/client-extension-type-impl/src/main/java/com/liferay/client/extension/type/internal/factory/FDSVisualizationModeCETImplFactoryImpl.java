/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.client.extension.type.internal.factory;

import com.liferay.client.extension.exception.ClientExtensionEntryTypeSettingsException;
import com.liferay.client.extension.type.FDSVisualizationModeCET;
import com.liferay.client.extension.type.internal.FDSVisualizationModeCETImpl;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletRequest;

import java.util.Date;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * @author Antonio Ortega
 */
public class FDSVisualizationModeCETImplFactoryImpl
	extends BaseCETImplFactoryImpl<FDSVisualizationModeCET> {

	public FDSVisualizationModeCETImplFactoryImpl(JSONFactory jsonFactory) {
		super(FDSVisualizationModeCET.class);

		_jsonFactory = jsonFactory;
	}

	@Override
	public FDSVisualizationModeCET create(
		String baseURL, long companyId, Date createDate, String description,
		String externalReferenceCode, Date modifiedDate, String name,
		Properties properties, boolean readOnly, String sourceCodeURL,
		int status, UnicodeProperties typeSettingsUnicodeProperties) {

		return new FDSVisualizationModeCETImpl(
			baseURL, companyId, createDate, description, externalReferenceCode,
			modifiedDate, name, properties, readOnly, sourceCodeURL, status,
			typeSettingsUnicodeProperties);
	}

	@Override
	public UnicodeProperties getUnicodeProperties(
		PortletRequest portletRequest) {

		return UnicodePropertiesBuilder.create(
			true
		).put(
			"icon", ParamUtil.getString(portletRequest, "icon")
		).put(
			"sections", ParamUtil.getString(portletRequest, "sections")
		).put(
			"url", ParamUtil.getString(portletRequest, "url")
		).build();
	}

	@Override
	public void validate(
			FDSVisualizationModeCET newFDSVisualizationModeCET,
			FDSVisualizationModeCET oldFDSVisualizationModeCET)
		throws PortalException {

		if (Validator.isNull(newFDSVisualizationModeCET.getURL())) {
			throw new ClientExtensionEntryTypeSettingsException(
				"At least one JavaScript URL is required",
				"please-enter-at-least-one-javascript-url");
		}

		if (Validator.isNull(newFDSVisualizationModeCET.getIcon())) {
			throw new ClientExtensionEntryTypeSettingsException(
				"Icon is null", "please-select-an-icon");
		}

		_validateSections(newFDSVisualizationModeCET.getSections());
	}

	private void _validateSections(String sectionsJSON) throws PortalException {
		if (Validator.isBlank(sectionsJSON)) {
			return;
		}

		JSONArray sectionsJSONArray = null;

		try {
			sectionsJSONArray = _jsonFactory.createJSONArray(sectionsJSON);
		}
		catch (JSONException jsonException) {
			if (_log.isDebugEnabled()) {
				_log.debug(jsonException);
			}

			throw new ClientExtensionEntryTypeSettingsException(
				"Invalid sections JSON: " + sectionsJSON,
				"the-item-elements-are-not-valid-json");
		}

		if (JSONUtil.isEmpty(sectionsJSONArray)) {
			throw new ClientExtensionEntryTypeSettingsException(
				"The sections must declare at least one section",
				"declare-at-least-one-item-element");
		}

		Set<String> names = new HashSet<>();

		for (int index = 0; index < sectionsJSONArray.length(); index++) {
			String name = null;

			JSONObject sectionJSONObject = sectionsJSONArray.getJSONObject(
				index);

			if (sectionJSONObject != null) {
				name = sectionJSONObject.getString("name");
			}

			if (Validator.isBlank(name)) {
				throw new ClientExtensionEntryTypeSettingsException(
					"Every section must have a \"name\"",
					"every-item-element-must-have-a-name");
			}

			if (!names.add(name)) {
				throw new ClientExtensionEntryTypeSettingsException(
					"Duplicate section name: " + name,
					"item-element-name-x-is-duplicated", name);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FDSVisualizationModeCETImplFactoryImpl.class);

	private final JSONFactory _jsonFactory;

}