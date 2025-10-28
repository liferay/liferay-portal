/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import {openModal} from 'frontend-js-components-web';
import React from 'react';

import Breadcrumb, {
	ActionDropdownItemProps,
	BreadcrumbItem,
} from '../../../../src/main/resources/META-INF/resources/js/common/components/Breadcrumb';

jest.mock('frontend-js-components-web', () => ({
	openModal: jest.fn(),
}));

const testBreadcrumbItemsLong = [
	{
		active: false,
		href: 'http://www.liferay.com/e/space/123/001',
		label: 'My Space',
	},
	{
		active: false,
		href: 'http://localhost:8080/e/view-folder/123/001',
		label: 'My Folder 1',
	},
	{
		active: false,
		href: 'http://localhost:8080/e/view-folder/123/002',
		label: 'My Folder 2',
	},
	{
		active: true,
		href: 'http://localhost:8080/e/view-folder/123/003',
		label: 'My Folder 3',
	},
];

const testBreadcrumbItemsShort = [
	{
		active: false,
		href: 'http://www.liferay.com/e/space/123/001',
		label: 'My Space',
	},
	{
		active: false,
		href: 'http://localhost:8080/e/view-folder/123/001',
		label: 'My Folder 1',
	},
	{
		active: true,
		href: 'http://localhost:8080/e/view-folder/123/002',
		label: 'My Folder 2',
	},
];

const testBreadcrumbItemsSingle = [
	{
		active: false,
		href: 'http://www.liferay.com/e/space/123/001',
		label: 'My Space',
	},
];

function expectBreadcrumbItem(
	breadcrumbItem: BreadcrumbItem,
	active: boolean = false
) {
	const link = screen.getByRole('link', {name: breadcrumbItem.label});

	if (active) {
		expect(link).toHaveAttribute('href', '#');
		expect(link.closest('li')).toHaveClass('active');
	}
	else {
		expect(link).toHaveAttribute('href', breadcrumbItem.href);
	}
}

function notExpectBreadcrumbItem(breadcrumbItem: BreadcrumbItem) {
	expect(
		screen.queryByRole('link', {name: breadcrumbItem.label})
	).not.toBeInTheDocument();
}

function expectBreadcrumbItemSticker(breadcrumbItem: BreadcrumbItem) {
	expect(
		screen.getByText(breadcrumbItem.label.charAt(0).toUpperCase())
	).toHaveClass('sticker-overlay');
}

describe('Breadcrumb', () => {
	afterEach(() => {
		jest.clearAllMocks();
	});

	it('renders all elements of a short breadcrumb', () => {
		render(<Breadcrumb breadcrumbItems={testBreadcrumbItemsShort} />);

		expectBreadcrumbItemSticker(testBreadcrumbItemsShort[0]);

		expectBreadcrumbItem(testBreadcrumbItemsShort[0]);
		expectBreadcrumbItem(testBreadcrumbItemsShort[1]);
		expectBreadcrumbItem(testBreadcrumbItemsShort[2], true);
	});

	it('renders last two elements of a long breadcrumb', () => {
		render(<Breadcrumb breadcrumbItems={testBreadcrumbItemsLong} />);

		expectBreadcrumbItemSticker(testBreadcrumbItemsLong[0]);

		notExpectBreadcrumbItem(testBreadcrumbItemsLong[0]);
		notExpectBreadcrumbItem(testBreadcrumbItemsLong[1]);

		expectBreadcrumbItem(testBreadcrumbItemsLong[2]);
		expectBreadcrumbItem(testBreadcrumbItemsLong[3], true);
	});

	it('applies the provided props to the SpaceSticker', () => {
		const {container} = render(
			<Breadcrumb
				breadcrumbItems={testBreadcrumbItemsSingle}
				displayType="outline-7"
				size="lg"
			/>
		);

		expectBreadcrumbItemSticker(testBreadcrumbItemsSingle[0]);

		expect(container.getElementsByClassName('sticker')[0]).toHaveClass(
			'sticker-outline-7'
		);

		expect(container.getElementsByClassName('sticker')[0]).toHaveClass(
			'sticker-lg'
		);
	});

	it('renders the provided action item', async () => {
		render(
			<Breadcrumb
				actionItems={[{label: 'Space Settings', symbolLeft: 'cog'}]}
				breadcrumbItems={testBreadcrumbItemsSingle}
			/>
		);

		userEvent.click(screen.getByLabelText('more-actions'));

		await waitFor(() => {
			expect(
				screen.getByRole('menuitem', {name: 'Space Settings'})
			).toBeInTheDocument();
		});

		expect(screen.getByLabelText('more-actions')).toHaveAttribute(
			'aria-expanded',
			'true'
		);
	});

	it('renders custom confirm modal when delete action is clicked', async () => {
		const confirmationMessage =
			'Are you sure you want to delete this space?';
		const confirmationTitle = 'Delete space My Space';

		render(
			<Breadcrumb
				actionItems={[
					{
						confirmationMessage,
						confirmationTitle,
						label: 'Delete',
						target: 'asyncDelete',
					} as ActionDropdownItemProps,
				]}
				breadcrumbItems={testBreadcrumbItemsSingle}
			/>
		);

		await userEvent.click(screen.getByLabelText('more-actions'));

		const deleteItem = await screen.getByRole('menuitem', {name: 'Delete'});
		await userEvent.click(deleteItem);

		expect(openModal).toHaveBeenCalledTimes(1);

		expect(openModal).toHaveBeenCalledWith(
			expect.objectContaining({
				bodyHTML: confirmationMessage,
				status: 'danger',
				title: confirmationTitle,
			})
		);
	});
});
