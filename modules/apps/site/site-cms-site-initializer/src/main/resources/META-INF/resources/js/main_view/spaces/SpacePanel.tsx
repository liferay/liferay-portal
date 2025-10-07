/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayPanel from '@clayui/panel';
import React from 'react';

function SpacePanel({
	children,
	title,
}: {
	children: React.ReactNode;
	title: string;
}) {
	return (
		<ClayPanel
			aria-label={title}
			className="mb-4"
			collapsable
			defaultExpanded={true}
			displayTitle={
				<ClayPanel.Title>
					<h2 className="mb-0 py-2 text-6 text-dark">{title}</h2>
				</ClayPanel.Title>
			}
			displayType="secondary"
			role="group"
			showCollapseIcon
		>
			<div className="pt-4 px-4">{children}</div>
		</ClayPanel>
	);
}

export default SpacePanel;
