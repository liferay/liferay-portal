/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import FrontendDataSetContext from '../../../src/main/resources/META-INF/resources/FrontendDataSetContext';
import CreationMenu from '../../../src/main/resources/META-INF/resources/management_bar/controls/CreationMenu';

describe('CreationMenu', () => {
	const renderCreationMenu = (primaryItems) =>
		render(
			<FrontendDataSetContext.Provider value={{loadData: jest.fn()}}>
				<CreationMenu
					inEmptyState={false}
					primaryItems={primaryItems}
				/>
			</FrontendDataSetContext.Provider>
		);

	it('forwards the item className to the dropdown item', async () => {
		renderCreationMenu([
			{className: 'cms-generate-with-ai', label: 'Generate'},
			{label: 'Create Folder'},
		]);

		await userEvent.click(screen.getByTestId('fdsCreationActionButton'));

		expect(screen.getByText('Generate')).toHaveClass(
			'cms-generate-with-ai'
		);
	});

	describe('on desktop', () => {

		// Clay renders contextual items as a drilldown below 768px, and JSDOM
		// reports a body width of 0

		beforeEach(() => {
			Object.defineProperty(document.body, 'clientWidth', {
				configurable: true,
				value: 1440,
			});
		});

		afterEach(() => {
			delete document.body.clientWidth;
		});

		it('opens the items of a contextual action in a submenu', async () => {
			const onClick = jest.fn();

			renderCreationMenu([
				{
					data: {id: 'fragments'},
					items: [
						{label: 'New Basic Fragment', onClick},
						{label: 'New Fragment Set'},
					],
					label: 'Fragments',
					type: 'contextual',
				},
				{label: 'New Style Book'},
			]);

			await userEvent.click(
				screen.getByTestId('fdsCreationActionButton')
			);

			const fragmentsMenuItem = screen.getByRole('menuitem', {
				name: 'Fragments',
			});

			expect(fragmentsMenuItem).toHaveAttribute('aria-expanded', 'false');
			expect(
				screen.queryByRole('menuitem', {name: 'New Basic Fragment'})
			).not.toBeInTheDocument();

			await userEvent.click(fragmentsMenuItem);

			expect(fragmentsMenuItem).toHaveAttribute('aria-expanded', 'true');

			await userEvent.click(
				screen.getByRole('menuitem', {name: 'New Basic Fragment'})
			);

			expect(onClick).toHaveBeenCalledTimes(1);
		});

		it('opens a submenu with the keyboard', async () => {
			renderCreationMenu([
				{
					data: {id: 'fragments'},
					items: [{label: 'New Basic Fragment'}],
					label: 'Fragments',
					type: 'contextual',
				},
				{label: 'New Style Book'},
			]);

			screen.getByTestId('fdsCreationActionButton').focus();

			await userEvent.keyboard('{Enter}');

			const fragmentsMenuItem = screen.getByRole('menuitem', {
				name: 'Fragments',
			});

			fragmentsMenuItem.focus();

			await userEvent.keyboard('{ArrowRight}');

			expect(fragmentsMenuItem).toHaveAttribute('aria-expanded', 'true');
			expect(
				screen.getByRole('menuitem', {name: 'New Basic Fragment'})
			).toHaveFocus();
		});
	});

	it('drills down into the items of a contextual action on mobile', async () => {
		renderCreationMenu([
			{
				data: {id: 'fragments'},
				items: [{label: 'New Basic Fragment'}],
				label: 'Fragments',
				type: 'contextual',
			},
			{label: 'New Style Book'},
		]);

		await userEvent.click(screen.getByTestId('fdsCreationActionButton'));

		await userEvent.click(
			screen.getByRole('menuitem', {name: /Fragments/})
		);

		expect(
			screen.getByRole('menuitem', {name: 'New Basic Fragment'})
		).toBeVisible();
		expect(
			screen.queryByRole('menuitem', {name: 'New Style Book'})
		).not.toBeInTheDocument();
	});

	it('shows the items of a group under its label', async () => {
		const onClick = jest.fn();

		renderCreationMenu([
			{label: 'New Style Book'},
			{
				data: {id: 'fragments'},
				items: [{label: 'New Basic Fragment', onClick}],
				label: 'Fragments',
				separator: true,
				type: 'group',
			},
		]);

		await userEvent.click(screen.getByTestId('fdsCreationActionButton'));

		expect(
			screen.queryByRole('menuitem', {name: 'Fragments'})
		).not.toBeInTheDocument();
		expect(screen.getByText('Fragments')).toBeVisible();
		expect(screen.getByRole('separator')).toBeInTheDocument();

		await userEvent.click(
			screen.getByRole('menuitem', {name: 'New Basic Fragment'})
		);

		expect(onClick).toHaveBeenCalledTimes(1);
	});

	it('shows the only item of a single group as a button', async () => {
		const onClick = jest.fn();

		renderCreationMenu([
			{
				data: {id: 'fragments'},
				items: [{label: 'New Basic Fragment', onClick}],
				label: 'Fragments',
				type: 'contextual',
			},
		]);

		await userEvent.click(
			screen.getByRole('button', {name: 'New Basic Fragment'})
		);

		expect(onClick).toHaveBeenCalledTimes(1);
		expect(screen.queryByText('Fragments')).not.toBeInTheDocument();
	});

	it('does not nest the items of an action of type item', async () => {
		renderCreationMenu([
			{
				items: [{label: 'New Basic Fragment'}],
				label: 'Fragments',
				type: 'item',
			},
			{label: 'New Style Book'},
		]);

		await userEvent.click(screen.getByTestId('fdsCreationActionButton'));

		expect(
			screen.getByRole('menuitem', {name: 'Fragments'})
		).not.toHaveAttribute('aria-haspopup');
		expect(
			screen.queryByText('New Basic Fragment')
		).not.toBeInTheDocument();
	});

	it('forwards the item className to the single creation button', () => {
		renderCreationMenu([
			{className: 'cms-generate-with-ai', label: 'Generate'},
		]);

		expect(screen.getByText('Generate')).toHaveClass(
			'cms-generate-with-ai'
		);
	});
});
