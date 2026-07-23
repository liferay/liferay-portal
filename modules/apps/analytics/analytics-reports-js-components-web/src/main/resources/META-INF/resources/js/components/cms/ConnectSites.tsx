/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import React from 'react';

import EmptyState from '../EmptyState';

interface IConnectSitesProps {
	onConnectSites?: () => void;
}

const ConnectSites: React.FC<IConnectSitesProps> = ({onConnectSites}) => {
	return (
		<EmptyState
			description={Liferay.Language.get(
				'no-sites-connected-yet.-connect-spaces-to-sites-to-see-how-assets-perform'
			)}
			externalImage={{
				src: '/o/site-cms-site-initializer/images/space_connection.svg',
				style: {
					width: 180,
				},
			}}
			title={Liferay.Language.get('connect-sites-to-show-data')}
		>
			{onConnectSites && (
				<ClayButton displayType="primary" onClick={onConnectSites}>
					{Liferay.Language.get('connect-sites')}
				</ClayButton>
			)}
		</EmptyState>
	);
};

export default ConnectSites;
