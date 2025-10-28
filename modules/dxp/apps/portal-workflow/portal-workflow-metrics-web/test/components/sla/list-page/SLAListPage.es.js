/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, cleanup, fireEvent, render} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';

import {SLAContext} from '../../../../src/main/resources/META-INF/resources/js/components/sla/SLAContainer.es';
import SLAListPage from '../../../../src/main/resources/META-INF/resources/js/components/sla/list-page/SLAListPage.es';
import ToasterProvider from '../../../../src/main/resources/META-INF/resources/js/shared/components/toaster/ToasterProvider.es';
import {MockRouter} from '../../../mock/MockRouter.es';
import FetchMock, {fetchMockResponse} from '../../../mock/fetch.es';

describe('The SLAListPage component should', () => {
	describe('Be rendered correctly with no items', () => {
		let getByTitle;
		let getByText;

		beforeAll(async () => {
			fetch.mockImplementation(async () => ({
				json: async () => ({items: []}),
				ok: true,
			}));

			const renderResult = render(
				<MockRouter>
					<ToasterProvider>
						<SLAContext.Provider value={{}}>
							<SLAListPage
								page="1"
								pageSize="1"
								processId="36001"
							/>
						</SLAContext.Provider>
					</ToasterProvider>
				</MockRouter>
			);

			getByText = renderResult.getByText;
			getByTitle = renderResult.getByTitle;

			await act(async () => {
				jest.advanceTimersByTime(100);
			});
		});

		it('Show navbar with New SLA button with correct link', () => {
			const newSLAButton = getByTitle('new-sla');
			const childLink = newSLAButton.children[0];

			expect(childLink.getAttribute('href')).toContain('/sla/36001/new');
		});

		it('Display empty state', () => {
			const emptyStateMessage = getByText(
				'sla-allows-to-define-and-measure-process-performance'
			);

			expect(emptyStateMessage).toBeTruthy();
		});
	});

	describe('Be rendered correctly with items', () => {
		let container;
		let fetchMock;
		let getByText;

		const data = {
			actions: {},
			items: [
				{
					dateModified: '2020-04-03T18:01:07Z',
					description: '',
					duration: 60000,
					id: 37975,
					name: 'SLA',
					processId: 36001,
				},
			],
			totalCount: 1,
		};

		const contextMock = {SLAUpdated: true, setSLAUpdated: jest.fn()};

		beforeAll(async () => {
			cleanup();
			fetch.mockReset();

			fetchMock = new FetchMock({
				DELETE: {
					'/o/portal-workflow-metrics/v1.0/slas/37975': [
						fetchMockResponse({}, false),
						fetchMockResponse(),
					],
				},
				GET: {
					default: fetchMockResponse(data),
				},
			});

			const renderResult = render(
				<MockRouter>
					<ToasterProvider>
						<SLAContext.Provider value={contextMock}>
							<SLAListPage
								page="1"
								pageSize="1"
								processId="36001"
							/>
						</SLAContext.Provider>
					</ToasterProvider>
				</MockRouter>
			);

			container = renderResult.container;
			getByText = renderResult.getByText;

			await act(async () => {
				jest.advanceTimersByTime(100);
			});
		});

		beforeEach(() => {
			fetchMock.mock();
		});

		afterEach(() => {
			fetchMock.reset();
		});

		it('Show table columns', () => {
			const slaDateModifiedHead = getByText('last-modified');
			const slaDescriptionHead = getByText('description');
			const slaDurationHead = getByText('duration');
			const slaNameHead = getByText('sla-name');
			const slaStatusHead = getByText('status');

			expect(slaDateModifiedHead).toBeTruthy();
			expect(slaDescriptionHead).toBeTruthy();
			expect(slaDurationHead).toBeTruthy();
			expect(slaNameHead).toBeTruthy();
			expect(slaStatusHead).toBeTruthy();
		});

		it('Show items info and kebab menu', async () => {
			const kebab = container.querySelector('.dropdown-toggle');
			const slaDateModified = getByText('Apr 03');
			const slaDescription = slaDateModified.parentNode.children[1];
			const slaDuration = getByText('1min');
			const slaName = container.querySelector('.table-list-title');
			const slaStatus = getByText('running');

			expect(slaName).toHaveTextContent('SLA');
			expect(slaDescription).toHaveTextContent('');
			expect(slaStatus).toBeTruthy();
			expect(slaDuration).toBeTruthy();
			expect(slaDateModified).toBeTruthy();

			fireEvent.click(kebab);

			const dropDownItems = document.querySelectorAll('.dropdown-item');

			expect(dropDownItems[0]).toHaveTextContent('edit');
			expect(dropDownItems[1]).toHaveTextContent('delete');

			fireEvent.click(dropDownItems[1]);

			await act(async () => {
				jest.advanceTimersByTime(100);
			});
		});

		it('Display modal after clicking on delete option of kebab menu', async () => {
			const cancelButton = getByText('cancel');
			const deleteButton = getByText('ok');
			const deleteModal = getByText(
				'deleting-slas-will-reflect-on-report-data'
			);

			expect(deleteModal).toBeTruthy();
			expect(cancelButton).toBeTruthy();
			expect(deleteButton).toBeTruthy();

			fireEvent.click(deleteButton);

			await act(async () => {
				jest.advanceTimersByTime(100);
			});
		});

		it('Display toast when failure occur while trying to confirm item delete', async () => {
			const alertToast = document.querySelector('.alert-dismissible');

			const alertClose = alertToast.children[1];

			const deleteButton = getByText('ok');

			expect(alertToast).toHaveTextContent('your-request-has-failed');

			fireEvent.click(alertClose);

			const alertContainer = document.querySelector('.alert-container');

			expect(alertContainer.children[0].children.length).toBe(0);

			fireEvent.click(deleteButton);

			await act(async () => {
				jest.advanceTimersByTime(100);
			});
		});

		it.skip('Display toast when confirm item delete', () => {
			const alertToast = document.querySelector('.alert-dismissible');

			const alertClose = alertToast.children[1];

			expect(alertToast).toHaveTextContent('sla-was-deleted');

			fireEvent.click(alertClose);

			const alertContainer = document.querySelector('.alert-container');

			expect(alertContainer.children[0].children.length).toBe(0);
		});

		it('Display info alert and toast after a SLA is created or updated', () => {
			const updateAlert = container.querySelector('.alert-info');

			expect(updateAlert).toHaveTextContent(
				'one-or-more-slas-are-being-updated'
			);

			fireEvent.click(updateAlert.children[1]);

			expect(contextMock.setSLAUpdated).toHaveBeenCalledWith(false);
		});
	});

	describe('Be rendered correctly with blocked items', () => {
		let container;
		let getByText;

		const data = {
			actions: {},
			items: [
				{
					calendarKey: '',
					dateModified: '2020-04-06T01:26:17Z',
					description: '',
					duration: 540000,
					id: 39409,
					name: 'SLAb',
					processId: 36001,
					status: 2,
					stopNodeKeys: {
						nodeKeys: [
							{
								executionType: 'end',
								id: '39522',
							},
						],
						status: 0,
					},
				},
				{
					calendarKey: '',
					dateModified: '2020-04-03T18:01:07Z',
					description: '',
					duration: 60000,
					id: 37975,
					name: 'SLA',
					processId: 36001,
					startNodeKeys: {
						nodeKeys: [
							{
								executionType: 'begin',
								id: '36005',
							},
						],
						status: 0,
					},
					status: 0,
					stopNodeKeys: {
						nodeKeys: [
							{
								executionType: 'end',
								id: '36003',
							},
						],
						status: 0,
					},
				},
			],
			lastPage: 1,
			page: 1,
			pageSize: 20,
			totalCount: 1,
		};

		beforeAll(async () => {
			cleanup();

			fetch.mockReset();

			fetch.mockImplementation(async () => ({
				json: async () => data,
				ok: true,
			}));

			const renderResult = render(
				<MockRouter>
					<ToasterProvider>
						<SLAContext.Provider value={{}}>
							<SLAListPage
								page="1"
								pageSize="1"
								processId="36001"
							/>
						</SLAContext.Provider>
					</ToasterProvider>
				</MockRouter>
			);

			container = renderResult.container;
			getByText = renderResult.getByText;

			await act(async () => {
				jest.advanceTimersByTime(100);
			});
		});

		it('Show alert error', () => {
			const alertBlockedSLA = getByText(
				'fix-blocked-slas-to-resume-accurate-reporting'
			);

			expect(alertBlockedSLA).toBeTruthy();

			const alertClose = container.querySelector('button.close');

			fireEvent.click(alertClose);
		});

		it('Show dividers', () => {
			const slaBlockedDivider = getByText('BLOCKED');
			const slaRunningDivider = getByText('RUNNING');

			expect(slaBlockedDivider).toBeTruthy();
			expect(slaRunningDivider).toBeTruthy();
		});

		it('Show blocked items info correctly', () => {
			const dangerIcon = container.querySelector(
				'.lexicon-icon-exclamation-full'
			);
			const slaStatusBlocked = getByText('blocked');

			expect(dangerIcon).toBeTruthy();
			expect(dangerIcon.classList).toContain('text-danger');
			expect(slaStatusBlocked.classList).toContain('text-danger');
		});
	});
});
