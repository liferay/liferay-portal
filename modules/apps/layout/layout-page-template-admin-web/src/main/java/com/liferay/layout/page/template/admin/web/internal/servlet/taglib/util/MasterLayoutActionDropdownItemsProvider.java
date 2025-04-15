/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.servlet.taglib.util;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownContextItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.ItemSelectorCriterion;
import com.liferay.item.selector.criteria.FileEntryItemSelectorReturnType;
import com.liferay.item.selector.criteria.upload.criterion.UploadItemSelectorCriterion;
import com.liferay.layout.admin.constants.LayoutAdminPortletKeys;
import com.liferay.layout.page.template.admin.constants.LayoutPageTemplateAdminPortletKeys;
import com.liferay.layout.page.template.admin.web.internal.constants.LayoutPageTemplateAdminWebKeys;
import com.liferay.layout.page.template.admin.web.internal.security.permission.resource.LayoutPageTemplateEntryPermission;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryServiceUtil;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.portlet.url.builder.ResourceURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.configuration.UploadServletRequestConfigurationProviderUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.taglib.security.PermissionsURLTag;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class MasterLayoutActionDropdownItemsProvider {

	public MasterLayoutActionDropdownItemsProvider(
		LayoutPageTemplateEntry layoutPageTemplateEntry,
		RenderRequest renderRequest, RenderResponse renderResponse) {

		_layoutPageTemplateEntry = layoutPageTemplateEntry;
		_renderResponse = renderResponse;

		_draftLayout = LayoutLocalServiceUtil.fetchDraftLayout(
			layoutPageTemplateEntry.getPlid());
		_httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
		_itemSelector = (ItemSelector)renderRequest.getAttribute(
			LayoutPageTemplateAdminWebKeys.ITEM_SELECTOR);
		_layout = LayoutLocalServiceUtil.fetchLayout(
			layoutPageTemplateEntry.getPlid());
		_themeDisplay = (ThemeDisplay)_httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public List<DropdownItem> getActionDropdownItems() throws Exception {
		LayoutPageTemplateEntry defaultLayoutPageTemplateEntry =
			LayoutPageTemplateEntryServiceUtil.
				fetchDefaultLayoutPageTemplateEntry(
					_themeDisplay.getScopeGroupId(),
					LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT,
					WorkflowConstants.STATUS_APPROVED);
		boolean hasUpdatePermission =
			LayoutPageTemplateEntryPermission.contains(
				_themeDisplay.getPermissionChecker(), _layoutPageTemplateEntry,
				ActionKeys.UPDATE);
		long layoutPageTemplateEntryId =
			_layoutPageTemplateEntry.getLayoutPageTemplateEntryId();

		if ((layoutPageTemplateEntryId <= 0) &&
			(defaultLayoutPageTemplateEntry == null)) {

			return Collections.emptyList();
		}

		return DropdownItemListBuilder.addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() ->
							(layoutPageTemplateEntryId > 0) &&
							hasUpdatePermission,
						_getEditMasterLayoutActionUnsafeConsumer()
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() ->
							(layoutPageTemplateEntryId > 0) &&
							hasUpdatePermission,
						_getUpdateMasterLayoutPreviewActionUnsafeConsumer()
					).add(
						() ->
							(layoutPageTemplateEntryId > 0) &&
							hasUpdatePermission &&
							(_layoutPageTemplateEntry.getPreviewFileEntryId() >
								0),
						_getDeleteMasterLayoutPreviewActionUnsafeConsumer()
					).add(
						() ->
							(layoutPageTemplateEntryId > 0) &&
							hasUpdatePermission && _isShowDiscardDraftAction(),
						_getDiscardDraftActionUnsafeConsumer()
					).add(
						() ->
							(layoutPageTemplateEntryId > 0) &&
							!_layoutPageTemplateEntry.isDefaultTemplate() &&
							hasUpdatePermission,
						_getMarkAsDefaultMasterLayoutActionUnsafeConsumer()
					).add(
						() ->
							(layoutPageTemplateEntryId <= 0) &&
							(defaultLayoutPageTemplateEntry != null),
						_getMarkAsDefaulBlanktMasterLayoutActionUnsafeConsumer(
							defaultLayoutPageTemplateEntry)
					).add(
						() ->
							(layoutPageTemplateEntryId > 0) &&
							hasUpdatePermission,
						_getRenameMasterLayoutActionUnsafeConsumer()
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() ->
							(layoutPageTemplateEntryId > 0) &&
							(_layoutPageTemplateEntry.getLayoutPrototypeId() ==
								0),
						_getExportMasterLayoutActionUnsafeConsumer()
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.addContext(
						() ->
							(layoutPageTemplateEntryId > 0) &&
							hasUpdatePermission,
						_getCopyMasterLayoutWithPermissionsActionUnsafeConsumer()
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() ->
							(layoutPageTemplateEntryId > 0) &&
							LayoutPageTemplateEntryPermission.contains(
								_themeDisplay.getPermissionChecker(),
								_layoutPageTemplateEntry,
								ActionKeys.PERMISSIONS),
						_getPermissionsMasterLayoutActionUnsafeConsumer()
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() ->
							(layoutPageTemplateEntryId > 0) &&
							LayoutPageTemplateEntryPermission.contains(
								_themeDisplay.getPermissionChecker(),
								_layoutPageTemplateEntry, ActionKeys.DELETE),
						_getDeleteMasterLayoutActionUnsafeConsumer()
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).build();
	}

	private UnsafeConsumer<DropdownContextItem, Exception>
		_getCopyMasterLayoutWithPermissionsActionUnsafeConsumer() {

		return dropdownContextItem -> {
			if (_layoutPageTemplateEntry.isDraft()) {
				dropdownContextItem.setDisabled(true);
			}
			else {
				dropdownContextItem.setDropdownItems(
					DropdownItemListBuilder.add(
						dropdownItem -> {
							dropdownItem.putData("action", "copyMasterLayout");
							dropdownItem.putData(
								"copyMasterLayoutURL", _getCopyURL(false));
							dropdownItem.setLabel(
								LanguageUtil.get(
									_httpServletRequest, "master-page"));
						}
					).add(
						dropdownItem -> {
							dropdownItem.putData("action", "copyMasterLayout");
							dropdownItem.putData(
								"copyMasterLayoutURL", _getCopyURL(true));
							dropdownItem.setLabel(
								LanguageUtil.get(
									_httpServletRequest,
									"master-page-with-permissions"));
						}
					).build());
			}

			dropdownContextItem.setIcon("copy");
			dropdownContextItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "make-a-copy"));
		};
	}

	private String _getCopyURL(boolean copyPermissions) {
		return PortletURLBuilder.createActionURL(
			_renderResponse
		).setActionName(
			"/layout_page_template_admin/copy_layout_page_template_entries_" +
				"and_layout_page_template_collections"
		).setRedirect(
			_themeDisplay.getURLCurrent()
		).setParameter(
			"copyPermissions", copyPermissions
		).setParameter(
			"layoutPageTemplateEntriesIds",
			_layoutPageTemplateEntry.getLayoutPageTemplateEntryId()
		).setParameter(
			"layoutParentPageTemplateCollectionId",
			_layoutPageTemplateEntry.getLayoutPageTemplateCollectionId()
		).buildString();
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getDeleteMasterLayoutActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.putData("action", "deleteMasterLayout");
			dropdownItem.putData(
				"deleteMasterLayoutURL",
				PortletURLBuilder.createActionURL(
					_renderResponse
				).setActionName(
					"/layout_page_template_admin/delete_master_layout"
				).setRedirect(
					_themeDisplay.getURLCurrent()
				).setParameter(
					"layoutPageTemplateEntryId",
					_layoutPageTemplateEntry.getLayoutPageTemplateEntryId()
				).buildString());
			dropdownItem.setIcon("trash");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "delete"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getDeleteMasterLayoutPreviewActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.putData("action", "deleteMasterLayoutPreview");
			dropdownItem.putData(
				"deleteMasterLayoutPreviewURL",
				PortletURLBuilder.createActionURL(
					_renderResponse
				).setActionName(
					"/layout_page_template_admin" +
						"/delete_layout_page_template_entry_preview"
				).setRedirect(
					_themeDisplay.getURLCurrent()
				).setParameter(
					"layoutPageTemplateEntryId",
					_layoutPageTemplateEntry.getLayoutPageTemplateEntryId()
				).buildString());
			dropdownItem.putData(
				"layoutPageTemplateEntryId",
				String.valueOf(
					_layoutPageTemplateEntry.getLayoutPageTemplateEntryId()));
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "remove-thumbnail"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getDiscardDraftActionUnsafeConsumer() {

		if (_draftLayout == null) {
			return null;
		}

		return dropdownItem -> {
			dropdownItem.putData("action", "discardDraft");
			dropdownItem.putData(
				"discardDraftURL",
				PortletURLBuilder.create(
					PortletURLFactoryUtil.create(
						_httpServletRequest, LayoutAdminPortletKeys.GROUP_PAGES,
						PortletRequest.ACTION_PHASE)
				).setActionName(
					"/layout_admin/discard_draft_layout"
				).setRedirect(
					_themeDisplay.getURLCurrent()
				).setParameter(
					"selPlid", _draftLayout.getPlid()
				).buildString());
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "discard-draft"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getEditMasterLayoutActionUnsafeConsumer() {

		if (_draftLayout == null) {
			return null;
		}

		PortletDisplay portletDisplay = _themeDisplay.getPortletDisplay();

		return dropdownItem -> {
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
		_getExportMasterLayoutActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.setDisabled(!_layout.isPublished());
			dropdownItem.setHref(
				ResourceURLBuilder.createResourceURL(
					_renderResponse
				).setParameter(
					"layoutPageTemplateEntryId",
					_layoutPageTemplateEntry.getLayoutPageTemplateEntryId()
				).setResourceID(
					"/layout_page_template_admin/export_master_layouts"
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
			).maxFileSize(
				UploadServletRequestConfigurationProviderUtil.getMaxSize()
			).portletId(
				LayoutPageTemplateAdminPortletKeys.LAYOUT_PAGE_TEMPLATES
			).repositoryName(
				LanguageUtil.get(_themeDisplay.getLocale(), "master-page")
			).url(
				PortletURLBuilder.createActionURL(
					_renderResponse
				).setActionName(
					"/layout_page_template_admin" +
						"/upload_layout_page_template_entry_preview"
				).setParameter(
					"layoutPageTemplateEntryId",
					_layoutPageTemplateEntry.getLayoutPageTemplateEntryId()
				).buildString()
			).build();

		return String.valueOf(
			_itemSelector.getItemSelectorURL(
				RequestBackedPortletURLFactoryUtil.create(_httpServletRequest),
				_renderResponse.getNamespace() + "changePreview",
				itemSelectorCriterion));
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getMarkAsDefaulBlanktMasterLayoutActionUnsafeConsumer(
			LayoutPageTemplateEntry defaultLayoutPageTemplateEntry) {

		if (defaultLayoutPageTemplateEntry == null) {
			return null;
		}

		return dropdownItem -> {
			dropdownItem.putData("action", "markAsDefaultMasterLayout");
			dropdownItem.putData(
				"markAsDefaultMasterLayoutURL",
				PortletURLBuilder.createActionURL(
					_renderResponse
				).setActionName(
					"/layout_page_template_admin" +
						"/edit_layout_page_template_entry_settings"
				).setRedirect(
					_themeDisplay.getURLCurrent()
				).setParameter(
					"defaultTemplate", false
				).setParameter(
					"layoutPageTemplateEntryId",
					defaultLayoutPageTemplateEntry.
						getLayoutPageTemplateEntryId()
				).buildString());
			dropdownItem.putData(
				"message",
				LanguageUtil.format(
					_httpServletRequest,
					"do-you-want-to-replace-x-for-x-as-the-default-master-" +
						"page-for-widget-pages",
					new String[] {
						defaultLayoutPageTemplateEntry.getName(),
						_layoutPageTemplateEntry.getName()
					}));
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "mark-as-default"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getMarkAsDefaultMasterLayoutActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.putData("action", "markAsDefaultMasterLayout");
			dropdownItem.putData(
				"markAsDefaultMasterLayoutURL",
				PortletURLBuilder.createActionURL(
					_renderResponse
				).setActionName(
					"/layout_page_template_admin" +
						"/edit_layout_page_template_entry_settings"
				).setRedirect(
					_themeDisplay.getURLCurrent()
				).setParameter(
					"defaultTemplate", true
				).setParameter(
					"layoutPageTemplateEntryId",
					_layoutPageTemplateEntry.getLayoutPageTemplateEntryId()
				).buildString());
			dropdownItem.setDisabled(!_layout.isPublished());

			String name = "Blank";

			LayoutPageTemplateEntry defaultLayoutPageTemplateEntry =
				LayoutPageTemplateEntryServiceUtil.
					fetchDefaultLayoutPageTemplateEntry(
						_layoutPageTemplateEntry.getGroupId(),
						LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT,
						WorkflowConstants.STATUS_APPROVED);

			if (defaultLayoutPageTemplateEntry != null) {
				name = defaultLayoutPageTemplateEntry.getName();
			}

			dropdownItem.putData(
				"message",
				LanguageUtil.format(
					_httpServletRequest,
					"do-you-want-to-replace-x-for-x-as-the-default-master-" +
						"page-for-widget-pages",
					new String[] {name, _layoutPageTemplateEntry.getName()}));

			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "mark-as-default"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
			_getPermissionsMasterLayoutActionUnsafeConsumer()
		throws Exception {

		String permissionsMasterLayoutURL = PermissionsURLTag.doTag(
			StringPool.BLANK, LayoutPageTemplateEntry.class.getName(),
			_layoutPageTemplateEntry.getName(), null,
			String.valueOf(
				_layoutPageTemplateEntry.getLayoutPageTemplateEntryId()),
			LiferayWindowState.POP_UP.toString(), null, _httpServletRequest);

		return dropdownItem -> {
			dropdownItem.putData("action", "permissionsMasterLayout");
			dropdownItem.putData(
				"permissionsMasterLayoutURL", permissionsMasterLayoutURL);
			dropdownItem.setIcon("password-policies");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "permissions"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getRenameMasterLayoutActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.putData("action", "renameMasterLayout");
			dropdownItem.putData(
				"layoutPageTemplateEntryId",
				String.valueOf(
					_layoutPageTemplateEntry.getLayoutPageTemplateEntryId()));
			dropdownItem.putData(
				"layoutPageTemplateEntryName",
				_layoutPageTemplateEntry.getName());
			dropdownItem.putData(
				"updateMasterLayoutURL",
				PortletURLBuilder.createActionURL(
					_renderResponse
				).setActionName(
					"/layout_page_template_admin" +
						"/update_layout_page_template_entry"
				).setRedirect(
					_themeDisplay.getURLCurrent()
				).setParameter(
					"layoutPageTemplateCollectionId",
					_layoutPageTemplateEntry.getLayoutPageTemplateCollectionId()
				).setParameter(
					"layoutPageTemplateEntryId",
					_layoutPageTemplateEntry.getLayoutPageTemplateEntryId()
				).buildString());
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "rename"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getUpdateMasterLayoutPreviewActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.putData("action", "updateMasterLayoutPreview");
			dropdownItem.putData("itemSelectorURL", _getItemSelectorURL());
			dropdownItem.putData(
				"layoutPageTemplateEntryId",
				String.valueOf(
					_layoutPageTemplateEntry.getLayoutPageTemplateEntryId()));
			dropdownItem.setIcon("change");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "change-thumbnail"));
		};
	}

	private boolean _isShowDiscardDraftAction() {
		if (_draftLayout == null) {
			return false;
		}

		return _draftLayout.isDraft();
	}

	private final Layout _draftLayout;
	private final HttpServletRequest _httpServletRequest;
	private final ItemSelector _itemSelector;
	private final Layout _layout;
	private final LayoutPageTemplateEntry _layoutPageTemplateEntry;
	private final RenderResponse _renderResponse;
	private final ThemeDisplay _themeDisplay;

}