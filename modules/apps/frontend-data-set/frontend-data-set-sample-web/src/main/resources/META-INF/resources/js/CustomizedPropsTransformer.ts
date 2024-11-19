/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {IInternalRenderer} from '@liferay/frontend-data-set-web';
import {openModal} from 'frontend-js-web';

import CustomAuthorTableCell from './CustomAuthorTableCell';

export default function propsTransformer({
	additionalProps: {greeting},
	selectedItemsKey,
	...otherProps
}: any) {
	const customAuthorTableCellRenderer: IInternalRenderer = {
		component: CustomAuthorTableCell,
		name: 'customAuthorTableCellRenderer',
		type: 'internal',
	};

	return {
		...otherProps,
		customRenderers: {
			tableCell: [customAuthorTableCellRenderer],
		},
		onActionDropdownItemClick({
			action,
			itemData,
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
				alert(`${greeting} ${itemData.title}!`);
			}
		},
		onBulkActionItemClick({
			action,
			loadData,
			selectedData: {items, keyValues},
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
							onClick: ({closeModal}: {closeModal: Function}) => {
								closeModal();

								loadData();
							},
						},
					],
					size: 'md',
					title: 'You selected:',
				});
			}
		},
	};
}
