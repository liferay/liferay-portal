/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.editor.configuration.internal;

import com.liferay.item.selector.ItemSelectorCriterion;
import com.liferay.item.selector.criteria.FileEntryItemSelectorReturnType;
import com.liferay.portal.kernel.editor.configuration.EditorConfigContributor;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.wiki.constants.WikiPortletKeys;

import org.osgi.service.component.annotations.Component;

/**
 * @author Ambrín Chaudhary
 */
@Component(
	configurationPid = "com.liferay.wiki.configuration.WikiFileUploadConfiguration",
	property = {
		"editor.config.key=contentEditor", "editor.name=alloyeditor_creole",
		"editor.name=ckeditor_creole",
		"jakarta.portlet.name=" + WikiPortletKeys.WIKI,
		"jakarta.portlet.name=" + WikiPortletKeys.WIKI_ADMIN,
		"jakarta.portlet.name=" + WikiPortletKeys.WIKI_DISPLAY,
		"service.ranking:Integer=100"
	},
	service = EditorConfigContributor.class
)
public class WikiAttachmentImageCreoleEditorConfigContributor
	extends BaseWikiAttachmentImageEditorConfigContributor {

	@Override
	protected String getItemSelectorURL(
		RequestBackedPortletURLFactory requestBackedPortletURLFactory,
		String itemSelectedEventName, long wikiPageResourcePrimKey,
		ThemeDisplay themeDisplay) {

		if (wikiPageResourcePrimKey == 0) {
			return String.valueOf(
				itemSelector.getItemSelectorURL(
					requestBackedPortletURLFactory, itemSelectedEventName,
					getURLItemSelectorCriterion()));
		}

		ItemSelectorCriterion attachmentItemSelectorCriterion =
			getWikiAttachmentItemSelectorCriterion(
				wikiPageResourcePrimKey, new FileEntryItemSelectorReturnType());

		ItemSelectorCriterion uploadItemSelectorCriterion =
			getUploadItemSelectorCriterion(
				wikiPageResourcePrimKey, themeDisplay,
				requestBackedPortletURLFactory);

		return String.valueOf(
			itemSelector.getItemSelectorURL(
				requestBackedPortletURLFactory, itemSelectedEventName,
				attachmentItemSelectorCriterion, getURLItemSelectorCriterion(),
				uploadItemSelectorCriterion));
	}

}