/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {act, render, screen, waitFor, within} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import CategorizationSuggestionService from '../../../../src/main/resources/META-INF/resources/js/common/services/CategorizationSuggestionService';
import ContentEditorSidePanel from '../../../../src/main/resources/META-INF/resources/js/content_editor/components/ContentEditorSidePanel';
import ObjectEntryService from '../../../../src/main/resources/META-INF/resources/js/main_view/info_panel/services/ObjectEntryService';
import {mockFetch} from '../../__mocks__/frontend-js-web';

const EXPIRATION_DATE = '2025-08-14T00:01';
const REVIEW_DATE = '2025-08-15T00:01';

jest.mock('frontend-js-web', () => ({
	...(jest.requireActual('frontend-js-web') as object),
	dateUtils: {
		getFirstDayOfWeek: jest.fn(),
		getMonthsLong: jest.fn().mockReturnValue([]),
		getWeekdaysShort: jest.fn().mockReturnValue([]),
	},
}));
jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/main_view/info_panel/services/ObjectEntryService'
);
jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/common/services/CategorizationSuggestionService'
);

const renderComponent = ({cmpEnabled = true, isSubscribed = false} = {}) => {
	return render(
		<ContentEditorSidePanel
			addCommentURL="addCommentURL"
			assetLibraryId="123"
			assetType={30982}
			cmpEnabled={cmpEnabled}
			cmsGroupId="21000"
			comments={[]}
			contentAPIURL="contentAPIURL"
			deleteCommentURL="deleteCommentURL"
			editCommentURL="editCommentURL"
			editorConfig={{}}
			entryClassName=""
			expirationDate={EXPIRATION_DATE}
			getCommentsURL="getCommentsURL"
			hasUpdatePermission={true}
			id="contentId"
			isSubscribed={isSubscribed}
			reviewDate={REVIEW_DATE}
			subscribeURL="subscribeURL"
			type="Content Type"
			version="Version 1"
		/>
	);
};

describe('ContentEditorSidePanel', () => {
	beforeEach(() => {
		global.Liferay.ThemeDisplay.getTimeZone = jest
			.fn()
			.mockReturnValue('utc');
		global.Liferay.ThemeDisplay.getSiteGroupId = jest
			.fn()
			.mockReturnValue('21000');

		(ObjectEntryService.getObjectEntry as jest.Mock).mockResolvedValue({
			data: {
				contentRawText: 'Japan',
				keywords: [],
				scopeId: 555,
				systemProperties: {
					objectDefinitionBrief: {classNameId: 30982},
				},
				taxonomyCategoryBriefs: [],
			},
			error: null,
		});
	});

	afterEach(() => {
		(global as any).Liferay.on = () => {};
		(global as any).Liferay.fire = () => {};
		(global as any).Liferay.detach = () => {};
	});

	it('calls the subscribe request', async () => {
		renderComponent();

		await userEvent.click(screen.getByLabelText('comments'));

		await waitFor(() => {
			expect(screen.getByText('comments')).toBeInTheDocument();
		});

		const subscribeButton = screen.getByLabelText('subscribe');

		expect(subscribeButton).toBeEnabled();

		const clickPromise = userEvent.click(subscribeButton);

		await waitFor(() => {
			expect(subscribeButton).toBeDisabled();
		});

		await clickPromise;

		await waitFor(() => {
			expect(subscribeButton).toBeEnabled();
		});

		expect(mockFetch).toBeCalledWith('subscribeURL', {
			body: {
				cmd: 'subscribe',
			},
			method: 'POST',
		});

		await waitFor(() => {
			expect(
				screen.getByText('you-have-successfully-subscribed-to-comments')
			).toBeInTheDocument();
		});
	});

	it('calls the unsubscribe request', async () => {
		renderComponent({isSubscribed: true});

		await userEvent.click(screen.getByLabelText('comments'));

		await waitFor(() => {
			expect(screen.getByText('comments')).toBeInTheDocument();
		});

		const unsubscribeButton = screen.getByLabelText('unsubscribe');

		expect(unsubscribeButton).toBeEnabled();

		const clickPromise = userEvent.click(unsubscribeButton);

		await waitFor(() => {
			expect(unsubscribeButton).toBeDisabled();
		});

		await clickPromise;

		await waitFor(() => {
			expect(unsubscribeButton).toBeEnabled();
		});

		expect(mockFetch).toBeCalledWith('subscribeURL', {
			body: {
				cmd: 'unsubscribe',
			},
			method: 'POST',
		});

		await waitFor(() => {
			expect(
				screen.getByText(
					'you-have-successfully-unsubscribed-from-comments'
				)
			).toBeInTheDocument();
		});
	});

	it('closes the panel pressing the Close button', async () => {
		const {container} = renderComponent();

		const panelButton = screen.getByLabelText('general');

		await userEvent.click(panelButton);

		await waitFor(() => {
			expect(screen.getByText('general')).toBeInTheDocument();
		});

		await userEvent.click(within(container).getByLabelText('close'));

		await waitFor(() => {
			expect(screen.queryByText('general')).not.toBeInTheDocument();
			expect(panelButton).toHaveFocus();
		});
	});

	it('fetches the entry once and dispatches a categorize event per action in order', async () => {
		const fireCalls: Array<{name: string; payload: any}> = [];
		const handlers: Record<string, (payload: any) => void> = {};

		(global as any).Liferay.on = jest.fn(
			(name: string, callback: (payload: any) => void) => {
				handlers[name] = callback;
			}
		);
		(global as any).Liferay.fire = jest.fn((name: string, payload: any) => {
			fireCalls.push({name, payload});
		});

		renderComponent();

		(ObjectEntryService.getObjectEntry as jest.Mock).mockClear();

		await act(async () => {
			handlers['cms:aiAssistant:requestCategorize']({
				actions: [
					{agent: 'categorize', count: 5, targets: ['Travel']},
					{agent: 'tag', targets: ['kayaking']},
				],
			});
		});

		await waitFor(() => {
			expect(fireCalls).toHaveLength(2);
		});

		expect(ObjectEntryService.getObjectEntry).toHaveBeenCalledTimes(1);
		expect(fireCalls[0].payload).toEqual(
			expect.objectContaining({
				agent: 'L_AUTO_CATEGORIZE',
				count: 5,
				suppressUserMessage: true,
				targets: ['Travel'],
			})
		);
		expect(fireCalls[1].payload).toEqual(
			expect.objectContaining({
				agent: 'L_GENERATE_TAGS',
				suppressUserMessage: true,
				targets: ['kayaking'],
			})
		);
	});

	it('fires the categorize event without opening the categorization panel when requested', async () => {
		const handlers: Record<string, (payload: any) => void> = {};

		(global as any).Liferay.on = jest.fn(
			(name: string, callback: (payload: any) => void) => {
				handlers[name] = callback;
			}
		);
		(global as any).Liferay.fire = jest.fn();

		renderComponent();

		await act(async () => {
			handlers['cms:aiAssistant:requestCategorize']({
				actions: [{agent: 'categorize'}],
			});
		});

		expect(screen.queryByText('categorization')).not.toBeInTheDocument();

		await waitFor(() => {
			expect(global.Liferay.fire as jest.Mock).toHaveBeenCalledWith(
				'cms:aiAssistant:categorize',
				expect.objectContaining({
					agent: 'L_AUTO_CATEGORIZE',
					classNameId: 30982,
					cmsGroupId: '21000',
					content: 'Japan',
					scopeId: 555,
					suppressUserMessage: true,
				})
			);
		});
	});

	it('persists a tag commit while the categorization panel is closed', async () => {
		const handlers: Record<string, (payload: any) => void> = {};

		(global as any).Liferay.on = jest.fn(
			(name: string, callback: (payload: any) => void) => {
				handlers[name] = callback;
			}
		);
		(global as any).Liferay.fire = jest.fn();

		(
			CategorizationSuggestionService.createTagNames as jest.Mock
		).mockResolvedValue(['Culture']);

		await act(async () => {
			renderComponent();
		});

		await act(async () => {
			handlers['cms:aiAssistant:commit']({
				agent: 'L_GENERATE_TAGS',
				scopeId: 555,
				suggestions: [{isNew: true, name: 'Culture'}],
			});
		});

		expect(screen.queryByText('categorization')).not.toBeInTheDocument();

		await waitFor(() => {
			expect(
				CategorizationSuggestionService.createTagNames
			).toHaveBeenCalledWith([{isNew: true, name: 'Culture'}], {
				assetLibraryId: 555,
				cmsGroupId: '21000',
			});

			const tagNamesInput: HTMLInputElement | null =
				document.querySelector('[name="assetTagNames"]');

			expect(tagNamesInput?.value).toBe('Culture');
		});
	});

	it('persists the schedule field value when checking Never Expire and switching tabs', async () => {
		renderComponent();

		await userEvent.click(screen.getByLabelText('schedule[noun]'));

		await waitFor(() => {
			expect(screen.getByText('schedule[noun]')).toBeInTheDocument();
		});

		const expireCheckbox = screen.getAllByLabelText('never-expire')[0];

		expect(expireCheckbox).not.toBeChecked();

		await userEvent.click(expireCheckbox);

		await waitFor(() => {
			expect(expireCheckbox).toBeChecked();
		});

		await userEvent.click(screen.getByLabelText('general'));

		await waitFor(() => {
			expect(screen.getByText('general')).toBeInTheDocument();
		});

		await userEvent.click(screen.getByLabelText('schedule[noun]'));

		await waitFor(() => {
			expect(screen.getByText('schedule[noun]')).toBeInTheDocument();
			expect(expireCheckbox).toBeChecked();
			expect(
				screen.getByRole('textbox', {name: 'expiration-date'})
			).toHaveValue('08/14/2025 12:01 AM');
		});
	});

	it('renders ContentEditorSidePanel', () => {
		renderComponent();

		[
			'general',
			'comments',
			'schedule[noun]',
			'categorization',
			'projects',
		].forEach((name) =>
			expect(screen.getByTitle(name)).toBeInTheDocument()
		);
	});

	it('renders no Projects panel when CMP is disabled', () => {
		renderComponent({cmpEnabled: false});

		expect(screen.queryByTitle('projects')).not.toBeInTheDocument();
	});

	it('renders the hidden inputs with initial values', async () => {
		renderComponent();

		const expirationInput: HTMLInputElement | null = document.querySelector(
			'[name="ObjectEntry_expirationDate"]'
		);
		const reviewInput: HTMLInputElement | null = document.querySelector(
			'[name="ObjectEntry_reviewDate"]'
		);

		expect(expirationInput?.value).toBe(EXPIRATION_DATE);
		expect(reviewInput?.value).toBe(REVIEW_DATE);
	});
});
