/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.web.internal.frontend.taglib.clay.servlet.taglib;

import com.liferay.fragment.constants.FragmentActionKeys;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.web.internal.security.permission.resource.FragmentPermission;
import com.liferay.fragment.web.internal.servlet.taglib.util.ContributedFragmentEntryActionDropdownItemsProvider;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItemListBuilder;
import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class ContributedFragmentEntryVerticalCard
	extends BaseFragmentEntryVerticalCard {

	public ContributedFragmentEntryVerticalCard(
		FragmentEntry fragmentEntry, RenderRequest renderRequest,
		RenderResponse renderResponse, RowChecker rowChecker) {

		super(fragmentEntry, renderRequest, rowChecker);

		_renderRequest = renderRequest;
		_renderResponse = renderResponse;
	}

	@Override
	public List<DropdownItem> getActionDropdownItems() {
		ContributedFragmentEntryActionDropdownItemsProvider
			contributedFragmentEntryActionDropdownItemsProvider =
				new ContributedFragmentEntryActionDropdownItemsProvider(
					fragmentEntry, _renderRequest, _renderResponse);

		try {
			return contributedFragmentEntryActionDropdownItemsProvider.
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
			"fragmentEntryKey", fragmentEntry.getFragmentEntryKey()
		).buildString();
	}

	@Override
	public String getInputValue() {
		return fragmentEntry.getFragmentEntryKey();
	}

	@Override
	public List<LabelItem> getLabels() {
		return LabelItemListBuilder.add(
			fragmentEntry::isCacheable,
			labelItem -> {
				labelItem.setDisplayType("info");
				labelItem.setLabel(
					LanguageUtil.get(
						PortalUtil.getHttpServletRequest(_renderRequest),
						"cached"));
			}
		).build();
	}

	@Override
	public String getStickerCssClass() {
		if (fragmentEntry.isTypeComponent() || fragmentEntry.isTypeSection() ||
			fragmentEntry.isTypeReact()) {

			return "fragment-entry-basic-sticker";
		}

		if (fragmentEntry.isTypeInput()) {
			return "fragment-entry-input-sticker";
		}

		return "fragment-composition-sticker";
	}

	@Override
	public String getStickerIcon() {
		if (fragmentEntry.isTypeComponent() || fragmentEntry.isTypeSection()) {
			return "code";
		}

		if (fragmentEntry.isTypeInput()) {
			return "forms";
		}

		if (fragmentEntry.isTypeReact()) {
			return "react";
		}

		return "edit-layout";
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ContributedFragmentEntryVerticalCard.class);

	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;

}