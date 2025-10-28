/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import {fetch} from 'frontend-js-web';
import React from 'react';

import '@testing-library/jest-dom';

import AICreatorModal from '../../src/main/resources/META-INF/resources/ai_creator_modal/AICreatorModal';

jest.mock('frontend-js-web', () => ({
	fetch: jest.fn(() => Promise.resolve({})),
}));

const mockedFetch = fetch as any;

async function renderModalWithRequest() {
	render(

		// @ts-ignore

		<AICreatorModal
			getCompletionURL="/sample-url"
			portletNamespace="namespace"
		/>
	);

	userEvent.type(screen.getByLabelText('description'), 'Sample text');
	userEvent.selectOptions(screen.getByLabelText('tone'), 'friendly');
	userEvent.type(screen.getByLabelText('word-count'), '123');

	await act(async () => {
		userEvent.click(screen.getByRole('button', {name: 'create'}));
	});
}

describe('AICreatorModal', () => {
	afterEach(() => {
		mockedFetch.mockReset();
	});

	it('calls backend with the given config', async () => {
		await renderModalWithRequest();

		expect(mockedFetch).toHaveBeenCalledTimes(1);

		const [[url, {body}]] = mockedFetch.mock.calls;

		expect(url).toBe('/sample-url');
		expect(body.get('namespacecontent')).toBe('Sample text');
		expect(body.get('namespacetone')).toBe('friendly');
		expect(body.get('namespacewords')).toBe('123');
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

	it('shows completion result', async () => {
		mockedFetch.mockReturnValue(
			Promise.resolve(
				new Response(
					JSON.stringify({
						completion: {
							content: 'This text has been generated',
						},
					})
				)
			)
		);

		await renderModalWithRequest();

		expect(screen.getByLabelText('content').textContent).toBe(
			'This text has been generated'
		);
	});
});
