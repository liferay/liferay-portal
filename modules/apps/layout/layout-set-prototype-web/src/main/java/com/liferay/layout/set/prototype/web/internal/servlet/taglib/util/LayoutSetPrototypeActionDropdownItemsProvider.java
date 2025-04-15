/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.set.prototype.web.internal.servlet.taglib.util;

import com.liferay.application.list.constants.ApplicationListWebKeys;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.application.list.display.context.logic.PanelCategoryHelper;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemList;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.permission.LayoutSetPrototypePermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.security.PermissionsURLTag;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class LayoutSetPrototypeActionDropdownItemsProvider {

	public LayoutSetPrototypeActionDropdownItemsProvider(
		LayoutSetPrototype layoutSetPrototype, RenderRequest renderRequest,
		RenderResponse renderResponse) {

		_layoutSetPrototype = layoutSetPrototype;
		_renderResponse = renderResponse;

		_httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);

		_themeDisplay = (ThemeDisplay)_httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public List<DropdownItem> getActionDropdownItems() throws Exception {
		boolean hasUpdatePermission = LayoutSetPrototypePermissionUtil.contains(
			_themeDisplay.getPermissionChecker(),
			_layoutSetPrototype.getLayoutSetPrototypeId(), ActionKeys.UPDATE);

		return new DropdownItemList() {
			{
				if (hasUpdatePermission) {
					Group group = _layoutSetPrototype.getGroup();

					PortletURL siteAdministrationURL =
						_getSiteAdministrationURL(group);

					if (siteAdministrationURL != null) {
						add(
							dropdownItem -> {
								dropdownItem.setHref(
									siteAdministrationURL.toString());
								dropdownItem.setLabel(
									LanguageUtil.get(
										_httpServletRequest, "manage"));
							});
					}

					if (_layoutSetPrototype.isActive() && !group.isGuest()) {
						add(_getDeactivateActionUnsafeConsumer());
					}
					else if (!_layoutSetPrototype.isActive()) {
						add(_getActivateActionUnsafeConsumer());
					}

					boolean readyForPropagation = GetterUtil.getBoolean(
						_layoutSetPrototype.getSettingsProperty(
							"readyForPropagation"),
						true);

					if (readyForPropagation && !group.isGuest()) {
						add(_getDisablePropagationActionUnsafeConsumer());
					}
					else if (!readyForPropagation) {
						add(_getReadyForPropagationActionUnsafeConsumer());
					}
				}

				if (LayoutSetPrototypePermissionUtil.contains(
						_themeDisplay.getPermissionChecker(),
						_layoutSetPrototype.getLayoutSetPrototypeId(),
						ActionKeys.PERMISSIONS)) {

					add(_getPermissionsActionUnsafeConsumer());
				}

				if (LayoutSetPrototypePermissionUtil.contains(
						_themeDisplay.getPermissionChecker(),
						_layoutSetPrototype.getLayoutSetPrototypeId(),
						ActionKeys.DELETE)) {

					add(_getDeleteActionUnsafeConsumer());
				}
			}
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getActivateActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.putData("action", "activate");
			dropdownItem.putData(
				"activateURL",
				PortletURLBuilder.createActionURL(
					_renderResponse
				).setActionName(
					"updateLayoutSetPrototypeAction"
				).setRedirect(
					_themeDisplay.getURLCurrent()
				).setParameter(
					"active", true
				).setParameter(
					"layoutSetPrototypeId",
					_layoutSetPrototype.getLayoutSetPrototypeId()
				).buildString());
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "activate"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getDeactivateActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.putData("action", "deactivate");
			dropdownItem.putData(
				"deactivateURL",
				PortletURLBuilder.createActionURL(
					_renderResponse
				).setActionName(
					"updateLayoutSetPrototypeAction"
				).setRedirect(
					_themeDisplay.getURLCurrent()
				).setParameter(
					"active", false
				).setParameter(
					"layoutSetPrototypeId",
					_layoutSetPrototype.getLayoutSetPrototypeId()
				).buildString());
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "deactivate"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getDeleteActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.putData("action", "delete");
			dropdownItem.putData(
				"deleteURL",
				PortletURLBuilder.createActionURL(
					_renderResponse
				).setActionName(
					"deleteLayoutSetPrototypes"
				).setRedirect(
					_themeDisplay.getURLCurrent()
				).setParameter(
					"layoutSetPrototypeId",
					_layoutSetPrototype.getLayoutSetPrototypeId()
				).buildString());
			dropdownItem.setIcon("trash");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "delete"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getDisablePropagationActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.putData("action", "disablePropagation");
			dropdownItem.putData(
				"disablePropagationURL",
				PortletURLBuilder.createActionURL(
					_renderResponse
				).setActionName(
					"updateLayoutSetPrototypeAction"
				).setRedirect(
					_themeDisplay.getURLCurrent()
				).setParameter(
					"layoutSetPrototypeId",
					_layoutSetPrototype.getLayoutSetPrototypeId()
				).setParameter(
					"readyForPropagation", false
				).buildString());
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "disable-propagation"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
			_getPermissionsActionUnsafeConsumer()
		throws Exception {

		String permissionsURL = PermissionsURLTag.doTag(
			StringPool.BLANK, LayoutSetPrototype.class.getName(),
			HtmlUtil.escape(
				_layoutSetPrototype.getName(_themeDisplay.getLocale())),
			null, String.valueOf(_layoutSetPrototype.getLayoutSetPrototypeId()),
			LiferayWindowState.POP_UP.toString(), null, _httpServletRequest);

		return dropdownItem -> {
			dropdownItem.putData("action", "permissions");
			dropdownItem.putData("permissionsURL", permissionsURL);
			dropdownItem.setIcon("password-policies");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "permissions"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getReadyForPropagationActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.putData("action", "readyForPropagation");
			dropdownItem.putData(
				"readyForPropagationURL",
				PortletURLBuilder.createActionURL(
					_renderResponse
				).setActionName(
					"updateLayoutSetPrototypeAction"
				).setRedirect(
					_themeDisplay.getURLCurrent()
				).setParameter(
					"layoutSetPrototypeId",
					_layoutSetPrototype.getLayoutSetPrototypeId()
				).setParameter(
					"readyForPropagation", true
				).buildString());
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "ready-for-propagation"));
		};
	}

	private PortletURL _getSiteAdministrationURL(Group group) {
		PanelCategoryHelper panelCategoryHelper =
			(PanelCategoryHelper)_httpServletRequest.getAttribute(
				ApplicationListWebKeys.PANEL_CATEGORY_HELPER);

		String portletId = panelCategoryHelper.getFirstPortletId(
			PanelCategoryKeys.SITE_ADMINISTRATION,
			_themeDisplay.getPermissionChecker(), group);

		if (Validator.isNull(portletId)) {
			return null;
		}

		return PortletURLBuilder.create(
			PortalUtil.getControlPanelPortletURL(
				_httpServletRequest, group, portletId, 0, 0,
				PortletRequest.RENDER_PHASE)
		).setRedirect(
			_themeDisplay.getURLCurrent()
		).buildPortletURL();
	}

	private final HttpServletRequest _httpServletRequest;
	private final LayoutSetPrototype _layoutSetPrototype;
	private final RenderResponse _renderResponse;
	private final ThemeDisplay _themeDisplay;

}