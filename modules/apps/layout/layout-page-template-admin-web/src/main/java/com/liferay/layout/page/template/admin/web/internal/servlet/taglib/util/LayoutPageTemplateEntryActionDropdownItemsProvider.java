/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.servlet.taglib.util;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
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
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.item.selector.LayoutPageTemplateCollectionItemSelectorCriterion;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutPrototype;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.portlet.url.builder.ResourceURLBuilder;
import com.liferay.portal.kernel.security.auth.AuthTokenUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutPrototypeLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutPrototypeServiceUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.configuration.UploadServletRequestConfigurationProviderUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.staging.StagingGroupHelper;
import com.liferay.staging.StagingGroupHelperUtil;
import com.liferay.taglib.security.PermissionsURLTag;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Objects;

/**
 * @author Eudaldo Alonso
 */
public class LayoutPageTemplateEntryActionDropdownItemsProvider {

	public LayoutPageTemplateEntryActionDropdownItemsProvider(
		LayoutPageTemplateEntry layoutPageTemplateEntry,
		RenderRequest renderRequest, RenderResponse renderResponse) {

		_layoutPageTemplateEntry = layoutPageTemplateEntry;
		_renderResponse = renderResponse;

		_httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);

		_itemSelector = (ItemSelector)_httpServletRequest.getAttribute(
			LayoutPageTemplateAdminWebKeys.ITEM_SELECTOR);
		_layoutPageTemplateAdminWebConfiguration =
			(LayoutPageTemplateAdminWebConfiguration)
				_httpServletRequest.getAttribute(
					LayoutPageTemplateAdminWebConfiguration.class.getName());
		_themeDisplay = (ThemeDisplay)_httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		_draftLayout = LayoutLocalServiceUtil.fetchDraftLayout(
			layoutPageTemplateEntry.getPlid());
		_layout = LayoutLocalServiceUtil.fetchLayout(
			layoutPageTemplateEntry.getPlid());
	}

	public List<DropdownItem> getActionDropdownItems() throws Exception {
		boolean hasUpdatePermission =
			LayoutPageTemplateEntryPermission.contains(
				_themeDisplay.getPermissionChecker(), _layoutPageTemplateEntry,
				ActionKeys.UPDATE);

		return DropdownItemListBuilder.addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() -> hasUpdatePermission,
						_getEditLayoutPageTemplateEntryActionUnsafeConsumer()
					).add(
						() ->
							LayoutPageTemplateEntryPermission.contains(
								_themeDisplay.getPermissionChecker(),
								_layoutPageTemplateEntry, ActionKeys.VIEW) &&
							!Objects.equals(
								_layoutPageTemplateEntry.getType(),
								LayoutPageTemplateEntryTypeConstants.
									WIDGET_PAGE),
						_getViewLayoutPageTemplateEntryActionUnsafeConsumer()
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() -> hasUpdatePermission,
						_getMoveLayoutPageTemplateEntryPreviewActionUnsafeConsumer()
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
						() -> hasUpdatePermission,
						_getRenameLayoutPageTemplateEntryActionUnsafeConsumer()
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() ->
							_layoutPageTemplateEntry.getLayoutPrototypeId() ==
								0,
						_getExportLayoutPageTemplateEntryActionUnsafeConsumer()
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() ->
							hasUpdatePermission &&
							(_layoutPageTemplateEntry.getLayoutPrototypeId() >
								0),
						_getConfigureLayoutPrototypeActionUnsafeConsumer()
					).add(
						() ->
							hasUpdatePermission &&
							(_layoutPageTemplateEntry.getLayoutPrototypeId() <=
								0),
						_getConfigureLayoutPageTemplateEntryActionUnsafeConsumer()
					).add(
						() -> LayoutPageTemplateEntryPermission.contains(
							_themeDisplay.getPermissionChecker(),
							_layoutPageTemplateEntry, ActionKeys.PERMISSIONS),
						_getPermissionsLayoutPageTemplateEntryActionUnsafeConsumer()
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
						_getDeleteLayoutPageTemplateEntryActionUnsafeConsumer()
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).build();
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getConfigureLayoutPageTemplateEntryActionUnsafeConsumer() {

		PortletURL editPageURL = PortalUtil.getControlPanelPortletURL(
			_httpServletRequest, LayoutAdminPortletKeys.GROUP_PAGES,
			PortletRequest.RENDER_PHASE);

		return dropdownItem -> {
			PortletDisplay portletDisplay = _themeDisplay.getPortletDisplay();

			dropdownItem.setHref(
				editPageURL, "mvcRenderCommandName",
				"/layout_admin/edit_layout", "redirect",
				_themeDisplay.getURLCurrent(), "backURL",
				_themeDisplay.getURLCurrent(), "backURLTitle",
				portletDisplay.getPortletDisplayName(), "portletResource",
				LayoutPageTemplateAdminPortletKeys.LAYOUT_PAGE_TEMPLATES,
				"selPlid", _layoutPageTemplateEntry.getPlid());

			dropdownItem.setIcon("cog");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "configure"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getConfigureLayoutPrototypeActionUnsafeConsumer() {

		PortletURL configureLayoutPrototypeURL =
			PortletURLBuilder.createRenderURL(
				_renderResponse
			).setMVCPath(
				"/edit_layout_prototype.jsp"
			).setRedirect(
				_themeDisplay.getURLCurrent()
			).setParameter(
				"backURLTitle",
				() -> {
					PortletDisplay portletDisplay =
						_themeDisplay.getPortletDisplay();

					return portletDisplay.getPortletDisplayName();
				}
			).setParameter(
				"layoutPrototypeId",
				_layoutPageTemplateEntry.getLayoutPrototypeId()
			).buildPortletURL();

		return dropdownItem -> {
			dropdownItem.setHref(configureLayoutPrototypeURL);
			dropdownItem.setIcon("cog");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "configure"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getDeleteLayoutPageTemplateEntryActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.putData("action", "deleteLayoutPageTemplateEntry");
			dropdownItem.putData(
				"deleteLayoutPageTemplateEntryURL",
				PortletURLBuilder.createActionURL(
					_renderResponse
				).setActionName(
					"/layout_page_template_admin" +
						"/delete_layout_page_template_entry"
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
			_getEditLayoutPageTemplateEntryActionUnsafeConsumer()
		throws Exception {

		PortletDisplay portletDisplay = _themeDisplay.getPortletDisplay();

		if (Objects.equals(
				_layoutPageTemplateEntry.getType(),
				LayoutPageTemplateEntryTypeConstants.WIDGET_PAGE)) {

			LayoutPrototype layoutPrototype =
				LayoutPrototypeLocalServiceUtil.fetchLayoutPrototype(
					_layoutPageTemplateEntry.getLayoutPrototypeId());

			if (layoutPrototype == null) {
				return null;
			}

			Group layoutPrototypeGroup = layoutPrototype.getGroup();

			return dropdownItem -> {
				dropdownItem.setHref(
					HttpComponentsUtil.addParameters(
						layoutPrototypeGroup.getDisplayURL(_themeDisplay, true),
						"p_l_back_url", _themeDisplay.getURLCurrent(),
						"p_l_back_url_title",
						portletDisplay.getPortletDisplayName()));
				dropdownItem.setIcon("pencil");
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "edit"));
			};
		}

		return dropdownItem -> {
			dropdownItem.setHref(
				HttpComponentsUtil.addParameters(
					PortalUtil.getLayoutFullURL(_draftLayout, _themeDisplay),
					"p_l_back_url", _themeDisplay.getURLCurrent(),
					"p_l_back_url_title", portletDisplay.getTitle(), "p_l_mode",
					Constants.EDIT));
			dropdownItem.setIcon("pencil");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "edit"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getExportLayoutPageTemplateEntryActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.setDisabled(_layoutPageTemplateEntry.isDraft());
			dropdownItem.setHref(
				ResourceURLBuilder.createResourceURL(
					_renderResponse
				).setParameter(
					"layoutPageTemplateCollectionId",
					_layoutPageTemplateEntry.getLayoutPageTemplateCollectionId()
				).setParameter(
					"layoutPageTemplateEntryId",
					_layoutPageTemplateEntry.getLayoutPageTemplateEntryId()
				).setResourceID(
					"/layout_page_template_admin" +
						"/export_layout_page_template_entries"
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

	private UnsafeConsumer<DropdownItem, Exception>
		_getMoveLayoutPageTemplateEntryPreviewActionUnsafeConsumer() {

		RenderResponse renderResponse =
			(RenderResponse)_httpServletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_RESPONSE);

		LayoutPageTemplateCollectionItemSelectorCriterion
			layoutPageTemplateCollectionItemSelectorCriterion =
				new LayoutPageTemplateCollectionItemSelectorCriterion();

		layoutPageTemplateCollectionItemSelectorCriterion.
			setDesiredItemSelectorReturnTypes(new UUIDItemSelectorReturnType());

		PortletURL itemSelectorURL = _itemSelector.getItemSelectorURL(
			RequestBackedPortletURLFactoryUtil.create(_httpServletRequest),
			renderResponse.getNamespace() + "selectItem",
			layoutPageTemplateCollectionItemSelectorCriterion);

		return dropdownItem -> {
			dropdownItem.putData("action", "moveLayoutPageTemplateEntry");
			dropdownItem.putData("itemSelectorURL", itemSelectorURL.toString());
			dropdownItem.putData(
				"moveLayoutPageTemplateEntryURL",
				PortletURLBuilder.createActionURL(
					_renderResponse
				).setActionName(
					"/layout_page_template_admin" +
						"/move_layout_page_template_entry"
				).setRedirect(
					_themeDisplay.getURLCurrent()
				).setParameter(
					"layoutPageTemplateEntryId",
					_layoutPageTemplateEntry.getLayoutPageTemplateEntryId()
				).buildString());
			dropdownItem.setIcon("move-folder");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "move-to"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
			_getPermissionsLayoutPageTemplateEntryActionUnsafeConsumer()
		throws Exception {

		String permissionsLayoutPageTemplateEntryURL = PermissionsURLTag.doTag(
			StringPool.BLANK, LayoutPageTemplateEntry.class.getName(),
			_layoutPageTemplateEntry.getName(), null,
			String.valueOf(
				_layoutPageTemplateEntry.getLayoutPageTemplateEntryId()),
			LiferayWindowState.POP_UP.toString(), null, _httpServletRequest);

		return dropdownItem -> {
			dropdownItem.putData(
				"action", "permissionsLayoutPageTemplateEntry");
			dropdownItem.putData(
				"permissionsLayoutPageTemplateEntryURL",
				permissionsLayoutPageTemplateEntryURL);
			dropdownItem.setIcon("password-policies");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "permissions"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
			_getRenameLayoutPageTemplateEntryActionUnsafeConsumer()
		throws Exception {

		if (Objects.equals(
				_layoutPageTemplateEntry.getType(),
				LayoutPageTemplateEntryTypeConstants.WIDGET_PAGE)) {

			LayoutPrototype layoutPrototype =
				LayoutPrototypeServiceUtil.fetchLayoutPrototype(
					_layoutPageTemplateEntry.getLayoutPrototypeId());

			return dropdownItem -> {
				dropdownItem.putData("action", "renameLayoutPageTemplateEntry");
				dropdownItem.putData("idFieldName", "layoutPrototypeId");
				dropdownItem.putData(
					"idFieldValue",
					String.valueOf(layoutPrototype.getLayoutPrototypeId()));
				dropdownItem.putData(
					"layoutPageTemplateEntryName",
					_layoutPageTemplateEntry.getName());
				dropdownItem.putData(
					"updateLayoutPageTemplateEntryURL",
					_getUpdateLayoutPrototypeURL(layoutPrototype));
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "rename"));
			};
		}

		return dropdownItem -> {
			dropdownItem.putData("action", "renameLayoutPageTemplateEntry");
			dropdownItem.putData("idFieldName", "layoutPageTemplateEntryId");
			dropdownItem.putData(
				"idFieldValue",
				String.valueOf(
					_layoutPageTemplateEntry.getLayoutPageTemplateEntryId()));
			dropdownItem.putData(
				"layoutPageTemplateEntryName",
				_layoutPageTemplateEntry.getName());
			dropdownItem.putData(
				"updateLayoutPageTemplateEntryURL",
				_getUpdateLayoutPageTemplateEntryURL());
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "rename"));
		};
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

	private String _getUpdateLayoutPageTemplateEntryURL() {
		return PortletURLBuilder.createActionURL(
			_renderResponse
		).setActionName(
			"/layout_page_template_admin/update_layout_page_template_entry"
		).setRedirect(
			_themeDisplay.getURLCurrent()
		).setParameter(
			"layoutPageTemplateCollectionId",
			_layoutPageTemplateEntry.getLayoutPageTemplateCollectionId()
		).setParameter(
			"layoutPageTemplateEntryId",
			_layoutPageTemplateEntry.getLayoutPageTemplateEntryId()
		).buildString();
	}

	private String _getUpdateLayoutPrototypeURL(
		LayoutPrototype layoutPrototype) {

		return PortletURLBuilder.createActionURL(
			_renderResponse
		).setActionName(
			"/layout_page_template_admin/update_layout_prototype"
		).setRedirect(
			_themeDisplay.getURLCurrent()
		).setParameter(
			"layoutPrototypeId", layoutPrototype.getLayoutPrototypeId()
		).buildString();
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getViewLayoutPageTemplateEntryActionUnsafeConsumer() {

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

	private boolean _isLiveGroup() {
		Group group = _themeDisplay.getScopeGroup();

		StagingGroupHelper stagingGroupHelper =
			StagingGroupHelperUtil.getStagingGroupHelper();

		return stagingGroupHelper.isLiveGroup(group);
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
	private final LayoutPageTemplateAdminWebConfiguration
		_layoutPageTemplateAdminWebConfiguration;
	private final LayoutPageTemplateEntry _layoutPageTemplateEntry;
	private final RenderResponse _renderResponse;
	private final ThemeDisplay _themeDisplay;

}