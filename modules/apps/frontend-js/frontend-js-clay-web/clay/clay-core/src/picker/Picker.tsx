/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Button from '@clayui/button';
import Icon from '@clayui/icon';
import {
	ClayPortal,
	InternalDispatch,
	Keys,
	Overlay,
	getFocusableList,
	isAppleDevice,
	isTypeahead,
	sub,
	useControlledState,
	useId,
	useInteractionFocus,
	useIsMobileDevice,
	useNavigation,
	useOverlayPosition,
} from '@clayui/shared';
import classNames from 'classnames';
import React, {useCallback, useEffect, useRef, useState} from 'react';

import {Collection, useCollection} from '../collection';
import {KeyboardArrowsIndicator} from '../keyboard-arrows-indicator';
import {LiveAnnouncer} from '../live-announcer';
import {Search} from './Search';
import {PickerContext} from './context';
import {useSearch} from './useSearch';
import {useTriggerLabel} from './useTriggerLabel';

import type {ICollectionProps} from '../collection';
import type {AnnouncerAPI} from '../live-announcer';

export type PickerMessages = {
	itemDescribedby: string;
	itemSelected: string;
	noResultsFound: string;
	scrollToBottomAriaLabel: string;
	scrollToTopAriaLabel: string;
	searchPlaceholder: string;
};

type Props<T> = {

	/**
	 * @ignore
	 */
	'UNSAFE_behavior'?: 'secondary';

	/**
	 * Sets the className for the React.Portal Menu element.
	 */
	'UNSAFE_menuClassName'?: string;

	/**
	 * Flag to indicate if the DropDown menu is active or not (controlled).
	 */
	'active'?: boolean;

	/**
	 * The global `aria-describedby` attribute identifies the element that
	 * describes the component.
	 */
	'aria-describedby'?: string;

	/**
	 * The `aria-label` attribute defines a string value that labels an interactive
	 * element.
	 */
	'aria-label'?: string;

	/**
	 * The `aria-labelledby` attribute identifies the element (or elements) that
	 * labels the element it is applied to.
	 */
	'aria-labelledby'?: string;

	/**
	 * Custom trigger component.
	 */
	'as'?:
		| 'button'
		| React.ForwardRefExoticComponent<any>
		| ((props: React.HTMLAttributes<HTMLElement>) => JSX.Element);

	/**
	 * Sets the CSS className for the component.
	 */
	'className'?: string;

	/**
	 *  Property to set the default value of `active` (uncontrolled).
	 */
	'defaultActive'?: boolean;

	/**
	 * The initial selected key (uncontrolled).
	 */
	'defaultSelectedKey'?: React.Key;

	/**
	 * Direction the menu will render relative to the Picker.
	 */
	'direction'?: 'bottom' | 'top';

	/**
	 * Flag to indicate that the component is disabled.
	 */
	'disabled'?: boolean;

	/**
	 * Flag to render the `KeyboardArrowsIndicator` alongside the Picker
	 * trigger, hinting that up and down arrow keys can be used to navigate
	 * the option list. The indicator floats to the right of the trigger
	 * and flips to the left when it would overflow the viewport. It is
	 * only rendered while the menu is open.
	 */
	'displayKeyboardArrowsIndicator'?: boolean;

	/**
	 * Defines the name of the property key that is used in the items filter
	 * test.
	 */
	'filterKey'?: string;

	/**
	 * The id of the component.
	 */
	'id'?: string;

	/**
	 * Messages for the Picker.
	 */
	'messages'?: Partial<PickerMessages>;

	/**
	 * Flag to make the component hybrid, when identified it is on a mobile
	 * device it will use the native selector.
	 */
	'native'?: boolean;

	/**
	 * Callback for when the active state changes (controlled).
	 */
	'onActiveChange'?: InternalDispatch<boolean>;

	/**
	 * Callback calling when an option is selected.
	 */
	'onSelectionChange'?: InternalDispatch<React.Key>;

	/**
	 * Text that appears when you don't have an item selected.
	 */
	'placeholder'?: string;

	/**
	 * Flag to indicate if the component should be searchable.
	 */
	'searchable'?: boolean;

	/**
	 * The threshold of items to show the search input.
	 */
	'searchableThreshold'?: number;

	/**
	 * The currently selected key (controlled).
	 */
	'selectedKey'?: React.Key;

	/**
	 * Flag to make the picker only as wide as its contents.
	 */
	'shrink'?: boolean;

	/**
	 * Sets the tabindex of the trigger to control its position in the tab
	 * order, for example when the picker takes part in roving tabindex
	 * navigation.
	 */
	'tabIndex'?: number;

	/**
	 * Sets the width of the panel.
	 */
	'width'?: number;

	[key: string]: any;
} & Omit<ICollectionProps<T, unknown>, 'virtualize'>;

const defaultMessages: PickerMessages = {
	itemDescribedby:
		'You are currently on a text element, inside of a list box.',
	itemSelected: '{0}, selected',
	noResultsFound: 'No results found',
	scrollToBottomAriaLabel: 'Scroll to bottom',
	scrollToTopAriaLabel: 'Scroll to top',
	searchPlaceholder: 'Search',
};

export function Picker<T extends Record<string, any> | string | number>({
	UNSAFE_behavior,
	UNSAFE_menuClassName,
	active: externalActive,
	as: As = 'button',
	children,
	className,
	defaultActive = false,
	defaultSelectedKey,
	direction = 'bottom',
	disabled,
	displayKeyboardArrowsIndicator = false,
	filterKey,
	id,
	items,
	messages: externalMessages,
	native = false,
	onActiveChange,
	onSelectionChange,
	placeholder = 'Select an option',
	searchable,
	searchableThreshold,
	selectedKey: externalSelectedKey,
	shrink,
	tabIndex = 0,
	width,
	...otherProps
}: Props<T>) {
	const messages = {
		...defaultMessages,
		...(externalMessages ?? {}),
	};

	const [active, setActive] = useControlledState({
		defaultName: 'defaultActive',
		defaultValue: defaultActive,
		handleName: 'onActiveChange',
		name: 'active',
		onChange: onActiveChange,
		value: externalActive,
	});

	const [selectedKey, setSelectedKey] = useControlledState({
		defaultName: 'defaultSelectedKey',
		defaultValue: defaultSelectedKey,
		handleName: 'onSelectionChange',
		name: 'selectedKey',
		onChange: onSelectionChange,
		value: externalSelectedKey,
	});

	const totalItems = items ? items.length : React.Children.count(children);

	const {filter, isSearchable, searchRef, searchValue, setSearchValue} =
		useSearch({
			active,
			searchable,
			searchableThreshold,
			totalItems,
		});

	// We initialize the collection in the picker and then pass it down so the
	// collection can be cached even before the listbox is not mounted.

	const collection = useCollection<T, unknown>({
		children,
		filter,
		filterKey: filterKey ?? 'textValue',
		items,
		suppressTextValueWarning: false,
	});

	const [activeDescendant, setActiveDescendant] = useState(() =>
		selectedKey || selectedKey === 0
			? selectedKey
			: collection.getFirstItem().key
	);

	const ariaControls = useId();

	const triggerRef = useRef<HTMLButtonElement | null>(null);
	const menuRef = useRef<HTMLDivElement | null>(null);
	const listRef = useRef<HTMLUListElement | null>(null);

	const announcerAPIRef = useRef<AnnouncerAPI>(null);

	const getOptions = useCallback(
		() =>
			getFocusableList(menuRef).filter(
				(element) => element.getAttribute('role') === 'option'
			),
		[]
	);

	const {isFocusVisible} = useInteractionFocus();

	useEffect(
		function announceEmptyStateWhenSearching() {
			if (
				isSearchable &&
				collection.getSize() === 0 &&
				totalItems !== 0
			) {
				announcerAPIRef.current?.announce(messages.noResultsFound);
			}
		},
		[collection.getSize(), searchValue, totalItems]
	);

	useOverlayPosition(
		{
			alignmentByViewport: true,
			alignmentPosition: direction === 'bottom' ? 5 : 7,
			autoBestAlign: true,
			isOpen: active,
			ref: menuRef,
			triggerRef,
		},
		[active, children]
	);

	const isMobile = useIsMobileDevice();

	const {accessibilityFocus, navigationProps} = useNavigation({
		activation: 'manual',
		active: activeDescendant,
		containerRef: listRef,
		onNavigate: (tab) =>
			setActiveDescendant((tab as HTMLElement).getAttribute('id')!),
		orientation: 'vertical',
		typeahead: true,
		visible: active,
	});

	const onPress = useCallback(() => {
		if (menuRef.current && activeDescendant) {
			const item = document.getElementById(String(activeDescendant));

			if (item) {
				item.click();
			}
		}
	}, [activeDescendant]);

	// Apple devices with VoiceOver do not announce correctly when the menu is
	// opened. There is a bug with `aria-activedescendant` when the element is
	// not an input and uses `aria-controls` or `aria-owns`.
	// https://github.com/liferay/clay/issues/5281#issuecomment-1399151900

	useEffect(() => {
		if (
			announcerAPIRef.current &&
			isAppleDevice() &&
			activeDescendant &&
			active
		) {
			const item = collection.getItem(activeDescendant);

			if (!item) {
				return;
			}

			announcerAPIRef.current.announce(
				selectedKey === activeDescendant
					? sub(messages.itemSelected, [item.value])
					: `${item.value}`
			);

			// Announces item description with delay to replace combobox description.

			setTimeout(() => {
				announcerAPIRef.current!.announce(messages.itemDescribedby);
			}, 1000);
		}
	}, [active]);

	// When the items are updated, the visual focus is stale, meaning that when
	// navigating via the keyboard it will not work because the key does not
	// exist in the list, so we need to update the visual focus when the list
	// is updated during the component life cycle.

	useEffect(() => {
		if (
			!collection.getItem(selectedKey) &&
			!collection.getItem(activeDescendant)
		) {
			setActiveDescendant(collection.getFirstItem().key);
		}
	}, [items, searchValue]);

	const [isArrowVisible, setIsArrowVisible] = useState<
		null | 'top' | 'bottom' | 'both'
	>(null);

	useEffect(() => {
		if (
			!active ||
			UNSAFE_behavior !== 'secondary' ||
			collection.getItems().length <= 12
		) {
			return;
		}

		const THRESHOLD = 32;

		function onScroll(event: any) {
			const scrollTop = event.target.scrollTop;
			const scrollHeightMax =
				event.target.scrollHeight -
				event.target.clientHeight -
				THRESHOLD;

			if (scrollTop >= THRESHOLD && scrollTop <= scrollHeightMax) {
				setIsArrowVisible('both');
			}
			else if (scrollTop >= THRESHOLD) {
				setIsArrowVisible('top');
			}
			else if (scrollTop <= scrollHeightMax) {
				setIsArrowVisible('bottom');
			}
		}

		listRef.current?.addEventListener('scroll', onScroll, true);

		return () =>
			listRef.current?.removeEventListener('scroll', onScroll, true);
	}, [active]);

	useEffect(() => {
		if (!active || !listRef.current) {
			return;
		}

		const key = selectedKey ?? activeDescendant;

		if (key === undefined) {
			return;
		}

		const list = listRef.current;

		const item = list.querySelector<HTMLElement>(
			`[id="${CSS.escape(String(key))}"]`
		);

		if (!item) {
			return;
		}

		const itemRect = item.getBoundingClientRect();
		const listRect = list.getBoundingClientRect();

		const itemOffsetInList = itemRect.top - listRect.top + list.scrollTop;

		const centeredTop =
			itemOffsetInList - (list.clientHeight - itemRect.height) / 2;

		list.scrollTop = Math.max(
			0,
			Math.min(centeredTop, list.scrollHeight - list.clientHeight)
		);
	}, [active]);

	const onMoveFocus = useCallback(
		(
			key: 'PageUp' | 'PageDown',
			position: number,
			list: Array<HTMLElement> | Array<React.Key>
		) => {
			if (position === -1) {
				return;
			}

			const option =
				list[key === 'PageUp' ? position - 10 : position + 10] ??
				list[key === 'PageUp' ? 0 : list.length - 1];

			accessibilityFocus(
				option instanceof HTMLElement
					? option
					: document.getElementById(String(option))!
			);
		},
		[accessibilityFocus]
	);

	const context = {
		activeDescendant,
		isMobile: isMobile && native,
		onActiveDescendant: setActiveDescendant,
		onSelectionChange: (key: React.Key) => {
			triggerRef.current!.focus();
			setActiveDescendant(String(key));
			setSelectedKey(key);
			setActive(false);
		},
		selectedKey,
	};

	const selectedItem =
		selectedKey !== undefined ? collection.getItem(selectedKey) : undefined;

	const triggerLabel = useTriggerLabel(selectedKey, selectedItem);

	if (context.isMobile) {
		return (
			<select
				{...otherProps}
				className={classNames(
					'form-control form-control-select form-control-select-secondary',
					{'form-control-shrink': shrink},
					className
				)}
				onChange={(event) => setSelectedKey(event.target.value)}
				value={selectedKey ? String(selectedKey) : undefined}
			>
				<PickerContext.Provider value={context}>
					<Collection<T> collection={collection} />
				</PickerContext.Provider>
			</select>
		);
	}

	const clientWidth = triggerRef.current?.clientWidth || 0;

	return (
		<>
			<LiveAnnouncer ref={announcerAPIRef} />

			<As
				{...otherProps}
				aria-activedescendant={active ? String(activeDescendant) : ''}
				aria-controls={active ? ariaControls : undefined}
				aria-expanded={active}
				aria-haspopup="listbox"
				aria-owns={
					active && !isAppleDevice() ? ariaControls : undefined
				}
				className={classNames(
					'form-control form-control-select form-control-select-secondary',
					{'form-control-shrink': shrink},
					className,
					{
						show: active,
					}
				)}
				disabled={disabled}
				id={id}
				onClick={() => setActive(!active)}
				onKeyDown={(event: React.KeyboardEvent<HTMLButtonElement>) => {
					if (otherProps['onKeyDown']) {
						otherProps['onKeyDown'](event);
					}

					switch (event.key) {
						case Keys.Enter:
						case Keys.Spacebar: {
							event.preventDefault();
							setActive(true);

							if (active && activeDescendant) {
								onPress();
							}
							break;
						}
						case Keys.Tab:
							onPress();
							break;
						case Keys.Home:
						case Keys.End: {
							if (!active) {
								setActive(true);
							}
							navigationProps.onKeyDown(event);
							break;
						}
						case Keys.Up:
						case Keys.Down: {
							if (
								active &&
								event.altKey &&
								event.key === Keys.Up
							) {
								event.stopPropagation();
								onPress();
								setActive(false);

								return;
							}

							if (!active) {
								return setActive(true);
							}

							navigationProps.onKeyDown(event);
							break;
						}
						case 'PageUp':
						case 'PageDown': {
							if (!active) {
								return;
							}

							event.preventDefault();

							const list = getOptions();

							onMoveFocus(
								event.key,
								list.findIndex(
									(element) =>
										element.getAttribute('id') ===
										String(activeDescendant)
								),
								list
							);
							break;
						}
						default: {
							if (isTypeahead(event)) {
								setActive(true);
							}

							navigationProps.onKeyDown(event);
							break;
						}
					}
				}}
				ref={triggerRef}
				role="combobox"
				tabIndex={tabIndex}
				type="button"
			>
				{triggerLabel ?? placeholder}
			</As>

			{active && (
				<Overlay
					isCloseOnInteractOutside
					isKeyboardDismiss
					isOpen
					menuClassName={UNSAFE_menuClassName}
					menuRef={menuRef}
					onClose={(action) => {
						if (
							isFocusVisible() &&
							activeDescendant &&
							action === 'blur'
						) {
							onPress();
						}
						else {
							const key =
								selectedKey || selectedKey === 0
									? selectedKey
									: collection.getFirstItem().key;

							if (key !== activeDescendant) {
								setActiveDescendant(key);
							}
						}

						setActive(false);
					}}
					portalRef={menuRef}
					suppress={[triggerRef, menuRef]}
					triggerRef={triggerRef}
				>
					<div
						className={classNames(
							'dropdown-menu dropdown-menu-indicator-start dropdown-menu-select dropdown-menu-width-shrink show',
							{
								'dropdown-menu-height-lg':
									UNSAFE_behavior === 'secondary',
							}
						)}
						ref={menuRef}
						role="presentation"
						style={{
							maxWidth: 'none',
							minWidth: !width
								? `${Math.max(160, clientWidth)}px`
								: undefined,
							width:
								typeof width === 'number'
									? `${Math.max(width, clientWidth)}px`
									: undefined,
						}}
					>
						{isSearchable && (
							<Search
								activeDescendant={activeDescendant}
								ariaControls={ariaControls}
								getOptions={getOptions}
								onActiveChange={setActive}
								onChange={setSearchValue}
								onKeyDown={navigationProps.onKeyDown}
								onMoveFocus={onMoveFocus}
								onPress={onPress}
								placeholder={messages.searchPlaceholder}
								ref={searchRef}
								triggerRef={triggerRef}
								value={searchValue}
							/>
						)}

						{UNSAFE_behavior === 'secondary' &&
							(isArrowVisible === 'top' ||
								isArrowVisible === 'both') && (
								<Button
									aria-hidden="true"
									aria-label={messages.scrollToTopAriaLabel}
									className="dropdown-item dropdown-item-scroll dropdown-item-scroll-up"
									displayType="unstyled"
									onClick={() => {
										const list = collection.getItems();

										onMoveFocus(
											'PageUp',
											list.findIndex(
												(item) =>
													item === activeDescendant
											),
											list
										);
										triggerRef.current?.focus();
									}}
									tabIndex={-1}
								>
									<Icon symbol="caret-top" />
								</Button>
							)}

						<ul
							aria-labelledby={otherProps['aria-labelledby']}
							className="inline-scroller list-unstyled"
							id={ariaControls}
							onFocus={() => {
								if (isSearchable) {
									searchRef.current?.focus();
								}
								else {
									triggerRef.current?.focus();
								}
							}}
							ref={listRef}
							role="listbox"
							tabIndex={-1}
						>
							{collection.getSize() === 0 &&
								searchValue !== '' && (
									<li
										className="dropdown-item"
										role="presentation"
									>
										{messages.noResultsFound}
									</li>
								)}

							<PickerContext.Provider value={context}>
								<Collection<T> collection={collection} />
							</PickerContext.Provider>
						</ul>

						{UNSAFE_behavior === 'secondary' &&
							(isArrowVisible === 'bottom' ||
								isArrowVisible === 'both') && (
								<Button
									aria-hidden="true"
									aria-label={
										messages.scrollToBottomAriaLabel
									}
									className="dropdown-item dropdown-item-scroll dropdown-item-scroll-down"
									displayType="unstyled"
									onClick={() => {
										const list = collection.getItems();

										onMoveFocus(
											'PageDown',
											list.findIndex(
												(item) =>
													item === activeDescendant
											),
											list
										);
										triggerRef.current?.focus();
									}}
									tabIndex={-1}
								>
									<Icon symbol="caret-bottom" />
								</Button>
							)}
					</div>
				</Overlay>
			)}

			{active && displayKeyboardArrowsIndicator && (
				<ClayPortal>
					<KeyboardArrowsIndicator
						anchorRef={triggerRef}
						direction="vertical"
					/>
				</ClayPortal>
			)}
		</>
	);
}
