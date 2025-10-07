/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLoadingIndicator from '@clayui/loading-indicator';
import {loadModule} from 'frontend-js-web';
import React, {ReactElement, useContext, useEffect, useState} from 'react';

import FrontendDataSetContext from '../../../FrontendDataSetContext';
import {activateFilter} from '../../../utils/filters/activateFilter';
import ViewsContext from '../../../views/ViewsContext';

// @ts-ignore

import clientExtensionFilterImplementation from './implementation/ClientExtensionFilter';
import dateRangeFilterImplementation from './implementation/DateRangeFilter';
import selectionFilterImplementation from './implementation/SelectionFilter';

export interface FilterImplementation<
	T extends FilterImplementationArgs<unknown>,
> {
	Component: (args: T) => ReactElement;
	getOdataString: (args: T) => string;
	getSelectedItemsLabel: (args: T) => string;
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

interface FilterComponentArgs {
	id: string;
	moduleURL: string;
	onClose: () => void;
	type: 'clientExtension' | 'dateRange' | 'selection';
}

const FILTER_IMPLEMENTATIONS = {
	clientExtension: clientExtensionFilterImplementation,
	dateRange: dateRangeFilterImplementation,
	selection: selectionFilterImplementation,
};

const Filter = ({
	id,
	moduleURL,
	onClose,
	type,
	...otherProps
}: FilterComponentArgs) => {
	const {setSearching, updateFilters} = useContext(FrontendDataSetContext);
	const [{filters}, viewsDispatch] = useContext(ViewsContext);

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

		const newFilter = {
			...filters.find(
				(filter: FilterConfiguration) => filter.id === filterId
			),
			...otherProps,
		};

		activateFilter({filter: newFilter, selectedData});

		setSearching(true);

		viewsDispatch(
			updateFilters(
				filters.map((filter: FilterConfiguration) =>
					filter.id === filterId ? newFilter : filter
				)
			)
		);
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
