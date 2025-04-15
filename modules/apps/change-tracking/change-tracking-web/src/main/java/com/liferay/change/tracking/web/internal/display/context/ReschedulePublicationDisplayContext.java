/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.display.context;

import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.scheduler.ScheduledPublishInfo;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;

/**
 * @author Samuel Trong Tran
 */
public class ReschedulePublicationDisplayContext {

	public ReschedulePublicationDisplayContext(
		CTCollection ctCollection, Language language, Portal portal,
		RenderRequest renderRequest, RenderResponse renderResponse,
		ScheduledPublishInfo scheduledPublishInfo) {

		_ctCollection = ctCollection;
		_language = language;
		_portal = portal;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;
		_scheduledPublishInfo = scheduledPublishInfo;

		_httpServletRequest = portal.getHttpServletRequest(renderRequest);
		_themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public Map<String, Object> getReactData() {
		Calendar calendar = CalendarFactoryUtil.getCalendar(
			_themeDisplay.getTimeZone(), _themeDisplay.getLocale());

		calendar.setTime(_scheduledPublishInfo.getStartDate());

		return HashMapBuilder.<String, Object>put(
			"redirect", getRedirect()
		).put(
			"rescheduleURL",
			() -> PortletURLBuilder.createActionURL(
				_renderResponse
			).setActionName(
				"/change_tracking/reschedule_publication"
			).setRedirect(
				getRedirect()
			).setParameter(
				"ctCollectionId", _ctCollection.getCtCollectionId()
			).buildString()
		).put(
			"scheduledDate",
			StringBundler.concat(
				calendar.get(Calendar.YEAR), StringPool.DASH,
				String.format("%02d", calendar.get(Calendar.MONTH) + 1),
				StringPool.DASH,
				String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH)))
		).put(
			"scheduledTime",
			String.format(
				"%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY),
				calendar.get(Calendar.MINUTE))
		).put(
			"spritemap", _themeDisplay.getPathThemeSpritemap()
		).put(
			"timeZone",
			() -> {
				TimeZone timeZone = _themeDisplay.getTimeZone();

				return timeZone.getID();
			}
		).put(
			"unscheduleURL",
			() -> PortletURLBuilder.createActionURL(
				_renderResponse
			).setActionName(
				"/change_tracking/unschedule_publication"
			).setRedirect(
				getRedirect()
			).setParameter(
				"ctCollectionId", _ctCollection.getCtCollectionId()
			).buildString()
		).build();
	}

	public String getRedirect() {
		String redirect = ParamUtil.getString(_renderRequest, "redirect");

		if (Validator.isNotNull(redirect)) {
			return redirect;
		}

		return PortletURLBuilder.createRenderURL(
			_renderResponse
		).setMVCRenderCommandName(
			"/change_tracking/view_scheduled"
		).buildString();
	}

	public String getTitle() {
		return StringBundler.concat(
			_language.get(_httpServletRequest, "reschedule"), ": ",
			_ctCollection.getName());
	}

	private final CTCollection _ctCollection;
	private final HttpServletRequest _httpServletRequest;
	private final Language _language;
	private final Portal _portal;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private final ScheduledPublishInfo _scheduledPublishInfo;
	private final ThemeDisplay _themeDisplay;

}