/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayPanel from '@clayui/panel';
import React from 'react';

interface Props {
	children: React.ReactNode;

	defaultExpanded?: boolean;

	title: string;

	titleId: string;
}

export function EditorSection({
	children,
	defaultExpanded = true,
	title,
	titleId,
}: Props) {
	return (
		<ClayPanel
			className="editor-panel"
			collapsable
			collapseHeaderClassNames="mb-3"
			defaultExpanded={defaultExpanded}
			displayTitle={
				<span className="editor-panel-title panel-title" id={titleId}>
					{title}
				</span>
			}
			displayType="unstyled"
			showCollapseIcon
			size="sm"
		>
			<ClayPanel.Body>{children}</ClayPanel.Body>
		</ClayPanel>
	);
}
