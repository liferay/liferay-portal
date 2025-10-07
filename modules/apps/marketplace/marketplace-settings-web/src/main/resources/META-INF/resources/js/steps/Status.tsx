/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import React from 'react';

import Container from '../components/Container';

type StatusProps = {
	onDisconnect: () => void;
};

export default function Status({onDisconnect}: StatusProps) {
	return (
		<Container
			footer={
				<ClayButton
					borderless
					displayType="secondary"
					onClick={onDisconnect}
					outline
				>
					{Liferay.Language.get('disconnect')}
				</ClayButton>
			}
			title="Status"
		>
			<ClayAlert displayType="success">
				{Liferay.Language.get('connected')}
			</ClayAlert>

			<p>
				{Liferay.Language.get(
					'your-liferay-instance-is-connected-to-the-marketplace'
				)}
			</p>
		</Container>
	);
}
