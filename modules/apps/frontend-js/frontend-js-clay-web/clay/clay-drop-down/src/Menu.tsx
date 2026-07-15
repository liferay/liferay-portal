/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {IPortalBaseProps, Overlay, useOverlayPosition} from '@clayui/shared';
import classNames from 'classnames';
import React, {useRef} from 'react';

import type {AlignPoints} from '@clayui/shared';

export const Align = {
	BottomCenter: 4,
	BottomLeft: 5,
	BottomRight: 3,
	LeftBottom: 11,
	LeftCenter: 6,
	LeftTop: 10,
	RightBottom: 9,
	RightCenter: 2,
	RightTop: 8,
	TopCenter: 0,
	TopLeft: 7,
	TopRight: 1,
} as const;

export interface IProps extends React.HTMLAttributes<HTMLDivElement> {

	/**
	 * Flag to indicate if menu is showing or not.
	 */
	active?: boolean;

	/**
	 * Flag to render the menu with the AI variant styling.
	 */
	ai?: boolean;

	/**
	 * HTML element that the menu should be aligned to
	 */
	alignElementRef: React.RefObject<HTMLElement>;

	/**
	 * Flag to align the DropDown menu within the viewport.
	 */
	alignmentByViewport?: boolean;

	/**
	 * Default position of menu element. Values come from above.
	 *
	 * Align.TopCenter = 0;
	 * Align.TopRight = 1;
	 * Align.RightCenter = 2;
	 * Align.BottomRight = 3;
	 * Align.BottomCenter = 4;
	 * Align.BottomLeft = 5;
	 * Align.LeftCenter = 6;
	 * Align.TopLeft = 7;
	 *
	 * Defaults to BottomLeft
	 *
	 * You can also pass an array of strings, corresponding to the points
	 * of the targetNode and sourceNode, `[sourceNode, targetNode]`.
	 *
	 * Points can be 't'(top), 'b'(bottom), 'c'(center), 'l'(left), 'r'(right).
	 * For example: `['tl', 'bl']` corresponds to the bottom left alignment.
	 */
	alignmentPosition?: number | AlignPoints;

	/**
	 * Flag to suggest or not the best region to align menu element.
	 */
	autoBestAlign?: boolean;

	/**
	 * Flag to indicate if clicking outside of the menu should automatically close it.
	 */
	closeOnClickOutside?: boolean;

	/**
	 * Props to add to the outer most container.
	 */
	containerProps?: IPortalBaseProps;

	/**
	 * @ignore
	 */
	deps?: Array<any>;

	/**
	 * Element ref to call focus() on after menu is closed via Escape key
	 * @deprecated since v3.80.0 - use `triggerRef` instead.
	 */
	focusRefOnEsc?: React.RefObject<HTMLElement>;

	/**
	 * Flag to indicate if menu is displaying a clay-icon on the left.
	 */
	hasLeftSymbols?: boolean;

	/**
	 * Flag to indicate if menu is displaying a clay-icon on the right.
	 */
	hasRightSymbols?: boolean;

	/**
	 * Adds utility class name `dropdown-menu-height-${height}`
	 */
	height?: 'auto';

	/**
	 * Flag to lock focus within the scope.
	 */
	lock?: boolean;

	/**
	 * Function for setting the offset of the menu from the trigger.
	 */
	offsetFn?: (points: AlignPoints) => [number, number];

	/**
	 * Callback function for when active state changes.
	 */
	onActiveChange?: (value: boolean) => void;

	/**
	 * Callback function for when active state changes.
	 * @deprecated since v3.52.0 - use `onActiveChange` instead.
	 */
	onSetActive?: (value: boolean) => void;

	/**
	 * Defines the reference of the elements that must be suppressed by the
	 * screen reader when the menu is opened.
	 */
	suppress?: Array<React.RefObject<HTMLElement>>;

	/**
	 * Reference of the element that triggers the Menu.
	 */
	triggerRef?: React.RefObject<HTMLElement>;

	/**
	 * The modifier class `dropdown-menu-width-${width}` makes the menu expand
	 * the full width of the page.
	 *
	 * - sm makes the menu 500px wide.
	 * - shrink makes the menu auto-adjust to text and max 240px wide.
	 * - full makes the menu 100% wide.
	 */
	width?: 'sm' | 'shrink' | 'full';
}

const Menu = React.forwardRef<HTMLDivElement, IProps>(
	(
		{
			active = false,
			ai,
			alignElementRef,
			alignmentByViewport = false,
			alignmentPosition = Align.BottomLeft,
			autoBestAlign = true,
			children,
			className,
			closeOnClickOutside = true,
			containerProps = {},
			deps = [active, children],
			hasLeftSymbols,
			hasRightSymbols,
			height,
			lock = false,
			offsetFn,
			onActiveChange,
			onSetActive,
			role = 'presentation',
			suppress,
			triggerRef,
			width,
			...otherProps
		}: IProps,
		ref
	) => {
		const setActive = onActiveChange ?? onSetActive;

		const menuInternalRef = useRef<HTMLDivElement | null>(null);
		const subPortalRef = useRef<HTMLDivElement | null>(null);

		let menuRef = menuInternalRef;

		if (ref) {
			menuRef = ref as React.MutableRefObject<HTMLDivElement | null>;
		}

		useOverlayPosition(
			{
				alignmentByViewport,
				alignmentPosition,
				autoBestAlign,
				getOffset: offsetFn,
				isOpen: active,
				ref: menuRef,
				triggerRef: alignElementRef,
			},
			deps
		);

		return (
			<Overlay
				isCloseOnInteractOutside={closeOnClickOutside}
				isKeyboardDismiss
				isModal={lock}
				isOpen={active}
				menuRef={menuRef}
				onClose={() => setActive!(false)}
				portalRef={subPortalRef}
				suppress={suppress}
				triggerRef={triggerRef ?? alignElementRef}
			>
				<div {...containerProps} ref={subPortalRef}>
					<div
						{...otherProps}
						aria-hidden={!active ? true : undefined}
						aria-modal={lock ? true : undefined}
						className={classNames('dropdown-menu', className, {
							'dropdown-menu-ai': ai,
							'dropdown-menu-indicator-end': hasRightSymbols,
							'dropdown-menu-indicator-start': hasLeftSymbols,
							[`dropdown-menu-height-${height}`]: height,
							[`dropdown-menu-width-${width}`]: width,
							'show': active,
						})}
						ref={menuRef}
						role={role}
						tabIndex={active ? 0 : undefined}
					>
						{children}
					</div>
				</div>
			</Overlay>
		);
	}
);

Menu.displayName = 'ClayDropDownMenu';

export default Menu;
