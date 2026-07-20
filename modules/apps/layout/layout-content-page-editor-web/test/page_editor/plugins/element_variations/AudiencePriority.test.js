/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {act, fireEvent, render, screen} from '@testing-library/react';
import React from 'react';

import configModule from '../../../../src/main/resources/META-INF/resources/page_editor/app/config/index';
import AudiencePriority from '../../../../src/main/resources/META-INF/resources/page_editor/plugins/element_variations/AudiencePriority';

const AUDIENCES = [
	{label: 'VIP', value: 'vip'},
	{label: 'Audience 1', value: 'audience-1'},
];

const frontendJsWebMock = jest.requireMock('frontend-js-web');

const renderComponent = () =>
	render(
		<AudiencePriority
			audiences={AUDIENCES}
			segmentsExperienceERC="experience-erc"
			updateAudiencesPriorityURL="http://localhost/update-audiences-priority"
		/>
	);

describe('AudiencePriority', () => {
	beforeAll(() => {
		Liferay.Language.direction = {en_US: 'ltr'};

		Object.defineProperty(configModule, 'config', {
			value: {portletNamespace: '_portlet_'},
		});

		jest.useFakeTimers();
	});

	afterAll(() => {
		jest.useRealTimers();
	});

	it('renders every audience in the given order', () => {
		renderComponent();

		const labels = screen
			.getAllByText(/VIP|Audience 1/)
			.map((element) => element.textContent);

		expect(labels).toEqual(['VIP', 'Audience 1']);
	});

	it('hides the edit button and shows a message when there are no audiences', () => {
		render(
			<AudiencePriority
				audiences={[]}
				segmentsExperienceERC="experience-erc"
				updateAudiencesPriorityURL="http://localhost/update-audiences-priority"
			/>
		);

		expect(screen.getByText('no-audiences-available')).toBeInTheDocument();
		expect(screen.queryByLabelText('edit')).not.toBeInTheDocument();
	});

	it('saves the reordered audiences for the experience', async () => {
		frontendJsWebMock.fetch.mockImplementation(() =>
			Promise.resolve({
				clone: () => ({json: () => Promise.resolve({})}),
				status: 200,
			})
		);

		renderComponent();

		fireEvent.click(screen.getByLabelText('edit'));

		act(() => jest.runAllTimers());

		const reorderButton = screen.getByRole('button', {
			name: /reorder vip/i,
		});

		reorderButton.focus();

		act(() => {
			reorderButton.dispatchEvent(
				new KeyboardEvent('keyup', {bubbles: true, key: 'Enter'})
			);
		});

		act(() => {
			reorderButton.dispatchEvent(
				new KeyboardEvent('keyup', {bubbles: true, key: 'ArrowDown'})
			);
		});

		act(() => {
			reorderButton.dispatchEvent(
				new KeyboardEvent('keyup', {bubbles: true, key: 'ArrowDown'})
			);
		});

		act(() => {
			reorderButton.dispatchEvent(
				new KeyboardEvent('keyup', {bubbles: true, key: 'Enter'})
			);
		});

		fireEvent.click(screen.getByText('save'));

		await act(async () => {
			jest.runAllTimers();
		});

		expect(frontendJsWebMock.fetch).toHaveBeenCalledWith(
			'http://localhost/update-audiences-priority',
			expect.anything()
		);

		const formData = frontendJsWebMock.fetch.mock.calls[0][1].body;

		expect(formData.get('_portlet_audienceEntryERCs')).toBe(
			JSON.stringify(['audience-1', 'vip'])
		);
		expect(formData.get('_portlet_segmentsExperienceERC')).toBe(
			'experience-erc'
		);

		const labels = screen
			.getAllByText(/VIP|Audience 1/)
			.map((element) => element.textContent);

		expect(labels).toEqual(['Audience 1', 'VIP']);
	});
});
