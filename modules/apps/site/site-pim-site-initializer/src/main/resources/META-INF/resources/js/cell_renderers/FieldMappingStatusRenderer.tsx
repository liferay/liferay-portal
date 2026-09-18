/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLabel from '@clayui/label';
import React from 'react';

function getDisplayType(mapped: boolean, required: boolean) {
	if (mapped) {
		return 'success';
	}

	if (required) {
		return 'danger';
	}

	return 'secondary';
}

function getLabel(mapped: boolean, required: boolean) {
	if (mapped) {
		return Liferay.Language.get('mapped');
	}

	if (required) {
		return Liferay.Language.get('no-source-data-required');
	}

	return Liferay.Language.get('no-source-data');
}

export default function FieldMappingStatusRenderer({
	itemData,
	value,
}: {
	itemData: any;
	value: boolean;
}) {
	const required = Boolean(itemData?.required);

	return (
		<ClayLabel
			className="font-weight-normal"
			displayType={getDisplayType(value, required)}
		>
			{getLabel(value, required)}
		</ClayLabel>
	);
}
