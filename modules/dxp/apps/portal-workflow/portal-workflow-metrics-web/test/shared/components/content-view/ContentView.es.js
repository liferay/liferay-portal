/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {act, cleanup, render} from '@testing-library/react';
import React from 'react';

import ContentView from '../../../../src/main/resources/META-INF/resources/js/shared/components/content-view/ContentView.es';
import PromisesResolver from '../../../../src/main/resources/META-INF/resources/js/shared/components/promises-resolver/PromisesResolver.es';

describe('The ContentView component should', () => {
	afterEach(cleanup);

	it('Be rendered with children', () => {
		const {getByText} = render(
			<ContentView>
				<div>Lorem Ipsum</div>
			</ContentView>
		);

		expect(getByText('Lorem Ipsum')).toBeTruthy();
	});

	it('Be rendered with empty state and the expected message', () => {
		const {getByText} = render(
			<ContentView emptyProps={{message: 'No results were found.'}} />
		);

		expect(getByText('No results were found.')).toBeTruthy();
	});

	it('Be rendered with loading state', async () => {
		const {container} = render(
			<PromisesResolver promises={[new Promise(() => {})]}>
				<ContentView />
			</PromisesResolver>
		);

		expect(
			container.querySelector('span.loading-animation')
		).not.toBeNull();
	});

	describe('Be rendered with error state', () => {
		let getByText;

		beforeAll(async () => {
			const renderResult = render(
				<PromisesResolver
					promises={[
						new Promise((_, reject) => {
							reject(new Error('error'));
						}),
					]}
				>
					<ContentView
						errorProps={{message: 'Unable to retrieve data'}}
					/>
				</PromisesResolver>
			);

			getByText = renderResult.getByText;

			await act(async () => {
				jest.runAllTimers();
			});
		});

		it('Be rendered with expected message', () => {
			expect(getByText('Unable to retrieve data')).toBeTruthy();
		});
	});
});
