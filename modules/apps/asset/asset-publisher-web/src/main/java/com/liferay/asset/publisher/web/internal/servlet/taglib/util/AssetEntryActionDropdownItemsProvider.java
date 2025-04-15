/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.publisher.web.internal.servlet.taglib.util;

import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.publisher.action.AssetEntryAction;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemList;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.RenderLayoutContentThreadLocal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class AssetEntryActionDropdownItemsProvider {

	public AssetEntryActionDropdownItemsProvider(
		AssetRenderer<?> assetRenderer,
		List<AssetEntryAction<?>> assetEntryActions, String fullContentRedirect,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		_assetRenderer = assetRenderer;
		_assetEntryActions = assetEntryActions;
		_fullContentRedirect = fullContentRedirect;
		_liferayPortletRequest = liferayPortletRequest;
		_liferayPortletResponse = liferayPortletResponse;

		_httpServletRequest = PortalUtil.getHttpServletRequest(
			liferayPortletRequest);

		_themeDisplay = (ThemeDisplay)_httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public List<DropdownItem> getActionDropdownItems() {
		if (RenderLayoutContentThreadLocal.isRenderLayoutContent()) {
			return Collections.emptyList();
		}

		return new DropdownItemList() {
			{
				PortletURL editAssetEntryURL = _getEditAssetEntryURL();

				if (editAssetEntryURL != null) {
					add(
						dropdownItem -> {
							dropdownItem.setHref(editAssetEntryURL.toString());
							dropdownItem.setIcon("pencil");
							dropdownItem.setLabel(
								LanguageUtil.get(_httpServletRequest, "edit"));
						});
				}

				if (ListUtil.isNotEmpty(_assetEntryActions)) {
					for (AssetEntryAction<?> assetEntryAction :
							_assetEntryActions) {

						AssetEntryAction<Object> objectAssetEntryAction =
							(AssetEntryAction<Object>)assetEntryAction;

						try {
							if (!objectAssetEntryAction.hasPermission(
									_themeDisplay.getPermissionChecker(),
									(AssetRenderer<Object>)_assetRenderer)) {

								continue;
							}
						}
						catch (Exception exception) {
							if (_log.isDebugEnabled()) {
								_log.debug(exception);
							}

							continue;
						}

						String title = objectAssetEntryAction.getMessage(
							_themeDisplay.getLocale());

						add(
							dropdownItem -> {
								dropdownItem.putData(
									"action", "assetEntryAction");
								dropdownItem.putData(
									"assetEntryActionTitle", title);
								dropdownItem.putData(
									"assetEntryActionURL",
									objectAssetEntryAction.getDialogURL(
										_httpServletRequest,
										(AssetRenderer<Object>)_assetRenderer));
								dropdownItem.putData(
									"useDialog", Boolean.TRUE.toString());
								dropdownItem.setIcon(
									objectAssetEntryAction.getIcon());
								dropdownItem.setLabel(title);
							});
					}
				}
			}
		};
	}

	private PortletURL _getEditAssetEntryURL() {
		boolean showEditURL = ParamUtil.getBoolean(
			_httpServletRequest, "showEditURL", true);

		if (!showEditURL) {
			return null;
		}

		try {
			if (!_assetRenderer.hasEditPermission(
					_themeDisplay.getPermissionChecker())) {

				return null;
			}

			String redirect = _themeDisplay.getURLCurrent();

			if (Validator.isNotNull(_fullContentRedirect)) {
				redirect = _fullContentRedirect;
			}

			return PortletURLBuilder.create(
				_assetRenderer.getURLEdit(
					_liferayPortletRequest, _liferayPortletResponse,
					LiferayWindowState.NORMAL, redirect)
			).setPortletResource(
				() -> {
					PortletDisplay portletDisplay =
						_themeDisplay.getPortletDisplay();

					return portletDisplay.getPortletName();
				}
			).buildPortletURL();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AssetEntryActionDropdownItemsProvider.class);

	private final List<AssetEntryAction<?>> _assetEntryActions;
	private final AssetRenderer<?> _assetRenderer;
	private final String _fullContentRedirect;
	private final HttpServletRequest _httpServletRequest;
	private final LiferayPortletRequest _liferayPortletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private final ThemeDisplay _themeDisplay;

}