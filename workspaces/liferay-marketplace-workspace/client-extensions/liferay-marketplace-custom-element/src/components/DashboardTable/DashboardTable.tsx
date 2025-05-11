/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import ClayTable from '@clayui/table';
import React, {ReactNode} from 'react';

import {DashboardEmptyTable} from './DashboardEmptyTable';

import './DashboardTable.scss';

export type TableHeaders = {
	iconSymbol?: string;
	style?: {width: string};
	title: string;
}[];

interface DashboardTableProps<T> {
	children?: (item: T) => ReactNode;
	emptyStateMessage: {
		className?: string;
		description1?: string;
		description2?: string;
		title: string;
	};
	icon: string;
	items?: T[];
	tableHeaders?: TableHeaders;
}

export function DashboardTable<T>({
	children = () => null,
	emptyStateMessage,
	icon = 'grid',
	items = [],
	tableHeaders = [],
}: DashboardTableProps<T>) {
	if (items.length) {
		return (
			<ClayTable borderless className="dashboard-table-container">
				<ClayTable.Head>
					{tableHeaders.map(({iconSymbol, style, title}) => (
						<ClayTable.Cell headingCell key={title} style={style}>
							<div className="dashboard-table-header-name">
								<span className="dashboard-table-header-text">
									{title}
								</span>

								{iconSymbol && <ClayIcon symbol={iconSymbol} />}
							</div>
						</ClayTable.Cell>
					))}
				</ClayTable.Head>

				<ClayTable.Body>
					{items.map((item, index) => (
						<React.Fragment key={index}>
							{children(item)}
						</React.Fragment>
					))}
				</ClayTable.Body>
			</ClayTable>
		);
	}

	const {description1, description2, title} = emptyStateMessage;

	return (
		<DashboardEmptyTable
			className={emptyStateMessage?.className}
			description1={description1 ?? ''}
			description2={description2 ?? ''}
			icon={icon}
			title={title}
		/>
	);
}
