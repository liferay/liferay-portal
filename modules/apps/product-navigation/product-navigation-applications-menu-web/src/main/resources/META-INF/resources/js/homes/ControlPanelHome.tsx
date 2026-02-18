/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLayout from '@clayui/layout';
import React from 'react';

import CategoryCardHorizontal from './components/CategoryCardHorizontal';
import HomePageLayout from './components/HomePageLayout';
import {CategoryItemGrouped, HomeProps} from './types';

const ControlPanelHome = ({icon, items, title}: HomeProps) => {
	return (
		<HomePageLayout icon={icon} title={title}>
			{(items as Array<CategoryItemGrouped>).map((group) => (
				<ClayLayout.ContainerFluid className="mb-4" key={group.label}>
					<ClayLayout.ContentRow className="mb-2 pb-2">
						<ClayLayout.ContentCol expand>
							<p className="font-weight-semi-bold mb-0 text-3 text-secondary text-uppercase">
								{group.label}
							</p>
						</ClayLayout.ContentCol>
					</ClayLayout.ContentRow>

					<ClayLayout.Row>
						{group.items.map((app) => (
							<ClayLayout.Col key={app.id} md={4} sm={6}>
								<CategoryCardHorizontal item={app} />
							</ClayLayout.Col>
						))}
					</ClayLayout.Row>
				</ClayLayout.ContainerFluid>
			))}
		</HomePageLayout>
	);
};
export default ControlPanelHome;
