/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.web.internal.servlet.taglib.util;

import com.liferay.fragment.collection.item.selector.FragmentCollectionItemSelectorCriterion;
import com.liferay.fragment.constants.FragmentActionKeys;
import com.liferay.fragment.constants.FragmentPortletKeys;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.web.internal.configuration.FragmentPortletConfiguration;
import com.liferay.fragment.web.internal.security.permission.resource.FragmentPermission;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.ItemSelectorCriterion;
import com.liferay.item.selector.criteria.FileEntryItemSelectorReturnType;
import com.liferay.item.selector.criteria.UUIDItemSelectorReturnType;
import com.liferay.item.selector.criteria.upload.criterion.UploadItemSelectorCriterion;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.configuration.UploadServletRequestConfigurationProviderUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;
import jakarta.portlet.ResourceURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class BasicFragmentEntryActionDropdownItemsProvider {

	public BasicFragmentEntryActionDropdownItemsProvider(
		FragmentEntry fragmentEntry, RenderRequest renderRequest,
		RenderResponse renderResponse) {

		_fragmentEntry = fragmentEntry;
		_renderResponse = renderResponse;

		_httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);

		_fragmentPortletConfiguration =
			(FragmentPortletConfiguration)_httpServletRequest.getAttribute(
				FragmentPortletConfiguration.class.getName());
		_itemSelector = (ItemSelector)_httpServletRequest.getAttribute(
			ItemSelector.class.getName());
		_themeDisplay = (ThemeDisplay)_httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public List<DropdownItem> getActionDropdownItems() throws Exception {
		boolean hasManageFragmentEntriesPermission =
			FragmentPermission.contains(
				_themeDisplay.getPermissionChecker(),
				_themeDisplay.getScopeGroupId(),
				FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES);

		return DropdownItemListBuilder.addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() ->
							hasManageFragmentEntriesPermission &&
							!_fragmentEntry.isTypeReact() &&
							!_fragmentEntry.isMarketplace(),
						_getEditFragmentEntryActionUnsafeConsumer()
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() ->
							hasManageFragmentEntriesPermission &&
							!_fragmentEntry.isReadOnly() &&
							!_fragmentEntry.isMarketplace(),
						_getUpdateFragmentEntryPreviewActionUnsafeConsumer()
					).add(
						() ->
							hasManageFragmentEntriesPermission &&
							!_fragmentEntry.isReadOnly() &&
							(_fragmentEntry.getPreviewFileEntryId() > 0) &&
							!_fragmentEntry.isMarketplace(),
						_getDeleteFragmentEntryPreviewActionUnsafeConsumer()
					).add(
						() ->
							hasManageFragmentEntriesPermission &&
							!_fragmentEntry.isReadOnly() &&
							(_fragmentEntry.isDraft() ||
							 (_fragmentEntry.fetchDraftFragmentEntry() !=
								 null)) &&
							!_fragmentEntry.isTypeReact() &&
							!_fragmentEntry.isMarketplace(),
						_getDeleteDraftFragmentEntryActionUnsafeConsumer()
					).add(
						() ->
							hasManageFragmentEntriesPermission &&
							!_fragmentEntry.isReadOnly() &&
							!_fragmentEntry.isMarketplace(),
						_getRenameFragmentEntryActionUnsafeConsumer()
					).add(
						() ->
							hasManageFragmentEntriesPermission &&
							!_fragmentEntry.isCacheable() &&
							!_fragmentEntry.isReadOnly() &&
							!_fragmentEntry.isTypeInput() &&
							!_fragmentEntry.isTypeReact() &&
							!_fragmentEntry.isMarketplace(),
						_getMarkAsCacheableActionUnsafeConsumer()
					).add(
						() ->
							hasManageFragmentEntriesPermission &&
							_fragmentEntry.isCacheable() &&
							!_fragmentEntry.isReadOnly() &&
							!_fragmentEntry.isTypeInput() &&
							!_fragmentEntry.isTypeReact() &&
							!_fragmentEntry.isMarketplace(),
						_getUnmarkAsCacheableActionUnsafeConsumer()
					).add(
						() ->
							hasManageFragmentEntriesPermission &&
							!_fragmentEntry.isReadOnly() &&
							(_fragmentEntry.getGroupId() ==
								_themeDisplay.getCompanyGroupId()),
						_getViewGroupFragmentEntryUsagesActionUnsafeConsumer()
					).add(
						() ->
							hasManageFragmentEntriesPermission &&
							!_fragmentEntry.isReadOnly() &&
							(_fragmentEntry.getGroupId() !=
								_themeDisplay.getCompanyGroupId()),
						_getViewFragmentEntryUsagesActionUnsafeConsumer()
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() ->
							hasManageFragmentEntriesPermission &&
							!_fragmentEntry.isReadOnly() &&
							!_fragmentEntry.isTypeReact() &&
							!_fragmentEntry.isMarketplace(),
						_getExportFragmentEntryActionUnsafeConsumer()
					).add(
						() ->
							hasManageFragmentEntriesPermission &&
							!_fragmentEntry.isMarketplace(),
						_getCopyFragmentEntryActionUnsafeConsumer()
					).add(
						() ->
							hasManageFragmentEntriesPermission &&
							!_fragmentEntry.isReadOnly(),
						_getMoveFragmentEntryActionUnsafeConsumer()
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() ->
							hasManageFragmentEntriesPermission &&
							!_fragmentEntry.isReadOnly(),
						_getDeleteFragmentEntryActionUnsafeConsumer()
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).build();
	}

	private UnsafeConsumer<DropdownItem, Exception>
			_getCopyFragmentEntryActionUnsafeConsumer()
		throws Exception {

		return dropdownItem -> {
			dropdownItem.putData("action", "copyFragmentEntry");
			dropdownItem.putData(
				"copyFragmentEntryURL",
				PortletURLBuilder.createActionURL(
					_renderResponse
				).setActionName(
					"/fragment/copy_fragment_entry"
				).setRedirect(
					_themeDisplay.getURLCurrent()
				).buildString());
			dropdownItem.putData(
				"fragmentCollectionId",
				String.valueOf(_fragmentEntry.getFragmentCollectionId()));
			dropdownItem.putData(
				"fragmentEntryId",
				String.valueOf(_fragmentEntry.getFragmentEntryId()));
			dropdownItem.setIcon("copy");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "make-a-copy"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getDeleteDraftFragmentEntryActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.putData("action", "deleteDraftFragmentEntry");
			dropdownItem.putData(
				"deleteDraftFragmentEntryURL",
				PortletURLBuilder.createActionURL(
					_renderResponse
				).setActionName(
					"/fragment/delete_draft_fragment_entries"
				).setRedirect(
					_themeDisplay.getURLCurrent()
				).setParameter(
					"fragmentEntryId", _fragmentEntry.getFragmentEntryId()
				).buildString());
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "discard-draft"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getDeleteFragmentEntryActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.putData("action", "deleteFragmentEntry");
			dropdownItem.putData(
				"deleteFragmentEntryURL",
				PortletURLBuilder.createActionURL(
					_renderResponse
				).setActionName(
					"/fragment/delete_fragment_entries"
				).setRedirect(
					_themeDisplay.getURLCurrent()
				).setParameter(
					"fragmentEntryId", _fragmentEntry.getFragmentEntryId()
				).buildString());
			dropdownItem.setIcon("trash");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "delete"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getDeleteFragmentEntryPreviewActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.putData("action", "deleteFragmentEntryPreview");
			dropdownItem.putData(
				"deleteFragmentEntryPreviewURL",
				PortletURLBuilder.createActionURL(
					_renderResponse
				).setActionName(
					"/fragment/delete_fragment_entry_preview"
				).setRedirect(
					_themeDisplay.getURLCurrent()
				).setParameter(
					"fragmentEntryId", _fragmentEntry.getFragmentEntryId()
				).buildString());
			dropdownItem.putData(
				"fragmentEntryId",
				String.valueOf(_fragmentEntry.getFragmentEntryId()));
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "remove-thumbnail"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getEditFragmentEntryActionUnsafeConsumer() {

		FragmentEntry fragmentEntry = null;

		if (_fragmentEntry.isDraft()) {
			fragmentEntry = _fragmentEntry;
		}
		else {
			fragmentEntry = _fragmentEntry.fetchDraftFragmentEntry();
		}

		if (fragmentEntry == null) {
			fragmentEntry = _fragmentEntry;
		}

		FragmentEntry editFragmentEntry = fragmentEntry;

		return dropdownItem -> {
			dropdownItem.setHref(
				_renderResponse.createRenderURL(), "mvcRenderCommandName",
				"/fragment/edit_fragment_entry", "redirect",
				_themeDisplay.getURLCurrent(), "fragmentCollectionId",
				editFragmentEntry.getFragmentCollectionId(), "fragmentEntryId",
				editFragmentEntry.getFragmentEntryId());
			dropdownItem.setIcon("pencil");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "edit"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getExportFragmentEntryActionUnsafeConsumer() {

		ResourceURL exportFragmentEntryURL =
			_renderResponse.createResourceURL();

		exportFragmentEntryURL.setParameter(
			"fragmentEntryId",
			String.valueOf(_fragmentEntry.getFragmentEntryId()));
		exportFragmentEntryURL.setResourceID(
			"/fragment/export_fragment_compositions_and_fragment_entries");

		return dropdownItem -> {
			dropdownItem.setDisabled(_fragmentEntry.isDraft());
			dropdownItem.setHref(exportFragmentEntryURL);
			dropdownItem.setIcon("export");
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
				_fragmentPortletConfiguration.thumbnailExtensions()
			).maxFileSize(
				UploadServletRequestConfigurationProviderUtil.getMaxSize()
			).portletId(
				FragmentPortletKeys.FRAGMENT
			).repositoryName(
				LanguageUtil.get(_themeDisplay.getLocale(), "fragments")
			).url(
				PortletURLBuilder.createActionURL(
					_renderResponse
				).setActionName(
					"/fragment/upload_fragment_entry_preview"
				).buildString()
			).build();

		itemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new FileEntryItemSelectorReturnType());

		return PortletURLBuilder.create(
			_itemSelector.getItemSelectorURL(
				RequestBackedPortletURLFactoryUtil.create(_httpServletRequest),
				_renderResponse.getNamespace() + "changePreview",
				itemSelectorCriterion)
		).setParameter(
			"fragmentEntryId", _fragmentEntry.getFragmentEntryId()
		).buildString();
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getMarkAsCacheableActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.putData("action", "markAsCacheableFragmentEntry");
			dropdownItem.putData(
				"markAsCacheableFragmentEntryURL",
				PortletURLBuilder.createActionURL(
					_renderResponse
				).setActionName(
					"/fragment/mark_as_cacheable_fragment_entry"
				).setRedirect(
					_themeDisplay.getURLCurrent()
				).setParameter(
					"fragmentEntryId", _fragmentEntry.getFragmentEntryId()
				).buildString());
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "mark-as-cacheable"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
			_getMoveFragmentEntryActionUnsafeConsumer()
		throws Exception {

		return dropdownItem -> {
			dropdownItem.putData("action", "moveFragmentEntry");
			dropdownItem.putData(
				"fragmentEntryId",
				String.valueOf(_fragmentEntry.getFragmentEntryId()));
			dropdownItem.putData(
				"moveFragmentEntryURL",
				PortletURLBuilder.createActionURL(
					_renderResponse
				).setActionName(
					"/fragment/move_fragment_compositions_and_fragment_entries"
				).setRedirect(
					_themeDisplay.getURLCurrent()
				).buildString());

			RequestBackedPortletURLFactory requestBackedPortletURLFactory =
				RequestBackedPortletURLFactoryUtil.create(_httpServletRequest);

			FragmentCollectionItemSelectorCriterion
				fragmentCollectionItemSelectorCriterion =
					new FragmentCollectionItemSelectorCriterion();

			fragmentCollectionItemSelectorCriterion.
				setDesiredItemSelectorReturnTypes(
					new UUIDItemSelectorReturnType());

			dropdownItem.putData(
				"selectFragmentCollectionURL",
				String.valueOf(
					_itemSelector.getItemSelectorURL(
						requestBackedPortletURLFactory,
						_renderResponse.getNamespace() +
							"selectFragmentCollection",
						fragmentCollectionItemSelectorCriterion)));

			dropdownItem.setIcon("move-folder");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "move"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getRenameFragmentEntryActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.putData("action", "renameFragmentEntry");
			dropdownItem.putData(
				"fragmentEntryId",
				String.valueOf(_fragmentEntry.getFragmentEntryId()));
			dropdownItem.putData("fragmentEntryName", _fragmentEntry.getName());
			dropdownItem.putData(
				"updateFragmentEntryURL",
				PortletURLBuilder.createActionURL(
					_renderResponse
				).setActionName(
					"/fragment/update_fragment_entry"
				).setParameter(
					"fragmentCollectionId",
					_fragmentEntry.getFragmentCollectionId()
				).setParameter(
					"fragmentEntryId", _fragmentEntry.getFragmentEntryId()
				).buildString());
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "rename"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getUnmarkAsCacheableActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.putData("action", "unmarkAsCacheableFragmentEntry");
			dropdownItem.putData(
				"unmarkAsCacheableFragmentEntryURL",
				PortletURLBuilder.createActionURL(
					_renderResponse
				).setActionName(
					"/fragment/unmark_as_cacheable_fragment_entry"
				).setRedirect(
					_themeDisplay.getURLCurrent()
				).setParameter(
					"fragmentEntryId", _fragmentEntry.getFragmentEntryId()
				).buildString());
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "unmark-as-cacheable"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getUpdateFragmentEntryPreviewActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.putData("action", "updateFragmentEntryPreview");
			dropdownItem.putData(
				"fragmentEntryId",
				String.valueOf(_fragmentEntry.getFragmentEntryId()));
			dropdownItem.putData("itemSelectorURL", _getItemSelectorURL());
			dropdownItem.setIcon("change");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "change-thumbnail"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getViewFragmentEntryUsagesActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.setDisabled(_fragmentEntry.getUsageCount() == 0);
			dropdownItem.setHref(
				_renderResponse.createRenderURL(), "mvcRenderCommandName",
				"/fragment/view_fragment_entry_usages", "redirect",
				_themeDisplay.getURLCurrent(), "fragmentCollectionId",
				_fragmentEntry.getFragmentCollectionId(), "fragmentEntryId",
				_fragmentEntry.getFragmentEntryId());
			dropdownItem.setIcon("list-ul");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "view-usages"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getViewGroupFragmentEntryUsagesActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.setDisabled(_fragmentEntry.getGlobalUsageCount() == 0);
			dropdownItem.setHref(
				_renderResponse.createRenderURL(), "mvcRenderCommandName",
				"/fragment/view_group_fragment_entry_usages", "redirect",
				_themeDisplay.getURLCurrent(), "fragmentCollectionId",
				_fragmentEntry.getFragmentCollectionId(), "fragmentEntryId",
				_fragmentEntry.getFragmentEntryId());
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "view-site-usages"));
		};
	}

	private final FragmentEntry _fragmentEntry;
	private final FragmentPortletConfiguration _fragmentPortletConfiguration;
	private final HttpServletRequest _httpServletRequest;
	private final ItemSelector _itemSelector;
	private final RenderResponse _renderResponse;
	private final ThemeDisplay _themeDisplay;

}