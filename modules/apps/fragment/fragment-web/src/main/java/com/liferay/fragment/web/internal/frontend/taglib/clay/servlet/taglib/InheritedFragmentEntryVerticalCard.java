/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.web.internal.frontend.taglib.clay.servlet.taglib;

import com.liferay.fragment.constants.FragmentActionKeys;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.web.internal.security.permission.resource.FragmentPermission;
import com.liferay.fragment.web.internal.servlet.taglib.util.InheritedFragmentEntryActionDropdownItemsProvider;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.util.List;

/**
 * @author Jürgen Kappler
 */
public class InheritedFragmentEntryVerticalCard
	extends BaseFragmentEntryVerticalCard {

	public InheritedFragmentEntryVerticalCard(
		FragmentEntry fragmentEntry, RenderRequest renderRequest,
		RenderResponse renderResponse, RowChecker rowChecker) {

		super(fragmentEntry, renderRequest, rowChecker);

		_renderRequest = renderRequest;
		_renderResponse = renderResponse;
	}

	@Override
	public List<DropdownItem> getActionDropdownItems() {
		InheritedFragmentEntryActionDropdownItemsProvider
			inheritedFragmentEntryActionDropdownItemsProvider =
				new InheritedFragmentEntryActionDropdownItemsProvider(
					fragmentEntry, _renderRequest, _renderResponse);

		try {
			return inheritedFragmentEntryActionDropdownItemsProvider.
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
		if (!FragmentPermission.contains(
				themeDisplay.getPermissionChecker(),
				themeDisplay.getScopeGroupId(),
				FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES)) {

			return null;
		}

		return PortletURLBuilder.createRenderURL(
			_renderResponse
		).setMVCRenderCommandName(
			"/fragment/edit_fragment_entry"
		).setRedirect(
			themeDisplay.getURLCurrent()
		).setParameter(
			"fragmentCollectionId", fragmentEntry.getFragmentCollectionId()
		).setParameter(
			"fragmentEntryId", fragmentEntry.getFragmentEntryId()
		).buildString();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		InheritedFragmentEntryVerticalCard.class);

	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;

}