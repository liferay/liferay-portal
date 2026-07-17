/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import {openSelectionModal} from 'frontend-js-components-web';
import * as React from 'react';

import StyleBookConfiguration from '../../../../src/main/resources/META-INF/resources/js/layout/look_and_feel/StyleBookConfiguration';

jest.mock('@liferay/frontend-js-item-selector-web', () => ({
	ItemSelectorModal: ({
		onItemsChange,
		open,
		title,
	}: {
		onItemsChange: (items: object[]) => void;
		open: boolean;
		title: string;
	}) =>
		open ? (
			<div aria-label={title} role="dialog">
				<button
					onClick={() =>
						onItemsChange([
							{
								externalReferenceCode: 'selected-erc',
								name: 'Selected Style Book',
								scope: {
									externalReferenceCode: 'lib-erc',
									label: 'Selected Library',
								},
							},
						])
					}
				>
					select
				</button>
			</div>
		) : null,
}));

jest.mock('frontend-js-components-web', () => ({
	openSelectionModal: jest.fn(),
}));

jest.mock('frontend-js-web', () => ({
	sub: jest.fn((_key: string, ...args: string[]) => args.join(' ')),
}));

const openSelectionModalMock = openSelectionModal as jest.Mock<
	typeof openSelectionModal
>;

const DEFAULT_PROPS = {
	changeStyleBookURL: '/change-style-book',
	isReadOnly: false,
	portletNamespace: 'ns_',
	styleBookEntryDesignLibraryName: null,
	styleBookEntryERC: 'initial-erc',
	styleBookEntryName: 'Initial Style Book',
	styleBookEntryScopeERC: '',
	styleBooksApiURL: '/api/style-books',
};

describe('StyleBookConfiguration', () => {
	beforeEach(() => {
		jest.clearAllMocks();
	});

	it('renders the current style book name', () => {
		render(<StyleBookConfiguration {...DEFAULT_PROPS} />);

		expect(screen.getByRole('textbox', {name: 'style-book'})).toHaveValue(
			'Initial Style Book'
		);
	});

	it('shows the design library label when a design library name is set', () => {
		render(
			<StyleBookConfiguration
				{...DEFAULT_PROPS}
				styleBookEntryDesignLibraryName="My Design Library"
			/>
		);

		expect(screen.getByText('My Design Library')).toBeInTheDocument();
	});

	it('does not show the design library label when no design library name is provided', () => {
		render(<StyleBookConfiguration {...DEFAULT_PROPS} />);

		expect(screen.queryByText('My Design Library')).not.toBeInTheDocument();
	});

	it('does not open a selector when read-only', async () => {
		render(<StyleBookConfiguration {...DEFAULT_PROPS} isReadOnly={true} />);

		await userEvent.click(
			screen.getByRole('textbox', {name: 'style-book'})
		);

		expect(openSelectionModalMock).not.toHaveBeenCalled();
	});

	it('opens the iframe selector when design library is disabled', async () => {
		render(<StyleBookConfiguration {...DEFAULT_PROPS} />);

		await userEvent.click(
			screen.getByRole('textbox', {name: 'style-book'})
		);

		expect(openSelectionModalMock).toHaveBeenCalledWith(
			expect.objectContaining({url: '/change-style-book'})
		);
	});

	it('updates the style book name after iframe selector selection', async () => {
		openSelectionModalMock.mockImplementation(({onSelect}) =>
			onSelect({
				value: JSON.stringify({
					externalReferenceCode: 'new-erc',
					name: 'New Style Book',
				}),
			})
		);

		render(<StyleBookConfiguration {...DEFAULT_PROPS} />);

		await userEvent.click(
			screen.getByRole('textbox', {name: 'style-book'})
		);

		expect(screen.getByRole('textbox', {name: 'style-book'})).toHaveValue(
			'New Style Book'
		);
	});

	it('hides the design library label after selecting a style book without a design library', async () => {
		openSelectionModalMock.mockImplementation(({onSelect}) =>
			onSelect({
				value: JSON.stringify({
					externalReferenceCode: 'new-erc',
					name: 'New Style Book',
				}),
			})
		);

		render(
			<StyleBookConfiguration
				{...DEFAULT_PROPS}
				styleBookEntryDesignLibraryName="Old Library"
			/>
		);

		expect(screen.getByText('Old Library')).toBeInTheDocument();

		await userEvent.click(
			screen.getByRole('textbox', {name: 'style-book'})
		);

		expect(screen.queryByText('Old Library')).not.toBeInTheDocument();
	});

	describe('when design library is enabled', () => {
		beforeEach(() => {
			Liferay.FeatureFlags['LPD-57283'] = true;
		});

		afterEach(() => {
			Liferay.FeatureFlags['LPD-57283'] = false;
		});

		it('opens the ItemSelectorModal', async () => {
			render(<StyleBookConfiguration {...DEFAULT_PROPS} />);

			await userEvent.click(
				screen.getByRole('textbox', {name: 'style-book'})
			);

			expect(
				screen.getByRole('dialog', {name: 'select-style-book'})
			).toBeInTheDocument();
		});

		it('updates the style book name and shows the design library label after ItemSelectorModal selection', async () => {
			render(<StyleBookConfiguration {...DEFAULT_PROPS} />);

			await userEvent.click(
				screen.getByRole('textbox', {name: 'style-book'})
			);

			await userEvent.click(screen.getByRole('button', {name: 'select'}));

			expect(
				screen.getByRole('textbox', {name: 'style-book'})
			).toHaveValue('Selected Style Book');

			expect(screen.getByText('Selected Library')).toBeInTheDocument();
		});
	});
});
