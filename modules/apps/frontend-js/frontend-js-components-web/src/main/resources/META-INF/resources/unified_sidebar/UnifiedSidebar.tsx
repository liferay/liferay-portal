/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {SidePanel} from '@clayui/core';
import ClaySticker from '@clayui/sticker';
import {sub} from 'frontend-js-web';
import React from 'react';

export interface UnifiedSidebarProps {
	canonicalName?: string;
	categoryImageUrl: string;
	children?: React.ReactNode;
	containerRef?: React.RefObject<HTMLElement | null>;
	label: string;
	navigationId?: string;
	onOpenChange?: (open: boolean) => void;
	open?: boolean;
	testId?: string;
}

const PANEL_WIDTH = 280;

const CLOSE_BUTTON_PROPS = Liferay.FeatureFlags['LPD-57922']
	? {
			className:
				'btn btn-monospaced btn-outline-borderless btn-outline-secondary btn-sm side-navigation-close-button',
		}
	: undefined;

const HEADER_MESSAGES = {
	backAriaLabel: Liferay.Language.get('go-back'),
	closeAriaLabel: Liferay.Language.get('close-product-menu'),
};

function UnifiedSidebarTop({children}: {children?: React.ReactNode}) {
	return <>{children}</>;
}

export default function UnifiedSidebar({
	canonicalName,
	categoryImageUrl,
	children,
	containerRef,
	label,
	navigationId,
	onOpenChange,
	open = false,
	testId = 'unified-sidebar',
}: UnifiedSidebarProps) {
	return (
		<SidePanel
			aria-label={sub(Liferay.Language.get('x-menu'), label)}
			closeOnEscape={false}
			containerRef={
				containerRef as unknown as React.RefObject<HTMLElement>
			}
			data-canonical-name={canonicalName}
			data-qa-id={testId}
			data-testid={testId}
			direction="left"
			displayType="light"
			id={navigationId}
			onOpenChange={onOpenChange}
			open={open}
			panelWidth={PANEL_WIDTH}
			position="fixed"
		>
			<SidePanel.Header
				className="c-mx-1 c-px-2"
				closeButtonProps={CLOSE_BUTTON_PROPS}
				data-qa-id="unifiedSidebarHeader"
				messages={HEADER_MESSAGES}
			>
				<SidePanel.Title className="align-items-center c-my-0 d-flex">
					<ClaySticker
						borderless
						className="c-mr-1"
						data-qa-id="unifiedSidebarProductIcon"
						displayType="outline"
					>
						<img
							alt=""
							className="c-mx-1"
							src={categoryImageUrl}
						/>
					</ClaySticker>

					<span
						className="c-ml-2 text-5"
						data-qa-id="unifiedSidebarLabel"
					>
						{label}
					</span>
				</SidePanel.Title>
			</SidePanel.Header>

			{children}
		</SidePanel>
	);
}

UnifiedSidebar.Body = SidePanel.Body;
UnifiedSidebar.Top = UnifiedSidebarTop;
