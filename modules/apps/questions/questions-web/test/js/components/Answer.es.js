/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import Answer from '../../../src/main/resources/META-INF/resources/js/components/Answer.es';

import '@testing-library/jest-dom';
import {waitForElementToBeRemoved} from '@testing-library/dom';
import {cleanup} from '@testing-library/react';

import {unMarkAsAnswerMessageBoardMessageQuery} from '../../../src/main/resources/META-INF/resources/js/utils/client.es';
import {renderComponent} from '../../helpers.es';

const mockAnswer = {
	actions: {
		'delete': {
			operation: 'deleteMessageBoardMessage',
			type: 'mutation',
		},
		'get': {
			operation: 'messageBoardMessage',
			type: 'query',
		},
		'replace': {
			operation: 'updateMessageBoardMessage',
			type: 'mutation',
		},
		'reply-to-message': {
			operation: 'createMessageBoardMessageMessageBoardMessage',
			type: 'mutation',
		},
		'subscribe': {
			operation: 'updateMessageBoardMessageSubscribe',
			type: 'mutation',
		},
		'unsubscribe': {
			operation: 'updateMessageBoardMessageSubscribe',
			type: 'mutation',
		},
		'update': {
			operation: 'patchMessageBoardMessage',
			type: 'mutation',
		},
	},
	aggregateRating: null,
	articleBody: '<p>my awesome answer</p>',
	creator: {
		id: 20126,
		image: null,
		name: 'Test Test',
	},
	creatorStatistics: {
		joinDate: '2020-07-30T09:44:49Z',
		lastPostDate: '2020-07-31T09:27:31Z',
		postsNumber: 12,
		rank: 'Youngling',
	},
	dateCreated: '2020-07-30T09:44:49Z',
	encodingFormat: 'html',
	friendlyUrlPath: 're-new-question',
	id: 36801,
	messageBoardMessages: {
		items: [],
	},
	myRating: null,
	showAsAnswer: true,
};

const apolloMocks = [
	{
		request: {
			query: unMarkAsAnswerMessageBoardMessageQuery,
			variables: {
				messageBoardMessageId: mockAnswer.id,
			},
		},
		result: {
			data: {
				patchMessageBoardMessage: {
					id: 36801,
					showAsAnswer: false,
				},
			},
		},
	},
];

describe('Answer', () => {
	afterEach(cleanup);

	it('Show as a valid answer in the case that it is', async () => {
		const mockIsSignedIn = jest.fn();
		window.Liferay.ThemeDisplay.isSignedIn = mockIsSignedIn;

		global.fetch.mockImplementationOnce(() =>
			Promise.resolve({
				json: () => Promise.resolve(apolloMocks),
				ok: true,
				text: () => Promise.resolve(JSON.stringify(apolloMocks)),
			})
		);

		const {getByTestId} = renderComponent({
			fetch,
			ui: (
				<Answer
					answer={mockAnswer}
					canMarkAsAnswer={true}
					key={mockAnswer.id}
				/>
			),
		});

		let markAsAnswerButton = getByTestId('mark-as-answer-button');
		expect(markAsAnswerButton.textContent).toMatch('unmark-as-answer');

		let markAsAnswerStyle = getByTestId('mark-as-answer-style');
		expect(
			markAsAnswerStyle.className.includes('questions-answer-success')
		).toBeTruthy();

		const markAsAnswerCheck = getByTestId('mark-as-answer-check');
		expect(markAsAnswerCheck).toBeInTheDocument();
		expect(
			markAsAnswerCheck.querySelectorAll(
				'.lexicon-icon-check-circle-full'
			).length
		).toBe(1);

		markAsAnswerButton.click();

		await waitForElementToBeRemoved(() =>
			getByTestId('mark-as-answer-check')
		);

		markAsAnswerButton = getByTestId('mark-as-answer-button');
		expect(markAsAnswerButton.textContent).toMatch('mark-as-answer');

		markAsAnswerStyle = getByTestId('mark-as-answer-style');
		expect(
			markAsAnswerStyle.className.includes('questions-answer-success')
		).toBeFalsy();
	});
});
