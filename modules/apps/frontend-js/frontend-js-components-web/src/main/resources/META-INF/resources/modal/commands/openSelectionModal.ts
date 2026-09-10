/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {IframeIframeProps} from '../components/Iframe';
import {ModalSize} from '../components/Modal';
import {EventHandler, IframeWindow, SearchContainer} from '../types';
import openModal from './openModal';

export interface OpenSelectionModalProps<T> {
	buttonAddLabel?: string;
	buttonCancelLabel?: string;
	containerProps?: {};
	customSelectEvent?: boolean;
	getSelectedItemsOnly?: boolean;
	height?: string;
	id?: string;
	iframeBodyCssClass?: string;
	multiple?: boolean;
	onClose?: EventHandler;
	onSelect: (selection: T) => void;
	selectEventName?: string;
	selectedData?: OpenSelectionModalSelectedDatum[] | string[];
	selectedDataCheckboxesDisabled?: boolean;
	size?: ModalSize;
	title: string;
	url: string;
	zIndex?: number;
}

export interface OpenSelectionModalSelectedDatum {
	externalReferenceCode: string;
	id: string;
	label: string;
}

export interface OpenSelectionModalSelectedItem {
	checked?: boolean;
	dataset?: DOMStringMap;
	value?: string;
	[prop: string]: boolean | DOMStringMap | string | undefined;
}

export default function openSelectionModal<
	T = OpenSelectionModalSelectedItem[],
>({
	buttonAddLabel = Liferay.Language.get('add'),
	buttonCancelLabel = Liferay.Language.get('cancel'),
	containerProps,
	customSelectEvent = false,
	getSelectedItemsOnly = true,
	height,
	id,
	iframeBodyCssClass,
	multiple = false,
	onClose,
	onSelect,
	selectEventName,
	selectedData,
	selectedDataCheckboxesDisabled = false,
	size,
	title,
	url,
	zIndex,
}: OpenSelectionModalProps<T>) {
	const eventHandlers: Liferay.EventHandler[] = [];
	let iframeWindowObj: IframeWindow | undefined;
	let processCloseFn: () => void;
	let selectedItem: OpenSelectionModalSelectedItem;

	const select = () => {
		if (!iframeWindowObj) {
			return;
		}

		if (multiple && !selectedItem) {
			const searchContainer =
				iframeWindowObj.document.querySelector('.searchcontainer');

			if (searchContainer) {
				iframeWindowObj.Liferay.componentReady<SearchContainer>(
					searchContainer.id
				).then((searchContainer) => {
					const allSelectedElements = getSelectedItemsOnly
						? searchContainer.select.getAllSelectedElements()
						: searchContainer.select._getAllElements(false);

					const allSelectedNodes = allSelectedElements.getDOMNodes();

					onSelect(

						// @ts-ignore

						allSelectedNodes.map((node) => {
							let item: OpenSelectionModalSelectedItem = {};

							if (node.value) {
								item.value = node.value;
							}

							if (!getSelectedItemsOnly && node.checked) {
								item.checked = node.checked;
							}

							if (node.dataset?.id && node.dataset.name) {
								item = {...item, ...node.dataset};
							}
							else {
								const row: HTMLElement | null =
									node.closest('dd, tr, li');

								if (row && Object.keys(row.dataset).length) {
									item = {...item, ...row.dataset};
								}
							}

							return item;
						})
					);

					processCloseFn();
				});
			}
			else {
				processCloseFn();
			}
		}
		else {

			// @ts-ignore

			onSelect(selectedItem);

			processCloseFn();
		}
	};

	const iframeProps: IframeIframeProps = {};

	if (selectedData) {
		const ercs: string[] = [];
		const ids: string[] = [];
		const labels: string[] = [];

		selectedData.forEach((item) => {
			const {externalReferenceCode, id, label} =
				item as OpenSelectionModalSelectedDatum;

			if (externalReferenceCode) {
				ercs.push(externalReferenceCode);
			}

			if (id) {
				ids.push(id);
			}

			if (label) {
				labels.push(label);
			}
		});

		if (ercs.length) {
			iframeProps['data-selecteditemsercs'] = ercs.join(',');
		}

		if (ids.length) {
			iframeProps['data-selecteditemsids'] = ids.join(',');
		}

		if (labels.length) {
			iframeProps['data-selecteditemslabels'] = labels.join(',');
		}
	}

	openModal({
		buttons: multiple
			? [
					{
						displayType: 'secondary',
						label: buttonCancelLabel,
						type: 'cancel',
					},
					{
						label: buttonAddLabel,
						onClick: select,
					},
				]
			: undefined,
		containerProps,
		disableButtonsOnLoading: multiple,
		height,
		id: id || selectEventName,
		iframeBodyCssClass,
		iframeProps,
		onClose: () => {
			eventHandlers.forEach((eventHandler) => {
				eventHandler.detach();
			});

			eventHandlers.splice(0, eventHandlers.length);

			if (onClose) {
				onClose();
			}
		},
		onOpen: ({iframeWindow, processClose}) => {
			if (!iframeWindow) {
				console.error('IframeWindow is null');

				return;
			}

			iframeWindowObj = iframeWindow;
			processCloseFn = processClose;

			const iframeBody = iframeWindow.document.body;

			const itemElements =
				iframeBody.querySelectorAll('.selector-button');

			if (selectedData) {
				const selectedDataSet = new Set(selectedData as string[]);

				itemElements.forEach((itemElement) => {
					const itemHTMLButtonElement =
						itemElement as HTMLButtonElement;

					const itemId =
						itemHTMLButtonElement.dataset.entityid ||
						itemHTMLButtonElement.dataset.entityname;

					if (itemId && selectedDataSet.has(itemId)) {
						itemHTMLButtonElement.disabled = true;
						itemHTMLButtonElement.classList.add('disabled');
					}
					else {
						itemHTMLButtonElement.disabled = false;
						itemHTMLButtonElement.classList.remove('disabled');
					}
				});

				if (multiple) {
					for (const row of iframeBody.querySelectorAll(
						'.searchcontainer tr'
					)) {
						const {dataset} = row as HTMLElement;

						const itemId = dataset.entityid || dataset.entityname;

						if (itemId && selectedDataSet.has(itemId)) {
							const checkbox: HTMLInputElement | null =
								row.querySelector('input[type="checkbox"]');

							if (!checkbox) {
								continue;
							}

							checkbox.checked = true;

							if (selectedDataCheckboxesDisabled) {
								checkbox.disabled = true;
							}
						}
					}
				}
			}

			if (selectEventName) {
				const selectEventHandler = Liferay.on(
					selectEventName,
					(event) => {
						selectedItem = event.data || event;

						if (!multiple) {
							select();
						}
					}
				);

				eventHandlers.push(selectEventHandler);

				if (!customSelectEvent) {
					iframeBody.addEventListener('click', (event) => {
						const targetElement = event.target as Element;

						const delegateTarget: HTMLElement | null =
							targetElement?.closest('.selector-button');

						if (delegateTarget) {
							Liferay.fire(
								selectEventName,
								delegateTarget.dataset
							);
						}
					});
				}
			}
		},
		size,
		title,
		url,
		zIndex,
	});
}
