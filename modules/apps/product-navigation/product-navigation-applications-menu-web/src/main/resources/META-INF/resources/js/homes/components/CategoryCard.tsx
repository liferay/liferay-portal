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
		<ClayCard className="home-card" href={href} interactive>
			<ClayCard.Body className="c-pb-1 c-pb-lg-3">
				<div className="c-mt-1 c-mt-lg-0 c-pt-2 c-pt-lg-4">
					{leadingIcon ? (
						<div className="c-pb-2">
							<ClaySticker
								borderless
								displayType="outline"
								size="xl"
							>
								<ClayIcon symbol={leadingIcon} />
							</ClaySticker>
						</div>
					) : null}

					<ClayCard.Description
						className="c-mt-lg-2 font-weight-semi-bold text-dark text-truncate"
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
