/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	KeyboardArrowsIndicator,
	__NOT_PUBLIC_COLLECTION,
	__NOT_PUBLIC_LIVE_ANNOUNCER,
} from '@clayui/core';
import DropDown from '@clayui/drop-down';
import {ClayInput as Input} from '@clayui/form';
import LoadingIndicator from '@clayui/loading-indicator';
import {
	ClayPortal,
	InternalDispatch,
	Keys,
	Overlay,
	getLocatorValue,
	isAppleDevice,
	sub,
	useControlledState,
	useDebounce,
	useId,
	useIsFirstRender,
	useNavigation,
	useOverlayPosition,
} from '@clayui/shared';
import classNames from 'classnames';
import React, {useCallback, useEffect, useMemo, useRef, useState} from 'react';

import {AutocompleteContext} from './Context';
import Item from './Item';
import {useInfiniteScroll} from './useInfiniteScroll';

import type {AnnouncerAPI, ICollectionProps} from '@clayui/core';
import type {Locator} from '@clayui/shared';

const {Collection, useCollection, useVirtual} = __NOT_PUBLIC_COLLECTION;
const {LiveAnnouncer} = __NOT_PUBLIC_LIVE_ANNOUNCER;

type Item = Record<string, any> | string | number;

type ItemProps<T extends Item> = {
	children: React.ReactElement;
	index: number;
	item: T;
	keyValue: React.Key;
};

export type AutocompleteMessages = {
	infiniteScrollInitialLoad?: string;
	infiniteScrollInitialLoadPlural?: string;
	infiniteScrollOnLoad?: string;
	infiniteScrollOnLoaded?: string;
	infiniteScrollOnLoadedPlural?: string;
	listCount?: string;
	listCountPlural?: string;
	loading: string;
	notFound: string;
};

export interface IProps<T>
	extends Omit<
			React.HTMLAttributes<HTMLInputElement>,
			'onChange' | 'children'
		>,
		Omit<Partial<ICollectionProps<T, unknown>>, 'virtualize' | 'items'> {

	/**
	 * Internal property to change the loading indicator markup to shrink.
	 * @ignore
	 */
	UNSAFE_loadingShrink?: boolean;

	/**
	 * Flag to indicate if menu is showing or not.
	 */
	active?: boolean;

	/**
	 * Flag to align the DropDown menu within the viewport.
	 * @deprecated since v3.92.0 - it is no longer necessary..
	 */
	alignmentByViewport?: boolean;

	/**
	 * Flag to allow an input value not corresponding to an item to be set.
	 */
	allowsCustomValue?: boolean;

	/**
	 * Custom input component.
	 */
	as?:
		| 'input'
		| React.ForwardRefExoticComponent<any>
		| ((props: React.ComponentProps<typeof Input>) => JSX.Element);

	/**
	 * Content rendered in a caption pinned to the bottom of the menu, after
	 * the list of items and the infinite scroll feedback.
	 */
	caption?: React.ReactNode;

	/**
	 * The initial value of the active state (uncontrolled).
	 */
	defaultActive?: boolean;

	/**
	 * Property to set the initial value of `items` (uncontrolled).
	 */
	defaultItems?: Array<T> | null;

	/**
	 * The initial value of the input (uncontrolled).
	 */
	defaultValue?: string;

	/**
	 * Direction the menu will render relative to the Autocomplete.
	 */
	direction?: 'bottom' | 'top';

	/**
	 * Flag to render the `KeyboardArrowsIndicator` alongside the Autocomplete
	 * input, hinting that up and down arrow keys can be used to navigate
	 * the suggestions list. The indicator floats to the right of the input
	 * and flips to the left when it would overflow the viewport. It is
	 * only rendered while the suggestions panel is open.
	 */
	displayKeyboardArrowsIndicator?: boolean;

	/**
	 * The estimated height of an item that is used by the virtualizer.
	 */
	estimateSize?: number;

	/**
	 * Defines the name of the property key that is used in the items filter
	 * test (Dynamic content).
	 */
	filterKey?: Locator;

	/**
	 * Property to render content with dynamic data.
	 */
	items?: Array<T> | null;

	/**
	 * The current state of Autocomplete current loading. Determines whether the
	 * loading indicator should be shown or not.
	 */
	loadingState?: number;

	/**
	 * The interaction required to display the menu.
	 */
	menuTrigger?: 'input' | 'focus';

	/**
	 * Messages for the Autocomplete.
	 */
	messages?: AutocompleteMessages;

	/**
	 * Callback for when the active state changes (controlled).
	 */
	onActiveChange?: InternalDispatch<boolean>;

	/**
	 * Callback called when input value changes (controlled).
	 */
	onChange?: InternalDispatch<string>;

	/**
	 * Callback called when items change (controlled).
	 */
	onItemsChange?: InternalDispatch<Array<T> | null>;

	/**
	 * Callback is called when more items need to be loaded when the scroll
	 * reaches the bottom.
	 */
	onLoadMore?: () => Promise<any> | null;

	/**
	 * Config for showing a default action as the first item in the dropdown
	 */
	primaryAction?: {
		label: React.ReactNode;
		onClick: () => void;
	};

	/**
	 * The currently selected keys (controlled).
	 */
	selectedKeys?: Array<React.Key>;

	/**
	 * The current value of the input (controlled).
	 */
	value?: string;

	[key: string]: any;
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

function hasItem<T extends Item>(
	items: Array<T>,
	value: string,
	filterKey?: Locator
) {
	return items.find((item) => {
		const itemValue = getLocatorValue({
			item,
			locator: filterKey,
		});

		return itemValue === value;
	});
}

const ESCAPE_REGEXP = /[.*+?^${}()|[\]\\]/g;

const defaultMessages: Required<AutocompleteMessages> = {
	infiniteScrollInitialLoad:
		'{0} item loaded. Reach the last item to load more.',
	infiniteScrollInitialLoadPlural:
		'{0} items loaded. Reach the last item to load more.',
	infiniteScrollOnLoad: 'Loading more items.',
	infiniteScrollOnLoaded: '{0} item loaded.',
	infiniteScrollOnLoadedPlural: '{0} items loaded.',
	listCount: '{0} option available.',
	listCountPlural: '{0} options available.',
	loading: 'Loading...',
	notFound: 'No results found',
};

function AutocompleteInner<T extends Item>(
	{
		UNSAFE_loadingShrink,
		active: externalActive,
		alignmentByViewport: _,
		allowsCustomValue,
		as: As = Input,
		children,
		containerElementRef,
		defaultActive,
		defaultItems,
		defaultValue,
		direction = 'bottom',
		caption,
		displayKeyboardArrowsIndicator = false,
		estimateSize,
		filterKey,
		items: externalItems,
		loadingState,
		menuTrigger = 'input',
		messages: externalMessages,
		onActiveChange,
		onChange,
		onItemsChange,
		onLoadMore,
		primaryAction,
		value: externalValue,
		selectedKeys,
		...otherProps
	}: IProps<T>,
	ref: React.Ref<HTMLInputElement>
) {
	const messages = {
		...defaultMessages,
		...(externalMessages ?? {}),
	};

	const [items, , isItemsUncontrolled] = useControlledState({
		defaultName: 'defaultItems',
		defaultValue: defaultItems,
		handleName: 'onItemsChange',
		name: 'items',
		onChange: onItemsChange,
		value: externalItems,
	});

	const [value = '', setValue, isUncontrolled] = useControlledState({
		defaultName: 'defaultValue',
		defaultValue,
		handleName: 'onChange',
		name: 'value',
		onChange,
		value: externalValue,
	});

	const [active, setActive] = useControlledState({
		defaultName: 'defaultActive',
		defaultValue: defaultActive,
		handleName: 'onActiveChange',
		name: 'active',
		onChange: onActiveChange,
		value: externalActive,
	});

	const inputRef = useRef<HTMLInputElement>(null);
	const menuRef = useRef<HTMLDivElement>(null);
	const shouldIgnoreOpenMenuOnFocusRef = useRef(false);

	const inputElementRef =
		(ref as React.RefObject<HTMLInputElement>) || inputRef;

	const isLoading = Boolean(loadingState !== undefined && loadingState === 1);
	const debouncedLoadingChange = useDebounce(isLoading, 500);

	const currentItemSelectedRef = useRef<string>('');

	const ariaControlsId = useId();

	const announcerAPIRef = useRef<AnnouncerAPI>(null);

	const isFirst = useIsFirstRender();

	const filterFn = useCallback(
		(itemValue: string) =>
			itemValue.match(
				new RegExp(value.replace(ESCAPE_REGEXP, '\\$&'), 'i')
			) !== null,
		[value]
	);

	useEffect(() => {

		// Validates that the initial value exists in the items.

		if (
			!allowsCustomValue &&
			!currentItemSelectedRef.current &&
			value &&
			items
		) {
			if (hasItem(items, value, filterKey)) {
				currentItemSelectedRef.current = value;
			}

			if (!filterKey && items.length && typeof items[0] === 'object') {
				console.warn(
					`<Autocomplete />: the component is trying to validate that the initial value exists in the items, but it doesn't know which key to use for comparison. Define the key in 'filterKey' ('<Autocomplete filterKey="value" />').`
				);
			}
		}
	}, [items]);

	useEffect(() => {

		// Does not update state on first render, if the custom value is allowed
		// or if the value is empty.

		if (isFirst || allowsCustomValue || !value) {
			return;
		}

		if (active === false && currentItemSelectedRef.current !== value) {

			// The state is controlled so we have to revalidate if the typed value
			// exists in the suggestion list.

			if (!isUncontrolled && items && hasItem(items, value, filterKey)) {
				currentItemSelectedRef.current = value;

				return;
			}

			setValue(currentItemSelectedRef.current);
		}
	}, [active]);

	const filteredItems = useMemo(() => {
		if (debouncedLoadingChange) {
			return [];
		}

		if (!isItemsUncontrolled) {
			return items ?? [];
		}

		if (!items) {
			return [];
		}

		return items?.filter((option) => {
			if (!filterKey && typeof option === 'object') {
				console.warn(
					`<Autocomplete />: the component is trying to filter the list but it doesn't know which key to use for comparison. Define the key in 'filterKey' ('<Autocomplete filterKey="value" />'). option=`,
					option
				);

				return true;
			}

			return filterFn(
				getLocatorValue({
					item: option,
					locator: filterKey,
				}) || option.toString()
			);
		});
	}, [debouncedLoadingChange, isItemsUncontrolled, items, filterFn]);

	const dynamicCollection = children instanceof Function;

	const filteredItemsWithPrimaryAction =
		primaryAction && dynamicCollection
			? ['__PRIMARY_ACTION__' as T, ...filteredItems]
			: filteredItems;

	const virtualizer = useVirtual({
		estimateSize: estimateSize ?? 37,
		items: filteredItemsWithPrimaryAction,
		parentRef: menuRef,
	});

	const primaryActionChild = (
		<Item
			{...primaryAction}
			className="text-primary"
			data-collection-no-filter
			highlightMatch={false}
			key="__PRIMARY_ACTION__"
			onClick={(event) => {
				event.preventDefault();

				if (primaryAction?.onClick) {
					primaryAction.onClick();
				}
			}}
		>
			{primaryAction?.label}
		</Item>
	);

	let wrappedChildren = children;

	if (primaryAction) {
		if (dynamicCollection) {
			wrappedChildren = (item: T, ...args: Array<any>) => {
				if (item === '__PRIMARY_ACTION__') {
					return primaryActionChild;
				}

				return children(item, ...args);
			};
		}
		else if (Array.isArray(children)) {
			wrappedChildren = [primaryActionChild, ...children];
		}
		else {
			wrappedChildren = [primaryActionChild, children];
		}
	}

	// We initialize the collection in the picker and then pass it down so the
	// collection can be cached even before the listbox is not mounted.

	const collection = useCollection<T, unknown>({
		children: wrappedChildren,
		filter: isItemsUncontrolled ? filterFn : undefined,
		filterKey: 'value',
		itemContainer: useCallback(
			({children, keyValue}: ItemProps<any>) => {
				const itemValue =
					children.props.textValue ??
					children.props.value ??
					children.props.children;

				return React.cloneElement(children, {
					keyValue,
					match: String(value),
					onClick: (
						event: React.MouseEvent<
							| HTMLSpanElement
							| HTMLButtonElement
							| HTMLAnchorElement
						>
					) => {
						if (children.props.onClick) {
							children.props.onClick(event);
						}

						if (event.defaultPrevented) {
							return;
						}

						setActive(false);

						currentItemSelectedRef.current = itemValue;
						setValue(itemValue);

						shouldIgnoreOpenMenuOnFocusRef.current = true;
						inputElementRef.current?.focus();
					},
					roleItem: 'option',
				}) as React.ReactElement;
			},
			[value]
		),
		items: filteredItemsWithPrimaryAction,
		notFound: (
			<DropDown.Item
				aria-disabled="true"
				className="disabled"
				roleItem="option"
			>
				{messages.notFound}
			</DropDown.Item>
		),
		suppressTextValueWarning: false,
		virtualizer: items ? virtualizer : undefined,
	});

	const [activeDescendant, setActiveDescendant] = useState<React.Key>('');

	useOverlayPosition(
		{
			alignmentByViewport: true,
			alignmentPosition: direction === 'bottom' ? 5 : 7,
			autoBestAlign: true,
			isOpen: active,
			ref: menuRef,
			triggerRef: containerElementRef,
		},
		[active, children]
	);

	const {navigationProps} = useNavigation({
		activation: 'manual',
		active: activeDescendant,
		collection,
		containerRef: menuRef,
		loop: true,
		onNavigate: (item) => setActiveDescendant(item as React.Key),
		orientation: 'vertical',
		visible: active,
	});

	// Resets `activeDescendant` when the menu is closed, this avoids a bug when
	// the `active` state is controlled and closes the menu with different
	// statements than what is expected internally.

	useEffect(() => {
		if (!active && activeDescendant) {
			setActiveDescendant('');
		}
	}, [active]);

	const optionCount = collection.getItems().length;
	const lastSizeRef = useRef(optionCount);

	const InfiniteScrollFeedback = useInfiniteScroll({
		active,
		announcer: announcerAPIRef,
		collection,
		loadingState,
		messages,
		onLoadMore,
	});

	useEffect(() => {

		// Only announces the number of options available when the menu is open
		// if there is no item with focus, with the exception of Voice Over
		// which does not include the message.

		if (
			announcerAPIRef.current &&
			active &&
			(!activeDescendant ||
				isAppleDevice() ||
				optionCount !== lastSizeRef.current)
		) {
			const optionCount = collection.getItems().length;

			announcerAPIRef.current.announce(
				sub(
					optionCount === 1
						? messages!.listCount!
						: messages!.listCountPlural!,
					[optionCount]
				)
			);
		}

		lastSizeRef.current = optionCount;
	}, [active, value]);

	const onClose = useCallback(() => setActive(false), []);

	const onPress = useCallback(() => {
		if (menuRef.current && activeDescendant) {
			const item = document.getElementById(String(activeDescendant));

			if (item) {
				item.click();
			}
		}
	}, [activeDescendant]);

	const LoadingGroupItem = UNSAFE_loadingShrink
		? Input.GroupItem
		: Input.GroupInsetItem;

	return (
		<>
			<LiveAnnouncer ref={announcerAPIRef} />

			<As
				{...otherProps}
				aria-activedescendant={
					active ? String(activeDescendant) : undefined
				}
				aria-autocomplete="list"
				aria-controls={ariaControlsId}
				aria-expanded={active}
				autoComplete="off"
				autoCorrect="off"
				insetAfter={isLoading}
				onChange={(event: React.ChangeEvent<HTMLInputElement>) => {
					const {value} = event.target;

					if (!value) {
						currentItemSelectedRef.current = value;
					}

					if (items !== null) {
						setActive(true);
					}

					setValue(value);

					if (activeDescendant) {
						setActiveDescendant('');
					}
				}}
				onFocus={(event) => {
					if (otherProps.onFocus) {
						otherProps.onFocus(event);
					}

					if (menuTrigger === 'focus' && items !== null) {
						if (shouldIgnoreOpenMenuOnFocusRef.current) {
							shouldIgnoreOpenMenuOnFocusRef.current = false;

							return;
						}

						setActive(true);
					}
				}}
				onKeyDown={(event) => {
					if (otherProps.onKeyDown) {
						otherProps.onKeyDown(event);
					}

					switch (event.key) {
						case Keys.Tab:
						case Keys.Esc: {
							setActive(false);
							break;
						}
						case Keys.Enter: {
							if (active && activeDescendant) {
								event.preventDefault();

								setActive(false);

								onPress();

								break;
							}

							// Typing into the input clears activeDescendant, so
							// an exact match would otherwise require an extra
							// arrow-down before Enter to commit it. Select the
							// match directly when one exists.

							if (active && value) {
								const lowerValue = value.toLowerCase();

								const matchedKey = collection
									.getItems()
									.find(
										(key) =>
											collection
												.getItem(key)
												?.value?.toLowerCase() ===
											lowerValue
									);

								if (matchedKey && menuRef.current) {
									event.preventDefault();

									setActive(false);

									const matchedElement =
										menuRef.current.querySelector(
											`#${CSS.escape(String(matchedKey))}`
										) as HTMLElement | null;

									matchedElement?.click();

									break;
								}
							}

							setActive(false);

							break;
						}
						case Keys.Home:
						case Keys.End: {
							setActiveDescendant('');
							break;
						}
						case Keys.Left:
						case Keys.Right: {
							if (activeDescendant) {
								setActiveDescendant('');
							}
							break;
						}
						case Keys.Up:
						case Keys.Down: {
							event.preventDefault();

							if (
								!active &&
								event.altKey &&
								event.key === Keys.Down &&
								items !== null
							) {
								event.stopPropagation();
								setActive(true);

								return;
							}

							if (!activeDescendant) {
								const item =
									event.key === Keys.Down
										? collection.getFirstItem()
										: collection.getLastItem();

								setActiveDescendant(item.key);
							}

							if (!active && items !== null) {
								return setActive(true);
							}

							navigationProps.onKeyDown(event);
							break;
						}
						default:
							navigationProps.onKeyDown(event);
							break;
					}
				}}
				ref={inputElementRef}
				role="combobox"
				spellCheck={false}
				value={value}
			/>

			{active && (
				<Overlay
					isCloseOnInteractOutside
					isKeyboardDismiss
					isOpen
					menuRef={menuRef}
					onClose={onClose}
					portalRef={menuRef}
					suppress={[menuRef, inputElementRef]}
					triggerRef={inputElementRef}
				>
					<div
						className={classNames(
							'dropdown-menu dropdown-menu-select show',
							{
								'dropdown-menu-has-caption': !!caption,
								'dropdown-menu-indicator-start': !!selectedKeys,
							}
						)}
						ref={menuRef}
						role="presentation"
						style={{
							maxWidth: 'none',
							width: `${containerElementRef.current?.clientWidth}px`,
						}}
					>
						<AutocompleteContext.Provider
							value={{
								activeDescendant,
								onActiveDescendant: setActiveDescendant,
								onClick: setValue,
								selectedKeys,
							}}
						>
							<Collection<T>
								aria-label={otherProps['aria-label']}
								aria-labelledby={otherProps['aria-labelledby']}
								as={List}
								collection={collection}
								id={ariaControlsId}
								isLoading={isLoading}
								onLoadMore={onLoadMore}
								role="listbox"
							>
								{debouncedLoadingChange ? (
									<DropDown.Item
										aria-disabled="true"
										className="disabled"
										roleItem="option"
									>
										{messages.loading}
									</DropDown.Item>
								) : (
									children
								)}
							</Collection>
						</AutocompleteContext.Provider>

						<InfiniteScrollFeedback />

						{caption && (
							<DropDown.Caption>{caption}</DropDown.Caption>
						)}
					</div>
				</Overlay>
			)}

			{active && displayKeyboardArrowsIndicator && (
				<ClayPortal>
					<KeyboardArrowsIndicator
						anchorRef={inputElementRef}
						direction="vertical"
					/>
				</ClayPortal>
			)}

			{isLoading && (
				<LoadingGroupItem
					after={!UNSAFE_loadingShrink ? true : undefined}
					aria-label={messages.loading}
					aria-valuemax={100}
					aria-valuemin={0}
					role="progressbar"
					shrink={UNSAFE_loadingShrink}
				>
					<span className="inline-item inline-item-middle">
						<LoadingIndicator size="sm" />
					</span>
				</LoadingGroupItem>
			)}
		</>
	);
}

type ForwardRef = {
	<T>(props: IProps<T> & {ref?: React.Ref<HTMLInputElement>}): JSX.Element;
	displayName: string;
};

export const Autocomplete = React.forwardRef(AutocompleteInner) as ForwardRef;

Autocomplete.displayName = 'Autocomplete';
