/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {StatusBadgeType} from '../../components/StatusBadge/StatusBadge';
import {DescriptionType} from '../../types';

export type ActionPermissionProperties = {
	href: string;
	method: string;
};

export type Facets = {
	facetCriteria: string;
	facetValues: {
		numberOfOccurrences: number;
		term: string;
	}[];
};

export type FacetAggregation = {
	facets: Facets[];
};

type ObjectActions = {
	create?: ActionPermissionProperties;
	createBatch?: ActionPermissionProperties;
	deleteBatch?: ActionPermissionProperties;
	updateBatch?: ActionPermissionProperties;
};

export type ObjectActionsItems = {
	delete?: ActionPermissionProperties;
	get?: ActionPermissionProperties;
	replace?: ActionPermissionProperties;
	update?: ActionPermissionProperties;
};

export type Results<Query = any> = {
	results: Query[];
};

export type APIResponse<Query = any> = {
	actions: ObjectActions;
	facets: Facets[];
	items: Query[];
	lastPage: number;
	page: number;
	pageSize: number;
	results?: Results[];
	testrayCaseResultComparisons?: Results[];
	totalCount: number;
};

// Objects Types

export type PickList = {
	key: string;
	name: string;
};

export type Role = {
	id: number;
	name: string;
};

export type UserGroup = {
	id: number;
	name: string;
};

export type UserActions = {
	'delete-user-account': ActionPermissionProperties;
	'get-my-user-account': ActionPermissionProperties;
	'patch-user-account': ActionPermissionProperties;
	'put-user-account': ActionPermissionProperties;
};

export type UserAccount = {
	actions: UserActions;
	additionalName: string;
	alternateName: string;
	emailAddress: string;
	familyName: string;
	givenName: string;
	id: number;
	image: string;
	name: string;
	roleBriefs: Role[];
	userGroupBriefs: UserGroup[];
	uuid: number;
};

export type CaseResultAggregation = {
	caseResultBlocked: number | string;
	caseResultFailed: number | string;
	caseResultInProgress: number | string;
	caseResultIncomplete: number | string;
	caseResultPassed: number | string;
	caseResultTestFix: number | string;
	caseResultUntested: number | string;
};

export type UserRole = {
	roles: number;
	rolesBriefs: Role[];
	userId: number;
};

export type TestrayJiraProject = {
	actions?: ObjectActionsItems;
	externalReferenceCode: string;
	id: number;
	name: string;
	projectToJiraProjects?: TestrayProject;
	r_projectToJiraProjects_c_projectId: number;
	r_routineToJiraProject_c_routineId: number;
	routineToJiraProject?: TestrayRoutine;
};

export type TestrayJiraIssue = {
	actions?: ObjectActionsItems;
	description: string;
	epicERC: string;
	externalReferenceCode: string;
	id: number;
	initiativeERC: string;
	issueType: PickList;
	parentIssueERC: string;
	projectType: PickList;
	storyERC: string;
	title: string;
};

export type TestrayBuild = {
	actions: ObjectActionsItems;
	archived?: boolean;
	buildToTasks: TestrayTask[];
	cpuUseTime: string;
	creator: {
		name: string;
	};
	dateArchived: string;
	dateCreated: string;
	description: string;
	dueDate: string;
	dueStatus: PickList;
	gitHash: string;
	id: number;
	importStatus: PickList;
	name: string;
	playwrightReports: string;
	productVersion?: TestrayProductVersion;
	project?: TestrayProject;
	promoted: boolean;
	r_productVersionToBuilds_c_productVersion?: TestrayProductVersion;
	r_projectToBuilds_c_project?: TestrayProject;
	r_routineToBuilds_c_routine?: TestrayRoutine;
	routine?: TestrayRoutine;
	runId?: number;
	runsData?: TestrayRun;
	tasks: TestrayTask[];
	template: boolean;
	templateTestrayBuildId: string;
} & CaseResultAggregation &
	Partial<TestrayBuildCustomAPI>;

export type TestrayBuildCustomAPI = {
	testrayBuildArchived: boolean;
	testrayBuildId: number;
	testrayBuildImportStatus: string;
	testrayBuildName: string;
	testrayBuildPromoted: boolean;
	testrayBuildTaskStatus: string;
	testrayStatusMetric: TestrayStatusMetric;
};

export type TestrayStatusMetric = {
	blocked: number;
	failed: number;
	inProgress: number;
	incomplete: number;
	passed: number;
	testfix: number;
	total: number;
	untested: number;
};

export type TestrayCase = {
	actions: ObjectActionsItems;
	caseResults?: TestrayCaseResult[];
	caseToBuildsCases: TestrayBuildsCases[];
	caseToCaseResult?: TestrayCaseResult[];
	caseType?: TestrayCaseType;
	component?: TestrayComponent;
	dateCreated: string;
	dateModified: string;
	description: string;
	descriptionType: string;
	estimatedDuration: number;
	flaky?: boolean;
	id: number;
	name: string;
	number: number;
	originationKey: string;
	priority: number;
	project?: TestrayProject;
	r_caseTypeToCases_c_caseType?: TestrayCaseType;
	r_componentToCases_c_component?: TestrayComponent;
	r_projectToCases_c_project?: TestrayProject;
	steps: string;
	stepsType: string;
};

export type TestrayBuildsCases = {
	r_buildToBuildsCases_c_build: TestrayBuild;
	r_caseToBuildsCases_c_caseId: TestrayCase;
};

export type TestrayCaseResult = {
	actions: ObjectActionsItems;
	assignedUserId: string;
	attachments: string;
	build?: TestrayBuild;
	case?: TestrayCase;
	closedDate: string;
	comment: string;
	component?: TestrayComponent;
	dateCreated: string;
	dateModified: string;
	dueStatus: PickList;
	duration: number;
	error?: string;
	errors: string;
	id: number;
	issues: string;
	key: string;
	mbMessageId: number;
	mbThreadId: number;
	priority?: number;
	r_buildToCaseResult_c_build?: TestrayBuild;
	r_buildToCaseResult_c_buildId?: number;
	r_caseToCaseResult_c_case?: TestrayCase;
	r_caseToCaseResult_c_caseId?: number;
	r_componentToCaseResult_c_component?: TestrayComponent;
	r_runToCaseResult_c_run?: TestrayRun;
	r_runToCaseResult_c_runId?: number;
	r_userToCaseResults_user?: UserAccount;
	run?: TestrayRun;
	runId?: number;
	startDate: string;
	status?: string;
	testrayCaseResultId?: number;
	user?: UserAccount;
	userName: string;
	warnings: number;
} & CaseResultAggregation;

export type TestrayCaseType = {
	caseTypeToCases: TestrayCase[];
	dateCreated: string;
	dateModified: string;
	externalReferenceCode: string;
	id: number;
	name: string;
	status: string;
};

export type TestrayCaseDetail = {
	buildToCaseDetail?: TestrayBuild;
	dueStatus: PickList;
	id: string;
	name: string;
	r_buildToCaseDetail_c_buildId: number;
};

export type TestrayDyspatchTrigger = {
	creator: {
		name: string;
		urlImage?: string;
	};
	status: StatusBadgeType;
	type: string;
};

export type TestrayFactorOption = {
	dateCreated: string;
	dateModified: string;
	factorCategory?: TestrayFactorCategory;
	id: number;
	name: string;
	r_factorCategoryToOptions_c_factorCategory: TestrayFactorCategory;
};

export type TestrayOptionsByCategory = {
	[key: string]: any;
};

export type TestrayProductVersion = {
	id: number;
	name: string;
	project?: TestrayProject;
	r_projectToProductVersions_c_project?: TestrayProject;
};

export type TestrayProject = {
	actions: ObjectActionsItems;
	creator: {
		name: string;
	};
	dateCreated: string;
	description: string;
	id: number;
	name: string;
};

export type TestrayRequirement = {
	actions: ObjectActionsItems;
	component?: TestrayComponent;
	components: string;
	description: string;
	descriptionType: keyof typeof DescriptionType;
	id: number;
	key: string;
	linkTitle: string;
	linkURL: string;
	r_componentToRequirements_c_component?: TestrayComponent;
	summary: string;
	url: string;
};

export type TestrayRequirementCase = {
	case?: TestrayCase;
	id: number;
	r_caseToRequirementsCases_c_case?: TestrayCase;
	r_requiremenToRequirementsCases_c_requirement?: TestrayRequirement;
	requirement?: TestrayRequirement;
};

export type TestrayRun = {
	applicationServer?: string;
	browser?: string;
	build?: TestrayBuild;
	database?: string;
	dateCreated: string;
	dateModified: string;
	description: string;
	environmentHash: string;
	externalReferenceCode: string;
	externalReferencePK: string;
	externalReferenceType: string;
	factorCategory?: TestrayFactorCategory;
	factorOption?: TestrayFactorOption;
	id: number;
	javaJDK?: string;
	jenkinsJobKey: string;
	name: string;
	number: string;
	operatingSystem?: string;
	r_buildToRuns_c_build?: TestrayBuild;
	runId?: number;
	status: string;
	testrayRunId: number;
	testrayRunName: string;
} & CaseResultAggregation;

export type TestraySubtask = {
	actions: ObjectActionsItems;
	caseResultIssues: string[];
	dateCreated: string;
	dateModified: string;
	dueStatus: PickList;
	error?: string;
	errors: string;
	id: number;
	issues: string;
	mbMessageId: number;
	mbThreadId: number;
	mergedToSubtask: TestraySubtask;
	name: string;
	number: number;
	r_mergedToTestraySubtask_c_subtask: TestraySubtask;
	r_splitFromTestraySubtask_c_subtask: TestraySubtask;
	r_taskToSubtasks_c_task: TestrayTask;
	r_userToSubtasks_user: UserAccount;
	r_userToSubtasks_userId: number;
	score: number;
	splitFromSubtask: TestraySubtask;
	status: string;
	statusUpdateDate: string;
	subtaskId: string;
	subtaskToCaseResults?: TestrayCaseResult[];
	subtaskToSubtasksCasesResults: TestraySubtaskCaseResult[];
	task: TestrayTask;
	testrayTaskId: number;
	user: UserAccount;
	userId: number;
};

export type TestraySubtaskCaseResult = {
	build?: TestrayBuild;
	case?: TestrayCase;
	errors: string;
	id: number;
	issues: string;
	r_buildToCaseResult_c_build?: TestrayBuild;
	r_buildToCaseResult_c_buildId?: number;
	r_caseToCaseResult_c_case?: TestrayCase;
	r_caseToCaseResult_c_caseId?: number;
	r_componentToCaseResult_c_component?: TestrayComponent;
	r_runToCaseResult_c_run?: TestrayRun;
	r_runToCaseResult_c_runId?: number;
	r_subtaskToCaseResults_c_subtask?: TestraySubtask;
	r_userToCaseResults_user?: UserAccount;
	runId?: number;
	subtask?: TestraySubtask;
	user?: UserAccount;
};

export type TestraySuite = {
	actions: ObjectActionsItems;
	caseParameters: string;
	creator: {
		name: string;
	};
	dateCreated: string;
	dateModified: string;
	description: string;
	id: number;
	name: string;
	type: string;
};

export type TestraySuiteCase = {
	case: TestrayCase;
	caseId: Number;
	id: number;
	r_caseToSuitesCases_c_case: TestrayCase;
	r_caseToSuitesCases_c_caseId: number;
	r_caseToSuitesCases_c_suite: TestraySuite;
	suite: TestraySuite;
};

export type TestrayTask = {
	actions: ObjectActionsItems;
	build?: TestrayBuild;
	dateCreated: string;
	dispatchTriggerId: number;
	dueStatus: PickList;
	id: number;
	name: string;
	r_buildToTasks_c_build?: TestrayBuild;
	subtaskScore: string;
	subtaskScoreCompleted: string;
	subtaskScoreSelfCompleted: string;
	subtaskScoreSelfIncomplete: string;
	taskToTasksUsers: any;
};

export type TestrayTaskCaseTypes = {
	caseType?: TestrayCaseType;
	id: number;
	name: string;
	r_caseTypeToTasksCaseTypes_c_caseType?: TestrayCaseType;
	r_taskToTasksCaseTypes_c_taskId?: TestrayTask;
	task?: TestrayTask;
};

export type TestrayTaskUser = {
	id: number;
	name: string;
	r_taskToTasksUsers_c_task?: TestrayTask;
	r_userToTasksUsers_user?: UserAccount;
	task?: TestrayTask;
	user?: UserAccount;
};

export type TestrayTeam = {
	dateCreated: string;
	dateModified: string;
	externalReferenceCode: string;
	id: number;
	name: string;
	project?: TestrayProject;
	r_projectToTeams_c_project?: TestrayProject;
	teamToComponents?: TestrayTeam[];
};

export type TestrayComponent = {
	dateCreated: string;
	dateModified: string;
	externalReferenceCode: string;
	id: number;
	name: string;
	originationKey: string;
	project?: TestrayProject;
	r_projectToComponents_c_project?: TestrayProject;
	r_teamToComponents_c_team?: TestrayTeam;
	r_teamToComponents_c_teamId: number;
	status: string;
	team?: TestrayTeam;
	teamId: number;
} & CaseResultAggregation;

export type TestrayFactorCategory = {
	dateCreated: string;
	dateModified: string;
	externalReferenceCode: string;
	id: number;
	name: string;
	status: string;
};

export type TestrayRoutine = {
	actions: ObjectActionsItems;
	id: number;
	name: string;
	parentRoutines: TestrayRoutine[];
	r_teamToRoutines_c_teamId?: number;
	routineToBuilds: TestrayBuild[];
	routineToProjects?: TestrayProject;
	testrayBuildCPUUseTime?: string;
	testrayBuildDueDate: string;
	testrayRoutineId?: number;
};

export type TestrayFactor = {
	dateCreated: string;
	dateModified: string;
	factorCategory?: TestrayFactorCategory;
	factorOption?: TestrayFactorOption;
	id: number;
	r_factorCategoryToFactors_c_factorCategory?: TestrayFactorCategory;
	r_factorOptionToFactors_c_factorOption?: TestrayFactorOption;
	r_runToFactors_c_run?: TestrayRun;
	run: TestrayRun;
};

export type TestrayAttachment = {
	name: string;
	url: string;
	value: string;
};

export type TestrayDispatchTrigger = {
	creator: {
		image: string;
		name: string;
	};
	dateCreated: string;
	dispatchTriggerId: number;
	dueStatus: PickList;
	externalReferenceCode: string;
	id: number;
	name: string;
	output: string;
	type: string;
};

export type MessageBoardMessage = {
	articleBody: string;
	creator: {
		image: string;
		name: string;
	};
	dateCreated: string;
	headline: string;
	id: string;
};

export type StorageType = 'persisted' | 'temporary';
