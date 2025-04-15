/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.frontend.taglib.clay.servlet.taglib;

import com.liferay.frontend.taglib.clay.servlet.taglib.BaseVerticalCard;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItemListBuilder;
import com.liferay.layout.constants.LayoutTypeSettingsConstants;
import com.liferay.layout.page.template.admin.web.internal.security.permission.resource.LayoutPageTemplateEntryPermission;
import com.liferay.layout.page.template.admin.web.internal.servlet.taglib.util.LayoutPageTemplateEntryActionDropdownItemsProvider;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutPrototype;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutPrototypeServiceUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Objects;

/**
 * @author Eudaldo Alonso
 */
public class LayoutPageTemplateEntryVerticalCard extends BaseVerticalCard {

	public LayoutPageTemplateEntryVerticalCard(
		BaseModel<?> baseModel, RenderRequest renderRequest,
		RenderResponse renderResponse, RowChecker rowChecker) {

		super(baseModel, renderRequest, rowChecker);

		_renderResponse = renderResponse;

		_layoutPageTemplateEntry = (LayoutPageTemplateEntry)baseModel;
		_httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
	}

	@Override
	public List<DropdownItem> getActionDropdownItems() {
		LayoutPageTemplateEntryActionDropdownItemsProvider
			layoutPageTemplateEntryActionDropdownItemsProvider =
				new LayoutPageTemplateEntryActionDropdownItemsProvider(
					_layoutPageTemplateEntry, renderRequest, _renderResponse);

		try {
			return layoutPageTemplateEntryActionDropdownItemsProvider.
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
			if (!LayoutPageTemplateEntryPermission.contains(
					themeDisplay.getPermissionChecker(),
					_layoutPageTemplateEntry, ActionKeys.UPDATE)) {

				return null;
			}

			PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

			if (Objects.equals(
					_layoutPageTemplateEntry.getType(),
					LayoutPageTemplateEntryTypeConstants.WIDGET_PAGE)) {

				LayoutPrototype layoutPrototype =
					LayoutPrototypeServiceUtil.fetchLayoutPrototype(
						_layoutPageTemplateEntry.getLayoutPrototypeId());

				if (layoutPrototype == null) {
					return null;
				}

				Group layoutPrototypeGroup = layoutPrototype.getGroup();

				return HttpComponentsUtil.addParameters(
					layoutPrototypeGroup.getDisplayURL(themeDisplay, true),
					"p_l_back_url", themeDisplay.getURLCurrent(),
					"p_l_back_url_title",
					portletDisplay.getPortletDisplayName());
			}

			return HttpComponentsUtil.addParameters(
				PortalUtil.getLayoutFullURL(
					LayoutLocalServiceUtil.fetchDraftLayout(
						_layoutPageTemplateEntry.getPlid()),
					themeDisplay),
				"p_l_back_url", themeDisplay.getURLCurrent(),
				"p_l_back_url_title", portletDisplay.getTitle(), "p_l_mode",
				Constants.EDIT);
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
		if (Objects.equals(
				_layoutPageTemplateEntry.getType(),
				LayoutPageTemplateEntryTypeConstants.WIDGET_PAGE)) {

			return "page-template";
		}

		return "page";
	}

	@Override
	public String getImageSrc() {
		return _layoutPageTemplateEntry.getImagePreviewURL(themeDisplay);
	}

	@Override
	public List<LabelItem> getLabels() {
		if (Objects.equals(
				_layoutPageTemplateEntry.getType(),
				LayoutPageTemplateEntryTypeConstants.WIDGET_PAGE)) {

			return super.getLabels();
		}

		Layout draftLayout = LayoutLocalServiceUtil.fetchDraftLayout(
			_layoutPageTemplateEntry.getPlid());

		if (draftLayout == null) {
			return super.getLabels();
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
	public String getSubtitle() {
		if (Objects.equals(
				_layoutPageTemplateEntry.getType(),
				LayoutPageTemplateEntryTypeConstants.WIDGET_PAGE)) {

			return LanguageUtil.get(
				_httpServletRequest, "widget-page-template");
		}

		return LanguageUtil.get(_httpServletRequest, "content-page-template");
	}

	@Override
	public String getTitle() {
		return _layoutPageTemplateEntry.getName();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutPageTemplateEntryVerticalCard.class);

	private final HttpServletRequest _httpServletRequest;
	private final LayoutPageTemplateEntry _layoutPageTemplateEntry;
	private final RenderResponse _renderResponse;

}