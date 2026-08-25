/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

// The "@liferay/frontend-data-set-web/api" import-map module is resolved at
// runtime by the portal. At build time, tsconfig "paths" redirects it to the
// types provided by "@liferay/js-api", so the value and its types come from a
// single import.

// This element declares what it owns when it connects, so the data set knows
// what to stop offering: taking the filtering over drops its filters dropdown
// and its filter chips, and disconnecting hands them back. A connection that
// left "filters" out would drive the search alone and the data set would keep
// filtering as it always has.

import {
	FDSConnection,
	FDSConnectionInfo,
	FDSConnectionStatus,
} from '@liferay/frontend-data-set-web/api';
import React, {useEffect, useRef, useState} from 'react';

import AppliedFilters from './AppliedFilters';
import FilterPanels from './FilterPanels';
import {
	FILTERS,
	FilterDefinition,
	Selections,
	getOdataFilterString,
	getSelectedValues,
	toggleOption,
} from './filters';

interface AppProps {
	fdsName: string;
}

const PLACEHOLDERS: Record<FDSConnectionStatus, string> = {
	connecting: 'waiting',
	disconnected: 'Search is not available',
	ready: 'Type search query...',
	timeout: 'Search is not available',
};

function App({fdsName}: AppProps) {
	const [disabled, setDisabled] = useState<boolean>(true);
	const [expression, setExpression] = useState('');
	const [manual, setManual] = useState(false);
	const [placeholder, setPlaceholder] = useState<string>(
		PLACEHOLDERS.connecting
	);
	const [query, setQuery] = useState('');
	const [selections, setSelections] = useState<Selections>({});
	const fdsConnectionRef = useRef<FDSConnection | null>(null);

	useEffect(() => {
		fdsConnectionRef.current = new FDSConnection(
			fdsName,
			{
				search: (query: string) => {
					setQuery(query);
				},
			},
			(fdsConnectionInfo: FDSConnectionInfo) => {
				setPlaceholder(PLACEHOLDERS[fdsConnectionInfo.status]);
				setDisabled(fdsConnectionInfo.status !== 'ready');
			},
			{owns: ['filters', 'search']}
		);

		return () => {
			if (fdsConnectionRef?.current) {
				fdsConnectionRef?.current.disconnect();
				fdsConnectionRef.current = null;
			}
		};
	}, [fdsName]);

	const applySelections = (selections: Selections) => {
		setSelections(selections);

		fdsConnectionRef.current?.setFilters(
			FILTERS.map((filterDefinition) => ({
				id: filterDefinition.id,
				odataFilterString: getOdataFilterString(
					filterDefinition,
					getSelectedValues(selections, filterDefinition.id)
				),
			}))
		);
	};

	const handleSearch = () => {
		fdsConnectionRef.current?.setSearch(query);
	};

	// Only one of the two ways of filtering is on screen at a time, so
	// leaving one behind applied would filter the data set by something the
	// user can no longer see, let alone undo.

	const handleSwapMode = () => {
		setExpression('');
		setSelections({});
		setManual((manual) => !manual);

		fdsConnectionRef.current?.clearFilters();
	};

	return (
		<div className="p-3">
			<h4 className="h5">Search</h4>

			<div className="d-flex" style={{gap: '0.5rem'}}>
				<input
					aria-label="Search query"
					className="form-control"
					disabled={disabled}
					onChange={(event) => setQuery(event.target.value)}
					onKeyDown={(event) => {
						if (event.key === 'Enter') {
							handleSearch();
						}
					}}
					placeholder={placeholder}
					style={{minWidth: 0}}
					type="text"
					value={query}
				/>

				<button
					className="btn btn-primary flex-shrink-0"
					disabled={disabled}
					onClick={handleSearch}
					type="button"
				>
					Search
				</button>
			</div>

			<hr className="my-4" />

			<div className="align-items-center d-flex justify-content-between mb-3">
				<h4 className="h5 mb-0">Filters</h4>

				<button
					className="btn btn-unstyled link"
					disabled={disabled}
					onClick={handleSwapMode}
					type="button"
				>
					{manual ? 'Choose from the options' : 'Filter manually'}
				</button>
			</div>

			{manual ? (
				<div className="d-flex" style={{gap: '0.5rem'}}>
					<input
						aria-label="OData filter expression"
						className="form-control"
						disabled={disabled}
						onChange={(event) => setExpression(event.target.value)}
						placeholder="Filter with OData, such as name eq 'Liferay'"
						style={{minWidth: 0}}
						type="text"
						value={expression}
					/>

					<button
						className="btn btn-primary flex-shrink-0"
						disabled={disabled || !expression.trim()}
						onClick={() =>
							fdsConnectionRef.current?.setFilters([
								{
									id: 'manual',
									odataFilterString: expression.trim(),
								},
							])
						}
						type="button"
					>
						Apply
					</button>
				</div>
			) : (
				<>
					<AppliedFilters
						onClearAll={() => applySelections({})}
						onClearFilter={(filterId: string) =>
							applySelections({...selections, [filterId]: []})
						}
						selections={selections}
					/>

					<FilterPanels
						disabled={disabled}
						onToggleOption={(
							filterDefinition: FilterDefinition,
							value: string
						) =>
							applySelections(
								toggleOption(
									selections,
									filterDefinition,
									value
								)
							)
						}
						selections={selections}
					/>
				</>
			)}
		</div>
	);
}

export default App;
