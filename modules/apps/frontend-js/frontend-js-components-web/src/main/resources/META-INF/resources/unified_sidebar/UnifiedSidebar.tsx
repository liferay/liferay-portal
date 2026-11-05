/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {SidePanel} from '@clayui/core';
import ClaySticker from '@clayui/sticker';
import {sub} from 'frontend-js-web';
import React, {useRef} from 'react';

import UnifiedSidebarColorSchemeButton from './UnifiedSidebarColorSchemeButton';

import './UnifiedSidebar.scss';

export type HeaderMessages = {
	backAriaLabel?: string;
	closeAriaLabel?: string;
};

export interface UnifiedSidebarProps {
	canonicalName?: string;
	categoryImageUrl: string;
	children?: React.ReactNode;
	colorScheme?: 'dark' | 'light';
	colorSchemeSessionKey?: string;
	containerRef?: React.RefObject<HTMLElement>;
	headerActions?: React.ReactNode;
	headerMessages?: HeaderMessages;
	label: string;
	navigationId?: string;
	onOpenChange?: (open: boolean) => void;
	open?: boolean;
	testId?: string;
}

function UnifiedSidebarTop({children}: {children?: React.ReactNode}) {
	return <>{children}</>;
}

export default function UnifiedSidebar({
	canonicalName,
	categoryImageUrl,
	children,
	colorScheme,
	colorSchemeSessionKey,
	containerRef: externalContainerRef,
	headerActions,
	headerMessages,
	label,
	navigationId,
	onOpenChange,
	open = false,
	testId = 'unified-sidebar',
}: UnifiedSidebarProps) {
	const internalContainerRef = useRef<HTMLElement>(null);

	const containerRef = externalContainerRef ?? internalContainerRef;

	const closeButtonProps = Liferay.FeatureFlags['LPD-57922']
		? {
				className:
					'btn btn-monospaced btn-outline-borderless btn-outline-secondary btn-sm unified-sidebar-close-button',
			}
		: undefined;

	const mergedHeaderMessages = {
		backAriaLabel: Liferay.Language.get('go-back'),
		closeAriaLabel: Liferay.Language.get('close-product-menu'),
		...headerMessages,
	};

	return (
		<SidePanel
			aria-label={sub(Liferay.Language.get('x-menu'), label)}
			className="unified-sidebar"
			closeOnEscape={false}
			containerRef={containerRef}
			data-canonical-name={canonicalName}
			data-qa-id={testId}
			data-testid={testId}
			direction="left"
			displayType="light"
			id={navigationId}
			onOpenChange={onOpenChange}
			open={open}
			position="fixed"
		>
			<SidePanel.Header
				className="c-mx-1 c-px-2 unified-sidebar-header"
				closeButtonProps={closeButtonProps}
				data-qa-id="unifiedSidebarHeader"
				messages={mergedHeaderMessages}
			>
				<SidePanel.Title className="align-items-center c-my-0 d-flex">
					<ClaySticker
						borderless
						className="c-mr-1"
						data-qa-id="unifiedSidebarProductIcon"
						displayType="outline"
					>
						<img alt="" className="c-mx-1" src={categoryImageUrl} />
					</ClaySticker>

					<span
						className="c-ml-2 text-5"
						data-qa-id="unifiedSidebarLabel"
					>
						{label}
					</span>

					{headerActions}

					{Liferay.FeatureFlags['LPD-57922'] && (
						<UnifiedSidebarColorSchemeButton
							className="c-ml-2"
							colorScheme={colorScheme}
							colorSchemeSessionKey={colorSchemeSessionKey}
						/>
					)}
				</SidePanel.Title>
			</SidePanel.Header>

			{children}
		</SidePanel>
	);
}

UnifiedSidebar.Body = SidePanel.Body;
UnifiedSidebar.Top = UnifiedSidebarTop;
