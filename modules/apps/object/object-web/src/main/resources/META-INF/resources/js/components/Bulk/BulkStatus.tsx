/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLoadingIndicator from '@clayui/loading-indicator';
import {useTimeout} from '@liferay/frontend-js-react-web';
import {openToast} from 'frontend-js-components-web';
import {fetch} from 'frontend-js-web';
import React, {useCallback, useEffect, useReducer} from 'react';

import './BulkStatus.scss';
import reducer, {STATES} from './reducer';

interface BulkStatusProps {
	bulkComponentId: string;
	bulkInProgress: boolean;
	bulkStatusUrl: string;
	intervalSpeed: number;
	pathModule: string;
	waitingTime: number;
}

/**
 * Shows the bulk actions status
 */
export default function BulkStatus({
	bulkComponentId,
	bulkInProgress,
	bulkStatusUrl = '/bulk/v1.0/status',
	intervalSpeed = 1000,
	pathModule,
	waitingTime = 1000,
}: BulkStatusProps) {
	const delay = useTimeout();

	const [state, dispatch] = useReducer(
		reducer,
		bulkInProgress ? {current: STATES.LONG_POLLING} : {current: STATES.IDLE}
	);

	const statusCallback = useCallback(() => {
		return delay(() => {
			fetch(
				`${Liferay.ThemeDisplay.getPortalURL()}${pathModule}${bulkStatusUrl}`
			)
				.then((response) => response.json())
				.then((response) => {
					if (response.actionInProgress) {
						dispatch({type: 'check'});
					}
					else {
						dispatch({type: 'success'});
					}
				})
				.catch(() => dispatch({type: 'error'}));
		}, intervalSpeed);
	}, [bulkStatusUrl, delay, pathModule, intervalSpeed]);

	useEffect(() => {
		let dispose;

		if (state.current === STATES.SHORT_POLLING) {
			statusCallback();

			dispose = delay(
				() => dispatch({type: 'initialDelayCompleted'}),
				waitingTime
			);
		}
		else if (state.current === STATES.LONG_POLLING) {
			statusCallback();
		}
		else if (state.current === STATES.NOTIFY && state.toast) {
			openToast(state.toast);

			dispatch({type: 'notificationCompleted'});
		}

		return dispose;
	}, [delay, state, statusCallback, waitingTime]);

	if (!Liferay.component(bulkComponentId)) {
		Liferay.component(
			bulkComponentId,
			{
				startWatch: () => {
					dispatch({type: 'start'});
				},
			},
			{
				destroyOnNavigate: true,
			}
		);
	}

	return (
		<div className="bulk-status-container">
			<div
				className={`bulk-status ${!state.current.running && 'closed'}`}
			>
				<div className="bulk-status-content">
					<ClayLoadingIndicator />
				</div>
			</div>
		</div>
	);
}
