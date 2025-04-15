/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.frontend.taglib.clay.servlet.taglib;

import com.liferay.frontend.taglib.clay.servlet.taglib.BaseHorizontalCard;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.layout.page.template.admin.web.internal.servlet.taglib.util.LayoutPageTemplateCollectionActionDropdownItem;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Yurena Cabrera
 */
public class DisplayPageTemplateCollectionHorizontalCard
	extends BaseHorizontalCard {

	public DisplayPageTemplateCollectionHorizontalCard(
		BaseModel<?> baseModel, RenderRequest renderRequest,
		RenderResponse renderResponse, RowChecker rowChecker) {

		super(baseModel, renderRequest, rowChecker);

		_renderResponse = renderResponse;

		_layoutPageTemplateCollection = (LayoutPageTemplateCollection)baseModel;
	}

	@Override
	public List<DropdownItem> getActionDropdownItems() {
		HttpServletRequest httpServletRequest =
			PortalUtil.getHttpServletRequest(renderRequest);

		LayoutPageTemplateCollectionActionDropdownItem
			layoutPageTemplateCollectionActionDropdownItem =
				new LayoutPageTemplateCollectionActionDropdownItem(
					httpServletRequest, _layoutPageTemplateCollection,
					_renderResponse, "display-page-templates");

		try {
			return layoutPageTemplateCollectionActionDropdownItem.
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
		return PortletURLBuilder.createRenderURL(
			_renderResponse
		).setTabs1(
			"display-page-templates"
		).setParameter(
			"groupId", _layoutPageTemplateCollection.getGroupId()
		).setParameter(
			"layoutPageTemplateCollectionId",
			_layoutPageTemplateCollection.getLayoutPageTemplateCollectionId()
		).buildString();
	}

	@Override
	public String getIcon() {
		return "folder";
	}

	@Override
	public String getInputName() {
		return rowChecker.getRowIds() +
			LayoutPageTemplateCollection.class.getSimpleName();
	}

	@Override
	public String getTitle() {
		return _layoutPageTemplateCollection.getName();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DisplayPageTemplateCollectionHorizontalCard.class);

	private final LayoutPageTemplateCollection _layoutPageTemplateCollection;
	private final RenderResponse _renderResponse;

}