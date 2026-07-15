/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLayout from '@clayui/layout';
import React from 'react';

import {SectionHeader} from '../../common/SectionHeader';
import {AssetConsumption} from './AssetConsumption';
import {TopAssets} from './TopAssets';

export function ContentConsumption() {
	return (
		<>
			<ClayLayout.Row className="mb-3">
				<ClayLayout.Col size={12}>
					<SectionHeader
						description={Liferay.Language.get(
							'find-how-your-content-is-being-consumed'
						)}
						icon="view"
						title={Liferay.Language.get('content-consumption')}
					/>
				</ClayLayout.Col>
			</ClayLayout.Row>

			<ClayLayout.Row className="mb-4">
				<ClayLayout.Col size={12}>
					<TopAssets />
				</ClayLayout.Col>
			</ClayLayout.Row>

			<ClayLayout.Row className="mb-4">
				<ClayLayout.Col size={12}>
					<AssetConsumption />
				</ClayLayout.Col>
			</ClayLayout.Row>
		</>
	);
}
