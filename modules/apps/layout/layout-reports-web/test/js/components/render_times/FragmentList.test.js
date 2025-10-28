/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';

import FragmentList from '../../../../src/main/resources/META-INF/resources/js/components/render_times/FragmentList';

const FRAGMENTS = [
	{
		cached: false,
		fragmentCollectionURL: 'url',
		fromMaster: false,
		hierarchy: 'Container',
		isPortlet: false,
		itemId: 'fragment-1',
		itemType: 'fragment',
		name: 'Container',
		renderTime: 1,
		warnings: ['Warning 1', 'Warning 2'],
	},
	{
		cached: true,
		fragmentCollectionURL: 'url',
		fromMaster: false,
		hierarchy: 'Container > Heading',
		isPortlet: true,
		itemId: 'fragment-2',
		itemType: 'fragment',
		name: 'Heading',
		renderTime: 6,
		warnings: [],
	},
	{
		cached: false,
		fragmentCollectionURL: 'url',
		fromMaster: true,
		hierarchy: 'Container > Image',
		isPortlet: true,
		itemId: 'fragment-3',
		itemType: 'fragment',
		name: 'Image',
		renderTime: 3,
		warnings: [],
	},
];

jest.mock('frontend-js-web', () => ({
	...jest.requireActual('frontend-js-web'),
	sub: jest.fn((langKey, args) => langKey.replace('x', args)),
}));

const renderComponent = ({ascendingSort} = {}) =>
	render(
		<FragmentList ascendingSort={ascendingSort} fragments={FRAGMENTS} />
	);

describe('FragmentList', () => {
	it('renders fragment list showing name and render time in each fragment', () => {
		renderComponent();

		FRAGMENTS.forEach(({name, renderTime}) => {
			expect(screen.getByText(name)).toBeInTheDocument();
			expect(screen.getByText(`${renderTime}-ms`)).toBeInTheDocument();
		});
	});

	it('shows the label "cached" if the fragment is cached', () => {
		renderComponent();

		const container = screen.getByText('Container');
		const heading = screen.getByText('Heading');

		expect(
			container.closest('.page-audit__fragment').textContent
		).not.toContain('cached');
		expect(heading.closest('.page-audit__fragment').textContent).toContain(
			'cached'
		);
	});

	it('shows the label "fragment" or "widget" depending the type of the fragment', () => {
		renderComponent();

		const heading = screen.getByText('Heading');
		const container = screen.getByText('Container');

		expect(heading.closest('.page-audit__fragment').textContent).toContain(
			'widget'
		);
		expect(
			container.closest('.page-audit__fragment').textContent
		).toContain('fragment');
	});

	it('shows the label "from master" if the fragment belongs to a master page', () => {
		renderComponent();

		const image = screen.getByText('Image');

		expect(image.closest('.page-audit__fragment').textContent).toContain(
			'from-master'
		);
	});

	it('sorts the fragments list by default in descending order', () => {
		renderComponent();

		const fragments = document.querySelectorAll('.page-audit__fragment');

		expect(fragments[0].textContent).toContain('Heading');
		expect(fragments[1].textContent).toContain('Image');
		expect(fragments[2].textContent).toContain('Container');
	});

	it('sorts the fragment list in ascending order', () => {
		renderComponent({ascendingSort: true});

		const fragments = document.querySelectorAll('.page-audit__fragment');

		expect(fragments[0].textContent).toContain('Container');
		expect(fragments[1].textContent).toContain('Image');
		expect(fragments[2].textContent).toContain('Heading');
	});

	it('shows a badge with the number of warnings', () => {
		renderComponent();

		const fragment = screen.getByText('Container');

		expect(fragment.querySelector('.badge-item')).toHaveTextContent('2');
	});

	it('does not show the badge if the fragment does not have any warning', () => {
		renderComponent();

		const fragment = screen.getByText('Heading');

		expect(fragment.querySelector('.badge-item')).toBeNull();
	});
});
