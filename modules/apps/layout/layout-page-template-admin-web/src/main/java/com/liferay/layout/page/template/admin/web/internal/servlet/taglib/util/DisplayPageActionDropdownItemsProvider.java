/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.servlet.taglib.util;

import com.liferay.asset.display.page.service.AssetDisplayPageEntryServiceUtil;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownContextItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.info.item.InfoItemClassDetails;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemDetailsProvider;
import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.ItemSelectorCriterion;
import com.liferay.item.selector.criteria.FileEntryItemSelectorReturnType;
import com.liferay.item.selector.criteria.UUIDItemSelectorReturnType;
import com.liferay.item.selector.criteria.upload.criterion.UploadItemSelectorCriterion;
import com.liferay.layout.admin.constants.LayoutAdminPortletKeys;
import com.liferay.layout.page.template.admin.constants.LayoutPageTemplateAdminPortletKeys;
import com.liferay.layout.page.template.admin.web.internal.configuration.LayoutPageTemplateAdminWebConfiguration;
import com.liferay.layout.page.template.admin.web.internal.constants.LayoutPageTemplateAdminWebKeys;
import com.liferay.layout.page.template.admin.web.internal.security.permission.resource.LayoutPageTemplateEntryPermission;
import com.liferay.layout.page.template.item.selector.LayoutPageTemplateCollectionTreeNodeItemSelectorCriterion;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryServiceUtil;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.portlet.url.builder.RenderURLBuilder;
import com.liferay.portal.kernel.portlet.url.builder.ResourceURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.configuration.UploadServletRequestConfigurationProviderUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.security.PermissionsURLTag;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class DisplayPageActionDropdownItemsProvider {

	public DisplayPageActionDropdownItemsProvider(
		boolean allowedMappedContentType, boolean existsMappedContentType,
		LayoutPageTemplateEntry layoutPageTemplateEntry,
		RenderRequest renderRequest, RenderResponse renderResponse) {

		_allowedMappedContentType = allowedMappedContentType;
		_existsMappedContentType = existsMappedContentType;
		_layoutPageTemplateEntry = layoutPageTemplateEntry;
		_renderResponse = renderResponse;

		_draftLayout = LayoutLocalServiceUtil.fetchDraftLayout(
			layoutPageTemplateEntry.getPlid());
		_httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
		_infoItemServiceRegistry =
			(InfoItemServiceRegistry)renderRequest.getAttribute(
				InfoItemServiceRegistry.class.getName());

		_itemSelector = (ItemSelector)_httpServletRequest.getAttribute(
			LayoutPageTemplateAdminWebKeys.ITEM_SELECTOR);
		_layoutPageTemplateAdminWebConfiguration =
			(LayoutPageTemplateAdminWebConfiguration)
				_httpServletRequest.getAttribute(
					LayoutPageTemplateAdminWebConfiguration.class.getName());
		_themeDisplay = (ThemeDisplay)_httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public List<DropdownItem> getActionDropdownItems() throws Exception {
		boolean hasUpdatePermission =
			LayoutPageTemplateEntryPermission.contains(
				_themeDisplay.getPermissionChecker(), _layoutPageTemplateEntry,
				ActionKeys.UPDATE);

		int count =
			AssetDisplayPageEntryServiceUtil.getAssetDisplayPageEntriesCount(
				_layoutPageTemplateEntry.getClassNameId(),
				_layoutPageTemplateEntry.getClassTypeId(),
				_layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
				_layoutPageTemplateEntry.isDefaultTemplate());

		return DropdownItemListBuilder.addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() -> hasUpdatePermission,
						_getEditDisplayPageActionUnsafeConsumer(count)
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() ->
							(_allowedMappedContentType ||
							 !_existsMappedContentType) &&
							hasUpdatePermission,
						_getChangeContentTypeActionUnsafeConsumer(count)
					).add(
						() -> hasUpdatePermission,
						_getUpdateLayoutPageTemplateEntryPreviewActionUnsafeConsumer()
					).add(
						() ->
							hasUpdatePermission &&
							(_layoutPageTemplateEntry.getPreviewFileEntryId() >
								0),
						_getDeleteLayoutPageTemplateEntryPreviewActionUnsafeConsumer()
					).add(
						() ->
							hasUpdatePermission && _isShowDiscardDraftAction(),
						_getDiscardDraftActionUnsafeConsumer()
					).add(
						() ->
							_layoutPageTemplateEntry.isApproved() &&
							Validator.isNotNull(
								_layoutPageTemplateEntry.getClassName()) &&
							hasUpdatePermission,
						_getMarkAsDefaultDisplayPageActionUnsafeConsumer()
					).add(
						() -> hasUpdatePermission,
						_getRenameDisplayPageActionUnsafeConsumer()
					).add(
						_getViewUsagesDisplayPageActionUnsafeConsumer(count)
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.addContext(
						() -> hasUpdatePermission,
						_getCopyDisplayPageWithPermissionsActionUnsafeConsumer()
					).add(
						() ->
							_layoutPageTemplateEntry.getLayoutPrototypeId() ==
								0,
						_getExportDisplayPageActionUnsafeConsumer()
					).add(
						() -> hasUpdatePermission,
						_getMoveDisplayPageActionUnsafeConsumer()
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() -> hasUpdatePermission,
						_getConfigureDisplayPageActionUnsafeConsumer()
					).add(
						() -> LayoutPageTemplateEntryPermission.contains(
							_themeDisplay.getPermissionChecker(),
							_layoutPageTemplateEntry, ActionKeys.PERMISSIONS),
						_getPermissionsDisplayPageActionUnsafeConsumer()
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() -> LayoutPageTemplateEntryPermission.contains(
							_themeDisplay.getPermissionChecker(),
							_layoutPageTemplateEntry, ActionKeys.DELETE),
						_getDeleteDisplayPageActionUnsafeConsumer()
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).build();
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getChangeContentTypeActionUnsafeConsumer(int count) {

		return dropdownItem -> {
			dropdownItem.putData("action", "changeContentType");
			dropdownItem.putData("assetType", _getTypeLabel());

			if (count > 0) {
				dropdownItem.putData("viewUsagesURL", _getViewUsagesURL());
			}
			else {
				dropdownItem.putData(
					"changeContentTypeURL",
					_getChangeContentTypeURL(_themeDisplay.getURLCurrent()));
				dropdownItem.putData(
					"classNameId",
					String.valueOf(_layoutPageTemplateEntry.getClassNameId()));
				dropdownItem.putData(
					"classTypeId",
					String.valueOf(_layoutPageTemplateEntry.getClassTypeId()));
			}

			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "change-content-type"));
		};
	}

	private String _getChangeContentTypeURL(String redirectURL) {
		return PortletURLBuilder.createActionURL(
			_renderResponse
		).setActionName(
			"/layout_page_template_admin/update_display_page_entry_content_type"
		).setRedirect(
			redirectURL
		).setParameter(
			"layoutPageTemplateEntryId",
			_layoutPageTemplateEntry.getLayoutPageTemplateEntryId()
		).setParameter(
			"p_l_back_url", _themeDisplay.getURLCurrent()
		).setParameter(
			"p_l_back_url_title",
			() -> {
				PortletDisplay portletDisplay =
					_themeDisplay.getPortletDisplay();

				return portletDisplay.getPortletDisplayName();
			}
		).buildString();
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getConfigureDisplayPageActionUnsafeConsumer() {

		String currentURL = PortalUtil.getCurrentURL(_httpServletRequest);

		String configureDisplayPageURL = PortletURLBuilder.create(
			PortalUtil.getControlPanelPortletURL(
				_httpServletRequest, LayoutAdminPortletKeys.GROUP_PAGES,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/layout_admin/edit_layout"
		).setRedirect(
			currentURL
		).setBackURL(
			currentURL
		).setParameter(
			"groupId", _layoutPageTemplateEntry.getGroupId()
		).setParameter(
			"selPlid", _layoutPageTemplateEntry.getPlid()
		).buildString();

		return dropdownItem -> {
			dropdownItem.setHref(configureDisplayPageURL);
			dropdownItem.setIcon("cog");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "configure"));
		};
	}

	private UnsafeConsumer<DropdownContextItem, Exception>
		_getCopyDisplayPageWithPermissionsActionUnsafeConsumer() {

		return dropdownContextItem -> {
			if (_layoutPageTemplateEntry.isDraft()) {
				dropdownContextItem.setDisabled(true);
			}
			else {
				dropdownContextItem.setDropdownItems(
					DropdownItemListBuilder.add(
						dropdownItem -> {
							dropdownItem.putData("action", "copyDisplayPage");
							dropdownItem.putData(
								"copyDisplayPageURL", _getCopyURL(false));
							dropdownItem.setLabel(
								LanguageUtil.get(
									_httpServletRequest, "display-page"));
						}
					).add(
						dropdownItem -> {
							dropdownItem.putData("action", "copyDisplayPage");
							dropdownItem.putData(
								"copyDisplayPageURL", _getCopyURL(true));
							dropdownItem.setLabel(
								LanguageUtil.get(
									_httpServletRequest,
									"display-page-with-permissions"));
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
		_getDeleteDisplayPageActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.putData("action", "deleteDisplayPage");

			String key = "are-you-sure-you-want-to-delete-this";

			if (_layoutPageTemplateEntry.isDefaultTemplate()) {
				key =
					"are-you-sure-you-want-to-delete-the-default-display-" +
						"page-template";
			}

			dropdownItem.putData(
				"deleteDisplayPageMessage",
				LanguageUtil.get(_httpServletRequest, key));
			dropdownItem.putData(
				"deleteDisplayPageURL",
				PortletURLBuilder.createActionURL(
					_renderResponse
				).setActionName(
					"/layout_page_template_admin" +
						"/delete_layout_page_template_entry"
				).setRedirect(
					_themeDisplay.getURLCurrent()
				).setTabs1(
					"display-page-templates"
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
		_getDeleteLayoutPageTemplateEntryPreviewActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.putData(
				"action", "deleteLayoutPageTemplateEntryPreview");
			dropdownItem.putData(
				"deleteLayoutPageTemplateEntryPreviewURL",
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
		_getEditDisplayPageActionUnsafeConsumer(int count) {

		PortletDisplay portletDisplay = _themeDisplay.getPortletDisplay();

		return dropdownItem -> {
			String editDisplayPageURL = HttpComponentsUtil.addParameters(
				PortalUtil.getLayoutFullURL(_draftLayout, _themeDisplay),
				"p_l_back_url", _themeDisplay.getURLCurrent(),
				"p_l_back_url_title", portletDisplay.getPortletDisplayName(),
				"p_l_mode", Constants.EDIT);

			if (!_existsMappedContentType && (count > 0)) {
				dropdownItem.setDisabled(true);
			}
			else if (!_existsMappedContentType) {
				dropdownItem.putData("action", "changeContentType");
				dropdownItem.putData(
					"changeContentTypeURL",
					_getChangeContentTypeURL(editDisplayPageURL));
				dropdownItem.putData(
					"classNameId",
					String.valueOf(_layoutPageTemplateEntry.getClassNameId()));
				dropdownItem.putData(
					"classTypeId",
					String.valueOf(_layoutPageTemplateEntry.getClassTypeId()));
				dropdownItem.putData("hasMissingType", Boolean.TRUE.toString());
				dropdownItem.putData("hasUsages", Boolean.FALSE.toString());
			}
			else {
				dropdownItem.setHref(editDisplayPageURL);
			}

			dropdownItem.setIcon("pencil");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "edit"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getExportDisplayPageActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.setDisabled(_layoutPageTemplateEntry.isDraft());
			dropdownItem.setHref(
				ResourceURLBuilder.createResourceURL(
					_renderResponse
				).setParameter(
					"layoutPageTemplateEntryId",
					_layoutPageTemplateEntry.getLayoutPageTemplateEntryId()
				).setResourceID(
					"/layout_page_template_admin/export_display_pages"
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
				_layoutPageTemplateAdminWebConfiguration.thumbnailExtensions()
			).maxFileSize(
				UploadServletRequestConfigurationProviderUtil.getMaxSize()
			).portletId(
				LayoutPageTemplateAdminPortletKeys.LAYOUT_PAGE_TEMPLATES
			).repositoryName(
				LanguageUtil.get(_themeDisplay.getLocale(), "page-template")
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

	private String _getLayoutPageTemplateCollectionItemSelectorURL() {
		LayoutPageTemplateCollectionTreeNodeItemSelectorCriterion
			layoutPageTemplateCollectionTreeNodeItemSelectorCriterion =
				new LayoutPageTemplateCollectionTreeNodeItemSelectorCriterion();

		layoutPageTemplateCollectionTreeNodeItemSelectorCriterion.
			setDesiredItemSelectorReturnTypes(new UUIDItemSelectorReturnType());

		return PortletURLBuilder.create(
			_itemSelector.getItemSelectorURL(
				RequestBackedPortletURLFactoryUtil.create(_httpServletRequest),
				"selectFolder",
				layoutPageTemplateCollectionTreeNodeItemSelectorCriterion)
		).buildString();
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getMarkAsDefaultDisplayPageActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.putData("action", "markAsDefaultDisplayPage");
			dropdownItem.putData(
				"markAsDefaultDisplayPageURL",
				PortletURLBuilder.createActionURL(
					_renderResponse
				).setActionName(
					"/layout_page_template_admin" +
						"/edit_layout_page_template_entry_settings"
				).setRedirect(
					_themeDisplay.getURLCurrent()
				).setParameter(
					"defaultTemplate",
					!_layoutPageTemplateEntry.isDefaultTemplate()
				).setParameter(
					"layoutPageTemplateEntryId",
					_layoutPageTemplateEntry.getLayoutPageTemplateEntryId()
				).buildString());

			String message = StringPool.BLANK;

			LayoutPageTemplateEntry defaultLayoutPageTemplateEntry =
				LayoutPageTemplateEntryServiceUtil.
					fetchDefaultLayoutPageTemplateEntry(
						_layoutPageTemplateEntry.getGroupId(),
						_layoutPageTemplateEntry.getClassNameId(),
						_layoutPageTemplateEntry.getClassTypeId());

			if (defaultLayoutPageTemplateEntry != null) {
				long defaultLayoutPageTemplateEntryId =
					defaultLayoutPageTemplateEntry.
						getLayoutPageTemplateEntryId();
				long layoutPageTemplateEntryId =
					_layoutPageTemplateEntry.getLayoutPageTemplateEntryId();

				if (defaultLayoutPageTemplateEntryId !=
						layoutPageTemplateEntryId) {

					message = LanguageUtil.format(
						_httpServletRequest,
						"do-you-want-to-replace-x-for-x-as-the-default-" +
							"display-page-template",
						new String[] {
							_layoutPageTemplateEntry.getName(),
							defaultLayoutPageTemplateEntry.getName()
						});
				}
			}

			if (Validator.isNull(message) &&
				_layoutPageTemplateEntry.isDefaultTemplate()) {

				message = LanguageUtil.get(
					_httpServletRequest, "unmark-default-confirmation");
			}

			dropdownItem.putData("message", message);

			String label = "mark-as-default";

			if (_layoutPageTemplateEntry.isDefaultTemplate()) {
				label = "unmark-as-default";
			}

			dropdownItem.setLabel(LanguageUtil.get(_httpServletRequest, label));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getMoveDisplayPageActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.putData("action", "moveDisplayPage");
			dropdownItem.putData(
				"itemSelectorURL",
				_getLayoutPageTemplateCollectionItemSelectorURL());
			dropdownItem.putData(
				"layoutPageTemplateEntryId",
				String.valueOf(
					_layoutPageTemplateEntry.getLayoutPageTemplateEntryId()));
			dropdownItem.putData(
				"layoutPageTemplateEntryName",
				_layoutPageTemplateEntry.getName());
			dropdownItem.putData(
				"moveSelectedDisplayPageURL",
				PortletURLBuilder.createActionURL(
					_renderResponse
				).setActionName(
					StringBundler.concat(
						"/layout_page_template_admin",
						"/move_layout_page_template_entries",
						"_and_layout_page_template_collections")
				).setRedirect(
					_themeDisplay.getURLCurrent()
				).buildString());
			dropdownItem.setIcon("move-folder");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "move"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
			_getPermissionsDisplayPageActionUnsafeConsumer()
		throws Exception {

		String permissionsDisplayPageURL = PermissionsURLTag.doTag(
			StringPool.BLANK, LayoutPageTemplateEntry.class.getName(),
			_layoutPageTemplateEntry.getName(), null,
			String.valueOf(
				_layoutPageTemplateEntry.getLayoutPageTemplateEntryId()),
			LiferayWindowState.POP_UP.toString(), null, _httpServletRequest);

		return dropdownItem -> {
			dropdownItem.putData("action", "permissionsDisplayPage");
			dropdownItem.putData(
				"permissionsDisplayPageURL", permissionsDisplayPageURL);
			dropdownItem.setIcon("password-policies");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "permissions"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getRenameDisplayPageActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.putData("action", "renameDisplayPage");
			dropdownItem.putData(
				"layoutPageTemplateEntryId",
				String.valueOf(
					_layoutPageTemplateEntry.getLayoutPageTemplateEntryId()));
			dropdownItem.putData(
				"layoutPageTemplateEntryName",
				_layoutPageTemplateEntry.getName());
			dropdownItem.putData(
				"updateDisplayPageURL",
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

	private String _getTypeLabel() {
		InfoItemDetailsProvider<?> infoItemDetailsProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemDetailsProvider.class,
				_layoutPageTemplateEntry.getClassName());

		if (infoItemDetailsProvider == null) {
			return StringPool.BLANK;
		}

		InfoItemClassDetails infoItemClassDetails =
			infoItemDetailsProvider.getInfoItemClassDetails();

		return infoItemClassDetails.getLabel(_themeDisplay.getLocale());
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getUpdateLayoutPageTemplateEntryPreviewActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.putData(
				"action", "updateLayoutPageTemplateEntryPreview");
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

	private UnsafeConsumer<DropdownItem, Exception>
		_getViewUsagesDisplayPageActionUnsafeConsumer(int count) {

		return dropdownItem -> {
			dropdownItem.setDisabled(count == 0);
			dropdownItem.setHref(_getViewUsagesURL());
			dropdownItem.setIcon("list-ul");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "view-usages"));
		};
	}

	private String _getViewUsagesURL() {
		if (_viewUsagesURL != null) {
			return _viewUsagesURL;
		}

		_viewUsagesURL = RenderURLBuilder.createRenderURL(
			_renderResponse
		).setMVCRenderCommandName(
			"/layout_page_template_admin/view_asset_display_page_usages"
		).setRedirect(
			_themeDisplay.getURLCurrent()
		).setParameter(
			"classNameId", _layoutPageTemplateEntry.getClassNameId()
		).setParameter(
			"classTypeId", _layoutPageTemplateEntry.getClassTypeId()
		).setParameter(
			"layoutPageTemplateEntryId",
			_layoutPageTemplateEntry.getLayoutPageTemplateEntryId()
		).setParameter(
			"defaultTemplate", _layoutPageTemplateEntry.isDefaultTemplate()
		).buildString();

		return _viewUsagesURL;
	}

	private boolean _isShowDiscardDraftAction() {
		if (_draftLayout == null) {
			return false;
		}

		return _draftLayout.isDraft();
	}

	private final boolean _allowedMappedContentType;
	private final Layout _draftLayout;
	private final boolean _existsMappedContentType;
	private final HttpServletRequest _httpServletRequest;
	private final InfoItemServiceRegistry _infoItemServiceRegistry;
	private final ItemSelector _itemSelector;
	private final LayoutPageTemplateAdminWebConfiguration
		_layoutPageTemplateAdminWebConfiguration;
	private final LayoutPageTemplateEntry _layoutPageTemplateEntry;
	private final RenderResponse _renderResponse;
	private final ThemeDisplay _themeDisplay;
	private String _viewUsagesURL;

}