/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useContext} from 'react';

import {InsightsViewContext} from '../InsightsViewContext';

export default function InsightNameCellRenderer({
	itemData,
}: {
	itemData: {externalReferenceCode: string; name?: {name?: string}};
}) {
	const {selectInsight} = useContext(InsightsViewContext);

	const name = itemData?.name;

	if (!name?.name) {
		return null;
	}

	return (
		<span
			onClick={(event) => {
				event.stopPropagation();

				selectInsight(itemData.externalReferenceCode);
			}}
			style={{cursor: 'pointer', textDecoration: 'underline'}}
		>
			{name.name}
		</span>
	);
}
