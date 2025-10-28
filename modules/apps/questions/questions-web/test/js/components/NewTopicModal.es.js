/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {cleanup, fireEvent} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';
import {act} from 'react-dom/test-utils';

import NewTopicModal from '../../../src/main/resources/META-INF/resources/js/components/NewTopicModal.es';
import {renderComponent} from '../../helpers.es';

import '@testing-library/jest-dom';

const mockFetch = {
	data: {
		createSiteMessageBoardSection: {
			id: 32376,
			title: 'New Topic',
		},
	},
};

describe('NewTopicModal', () => {
	afterEach(cleanup);

	beforeAll(() => {
		jest.useFakeTimers();
	});

	it('creates the topic when Enter key is pressed', () => {
		const onCreateNavigateToFn = jest.fn();

		global.fetch.mockImplementationOnce(() =>
			Promise.resolve({
				json: () => Promise.resolve(mockFetch),
				ok: true,
				text: () => Promise.resolve(JSON.stringify(mockFetch)),
			})
		);

		const {getByPlaceholderText} = renderComponent({
			ui: (
				<NewTopicModal
					onCreateNavigateTo={onCreateNavigateToFn}
					visible={true}
				/>
			),
		});

		act(() => {
			jest.runAllTimers();
		});

		const newTopicInput = getByPlaceholderText(
			'please-enter-a-valid-topic-name'
		);

		userEvent.type(newTopicInput, 'New Topic');

		fireEvent.submit(newTopicInput);

		expect(global.fetch).toHaveBeenCalledTimes(1);
	});
});
