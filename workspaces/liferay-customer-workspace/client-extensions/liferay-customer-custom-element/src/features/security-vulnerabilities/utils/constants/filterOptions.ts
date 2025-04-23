/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {JiraEnum} from './jiraEnum';

export interface IProps {
	[JiraEnum.AFFECTED_VERSIONS]?: string[];
	[JiraEnum.CATEGORIES]?: string[];
	[JiraEnum.FIX_VERSIONS]?: string[];
	[JiraEnum.ISSUE_CLASSIFICATION]?: string[];
	[JiraEnum.SEVERITY]?: string[];
}

export const FILTER_MAP: {[key: string]: string} = {
	affectedVersions: 'filterAffectedVersions',
	categories: 'filterCategories',
	fixVersions: 'filterFixVersions',
	issueClassification: 'filterClassifications',
	severity: 'filterSeverities',
};

export const FILTER_OPTIONS: IProps = {
	[JiraEnum.AFFECTED_VERSIONS]: ['2024.Q4', '2024.Q3', '2024.Q2', '2024.Q1'],
	[JiraEnum.CATEGORIES]: ['Docker', 'DXP', 'PaaS', 'SaaS'],
	[JiraEnum.FIX_VERSIONS]: ['2024.Q4', '2024.Q3', '2024.Q2', '2024.Q1'],
	[JiraEnum.ISSUE_CLASSIFICATION]: [
		'Advisory',
		'Confirmed Vulnerability',
		'False Positive',
		'Ignored',
		'Threat Information',
		'Not Exploitable'
	],
	[JiraEnum.SEVERITY]: ['Critical', 'High', 'Medium', 'Low', 'None'],
};
