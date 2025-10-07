/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLayout from '@clayui/layout';
import {ClayVerticalNav} from '@clayui/nav';
import React, {useMemo, useState} from 'react';

type Item = {
	component: React.ReactNode;
	id: string;
	label: string;
};

type VerticalNavItem = Item & {
	active: boolean;
	onClick: () => void;
};

export default function VerticalNavLayout({items}: {items: Item[]}) {
	const [active, setActive] = useState<string>(items[0].id);
	const [children, setChildren] = useState<Item['component']>(
		items[0].component
	);

	const verticalNavItems: VerticalNavItem[] = useMemo(
		() =>
			items.map((item) => ({
				...item,
				active: active === item.id,
				onClick: () => {
					setChildren(item.component);
					setActive(item.id);
				},
			})),
		[active, items]
	);

	return (
		<ClayLayout.ContainerFluid size={false}>
			<ClayLayout.Row>
				<ClayLayout.Col
					className="cms-sidebar-nav sidebar-layout"
					md="auto"
					sm={12}
				>
					<div className="px-md-2 py-3 py-md-4">
						<ClayVerticalNav items={verticalNavItems} />
					</div>
				</ClayLayout.Col>

				<ClayLayout.Col className="col-md" sm={12}>
					{children}
				</ClayLayout.Col>
			</ClayLayout.Row>
		</ClayLayout.ContainerFluid>
	);
}
