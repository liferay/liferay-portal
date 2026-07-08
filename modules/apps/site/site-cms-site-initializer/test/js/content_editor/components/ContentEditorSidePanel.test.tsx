/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {act, render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import ContentEditorSidePanel from '../../../../src/main/resources/META-INF/resources/js/content_editor/components/ContentEditorSidePanel';
import CategorizationCommitService from '../../../../src/main/resources/META-INF/resources/js/main_view/info_panel/services/CategorizationCommitService';
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
	'../../../../src/main/resources/META-INF/resources/js/main_view/info_panel/services/CategorizationCommitService'
);

const renderComponent = ({isSubscribed = false} = {}) => {
	return render(
		<ContentEditorSidePanel
			addCommentURL="addCommentURL"
			assetLibraryId="123"
			assetType={30982}
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

	it('renders ContentEditorSidePanel', () => {
		renderComponent();

		['general', 'comments', 'schedule[noun]', 'categorization'].forEach(
			(name) => expect(screen.getByTitle(name)).toBeInTheDocument()
		);
	});

	it('closes the panel pressing the Close button', async () => {
		renderComponent();

		const panelButton = screen.getByLabelText('general');

		await userEvent.click(panelButton);

		await waitFor(() => {
			expect(screen.getByText('general')).toBeInTheDocument();
		});

		await userEvent.click(screen.getByLabelText('close'));

		await waitFor(() => {
			expect(screen.queryByText('general')).not.toBeInTheDocument();
			expect(panelButton).toHaveFocus();
		});
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
				agent: 'L_AUTO_CATEGORIZE',
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
			CategorizationCommitService.createTagNames as jest.Mock
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
				CategorizationCommitService.createTagNames
			).toHaveBeenCalledWith([{isNew: true, name: 'Culture'}], {
				assetLibraryId: 555,
				cmsGroupId: '21000',
			});

			const tagNamesInput: HTMLInputElement | null =
				document.querySelector('[name="assetTagNames"]');

			expect(tagNamesInput?.value).toBe('Culture');
		});
	});

	it('opens the categorization panel when requested directly', async () => {
		const handlers: Record<string, (payload: any) => void> = {};

		(global as any).Liferay.on = jest.fn(
			(name: string, callback: (payload: any) => void) => {
				handlers[name] = callback;
			}
		);
		(global as any).Liferay.fire = jest.fn();

		renderComponent();

		await act(async () => {
			handlers['cms:aiAssistant:openCategorizationPanel']({});
		});

		expect(screen.getByText('categorization')).toBeInTheDocument();
	});
});
