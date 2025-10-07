/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Rest from '../../core/Rest';
import yupSchema from '../../schema/yup';
import {TestrayJiraIssue} from './types';

type JiraProject = typeof yupSchema.jiraProject.__outputType;

class TestrayJiraProjectImpl extends Rest<JiraProject, TestrayJiraIssue> {
	constructor() {
		super({
			adapter: ({externalReferenceCode, name}) => ({
				externalReferenceCode,
				name,
			}),
			fields: 'id,title,externalReferenceCode,description',
			nestedFields: 'projectToJiraProjects,routineToJiraProject',
			transformData: (testrayJiraIssue) => {
				return {
					...testrayJiraIssue,
				};
			},
			uri: 'jiraprojects',
		});
	}
}

export const testrayJiraProjectImpl = new TestrayJiraProjectImpl();
