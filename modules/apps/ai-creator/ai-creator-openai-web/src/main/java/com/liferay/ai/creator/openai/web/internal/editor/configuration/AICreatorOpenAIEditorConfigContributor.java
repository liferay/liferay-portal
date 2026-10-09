/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.creator.openai.web.internal.editor.configuration;

import com.liferay.ai.creator.openai.configuration.manager.AICreatorOpenAIConfigurationManager;
import com.liferay.ai.creator.openai.manager.AICreatorOpenAIManager;
import com.liferay.ai.creator.openai.web.internal.constants.AICreatorOpenAIPortletKeys;
import com.liferay.portal.kernel.editor.configuration.BaseEditorConfigContributor;
import com.liferay.portal.kernel.editor.configuration.EditorConfigContributor;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletMode;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lourdes Fernández Besada
 */
@Component(
	property = {
		"editor.name=ckeditor_classic", "editor.name=ckeditor5_classic",
		"service.ranking:Integer=101"
	},
	service = EditorConfigContributor.class
)
public class AICreatorOpenAIEditorConfigContributor
	extends BaseEditorConfigContributor {

	@Override
	public void populateConfigJSONObject(
		JSONObject jsonObject, Map<String, Object> inputEditorTaglibAttributes,
		ThemeDisplay themeDisplay,
		RequestBackedPortletURLFactory requestBackedPortletURLFactory) {

		if (!_aiCreatorOpenAIManager.isAICreatorToolbarEnabled(
				themeDisplay.getCompanyId(), themeDisplay.getScopeGroupId(),
				_portal.getPortletNamespace(themeDisplay.getPpid()))) {

			return;
		}

		jsonObject.put(
			"aiCreatorOpenAIURL",
			() -> PortletURLBuilder.create(
				requestBackedPortletURLFactory.createControlPanelRenderURL(
					AICreatorOpenAIPortletKeys.AI_CREATOR_OPENAI,
					themeDisplay.getScopeGroup(),
					themeDisplay.getRefererGroupId(), 0)
			).setMVCPath(
				"/view.jsp"
			).setPortletMode(
				PortletMode.VIEW
			).setWindowState(
				LiferayWindowState.POP_UP
			).buildString()
		).put(
			"aiCreatorPortletNamespace",
			() -> _portal.getPortletNamespace(
				AICreatorOpenAIPortletKeys.AI_CREATOR_OPENAI)
		).put(
			"isAICreatorOpenAIAPIKey",
			() -> {
				try {
					if (Validator.isNotNull(
							_aiCreatorOpenAIConfigurationManager.
								getAICreatorOpenAIGroupAPIKey(
									themeDisplay.getCompanyId(),
									themeDisplay.getScopeGroupId()))) {

						return true;
					}
				}
				catch (ConfigurationException configurationException) {
					if (_log.isDebugEnabled()) {
						_log.debug(configurationException);
					}
				}

				return false;
			}
		).put(
			"showAICreator", true
		);

		// CKEditor 4 (ckeditor_classic) renders the AI Creator button from the
		// "aicreator" plugin and a toolbar item. CKEditor 5 (ckeditor5_classic)
		// reads the "showAICreator" configuration directly, so only the classic
		// editor populates "extraPlugins".

		String extraPlugins = jsonObject.getString("extraPlugins");

		if (Validator.isNotNull(extraPlugins)) {
			jsonObject.put("extraPlugins", extraPlugins + ",aicreator");

			_addAICreatorToolbarItem(jsonObject);
		}
	}

	private void _addAICreatorToolbarItem(JSONObject jsonObject) {
		for (String key : jsonObject.keySet()) {
			if (!key.startsWith("toolbar_")) {
				continue;
			}

			JSONArray toolbarJSONArray = jsonObject.getJSONArray(key);

			if ((toolbarJSONArray == null) ||
				_hasAICreatorToolbarItem(toolbarJSONArray)) {

				continue;
			}

			toolbarJSONArray.put(toJSONArray("['AICreator']"));
		}
	}

	private boolean _hasAICreatorToolbarItem(JSONArray toolbarJSONArray) {
		for (int i = 0; i < toolbarJSONArray.length(); i++) {
			JSONArray itemJSONArray = toolbarJSONArray.getJSONArray(i);

			if ((itemJSONArray != null) &&
				JSONUtil.hasValue(itemJSONArray, "AICreator")) {

				return true;
			}
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AICreatorOpenAIEditorConfigContributor.class);

	@Reference
	private AICreatorOpenAIConfigurationManager
		_aiCreatorOpenAIConfigurationManager;

	@Reference
	private AICreatorOpenAIManager _aiCreatorOpenAIManager;

	@Reference
	private Portal _portal;

}