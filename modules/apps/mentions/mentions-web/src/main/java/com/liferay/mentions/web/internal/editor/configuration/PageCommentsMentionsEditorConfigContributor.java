/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mentions.web.internal.editor.configuration;

import com.liferay.layout.content.page.editor.constants.ContentPageEditorPortletKeys;
import com.liferay.portal.kernel.editor.configuration.EditorConfigContributor;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;

import jakarta.portlet.PortletURL;

import org.osgi.service.component.annotations.Component;

/**
 * @author Ambrín Chaudhary
 */
@Component(
	property = {
		"editor.config.key=pageEditorCommentEditor",
		"jakarta.portlet.name=" + ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET,
		"service.ranking:Integer=10"
	},
	service = EditorConfigContributor.class
)
public class PageCommentsMentionsEditorConfigContributor
	extends BaseMentionsEditorConfigContributor {

	@Override
	protected PortletURL getPortletURL(
		ThemeDisplay themeDisplay,
		RequestBackedPortletURLFactory requestBackedPortletURLFactory) {

		return PortletURLBuilder.create(
			super.getPortletURL(themeDisplay, requestBackedPortletURLFactory)
		).setParameter(
			"strategy",
			JSONUtil.put(
				"plid", themeDisplay.getPlid()
			).put(
				"strategy", "pageEditorCommentStrategy"
			)
		).buildPortletURL();
	}

}