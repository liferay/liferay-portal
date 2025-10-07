/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Rest from '../../core/Rest';
import yupSchema from '../../schema/yup';
import {TestrayJiraIssue} from './types';

type JiraIssue = typeof yupSchema.jiraIssue.__outputType;

class TestrayJiraIssueImpl extends Rest<JiraIssue, TestrayJiraIssue> {
	constructor() {
		super({
			adapter: ({description, externalReferenceCode, title}) => ({
				description,
				externalReferenceCode,
				title,
			}),
			fields: 'id,title,externalReferenceCode,description',
			nestedFields: '',
			transformData: (testrayJiraIssue) => {
				return {
					...testrayJiraIssue,
				};
			},
			uri: 'jiraissues',
		});
	}
}

export const testrayJiraIssueImpl = new TestrayJiraIssueImpl();
