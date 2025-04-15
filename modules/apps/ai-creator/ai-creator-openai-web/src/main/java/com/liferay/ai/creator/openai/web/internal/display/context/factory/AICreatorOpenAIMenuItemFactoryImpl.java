/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.creator.openai.web.internal.display.context.factory;

import com.liferay.ai.creator.openai.configuration.manager.AICreatorOpenAIConfigurationManager;
import com.liferay.ai.creator.openai.display.context.factory.AICreatorOpenAIMenuItemFactory;
import com.liferay.ai.creator.openai.web.internal.constants.AICreatorOpenAIPortletKeys;
import com.liferay.document.library.display.context.DLUIItemKeys;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.servlet.taglib.ui.JavaScriptMenuItem;
import com.liferay.portal.kernel.servlet.taglib.ui.MenuItem;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletMode;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Roberto Díaz
 */
@Component(service = AICreatorOpenAIMenuItemFactory.class)
public class AICreatorOpenAIMenuItemFactoryImpl
	implements AICreatorOpenAIMenuItemFactory {

	@Override
	public MenuItem createAICreatorCreateImageMenuItem(
		long repositoryId, long folderId, long fileEntryTypeId,
		ThemeDisplay themeDisplay) {

		try {
			if (!_aiCreatorOpenAIConfigurationManager.
					isAICreatorDALLEGroupEnabled(
						themeDisplay.getCompanyId(),
						themeDisplay.getScopeGroupId())) {

				return null;
			}
		}
		catch (ConfigurationException configurationException) {
			if (_log.isDebugEnabled()) {
				_log.debug(configurationException);
			}
		}

		JavaScriptMenuItem javaScriptMenuItem = new JavaScriptMenuItem();

		javaScriptMenuItem.setData(
			HashMapBuilder.<String, Object>put(
				"action", "openAICreateImage"
			).put(
				"aiCreatorURL",
				() -> {
					RequestBackedPortletURLFactory
						requestBackedPortletURLFactory =
							RequestBackedPortletURLFactoryUtil.create(
								themeDisplay.getRequest());

					return PortletURLBuilder.create(
						requestBackedPortletURLFactory.
							createControlPanelRenderURL(
								AICreatorOpenAIPortletKeys.AI_CREATOR_OPENAI,
								themeDisplay.getScopeGroup(),
								themeDisplay.getRefererGroupId(), 0)
					).setMVCPath(
						"/view.jsp"
					).setParameter(
						"fileEntryTypeId", fileEntryTypeId
					).setParameter(
						"folderId", folderId
					).setParameter(
						"generations", true
					).setParameter(
						"repositoryId", repositoryId
					).setPortletMode(
						PortletMode.VIEW
					).setWindowState(
						LiferayWindowState.POP_UP
					).buildString();
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
			).build());

		javaScriptMenuItem.setIcon("stars");
		javaScriptMenuItem.setKey(DLUIItemKeys.AI_CREATOR);
		javaScriptMenuItem.setLabel(
			_language.get(themeDisplay.getRequest(), "create-ai-image"));

		return javaScriptMenuItem;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AICreatorOpenAIMenuItemFactoryImpl.class);

	@Reference
	private AICreatorOpenAIConfigurationManager
		_aiCreatorOpenAIConfigurationManager;

	@Reference
	private Language _language;

}