/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.controller.main;

import com.liferay.oauth2.provider.scope.RequiresNoScope;
import com.liferay.osb.faro.engine.client.constants.FilterConstants;
import com.liferay.osb.faro.engine.client.util.OrderByField;
import com.liferay.osb.faro.util.FaroThreadLocal;
import com.liferay.osb.faro.web.internal.controller.BaseFaroController;
import com.liferay.osb.faro.web.internal.controller.api.ReportControllerResponseFactory;
import com.liferay.osb.faro.web.internal.param.FaroParam;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Component;

/**
 * @author Marcos Martins
 */
@Component(service = ReportController.class)
@Path("{groupId}/reports")
@Produces(MediaType.APPLICATION_JSON)
@RequiresNoScope
public class ReportController extends BaseFaroController {

	@GET
	@Path("/export/csv/{type}")
	public Object getCSV(
			@QueryParam("assetId") String assetId,
			@QueryParam("assetTitle") String assetTitle,
			@QueryParam("assetType") String assetType,
			@QueryParam("channelId") String channelId,
			@QueryParam("fromDate") String fromDateString,
			@PathParam("groupId") long groupId,
			@QueryParam("individualId") String individualId,
			@DefaultValue(StringPool.BLANK) @QueryParam("orderByFields")
				FaroParam<List<OrderByField>> orderByFieldsFaroParam,
			@QueryParam("query") String query,
			@QueryParam("rangeKey") String rangeKey,
			@QueryParam("segmentId") String segmentId,
			@QueryParam("toDate") String toDateString,
			@PathParam("type") String type)
		throws Exception {

		Object result = _buildQueryParameters(
			assetId, assetType, channelId, fromDateString, individualId,
			orderByFieldsFaroParam, query, rangeKey, segmentId, toDateString,
			type);

		Map<String, List<String>> queryParameters;

		if (result instanceof Map<?, ?>) {
			queryParameters = (Map<String, List<String>>)result;
		}
		else {
			return result;
		}

		StreamingOutput streamingOutput = outputStream -> {
			try {
				FaroThreadLocal.setCacheEnabled(false);

				contactsEngineClient.getToOutputStream(
					faroProjectLocalService.getFaroProjectByGroupId(groupId),
					HashMapBuilder.put(
						"Accept", "application/octet-stream, */*"
					).build(),
					String.format("/reports/export/csv/%s", type),
					queryParameters, outputStream);
			}
			catch (Exception exception) {
				_log.error(exception);
			}

			outputStream.flush();
		};

		String fileName = null;

		if (StringUtil.equals(type, "individual") &&
			Validator.isNotNull(assetTitle) && Validator.isNotNull(assetType)) {

			fileName = String.format(
				"analytics-cloud-%s-known-individuals-%s",
				StringUtil.lowerCase(
					assetTitle.replaceAll(
						_ESCAPED_CHARACTERS_REGEX, StringPool.DASH)),
				LocalDate.now());
		}
		else if (StringUtil.equals(type, "journal")) {
			fileName = String.format(
				"analytics-cloud-web-contents-list-%s", type, LocalDate.now());
		}
		else if (StringUtil.equals(type, "search-terms")) {
			fileName = String.format(
				"analytics-cloud-search-terms-list-%s", LocalDate.now());
		}
		else {
			fileName = String.format(
				"analytics-cloud-%ss-list-%s", type, LocalDate.now());
		}

		return Response.ok(
			streamingOutput, "application/csv"
		).header(
			HttpHeaders.CONTENT_DISPOSITION,
			String.format("filename=\"%s.csv\"", fileName, LocalDate.now())
		).build();
	}

	@GET
	@Path("/export/csv/{type}/count")
	public Object getCSVCount(
			@QueryParam("assetId") String assetId,
			@QueryParam("assetType") String assetType,
			@QueryParam("channelId") String channelId,
			@QueryParam("fromDate") String fromDateString,
			@PathParam("groupId") long groupId,
			@QueryParam("individualId") String individualId,
			@QueryParam("query") String query,
			@QueryParam("rangeKey") String rangeKey,
			@QueryParam("toDate") String toDateString,
			@PathParam("type") String type)
		throws Exception {

		Object result = _buildQueryParameters(
			assetId, assetType, channelId, fromDateString, individualId, null,
			query, rangeKey, null, toDateString, type);

		if (!(result instanceof Map<?, ?>)) {
			return result;
		}

		Map<String, List<String>> queryParameters =
			(Map<String, List<String>>)result;

		return contactsEngineClient.getReportsExportCSVCount(
			faroProjectLocalService.getFaroProjectByGroupId(groupId),
			String.format("/reports/export/csv/%s/count", type),
			queryParameters);
	}

	private Object _buildQueryParameters(
		String assetId, String assetType, String channelId,
		String fromDateString, String individualId,
		FaroParam<List<OrderByField>> orderByFieldsFaroParam, String query,
		String rangeKey, String segmentId, String toDateString, String type) {

		if (!_csvExportTypes.contains(type)) {
			return _reportControllerResponseFactory.create(
				"The \"type\" query parameter must be either \"blog\", " +
					"\"document\", \"event\", \"form\", \"individual\", " +
						"\"journal\", \"membership\", or \"page\".",
				Response.Status.BAD_REQUEST);
		}

		List<OrderByField> orderByFields = null;

		if (orderByFieldsFaroParam != null) {
			orderByFields = orderByFieldsFaroParam.getValue();
		}

		HashMapBuilder.HashMapWrapper<String, List<String>> hashMapWrapper =
			HashMapBuilder.<String, List<String>>put(
				"assetId", Collections.singletonList(assetId)
			).put(
				"assetType", Collections.singletonList(assetType)
			).put(
				"channelId", Collections.singletonList(channelId)
			).put(
				"query", Collections.singletonList(query)
			).put(
				"sort",
				TransformUtil.transform(
					orderByFields,
					orderByField -> {
						String fieldName = orderByField.getFieldName();

						if (!orderByField.isSystem() &&
							StringUtil.equals(type, "individual")) {

							fieldName = StringUtil.replace(
								FilterConstants.FIELD_NAME_CONTEXT_INDIVIDUAL,
								CharPool.QUESTION, fieldName);
						}

						return fieldName + StringPool.COMMA +
							orderByField.getOrderBy();
					})
			);

		if (Validator.isNotNull(segmentId)) {
			hashMapWrapper.put(
				"segmentId", Collections.singletonList(segmentId));
		}

		if (!StringUtil.equals(type, "individual") ||
			Validator.isNotNull(assetType)) {

			if (StringUtil.equalsIgnoreCase(rangeKey, "CUSTOM")) {
				if (Validator.isBlank(fromDateString) ||
					Validator.isBlank(toDateString)) {

					return _reportControllerResponseFactory.create(
						"The \"fromDate\" and \"toDate\" query parameters " +
							"are mandatory and must be ISO 8601 compliant " +
								_ISO_8601_DATE_FORMAT,
						Response.Status.BAD_REQUEST);
				}

				LocalDateTime fromLocalDateTime;
				LocalDateTime toLocalDateTime;

				try {
					fromLocalDateTime = _toUTCLocalDateTime(
						fromDateString, LocalTime.MIN);
					toLocalDateTime = _toUTCLocalDateTime(
						toDateString, LocalTime.MAX);
				}
				catch (Exception exception) {
					_log.error(exception);

					return _reportControllerResponseFactory.create(
						"Both dates in range must be ISO 8601 compliant " +
							_ISO_8601_DATE_FORMAT,
						Response.Status.BAD_REQUEST);
				}

				if (fromLocalDateTime.isAfter(toLocalDateTime)) {
					return _reportControllerResponseFactory.create(
						"The \"fromDate\" cannot be after \"toDate\"",
						Response.Status.BAD_REQUEST);
				}

				hashMapWrapper = hashMapWrapper.put(
					"fromDate",
					Collections.singletonList(
						fromLocalDateTime.format(_dateTimeDateTimeFormatter))
				).put(
					"toDate",
					Collections.singletonList(
						toLocalDateTime.format(_dateTimeDateTimeFormatter))
				);
			}
			else {
				hashMapWrapper = hashMapWrapper.put(
					"rangeKey", Collections.singletonList(rangeKey));

				if (Validator.isNotNull(individualId)) {
					hashMapWrapper = hashMapWrapper.put(
						"individualId",
						Collections.singletonList(individualId));
				}
			}
		}

		return hashMapWrapper.build();
	}

	private LocalDateTime _toUTCLocalDateTime(
		String dateString, LocalTime localTime) {

		LocalDate localDate = LocalDate.parse(
			dateString, _dateDateTimeFormatter);

		return localDate.atTime(localTime);
	}

	private static final String _ESCAPED_CHARACTERS_REGEX = "[^a-zA-Z0-9\\.]+";

	private static final String _ISO_8601_DATE_FORMAT = "yyyy-MM-dd";

	private static final String _ISO_8601_DATE_TIME_FORMAT =
		"yyyy-MM-dd'T'HH:mm[:ss.SSS'Z']";

	private static final Log _log = LogFactoryUtil.getLog(
		ReportController.class);

	private static final Set<String> _csvExportTypes = SetUtil.fromArray(
		"blog", "document", "event", "form", "individual", "journal",
		"membership", "page", "search-terms");
	private static final DateTimeFormatter _dateDateTimeFormatter =
		DateTimeFormatter.ofPattern(_ISO_8601_DATE_FORMAT);
	private static final DateTimeFormatter _dateTimeDateTimeFormatter =
		DateTimeFormatter.ofPattern(_ISO_8601_DATE_TIME_FORMAT);
	private static final ReportControllerResponseFactory
		_reportControllerResponseFactory =
			new ReportControllerResponseFactory();

}