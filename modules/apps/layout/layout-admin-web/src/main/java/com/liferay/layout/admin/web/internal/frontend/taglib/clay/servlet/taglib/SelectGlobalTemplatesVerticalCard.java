/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.frontend.taglib.clay.servlet.taglib;

import com.liferay.frontend.taglib.clay.servlet.taglib.VerticalCard;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Eudaldo Alonso
 */
public class SelectGlobalTemplatesVerticalCard implements VerticalCard {

	public SelectGlobalTemplatesVerticalCard(
		LayoutPageTemplateEntry layoutPageTemplateEntry,
		RenderRequest renderRequest, RenderResponse renderResponse) {

		_layoutPageTemplateEntry = layoutPageTemplateEntry;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;
	}

	@Override
	public String getCssClass() {
		return "add-layout-action-option card-interactive " +
			"card-interactive-primary";
	}

	@Override
	public Map<String, String> getDynamicAttributes() {
		Map<String, String> data = new HashMap<>();

		try {
			data.put("data-add-layout-url", _getAddLayoutURL());
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		data.put("role", "button");
		data.put("tabIndex", "0");

		return data;
	}

	@Override
	public String getIcon() {
		return "page-template";
	}

	@Override
	public String getTitle() {
		Layout layout = LayoutLocalServiceUtil.fetchLayout(
			_layoutPageTemplateEntry.getPlid());

		if (layout == null) {
			return HtmlUtil.escape(_layoutPageTemplateEntry.getName());
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)_renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		return HtmlUtil.escape(layout.getName(themeDisplay.getLocale()));
	}

	@Override
	public boolean isSelectable() {
		return false;
	}

	private String _getAddLayoutURL() {
		long selPlid = ParamUtil.getLong(_renderRequest, "selPlid");

		PortletURL addLayoutURL = PortletURLBuilder.createRenderURL(
			_renderResponse
		).setMVCRenderCommandName(
			"/layout_admin/add_layout"
		).setBackURL(
			ParamUtil.getString(_renderRequest, "redirect")
		).setParameter(
			"layoutPageTemplateEntryId",
			_layoutPageTemplateEntry.getLayoutPageTemplateEntryId()
		).setParameter(
			"layoutPrototypeId", _layoutPageTemplateEntry.getLayoutPrototypeId()
		).setParameter(
			"privateLayout",
			ParamUtil.getBoolean(_renderRequest, "privateLayout")
		).setParameter(
			"selPlid", selPlid
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildPortletURL();

		if (selPlid != LayoutConstants.DEFAULT_PLID) {
			Layout layout = LayoutLocalServiceUtil.fetchLayout(selPlid);

			if ((layout != null) && layout.isTypeEmpty()) {
				addLayoutURL.setParameter(
					"emptyLayout",
					String.valueOf(
						ParamUtil.getBoolean(_renderRequest, "emptyLayout")));
				addLayoutURL.setParameter(
					"externalReferenceCode", layout.getExternalReferenceCode());
			}
		}

		return addLayoutURL.toString();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SelectGlobalTemplatesVerticalCard.class);

	private final LayoutPageTemplateEntry _layoutPageTemplateEntry;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;

}