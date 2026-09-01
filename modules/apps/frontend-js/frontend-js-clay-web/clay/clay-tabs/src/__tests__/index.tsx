/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import ClayTabs from '..';
import {cleanup, fireEvent, render} from '@testing-library/react';
import React from 'react';

function ClayTabsWithItems() {
	const [active, setActive] = React.useState<number>(0);

	return (
		<>
			<ClayTabs active={active} onActiveChange={setActive}>
				<ClayTabs.Item>Dummy1</ClayTabs.Item>

				<ClayTabs.Item data-testid="tabItem2">Dummy2</ClayTabs.Item>

				<ClayTabs.Item>Dummy3</ClayTabs.Item>
			</ClayTabs>
			<ClayTabs.Content activeIndex={active}>
				<ClayTabs.TabPane data-testid="tabPane1">
					Tab Content 1
				</ClayTabs.TabPane>

				<ClayTabs.TabPane data-testid="tabPane2">
					Tab Content 2
				</ClayTabs.TabPane>

				<ClayTabs.TabPane>Tab Content 3</ClayTabs.TabPane>
			</ClayTabs.Content>
		</>
	);
}

describe('ClayTabs', () => {
	afterEach(cleanup);

	it('renders', () => {
		const {container} = render(<ClayTabs />);

		expect(container).toMatchSnapshot();
	});

	it('renders justifying nav items', () => {
		const {container} = render(<ClayTabs justified />);

		expect(container.querySelector('.nav.nav-justified')).toBeTruthy();
	});

	it('renders with items', () => {
		const {container} = render(<ClayTabsWithItems />);

		expect(container).toMatchSnapshot();
	});

	it('renders nav items as anchors', () => {
		const {getAllByTestId} = render(
			<>
				<ClayTabs>
					<ClayTabs.Item active href="https://clay.dev/foo">
						One
					</ClayTabs.Item>

					<ClayTabs.Item href="https://clay.dev/bar">
						Two
					</ClayTabs.Item>
				</ClayTabs>
				<ClayTabs.Content activeIndex={1}>
					<ClayTabs.TabPane>Content One</ClayTabs.TabPane>

					<ClayTabs.TabPane>Content Two</ClayTabs.TabPane>
				</ClayTabs.Content>
			</>
		);

		const tabItems = getAllByTestId('tabItem');

		expect(tabItems[0]!.nodeName).toBe('A');
		expect(tabItems[1]!.nodeName).toBe('A');
	});

	it('renders disabled nav items', () => {
		const {getAllByTestId} = render(
			<>
				<ClayTabs>
					<ClayTabs.Item active>One</ClayTabs.Item>

					<ClayTabs.Item>Two</ClayTabs.Item>

					<ClayTabs.Item disabled>Three</ClayTabs.Item>
				</ClayTabs>
				<ClayTabs.Content activeIndex={1}>
					<ClayTabs.TabPane>Content One</ClayTabs.TabPane>

					<ClayTabs.TabPane>Content Two</ClayTabs.TabPane>

					<ClayTabs.TabPane>Content Three</ClayTabs.TabPane>
				</ClayTabs.Content>
			</>
		);

		const tabItems = getAllByTestId('tabItem');

		expect(tabItems[2]!.classList).toContain('disabled');
		expect(tabItems[2]!.attributes.getNamedItem('disabled')).toBeTruthy();
	});

	it('emits a number when clicking an item', () => {
		const onClick = jest.fn();

		const {getAllByTestId} = render(
			<>
				<ClayTabs>
					<ClayTabs.Item active onClick={onClick}>
						One
					</ClayTabs.Item>

					<ClayTabs.Item onClick={onClick}>Two</ClayTabs.Item>
				</ClayTabs>
				<ClayTabs.Content activeIndex={1}>
					<ClayTabs.TabPane>Content One</ClayTabs.TabPane>

					<ClayTabs.TabPane>Content Two</ClayTabs.TabPane>
				</ClayTabs.Content>
			</>
		);

		const tabItems = getAllByTestId('tabItem');

		fireEvent.click(tabItems[0]!);
		expect(onClick).toBeCalled();

		fireEvent.click(tabItems[1]!);
		expect(onClick).toBeCalled();
	});

	it('renders elements not valid tabs should continue to work', () => {
		const {getAllByRole} = render(
			<>
				<ClayTabs>
					{false && <ClayTabs.Item active>One</ClayTabs.Item>}

					<ClayTabs.Item>Two</ClayTabs.Item>
				</ClayTabs>
				<ClayTabs.Content activeIndex={1}>
					{false && <ClayTabs.TabPane>Content One</ClayTabs.TabPane>}

					<ClayTabs.TabPane>Content Two</ClayTabs.TabPane>
				</ClayTabs.Content>
			</>
		);

		const tabItems = getAllByRole('tab');
		const tabPanels = getAllByRole('tabpanel');

		expect(tabItems[0]!.innerHTML).toBe('Two');
		expect(tabItems.length).toBe(1);

		expect(tabPanels[0]!.innerHTML).toBe('Content Two');
		expect(tabPanels.length).toBe(1);
	});

	it('renders the new default composition', () => {
		const {getAllByRole} = render(
			<ClayTabs>
				<ClayTabs.List>
					<ClayTabs.Item>Tab 1</ClayTabs.Item>

					<ClayTabs.Item>Tab 2</ClayTabs.Item>

					<ClayTabs.Item>Tab 3</ClayTabs.Item>
				</ClayTabs.List>

				<ClayTabs.Panels>
					<ClayTabs.TabPanel>Tab Content 1</ClayTabs.TabPanel>

					<ClayTabs.TabPanel>Tab Content 2</ClayTabs.TabPanel>

					<ClayTabs.TabPanel>Tab Content 3</ClayTabs.TabPanel>
				</ClayTabs.Panels>
			</ClayTabs>
		);

		expect(getAllByRole('tab').length).toBe(3);
		expect(getAllByRole('tabpanel').length).toBe(3);
	});

	it('renders the tab item active when `active` is set on uncontrolled state', () => {
		const {getByRole} = render(
			<ClayTabs>
				<ClayTabs.List>
					<ClayTabs.Item>Tab 1</ClayTabs.Item>

					<ClayTabs.Item active>Tab 2</ClayTabs.Item>

					<ClayTabs.Item>Tab 3</ClayTabs.Item>
				</ClayTabs.List>

				<ClayTabs.Panels>
					<ClayTabs.TabPanel>Tab Content 1</ClayTabs.TabPanel>

					<ClayTabs.TabPanel>Tab Content 2</ClayTabs.TabPanel>

					<ClayTabs.TabPanel>Tab Content 3</ClayTabs.TabPanel>
				</ClayTabs.Panels>
			</ClayTabs>
		);

		const activeTab = getByRole('tab', {selected: true});

		expect(activeTab.innerHTML).toBe('Tab 2');
	});

	it('renders the className passed through innerProps', () => {
		const {getByRole} = render(
			<ClayTabs>
				<ClayTabs.Item innerProps={{className: 'my-class'}}>
					Tab 1
				</ClayTabs.Item>
			</ClayTabs>
		);

		const tabItem = getByRole('tab');

		expect(tabItem.classList).toContain('nav-link');
		expect(tabItem.classList).toContain('my-class');
	});

	it('renders an item with an icon and its label out of sight', () => {
		const {getByRole} = render(
			<ClayTabs>
				<ClayTabs.ItemWithIcon label="Details" symbol="info-circle" />
			</ClayTabs>
		);

		const tabItem = getByRole('tab', {name: 'Details'});

		expect(tabItem).toHaveAttribute('title', 'Details');

		expect(tabItem.querySelector('.lexicon-icon-info-circle')).toBeTruthy();

		expect(tabItem.querySelector('.sr-only')!.innerHTML).toBe('Details');
	});

	it('renders items with an icon in the new composition', () => {
		const {getByRole} = render(
			<ClayTabs>
				<ClayTabs.List>
					<ClayTabs.ItemWithIcon
						label="Details"
						symbol="info-circle"
					/>

					<ClayTabs.ItemWithIcon label="Comments" symbol="comments" />
				</ClayTabs.List>

				<ClayTabs.Panels>
					<ClayTabs.TabPanel>Details Content</ClayTabs.TabPanel>

					<ClayTabs.TabPanel>Comments Content</ClayTabs.TabPanel>
				</ClayTabs.Panels>
			</ClayTabs>
		);

		expect(
			getByRole('tab', {name: 'Comments'}).querySelector(
				'.lexicon-icon-comments'
			)
		).toBeTruthy();
	});
});
