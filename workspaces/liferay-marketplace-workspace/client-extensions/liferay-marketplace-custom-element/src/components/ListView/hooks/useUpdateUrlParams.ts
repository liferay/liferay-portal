/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useMemo} from 'react';
import {useSearchParams} from 'react-router-dom';

type Params = {
	[key: string]: string | number | boolean;
};

const useUpdateUrlParams = () => {
	const [searchParams, setSearchParams] = useSearchParams();

	const page = searchParams.get('page');
	const pageSize = searchParams.get('pageSize');

	const serializedFilter = useMemo(() => {
		return JSON.parse(searchParams.get('filter') as string) || '';
	}, [searchParams]);

	const filterSchemaKey = searchParams.get('filterSchema');

	const updateParams = (param: Params) => {
		setSearchParams(
			new URLSearchParams({
				...(serializedFilter && {
					filter: JSON.stringify(serializedFilter),
					filterSchema: filterSchemaKey,
				}),
				...(page && {page}),
				...(pageSize && {page: 1, pageSize}),
				...param,
			})
		);
	};

	return updateParams;
};

export default useUpdateUrlParams;
