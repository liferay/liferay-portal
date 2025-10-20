/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import {ClayDropDownWithItems} from '@clayui/drop-down';
import {SearchForm} from '@liferay/layout-js-components-web';
import {ManagementToolbar} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import React, {useState} from 'react';

import {useSelector, useStateDispatch} from '../contexts/StateContext';
import selectSelection from '../selectors/selectSelection';
import AddChildDropdown from './AddChildDropdown';
import StructureTree from './StructureTree';

export default function () {
	return (
		<div className="border rounded-lg structure-builder__sidebar">
			<h3 className="font-weight-semi-bold pt-4 px-4 text-4">
				{Liferay.Language.get('content-structure-fields')}
			</h3>

			<Content />
		</div>
	);
}

function Content() {
	const [search, setSearch] = useState('');

	return (
		<>
			<Toolbar setSearch={setSearch} />

			<StructureTree search={search} />
		</>
	);
}

function Toolbar({
	setSearch,
}: {
	setSearch: React.Dispatch<React.SetStateAction<string>>;
}) {
	const dispatch = useStateDispatch();
	const selection = useSelector(selectSelection);

	if (selection.length <= 1) {
		return (
			<div className="align-items-center c-gap-2 d-flex px-4">
				<SearchForm
					className="flex-grow-1 my-3"
					label={Liferay.Language.get('search-fields')}
					onChange={setSearch}
					size="sm"
					variant="white"
				/>

				<AddChildDropdown triggerType="icon" />
			</div>
		);
	}

	return (
		<ManagementToolbar.Container
			active
			className="mb-2 mt-3"
			onClick={(event) => event.stopPropagation()}
		>
			{sub(Liferay.Language.get('x-items-selected'), selection.length)}

			<ClayDropDownWithItems
				items={[
					{
						label: Liferay.Language.get('delete'),
						onClick: () => dispatch({type: 'delete-selection'}),
						symbolLeft: 'trash',
					},
					{
						label: Liferay.Language.get('create-repeatable-group'),
						onClick: () => dispatch({type: 'add-repeatable-group'}),
						symbolLeft: 'repeat',
					},
				]}
				trigger={
					<ClayButtonWithIcon
						aria-label={Liferay.Language.get('selection-options')}
						borderless
						displayType="unstyled"
						size="sm"
						symbol="ellipsis-v"
					/>
				}
			/>
		</ManagementToolbar.Container>
	);
}
