/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.set.prototype.web.internal.frontend.taglib.clay.servlet.taglib;

import com.liferay.frontend.taglib.clay.servlet.taglib.BaseVerticalCard;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.layout.set.prototype.web.internal.servlet.taglib.util.LayoutSetPrototypeActionDropdownItemsProvider;
import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.permission.LayoutSetPrototypePermissionUtil;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class LayoutSetPrototypeVerticalCard extends BaseVerticalCard {

	public LayoutSetPrototypeVerticalCard(
		LayoutSetPrototype layoutSetPrototype, RenderRequest renderRequest,
		RenderResponse renderResponse, RowChecker rowChecker) {

		super(layoutSetPrototype, renderRequest, rowChecker);

		_layoutSetPrototype = layoutSetPrototype;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;
	}

	@Override
	public List<DropdownItem> getActionDropdownItems() {
		LayoutSetPrototypeActionDropdownItemsProvider
			layoutSetPrototypeActionDropdownItemsProvider =
				new LayoutSetPrototypeActionDropdownItemsProvider(
					_layoutSetPrototype, _renderRequest, _renderResponse);

		try {
			return layoutSetPrototypeActionDropdownItemsProvider.
				getActionDropdownItems();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return null;
	}

	@Override
	public String getHref() {
		try {
			Group group = _layoutSetPrototype.getGroup();

			if (LayoutSetPrototypePermissionUtil.contains(
					themeDisplay.getPermissionChecker(),
					_layoutSetPrototype.getLayoutSetPrototypeId(),
					ActionKeys.UPDATE) &&
				(group.getPrivateLayoutsPageCount() > 0)) {

				return group.getDisplayURL(themeDisplay, true);
			}
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return null;
	}

	@Override
	public String getIcon() {
		return "site-template";
	}

	@Override
	public String getSubtitle() {
		if (_layoutSetPrototype.isActive()) {
			return LanguageUtil.get(themeDisplay.getLocale(), "active");
		}

		return LanguageUtil.get(themeDisplay.getLocale(), "not-active");
	}

	@Override
	public String getTitle() {
		return _layoutSetPrototype.getName(themeDisplay.getLocale());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutSetPrototypeVerticalCard.class);

	private final LayoutSetPrototype _layoutSetPrototype;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;

}