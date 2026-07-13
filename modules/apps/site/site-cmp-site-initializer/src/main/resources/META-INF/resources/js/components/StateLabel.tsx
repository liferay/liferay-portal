/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Label from '@clayui/label';
import React from 'react';

import isOverdue from '../utils/isOverdue';

type NameDisplayType =
	| 'danger'
	| 'info'
	| 'secondary'
	| 'success'
	| 'unstyled'
	| 'warning';

const mapKeyToNameDisplayType: {[key: string]: NameDisplayType} = {
	blocked: 'danger',
	completed: 'success',
	done: 'success',
	inProgress: 'info',
	notStarted: 'secondary',
	overdue: 'warning',
	pending: 'info',
};

type State = {
	key: string;
	name: string;
};

interface StateLabelProps {
	dueDate?: string;
	state?: State;
}

function StateLabel({dueDate, state}: StateLabelProps) {
	if (!state || !state.name) {
		return null;
	}

	return (
		<div>
			<Label displayType={mapKeyToNameDisplayType[state.key]} inverse>
				{state.name}
			</Label>

			{isOverdue({dueDate, state}) && (
				<Label displayType="warning" inverse>
					{Liferay.Language.get('overdue')}
				</Label>
			)}
		</div>
	);
}

export default StateLabel;
