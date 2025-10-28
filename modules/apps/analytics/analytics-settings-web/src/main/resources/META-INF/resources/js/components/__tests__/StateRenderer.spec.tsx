/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import React from 'react';

import StateRenderer, {
	EmptyStateComponent,
	ErrorStateComponent,
} from '../StateRenderer';

describe('State Renderer', () => {
	it('renders EmptyStateComponent', () => {
		const {container} = render(
			<StateRenderer empty error={false} loading={false}>
				<StateRenderer.Empty>
					<EmptyStateComponent
						className="empty-state-border"
						description="this is a description"
						imgSrc=""
						title="this is a title"
					/>
				</StateRenderer.Empty>
			</StateRenderer>
		);

		const emptyStateBorder = container.querySelector('.empty-state-border');

		expect(emptyStateBorder).toBeInTheDocument();

		expect(screen.getByText(/this is a title/i)).toBeInTheDocument();

		expect(screen.getByText(/this is a description/i)).toBeInTheDocument();
	});

	it('renders EmptyStateComponent with loading', () => {
		const {container} = render(
			<StateRenderer empty={false} error={false} loading>
				<StateRenderer.Empty>
					<EmptyStateComponent imgSrc="" />
				</StateRenderer.Empty>
			</StateRenderer>
		);

		const loadingAnimationChild = container.querySelector(
			'span.loading-animation'
		);

		const loadingElement = screen.getByTestId('loading');

		expect(loadingElement).toBeInTheDocument();

		expect(loadingElement.contains(loadingAnimationChild)).toBe(true);
	});

	it('renders ErrorStateComponent', () => {
		const {container} = render(
			<StateRenderer empty={false} error loading={false}>
				<StateRenderer.Error>
					<ErrorStateComponent className="empty-state-border" />
				</StateRenderer.Error>
			</StateRenderer>
		);

		const emptyStateBorder = container.querySelector('.empty-state-border');

		expect(emptyStateBorder).toBeInTheDocument();

		expect(
			screen.getByText(/an-unexpected-system-error-occurred/i)
		).toBeInTheDocument();
	});
});
