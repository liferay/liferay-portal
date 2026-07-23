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

const LEARN_PERFORMANCE_CAPABILITIES_URL =
	'https://www.liferay.com/capabilities/data-platform?utm_medium=referral&utm_source=ft-cms&utm_content=ldp&utm_cid=701VO00001AIY6bYAH';

function ConnectToAnalyticsCloud({admin}: {admin: boolean}) {
	return (
		<EmptyState
			description={Liferay.Language.get(
				'track-views,-engagement,-and-where-your-assets-are-used-across-your-sites-with-liferay-data-platform'
			)}
			externalImage={{
				src: '/o/site-cms-site-initializer/images/performance.svg',
				style: {
					marginBottom: '1rem',
					width: 245,
				},
			}}
			title={Liferay.Language.get('see-how-your-content-performs')}
		>
			<ClayLink
				className="d-block mb-3 text-decoration-underline"
				href={LEARN_PERFORMANCE_CAPABILITIES_URL}
				rel="noopener noreferrer"
				target="_blank"
			>
				{Liferay.Language.get(
					'learn-more-about-performance-capabilities'
				)}
			</ClayLink>

			{admin && (
				<ClayLink
					button
					displayType="primary"
					href={buildAnalyticsCloudConfigURL().href}
					small
				>
					{Liferay.Language.get('connect-your-liferay-data-platform')}
				</ClayLink>
			)}
		</EmptyState>
	);
}

export {buildAnalyticsCloudConfigURL};

export default ConnectToAnalyticsCloud;
