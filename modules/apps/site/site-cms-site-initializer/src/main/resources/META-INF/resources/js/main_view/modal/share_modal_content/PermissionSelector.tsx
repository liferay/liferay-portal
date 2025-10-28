/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Option, Picker} from '@clayui/core';
import React from 'react';

const PERMISSION_OPTIONS = [
	{
		label: Liferay.Language.get('view-and-download'),
		value: 'VIEW',
	},
	{
		label: Liferay.Language.get('view-download-and-comment'),
		value: 'ADD_DISCUSSION,VIEW',
	},
	{
		label: Liferay.Language.get('view-download-comment-and-update'),
		value: 'ADD_DISCUSSION,UPDATE,VIEW',
	},
];

export default function PermissionSelector({
	actionIds,
	onChange,
}: {
	actionIds?: string;
	onChange: (value: object) => void;
}) {
	return (
		<Picker
			aria-label={Liferay.Language.get('edit-permissions')}
			className="border-0 c-py-0 permissions-picker text-2 text-secondary text-weight-semi-bold"
			items={PERMISSION_OPTIONS}
			messages={{
				itemDescribedby: Liferay.Language.get(
					'you-are-currently-on-a-text-element,-inside-of-a-list-box'
				),
				itemSelected: Liferay.Language.get('x-selected'),
				scrollToBottomAriaLabel:
					Liferay.Language.get('scroll-to-bottom'),
				scrollToTopAriaLabel: Liferay.Language.get('scroll-to-top'),
			}}
			onSelectionChange={(value: React.Key) =>
				onChange({actionIds: value as string})
			}
			placeholder=""
			selectedKey={actionIds}
		>
			{(item: {label: string; value: string}) => (
				<Option key={item.value}>{item.label}</Option>
			)}
		</Picker>
	);
}
