/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import Title from '../Title';
import {ChannelsFilter} from './ChannelsFilter';
import {RangeSelectorsFilter} from './RangeSelectorsFilter';

const GlobalFilters = () => (
	<div className="d-flex global-filters justify-content-between mb-3">
		<Title value={Liferay.Language.get('overview')} />

		<div className="d-flex">
			<ChannelsFilter />

			<RangeSelectorsFilter />
		</div>
	</div>
);

export default GlobalFilters;
