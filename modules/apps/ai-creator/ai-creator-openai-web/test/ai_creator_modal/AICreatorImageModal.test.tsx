/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import {fetch} from 'frontend-js-web';
import React from 'react';

import '@testing-library/jest-dom';

import AICreatorImageModal from '../../src/main/resources/META-INF/resources/ai_creator_modal/AICreatorImageModal';

jest.mock('frontend-js-web', () => ({
	...(jest.requireActual('frontend-js-web') as any),
	fetch: jest.fn(() => Promise.resolve({})),
	getOpener: jest.fn(() => ({
		Liferay: {
			fire: jest.fn(),
		},
	})),
}));

const mockedFetch = fetch as any;

async function renderModalWithRequest() {
	render(

		// @ts-ignore

		<AICreatorImageModal
			getGenerationsURL="/sample-url"
			portletNamespace="namespace"
			uploadGenerationsURL="/upload-url"
		/>
	);

	userEvent.type(
		screen.getByLabelText('description'),
		'Sample image description'
	);
	userEvent.selectOptions(screen.getByLabelText('image-size'), '256x256');
	userEvent.type(screen.getByLabelText('number-of-images-to-generate'), '4');

	await act(async () => {
		userEvent.click(screen.getByRole('button', {name: 'create'}));
	});
}

describe('AICreatorImageModal', () => {
	afterEach(() => {
		mockedFetch.mockReset();
	});

	it('calls backend with the given config', async () => {
		await renderModalWithRequest();

		expect(mockedFetch).toHaveBeenCalledTimes(1);

		const [[url, {body}]] = mockedFetch.mock.calls;

		expect(url).toBe('/sample-url');

		expect(body.get('namespaceprompt')).toBe('Sample image description');
		expect(body.get('namespacesize')).toBe('256x256');
		expect(body.get('namespacenumberOfImages')).toBe('4');
	});

	it('shows error messages if any', async () => {
		mockedFetch.mockReturnValue(
			Promise.resolve(
				new Response(
					JSON.stringify({
						error: {
							message: 'Some testing error',
						},
					})
				)
			)
		);

		await renderModalWithRequest();

		expect(screen.getByText('Some testing error')).toBeInTheDocument();
	});

	it('shows generation result', async () => {
		mockedFetch.mockReturnValue(
			Promise.resolve(
				new Response(
					JSON.stringify({
						generations: {
							content: [
								'/generated-image-url-1.png',
								'/generated-image-url-2.png',
								'/generated-image-url-3.png',
								'/generated-image-url-4.png',
							],
						},
					})
				)
			)
		);

		await renderModalWithRequest();

		expect(screen.getAllByRole('checkbox')).toHaveLength(4);

		expect(screen.getByRole('button', {name: 'try-again'})).toBeEnabled();
		expect(
			screen.getByRole('button', {name: 'add-selected'})
		).toBeDisabled();
	});

	it('sends two selected images to the backend', async () => {
		mockedFetch.mockReturnValue(
			Promise.resolve(
				new Response(
					JSON.stringify({
						generations: {
							content: [
								'/generated-image-url-1.png',
								'/generated-image-url-2.png',
								'/generated-image-url-3.png',
							],
						},
					})
				)
			)
		);

		await renderModalWithRequest();

		const cards = screen.getAllByRole('checkbox');

		userEvent.click(cards[0]);
		userEvent.click(cards[1]);
		userEvent.dblClick(cards[2]);

		const buttonAddSelected = screen.getByRole('button', {
			name: 'add-selected',
		});

		expect(buttonAddSelected).toBeEnabled();

		mockedFetch.mockReturnValue(
			Promise.resolve(
				new Response(
					JSON.stringify({
						success: true,
					})
				)
			)
		);

		await act(async () => {
			userEvent.click(buttonAddSelected);
		});

		expect(mockedFetch).toHaveBeenCalledTimes(3);

		const [url] = mockedFetch.mock.calls[1];

		expect(url).toBe('/upload-url');
	});

	it('shows unexpected error messages if any while generating images', async () => {
		mockedFetch.mockReturnValue(
			Promise.resolve(
				new Response(
					JSON.stringify({
						generations: {},
					})
				)
			)
		);

		await renderModalWithRequest();

		expect(
			screen.getByText('an-unexpected-error-occurred')
		).toBeInTheDocument();
	});
});
