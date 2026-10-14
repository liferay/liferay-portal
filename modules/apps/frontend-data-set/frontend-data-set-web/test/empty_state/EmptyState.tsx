/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayIconIllustrationSpriteContext} from '@clayui/icon';
import {render, screen} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';

import FrontendDataSetContext from '../../src/main/resources/META-INF/resources/FrontendDataSetContext';
import EmptyState from '../../src/main/resources/META-INF/resources/empty_state/EmptyState';
import {IEmptyStateConfiguration} from '../../src/main/resources/META-INF/resources/utils/types';

describe('EmptyState', () => {
	function renderEmptyState({
		emptyStateConfiguration,
		searchQuery = '',
	}: {
		emptyStateConfiguration?: IEmptyStateConfiguration;
		searchQuery?: string;
	}) {
		render(
			<ClayIconIllustrationSpriteContext.Provider
				value={Liferay.ThemeDisplay.getPathThemeEmptyStatesSpritemap()}
			>
				<FrontendDataSetContext.Provider
					value={
						{
							globalFDSState: {
								filters: [],
								search: {query: searchQuery},
							},
						} as any
					}
				>
					<EmptyState
						emptyStateConfiguration={emptyStateConfiguration}
						onClearFilters={() => {}}
					/>
				</FrontendDataSetContext.Provider>
			</ClayIconIllustrationSpriteContext.Provider>
		);

		return screen.getAllByRole('presentation')[0];
	}

	it('displays the default search state image', () => {
		const image = renderEmptyState({});

		expect(image).toHaveAttribute(
			'src',
			expect.stringMatching(/\/states\/search_state\.svg$/)
		);
	});

	it('displays the configured illustration', () => {
		const image = renderEmptyState({
			emptyStateConfiguration: {symbol: 'empty-state'},
		});

		expect(image.querySelector('use')).toHaveAttribute(
			'href',
			expect.stringMatching(/\/clay\/empty_states\.svg#empty-state$/)
		);
	});

	it('displays the configured illustration for a search', () => {
		const image = renderEmptyState({
			emptyStateConfiguration: {
				filtered: {search: {symbol: 'search-state'}},
			},
			searchQuery: 'foo',
		});

		expect(image.querySelector('use')).toHaveAttribute(
			'href',
			expect.stringMatching(/\/clay\/empty_states\.svg#search-state$/)
		);
	});
});
