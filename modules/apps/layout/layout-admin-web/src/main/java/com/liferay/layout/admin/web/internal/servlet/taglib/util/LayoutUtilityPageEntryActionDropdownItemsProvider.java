/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.servlet.taglib.util;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.ItemSelectorCriterion;
import com.liferay.item.selector.criteria.FileEntryItemSelectorReturnType;
import com.liferay.item.selector.criteria.upload.criterion.UploadItemSelectorCriterion;
import com.liferay.layout.admin.constants.LayoutAdminPortletKeys;
import com.liferay.layout.admin.web.internal.configuration.LayoutUtilityPageThumbnailConfiguration;
import com.liferay.layout.admin.web.internal.security.permission.resource.LayoutUtilityPageEntryPermission;
import com.liferay.layout.utility.page.constants.LayoutUtilityPageActionKeys;
import com.liferay.layout.utility.page.kernel.LayoutUtilityPageEntryViewRenderer;
import com.liferay.layout.utility.page.kernel.LayoutUtilityPageEntryViewRendererRegistryUtil;
import com.liferay.layout.utility.page.model.LayoutUtilityPageEntry;
import com.liferay.layout.utility.page.service.LayoutUtilityPageEntryLocalServiceUtil;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.portlet.url.builder.ResourceURLBuilder;
import com.liferay.portal.kernel.security.auth.AuthTokenUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.configuration.UploadServletRequestConfigurationProviderUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.staging.StagingGroupHelper;
import com.liferay.staging.StagingGroupHelperUtil;
import com.liferay.taglib.security.PermissionsURLTag;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Bárbara Cabrera
 */
public class LayoutUtilityPageEntryActionDropdownItemsProvider {

	public LayoutUtilityPageEntryActionDropdownItemsProvider(
		LayoutUtilityPageEntry layoutUtilityPageEntry,
		RenderRequest renderRequest, RenderResponse renderResponse) {

		_layoutUtilityPageEntry = layoutUtilityPageEntry;
		_renderResponse = renderResponse;

		_draftLayout = LayoutLocalServiceUtil.fetchDraftLayout(
			layoutUtilityPageEntry.getPlid());
		_httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
		_itemSelector = (ItemSelector)renderRequest.getAttribute(
			ItemSelector.class.getName());
		_layout = LayoutLocalServiceUtil.fetchLayout(
			layoutUtilityPageEntry.getPlid());
		_layoutUtilityPageThumbnailConfiguration =
			(LayoutUtilityPageThumbnailConfiguration)renderRequest.getAttribute(
				LayoutUtilityPageThumbnailConfiguration.class.getName());
		_themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public List<DropdownItem> getActionDropdownItems() {
		return DropdownItemListBuilder.addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() -> _hasUpdatePermission(),
						_getEditLayoutUtilityPageEntryActionUnsafeConsumer()
					).add(
						() -> LayoutUtilityPageEntryPermission.contains(
							_themeDisplay.getPermissionChecker(),
							_layoutUtilityPageEntry, ActionKeys.VIEW),
						_getViewLayoutUtilityPageEntryActionUnsafeConsumer()
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() ->
							_hasAssignDefaultLayoutUtilityPagePermission() &&
							_hasUpdatePermission(),
						_getMarkAsDefaultLayoutUtilityPageEntryActionUnsafeConsumer()
					).add(
						() -> _hasUpdatePermission(),
						_getRenameLayoutUtilityPageEntryActionUnsafeConsumer()
					).add(
						() -> _hasUpdatePermission(),
						_getUpdateLayoutUtilityPageEntryPreviewActionUnsafeConsumer()
					).add(
						() ->
							_hasUpdatePermission() &&
							(_layoutUtilityPageEntry.getPreviewFileEntryId() >
								0),
						_getDeleteLayoutUtilityPageEntryPreviewActionUnsafeConsumer()
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						_getExportLayoutUtilityPageEntryActionUnsafeConsumer()
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() ->
							!_isLiveGroup() &&
							GroupPermissionUtil.contains(
								_themeDisplay.getPermissionChecker(),
								_themeDisplay.getScopeGroup(),
								LayoutUtilityPageActionKeys.
									ADD_LAYOUT_UTILITY_PAGE_ENTRY),
						_getCopyLayoutUtilityPageEntryActionUnsafeConsumer()
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() -> _hasUpdatePermission(),
						_getConfigureLayoutUtilityPageEntryActionUnsafeConsumer()
					).add(
						() -> LayoutUtilityPageEntryPermission.contains(
							_themeDisplay.getPermissionChecker(),
							_layoutUtilityPageEntry, ActionKeys.PERMISSIONS),
						_getPermissionsLayoutUtilityPageEntryActionUnsafeConsumer()
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() -> _hasDeletePermission(),
						_getDeleteLayoutUtilityPageEntryActionUnsafeConsumer()
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).build();
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getConfigureLayoutUtilityPageEntryActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.setHref(
				PortletURLBuilder.createRenderURL(
					_renderResponse
				).setMVCRenderCommandName(
					"/layout_admin/edit_layout"
				).setRedirect(
					_themeDisplay.getURLCurrent()
				).setBackURL(
					_themeDisplay.getURLCurrent()
				).setParameter(
					"backURLTitle",
					LanguageUtil.get(_httpServletRequest, "utility-pages")
				).setParameter(
					"groupId", _layout.getGroupId()
				).setParameter(
					"privateLayout", _layout.isPrivateLayout()
				).setParameter(
					"selPlid", _layout.getPlid()
				).buildString());
			dropdownItem.setIcon("cog");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "configure"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getCopyLayoutUtilityPageEntryActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.putData("action", "copyLayoutUtilityPageEntry");
			dropdownItem.putData(
				"copyLayoutUtilityPageEntryURL",
				PortletURLBuilder.createActionURL(
					_renderResponse
				).setActionName(
					"/layout_admin/copy_layout_utility_page_entry"
				).setRedirect(
					_themeDisplay.getURLCurrent()
				).setParameter(
					"layoutUtilityPageEntryId",
					_layoutUtilityPageEntry.getLayoutUtilityPageEntryId()
				).buildString());
			dropdownItem.setIcon("copy");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "make-a-copy"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getDeleteLayoutUtilityPageEntryActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.putData("action", "deleteLayoutUtilityPageEntry");

			String key = "are-you-sure-you-want-to-delete-this";

			if (_layoutUtilityPageEntry.isDefaultLayoutUtilityPageEntry()) {
				key =
					"are-you-sure-you-want-to-delete-the-default-utility-page";
			}

			dropdownItem.putData(
				"deleteLayoutUtilityPageEntryMessage",
				LanguageUtil.get(_httpServletRequest, key));
			dropdownItem.putData(
				"deleteLayoutUtilityPageEntryURL",
				PortletURLBuilder.createActionURL(
					_renderResponse
				).setActionName(
					"/layout_admin/delete_layout_utility_page_entry"
				).setRedirect(
					_themeDisplay.getURLCurrent()
				).setParameter(
					"layoutUtilityPageEntryId",
					_layoutUtilityPageEntry.getLayoutUtilityPageEntryId()
				).buildString());
			dropdownItem.setIcon("trash");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "delete"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getDeleteLayoutUtilityPageEntryPreviewActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.putData(
				"action", "deleteLayoutUtilityPageEntryPreview");
			dropdownItem.putData(
				"deleteLayoutUtilityPageEntryPreviewURL",
				PortletURLBuilder.createActionURL(
					_renderResponse
				).setActionName(
					"/layout_admin/delete_layout_utility_page_entry_preview"
				).setRedirect(
					_themeDisplay.getURLCurrent()
				).setParameter(
					"layoutUtilityPageEntryId",
					_layoutUtilityPageEntry.getLayoutUtilityPageEntryId()
				).buildString());
			dropdownItem.putData(
				"layoutUtilityPageEntryId",
				String.valueOf(
					_layoutUtilityPageEntry.getLayoutUtilityPageEntryId()));
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "remove-thumbnail"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getEditLayoutUtilityPageEntryActionUnsafeConsumer() {

		return dropdownItem -> {
			PortletDisplay portletDisplay = _themeDisplay.getPortletDisplay();

			dropdownItem.setHref(
				HttpComponentsUtil.addParameters(
					PortalUtil.getLayoutFullURL(_draftLayout, _themeDisplay),
					"p_l_back_url", _themeDisplay.getURLCurrent(),
					"p_l_back_url_title",
					portletDisplay.getPortletDisplayName(), "p_l_mode",
					Constants.EDIT));

			dropdownItem.setIcon("pencil");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "edit"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getExportLayoutUtilityPageEntryActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.setDisabled(!_layout.isPublished());
			dropdownItem.setHref(
				ResourceURLBuilder.createResourceURL(
					_renderResponse
				).setParameter(
					"layoutUtilityPageEntryId",
					_layoutUtilityPageEntry.getLayoutUtilityPageEntryId()
				).setResourceID(
					"/layout_admin/export_layout_utility_page_entries"
				).buildString());
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
				_layoutUtilityPageThumbnailConfiguration.thumbnailExtensions()
			).maxFileSize(
				UploadServletRequestConfigurationProviderUtil.getMaxSize()
			).portletId(
				LayoutAdminPortletKeys.GROUP_PAGES
			).repositoryName(
				LanguageUtil.get(_themeDisplay.getLocale(), "utility-pages")
			).url(
				PortletURLBuilder.createActionURL(
					_renderResponse
				).setActionName(
					"/layout_admin/upload_layout_utility_page_entry_preview"
				).setParameter(
					"layoutUtilityPageEntryId",
					_layoutUtilityPageEntry.getLayoutUtilityPageEntryId()
				).buildString()
			).build();

		return String.valueOf(
			_itemSelector.getItemSelectorURL(
				RequestBackedPortletURLFactoryUtil.create(_httpServletRequest),
				_renderResponse.getNamespace() + "changePreview",
				itemSelectorCriterion));
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getMarkAsDefaultLayoutUtilityPageEntryActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.putData(
				"action", "markAsDefaultLayoutUtilityPageEntry");
			dropdownItem.putData(
				"markAsDefaultLayoutUtilityPageEntryURL",
				PortletURLBuilder.createActionURL(
					_renderResponse
				).setActionName(
					"/layout_admin/update_default_layout_utility_page_entry"
				).setRedirect(
					_themeDisplay.getURLCurrent()
				).setParameter(
					"isDefaultLayoutUtilityPageEntry",
					!_layoutUtilityPageEntry.isDefaultLayoutUtilityPageEntry()
				).setParameter(
					"layoutUtilityPageEntryId",
					_layoutUtilityPageEntry.getLayoutUtilityPageEntryId()
				).buildString());
			dropdownItem.setDisabled(!_layout.isPublished());

			String message = StringPool.BLANK;

			LayoutUtilityPageEntry defaultLayoutUtilityPageEntry =
				LayoutUtilityPageEntryLocalServiceUtil.
					fetchDefaultLayoutUtilityPageEntry(
						_layoutUtilityPageEntry.getGroupId(),
						_layoutUtilityPageEntry.getType());

			if (defaultLayoutUtilityPageEntry != null) {
				long defaultLayoutUtilityPageEntryId =
					defaultLayoutUtilityPageEntry.getLayoutUtilityPageEntryId();
				long layoutUtilityPageEntryId =
					_layoutUtilityPageEntry.getLayoutUtilityPageEntryId();

				if (defaultLayoutUtilityPageEntryId !=
						layoutUtilityPageEntryId) {

					message = LanguageUtil.format(
						_httpServletRequest,
						"do-you-want-to-replace-x-for-x-as-the-default-" +
							"utility-page",
						new String[] {
							_layoutUtilityPageEntry.getName(),
							defaultLayoutUtilityPageEntry.getName()
						});
				}
			}

			if (Validator.isNull(message) &&
				_layoutUtilityPageEntry.isDefaultLayoutUtilityPageEntry()) {

				LayoutUtilityPageEntryViewRenderer
					layoutUtilityPageEntryViewRenderer =
						LayoutUtilityPageEntryViewRendererRegistryUtil.
							getLayoutUtilityPageEntryViewRenderer(
								_layoutUtilityPageEntry.getType());

				message = LanguageUtil.format(
					_httpServletRequest,
					"the-site-will-use-the-default-x-system-page-from-now-" +
						"on.-are-you-sure-you-want-to-unmark-this",
					new String[] {
						layoutUtilityPageEntryViewRenderer.getLabel(
							_themeDisplay.getLocale())
					},
					false);
			}

			dropdownItem.putData("message", message);

			String label = "mark-as-default";

			if (_layoutUtilityPageEntry.isDefaultLayoutUtilityPageEntry()) {
				label = "unmark-as-default";
			}

			dropdownItem.setLabel(LanguageUtil.get(_httpServletRequest, label));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
			_getPermissionsLayoutUtilityPageEntryActionUnsafeConsumer()
		throws Exception {

		String permissionsLayoutUtilityPageEntryURL = PermissionsURLTag.doTag(
			StringPool.BLANK, LayoutUtilityPageEntry.class.getName(),
			_layoutUtilityPageEntry.getName(), null,
			String.valueOf(
				_layoutUtilityPageEntry.getLayoutUtilityPageEntryId()),
			LiferayWindowState.POP_UP.toString(), null, _httpServletRequest);

		return dropdownItem -> {
			dropdownItem.putData("action", "permissionsLayoutUtilityPageEntry");
			dropdownItem.putData(
				"permissionsLayoutUtilityPageEntryURL",
				permissionsLayoutUtilityPageEntryURL);
			dropdownItem.setIcon("password-policies");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "permissions"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getRenameLayoutUtilityPageEntryActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.putData("action", "renameLayoutUtilityPageEntry");
			dropdownItem.putData(
				"layoutUtilityPageEntryId",
				String.valueOf(
					_layoutUtilityPageEntry.getLayoutUtilityPageEntryId()));
			dropdownItem.putData(
				"layoutUtilityPageEntryName",
				_layoutUtilityPageEntry.getName());
			dropdownItem.putData(
				"updateLayoutUtilityPageEntryURL",
				PortletURLBuilder.createActionURL(
					_renderResponse
				).setActionName(
					"/layout_admin/update_layout_utility_page_entry"
				).setRedirect(
					_themeDisplay.getURLCurrent()
				).setParameter(
					"layoutUtilityPageEntryId",
					_layoutUtilityPageEntry.getLayoutUtilityPageEntryId()
				).buildString());
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "rename"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getUpdateLayoutUtilityPageEntryPreviewActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.putData(
				"action", "updateLayoutUtilityPageEntryPreview");
			dropdownItem.putData("itemSelectorURL", _getItemSelectorURL());
			dropdownItem.putData(
				"layoutUtilityPageEntryId",
				String.valueOf(
					_layoutUtilityPageEntry.getLayoutUtilityPageEntryId()));
			dropdownItem.setIcon("change");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "change-thumbnail"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getViewLayoutUtilityPageEntryActionUnsafeConsumer() {

		return dropdownItem -> {
			Layout previewLayout = _draftLayout;

			if (_isLiveGroup()) {
				previewLayout = _layout;
			}

			String layoutFullURL = PortalUtil.getLayoutFullURL(
				previewLayout, _themeDisplay);

			layoutFullURL = HttpComponentsUtil.setParameter(
				layoutFullURL, "p_l_back_url", _themeDisplay.getURLCurrent());
			layoutFullURL = HttpComponentsUtil.setParameter(
				layoutFullURL, "p_l_mode", Constants.PREVIEW);
			layoutFullURL = HttpComponentsUtil.addParameter(
				layoutFullURL, "p_p_auth",
				AuthTokenUtil.getToken(_httpServletRequest));

			dropdownItem.setHref(layoutFullURL);

			dropdownItem.setIcon("shortcut");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "preview"));
			dropdownItem.setTarget("_blank");
		};
	}

	private boolean _hasAssignDefaultLayoutUtilityPagePermission() {
		if (_assignDefaultLayoutUtilityPagePermission != null) {
			return _assignDefaultLayoutUtilityPagePermission;
		}

		try {
			_assignDefaultLayoutUtilityPagePermission =
				GroupPermissionUtil.contains(
					_themeDisplay.getPermissionChecker(),
					_layoutUtilityPageEntry.getGroupId(),
					LayoutUtilityPageActionKeys.
						ASSIGN_DEFAULT_LAYOUT_UTILITY_PAGE_ENTRY);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return false;
		}

		return _assignDefaultLayoutUtilityPagePermission;
	}

	private boolean _hasDeletePermission() {
		if (_deletePermission != null) {
			return _deletePermission;
		}

		Boolean deletePermission = null;

		try {
			deletePermission = LayoutUtilityPageEntryPermission.contains(
				_themeDisplay.getPermissionChecker(), _layoutUtilityPageEntry,
				ActionKeys.DELETE);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			deletePermission = false;
		}

		if (!deletePermission) {
			_deletePermission = false;

			return false;
		}

		if (!_layoutUtilityPageEntry.isDefaultLayoutUtilityPageEntry()) {
			_deletePermission = true;

			return true;
		}

		_deletePermission = _hasAssignDefaultLayoutUtilityPagePermission();

		return _deletePermission;
	}

	private boolean _hasUpdatePermission() {
		if (_updatePermission != null) {
			return _updatePermission;
		}

		try {
			_updatePermission = LayoutUtilityPageEntryPermission.contains(
				_themeDisplay.getPermissionChecker(), _layoutUtilityPageEntry,
				ActionKeys.UPDATE);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return false;
		}

		return _updatePermission;
	}

	private boolean _isLiveGroup() {
		Group group = _themeDisplay.getScopeGroup();

		StagingGroupHelper stagingGroupHelper =
			StagingGroupHelperUtil.getStagingGroupHelper();

		return stagingGroupHelper.isLiveGroup(group);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutUtilityPageEntryActionDropdownItemsProvider.class);

	private Boolean _assignDefaultLayoutUtilityPagePermission;
	private Boolean _deletePermission;
	private final Layout _draftLayout;
	private final HttpServletRequest _httpServletRequest;
	private final ItemSelector _itemSelector;
	private final Layout _layout;
	private final LayoutUtilityPageEntry _layoutUtilityPageEntry;
	private final LayoutUtilityPageThumbnailConfiguration
		_layoutUtilityPageThumbnailConfiguration;
	private final RenderResponse _renderResponse;
	private final ThemeDisplay _themeDisplay;
	private Boolean _updatePermission;

}