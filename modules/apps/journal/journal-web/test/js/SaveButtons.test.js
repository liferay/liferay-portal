/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {act, render, screen, waitFor} from '@testing-library/react';
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
			<input id={`${props.portletNamespace}articleId`} />
			<SaveButtons {...props} />
		</>
	);
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

		global.Liferay.componentReady = jest.fn().mockResolvedValue({
			reactComponentRef: {
				current: {
					getFields: () => [{valid: true}],
					validate: jest.fn().mockResolvedValue([null, true]),
				},
			},
		});

		global.Liferay.Form = {
			get: () => ({
				formValidator: {
					hasErrors: jest.fn().mockReturnValue(false),
					validate: jest.fn().mockReturnValue(true),
				},
			}),
		};

		global.Liferay.Workflow = {ACTION_PUBLISH: null};

		global.Liferay.on = jest.fn(() => ({detach: jest.fn()}));
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

	it('Do not see permissions modal in dropdown options when there is an articleId', () => {
		renderComponent({
			...DEFAULT_PROPS,
			articleId: '2611',
			saveButtonLabel: 'save',
		});

		userEvent.click(screen.getByTitle('publish-options'));

		expect(
			screen.queryByText('publish-with-permissions', {
				selector: '.dropdown-item',
			})
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

	it('View permissions modal in dropdown options when there is not an articleId', async () => {
		renderComponent({
			...DEFAULT_PROPS,
			articleId: null,
			saveButtonLabel: 'save',
		});

		userEvent.click(screen.getByText('save'));

		expect(
			await screen.findByText(
				'confirm-the-web-content-visibility-before-saving-as-draft'
			)
		).toBeInTheDocument();

		userEvent.click(screen.getByTitle('publish-options'));

		expect(
			screen.getByText('publish-with-permissions', {
				selector: '.dropdown-item',
			})
		).toBeInTheDocument();

		userEvent.click(
			screen.getByText('publish-with-permissions', {
				selector: '.dropdown-item',
			})
		);

		expect(
			await screen.findByText(
				'confirm-the-web-content-visibility-before-publishing'
			)
		).toBeInTheDocument();

		userEvent.click(screen.getByLabelText('close'));

		await waitFor(() => {
			expect(
				screen.queryByText(
					'confirm-the-web-content-visibility-before-publishing'
				)
			).not.toBeInTheDocument();
		});

		userEvent.click(
			screen.getByText('schedule-publication', {
				selector: '.dropdown-item',
			})
		);

		expect(
			await screen.findByText(
				'set-the-date-and-time-for-publishing-the-web-content-and-confirm-the-visibility-before-scheduling'
			)
		).toBeInTheDocument();
	});

	it('show alert and input feedback when trying to schedule without a date introduced', async () => {
		renderComponent({
			...DEFAULT_PROPS,
			articleId: null,
		});

		userEvent.click(screen.getByText('schedule-publication'));

		userEvent.click(await screen.findByText('schedule[verb]'));

		const alerts = screen.getAllByText('please-enter-a-valid-date');

		expect(alerts.length).toBe(2);
	});

	it('shows error when introducing an invalid date', async () => {
		renderComponent({
			...DEFAULT_PROPS,
			articleId: null,
		});

		userEvent.click(screen.getByText('schedule-publication'));

		userEvent.type(await screen.findByLabelText('date-and-time'), 'pepito');

		expect(
			screen.getByText('please-enter-a-valid-date')
		).toBeInTheDocument();
	});

	it('show no error when introducing a past date', async () => {
		renderComponent({
			...DEFAULT_PROPS,
			articleId: null,
		});

		userEvent.click(screen.getByText('schedule-publication'));

		userEvent.type(
			await screen.findByLabelText('date-and-time'),
			'1970-01-01 12:00'
		);

		expect(
			screen.queryByText('please-enter-a-valid-date')
		).not.toBeInTheDocument();
	});

	it('select past years from date picker when scheduling', async () => {
		jest.useFakeTimers().setSystemTime(new Date('2023-01-01'));

		renderComponent({
			...DEFAULT_PROPS,
			articleId: null,
		});

		userEvent.click(await screen.findByText('schedule-publication'));

		jest.runOnlyPendingTimers();

		userEvent.click(
			await screen.findByRole('button', {name: 'select-date'})
		);

		userEvent.click(await screen.findByLabelText('select-a-year'));

		const yearToCheck = 2023 - 5;
		expect(screen.getByText(yearToCheck)).toBeInTheDocument();

		jest.useRealTimers();
	});

	it('shows an error alert when the publish validation request fails', async () => {
		global.Liferay.componentReady = jest.fn().mockResolvedValue({
			reactComponentRef: {
				current: {
					getFields: () => [{valid: true}],
					validate: jest
						.fn()
						.mockRejectedValue(
							new Error('Upload size is too large.')
						),
				},
			},
		});

		renderComponent({
			...DEFAULT_PROPS,
			articleId: null,
		});

		userEvent.click(screen.getByText('publish'));

		expect(
			await screen.findByText('Upload size is too large.')
		).toBeInTheDocument();
	});

	it('does not show an error alert when the publish validation request is aborted', async () => {
		const validate = jest
			.fn()
			.mockRejectedValue(
				new DOMException('The user aborted a request.', 'AbortError')
			);

		global.Liferay.componentReady = jest.fn().mockResolvedValue({
			reactComponentRef: {
				current: {
					getFields: () => [{valid: true}],
					validate,
				},
			},
		});

		renderComponent({
			...DEFAULT_PROPS,
			articleId: null,
		});

		userEvent.click(screen.getByText('publish'));

		await waitFor(() => expect(validate).toHaveBeenCalled());

		await act(async () => {});

		expect(
			screen.queryByText('The user aborted a request.')
		).not.toBeInTheDocument();
	});

	it('does not proceed if required fields validation fails', async () => {
		global.Liferay.Form = {
			get: () => ({
				formValidator: {
					hasErrors: jest.fn().mockReturnValue(true),
					validate: jest.fn(),
				},
			}),
		};

		renderComponent({
			...DEFAULT_PROPS,
			articleId: null,
			saveButtonLabel: 'save',
		});

		userEvent.click(screen.getByText('save'));

		expect(
			screen.queryByText(
				'confirm-the-web-content-visibility-before-saving-as-draft'
			)
		).not.toBeInTheDocument();
	});

	it('routes to update_article from the hidden articleId field, even when its own articleId state never learned the ID', async () => {
		const lock = {
			isLocked: jest.fn(() => false),
			lock: jest.fn(),
			unlock: jest.fn(),
		};

		global.Liferay.componentReady = jest.fn((componentId) => {
			if (componentId === `${DEFAULT_PROPS.portletNamespace}publishing`) {
				return Promise.resolve(lock);
			}

			return Promise.resolve({
				reactComponentRef: {
					current: {
						getFields: () => [{valid: true}],
						validate: jest.fn().mockResolvedValue([null, true]),
					},
				},
			});
		});

		renderComponent({
			...DEFAULT_PROPS,
			articleId: null,
		});

		const articleIdInput = document.getElementById(
			`${DEFAULT_PROPS.portletNamespace}articleId`
		);

		// Simulates autosave having already created the draft under a
		// custom ID: the hidden field is updated, but SaveButtons' own
		// `articleId` state never learns about it, because
		// `asyncFormSubmission` only fires when the ID was still unknown
		// at the time autosave completed.

		articleIdInput.value = 'test123456';

		userEvent.click(screen.getByText('publish'));

		await waitFor(() => {
			expect(
				document.getElementById(
					`${DEFAULT_PROPS.portletNamespace}jakarta-portlet-action`
				)
			).toHaveValue('/journal/update_article');
		});
	});
});
