/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

// The "@liferay/frontend-data-set-web/api" import-map module is resolved at
// runtime by the portal. At build time, tsconfig "paths" redirects it to the
// types provided by "@liferay/js-api", so the value and its types come from a
// single import.

// This element declares that it owns the filtering when it connects, so the
// data set knows what to stop offering: taking the filtering over drops its
// filters dropdown and its filter chips, and disconnecting hands them back.
// Driving the search of a data set is what
// "liferay-sample-custom-element-7" shows.

// The whole filter UI is one value, and the data set remembers it: it is
// passed along with the expressions it produced, kept in the page URL, and
// handed back on the next visit, so that a filtered data set can be shared
// as a link and survives a reload or the back button. Restoring is therefore
// the same operation as filtering, which is why there is one function below
// that applies a filter state and nothing that applies one a second way.

import {
	FDSConnection,
	FDSConnectionInfo,
} from '@liferay/frontend-data-set-web/api';
import React, {useEffect, useRef, useState} from 'react';

import AppliedFilters from './AppliedFilters';
import FilterPanels from './FilterPanels';
import {
	EMPTY_FILTER_STATE,
	FilterDefinition,
	FilterState,
	Selections,
	getConnectionState,
	getFilters,
	getValidFilterState,
	toggleOption,
} from './filters';

interface AppProps {
	fdsName: string;
}

function App({fdsName}: AppProps) {
	const [filterState, setFilterState] =
		useState<FilterState>(EMPTY_FILTER_STATE);
	const [ready, setReady] = useState(false);
	const [typedExpression, setTypedExpression] = useState('');
	const fdsConnectionRef = useRef<FDSConnection | null>(null);

	const applyFilterState = (filterState: FilterState) => {
		setFilterState(filterState);

		fdsConnectionRef.current?.setFilters(
			getFilters(filterState),
			getConnectionState(filterState)
		);
	};

	useEffect(() => {
		fdsConnectionRef.current = new FDSConnection(
			fdsName,
			{
				restore: (connectionState: unknown) =>
					applyFilterState(getValidFilterState(connectionState)),
				search: () => {},
			},
			(fdsConnectionInfo: FDSConnectionInfo) => {
				setReady(fdsConnectionInfo.status === 'ready');
			},
			{owns: ['filters']}
		);

		return () => {
			if (fdsConnectionRef?.current) {
				fdsConnectionRef?.current.disconnect();
				fdsConnectionRef.current = null;
			}
		};
	}, [fdsName]);

	// The applied expression is what a link carries; the input is where the
	// next one is typed. The two part company while the user types and meet
	// again whenever an expression is applied or restored.

	useEffect(() => {
		setTypedExpression(filterState.expression);
	}, [filterState.expression]);

	const {manual, selections} = filterState;

	const setSelections = (selections: Selections) =>
		applyFilterState({...filterState, selections});

	const handleSwapMode = () =>
		applyFilterState({...EMPTY_FILTER_STATE, manual: !manual});

	return (
		<div className="p-3">
			<div className="align-items-center d-flex justify-content-between mb-3">
				<h4 className="h5 mb-0">Filters</h4>

				<button
					className="btn btn-unstyled link"
					disabled={!ready}
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
						disabled={!ready}
						onChange={(event) =>
							setTypedExpression(event.target.value)
						}
						placeholder="Filter with OData, such as name eq 'Liferay'"
						style={{minWidth: 0}}
						type="text"
						value={typedExpression}
					/>

					<button
						className="btn btn-primary flex-shrink-0"
						disabled={!ready || !typedExpression.trim()}
						onClick={() =>
							applyFilterState({
								...filterState,
								expression: typedExpression.trim(),
							})
						}
						type="button"
					>
						Apply
					</button>
				</div>
			) : (
				<>
					<AppliedFilters
						onClearAll={() => setSelections({})}
						onClearFilter={(filterId: string) =>
							setSelections({...selections, [filterId]: []})
						}
						selections={selections}
					/>

					<FilterPanels
						disabled={!ready}
						onToggleOption={(
							filterDefinition: FilterDefinition,
							value: string
						) =>
							setSelections(
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
