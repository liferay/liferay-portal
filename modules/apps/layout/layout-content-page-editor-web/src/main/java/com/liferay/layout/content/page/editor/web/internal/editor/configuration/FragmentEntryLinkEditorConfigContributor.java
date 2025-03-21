/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.editor.configuration;

import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.ItemSelectorCriterion;
import com.liferay.item.selector.criteria.DownloadURLItemSelectorReturnType;
import com.liferay.item.selector.criteria.URLItemSelectorReturnType;
import com.liferay.item.selector.criteria.file.criterion.FileItemSelectorCriterion;
import com.liferay.item.selector.criteria.image.criterion.ImageItemSelectorCriterion;
import com.liferay.item.selector.criteria.url.criterion.URLItemSelectorCriterion;
import com.liferay.layout.content.page.editor.constants.ContentPageEditorPortletKeys;
import com.liferay.layout.item.selector.criterion.LayoutItemSelectorCriterion;
import com.liferay.portal.kernel.editor.configuration.BaseEditorConfigContributor;
import com.liferay.portal.kernel.editor.configuration.EditorConfigContributor;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;

import jakarta.portlet.PortletURL;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pavel Savinov
 */
@Component(
	property = {
		"editor.config.key=fragmentEntryLinkEditor",
		"jakarta.portlet.name=" + ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET
	},
	service = EditorConfigContributor.class
)
public class FragmentEntryLinkEditorConfigContributor
	extends BaseEditorConfigContributor {

	@Override
	public void populateConfigJSONObject(
		JSONObject jsonObject, Map<String, Object> inputEditorTaglibAttributes,
		ThemeDisplay themeDisplay,
		RequestBackedPortletURLFactory requestBackedPortletURLFactory) {

		PortletURL itemSelectorURL = itemSelector.getItemSelectorURL(
			requestBackedPortletURLFactory, "_EDITOR_NAME_selectItem",
			getFileItemSelectorCriterion(), getLayoutItemSelectorURL());
		PortletURL imageSelectorURL = itemSelector.getItemSelectorURL(
			requestBackedPortletURLFactory, "_EDITOR_NAME_selectImage",
			getImageItemSelectorCriterion(), getURLItemSelectorCriterion());

		jsonObject.put(
			"allowedContent", ""
		).put(
			"disallowedContent", "br"
		).put(
			"documentBrowseLinkUrl", itemSelectorURL.toString()
		).put(
			"enterMode", 2
		).put(
			"extraPlugins", getExtraPluginsLists()
		).put(
			"filebrowserImageBrowseLinkUrl", imageSelectorURL.toString()
		).put(
			"filebrowserImageBrowseUrl", imageSelectorURL.toString()
		).put(
			"removePlugins", getRemovePluginsLists()
		).put(
			"skin", "moono-lisa"
		).put(
			"toolbars", jsonFactory.createJSONObject()
		);
	}

	protected String getExtraPluginsLists() {
		return "autolink,ae_dragresize,ae_addimages,ae_imagealignment," +
			"ae_placeholder,ae_selectionregion,ae_tableresize," +
				"ae_tabletools,ae_uicore,itemselector,media,adaptivemedia";
	}

	protected ItemSelectorCriterion getFileItemSelectorCriterion() {
		ItemSelectorCriterion fileItemSelectorCriterion =
			new FileItemSelectorCriterion();

		fileItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new DownloadURLItemSelectorReturnType());

		return fileItemSelectorCriterion;
	}

	protected ItemSelectorCriterion getImageItemSelectorCriterion() {
		ItemSelectorCriterion itemSelectorCriterion =
			new ImageItemSelectorCriterion();

		itemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new DownloadURLItemSelectorReturnType());

		return itemSelectorCriterion;
	}

	protected ItemSelectorCriterion getLayoutItemSelectorURL() {
		LayoutItemSelectorCriterion layoutItemSelectorCriterion =
			new LayoutItemSelectorCriterion();

		layoutItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new URLItemSelectorReturnType());

		return layoutItemSelectorCriterion;
	}

	protected String getRemovePluginsLists() {
		return "contextmenu,elementspath,floatingspace,image,liststyle," +
			"magicline,resize,tabletools,toolbar,ae_embed";
	}

	protected ItemSelectorCriterion getURLItemSelectorCriterion() {
		ItemSelectorCriterion itemSelectorCriterion =
			new URLItemSelectorCriterion();

		itemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new URLItemSelectorReturnType());

		return itemSelectorCriterion;
	}

	@Reference
	protected ItemSelector itemSelector;

	@Reference
	protected JSONFactory jsonFactory;

}