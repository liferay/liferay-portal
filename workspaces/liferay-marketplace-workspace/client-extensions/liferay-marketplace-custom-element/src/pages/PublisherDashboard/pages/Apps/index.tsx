/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {useNavigate} from 'react-router-dom';

import ListView from '../../../../components/ListView';
import OrderStatus from '../../../../components/OrderStatus';
import Page from '../../../../components/Page';
import {useMarketplaceContext} from '../../../../context/MarketplaceContext';
import SearchBuilder from '../../../../core/SearchBuilder';
import {
	ProductSpecificationKey,
	ProductTypeLabels,
	ProductTypeVocabulary,
	ProductWorkflowStatusCode,
	ProductWorkflowStatusLabel,
} from '../../../../enums/Product';
import i18n from '../../../../i18n';
import {formatDate} from '../../../../utils/date';
import {usePublisherDashboardOutletContext} from '../../PublisherDashboardOutlet';

function filterLatestProductVersions(products: Product[]): Product[] {
	const latestVersions = new Map<number, Product>();

	for (const product of products) {
		const current = latestVersions.get(product.productId);

		if (!current || product.version > current.version) {
			latestVersions.set(product.productId, product);
		}
	}

	return [...latestVersions.values()];
}

const Apps = () => {
	const {catalogId} = usePublisherDashboardOutletContext();
	const {properties} = useMarketplaceContext();
	const navigate = useNavigate();

	const isNewAppEnabled = properties.featureFlags.includes('LPD-24546');

	return (
		<Page
			description={i18n.translate(
				'manage-and-publish-apps-on-the-marketplace'
			)}
			rightButton={
				<ClayButton
					disabled={!catalogId}
					onClick={() =>
						navigate(
							isNewAppEnabled
								? '/newapp/publisher'
								: '/app/create'
						)
					}
				>
					{i18n.translate('new-app')}
				</ClayButton>
			}
			title={i18n.translate('apps')}
		>
			<ListView<Product>
				defaultFilters={{
					filter: new SearchBuilder()
						.eq('catalogId', (catalogId || 0) as number, {
							unquote: true,
						})
						.and()
						.lambda('categoryNames', ProductTypeVocabulary.APP)
						.build(),
				}}
				emptyStateProps={{
					className:
						'border px-4 py-6 d-flex align-items-center flex-column justify-content-center',
					description:
						"Publish apps and they will show up hereClick on 'Add Apps' to start.",
					title: i18n.translate('no-apps-yet'),
					type: 'BLANK',
				}}
				id={`publisher-apps/${catalogId}`}
				initialContext={{
					pageSize: 20,
					paginationDeltaOptions: [20, 40, 80, 120],
				}}
				resource={`/o/headless-commerce-admin-catalog/v1.0/products?${new URLSearchParams(
					{
						'accountId': '-1',
						'nestedFields': 'productSpecifications,sku',
						'skus.accountId': '-1',
						'sort': 'createDate:desc',
					}
				)}`}
				tableProps={{
					actions: isNewAppEnabled
						? [
								{
									disabled: (row: Product) =>
										row.productStatus ===
										ProductWorkflowStatusCode.PENDING,
									name: i18n.translate('edit-details'),
									onClick: (row: Product) =>
										navigate(
											`newapp/${row.productId}/publisher/profile`
										),
								},
								{
									disabled: (row: Product) =>
										row.productStatus !==
										ProductWorkflowStatusCode.APPROVED,
									name: i18n.translate('add-new-version'),
									onClick: (row: Product) =>
										navigate(
											`newapp/${row.productId}/newbuild`
										),
								},
							]
						: undefined,
					columns: [
						{
							clickable: true,
							id: 'name',
							name: i18n.translate('name'),
							render: (name, item) => {
								return (
									<div className="align-items-center d-flex">
										<img
											alt={`${name.en_US} app icon`}
											className="app-details-page-table-icon"
											draggable={false}
											height={32}
											src={item.thumbnail}
											width={32}
										/>

										<span className="font-weight-semi-bold ml-2 text-truncate">
											{name.en_US}
										</span>
									</div>
								);
							},
							size: 'sm',
						},
						{
							id: 'productSpecifications',
							name: i18n.translate('version'),
							render: (productSpecification) => {
								const version = productSpecification.find(
									(specification) =>
										specification.specificationKey ===
										ProductSpecificationKey.APP_VERSION
								)?.value?.en_US;

								return (
									<div className="text-capitalize">
										{version ? version : '1.0.0'}
									</div>
								);
							},
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

								return (
									<div className="text-capitalize">
										{label}
									</div>
								);
							},
						},
						{
							id: 'modifiedDate',
							name: i18n.translate('last-update'),
							render: (modifiedDate) => (
								<b>{formatDate(modifiedDate ?? '')}</b>
							),
						},
						{
							id: 'workflowStatusInfo',
							name: i18n.translate('status'),
							render: (workflowStatusInfo) => {
								if (!workflowStatusInfo?.label) {
									return null;
								}

								return (
									<OrderStatus
										orderStatus={workflowStatusInfo.label}
									>
										{
											ProductWorkflowStatusLabel[
												workflowStatusInfo.code as keyof typeof ProductWorkflowStatusLabel
											]
										}
									</OrderStatus>
								);
							},
						},
					],
					navigateTo: (item) => `/app/${item.productId}`,
				}}
				transformData={(response) => {
					const items = filterLatestProductVersions(response.items);

					return {
						...response,
						items,
						totalCount:
							response.totalCount -
							(items.length - response.items.length),
					};
				}}
			/>
		</Page>
	);
};

export default Apps;
