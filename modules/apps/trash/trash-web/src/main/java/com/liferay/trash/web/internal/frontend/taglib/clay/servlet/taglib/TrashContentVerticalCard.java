/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.trash.web.internal.frontend.taglib.clay.servlet.taglib;

import com.liferay.frontend.taglib.clay.servlet.taglib.VerticalCard;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ClassedModel;
import com.liferay.portal.kernel.model.TrashedModel;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.trash.TrashRenderer;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.trash.web.internal.servlet.taglib.util.TrashViewContentActionDropdownItemsProvider;

import jakarta.portlet.RenderRequest;

import java.util.Collections;
import java.util.List;

/**
 * @author Pavel Savinov
 */
public class TrashContentVerticalCard implements VerticalCard {

	public TrashContentVerticalCard(
		TrashedModel trashedModel, TrashRenderer trashRenderer,
		LiferayPortletResponse liferayPortletResponse,
		RenderRequest renderRequest, String viewContentURL) {

		_trashedModel = trashedModel;
		_trashRenderer = trashRenderer;
		_liferayPortletResponse = liferayPortletResponse;
		_viewContentURL = viewContentURL;

		_liferayPortletRequest = PortalUtil.getLiferayPortletRequest(
			renderRequest);
	}

	@Override
	public List<DropdownItem> getActionDropdownItems() {
		try {
			ClassedModel classedModel = (ClassedModel)_trashedModel;

			TrashViewContentActionDropdownItemsProvider
				trashViewContentActionDropdownItemsProvider =
					new TrashViewContentActionDropdownItemsProvider(
						_liferayPortletRequest, _liferayPortletResponse,
						classedModel.getModelClassName(),
						_trashedModel.getTrashEntryClassPK());

			return trashViewContentActionDropdownItemsProvider.
				getActionDropdownItems();
		}
		catch (Exception exception) {
			_log.error("Unable to get trashed model actions", exception);
		}

		return Collections.emptyList();
	}

	@Override
	public String getHref() {
		return _viewContentURL;
	}

	@Override
	public String getIcon() {
		try {
			return _trashRenderer.getIconCssClass();
		}
		catch (PortalException portalException) {
			_log.error(
				"Unable to get trash renderer icon css class", portalException);
		}

		return "magic";
	}

	@Override
	public String getSubtitle() {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)_liferayPortletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return ResourceActionsUtil.getModelResource(
			themeDisplay.getLocale(), _trashRenderer.getClassName());
	}

	@Override
	public String getTitle() {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)_liferayPortletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return _trashRenderer.getTitle(themeDisplay.getLocale());
	}

	@Override
	public boolean isSelectable() {
		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		TrashContentVerticalCard.class);

	private final LiferayPortletRequest _liferayPortletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private final TrashedModel _trashedModel;
	private final TrashRenderer _trashRenderer;
	private final String _viewContentURL;

}