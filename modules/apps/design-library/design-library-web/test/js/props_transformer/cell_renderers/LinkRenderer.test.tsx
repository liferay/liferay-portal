/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import React from 'react';

import LinkRenderer from '../../../../src/main/resources/META-INF/resources/js/props_transformer/cell_renderers/LinkRenderer';

const FRAGMENT_COLLECTION_CLASS_NAME =
	'com.liferay.fragment.model.FragmentCollection';
const STYLE_BOOK_ENTRY_CLASS_NAME =
	'com.liferay.style.book.model.StyleBookEntry';

const _renderLinkRenderer = ({
	actions,
	itemData,
}: {
	actions: any;
	itemData: any;
}) =>
	render(
		<LinkRenderer
			actions={actions as any}
			itemData={itemData as any}
			options={{actionId: 'edit'}}
			stickerClassName=""
			symbol="book"
			value={itemData?.name ?? ''}
		/>
	);

describe('LinkRenderer', () => {
	it('renders a link when an action matches the actionId', () => {
		_renderLinkRenderer({
			actions: [
				{
					data: {id: 'edit'},
					href: '/fragment',
					label: 'edit',
				},
			],
			itemData: {
				entryClassName: FRAGMENT_COLLECTION_CLASS_NAME,
				name: 'Set 1',
			},
		});

		expect(screen.getByRole('link', {name: 'Set 1'})).toHaveAttribute(
			'href',
			'/fragment'
		);
	});

	it('renders plain text when the action visibilityFilter does not match the entryClassName', () => {
		_renderLinkRenderer({
			actions: [
				{
					data: {
						id: 'edit',
						visibilityFilters: {
							entryClassName: STYLE_BOOK_ENTRY_CLASS_NAME,
						},
					},
					href: '/style-book',
					label: 'edit',
				},
			],
			itemData: {
				entryClassName: FRAGMENT_COLLECTION_CLASS_NAME,
				name: 'Set 1',
			},
		});

		expect(screen.queryByRole('link')).not.toBeInTheDocument();
		expect(screen.getByText('Set 1')).toBeInTheDocument();
	});

	it('renders plain text when no action matches the actionId', () => {
		_renderLinkRenderer({
			actions: [
				{
					data: {id: 'delete'},
					href: '/other',
					label: 'delete',
				},
			],
			itemData: {
				entryClassName: FRAGMENT_COLLECTION_CLASS_NAME,
				name: 'Set 1',
			},
		});

		expect(screen.queryByRole('link')).not.toBeInTheDocument();
		expect(screen.getByText('Set 1')).toBeInTheDocument();
	});

	it('decorates the link href with backURL and redirect when the action target is a portlet link', () => {
		_renderLinkRenderer({
			actions: [
				{
					data: {id: 'edit'},
					href: '/o/fragment?p_p_id=fragment_portlet',
					label: 'edit',
					target: 'link',
				},
			],
			itemData: {
				entryClassName: FRAGMENT_COLLECTION_CLASS_NAME,
				name: 'Set 1',
			},
		});

		const href = screen
			.getByRole('link', {name: 'Set 1'})
			.getAttribute('href');

		expect(href).toContain('_fragment_portlet_backURL=');
		expect(href).toContain('_fragment_portlet_redirect=');
	});
});
