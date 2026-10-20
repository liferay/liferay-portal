/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLayout from '@clayui/layout';
import React from 'react';

import FieldSelectWithOption from '../forms/FieldSelectWithOption';
import {EditingState, LastRange} from './types';
import {LAST_RANGE_OPTIONS} from './utils';

type Props = {
	handleUpdateFilter: (payload: Partial<EditingState>) => void;
	value: LastRange;
};

const LastRangeFields = ({handleUpdateFilter, value}: Props) => (
	<ClayLayout.Col md={4}>
		<FieldSelectWithOption
			formGroupProps={{className: 'mb-0'}}
			id="last"
			label={Liferay.Language.get('modified-last')}
			name="last"
			onChange={(event) =>
				handleUpdateFilter({
					last: event.target.value as LastRange,
				})
			}
			options={LAST_RANGE_OPTIONS}
			value={value}
		/>
	</ClayLayout.Col>
);

export default LastRangeFields;
