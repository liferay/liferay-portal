/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useIsMounted, useTimeout} from '@liferay/frontend-js-react-web';
import {openToast} from 'frontend-js-components-web';
import {fetch, navigate} from 'frontend-js-web';
import {useEffect} from 'react';

interface BulkStatusProps {
	bulkInProgress: boolean;
	deletionStarted: boolean;
	pathModule: string;
}

export default function BulkStatus({
	bulkInProgress,
	deletionStarted,
	pathModule,
}: BulkStatusProps) {
	const delay = useTimeout();
	const isMounted = useIsMounted();

	useEffect(() => {
		if (deletionStarted) {
			openToast({
				autoClose: false,
				message: Liferay.Language.get(
					'deletion-started-for-all-entries'
				),
				type: 'info',
			});
		}

		if (!bulkInProgress && !deletionStarted) {
			return;
		}

		const checkStatus = () => {
			fetch(`${pathModule}/bulk/v1.0/status`)
				.then((response) => response.json())
				.then(({actionInProgress}) => {
					if (!isMounted()) {
						return;
					}

					if (actionInProgress) {
						delay(checkStatus, 1000);

						return;
					}

					openToast({
						message: Liferay.Language.get(
							'deletion-completed-successfully'
						),
					});

					navigate(window.location.href);
				})
				.catch(() => {
					if (!isMounted()) {
						return;
					}

					openToast({
						message: Liferay.Language.get(
							'an-unexpected-error-occurred'
						),
						type: 'danger',
					});
				});
		};

		return delay(checkStatus, 1000);
	}, [bulkInProgress, delay, deletionStarted, isMounted, pathModule]);

	return null;
}
