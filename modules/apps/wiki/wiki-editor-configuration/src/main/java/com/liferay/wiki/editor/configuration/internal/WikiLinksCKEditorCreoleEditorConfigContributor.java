/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.editor.configuration.internal;

import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.portal.kernel.editor.configuration.EditorConfigContributor;
import com.liferay.wiki.constants.WikiPortletKeys;
import com.liferay.wiki.item.selector.WikiPageTitleItemSelectorReturnType;

import org.osgi.service.component.annotations.Component;

/**
 * @author Roberto Díaz
 */
@Component(
	property = {
		"editor.config.key=contentEditor", "editor.name=ckeditor_creole",
		"jakarta.portlet.name=" + WikiPortletKeys.WIKI,
		"jakarta.portlet.name=" + WikiPortletKeys.WIKI_ADMIN,
		"jakarta.portlet.name=" + WikiPortletKeys.WIKI_DISPLAY
	},
	service = EditorConfigContributor.class
)
public class WikiLinksCKEditorCreoleEditorConfigContributor
	extends BaseWikiLinksCKEditorConfigContributor {

	@Override
	protected ItemSelectorReturnType getItemSelectorReturnType() {
		return new WikiPageTitleItemSelectorReturnType();
	}

}