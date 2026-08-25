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
	const [placeholder, setPlaceholder] = useState<string>(
		PLACEHOLDERS.connecting
	);
	const [query, setQuery] = useState('');
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

	const handleSearch = () => {
		fdsConnectionRef.current?.setSearch(query);
	};

	const handleApplyFilter = () => {
		fdsConnectionRef.current?.setFilters([
			{id: 'custom', odataFilterString: expression.trim()},
		]);
	};

	const handleClearFilters = () => {
		setExpression('');

		fdsConnectionRef.current?.clearFilters();
	};

	return (
		<div style={{display: 'grid', gap: '1rem', padding: '1rem'}}>
			<div style={{display: 'flex', gap: '0.5rem'}}>
				<input
					className="form-control"
					disabled={disabled}
					onChange={(event) => setQuery(event.target.value)}
					onKeyDown={(event) => {
						if (event.key === 'Enter') {
							handleSearch();
						}
					}}
					placeholder={placeholder}
					style={{flex: 1}}
					type="text"
					value={query}
				/>

				<button
					className="btn btn-primary"
					disabled={disabled}
					onClick={handleSearch}
					type="button"
				>
					Search
				</button>
			</div>

			<div style={{display: 'flex', gap: '0.5rem'}}>
				<input
					className="form-control"
					disabled={disabled}
					onChange={(event) => setExpression(event.target.value)}
					placeholder="Filter with OData, such as name eq 'Liferay'"
					style={{flex: 1}}
					type="text"
					value={expression}
				/>

				<button
					className="btn btn-primary"
					disabled={disabled || !expression.trim()}
					onClick={handleApplyFilter}
					type="button"
				>
					Apply filter
				</button>

				<button
					className="btn btn-secondary"
					disabled={disabled}
					onClick={handleClearFilters}
					type="button"
				>
					Clear filters
				</button>
			</div>
		</div>
	);
}

export default App;
