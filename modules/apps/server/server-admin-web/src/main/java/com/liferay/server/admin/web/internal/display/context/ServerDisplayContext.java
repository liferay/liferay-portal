/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.server.admin.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItemList;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.util.List;

/**
 * @author Pei-Jung Lan
 */
public class ServerDisplayContext {

	public ServerDisplayContext(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		_renderRequest = renderRequest;
		_renderResponse = renderResponse;
	}

	public List<NavigationItem> getServerNavigationItems() {
		String tabs1 = ParamUtil.getString(
			_renderRequest, "tabs1", "resources");
		String tabs2 = ParamUtil.getString(_renderRequest, "tabs2");

		ThemeDisplay themeDisplay = (ThemeDisplay)_renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		return new NavigationItemList() {
			{
				for (String tabs1Name : _TABS1_NAMES) {
					add(
						navigationItem -> {
							navigationItem.setActive(tabs1.equals(tabs1Name));
							navigationItem.setHref(
								_renderResponse.createRenderURL(),
								"mvcRenderCommandName", "/server_admin/view",
								"tabs1", tabs1Name, "tabs2", tabs2);
							navigationItem.setLabel(
								LanguageUtil.get(
									themeDisplay.getLocale(), tabs1Name));
						});
				}
			}
		};
	}

	public String getSessionMessagesKey() {
		if (SessionMessages.contains(
				_renderRequest, "dlGenerateAudioPreviews")) {

			return "audio-file-preview-and-thumbnail-regeneration-has-" +
				"started-successfully-and-will-continue-in-the-background";
		}
		else if (SessionMessages.contains(
					_renderRequest, "dlGenerateOpenOfficePreviews")) {

			return "openoffice-file-preview-and-thumbnail-regeneration-has-" +
				"started-successfully-and-will-continue-in-the-background";
		}
		else if (SessionMessages.contains(
					_renderRequest, "dlGeneratePDFPreviews")) {

			return "pdf-file-preview-and-thumbnail-regeneration-has-started-" +
				"successfully-and-will-continue-in-the-background";
		}
		else if (SessionMessages.contains(
					_renderRequest, "dlGenerateVideoPreviews")) {

			return "video-file-preview-and-thumbnail-regeneration-has-" +
				"started-successfully-and-will-continue-in-the-background";
		}

		return StringPool.BLANK;
	}

	private static final String[] _TABS1_NAMES = {
		"resources", "log-levels", "properties", "data-migration", "mail",
		"external-services", "script", "shutdown"
	};

	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;

}