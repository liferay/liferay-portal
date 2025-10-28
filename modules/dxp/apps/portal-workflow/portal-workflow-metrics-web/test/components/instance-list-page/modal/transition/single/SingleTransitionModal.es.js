/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, fireEvent, render} from '@testing-library/react';
import React, {useState} from 'react';

import {InstanceListContext} from '../../../../../../src/main/resources/META-INF/resources/js/components/instance-list-page/InstanceListPageProvider.es';
import {ModalContext} from '../../../../../../src/main/resources/META-INF/resources/js/components/instance-list-page/modal/ModalProvider.es';
import SingleTransitionModal from '../../../../../../src/main/resources/META-INF/resources/js/components/instance-list-page/modal/transition/single/SingleTransitionModal.es';
import ToasterProvider from '../../../../../../src/main/resources/META-INF/resources/js/shared/components/toaster/ToasterProvider.es';
import {MockRouter} from '../../../../../mock/MockRouter.es';
import FetchMock, {fetchMockResponse} from '../../../../../mock/fetch.es';

import '@testing-library/jest-dom';

const ContainerMock = ({children}) => {
	const selectedInstance = {
		assetTitle: 'Blog1',
		assetType: 'Blogs Entry',
		assignees: [{id: 2, name: 'Test Test'}],
		id: 1,
		status: 'In Progress',
		taskNames: ['Review'],
	};
	const [singleTransition, setSingleTransition] = useState({
		title: 'Test',
		transitionName: 'test',
	});

	return (
		<InstanceListContext.Provider
			value={{
				selectedInstance,
			}}
		>
			<ModalContext.Provider
				value={{
					setSingleTransition,
					singleTransition,
					visibleModal: 'singleTransition',
				}}
			>
				<ToasterProvider>{children}</ToasterProvider>
			</ModalContext.Provider>
		</InstanceListContext.Provider>
	);
};

const items = [
	{
		id: 2,
		label: 'Testing',
		name: 'testing',
	},
];

const fetchMock = new FetchMock({
	GET: {
		default: fetchMockResponse({
			items,
			totalCount: 1,
		}),
	},
	POST: {
		default: [
			fetchMockResponse({title: 'Request failed'}, false),
			fetchMockResponse({items: []}),
		],
	},
});

describe('The SingleTransitionModal component should', () => {
	let getByPlaceholderText;
	let getByText;

	beforeAll(async () => {
		const renderResult = render(
			<MockRouter>
				<SingleTransitionModal />
			</MockRouter>,
			{
				wrapper: ContainerMock,
			}
		);

		getByPlaceholderText = renderResult.getByPlaceholderText;
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

	it('Be rendered when its attribute visible is "true"', () => {
		const transitionModal = getByText('Test');
		expect(transitionModal).toBeInTheDocument();
	});

	it('Change comment field value, click in "Done" button', async () => {
		const commentField = getByPlaceholderText('comment');
		const doneButton = getByText('done');

		fireEvent.change(commentField, {target: {value: 'Comment field test'}});

		expect(commentField).toHaveValue('Comment field test');

		fireEvent.click(doneButton);

		await act(async () => {
			jest.runAllTimers();
		});
	});

	it('Show error alert after failing request and click in "Done" to retry request', async () => {
		const alertError = getByText(
			'your-request-has-failed select-done-to-retry'
		);
		const doneButton = getByText('done');

		expect(alertError).toBeTruthy();

		fireEvent.click(doneButton);

		await act(async () => {
			jest.runAllTimers();
		});
	});

	it('Show success alert message after post request success', () => {
		const alertToast = document.querySelector('.alert-dismissible');

		expect(alertToast).toHaveTextContent(
			'the-selected-step-has-transitioned-successfully'
		);
	});
});
