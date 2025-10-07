/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import i18n from '~/utils/I18n';

const getInitialColumns = () => [
	{
		accessor: 'ticket',
		bodyClass: 'border-0',
		filterIdentifier: 'ticketId',
		header: {
			name: (
				<div className="align-items-center d-flex">
					<div className="mr-2">{i18n.translate('ticket')}</div>
				</div>
			),
			styles:
				'h6 text-neutral-10 font-weight-bold table-cell-expand-smaller',
		},
		truncate: true,
	},
	{
		accessor: 'fileName',
		bodyClass: 'border-0',
		filterIdentifier: 'fileName',
		header: {
			name: (
				<div className="align-items-center d-flex">
					<div className="mr-2">{i18n.translate('file-name')}</div>
				</div>
			),
			styles: 'h6 text-neutral-10 font-weight-bold table-cell-expand',
		},
		truncate: true,
	},
	{
		accessor: 'fileSize',
		bodyClass: 'border-0',
		filterIdentifier: 'fileSize',
		header: {
			name: (
				<div className="align-items-center d-flex">
					<div className="mr-2">{i18n.translate('file-size')}</div>
				</div>
			),
			styles:
				'h6 text-neutral-10 font-weight-bold table-cell-expand-smaller',
		},
		truncate: true,
	},
	{
		accessor: 'attached',
		bodyClass: 'border-0',
		filterIdentifier: 'dateCreated',
		header: {
			name: (
				<div className="align-items-center d-flex">
					<div className="mr-2">{i18n.translate('attached')}</div>
				</div>
			),
			styles:
				'h6 text-neutral-10 font-weight-bold table-cell-expand-small',
		},
	},
	{
		accessor: 'options',
		align: 'right',
		bodyClass: 'border-0',
		header: {
			name: '',
			styles: 'border-bottom bg-transparent',
		},
	},
];

export function getColumns() {
	const columns = getInitialColumns();

	return columns;
}
