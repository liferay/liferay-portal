/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.web.internal.servlet.taglib.util;

import com.liferay.fragment.collection.item.selector.FragmentCollectionItemSelectorCriterion;
import com.liferay.fragment.constants.FragmentActionKeys;
import com.liferay.fragment.constants.FragmentPortletKeys;
import com.liferay.fragment.model.FragmentComposition;
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
 * @author Pavel Savinov
 */
public class BasicFragmentCompositionActionDropdownItemsProvider {

	public BasicFragmentCompositionActionDropdownItemsProvider(
		FragmentComposition fragmentComposition, RenderRequest renderRequest,
		RenderResponse renderResponse) {

		_fragmentComposition = fragmentComposition;
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
							!_fragmentComposition.isMarketplace(),
						_getUpdateFragmentCompositionPreviewActionUnsafeConsumer()
					).add(
						() ->
							hasManageFragmentEntriesPermission &&
							(_fragmentComposition.getPreviewFileEntryId() >
								0) &&
							!_fragmentComposition.isMarketplace(),
						_getDeleteFragmentCompositionPreviewActionUnsafeConsumer()
					).add(
						() ->
							hasManageFragmentEntriesPermission &&
							!_fragmentComposition.isMarketplace(),
						_getRenameFragmentCompositionActionUnsafeConsumer()
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() ->
							hasManageFragmentEntriesPermission &&
							!_fragmentComposition.isMarketplace(),
						_getExportFragmentCompositionActionUnsafeConsumer()
					).add(
						() -> hasManageFragmentEntriesPermission,
						_getMoveFragmentCompositionActionUnsafeConsumer()
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() -> hasManageFragmentEntriesPermission,
						_getDeleteFragmentCompositionActionUnsafeConsumer()
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).build();
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getDeleteFragmentCompositionActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.putData("action", "deleteFragmentComposition");
			dropdownItem.putData(
				"deleteFragmentCompositionURL",
				PortletURLBuilder.createActionURL(
					_renderResponse
				).setActionName(
					"/fragment/delete_fragment_compositions"
				).setRedirect(
					_themeDisplay.getURLCurrent()
				).setParameter(
					"fragmentCompositionId",
					_fragmentComposition.getFragmentCompositionId()
				).buildString());
			dropdownItem.setIcon("trash");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "delete"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getDeleteFragmentCompositionPreviewActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.putData("action", "deleteFragmentCompositionPreview");
			dropdownItem.putData(
				"deleteFragmentCompositionPreviewURL",
				PortletURLBuilder.createActionURL(
					_renderResponse
				).setActionName(
					"/fragment/delete_fragment_composition_preview"
				).setParameter(
					"fragmentCompositionId",
					_fragmentComposition.getFragmentCompositionId()
				).buildString());
			dropdownItem.putData(
				"fragmentCompositionId",
				String.valueOf(
					_fragmentComposition.getFragmentCompositionId()));
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "remove-thumbnail"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getExportFragmentCompositionActionUnsafeConsumer() {

		ResourceURL exportFragmentEntryURL =
			_renderResponse.createResourceURL();

		exportFragmentEntryURL.setParameter(
			"fragmentCompositionId",
			String.valueOf(_fragmentComposition.getFragmentCompositionId()));
		exportFragmentEntryURL.setResourceID(
			"/fragment/export_fragment_compositions_and_fragment_entries");

		return dropdownItem -> {
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
					"/fragment/upload_fragment_composition_preview"
				).buildString()
			).build();

		return PortletURLBuilder.create(
			_itemSelector.getItemSelectorURL(
				RequestBackedPortletURLFactoryUtil.create(_httpServletRequest),
				_renderResponse.getNamespace() + "changePreview",
				itemSelectorCriterion)
		).setParameter(
			"fragmentCompositionId",
			_fragmentComposition.getFragmentCompositionId()
		).buildString();
	}

	private UnsafeConsumer<DropdownItem, Exception>
			_getMoveFragmentCompositionActionUnsafeConsumer()
		throws Exception {

		return dropdownItem -> {
			dropdownItem.putData("action", "moveFragmentComposition");
			dropdownItem.putData(
				"fragmentCompositionId",
				String.valueOf(
					_fragmentComposition.getFragmentCompositionId()));
			dropdownItem.putData(
				"moveFragmentCompositionURL",
				PortletURLBuilder.createActionURL(
					_renderResponse
				).setActionName(
					"/fragment/move_fragment_composition"
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
		_getRenameFragmentCompositionActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.putData("action", "renameFragmentComposition");
			dropdownItem.putData(
				"fragmentCompositionId",
				String.valueOf(
					_fragmentComposition.getFragmentCompositionId()));
			dropdownItem.putData(
				"fragmentCompositionName", _fragmentComposition.getName());
			dropdownItem.putData(
				"renameFragmentCompositionURL",
				PortletURLBuilder.createActionURL(
					_renderResponse
				).setActionName(
					"/fragment/rename_fragment_composition"
				).setParameter(
					"fragmentCompositionId",
					_fragmentComposition.getFragmentCompositionId()
				).buildString());
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "rename"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getUpdateFragmentCompositionPreviewActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.putData("action", "updateFragmentCompositionPreview");
			dropdownItem.putData(
				"fragmentCompositionId",
				String.valueOf(
					_fragmentComposition.getFragmentCompositionId()));
			dropdownItem.putData("itemSelectorURL", _getItemSelectorURL());
			dropdownItem.setIcon("change");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "change-thumbnail"));
		};
	}

	private final FragmentComposition _fragmentComposition;
	private final FragmentPortletConfiguration _fragmentPortletConfiguration;
	private final HttpServletRequest _httpServletRequest;
	private final ItemSelector _itemSelector;
	private final RenderResponse _renderResponse;
	private final ThemeDisplay _themeDisplay;

}