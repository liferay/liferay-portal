/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.definitions.web.internal.editor.configuration;

import com.liferay.ai.creator.openai.configuration.manager.AICreatorOpenAIConfigurationManager;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.portal.kernel.editor.configuration.BaseEditorConfigContributor;
import com.liferay.portal.kernel.editor.configuration.EditorConfigContributor;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.module.service.Snapshot;
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
 * @author Andrea Sbarra
 */
@Component(
	property = "jakarta.portlet.name=" + CPPortletKeys.CP_DEFINITIONS,
	service = EditorConfigContributor.class
)
public class CommerceCKEditorConfigContributor
	extends BaseEditorConfigContributor {

	@Override
	public void populateConfigJSONObject(
		JSONObject jsonObject, Map<String, Object> inputEditorTaglibAttributes,
		ThemeDisplay themeDisplay,
		RequestBackedPortletURLFactory requestBackedPortletURLFactory) {

		if (!_isAICreatorChatGPTGroupEnabled(
				themeDisplay.getCompanyId(), themeDisplay.getScopeGroupId())) {

			return;
		}

		EditorConfigContributor ckEditorConfigContributor =
			_ckEditorConfigContributorSnapshot.get();

		ckEditorConfigContributor.populateConfigJSONObject(
			jsonObject, inputEditorTaglibAttributes, themeDisplay,
			requestBackedPortletURLFactory);

		jsonObject.put(
			"aiCreatorOpenAIURL",
			() -> PortletURLBuilder.create(
				requestBackedPortletURLFactory.createControlPanelRenderURL(
					"com_liferay_ai_creator_openai_web_internal_portlet_" +
						"AICreatorOpenAIPortlet",
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
				"com_liferay_ai_creator_openai_web_internal_portlet_" +
					"AICreatorOpenAIPortlet")
		).put(
			"extraPlugins",
			() -> {
				String extraPlugins = (String)jsonObject.get("extraPlugins");

				return extraPlugins.concat(",aicreator");
			}
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
		);

		_putAICreator(
			jsonObject, "toolbar_simple", "toolbar_text_advanced",
			"toolbar_text_simple");
	}

	private boolean _isAICreatorChatGPTGroupEnabled(
		long companyId, long groupId) {

		try {
			if (_aiCreatorOpenAIConfigurationManager.
					isAICreatorChatGPTGroupEnabled(companyId, groupId)) {

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

	private void _putAICreator(JSONObject jsonObject, String... keys) {
		for (String key : keys) {
			JSONArray jsonArray = (JSONArray)jsonObject.get(key);

			jsonArray.put(JSONUtil.put("AICreator"));
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceCKEditorConfigContributor.class);

	@Reference
	private AICreatorOpenAIConfigurationManager
		_aiCreatorOpenAIConfigurationManager;

	private final Snapshot<EditorConfigContributor>
		_ckEditorConfigContributorSnapshot = new Snapshot<>(
			CommerceCKEditorConfigContributor.class,
			EditorConfigContributor.class, "(editor.name=ckeditor)");

	@Reference
	private Portal _portal;

}