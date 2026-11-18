/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLabel from '@clayui/label';
import React from 'react';

import ProductSticker from './ProductSticker';

export default function ProductRelationshipSelectorTitleRenderer({
	current = false,
	imageURL,
	value,
}: {
	current?: boolean;
	imageURL?: string | null;
	value: string;
}) {
	return (
		<span className="align-items-center d-flex table-list-title">
			<ProductSticker imageURL={imageURL} />

			<span>{value}</span>

			{current && (
				<ClayLabel className="c-ml-auto" displayType="info" inverse>
					{Liferay.Language.get('viewing')}
				</ClayLabel>
			)}
		</span>
	);
}
