/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {KeyboardArrowsIndicator, __NOT_PUBLIC_COLLECTION} from '@clayui/core';
import ClayIcon from '@clayui/icon';
import {
	ClayPortal,
	FOCUSABLE_ELEMENTS,
	Keys,
	getFocusableList,
	useControlledState,
	useNavigation,
} from '@clayui/shared';
import classNames from 'classnames';
import React, {useCallback, useMemo, useRef, useState} from 'react';

import Action from './Action';
import Caption from './Caption';
import Divider from './Divider';
import {DropDownContext} from './DropDownContext';
import {FocusMenu} from './FocusMenu';
import Group from './Group';
import Help from './Help';
import Item from './Item';
import ItemList from './ItemList';
import Menu from './Menu';
import Search from './Search';
import Section from './Section';

import type {AlignPoints, InternalDispatch} from '@clayui/shared';

import type {DropDownHTMLAttributes} from './types';

const {Collection} = __NOT_PUBLIC_COLLECTION;

interface IProps<T> extends DropDownHTMLAttributes {

	/**
	 * Flag to indicate if the DropDown menu is active or not (controlled).
	 *
	 * This API is generally used in conjunction with `closeOnClickOutside=true`
	 * since often we are controlling the active state by clicking another element
	 * within the document.
	 */
	active?: boolean;

	/**
	 * Flag to render the menu with the AI variant styling.
	 */
	ai?: boolean;

	/**
	 * Flag to align the DropDown menu within the viewport.
	 */
	alignmentByViewport?: boolean;

	/**
	 * Default position of menu element. Values come from `./Menu`.
	 */
	alignmentPosition?: number | AlignPoints;

	/**
	 * Children content to render a dynamic or static content.
	 */
	children:
		| React.ReactNode
		| ((item: T, index?: number) => React.ReactElement);

	/**
	 * Flag that indicates whether to close DropDown when clicking on the item.
	 */
	closeOnClick?: boolean;

	closeOnClickOutside?: boolean;

	/**
	 * HTML element tag that the container should render.
	 */
	containerElement?: React.JSXElementConstructor<any> | 'div' | 'li';

	/**
	 *  Property to set the default value of `active` (uncontrolled).
	 */
	defaultActive?: boolean;

	/**
	 * Flag to render the `KeyboardArrowsIndicator` alongside the DropDown
	 * menu, hinting that up and down arrow keys can be used to navigate
	 * the menu items. The indicator floats to the right of the menu and
	 * flips to the left when it would overflow the viewport. It is only
	 * rendered while the menu is open and visible while the menu has
	 * keyboard focus.
	 */
	displayKeyboardArrowsIndicator?: boolean;

	/**
	 * Defines the name of the property key that is used in the items filter
	 * test (Dynamic content).
	 */
	filterKey?: string;

	/**
	 * Flag to indicate if menu contains icon symbols on the left side.
	 */
	hasLeftSymbols?: boolean;

	/**
	 * Flag to indicate if menu contains icon symbols on the right side.
	 */
	hasRightSymbols?: boolean;

	/**
	 * Property to render content with dynamic data.
	 */
	items?: Array<T>;

	/**
	 * Prop to pass DOM element attributes to DropDown.Menu.
	 */
	menuElementAttrs?: React.HTMLAttributes<HTMLDivElement> &
		Pick<React.ComponentProps<typeof Menu>, 'containerProps'>;

	/**
	 * Adds utility class name `dropdown-menu-height-${height}`
	 */
	menuHeight?: 'auto';

	/**
	 * The modifier class `dropdown-menu-width-${width}` makes the menu expand
	 * the full width of the page.
	 *
	 * - sm makes the menu 500px wide.
	 * - shrink makes the menu auto-adjust to text and max 240px wide.
	 * - full makes the menu 100% wide.
	 */
	menuWidth?: 'sm' | 'shrink' | 'full';

	/**
	 * Function for setting the offset of the menu from the trigger.
	 */
	offsetFn?: (points: AlignPoints) => [number, number];

	/**
	 * Callback for when the active state changes (controlled).
	 *
	 * This API is generally used in conjunction with `closeOnClickOutside=true`
	 * since often we are controlling the active state by clicking another element
	 * within the document.
	 */
	onActiveChange?: InternalDispatch<boolean>;

	/**
	 * Flag indicating if the menu should be rendered lazily
	 */
	renderMenuOnClick?: boolean;

	/**
	 * Element that is used as the trigger which will activate the dropdown on click.
	 */
	trigger: React.ReactElement & {
		ref?: (node: HTMLButtonElement | null) => void;
	};

	/**
	 * Flag indicating if the caret icon should be displayed on the right side.
	 */
	triggerIcon?: string | null;
}

const List = React.forwardRef<
	HTMLUListElement,
	React.HTMLAttributes<HTMLUListElement>
>(function List({children, ...otherProps}, ref) {
	return (
		<ul {...otherProps} className="list-unstyled" ref={ref}>
			{children}
		</ul>
	);
});

let counter = 0;

function DropDown<T>({
	active,
	ai,
	alignmentByViewport,
	alignmentPosition,
	children,
	className,
	closeOnClick = false,
	closeOnClickOutside,
	containerElement: ContainerElement = 'div',
	defaultActive = false,
	displayKeyboardArrowsIndicator = false,
	filterKey,
	hasLeftSymbols,
	hasRightSymbols,
	items,
	menuElementAttrs,
	menuHeight,
	menuWidth,
	offsetFn,
	onActiveChange,
	renderMenuOnClick = false,
	role = 'menu',
	trigger,
	triggerIcon = null,
	...otherProps
}: IProps<T>) {
	const triggerElementRef = useRef<HTMLButtonElement | null>(null);
	const menuElementRef = useRef<HTMLDivElement>(null);

	const [initialized, setInitialized] = useState(!renderMenuOnClick);

	const [internalActive, setInternalActive] = useControlledState({
		defaultName: 'defaultActive',
		defaultValue: defaultActive,
		handleName: 'onActiveChange',
		name: 'active',
		onChange: onActiveChange,
		value: active,
	});

	const [search, setSearch] = useState('');

	const ariaControls = useMemo(() => {
		counter++;

		return `clay-dropdown-menu-${counter}`;
	}, []);

	const openMenu = useCallback(
		(value: boolean) => {
			if (!initialized) {
				setInitialized(true);
			}

			setInternalActive(value);
		},
		[initialized]
	);

	const {navigationProps} = useNavigation({
		activation: 'manual',
		containerRef: menuElementRef,
		loop: true,
		orientation: 'vertical',
		typeahead: true,
		visible: internalActive,
	});

	return (
		<ContainerElement
			{...otherProps}
			className={classNames('dropdown', className)}
		>
			{React.cloneElement(trigger, {
				'aria-controls': internalActive ? ariaControls : undefined,
				'aria-expanded': internalActive,
				'aria-haspopup': 'true',
				'children':
					React.isValidElement(trigger) &&

					// @ts-ignore

					trigger?.type.displayName === 'ClayButton' &&
					triggerIcon ? (
						<>
							{trigger.props.children}{' '}
							<ClayIcon className="ml-1" symbol={triggerIcon} />
						</>
					) : (
						trigger.props.children
					),
				'className': classNames(
					'dropdown-toggle',
					trigger.props.className
				),
				'onClick': (event: React.MouseEvent<HTMLButtonElement>) => {
					if (trigger.props.onClick) {
						trigger.props.onClick(event);
					}

					openMenu(!internalActive);
				},
				'onKeyDown': (
					event: React.KeyboardEvent<HTMLButtonElement>
				) => {
					if (trigger.props.onKeyDown) {
						trigger.props.onKeyDown(event);
					}

					if (event.key === Keys.Spacebar) {
						openMenu(!internalActive);
					}

					if (event.key === Keys.Down) {
						event.preventDefault();
						event.stopPropagation();

						openMenu(true);
					}

					if (internalActive && event.key === Keys.Down) {
						event.preventDefault();
						event.stopPropagation();

						const list = getFocusableList(menuElementRef);

						if (list.length) {
							list[0]!.focus();
						}
					}

					if ([Keys.Spacebar, Keys.Down].includes(event.key)) {
						event.preventDefault();
					}
				},
				'ref': (node: HTMLButtonElement) => {
					triggerElementRef.current = node;

					// Call the original ref, if any.

					const {ref} = trigger;
					if (typeof ref === 'function') {
						ref(node);
					}
				},
			})}

			{initialized && (
				<Menu
					{...menuElementAttrs}
					active={internalActive}
					ai={ai}
					alignElementRef={triggerElementRef}
					alignmentByViewport={alignmentByViewport}
					alignmentPosition={alignmentPosition}
					closeOnClickOutside={closeOnClickOutside}
					hasLeftSymbols={hasLeftSymbols}
					hasRightSymbols={hasRightSymbols}
					height={menuHeight}
					id={ariaControls}
					offsetFn={offsetFn}
					onActiveChange={setInternalActive}
					onKeyDown={(event) => {
						if (menuElementAttrs?.onKeyDown) {
							menuElementAttrs.onKeyDown(event);
						}

						if (event.key === Keys.Tab) {
							event.preventDefault();

							openMenu(false);

							const list = Array.from<HTMLElement>(
								document.querySelectorAll(
									FOCUSABLE_ELEMENTS.join(',')
								)
							);
							const position = list.indexOf(
								triggerElementRef.current!
							);

							const nextElement = list[position + 1];

							if (nextElement) {
								nextElement.focus();
							}
						}

						navigationProps.onKeyDown(event);
					}}
					ref={menuElementRef}
					suppress={[triggerElementRef, menuElementRef]}
					triggerRef={triggerElementRef}
					width={menuWidth}
				>
					<FocusMenu
						condition={internalActive}
						onRender={() => {

							// After a few milliseconds querying the elements in the DOM
							// inside the menu. This especially when the menu is not
							// rendered yet only after the menu is opened, React needs
							// to commit the changes to the DOM so that the elements are
							// visible and we can move the focus.

							setTimeout(() => {
								const list = getFocusableList(menuElementRef);

								if (list.length) {
									list[0]!.focus();
								}
							}, 10);
						}}
					>
						<DropDownContext.Provider
							value={{
								close: () => {
									setInternalActive(false);
									triggerElementRef.current?.focus();
								},
								closeOnClick,
								filterKey,
								onSearch: setSearch,
								search,
								tabFocus: false,
							}}
						>
							{children instanceof Function ? (
								<Collection<T>
									as={List}
									items={items}
									role={role}
								>
									{children}
								</Collection>
							) : (
								children
							)}
						</DropDownContext.Provider>
					</FocusMenu>
				</Menu>
			)}

			{internalActive && displayKeyboardArrowsIndicator && (
				<ClayPortal>
					<KeyboardArrowsIndicator
						anchorRef={menuElementRef}
						direction="vertical"
					/>
				</ClayPortal>
			)}
		</ContainerElement>
	);
}

DropDown.Action = Action;
DropDown.Caption = Caption;
DropDown.Divider = Divider;
DropDown.Group = Group;
DropDown.Help = Help;
DropDown.Menu = Menu;
DropDown.Item = Item;
DropDown.ItemList = ItemList;
DropDown.Search = Search;
DropDown.Section = Section;

export default DropDown;
