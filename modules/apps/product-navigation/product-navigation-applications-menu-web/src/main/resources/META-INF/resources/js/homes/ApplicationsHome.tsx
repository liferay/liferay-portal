/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLayout from '@clayui/layout';
import React from 'react';

import CategoryCard from './components/CategoryCard';
import HomePageLayout from './components/HomePageLayout';
import {CategoryItem, HomeProps} from './types';

const ApplicationsHome = ({icon, items, title}: HomeProps) => {
	return (
		<HomePageLayout icon={icon} title={title}>
			{(items.flatMap((group) => group.items) as Array<CategoryItem>).map(
				(item) => (
					<ClayLayout.Col key={item.id} lg={3} md={4} sm={6}>
						<CategoryCard item={item} />
					</ClayLayout.Col>
				)
			)}
		</HomePageLayout>
	);
};

export default ApplicationsHome;
