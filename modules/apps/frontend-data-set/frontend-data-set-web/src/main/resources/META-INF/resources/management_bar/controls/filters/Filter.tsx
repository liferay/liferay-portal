/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLoadingIndicator from '@clayui/loading-indicator';
import {loadModule} from 'frontend-js-web';
import React, {ReactElement, useContext, useEffect, useState} from 'react';

import FrontendDataSetContext from '../../../FrontendDataSetContext';

// @ts-ignore

import clientExtensionFilterImplementation from './implementation/ClientExtensionFilter';
import {
	dateRangeFilterImplementation,
	dateTimeRangeFilterImplementation,
} from './implementation/DateTimeRangeFilter';
import selectionFilterImplementation from './implementation/SelectionFilter';
import {EEntityFieldType} from './utils/types';

export interface IOdataStringArgs {
	clientExtensionFilterImplementation?: any;
	clientExtensionFilterURL?: string;
	entityFieldType: EEntityFieldType;
	id: string;
	multiple?: boolean;
	selectedData?: Record<string, unknown>;
}

export interface ISelectedItemsLabelArgs {
	clientExtensionFilterImplementation?: any;
	clientExtensionFilterURL?: string;
	selectedData?: Record<string, unknown>;
}

export interface ISelectedItemsPreview {
	hiddenItemsCount: number;
	label: string;
}

export interface FilterImplementation<
	T extends FilterImplementationArgs<unknown>,
> {
	Component: (args: T) => ReactElement;
	getOdataString: (args: IOdataStringArgs) => string;
	getSelectedItemsLabel: (args: ISelectedItemsLabelArgs) => string;
	getSelectedItemsPreview?: (
		args: ISelectedItemsLabelArgs
	) => ISelectedItemsPreview;
}

export interface FilterImplementationArgs<T> {
	active: boolean;
	id: string;
	selectedData: T;
	setFilter: (args: SetFilterArgs) => void;
}

export interface SetFilterArgs {
	active?: boolean;
	id?: string;
	odataFilterString?: string;
	selectedData?: unknown;
}

interface FilterConfiguration {
	id: string;
}

export interface IFilter {
	clientExtensionResolutionError?: string;
	id: string;
	label: string;
	moduleURL?: string;
	onClose: () => void;
	selectedItemsLabel: string;
	type: 'clientExtension' | 'dateRange' | 'dateTimeRange' | 'selection';
}

const FILTER_IMPLEMENTATIONS = {
	clientExtension: clientExtensionFilterImplementation,
	dateRange: dateRangeFilterImplementation,
	dateTimeRange: dateTimeRangeFilterImplementation,
	selection: selectionFilterImplementation,
};

const Filter = ({id, moduleURL, onClose, type, ...otherProps}: IFilter) => {
	const {globalFDSState, onFilterChange} = useContext(FrontendDataSetContext);

	const filterImplementation = FILTER_IMPLEMENTATIONS[type];

	if (!filterImplementation) {
		throw new Error(`Filter type '${type}' not found.`);
	}

	const [Component, setComponent] = useState(
		() => (moduleURL ? null : filterImplementation.Component) as any
	);

	useEffect(() => {
		if (moduleURL) {
			loadModule(moduleURL).then((FetchedComponent: React.Component) =>
				setComponent(() => FetchedComponent)
			);
		}
	}, [moduleURL]);

	const filterId = id;

	const setFilter = ({id, selectedData, ...otherProps}: SetFilterArgs) => {
		if (id !== undefined && id !== filterId) {
			throw new Error(
				`Trying to modify filter ${id} from filter ${filterId}`
			);
		}

		const filter = {
			...globalFDSState.filters.find(
				(filter: FilterConfiguration) => filter.id === filterId
			),
			selectedData,
			...otherProps,
		};

		onFilterChange({
			changedFilter: filter,
		});
	};

	return Component ? (
		<div className="data-set-filter">
			<Component
				id={id}
				onClose={onClose}
				setFilter={setFilter}
				{...otherProps}
			/>
		</div>
	) : (
		<ClayLoadingIndicator size="sm" />
	);
};

export {FILTER_IMPLEMENTATIONS};
export default Filter;
