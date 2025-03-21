/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.controller.api;

import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.ws.rs.core.Response;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Alejo Ceballos
 */
public class ReportControllerResponseFactory {

	public Response create(
		Map<String, Object> responseMap, Response.Status responseStatus) {

		Map<String, String> stringMap = new HashMap<>();

		String createdDateString = MapUtil.getString(
			responseMap, "createdDate");

		if (!Validator.isBlank(createdDateString)) {
			stringMap.put("createdDate", createdDateString);
		}

		String fromDateString = MapUtil.getString(responseMap, "fromDate");

		if (!Validator.isBlank(fromDateString)) {
			stringMap.put("fromDate", fromDateString);
		}

		String startedDateString = MapUtil.getString(
			responseMap, "startedDate");

		if (!Validator.isBlank(startedDateString)) {
			stringMap.put("startedDate", startedDateString);
		}

		String status = MapUtil.getString(responseMap, "status");

		if (!Validator.isBlank(status)) {
			stringMap.put("status", status);
		}
		else if (responseStatus != Response.Status.OK) {
			stringMap.put("status", "ERROR");
		}

		String message = MapUtil.getString(responseMap, "message");

		if (!Validator.isBlank(message)) {
			stringMap.put("message", message);
		}
		else if (status.equals("PENDING")) {
			String previousStatus = MapUtil.getString(
				responseMap, "previousStatus");

			if (!Validator.isBlank(previousStatus) &&
				previousStatus.equals("ERROR")) {

				stringMap.put(
					"message",
					"The last data export for this date range and type " +
						"failed. A new data export file will be created. " +
							"Please come back later. ");
			}
			else if (!Validator.isBlank(previousStatus) &&
					 previousStatus.equals("PENDING")) {

				stringMap.put(
					"message",
					"A data export for this date range and type has already " +
						"been scheduled. Please come back later.");
			}
			else {
				stringMap.put(
					"message",
					"A new data export file for this date range and type " +
						"will be created. Please come back later.");
			}
		}
		else if (status.equals("RUNNING")) {
			stringMap.put(
				"message",
				"The data export file for this date range and type is being " +
					"created. Please come back later.");
		}
		else if (status.equals("ERROR")) {
			stringMap.put(
				"message",
				"The last data export for this date range and type failed. A " +
					"new data export file will be created. Please come back " +
						"later.");
		}

		String toDateString = MapUtil.getString(responseMap, "toDate");

		if (!Validator.isBlank(toDateString)) {
			stringMap.put("toDate", toDateString);
		}

		String type = MapUtil.getString(responseMap, "type");

		if (!Validator.isBlank(type)) {
			stringMap.put("type", type);
		}

		Response.ResponseBuilder responseBuilder = Response.status(
			responseStatus);

		responseBuilder.entity(stringMap);

		return responseBuilder.build();
	}

	public Response create(String message, Response.Status responseStatus) {
		return create(
			HashMapBuilder.<String, Object>put(
				"message", message
			).build(),
			responseStatus);
	}

}