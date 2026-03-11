/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayCardWithInfo} from '@clayui/card';
import classNames from 'classnames';
import {getObjectValueFromPath, sub} from 'frontend-js-web';
import React, {forwardRef, useContext, useRef} from 'react';

import FrontendDataSetContext, {
	IFrontendDataSetContext,
} from '../../FrontendDataSetContext';
import FDSDndProvider from '../../dnd/FDSDndProvider';
import useFDSDrop from '../../dnd/useFDSDrop';
import filterItemActions from '../../utils/actionItems/filterItemActions';
import formatActionURL from '../../utils/actionItems/formatActionURL';
import handleActionClick from '../../utils/actionItems/handleActionClick';
import getLabels from '../../utils/getLabels';
import {getLocalizedValue} from '../../utils/getLocalizedValue';
import getRandomId from '../../utils/getRandomId';
import isLink from '../../utils/isLink';
import {
	EItemActionsType,
	ICardSchema,
	IItemsActions,
	IView,
} from '../../utils/types';
import ViewsContext from '../ViewsContext';
import imagePropsTransformer from '../utils/imagePropsTransformer';

const Card = forwardRef<HTMLDivElement, any>(
	(
		{
			item,
			items,
			onItemSelectionChange,
			schema,
		}: {
			item: any;
			items: any[];
			onItemSelectionChange: Function;
			schema: ICardSchema;
		},
		ref
	) => {
		const {
			executeAsyncItemAction,
			highlightItems,
			infoPanelOpen,
			itemsActions,
			loadData,
			onActionDropdownItemClick,
			onInfoPanelToggleButtonClick,
			openModal,
			openSidePanel,
			selectable,
			selectedItemsKey,
			selectedItemsValue,
			selectionType,
			toggleItemInlineEdit,
		}: IFrontendDataSetContext = useContext(FrontendDataSetContext);

		const {
			accessibleNameField,
			description,
			image,
			labels,
			link,
			sticker,
			symbol,
			title,
		} = schema;

		const [viewsContext] = useContext(ViewsContext);

		const activeView: IView = viewsContext.activeView;

		const actions =
			(itemsActions?.length && itemsActions) || item.actionDropdownItems;

		const formattedActions =
			actions &&
			(filterItemActions({
				actions,
				infoPanelOpen,
				itemData: item,
				selectable,
				selectedItemsKey,
				selectedItemsValue,
			}) as any);

		const selectedItemKey =
			selectedItemsKey &&
			getObjectValueFromPath({object: item, path: selectedItemsKey});

		const getDropdownActions = (actions: IItemsActions[]): Array<any> => {
			const processedActions: any[] = [];

			actions.forEach((action, index) => {
				if (
					action.type === EItemActionsType.GROUP ||
					action.type === EItemActionsType.CONTEXTUAL
				) {
					const {items: nestedItems, ...otherProps} = action;

					if (nestedItems?.length) {
						if (action.separator && index !== 0) {
							processedActions.push({type: 'divider'});
						}

						processedActions.push({
							...otherProps,
							items: getDropdownActions(nestedItems),
							symbolLeft: action.icon,
						});
					}
				}
				else {
					processedActions.push({
						...action,
						href: isLink(action.target, null)
							? formatActionURL(action.href, item, action.target)
							: null,
						onClick: (event: Event) => {
							handleActionClick({
								action,
								event,
								executeAsyncItemAction,
								highlightItems,
								infoPanelOpen,
								itemData: item,
								itemId: selectedItemKey,
								items,
								loadData,
								onActionDropdownItemClick,
								onInfoPanelToggleButtonClick,
								onItemSelectionChange,
								openModal,
								openSidePanel,
								toggleItemInlineEdit,
							});
						},
						symbolLeft: action.icon,
					});
				}
			});

			return processedActions;
		};

		const accessibleNameItemKey =
			accessibleNameField || title || description;

		const accessibleName =
			getLocalizedValue(item, accessibleNameItemKey)?.value ||
			Liferay.Language.get('card');

		const props = {
			actions: formattedActions && getDropdownActions(formattedActions),
			checkboxProps: {
				'aria-label': sub(
					Liferay.Language.get('select-x'),
					accessibleName
				),
			},
			description: getLocalizedValue(item, description)?.value,
			dropDownTriggerProps: {
				'aria-label': sub(
					Liferay.Language.get('x-actions'),
					accessibleName
				),
			},
			href: (link && item[link]) || null,
			imgProps:
				image &&
				imagePropsTransformer(getLocalizedValue(item, image)?.value),
			labels: getLabels(item, labels),
			onClick: (event: React.MouseEvent) => {
				const target = event.nativeEvent.target as Element;

				if (
					target?.closest('.dropdown-toggle') ||
					target?.closest('.dropdown-item')
				) {
					return;
				}

				// This logic is to avoid the onClick event from being
				// triggered twice when the user clicks on anything other
				// than the checkbox/radio in a selectable card

				if (target.tagName !== 'INPUT' && target.tagName !== 'A') {
					event.preventDefault();

					onItemSelectionChange?.(item, true);
				}
			},
			onSelectChange: selectable
				? () => {
						onItemSelectionChange?.(item);
					}
				: undefined,
			radioProps: {
				'aria-label': sub(
					Liferay.Language.get('select-x'),
					accessibleName
				),
			},
			selectableType: selectionType === 'single' ? 'radio' : 'checkbox',
			selected:
				selectable &&
				!!selectedItemsValue?.find(
					(element) =>
						selectedItemsKey &&
						element ===
							getObjectValueFromPath({
								object: item,
								path: selectedItemsKey,
							})
				),
			stickerProps: (sticker && item[sticker]) || null,
			symbol: symbol && item[symbol],
			title: getLocalizedValue(item, title)?.value || '',
		};

		return (
			<div ref={ref}>
				<ClayCardWithInfo
					{...props}
					{...(activeView.setItemComponentProps?.({item, props}) ??
						{})}
				/>
			</div>
		);
	}
);

function ClayCardOptionalDropTarget({
	item,
	items,
	onItemSelectionChange,
	schema,
}: React.ComponentProps<typeof Card>) {
	const cardRef = useRef<HTMLDivElement>(null);

	// ClayCardWithInfo does not take a ref, so we must query the target
	// element to highlight just the part we want

	useFDSDrop({
		item,
		targetDropRef: cardRef,
		targetDropRefQuerySelector: '.card',
	});

	return (
		<div className="col-md-3">
			<Card
				item={item}
				items={items}
				onItemSelectionChange={onItemSelectionChange}
				ref={cardRef}
				schema={schema}
			/>
		</div>
	);
}

const Cards = ({
	items,
	onItemSelectionChange,
	schema,
}: {
	items: Array<any>;
	onItemSelectionChange: Function;
	schema: ICardSchema;
}) => {
	const {selectedItemsKey, style}: IFrontendDataSetContext = useContext(
		FrontendDataSetContext
	);

	if (!items?.length) {
		return null;
	}

	return (
		<div
			className={classNames(
				'cards-container mb-n4',
				style === 'default' && 'px-3 pt-4'
			)}
		>
			<FDSDndProvider>
				<div className="row">
					{items.map((item) => {
						return (
							<ClayCardOptionalDropTarget
								item={item}
								items={items}
								key={
									selectedItemsKey
										? getObjectValueFromPath({
												object: item,
												path: selectedItemsKey,
											})
										: getRandomId()
								}
								onItemSelectionChange={onItemSelectionChange}
								schema={schema}
							/>
						);
					})}
				</div>
			</FDSDndProvider>
		</div>
	);
};

export {Card};
export default Cards;
