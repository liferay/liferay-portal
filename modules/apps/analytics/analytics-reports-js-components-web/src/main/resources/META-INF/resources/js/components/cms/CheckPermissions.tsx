/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayLink from '@clayui/link';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import {createRenderURL} from 'frontend-js-web';
import React from 'react';

import useFetch from '../../hooks/useFetch';
import {buildQueryString} from '../../utils/buildQueryString';
import EmptyState from '../EmptyState';

function buildAnalyticsCloudConfigURL() {
	return createRenderURL('group/control_panel/manage', {
		configurationScreenKey: 'analytics-cloud-connection',
		mvcRenderCommandName: '/configuration_admin/view_configuration_screen',
		p_p_id: Liferay.PortletKeys.INSTANCE_SETTINGS,
	});
}

interface IEmptyStateProps extends React.HTMLAttributes<HTMLElement> {
	admin: boolean;
	connectedToAnalyticsCloud: boolean;
	connectedToSpace: boolean;
	onConnectSites: () => void;
	siteSyncedToAnalyticsCloud: boolean;
}

const EmptyStates: React.FC<IEmptyStateProps> = ({
	admin,
	children,
	connectedToAnalyticsCloud,
	connectedToSpace,
	onConnectSites,
	siteSyncedToAnalyticsCloud,
}) => {
	if (!connectedToSpace) {
		if (admin) {
			return (
				<EmptyState
					description={Liferay.Language.get(
						'connect-sites-to-this-space'
					)}
					title={Liferay.Language.get('no-sites-are-connected-yet')}
				>
					<ClayButton onClick={onConnectSites} size="sm">
						{Liferay.Language.get('connect')}
					</ClayButton>
				</EmptyState>
			);
		}

		return (
			<EmptyState
				description={Liferay.Language.get(
					'please-contact-an-administrator-to-sync-sites-to-this-space'
				)}
				title={Liferay.Language.get('no-sites-are-connected-yet')}
			/>
		);
	}

	if (!connectedToAnalyticsCloud) {
		if (admin) {
			return (
				<EmptyState
					description={Liferay.Language.get(
						'in-order-to-view-asset-performance,-your-liferay-dxp-instance-has-to-be-connected-with-liferay-analytics-cloud'
					)}
					externalImage={{
						src: '/o/analytics-reports-js-components-web/assets/performance_tab_empty_state.svg',
						style: {
							marginBottom: '1rem',
							width: 245,
						},
					}}
					title={Liferay.Language.get(
						'connect-to-liferay-analytics-cloud'
					)}
				>
					<ClayLink
						button
						displayType="primary"
						href={buildAnalyticsCloudConfigURL().href}
						small
					>
						{Liferay.Language.get('connect')}
					</ClayLink>
				</EmptyState>
			);
		}

		return (
			<EmptyState
				description={Liferay.Language.get(
					'please-contact-a-dxp-instance-administrator-to-connect-your-dxp-instance-to-analytics-cloud'
				)}
				externalImage={{
					src: '/o/analytics-reports-js-components-web/assets/performance_tab_empty_state.svg',
					style: {
						marginBottom: '1rem',
						width: 245,
					},
				}}
				title={Liferay.Language.get(
					'connect-to-liferay-analytics-cloud'
				)}
			/>
		);
	}

	if (!siteSyncedToAnalyticsCloud) {
		if (admin) {
			return (
				<EmptyState
					description={Liferay.Language.get(
						'in-order-to-view-asset-performance,-your-sites-have-to-be-synced-to-liferay-analytics-cloud'
					)}
					externalImage={{
						src: '/o/analytics-reports-js-components-web/assets/performance_tab_empty_state.svg',
						style: {
							marginBottom: '1rem',
							width: 245,
						},
					}}
					title={Liferay.Language.get('sync-to-analytics-cloud')}
				>
					<ClayLink
						button
						displayType="primary"
						href={`${buildAnalyticsCloudConfigURL().href}&currentPage=PROPERTIES`}
						small
					>
						{Liferay.Language.get('sync')}
					</ClayLink>
				</EmptyState>
			);
		}

		return (
			<EmptyState
				description={Liferay.Language.get(
					'please-contact-a-dxp-instance-administrator-to-sync-your-sites-to-analytics-cloud'
				)}
				externalImage={{
					src: '/o/analytics-reports-js-components-web/assets/performance_tab_empty_state.svg',
					style: {
						marginBottom: '1rem',
						width: 245,
					},
				}}
				title={Liferay.Language.get('sync-to-analytics-cloud')}
			/>
		);
	}

	return <>{children}</>;
};

type ConnectionInfoData = {
	admin: boolean;
	connectedToAnalyticsCloud: boolean;
	connectedToSpace: boolean;
	siteSyncedToAnalyticsCloud: boolean;
};

interface ICheckPermissions extends React.HTMLAttributes<HTMLElement> {
	onConnectSites?: (loadData: () => void) => void;
	scopeId: string;
}

const CheckPermissions: React.FC<ICheckPermissions> = ({
	children,
	onConnectSites,
	scopeId,
}) => {
	const queryString = buildQueryString({
		depotEntryGroupId: scopeId,
	});

	const {data, loading, refetch} = useFetch<ConnectionInfoData>(
		`/o/analytics-cms-rest/v1.0/connection-info${queryString}`
	);

	if (loading) {
		return <ClayLoadingIndicator data-testid="loading" />;
	}

	if (!data) {
		return null;
	}

	return (
		<EmptyStates
			{...data}
			onConnectSites={() =>
				onConnectSites ? onConnectSites(refetch) : null
			}
		>
			{children}
		</EmptyStates>
	);
};

export {CheckPermissions};
