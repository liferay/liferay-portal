/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Label from '@clayui/label';
import {ComponentProps} from 'react';

import ListView, {ListViewProps} from '../../../../components/ListView';
import {ManagementToolbarProps} from '../../../../components/ListView/components/ManagementToolbar';
import SearchBuilder from '../../../../core/SearchBuilder';
import {
	ProductSpecificationKey,
	ProductTypeLabels,
	ProductTypeVocabulary,
	ProductWorkflowDisplayType,
} from '../../../../enums/Product';
import i18n from '../../../../i18n';
import {formatDate} from '../../../../utils/date';

type AdministratorAppsListViewProps = {
	filter?: string;
	isSortable?: boolean;
	listViewProps?: Partial<ListViewProps<Product>>;
	managementToolbarProps?: {
		visible?: boolean;
	} & Omit<
		ManagementToolbarProps,
		| 'actions'
		| 'onSelectAllRows'
		| 'rowSelectable'
		| 'tableProps'
		| 'totalItems'
	>;
};

const AdministratorAppsListView: React.FC<AdministratorAppsListViewProps> = ({
	isSortable = false,
	listViewProps,
	managementToolbarProps,
}) => (
	<ListView<Product>
		defaultFilters={{
			filter: `${SearchBuilder.lambda(
				'categoryNames',
				ProductTypeVocabulary.APP
			)}`,
		}}
		id="administrator-apps"
		managementToolbarProps={{
			filterSchema: 'administratorApps',
			...managementToolbarProps,
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
					onClick: (product: Product) => {
						window.open(
							`/group/guest/~/control_panel/manage?p_p_id=com_liferay_commerce_product_definitions_web_internal_portlet_CPDefinitionsPortlet&p_p_lifecycle=0&p_p_state=maximized&_com_liferay_commerce_product_definitions_web_internal_portlet_CPDefinitionsPortlet_mvcRenderCommandName=%2Fcp_definitions%2Fedit_cp_definition&_com_liferay_commerce_product_definitions_web_internal_portlet_CPDefinitionsPortlet_cpDefinitionId=${product.id}`,
							'_blank'
						);
					},
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
					sortable: isSortable,
				},
				{
					id: 'productSpecifications',
					name: i18n.translate('app-type'),
					render: (productSpecifications) => {
						const productType = productSpecifications.find(
							({specificationKey}) =>
								specificationKey ===
								ProductSpecificationKey.APP_TYPE
						)?.value?.en_US;

						const label =
							ProductTypeLabels[
								productType as keyof typeof ProductTypeLabels
							];

						return <div className="text-capitalize">{label}</div>;
					},
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
					sortable: isSortable,
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
								] as ComponentProps<typeof Label>['displayType']
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

export default AdministratorAppsListView;
