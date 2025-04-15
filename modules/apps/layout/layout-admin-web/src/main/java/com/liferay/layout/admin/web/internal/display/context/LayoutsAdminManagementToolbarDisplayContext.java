/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.display.context.SearchContainerManagementToolbarDisplayContext;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.ClassNameLocalServiceUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.security.PermissionsURLTag;
import com.liferay.translation.url.provider.TranslationURLProvider;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Objects;

/**
 * @author Eudaldo Alonso
 */
public class LayoutsAdminManagementToolbarDisplayContext
	extends SearchContainerManagementToolbarDisplayContext {

	public LayoutsAdminManagementToolbarDisplayContext(
			HttpServletRequest httpServletRequest,
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse,
			LayoutsAdminDisplayContext layoutsAdminDisplayContext)
		throws PortalException {

		super(
			httpServletRequest, liferayPortletRequest, liferayPortletResponse,
			layoutsAdminDisplayContext.getLayoutsSearchContainer());

		_layoutsAdminDisplayContext = layoutsAdminDisplayContext;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
		_translationURLProvider =
			(TranslationURLProvider)httpServletRequest.getAttribute(
				TranslationURLProvider.class.getName());
	}

	@Override
	public List<DropdownItem> getActionDropdownItems() {
		return DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.putData("action", "convertSelectedPages");
				dropdownItem.putData(
					"convertLayoutURL",
					PortletURLBuilder.createActionURL(
						liferayPortletResponse
					).setActionName(
						"/layout_admin/convert_layout"
					).setRedirect(
						_themeDisplay.getURLCurrent()
					).buildString());
				dropdownItem.setIcon("page");
				dropdownItem.setLabel(
					LanguageUtil.get(
						httpServletRequest, "convert-to-content-page"));
				dropdownItem.setQuickAction(true);
			}
		).add(
			dropdownItem -> {
				dropdownItem.putData("action", "exportTranslation");
				dropdownItem.putData(
					"exportTranslationURL",
					PortletURLBuilder.create(
						_translationURLProvider.getExportTranslationURL(
							_themeDisplay.getScopeGroupId(),
							PortalUtil.getClassNameId(Layout.class),
							RequestBackedPortletURLFactoryUtil.create(
								httpServletRequest))
					).setRedirect(
						_themeDisplay.getURLCurrent()
					).setParameter(
						"backURLTitle",
						() -> {
							PortletDisplay portletDisplay =
								_themeDisplay.getPortletDisplay();

							return portletDisplay.getPortletDisplayName();
						}
					).buildString());
				dropdownItem.setDisabled(false);
				dropdownItem.setIcon("upload");
				dropdownItem.setLabel(
					LanguageUtil.get(
						httpServletRequest, "export-for-translations"));
				dropdownItem.setQuickAction(true);
			}
		).add(
			dropdownItem -> {
				dropdownItem.putData("action", "changePermissions");
				dropdownItem.putData(
					"changePermissionsURL",
					PermissionsURLTag.doTag(
						StringPool.BLANK, Layout.class.getName(),
						_themeDisplay.getScopeGroupId(),
						LiferayWindowState.POP_UP.toString(),
						_themeDisplay.getRequest()));
				dropdownItem.putData(
					"maxItemsToShowInfoMessage", String.valueOf(200));
				dropdownItem.setIcon("password-policies");
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "permissions"));
				dropdownItem.setMultipleTypesBulkActionDisabled(true);
				dropdownItem.setQuickAction(true);
			}
		).add(
			dropdownItem -> {
				dropdownItem.putData("action", "deleteSelectedPages");
				dropdownItem.putData(
					"deleteLayoutURL",
					PortletURLBuilder.createActionURL(
						liferayPortletResponse
					).setActionName(
						"/layout_admin/delete_layout"
					).setRedirect(
						_themeDisplay.getURLCurrent()
					).buildString());
				dropdownItem.setIcon("trash");
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "delete"));
				dropdownItem.setQuickAction(true);
			}
		).build();
	}

	@Override
	public String getClearResultsURL() {
		return PortletURLBuilder.create(
			getPortletURL()
		).setKeywords(
			StringPool.BLANK
		).buildString();
	}

	@Override
	public String getComponentId() {
		return "pagesManagementToolbar";
	}

	@Override
	public CreationMenu getCreationMenu() {
		Layout selLayout = _layoutsAdminDisplayContext.getSelLayout();
		long selPlid = _layoutsAdminDisplayContext.getSelPlid();

		return CreationMenuBuilder.addPrimaryDropdownItem(
			() ->
				_layoutsAdminDisplayContext.isShowPublicLayouts() &&
				_layoutsAdminDisplayContext.isShowAddChildPageAction(
					selLayout) &&
				(!_layoutsAdminDisplayContext.isPrivateLayout() ||
				 _layoutsAdminDisplayContext.isFirstColumn() ||
				 !_layoutsAdminDisplayContext.hasLayouts()),
			dropdownItem -> {
				dropdownItem.setHref(
					_layoutsAdminDisplayContext.
						getSelectLayoutPageTemplateEntryURL(0, selPlid, false));
				dropdownItem.setLabel(_getLabel(false));
			}
		).addPrimaryDropdownItem(
			() ->
				_layoutsAdminDisplayContext.isShowUserPrivateLayouts() &&
				((_layoutsAdminDisplayContext.isShowAddChildPageAction(
					selLayout) &&
				  _layoutsAdminDisplayContext.isPrivateLayout()) ||
				 _layoutsAdminDisplayContext.isFirstColumn() ||
				 !_layoutsAdminDisplayContext.hasLayouts()),
			dropdownItem -> {
				dropdownItem.setHref(
					_layoutsAdminDisplayContext.
						getSelectLayoutPageTemplateEntryURL(0, selPlid, true));
				dropdownItem.setLabel(_getLabel(true));
			}
		).build();
	}

	@Override
	public String getSearchActionURL() {
		return PortletURLBuilder.createRenderURL(
			liferayPortletResponse
		).setParameter(
			"privateLayout", _layoutsAdminDisplayContext.isPrivateLayout()
		).buildString();
	}

	@Override
	public String getSearchContainerId() {
		return "pages";
	}

	@Override
	public String getSearchFormName() {
		return "fm";
	}

	@Override
	public String getSortingOrder() {
		if (_layoutsAdminDisplayContext.isFirstColumn() ||
			Objects.equals(getOrderByCol(), "relevance")) {

			return null;
		}

		if (_layoutsAdminDisplayContext.isSearch()) {
			return super.getSortingOrder();
		}

		return null;
	}

	@Override
	public String getSortingURL() {
		return null;
	}

	@Override
	public Boolean isDisabled() {
		if (Objects.equals(
				_layoutsAdminDisplayContext.getDisplayStyle(),
				"miller-columns")) {

			return false;
		}

		return super.isDisabled();
	}

	@Override
	public Boolean isSelectable() {
		if (_layoutsAdminDisplayContext.isFirstColumn()) {
			return false;
		}

		return super.isSelectable();
	}

	@Override
	public Boolean isShowCreationMenu() {
		try {
			CreationMenu creationMenu = getCreationMenu();

			if (creationMenu.isEmpty()) {
				return false;
			}

			return _layoutsAdminDisplayContext.isShowAddRootLayoutButton();
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return false;
	}

	@Override
	protected String[] getOrderByKeys() {
		if (_layoutsAdminDisplayContext.isFirstColumn()) {
			return null;
		}

		if (_layoutsAdminDisplayContext.isSearch()) {
			return new String[] {"create-date", "relevance"};
		}

		return null;
	}

	private String _getLabel(boolean privateLayout) {
		Layout layout = _layoutsAdminDisplayContext.getSelLayout();

		if (layout != null) {
			return LanguageUtil.format(
				httpServletRequest, "add-child-page-of-x",
				HtmlUtil.escape(layout.getName(_themeDisplay.getLocale())));
		}

		if (_isSiteTemplate()) {
			return LanguageUtil.get(
				httpServletRequest, "add-site-template-page");
		}

		if (privateLayout) {
			return LanguageUtil.get(httpServletRequest, "private-page");
		}

		if (_layoutsAdminDisplayContext.isPrivateLayoutsEnabled()) {
			return LanguageUtil.get(httpServletRequest, "public-page");
		}

		return LanguageUtil.get(httpServletRequest, "page");
	}

	private boolean _isSiteTemplate() {
		Group group = _layoutsAdminDisplayContext.getGroup();

		if (group == null) {
			return false;
		}

		long layoutSetPrototypeClassNameId =
			ClassNameLocalServiceUtil.getClassNameId(LayoutSetPrototype.class);

		if (layoutSetPrototypeClassNameId == group.getClassNameId()) {
			return true;
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutsAdminManagementToolbarDisplayContext.class);

	private final LayoutsAdminDisplayContext _layoutsAdminDisplayContext;
	private final ThemeDisplay _themeDisplay;
	private final TranslationURLProvider _translationURLProvider;

}