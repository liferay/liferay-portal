/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {act, render} from '@testing-library/react';
import React from 'react';

import JournalPortlet from '../../src/main/resources/META-INF/resources/js/JournalPortlet.es';

const NAMESPACE = 'portletNamespace';

const renderComponent = () => {
	return render(
		<>
			<div className="article-content-content" />
			<form id={`${NAMESPACE}fm1`} method="post">
				<input
					id={`${NAMESPACE}jakarta-portlet-action`}
					type="hidden"
				/>

				<input
					id={`${NAMESPACE}availableLocales`}
					name={`${NAMESPACE}availableLocales`}
					type="hidden"
				/>

				<button
					id={`${NAMESPACE}contextualSidebarButton`}
					type="button"
				/>

				<div id={`${NAMESPACE}contextualSidebarContainer`} />

				<input
					id={`${NAMESPACE}formDate`}
					name={`${NAMESPACE}formDate`}
					type="hidden"
				/>

				<button
					data-actionname="publish"
					id={`${NAMESPACE}publishButton`}
					type="button"
				/>

				<button id={`${NAMESPACE}resetValuesButton`} type="button" />

				<button id={`${NAMESPACE}saveButton`} type="button" />

				<input
					id={`${NAMESPACE}newArticleId`}
					name={`${NAMESPACE}newArticleId`}
				/>

				<input
					id={`${NAMESPACE}articleId`}
					name={`${NAMESPACE}articleId`}
					type="hidden"
				/>

				<div className="hide" id={`${NAMESPACE}articleIdWrapper`} />

				<input
					id={`${NAMESPACE}version`}
					name={`${NAMESPACE}version`}
					type="hidden"
				/>

				<div
					className="hide"
					id={`${NAMESPACE}articleVersionStatusWrapper`}
				/>

				<span id={`${NAMESPACE}displayedArticleId`} />

				<span id={`${NAMESPACE}displayedVersion`} />

				<div className="hide" id={`${NAMESPACE}statusDraftLabel`} />
			</form>
		</>
	);
};

describe('JournalPortlet', () => {
	let lock;

	beforeEach(() => {
		jest.clearAllMocks();

		lock = {
			isLocked: jest.fn(() => false),
			lock: jest.fn(),
			unlock: jest.fn(),
		};

		global.Liferay.BREAKPOINTS = {PHONE: 767};

		global.Liferay.component = jest.fn((componentId) => {
			if (componentId === `${NAMESPACE}titleMapAsXML`) {
				return {getValue: () => 'Test'};
			}

			return undefined;
		});

		global.Liferay.componentReady = jest.fn((componentId) => {
			if (componentId === `${NAMESPACE}publishing`) {
				return Promise.resolve(lock);
			}

			return Promise.resolve();
		});
	});

	afterEach(() => {
		jest.useRealTimers();
	});

	it('routes the real publish submission to update_article once autosave already created the draft, even when the ID was typed before the title', async () => {
		jest.useFakeTimers();

		renderComponent();

		JournalPortlet({
			articleId: null,
			autoSaveDraftEnabled: true,
			autoSaveDraftURL: 'http://localhost/o/journal/auto_save_article',
			availableLocales: ['en_US'],
			classNameId: '0',
			contentTitle: 'Test',
			defaultLanguageId: 'en_US',
			hasSavePermission: true,
			namespace: NAMESPACE,
		});

		// Let the `publishing`/`SelectAssetDisplayPage` `componentReady`
		// promises resolve so the autosave form-change listener gets armed.

		await act(async () => {});

		const form = document.getElementById(`${NAMESPACE}fm1`);

		form.submit = jest.fn();

		// The reporter types the custom ID before the title is present; the
		// autosave gate only requires a title, so simulate that ordering by
		// changing the ID field first.

		const newArticleIdInput = document.getElementById(
			`${NAMESPACE}newArticleId`
		);

		newArticleIdInput.value = 'test123456';
		newArticleIdInput.dispatchEvent(new Event('change', {bubbles: true}));

		fetch.mockResponseOnce(
			JSON.stringify({
				articleId: 'test123456',
				friendlyURL: 'test123456',
				modifiedDate: 1700000000000,
				success: true,
				version: '1.0',
			})
		);

		// Fires the debounced autosave tick, which creates the draft article
		// server-side under the custom ID.

		act(() => {
			jest.advanceTimersByTime(1500);
		});

		await act(async () => {});

		const actionInput = document.getElementById(
			`${NAMESPACE}jakarta-portlet-action`
		);

		expect(actionInput).toHaveValue('/journal/add_article');

		const [, ddmFormValidHandler] = global.Liferay.on.mock.calls.find(
			([eventName]) => eventName === 'ddmFormValid'
		);

		// Simulates the user clicking Publish right after the autosave draft
		// was created: this must route to `update_article`, not `add_article`,
		// or it collides with the article's own draft and fails with
		// "Please enter a unique ID".

		ddmFormValidHandler();

		expect(actionInput).toHaveValue('/journal/update_article');
	});
});
