/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.editor.configuration.internal;

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
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.editor.configuration.BaseEditorConfigContributor;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.wiki.configuration.WikiFileUploadConfiguration;
import com.liferay.wiki.constants.WikiPortletKeys;
import com.liferay.wiki.item.selector.WikiAttachmentItemSelectorCriterion;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Sergio González
 * @author Roberto Díaz
 */
public abstract class BaseWikiAttachmentImageEditorConfigContributor
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

		long wikiPageResourcePrimKey = 0;

		if (fileBrowserParamsMap != null) {
			wikiPageResourcePrimKey = GetterUtil.getLong(
				fileBrowserParamsMap.get("wikiPageResourcePrimKey"));
		}

		if (wikiPageResourcePrimKey == 0) {
			String removePlugins = jsonObject.getString("removePlugins");

			if (Validator.isNotNull(removePlugins)) {
				removePlugins += ",ae_addimages";
			}
			else {
				removePlugins = "ae_addimages";
			}

			jsonObject.put("removePlugins", removePlugins);
		}

		String name = GetterUtil.getString(
			inputEditorTaglibAttributes.get("liferay-ui:input-editor:name"));

		boolean inlineEdit = GetterUtil.getBoolean(
			inputEditorTaglibAttributes.get(
				"liferay-ui:input-editor:inlineEdit"));

		if (!inlineEdit) {
			String namespace = GetterUtil.getString(
				inputEditorTaglibAttributes.get(
					"liferay-ui:input-editor:namespace"));

			name = namespace + name;
		}

		String itemSelectorURL = getItemSelectorURL(
			requestBackedPortletURLFactory, name + "selectItem",
			wikiPageResourcePrimKey, themeDisplay);

		jsonObject.put(
			"filebrowserImageBrowseLinkUrl", itemSelectorURL
		).put(
			"filebrowserImageBrowseUrl", itemSelectorURL
		);
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		wikiFileUploadConfiguration = ConfigurableUtil.createConfigurable(
			WikiFileUploadConfiguration.class, properties);
	}

	protected ItemSelectorCriterion getImageItemSelectorCriterion(
		ItemSelectorReturnType... desiredItemSelectorReturnTypes) {

		ItemSelectorCriterion imageItemSelectorCriterion =
			new ImageItemSelectorCriterion();

		imageItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			desiredItemSelectorReturnTypes);

		return imageItemSelectorCriterion;
	}

	protected abstract String getItemSelectorURL(
		RequestBackedPortletURLFactory requestBackedPortletURLFactory,
		String itemSelectedEventName, long wikiPageResourcePrimKey,
		ThemeDisplay themeDisplay);

	protected ItemSelectorCriterion getUploadItemSelectorCriterion(
		long wikiPageResourcePrimKey, ThemeDisplay themeDisplay,
		RequestBackedPortletURLFactory requestBackedPortletURLFactory) {

		List<String> extensions = new ArrayList<>();

		String[] attachmentMimeTypes =
			wikiFileUploadConfiguration.attachmentMimeTypes();

		for (String attachmentMimeType : attachmentMimeTypes) {
			extensions.addAll(MimeTypesUtil.getExtensions(attachmentMimeType));
		}

		return UploadItemSelectorCriterion.builder(
		).desiredItemSelectorReturnTypes(
			new FileEntryItemSelectorReturnType()
		).extensions(
			extensions.toArray(new String[0])
		).maxFileSize(
			dlValidator.getMaxAllowableSize(
				themeDisplay.getScopeGroupId(), null,
				wikiFileUploadConfiguration.attachmentMaxSize())
		).mimeTypeRestriction(
			ItemSelectorCriterionConstants.MIME_TYPE_RESTRICTION_IMAGE
		).portletId(
			WikiPortletKeys.WIKI
		).repositoryName(
			LanguageUtil.get(themeDisplay.getLocale(), "page-attachments")
		).url(
			PortletURLBuilder.create(
				requestBackedPortletURLFactory.createActionURL(
					WikiPortletKeys.WIKI)
			).setActionName(
				"/wiki/upload_page_attachment"
			).setParameter(
				"mimeTypes", _getMimeTypes()
			).setParameter(
				"resourcePrimKey", wikiPageResourcePrimKey
			).buildString()
		).build();
	}

	protected ItemSelectorCriterion getURLItemSelectorCriterion() {
		ItemSelectorCriterion itemSelectorCriterion =
			new URLItemSelectorCriterion();

		itemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new URLItemSelectorReturnType());

		return itemSelectorCriterion;
	}

	protected ItemSelectorCriterion getWikiAttachmentItemSelectorCriterion(
		long wikiPageResourcePrimKey,
		ItemSelectorReturnType... desiredItemSelectorReturnTypes) {

		ItemSelectorCriterion itemSelectorCriterion =
			new WikiAttachmentItemSelectorCriterion(
				wikiPageResourcePrimKey, _getMimeTypes());

		itemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			desiredItemSelectorReturnTypes);

		return itemSelectorCriterion;
	}

	@Reference
	protected DLValidator dlValidator;

	@Reference
	protected ItemSelector itemSelector;

	protected volatile WikiFileUploadConfiguration wikiFileUploadConfiguration;

	private String[] _getMimeTypes() {
		String[] dlFileEntryPreviewImageMimeTypes =
			PropsValues.DL_FILE_ENTRY_PREVIEW_IMAGE_MIME_TYPES;

		List<String> wikiAttachmentMimeTypes = ListUtil.fromArray(
			wikiFileUploadConfiguration.attachmentMimeTypes());

		if (wikiAttachmentMimeTypes.contains(StringPool.STAR)) {
			return dlFileEntryPreviewImageMimeTypes;
		}

		List<String> mimeTypes = new ArrayList<>();

		for (String mimeType : dlFileEntryPreviewImageMimeTypes) {
			if (wikiAttachmentMimeTypes.contains(mimeType)) {
				mimeTypes.add(mimeType);
			}
		}

		return mimeTypes.toArray(new String[0]);
	}

}