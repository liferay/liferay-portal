/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Label from '@clayui/label';
import {ComponentProps} from 'react';
import {useNavigate} from 'react-router-dom';

import ListView from '../../../components/ListView';
import Page from '../../../components/Page';
import SearchBuilder from '../../../core/SearchBuilder';
import {
	ProductTypeVocabulary,
	ProductWorkflowDisplayType,
} from '../../../enums/Product';
import i18n from '../../../i18n';
import {formatDate} from '../../../utils/date';

export default function Solutions() {
	const navigate = useNavigate();

	return (
		<Page pageRendererProps={{className: 'border py-2'}} title="Solutions">
			<ListView<Product>
				defaultFilters={{
					filter: `${SearchBuilder.lambda(
						'categoryNames',
						ProductTypeVocabulary.SOLUTION
					)}`,
				}}
				id="administrator-solutions"
				managementToolbarProps={{
					filterSchema: 'administratorSolutions',
					searchVisible: true,
					visible: true,
				}}
				resource={`/o/headless-commerce-admin-catalog/v1.0/products?${new URLSearchParams(
					{
						'nestedFields': 'catalog,productSpecifications',
						'productSpecifications.pageSize': '-1',
						'sort': 'createDate:desc',
					}
				)}`}
				tableProps={{
					actions: [
						{
							name: i18n.translate('view-details'),
							onClick: (row) =>
								navigate(`/solutions/${row.productId}`),
						},
					],
					columns: [
						{
							clickable: true,
							id: 'name',
							name: i18n.translate('name'),
							render: (name, {thumbnail}) => (
								<div>
									<img
										alt="App Image"
										className="app-details-page-table-icon"
										src={thumbnail}
									/>

									<span className="font-weight-semi-bold ml-2 text-nowrap">
										{name?.en_US}
									</span>
								</div>
							),
							sortable: true,
						},
						{
							id: 'modifiedDate',
							name: i18n.translate('last-update'),
							render: (modifiedDate) => formatDate(modifiedDate),
							sortable: true,
						},
						{
							id: 'workflowStatusInfo',
							name: i18n.translate('status'),
							render: (
								workflowStatusInfo: Product['workflowStatusInfo']
							) => (
								<Label
									displayType={
										ProductWorkflowDisplayType[
											workflowStatusInfo.code as keyof typeof ProductWorkflowDisplayType
										] as ComponentProps<
											typeof Label
										>['displayType']
									}
								>
									{workflowStatusInfo.label}
								</Label>
							),
						},
					],
					navigateTo: ({productId}) => `/solutions/${productId}`,
				}}
			/>
		</Page>
	);
}
