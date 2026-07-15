/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useControlledState, useIsMobileDevice} from '@clayui/shared';
import React, {useMemo, useRef} from 'react';

import Caption from './Caption';
import ClayDropDown from './DropDown';
import {ClayDropDownWithDrilldown} from './DropDownWithDrilldown';
import Help from './Help';
import {ClayDropDownContext, DropDownItems, findNested} from './Items';
import Menu from './Menu';
import Search from './Search';

import type {AlignPoints, InternalDispatch} from '@clayui/shared';

import type {Item} from './Items';
import type {IProps as SearchProps} from './Search';
import type {DropDownHTMLAttributes} from './types';

export type Props = {

	/**
	 * Flag to indicate if the DropDown menu is active or not (controlled).
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
	 * Informational text that appears at the end or above the `footerContent` prop.
	 */
	caption?: string;

	className?: string;

	closeOnClickOutside?: boolean;

	/**
	 * HTML element tag that the container should render.
	 */
	containerElement?: React.ComponentProps<
		typeof ClayDropDown
	>['containerElement'];

	/**
	 * Property to set the initial value of `active` (uncontrolled).
	 */
	defaultActive?: boolean;

	/**
	 * Add an action button or any other element you want to be fixed position to the
	 * footer from the DropDown.
	 */
	footerContent?: React.ReactElement;

	/**
	 * Add informational text at the top of DropDown.
	 */
	helpText?: string;

	/**
	 * List of items to display in drop down menu
	 */
	items: Array<Item>;

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
	 * Prop to use language keys.
	 */
	messages?: string;

	/**
	 * Function for setting the offset of the menu from the trigger.
	 */
	offsetFn?: (points: AlignPoints) => [number, number];

	/**
	 * Callback for when the active state changes (controlled).
	 */
	onActiveChange?: InternalDispatch<boolean>;

	/**
	 * Callback will always be called when the user is interacting with search.
	 */
	onSearchValueChange?: (newValue: string) => void;

	/**
	 * Flag indicating if the menu should be rendered lazily
	 */
	renderMenuOnClick?: boolean;

	/**
	 * Pass the props to the Search component.
	 */
	searchProps?: SearchProps;

	/**
	 * The value that will be passed to the search input element.
	 */
	searchValue?: string;

	/**
	 * Flag to show search at the top of the DropDown.
	 */
	searchable?: boolean;

	/**
	 * The path to the SVG spritemap file containing the icons.
	 */
	spritemap?: string;

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
} & DropDownHTMLAttributes;

let counter = 0;

function getId() {
	return `id-${counter++}`;
}

function transformTreeToLinkedList(items: any, id: string, menu: any = {}) {
	menu[id] = [];
	items?.forEach((item: any, index: number) => {
		const keys = Object.keys(item);
		menu[id][index] = {};
		keys.forEach((key) => {
			if (item['type'] === 'contextual' && Array.isArray(item[key])) {
				const childId = getId();
				menu[id][index].child = childId;
				transformTreeToLinkedList(item[key], childId, menu);
			}
			else {
				if (item[key] === 'contextual') {
					menu[id][index].type = 'item';
				}
				else {
					menu[id][index][key] = item[key];
				}
			}
		});
	});

	return menu;
}

export function ClayDropDownWithItems({
	active,
	alignmentByViewport,
	alignmentPosition,
	caption,
	className,
	closeOnClickOutside,
	containerElement,
	defaultActive = false,
	footerContent,
	helpText,
	items,
	menuElementAttrs,
	menuHeight,
	menuWidth,
	messages: _messages,
	offsetFn,
	onActiveChange,
	onSearchValueChange = () => {},
	renderMenuOnClick,
	searchable,
	searchProps,
	searchValue = '',
	triggerIcon = null,
	spritemap,
	trigger,
	...otherProps
}: Props) {
	const triggerElementRef = useRef<HTMLButtonElement | null>(null);
	const [internalActive, setInternalActive] = useControlledState({
		defaultName: 'defaultActive',
		defaultValue: defaultActive,
		handleName: 'onActiveChange',
		name: 'active',
		onChange: onActiveChange,
		value: active,
	});
	const hasRightSymbols = useMemo(
		() => !!findNested(items, 'symbolRight'),
		[items]
	);
	const hasLeftSymbols = useMemo(
		() => !!findNested(items, 'symbolLeft'),
		[items]
	);
	const hasContextual = useMemo(
		() => items.some((item) => item.type === 'contextual'),
		[items]
	);
	const Wrap = footerContent ? 'form' : React.Fragment;
	const isMobile = useIsMobileDevice();
	const activeMenu = useMemo(() => getId(), []);
	const linkedList = useMemo(() => {
		if (isMobile && hasContextual) {
			return transformTreeToLinkedList(items, activeMenu);
		}
	}, [items, hasContextual, isMobile]);
	if (hasContextual && isMobile) {
		return (
			<ClayDropDownWithDrilldown
				{...otherProps}
				active={internalActive}
				alignmentByViewport={alignmentByViewport}
				alignmentPosition={alignmentPosition}
				caption={caption}
				className={className}
				containerElement={containerElement}
				defaultActiveMenu={activeMenu}
				footerContent={footerContent}
				helpText={helpText}
				menuElementAttrs={menuElementAttrs}
				menuHeight={menuHeight}
				menuWidth={menuWidth}
				menus={linkedList}
				offsetFn={offsetFn}
				onActiveChange={setInternalActive}
				onSearchValueChange={onSearchValueChange}
				renderMenuOnClick={renderMenuOnClick}
				searchProps={searchProps}
				searchValue={searchValue}
				searchable={searchable}
				trigger={trigger}
				triggerIcon={triggerIcon}
			/>
		);
	}
	else {
		return (
			<ClayDropDown
				{...otherProps}
				active={internalActive}
				alignmentByViewport={alignmentByViewport}
				alignmentPosition={alignmentPosition}
				className={className}
				closeOnClickOutside={closeOnClickOutside}
				containerElement={containerElement}
				hasLeftSymbols={hasLeftSymbols}
				hasRightSymbols={hasRightSymbols}
				menuElementAttrs={menuElementAttrs}
				menuHeight={menuHeight}
				menuWidth={menuWidth}
				offsetFn={offsetFn}
				onActiveChange={setInternalActive}
				renderMenuOnClick={renderMenuOnClick}
				trigger={React.cloneElement(trigger, {
					ref: (node: HTMLButtonElement) => {
						if (node) {
							triggerElementRef.current = node;

							// Call the original ref, if any.

							const {ref} = trigger;
							if (typeof ref === 'function') {
								ref(node);
							}
						}
					},
				})}
				triggerIcon={triggerIcon}
			>
				<ClayDropDownContext.Provider
					value={{
						close: () => {
							setInternalActive(false);
							triggerElementRef.current?.focus();
						},
					}}
				>
					{helpText && <Help>{helpText}</Help>}

					{searchable && (
						<Search
							{...searchProps}
							onChange={onSearchValueChange}
							spritemap={spritemap}
							value={searchValue}
						/>
					)}

					<Wrap>
						{footerContent ? (
							<div className="inline-scroller">
								<DropDownItems
									items={items}
									spritemap={spritemap}
								/>
							</div>
						) : (
							<DropDownItems
								items={items}
								spritemap={spritemap}
							/>
						)}

						{caption && <Caption>{caption}</Caption>}

						{footerContent && (
							<div
								className="dropdown-section"
								role="presentation"
							>
								{footerContent}
							</div>
						)}
					</Wrap>
				</ClayDropDownContext.Provider>
			</ClayDropDown>
		);
	}
}

ClayDropDownWithItems.displayName = 'ClayDropDownWithItems';
