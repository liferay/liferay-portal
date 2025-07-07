/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Button from '@clayui/button';
import EmptyState from '@clayui/empty-state';
import ClayLink from '@clayui/link';
import React from 'react';

import {getImage} from '../../util/getImage';
import {EmptyStateData} from './tab_content/PerformanceTabContent';

export function getEmptyState(data: EmptyStateData) {
	if (!data.connectedToSpace) {
		if (data.isAdmin) {
			return (
				<EmptyState
					className="d-flex flex-column justify-content-center pt-6 text-center"
					description={Liferay.Language.get(
						'connect-sites-within-this-space'
					)}
					title={Liferay.Language.get('no-sites-are-connected-yet')}
				>
					<Button>{Liferay.Language.get('connect')}</Button>
				</EmptyState>
			);
		}

		return (
			<EmptyState
				className="d-flex flex-column justify-content-center pt-6 text-center"
				description={Liferay.Language.get(
					'please-contact-an-administrator-to-sync-sites-to-this-space'
				)}
				title={Liferay.Language.get('no-sites-are-connected-yet')}
			/>
		);
	}

	if (!data.connectedToAnalyticsCloud) {
		if (data.isAdmin) {
			return (
				<EmptyState
					className="d-flex flex-column justify-content-center pt-6 text-center"
					description={Liferay.Language.get(
						'in-order-to-view-asset-performance,-your-liferay-dxp-instance-has-to-be-connected-with-liferay-analytics-cloud'
					)}
					imgSrc={getImage('performance_tab_empty_state.svg')}
					title={Liferay.Language.get(
						'connect-to-liferay-analytics-cloud'
					)}
				>
					<ClayLink
						button
						displayType="primary"
						href={data.analyticsSettingsPortletURL}
					>
						{Liferay.Language.get('connect')}
					</ClayLink>
				</EmptyState>
			);
		}

		return (
			<EmptyState
				className="d-flex flex-column justify-content-center pt-6 text-center"
				description={Liferay.Language.get(
					'please-contact-a-dxp-instance-administrator-to-connect-your-dxp-instance-to-analytics-cloud'
				)}
				imgSrc={getImage('performance_tab_empty_state.svg')}
				title={Liferay.Language.get(
					'connect-to-liferay-analytics-cloud'
				)}
			/>
		);
	}

	if (!data.siteSyncedToAnalyticsCloud) {
		if (data.isAdmin) {
			return (
				<EmptyState
					className="d-flex flex-column justify-content-center pt-6 text-center"
					description={Liferay.Language.get(
						'in-order-to-view-asset-performance,-your-sites-have-to-be-synced-to-liferay-analytics-cloud'
					)}
					imgSrc={getImage('performance_tab_empty_state.svg')}
					title={Liferay.Language.get('sync-to-analytics-cloud')}
				>
					<ClayLink
						button
						displayType="primary"
						href={`${data.analyticsSettingsPortletURL}&currentPage=PROPERTIES`}
					>
						{Liferay.Language.get('sync')}
					</ClayLink>
				</EmptyState>
			);
		}

		return (
			<EmptyState
				className="d-flex flex-column justify-content-center pt-6 text-center"
				description={Liferay.Language.get(
					'please-contact-a-dxp-instance-administrator-to-sync-your-sites-to-analytics-cloud'
				)}
				imgSrc={getImage('performance_tab_empty_state.svg')}
				title={Liferay.Language.get('sync-to-analytics-cloud')}
			/>
		);
	}

	return null;
}
