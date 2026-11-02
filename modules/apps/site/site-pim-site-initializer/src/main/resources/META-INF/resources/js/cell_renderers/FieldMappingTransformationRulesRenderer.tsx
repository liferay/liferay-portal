/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLabel from '@clayui/label';
import React from 'react';

const MAX_VISIBLE_TRANSFORMATION_RULES = 2;

export default function FieldMappingTransformationRulesRenderer({
	value,
}: {
	value: string[];
}) {
	if (!value?.length) {
		return null;
	}

	const visibleValue = value.slice(0, MAX_VISIBLE_TRANSFORMATION_RULES);

	const hiddenCount = value.length - visibleValue.length;

	return (
		<span className="c-gap-1 d-inline-flex flex-wrap">
			{visibleValue.map((transformationRule, index) => (
				<ClayLabel className="label-inverse-info" key={index}>
					{transformationRule}
				</ClayLabel>
			))}

			{hiddenCount > 0 && (
				<ClayLabel className="label-inverse-info">
					{`+${hiddenCount}`}
				</ClayLabel>
			)}
		</span>
	);
}
