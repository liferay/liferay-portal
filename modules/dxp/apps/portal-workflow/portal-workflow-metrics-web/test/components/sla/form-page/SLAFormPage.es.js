/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, cleanup, fireEvent, render} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';

import {SLAContext} from '../../../../src/main/resources/META-INF/resources/js/components/sla/SLAContainer.es';
import SLAFormPage from '../../../../src/main/resources/META-INF/resources/js/components/sla/form-page/SLAFormPage.es';
import ToasterProvider from '../../../../src/main/resources/META-INF/resources/js/shared/components/toaster/ToasterProvider.es';
import {MockRouter} from '../../../mock/MockRouter.es';
import FetchMock, {fetchMockResponse} from '../../../mock/fetch.es';

describe('The SLAFormPage component should', () => {
	const calendars = [
		{
			defaultCalendar: false,
			key: 'dummy',
			title: 'Dummy',
		},
		{
			defaultCalendar: true,
			key: 'default',
			title: '24/7',
		},
	];

	const nodes = [
		{
			id: 35903,
			initial: false,
			label: 'Approved',
			name: 'approved',
			terminal: true,
			type: 'STATE',
		},
		{
			id: 35905,
			initial: true,
			label: 'Created',
			name: 'created',
			terminal: false,
			type: 'STATE',
		},
		{
			id: 35911,
			initial: false,
			label: 'Review',
			name: 'review',
			terminal: false,
			type: 'TASK',
		},
		{
			id: 35927,
			initial: false,
			label: 'Update',
			name: 'update',
			terminal: false,
			type: 'TASK',
		},
	];

	describe('Create a new SLA', () => {
		let alertMessage;
		let container;
		let durationDaysField;
		let durationHoursField;
		let durationHoursInput;
		let fetchMock;
		let getByText;
		let nameField;
		let nameInput;
		let renderResult;
		let saveButton;
		let startField;
		let stopField;

		const data = {
			calendarKey: '',
			dateModified: '2020-04-01T02:36:00Z',
			description: '',
			duration: 300000,
			id: 38067,
			name: 'SLA',
			processId: 35901,
			startNodeKeys: {
				nodeKeys: [
					{
						executionType: 'begin',
						id: '37735',
					},
				],
				status: 0,
			},
			status: 0,
			stopNodeKeys: {
				nodeKeys: [
					{
						executionType: 'end',
						id: '37733',
					},
				],
				status: 0,
			},
		};

		const historyMock = {
			goBack: jest.fn(),
		};

		beforeAll(async () => {
			cleanup();

			fetchMock = new FetchMock({
				GET: {
					'/o/portal-workflow-metrics/v1.0/calendars':
						fetchMockResponse({
							items: calendars,
						}),
					'default': fetchMockResponse({items: nodes}),
				},
				POST: {
					'/o/portal-workflow-metrics/v1.0/processes/5678/slas': [
						fetchMockResponse({}, false),
						fetchMockResponse(
							[
								{
									fieldName: 'name',
									message:
										'An SLA with the same name already exists.',
								},
							],
							false
						),
						fetchMockResponse(data),
					],
				},
			});

			renderResult = render(
				<MockRouter>
					<ToasterProvider>
						<SLAContext.Provider value={{}}>
							<SLAFormPage
								history={historyMock}
								processId="5678"
								query=""
							/>
						</SLAContext.Provider>
					</ToasterProvider>
				</MockRouter>
			);

			container = renderResult.container;
			getByText = renderResult.getByText;

			await act(async () => {
				jest.runAllTimers();
			});
		});

		beforeEach(() => {
			fetchMock.mock();
		});

		afterEach(() => {
			fetchMock.reset();
		});

		it('Be rendered correctly', () => {
			durationDaysField = getByText('days').parentNode;
			durationHoursField = getByText('hours').parentNode;
			durationHoursInput = container.querySelector('#slaDurationHours');
			nameField = getByText('name').parentNode;
			nameInput = container.querySelector('#slaName');
			saveButton = getByText('save');
			startField = getByText('start').parentNode;
			stopField = getByText('stop').parentNode;
			const bodySheetTitle = getByText('sla-definition');
			const calendar = container.querySelector('#slaCalendarKey');
			const cancelButton = getByText('cancel');
			const descriptionField = getByText('description').parentNode;
			const descriptionInput = container.querySelector('#slaDescription');
			const daysFieldDescription = getByText('enter-a-whole-number');
			const durationDaysInput =
				container.querySelector('#slaDurationDays');
			const durationDescription = getByText(
				'define-the-sla-duration-and-calendar-format'
			);
			const durationLabel = getByText('DURATION');
			const pauseDescription = getByText(
				'time-will-not-be-considered-when'
			);
			const pauseField = getByText('pause');
			const startDescription = getByText('time-will-begin-counting-when');
			const stopDescription = getByText('time-will-stop-counting-when');
			const timeFrameDescription = getByText(
				'define-when-time-should-be-tracked-based-on-workflow-steps'
			);
			const timeFrameLabel = getByText('TIME-FRAME');

			expect(bodySheetTitle).toBeTruthy();
			expect(nameInput.value).toBe('');
			expect(descriptionField).toHaveTextContent('description');
			expect(descriptionInput.value).toBe('');
			expect(timeFrameLabel).toBeTruthy();
			expect(timeFrameDescription).toBeTruthy();
			expect(startDescription).toBeTruthy();
			expect(pauseField).toBeTruthy();
			expect(pauseDescription).toBeTruthy();
			expect(stopDescription).toBeTruthy();
			expect(stopField).toHaveTextContent('stop');
			expect(durationLabel).toBeTruthy();
			expect(durationDaysInput.value).toBe('');
			expect(durationDescription).toBeTruthy();
			expect(daysFieldDescription).toBeTruthy();
			expect(calendar.value).toBe('dummy');
			expect(calendar.children[0]).toHaveTextContent('Dummy');
			expect(calendar.children[1]).toHaveTextContent('24/7');
			expect(cancelButton).toBeTruthy();
			expect(nameField.classList).not.toContain('has-error');
			expect(startField.classList).not.toContain('has-error');
			expect(stopField.classList).not.toContain('has-error');
			expect(durationDaysField.classList).not.toContain('has-error');
			expect(durationHoursField.classList).not.toContain('has-error');
		});

		it('Display errors when submitting the form with empty values', () => {
			fireEvent.click(saveButton);

			alertMessage = getByText('please-fill-in-the-required-fields');

			expect(nameField).toHaveTextContent('a-name-is-required');
			expect(startField).toHaveTextContent(
				'at-least-one-parameter-is-required'
			);
			expect(stopField).toHaveTextContent(
				'at-least-one-parameter-is-required'
			);
			expect(durationDaysField).toHaveTextContent(
				'a-duration-time-is-required'
			);
			expect(durationHoursField).toHaveTextContent(
				'a-duration-time-is-required'
			);
			expect(alertMessage).toBeTruthy();
		});

		it('Display a field error when the duration receives an invalid value', () => {
			fireEvent.change(durationHoursInput, {target: {value: '99:99'}});

			fireEvent.blur(durationHoursInput);

			expect(durationHoursField).toHaveTextContent(
				'value-must-be-an-hour-below'
			);

			fireEvent.change(durationHoursInput, {target: {value: '00:01'}});

			fireEvent.blur(durationHoursInput);

			expect(durationHoursField.classList).not.toContain('has-error');
		});

		it('Dismiss errors when the inputs receive valid values and submit', async () => {
			const dropDownListItems =
				document.querySelectorAll('.dropdown-item');

			fireEvent.change(nameInput, {target: {value: 'SLA'}});

			fireEvent.blur(nameInput);

			fireEvent.mouseDown(dropDownListItems[0]);

			fireEvent.mouseDown(dropDownListItems[11]);

			fireEvent.change(durationHoursInput, {target: {value: '00:01'}});

			fireEvent.blur(durationHoursInput);

			expect(nameField.classList).not.toContain('has-error');
			expect(startField.classList).not.toContain('has-error');
			expect(stopField.classList).not.toContain('has-error');
			expect(durationDaysField.classList).not.toContain('has-error');
			expect(durationHoursField.classList).not.toContain('has-error');

			fireEvent.click(saveButton);

			await act(async () => {
				jest.runAllTimers();
			});
		});

		it('Display an error when a SLA submission failure happens and resubmit', async () => {
			const alertToast =
				await document.querySelector('.alert-dismissible');

			const alertClose = alertToast.children[1];

			expect(alertToast).toHaveTextContent('your-request-has-failed');

			fireEvent.click(alertClose);

			const alertContainer = document.querySelector('.alert-container');

			expect(alertContainer.children[0].children.length).toBe(0);

			fireEvent.click(saveButton);

			await act(async () => {
				jest.runAllTimers();
			});
		});

		it('Display an error when trying to submit a SLA with a name that already exists', async () => {
			expect(nameField).toHaveTextContent(
				'An SLA with the same name already exists.'
			);

			fireEvent.click(saveButton);

			await act(async () => {
				jest.runAllTimers();
			});
		});

		it('Redirect to SLAListPage after successful submit', async () => {
			expect(historyMock.goBack).toHaveBeenCalled();
		});
	});

	describe('Edit a SLA', () => {
		let container;
		let fetchMock;
		let getByText;
		let renderResult;

		const data = {
			calendarKey: 'default',
			dateModified: '2020-03-31T19:22:35Z',
			description: '',
			duration: 60000,
			id: 37772,
			name: 'SLA',
			processId: 35901,
			startNodeKeys: {
				nodeKeys: [
					{
						executionType: 'begin',
						id: '35905',
					},
				],
				status: 0,
			},
			status: 0,
			stopNodeKeys: {
				nodeKeys: [
					{
						executionType: 'end',
						id: '35903',
					},
				],
				status: 0,
			},
		};

		const historyMock = {
			goBack: jest.fn(),
		};

		const contextMock = {setSLAUpdated: jest.fn()};

		beforeAll(async () => {
			cleanup();

			fetchMock = new FetchMock({
				GET: {
					'/o/portal-workflow-metrics/v1.0/calendars':
						fetchMockResponse({
							items: calendars,
						}),
					'/o/portal-workflow-metrics/v1.0/processes/5678/nodes':
						fetchMockResponse({
							items: nodes,
						}),
					'default': fetchMockResponse(data),
				},
				PUT: {
					default: fetchMockResponse({}),
				},
			});

			renderResult = render(
				<MockRouter>
					<ToasterProvider>
						<SLAContext.Provider value={contextMock}>
							<SLAFormPage
								history={historyMock}
								id="1234"
								processId="5678"
							/>
						</SLAContext.Provider>
					</ToasterProvider>
				</MockRouter>
			);

			container = renderResult.container;
			getByText = renderResult.getByText;

			await act(async () => {
				jest.runAllTimers();
			});
		});

		beforeEach(() => {
			fetchMock.mock();
		});

		afterEach(() => {
			fetchMock.reset();
		});

		it('Render form in edit mode with correct data', async () => {
			const calendar = container.querySelector('#slaCalendarKey');
			const durationDaysField = getByText('days').parentNode;
			const durationHoursField = getByText('hours').parentNode;
			const durationHoursInput =
				container.querySelector('#slaDurationHours');
			const multiSelectItems =
				container.querySelectorAll('.label-dismissible');
			const nameField = getByText('name').parentNode;
			const nameInput = container.querySelector('#slaName');
			const startField = getByText('start').parentNode;
			const stopField = getByText('stop').parentNode;
			const updateButton = getByText('update');

			expect(nameInput.value).toBe('SLA');
			expect(multiSelectItems[0]).toHaveTextContent('process-begins');
			expect(multiSelectItems[1]).toHaveTextContent('process-ends');
			expect(durationHoursInput.value).toBe('00:01');
			expect(calendar.value).toBe('default');
			expect(nameField.classList).not.toContain('has-error');
			expect(startField.classList).not.toContain('has-error');
			expect(stopField.classList).not.toContain('has-error');
			expect(durationDaysField.classList).not.toContain('has-error');
			expect(durationHoursField.classList).not.toContain('has-error');

			fireEvent.click(updateButton);

			await act(async () => {
				jest.runAllTimers();
			});
		});

		it.skip('Redirect to SLAListPage after successful submit', async () => {
			expect(historyMock.goBack).toHaveBeenCalled();
			expect(contextMock.setSLAUpdated).toHaveBeenCalledWith(true);
		});
	});

	describe('Edit a Blocked SLA', () => {
		let fetchMock;
		let getByText;
		let renderResult;

		const nodes = [
			{
				id: 37733,
				initial: false,
				label: 'Approved',
				name: 'approved',
				terminal: true,
				type: 'STATE',
			},
			{
				id: 37735,
				initial: true,
				label: 'Created',
				name: 'created',
				terminal: false,
				type: 'STATE',
			},
			{
				id: 37741,
				initial: false,
				label: 'Review',
				name: 'review',
				terminal: false,
				type: 'TASK',
			},
		];

		const data = {
			calendarKey: '',
			dateModified: '2020-03-31T15:56:06Z',
			description: '',
			duration: 60000,
			id: 37538,
			name: 'SLA',
			pauseNodeKeys: {
				nodeKeys: [
					{
						executionType: 'on',
						id: '37741',
					},
				],
				status: 0,
			},
			processId: 35901,
			status: 2,
		};

		beforeAll(async () => {
			cleanup();

			fetchMock = new FetchMock({
				GET: {
					'/o/portal-workflow-metrics/v1.0/calendars':
						fetchMockResponse({
							items: calendars,
						}),
					'/o/portal-workflow-metrics/v1.0/processes/35901/nodes':
						fetchMockResponse({
							items: nodes,
						}),
					'default': fetchMockResponse(data),
				},
			});

			renderResult = render(
				<MockRouter>
					<ToasterProvider>
						<SLAContext.Provider value={{}}>
							<SLAFormPage id="37741" processId="35901" />
						</SLAContext.Provider>
					</ToasterProvider>
				</MockRouter>
			);

			getByText = renderResult.getByText;

			await act(async () => {
				jest.runAllTimers();
			});
		});

		beforeEach(() => {
			fetchMock.mock();
		});

		afterEach(() => {
			fetchMock.reset();
		});

		it('Handle errors at start and stop node keys', () => {
			const alertChange = getByText(
				'the-time-frame-options-changed-in-the-workflow-definition'
			);
			const startField = getByText('start').parentNode;
			const stopField = getByText('stop').parentNode;

			expect(alertChange).toBeTruthy();
			expect(startField).toHaveTextContent(
				'selected-option-is-no-longer-available'
			);
			expect(stopField).toHaveTextContent(
				'selected-option-is-no-longer-available'
			);
		});
	});
});
