/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR
 * LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.display.context;

import com.liferay.change.tracking.web.internal.frontend.data.set.filter.CTCollectionStatusSelectionFDSFilter;
import com.liferay.change.tracking.web.internal.frontend.data.set.filter.ChangeTypeSelectionFDSFilter;
import com.liferay.frontend.data.set.filter.FDSFilter;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Cheryl Tang
 */
public class ViewTimelineHistoryDisplayContext {

	public ViewTimelineHistoryDisplayContext(
		HttpServletRequest httpServletRequest, Language language,
		RenderRequest renderRequest, RenderResponse renderResponse) {

		_httpServletRequest = httpServletRequest;
		_language = language;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;

		_themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public String getAPIURL() {
		long classNameId = ParamUtil.getLong(_renderRequest, "classNameId");
		long classPK = ParamUtil.getLong(_renderRequest, "classPK");

		StringBundler sb = new StringBundler(4);

		sb.append("?classNameId=");
		sb.append(classNameId);

		if (classPK != 0) {
			sb.append("&classPK=");
			sb.append(classPK);
		}

		return "/o/change-tracking-rest/v1.0/ct-entries/history" + sb;
	}

	public List<FDSActionDropdownItem> getFDSActionDropdownItems() {
		return ListUtil.fromArray(
			new FDSActionDropdownItem(
				PortletURLBuilder.createRenderURL(
					_renderResponse
				).setMVCRenderCommandName(
					"/change_tracking/view_discard"
				).setRedirect(
					_themeDisplay.getURLCurrent()
				).setParameter(
					"ctCollectionId", "{ctCollectionId}"
				).setParameter(
					"modelClassNameId", "{modelClassNameId}"
				).setParameter(
					"modelClassPK", "{modelClassPK}"
				).buildString(),
				"times-circle", "view-discard",
				_language.get(_httpServletRequest, "discard"), "get",
				"view-discard", null),
			new FDSActionDropdownItem(
				StringBundler.concat(
					"javascript:window.open('",
					PortletURLBuilder.createActionURL(
						_renderResponse
					).setActionName(
						"/change_tracking/checkout_ct_collection"
					).setRedirect(
						ParamUtil.getString(_renderRequest, "redirect")
					).setParameter(
						"ctCollectionId", "{ctCollectionId}"
					).buildString(),
					"', '_top');"),
				"pencil", "edit",
				_language.format(
					_httpServletRequest, "edit-in-x",
					_language.get(_httpServletRequest, "publication")),
				"post", "checkout", null),
			new FDSActionDropdownItem(
				PortletURLBuilder.createRenderURL(
					_renderResponse
				).setMVCRenderCommandName(
					"/change_tracking/view_move_changes"
				).setRedirect(
					_themeDisplay.getURLCurrent()
				).setParameter(
					"ctCollectionId", "{ctCollectionId}"
				).setParameter(
					"modelClassNameId", "{modelClassNameId}"
				).setParameter(
					"modelClassPK", "{modelClassPK}"
				).buildString(),
				"move-folder", "move-changes",
				_language.get(_httpServletRequest, "move-changes"), "post",
				"move-changes", null),
			new FDSActionDropdownItem(
				PortletURLBuilder.createRenderURL(
					_renderResponse
				).setMVCRenderCommandName(
					"/change_tracking/view_change"
				).setRedirect(
					_themeDisplay.getURLCurrent()
				).setParameter(
					"ctCollectionId", "{ctCollectionId}"
				).setParameter(
					"modelClassNameId", "{modelClassNameId}"
				).setParameter(
					"modelClassPK", "{modelClassPK}"
				).buildString(),
				"list-ul", "view-change",
				_language.get(_httpServletRequest, "review-change"), "get",
				"get", null));
	}

	public List<FDSFilter> getFDSFilters() {
		return ListUtil.fromArray(
			new ChangeTypeSelectionFDSFilter(),
			new CTCollectionStatusSelectionFDSFilter());
	}

	private final HttpServletRequest _httpServletRequest;
	private final Language _language;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private final ThemeDisplay _themeDisplay;

}