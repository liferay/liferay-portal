/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openToast} from 'frontend-js-components-web';
import {escapeHTML, sub} from 'frontend-js-web';

import {getFormattedLabel} from './getFormattedText';

const displayAssignSuccessToast = (title: string, value: string) => {
	openToast({
		message: sub(
			Liferay.Language.get('x-was-successfully-assigned-to-x'),
			getFormattedLabel(title),
			escapeHTML(value)
		),
		type: 'success',
	});
};

const displayBulkAssignSuccessToast = (assignee: string, count: number) => {
	openToast({
		message:
			count === 1
				? sub(
						Liferay.Language.get(
							'task-was-successfully-assigned-to-x'
						),
						escapeHTML(assignee)
					)
				: sub(
						Liferay.Language.get(
							'x-tasks-were-successfully-assigned-to-x'
						),
						String(count),
						escapeHTML(assignee)
					),
		type: 'success',
	});
};

const displayBulkDueDateSuccessToast = (count: number) => {
	openToast({
		message:
			count === 1
				? Liferay.Language.get(
						'due-date-was-successfully-updated-for-one-task'
					)
				: sub(
						Liferay.Language.get(
							'due-date-was-successfully-updated-for-x-tasks'
						),
						String(count)
					),
		type: 'success',
	});
};

const displayBulkStateSuccessToast = (count: number) => {
	openToast({
		message:
			count === 1
				? Liferay.Language.get(
						'state-was-successfully-updated-for-one-task'
					)
				: sub(
						Liferay.Language.get(
							'state-was-successfully-updated-for-x-tasks'
						),
						String(count)
					),
		type: 'success',
	});
};

const displayDeleteSuccessToast = (title: string) => {
	openToast({
		message: sub(
			Liferay.Language.get('x-was-successfully-deleted'),
			getFormattedLabel(title)
		),
		type: 'success',
	});
};

const displayDueDateSuccessToast = (title: string) => {
	openToast({
		message: sub(
			Liferay.Language.get('x-due-date-was-successfully-updated'),
			getFormattedLabel(title)
		),
		type: 'success',
	});
};

const displayErrorToast = (errorMessage?: string) => {
	openToast({
		message: errorMessage
			? escapeHTML(errorMessage)
			: Liferay.Language.get('an-unexpected-error-occurred'),
		title: Liferay.Language.get('error'),
		type: 'danger',
	});
};

const displayOpenSubtasksToast = (count: number) => {
	openToast({
		message:
			count === 1
				? Liferay.Language.get(
						'task-was-marked-as-done-one-subtask-is-not-done-yet'
					)
				: sub(
						Liferay.Language.get(
							'task-was-marked-as-done-x-subtasks-are-not-done-yet'
						),
						String(count)
					),
		type: 'info',
	});
};

const displayStateSuccessToast = () => {
	openToast({
		message: Liferay.Language.get('state-was-successfully-updated'),
		type: 'success',
	});
};

export {
	displayAssignSuccessToast,
	displayBulkAssignSuccessToast,
	displayBulkDueDateSuccessToast,
	displayBulkStateSuccessToast,
	displayDeleteSuccessToast,
	displayDueDateSuccessToast,
	displayErrorToast,
	displayOpenSubtasksToast,
	displayStateSuccessToast,
};
