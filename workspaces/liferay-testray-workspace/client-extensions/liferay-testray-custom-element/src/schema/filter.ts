/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {RendererFields} from '../components/Form/Renderer';
import SearchBuilder from '../core/SearchBuilder';
import i18n from '../i18n';
import {
	TestrayCaseType,
	TestrayComponent,
	TestrayProductVersion,
	TestrayProject,
	TestrayRoutine,
	TestrayRun,
	TestrayTeam,
	UserAccount,
} from '../services/rest';
import {
	BuildStatuses,
	CaseResultStatuses,
	SubtaskStatuses,
	TaskStatuses,
} from '../util/statuses';

export type Filters = {
	[key: string]: RendererFields[];
};

type Filter = {
	[key: string]: RendererFields;
};

export type FilterVariables = {
	appliedFilter: {
		[key: string]: string;
	};
	defaultFilter: string | SearchBuilder;
	filterSchema: FilterSchema;
};

export type FilterSchema = {
	fields: RendererFields[];
	name?: string;
	onApply?: (filterVariables: FilterVariables) => string;
	placeholder?: string;
};

export type FilterSchemas = {
	[key: string]: FilterSchema;
};

export type FilterSchemaOption = keyof typeof filterSchema;

const transformData = <T = any>(response: any): T[] => {
	return response?.items || [];
};

const dataToOptions = <T = any>(
	entries: T[],
	transformAction?: (entry: T) => {label: string; value: number | string}
) =>
	entries.map((entry: any) =>
		transformAction
			? transformAction(entry)
			: {label: entry.name, value: entry.id}
	);

const baseFilters: Filter = {
	assignee: {
		label: i18n.translate('assignee'),
		name: 'assignedUsers',
		resource: '/user-accounts',
		transformData(item) {
			return dataToOptions(
				transformData<UserAccount>(item),
				(userAccount) => ({
					label: `${userAccount.name}`,
					value: userAccount.id,
				})
			);
		},
		type: 'select',
	},
	caseType: {
		label: i18n.translate('case-type'),
		name: 'caseType',
		resource: '/casetypes?fields=id,name&pageSize=-1&sort=name:asc',
		transformData(item) {
			return dataToOptions(transformData<TestrayCaseType>(item));
		},
		type: 'multiselect',
	},
	component: {
		label: i18n.translate('component'),
		name: 'componentId',
		resource: ({projectId}) => {
			const filter = `${SearchBuilder.eq(
				'projectId',
				projectId as string
			)}`;

			return `/components?fields=id,name&filter=${filter}&pageSize=-1&sort=name:asc`;
		},

		transformData(item) {
			return dataToOptions(transformData<TestrayComponent>(item));
		},
		type: 'select',
	},
	description: {
		label: i18n.translate('description'),
		name: 'description',
		operator: 'contains',
		optionalOperator: 'ne',
		type: 'textarea',
	},
	dueStatus: {
		label: i18n.translate('status'),
		name: 'dueStatus',
		type: 'checkbox',
	},
	erros: {
		label: i18n.translate('errors'),
		name: 'errors',
		operator: 'contains',
		optionalOperator: 'ne',
		type: 'textarea',
	},
	hasRequirements: {
		label: i18n.translate('has-requirements'),
		name: 'caseToRequirementsCases/id',
		operator: 'eq',
		optionalOperator: 'ne',
		options: ['true', 'false'],
		type: 'select',
	},
	issues: {
		label: i18n.translate('issues'),
		name: 'issues',
		optionalOperator: 'ne',
		type: 'textarea',
	},
	priority: {
		label: i18n.translate('priority'),
		name: 'priority',
		options: ['5', '4', '3', '2', '1'],
		type: 'multiselect',
	},
	productVersion: {
		label: i18n.translate('product-version'),
		name: 'productVersion',
		resource: ({projectId}) => {
			const filter = `${SearchBuilder.eq(
				'projectId',
				projectId as string
			)}`;

			return `/productversions?fields=id,name&filter=${filter}&pageSize=-1&sort=name:asc`;
		},
		transformData(item) {
			return dataToOptions(transformData<TestrayProductVersion>(item));
		},
		type: 'select',
	},
	project: {
		label: i18n.translate('project'),
		name: 'projectId',
		resource: '/projects?fields=id,name&pageSize=-1',
		transformData(item) {
			return dataToOptions(transformData<TestrayProject>(item));
		},
		type: 'select',
	},
	routine: {
		label: i18n.translate('routines'),
		name: 'routines',
		resource: ({projectId}) => {
			const filter = `${SearchBuilder.eq(
				'projectId',
				projectId as string
			)}`;

			return `/routines?fields=id,name&filter=${filter}&pageSize=-1`;
		},
		transformData(item) {
			return dataToOptions(transformData<TestrayRoutine>(item));
		},
		type: 'select',
	},
	run: {
		label: i18n.translate('run'),
		name: 'run',
		resource: ({buildId}) =>
			`/runs?fields=id,number&filter=${SearchBuilder.eq(
				'buildId',
				buildId as string
			)}`,
		transformData(item) {
			return dataToOptions(transformData<TestrayRun>(item), (run) => ({
				label: run?.number?.toString().padStart(2, '0'),
				value: run.id,
			}));
		},
		type: 'select',
	},
	steps: {
		label: i18n.translate('steps'),
		name: 'steps',
		operator: 'contains',
		optionalOperator: 'ne',
		type: 'textarea',
	},
	team: {
		label: i18n.translate('team'),
		name: 'teamId',
		resource: ({projectId}) => {
			const filter = `${SearchBuilder.eq(
				'projectId',
				projectId as string
			)}`;

			return `/teams?fields=id,name&filter=${filter}&pageSize=-1&sort=name:asc`;
		},
		transformData(item) {
			return dataToOptions(transformData<TestrayTeam>(item));
		},
		type: 'select',
	},
	user: {label: i18n.translate('name'), name: 'name', type: 'text'},
};

const overrides = (
	object: RendererFields,
	newObject: Partial<RendererFields>
) => ({
	...object,
	...newObject,
});

const filterSchema = {
	buildCaseTypes: {
		fields: [
			overrides(baseFilters.priority, {
				isCustomFilter: true,
				name: 'testrayCasePriorities',
				removeQuoteMark: true,
			}),
			overrides(baseFilters.team, {
				isCustomFilter: true,
				name: 'testrayTeamIds',
				resource: ({projectId}) => {
					const filter = `${SearchBuilder.eq(
						'projectId',
						projectId as string
					)}`;

					return `/teams?fields=id,name&filter=${filter}&pageSize=-1&sort=name:asc`;
				},
				type: 'multiselect',
			}),
		] as RendererFields[],
		name: 'buildCaseTypes',
	},
	buildComponents: {
		fields: [
			overrides(baseFilters.priority, {
				isCustomFilter: true,
				name: 'testrayCasePriorities',
				removeQuoteMark: true,
				type: 'select',
			}),
			overrides(baseFilters.caseType, {
				isCustomFilter: true,
				name: 'testrayCaseTypes',
			}),
			overrides(baseFilters.team, {
				isCustomFilter: true,
				name: 'testrayTeamIds',
				resource: ({projectId}) => {
					const filter = `${SearchBuilder.eq(
						'projectId',
						projectId as string
					)}`;

					return `/teams?fields=id,name&filter=${filter}&pageSize=-1&sort=name:asc`;
				},
				type: 'multiselect',
			}),
			overrides(baseFilters.run, {
				isCustomFilter: true,
				name: 'testrayRunId',
				transformData(item) {
					return dataToOptions(
						transformData<TestrayRun>(item),
						(run) => ({
							label: run?.number?.toString().padStart(2, '0'),
							value: run?.id,
						})
					);
				},
			}),
		] as RendererFields[],
		name: 'buildComponents',
	},
	buildResults: {
		fields: [
			overrides(baseFilters.caseType, {
				isCustomFilter: true,
				name: 'testrayCaseTypeIds',
				type: 'multiselect',
			}),
			{
				isCustomFilter: true,
				label: i18n.translate('flaky'),
				name: 'flaky',
				options: [
					{
						label: i18n.translate('true'),
						value: true,
					},
					{
						label: i18n.translate('false'),
						value: false,
					},
				],
				removeQuoteMark: true,
				type: 'select',
			},
			overrides(baseFilters.priority, {
				isCustomFilter: true,
				name: 'priority',
				removeQuoteMark: true,
				type: 'multiselect',
			}),
			overrides(baseFilters.team, {
				isCustomFilter: true,
				name: 'testrayTeamIds',
				resource: ({projectId}) => {
					const filter = `${SearchBuilder.eq(
						'projectId',
						projectId as string
					)}`;

					return `/teams?fields=id,name&filter=${filter}&pageSize=-1&sort=name:asc`;
				},
				type: 'multiselect',
			}),
			overrides(baseFilters.component, {
				isCustomFilter: true,
				name: 'testrayComponentIds',
				resource: ({projectId}) => {
					const filter = `${SearchBuilder.eq(
						'projectId',
						projectId as string
					)}`;

					return `/components?fields=id,name&filter=${filter}&pageSize=-1&sort=name:asc`;
				},
				type: 'multiselect',
			}),
			{
				isCustomFilter: true,
				label: i18n.translate('environment'),
				name: 'testrayRunName',

				type: 'text',
			},
			overrides(baseFilters.run, {
				isCustomFilter: true,
				name: 'testrayRunId',
				type: 'select',
			}),
			{
				isCustomFilter: true,
				label: i18n.translate('case-name'),
				name: 'testrayCaseName',
				type: 'text',
			},
			overrides(baseFilters.assignee, {
				isCustomFilter: true,
				name: 'userId',
			}),
			overrides(baseFilters.dueStatus, {
				isCustomFilter: true,
				name: 'status',
				options: [
					{
						label: i18n.translate('blocked'),
						value: CaseResultStatuses.BLOCKED,
					},
					{
						label: i18n.translate('failed'),
						value: CaseResultStatuses.FAILED,
					},
					{
						label: i18n.translate('in-progress'),
						value: CaseResultStatuses.IN_PROGRESS,
					},
					{
						label: i18n.translate('incomplete'),
						value: CaseResultStatuses.INCOMPLETE,
					},
					{
						label: i18n.translate('passed'),
						value: CaseResultStatuses.PASSED,
					},
					{
						label: i18n.translate('test-fix'),
						value: CaseResultStatuses.TEST_FIX,
					},
					{
						label: i18n.translate('untested'),
						value: CaseResultStatuses.UNTESTED,
					},
				],
			}),
			overrides(baseFilters.issues, {
				isCustomFilter: true,
				name: 'issues',
			}),
			{
				isCustomFilter: true,
				label: i18n.translate('errors'),
				name: 'error',
				optionalOperator: 'ne',
				type: 'textarea',
			},
			{
				isCustomFilter: true,
				label: i18n.translate('comments'),
				name: 'comment',
				type: 'textarea',
			},
		] as RendererFields[],
		name: 'buildResults',
	},
	buildResultsHistory: {
		fields: [
			overrides(baseFilters.productVersion, {
				isCustomFilter: true,
				label: i18n.translate('product-version-name'),
				name: 'testrayProductVersionIds',
				type: 'multiselect',
			}),
			{
				isCustomFilter: true,
				label: i18n.translate('environment'),
				name: 'testrayRunName',
				type: 'text',
			},
			overrides(baseFilters.routine, {
				isCustomFilter: true,
				name: 'testrayRoutineIds',
				type: 'multiselect',
			}),
			overrides(baseFilters.team, {
				isCustomFilter: true,
				name: 'testrayTeamIds',
				type: 'multiselect',
			}),
			overrides(baseFilters.assignee, {
				isCustomFilter: true,
				name: 'userId',
			}),
			overrides(baseFilters.dueStatus, {
				isCustomFilter: true,
				name: 'status',
				options: [
					{
						label: 'Blocked',
						value: CaseResultStatuses.BLOCKED,
					},
					{
						label: 'Failed',
						value: CaseResultStatuses.FAILED,
					},
					{
						label: 'In Progress',
						value: CaseResultStatuses.IN_PROGRESS,
					},
					{
						label: 'Incomplete',
						value: CaseResultStatuses.INCOMPLETE,
					},
					{
						label: 'Passed',
						value: CaseResultStatuses.PASSED,
					},
					{
						label: 'Test Fix',
						value: CaseResultStatuses.TEST_FIX,
					},
					{
						label: 'Untested',
						value: CaseResultStatuses.UNTESTED,
					},
				],
			}),
			overrides(baseFilters.issues, {
				isCustomFilter: true,
				name: 'issues',
			}),
			{
				isCustomFilter: true,
				label: i18n.translate('errors'),
				name: 'error',
				optionalOperator: 'ne',
				type: 'textarea',
			},
			{
				isCustomFilter: true,
				label: i18n.translate('case-result-warning'),
				name: 'warning',
				type: 'number',
			},
			{
				isCustomFilter: true,
				label: i18n.sub('x-execution-date', 'min'),
				name: 'minExecutionDate',
				type: 'date',
			},
			{
				isCustomFilter: true,
				label: i18n.sub('x-execution-date', 'max'),
				name: 'maxExecutionDate',
				type: 'date',
			},
		] as RendererFields[],
		name: 'buildResultsHistory',
	},
	buildRuns: {
		fields: [
			overrides(baseFilters.priority, {
				isCustomFilter: true,
				name: 'testrayCasePriorities',
				removeQuoteMark: true,
				type: 'multiselect',
			}),
			overrides(baseFilters.caseType, {
				isCustomFilter: true,
				name: 'testrayCaseTypes',
			}),
			overrides(baseFilters.team, {
				isCustomFilter: true,
				name: 'testrayTeamIds',
				resource: ({projectId}) => {
					const filter = `${SearchBuilder.eq(
						'projectId',
						projectId as string
					)}`;

					return `/teams?fields=id,name&filter=${filter}&pageSize=-1&sort=name:asc`;
				},
				type: 'multiselect',
			}),
		] as RendererFields[],
		name: 'buildRuns',
	},
	buildTeams: {
		fields: [
			overrides(baseFilters.priority, {
				isCustomFilter: true,
				name: 'testrayCasePriorities',
				removeQuoteMark: true,
			}),
			overrides(baseFilters.caseType, {
				isCustomFilter: true,
				name: 'testrayCaseTypes',
			}),
			overrides(baseFilters.team, {
				isCustomFilter: true,
				name: 'testrayTeamIds',
				resource: ({projectId}) => {
					const filter = `${SearchBuilder.eq(
						'projectId',
						projectId as string
					)}`;

					return `/teams?fields=id,name&filter=${filter}&pageSize=-1&sort=name:asc`;
				},
				type: 'multiselect',
			}),
			overrides(baseFilters.run, {
				isCustomFilter: true,
				name: 'testrayRunId',
			}),
		] as RendererFields[],
		name: 'buildTeams',
	},
	buildTemplates: {
		fields: [
			{
				label: i18n.translate('template-name'),
				name: 'name',
				operator: 'contains',
				type: 'text',
			},
			overrides(baseFilters.dueStatus, {
				options: [
					{
						label: i18n.translate('activated'),
						value: BuildStatuses.ACTIVATED,
					},
					{
						label: i18n.translate('deactivated'),
						value: BuildStatuses.DEACTIVATED,
					},
				],
				type: 'select',
			}),
		] as RendererFields[],
		name: 'buildTemplates',
	},
	builds: {
		fields: [
			overrides(baseFilters.productVersion, {
				isCustomFilter: true,
				name: 'testrayProductVersion',
				type: 'select',
			}),
			{
				isCustomFilter: true,
				label: i18n.translate('build-name'),
				name: 'testrayBuildName',
				type: 'text',
			},
			{
				isCustomFilter: true,
				label: i18n.translate('status'),
				name: 'testrayTaskStatus',
				options: [
					{
						label: i18n.translate('abandoned'),
						value: TaskStatuses.ABANDONED,
					},
					{
						label: i18n.translate('complete'),
						value: TaskStatuses.COMPLETE,
					},
					{
						label: i18n.translate('in-analysis'),
						value: TaskStatuses.IN_ANALYSIS,
					},
					{
						label: i18n.translate('open'),
						value: TaskStatuses.OPEN,
					},
				],
				type: 'checkbox',
			},
		] as RendererFields[],
		name: 'builds',
	},
	caseRequirements: {
		fields: [
			{
				label: i18n.translate('key'),
				name: 'requiremenToRequirementsCases/key',
				operator: 'contains',
				type: 'text',
			},
			{
				label: i18n.translate('link'),
				name: 'requiremenToRequirementsCases/linkURL',
				operator: 'contains',
				type: 'text',
			},
			{
				label: i18n.translate('jira-components'),
				name: 'requiremenToRequirementsCases/components',
				operator: 'contains',
				type: 'text',
			},
			{
				label: i18n.translate('summary'),
				name: 'requiremenToRequirementsCases/summary',
				operator: 'contains',
				type: 'text',
			},
		] as RendererFields[],
		name: 'caseRequirements',
	},
	cases: {
		fields: [
			overrides(baseFilters.priority, {
				removeQuoteMark: true,
				type: 'multiselect',
			}),
			overrides(baseFilters.caseType, {
				name: 'r_caseTypeToCases_c_caseTypeId',
			}),
			{
				label: i18n.translate('case-name'),
				name: 'name',
				operator: 'contains',
				type: 'text',
			},
			{
				label: i18n.translate('Flaky'),
				name: 'flaky',
				options: [
					{
						label: i18n.translate('true'),
						value: true,
					},
					{
						label: i18n.translate('false'),
						value: false,
					},
				],
				removeQuoteMark: true,
				type: 'select',
			},
			overrides(baseFilters.team, {
				name: 'componentToCases/r_teamToComponents_c_teamId',
				type: 'multiselect',
			}),
			overrides(baseFilters.component, {
				name: 'componentId',
				type: 'multiselect',
			}),
			baseFilters.description,
			baseFilters.steps,
			overrides(baseFilters.issues, {
				name: 'caseToCaseResult/issues',
				operator: 'contains',
			}),
			baseFilters.hasRequirements,
		] as RendererFields[],
		name: 'cases',
	},
	compareRunsCases: {
		fields: [
			overrides(baseFilters.priority, {
				name: 'caseToCaseResult/priority',
				removeQuoteMark: true,
				type: 'multiselect',
			}),
			overrides(baseFilters.team, {
				label: 'Teams',
				name: 'componentToCaseResult/teamToComponents/name',
				resource: ({runA, runB}) => {
					const filter = `${SearchBuilder.eq(
						'teamToComponents/componentToCaseResult/r_runToCaseResult_c_runId',
						runA as string
					)} or ${SearchBuilder.eq(
						'teamToComponents/componentToCaseResult/r_runToCaseResult_c_runId',
						runB as string
					)}`;

					return `/teams?filter=${filter}&pageSize=-1&sort=name:asc`;
				},
				type: 'select',
			}),
			overrides(baseFilters.component, {
				label: 'Components',
				name: 'componentToCaseResult/name',
				resource: ({runA, runB}) => {
					const filter = `${SearchBuilder.eq(
						'componentToCaseResult/r_runToCaseResult_c_runId',
						runA as string
					)} or ${SearchBuilder.eq(
						'componentToCaseResult/r_runToCaseResult_c_runId',
						runB as string
					)}`;

					return `/components?filter=${filter}&pageSize=-1&sort=name:asc`;
				},
				type: 'multiselect',
			}),
			{
				label: i18n.translate('case-name'),
				name: 'caseToCaseResult/name',
				operator: 'contains',
				type: 'text',
			},
			{
				isCustomFilter: true,
				label: i18n.sub('status-in-x', 'run-a'),
				name: 'testrayCaseResultStatus1',
				operator: 'eq',
				optionalOperator: 'ne',
				options: [
					{
						label: i18n.translate('passed'),
						value: CaseResultStatuses.PASSED,
					},
					{
						label: i18n.translate('failed'),
						value: CaseResultStatuses.FAILED,
					},
					{
						label: i18n.translate('blocked'),
						value: CaseResultStatuses.BLOCKED,
					},
					{
						label: i18n.translate('test-fix'),
						value: CaseResultStatuses.TEST_FIX,
					},
					{
						label: i18n.translate('dnr'),
						value: CaseResultStatuses.DID_NOT_RUN,
					},
				],
				requestOperator: 'dueStatus',
				type: 'select',
			},
			{
				isCustomFilter: true,
				label: i18n.sub('status-in-x', 'run-b'),
				name: 'testrayCaseResultStatus2',
				operator: 'eq',
				optionalOperator: 'ne',
				options: [
					{
						label: i18n.translate('passed'),
						value: CaseResultStatuses.PASSED,
					},
					{
						label: i18n.translate('failed'),
						value: CaseResultStatuses.FAILED,
					},
					{
						label: i18n.translate('blocked'),
						value: CaseResultStatuses.BLOCKED,
					},
					{
						label: i18n.translate('test-fix'),
						value: CaseResultStatuses.TEST_FIX,
					},
					{
						label: i18n.translate('dnr'),
						value: CaseResultStatuses.DID_NOT_RUN,
					},
				],
				requestOperator: 'dueStatus',
				type: 'select',
			},
			{
				isCustomFilter: true,
				label: i18n.sub('issues-in-x', 'run-a'),
				name: 'testrayCaseResultIssue1',
				operator: 'contains',
				optionalOperator: 'ne',
				requestOperator: 'issues',
				type: 'textarea',
			},
			{
				isCustomFilter: true,
				label: i18n.sub('issues-in-x', 'run-b'),
				name: 'testrayCaseResultIssue2',
				operator: 'contains',
				optionalOperator: 'ne',
				requestOperator: 'issues',
				type: 'textarea',
			},
			{
				isCustomFilter: true,
				label: i18n.sub('error-in-x', 'run-a'),
				name: 'testrayCaseResultError1',
				operator: 'contains',
				requestOperator: 'errors',
				type: 'text',
			},
			{
				isCustomFilter: true,
				label: i18n.sub('error-in-x', 'run-b'),
				name: 'testrayCaseResultError2',
				operator: 'contains',
				requestOperator: 'errors',
				type: 'text',
			},
		] as RendererFields[],

		name: 'compareRunsCases',
	},
	compareRunsTeamsAndComponents: {
		fields: [
			overrides(baseFilters.priority, {
				name: 'caseToCaseResult/priority',
				removeQuoteMark: true,
				type: 'multiselect',
			}),
			overrides(baseFilters.team, {
				name: 'componentToCaseResult/teamId',
				resource: ({runA, runB}) => {
					const filter = `${SearchBuilder.eq(
						'teamToComponents/componentToCaseResult/r_runToCaseResult_c_runId',
						runA as string
					)} or ${SearchBuilder.eq(
						'teamToComponents/componentToCaseResult/r_runToCaseResult_c_runId',
						runB as string
					)}`;

					return `/teams?filter=${filter}&pageSize=-1&sort=name:asc`;
				},

				type: 'select',
			}),
		] as RendererFields[],
		name: 'compareRunsTeamsAndComponents',
	},
	components: {
		fields: [
			{
				label: i18n.translate('component-name'),
				name: 'name',
				operator: 'contains',
				type: 'text',
			},
		] as RendererFields[],
		name: 'components',
	},
	issueResults: {
		fields: [
			{
				isCustomFilter: false,
				label: i18n.translate('case-name'),
				name: 'caseToCaseDetails/name',
				operator: 'contains',
				type: 'text',
			},
			{
				isCustomFilter: false,
				label: i18n.translate('test-name'),
				name: 'name',
				operator: 'contains',
				type: 'text',
			},
			overrides(baseFilters.priority, {
				name: 'caseToCaseDetails/priority',
				removeQuoteMark: true,
			}),
			overrides(baseFilters.dueStatus, {
				isCustomFilter: false,
				name: 'dueStatus',
				options: [
					{
						label: i18n.translate('blocked'),
						value: CaseResultStatuses.BLOCKED,
					},
					{
						label: i18n.translate('failed'),
						value: CaseResultStatuses.FAILED,
					},
					{
						label: i18n.translate('in-progress'),
						value: CaseResultStatuses.IN_PROGRESS,
					},
					{
						label: i18n.translate('incomplete'),
						value: CaseResultStatuses.INCOMPLETE,
					},
					{
						label: i18n.translate('passed'),
						value: CaseResultStatuses.PASSED,
					},
					{
						label: i18n.translate('test-fix'),
						value: CaseResultStatuses.TEST_FIX,
					},
					{
						label: i18n.translate('untested'),
						value: CaseResultStatuses.UNTESTED,
					},
				],
			}),
			overrides(baseFilters.issues, {
				name: 'caseDetailsToJiraIssues/externalReferenceCode',
				operator: 'contains',
			}),
		] as RendererFields[],
		name: 'issueResults',
	},
	issues: {
		fields: [
			{
				isCustomFilter: false,
				label: i18n.translate('issue-key'),
				name: 'externalReferenceCode',
				operator: 'contains',
				type: 'text',
			},
			{
				isCustomFilter: false,
				label: i18n.translate('title'),
				name: 'title',
				operator: 'contains',
				type: 'text',
			},
		] as RendererFields[],
		name: 'issues',
	},
	requirementCases: {
		fields: [
			baseFilters.priority,
			baseFilters.caseType,
			{
				label: i18n.translate('case-name'),
				name: 'caseName',
				type: 'text',
			},
			baseFilters.team,
			{
				label: i18n.translate('component'),
				name: 'component',
				type: 'text',
			},
		] as RendererFields[],
		name: 'requirementCases',
	},
	requirements: {
		fields: [
			{
				label: i18n.translate('key'),
				name: 'key',
				operator: 'contains',
				type: 'text',
			},
			{
				label: i18n.translate('link'),
				name: 'linkTitle',
				operator: 'contains',
				type: 'text',
			},
			overrides(baseFilters.team, {
				name: 'componentToRequirements/r_teamToComponents_c_teamId',
				type: 'multiselect',
			}),
			overrides(baseFilters.component, {type: 'multiselect'}),
			{
				label: i18n.translate('jira-components'),
				name: 'components',
				operator: 'contains',
				type: 'text',
			},
			{
				label: i18n.translate('summary'),
				name: 'summary',
				operator: 'contains',
				type: 'text',
			},
			{
				label: i18n.translate('case'),
				name: 'requiremenToRequirementsCases/caseToRequirementsCases/name',
				operator: 'contains',
				optionalOperator: 'ne',
				type: 'textarea',
			},
		] as RendererFields[],
		name: 'requirements',
	},
	routineDuration: {
		fields: [
			{
				isCustomFilter: true,
				label: i18n.translate('flaky'),
				name: 'flaky',
				options: [
					{
						label: i18n.translate('true'),
						value: true,
					},
					{
						label: i18n.translate('false'),
						value: false,
					},
				],
				removeQuoteMark: true,
				type: 'select',
			},
			overrides(baseFilters.priority, {
				isCustomFilter: true,
				name: 'priority',
				removeQuoteMark: true,
				type: 'multiselect',
			}),
			{
				isCustomFilter: true,
				label: i18n.translate('case-name'),
				name: 'testrayCaseName',
				type: 'text',
			},
			overrides(baseFilters.caseType, {
				isCustomFilter: true,
				name: 'testrayCaseTypeIds',
				type: 'multiselect',
			}),
			overrides(baseFilters.component, {
				isCustomFilter: true,
				name: 'testrayComponentIds',
				type: 'multiselect',
			}),
			overrides(baseFilters.team, {
				isCustomFilter: true,
				name: 'testrayTeamIds',
				type: 'multiselect',
			}),
		] as RendererFields[],
		name: 'routineDuration',
	},
	routines: {
		fields: [
			overrides(baseFilters.priority, {
				isCustomFilter: true,
				name: 'testrayCasePriorities',
				removeQuoteMark: true,
			}),
			overrides(baseFilters.caseType, {
				isCustomFilter: true,
				name: 'testrayCaseTypes',
			}),
			overrides(baseFilters.team, {
				isCustomFilter: true,
				name: 'testrayTeamId',
			}),
		] as RendererFields[],
		name: 'routines',
	},
	subtaskCaseResults: {
		fields: [
			overrides(baseFilters.caseType, {
				isCustomFilter: true,
				name: 'testrayCaseTypeIds',
				type: 'multiselect',
			}),
			{
				isCustomFilter: true,
				label: i18n.translate('flaky'),
				name: 'flaky',
				options: [
					{
						label: i18n.translate('true'),
						value: true,
					},
					{
						label: i18n.translate('false'),
						value: false,
					},
				],
				removeQuoteMark: true,
				type: 'select',
			},
			overrides(baseFilters.priority, {
				isCustomFilter: true,
				name: 'priority',
				removeQuoteMark: true,
				type: 'multiselect',
			}),
			overrides(baseFilters.team, {
				isCustomFilter: true,
				name: 'testrayTeamIds',
				resource: ({projectId}) => {
					const filter = `${SearchBuilder.eq(
						'projectId',
						projectId as string
					)}`;

					return `/teams?fields=id,name&filter=${filter}&pageSize=-1&sort=name:asc`;
				},
				type: 'multiselect',
			}),
			overrides(baseFilters.component, {
				isCustomFilter: true,
				name: 'testrayComponentIds',
				resource: ({projectId}) => {
					const filter = `${SearchBuilder.eq(
						'projectId',
						projectId as string
					)}`;

					return `/components?fields=id,name&filter=${filter}&pageSize=-1&sort=name:asc`;
				},
				type: 'multiselect',
			}),
			{
				isCustomFilter: true,
				label: i18n.translate('environment'),
				name: 'testrayRunName',

				type: 'text',
			},
			overrides(baseFilters.run, {
				isCustomFilter: true,
				name: 'testrayRunId',
				type: 'select',
			}),
			{
				isCustomFilter: true,
				label: i18n.translate('case-name'),
				name: 'testrayCaseName',
				type: 'text',
			},
			overrides(baseFilters.assignee, {
				isCustomFilter: true,
				name: 'userId',
			}),
			overrides(baseFilters.dueStatus, {
				isCustomFilter: true,
				name: 'status',
				options: [
					{
						label: i18n.translate('blocked'),
						value: CaseResultStatuses.BLOCKED,
					},
					{
						label: i18n.translate('failed'),
						value: CaseResultStatuses.FAILED,
					},
					{
						label: i18n.translate('in-progress'),
						value: CaseResultStatuses.IN_PROGRESS,
					},
					{
						label: i18n.translate('incomplete'),
						value: CaseResultStatuses.INCOMPLETE,
					},
					{
						label: i18n.translate('passed'),
						value: CaseResultStatuses.PASSED,
					},
					{
						label: i18n.translate('test-fix'),
						value: CaseResultStatuses.TEST_FIX,
					},
					{
						label: i18n.translate('untested'),
						value: CaseResultStatuses.UNTESTED,
					},
				],
			}),
			overrides(baseFilters.issues, {
				isCustomFilter: true,
				name: 'issues',
			}),
			overrides(baseFilters.erros, {
				isCustomFilter: true,
				name: 'error',
			}),
			{
				isCustomFilter: true,
				label: i18n.translate('comments'),
				name: 'comment',
				type: 'textarea',
			},
		] as RendererFields[],
		name: 'subtaskCaseResults',
	},
	subtasks: {
		fields: [
			{
				isCustomFilter: true,
				label: i18n.translate('subtask-name'),
				name: 'name',
				type: 'text',
			},
			{
				isCustomFilter: true,
				label: i18n.translate('errors'),
				name: 'error',
				type: 'text',
			},
			overrides(baseFilters.assignee, {
				isCustomFilter: true,
				name: 'userId',
			}),
			{
				isCustomFilter: true,
				label: i18n.translate('status'),
				name: 'status',
				options: [
					{
						label: i18n.translate('complete'),
						value: SubtaskStatuses.COMPLETE,
					},
					{
						label: i18n.translate('in-analysis'),
						value: SubtaskStatuses.IN_ANALYSIS,
					},
					{
						label: i18n.translate('open'),
						value: SubtaskStatuses.OPEN,
					},
				],
				type: 'checkbox',
			},
			overrides(baseFilters.issues, {
				isCustomFilter: true,
				name: 'issues',
			}),
			overrides(baseFilters.team, {
				isCustomFilter: true,
				name: 'testrayTeamIds',
				type: 'multiselect',
			}),
			overrides(baseFilters.component, {
				isCustomFilter: true,
				name: 'testrayComponentIds',
				type: 'multiselect',
			}),
		] as RendererFields[],
		name: 'subtasks',
	},
	suites: {
		fields: [
			{
				label: i18n.translate('suite-name'),
				name: 'name',
				operator: 'contains',
				placeholder: i18n.translate('search'),
				type: 'text',
			},
		] as RendererFields[],
		name: 'suites',
	},
	teams: {
		fields: [
			{
				label: i18n.translate('team-name'),
				name: 'name',
				operator: 'contains',
				type: 'text',
			},
		] as RendererFields[],
		name: 'teams',
	},
	testflow: {
		fields: [
			{
				label: i18n.sub('task-x', 'name'),
				name: 'name',
				operator: 'contains',
				type: 'text',
			},
			overrides(baseFilters.project, {
				label: i18n.translate('project-name'),
				name: 'buildToTasks/r_projectToBuilds_c_projectId',
				type: 'multiselect',
			}),
			overrides(baseFilters.routine, {
				label: i18n.translate('routine-name'),
				name: 'buildToTasks/r_routineToBuilds_c_routineId',
				resource:
					'/routines?fields=id,name,routineToProjects.name&nestedFields=routineToProjects&pageSize=-1&sort=name:asc',
				transformData(item) {
					const transformRoutineData = (routine: TestrayRoutine) => ({
						label: `${routine.routineToProjects?.name} / ${routine.name}`,
						value: routine.id,
					});

					return dataToOptions(
						transformData<TestrayRoutine>(item),
						transformRoutineData
					);
				},
				type: 'multiselect',
			}),
			{
				label: i18n.translate('build-name'),
				name: 'buildToTasks/name',
				operator: 'contains',
				type: 'text',
			},
			overrides(baseFilters.dueStatus, {
				options: [
					{
						label: i18n.translate('abandoned'),
						value: TaskStatuses.ABANDONED,
					},
					{
						label: i18n.translate('complete'),
						value: TaskStatuses.COMPLETE,
					},
					{
						label: i18n.translate('in-analysis'),
						value: TaskStatuses.IN_ANALYSIS,
					},
				],
			}),
			overrides(baseFilters.assignee, {
				name: 'taskToTasksUsers/r_userToTasksUsers_userId',
				type: 'select',
			}),
		] as RendererFields[],
		name: 'testflow',
	},
	user: {
		fields: [overrides(baseFilters.user, {operator: 'contains'})],
		name: 'user',
	},
} as const;

export {filterSchema};
