/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import Questions from '../../../../src/main/resources/META-INF/resources/js/pages/questions/Questions.es';

import '@testing-library/jest-dom';
import {act, cleanup} from '@testing-library/react';
import {Route} from 'react-router-dom';

import {renderComponent} from '../../../helpers.es';

const mockKeywords = {
	data: {
		keywords: {
			items: [
				{id: 1, name: 'React'},
				{id: 2, name: 'Liferay'},
			],
		},
	},
};

const mockMessageBoardSections = {
	data: {
		messageBoardSections: {
			items: [
				{
					actions: {
						'add-subcategory': {
							operation:
								'createMessageBoardSectionMessageBoardSection',
							type: 'mutation',
						},
						'add-thread': {
							operation:
								'createMessageBoardSectionMessageBoardThread',
							type: 'mutation',
						},
						'delete': {
							operation: 'deleteMessageBoardSection',
							type: 'mutation',
						},
						'get': {
							operation: 'messageBoardSection',
							type: 'query',
						},
						'replace': {
							operation: 'updateMessageBoardSection',
							type: 'mutation',
						},
						'subscribe': {
							operation: 'updateMessageBoardSectionSubscribe',
							type: 'mutation',
						},
						'unsubscribe': {
							operation: 'updateMessageBoardSectionUnsubscribe',
							type: 'mutation',
						},
					},
					id: 37201,
					messageBoardSections: {
						items: [
							{
								id: 37203,
								numberOfMessageBoardSections: 1,
								parentMessageBoardSectionId: 37201,
								subscribed: false,
								title: 'One',
							},
							{
								id: 37207,
								numberOfMessageBoardSections: 0,
								parentMessageBoardSectionId: 37201,
								subscribed: false,
								title: 'One copy',
							},
						],
					},
					numberOfMessageBoardSections: 2,
					parentMessageBoardSectionId: null,
					subscribed: false,
					title: 'Root',
				},
			],
		},
	},
};

const mockThreads = {
	data: {
		messageBoardSectionMessageBoardThreads: {
			items: [
				{
					aggregateRating: null,
					articleBody: '<p>This is the test question 1</p>',
					creator: {
						id: 20126,
						image: null,
						name: 'Test Test',
					},
					dateModified: '2020-06-19T20:51:51Z',
					friendlyUrlPath: 'test-question-1',
					hasValidAnswer: false,
					headline: 'Test Question 1',
					id: 36804,
					keywords: [],
					messageBoardSection: {
						numberOfMessageBoardSections: 0,
						title: 'Portal',
					},
					numberOfMessageBoardMessages: 0,
					seen: false,
					viewCount: 0,
				},
				{
					aggregateRating: null,
					articleBody: '<p>This is the test question 2</p>',
					creator: {
						id: 20127,
						image: null,
						name: 'John Doe',
					},
					dateModified: '2020-06-20T20:00:00Z',
					friendlyUrlPath: 'test-question-2',
					hasValidAnswer: false,
					headline: 'Test Question 2',
					id: 36805,
					keywords: [],
					messageBoardSection: {
						numberOfMessageBoardSections: 0,
						title: 'Portal',
					},
					numberOfMessageBoardMessages: 0,
					seen: false,
					viewCount: 0,
				},
			],
			page: 1,
			pageSize: 20,
			totalCount: 2,
		},
	},
};

describe.skip('Questions', () => {
	beforeEach(() => {
		jest.useFakeTimers();
	});

	afterEach(() => {
		cleanup();
		jest.clearAllTimers();
		jest.restoreAllMocks();
	});

	afterAll(() => {
		jest.useRealTimers();
	});

	it('questions shows loading animation', async () => {
		const path = '/questions/:sectionTitle';
		const route = '/questions/portal';

		global.fetch
			.mockImplementationOnce(() =>
				Promise.resolve({
					json: () => Promise.resolve(mockKeywords),
					ok: true,
				})
			)
			.mockImplementationOnce(() =>
				Promise.resolve({
					json: () => Promise.resolve(mockMessageBoardSections),
					ok: true,
					text: () =>
						Promise.resolve(
							JSON.stringify(mockMessageBoardSections)
						),
				})
			)
			.mockImplementation(() =>
				Promise.resolve({
					json: () => Promise.resolve(mockThreads),
					ok: true,
					text: () => Promise.resolve(JSON.stringify(mockThreads)),
				})
			);

		const {container, findByText} = renderComponent({
			contextValue: {
				questionsVisited: [],
				sections: [],
				siteKey: '20020',
			},
			fetch,
			route,
			ui: <Route component={Questions} path={path} />,
		});

		const loading = container.querySelectorAll('.loading-animation');
		expect(loading.length).toBe(1);

		await act(async () => {
			jest.runAllTimers();
		});

		await findByText('Test Question 1');

		expect(container.querySelector('.loading-animation')).toBe(null);

		const questionHeadline1 = await findByText('Test Question 1');
		const questionBody1 = await findByText('This is the test question 1');
		const questionHeadline2 = await findByText('Test Question 2');
		const questionBody2 = await findByText('This is the test question 2');

		expect(questionHeadline1).toBeInTheDocument();
		expect(questionBody1).toBeInTheDocument();
		expect(questionHeadline2).toBeInTheDocument();
		expect(questionBody2).toBeInTheDocument();
	});
});
