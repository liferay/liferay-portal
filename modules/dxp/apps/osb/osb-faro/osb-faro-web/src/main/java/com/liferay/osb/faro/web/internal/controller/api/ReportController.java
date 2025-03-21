/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.controller.api;

import com.liferay.oauth2.provider.scope.RequiresNoScope;
import com.liferay.osb.faro.engine.client.exception.InvalidFilterException;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.util.FaroThreadLocal;
import com.liferay.osb.faro.web.internal.context.GroupInfo;
import com.liferay.osb.faro.web.internal.controller.BaseFaroController;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcellus Tavares
 */
@Component(service = ReportController.class)
@Path("/reports")
@Produces(MediaType.APPLICATION_JSON)
@RequiresNoScope
public class ReportController extends BaseFaroController {

	@GET
	@Path("{any:(?!/export.*).*}")
	public Map<Object, Object> get(
			@Context GroupInfo groupInfo, @Context UriInfo uriInfo)
		throws Exception {

		FaroProject faroProject =
			faroProjectLocalService.getFaroProjectByGroupId(
				groupInfo.getGroupId());

		return contactsEngineClient.get(
			faroProject, _createHeaders(uriInfo.getBaseUri()),
			"/api/" + uriInfo.getPath(), uriInfo.getQueryParameters(),
			Map.class);
	}

	@GET
	@Path("/export/{type}")
	public Object get(
			@QueryParam("fromDate") String fromDateString,
			@Context GroupInfo groupInfo,
			@QueryParam("toDate") String toDateString,
			@PathParam("type") String type)
		throws Exception {

		if (!_exportTypes.contains(type)) {
			return _reportControllerResponseFactory.create(
				"The \"type\" query parameter must be either \"event\", " +
					"\"identity\", \"individual\", \"membership\", \"page\", " +
						"or \"segment\".",
				Response.Status.BAD_REQUEST);
		}

		if (Validator.isBlank(fromDateString) ||
			Validator.isBlank(toDateString)) {

			return _reportControllerResponseFactory.create(
				"\"fromDate\" and \"toDate\" query parameters are mandatory " +
					"and must be ISO 8601 compliant " + _ISO_8601_FORMAT,
				Response.Status.BAD_REQUEST);
		}

		Date fromDate;
		Date toDate;

		try {
			fromDate = _toUTCDate(fromDateString);
			toDate = _toUTCDate(toDateString);
		}
		catch (Exception exception) {
			_log.error(exception);

			return _reportControllerResponseFactory.create(
				"Both dates in range must be ISO 8601 compliant " +
					_ISO_8601_FORMAT,
				Response.Status.BAD_REQUEST);
		}

		if (fromDate.after(toDate)) {
			return _reportControllerResponseFactory.create(
				"Wrong range date. \"fromDate\" cannot be after \"toDate\"",
				Response.Status.BAD_REQUEST);
		}

		FaroProject faroProject =
			faroProjectLocalService.getFaroProjectByGroupId(
				groupInfo.getGroupId());

		String path = "/api/reports/export/" + type;

		Map<String, Object> responseMap;

		Map<String, List<String>> queryParameters =
			HashMapBuilder.<String, List<String>>put(
				"fromDate", Collections.singletonList(fromDateString)
			).put(
				"toDate", Collections.singletonList(toDateString)
			).build();

		try {
			responseMap = contactsEngineClient.get(
				faroProject, Collections.emptyMap(), path, queryParameters,
				Map.class);
		}
		catch (InvalidFilterException invalidFilterException) {
			Response.ResponseBuilder responseBuilder = Response.status(
				Response.Status.BAD_REQUEST);

			String description = "";

			JSONObject jsonObject = _jsonFactory.createJSONObject(
				invalidFilterException.getMessage());

			JSONObject errorAttributesJSONObject = jsonObject.getJSONObject(
				"errorAttributes");

			if (errorAttributesJSONObject != null) {
				description = errorAttributesJSONObject.getString(
					"message", "");
			}

			return responseBuilder.entity(
				HashMapBuilder.put(
					"description", description
				).put(
					"message", "Bad Request"
				).build()
			).build();
		}
		catch (Exception exception) {
			_log.error(exception);

			return _reportControllerResponseFactory.create(
				"An internal problem happened when trying to reach our " +
					"services",
				Response.Status.INTERNAL_SERVER_ERROR);
		}

		String status = MapUtil.getString(responseMap, "status");

		if (!Objects.equals(status, "COMPLETED")) {
			return _reportControllerResponseFactory.create(
				responseMap, Response.Status.OK);
		}

		StreamingOutput streamingOutput = outputStream -> {
			try {
				FaroThreadLocal.setCacheEnabled(false);

				contactsEngineClient.getToOutputStream(
					faroProject,
					HashMapBuilder.put(
						"Accept", "application/octet-stream, */*"
					).build(),
					String.format("%s/file", path), queryParameters,
					outputStream);
			}
			catch (Exception exception) {
				_log.error(exception);
			}

			outputStream.flush();
		};

		return Response.ok(
			streamingOutput, "application/zip"
		).build();
	}

	private Map<String, String> _createHeaders(URI baseURI) {
		return HashMapBuilder.put(
			"X-Liferay-Origin-Forwarded-Host", baseURI.getHost()
		).put(
			"X-Liferay-Origin-Forwarded-Port", String.valueOf(baseURI.getPort())
		).put(
			"X-Liferay-Origin-Forwarded-Proto", baseURI.getScheme()
		).build();
	}

	private Date _toUTCDate(String dateString) {
		LocalDateTime localDateTime = LocalDateTime.parse(
			dateString, _dateTimeFormatter);

		ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneOffset.UTC);

		return Date.from(zonedDateTime.toInstant());
	}

	private static final String _ISO_8601_FORMAT =
		"yyyy-MM-dd'T'HH:mm[:ss.SSS'Z']";

	private static final Log _log = LogFactoryUtil.getLog(
		ReportController.class);

	private static final DateTimeFormatter _dateTimeFormatter =
		DateTimeFormatter.ofPattern(_ISO_8601_FORMAT);
	private static final List<String> _exportTypes = new ArrayList<String>() {
		{
			add("event");
			add("identity");
			add("individual");
			add("membership");
			add("page");
			add("segment");
		}
	};
	private static final ReportControllerResponseFactory
		_reportControllerResponseFactory =
			new ReportControllerResponseFactory();

	@Reference
	private JSONFactory _jsonFactory;

}