/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {ClayVerticalNav} from '@clayui/nav';
import {UnifiedSidebar} from 'frontend-js-components-web';
import React, {useRef, useState} from 'react';

const ITEMS = [
	{
		href: '#',
		items: [
			{href: '#', label: 'Users and Organizations'},
			{href: '#', label: 'Roles'},
			{href: '#', label: 'Password Policies'},
		],
		label: 'Users',
	},
	{
		href: '#',
		items: [
			{href: '#', label: 'Sites'},
			{href: '#', label: 'Templates'},
		],
		label: 'Sites',
	},
	{
		href: '#',
		label: 'Configuration',
	},
];

export default function UnifiedSidebarSamples() {
	const containerRef = useRef<HTMLDivElement>(null);

	const [open, setOpen] = useState(false);

	return (
		<div
			ref={containerRef}
			style={
				{'--c-slideout-sidebar-width': '280px'} as React.CSSProperties
			}
		>
			<ClayButton className="float-right" onClick={() => setOpen(!open)}>
				{open
					? Liferay.Language.get('close')
					: Liferay.Language.get('open')}
			</ClayButton>

			<UnifiedSidebar
				categoryImageUrl={`${Liferay.ThemeDisplay.getPathThemeImages()}/product_icons/control_panel_sm.svg`}
				containerRef={containerRef}
				label="Unified Sidebar"
				onOpenChange={setOpen}
				open={open}
			>
				<UnifiedSidebar.Body className="c-px-0">
					<ClayVerticalNav
						displayType="primary"
						items={ITEMS}
						stacked
					/>
				</UnifiedSidebar.Body>
			</UnifiedSidebar>
		</div>
	);
}
