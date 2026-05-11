/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import {Option, OptionsList} from './OptionsList';

export default function MasterLayoutsList({
	masterLayoutPageTemplateEntryERC,
	masterLayouts,
	onSelectMasterLayout,
}) {
	return (
		<OptionsList options={masterLayouts}>
			{(masterLayout) => (
				<Option
					{...masterLayout}
					icon="page"
					isActive={
						masterLayoutPageTemplateEntryERC ===
						masterLayout.masterLayoutPageTemplateEntryERC
					}
					onClick={() => onSelectMasterLayout(masterLayout)}
				/>
			)}
		</OptionsList>
	);
}
