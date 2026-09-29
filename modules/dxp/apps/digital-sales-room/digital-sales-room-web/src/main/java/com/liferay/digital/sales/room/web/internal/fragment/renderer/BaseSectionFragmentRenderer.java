/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.sales.room.web.internal.fragment.renderer;

import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.fragment.util.configuration.FragmentConfigurationField;
import com.liferay.fragment.util.configuration.FragmentEntryConfigurationParser;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Gianmarco Brunialti Masera
 */
public abstract class BaseSectionFragmentRenderer implements FragmentRenderer {

	@Override
	public JSONObject getConfigurationJSONObject(
		FragmentRendererContext fragmentRendererContext) {

		if (Validator.isNull(getConfigurationPath())) {
			return FragmentRenderer.super.getConfigurationJSONObject(
				fragmentRendererContext);
		}

		try {
			JSONObject jsonObject = jsonFactory.createJSONObject(
				StringUtil.read(getClass(), getConfigurationPath()));

			return fragmentEntryConfigurationParser.translateConfiguration(
				jsonObject,
				ResourceBundleUtil.getBundle("content.Language", getClass()));
		}
		catch (JSONException jsonException) {
			if (_log.isDebugEnabled()) {
				_log.debug(jsonException);
			}

			return FragmentRenderer.super.getConfigurationJSONObject(
				fragmentRendererContext);
		}
	}

	protected String getConfigurationPath() {
		return StringPool.BLANK;
	}

	protected Map<String, Object> getConfigurationValuesMap(
		FragmentRendererContext fragmentRendererContext) {

		Map<String, Object> configurationValuesMap = new HashMap<>();

		JSONObject configurationJSONObject = getConfigurationJSONObject(
			fragmentRendererContext);

		if (configurationJSONObject == null) {
			return configurationValuesMap;
		}

		FragmentEntryLink fragmentEntryLink =
			fragmentRendererContext.getFragmentEntryLink();

		for (FragmentConfigurationField fragmentConfigurationField :
				fragmentEntryConfigurationParser.getFragmentConfigurationFields(
					configurationJSONObject)) {

			configurationValuesMap.put(
				fragmentConfigurationField.getName(),
				fragmentEntryConfigurationParser.getFieldValue(
					fragmentEntryLink.getEditableValuesJSONObject(),
					fragmentConfigurationField,
					fragmentRendererContext.getLocale()));
		}

		return configurationValuesMap;
	}

	@Reference
	protected FragmentEntryConfigurationParser fragmentEntryConfigurationParser;

	@Reference
	protected JSONFactory jsonFactory;

	@Reference
	protected Language language;

	@Reference
	protected Portal portal;

	private static final Log _log = LogFactoryUtil.getLog(
		BaseSectionFragmentRenderer.class);

}