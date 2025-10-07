/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {yupResolver} from '@hookform/resolvers/yup';
import * as yup from 'yup';

import i18n from '../i18n';

const DEFAULT_REQUIRED_TEXT = i18n.translate('this-field-is-required');
const MINIMUM_CHARACTERS_PASSWORD = 8;

const userSchema = yup.object({
	alternateName: yup.string().required(i18n.translate(DEFAULT_REQUIRED_TEXT)),
	emailAddress: yup
		.string()
		.email()
		.required(i18n.translate(DEFAULT_REQUIRED_TEXT)),
	familyName: yup.string().required(i18n.translate(DEFAULT_REQUIRED_TEXT)),
	givenName: yup.string().required(i18n.translate(DEFAULT_REQUIRED_TEXT)),
	rolesUser: yup.mixed(),
});

const passwordStructure = {
	currentPassword: yup.string(),
	password: yup
		.string()
		.required(i18n.translate(DEFAULT_REQUIRED_TEXT))
		.min(
			MINIMUM_CHARACTERS_PASSWORD,
			i18n.sub(
				'minimum-x-characters',
				MINIMUM_CHARACTERS_PASSWORD.toString()
			)
		)
		.matches(
			/[a-zA-Z0-9]/,
			i18n.translate('password-may-contain-letters-and-number')
		),
	rePassword: yup
		.string()
		.required(i18n.translate(DEFAULT_REQUIRED_TEXT))
		.oneOf(
			[yup.ref('password'), null],
			i18n.translate('passwords-do-not-match')
		),
};

const passwordRequiredStructure = {
	...passwordStructure,
	currentPassword: yup
		.string()
		.required(i18n.translate(DEFAULT_REQUIRED_TEXT)),
};

const buildStructure = {
	caseIds: yup.array().of(yup.number()),
	cpuUseTime: yup.string(),
	description: yup.string(),
	dueStatus: yup.string(),
	factorStacks: yup.mixed(),
	gitHash: yup.string(),
	id: yup.string(),
	name: yup.string().required(i18n.sub('x-is-a-required-field', 'name')),
	productVersionId: yup
		.string()
		.required(i18n.sub('x-is-a-required-field', 'product-version')),
	projectId: yup.number(),
	promoted: yup.boolean(),
	routineId: yup.string().required(),
	runOptions: yup.mixed(),
	template: yup.boolean(),
	templateTestrayBuildId: yup.string(),
};

const buildTemplateStructure = {
	...buildStructure,
	productVersionId: yup.string(),
};

const yupSchema = {
	build: yup.object(buildStructure),
	buildTemplate: yup.object(buildTemplateStructure),
	case: yup.object({
		addAnother: yup.boolean(),
		caseTypeId: yup.string().required(),
		componentId: yup.string().required(),
		description: yup.string(),
		descriptionType: yup.string(),
		estimatedDuration: yup.number().min(0),
		name: yup.string().required(),
		priority: yup.string(),
		steps: yup.string(),
		stepsType: yup.string(),
	}),
	caseDetail: yup.object({
		dueStatus: yup.string().required(),
		id: yup.string(),
		name: yup.string().required(),
	}),
	caseResult: yup.object({
		buildId: yup.number(),
		caseId: yup.number(),
		comment: yup.string(),
		dueStatus: yup.string().required(),
		errors: yup.string().nullable(),
		issues: yup.string(),
		mbMessageId: yup.number().nullable(),
		mbThreadId: yup.number().nullable(),
		runId: yup.number(),
		startDate: yup.string().nullable(),
		userId: yup.number(),
	}),
	caseType: yup.object({
		name: yup.string().required(),
	}),
	component: yup.object({
		id: yup.string(),
		name: yup.string().required(),
		projectId: yup.string(),
		teamId: yup.string(),
	}),
	enviroment: yup.object({
		factorCategoryIds: yup.mixed(),
		factorOptionIds: yup.mixed(),
		routineId: yup.number(),
	}),
	factor: yup.object({
		factorCategoryId: yup.string().required(),
		factorOptionId: yup.string().required(),
		id: yup.string(),
		name: yup.string().required(),
		routineId: yup.number(),
		runId: yup.number(),
	}),
	factorCategory: yup.object({
		id: yup.string(),
		name: yup.string().required(),
	}),
	factorOption: yup.object({
		factorCategoryId: yup
			.string()
			.required(i18n.translate(DEFAULT_REQUIRED_TEXT)),
		id: yup.string(),
		name: yup.string().required(i18n.translate(DEFAULT_REQUIRED_TEXT)),
	}),
	factorToRun: yup.object({
		buildId: yup.number(),
		id: yup.string(),
		number: yup.number(),
	}),
	jiraIssue: yup.object({
		description: yup.string(),
		externalReferenceCode: yup.string().required(),
		id: yup.string(),
		title: yup.string().required(),
	}),
	jiraProject: yup.object({
		externalReferenceCode: yup.string().required(),
		id: yup.string(),
		name: yup.string().required(),
	}),
	option: yup.object({
		name: yup.string(),
	}),
	password: yup.object(passwordStructure),
	passwordRequired: yup.object(passwordRequiredStructure),
	productVersion: yup.object({
		id: yup.string(),
		name: yup.string().required(),
		projectId: yup.string(),
	}),
	project: yup.object({
		description: yup.string().notRequired().max(280),
		id: yup.string().notRequired(),
		name: yup.string().required(),
	}),
	requirement: yup.object({
		componentId: yup.string().required(),
		components: yup.string(),
		description: yup.string(),
		descriptionType: yup.string().required(),
		id: yup.string(),
		key: yup.string(),
		linkTitle: yup.string().required(),
		linkURL: yup
			.string()
			.url(i18n.translate('the-link-url-must-be-a-valid-url'))
			.required(),
		summary: yup.string().required(),
	}),
	role: yup.object({
		role: yup.number(),
		rolesBrief: yup.mixed(),
		userId: yup.number(),
	}),
	routine: yup.object({
		autoanalyze: yup.boolean().required(),
		id: yup.number(),
		name: yup.string().required(),
		parentRoutines: yup.mixed(),
		r_teamToRoutines_c_teamId: yup
			.number()
			.transform((value, originalValue) => {
				return originalValue === '' ? 0 : value;
			}),
	}),
	run: yup.object({
		buildId: yup.number(),
		description: yup.string(),
		environmentHash: yup.string(),
		id: yup.number().notRequired(),
		name: yup.string().required(),
		number: yup.number().required(),
	}),
	subtask: yup.object({
		comment: yup.string(),
		dueStatus: yup.string(),
		errors: yup.string(),
		id: yup.number(),
		issues: yup.string(),
		mbMessageId: yup.number().nullable(),
		mbThreadId: yup.number().nullable(),
		mergedToSubtaskId: yup.number(),
		name: yup.string(),
		number: yup.number(),
		score: yup.number(),
		splitFromSubtaskId: yup.number(),
		taskId: yup.number(),
		userId: yup.number(),
	}),
	subtaskToCaseResult: yup.object({
		issues: yup.string(),
		name: yup.string(),
		subtaskId: yup.number(),
	}),
	suite: yup.object({
		autoanalyze: yup.boolean(),
		caseParameters: yup.string(),
		description: yup.string(),
		id: yup.string(),
		name: yup.string().required(),
		type: yup.string(),
	}),
	suiteCase: yup.object({
		caseId: yup.number(),
		name: yup.string(),
		suiteId: yup.number(),
	}),
	task: yup.object({
		buildId: yup.number(),
		dueStatus: yup.string(),
		id: yup.number().required(),
		name: yup.string().required(i18n.sub('x-is-a-required-field', 'name')),
		userIds: yup.array().of(yup.number()),
	}),
	taskToCaseTypes: yup.object({
		caseTypeId: yup.number(),
		name: yup.string(),
		taskId: yup.number(),
	}),
	taskToUser: yup.object({
		name: yup.string(),
		taskId: yup.number(),
		userId: yup.number(),
	}),
	team: yup.object({
		id: yup.string(),
		name: yup.string().required(),
		projectId: yup.string(),
		teamId: yup.string(),
	}),
	user: userSchema,
	userWithPassword: userSchema.shape(passwordStructure),
	userWithPasswordRequired: userSchema.shape(passwordRequiredStructure),
};

export {yupResolver};

export default yupSchema;
