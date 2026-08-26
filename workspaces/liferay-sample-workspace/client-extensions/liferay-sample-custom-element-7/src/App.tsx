/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

// The "@liferay/frontend-data-set-web/api" import-map module is resolved at
// runtime by the portal. At build time, tsconfig "paths" redirects it to the
// types provided by "@liferay/js-api", so the value and its types come from a
// single import.

// This element drives the search of the data set it connects to and owns
// nothing else, which is what a connection owns when it declares nothing: the
// data set keeps filtering as it always has, and keeps its own search box,
// which stays in sync with the one here. Taking the filtering over is what
// "liferay-sample-custom-element-8" shows.

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
			}
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

	return (
		<div className="d-flex p-3" style={{gap: '0.5rem'}}>
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
	);
}

export default App;
