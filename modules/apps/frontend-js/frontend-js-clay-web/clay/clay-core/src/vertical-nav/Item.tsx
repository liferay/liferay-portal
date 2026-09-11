/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import {useProvider} from '@clayui/provider';
import {Keys, setElementFullHeight, useId} from '@clayui/shared';
import classNames from 'classnames';
import React, {useContext, useLayoutEffect, useRef, useState} from 'react';
import {CSSTransition} from 'react-transition-group';

import {Collection} from '../collection';
import {Nav} from '../nav';
import {useSetManualFocus, useVerticalNavContext} from './context';

interface IProps<T> extends React.HTMLAttributes<HTMLLIElement> {

	/**
	 * Flag to indicate if item is active.
	 * @deprecated since version 3.94.0 - use the `active` property on the
	 * root component.
	 */
	active?: boolean;

	/**
	 * The contents of the component.
	 */
	children?: React.ReactNode;

	/**
	 * Link href for item.
	 */
	href?: string;

	/**
	 * @ignore
	 */
	index?: number;

	/**
	 * @ignore
	 * @deprecated
	 */
	initialExpanded?: boolean;

	/**
	 * Property to inform the dynamic data of the tree.
	 */
	items?: Array<T>;

	/**
	 * @ignore
	 */
	keyValue?: React.Key;

	/**
	 * Properties to pass to the menubar action
	 */
	menubarAction?: React.ComponentProps<typeof ClayButtonWithIcon>;

	/**
	 * @ignore
	 */
	textValue?: string;
}

const ParentContext = React.createContext<React.RefObject<
	HTMLButtonElement | HTMLAnchorElement
> | null>(null);

export function Item<T extends Record<string, any>>({
	active: depreactedActive,
	children,
	href,
	index: _,
	initialExpanded,
	items,
	keyValue,
	menubarAction,
	onClick,
	textValue: _textValue,
	...otherProps
}: IProps<T>) {
	const setManualFocus = useSetManualFocus();
	const {
		activeKey,
		ariaCurrent = 'page',
		childrenRoot,
		close,
		expandedKeys,
		firstKey,
		focusedElement,
		open,
		spritemap,
		toggle,
	} = useVerticalNavContext();

	const {prefersReducedMotion} = useProvider();

	const itemRef = useRef<HTMLButtonElement | HTMLAnchorElement>(null);
	const menusRef = useRef<HTMLUListElement | null>(null);
	const parentItemRef = useContext(ParentContext);

	// State only for compatibility with the old compositing version where state
	// was kept on each Item instead of centralized with `expandedKeys`.

	const [expanded, setExpanded] = useState(initialExpanded);
	const [tabIndex, setTabIndex] = useState<0 | -1>(-1);

	const isOldVersion = typeof initialExpanded !== 'undefined';

	const isExpanded = isOldVersion ? expanded : expandedKeys.has(keyValue!);

	const active = depreactedActive ?? activeKey === keyValue;

	const ariaControlsId = useId();

	useLayoutEffect(() => {
		const isInitialTabStop = !focusedElement && firstKey === keyValue;
		const isCurrentTabStop =
			focusedElement && itemRef.current === focusedElement;

		setTabIndex(isInitialTabStop || isCurrentTabStop ? 0 : -1);
	}, [firstKey, focusedElement, keyValue]);

	const {'aria-describedby': ariaDescribedby, ...navItemProps} = otherProps;

	return (
		<Nav.Item role="none" {...navItemProps}>
			<Nav.Link
				active={active}
				aria-controls={items ? ariaControlsId : undefined}
				aria-current={active ? ariaCurrent ?? undefined : undefined}
				aria-describedby={ariaDescribedby}
				aria-expanded={items ? isExpanded : undefined}
				className={menubarAction ? 'menubar-action-1' : ''}
				collapsed={!isExpanded}
				href={href}
				onClick={(event) => {
					if (onClick) {
						onClick(
							event as unknown as React.MouseEvent<
								HTMLLIElement,
								MouseEvent
							>
						);
					}

					if (isOldVersion) {
						setExpanded(!expanded);
					}
					else {
						toggle(keyValue!);
					}
				}}
				onKeyDown={(event) => {
					switch (event.key) {
						case Keys.Right: {
							if (items && !isExpanded) {
								if (isOldVersion) {
									setExpanded(true);
								}
								else {
									open(keyValue!);
								}
							}
							else if (items && menusRef.current) {
								const firstItemElement =
									menusRef.current.querySelector<HTMLElement>(
										'.nav-link:not([disabled])'
									);

								setManualFocus(firstItemElement);
							}
							break;
						}
						case Keys.Left: {
							if (items && isExpanded) {
								if (isOldVersion) {
									setExpanded(false);
								}
								else {
									close(keyValue!);
								}
							}
							else if (!items && parentItemRef) {
								setManualFocus(parentItemRef.current);
							}
							break;
						}
						default:
							break;
					}
				}}
				ref={itemRef}
				role="menuitem"
				showIcon={!!items}
				spritemap={spritemap}
				tabIndex={tabIndex}
			>
				{children}
			</Nav.Link>

			{menubarAction && (
				<ClayButtonWithIcon
					{...menubarAction}
					className={classNames(
						menubarAction.className,
						'menubar-action'
					)}
					displayType={
						menubarAction.displayType === null
							? null
							: menubarAction.displayType || 'secondary'
					}
					monospaced={
						menubarAction.monospaced === false ? false : true
					}
					size={
						menubarAction.size === null
							? undefined
							: menubarAction.size || 'xs'
					}
					symbol={menubarAction.symbol || 'plus'}
				/>
			)}

			{items && (
				<CSSTransition
					className={isExpanded ? undefined : 'collapse'}
					classNames={{
						enter: 'collapsing',
						enterActive: `show`,
						enterDone: 'show',
						exit: `show`,
						exitActive: 'collapsing',
					}}
					in={isExpanded}
					onEnter={(element: HTMLElement) =>
						element.setAttribute('style', 'height: 0px')
					}
					onEntered={(element: HTMLElement) =>
						element.removeAttribute('style')
					}
					onEntering={(element: HTMLElement) =>
						setElementFullHeight(element)
					}
					onExit={(element) => setElementFullHeight(element)}
					onExiting={(element) =>
						element.setAttribute('style', 'height: 0px')
					}
					timeout={prefersReducedMotion ? 0 : 250}
					unmountOnExit
				>
					<Nav id={ariaControlsId} ref={menusRef} role="menu" stacked>
						<ParentContext.Provider value={itemRef}>
							<Collection<T> items={items} parentKey={keyValue}>
								{childrenRoot.current}
							</Collection>
						</ParentContext.Provider>
					</Nav>
				</CSSTransition>
			)}
		</Nav.Item>
	);
}

Item.displayName = 'Item';
