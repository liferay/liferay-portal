/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Label from '@clayui/label';

import ListView from '../../../components/ListView';
import Page from '../../../components/Page';
import SearchBuilder from '../../../core/SearchBuilder';
import i18n from '../../../i18n';
import {formatDate} from '../../../utils/date';

export function Publishers() {
	return (
		<Page
			pageRendererProps={{className: 'border py-2 rounded-lg'}}
			title={i18n.translate('publishers')}
		>
			<ListView<Account>
				defaultFilters={{
					filter: `${SearchBuilder.eq('type', 'supplier')}`,
				}}
				id="administrator-publishers"
				managementToolbarProps={{
					filterSchema: 'administratorPublishers',
					searchVisible: true,
					visible: true,
				}}
				resource={`/o/headless-admin-user/v1.0/accounts?${new URLSearchParams({sort: 'dateCreated:desc'})}`}
				tableProps={{
					columns: [
						{
							id: 'name',
							name: 'Name',
							render: (name, {logoURL}) => (
								<div>
									<img
										className="mr-2 rounded"
										draggable={false}
										height={42}
										src={logoURL}
										width={42}
									/>
									<span className="font-weight-bold">
										{name}
									</span>
								</div>
							),
							sortable: true,
						},
						{
							id: 'id',
							name: 'ID',
						},
						{
							id: 'externalReferenceCode',
							name: 'External Reference Code',
						},
						{
							id: 'customFields',
							name: 'Type',
							render: (customFields) => {
								const type = customFields?.find(
									({name}) => name === 'AccountType'
								);

								return (
									type?.customValue.data ||
									'Marketplace Publisher'
								);
							},
							sortable: true,
						},
						{
							id: 'dateCreated',
							name: 'Created at',
							render: (createDate) => formatDate(createDate),
							sortable: true,
						},
						{
							id: 'status',
							name: 'Status',
							render: (status) => {
								return (
									<Label
										displayType={
											status === 0 ? 'success' : 'warning'
										}
									>
										{status === 0 ? 'Approved' : 'Pending'}
									</Label>
								);
							},
						},
					],
				}}
			/>
		</Page>
	);
}
