/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayManagementToolbar from '@clayui/management-toolbar';
import {ReactElement, useContext} from 'react';

import {
	FilterSchemaOption,
	filterSchema as filterSchemas,
} from '../../../schema/filters';
import {ListViewContext} from '../hooks/ListViewContext';
import ManagementToolbarFilter from './ManagementToolbarFilters/ManagementToolbarFilters';
import ManagementToolbarResultsBar from './ManagementToolbarResultsBar/ManagementToolbarResultsBar';
import ManagementToolbarSearch from './ManagementToolbarSearch';

export type ManagementToolbarProps = {
	actionButton?: (
		filter: {
			[key: string]: string;
		},
		filterSchema?: FilterSchemaOption
	) => ReactElement;

	filterSchema?: FilterSchemaOption;
	searchVisible?: boolean;
	totalItems: number;
};

const ManagementToolbar: React.FC<ManagementToolbarProps> = ({
	actionButton,
	filterSchema,
	searchVisible = false,
	totalItems,
}) => {
	const [{filters}] = useContext(ListViewContext);

	return (
		<>
			<ClayManagementToolbar>
				<div className="d-flex justify-content-between w-100">
					{filterSchema && (
						<ManagementToolbarFilter
							filterSchema={
								(filterSchemas as any)[filterSchema ?? '']
							}
						/>
					)}

					{!!searchVisible && (
						<div className="d-flex w-100">
							<ManagementToolbarSearch />
							{actionButton &&
								actionButton(filters.filter, filterSchema)}
						</div>
					)}
				</div>

				{!!filters.entries?.filter(({value}) => value).length && (
					<ManagementToolbarResultsBar totalItems={totalItems} />
				)}
			</ClayManagementToolbar>
		</>
	);
};

export default ManagementToolbar;
