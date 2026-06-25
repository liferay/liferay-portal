/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import classNames from 'classnames';
import React, {
	Fragment,
	KeyboardEventHandler,
	ReactNode,
	useContext,
	useRef,
} from 'react';
import {flushSync} from 'react-dom';

import {ScreenReaderAnnouncerContext} from '../../contexts/ScreenReaderContext';

import './RowBuilder.scss';

interface ItemWithId {
	id: string;
}

export interface RowBuilderLabels<T> {

	/**
	 * Label shown in the add button.
	 */
	add: string;

	/**
	 * Screen reader announcement sent after adding an item.
	 */
	addedAnnouncement: string;

	/**
	 * Generic delete label used as button title and aria fallback.
	 */
	delete: string;

	/**
	 * Optional item-specific aria label for the delete button. Falls
	 * back to `delete` when omitted.
	 */
	deleteAriaLabel?: (item: T, index: number) => string;

	/**
	 * Screen reader announcement sent after deleting an item.
	 */
	deletedAnnouncement: string;

	/**
	 * Optional aria label for each row wrapper.
	 */
	itemAriaLabel?: (item: T, index: number) => string;

	/**
	 * Aria label applied to the list container.
	 */
	list: string;
}

interface RowBuilderProps<T extends ItemWithId> {

	/**
	 * Optional predicate that controls whether an item can be deleted.
	 * It is used both to render the delete button and to guard deletion.
	 * Defaults to `() => true`.
	 */
	canDelete?: (item: T, index: number, items: T[]) => boolean;

	/**
	 * Factory used to create a new item when the user adds a row and when
	 * the last item is deleted and must be replaced with an empty one.
	 */
	createItem: () => T;

	/**
	 * Optional flag that hides the built-in add button (for example when
	 * rows are added from an external source such as a sidebar).
	 */
	hideAddButton?: boolean;

	/**
	 * Optional class name applied to each row wrapper.
	 */
	itemClassName?: string;

	/**
	 * Current list of items rendered by the builder.
	 */
	items: T[];

	/**
	 * Labels and accessibility strings used by the list UI.
	 */
	labels: RowBuilderLabels<T>;

	/**
	 * Renders the row content for a given item.
	 */
	renderItem: (props: {
		index: number;
		item: T;
		onChange: (item: T) => void;
	}) => ReactNode;

	/**
	 * Renders extra action buttons placed before the built-in delete button
	 * (for example a duplicate button).
	 */
	renderItemActions?: (props: {index: number; item: T}) => ReactNode;

	/**
	 * Renders content between consecutive rows (for example a conjunction
	 * label). Called for every item after the first.
	 */
	renderItemSeparator?: (props: {index: number; item: T}) => ReactNode;

	/**
	 * Updates the full items array after add, delete, or item changes.
	 */
	setItems: (items: T[]) => void;
}

/**
 * Renders a list of items with an "add" button, per-item delete buttons,
 * roving keyboard focus, and screen reader announcements.
 */
export function RowBuilder<T extends ItemWithId>({
	canDelete = () => true,
	createItem,
	hideAddButton = false,
	itemClassName,
	items,
	labels,
	renderItem,
	renderItemActions,
	renderItemSeparator,
	setItems,
}: RowBuilderProps<T>) {
	const {sendMessage} = useContext(ScreenReaderAnnouncerContext);

	const itemElementsRef = useRef(new Map<string, HTMLElement>());

	const {
		add: addLabel,
		addedAnnouncement,
		delete: deleteLabel,
		deleteAriaLabel = () => deleteLabel,
		deletedAnnouncement,
		itemAriaLabel,
		list: listLabel,
	} = labels;

	const focusItem = (id: string) => {
		itemElementsRef.current.get(id)?.focus();
	};

	const announce = (message: string | undefined) => {
		if (message) {
			sendMessage(message);
		}
	};

	const handleAdd = () => {
		const newItem = createItem();

		flushSync(() => {
			setItems([...items, newItem]);
		});

		focusItem(newItem.id);

		announce(addedAnnouncement);
	};

	const handleDelete = (index: number) => {
		const item = items[index];

		if (!item || !canDelete(item, index, items)) {
			return;
		}

		if (items.length === 1) {
			setItems([createItem()]);
		}
		else {
			const neighbor = items[index - 1] ?? items[index + 1];

			focusItem(neighbor.id);

			setItems(
				items.filter((_item, currentIndex) => currentIndex !== index)
			);
		}

		announce(deletedAnnouncement);
	};

	const handleItemChange = (index: number, newItem: T) => {
		const newItems = [...items];

		newItems[index] = newItem;

		setItems(newItems);
	};

	const makeKeyDownHandler =
		(item: T, index: number): KeyboardEventHandler<HTMLDivElement> =>
		(event) => {
			if (event.target !== event.currentTarget) {
				return;
			}

			if (event.key === 'Enter' || event.key === ' ') {
				event.preventDefault();

				const wrapper = itemElementsRef.current.get(item.id);

				const firstFocusable = wrapper?.querySelector<HTMLElement>(
					'button, input, select, [tabindex]:not([tabindex="-1"])'
				);

				firstFocusable?.focus();
			}

			if (event.key === 'ArrowDown') {
				event.preventDefault();

				const nextIndex = (index + 1) % items.length;

				focusItem(items[nextIndex].id);
			}

			if (event.key === 'ArrowUp') {
				event.preventDefault();

				const prevIndex = index === 0 ? items.length - 1 : index - 1;

				focusItem(items[prevIndex].id);
			}
		};

	return (
		<>
			<div aria-label={listLabel} role="menu">
				{items.map((item, index) => {
					const ariaLabel = deleteAriaLabel(item, index);

					return (
						<Fragment key={item.id}>
							{index > 0
								? renderItemSeparator?.({index, item})
								: null}

							<div
								aria-label={itemAriaLabel?.(item, index)}
								className={classNames(
									'align-items-center d-flex justify-content-between mb-3 p-2 layout__row-builder-item',
									itemClassName
								)}
								onKeyDown={makeKeyDownHandler(item, index)}
								ref={(element) => {
									if (element) {
										itemElementsRef.current.set(
											item.id,
											element
										);
									}
									else {
										itemElementsRef.current.delete(item.id);
									}
								}}
								role="menuitem"
								tabIndex={0}
							>
								<div className="c-gap-2 d-flex flex-grow-1 flex-wrap">
									{renderItem({
										index,
										item,
										onChange: (newItem) =>
											handleItemChange(index, newItem),
									})}
								</div>

								{renderItemActions?.({index, item})}

								{canDelete(item, index, items) ? (
									<ClayButtonWithIcon
										aria-label={ariaLabel}
										borderless
										className="align-self-baseline layout__row-builder-delete-button lfr-portal-tooltip"
										displayType="secondary"
										onClick={() => handleDelete(index)}
										size="sm"
										symbol="times-circle"
										title={deleteLabel}
									/>
								) : null}
							</div>
						</Fragment>
					);
				})}
			</div>

			{hideAddButton ? null : (
				<ClayButton
					className="mt-2"
					displayType="secondary"
					onClick={handleAdd}
					size="sm"
				>
					{addLabel}
				</ClayButton>
			)}
		</>
	);
}
