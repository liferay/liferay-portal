/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {act, render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import SaveButtons from '../../src/main/resources/META-INF/resources/js/SaveButtons';

const DEFAULT_PROPS = {
	articleId: null,
	defaultLanguageId: 'en_US',
	displayDate: null,
	editingDefaultValues: false,
	permissionsURL: null,
	portletNamespace: 'portletNamespace',
	publishButtonLabel: 'publish',
	saveButtonLabel: 'save',
	selectedLanguageId: 'en_US',
	timeZone: 'UTC',
	workflowEnabled: false,
};

const renderComponent = (props = DEFAULT_PROPS) => {
	return render(
		<>
			<div className="article-content-content" />
			<input id={`${props.portletNamespace}workflowAction`} />
			<input id={`${props.portletNamespace}jakarta-portlet-action`} />
			<SaveButtons {...props} />
		</>
	);
};

const runAllTimersAndExecuteAction = (action) => {
	jest.useFakeTimers();

	action();

	act(() => {
		jest.runAllTimers();
	});

	jest.useRealTimers();
};

describe('SaveButtons', () => {
	beforeEach(() => {
		global.Liferay.component = jest.fn().mockReturnValue({
			get: () => new Set([DEFAULT_PROPS.selectedLanguageId]),
			getValue: () => 'title',
		});

		global.fetch = jest.fn().mockReturnValue(
			Promise.resolve({
				html: () => Promise.resolve('<div>holi</div>'),
			})
		);

		global.Liferay.Workflow = {ACTION_PUBLISH: null};
	});

	it('renders', () => {
		renderComponent({
			...DEFAULT_PROPS,
			saveButtonLabel: 'save article',
		});

		expect(screen.getByText('save article')).toBeInTheDocument();
	});

	it('submit for workflow with permissions when publishing for the first time', () => {
		renderComponent({
			...DEFAULT_PROPS,
			articleId: '2611',
			showPublishModal: true,
			workflowEnabled: true,
		});

		expect(
			screen.getByText('submit-for-workflow-with-permissions')
		).toBeInTheDocument();
	});

	it('Do not open modal for all buttons when there is an articleId', () => {
		renderComponent({
			...DEFAULT_PROPS,
			articleId: '2611',
			saveButtonLabel: 'save',
		});

		userEvent.click(screen.getByText('save'));

		expect(
			screen.queryByText(
				'confirm-the-web-content-visibility-before-saving-as-draft'
			)
		).not.toBeInTheDocument();

		userEvent.click(
			screen.getByText('publish', {
				selector: '.dropdown-item',
			})
		);

		expect(
			screen.queryByText(
				'confirm-the-web-content-visibility-before-publishing'
			)
		).not.toBeInTheDocument();

		userEvent.click(
			screen.getByText('schedule-publication', {
				selector: '.dropdown-item',
			})
		);

		expect(
			screen.queryByText(
				'set-the-date-and-time-for-publishing-the-web-content-and-confirm-the-visibility-before-scheduling'
			)
		).not.toBeInTheDocument();
	});

	it('opens modal for all buttons when there is not an articleId', () => {
		renderComponent({
			...DEFAULT_PROPS,
			articleId: null,
			saveButtonLabel: 'save',
		});

		runAllTimersAndExecuteAction(() => {
			userEvent.click(screen.getByText('save'));
		});

		expect(
			screen.getByText(
				'confirm-the-web-content-visibility-before-saving-as-draft'
			)
		).toBeInTheDocument();

		runAllTimersAndExecuteAction(() => {
			userEvent.click(screen.getByLabelText('close'));
		});

		runAllTimersAndExecuteAction(() => {
			userEvent.click(
				screen.getByText('publish-with-permissions', {
					selector: '.dropdown-item',
				})
			);
		});

		expect(
			screen.getByText(
				'confirm-the-web-content-visibility-before-publishing'
			)
		).toBeInTheDocument();

		runAllTimersAndExecuteAction(() => {
			userEvent.click(screen.getByLabelText('close'));
		});

		runAllTimersAndExecuteAction(() => {
			userEvent.click(
				screen.getByText('schedule-publication', {
					selector: '.dropdown-item',
				})
			);
		});

		expect(
			screen.getByText(
				'set-the-date-and-time-for-publishing-the-web-content-and-confirm-the-visibility-before-scheduling'
			)
		).toBeInTheDocument();
	});

	it('show alert and input feedback when trying to schedule without a date introduced', () => {
		renderComponent({
			...DEFAULT_PROPS,
			articleId: null,
		});

		runAllTimersAndExecuteAction(() => {
			userEvent.click(screen.getByText('schedule-publication'));
		});

		userEvent.click(screen.getByText('schedule'));

		const alerts = screen.getAllByText('please-enter-a-valid-date');

		expect(alerts.length).toBe(2);
	});

	it('shows error when introducing an invalid date', () => {
		renderComponent({
			...DEFAULT_PROPS,
			articleId: null,
		});

		runAllTimersAndExecuteAction(() => {
			userEvent.click(screen.getByText('schedule-publication'));
		});

		userEvent.type(screen.getByLabelText('date-and-time'), 'pepito');

		expect(
			screen.getByText('please-enter-a-valid-date')
		).toBeInTheDocument();
	});

	it('show error when introducing a past date', () => {
		renderComponent({
			...DEFAULT_PROPS,
			articleId: null,
		});

		runAllTimersAndExecuteAction(() => {
			userEvent.click(screen.getByText('schedule-publication'));
		});

		userEvent.type(
			screen.getByLabelText('date-and-time'),
			'1970-01-01 12:00'
		);

		expect(
			screen.getByText('the-date-entered-is-in-the-past')
		).toBeInTheDocument();
	});
});
