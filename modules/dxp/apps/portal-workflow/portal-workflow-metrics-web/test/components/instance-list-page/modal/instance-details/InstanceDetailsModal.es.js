/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, cleanup, fireEvent, render} from '@testing-library/react';
import React, {useState} from 'react';

import '@testing-library/jest-dom';

import {InstanceListContext} from '../../../../../src/main/resources/META-INF/resources/js/components/instance-list-page/InstanceListPageProvider.es';
import {ModalContext} from '../../../../../src/main/resources/META-INF/resources/js/components/instance-list-page/modal/ModalProvider.es';
import InstanceDetailsModal from '../../../../../src/main/resources/META-INF/resources/js/components/instance-list-page/modal/instance-details/InstanceDetailsModal.es';
import {MockRouter} from '../../../../mock/MockRouter.es';

const ContainerMock = ({children}) => {
	const [instanceId, setInstanceId] = useState(37634);

	return (
		<MockRouter>
			<InstanceListContext.Provider value={{instanceId, setInstanceId}}>
				<ModalContext.Provider
					value={{
						closeModal: jest.fn(),
						processId: '12345',
						visibleModal: 'instanceDetails',
					}}
				>
					{children}
				</ModalContext.Provider>
			</InstanceListContext.Provider>
		</MockRouter>
	);
};

const data = {
	assetTitle: 'Blog 01',
	assetType: 'Blogs Entry',
	completed: true,
	creator: {
		id: 1,
		name: 'Test Test',
	},
	dateCompletion: '2020-01-21T10:08:30Z',
	dateCreated: '2020-01-20T11:08:30Z',
	id: 37634,
	processId: 30000,
	slaResults: [
		{
			dateOverdue: '2020-01-23T07:08:30Z',
			id: 37315,
			name: 'Review',
			onTime: true,
			remainingTime: 13427723,
			status: 'STOPPED',
		},
		{
			dateOverdue: '2020-01-24T10:08:30Z',
			id: 37318,
			name: 'Update',
			onTime: false,
			remainingTime: -13427723,
			status: 'RUNNING',
		},
	],
	slaStatus: 'Overdue',
};

describe('The InstanceDetailsModal component should', () => {
	let getByText;
	let renderResult;

	const renderComponent = () => {
		cleanup();

		renderResult = render(
			<ContainerMock>
				<InstanceDetailsModal />
			</ContainerMock>
		);

		getByText = renderResult.getByText;
	};

	describe('render with a completed Instance', () => {
		beforeAll(async () => {
			fetch.mockResolvedValueOnce({
				json: () => Promise.resolve(data),
				ok: true,
			});

			renderComponent();

			await act(async () => {
				jest.runAllTimers();
			});
		});

		it('Render Modal title with correct item id and status icon', () => {
			const instanceDetailsTitle = getByText('item #37634');
			const instanceIconTitle = document.querySelector(
				'.lexicon-icon-check-circle'
			);

			expect(instanceDetailsTitle).toBeTruthy();
			expect(instanceIconTitle).toBeTruthy();
		});

		it('Render SLA details with correct status', () => {
			const resultIcons = document.querySelectorAll('.sticker');
			const resultStatus = document.querySelectorAll('.sla-result');

			expect(getByText('OPEN (1)')).toBeTruthy();
			expect(resultStatus[0]).toHaveTextContent(
				'Jan 24, 2020, 10:08 AM (0d 3h 43min overdue)'
			);
			expect(resultIcons[1].children[0].classList).toContain(
				'lexicon-icon-exclamation-circle'
			);
			expect(getByText('RESOLVED (1)')).toBeTruthy();
			expect(resultStatus[1]).toHaveTextContent('(resolved-on-time)');
			expect(resultIcons[2].children[0].classList).toContain(
				'lexicon-icon-check-circle'
			);
		});

		it('Render Process details with correct infos', () => {
			const instanceDetailsRows = document.querySelectorAll('p.row');

			expect(instanceDetailsRows.length).toBe(6);
			expect(instanceDetailsRows[0]).toHaveTextContent('completed');
			expect(instanceDetailsRows[1]).toHaveTextContent('Test Test');
			expect(instanceDetailsRows[2]).toHaveTextContent(
				'Jan 20, 2020, 11:08 AM'
			);
			expect(instanceDetailsRows[3]).toHaveTextContent('Blogs Entry');
			expect(instanceDetailsRows[4]).toHaveTextContent('Blog 01');
			expect(instanceDetailsRows[5]).toHaveTextContent(
				'Jan 21, 2020, 10:08 AM'
			);
		});

		it('Render Go to Submission Page button with correct link', () => {
			window.open = jest.fn();
			const submissionPageButton = getByText('go-to-submission-page');

			fireEvent.click(submissionPageButton);

			expect(window.open).toHaveBeenCalledWith(
				'/group/control_panel/manage/-/workflow_instance/view/37634',
				'_blank'
			);
		});
	});

	describe('render with a pending Instance', () => {
		beforeAll(async () => {
			fetch.mockResolvedValueOnce({
				json: () =>
					Promise.resolve({
						...data,
						completed: false,
						slaResults: [
							{...data.slaResults[0]},
							{
								...data.slaResults[1],
								onTime: true,
								status: 'NEW',
							},
						],
						slaStatus: 'Untracked',
						taskNames: ['Review'],
					}),
				ok: true,
			});

			renderComponent();

			await act(async () => {
				jest.runAllTimers();
			});
		});

		it('Render details with slaStatus Untracked', () => {
			const untrackedIcons =
				document.querySelectorAll('.lexicon-icon-hr');
			const slaNotStartedElement =
				renderResult.getByText('NOT-STARTED (1)');
			const slaResultLabelElement = renderResult.getByText('(untracked)');

			expect(untrackedIcons.length).toBe(2);
			expect(slaNotStartedElement).toBeTruthy();
			expect(slaResultLabelElement).toBeTruthy();
		});

		it('Render Process details with correct infos', () => {
			const instanceDetailsRows = document.querySelectorAll('p.row');

			expect(instanceDetailsRows.length).toBe(7);
			expect(instanceDetailsRows[0]).toHaveTextContent('pending');
			expect(instanceDetailsRows[1]).toHaveTextContent('Test Test');
			expect(instanceDetailsRows[2]).toHaveTextContent(
				'Jan 20, 2020, 11:08 AM'
			);
			expect(instanceDetailsRows[3]).toHaveTextContent('Blogs Entry');
			expect(instanceDetailsRows[4]).toHaveTextContent('Blog 01');
			expect(instanceDetailsRows[5]).toHaveTextContent('Review');
			expect(instanceDetailsRows[6]).toHaveTextContent('unassigned');
		});
	});
});
