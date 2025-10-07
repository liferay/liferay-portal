/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import i18n from '../../i18n';
import fetcher from '../fetcher';

const deleteResource = (resource: RequestInfo) => {
	if (confirm(i18n.translate('are-you-sure-you-want-to-delete-this-item'))) {
		return fetcher.delete(resource);
	}
};

export {deleteResource};

export * from './LiferayMessageBoard';
export * from './LiferayUserAccounts';
export * from './TestrayBuild';
export * from './TestrayCase';
export * from './TestrayCaseRequirements';
export * from './TestrayCaseResult';
export * from './TestrayCaseTypes';
export * from './TestrayComponent';
export * from './TestrayFactor';
export * from './TestrayFactorCategory';
export * from './TestrayFactorOptions';
export * from './TestrayJiraIssue';
export * from './TestrayJiraProject';
export * from './TestrayProductVersion';
export * from './TestrayProject';
export * from './TestrayRequirement';
export * from './TestrayRequirementCase';
export * from './TestrayRoutine';
export * from './TestrayRun';
export * from './TestraySubtask';
export * from './TestraySubtaskCaseResults';
export * from './TestraySuite';
export * from './TestraySuiteCases';
export * from './TestrayTask';
export * from './TestrayTaskUsers';
export * from './TestrayTeam';
export * from './types';
