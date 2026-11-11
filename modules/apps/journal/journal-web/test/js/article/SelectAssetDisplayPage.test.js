/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

// eslint-disable-next-line @liferay/portal/no-cross-module-deep-import
import {checkAccessibility} from '@liferay/layout-js-components-web/test/__lib__/index';
import {act, fireEvent, render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import {openModal} from 'frontend-js-components-web';
import React from 'react';

import '@testing-library/jest-dom';

import SelectAssetDisplayPage from '../../../src/main/resources/META-INF/resources/js/article/SelectAssetDisplayPage';

jest.mock('frontend-js-components-web', () => ({
	...jest.requireActual('frontend-js-components-web'),
	openModal: jest.fn(),
}));

const DISPLAY_PAGE_TYPE_DEFAULT = 1;

const DISPLAY_PAGE_TYPE_NONE = 0;

const DISPLAY_PAGE_TYPE_SPECIFIC = 2;

const DEFAULT_PROPS = {
	assetDisplayPageId: '',
	assetDisplayPageType: DISPLAY_PAGE_TYPE_NONE,
	defaultDisplayPageName: '',
	layoutUuid: '',
	newArticle: true,
	portletNamespace: 'namespace',
	saveAsDraftURL: 'http://localhost/save-as-draft',
	selectAssetDisplayPageEventName: 'selectAssetDisplayPage',
	selectAssetDisplayPageURL: 'http://localhost/select-asset-display-page',
	specificAssetDisplayPageName: '',
};

const renderComponent = () =>
	render(<SelectAssetDisplayPage {...DEFAULT_PROPS} />);

const renderComponentInForm = async (props) => {
	const form = document.createElement('form');

	form.id = `${DEFAULT_PROPS.portletNamespace}fm1`;
	form.method = 'post';
	form.innerHTML = `
		<input id="${DEFAULT_PROPS.portletNamespace}articleId" name="${DEFAULT_PROPS.portletNamespace}articleId" value="123" />
		<input id="${DEFAULT_PROPS.portletNamespace}formDate" name="${DEFAULT_PROPS.portletNamespace}formDate" />
		<input aria-label="title" name="${DEFAULT_PROPS.portletNamespace}title" value="title" />
		<input id="${DEFAULT_PROPS.portletNamespace}version" name="${DEFAULT_PROPS.portletNamespace}version" />
	`;

	document.body.appendChild(form);

	render(
		<SelectAssetDisplayPage
			{...DEFAULT_PROPS}
			assetDisplayPageType={DISPLAY_PAGE_TYPE_DEFAULT}
			defaultDisplayPageName="Display Page"
			newArticle={false}
			{...props}
		/>,
		{container: form.appendChild(document.createElement('div'))}
	);

	await act(() => Promise.resolve());
};

const getCmd = () => {
	const [[, {body}]] = fetch.mock.calls;

	return body.get(`${DEFAULT_PROPS.portletNamespace}cmd`);
};

describe('SelectAssetDisplayPage', () => {
	it('submits the display page fields as native hidden inputs', () => {
		const {container} = renderComponent();

		['assetDisplayPageId', 'displayPageType', 'layoutUuid'].forEach(
			(name) => {
				expect(
					container.querySelector(
						`input[name="${DEFAULT_PROPS.portletNamespace}${name}"]`
					)
				).toHaveAttribute('type', 'hidden');
			}
		);
	});

	it('has no accessibility violations', async () => {
		const {container} = renderComponent();

		await checkAccessibility({bestPractices: true, context: container});
	});

	describe('preview', () => {
		const {fetch: initialUtilFetch} = Liferay.Util;

		const previewResponse = JSON.stringify({
			articleId: '123',
			classPK: 1,
			friendlyUrlMap: {},
			previewURL: 'http://localhost/preview',
			version: 1.1,
		});

		beforeEach(() => {
			Liferay.componentReady = jest.fn(() =>
				Promise.resolve({
					isLocked: () => false,
					lock: () => {},
					unlock: () => {},
				})
			);
			Liferay.Util.fetch = global.fetch;

			fetch.mockResponseOnce(previewResponse);
		});

		afterEach(() => {
			document
				.getElementById(`${DEFAULT_PROPS.portletNamespace}fm1`)
				.remove();

			delete Liferay.componentReady;

			Liferay.Util.fetch = initialUtilFetch;

			jest.clearAllMocks();
		});

		it('previews the saved version when a field renders after the form loads', async () => {
			await renderComponentInForm();

			const input = document.createElement('input');

			input.name = `${DEFAULT_PROPS.portletNamespace}content`;
			input.value = '<p>content</p>';

			document
				.getElementById(`${DEFAULT_PROPS.portletNamespace}fm1`)
				.appendChild(input);

			await userEvent.click(
				screen.getByRole('button', {name: /preview/})
			);

			await waitFor(() => expect(openModal).toHaveBeenCalled());

			expect(getCmd()).toBe('preview');
		});

		it('previews the saved version when a field renders inside an element added after the form loads', async () => {
			await renderComponentInForm();

			document
				.getElementById(`${DEFAULT_PROPS.portletNamespace}fm1`)
				.insertAdjacentHTML(
					'beforeend',
					`<div><input name="${DEFAULT_PROPS.portletNamespace}content" value="content" /></div>`
				);

			await userEvent.click(
				screen.getByRole('button', {name: /preview/})
			);

			await waitFor(() => expect(openModal).toHaveBeenCalled());

			expect(getCmd()).toBe('preview');
		});

		it('previews the saved version when a field is named after it renders', async () => {
			await renderComponentInForm();

			const input = document.createElement('input');

			document
				.getElementById(`${DEFAULT_PROPS.portletNamespace}fm1`)
				.appendChild(input);

			await act(() => Promise.resolve());

			input.name = `${DEFAULT_PROPS.portletNamespace}content_es_ES`;

			await userEvent.click(
				screen.getByRole('button', {name: /preview/})
			);

			await waitFor(() => expect(openModal).toHaveBeenCalled());

			expect(getCmd()).toBe('preview');
		});

		it('saves a draft on the next preview when the form changes while a preview loads', async () => {
			await renderComponentInForm();

			fetch.resetMocks();

			fetch.mockResponseOnce(() => {
				screen.getByLabelText('title').value = 'title updated';

				return Promise.resolve(previewResponse);
			});
			fetch.mockResponseOnce(previewResponse);

			const previewButton = screen.getByRole('button', {name: /preview/});

			await userEvent.click(previewButton);

			await waitFor(() => expect(openModal).toHaveBeenCalledTimes(1));

			await userEvent.click(previewButton);

			await waitFor(() => expect(openModal).toHaveBeenCalledTimes(2));

			const [[, {body: firstBody}], [, {body: secondBody}]] =
				fetch.mock.calls;

			expect(firstBody.get(`${DEFAULT_PROPS.portletNamespace}cmd`)).toBe(
				'preview'
			);
			expect(secondBody.get(`${DEFAULT_PROPS.portletNamespace}cmd`)).toBe(
				'update'
			);
		});

		it('saves a draft before previewing a display page change', async () => {
			await renderComponentInForm({
				assetDisplayPageId: '1',
				assetDisplayPageType: DISPLAY_PAGE_TYPE_SPECIFIC,
				specificAssetDisplayPageName: 'Specific Display Page',
			});

			fireEvent.change(
				screen.getByLabelText('select-display-page-type'),
				{target: {value: String(DISPLAY_PAGE_TYPE_DEFAULT)}}
			);

			await userEvent.click(
				screen.getByRole('button', {name: /preview/})
			);

			await waitFor(() => expect(openModal).toHaveBeenCalled());

			expect(getCmd()).toBe('update');
		});

		it('previews the saved version when only the editing language changes', async () => {
			await renderComponentInForm();

			const form = document.getElementById(
				`${DEFAULT_PROPS.portletNamespace}fm1`
			);

			form.insertAdjacentHTML(
				'beforeend',
				`
				<input name="${DEFAULT_PROPS.portletNamespace}availableLocales" value="en_US" />
				<input name="${DEFAULT_PROPS.portletNamespace}ddm$$content$$en_US_edited" value="true" />
				<input class="language-value" name="${DEFAULT_PROPS.portletNamespace}friendlyURL" value="title" />
				<input name="${DEFAULT_PROPS.portletNamespace}friendlyURL_en_US" value="title" />
			`
			);

			await act(() => Promise.resolve());

			form.elements[
				`${DEFAULT_PROPS.portletNamespace}availableLocales`
			].value = 'en_US,ca_ES';
			form.elements[
				`${DEFAULT_PROPS.portletNamespace}friendlyURL`
			].value = '';

			form.elements[
				`${DEFAULT_PROPS.portletNamespace}ddm$$content$$en_US_edited`
			].name =
				`${DEFAULT_PROPS.portletNamespace}ddm$$content$$ca_ES_edited`;

			await userEvent.click(
				screen.getByRole('button', {name: /preview/})
			);

			await waitFor(() => expect(openModal).toHaveBeenCalled());

			expect(getCmd()).toBe('preview');
		});

		it('saves a draft before previewing edits made while the display page type was none', async () => {
			await renderComponentInForm();

			const select = screen.getByLabelText('select-display-page-type');

			fireEvent.change(select, {
				target: {value: String(DISPLAY_PAGE_TYPE_NONE)},
			});

			await userEvent.type(screen.getByLabelText('title'), ' updated');

			fireEvent.change(select, {
				target: {value: String(DISPLAY_PAGE_TYPE_DEFAULT)},
			});

			await userEvent.click(
				screen.getByRole('button', {name: /preview/})
			);

			await waitFor(() => expect(openModal).toHaveBeenCalled());

			expect(getCmd()).toBe('update');
		});
	});
});
