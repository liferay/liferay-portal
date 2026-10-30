/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.jaxrs.container.request.filter;

import com.liferay.osb.faro.rest.internal.resource.v1_0.Interval;
import com.liferay.osb.faro.rest.internal.resource.v1_0.TimeRange;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import jakarta.annotation.Priority;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.lang.reflect.Method;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Nilton Vieira
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=Liferay.Faro.Rest)",
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.name=Liferay.Faro.Rest.Parameter.Container.Request.Filter"
	},
	scope = ServiceScope.PROTOTYPE, service = ContainerRequestFilter.class
)
@Priority(Priorities.USER)
public class ParameterContainerRequestFilter implements ContainerRequestFilter {

	@Override
	public void filter(ContainerRequestContext containerRequestContext) {
		try {
			UriInfo uriInfo = containerRequestContext.getUriInfo();

			_validate(
				uriInfo.getPathParameters(), uriInfo.getQueryParameters());
		}
		catch (BadRequestException badRequestException) {
			containerRequestContext.abortWith(
				Response.status(
					Response.Status.BAD_REQUEST
				).entity(
					new Problem(badRequestException)
				).type(
					MediaType.APPLICATION_JSON
				).build());
		}
	}

	private static String[] _getNames(Enum<?>[] enums) {
		String[] names = new String[enums.length];

		for (int i = 0; i < enums.length; i++) {
			names[i] = enums[i].name();
		}

		Arrays.sort(names);

		return names;
	}

	private String _getResourceMethodName() {
		Method method = _resourceInfo.getResourceMethod();

		if (method == null) {
			return null;
		}

		return method.getName();
	}

	private LocalDate _toLocalDate(String name, String value) {
		if (value == null) {
			return null;
		}

		try {
			return LocalDate.parse(value);
		}
		catch (DateTimeParseException dateTimeParseException) {
			throw new BadRequestException(
				StringBundler.concat(
					"\"", name,
					"\" must be a date in the \"yyyy-MM-dd\" format"),
				dateTimeParseException);
		}
	}

	private void _validate(
		MultivaluedMap<String, String> pathParameters,
		MultivaluedMap<String, String> queryParameters) {

		for (String name : new String[] {"individualSegmentId", "segmentId"}) {
			_validateId(name, pathParameters.getFirst(name));
			_validateId(name, queryParameters.getFirst(name));
		}

		for (Map.Entry<String, String[]> entry : _validValues.entrySet()) {
			_validateValue(
				entry.getKey(), entry.getValue(),
				queryParameters.getFirst(entry.getKey()));
		}

		_validateInteger(
			Integer.MAX_VALUE, "page", queryParameters.getFirst("page"));
		_validateInteger(500, "pageSize", queryParameters.getFirst("pageSize"));
		_validateRange(
			queryParameters.getFirst("rangeEnd"),
			queryParameters.getFirst("rangeStart"));
		_validateSort(queryParameters.getFirst("sort"));
	}

	private void _validateId(String name, String value) {
		if ((value != null) && !Validator.isNumber(value)) {
			throw new BadRequestException(
				StringBundler.concat("\"", name, "\" must be a numeric ID"));
		}
	}

	private void _validateInteger(int max, String name, String value) {
		if (value == null) {
			return;
		}

		if (Validator.isNumber(value) && (value.length() <= 10)) {
			long number = GetterUtil.getLong(value);

			if ((number >= 1) && (number <= max)) {
				return;
			}
		}

		if (max == Integer.MAX_VALUE) {
			throw new BadRequestException(
				StringBundler.concat(
					"\"", name, "\" must be a whole number of at least 1"));
		}

		throw new BadRequestException(
			StringBundler.concat(
				"\"", name, "\" must be a whole number from 1 to ", max));
	}

	private void _validateRange(String rangeEnd, String rangeStart) {
		LocalDate rangeEndLocalDate = _toLocalDate("rangeEnd", rangeEnd);
		LocalDate rangeStartLocalDate = _toLocalDate("rangeStart", rangeStart);

		if ((rangeEndLocalDate == null) != (rangeStartLocalDate == null)) {
			throw new BadRequestException(
				"\"rangeEnd\" and \"rangeStart\" must be set together");
		}

		if ((rangeEndLocalDate != null) &&
			rangeStartLocalDate.isAfter(rangeEndLocalDate)) {

			throw new BadRequestException(
				"\"rangeStart\" must not be after \"rangeEnd\"");
		}
	}

	private void _validateSort(String sort) {
		if (Validator.isNull(sort)) {
			return;
		}

		String resourceMethodName = _getResourceMethodName();

		String[] sortableFieldNames = _sortableFieldNames.get(
			resourceMethodName);

		if ((sortableFieldNames != null) && (sortableFieldNames.length == 0)) {
			throw new BadRequestException(
				"\"sort\" is not supported by this tool");
		}

		String[] sortStrings = StringUtil.split(sort);

		if ((sortStrings.length > 1) &&
			!Objects.equals(
				resourceMethodName,
				"getWorkspaceGroupChannelIndividualsPage")) {

			throw new BadRequestException(
				"\"sort\" accepts only one column on this tool");
		}

		for (String sortString : sortStrings) {
			String fieldName = sortString;
			String orderBy = StringUtil.extractLast(sortString, CharPool.COLON);

			if (orderBy != null) {
				fieldName = StringUtil.extractFirst(sortString, CharPool.COLON);

				if (!orderBy.equals("asc") && !orderBy.equals("desc")) {
					throw new BadRequestException(
						"\"sort\" must be a comma separated list of " +
							"\"column:asc\" or \"column:desc\" expressions");
				}
			}

			if ((sortableFieldNames != null) &&
				!ArrayUtil.contains(sortableFieldNames, fieldName)) {

				throw new BadRequestException(
					StringBundler.concat(
						"\"", fieldName, "\" is not a sortable column. ",
						"Sortable columns: ",
						StringUtil.merge(
							sortableFieldNames, StringPool.COMMA_AND_SPACE)));
			}
		}
	}

	private void _validateValue(
		String name, String[] validValues, String value) {

		if ((value != null) && !ArrayUtil.contains(validValues, value)) {
			throw new BadRequestException(
				StringBundler.concat(
					"\"", name, "\" must be one of ",
					StringUtil.merge(validValues, StringPool.COMMA_AND_SPACE)));
		}
	}

	private static final Map<String, String[]> _sortableFieldNames =
		HashMapBuilder.put(
			"getWorkspaceGroupAccountLifecycleStageTransitionsPage",
			new String[] {
				"accountId", "accountName", "fromAccountLifecycleStageId",
				"toAccountLifecycleStageId", "transitionDate"
			}
		).put(
			"getWorkspaceGroupChannelAccountsPage",
			new String[] {
				"accountName", "accountType", "activitiesCount",
				"annualRevenue", "country", "createDate", "firstActivityDate",
				"industry", "lastActivityDate", "lifecycleStage",
				"modifiedDate", "numberOfEmployees", "state", "yearStarted"
			}
		).put(
			"getWorkspaceGroupChannelAssetSummariesPage",
			new String[] {
				"downloadsMetric", "impressionsMetric", "readsMetric",
				"viewsMetric"
			}
		).put(
			"getWorkspaceGroupChannelIndividualsPage",
			new String[] {
				"accountName", "activitiesCount", "activityStatus", "country",
				"createDate", "emailAddress", "firstActivityDate", "firstName",
				"id", "jobTitle", "knownSinceDate", "lastActivityDate",
				"lastName", "lastSessionCountry", "middleName", "modifiedDate",
				"name", "profileType", "sessionsCount"
			}
		).put(
			"getWorkspaceGroupChannelPagesPage",
			new String[] {
				"avgTimeOnPageMetric", "bounceRateMetric", "directAccessMetric",
				"entrancesMetric", "exitRateMetric", "indirectAccessMetric",
				"viewsMetric", "visitorsMetric"
			}
		).put(
			"getWorkspaceGroupIndividualSegmentMembershipsPage", new String[0]
		).build();
	private static final Map<String, String[]> _validValues =
		HashMapBuilder.put(
			"activityStatus", new String[] {"ACTIVE", "INACTIVE"}
		).put(
			"interval", _getNames(Interval.values())
		).put(
			"profileType", new String[] {"ANONYMOUS", "KNOWN"}
		).put(
			"rangeKey", _getNames(TimeRange.values())
		).put(
			"status", new String[] {"ACTIVE", "INACTIVE"}
		).build();

	@Context
	private ResourceInfo _resourceInfo;

}