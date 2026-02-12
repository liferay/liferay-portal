/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayCard from '@clayui/card';
import ClayIcon from '@clayui/icon';
import ClaySticker from '@clayui/sticker';
import React from 'react';

import {CategoryItem} from '../types';

const CategoryCard = ({
	item: {href, label, leadingIcon},
}: {
	item: CategoryItem;
}) => {
	return (
		<ClayCard href={href} interactive>
			<ClayCard.Body>
				<div className="pt-4 text-center">
					<div className="mb-3">
						<ClaySticker
							className="border-0"
							displayType="outline"
							size="xl"
						>
							<ClayIcon symbol={leadingIcon} />
						</ClaySticker>
					</div>

					<ClayCard.Description
						className="font-weight-semi-bold text-3 text-dark text-truncate"
						displayType="title"
					>
						{label}
					</ClayCard.Description>
				</div>
			</ClayCard.Body>
		</ClayCard>
	);
};

export default CategoryCard;
