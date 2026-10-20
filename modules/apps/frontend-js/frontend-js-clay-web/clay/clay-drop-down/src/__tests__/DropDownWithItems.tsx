/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayDropDownWithItems} from '..';
import ClayButton from '@clayui/button';
import {cleanup, fireEvent, render, screen} from '@testing-library/react';
import React from 'react';

const spritemap = 'icons.svg';

describe('ClayDropDownWithItems', () => {
	afterEach(cleanup);

	it('renders a DropDownWithItems', () => {
		render(
			<ClayDropDownWithItems
				items={[
					{
						label: 'clickable',
						onClick: () => {
							alert('you clicked!');
						},
					},
					{
						type: 'divider' as const,
					},
					{
						href: '#',
						label: 'linkable',
					},
				]}
				renderMenuOnClick
				spritemap={spritemap}
				trigger={<ClayButton>Click Me</ClayButton>}
			/>
		);

		expect(document.body).toMatchSnapshot();
	});

	it('renders a DropDownWithItems with radio group', () => {
		render(
			<ClayDropDownWithItems
				items={[
					{
						items: [
							{
								label: 'one',
								type: 'radio' as const,
								value: 'one',
							},
							{
								label: 'two',
								type: 'radio' as const,
								value: 'two',
							},
						],
						label: 'radio',
						name: 'radio',
						type: 'radiogroup' as const,
					},
				]}
				renderMenuOnClick
				spritemap={spritemap}
				trigger={<ClayButton>Click Me</ClayButton>}
			/>
		);

		expect(document.body).toMatchSnapshot();
	});

	it('renders a DropDownWithItems with checkbox', () => {
		render(
			<ClayDropDownWithItems
				items={[
					{
						items: [
							{
								checked: true,
								label: 'checkbox',
								type: 'checkbox' as const,
							},
							{
								checked: false,
								label: 'checkbox 1',
								type: 'checkbox' as const,
							},
						],
						label: 'checkbox',
						type: 'group' as const,
					},
				]}
				renderMenuOnClick
				spritemap={spritemap}
				trigger={<ClayButton>Click Me</ClayButton>}
			/>
		);

		expect(document.body).toMatchSnapshot();
	});

	it('renders a DropDownWithItems with caption', () => {
		render(
			<ClayDropDownWithItems
				caption="Showing 7 of 203 Structures"
				items={[
					{
						href: '#',
						label: 'linkable',
					},
				]}
				renderMenuOnClick
				spritemap={spritemap}
				trigger={<ClayButton>Click Me</ClayButton>}
			/>
		);

		expect(document.body).toMatchSnapshot();
	});

	it('renders a DropDownWithItems with header content before the items', () => {
		render(
			<ClayDropDownWithItems
				defaultActive
				headerContent={<ClayButton>Header</ClayButton>}
				items={[
					{
						href: '#',
						label: 'linkable',
					},
				]}
				spritemap={spritemap}
				trigger={<ClayButton>Click Me</ClayButton>}
			/>
		);

		const header = screen.getByText('Header');
		const item = screen.getByText('linkable');

		expect(
			header.compareDocumentPosition(item) &
				Node.DOCUMENT_POSITION_FOLLOWING
		).toBeTruthy();
	});

	it('renders a DropDownWithItems with footer content', () => {
		render(
			<ClayDropDownWithItems
				footerContent={<ClayButton>Done</ClayButton>}
				items={[
					{
						href: '#',
						label: 'linkable',
					},
				]}
				renderMenuOnClick
				spritemap={spritemap}
				trigger={<ClayButton>Click Me</ClayButton>}
			/>
		);

		expect(document.body).toMatchSnapshot();
	});

	it('renders a DropDownWithItems with help text', () => {
		render(
			<ClayDropDownWithItems
				helpText="Help Text"
				items={[
					{
						href: '#',
						label: 'linkable',
					},
				]}
				renderMenuOnClick
				spritemap={spritemap}
				trigger={<ClayButton>Click Me</ClayButton>}
			/>
		);

		expect(document.body).toMatchSnapshot();
	});

	it('renders a DropDownWithItems with search', () => {
		render(
			<ClayDropDownWithItems
				items={[
					{
						href: '#',
						label: 'linkable',
					},
				]}
				onSearchValueChange={() => {}}
				renderMenuOnClick
				searchValue="Search"
				searchable
				spritemap={spritemap}
				trigger={<ClayButton>Click Me</ClayButton>}
			/>
		);

		expect(document.body).toMatchSnapshot();
	});

	it('renders a DropDownWithItems using title', () => {
		render(
			<ClayDropDownWithItems
				items={[
					{
						href: '#',
						title: 'linkable',
					},
					{
						items: [
							{
								checked: true,
								title: 'checkbox',
								type: 'checkbox' as const,
							},
							{
								checked: false,
								title: 'checkbox 1',
								type: 'checkbox' as const,
							},
						],
						title: 'checkbox',
						type: 'group' as const,
					},
					{
						items: [
							{
								title: 'one',
								type: 'radio' as const,
								value: 'one',
							},
							{
								title: 'two',
								type: 'radio' as const,
								value: 'two',
							},
						],
						name: 'radio',
						title: 'radio',
						type: 'radiogroup' as const,
					},
				]}
				renderMenuOnClick
				spritemap={spritemap}
				trigger={<ClayButton>Click Me</ClayButton>}
			/>
		);

		const toggleButton = document.querySelector('.dropdown-toggle');

		fireEvent.click(toggleButton as HTMLButtonElement, {});

		expect(screen.getByText('linkable')).toBeDefined();
	});

	it('forwards other props to the underlying ClayDropDown container', () => {
		const onMouseEnter = jest.fn();

		render(
			<ClayDropDownWithItems
				data-testid="dropdown"
				items={[{label: 'clickable'}]}
				onMouseEnter={onMouseEnter}
				spritemap={spritemap}
				trigger={<ClayButton>Click Me</ClayButton>}
			/>
		);

		fireEvent.mouseEnter(screen.getByTestId('dropdown'));

		expect(onMouseEnter).toHaveBeenCalledTimes(1);
	});
});
