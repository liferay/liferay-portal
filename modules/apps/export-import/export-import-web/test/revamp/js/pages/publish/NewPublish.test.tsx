/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	fireEvent,
	render,
	screen,
	waitFor,
	within,
} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import fetch from 'jest-fetch-mock';
import React from 'react';

import '@testing-library/jest-dom';

import {NewPublish} from '../../../../../src/main/resources/META-INF/resources/revamp/js/pages/publish/NewPublish';
import {
	PublishProcessRequest,
	ScheduledPublishProcess,
} from '../../../../../src/main/resources/META-INF/resources/revamp/js/types/exportImportProcess';
import {
	toDateTimeParts,
	toWallClockDateTime,
} from '../../../../../src/main/resources/META-INF/resources/revamp/js/utils/dateTime';
import {mockPreview} from '../../mocks/mockPreview';

const DAY = 24 * 60 * 60 * 1000;

const FUTURE_DATE = new Date(
	new Date(Date.now() + DAY).setUTCHours(15, 30, 0, 0)
);

const FUTURE_DATE_TIME = toWallClockDateTime(FUTURE_DATE.toISOString(), 'UTC');

function toDisplayDate(date: Date): string {
	return date.toLocaleDateString('en-US', {
		day: '2-digit',
		month: '2-digit',
		timeZone: 'UTC',
		year: 'numeric',
	});
}

const FUTURE_DISPLAY_DATE = toDisplayDate(FUTURE_DATE);

const FUTURE_DISPLAY_DATE_TIME = `${FUTURE_DISPLAY_DATE} 03:30 PM`;

const PUBLISH_PROCESS_REQUEST: PublishProcessRequest = {
	cronExpression: '0 30 9 ? * MON/1 *',
	dateRangeType: 'FROM_LAST_PUBLISH_DATE',
	deletions: true,
	name: 'Weekly Content Sync',
	permissions: false,
	portletDataHandlers: [
		{
			name: 'PORTLET_DATA_com_liferay_journal_web_portlet_JournalPortlet',
		},
	],
	timeZoneId: 'UTC',
};

const SCHEDULED_PUBLISH_PROCESS: ScheduledPublishProcess = {
	id: 1234,
	name: 'Weekly Content Sync',
	publishProcessRequest: PUBLISH_PROCESS_REQUEST,
	scheduleStartDate: FUTURE_DATE.toISOString(),
};

const DEFAULT_PROPS = {
	backURL: '/some/back/url',
	pageTreeModalConfiguration: {
		groupId: 20121,
		pageSize: 20,
		privateLayoutsAvailable: false,
	},
	processesBackURL: '/some/back/url?tab=processes',
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

const getFilteredPreviewCall = () =>
	fetch.mock.calls.find(([url]) => String(url).includes('publish-preview?'));

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

const pickCalendarDay = async (datePicker: HTMLElement) => {
	const chooseDateButton = within(datePicker).getByRole('button', {
		name: 'Choose date',
	});

	await user.click(chooseDateButton);

	const calendar = document.getElementById(
		chooseDateButton.getAttribute('aria-controls') as string
	) as HTMLElement;

	if (!within(calendar).queryByLabelText(FUTURE_DATE.toDateString())) {
		await user.click(
			within(calendar).getByRole('button', {
				name: 'Select the next month',
			})
		);
	}

	await user.click(
		within(calendar).getByLabelText(FUTURE_DATE.toDateString())
	);
};

describe('NewPublish', () => {
	beforeEach(() => {
		jest.clearAllMocks();

		fetch.resetMocks();

		mockAPIRoutes();
	});

	it('publishes immediately and navigates to processes', async () => {
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
		expect(body.portletDataHandlers.length).toBeGreaterThan(0);

		expect(Liferay.Util.navigate).toHaveBeenCalledWith(
			DEFAULT_PROPS.processesBackURL
		);
	});

	it('selects the schedule option when it is the default', async () => {
		renderComponent({defaultScheduled: true});

		expect(
			await screen.findByRole('radio', {name: /schedule-for-later/})
		).toBeChecked();
	});

	it('starts a new schedule on the time zone of the user', async () => {
		renderComponent({
			defaultScheduled: true,
			timeZoneId: 'Europe/Paris',
			timeZones: [
				{label: '(UTC) Coordinated Universal Time', value: 'UTC'},
				{
					label: '(UTC +01:00) Central European Time',
					value: 'Europe/Paris',
				},
			],
		});

		expect(
			await screen.findByRole('combobox', {name: 'time-zone'})
		).toHaveValue('Europe/Paris');
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

		expect(
			screen.queryByText(
				'please-set-a-start-date-and-time-to-schedule-the-publication'
			)
		).not.toBeInTheDocument();

		const startDateField = screen.getByRole('textbox', {
			name: /start-date/,
		});

		await user.click(startDateField);
		await user.tab();

		expect(
			await screen.findByText(
				'please-set-a-start-date-and-time-to-schedule-the-publication'
			)
		).toBeInTheDocument();

		await user.click(startDateField);
		await user.paste(FUTURE_DISPLAY_DATE_TIME);

		await waitFor(() => {
			expect(submitButton).toBeEnabled();
		});

		expect(
			screen.queryByText(
				'please-set-a-start-date-and-time-to-schedule-the-publication'
			)
		).not.toBeInTheDocument();
	});

	it('does not show an error while a valid date is still being typed', async () => {
		renderComponent();

		await fillRequiredFields();

		await user.click(
			screen.getByRole('radio', {name: /schedule-for-later/})
		);

		await user.type(
			screen.getByRole('textbox', {name: /start-date/}),
			FUTURE_DISPLAY_DATE.slice(0, 5)
		);

		expect(
			screen.queryByText('please-enter-a-valid-date')
		).not.toBeInTheDocument();
		expect(
			screen.queryByText(
				'please-set-a-start-date-and-time-to-schedule-the-publication'
			)
		).not.toBeInTheDocument();
	});

	it('does not overwrite a time being typed after a complete date', async () => {
		renderComponent();

		await fillRequiredFields();

		await user.click(
			screen.getByRole('radio', {name: /schedule-for-later/})
		);

		const startDateField = screen.getByRole('textbox', {
			name: /start-date/,
		});

		await user.click(startDateField);
		await user.type(startDateField, `${FUTURE_DISPLAY_DATE} 1`);

		expect(startDateField).toHaveValue(`${FUTURE_DISPLAY_DATE} 1`);
	});

	it('defaults the start date time to midnight when a day is picked from the calendar', async () => {
		renderComponent();

		await fillRequiredFields();

		await user.click(
			screen.getByRole('radio', {name: /schedule-for-later/})
		);

		const startDateField = screen.getByRole('textbox', {
			name: /start-date/,
		});
		const startDatePicker = startDateField.closest(
			'.date-picker'
		) as HTMLElement;

		await pickCalendarDay(startDatePicker);

		expect(startDateField).toHaveValue(`${FUTURE_DISPLAY_DATE} 12:00 AM`);

		await user.click(document.body);

		await waitFor(() => {
			expect(
				screen.getByRole('button', {
					name: /schedule-publication-to-live/i,
				})
			).toBeEnabled();
		});
	});

	it('enables scheduling once a typed start date gets its default time on blur', async () => {
		renderComponent();

		await fillRequiredFields();

		await user.click(
			screen.getByRole('radio', {name: /schedule-for-later/})
		);

		const startDateField = screen.getByRole('textbox', {
			name: /start-date/,
		});

		await user.click(startDateField);
		await user.paste(FUTURE_DISPLAY_DATE);
		await user.tab();

		expect(startDateField).toHaveValue(`${FUTURE_DISPLAY_DATE} 12:00 AM`);

		await waitFor(() => {
			expect(
				screen.getByRole('button', {
					name: /schedule-publication-to-live/i,
				})
			).toBeEnabled();
		});
	});

	it('defaults the end date time to 23:59 when a day is picked without a time', async () => {
		renderComponent();

		await fillRequiredFields();

		await user.click(
			screen.getByRole('radio', {name: /schedule-for-later/})
		);

		await user.selectOptions(
			screen.getByRole('combobox', {name: 'repeat'}),
			'week'
		);

		await user.click(screen.getByRole('checkbox', {name: /never-end/}));

		const endDateField = screen.getByRole('textbox', {
			name: /end-date/,
		});

		fireEvent.change(endDateField, {
			target: {value: `${FUTURE_DISPLAY_DATE} --:-- --`},
		});

		expect(endDateField).toHaveValue(`${FUTURE_DISPLAY_DATE} 11:59 PM`);
	});

	it('shows the end date error only after the field is touched', async () => {
		renderComponent();

		await fillRequiredFields();

		await user.click(
			screen.getByRole('radio', {name: /schedule-for-later/})
		);

		await user.click(screen.getByRole('textbox', {name: /start-date/}));

		await user.paste(FUTURE_DISPLAY_DATE_TIME);

		await user.selectOptions(
			screen.getByRole('combobox', {name: 'repeat'}),
			'week'
		);

		await user.click(screen.getByRole('checkbox', {name: /never-end/}));

		expect(
			screen.queryByText('please-enter-a-valid-date')
		).not.toBeInTheDocument();

		const endDateField = screen.getByRole('textbox', {name: /end-date/});

		await user.click(endDateField);
		await user.tab();

		expect(
			await screen.findByText('please-enter-a-valid-date')
		).toBeInTheDocument();
	});

	it('marks the schedule fields as required as soon as they appear', async () => {
		renderComponent();

		await fillRequiredFields();

		await user.click(
			screen.getByRole('radio', {name: /schedule-for-later/})
		);

		expect(
			screen.getByRole('textbox', {name: /start-date/})
		).toHaveAccessibleName(/mandatory/i);

		expect(
			screen.queryByRole('textbox', {name: /end-date/})
		).not.toBeInTheDocument();

		await user.selectOptions(
			screen.getByRole('combobox', {name: 'repeat'}),
			'week'
		);

		await user.click(screen.getByRole('checkbox', {name: /never-end/}));

		expect(
			screen.getByRole('textbox', {name: /end-date/})
		).toHaveAccessibleName(/mandatory/i);

		await user.selectOptions(
			screen.getByRole('combobox', {name: 'repeat'}),
			'custom'
		);

		expect(
			screen.getByRole('textbox', {name: /cron-expression/})
		).toHaveAccessibleName(/mandatory/i);

		await user.selectOptions(
			screen.getByRole('combobox', {name: 'repeat'}),
			'week'
		);

		expect(
			screen.queryByRole('group', {name: /repeat-at.*mandatory/i})
		).not.toBeInTheDocument();

		await user.click(
			screen.getByRole('checkbox', {name: 'sync-with-start-date-time'})
		);

		expect(
			screen.getByRole('group', {name: /repeat-at.*mandatory/i})
		).toBeInTheDocument();
	});

	it('schedules the publication with the cron fields', async () => {
		renderComponent();

		await fillRequiredFields();

		await user.click(
			screen.getByRole('radio', {name: /schedule-for-later/})
		);

		await user.click(screen.getByRole('textbox', {name: /start-date/}));

		await user.paste(FUTURE_DISPLAY_DATE_TIME);

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

	it('drops the end date once the repetition goes back to never', async () => {
		renderComponent();

		await fillRequiredFields();

		await user.click(
			screen.getByRole('radio', {name: /schedule-for-later/})
		);

		await user.click(screen.getByRole('textbox', {name: /start-date/}));

		await user.paste(FUTURE_DISPLAY_DATE_TIME);

		await user.selectOptions(
			screen.getByRole('combobox', {name: 'repeat'}),
			'week'
		);

		await user.click(screen.getByRole('checkbox', {name: /never-end/}));

		await user.click(screen.getByRole('textbox', {name: /end-date/}));

		await user.paste(
			`${toDisplayDate(new Date(FUTURE_DATE.getTime() + DAY))} 03:30 PM`
		);

		await user.selectOptions(
			screen.getByRole('combobox', {name: 'repeat'}),
			'never'
		);

		await user.click(
			screen.getByRole('button', {name: /schedule-publication-to-live/i})
		);

		await waitFor(() => {
			expect(getPublishProcessCall()).toBeDefined();
		});

		const body = JSON.parse(getPublishProcessCall()![1]!.body as string);

		expect(body.scheduleEndDate).toBeUndefined();
	});

	it('drops the seeded end date once the repetition goes back to never', async () => {
		mockAPIRoutes({
			scheduledPublishProcess: {
				...SCHEDULED_PUBLISH_PROCESS,
				scheduleEndDate: new Date(
					FUTURE_DATE.getTime() + DAY
				).toISOString(),
			},
		});

		renderComponent({
			scheduledPublishProcessId: SCHEDULED_PUBLISH_PROCESS.id,
		});

		await screen.findByRole('textbox', {name: /^name/i});
		await screen.findByText('loaded');

		expect(
			screen.getByRole('checkbox', {name: /never-end/})
		).not.toBeChecked();

		await user.selectOptions(
			screen.getByRole('combobox', {name: 'repeat'}),
			'never'
		);

		await user.click(
			screen.getByRole('button', {name: /schedule-publication-to-live/i})
		);

		await waitFor(() => {
			expect(getPublishProcessCall()).toBeDefined();
		});

		const body = JSON.parse(getPublishProcessCall()![1]!.body as string);

		expect(body.scheduleEndDate).toBeUndefined();
	});

	it('drops the stored end date of an edited one time schedule', async () => {
		const {day, hour, minute, month, year} =
			toDateTimeParts(FUTURE_DATE_TIME);

		const scheduleEndDate = new Date(
			FUTURE_DATE.getTime() + DAY
		).toISOString();

		mockAPIRoutes({
			scheduledPublishProcess: {
				...SCHEDULED_PUBLISH_PROCESS,
				publishProcessRequest: {
					...PUBLISH_PROCESS_REQUEST,
					cronExpression: `0 ${minute} ${hour} ${day} ${month} ? ${year}`,
				},
				scheduleEndDate,
			},
		});

		renderComponent({
			scheduledPublishProcessId: SCHEDULED_PUBLISH_PROCESS.id,
		});

		await screen.findByRole('textbox', {name: /^name/i});
		await screen.findByText('loaded');

		expect(screen.getByRole('combobox', {name: 'repeat'})).toHaveValue(
			'never'
		);

		await user.click(
			screen.getByRole('button', {name: /schedule-publication-to-live/i})
		);

		await waitFor(() => {
			expect(getPublishProcessCall()).toBeDefined();
		});

		const body = JSON.parse(getPublishProcessCall()![1]!.body as string);

		expect(body.scheduleEndDate).toBeUndefined();
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
		expect(screen.getByRole('textbox', {name: /start-date/})).toHaveValue(
			FUTURE_DISPLAY_DATE_TIME
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

	it('round trips a seeded date range through the portal time zone', async () => {
		mockAPIRoutes({
			scheduledPublishProcess: {
				...SCHEDULED_PUBLISH_PROCESS,
				publishProcessRequest: {
					...PUBLISH_PROCESS_REQUEST,
					dateRangeType: 'DATE_RANGE',
					endDate: '2026-09-01T07:30:00Z',
					name: SCHEDULED_PUBLISH_PROCESS.name!,
					startDate: '2026-08-22T13:05:00Z',
				},
			},
		});

		renderComponent({
			scheduledPublishProcessId: SCHEDULED_PUBLISH_PROCESS.id,
			timeZoneId: 'Europe/Madrid',
		});

		await screen.findByRole('textbox', {name: /^name/i});
		await screen.findByText('loaded');

		expect(screen.getByLabelText('filter-content-by')).toHaveValue(
			'dateRange'
		);
		expect(screen.getByLabelText('from')).toHaveValue(
			'08/22/2026 03:05 PM'
		);
		expect(screen.getByLabelText('to[date-time]')).toHaveValue(
			'09/01/2026 09:30 AM'
		);

		const previewSearchParams = new URL(
			String(getFilteredPreviewCall()![0]),
			'http://localhost'
		).searchParams;

		expect(previewSearchParams.get('startDate')).toBe(
			'2026-08-22T13:05:00.000Z'
		);
		expect(previewSearchParams.get('endDate')).toBe(
			'2026-09-01T07:30:00.000Z'
		);

		await user.click(
			screen.getByRole('button', {name: /schedule-publication-to-live/i})
		);

		await waitFor(() => {
			expect(getPublishProcessCall()).toBeDefined();
		});

		const body = JSON.parse(getPublishProcessCall()![1]!.body as string);

		expect(body.dateRangeType).toBe('DATE_RANGE');
		expect(body.startDate).toBe('2026-08-22T13:05:00.000Z');
		expect(body.endDate).toBe('2026-09-01T07:30:00.000Z');
	});

	it('reclassifies a custom cron whose time matches the start date, keeping the sync', async () => {
		const midnight = new Date(FUTURE_DATE);

		midnight.setUTCHours(0, 0, 0, 0);

		mockAPIRoutes({
			scheduledPublishProcess: {
				...SCHEDULED_PUBLISH_PROCESS,
				publishProcessRequest: {
					...PUBLISH_PROCESS_REQUEST,
					cronExpression: '0 0 0 ? * MON,FRI *',
				},
				scheduleStartDate: midnight.toISOString(),
			},
		});

		renderComponent({
			scheduledPublishProcessId: SCHEDULED_PUBLISH_PROCESS.id,
		});

		await screen.findByRole('textbox', {name: /^name/i});

		expect(screen.getByRole('combobox', {name: 'repeat'})).toHaveValue(
			'week'
		);
		expect(screen.getByRole('button', {name: 'Monday'})).toHaveAttribute(
			'aria-pressed',
			'true'
		);
		expect(screen.getByRole('button', {name: 'Friday'})).toHaveAttribute(
			'aria-pressed',
			'true'
		);
		expect(
			screen.getByRole('checkbox', {name: 'sync-with-start-date-time'})
		).toBeChecked();
		expect(screen.getByLabelText('hours')).toHaveValue('12');
		expect(screen.getByLabelText('am-pm')).toHaveValue('AM');
		expect(screen.getByLabelText('hours')).toBeDisabled();
	});

	it('reclassifies a custom cron whose time differs from the start date, unchecking the sync', async () => {
		mockAPIRoutes({
			scheduledPublishProcess: {
				...SCHEDULED_PUBLISH_PROCESS,
				publishProcessRequest: {
					...PUBLISH_PROCESS_REQUEST,
					cronExpression: '0 0 0 ? * MON,FRI *',
				},
				scheduleStartDate: FUTURE_DATE.toISOString(),
			},
		});

		renderComponent({
			scheduledPublishProcessId: SCHEDULED_PUBLISH_PROCESS.id,
		});

		await screen.findByRole('textbox', {name: /^name/i});

		expect(screen.getByRole('combobox', {name: 'repeat'})).toHaveValue(
			'week'
		);
		expect(screen.getByRole('button', {name: 'Monday'})).toHaveAttribute(
			'aria-pressed',
			'true'
		);
		expect(screen.getByRole('button', {name: 'Friday'})).toHaveAttribute(
			'aria-pressed',
			'true'
		);
		expect(
			screen.getByRole('checkbox', {name: 'sync-with-start-date-time'})
		).not.toBeChecked();
		expect(screen.getByLabelText('hours')).toHaveValue('12');
		expect(screen.getByLabelText('am-pm')).toHaveValue('AM');
		expect(screen.getByLabelText('hours')).toBeEnabled();
	});

	it('shows the original cron when a reclassified process is switched back to custom', async () => {
		mockAPIRoutes({
			scheduledPublishProcess: {
				...SCHEDULED_PUBLISH_PROCESS,
				publishProcessRequest: {
					...PUBLISH_PROCESS_REQUEST,
					cronExpression: '0 0 0 ? * MON,FRI *',
				},
				scheduleStartDate: FUTURE_DATE.toISOString(),
			},
		});

		renderComponent({
			scheduledPublishProcessId: SCHEDULED_PUBLISH_PROCESS.id,
		});

		await screen.findByRole('textbox', {name: /^name/i});

		await user.selectOptions(
			screen.getByRole('combobox', {name: 'repeat'}),
			'custom'
		);

		expect(
			screen.getByRole('textbox', {name: /cron-expression/})
		).toHaveValue('0 0 0 ? * MON,FRI *');
	});

	it('rebuilds the cron of a reclassified process edited before switching to custom', async () => {
		mockAPIRoutes({
			scheduledPublishProcess: {
				...SCHEDULED_PUBLISH_PROCESS,
				publishProcessRequest: {
					...PUBLISH_PROCESS_REQUEST,
					cronExpression: '0 0 0 ? * MON,FRI *',
				},
				scheduleStartDate: FUTURE_DATE.toISOString(),
			},
		});

		renderComponent({
			scheduledPublishProcessId: SCHEDULED_PUBLISH_PROCESS.id,
		});

		await screen.findByRole('textbox', {name: /^name/i});

		fireEvent.keyDown(screen.getByLabelText('hours'), {key: 'Backspace'});
		fireEvent.keyDown(screen.getByLabelText('hours'), {key: '3'});
		fireEvent.keyDown(screen.getByLabelText('minutes'), {key: 'Backspace'});
		fireEvent.keyDown(screen.getByLabelText('minutes'), {key: '1'});
		fireEvent.keyDown(screen.getByLabelText('minutes'), {key: '5'});
		fireEvent.keyDown(screen.getByLabelText('am-pm'), {key: 'ArrowUp'});

		await user.selectOptions(
			screen.getByRole('combobox', {name: 'repeat'}),
			'custom'
		);

		expect(
			screen.getByRole('textbox', {name: /cron-expression/})
		).toHaveValue('0 15 15 ? * MON,FRI *');
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

	it('navigates to processes when an edited scheduled process is switched to publish now', async () => {
		renderComponent({
			scheduledPublishProcessId: SCHEDULED_PUBLISH_PROCESS.id,
		});

		await screen.findByRole('textbox', {name: /^name/i});
		await screen.findByText('loaded');

		await user.click(screen.getByRole('radio', {name: /publish-now/}));

		await user.click(screen.getByRole('button', {name: /-to-live/i}));

		await waitFor(() => {
			expect(getUnscheduleCall()).toBeDefined();
		});

		expect(String(getUnscheduleCall()![0])).toContain(
			String(SCHEDULED_PUBLISH_PROCESS.id)
		);
		expect(Liferay.Util.navigate).toHaveBeenCalledWith(
			DEFAULT_PROPS.processesBackURL
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
			await screen.findByRole('textbox', {name: /start-date/})
		).toHaveValue('07/01/2026 09:30 AM');

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

		await user.clear(screen.getByRole('textbox', {name: /start-date/}));

		await user.paste(FUTURE_DISPLAY_DATE_TIME);

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

		await user.click(screen.getByRole('textbox', {name: /start-date/}));

		await user.paste(FUTURE_DISPLAY_DATE_TIME);

		await user.selectOptions(
			screen.getByRole('combobox', {name: 'repeat'}),
			'custom'
		);

		expect(
			screen.queryByText('this-field-is-required')
		).not.toBeInTheDocument();

		const cronExpressionField = screen.getByRole('textbox', {
			name: /cron-expression/,
		});

		await user.click(cronExpressionField);
		await user.tab();

		expect(
			await screen.findByText('this-field-is-required')
		).toBeInTheDocument();

		await waitFor(() => {
			expect(
				screen.getByRole('button', {
					name: /schedule-publication-to-live/i,
				})
			).toBeDisabled();
		});
	});

	it('requires the repeat at time once the sync is unchecked', async () => {
		renderComponent();

		await fillRequiredFields();

		await user.click(
			screen.getByRole('radio', {name: /schedule-for-later/})
		);

		await user.click(screen.getByRole('textbox', {name: /start-date/}));

		await user.paste(FUTURE_DISPLAY_DATE_TIME);

		await user.selectOptions(
			screen.getByRole('combobox', {name: 'repeat'}),
			'week'
		);

		await user.click(
			screen.getByRole('checkbox', {name: 'sync-with-start-date-time'})
		);

		await user.click(screen.getByLabelText('hours'));

		fireEvent.keyDown(screen.getByLabelText('hours'), {key: 'Backspace'});

		await user.tab();

		expect(
			screen.queryByText('this-field-is-required')
		).not.toBeInTheDocument();

		await user.click(document.body);

		expect(
			await screen.findByText('this-field-is-required')
		).toBeInTheDocument();

		await waitFor(() => {
			expect(
				screen.getByRole('button', {
					name: /schedule-publication-to-live/i,
				})
			).toBeDisabled();
		});
	});
});
