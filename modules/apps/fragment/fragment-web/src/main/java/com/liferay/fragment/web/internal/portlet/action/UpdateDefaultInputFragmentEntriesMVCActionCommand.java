/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.web.internal.portlet.action;

import com.liferay.fragment.constants.FragmentPortletKeys;
import com.liferay.fragment.helper.DefaultInputFragmentEntryConfigurationProvider;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Víctor Galán
 */
@Component(
	property = {
		"jakarta.portlet.name=" + FragmentPortletKeys.FRAGMENT,
		"mvc.command.name=/fragment/update_default_input_fragment_entries"
	},
	service = MVCActionCommand.class
)
public class UpdateDefaultInputFragmentEntriesMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		JSONObject defaultInputFragmentEntryKeysJSONObject =
			_defaultInputFragmentEntryConfigurationProvider.
				getDefaultInputFragmentEntryKeysJSONObject(
					themeDisplay.getScopeGroupId());

		JSONObject valuesJSONObject = _jsonFactory.createJSONObject(
			ParamUtil.getString(actionRequest, "values"));

		for (String key : valuesJSONObject.keySet()) {
			JSONObject jsonObject = valuesJSONObject.getJSONObject(key);

			defaultInputFragmentEntryKeysJSONObject.put(
				key,
				JSONUtil.put(
					"groupKey", jsonObject.getString("groupKey")
				).put(
					"key", jsonObject.getString("key")
				));
		}

		_defaultInputFragmentEntryConfigurationProvider.
			updateDefaultInputFragmentEntryKeysJSONObject(
				defaultInputFragmentEntryKeysJSONObject,
				themeDisplay.getScopeGroupId());
	}

	@Reference
	private DefaultInputFragmentEntryConfigurationProvider
		_defaultInputFragmentEntryConfigurationProvider;

	@Reference
	private JSONFactory _jsonFactory;

}