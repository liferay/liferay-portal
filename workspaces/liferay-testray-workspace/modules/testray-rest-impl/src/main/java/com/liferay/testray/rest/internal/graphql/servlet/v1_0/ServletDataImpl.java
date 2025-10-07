package com.liferay.testray.rest.internal.graphql.servlet.v1_0;

import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.vulcan.graphql.servlet.ServletData;
import com.liferay.testray.rest.internal.graphql.mutation.v1_0.Mutation;
import com.liferay.testray.rest.internal.graphql.query.v1_0.Query;
import com.liferay.testray.rest.internal.resource.v1_0.TestrayBuildAutofillResourceImpl;
import com.liferay.testray.rest.internal.resource.v1_0.TestrayBuildResourceImpl;
import com.liferay.testray.rest.internal.resource.v1_0.TestrayCaseResultResourceImpl;
import com.liferay.testray.rest.internal.resource.v1_0.TestrayRoutineDurationReportResourceImpl;
import com.liferay.testray.rest.internal.resource.v1_0.TestrayRunComparisonResourceImpl;
import com.liferay.testray.rest.internal.resource.v1_0.TestrayStatusMetricResourceImpl;
import com.liferay.testray.rest.internal.resource.v1_0.TestrayTestFlowResourceImpl;
import com.liferay.testray.rest.internal.resource.v1_0.TestrayTestSuiteResourceImpl;
import com.liferay.testray.rest.resource.v1_0.TestrayBuildAutofillResource;
import com.liferay.testray.rest.resource.v1_0.TestrayBuildResource;
import com.liferay.testray.rest.resource.v1_0.TestrayCaseResultResource;
import com.liferay.testray.rest.resource.v1_0.TestrayRoutineDurationReportResource;
import com.liferay.testray.rest.resource.v1_0.TestrayRunComparisonResource;
import com.liferay.testray.rest.resource.v1_0.TestrayStatusMetricResource;
import com.liferay.testray.rest.resource.v1_0.TestrayTestFlowResource;
import com.liferay.testray.rest.resource.v1_0.TestrayTestSuiteResource;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Generated;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentServiceObjects;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceScope;

/**
 * @author Nilton Vieira
 * @generated
 */
@Component(service = ServletData.class)
@Generated("")
public class ServletDataImpl implements ServletData {

	@Activate
	public void activate(BundleContext bundleContext) {
		Mutation.setTestrayBuildResourceComponentServiceObjects(
			_testrayBuildResourceComponentServiceObjects);
		Mutation.setTestrayBuildAutofillResourceComponentServiceObjects(
			_testrayBuildAutofillResourceComponentServiceObjects);
		Mutation.setTestrayTestFlowResourceComponentServiceObjects(
			_testrayTestFlowResourceComponentServiceObjects);
		Mutation.setTestrayTestSuiteResourceComponentServiceObjects(
			_testrayTestSuiteResourceComponentServiceObjects);

		Query.setTestrayCaseResultResourceComponentServiceObjects(
			_testrayCaseResultResourceComponentServiceObjects);
		Query.setTestrayRoutineDurationReportResourceComponentServiceObjects(
			_testrayRoutineDurationReportResourceComponentServiceObjects);
		Query.setTestrayRunComparisonResourceComponentServiceObjects(
			_testrayRunComparisonResourceComponentServiceObjects);
		Query.setTestrayStatusMetricResourceComponentServiceObjects(
			_testrayStatusMetricResourceComponentServiceObjects);
		Query.setTestrayTestFlowResourceComponentServiceObjects(
			_testrayTestFlowResourceComponentServiceObjects);
	}

	public String getApplicationName() {
		return "Liferay.Testray.REST";
	}

	@Override
	public Mutation getMutation() {
		return new Mutation();
	}

	@Override
	public String getPath() {
		return "/testray-rest-graphql/v1_0";
	}

	@Override
	public Query getQuery() {
		return new Query();
	}

	public ObjectValuePair<Class<?>, String> getResourceMethodObjectValuePair(
		String methodName, boolean mutation) {

		if (mutation) {
			return _resourceMethodObjectValuePairs.get(
				"mutation#" + methodName);
		}

		return _resourceMethodObjectValuePairs.get("query#" + methodName);
	}

	private static final Map<String, ObjectValuePair<Class<?>, String>>
		_resourceMethodObjectValuePairs =
			new HashMap<String, ObjectValuePair<Class<?>, String>>() {
				{
					put(
						"mutation#patchTestrayBuild",
						new ObjectValuePair<>(
							TestrayBuildResourceImpl.class,
							"patchTestrayBuild"));
					put(
						"mutation#createTestrayBuildAutofill",
						new ObjectValuePair<>(
							TestrayBuildAutofillResourceImpl.class,
							"postTestrayBuildAutofill"));
					put(
						"mutation#createTestrayBuildAutofillBatch",
						new ObjectValuePair<>(
							TestrayBuildAutofillResourceImpl.class,
							"postTestrayBuildAutofillBatch"));
					put(
						"mutation#createTestrayTestFlow",
						new ObjectValuePair<>(
							TestrayTestFlowResourceImpl.class,
							"postTestrayTestFlow"));
					put(
						"mutation#createTestrayTestFlowBatch",
						new ObjectValuePair<>(
							TestrayTestFlowResourceImpl.class,
							"postTestrayTestFlowBatch"));
					put(
						"mutation#updateTestrayTestFlowByTestraySubtaskIdTestraySubtask",
						new ObjectValuePair<>(
							TestrayTestFlowResourceImpl.class,
							"putTestrayTestFlowByTestraySubtaskIdTestraySubtask"));
					put(
						"mutation#updateTestrayTestFlowTestraySubtaskMergePage",
						new ObjectValuePair<>(
							TestrayTestFlowResourceImpl.class,
							"putTestrayTestFlowTestraySubtaskMergePage"));
					put(
						"mutation#createTestrayTestSuite",
						new ObjectValuePair<>(
							TestrayTestSuiteResourceImpl.class,
							"postTestrayTestSuite"));
					put(
						"mutation#createTestrayTestSuiteBatch",
						new ObjectValuePair<>(
							TestrayTestSuiteResourceImpl.class,
							"postTestrayTestSuiteBatch"));

					put(
						"query#testrayCaseResultHistoryTestrayCase",
						new ObjectValuePair<>(
							TestrayCaseResultResourceImpl.class,
							"getTestrayCaseResultHistoryTestrayCasePage"));
					put(
						"query#testrayCaseResultsTestrayBuild",
						new ObjectValuePair<>(
							TestrayCaseResultResourceImpl.class,
							"getTestrayCaseResultsTestrayBuildPage"));
					put(
						"query#testrayExportCaseResultTestrayBuild",
						new ObjectValuePair<>(
							TestrayCaseResultResourceImpl.class,
							"getTestrayExportCaseResultTestrayBuild"));
					put(
						"query#testrayRoutineDurationReportsTestrayRoutine",
						new ObjectValuePair<>(
							TestrayRoutineDurationReportResourceImpl.class,
							"getTestrayRoutineDurationReportsTestrayRoutinePage"));
					put(
						"query#testrayRunComparison",
						new ObjectValuePair<>(
							TestrayRunComparisonResourceImpl.class,
							"getTestrayRunComparison"));
					put(
						"query#testrayRunComparisonByTestrayRoutineIdTestrayRoutine",
						new ObjectValuePair<>(
							TestrayRunComparisonResourceImpl.class,
							"getTestrayRunComparisonByTestrayRoutineIdTestrayRoutine"));
					put(
						"query#testrayRunComparisonRun",
						new ObjectValuePair<>(
							TestrayRunComparisonResourceImpl.class,
							"getTestrayRunComparisonRun"));
					put(
						"query#testrayRunComparisonTestrayCaseResultComparisons",
						new ObjectValuePair<>(
							TestrayRunComparisonResourceImpl.class,
							"getTestrayRunComparisonTestrayCaseResultComparisonsPage"));
					put(
						"query#testrayStatusMetricByTestrayBuildIdTestrayBuildTestrayCaseTypesMetrics",
						new ObjectValuePair<>(
							TestrayStatusMetricResourceImpl.class,
							"getTestrayStatusMetricByTestrayBuildIdTestrayBuildTestrayCaseTypesMetricsPage"));
					put(
						"query#testrayStatusMetricByTestrayBuildIdTestrayBuildTestrayComponentsMetrics",
						new ObjectValuePair<>(
							TestrayStatusMetricResourceImpl.class,
							"getTestrayStatusMetricByTestrayBuildIdTestrayBuildTestrayComponentsMetricsPage"));
					put(
						"query#testrayStatusMetricByTestrayBuildIdTestrayBuildTestrayRunsMetrics",
						new ObjectValuePair<>(
							TestrayStatusMetricResourceImpl.class,
							"getTestrayStatusMetricByTestrayBuildIdTestrayBuildTestrayRunsMetricsPage"));
					put(
						"query#testrayStatusMetricByTestrayBuildIdTestrayBuildTestrayTeamsMetrics",
						new ObjectValuePair<>(
							TestrayStatusMetricResourceImpl.class,
							"getTestrayStatusMetricByTestrayBuildIdTestrayBuildTestrayTeamsMetricsPage"));
					put(
						"query#testrayStatusMetricByTestrayJiraIssueIdTestrayJiraIssueTestrayIssuesMetrics",
						new ObjectValuePair<>(
							TestrayStatusMetricResourceImpl.class,
							"getTestrayStatusMetricByTestrayJiraIssueIdTestrayJiraIssueTestrayIssuesMetricsPage"));
					put(
						"query#testrayStatusMetricByTestrayProjectIdTestrayProjectTestrayRoutinesMetrics",
						new ObjectValuePair<>(
							TestrayStatusMetricResourceImpl.class,
							"getTestrayStatusMetricByTestrayProjectIdTestrayProjectTestrayRoutinesMetricsPage"));
					put(
						"query#testrayStatusMetricByTestrayRoutineIdTestrayRoutineTestrayBuildsMetrics",
						new ObjectValuePair<>(
							TestrayStatusMetricResourceImpl.class,
							"getTestrayStatusMetricByTestrayRoutineIdTestrayRoutineTestrayBuildsMetricsPage"));
					put(
						"query#testrayTestFlowTestraySubtask",
						new ObjectValuePair<>(
							TestrayTestFlowResourceImpl.class,
							"getTestrayTestFlowTestraySubtaskPage"));
				}
			};

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<TestrayBuildResource>
		_testrayBuildResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<TestrayBuildAutofillResource>
		_testrayBuildAutofillResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<TestrayTestFlowResource>
		_testrayTestFlowResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<TestrayTestSuiteResource>
		_testrayTestSuiteResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<TestrayCaseResultResource>
		_testrayCaseResultResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<TestrayRoutineDurationReportResource>
		_testrayRoutineDurationReportResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<TestrayRunComparisonResource>
		_testrayRunComparisonResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<TestrayStatusMetricResource>
		_testrayStatusMetricResourceComponentServiceObjects;

}