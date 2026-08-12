/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Text} from '@clayui/core';
import React from 'react';

import {TEmptyState} from '../table/StateRenderer';
import Table from '../table/Table';
import {TColumn, TTableRequestParams} from '../table/types';
import {getIds} from '../table/utils';
import {TProperty} from './Properties';

export type TRawItem = {
	channelName?: string;
	friendlyURL?: string;
	id: string;
	name: string;
	siteName: string;
};

interface ITabProps {
	columns: Array<keyof TRawItem>;
	description?: string;
	emptyState: TEmptyState;
	header: TColumn[];
	initialIds: number[];
	onItemsChange: (ids: number[]) => void;
	property: TProperty;
	requestFn: (params: TTableRequestParams) => Promise<any>;
	type: string;
}

const Tab: React.FC<{children?: React.ReactNode | undefined} & ITabProps> = ({
	columns,
	description,
	emptyState,
	header,
	initialIds,
	onItemsChange,
	property,
	requestFn,
	type,
}) => (
	<>
		{description && (
			<div className="my-3 text-secondary">
				<Text size={3}>{description}</Text>
			</div>
		)}

		<Table<TRawItem>
			columns={header}
			emptyState={emptyState}
			mapperItems={(items) => {
				return items.map((item) => ({
					checked: !!(
						item.channelName && item.channelName === property.name
					),
					columns: columns.map((column) => {
						const value = item?.[column] ?? '';

						return {
							id: column,
							value: decodeURIComponent(value),
						};
					}),
					disabled: !!(
						item.channelName && item.channelName !== property.name
					),
					id: item.id,
				}));
			}}
			onItemsChange={(items) => onItemsChange(getIds(items, initialIds))}
			requestFn={requestFn}
			type={type}
		/>
	</>
);

export default Tab;
