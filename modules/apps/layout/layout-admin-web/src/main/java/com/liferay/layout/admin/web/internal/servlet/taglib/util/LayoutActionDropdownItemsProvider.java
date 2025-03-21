/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.servlet.taglib.util;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownContextItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.layout.admin.constants.LayoutAdminPortletKeys;
import com.liferay.layout.admin.web.internal.display.context.LayoutsAdminDisplayContext;
import com.liferay.layout.admin.web.internal.helper.LayoutActionsHelper;
import com.liferay.layout.admin.web.internal.security.permission.resource.LayoutPageTemplatePermission;
import com.liferay.layout.constants.LayoutTypeSettingsConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateActionKeys;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.bean.BeanPropertiesUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.segments.service.SegmentsExperienceLocalServiceUtil;
import com.liferay.taglib.security.PermissionsURLTag;
import com.liferay.translation.url.provider.TranslationURLProvider;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Víctor Galán
 */
public class LayoutActionDropdownItemsProvider {

	public LayoutActionDropdownItemsProvider(
		HttpServletRequest httpServletRequest,
		LayoutActionsHelper layoutActionsHelper,
		LayoutsAdminDisplayContext layoutsAdminDisplayContext,
		LiferayPortletResponse liferayPortletResponse,
		TranslationURLProvider translationURLProvider) {

		_httpServletRequest = httpServletRequest;
		_layoutActionsHelper = layoutActionsHelper;
		_layoutsAdminDisplayContext = layoutsAdminDisplayContext;
		_liferayPortletResponse = liferayPortletResponse;
		_translationURLProvider = translationURLProvider;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public List<DropdownItem> getActionDropdownItems(
			Layout layout, boolean includeAddChildPageAction)
		throws Exception {

		Layout draftLayout = _layoutsAdminDisplayContext.getDraftLayout(layout);

		return DropdownItemListBuilder.addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() ->
							_isEditable(layout) &&
							layout.isLayoutUpdateable() &&
							_layoutActionsHelper.isShowConfigureAction(layout),
						_getEditLayoutActionUnsafeConsumer(layout)
					).add(
						() -> _layoutActionsHelper.isShowTranslateAction(
							layout),
						_getAutomaticTranslateLayoutActionUnsafeConsumer(
							draftLayout, layout)
					).add(
						_getPreviewLayoutActionUnsafeConsumer(
							draftLayout, layout)
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() ->
							includeAddChildPageAction &&
							_layoutsAdminDisplayContext.
								isShowAddChildPageAction(layout),
						_getAddLayoutActionUnsafeConsumer(layout)
					).add(
						() ->
							_layoutActionsHelper.isShowConvertLayoutAction(
								layout) &&
							(draftLayout == null),
						_getConvertToContentPageLayoutActionUnsafeConsumer(
							layout)
					).add(
						() ->
							_layoutActionsHelper.isShowConvertLayoutAction(
								layout) &&
							(draftLayout != null),
						_getDiscardConversionDraftLayoutActionUnsafeConsumer(
							draftLayout)
					).add(
						() -> _layoutActionsHelper.isShowPreviewDraftActions(
							layout),
						_getPreviewDraftLayoutActionUnsafeConsumer(layout)
					).add(
						() -> _layoutActionsHelper.isShowDiscardDraftActions(
							layout),
						_getDiscardDraftLayoutActionUnsafeConsumer(layout)
					).add(
						() -> _layoutActionsHelper.isShowOrphanPortletsAction(
							layout, _layoutsAdminDisplayContext.getSelGroup()),
						_getOrphanWidgetsLayoutActionUnsafeConsumer(layout)
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() -> _isShowConvertToPageTemplateAction(layout),
						_getConvertToPageTemplateActionUnsafeConsumer(layout)
					).addContext(
						_getCopyLayoutWithPermissionsActionUnsafeConsumer(
							layout)
					).add(
						() ->
							_layoutActionsHelper.isShowExportTranslationAction(
								layout),
						_getExportForTranslationLayoutActionUnsafeConsumer(
							draftLayout, layout)
					).add(
						() ->
							_layoutActionsHelper.isShowImportTranslationAction(
								layout),
						_getImportTranslationLayoutActionUnsafeConsumer(
							draftLayout, layout)
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() -> _layoutActionsHelper.isShowConfigureAction(
							layout),
						_getConfigureLayoutActionUnsafeConsumer(layout)
					).add(
						() -> _layoutActionsHelper.isShowPermissionsAction(
							layout, _layoutsAdminDisplayContext.getSelGroup()),
						_getPermissionLayoutActionUnsafeConsumer(layout)
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() -> _layoutActionsHelper.isShowDeleteAction(layout),
						_getDeleteLayoutActionUnsafeConsumer(layout)
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).build();
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getAddLayoutActionUnsafeConsumer(Layout layout) {

		return dropdownItem -> {
			dropdownItem.setHref(
				_layoutsAdminDisplayContext.getSelectLayoutPageTemplateEntryURL(
					0, layout.getPlid(), layout.isPrivateLayout()));
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "add-page"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getAutomaticTranslateLayoutActionUnsafeConsumer(
			Layout draftLayout, Layout layout) {

		return dropdownItem -> {
			dropdownItem.setHref(
				PortletURLBuilder.create(
					_translationURLProvider.getTranslateURL(
						_themeDisplay.getScopeGroupId(),
						PortalUtil.getClassNameId(Layout.class.getName()),
						BeanPropertiesUtil.getLong(
							draftLayout, "plid", layout.getPlid()),
						RequestBackedPortletURLFactoryUtil.create(
							_httpServletRequest))
				).setRedirect(
					_layoutsAdminDisplayContext.getRedirect()
				).setPortletResource(
					() -> {
						PortletDisplay portletDisplay =
							_themeDisplay.getPortletDisplay();

						return portletDisplay.getId();
					}
				).setParameter(
					"backURLTitle",
					LanguageUtil.get(_themeDisplay.getLocale(), "pages")
				).setParameter(
					"segmentsExperienceId",
					SegmentsExperienceLocalServiceUtil.
						fetchDefaultSegmentsExperienceId(layout.getPlid())
				).buildString());
			dropdownItem.setIcon("automatic-translate");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "translate"));
		};
	}

	private String _getBackURL() {
		PortletURL portletURL = PortalUtil.getControlPanelPortletURL(
			_httpServletRequest, _layoutsAdminDisplayContext.getGroup(),
			LayoutAdminPortletKeys.GROUP_PAGES, 0, 0,
			PortletRequest.RENDER_PHASE);

		return portletURL.toString();
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getConfigureLayoutActionUnsafeConsumer(Layout layout) {

		return dropdownItem -> {
			dropdownItem.setHref(
				_layoutsAdminDisplayContext.getConfigureLayoutURL(layout));
			dropdownItem.setIcon("cog");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "configure"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getConvertToContentPageLayoutActionUnsafeConsumer(Layout layout) {

		return dropdownItem -> {
			dropdownItem.setHref(_getLayoutConversionPreviewURL(layout));
			dropdownItem.setIcon("page");
			dropdownItem.setLabel(
				LanguageUtil.get(
					_httpServletRequest, "convert-to-content-page..."));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getConvertToPageTemplateActionUnsafeConsumer(Layout layout) {

		return dropdownItem -> {
			if (_layoutActionsHelper.isShowCopyLayoutAction(
					layout, _layoutsAdminDisplayContext.getSelGroup())) {

				dropdownItem.putData("action", "convertToPageTemplate");
			}
			else {
				dropdownItem.setDisabled(true);
			}

			dropdownItem.setIcon("page-template");
			dropdownItem.setLabel(
				LanguageUtil.get(
					_httpServletRequest, "convert-to-page-template"));
		};
	}

	private String _getCopyLayoutRenderURL(
			boolean copyPermissions, Layout layout)
		throws Exception {

		return PortletURLBuilder.createRenderURL(
			_liferayPortletResponse
		).setMVCRenderCommandName(
			"/layout_admin/add_layout"
		).setParameter(
			"copyPermissions", copyPermissions
		).setParameter(
			"privateLayout", _layoutsAdminDisplayContext.isPrivateLayout()
		).setParameter(
			"sourcePlid", layout.getPlid()
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	private UnsafeConsumer<DropdownContextItem, Exception>
		_getCopyLayoutWithPermissionsActionUnsafeConsumer(Layout layout) {

		return dropdownContextItem -> {
			if (_layoutActionsHelper.isShowCopyLayoutAction(
					layout, _layoutsAdminDisplayContext.getSelGroup())) {

				dropdownContextItem.setDropdownItems(
					DropdownItemListBuilder.add(
						dropdownItem -> {
							dropdownItem.putData("action", "copyLayout");
							dropdownItem.putData(
								"copyLayoutURL",
								_getCopyLayoutRenderURL(false, layout));
							dropdownItem.setLabel(
								LanguageUtil.get(_httpServletRequest, "page"));
						}
					).add(
						dropdownItem -> {
							dropdownItem.putData(
								"action", "copyLayoutWithPermissions");
							dropdownItem.putData(
								"copyLayoutURL",
								_getCopyLayoutRenderURL(true, layout));
							dropdownItem.setLabel(
								LanguageUtil.get(
									_httpServletRequest,
									"page-with-permissions"));
						}
					).build());
			}
			else {
				dropdownContextItem.setDisabled(true);
			}

			dropdownContextItem.setIcon("copy");
			dropdownContextItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "make-a-copy"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getDeleteLayoutActionUnsafeConsumer(Layout layout) {

		return dropdownItem -> {
			dropdownItem.putData("action", "deleteLayout");
			dropdownItem.putData(
				"deleteLayoutURL", _getDeleteLayoutURL(layout));

			String messageKey =
				"are-you-sure-you-want-to-delete-the-page-x.-it-will-be-" +
					"removed-immediately";

			if (layout.hasChildren() && _hasScopeGroup(layout)) {
				messageKey = StringBundler.concat(
					"are-you-sure-you-want-to-delete-the-page-x.-this-page-",
					"serves-as-a-scope-for-content-and-also-contains-child-",
					"pages");
			}
			else if (layout.hasChildren()) {
				messageKey =
					"are-you-sure-you-want-to-delete-the-page-x.-this-page-" +
						"contains-child-pages-that-will-also-be-removed";
			}
			else if (_hasScopeGroup(layout)) {
				messageKey =
					"are-you-sure-you-want-to-delete-the-page-x.-this-page-" +
						"serves-as-a-scope-for-content";
			}

			dropdownItem.putData(
				"message",
				LanguageUtil.format(
					_httpServletRequest, messageKey,
					HtmlUtil.escape(
						layout.getName(_themeDisplay.getLocale()))));

			dropdownItem.setIcon("trash");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "delete"));
		};
	}

	private String _getDeleteLayoutURL(Layout layout) throws PortalException {
		return PortletURLBuilder.createActionURL(
			_liferayPortletResponse
		).setActionName(
			"/layout_admin/delete_layout"
		).setRedirect(
			PortletURLBuilder.createRenderURL(
				_liferayPortletResponse
			).setParameter(
				"layoutSetBranchId",
				_layoutsAdminDisplayContext.getActiveLayoutSetBranchId()
			).setParameter(
				"selPlid", layout.getParentPlid()
			).buildString()
		).setParameter(
			"layoutSetBranchId",
			_layoutsAdminDisplayContext.getActiveLayoutSetBranchId()
		).setParameter(
			"selPlid", layout.getPlid()
		).buildString();
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getDiscardConversionDraftLayoutActionUnsafeConsumer(
			Layout draftLayout) {

		return dropdownItem -> {
			dropdownItem.setHref(_getDeleteLayoutURL(draftLayout));
			dropdownItem.setLabel(
				LanguageUtil.get(
					_httpServletRequest, "discard-conversion-draft"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getDiscardDraftLayoutActionUnsafeConsumer(Layout layout) {

		return dropdownItem -> {
			dropdownItem.putData("action", "discardDraft");
			dropdownItem.putData(
				"discardDraftURL", _getDiscardDraftURL(layout));
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "discard-draft"));
		};
	}

	private String _getDiscardDraftURL(Layout layout) {
		return PortletURLBuilder.createActionURL(
			_liferayPortletResponse
		).setActionName(
			"/layout_admin/discard_draft_layout"
		).setRedirect(
			_layoutsAdminDisplayContext.getRedirect()
		).setParameter(
			"selPlid",
			() -> {
				Layout draftLayout = layout.fetchDraftLayout();

				return draftLayout.getPlid();
			}
		).buildString();
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getEditLayoutActionUnsafeConsumer(Layout layout) {

		return dropdownItem -> {
			dropdownItem.setHref(
				_layoutsAdminDisplayContext.getEditLayoutURL(layout));
			dropdownItem.setIcon("pencil");

			String label = LanguageUtil.get(_httpServletRequest, "edit");

			if (_layoutsAdminDisplayContext.isConversionDraft(layout)) {
				label = LanguageUtil.get(
					_httpServletRequest, "edit-conversion-draft");
			}

			dropdownItem.setLabel(label);
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getExportForTranslationLayoutActionUnsafeConsumer(
			Layout draftLayout, Layout layout) {

		return dropdownItem -> {
			dropdownItem.setHref(
				PortletURLBuilder.create(
					_translationURLProvider.getExportTranslationURL(
						layout.getGroupId(),
						PortalUtil.getClassNameId(Layout.class.getName()),
						BeanPropertiesUtil.getLong(
							draftLayout, "plid", layout.getPlid()),
						RequestBackedPortletURLFactoryUtil.create(
							_httpServletRequest))
				).setRedirect(
					_layoutsAdminDisplayContext.getRedirect()
				).setPortletResource(
					() -> {
						PortletDisplay portletDisplay =
							_themeDisplay.getPortletDisplay();

						return portletDisplay.getId();
					}
				).setParameter(
					"backURLTitle",
					LanguageUtil.get(_themeDisplay.getLocale(), "pages")
				).buildString());
			dropdownItem.setIcon("upload");
			dropdownItem.setLabel(
				LanguageUtil.get(
					_httpServletRequest, "export-for-translation"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getImportTranslationLayoutActionUnsafeConsumer(
			Layout draftLayout, Layout layout) {

		return dropdownItem -> {
			dropdownItem.setHref(
				PortletURLBuilder.create(
					_translationURLProvider.getImportTranslationURL(
						layout.getGroupId(),
						PortalUtil.getClassNameId(Layout.class.getName()),
						BeanPropertiesUtil.getLong(
							draftLayout, "plid", layout.getPlid()),
						RequestBackedPortletURLFactoryUtil.create(
							_httpServletRequest))
				).setRedirect(
					_layoutsAdminDisplayContext.getRedirect()
				).setPortletResource(
					() -> {
						PortletDisplay portletDisplay =
							_themeDisplay.getPortletDisplay();

						return portletDisplay.getId();
					}
				).setParameter(
					"backURLTitle",
					LanguageUtil.get(_themeDisplay.getLocale(), "pages")
				).buildString());
			dropdownItem.setIcon("download");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "import-translation"));
		};
	}

	private String _getLayoutConversionPreviewURL(Layout layout) {
		return PortletURLBuilder.createActionURL(
			_liferayPortletResponse
		).setActionName(
			"/layout_admin/add_layout_conversion_preview"
		).setRedirect(
			_layoutsAdminDisplayContext.getRedirect()
		).setParameter(
			"selPlid", layout.getPlid()
		).buildString();
	}

	private String _getOrphanPortletsURL(Layout layout) {
		return PortletURLBuilder.createRenderURL(
			_liferayPortletResponse
		).setMVCRenderCommandName(
			"/layout_admin/view_orphan_portlets"
		).setBackURL(
			_getBackURL()
		).setParameter(
			"selPlid", layout.getPlid()
		).buildString();
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getOrphanWidgetsLayoutActionUnsafeConsumer(Layout layout) {

		return dropdownItem -> {
			dropdownItem.setHref(_getOrphanPortletsURL(layout));
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "orphan-widgets"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getPermissionLayoutActionUnsafeConsumer(Layout layout) {

		return dropdownItem -> {
			dropdownItem.putData("action", "permissionLayout");
			dropdownItem.putData(
				"permissionLayoutURL", _getPermissionsURL(layout));
			dropdownItem.setIcon("password-policies");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "permissions"));
		};
	}

	private String _getPermissionsURL(Layout layout) throws Exception {
		return PermissionsURLTag.doTag(
			StringPool.BLANK, Layout.class.getName(),
			HtmlUtil.escape(layout.getName(_themeDisplay.getLocale())), null,
			String.valueOf(layout.getPlid()),
			LiferayWindowState.POP_UP.toString(), null,
			_themeDisplay.getRequest());
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getPreviewDraftLayoutActionUnsafeConsumer(Layout layout) {

		return dropdownItem -> {
			dropdownItem.put("symbolRight", "shortcut");
			dropdownItem.setHref(_getPreviewDraftURL(layout));
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "preview-draft"));
			dropdownItem.setTarget("_blank");
		};
	}

	private String _getPreviewDraftURL(Layout layout) throws PortalException {
		return PortalUtil.getLayoutFriendlyURL(
			layout.fetchDraftLayout(), _themeDisplay);
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getPreviewLayoutActionUnsafeConsumer(
			Layout draftLayout, Layout layout) {

		return dropdownItem -> {
			if (layout.isTypeContent() &&
				!GetterUtil.getBoolean(
					draftLayout.getTypeSettingsProperty(
						LayoutTypeSettingsConstants.KEY_PUBLISHED))) {

				dropdownItem.setDisabled(true);
			}

			dropdownItem.setHref(
				_layoutsAdminDisplayContext.getViewLayoutURL(layout));
			dropdownItem.setIcon("view");

			String label = LanguageUtil.get(_httpServletRequest, "view");

			if (layout.isDenied() || layout.isPending()) {
				label = LanguageUtil.get(_httpServletRequest, "preview");
			}

			dropdownItem.setLabel(label);
			dropdownItem.setTarget(
				HtmlUtil.escape(layout.getTypeSettingsProperty("target")));
		};
	}

	private boolean _hasScopeGroup(Layout layout) throws Exception {
		if (layout.hasScopeGroup()) {
			return true;
		}

		Layout draftLayout = layout.fetchDraftLayout();

		if (draftLayout == null) {
			return false;
		}

		return draftLayout.hasScopeGroup();
	}

	private boolean _isEditable(Layout layout) {
		if (_layoutsAdminDisplayContext.isConversionDraft(layout) ||
			layout.isTypeContent()) {

			return true;
		}

		return false;
	}

	private boolean _isShowConvertToPageTemplateAction(Layout layout) {
		if (layout.isTypeContent() &&
			LayoutPageTemplatePermission.contains(
				_themeDisplay.getPermissionChecker(), layout.getGroupId(),
				LayoutPageTemplateActionKeys.
					ADD_LAYOUT_PAGE_TEMPLATE_COLLECTION) &&
			LayoutPageTemplatePermission.contains(
				_themeDisplay.getPermissionChecker(), layout.getGroupId(),
				LayoutPageTemplateActionKeys.ADD_LAYOUT_PAGE_TEMPLATE_ENTRY)) {

			return true;
		}

		return false;
	}

	private final HttpServletRequest _httpServletRequest;
	private final LayoutActionsHelper _layoutActionsHelper;
	private final LayoutsAdminDisplayContext _layoutsAdminDisplayContext;
	private final LiferayPortletResponse _liferayPortletResponse;
	private final ThemeDisplay _themeDisplay;
	private final TranslationURLProvider _translationURLProvider;

}