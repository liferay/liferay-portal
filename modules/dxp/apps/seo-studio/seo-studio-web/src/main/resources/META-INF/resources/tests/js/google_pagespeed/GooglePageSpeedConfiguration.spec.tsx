/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {fireEvent, render, screen, waitFor} from '@testing-library/react';
import {openToast} from 'frontend-js-components-web';
import React from 'react';

import GooglePageSpeedConfiguration from '../../../js/google_pagespeed/GooglePageSpeedConfiguration';

jest.mock('frontend-js-components-web', () => ({
	openToast: jest.fn(),
}));

const BACK_URL = '/back/here';

type ConfigurationProps = React.ComponentProps<
	typeof GooglePageSpeedConfiguration
>;

function randomInt() {
	return Math.floor(Math.random() * 10000);
}

function randomString() {
	return Math.random().toString(36).slice(2);
}

function renderConfiguration(props: Partial<ConfigurationProps> = {}) {
	return render(
		<GooglePageSpeedConfiguration
			backURL={randomString()}
			domainsURL={randomString()}
			instancesURL={randomString()}
			{...props}
		/>
	);
}

function getAPIKeyInput() {
	return screen.getByPlaceholderText('enter-your-api-key');
}

function getCancelLink() {
	return screen.getByRole('link', {name: 'cancel'});
}

function getSaveButton() {
	return screen.getByRole('button', {name: /save|validating/});
}

function getToggleButton() {
	return screen.getByLabelText('toggle-api-key-visibility');
}

function mockInitialLoad({
	domains = [],
	instances = [],
}: {
	domains?: Array<{id: number}>;
	instances?: Array<{googlePageSpeedAPIKey?: string; id: number}>;
} = {}) {
	const fetchMock = Liferay.Util.fetch as jest.Mock;

	fetchMock.mockResolvedValueOnce({
		json: () => Promise.resolve({items: domains}),
		ok: true,
	});
	fetchMock.mockResolvedValueOnce({
		json: () => Promise.resolve({items: instances}),
		ok: true,
	});
}

describe('GooglePageSpeedConfiguration', () => {
	beforeEach(() => {
		(openToast as jest.Mock).mockClear();

		(Liferay.Util as unknown) = {
			fetch: jest.fn(),
		};

		delete (window as any).location;
		(window as any).location = {assign: jest.fn(), href: ''};

		sessionStorage.clear();
	});

	describe('render', () => {
		it('links Cancel to the provided backURL', async () => {
			mockInitialLoad();

			renderConfiguration({backURL: BACK_URL});

			expect(getCancelLink()).toHaveAttribute('href', BACK_URL);
		});

		it('starts with the API key input hidden as a password', async () => {
			mockInitialLoad();

			renderConfiguration();

			expect(getAPIKeyInput()).toHaveAttribute('type', 'password');
		});

		it('toggles the API key input visibility on click', async () => {
			mockInitialLoad();

			renderConfiguration();

			fireEvent.click(getToggleButton());

			expect(getAPIKeyInput()).toHaveAttribute('type', 'text');

			fireEvent.click(getToggleButton());

			expect(getAPIKeyInput()).toHaveAttribute('type', 'password');
		});
	});

	describe('initial load', () => {
		it('populates the API key from an existing instance', async () => {
			const existingKey = randomString();

			mockInitialLoad({
				domains: [{id: randomInt()}],
				instances: [
					{
						googlePageSpeedAPIKey: existingKey,
						id: randomInt(),
					},
				],
			});

			renderConfiguration();

			await waitFor(() => {
				expect(getAPIKeyInput()).toHaveValue(existingKey);
			});
		});

		it('leaves the input empty when no instance has a key', async () => {
			mockInitialLoad({
				domains: [{id: randomInt()}],
				instances: [{id: randomInt()}],
			});

			renderConfiguration();

			await waitFor(() => {
				expect(getSaveButton()).toBeInTheDocument();
			});

			expect(getAPIKeyInput()).toHaveValue('');
		});

		it('shows a danger toast when the initial fetch returns an HTTP error', async () => {
			const fetchMock = Liferay.Util.fetch as jest.Mock;

			fetchMock.mockResolvedValueOnce({
				json: () => Promise.resolve({}),
				ok: false,
			});
			fetchMock.mockResolvedValueOnce({
				json: () => Promise.resolve({}),
				ok: false,
			});

			renderConfiguration();

			await waitFor(() => {
				expect(openToast).toHaveBeenCalledWith(
					expect.objectContaining({
						message: 'failed-to-load-configuration',
						type: 'danger',
					})
				);
			});
		});
	});

	describe('save flow', () => {
		it('disables Save while the API key input is empty', async () => {
			mockInitialLoad({domains: [{id: randomInt()}]});

			renderConfiguration();

			await waitFor(() => {
				expect(getSaveButton()).toBeDisabled();
			});
		});

		it('enables Save once the user types an API key', async () => {
			mockInitialLoad({domains: [{id: randomInt()}]});

			renderConfiguration();

			await waitFor(() => {
				expect(getSaveButton()).toBeDisabled();
			});

			fireEvent.change(getAPIKeyInput(), {
				target: {value: randomString()},
			});

			expect(getSaveButton()).toBeEnabled();
		});

		it('shows a danger toast when no domains exist', async () => {
			mockInitialLoad();

			renderConfiguration();

			await waitFor(() => {
				expect(getSaveButton()).toBeInTheDocument();
			});

			fireEvent.change(getAPIKeyInput(), {
				target: {value: randomString()},
			});
			fireEvent.click(getSaveButton());

			await waitFor(() => {
				expect(openToast).toHaveBeenCalledWith(
					expect.objectContaining({type: 'danger'})
				);
			});
		});

		it('patches the instance with the new API key and navigates back on success', async () => {
			const apiKey = randomString();

			mockInitialLoad({
				domains: [{id: randomInt()}],
				instances: [{id: randomInt()}],
			});

			const fetchMock = Liferay.Util.fetch as jest.Mock;

			fetchMock.mockResolvedValueOnce({
				json: () => Promise.resolve({}),
			});
			fetchMock.mockResolvedValueOnce({ok: true});

			renderConfiguration({backURL: BACK_URL});

			await waitFor(() => {
				expect(getSaveButton()).toBeInTheDocument();
			});

			fireEvent.change(getAPIKeyInput(), {target: {value: apiKey}});
			fireEvent.click(getSaveButton());

			await waitFor(() => {
				expect(window.location.assign).toHaveBeenCalledWith(BACK_URL);
			});

			const patchCalls = fetchMock.mock.calls.filter(
				([, options]) => options?.method === 'PATCH'
			);

			expect(patchCalls).toHaveLength(1);

			expect(
				JSON.parse(patchCalls[0][1].body).googlePageSpeedAPIKey
			).toBe(apiKey);

			expect(sessionStorage.getItem('seoStudioToast')).toBe(
				'google-pagespeed-api-key-added'
			);
		});

		it.each([
			{
				json: () =>
					Promise.resolve({
						error: {status: 'PERMISSION_DENIED'},
					}),
				name: 'the key fails Google validation',
			},
			{
				json: () => Promise.reject(new SyntaxError()),
				name: 'Google returns a malformed body',
			},
		])('shows an inline validation error when $name', async ({json}) => {
			mockInitialLoad({domains: [{id: randomInt()}]});

			const fetchMock = Liferay.Util.fetch as jest.Mock;

			fetchMock.mockResolvedValueOnce({
				json,
				ok: false,
			});

			renderConfiguration();

			await waitFor(() => {
				expect(getSaveButton()).toBeInTheDocument();
			});

			fireEvent.change(getAPIKeyInput(), {
				target: {value: randomString()},
			});
			fireEvent.click(getSaveButton());

			await waitFor(() => {
				expect(
					screen.getByText(
						'unable-to-connect-to-google-pagespeed-verify-the-configuration-and-try-again'
					)
				).toBeInTheDocument();
			});

			expect(window.location.assign).not.toHaveBeenCalled();
		});

		it('shows an inline validation error when an instance save fails', async () => {
			mockInitialLoad({
				domains: [{id: randomInt()}],
				instances: [{id: randomInt()}],
			});

			const fetchMock = Liferay.Util.fetch as jest.Mock;

			fetchMock.mockResolvedValueOnce({
				json: () => Promise.resolve({}),
			});
			fetchMock.mockResolvedValueOnce({ok: false});

			renderConfiguration();

			await waitFor(() => {
				expect(getSaveButton()).toBeInTheDocument();
			});

			fireEvent.change(getAPIKeyInput(), {
				target: {value: randomString()},
			});
			fireEvent.click(getSaveButton());

			await waitFor(() => {
				expect(
					screen.getByText('unable-to-save-api-key')
				).toBeInTheDocument();
			});

			expect(window.location.assign).not.toHaveBeenCalled();
		});
	});
});
