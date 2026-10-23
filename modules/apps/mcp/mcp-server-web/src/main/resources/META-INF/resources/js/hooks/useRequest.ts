/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useEffect, useState} from 'react';

import {RequestResult} from '../services/ApiHelper';

export default function useRequest<P extends number | string, T>(
	request: (params: P) => Promise<RequestResult<T>>,
	params: P | null
) {
	const [data, setData] = useState<T | null>(null);
	const [error, setError] = useState<string | null>(null);
	const [loading, setLoading] = useState(params !== null);

	useEffect(() => {
		if (params === null) {
			setLoading(false);

			return;
		}

		let isMounted = true;

		setError(null);
		setLoading(true);

		request(params).then((result) => {
			if (!isMounted) {
				return;
			}

			if (result.error === null) {
				setData(result.data);
			}
			else {
				setError(result.error);
			}

			setLoading(false);
		});

		return () => {
			isMounted = false;
		};
	}, [params, request]);

	return {data, error, loading};
}
