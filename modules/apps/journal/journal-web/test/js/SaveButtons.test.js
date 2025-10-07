/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {render, screen, waitFor} from '@testing-library/react';
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
			reactComponentRef: {current: {validate: () => true}},
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

	it('opens modal for all buttons when there is not an articleId', async () => {
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

		userEvent.click(screen.getByLabelText('Close'));

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

		userEvent.click(screen.getByLabelText('Close'));

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
});
