/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {DEFAULT_FETCH_HEADERS} from '@liferay/frontend-data-set-web';
import classNames from 'classnames';
import {openModal} from 'frontend-js-components-web';
import {fetch} from 'frontend-js-web';

import CustomAuthorTableCell from './CustomAuthorTableCell';
import CustomListTitleRenderer from './CustomListTitleRenderer';
import SampleInfoPanel from './SampleInfoPanel';
import dummyUploader from './dummyUploader';
import {advancedFDSAtom} from './utils/atoms';

import type {
	ICardSchema,
	IFileDropSettings,
	IInternalRenderer,
	IItemsActions,
	IListSchema,
	IView,
} from '@liferay/frontend-data-set-web';

function applyStyles(itemsActions: Array<IItemsActions>): Array<IItemsActions> {
	return itemsActions.map((action: IItemsActions) => {
		const newItems = action.items ? applyStyles(action.items) : undefined;
		const itemsChanged = newItems !== action.items;

		const needsStyling = action?.data?.id === 'sampleDeleteMessage';

		if (!itemsChanged && !needsStyling) {
			return action;
		}

		return {
			...action,
			...(itemsChanged && {items: newItems}),
			...(needsStyling && {className: 'text-danger'}),
		};
	});
}

export default function propsTransformer({
	additionalProps: {greeting},
	itemsActions,
	selectedItemsKey,
	...otherProps
}: any) {
	const customAuthorTableCellRenderer: IInternalRenderer = {
		component: CustomAuthorTableCell,
		name: 'customAuthorTableCellRenderer',
		type: 'internal',
	};

	const customListTitleRenderer: IInternalRenderer = {
		component: CustomListTitleRenderer,
		name: 'customListTitleRenderer',
		type: 'internal',
	};

	const fileDropSettings: IFileDropSettings = {
		enabled: true,
		isDropTarget: ({item}: {item: any}) => item.color !== 'Green',
		onFileDrop: dummyUploader,
	};

	const views: Array<IView> = otherProps.views;

	const cardView = views.find((view) => view.name === 'cards')!;

	cardView.setItemComponentProps = ({
		item,
		props,
	}: {
		item: any;
		props: any;
	}) => {
		if (item.title === 'Sample1') {
			const schema = cardView.schema as ICardSchema;

			return {
				...props,
				imgProps: {
					src: '/documents/d/guest/planet-png',
				},
				stickerProps: {
					displayType: 'outline-7',
					position: 'bottom-left',
					shape: 'circle',
				},
				title: `${item[schema.title]} 🚀`,
			};
		}

		return props;
	};

	const listView = views.find((view) => view.name === 'list')!;

	const listSchema = listView.schema as IListSchema;

	listView.setItemComponentProps = ({
		item,
		props,
	}: {
		item: any;
		props: any;
	}) => {
		const updatedProps = {
			...props,
			schema: {
				...listSchema,
				titleRenderer: 'customListTitleRenderer',
			},
		};

		if (item.title === 'Sample1') {
			return {
				...updatedProps,
				className: classNames(
					'sample-css-class',
					updatedProps.className
				),
			};
		}

		return updatedProps;
	};

	const tableView = views.find((view) =>
		view.name?.toLowerCase().includes('table')
	)!;

	tableView.setItemComponentProps = ({
		item,
		props,
	}: {
		item: any;
		props: any;
	}) => {
		if (item.title === 'Sample1') {
			return {
				...props,
				className: classNames('sample-css-class', props.className),
			};
		}

		return props;
	};

	return {
		...otherProps,
		atom: advancedFDSAtom,
		customRenderers: {
			listSection: [customListTitleRenderer],
			tableCell: [customAuthorTableCellRenderer],
		},
		fileDropSettings,
		infoPanelComponent: SampleInfoPanel,
		itemsActions: applyStyles(itemsActions),
		onActionDropdownItemClick({
			action,
			itemData,
			items,
			loadData,
			openSidePanel,
		}: any) {
			if (action.data.id === 'openSidePanel') {
				openSidePanel({url: 'about:blank'});
			}
			else if (action.data.id === 'reload') {
				loadData();
			}
			else if (action.data.id === 'sampleMessage') {
				const itemsIds = items
					.map((item: any): string => item.id)
					.join(',');

				const currentItemPos =
					items.findIndex((item: any) => item.id === itemData.id) + 1;

				alert(
					`${greeting} ${itemData.title}! You are ${itemData.id}, the element #${currentItemPos} in [${itemsIds}]`
				);
			}
		},
		onBulkActionItemClick({
			action,
			loadData,
			selectedData: {filters, items, keyValues, searchQuery, selectAll},
		}: any) {
			if (action.data.id === 'sampleBulkAction') {
				openModal({
					bodyHTML: `
						<ol>
							${items.map((item: any) => `<li>${item.title}</li>`).join('')}
						</ol>

						<p>
							Key field: <code>${selectedItemsKey}</code> <br>
							Values of key fields of selected items:
							<ol>
								${keyValues.map((keyValue: any) => `<li>${keyValue}</li>`).join('')}
							</ol>
						</p>
					`,
					buttons: [
						{
							label: 'OK',
							onClick: ({processClose}) => {
								processClose();

								loadData();
							},
						},
					],
					size: 'md',
					title: 'You selected:',
				});
			}

			if (action.data.id === 'testBulkAction') {
				fetch(action.href, {
					body: JSON.stringify({
						filters,
						items,
						keyValues,
						searchQuery,
						selectAll,
					}),
					headers: DEFAULT_FETCH_HEADERS,
					method: action.data.method,
				});
			}
		},
		views,
	};
}
