/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.editor.configuration.internal;

import com.liferay.document.library.kernel.util.DLValidator;
import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.ItemSelectorCriterion;
import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.constants.ItemSelectorCriterionConstants;
import com.liferay.item.selector.criteria.FileEntryItemSelectorReturnType;
import com.liferay.item.selector.criteria.URLItemSelectorReturnType;
import com.liferay.item.selector.criteria.image.criterion.ImageItemSelectorCriterion;
import com.liferay.item.selector.criteria.upload.criterion.UploadItemSelectorCriterion;
import com.liferay.item.selector.criteria.url.criterion.URLItemSelectorCriterion;
import com.liferay.knowledge.base.constants.KBPortletKeys;
import com.liferay.knowledge.base.item.selector.KBAttachmentItemSelectorCriterion;
import com.liferay.portal.kernel.editor.configuration.BaseEditorConfigContributor;
import com.liferay.portal.kernel.editor.configuration.EditorConfigContributor;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.language.LanguageResources;

import jakarta.portlet.PortletURL;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Roberto Díaz
 */
@Component(
	property = {
		"editor.config.key=contentEditor",
		"jakarta.portlet.name=" + KBPortletKeys.KNOWLEDGE_BASE_ADMIN,
		"jakarta.portlet.name=" + KBPortletKeys.KNOWLEDGE_BASE_ARTICLE,
		"jakarta.portlet.name=" + KBPortletKeys.KNOWLEDGE_BASE_DISPLAY,
		"jakarta.portlet.name=" + KBPortletKeys.KNOWLEDGE_BASE_SEARCH,
		"jakarta.portlet.name=" + KBPortletKeys.KNOWLEDGE_BASE_SECTION
	},
	service = EditorConfigContributor.class
)
public class KBAttachmentEditorConfigContributor
	extends BaseEditorConfigContributor {

	@Override
	public void populateConfigJSONObject(
		JSONObject jsonObject, Map<String, Object> inputEditorTaglibAttributes,
		ThemeDisplay themeDisplay,
		RequestBackedPortletURLFactory requestBackedPortletURLFactory) {

		boolean allowBrowseDocuments = GetterUtil.getBoolean(
			inputEditorTaglibAttributes.get(
				"liferay-ui:input-editor:allowBrowseDocuments"));

		if (!allowBrowseDocuments) {
			return;
		}

		Map<String, String> fileBrowserParamsMap =
			(Map<String, String>)inputEditorTaglibAttributes.get(
				"liferay-ui:input-editor:fileBrowserParams");

		long resourcePrimKey = 0;

		if (fileBrowserParamsMap != null) {
			resourcePrimKey = GetterUtil.getLong(
				fileBrowserParamsMap.get("resourcePrimKey"));
		}

		if (resourcePrimKey == 0) {
			return;
		}

		String namespace = GetterUtil.getString(
			inputEditorTaglibAttributes.get(
				"liferay-ui:input-editor:namespace"));
		String name = GetterUtil.getString(
			inputEditorTaglibAttributes.get("liferay-ui:input-editor:name"));

		List<ItemSelectorReturnType> desiredItemSelectorReturnTypes =
			new ArrayList<>();

		desiredItemSelectorReturnTypes.add(
			new FileEntryItemSelectorReturnType());
		desiredItemSelectorReturnTypes.add(new URLItemSelectorReturnType());

		PortletURL itemSelectorURL = _itemSelector.getItemSelectorURL(
			requestBackedPortletURLFactory, namespace + name + "selectItem",
			_getKBAttachmentItemSelectorCriterion(
				resourcePrimKey, desiredItemSelectorReturnTypes),
			_getImageItemSelectorCriterion(desiredItemSelectorReturnTypes),
			_getURLItemSelectorCriterion(),
			_getUploadItemSelectorCriterion(
				resourcePrimKey, themeDisplay, requestBackedPortletURLFactory));

		jsonObject.put(
			"filebrowserImageBrowseLinkUrl", itemSelectorURL.toString()
		).put(
			"filebrowserImageBrowseUrl", itemSelectorURL.toString()
		);
	}

	private ItemSelectorCriterion _getImageItemSelectorCriterion(
		List<ItemSelectorReturnType> desiredItemSelectorReturnTypes) {

		ItemSelectorCriterion itemSelectorCriterion =
			new ImageItemSelectorCriterion();

		itemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			desiredItemSelectorReturnTypes);

		return itemSelectorCriterion;
	}

	private ItemSelectorCriterion _getKBAttachmentItemSelectorCriterion(
		long resourcePrimKey,
		List<ItemSelectorReturnType> desiredItemSelectorReturnTypes) {

		ItemSelectorCriterion itemSelectorCriterion =
			new KBAttachmentItemSelectorCriterion(resourcePrimKey);

		itemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			desiredItemSelectorReturnTypes);

		return itemSelectorCriterion;
	}

	private ItemSelectorCriterion _getUploadItemSelectorCriterion(
		long resourcePrimKey, ThemeDisplay themeDisplay,
		RequestBackedPortletURLFactory requestBackedPortletURLFactory) {

		return UploadItemSelectorCriterion.builder(
		).desiredItemSelectorReturnTypes(
			new FileEntryItemSelectorReturnType()
		).maxFileSize(
			_dlValidator.getMaxAllowableSize(
				themeDisplay.getScopeGroupId(), null)
		).mimeTypeRestriction(
			ItemSelectorCriterionConstants.MIME_TYPE_RESTRICTION_IMAGE
		).repositoryName(
			LanguageResources.getMessage(
				themeDisplay.getLocale(), "article-attachments")
		).url(
			PortletURLBuilder.create(
				requestBackedPortletURLFactory.createActionURL(
					KBPortletKeys.KNOWLEDGE_BASE_ADMIN)
			).setActionName(
				"/knowledge_base/upload_kb_article_attachments"
			).setParameter(
				"resourcePrimKey", resourcePrimKey
			).buildString()
		).build();
	}

	private ItemSelectorCriterion _getURLItemSelectorCriterion() {
		ItemSelectorCriterion itemSelectorCriterion =
			new URLItemSelectorCriterion();

		itemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new URLItemSelectorReturnType());

		return itemSelectorCriterion;
	}

	@Reference
	private DLValidator _dlValidator;

	@Reference
	private ItemSelector _itemSelector;

}