/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen} from '@testing-library/react';
import React from 'react';

import getAssetEntriesItemSelectorProps from '../../src/main/resources/META-INF/resources/js/getAssetEntriesItemSelectorProps';

const STATUS_FILTER = "(status eq 'approved' or status eq 'scheduled')";

function getAPIURL(assetEntryTypes) {
	const {apiURL} = getAssetEntriesItemSelectorProps({
		assetEntryTypes,
		portletNamespace: 'ns',
	});

	return new URL(apiURL);
}

function getTitleCell() {
	const {fdsProps} = getAssetEntriesItemSelectorProps({
		assetEntryTypes: [
			{classNameId: 1, groupIds: [123], label: 'Web Content'},
		],
		portletNamespace: 'ns',
	});

	const {component: TitleCell} = fdsProps.customRenderers.tableCell.find(
		(contentRenderer) => contentRenderer.name === 'assetEntryTitle'
	);

	return TitleCell;
}

describe('getAssetEntriesItemSelectorProps', () => {
	describe('apiURL', () => {
		it('filters by class name only when every type shares the same groups', () => {
			const url = getAPIURL([
				{classNameId: 10, groupIds: [1, 2], label: 'Blogs Entry'},
				{classNameId: 20, groupIds: [1, 2], label: 'Web Content'},
			]);

			expect(url.searchParams.getAll('groupIds')).toEqual(['1', '2']);
			expect(url.searchParams.get('filter')).toBe(
				`classNameId in (10,20) and ${STATUS_FILTER}`
			);
		});

		it('filters by class name and group per type when the types have different groups', () => {
			const url = getAPIURL([
				{classNameId: 10, groupIds: [1, 2], label: 'Blogs Entry'},
				{classNameId: 20, groupIds: [3, 2], label: 'Web Content'},
				{classNameId: 30, groupIds: [1, 2], label: 'Document'},
			]);

			expect(url.searchParams.getAll('groupIds')).toEqual([
				'1',
				'2',
				'3',
			]);
			expect(url.searchParams.get('filter')).toBe(
				`((classNameId in (10,30) and groupId in (1,2)) or (classNameId eq 20 and groupId in (3,2))) and ${STATUS_FILTER}`
			);
		});
	});

	describe('TitleCell', () => {
		it('renders the not-visible-to-guest icon when the guest role lacks VIEW', () => {
			const TitleCell = getTitleCell();

			render(
				<TitleCell
					itemData={{
						permissions: [
							{actionIds: ['UPDATE'], roleName: 'Guest'},
						],
					}}
					value="Title"
				/>
			);

			expect(
				screen.getByLabelText('not-visible-to-guest-users')
			).toBeInTheDocument();
		});

		it('does not render the icon when the guest role has VIEW', () => {
			const TitleCell = getTitleCell();

			render(
				<TitleCell
					itemData={{
						permissions: [{actionIds: ['VIEW'], roleName: 'Guest'}],
					}}
					value="Title"
				/>
			);

			expect(
				screen.queryByLabelText('not-visible-to-guest-users')
			).not.toBeInTheDocument();
		});

		it('renders the icon when the guest role is absent from permissions', () => {
			const TitleCell = getTitleCell();

			render(
				<TitleCell
					itemData={{
						permissions: [{actionIds: ['VIEW'], roleName: 'Owner'}],
					}}
					value="Title"
				/>
			);

			expect(
				screen.getByLabelText('not-visible-to-guest-users')
			).toBeInTheDocument();
		});
	});
});
