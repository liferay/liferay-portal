/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayEmptyState from '@clayui/empty-state';
import ClayLink from '@clayui/link';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import {createRenderURL} from 'frontend-js-web';
import React, {useContext, useEffect, useState} from 'react';

import {getImage} from '../../../../common/utils/getImage';
import {
	AssetTypeInfoPanelContext,
	IAssetTypeInfoPanelContext,
} from '../../context';

type EmptyStateApiResponse = {
	connectedToAnalyticsCloud: boolean;
	connectedToSpace: boolean;
	isAdmin: boolean;
	siteSyncedToAnalyticsCloud: boolean;
};

function buildAnalyticsCloudConfigURL() {
	return createRenderURL('group/control_panel/manage', {
		configurationScreenKey: 'analytics-cloud-connection',
		mvcRenderCommandName: '/configuration_admin/view_configuration_screen',
		p_p_id: Liferay.PortletKeys.INSTANCE_SETTINGS,
	});
}

async function fetchEmptyStateData(_spaceId: number) {

	// TODO fetch from API

	return {
		connectedToAnalyticsCloud: true,
		connectedToSpace: true,
		isAdmin: true,
		siteSyncedToAnalyticsCloud: true,
	};
}

interface IEmptyStateProps extends React.HTMLAttributes<HTMLElement> {
	connectedToAnalyticsCloud: boolean;
	connectedToSpace: boolean;
	isAdmin: boolean;
	siteSyncedToAnalyticsCloud: boolean;
}

const EmptyStates: React.FC<IEmptyStateProps> = ({
	children,
	connectedToAnalyticsCloud,
	connectedToSpace,
	isAdmin,
	siteSyncedToAnalyticsCloud,
}) => {
	const className =
		'd-flex empty-state-performance-tab flex-column justify-content-center pt-6 text-center';

	if (!connectedToSpace) {
		if (isAdmin) {
			return (
				<ClayEmptyState
					className={className}
					description={Liferay.Language.get(
						'connect-sites-within-this-space'
					)}
					title={Liferay.Language.get('no-sites-are-connected-yet')}
				>
					<ClayButton>{Liferay.Language.get('connect')}</ClayButton>
				</ClayEmptyState>
			);
		}

		return (
			<ClayEmptyState
				className={className}
				description={Liferay.Language.get(
					'please-contact-an-administrator-to-sync-sites-to-this-space'
				)}
				title={Liferay.Language.get('no-sites-are-connected-yet')}
			/>
		);
	}

	if (!connectedToAnalyticsCloud) {
		if (isAdmin) {
			return (
				<ClayEmptyState
					className={className}
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
						href={buildAnalyticsCloudConfigURL().href}
					>
						{Liferay.Language.get('connect')}
					</ClayLink>
				</ClayEmptyState>
			);
		}

		return (
			<ClayEmptyState
				className={className}
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

	if (!siteSyncedToAnalyticsCloud) {
		if (isAdmin) {
			return (
				<ClayEmptyState
					className={className}
					description={Liferay.Language.get(
						'in-order-to-view-asset-performance,-your-sites-have-to-be-synced-to-liferay-analytics-cloud'
					)}
					imgSrc={getImage('performance_tab_empty_state.svg')}
					title={Liferay.Language.get('sync-to-analytics-cloud')}
				>
					<ClayLink
						button
						displayType="primary"
						href={`${buildAnalyticsCloudConfigURL().href}&currentPage=PROPERTIES`}
					>
						{Liferay.Language.get('sync')}
					</ClayLink>
				</ClayEmptyState>
			);
		}

		return (
			<ClayEmptyState
				className={className}
				description={Liferay.Language.get(
					'please-contact-a-dxp-instance-administrator-to-sync-your-sites-to-analytics-cloud'
				)}
				imgSrc={getImage('performance_tab_empty_state.svg')}
				title={Liferay.Language.get('sync-to-analytics-cloud')}
			/>
		);
	}

	return children;
};

const CheckPermissions: React.FC<React.HTMLAttributes<HTMLElement>> = ({
	children,
}) => {
	const [loading, setLoading] = useState(false);
	const [data, setData] = useState<EmptyStateApiResponse | null>(null);

	const selectedAsset = useContext<IAssetTypeInfoPanelContext>(
		AssetTypeInfoPanelContext
	);

	useEffect(() => {
		const fetchData = async () => {
			try {
				setLoading(true);

				const data = await fetchEmptyStateData(
					selectedAsset?.id as number
				);

				setData(data);
				setLoading(false);
			}
			catch (error) {
				console.error(error);

				setLoading(false);
			}
		};

		fetchData();
	}, [selectedAsset?.id]);

	if (loading) {
		return <ClayLoadingIndicator data-testid="loading" />;
	}

	if (!data) {
		return null;
	}

	return <EmptyStates {...data}>{children}</EmptyStates>;
};

export {CheckPermissions, EmptyStates};
