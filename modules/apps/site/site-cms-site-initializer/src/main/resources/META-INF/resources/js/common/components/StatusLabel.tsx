/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Label from '@clayui/label';
import React from 'react';

declare type LabelDisplayType =
	| 'secondary'
	| 'info'
	| 'warning'
	| 'danger'
	| 'success'
	| 'unstyled';

const mapLabelToLabelDisplayType: {[key: string]: LabelDisplayType} = {
	'approved': 'success',
	'denied': 'danger',
	'draft': 'secondary',
	'expired': 'danger',
	'in-trash': 'info',
	'inactive': 'secondary',
	'incomplete': 'warning',
	'pending': 'info',
	'scheduled': 'info',
};

interface StatusLabelProps {
	label: string;
}

const StatusLabel = ({label}: StatusLabelProps) => (
	<Label displayType={mapLabelToLabelDisplayType[label]}>
		{Liferay.Language.get(label)}
	</Label>
);

export default StatusLabel;
