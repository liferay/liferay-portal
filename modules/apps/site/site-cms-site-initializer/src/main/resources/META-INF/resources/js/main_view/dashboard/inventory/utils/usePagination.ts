/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useCallback, useState} from 'react';

export type PaginationState = {
	deltas: {
		label: number;
	}[];
	page: number;
	pageSize: number;
};

const INITIAL_PAGINATION: PaginationState = {
	deltas: [20, 40, 60].map((pageSize) => ({
		label: pageSize,
	})),
	page: 1,
	pageSize: 20,
};

function usePagination() {
	const [pagination, setPagination] = useState(INITIAL_PAGINATION);

	const handlePageChange = useCallback((newPage: number) => {
		setPagination((prevState) => ({
			...prevState,
			page: newPage,
		}));
	}, []);

	const handleDeltaChange = useCallback((newPageSize: number) => {
		setPagination((prevState) => ({
			...prevState,
			page: 1,
			pageSize: newPageSize,
		}));
	}, []);

	return {handleDeltaChange, handlePageChange, pagination};
}

export default usePagination;
