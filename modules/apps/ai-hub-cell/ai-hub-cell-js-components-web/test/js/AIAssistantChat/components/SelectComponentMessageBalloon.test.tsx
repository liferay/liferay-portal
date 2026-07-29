/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import {fetch} from 'frontend-js-web';
import React from 'react';

import '@testing-library/jest-dom';

import SelectComponentMessageBalloon from '../../../../src/main/resources/META-INF/resources/js/AIAssistantChat/components/SelectComponentMessageBalloon';
import {AgentComponent} from '../../../../src/main/resources/META-INF/resources/js/AIAssistantChat/types';

jest.mock('frontend-js-web', () => ({fetch: jest.fn()}));

const mockFetch = fetch as jest.MockedFunction<typeof fetch>;

const COMPONENT: AgentComponent = {
	options: [
		{
			action: {
				'http-request': {
					body: {transitionName: 'findMatchingAssets'},
					href: '/o/ai-hub/v1.0/agent-instances/123/resume',
					method: 'PUT',
				},
			},
			label: 'Find Matching Assets in CMS',
		},
		{
			action: {
				'http-request': {
					body: {context: {transitionName: 'generateContent'}},
					href: '/o/ai-hub/v1.0/agent-instances/123/resume',
					method: 'PUT',
				},
			},
			label: 'Generate Content for Gaps',
		},
	],
	title: 'What would you like to do next?',
	type: 'select',
};

describe('SelectComponentMessageBalloon', () => {
	beforeEach(() => {
		mockFetch.mockReset();

		mockFetch.mockImplementation((resource) => {
			if (String(resource).includes('authorization-tokens')) {
				return Promise.resolve({
					json: () =>
						Promise.resolve({
							accessToken: 'access-token',
							serviceURL: 'http://ai-hub',
							userToken: 'user-token',
						}),
					ok: true,
				} as never);
			}

			return Promise.resolve({ok: true} as never);
		});
	});

	it('disables the select after submitting', async () => {
		render(
			<SelectComponentMessageBalloon
				component={COMPONENT}
				setIsGenerating={jest.fn()}
			/>
		);

		await userEvent.selectOptions(
			screen.getByLabelText('What would you like to do next?'),
			screen.getByRole('option', {name: 'Find Matching Assets in CMS'})
		);

		expect(
			screen.getByLabelText('What would you like to do next?')
		).toBeDisabled();
	});

	it('renders the title and the option labels', () => {
		render(
			<SelectComponentMessageBalloon
				component={COMPONENT}
				setIsGenerating={jest.fn()}
			/>
		);

		expect(
			screen.getByText('What would you like to do next?')
		).toBeInTheDocument();

		expect(
			screen.getByRole('option', {name: 'Find Matching Assets in CMS'})
		).toBeInTheDocument();

		expect(
			screen.getByRole('option', {name: 'Generate Content for Gaps'})
		).toBeInTheDocument();
	});

	it('sends a nested context body verbatim', async () => {
		render(
			<SelectComponentMessageBalloon
				component={COMPONENT}
				setIsGenerating={jest.fn()}
			/>
		);

		await userEvent.selectOptions(
			screen.getByLabelText('What would you like to do next?'),
			screen.getByRole('option', {name: 'Generate Content for Gaps'})
		);

		await waitFor(() =>
			expect(mockFetch).toHaveBeenCalledWith(
				'/o/ai-hub/v1.0/agent-instances/123/resume',
				expect.objectContaining({
					body: JSON.stringify({
						context: {transitionName: 'generateContent'},
					}),
					method: 'PUT',
				})
			)
		);
	});

	it('sends the chosen option action request with its body verbatim', async () => {
		const setIsGenerating = jest.fn();

		render(
			<SelectComponentMessageBalloon
				component={COMPONENT}
				setIsGenerating={setIsGenerating}
			/>
		);

		await userEvent.selectOptions(
			screen.getByLabelText('What would you like to do next?'),
			screen.getByRole('option', {name: 'Find Matching Assets in CMS'})
		);

		expect(setIsGenerating).toHaveBeenCalledWith(true);

		await waitFor(() =>
			expect(mockFetch).toHaveBeenCalledWith(
				'/o/ai-hub/v1.0/agent-instances/123/resume',
				expect.objectContaining({
					body: JSON.stringify({
						transitionName: 'findMatchingAssets',
					}),
					method: 'PUT',
				})
			)
		);
	});
});
