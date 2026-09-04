/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm from '@clayui/form';
import React from 'react';

const NEAR_LIMIT_THRESHOLD = 500;

interface CharacterCounterProps {
	count: number;
	max: number;
}

const CharacterCounter: React.FC<CharacterCounterProps> = ({count, max}) => {
	if (count < max - NEAR_LIMIT_THRESHOLD) {
		return null;
	}

	return (
		<ClayForm.FeedbackGroup>
			<ClayForm.Text>
				{`${count.toLocaleString()} / ${max.toLocaleString()}`}
			</ClayForm.Text>
		</ClayForm.FeedbackGroup>
	);
};

export default CharacterCounter;
