/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useCallback, useState} from 'react';

interface IParams {
	gcsSessionURL: string;
	totalSize: number;
}

interface IProps {
	error: Error | null;
	getUploadOffset: (params: IParams) => Promise<number>;
	loading: boolean;
}

const useGCSGetUploadOffset = (): IProps => {
	const [error, setError] = useState<Error | null>(null);
	const [loading, setLoading] = useState(false);

	const getUploadOffset = useCallback(
		async (params: IParams): Promise<number> => {
			setLoading(true);
			setError(null);

			const {gcsSessionURL, totalSize} = params;

			try {
				const response = await fetch(gcsSessionURL, {
					headers: {
						'Content-Length': '0',
						'Content-Range': `bytes */${totalSize}`,
					},
					method: 'PUT',
				});

				if (response.status === 200 || response.status === 201) {
					return totalSize;
				}
				else if (response.status === 308) {
					const rangeHeader = response.headers.get('Range');

					if (!rangeHeader) {
						return 0;
					}

					const match = rangeHeader?.match(/bytes=0-(\d+)/);

					if (match && match[1]) {
						return parseInt(match[1], 10) + 1;
					}
				}

				return 0;
			}
			catch (offsetError) {
				console.error('Error getting upload offset:', offsetError);

				setError(
					offsetError instanceof Error
						? offsetError
						: new Error(String(offsetError))
				);

				return 0;
			}
			finally {
				setLoading(false);
			}
		},
		[]
	);

	return {error, getUploadOffset, loading};
};

export default useGCSGetUploadOffset;
