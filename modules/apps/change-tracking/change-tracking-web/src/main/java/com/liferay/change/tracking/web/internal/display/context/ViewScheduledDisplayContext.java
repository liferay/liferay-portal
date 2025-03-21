/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.display.context;

import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItemListBuilder;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.util.PropsValues;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Samuel Trong Tran
 */
public class ViewScheduledDisplayContext {

	public ViewScheduledDisplayContext(
		HttpServletRequest httpServletRequest, Language language,
		RenderRequest renderRequest, RenderResponse renderResponse) {

		_httpServletRequest = httpServletRequest;
		_language = language;
		_renderResponse = renderResponse;

		_themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public String getAPIURL() {
		return "/o/change-tracking-rest/v1.0/ct-collections?status=" +
			WorkflowConstants.STATUS_SCHEDULED;
	}

	public List<FDSActionDropdownItem> getFDSActionDropdownItems() {
		return ListUtil.fromArray(
			new FDSActionDropdownItem(
				PortletURLBuilder.createRenderURL(
					_renderResponse
				).setMVCRenderCommandName(
					"/change_tracking/reschedule_publication"
				).setRedirect(
					_themeDisplay.getURLCurrent()
				).setParameter(
					"ctCollectionId", "{id}"
				).buildString(),
				"date-time", "reschedule",
				_language.get(_httpServletRequest, "reschedule"), "get",
				"schedule", null),
			new FDSActionDropdownItem(
				_language.get(
					_httpServletRequest,
					"are-you-sure-you-want-to-unschedule-this-publication"),
				PortletURLBuilder.createActionURL(
					_renderResponse
				).setActionName(
					"/change_tracking/unschedule_publication"
				).setRedirect(
					_themeDisplay.getURLCurrent()
				).setParameter(
					"ctCollectionId", "{id}"
				).buildString(),
				"time", "unschedule",
				_language.get(_httpServletRequest, "unschedule"), "post",
				"schedule", "async"),
			new FDSActionDropdownItem(
				PortletURLBuilder.createRenderURL(
					_renderResponse
				).setMVCRenderCommandName(
					"/change_tracking/view_changes"
				).setParameter(
					"ctCollectionId", "{id}"
				).buildString(),
				"list-ul", "review-changes",
				_language.get(_httpServletRequest, "review-changes"), "get",
				"get", null));
	}

	public List<NavigationItem> getViewNavigationItems() {
		return NavigationItemListBuilder.add(
			navigationItem -> {
				navigationItem.setActive(false);
				navigationItem.setHref(_renderResponse.createRenderURL());
				navigationItem.setLabel(
					_language.get(_httpServletRequest, "ongoing"));
			}
		).add(
			() -> PropsValues.SCHEDULER_ENABLED,
			navigationItem -> {
				navigationItem.setActive(true);
				navigationItem.setHref(
					_renderResponse.createRenderURL(), "mvcRenderCommandName",
					"/change_tracking/view_scheduled");
				navigationItem.setLabel(
					_language.get(_httpServletRequest, "scheduled"));
			}
		).add(
			navigationItem -> {
				navigationItem.setActive(false);
				navigationItem.setHref(
					_renderResponse.createRenderURL(), "mvcRenderCommandName",
					"/change_tracking/view_history");
				navigationItem.setLabel(
					_language.get(_httpServletRequest, "history"));
			}
		).build();
	}

	private final HttpServletRequest _httpServletRequest;
	private final Language _language;
	private final RenderResponse _renderResponse;
	private final ThemeDisplay _themeDisplay;

}