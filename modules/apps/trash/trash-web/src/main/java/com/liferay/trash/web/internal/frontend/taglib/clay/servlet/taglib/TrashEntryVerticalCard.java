/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.trash.web.internal.frontend.taglib.clay.servlet.taglib;

import com.liferay.frontend.taglib.clay.servlet.taglib.BaseVerticalCard;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.trash.TrashRenderer;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.trash.model.TrashEntry;
import com.liferay.trash.web.internal.servlet.taglib.util.TrashEntryActionDropdownItemsProvider;
import com.liferay.trash.web.internal.servlet.taglib.util.TrashViewContentActionDropdownItemsProvider;

import jakarta.portlet.RenderRequest;

import java.util.Collections;
import java.util.List;

/**
 * @author Pavel Savinov
 */
public class TrashEntryVerticalCard extends BaseVerticalCard {

	public TrashEntryVerticalCard(
		TrashEntry trashEntry, TrashRenderer trashRenderer,
		LiferayPortletResponse liferayPortletResponse,
		RenderRequest renderRequest, RowChecker rowChecker,
		String viewContentURL) {

		super(trashEntry, renderRequest, rowChecker);

		_trashEntry = trashEntry;
		_trashRenderer = trashRenderer;
		_liferayPortletResponse = liferayPortletResponse;
		_viewContentURL = viewContentURL;

		_liferayPortletRequest = PortalUtil.getLiferayPortletRequest(
			renderRequest);
	}

	@Override
	public List<DropdownItem> getActionDropdownItems() {
		try {
			if (_trashEntry.getRootEntry() == null) {
				TrashEntryActionDropdownItemsProvider
					trashEntryActionDropdownItemsProvider =
						new TrashEntryActionDropdownItemsProvider(
							_liferayPortletRequest, _liferayPortletResponse,
							_trashEntry);

				return trashEntryActionDropdownItemsProvider.
					getActionDropdownItems();
			}

			TrashViewContentActionDropdownItemsProvider
				trashViewContentActionDropdownItemsProvider =
					new TrashViewContentActionDropdownItemsProvider(
						_liferayPortletRequest, _liferayPortletResponse,
						_trashRenderer.getClassName(),
						_trashRenderer.getClassPK());

			return trashViewContentActionDropdownItemsProvider.
				getActionDropdownItems();
		}
		catch (Exception exception) {
			_log.error("Unable to get trash entry actions", exception);
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
		return ResourceActionsUtil.getModelResource(
			themeDisplay.getLocale(), _trashEntry.getClassName());
	}

	@Override
	public String getTitle() {
		return _trashRenderer.getTitle(themeDisplay.getLocale());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		TrashEntryVerticalCard.class);

	private final LiferayPortletRequest _liferayPortletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private final TrashEntry _trashEntry;
	private final TrashRenderer _trashRenderer;
	private final String _viewContentURL;

}