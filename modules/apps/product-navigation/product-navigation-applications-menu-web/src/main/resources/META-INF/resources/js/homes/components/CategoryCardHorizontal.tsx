/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayCard from '@clayui/card';
import ClayIcon from '@clayui/icon';
import ClayLayout from '@clayui/layout';
import ClaySticker from '@clayui/sticker';
import React from 'react';

import {CategoryItem} from '../types';

const CategoryCardHorizontal = ({
	item: {href, label, leadingIcon},
}: {
	item: CategoryItem;
}) => {
	return (
		<ClayCard
			className="c-mb-2 c-mb-lg-3 home-card"
			horizontal
			href={href}
			interactive
		>
			<ClayCard.Body className="c-p-2 c-px-lg-3 c-py-lg-2">
				<ClayCard.Row>
					{leadingIcon ? (
						<ClayLayout.ContentCol>
							<ClaySticker
								className="border-0"
								displayType="outline"
								inline
								size="sm"
							>
								<ClayIcon fontSize={12} symbol={leadingIcon} />
							</ClaySticker>
						</ClayLayout.ContentCol>
					) : null}

					<ClayLayout.ContentCol expand>
						<ClayCard.Description
							className="text-4 text-dark"
							displayType="title"
						>
							{label}
						</ClayCard.Description>
					</ClayLayout.ContentCol>
				</ClayCard.Row>
			</ClayCard.Body>
		</ClayCard>
	);
};

export default CategoryCardHorizontal;
