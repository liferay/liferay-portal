/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Label from '@clayui/label';
import {ComponentProps} from 'react';

import ListView, {ListViewProps} from '../../../components/ListView';
import Page from '../../../components/Page';
import SearchBuilder from '../../../core/SearchBuilder';
import {
	ProductTypeVocabulary,
	ProductWorkflowDisplayType,
} from '../../../enums/Product';
import i18n from '../../../i18n';
import HeadlessCommerceAdminCatalog from '../../../services/rest/HeadlessCommerceAdminCatalog';
import {formatDate} from '../../../utils/date';

type AdministratorAppsListViewProps = {
	filter?: string;
	listViewProps?: Partial<ListViewProps<Product>>;
};

export function AdministratorAppsListView({
	filter,
	listViewProps,
}: AdministratorAppsListViewProps) {
	return (
		<ListView<Product>
			id="administrator-apps"
			resource={function getProducts({page, pageSize}) {
				return HeadlessCommerceAdminCatalog.getProducts(
					new URLSearchParams({
						'nestedFields': 'catalog,productSpecifications',
						'page': page.toString(),
						'pageSize': pageSize.toString(),
						'productSpecifications.pageSize': '-1',
						'sort': 'createDate:desc',
						...(filter ? {filter} : {}),
					})
				);
			}}
			tableProps={{
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
					},
					{
						id: '__marketplaceProduct',
						name: i18n.translate('app-type'),
						render: (marketplaceProduct) => (
							<div className="text-capitalize">
								{marketplaceProduct.appType}
							</div>
						),
					},
					{
						id: 'catalog',
						name: i18n.translate('publisher-name'),
						render: (catalog) => catalog.name,
					},
					{
						id: 'modifiedDate',
						name: i18n.translate('last-update'),
						render: (modifiedDate) => formatDate(modifiedDate),
					},
					{
						id: 'createDate',
						name: i18n.translate('published-at'),
						render: (createDate) => formatDate(createDate),
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
				navigateTo: ({productId}) => `/apps/${productId}`,
			}}
			{...listViewProps}
		/>
	);
}

export default function Apps() {
	return (
		<Page
			description={i18n.translate('list-with-latest-published-apps')}
			title="Marketplace Apps"
		>
			<AdministratorAppsListView
				filter={SearchBuilder.lambda(
					'categoryNames',
					ProductTypeVocabulary.APP
				)}
			/>
		</Page>
	);
}
