/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayIconSpriteContext} from '@clayui/icon';
import {ClayLinkContext} from '@clayui/link';
import classNames from 'classnames';
import React, {useCallback, useEffect, useRef, useState} from 'react';

import './UnifiedSidebar.scss';

export interface UnifiedSidebarProps {
	ariaLabel?: string;
	children?: React.ReactNode;
	containerRef?: React.RefObject<HTMLElement | null>;
	linkComponent?: React.ComponentType<any>;
	navigationId?: string;
	onPinnedChange?: (pinned: boolean) => void;
	pinned?: boolean;
	spritemap?: string;
	title: string;
}

function UnifiedSidebarHeader({children}: {children?: React.ReactNode}) {
	return <>{children}</>;
}

function UnifiedSidebarTop({children}: {children?: React.ReactNode}) {
	return <>{children}</>;
}

function UnifiedSidebarBody({children}: {children?: React.ReactNode}) {
	return <>{children}</>;
}

export default function UnifiedSidebar({
	ariaLabel,
	children,
	containerRef,
	linkComponent,
	navigationId,
	pinned = false,
	spritemap,
	title,
}: UnifiedSidebarProps) {
	let headerContent: React.ReactNode = null;
	let topContent: React.ReactNode = null;
	let bodyContent: React.ReactNode = null;
	const bodyChildren: React.ReactNode[] = [];

	React.Children.forEach(children, (child) => {
		if (React.isValidElement(child)) {
			if (child.type === UnifiedSidebarHeader) {
				headerContent = child.props.children;
			}
			else if (child.type === UnifiedSidebarTop) {
				topContent = child.props.children;
			}
			else if (child.type === UnifiedSidebarBody) {
				bodyContent = child.props.children;
			}
			else {
				bodyChildren.push(child);
			}
		}
		else if (child !== null && child !== undefined) {
			bodyChildren.push(child);
		}
	});

	useEffect(() => {
		const container = containerRef?.current;

		if (!container) {
			return;
		}

		container.style.setProperty('--c-side-panel-width', '320px');
		container.classList.toggle('c-slideout-push-start', pinned);
	}, [containerRef, pinned]);

	const bodyRef = useRef<HTMLDivElement>(null);

	const [clipped, setClipped] = useState({bottom: false, top: false});

	const updateClipped = useCallback(() => {
		const body = bodyRef.current;

		if (!body) {
			return;
		}

		const bottom =
			body.scrollTop + body.clientHeight < body.scrollHeight - 1;
		const top = body.scrollTop > 0;

		setClipped((previous) =>
			previous.bottom === bottom && previous.top === top
				? previous
				: {bottom, top}
		);
	}, []);

	useEffect(() => {
		const body = bodyRef.current;

		if (!body) {
			return;
		}

		updateClipped();

		const resizeObserver = new ResizeObserver(updateClipped);

		resizeObserver.observe(body);

		Array.from(body.children).forEach((child) =>
			resizeObserver.observe(child)
		);

		return () => resizeObserver.disconnect();
	}, [updateClipped]);

	const content = (
		<div
			className={classNames(
				'c-slideout c-slideout-fixed c-slideout-push c-slideout-start',
				{'c-slideout-shown': pinned}
			)}
		>
			<nav
				aria-label={ariaLabel ?? title}
				className={classNames('sidebar', {
					'sidebar-clipped-bottom': clipped.bottom,
					'sidebar-clipped-top': clipped.top,
				})}
				id={navigationId}
			>
				<div className="sidebar-header">
					<span className="sidebar-title">{title}</span>

					{headerContent}
				</div>

				{topContent}

				<div
					className="sidebar-body"
					onScroll={updateClipped}
					ref={bodyRef}
				>
					{bodyContent ?? bodyChildren}
				</div>
			</nav>
		</div>
	);

	const withSpritemap = (
		<ClayIconSpriteContext.Provider
			value={spritemap ?? Liferay.Icons.spritemap}
		>
			{content}
		</ClayIconSpriteContext.Provider>
	);

	if (linkComponent) {
		return (
			<ClayLinkContext.Provider value={linkComponent}>
				{withSpritemap}
			</ClayLinkContext.Provider>
		);
	}

	return withSpritemap;
}

UnifiedSidebar.Body = UnifiedSidebarBody;
UnifiedSidebar.Header = UnifiedSidebarHeader;
UnifiedSidebar.Top = UnifiedSidebarTop;
