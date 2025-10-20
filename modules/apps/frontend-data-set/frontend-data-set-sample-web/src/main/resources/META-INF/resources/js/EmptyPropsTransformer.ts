/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export default function propsTransformer({...otherProps}: any) {
	const customCreationMenu = {
		primaryItems: [
			{
				href: '#',
				icon: 'plus',
				label: 'Create New Item',
				onClick: () => alert('Create New Item clicked!'),
			},
		],
	};

	return {
		...otherProps,
		creationMenu: customCreationMenu,
		showManagementBarInEmptyState: false,
	};
}
