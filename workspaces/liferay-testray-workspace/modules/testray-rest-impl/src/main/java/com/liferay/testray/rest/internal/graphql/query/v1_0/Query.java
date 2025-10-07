package com.liferay.testray.rest.internal.graphql.query.v1_0;

import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.testray.rest.dto.v1_0.TestrayCaseResult;
import com.liferay.testray.rest.dto.v1_0.TestrayRoutineDurationReport;
import com.liferay.testray.rest.dto.v1_0.TestrayRunComparison;
import com.liferay.testray.rest.dto.v1_0.TestrayStatusMetric;
import com.liferay.testray.rest.dto.v1_0.TestrayTestFlow;
import com.liferay.testray.rest.resource.v1_0.TestrayCaseResultResource;
import com.liferay.testray.rest.resource.v1_0.TestrayRoutineDurationReportResource;
import com.liferay.testray.rest.resource.v1_0.TestrayRunComparisonResource;
import com.liferay.testray.rest.resource.v1_0.TestrayStatusMetricResource;
import com.liferay.testray.rest.resource.v1_0.TestrayTestFlowResource;

import java.util.Map;
import java.util.function.BiFunction;

import javax.annotation.Generated;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.osgi.service.component.ComponentServiceObjects;

/**
 * @author Nilton Vieira
 * @generated
 */
@Generated("")
public class Query {

	public static void setTestrayCaseResultResourceComponentServiceObjects(
		ComponentServiceObjects<TestrayCaseResultResource>
			testrayCaseResultResourceComponentServiceObjects) {

		_testrayCaseResultResourceComponentServiceObjects =
			testrayCaseResultResourceComponentServiceObjects;
	}

	public static void
		setTestrayRoutineDurationReportResourceComponentServiceObjects(
			ComponentServiceObjects<TestrayRoutineDurationReportResource>
				testrayRoutineDurationReportResourceComponentServiceObjects) {

		_testrayRoutineDurationReportResourceComponentServiceObjects =
			testrayRoutineDurationReportResourceComponentServiceObjects;
	}

	public static void setTestrayRunComparisonResourceComponentServiceObjects(
		ComponentServiceObjects<TestrayRunComparisonResource>
			testrayRunComparisonResourceComponentServiceObjects) {

		_testrayRunComparisonResourceComponentServiceObjects =
			testrayRunComparisonResourceComponentServiceObjects;
	}

	public static void setTestrayStatusMetricResourceComponentServiceObjects(
		ComponentServiceObjects<TestrayStatusMetricResource>
			testrayStatusMetricResourceComponentServiceObjects) {

		_testrayStatusMetricResourceComponentServiceObjects =
			testrayStatusMetricResourceComponentServiceObjects;
	}

	public static void setTestrayTestFlowResourceComponentServiceObjects(
		ComponentServiceObjects<TestrayTestFlowResource>
			testrayTestFlowResourceComponentServiceObjects) {

		_testrayTestFlowResourceComponentServiceObjects =
			testrayTestFlowResourceComponentServiceObjects;
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {testrayCaseResultHistoryTestrayCase(error: ___, issues: ___, maxExecutionDate: ___, minExecutionDate: ___, noError: ___, noIssues: ___, page: ___, pageSize: ___, status: ___, testrayCaseId: ___, testrayProductVersionIds: ___, testrayRoutineIds: ___, testrayRunName: ___, testrayTeamIds: ___, userId: ___, warning: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public TestrayCaseResultPage testrayCaseResultHistoryTestrayCase(
			@GraphQLName("testrayCaseId") Long testrayCaseId,
			@GraphQLName("error") String error,
			@GraphQLName("issues") String issues,
			@GraphQLName("maxExecutionDate") String maxExecutionDate,
			@GraphQLName("minExecutionDate") String minExecutionDate,
			@GraphQLName("noError") Boolean noError,
			@GraphQLName("noIssues") Boolean noIssues,
			@GraphQLName("status") String status,
			@GraphQLName("testrayProductVersionIds") String
				testrayProductVersionIds,
			@GraphQLName("testrayRoutineIds") String testrayRoutineIds,
			@GraphQLName("testrayRunName") String testrayRunName,
			@GraphQLName("testrayTeamIds") String testrayTeamIds,
			@GraphQLName("userId") String userId,
			@GraphQLName("warning") String warning,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_testrayCaseResultResourceComponentServiceObjects,
			this::_populateResourceContext,
			testrayCaseResultResource -> new TestrayCaseResultPage(
				testrayCaseResultResource.
					getTestrayCaseResultHistoryTestrayCasePage(
						testrayCaseId, error, issues, maxExecutionDate,
						minExecutionDate, noError, noIssues, status,
						testrayProductVersionIds, testrayRoutineIds,
						testrayRunName, testrayTeamIds, userId, warning,
						Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {testrayCaseResultsTestrayBuild(comment: ___, error: ___, flaky: ___, issues: ___, noComment: ___, noError: ___, noIssues: ___, page: ___, pageSize: ___, priority: ___, sorts: ___, status: ___, testrayBuildId: ___, testrayCaseName: ___, testrayCaseTypeIds: ___, testrayComponentIds: ___, testrayRunId: ___, testrayRunName: ___, testraySubtaskId: ___, testrayTeamIds: ___, userId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public TestrayCaseResultPage testrayCaseResultsTestrayBuild(
			@GraphQLName("testrayBuildId") Long testrayBuildId,
			@GraphQLName("comment") String comment,
			@GraphQLName("error") String error,
			@GraphQLName("flaky") Boolean flaky,
			@GraphQLName("issues") String issues,
			@GraphQLName("noComment") Boolean noComment,
			@GraphQLName("noError") Boolean noError,
			@GraphQLName("noIssues") Boolean noIssues,
			@GraphQLName("priority") String priority,
			@GraphQLName("status") String status,
			@GraphQLName("testrayCaseName") String testrayCaseName,
			@GraphQLName("testrayCaseTypeIds") String testrayCaseTypeIds,
			@GraphQLName("testrayComponentIds") String testrayComponentIds,
			@GraphQLName("testrayRunId") String testrayRunId,
			@GraphQLName("testrayRunName") String testrayRunName,
			@GraphQLName("testraySubtaskId") String testraySubtaskId,
			@GraphQLName("testrayTeamIds") String testrayTeamIds,
			@GraphQLName("userId") String userId,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_testrayCaseResultResourceComponentServiceObjects,
			this::_populateResourceContext,
			testrayCaseResultResource -> new TestrayCaseResultPage(
				testrayCaseResultResource.getTestrayCaseResultsTestrayBuildPage(
					testrayBuildId, comment, error, flaky, issues, noComment,
					noError, noIssues, priority, status, testrayCaseName,
					testrayCaseTypeIds, testrayComponentIds, testrayRunId,
					testrayRunName, testraySubtaskId, testrayTeamIds, userId,
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(
						testrayCaseResultResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {testrayExportCaseResultTestrayBuild(testrayBuildId: ___){}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public Response testrayExportCaseResultTestrayBuild(
			@GraphQLName("testrayBuildId") Long testrayBuildId)
		throws Exception {

		return _applyComponentServiceObjects(
			_testrayCaseResultResourceComponentServiceObjects,
			this::_populateResourceContext,
			testrayCaseResultResource ->
				testrayCaseResultResource.
					getTestrayExportCaseResultTestrayBuild(testrayBuildId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {testrayRoutineDurationReportsTestrayRoutine(flaky: ___, page: ___, pageSize: ___, priority: ___, testrayCaseName: ___, testrayCaseTypeIds: ___, testrayComponentIds: ___, testrayRoutineId: ___, testrayTeamIds: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public TestrayRoutineDurationReportPage
			testrayRoutineDurationReportsTestrayRoutine(
				@GraphQLName("testrayRoutineId") Long testrayRoutineId,
				@GraphQLName("flaky") Boolean flaky,
				@GraphQLName("priority") String priority,
				@GraphQLName("testrayCaseName") String testrayCaseName,
				@GraphQLName("testrayCaseTypeIds") String testrayCaseTypeIds,
				@GraphQLName("testrayComponentIds") String testrayComponentIds,
				@GraphQLName("testrayTeamIds") String testrayTeamIds,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_testrayRoutineDurationReportResourceComponentServiceObjects,
			this::_populateResourceContext,
			testrayRoutineDurationReportResource ->
				new TestrayRoutineDurationReportPage(
					testrayRoutineDurationReportResource.
						getTestrayRoutineDurationReportsTestrayRoutinePage(
							testrayRoutineId, flaky, priority, testrayCaseName,
							testrayCaseTypeIds, testrayComponentIds,
							testrayTeamIds, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {testrayRunComparison(filter: ___, testrayRunId1: ___, testrayRunId2: ___){results, testrayCaseResultComparisons}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public TestrayRunComparison testrayRunComparison(
			@GraphQLName("testrayRunId1") Long testrayRunId1,
			@GraphQLName("testrayRunId2") Long testrayRunId2,
			@GraphQLName("filter") String filterString)
		throws Exception {

		return _applyComponentServiceObjects(
			_testrayRunComparisonResourceComponentServiceObjects,
			this::_populateResourceContext,
			testrayRunComparisonResource ->
				testrayRunComparisonResource.getTestrayRunComparison(
					testrayRunId1, testrayRunId2,
					_filterBiFunction.apply(
						testrayRunComparisonResource, filterString)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {testrayRunComparisonByTestrayRoutineIdTestrayRoutine(testrayRoutineId: ___){}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public Object testrayRunComparisonByTestrayRoutineIdTestrayRoutine(
			@GraphQLName("testrayRoutineId") Long testrayRoutineId)
		throws Exception {

		return _applyComponentServiceObjects(
			_testrayRunComparisonResourceComponentServiceObjects,
			this::_populateResourceContext,
			testrayRunComparisonResource ->
				testrayRunComparisonResource.
					getTestrayRunComparisonByTestrayRoutineIdTestrayRoutine(
						testrayRoutineId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {testrayRunComparisonRun(filter: ___, testrayCaseResultError1: ___, testrayCaseResultError2: ___, testrayCaseResultIssue1: ___, testrayCaseResultIssue2: ___, testrayCaseResultStatus1: ___, testrayCaseResultStatus2: ___, testrayRunId1: ___, testrayRunId2: ___){results, testrayCaseResultComparisons}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public TestrayRunComparison testrayRunComparisonRun(
			@GraphQLName("testrayRunId1") Long testrayRunId1,
			@GraphQLName("testrayRunId2") Long testrayRunId2,
			@GraphQLName("testrayCaseResultError1") String
				testrayCaseResultError1,
			@GraphQLName("testrayCaseResultError2") String
				testrayCaseResultError2,
			@GraphQLName("testrayCaseResultIssue1") String
				testrayCaseResultIssue1,
			@GraphQLName("testrayCaseResultIssue2") String
				testrayCaseResultIssue2,
			@GraphQLName("testrayCaseResultStatus1") String
				testrayCaseResultStatus1,
			@GraphQLName("testrayCaseResultStatus2") String
				testrayCaseResultStatus2,
			@GraphQLName("filter") String filterString)
		throws Exception {

		return _applyComponentServiceObjects(
			_testrayRunComparisonResourceComponentServiceObjects,
			this::_populateResourceContext,
			testrayRunComparisonResource ->
				testrayRunComparisonResource.getTestrayRunComparisonRun(
					testrayRunId1, testrayRunId2, testrayCaseResultError1,
					testrayCaseResultError2, testrayCaseResultIssue1,
					testrayCaseResultIssue2, testrayCaseResultStatus1,
					testrayCaseResultStatus2,
					_filterBiFunction.apply(
						testrayRunComparisonResource, filterString)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {testrayRunComparisonTestrayCaseResultComparisons(filter: ___, page: ___, pageSize: ___, testrayCaseResultError1: ___, testrayCaseResultError2: ___, testrayCaseResultIssue1: ___, testrayCaseResultIssue2: ___, testrayCaseResultStatus1: ___, testrayCaseResultStatus2: ___, testrayRunId1: ___, testrayRunId2: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public TestrayRunComparisonPage
			testrayRunComparisonTestrayCaseResultComparisons(
				@GraphQLName("testrayRunId1") Long testrayRunId1,
				@GraphQLName("testrayRunId2") Long testrayRunId2,
				@GraphQLName("testrayCaseResultError1") String
					testrayCaseResultError1,
				@GraphQLName("testrayCaseResultError2") String
					testrayCaseResultError2,
				@GraphQLName("testrayCaseResultIssue1") String
					testrayCaseResultIssue1,
				@GraphQLName("testrayCaseResultIssue2") String
					testrayCaseResultIssue2,
				@GraphQLName("testrayCaseResultStatus1") String
					testrayCaseResultStatus1,
				@GraphQLName("testrayCaseResultStatus2") String
					testrayCaseResultStatus2,
				@GraphQLName("filter") String filterString,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_testrayRunComparisonResourceComponentServiceObjects,
			this::_populateResourceContext,
			testrayRunComparisonResource -> new TestrayRunComparisonPage(
				testrayRunComparisonResource.
					getTestrayRunComparisonTestrayCaseResultComparisonsPage(
						testrayRunId1, testrayRunId2, testrayCaseResultError1,
						testrayCaseResultError2, testrayCaseResultIssue1,
						testrayCaseResultIssue2, testrayCaseResultStatus1,
						testrayCaseResultStatus2,
						_filterBiFunction.apply(
							testrayRunComparisonResource, filterString),
						Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {testrayStatusMetricByTestrayBuildIdTestrayBuildTestrayCaseTypesMetrics(page: ___, pageSize: ___, testrayBuildId: ___, testrayCasePriorities: ___, testrayTeamIds: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public TestrayStatusMetricPage
			testrayStatusMetricByTestrayBuildIdTestrayBuildTestrayCaseTypesMetrics(
				@GraphQLName("testrayBuildId") Long testrayBuildId,
				@GraphQLName("testrayCasePriorities") String
					testrayCasePriorities,
				@GraphQLName("testrayTeamIds") String testrayTeamIds,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_testrayStatusMetricResourceComponentServiceObjects,
			this::_populateResourceContext,
			testrayStatusMetricResource -> new TestrayStatusMetricPage(
				testrayStatusMetricResource.
					getTestrayStatusMetricByTestrayBuildIdTestrayBuildTestrayCaseTypesMetricsPage(
						testrayBuildId, testrayCasePriorities, testrayTeamIds,
						Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {testrayStatusMetricByTestrayBuildIdTestrayBuildTestrayComponentsMetrics(page: ___, pageSize: ___, testrayBuildId: ___, testrayCasePriorities: ___, testrayCaseTypes: ___, testrayTeamIds: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public TestrayStatusMetricPage
			testrayStatusMetricByTestrayBuildIdTestrayBuildTestrayComponentsMetrics(
				@GraphQLName("testrayBuildId") Long testrayBuildId,
				@GraphQLName("testrayCasePriorities") String
					testrayCasePriorities,
				@GraphQLName("testrayCaseTypes") String testrayCaseTypes,
				@GraphQLName("testrayTeamIds") String testrayTeamIds,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_testrayStatusMetricResourceComponentServiceObjects,
			this::_populateResourceContext,
			testrayStatusMetricResource -> new TestrayStatusMetricPage(
				testrayStatusMetricResource.
					getTestrayStatusMetricByTestrayBuildIdTestrayBuildTestrayComponentsMetricsPage(
						testrayBuildId, testrayCasePriorities, testrayCaseTypes,
						testrayTeamIds, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {testrayStatusMetricByTestrayBuildIdTestrayBuildTestrayRunsMetrics(page: ___, pageSize: ___, testrayBuildId: ___, testrayCasePriorities: ___, testrayCaseTypes: ___, testrayTeamIds: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public TestrayStatusMetricPage
			testrayStatusMetricByTestrayBuildIdTestrayBuildTestrayRunsMetrics(
				@GraphQLName("testrayBuildId") Long testrayBuildId,
				@GraphQLName("testrayCasePriorities") String
					testrayCasePriorities,
				@GraphQLName("testrayCaseTypes") String testrayCaseTypes,
				@GraphQLName("testrayTeamIds") String testrayTeamIds,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_testrayStatusMetricResourceComponentServiceObjects,
			this::_populateResourceContext,
			testrayStatusMetricResource -> new TestrayStatusMetricPage(
				testrayStatusMetricResource.
					getTestrayStatusMetricByTestrayBuildIdTestrayBuildTestrayRunsMetricsPage(
						testrayBuildId, testrayCasePriorities, testrayCaseTypes,
						testrayTeamIds, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {testrayStatusMetricByTestrayBuildIdTestrayBuildTestrayTeamsMetrics(page: ___, pageSize: ___, testrayBuildId: ___, testrayCasePriorities: ___, testrayCaseTypes: ___, testrayRunId: ___, testrayTeamIds: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public TestrayStatusMetricPage
			testrayStatusMetricByTestrayBuildIdTestrayBuildTestrayTeamsMetrics(
				@GraphQLName("testrayBuildId") Long testrayBuildId,
				@GraphQLName("testrayCasePriorities") String
					testrayCasePriorities,
				@GraphQLName("testrayCaseTypes") String testrayCaseTypes,
				@GraphQLName("testrayRunId") Long testrayRunId,
				@GraphQLName("testrayTeamIds") String testrayTeamIds,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_testrayStatusMetricResourceComponentServiceObjects,
			this::_populateResourceContext,
			testrayStatusMetricResource -> new TestrayStatusMetricPage(
				testrayStatusMetricResource.
					getTestrayStatusMetricByTestrayBuildIdTestrayBuildTestrayTeamsMetricsPage(
						testrayBuildId, testrayCasePriorities, testrayCaseTypes,
						testrayRunId, testrayTeamIds,
						Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {testrayStatusMetricByTestrayJiraIssueIdTestrayJiraIssueTestrayIssuesMetrics(page: ___, pageSize: ___, testrayBuildId: ___, testrayJiraIssueId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public TestrayStatusMetricPage
			testrayStatusMetricByTestrayJiraIssueIdTestrayJiraIssueTestrayIssuesMetrics(
				@GraphQLName("testrayJiraIssueId") Long testrayJiraIssueId,
				@GraphQLName("testrayBuildId") Long testrayBuildId,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_testrayStatusMetricResourceComponentServiceObjects,
			this::_populateResourceContext,
			testrayStatusMetricResource -> new TestrayStatusMetricPage(
				testrayStatusMetricResource.
					getTestrayStatusMetricByTestrayJiraIssueIdTestrayJiraIssueTestrayIssuesMetricsPage(
						testrayJiraIssueId, testrayBuildId,
						Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {testrayStatusMetricByTestrayProjectIdTestrayProjectTestrayRoutinesMetrics(page: ___, pageSize: ___, sorts: ___, testrayProjectId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public TestrayStatusMetricPage
			testrayStatusMetricByTestrayProjectIdTestrayProjectTestrayRoutinesMetrics(
				@GraphQLName("testrayProjectId") Long testrayProjectId,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_testrayStatusMetricResourceComponentServiceObjects,
			this::_populateResourceContext,
			testrayStatusMetricResource -> new TestrayStatusMetricPage(
				testrayStatusMetricResource.
					getTestrayStatusMetricByTestrayProjectIdTestrayProjectTestrayRoutinesMetricsPage(
						testrayProjectId, Pagination.of(page, pageSize),
						_sortsBiFunction.apply(
							testrayStatusMetricResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {testrayStatusMetricByTestrayRoutineIdTestrayRoutineTestrayBuildsMetrics(page: ___, pageSize: ___, testrayBuildId: ___, testrayBuildName: ___, testrayProductVersion: ___, testrayRoutineId: ___, testrayTaskStatus: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public TestrayStatusMetricPage
			testrayStatusMetricByTestrayRoutineIdTestrayRoutineTestrayBuildsMetrics(
				@GraphQLName("testrayRoutineId") Long testrayRoutineId,
				@GraphQLName("testrayBuildId") Long testrayBuildId,
				@GraphQLName("testrayBuildName") String testrayBuildName,
				@GraphQLName("testrayProductVersion") String
					testrayProductVersion,
				@GraphQLName("testrayTaskStatus") String testrayTaskStatus,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_testrayStatusMetricResourceComponentServiceObjects,
			this::_populateResourceContext,
			testrayStatusMetricResource -> new TestrayStatusMetricPage(
				testrayStatusMetricResource.
					getTestrayStatusMetricByTestrayRoutineIdTestrayRoutineTestrayBuildsMetricsPage(
						testrayRoutineId, testrayBuildId, testrayBuildName,
						testrayProductVersion, testrayTaskStatus,
						Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {testrayTestFlowTestraySubtask(error: ___, issues: ___, name: ___, noIssues: ___, page: ___, pageSize: ___, status: ___, testrayComponentIds: ___, testrayTaskId: ___, testrayTeamIds: ___, userId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public TestrayTestFlowPage testrayTestFlowTestraySubtask(
			@GraphQLName("error") String error,
			@GraphQLName("issues") String issues,
			@GraphQLName("name") String name,
			@GraphQLName("noIssues") Boolean noIssues,
			@GraphQLName("status") String status,
			@GraphQLName("testrayComponentIds") String testrayComponentIds,
			@GraphQLName("testrayTaskId") Long testrayTaskId,
			@GraphQLName("testrayTeamIds") String testrayTeamIds,
			@GraphQLName("userId") String userId,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_testrayTestFlowResourceComponentServiceObjects,
			this::_populateResourceContext,
			testrayTestFlowResource -> new TestrayTestFlowPage(
				testrayTestFlowResource.getTestrayTestFlowTestraySubtaskPage(
					error, issues, name, noIssues, status, testrayComponentIds,
					testrayTaskId, testrayTeamIds, userId,
					Pagination.of(page, pageSize))));
	}

	@GraphQLName("TestrayCaseResultPage")
	public class TestrayCaseResultPage {

		public TestrayCaseResultPage(Page testrayCaseResultPage) {
			actions = testrayCaseResultPage.getActions();

			items = testrayCaseResultPage.getItems();
			lastPage = testrayCaseResultPage.getLastPage();
			page = testrayCaseResultPage.getPage();
			pageSize = testrayCaseResultPage.getPageSize();
			totalCount = testrayCaseResultPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<TestrayCaseResult> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("TestrayRoutineDurationReportPage")
	public class TestrayRoutineDurationReportPage {

		public TestrayRoutineDurationReportPage(
			Page testrayRoutineDurationReportPage) {

			actions = testrayRoutineDurationReportPage.getActions();

			items = testrayRoutineDurationReportPage.getItems();
			lastPage = testrayRoutineDurationReportPage.getLastPage();
			page = testrayRoutineDurationReportPage.getPage();
			pageSize = testrayRoutineDurationReportPage.getPageSize();
			totalCount = testrayRoutineDurationReportPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<TestrayRoutineDurationReport> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("TestrayRunComparisonPage")
	public class TestrayRunComparisonPage {

		public TestrayRunComparisonPage(Page testrayRunComparisonPage) {
			actions = testrayRunComparisonPage.getActions();

			items = testrayRunComparisonPage.getItems();
			lastPage = testrayRunComparisonPage.getLastPage();
			page = testrayRunComparisonPage.getPage();
			pageSize = testrayRunComparisonPage.getPageSize();
			totalCount = testrayRunComparisonPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<TestrayRunComparison> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("TestrayStatusMetricPage")
	public class TestrayStatusMetricPage {

		public TestrayStatusMetricPage(Page testrayStatusMetricPage) {
			actions = testrayStatusMetricPage.getActions();

			items = testrayStatusMetricPage.getItems();
			lastPage = testrayStatusMetricPage.getLastPage();
			page = testrayStatusMetricPage.getPage();
			pageSize = testrayStatusMetricPage.getPageSize();
			totalCount = testrayStatusMetricPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<TestrayStatusMetric> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("TestrayTestFlowPage")
	public class TestrayTestFlowPage {

		public TestrayTestFlowPage(Page testrayTestFlowPage) {
			actions = testrayTestFlowPage.getActions();

			items = testrayTestFlowPage.getItems();
			lastPage = testrayTestFlowPage.getLastPage();
			page = testrayTestFlowPage.getPage();
			pageSize = testrayTestFlowPage.getPageSize();
			totalCount = testrayTestFlowPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<TestrayTestFlow> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	private <T, R, E1 extends Throwable, E2 extends Throwable> R
			_applyComponentServiceObjects(
				ComponentServiceObjects<T> componentServiceObjects,
				UnsafeConsumer<T, E1> unsafeConsumer,
				UnsafeFunction<T, R, E2> unsafeFunction)
		throws E1, E2 {

		T resource = componentServiceObjects.getService();

		try {
			unsafeConsumer.accept(resource);

			return unsafeFunction.apply(resource);
		}
		finally {
			componentServiceObjects.ungetService(resource);
		}
	}

	private void _populateResourceContext(
			TestrayCaseResultResource testrayCaseResultResource)
		throws Exception {

		testrayCaseResultResource.setContextAcceptLanguage(_acceptLanguage);
		testrayCaseResultResource.setContextCompany(_company);
		testrayCaseResultResource.setContextHttpServletRequest(
			_httpServletRequest);
		testrayCaseResultResource.setContextHttpServletResponse(
			_httpServletResponse);
		testrayCaseResultResource.setContextUriInfo(_uriInfo);
		testrayCaseResultResource.setContextUser(_user);
		testrayCaseResultResource.setGroupLocalService(_groupLocalService);
		testrayCaseResultResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			TestrayRoutineDurationReportResource
				testrayRoutineDurationReportResource)
		throws Exception {

		testrayRoutineDurationReportResource.setContextAcceptLanguage(
			_acceptLanguage);
		testrayRoutineDurationReportResource.setContextCompany(_company);
		testrayRoutineDurationReportResource.setContextHttpServletRequest(
			_httpServletRequest);
		testrayRoutineDurationReportResource.setContextHttpServletResponse(
			_httpServletResponse);
		testrayRoutineDurationReportResource.setContextUriInfo(_uriInfo);
		testrayRoutineDurationReportResource.setContextUser(_user);
		testrayRoutineDurationReportResource.setGroupLocalService(
			_groupLocalService);
		testrayRoutineDurationReportResource.setRoleLocalService(
			_roleLocalService);
	}

	private void _populateResourceContext(
			TestrayRunComparisonResource testrayRunComparisonResource)
		throws Exception {

		testrayRunComparisonResource.setContextAcceptLanguage(_acceptLanguage);
		testrayRunComparisonResource.setContextCompany(_company);
		testrayRunComparisonResource.setContextHttpServletRequest(
			_httpServletRequest);
		testrayRunComparisonResource.setContextHttpServletResponse(
			_httpServletResponse);
		testrayRunComparisonResource.setContextUriInfo(_uriInfo);
		testrayRunComparisonResource.setContextUser(_user);
		testrayRunComparisonResource.setGroupLocalService(_groupLocalService);
		testrayRunComparisonResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			TestrayStatusMetricResource testrayStatusMetricResource)
		throws Exception {

		testrayStatusMetricResource.setContextAcceptLanguage(_acceptLanguage);
		testrayStatusMetricResource.setContextCompany(_company);
		testrayStatusMetricResource.setContextHttpServletRequest(
			_httpServletRequest);
		testrayStatusMetricResource.setContextHttpServletResponse(
			_httpServletResponse);
		testrayStatusMetricResource.setContextUriInfo(_uriInfo);
		testrayStatusMetricResource.setContextUser(_user);
		testrayStatusMetricResource.setGroupLocalService(_groupLocalService);
		testrayStatusMetricResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			TestrayTestFlowResource testrayTestFlowResource)
		throws Exception {

		testrayTestFlowResource.setContextAcceptLanguage(_acceptLanguage);
		testrayTestFlowResource.setContextCompany(_company);
		testrayTestFlowResource.setContextHttpServletRequest(
			_httpServletRequest);
		testrayTestFlowResource.setContextHttpServletResponse(
			_httpServletResponse);
		testrayTestFlowResource.setContextUriInfo(_uriInfo);
		testrayTestFlowResource.setContextUser(_user);
		testrayTestFlowResource.setGroupLocalService(_groupLocalService);
		testrayTestFlowResource.setRoleLocalService(_roleLocalService);
	}

	private static ComponentServiceObjects<TestrayCaseResultResource>
		_testrayCaseResultResourceComponentServiceObjects;
	private static ComponentServiceObjects<TestrayRoutineDurationReportResource>
		_testrayRoutineDurationReportResourceComponentServiceObjects;
	private static ComponentServiceObjects<TestrayRunComparisonResource>
		_testrayRunComparisonResourceComponentServiceObjects;
	private static ComponentServiceObjects<TestrayStatusMetricResource>
		_testrayStatusMetricResourceComponentServiceObjects;
	private static ComponentServiceObjects<TestrayTestFlowResource>
		_testrayTestFlowResourceComponentServiceObjects;

	private AcceptLanguage _acceptLanguage;
	private com.liferay.portal.kernel.model.Company _company;
	private BiFunction
		<Object, String, com.liferay.portal.kernel.search.filter.Filter>
			_filterBiFunction;
	private GroupLocalService _groupLocalService;
	private HttpServletRequest _httpServletRequest;
	private HttpServletResponse _httpServletResponse;
	private RoleLocalService _roleLocalService;
	private BiFunction<Object, String, com.liferay.portal.kernel.search.Sort[]>
		_sortsBiFunction;
	private UriInfo _uriInfo;
	private com.liferay.portal.kernel.model.User _user;

}