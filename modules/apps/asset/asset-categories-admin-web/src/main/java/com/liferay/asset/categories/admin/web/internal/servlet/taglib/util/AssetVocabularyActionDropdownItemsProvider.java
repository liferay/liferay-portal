/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.categories.admin.web.internal.servlet.taglib.util;

import com.liferay.asset.categories.admin.web.constants.AssetCategoriesAdminPortletKeys;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.exportimport.kernel.staging.permission.StagingPermissionUtil;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portlet.asset.service.permission.AssetVocabularyPermission;
import com.liferay.taglib.security.PermissionsURLTag;

import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Víctor Galán
 */
public class AssetVocabularyActionDropdownItemsProvider {

	public AssetVocabularyActionDropdownItemsProvider(
		HttpServletRequest httpServletRequest, RenderResponse renderResponse) {

		_httpServletRequest = httpServletRequest;
		_renderResponse = renderResponse;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public List<DropdownItem> getActionDropdownItems(
		AssetVocabulary vocabulary) {

		return DropdownItemListBuilder.addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() -> _hasPermission(vocabulary, ActionKeys.UPDATE),
						dropdownItem -> {
							dropdownItem.setHref(
								PortletURLBuilder.createRenderURL(
									_renderResponse
								).setMVCPath(
									"/edit_asset_vocabulary.jsp"
								).setParameter(
									"vocabularyId", vocabulary.getVocabularyId()
								).buildString());
							dropdownItem.setIcon("pencil");
							dropdownItem.setLabel(
								LanguageUtil.get(_httpServletRequest, "edit"));
						}
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() -> _hasPermission(
							vocabulary, ActionKeys.PERMISSIONS),
						dropdownItem -> {
							dropdownItem.putData(
								"action", "permissionsVocabulary");
							dropdownItem.putData(
								"permissionsVocabularyURL",
								PermissionsURLTag.doTag(
									StringPool.BLANK,
									AssetVocabulary.class.getName(),
									vocabulary.getTitle(
										_themeDisplay.getLocale()),
									null,
									String.valueOf(
										vocabulary.getVocabularyId()),
									LiferayWindowState.POP_UP.toString(), null,
									_httpServletRequest));
							dropdownItem.setIcon("password-policies");
							dropdownItem.setLabel(
								LanguageUtil.get(
									_httpServletRequest, "permissions"));
						}
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() -> _hasPermission(vocabulary, ActionKeys.DELETE),
						dropdownItem -> {
							dropdownItem.putData("action", "deleteVocabulary");
							dropdownItem.putData(
								"deleteVocabularyURL",
								PortletURLBuilder.createActionURL(
									_renderResponse
								).setActionName(
									"/asset_categories_admin" +
										"/delete_asset_vocabulary"
								).setRedirect(
									_getDefaultRedirect()
								).setParameter(
									"vocabularyId", vocabulary.getVocabularyId()
								).buildString());
							dropdownItem.setIcon("trash");
							dropdownItem.setLabel(
								LanguageUtil.get(
									_httpServletRequest, "delete"));
						}
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).build();
	}

	private String _getDefaultRedirect() {
		return PortletURLBuilder.createRenderURL(
			_renderResponse
		).setMVCPath(
			"/view.jsp"
		).buildString();
	}

	private boolean _hasPermission(
		AssetVocabulary vocabulary, String actionId) {

		if (vocabulary.getGroupId() != _themeDisplay.getScopeGroupId()) {
			return false;
		}

		Boolean hasPermission = StagingPermissionUtil.hasPermission(
			_themeDisplay.getPermissionChecker(),
			_themeDisplay.getScopeGroupId(), AssetVocabulary.class.getName(),
			vocabulary.getVocabularyId(),
			AssetCategoriesAdminPortletKeys.ASSET_CATEGORIES_ADMIN, actionId);

		if (hasPermission != null) {
			return hasPermission.booleanValue();
		}

		return AssetVocabularyPermission.contains(
			_themeDisplay.getPermissionChecker(), vocabulary, actionId);
	}

	private final HttpServletRequest _httpServletRequest;
	private final RenderResponse _renderResponse;
	private final ThemeDisplay _themeDisplay;

}