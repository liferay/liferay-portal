/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import fetch from 'jest-fetch-mock';
import React from 'react';

import '@testing-library/jest-dom';

import {NewPublish} from '../../../../../src/main/resources/META-INF/resources/revamp/js/pages/publish/NewPublish';
import {toWallClockDateTime} from '../../../../../src/main/resources/META-INF/resources/revamp/js/pages/publish/components/scheduler/cron';
import {ScheduledPublishProcess} from '../../../../../src/main/resources/META-INF/resources/revamp/js/types/exportImportProcess';
import {mockPreview} from '../../mocks/mockPreview';

jest.mock('staging-taglib', () => ({
	PagesTree: require('../../mocks/MockPagesTree').MockPagesTree,
}));

const DAY = 24 * 60 * 60 * 1000;

const FUTURE_DATE = new Date(Date.now() + DAY);

const FUTURE_DATE_TIME = toWallClockDateTime(FUTURE_DATE.toISOString(), 'UTC');

const SCHEDULED_PUBLISH_PROCESS: ScheduledPublishProcess = {
	cronExpression: '0 30 9 ? * MON/1 *',
	id: 1234,
	name: 'Weekly Content Sync',
	publishParameters: {
		DELETIONS: ['true'],
		PERMISSIONS: ['false'],
		PORTLET_DATA_com_liferay_journal_web_portlet_JournalPortlet: ['true'],
		range: ['fromLastPublishDate'],
		timeZoneId: ['UTC'],
	},
	scheduleStartDate: FUTURE_DATE.toISOString(),
};

const DEFAULT_PROPS = {
	backURL: '/some/back/url',
	pageTreeModalConfiguration: {
		groupId: 20121,
		pageSize: 20,
		privateLayoutsAvailable: false,
	},
	publishPreviewAPIURL:
		'/o/export-import/v1.0/sites/site-erc/publish-preview',
	publishProcessAPIURL:
		'/o/export-import/v1.0/sites/site-erc/publish-processes',
	scheduledBackURL: '/some/back/url?tab=scheduled',
	scheduledPublishProcessAPIURL:
		'/o/export-import/v1.0/sites/site-erc/scheduled-publish-processes',
	timeZoneId: 'UTC',
	timeZones: [{label: '(UTC) Coordinated Universal Time', value: 'UTC'}],
};

const getPublishProcessCall = () =>
	fetch.mock.calls.find(
		([url, init]) =>
			String(url).endsWith('publish-processes') && init?.method === 'POST'
	);

const getUnscheduleCall = () =>
	fetch.mock.calls.find(([, init]) => init?.method === 'DELETE');

const mockAPIRoutes = ({
	deleteStatus = 204,
	scheduledPublishProcess = SCHEDULED_PUBLISH_PROCESS,
} = {}) => {
	fetch.mockResponse(async (request) => {
		if (request.method === 'DELETE') {
			return {
				body: '',
				status: deleteStatus,
			};
		}

		if (request.url.includes('scheduled-publish-processes')) {
			return {body: JSON.stringify(scheduledPublishProcess)};
		}

		if (request.url.includes('publish-preview')) {
			return {body: JSON.stringify(mockPreview)};
		}

		return {body: JSON.stringify({})};
	});
};

const user = userEvent.setup({delay: null});

const fillRequiredFields = async () => {
	await screen.findByText('loaded');

	await user.click(screen.getByRole('textbox', {name: /^name/i}));

	await user.paste('My Publication');
};

const renderComponent = (
	props: Partial<React.ComponentProps<typeof NewPublish>> = {}
) => render(<NewPublish {...DEFAULT_PROPS} {...props} />);

describe('NewPublish', () => {
	beforeEach(() => {
		jest.clearAllMocks();

		fetch.resetMocks();

		mockAPIRoutes();
	});

	it('publishes immediately and navigates back', async () => {
		renderComponent();

		await fillRequiredFields();

		await user.click(
			screen.getByRole('button', {name: /publish-to-live/i})
		);

		await waitFor(() => {
			expect(getPublishProcessCall()).toBeDefined();
		});

		const body = JSON.parse(getPublishProcessCall()![1]!.body as string);

		expect(body.cronExpression).toBeUndefined();
		expect(body.deletions).toBe(false);
		expect(body.name).toBe('My Publication');
		expect(body.permissions).toBe(false);
		expect(body.requestPortletDataHandlers.length).toBeGreaterThan(0);

		expect(Liferay.Util.navigate).toHaveBeenCalledWith(
			DEFAULT_PROPS.backURL
		);
	});

	it('selects the schedule option when it is the default', async () => {
		renderComponent({defaultScheduled: true});

		expect(
			await screen.findByRole('radio', {name: /schedule-for-later/})
		).toBeChecked();
	});

	it('requires a start date to schedule the publication', async () => {
		renderComponent();

		await fillRequiredFields();

		await user.click(
			screen.getByRole('radio', {name: /schedule-for-later/})
		);

		const submitButton = screen.getByRole('button', {
			name: /schedule-publication-to-live/i,
		});

		await waitFor(() => {
			expect(submitButton).toBeDisabled();
		});

		await user.click(screen.getByRole('textbox', {name: 'start-date'}));

		await user.paste(FUTURE_DATE_TIME);

		await waitFor(() => {
			expect(submitButton).toBeEnabled();
		});
	});

	it('schedules the publication with the cron fields', async () => {
		renderComponent();

		await fillRequiredFields();

		await user.click(
			screen.getByRole('radio', {name: /schedule-for-later/})
		);

		await user.click(screen.getByRole('textbox', {name: 'start-date'}));

		await user.paste(FUTURE_DATE_TIME);

		await user.tab();

		await user.click(
			screen.getByRole('button', {name: /schedule-publication-to-live/i})
		);

		await waitFor(() => {
			expect(getPublishProcessCall()).toBeDefined();
		});

		const body = JSON.parse(getPublishProcessCall()![1]!.body as string);

		expect(body.scheduleStartDate).toBe(
			new Date(`${FUTURE_DATE_TIME.replace(' ', 'T')}:00Z`).toISOString()
		);
		expect(body.timeZoneId).toBe('UTC');

		expect(Liferay.Util.navigate).toHaveBeenCalledWith(
			DEFAULT_PROPS.scheduledBackURL
		);
	});

	it('seeds the form from the scheduled process when editing', async () => {
		renderComponent({
			scheduledPublishProcessId: SCHEDULED_PUBLISH_PROCESS.id,
		});

		expect(
			await screen.findByRole('textbox', {name: /^name/i})
		).toHaveValue(SCHEDULED_PUBLISH_PROCESS.name);

		expect(
			screen.getByRole('radio', {name: /schedule-for-later/})
		).toBeChecked();
		expect(screen.getByRole('textbox', {name: 'start-date'})).toHaveValue(
			FUTURE_DATE_TIME
		);
		expect(
			screen.getByRole('checkbox', {
				name: 'replicate-individual-deletions',
			})
		).toBeChecked();
		expect(screen.getByLabelText('filter-content-by')).toHaveValue(
			'fromLastPublishDate'
		);
	});

	it('replaces the scheduled process on submit when editing', async () => {
		renderComponent({
			scheduledPublishProcessId: SCHEDULED_PUBLISH_PROCESS.id,
		});

		await screen.findByRole('textbox', {name: /^name/i});
		await screen.findByText('loaded');

		await user.click(
			screen.getByRole('button', {name: /schedule-publication-to-live/i})
		);

		await waitFor(() => {
			expect(getUnscheduleCall()).toBeDefined();
		});

		expect(String(getUnscheduleCall()![0])).toContain(
			String(SCHEDULED_PUBLISH_PROCESS.id)
		);
		expect(Liferay.Util.navigate).toHaveBeenCalledWith(
			DEFAULT_PROPS.scheduledBackURL
		);
	});

	it('requires a future start date when the edited process has started', async () => {
		mockAPIRoutes({
			scheduledPublishProcess: {
				...SCHEDULED_PUBLISH_PROCESS,
				scheduleStartDate: '2026-07-01T09:30:00Z',
			},
		});

		renderComponent({
			scheduledPublishProcessId: SCHEDULED_PUBLISH_PROCESS.id,
		});

		expect(
			await screen.findByRole('textbox', {name: 'start-date'})
		).toHaveValue('2026-07-01 09:30');

		await screen.findByText('the-publish-time-must-be-in-the-future');

		expect(
			screen.getByRole('button', {name: /schedule-publication-to-live/i})
		).toBeDisabled();
	});

	it('schedules the edited process once the start date moves forward', async () => {
		mockAPIRoutes({
			scheduledPublishProcess: {
				...SCHEDULED_PUBLISH_PROCESS,
				scheduleStartDate: '2026-07-01T09:30:00Z',
			},
		});

		renderComponent({
			scheduledPublishProcessId: SCHEDULED_PUBLISH_PROCESS.id,
		});

		await screen.findByRole('textbox', {name: /^name/i});
		await screen.findByText('loaded');

		await user.clear(screen.getByRole('textbox', {name: 'start-date'}));

		await user.paste(FUTURE_DATE_TIME);

		const submitButton = screen.getByRole('button', {
			name: /schedule-publication-to-live/i,
		});

		await waitFor(() => {
			expect(submitButton).toBeEnabled();
		});

		await user.click(submitButton);

		await waitFor(() => {
			expect(getPublishProcessCall()).toBeDefined();
		});

		const body = JSON.parse(getPublishProcessCall()![1]!.body as string);

		expect(body.scheduleStartDate).toBe(
			new Date(`${FUTURE_DATE_TIME.replace(' ', 'T')}:00Z`).toISOString()
		);
	});

	it('reschedules when the old process is already unscheduled', async () => {
		mockAPIRoutes({deleteStatus: 404});

		renderComponent({
			scheduledPublishProcessId: SCHEDULED_PUBLISH_PROCESS.id,
		});

		await screen.findByRole('textbox', {name: /^name/i});
		await screen.findByText('loaded');

		await user.click(
			screen.getByRole('button', {name: /schedule-publication-to-live/i})
		);

		await waitFor(() => {
			expect(getPublishProcessCall()).toBeDefined();
		});

		expect(Liferay.Util.navigate).toHaveBeenCalledWith(
			DEFAULT_PROPS.scheduledBackURL
		);
	});

	it('keeps the old process scheduled until the new one is created', async () => {
		mockAPIRoutes();

		renderComponent({
			scheduledPublishProcessId: SCHEDULED_PUBLISH_PROCESS.id,
		});

		await screen.findByRole('textbox', {name: /^name/i});
		await screen.findByText('loaded');

		await user.click(
			screen.getByRole('button', {name: /schedule-publication-to-live/i})
		);

		await waitFor(() => {
			expect(getUnscheduleCall()).toBeDefined();
		});

		const requestMethods = fetch.mock.calls
			.filter(
				([url, init]) =>
					init?.method === 'DELETE' ||
					(String(url).endsWith('publish-processes') &&
						init?.method === 'POST')
			)
			.map(([, init]) => init?.method);

		expect(requestMethods).toEqual(['POST', 'DELETE']);
	});

	it('reports the failure when unscheduling the old process fails', async () => {
		mockAPIRoutes({deleteStatus: 500});

		renderComponent({
			scheduledPublishProcessId: SCHEDULED_PUBLISH_PROCESS.id,
		});

		await screen.findByRole('textbox', {name: /^name/i});
		await screen.findByText('loaded');

		await user.click(
			screen.getByRole('button', {name: /schedule-publication-to-live/i})
		);

		await waitFor(() => {
			expect(getUnscheduleCall()).toBeDefined();
		});

		expect(getPublishProcessCall()).toBeDefined();
		expect(Liferay.Util.openToast).toHaveBeenCalledWith(
			expect.objectContaining({type: 'danger'})
		);
	});

	it('requires a cron expression for a custom recurrence', async () => {
		renderComponent();

		await fillRequiredFields();

		await user.click(
			screen.getByRole('radio', {name: /schedule-for-later/})
		);

		await user.click(screen.getByRole('textbox', {name: 'start-date'}));

		await user.paste(FUTURE_DATE_TIME);

		await user.selectOptions(
			screen.getByRole('combobox', {name: 'repeat'}),
			'custom'
		);

		await screen.findByText('this-field-is-required');

		await waitFor(() => {
			expect(
				screen.getByRole('button', {
					name: /schedule-publication-to-live/i,
				})
			).toBeDisabled();
		});
	});
});
