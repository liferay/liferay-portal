/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.frontend.taglib.clay.servlet.taglib;

import com.liferay.frontend.taglib.clay.servlet.taglib.BaseBaseClayCard;
import com.liferay.frontend.taglib.clay.servlet.taglib.VerticalCard;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItemListBuilder;
import com.liferay.layout.constants.LayoutTypeSettingsConstants;
import com.liferay.layout.page.template.admin.web.internal.security.permission.resource.LayoutPageTemplateEntryPermission;
import com.liferay.layout.page.template.admin.web.internal.servlet.taglib.util.MasterLayoutActionDropdownItemsProvider;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryServiceUtil;
import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class MasterLayoutVerticalCard
	extends BaseBaseClayCard implements VerticalCard {

	public MasterLayoutVerticalCard(
		BaseModel<?> baseModel, RenderRequest renderRequest,
		RenderResponse renderResponse, RowChecker rowChecker) {

		super(baseModel, rowChecker);

		_renderRequest = renderRequest;
		_renderResponse = renderResponse;

		_layoutPageTemplateEntry = (LayoutPageTemplateEntry)baseModel;

		_httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
		_themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	@Override
	public List<DropdownItem> getActionDropdownItems() {
		try {
			MasterLayoutActionDropdownItemsProvider
				masterLayoutActionDropdownItemsProvider =
					new MasterLayoutActionDropdownItemsProvider(
						_layoutPageTemplateEntry, _renderRequest,
						_renderResponse);

			List<DropdownItem> actionDropdownItems =
				masterLayoutActionDropdownItemsProvider.
					getActionDropdownItems();

			if (!actionDropdownItems.isEmpty()) {
				return actionDropdownItems;
			}

			return null;
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
			if (!LayoutPageTemplateEntryPermission.contains(
					_themeDisplay.getPermissionChecker(),
					_layoutPageTemplateEntry, ActionKeys.UPDATE)) {

				return null;
			}

			Layout layout = LayoutLocalServiceUtil.getLayout(
				_layoutPageTemplateEntry.getPlid());
			PortletDisplay portletDisplay = _themeDisplay.getPortletDisplay();

			return HttpComponentsUtil.addParameters(
				PortalUtil.getLayoutFullURL(
					layout.fetchDraftLayout(), _themeDisplay),
				"p_l_back_url", _themeDisplay.getURLCurrent(),
				"p_l_back_url_title", portletDisplay.getPortletDisplayName(),
				"p_l_mode", Constants.EDIT);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return null;
	}

	@Override
	public String getIcon() {
		return "page";
	}

	@Override
	public String getImageSrc() {
		return _layoutPageTemplateEntry.getImagePreviewURL(_themeDisplay);
	}

	@Override
	public List<LabelItem> getLabels() {
		if (_isBlankMasterLayout()) {
			return LabelItemListBuilder.add(
				labelItem -> labelItem.setStatus(
					WorkflowConstants.STATUS_APPROVED)
			).build();
		}

		Layout layout = LayoutLocalServiceUtil.fetchLayout(
			_layoutPageTemplateEntry.getPlid());

		if (layout == null) {
			return Collections.emptyList();
		}

		Layout draftLayout = layout.fetchDraftLayout();

		if (draftLayout == null) {
			return Collections.emptyList();
		}

		if (!GetterUtil.getBoolean(
				draftLayout.getTypeSettingsProperty(
					LayoutTypeSettingsConstants.KEY_PUBLISHED))) {

			return LabelItemListBuilder.add(
				labelItem -> labelItem.setStatus(WorkflowConstants.STATUS_DRAFT)
			).build();
		}

		return LabelItemListBuilder.add(
			labelItem -> labelItem.setStatus(draftLayout.getStatus())
		).build();
	}

	@Override
	public String getStickerIcon() {
		if (_layoutPageTemplateEntry.getLayoutPageTemplateEntryId() <= 0) {
			LayoutPageTemplateEntry defaultLayoutPageTemplateEntry =
				LayoutPageTemplateEntryServiceUtil.
					fetchDefaultLayoutPageTemplateEntry(
						_themeDisplay.getScopeGroupId(),
						LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT,
						WorkflowConstants.STATUS_APPROVED);

			if (defaultLayoutPageTemplateEntry == null) {
				return "check-circle";
			}
		}

		if (_layoutPageTemplateEntry.isDefaultTemplate()) {
			return "check-circle";
		}

		return null;
	}

	@Override
	public String getStickerStyle() {
		return "primary";
	}

	@Override
	public String getSubtitle() {
		int layoutsCount = LayoutLocalServiceUtil.getMasterLayoutsCount(
			_themeDisplay.getScopeGroupId(),
			_layoutPageTemplateEntry.getPlid());

		return LanguageUtil.format(
			_httpServletRequest, "x-usages", layoutsCount);
	}

	@Override
	public String getTitle() {
		return _layoutPageTemplateEntry.getName();
	}

	@Override
	public boolean isSelectable() {
		if (_layoutPageTemplateEntry.getLayoutPageTemplateEntryId() > 0) {
			return true;
		}

		return false;
	}

	private boolean _isBlankMasterLayout() {
		if (_layoutPageTemplateEntry.getLayoutPageTemplateEntryId() == 0L) {
			return true;
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MasterLayoutVerticalCard.class);

	private final HttpServletRequest _httpServletRequest;
	private final LayoutPageTemplateEntry _layoutPageTemplateEntry;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private final ThemeDisplay _themeDisplay;

}