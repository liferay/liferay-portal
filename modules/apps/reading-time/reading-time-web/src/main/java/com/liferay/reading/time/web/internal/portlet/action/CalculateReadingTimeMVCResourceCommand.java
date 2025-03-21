/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.reading.time.web.internal.portlet.action;

import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.reading.time.calculator.ReadingTimeCalculator;
import com.liferay.reading.time.message.ReadingTimeMessageProvider;
import com.liferay.reading.time.web.internal.constants.ReadingTimePortletKeys;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.time.Duration;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ReadingTimePortletKeys.READING_TIME,
		"mvc.command.name=/reading_time/calculate_reading_time"
	},
	service = MVCResourceCommand.class
)
public class CalculateReadingTimeMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		JSONObject jsonObject = _jsonFactory.createJSONObject();

		String content = ParamUtil.getString(resourceRequest, "content");
		String contentType = ParamUtil.getString(
			resourceRequest, "contentType");

		Duration readingTimeDuration = _readingTimeCalculator.calculate(
			content, contentType, resourceRequest.getLocale());

		if (readingTimeDuration != null) {
			jsonObject.put(
				"readingTimeInSeconds", (float)readingTimeDuration.getSeconds()
			).put(
				"readingTimeMessage",
				_readingTimeMessageProvider.provide(
					readingTimeDuration, resourceRequest.getLocale())
			);
		}

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse, jsonObject);
	}

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private ReadingTimeCalculator _readingTimeCalculator;

	@Reference
	private ReadingTimeMessageProvider _readingTimeMessageProvider;

}