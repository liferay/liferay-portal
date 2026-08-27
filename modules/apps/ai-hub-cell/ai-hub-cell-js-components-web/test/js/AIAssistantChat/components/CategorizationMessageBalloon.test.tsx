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
import {CATEGORIZE_EVENT} from '../../../../src/main/resources/META-INF/resources/js/Categorization/events';
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

function fireCategorizeEvent(payload: {agent: string}) {
	const detachedHandlers = (Liferay.detach as jest.Mock).mock.calls.map(
		([, handler]) => handler
	);

	(Liferay.on as jest.Mock).mock.calls
		.filter(
			([name, handler]) =>
				name === CATEGORIZE_EVENT && !detachedHandlers.includes(handler)
		)
		.forEach(([, handler]) => handler(payload));
}

describe('CategorizationMessageBalloon', () => {
	let mockFire: jest.Mock;
	let setBalloonGenerating: jest.Mock;

	beforeEach(() => {
		mockCreateEventSource.mockReset();
		mockPostAgentInstance.mockReset();
		mockPostAgentInstance.mockResolvedValue(undefined);
		mockGetCandidateCategories.mockReset();
		mockGetExistingTags.mockReset();

		mockFire = jest.fn();
		setBalloonGenerating = jest.fn();

		(Liferay.on as jest.Mock).mockClear();
		(Liferay.detach as jest.Mock).mockClear();

		global.Liferay = {
			...global.Liferay,
			fire: mockFire,
		} as never;
	});

	afterEach(() => {
		(Liferay.Language.get as jest.Mock).mockImplementation(
			(key: string) => key
		);
	});

	it('reports loading through the shared state instead of its own indicator', async () => {
		const fakeEventSource = createFakeEventSource();

		mockCreateEventSource.mockResolvedValue(fakeEventSource as never);
		mockGetExistingTags.mockResolvedValue([]);

		await act(async () => {
			render(
				<CategorizationMessageBalloon
					agent={ECategorizationAgent.GENERATE_TAGS}
					cmsGroupId={20124}
					content="Japan"
					scopeId={555}
					setBalloonGenerating={setBalloonGenerating}
				/>
			);
		});

		await act(async () => {
			fakeEventSource.emit('Subscribe', 'sink-7');
		});

		expect(screen.queryByText('generating-tags')).not.toBeInTheDocument();
		expect(setBalloonGenerating).toHaveBeenLastCalledWith(
			expect.any(String),
			true
		);

		await act(async () => {
			fakeEventSource.emit(
				'L_GENERATE_TAGS',
				JSON.stringify({
					data: '{"suggestions":[{"name":"Culture","isNew":true}]}',
					nodeName: 'llm',
				})
			);
		});

		expect(screen.getByText('Culture')).toBeInTheDocument();
		expect(setBalloonGenerating).toHaveBeenLastCalledWith(
			expect.any(String),
			false
		);
	});

	it('clears the shared state when it unmounts while loading', async () => {
		const fakeEventSource = createFakeEventSource();

		mockCreateEventSource.mockResolvedValue(fakeEventSource as never);
		mockGetExistingTags.mockResolvedValue([]);

		let unmount!: () => void;

		await act(async () => {
			unmount = render(
				<CategorizationMessageBalloon
					agent={ECategorizationAgent.GENERATE_TAGS}
					cmsGroupId={20124}
					content="Japan"
					scopeId={555}
					setBalloonGenerating={setBalloonGenerating}
				/>
			).unmount;
		});

		await act(async () => {
			fakeEventSource.emit('Subscribe', 'sink-9');
		});

		expect(setBalloonGenerating).toHaveBeenLastCalledWith(
			expect.any(String),
			true
		);

		act(() => {
			unmount();
		});

		expect(setBalloonGenerating).toHaveBeenLastCalledWith(
			expect.any(String),
			false
		);
	});

	it('reports a replaced request when a new call to the same agent opens', async () => {
		const fakeEventSource = createFakeEventSource();

		mockCreateEventSource.mockResolvedValue(fakeEventSource as never);
		mockGetExistingTags.mockResolvedValue([]);

		await act(async () => {
			render(
				<CategorizationMessageBalloon
					agent={ECategorizationAgent.GENERATE_TAGS}
					cmsGroupId={20124}
					content="Japan"
					scopeId={555}
					setBalloonGenerating={setBalloonGenerating}
				/>
			);
		});

		await act(async () => {
			fakeEventSource.emit('Subscribe', 'sink-11');
		});

		await act(async () => {
			fireCategorizeEvent({agent: 'L_GENERATE_TAGS'});
		});

		expect(
			screen.getByText('this-request-was-replaced-by-a-newer-one')
		).toBeInTheDocument();
		expect(setBalloonGenerating).toHaveBeenLastCalledWith(
			expect.any(String),
			false
		);
		expect(fakeEventSource.close).toHaveBeenCalled();
	});

	it('keeps a loading balloon when the new call is for the other agent', async () => {
		const fakeEventSource = createFakeEventSource();

		mockCreateEventSource.mockResolvedValue(fakeEventSource as never);
		mockGetExistingTags.mockResolvedValue([]);

		await act(async () => {
			render(
				<CategorizationMessageBalloon
					agent={ECategorizationAgent.GENERATE_TAGS}
					cmsGroupId={20124}
					content="Japan"
					scopeId={555}
					setBalloonGenerating={setBalloonGenerating}
				/>
			);
		});

		await act(async () => {
			fakeEventSource.emit('Subscribe', 'sink-13');
		});

		await act(async () => {
			fireCategorizeEvent({agent: 'L_AUTO_CATEGORIZE'});
		});

		expect(setBalloonGenerating).toHaveBeenLastCalledWith(
			expect.any(String),
			true
		);
		expect(fakeEventSource.close).not.toHaveBeenCalled();
	});

	it('keeps the suggestions of a balloon that already answered', async () => {
		const fakeEventSource = createFakeEventSource();

		mockCreateEventSource.mockResolvedValue(fakeEventSource as never);
		mockGetExistingTags.mockResolvedValue([]);

		await act(async () => {
			render(
				<CategorizationMessageBalloon
					agent={ECategorizationAgent.GENERATE_TAGS}
					cmsGroupId={20124}
					content="Japan"
					scopeId={555}
					setBalloonGenerating={setBalloonGenerating}
				/>
			);
		});

		await act(async () => {
			fakeEventSource.emit('Subscribe', 'sink-12');
		});

		await act(async () => {
			fakeEventSource.emit(
				'L_GENERATE_TAGS',
				JSON.stringify({
					data: '{"suggestions":[{"name":"Culture","isNew":true}]}',
					nodeName: 'llm',
				})
			);
		});

		await act(async () => {
			fireCategorizeEvent({agent: 'L_GENERATE_TAGS'});
		});

		expect(screen.getByText('Culture')).toBeInTheDocument();
	});

	it('reports a replaced request while it is regenerating', async () => {
		const fakeEventSource = createFakeEventSource();

		mockCreateEventSource.mockResolvedValue(fakeEventSource as never);
		mockGetExistingTags.mockResolvedValue([]);

		await act(async () => {
			render(
				<CategorizationMessageBalloon
					agent={ECategorizationAgent.GENERATE_TAGS}
					cmsGroupId={20124}
					content="Japan"
					scopeId={555}
					setBalloonGenerating={setBalloonGenerating}
				/>
			);
		});

		await act(async () => {
			fakeEventSource.emit('Subscribe', 'sink-14');
		});

		await act(async () => {
			fakeEventSource.emit(
				'L_GENERATE_TAGS',
				JSON.stringify({
					data: '{"suggestions":[{"name":"Culture","isNew":true}]}',
					nodeName: 'llm',
				})
			);
		});

		await act(async () => {
			fireEvent.click(screen.getByRole('button', {name: 'try-again'}));
		});

		await act(async () => {
			fireCategorizeEvent({agent: 'L_GENERATE_TAGS'});
		});

		expect(
			screen.getByText('this-request-was-replaced-by-a-newer-one')
		).toBeInTheDocument();
	});

	it('counts only the tags not already on the content in the confirmation', async () => {
		(Liferay.Language.get as jest.Mock).mockImplementation((key: string) =>
			key === 'great-i-have-added-x-tags-to-your-content'
				? 'Great! I have added {0} tags to your content.'
				: key
		);

		const fakeEventSource = createFakeEventSource();

		mockCreateEventSource.mockResolvedValue(fakeEventSource as never);
		mockGetExistingTags.mockResolvedValue(['Japan']);

		await act(async () => {
			render(
				<CategorizationMessageBalloon
					agent={ECategorizationAgent.GENERATE_TAGS}
					cmsGroupId={20124}
					content="Japan"
					currentTagNames={['Japan']}
					scopeId={555}
					setBalloonGenerating={setBalloonGenerating}
				/>
			);
		});

		await act(async () => {
			fakeEventSource.emit('Subscribe', 'sink-3');
		});

		await act(async () => {
			fakeEventSource.emit(
				'L_GENERATE_TAGS',
				JSON.stringify({
					data: '{"suggestions":[{"name":"Japan","isNew":false},{"name":"Culture","isNew":true},{"name":"Tradition","isNew":true}]}',
					nodeName: 'llm',
				})
			);
		});

		fireEvent.click(screen.getByRole('button', {name: 'add-tags'}));

		expect(
			screen.getByText(/Great! I have added 2 tags/)
		).toBeInTheDocument();
		expect(screen.queryByText(/added 3 tags/)).not.toBeInTheDocument();
	});

	it('does not lock the chat when the channel is unavailable', async () => {
		mockCreateEventSource.mockResolvedValue(null);
		mockGetExistingTags.mockResolvedValue([]);

		await act(async () => {
			render(
				<CategorizationMessageBalloon
					agent={ECategorizationAgent.GENERATE_TAGS}
					cmsGroupId={20124}
					content="Japan"
					scopeId={555}
					setBalloonGenerating={setBalloonGenerating}
				/>
			);
		});

		expect(
			screen.getByText('an-unexpected-error-occurred')
		).toBeInTheDocument();
		expect(setBalloonGenerating).toHaveBeenLastCalledWith(
			expect.any(String),
			false
		);
	});

	it('does not show a confirmation when no new categories are added', async () => {
		(Liferay.Language.get as jest.Mock).mockImplementation((key: string) =>
			key === 'great-i-have-added-x-categories-to-your-content'
				? 'Great! I have added {0} categories to your content.'
				: key
		);

		const fakeEventSource = createFakeEventSource();

		mockCreateEventSource.mockResolvedValue(fakeEventSource as never);
		mockGetCandidateCategories.mockResolvedValue([
			{id: 39001, name: 'International', vocabulary: 'Travel'},
			{id: 39002, name: 'Roadtrip', vocabulary: 'Travel'},
		]);

		await act(async () => {
			render(
				<CategorizationMessageBalloon
					agent={ECategorizationAgent.AUTO_CATEGORIZE}
					cmsGroupId={20124}
					content="Japan"
					currentCategoryIds={[39001, 39002]}
					scopeId={555}
					setBalloonGenerating={setBalloonGenerating}
				/>
			);
		});

		await act(async () => {
			fakeEventSource.emit('Subscribe', 'sink-5');
		});

		await act(async () => {
			fakeEventSource.emit(
				'L_AUTO_CATEGORIZE',
				JSON.stringify({
					data: '{"suggestions":[{"id":39001,"confidence":0.9},{"id":39002,"confidence":0.8}]}',
					nodeName: 'llm',
				})
			);
		});

		fireEvent.click(screen.getByRole('button', {name: 'add-categories'}));

		expect(
			screen.queryByText(/Great! I have added/)
		).not.toBeInTheDocument();
	});

	it('excludes already-attached categories from the confirmation count', async () => {
		(Liferay.Language.get as jest.Mock).mockImplementation((key: string) =>
			key === 'great-i-have-added-x-categories-to-your-content'
				? 'Great! I have added {0} categories to your content.'
				: key
		);

		const fakeEventSource = createFakeEventSource();

		mockCreateEventSource.mockResolvedValue(fakeEventSource as never);
		mockGetCandidateCategories.mockResolvedValue([
			{id: 39001, name: 'International', vocabulary: 'Travel'},
			{id: 39002, name: 'Roadtrip', vocabulary: 'Travel'},
		]);

		await act(async () => {
			render(
				<CategorizationMessageBalloon
					agent={ECategorizationAgent.AUTO_CATEGORIZE}
					cmsGroupId={20124}
					content="Japan"
					currentCategoryIds={[39001]}
					scopeId={555}
					setBalloonGenerating={setBalloonGenerating}
				/>
			);
		});

		await act(async () => {
			fakeEventSource.emit('Subscribe', 'sink-4');
		});

		await act(async () => {
			fakeEventSource.emit(
				'L_AUTO_CATEGORIZE',
				JSON.stringify({
					data: '{"suggestions":[{"id":39001,"confidence":0.9},{"id":39002,"confidence":0.8}]}',
					nodeName: 'llm',
				})
			);
		});

		fireEvent.click(screen.getByRole('button', {name: 'add-categories'}));

		expect(
			screen.getByText(/Great! I have added 1 categories/)
		).toBeInTheDocument();
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
					setBalloonGenerating={setBalloonGenerating}
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

		fireEvent.click(screen.getByRole('button', {name: 'add-categories'}));

		expect(mockFire).toHaveBeenCalledWith(
			'cms:aiAssistant:commit',
			expect.objectContaining({
				agent: 'L_AUTO_CATEGORIZE',
				scopeId: 555,
				suggestions: [{id: 39001, name: 'International'}],
			})
		);
	});

	it('ignores case when counting new tags in the confirmation', async () => {
		(Liferay.Language.get as jest.Mock).mockImplementation((key: string) =>
			key === 'great-i-have-added-x-tags-to-your-content'
				? 'Great! I have added {0} tags to your content.'
				: key
		);

		const fakeEventSource = createFakeEventSource();

		mockCreateEventSource.mockResolvedValue(fakeEventSource as never);
		mockGetExistingTags.mockResolvedValue(['japan']);

		await act(async () => {
			render(
				<CategorizationMessageBalloon
					agent={ECategorizationAgent.GENERATE_TAGS}
					cmsGroupId={20124}
					content="Japan"
					currentTagNames={['japan']}
					scopeId={555}
					setBalloonGenerating={setBalloonGenerating}
				/>
			);
		});

		await act(async () => {
			fakeEventSource.emit('Subscribe', 'sink-6');
		});

		await act(async () => {
			fakeEventSource.emit(
				'L_GENERATE_TAGS',
				JSON.stringify({
					data: '{"suggestions":[{"name":"Japan","isNew":false},{"name":"Culture","isNew":true}]}',
					nodeName: 'llm',
				})
			);
		});

		fireEvent.click(screen.getByRole('button', {name: 'add-tags'}));

		expect(
			screen.getByText(/Great! I have added 1 tags/)
		).toBeInTheDocument();
	});

	it('keeps the balloon with its own indicator while regenerating', async () => {
		const fakeEventSource = createFakeEventSource();

		mockCreateEventSource.mockResolvedValue(fakeEventSource as never);
		mockGetExistingTags.mockResolvedValue([]);

		await act(async () => {
			render(
				<CategorizationMessageBalloon
					agent={ECategorizationAgent.GENERATE_TAGS}
					cmsGroupId={20124}
					content="Japan"
					scopeId={555}
					setBalloonGenerating={setBalloonGenerating}
				/>
			);
		});

		await act(async () => {
			fakeEventSource.emit('Subscribe', 'sink-8');
		});

		await act(async () => {
			fakeEventSource.emit(
				'L_GENERATE_TAGS',
				JSON.stringify({
					data: '{"suggestions":[{"name":"Culture","isNew":true}]}',
					nodeName: 'llm',
				})
			);
		});

		setBalloonGenerating.mockClear();

		await act(async () => {
			fireEvent.click(screen.getByRole('button', {name: 'try-again'}));
		});

		expect(screen.getByText('generating-tags')).toBeInTheDocument();
		expect(setBalloonGenerating).not.toHaveBeenCalledWith(
			expect.any(String),
			true
		);
	});

	it('leaves the shared state alone when it unmounts after loading', async () => {
		const fakeEventSource = createFakeEventSource();

		mockCreateEventSource.mockResolvedValue(fakeEventSource as never);
		mockGetExistingTags.mockResolvedValue([]);

		let unmount!: () => void;

		await act(async () => {
			unmount = render(
				<CategorizationMessageBalloon
					agent={ECategorizationAgent.GENERATE_TAGS}
					cmsGroupId={20124}
					content="Japan"
					scopeId={555}
					setBalloonGenerating={setBalloonGenerating}
				/>
			).unmount;
		});

		await act(async () => {
			fakeEventSource.emit('Subscribe', 'sink-10');
		});

		await act(async () => {
			fakeEventSource.emit(
				'L_GENERATE_TAGS',
				JSON.stringify({
					data: '{"suggestions":[{"name":"Culture","isNew":true}]}',
					nodeName: 'llm',
				})
			);
		});

		setBalloonGenerating.mockClear();

		act(() => {
			unmount();
		});

		expect(setBalloonGenerating).not.toHaveBeenCalled();
	});

	it('marks unknown tag targets as new and known ones as existing', async () => {
		mockGetExistingTags.mockResolvedValue(['japan']);

		await act(async () => {
			render(
				<CategorizationMessageBalloon
					agent={ECategorizationAgent.GENERATE_TAGS}
					cmsGroupId={20124}
					content="Japan"
					scopeId={555}
					setBalloonGenerating={setBalloonGenerating}
					targets={['kayaking', 'Japan']}
				/>
			);
		});

		expect(screen.getByText('kayaking')).toBeInTheDocument();
		expect(screen.getByText('japan')).toBeInTheDocument();
		expect(screen.queryByText('Japan')).not.toBeInTheDocument();
		expect(
			screen.getByText('i-found-x-existing-tags-and-suggest-x-new-tags')
		).toBeInTheDocument();
		expect(mockCreateEventSource).not.toHaveBeenCalled();
	});

	it('resolves a named category target without invoking the model', async () => {
		mockGetCandidateCategories.mockResolvedValue([
			{id: 39001, name: 'Fishing', vocabulary: 'Topic'},
			{id: 39002, name: 'Travel', vocabulary: 'Topic'},
		]);

		await act(async () => {
			render(
				<CategorizationMessageBalloon
					agent={ECategorizationAgent.AUTO_CATEGORIZE}
					cmsGroupId={20124}
					content="Japan"
					scopeId={555}
					setBalloonGenerating={setBalloonGenerating}
					targets={['fishing']}
				/>
			);
		});

		expect(screen.getByText('Fishing')).toBeInTheDocument();
		expect(
			screen.getByRole('button', {name: 'add-categories'})
		).toBeInTheDocument();
		expect(mockCreateEventSource).not.toHaveBeenCalled();
	});

	it('shows the empty state when a category target does not match', async () => {
		mockGetCandidateCategories.mockResolvedValue([
			{id: 39002, name: 'Travel', vocabulary: 'Topic'},
		]);

		await act(async () => {
			render(
				<CategorizationMessageBalloon
					agent={ECategorizationAgent.AUTO_CATEGORIZE}
					cmsGroupId={20124}
					content="Japan"
					scopeId={555}
					setBalloonGenerating={setBalloonGenerating}
					targets={['Fishing']}
				/>
			);
		});

		expect(
			screen.getByText(
				'i-have-not-found-any-matching-categories-what-would-you-like-to-do'
			)
		).toBeInTheDocument();
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
					setBalloonGenerating={setBalloonGenerating}
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
