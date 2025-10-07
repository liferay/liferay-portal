/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useCallback, useMemo} from 'react';

import SearchBuilder from '../../core/SearchBuilder';
import {
	APIResponse,
	FacetAggregation,
	TestrayBuild,
	TestrayCaseDetail,
	TestrayJiraIssue,
} from '../../services/rest';
import {chartColors} from '../../util/constants';
import {CaseResultStatuses} from '../../util/statuses';
import {useFetch} from '../useFetch';

function getStatusesMap(
	facetAggregation: FacetAggregation | undefined
): Map<string, number> {
	const facetValueMap: Map<string, number> = new Map();

	if (!facetAggregation?.facets) {
		return facetValueMap;
	}

	for (const facet of facetAggregation.facets) {
		for (const facetValue of facet.facetValues) {
			facetValueMap.set(facetValue.term, facetValue.numberOfOccurrences);
		}
	}

	return facetValueMap;
}

const getAggregationValue = (value: number | string) =>
	value ? Number(value) : 0;

const useTotalTestCasesByTestrayBuild = (testrayBuild: TestrayBuild) => {
	const donutColumns = useMemo(
		() => [
			[
				CaseResultStatuses.PASSED,
				getAggregationValue(testrayBuild.caseResultPassed),
			],
			[
				CaseResultStatuses.FAILED,
				getAggregationValue(testrayBuild.caseResultFailed),
			],
			[
				CaseResultStatuses.BLOCKED,
				getAggregationValue(testrayBuild.caseResultBlocked),
			],
			[
				CaseResultStatuses.TEST_FIX,
				getAggregationValue(testrayBuild.caseResultTestFix),
			],
			[
				CaseResultStatuses.INCOMPLETE,
				getAggregationValue(testrayBuild.caseResultIncomplete) +
					getAggregationValue(testrayBuild.caseResultUntested),
			],
		],
		[
			testrayBuild.caseResultBlocked,
			testrayBuild.caseResultFailed,
			testrayBuild.caseResultIncomplete,
			testrayBuild.caseResultPassed,
			testrayBuild.caseResultTestFix,
			testrayBuild.caseResultUntested,
		]
	);

	return useMemo(
		() => ({
			colors: chartColors,
			donut: {
				columns: donutColumns,
				total: donutColumns
					.map(([, totalCase]) => Number(totalCase))
					.reduce(
						(previousValue, currentValue) =>
							previousValue + currentValue
					),
			},
			ready: !!testrayBuild,
			statuses: Object.values(CaseResultStatuses),
		}),
		[donutColumns, testrayBuild]
	);
};

const useTotalTestCasesByTestrayJiraIssue = (
	testrayBuildId: number,
	testrayJiraIssue: TestrayJiraIssue
) => {
	const {data, loading} = useFetch<APIResponse<TestrayCaseDetail>>(
		`/casedetails`,
		{
			params: {
				aggregationTerms: 'dueStatus',
				fields: 'id',
				filter: `caseDetailsToJiraIssues/r_${testrayJiraIssue.issueType.key.toLowerCase()}_c_jiraIssueId eq '${testrayJiraIssue.id}' and r_buildToCaseDetail_c_buildId eq '${testrayBuildId}'`,
				pageSize: 10,
			},
		}
	);

	const donutColumns = useMemo(() => {
		if (!data) {
			return [
				[CaseResultStatuses.PASSED, 0],
				[CaseResultStatuses.FAILED, 0],
				[CaseResultStatuses.BLOCKED, 0],
				[CaseResultStatuses.TEST_FIX, 0],
				[CaseResultStatuses.INCOMPLETE, 0],
			];
		}

		const statusCounts: Record<string, number> = {
			BLOCKED: 0,
			FAILED: 0,
			INCOMPLETE: 0,
			PASSED: 0,
			TEST_FIX: 0,
			UNTESTED: 0,
		};

		data.facets[0].facetValues.forEach(({numberOfOccurrences, term}) => {
			if (term in statusCounts) {
				statusCounts[term] = numberOfOccurrences;
			}
		});

		return [
			[CaseResultStatuses.PASSED, statusCounts.PASSED],
			[CaseResultStatuses.FAILED, statusCounts.FAILED],
			[CaseResultStatuses.BLOCKED, statusCounts.BLOCKED],
			[CaseResultStatuses.TEST_FIX, statusCounts.TEST_FIX],
			[
				CaseResultStatuses.INCOMPLETE,
				statusCounts.INCOMPLETE + statusCounts.UNTESTED,
			],
		];
	}, [data]);

	return useMemo(
		() => ({
			colors: chartColors,
			donut: {
				columns: donutColumns,
				total: donutColumns
					?.map(([, totalCase]) => Number(totalCase))
					.reduce(
						(previousValue, currentValue) =>
							previousValue + currentValue
					),
			},
			ready: !loading,
			statuses: Object.values(CaseResultStatuses),
		}),
		[donutColumns, loading]
	);
};

const useCaseResultGroupBy = (buildId: number = 0) => {
	const {data, loading} = useFetch<
		APIResponse<TestrayBuild> & FacetAggregation
	>('/caseresults', {
		params: {
			aggregationTerms: 'dueStatus',
			fields: 'id',
			filter: SearchBuilder.eq('buildId', buildId),
		},
	});

	const statuses = useMemo(() => getStatusesMap(data), [data]);

	const getStatusValue = useCallback(
		(status: string | number) => statuses.get(String(status)) || 0,
		[statuses]
	);

	const donutColumns = [
		[CaseResultStatuses.PASSED, getStatusValue(CaseResultStatuses.PASSED)],
		[CaseResultStatuses.FAILED, getStatusValue(CaseResultStatuses.FAILED)],
		[
			CaseResultStatuses.BLOCKED,
			getStatusValue(CaseResultStatuses.BLOCKED),
		],
		[
			CaseResultStatuses.TEST_FIX,
			getStatusValue(CaseResultStatuses.TEST_FIX),
		],
		[
			CaseResultStatuses.INCOMPLETE,
			getStatusValue(CaseResultStatuses.INCOMPLETE) +
				getStatusValue(CaseResultStatuses.UNTESTED),
		],
	];

	return {
		colors: chartColors,
		donut: {
			columns: donutColumns,
			total: donutColumns
				.map(([, totalCase]) => totalCase)
				.reduce(
					(previousValue, currentValue) =>
						Number(previousValue) + Number(currentValue)
				),
		},
		ready: !loading && statuses.size > 0,
		statuses: Object.values(CaseResultStatuses),
	};
};

export {useTotalTestCasesByTestrayBuild, useTotalTestCasesByTestrayJiraIssue};

export default useCaseResultGroupBy;
