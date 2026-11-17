/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	hideProductMenuIfPresent,
	onProductMenuVisibleChange,
} from '@liferay/layout-js-components-web';
import classNames from 'classnames';
import {useId} from 'frontend-js-components-web';
import {COOKIE_TYPES, sessionStorage} from 'frontend-js-web';
import PropTypes from 'prop-types';
import React, {useEffect, useMemo, useRef} from 'react';

import {APP_LAYOUT_CONTENT_CLASS_NAME} from '../constants/appLayoutClassName';
import {useConstants} from '../contexts/ConstantsContext';
import {
	useSetSidebarPanelId,
	useSidebarPanelId,
} from '../contexts/SidebarPanelIdContext';
import DragPreview from './DragPreview';
import KeyboardMovementText from './KeyboardMovementText';

const DEFAULT_SIDEBAR_PANELS = [];

export function AppLayout({
	contentChildren,
	sidebarPanels = DEFAULT_SIDEBAR_PANELS,
	toolbarChildren,
	sidebarPanelRef,
	configButtonRef,
}) {
	const setSidebarPanelId = useSetSidebarPanelId();
	const sidebarPanelId = useSidebarPanelId();
	const titleId = useId();

	const {portletNamespace} = useConstants();

	const SidebarPanel = useMemo(
		() =>
			sidebarPanels?.find(
				(sidebarPanel) => sidebarPanel.sidebarPanelId === sidebarPanelId
			)?.component,
		[sidebarPanelId, sidebarPanels]
	);

	const appLayoutContentRef = useRef();

	useEffect(() => {
		const handler = onProductMenuVisibleChange((visible) => {
			if (visible) {
				setSidebarPanelId(null);
			}
		});

		return () => {
			handler.removeListener();
		};
	}, [setSidebarPanelId]);

	useEffect(() => {
		if (SidebarPanel) {
			hideProductMenuIfPresent();
		}
	}, [SidebarPanel]);

	useEffect(() => {
		const key = `${portletNamespace}itemAdded`;

		const itemAdded = sessionStorage.getItem(key, COOKIE_TYPES.FUNCTIONAL);

		if (itemAdded) {
			appLayoutContentRef.current?.scrollTo(
				0,
				appLayoutContentRef.current?.scrollHeight
			);

			sessionStorage.removeItem(key);
		}
	}, [portletNamespace]);

	return (
		<>
			<div className="bg-white component-tbar tbar">
				<div className="container-fluid">
					<div className="cadmin px-1 tbar-nav">
						{toolbarChildren}
					</div>
				</div>
			</div>

			<div
				className={classNames(APP_LAYOUT_CONTENT_CLASS_NAME, {
					[`${APP_LAYOUT_CONTENT_CLASS_NAME}--with-sidebar`]:
						!!SidebarPanel,
				})}
				ref={appLayoutContentRef}
			>
				<DragPreview wrapperRef={appLayoutContentRef} />

				{contentChildren}

				<div
					aria-labelledby={titleId}
					className={classNames(
						'site_navigation_menu_editor_AppLayout-sidebar',
						{
							'site_navigation_menu_editor_AppLayout-sidebar--visible':
								!!SidebarPanel,
						}
					)}
					ref={sidebarPanelRef}
					tabIndex={-1}
				>
					{SidebarPanel && (
						<SidebarPanel
							configButtonRef={configButtonRef}
							titleId={titleId}
						/>
					)}
				</div>

				<KeyboardMovementText />
			</div>
		</>
	);
}

AppLayout.propTypes = {
	contentChildren: PropTypes.node,
	initialSidebarPanelId: PropTypes.string,
	sidebarPanels: PropTypes.arrayOf(
		PropTypes.shape({
			component: PropTypes.func.isRequired,
			sidebarPanelId: PropTypes.string.isRequired,
		})
	),
	toolbarChildren: PropTypes.node,
};

AppLayout.ToolbarItem = ({children, expand}) => {
	return (
		<li className={classNames('tbar-item', {'tbar-item-expand': expand})}>
			{children}
		</li>
	);
};

AppLayout.ToolbarItem.propTypes = {
	children: PropTypes.node,
	expand: PropTypes.bool,
};
