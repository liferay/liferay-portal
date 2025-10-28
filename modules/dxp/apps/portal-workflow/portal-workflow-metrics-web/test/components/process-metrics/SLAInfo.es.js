/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, cleanup, fireEvent, render} from '@testing-library/react';
import React from 'react';

import SLAInfo from '../../../src/main/resources/META-INF/resources/js/components/process-metrics/SLAInfo.es';
import {MockRouter} from '../../mock/MockRouter.es';

import '@testing-library/jest-dom';

describe('The SLAInfo component should', () => {
	let container;
	let getByText;

	describe('SLA count 0', () => {
		beforeAll(async () => {
			fetch.mockResolvedValue({
				json: () => Promise.resolve({items: [], totalCount: 0}),
				ok: true,
			});

			const renderResult = render(
				<MockRouter>
					<SLAInfo processId="1" />
				</MockRouter>
			);

			container = renderResult.container;
			getByText = renderResult.getByText;

			await act(async () => {
				jest.runAllTimers();
			});
		});

		it('Show alert with correct data', () => {
			const slaInfoAlert = getByText(
				'no-slas-are-defined-for-this-process'
			);
			const slaInfoLink = getByText('add-a-new-sla');

			expect(slaInfoLink.getAttribute('href')).toContain('/sla/1/new');
			expect(slaInfoAlert).toBeTruthy();

			fireEvent.click(container.querySelector('button.close'));

			expect(container.innerHTML).toBe('');

			cleanup();
		});
	});

	describe('SLA blocked count 1', () => {
		beforeAll(async () => {
			fetch.mockResolvedValue({
				json: () => Promise.resolve({items: [], totalCount: 1}),
				ok: true,
			});

			const renderResult = render(
				<MockRouter>
					<SLAInfo processId="1" />
				</MockRouter>
			);

			container = renderResult.container;
			getByText = renderResult.getByText;

			await act(async () => {
				jest.runAllTimers();
			});
		});

		it('Show alert with correct data', () => {
			const slaInfoAlert = getByText(
				'x-sla-is-blocked fix-the-sla-configuration-to-resume-accurate-reporting'
			);
			const slaInfoLink = getByText('set-up-slas');

			expect(slaInfoLink.getAttribute('href')).toContain(
				'/sla/1/list/20/1'
			);
			expect(slaInfoAlert).toBeTruthy();

			fireEvent.click(container.querySelector('button.close'));

			expect(container.innerHTML).toBe('');
		});
	});

	describe('SLA blocked count greater than 1', () => {
		beforeAll(async () => {
			fetch.mockResolvedValue({
				json: () => Promise.resolve({items: [], totalCount: 2}),
				ok: true,
			});

			const renderResult = render(
				<MockRouter>
					<SLAInfo processId="1" />
				</MockRouter>
			);

			container = renderResult.container;
			getByText = renderResult.getByText;

			await act(async () => {
				jest.runAllTimers();
			});
		});

		it('Show alert with correct data', () => {
			const slaInfoAlert = getByText(
				'x-slas-are-blocked fix-the-sla-configuration-to-resume-accurate-reporting'
			);
			const slaInfoLink = getByText('set-up-slas');

			expect(slaInfoLink.getAttribute('href')).toContain(
				'/sla/1/list/20/1'
			);
			expect(slaInfoAlert).toBeTruthy();

			fireEvent.click(container.querySelector('button.close'));

			expect(container.innerHTML).toBe('');
		});
	});
});
