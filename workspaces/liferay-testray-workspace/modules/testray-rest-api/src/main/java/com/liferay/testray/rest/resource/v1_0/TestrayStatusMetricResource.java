package com.liferay.testray.rest.resource.v1_0;

import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.odata.filter.ExpressionConvert;
import com.liferay.portal.odata.filter.FilterParserProvider;
import com.liferay.portal.odata.sort.SortParserProvider;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.testray.rest.dto.v1_0.TestrayBuildMetric;
import com.liferay.testray.rest.dto.v1_0.TestrayCaseTypeMetric;
import com.liferay.testray.rest.dto.v1_0.TestrayComponentMetric;
import com.liferay.testray.rest.dto.v1_0.TestrayIssueMetric;
import com.liferay.testray.rest.dto.v1_0.TestrayRoutineMetric;
import com.liferay.testray.rest.dto.v1_0.TestrayRunMetric;
import com.liferay.testray.rest.dto.v1_0.TestrayTeamMetric;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Generated;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.ws.rs.core.UriInfo;

import org.osgi.annotation.versioning.ProviderType;

/**
 * To access this resource, run:
 *
 *     curl -u your@email.com:yourpassword -D - http://localhost:8080/o/testray-rest/v1.0
 *
 * @author Nilton Vieira
 * @generated
 */
@Generated("")
@ProviderType
public interface TestrayStatusMetricResource {

	public Page<TestrayCaseTypeMetric>
			getTestrayStatusMetricByTestrayBuildIdTestrayBuildTestrayCaseTypesMetricsPage(
				Long testrayBuildId, String testrayCasePriorities,
				String testrayTeamIds, Pagination pagination)
		throws Exception;

	public Page<TestrayComponentMetric>
			getTestrayStatusMetricByTestrayBuildIdTestrayBuildTestrayComponentsMetricsPage(
				Long testrayBuildId, String testrayCasePriorities,
				String testrayCaseTypes, String testrayTeamIds,
				Pagination pagination)
		throws Exception;

	public Page<TestrayRunMetric>
			getTestrayStatusMetricByTestrayBuildIdTestrayBuildTestrayRunsMetricsPage(
				Long testrayBuildId, String testrayCasePriorities,
				String testrayCaseTypes, String testrayTeamIds,
				Pagination pagination)
		throws Exception;

	public Page<TestrayTeamMetric>
			getTestrayStatusMetricByTestrayBuildIdTestrayBuildTestrayTeamsMetricsPage(
				Long testrayBuildId, String testrayCasePriorities,
				String testrayCaseTypes, Long testrayRunId,
				String testrayTeamIds, Pagination pagination)
		throws Exception;

	public Page<TestrayIssueMetric>
			getTestrayStatusMetricByTestrayJiraIssueIdTestrayJiraIssueTestrayIssuesMetricsPage(
				Long testrayJiraIssueId, Long testrayBuildId,
				Pagination pagination)
		throws Exception;

	public Page<TestrayRoutineMetric>
			getTestrayStatusMetricByTestrayProjectIdTestrayProjectTestrayRoutinesMetricsPage(
				Long testrayProjectId, Pagination pagination,
				com.liferay.portal.kernel.search.Sort[] sorts)
		throws Exception;

	public Page<TestrayBuildMetric>
			getTestrayStatusMetricByTestrayRoutineIdTestrayRoutineTestrayBuildsMetricsPage(
				Long testrayRoutineId, Long testrayBuildId,
				String testrayBuildName, String testrayProductVersion,
				String testrayTaskStatus, Pagination pagination)
		throws Exception;

	public default void setContextAcceptLanguage(
		AcceptLanguage contextAcceptLanguage) {
	}

	public void setContextCompany(
		com.liferay.portal.kernel.model.Company contextCompany);

	public default void setContextHttpServletRequest(
		HttpServletRequest contextHttpServletRequest) {
	}

	public default void setContextHttpServletResponse(
		HttpServletResponse contextHttpServletResponse) {
	}

	public default void setContextUriInfo(UriInfo contextUriInfo) {
	}

	public void setContextUser(
		com.liferay.portal.kernel.model.User contextUser);

	public void setExpressionConvert(
		ExpressionConvert<com.liferay.portal.kernel.search.filter.Filter>
			expressionConvert);

	public void setFilterParserProvider(
		FilterParserProvider filterParserProvider);

	public void setGroupLocalService(GroupLocalService groupLocalService);

	public void setResourceActionLocalService(
		ResourceActionLocalService resourceActionLocalService);

	public void setResourcePermissionLocalService(
		ResourcePermissionLocalService resourcePermissionLocalService);

	public void setRoleLocalService(RoleLocalService roleLocalService);

	public void setSortParserProvider(SortParserProvider sortParserProvider);

	public default com.liferay.portal.kernel.search.filter.Filter toFilter(
		String filterString) {

		return toFilter(
			filterString, Collections.<String, List<String>>emptyMap());
	}

	public default com.liferay.portal.kernel.search.filter.Filter toFilter(
		String filterString, Map<String, List<String>> multivaluedMap) {

		return null;
	}

	public default com.liferay.portal.kernel.search.Sort[] toSorts(
		String sortsString) {

		return new com.liferay.portal.kernel.search.Sort[0];
	}

	@ProviderType
	public interface Builder {

		public TestrayStatusMetricResource build();

		public Builder checkPermissions(boolean checkPermissions);

		public Builder httpServletRequest(
			HttpServletRequest httpServletRequest);

		public Builder httpServletResponse(
			HttpServletResponse httpServletResponse);

		public Builder preferredLocale(Locale preferredLocale);

		public Builder uriInfo(UriInfo uriInfo);

		public Builder user(com.liferay.portal.kernel.model.User user);

	}

	@ProviderType
	public interface Factory {

		public Builder create();

	}

}