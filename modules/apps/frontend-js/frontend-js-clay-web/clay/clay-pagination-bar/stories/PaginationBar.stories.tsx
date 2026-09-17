/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import {ClayPaginationWithBasicItems} from '@clayui/pagination';
import React, {useState} from 'react';

import ClayPaginationBar, {ClayPaginationBarWithBasicItems} from '../src';

export default {
	component: ClayPaginationBar,
	title: 'Design System/Components/PaginationBar',
};
export function Default() {
	return (
		<ClayPaginationBar>
			<ClayPaginationBar.DropDown
				items={[
					{
						label: '10',
						onClick: () => {},
					},
				]}
				trigger={
					<ClayButton displayType="unstyled">
						10 items per page
						<ClayIcon symbol="caret-double-l" />
					</ClayButton>
				}
			/>

			<ClayPaginationBar.Results>
				Showing a handful of items...
			</ClayPaginationBar.Results>

			<ClayPaginationWithBasicItems
				activePage={1}
				ellipsisProps={{'aria-label': 'More', 'title': 'More'}}
				onPageChange={() => {}}
				totalPages={10}
			/>
		</ClayPaginationBar>
	);
}
export function WithItems(args: any) {
	const [delta, setDelta] = useState<number>(5);

	const deltas = [
		{
			href: '#1',
			label: 1,
		},
		{
			label: 2,
		},
		{
			href: '#3',
			label: 3,
		},
		{
			label: 4,
		},
		{
			label: 5,
		},
	];

	return (
		<ClayPaginationBarWithBasicItems
			activeDelta={delta}
			deltas={deltas}
			ellipsisBuffer={args.ellipsisBuffer}
			ellipsisProps={{'aria-label': 'More', 'title': 'More'}}
			onDeltaChange={setDelta}
			totalItems={args.totalItems}
		/>
	);
}

WithItems.args = {
	ellipsisBuffer: 3,
	totalItems: 21,
};
export function WithoutDropdown() {
	const [delta, setDelta] = useState<number>(5);

	return (
		<ClayPaginationBarWithBasicItems
			activeDelta={delta}
			defaultActive={3}
			ellipsisBuffer={3}
			ellipsisProps={{'aria-label': 'More', 'title': 'More'}}
			onDeltaChange={setDelta}
			showDeltasDropDown={false}
			totalItems={21}
		/>
	);
}
export function WithCustomAriaLabels() {
	const [delta, setDelta] = useState<number>(5);

	return (
		<ClayPaginationBarWithBasicItems
			activeDelta={delta}
			ellipsisBuffer={2}
			labels={{
				ellipsisAriaLabel: 'Mostra le pagine da {0} a {1}',
				itemsPerPagePickerAriaLabel: 'Elementi per pagina',
				nextPageAriaLabel: 'Vai alla pagina successiva, {0}',
				pageLinkAriaLabel: 'Vai alla pagina, {0}',
				paginationResults: 'Da {0} a {1} di {2}',
				perPageItems: '{0} elementi',
				previousPageAriaLabel: 'Vai alla pagina precedente, {0}',
				selectPerPageItems: '{0} elementi',
			}}
			onDeltaChange={setDelta}
			totalItems={100}
		/>
	);
}
