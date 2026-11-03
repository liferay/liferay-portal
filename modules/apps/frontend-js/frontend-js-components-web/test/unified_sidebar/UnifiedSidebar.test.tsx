/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import '@testing-library/jest-dom';

import UnifiedSidebar from '../../src/main/resources/META-INF/resources/unified_sidebar/UnifiedSidebar';
import UnifiedSidebarSiteSelector from '../../src/main/resources/META-INF/resources/unified_sidebar/UnifiedSidebarSiteSelector';

const DEFAULT_PROPS = {
	categoryImageUrl: '/images/test-icon.svg',
	label: 'Control Panel',
};

describe('UnifiedSidebar', () => {
	it('renders the label', () => {
		render(<UnifiedSidebar {...DEFAULT_PROPS} />);

		expect(screen.getByText('Control Panel')).toBeInTheDocument();
	});

	it('renders the product icon with the given image URL', () => {
		const {container} = render(<UnifiedSidebar {...DEFAULT_PROPS} />);

		expect(container.querySelector('img')).toHaveAttribute(
			'src',
			'/images/test-icon.svg'
		);
	});

	it('renders children inside the panel body', () => {
		render(
			<UnifiedSidebar {...DEFAULT_PROPS}>
				<UnifiedSidebar.Body>
					<p>Panel content</p>
				</UnifiedSidebar.Body>
			</UnifiedSidebar>
		);

		expect(screen.getByText('Panel content')).toBeInTheDocument();
	});

	it('applies testId as data-testid', () => {
		render(<UnifiedSidebar {...DEFAULT_PROPS} testId="my-sidebar" />);

		expect(screen.getByTestId('my-sidebar')).toBeInTheDocument();
	});

	it('renders headerActions inside the header', () => {
		render(
			<UnifiedSidebar
				{...DEFAULT_PROPS}
				headerActions={
					<UnifiedSidebarSiteSelector
						eventName="selectSite"
						url="/select-site"
					/>
				}
			/>
		);

		expect(screen.getByLabelText('go-to-other-site')).toBeInTheDocument();
	});

	it('does not render the site selector button when headerActions is absent', () => {
		render(<UnifiedSidebar {...DEFAULT_PROPS} />);

		expect(
			screen.queryByLabelText('go-to-other-site')
		).not.toBeInTheDocument();
	});

	describe('when LPD-57922 is enabled', () => {
		beforeEach(() => {
			Liferay.FeatureFlags['LPD-57922'] = true;
		});

		afterEach(() => {
			Liferay.FeatureFlags['LPD-57922'] = false;
		});

		it('shows the color scheme toggle button', () => {
			render(
				<UnifiedSidebar
					{...DEFAULT_PROPS}
					colorScheme="light"
					colorSchemeSessionKey="colorScheme"
				/>
			);

			expect(
				screen.getByLabelText('switch-to-dark-mode')
			).toBeInTheDocument();
		});
	});

	it('does not show the color scheme toggle button when LPD-57922 is disabled', () => {
		render(<UnifiedSidebar {...DEFAULT_PROPS} />);

		expect(
			screen.queryByLabelText('switch-to-dark-mode')
		).not.toBeInTheDocument();
	});

	it('calls onOpenChange when the close button is clicked', async () => {
		const onOpenChange = jest.fn();

		render(
			<UnifiedSidebar
				{...DEFAULT_PROPS}
				onOpenChange={onOpenChange}
				open
			/>
		);

		await userEvent.click(screen.getByLabelText('close-product-menu'));

		expect(onOpenChange).toHaveBeenCalledWith(false);
	});

	it('uses the custom headerMessages when provided', async () => {
		const onOpenChange = jest.fn();

		render(
			<UnifiedSidebar
				{...DEFAULT_PROPS}
				headerMessages={{closeAriaLabel: 'close-site-panel'}}
				onOpenChange={onOpenChange}
				open
			/>
		);

		await userEvent.click(screen.getByLabelText('close-site-panel'));

		expect(onOpenChange).toHaveBeenCalledWith(false);
	});
});
