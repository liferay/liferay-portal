/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLink from '@clayui/link';
import {createRenderURL} from 'frontend-js-web';
import React from 'react';

import EmptyState from '../EmptyState';

function buildAnalyticsCloudConfigURL() {
	return createRenderURL('group/control_panel/manage', {
		configurationScreenKey: 'analytics-cloud-connection',
		mvcRenderCommandName: '/configuration_admin/view_configuration_screen',
		p_p_id: Liferay.PortletKeys.INSTANCE_SETTINGS,
	});
}

function ConnectToAnalyticsCloud({admin}: {admin: boolean}) {
	return (
		<EmptyState
			description={
				admin
					? Liferay.Language.get(
							'in-order-to-view-asset-performance,-your-liferay-dxp-instance-has-to-be-connected-with-liferay-analytics-cloud'
						)
					: Liferay.Language.get(
							'please-contact-a-dxp-instance-administrator-to-connect-your-dxp-instance-to-analytics-cloud'
						)
			}
			externalImage={{
				src: '/o/analytics-reports-js-components-web/assets/performance_tab_empty_state.svg',
				style: {
					marginBottom: '1rem',
					width: 245,
				},
			}}
			title={Liferay.Language.get('connect-to-liferay-analytics-cloud')}
		>
			{admin && (
				<ClayLink
					button
					displayType="primary"
					href={buildAnalyticsCloudConfigURL().href}
					small
				>
					{Liferay.Language.get('connect')}
				</ClayLink>
			)}
		</EmptyState>
	);
}

export {buildAnalyticsCloudConfigURL};

export default ConnectToAnalyticsCloud;
