/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, fireEvent, render, screen} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';

import CategorizationMessageBalloon from '../../../../src/main/resources/META-INF/resources/js/AIAssistantChat/components/CategorizationMessageBalloon';
import {
	createCategorizationEventSource,
	postCategorizationAgentInstance,
} from '../../../../src/main/resources/META-INF/resources/js/Categorization/api';
import {getCandidateCategories} from '../../../../src/main/resources/META-INF/resources/js/Categorization/services/getCandidateCategories';
import {getExistingTags} from '../../../../src/main/resources/META-INF/resources/js/Categorization/services/getExistingTags';
import {ECategorizationAgent} from '../../../../src/main/resources/META-INF/resources/js/Categorization/types';

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/Categorization/api'
);
jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/Categorization/services/getCandidateCategories'
);
jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/Categorization/services/getExistingTags'
);

const mockCreateEventSource =
	createCategorizationEventSource as jest.MockedFunction<
		typeof createCategorizationEventSource
	>;
const mockPostAgentInstance =
	postCategorizationAgentInstance as jest.MockedFunction<
		typeof postCategorizationAgentInstance
	>;
const mockGetCandidateCategories =
	getCandidateCategories as jest.MockedFunction<
		typeof getCandidateCategories
	>;
const mockGetExistingTags = getExistingTags as jest.MockedFunction<
	typeof getExistingTags
>;

function createFakeEventSource() {
	const listeners: Record<string, (event: {data: string}) => void> = {};

	return {
		addEventListener: jest.fn(
			(type: string, handler: (event: {data: string}) => void) => {
				listeners[type] = handler;
			}
		),
		close: jest.fn(),
		emit(type: string, data: string) {
			listeners[type]?.({data});
		},
	};
}

describe('CategorizationMessageBalloon', () => {
	let mockFire: jest.Mock;

	beforeEach(() => {
		mockCreateEventSource.mockReset();
		mockPostAgentInstance.mockReset();
		mockPostAgentInstance.mockResolvedValue(undefined);
		mockGetCandidateCategories.mockReset();
		mockGetExistingTags.mockReset();

		mockFire = jest.fn();

		global.Liferay = {
			...global.Liferay,
			fire: mockFire,
		} as never;
	});

	it('fetches candidates, renders suggestions, and fires the commit event', async () => {
		const fakeEventSource = createFakeEventSource();

		mockCreateEventSource.mockResolvedValue(fakeEventSource as never);
		mockGetCandidateCategories.mockResolvedValue([
			{id: 39001, name: 'International', vocabulary: 'Travel'},
		]);

		await act(async () => {
			render(
				<CategorizationMessageBalloon
					agent={ECategorizationAgent.AUTO_CATEGORIZE}
					cmsGroupId={20124}
					content="Japan"
					scopeId={555}
				/>
			);
		});

		expect(mockGetCandidateCategories).toHaveBeenCalledWith(
			expect.objectContaining({cmsGroupId: 20124, scopeId: 555})
		);

		await act(async () => {
			fakeEventSource.emit('Subscribe', 'sink-1');
		});

		expect(mockPostAgentInstance).toHaveBeenCalledWith(
			expect.objectContaining({
				agent: 'L_AUTO_CATEGORIZE',
				sseEventSinkKey: 'sink-1',
			})
		);

		await act(async () => {
			fakeEventSource.emit(
				'L_AUTO_CATEGORIZE',
				JSON.stringify({
					data: '{"suggestions":[{"id":39001,"confidence":0.9}]}',
					nodeName: 'llm',
				})
			);
		});

		expect(screen.getByText('International')).toBeInTheDocument();

		fireEvent.click(screen.getByRole('button', {name: 'save-categories'}));

		expect(mockFire).toHaveBeenCalledWith('cms:aiAssistant:commit', {
			agent: 'L_AUTO_CATEGORIZE',
			suggestions: [{id: 39001, name: 'International'}],
		});
	});

	it('uses the tags fetcher for the generate tags agent', async () => {
		const fakeEventSource = createFakeEventSource();

		mockCreateEventSource.mockResolvedValue(fakeEventSource as never);
		mockGetExistingTags.mockResolvedValue(['Japan']);

		await act(async () => {
			render(
				<CategorizationMessageBalloon
					agent={ECategorizationAgent.GENERATE_TAGS}
					cmsGroupId={20124}
					content="Japan"
					scopeId={555}
				/>
			);
		});

		expect(mockGetExistingTags).toHaveBeenCalledWith(
			expect.objectContaining({cmsGroupId: 20124, scopeId: 555})
		);

		await act(async () => {
			fakeEventSource.emit('Subscribe', 'sink-2');
		});

		expect(mockPostAgentInstance).toHaveBeenCalledWith(
			expect.objectContaining({
				agent: 'L_GENERATE_TAGS',
				sseEventSinkKey: 'sink-2',
			})
		);
	});
});
