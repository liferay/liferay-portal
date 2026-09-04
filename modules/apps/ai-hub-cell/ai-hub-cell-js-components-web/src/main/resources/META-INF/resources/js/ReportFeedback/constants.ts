/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ReportFeedbackReason} from './api';

export const REASON_OPTIONS: {
	label: string;
	value: ReportFeedbackReason | '';
}[] = [
	{label: Liferay.Language.get('select-reason'), value: ''},
	{
		label: Liferay.Language.get('incorrect-or-inaccurate-response'),
		value: 'inaccurateResponse',
	},
	{
		label: Liferay.Language.get('inappropriate-or-harmful-content'),
		value: 'harmfulContent',
	},
	{
		label: Liferay.Language.get(
			'exposure-of-personal-or-sensitive-data-pii'
		),
		value: 'personalDataExposure',
	},
	{
		label: Liferay.Language.get('agent-error-or-malfunction'),
		value: 'agentError',
	},
	{
		label: Liferay.Language.get('other'),
		value: 'other',
	},
];

export const USER_MESSAGE_LENGTH_MAX = 5000;
