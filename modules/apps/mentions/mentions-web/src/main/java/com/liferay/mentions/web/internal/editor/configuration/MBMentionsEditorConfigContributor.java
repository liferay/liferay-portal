/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mentions.web.internal.editor.configuration;

import com.liferay.message.boards.constants.MBPortletKeys;
import com.liferay.portal.kernel.editor.configuration.EditorConfigContributor;

import org.osgi.service.component.annotations.Component;

/**
 * @author Sergio González
 */
@Component(
	property = {
		"editor.config.key=bodyEditor", "editor.config.key=replyMBEditor",
		"editor.name=alloyeditor", "editor.name=alloyeditor_bbcode",
		"editor.name=ckeditor_bbcode", "editor.name=ckeditor_classic",
		"jakarta.portlet.name=" + MBPortletKeys.MESSAGE_BOARDS,
		"jakarta.portlet.name=" + MBPortletKeys.MESSAGE_BOARDS_ADMIN,
		"service.ranking:Integer=10"
	},
	service = EditorConfigContributor.class
)
public class MBMentionsEditorConfigContributor
	extends BaseMentionsEditorConfigContributor {
}