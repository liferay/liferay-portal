/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.creator.openai.web.internal.editor.configuration;

import com.liferay.ai.creator.openai.configuration.manager.AICreatorOpenAIConfigurationManager;
import com.liferay.ai.creator.openai.web.internal.constants.AICreatorOpenAIPortletKeys;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.portal.kernel.editor.configuration.BaseEditorConfigContributor;
import com.liferay.portal.kernel.editor.configuration.EditorConfigContributor;
import com.liferay.portal.kernel.json.JSONObject;
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
		"editor.config.key=rich_text", "editor.name=ckeditor_classic",
		"jakarta.portlet.name=" + JournalPortletKeys.JOURNAL
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

		if (!_isAICreatorChatGPTGroupEnabled(
				themeDisplay.getCompanyId(), themeDisplay.getScopeGroupId())) {

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
		);
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

	private static final Log _log = LogFactoryUtil.getLog(
		AICreatorOpenAIEditorConfigContributor.class);

	@Reference
	private AICreatorOpenAIConfigurationManager
		_aiCreatorOpenAIConfigurationManager;

	@Reference
	private Portal _portal;

}