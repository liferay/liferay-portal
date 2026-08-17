/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {SidePanel} from '@clayui/core';
import {SearchForm} from '@liferay/layout-js-components-web';
import React, {useRef} from 'react';

export default function ResponsivePanel({
	children,
	onOpenChange,
	onSearch,
	open,
}: {
	children: React.ReactNode;
	onOpenChange: (open: boolean) => void;
	onSearch: (search: string) => void;
	open: boolean;
}) {
	const wrapperRef = useRef<HTMLElement | null>(
		document.getElementById('wrapper')
	);

	return (
		<SidePanel
			className="shadow-none version-history__side-panel"
			containerRef={wrapperRef}
			direction="left"
			displayType="light"
			onOpenChange={onOpenChange}
			open={open}
			position="fixed"
		>
			<div className="border-bottom pb-3">
				<SidePanel.Header
					className="pb-0 px-3"
					messages={{closeAriaLabel: Liferay.Language.get('close')}}
				>
					<SidePanel.Title className="font-weight-semi-bold m-0">
						{Liferay.Language.get('version-history')}
					</SidePanel.Title>
				</SidePanel.Header>

				<SearchForm
					className="mb-0 mt-4 px-3"
					onChange={onSearch}
					size="sm"
				/>
			</div>

			<SidePanel.Body className="p-3">{children}</SidePanel.Body>
		</SidePanel>
	);
}
