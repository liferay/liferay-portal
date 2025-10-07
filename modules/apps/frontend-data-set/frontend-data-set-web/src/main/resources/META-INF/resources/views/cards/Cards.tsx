/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayCardWithInfo} from '@clayui/card';
import classNames from 'classnames';
import {getObjectValueFromPath} from 'frontend-js-web';
import React, {forwardRef, useContext, useRef} from 'react';

import FrontendDataSetContext, {
	IFrontendDataSetContext,
} from '../../FrontendDataSetContext';
import FDSDndProvider from '../../dnd/FDSDndProvider';
import useFDSDrop from '../../dnd/useFDSDrop';
import filterItemActions from '../../utils/actionItems/filterItemActions';
import formatActionURL from '../../utils/actionItems/formatActionURL';
import handleActionClick from '../../utils/actionItems/handleActionClick';
import {getLocalizedValue} from '../../utils/getLocalizedValue';
import getRandomId from '../../utils/getRandomId';
import isLink from '../../utils/isLink';
import {
	DisplayType,
	ICardLabelSchema,
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

		const getLabels = (
			item: any
		): Array<{
			displayType: DisplayType;
			value: string;
		}> => {
			if (!schema.labels) {
				return [];
			}

			return schema.labels.flatMap((label: ICardLabelSchema) => {
				const {displayTypeKey, displayTypeValues} = label;
				let {displayType} = label;

				if (!displayType && displayTypeValues && displayTypeKey) {
					const keyValue = getLocalizedValue(
						item,
						displayTypeKey
					)?.value;

					displayType = displayTypeValues[keyValue!];
				}

				const value = getLocalizedValue(item, label.value)?.value;

				if (!value) {
					return [];
				}

				return [
					{
						displayType: displayType || DisplayType.UNSTYLED,
						value,
					},
				];
			});
		};

		const getDropdownActions = (actions: IItemsActions[]): Array<any> => {
			const processedActions: any[] = [];

			actions.forEach((action, index) => {
				if (action.type === 'group') {
					const {items: nestedItems, ...otherProps} = action;

					if (nestedItems?.length) {
						if (action.separator && index !== 0) {
							processedActions.push({type: 'divider'});
						}

						processedActions.push({
							...otherProps,
							items: getDropdownActions(nestedItems),
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

		const props = {
			actions: formattedActions && getDropdownActions(formattedActions),
			description: getLocalizedValue(item, schema.description)?.value,
			href: (schema.link && item[schema.link]) || null,
			imgProps:
				schema.image &&
				imagePropsTransformer(
					getLocalizedValue(item, schema.image)?.value
				),
			labels: getLabels(item),
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

				if (target.tagName !== 'INPUT') {
					event.preventDefault();

					onItemSelectionChange?.(item, true);
				}
			},
			onSelectChange: selectable
				? () => {
						onItemSelectionChange?.(item);
					}
				: undefined,
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
			stickerProps: (schema.sticker && item[schema.sticker]) || null,
			symbol: schema.symbol && item[schema.symbol],
			title: getLocalizedValue(item, schema.title)?.value || '',
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
