/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fireEvent, render, screen} from '@testing-library/react';

import '@testing-library/jest-dom';
import React from 'react';

import {HeaderDropdown} from '../../components/Layout/LayoutScreen/HeaderDropdown';

const mockUseLayoutContext = jest.fn();

jest.mock('../../components/Layout/objectLayoutContext', () => ({
	useLayoutContext: () =>
		mockUseLayoutContext() || [
			{
				enableCategorization: true,
				enableFriendlyURLCustomization: true,
				isViewOnly: false,
				objectLayout: {
					objectLayoutTabs: [],
				},
			},
			jest.fn(),
		],
}));

const mockAddCategorization = jest.fn();

const mockAddSeo = jest.fn();

const mockDeleteElement = jest.fn();

function mockLayoutContextWithBoxes(
	objectLayoutBoxes: Array<
		{type: string} & Partial<{
			collapsable: boolean;
		}>
	> = []
) {
	mockUseLayoutContext.mockReturnValue([
		{
			enableCategorization: true,
			enableFriendlyURLCustomization: true,
			isViewOnly: false,
			objectLayout: {
				objectLayoutTabs: [
					{
						objectLayoutBoxes,
					},
				],
			},
		},
		jest.fn(),
	]);
}

function renderHeaderDropdown() {
	const defaultProps = {
		addCategorization: mockAddCategorization,
		addSeo: mockAddSeo,
		deleteElement: mockDeleteElement,
	};

	return render(<HeaderDropdown {...defaultProps} />);
}

const openDropdown = () => {
	const triggerButton = screen.getByRole('button', {
		name: 'more-actions',
	});

	fireEvent.click(triggerButton);
};

describe('HeaderDropdown component', () => {
	beforeEach(() => {
		jest.clearAllMocks();
		mockUseLayoutContext.mockClear();
	});

	it('disables "add-categorization" if it already exists in a tab', () => {
		mockLayoutContextWithBoxes([{type: 'categorization'}]);

		renderHeaderDropdown();

		openDropdown();

		const categorizationButton = screen.getByRole('menuitem', {
			name: 'add-categorization',
		});

		expect(categorizationButton).toBeDisabled();
	});

	it('disables "add-seo" if it already exists in a tab', () => {
		mockLayoutContextWithBoxes([{type: 'seo'}]);

		renderHeaderDropdown();

		openDropdown();

		const addSeoButton = screen.getByRole('menuitem', {
			name: 'add-seo',
		});

		expect(addSeoButton).toBeDisabled();
	});

	it('opens the dropdown and renders all available items', async () => {
		mockLayoutContextWithBoxes([]);

		renderHeaderDropdown();

		openDropdown();

		const categorizationButton = await screen.findByRole('menuitem', {
			name: 'add-categorization',
		});

		const deleteButton = await screen.findByRole('menuitem', {
			name: 'delete',
		});

		const seoButton = await screen.findByRole('menuitem', {
			name: 'add-seo',
		});

		expect(categorizationButton).not.toBeDisabled();

		expect(deleteButton).not.toBeDisabled();

		expect(seoButton).not.toBeDisabled();
	});
});
