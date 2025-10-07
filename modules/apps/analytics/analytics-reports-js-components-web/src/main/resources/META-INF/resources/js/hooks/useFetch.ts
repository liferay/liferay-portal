/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useCallback, useEffect, useState} from 'react';

import ApiHelper from '../apis/ApiHelper';

interface FetchState<Data> {
	data: Data | null;
	loading: boolean;
	refetch: () => void;
}

const useFetch = <Data>(endpointUrl: string): FetchState<Data> => {
	const [data, setData] = useState<Data | null>(null);
	const [loading, setLoading] = useState(false);
	const [reloadFlag, setReloadFlag] = useState(0);

	const fetchData = useCallback(async () => {
		try {
			setLoading(true);
			setData(null);

			const {data, error} = await ApiHelper.get<Data>(endpointUrl);

			if (error) {
				throw new Error(error);
			}

			setData(data);
		}
		catch (error: any) {
			if (process.env.NODE_ENV === 'development') {
				console.error(error);
			}
			setData(null);
		}
		finally {
			setLoading(false);
		}
	}, [endpointUrl]);

	useEffect(() => {
		fetchData();
	}, [fetchData, reloadFlag]);

	const refetch = () => {
		setReloadFlag((prev) => prev + 1);
	};

	return {data, loading, refetch};
};

export default useFetch;
