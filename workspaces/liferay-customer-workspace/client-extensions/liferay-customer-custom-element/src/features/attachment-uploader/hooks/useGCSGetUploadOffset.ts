/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useCallback, useState} from 'react';

interface IParams {
	sessionURL: string;
	totalSize: number;
}

interface IProps {
	error: Error | null;
	getUploadOffset: (params: IParams) => Promise<void>;
	loading: boolean;
	offset: number | null;
}

const useGCSGetUploadOffset = (): IProps => {
	const [error, setError] = useState<Error | null>(null);
	const [loading, setLoading] = useState(false);
	const [offset, setOffset] = useState<number | null>(null);

	const getUploadOffset = useCallback(async (params: IParams) => {
		setLoading(true);
		setError(null);
		setOffset(null);

		const {sessionURL, totalSize} = params;

		try {
			const response = await fetch(sessionURL, {
				headers: {
					'Content-Length': '0',
					'Range': `bytes */${totalSize}`,
				},
				method: 'PUT',
			});

			let calculatedOffset: number | undefined = undefined;

			if (response.status === 200 || response.status === 201) {
				calculatedOffset = totalSize;
			}
			else if (response.status === 308) {
				const rangeHeader = response.headers.get('Range');

				if (rangeHeader) {
					const match = rangeHeader.match(/bytes=0-(\d+)/);

					if (match && match[1]) {
						calculatedOffset = parseInt(match[1], 10) + 1;
					}
					else {
						throw new Error(
							`Received status 308 but Range header was malformed: ${rangeHeader}`
						);
					}
				}
				else {
					throw new Error(
						'Received status 308 but Range header was missing.'
					);
				}
			}
			else {
				throw new Error(
					`Failed to get upload offset: ${response.text()}`
				);
			}

			setOffset(calculatedOffset);
		}
		catch (offsetError) {
			console.error('Error getting upload offset:', offsetError);

			setError(
				offsetError instanceof Error
					? offsetError
					: new Error(String(offsetError))
			);

			setOffset(null);
		}
		finally {
			setLoading(false);
		}
	}, []);

	return {error, getUploadOffset, loading, offset};
};

export default useGCSGetUploadOffset;
