/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.web.internal.servlet.taglib.util;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.ItemSelectorCriterion;
import com.liferay.item.selector.criteria.FileEntryItemSelectorReturnType;
import com.liferay.item.selector.criteria.upload.criterion.UploadItemSelectorCriterion;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.configuration.UploadServletRequestConfigurationProviderUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.style.book.constants.StyleBookPortletKeys;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryLocalServiceUtil;
import com.liferay.style.book.web.internal.constants.StyleBookWebKeys;

import java.util.List;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceURL;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Eudaldo Alonso
 */
public class StyleBookEntryActionDropdownItemsProvider {

	public StyleBookEntryActionDropdownItemsProvider(
		StyleBookEntry styleBookEntry, RenderRequest renderRequest,
		RenderResponse renderResponse) {

		_styleBookEntry = styleBookEntry;
		_renderResponse = renderResponse;

		_httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
		_itemSelector = (ItemSelector)renderRequest.getAttribute(
			StyleBookWebKeys.ITEM_SELECTOR);
		_themeDisplay = (ThemeDisplay)_httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public List<DropdownItem> getActionDropdownItems() {
		if (_styleBookEntry.getStyleBookEntryId() <= 0) {
			return DropdownItemListBuilder.add(
				() -> !_styleBookEntry.isDefaultStyleBookEntry(),
				_getMarkAsDefaultStyleBookEntryActionUnsafeConsumer()
			).build();
		}

		return DropdownItemListBuilder.addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						_getEditStyleBookEntryActionUnsafeConsumer()
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						_getUpdateStyleBookEntryPreviewActionUnsafeConsumer()
					).add(
						() -> _styleBookEntry.getPreviewFileEntryId() > 0,
						_getDeleteStyleBookEntryPreviewActionUnsafeConsumer()
					).add(
						() -> {
							StyleBookEntry draftStyleBookEntry =
								StyleBookEntryLocalServiceUtil.fetchDraft(
									_styleBookEntry);

							if (draftStyleBookEntry != null) {
								return true;
							}

							return false;
						},
						_getDiscardDraftStyleBookEntryActionUnsafeConsumer()
					).add(
						() -> !_styleBookEntry.isDefaultStyleBookEntry(),
						_getMarkAsDefaultStyleBookEntryActionUnsafeConsumer()
					).add(
						_getRenameStyleBookEntrytActionUnsafeConsumer()
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						_getExportStyleBookEntryActionUnsafeConsumer()
					).add(
						_getCopyStyleBookEntryActionUnsafeConsumer()
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						_getDeleteStyleBookEntryActionUnsafeConsumer()
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).build();
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getCopyStyleBookEntryActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.putData("action", "copyStyleBookEntry");
			dropdownItem.putData(
				"copyStyleBookEntryURL",
				PortletURLBuilder.createActionURL(
					_renderResponse
				).setActionName(
					"/style_book/copy_style_book_entry"
				).setRedirect(
					_themeDisplay.getURLCurrent()
				).setParameter(
					"styleBookEntryIds", _styleBookEntry.getStyleBookEntryId()
				).buildString());
			dropdownItem.setIcon("copy");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "make-a-copy"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getDeleteStyleBookEntryActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.putData("action", "deleteStyleBookEntry");
			dropdownItem.putData(
				"deleteStyleBookEntryURL",
				PortletURLBuilder.createActionURL(
					_renderResponse
				).setActionName(
					"/style_book/delete_style_book_entry"
				).setRedirect(
					_themeDisplay.getURLCurrent()
				).setParameter(
					"styleBookEntryId", _styleBookEntry.getStyleBookEntryId()
				).buildString());
			dropdownItem.setIcon("trash");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "delete"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getDeleteStyleBookEntryPreviewActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.putData("action", "deleteStyleBookEntryPreview");
			dropdownItem.putData(
				"deleteStyleBookEntryPreviewURL",
				PortletURLBuilder.createActionURL(
					_renderResponse
				).setActionName(
					"/style_book/delete_style_book_entry_preview"
				).setRedirect(
					_themeDisplay.getURLCurrent()
				).setParameter(
					"styleBookEntryId", _styleBookEntry.getStyleBookEntryId()
				).buildString());
			dropdownItem.putData(
				"styleBookEntryId",
				String.valueOf(_styleBookEntry.getStyleBookEntryId()));
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "remove-thumbnail"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getDiscardDraftStyleBookEntryActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.putData("action", "discardDraftStyleBookEntry");
			dropdownItem.putData(
				"discardDraftStyleBookEntryURL",
				PortletURLBuilder.createActionURL(
					_renderResponse
				).setActionName(
					"/style_book/discard_draft_style_book_entry"
				).setRedirect(
					_themeDisplay.getURLCurrent()
				).setParameter(
					"styleBookEntryId", _styleBookEntry.getStyleBookEntryId()
				).buildString());
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "discard-draft"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getEditStyleBookEntryActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.setHref(
				_renderResponse.createRenderURL(), "mvcRenderCommandName",
				"/style_book/edit_style_book_entry", "styleBookEntryId",
				_styleBookEntry.getStyleBookEntryId());
			dropdownItem.setIcon("pencil");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "edit"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getExportStyleBookEntryActionUnsafeConsumer() {

		ResourceURL exportStyleBookEntryURL =
			_renderResponse.createResourceURL();

		exportStyleBookEntryURL.setParameter(
			"styleBookEntryId",
			String.valueOf(_styleBookEntry.getStyleBookEntryId()));
		exportStyleBookEntryURL.setResourceID(
			"/style_book/export_style_book_entries");

		return dropdownItem -> {
			dropdownItem.setHref(exportStyleBookEntryURL);
			dropdownItem.setIcon("upload");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "export"));
		};
	}

	private String _getItemSelectorURL() {
		ItemSelectorCriterion itemSelectorCriterion =
			UploadItemSelectorCriterion.builder(
			).desiredItemSelectorReturnTypes(
				new FileEntryItemSelectorReturnType()
			).extensions(
				new String[] {".bmp", ".jpeg", ".jpg", ".png", ".tiff"}
			).maxFileSize(
				UploadServletRequestConfigurationProviderUtil.getMaxSize()
			).portletId(
				StyleBookPortletKeys.STYLE_BOOK
			).repositoryName(
				LanguageUtil.get(_httpServletRequest, "style-book")
			).url(
				PortletURLBuilder.createActionURL(
					_renderResponse
				).setActionName(
					"/style_book/upload_style_book_entry_preview"
				).setParameter(
					"styleBookEntryId", _styleBookEntry.getStyleBookEntryId()
				).buildString()
			).build();

		return String.valueOf(
			_itemSelector.getItemSelectorURL(
				RequestBackedPortletURLFactoryUtil.create(_httpServletRequest),
				_renderResponse.getNamespace() + "changePreview",
				itemSelectorCriterion));
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getMarkAsDefaultStyleBookEntryActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.putData("action", "markAsDefaultStyleBookEntry");
			dropdownItem.putData(
				"markAsDefaultStyleBookEntryURL",
				PortletURLBuilder.createActionURL(
					_renderResponse
				).setActionName(
					"/style_book/update_style_book_entry_default"
				).setRedirect(
					_themeDisplay.getURLCurrent()
				).setParameter(
					"defaultStyleBookEntry",
					!_styleBookEntry.isDefaultStyleBookEntry()
				).setParameter(
					"styleBookEntryId", _styleBookEntry.getStyleBookEntryId()
				).buildString());

			StyleBookEntry defaultStyleBookEntry =
				StyleBookEntryLocalServiceUtil.fetchDefaultStyleBookEntry(
					_styleBookEntry.getGroupId());

			String defaultStyleBookEntryName = LanguageUtil.get(
				_httpServletRequest, "styles-from-theme");

			if (defaultStyleBookEntry != null) {
				defaultStyleBookEntryName = defaultStyleBookEntry.getName();
			}

			dropdownItem.putData(
				"message",
				LanguageUtil.format(
					_httpServletRequest,
					"do-you-want-to-replace-x-for-x-as-the-default-style-book",
					new String[] {
						defaultStyleBookEntryName, _styleBookEntry.getName()
					}));

			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "mark-as-default"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getRenameStyleBookEntrytActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.putData("action", "renameStyleBookEntry");
			dropdownItem.putData(
				"styleBookEntryId",
				String.valueOf(_styleBookEntry.getStyleBookEntryId()));
			dropdownItem.putData(
				"styleBookEntryName", _styleBookEntry.getName());
			dropdownItem.putData(
				"updateStyleBookEntryURL",
				PortletURLBuilder.createActionURL(
					_renderResponse
				).setActionName(
					"/style_book/update_style_book_entry_name"
				).setRedirect(
					_themeDisplay.getURLCurrent()
				).setParameter(
					"styleBookEntryId", _styleBookEntry.getStyleBookEntryId()
				).buildString());
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "rename"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getUpdateStyleBookEntryPreviewActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.putData("action", "updateStyleBookEntryPreview");
			dropdownItem.putData("itemSelectorURL", _getItemSelectorURL());
			dropdownItem.putData(
				"styleBookEntryId",
				String.valueOf(_styleBookEntry.getStyleBookEntryId()));
			dropdownItem.setIcon("change");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "change-thumbnail"));
		};
	}

	private final HttpServletRequest _httpServletRequest;
	private final ItemSelector _itemSelector;
	private final RenderResponse _renderResponse;
	private final StyleBookEntry _styleBookEntry;
	private final ThemeDisplay _themeDisplay;

}