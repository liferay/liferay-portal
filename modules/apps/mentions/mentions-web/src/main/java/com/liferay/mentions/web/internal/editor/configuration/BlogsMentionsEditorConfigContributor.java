/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mentions.web.internal.editor.configuration;

import com.liferay.blogs.constants.BlogsPortletKeys;
import com.liferay.portal.kernel.editor.configuration.EditorConfigContributor;

import org.osgi.service.component.annotations.Component;

/**
 * @author Sergio González
 */
@Component(
	property = {
		"editor.config.key=contentEditor", "editor.name=alloyeditor",
		"editor.name=ckeditor", "editor.name=ckeditor_classic",
		"jakarta.portlet.name=" + BlogsPortletKeys.BLOGS,
		"jakarta.portlet.name=" + BlogsPortletKeys.BLOGS_ADMIN,
		"service.ranking:Integer=10"
	},
	service = EditorConfigContributor.class
)
public class BlogsMentionsEditorConfigContributor
	extends BaseMentionsEditorConfigContributor {
}