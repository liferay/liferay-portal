/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.web.internal.frontend.taglib.clay.servlet.taglib;

import com.liferay.fragment.constants.FragmentActionKeys;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.service.FragmentEntryLinkLocalServiceUtil;
import com.liferay.fragment.web.internal.security.permission.resource.FragmentPermission;
import com.liferay.fragment.web.internal.servlet.taglib.util.BasicFragmentEntryActionDropdownItemsProvider;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItemListBuilder;
import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class BasicFragmentEntryVerticalCard
	extends BaseFragmentEntryVerticalCard {

	public BasicFragmentEntryVerticalCard(
		FragmentEntry fragmentEntry, RenderRequest renderRequest,
		RenderResponse renderResponse, RowChecker rowChecker) {

		super(fragmentEntry, renderRequest, rowChecker);

		_renderRequest = renderRequest;
		_renderResponse = renderResponse;

		_httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
	}

	@Override
	public List<DropdownItem> getActionDropdownItems() {
		BasicFragmentEntryActionDropdownItemsProvider
			basicFragmentEntryActionDropdownItemsProvider =
				new BasicFragmentEntryActionDropdownItemsProvider(
					fragmentEntry, _renderRequest, _renderResponse);

		try {
			return basicFragmentEntryActionDropdownItemsProvider.
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
				FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES) ||
			fragmentEntry.isTypeReact() || fragmentEntry.isMarketplace()) {

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

	@Override
	public String getIcon() {
		if (fragmentEntry.isMarketplace()) {
			return "marketplace";
		}

		return super.getIcon();
	}

	@Override
	public List<LabelItem> getLabels() {
		if (fragmentEntry.isApproved() &&
			(fragmentEntry.fetchDraftFragmentEntry() != null)) {

			return LabelItemListBuilder.add(
				labelItem -> labelItem.setStatus(
					WorkflowConstants.STATUS_APPROVED)
			).add(
				labelItem -> labelItem.setStatus(WorkflowConstants.STATUS_DRAFT)
			).add(
				fragmentEntry::isCacheable,
				labelItem -> {
					labelItem.setDisplayType("info");
					labelItem.setLabel(
						LanguageUtil.get(_httpServletRequest, "cached"));
				}
			).build();
		}

		return LabelItemListBuilder.add(
			labelItem -> labelItem.setStatus(fragmentEntry.getStatus())
		).add(
			fragmentEntry::isCacheable,
			labelItem -> {
				labelItem.setDisplayType("info");
				labelItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "cached"));
			}
		).build();
	}

	@Override
	public String getStickerCssClass() {
		if (fragmentEntry.isMarketplace()) {
			return "fragment-marketplace-sticker";
		}

		return super.getStickerCssClass();
	}

	@Override
	public String getStickerIcon() {
		if (fragmentEntry.isMarketplace()) {
			return "marketplace";
		}

		return super.getStickerIcon();
	}

	@Override
	public String getSubtitle() {
		return LanguageUtil.format(
			_httpServletRequest, "x-usages",
			FragmentEntryLinkLocalServiceUtil.
				getFragmentEntryLinksCountByFragmentEntryERC(
					fragmentEntry.getExternalReferenceCode(),
					fragmentEntry.getScopeERC(), false));
	}

	@Override
	public boolean isSelectable() {
		if (fragmentEntry.isTypeReact()) {
			return false;
		}

		return super.isSelectable();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BasicFragmentEntryVerticalCard.class);

	private final HttpServletRequest _httpServletRequest;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;

}