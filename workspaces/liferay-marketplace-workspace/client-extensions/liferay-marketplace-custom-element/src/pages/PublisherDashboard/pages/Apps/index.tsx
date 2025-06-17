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
import {MarketplaceProduct} from '../../../../entity/MarketplaceProduct';
import {
	ProductTypeVocabulary,
	ProductWorkflowStatusCode,
} from '../../../../enums/Product';
import i18n from '../../../../i18n';
import HeadlessCommerceAdminCatalog from '../../../../services/rest/HeadlessCommerceAdminCatalog';
import {formatDate} from '../../../../utils/date';
import {usePublisherDashboardOutletContext} from '../../PublisherDashboardOutlet';

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
				emptyStateProps={{
					className:
						'border px-4 py-6 d-flex align-items-center flex-column justify-content-center',
					description:
						"Publish apps and they will show up hereClick on 'Add Apps' to start.",
					title: i18n.translate('no-apps-yet'),
					type: 'BLANK',
				}}
				id={`publisher-apps/${catalogId}`}
				resource={function getPublisherProducts({page, pageSize}) {
					return HeadlessCommerceAdminCatalog.getProducts(
						new URLSearchParams({
							'accountId': '-1',
							'filter': new SearchBuilder()
								.eq('catalogId', (catalogId || 0) as number, {
									unquote: true,
								})
								.and()
								.lambda(
									'categoryNames',
									ProductTypeVocabulary.APP
								)
								.build(),
							'nestedFields': 'productSpecifications,skus',
							'page': page.toString(),
							'pageSize': pageSize.toString(),
							'skus.accountId': '-1',
							'sort': 'createDate:desc',
						})
					);
				}}
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
									<>
										<img
											alt={`${name.en_US} app icon`}
											className="app-details-page-table-icon"
											draggable={false}
											height={32}
											src={item.thumbnail}
											width={32}
										/>

										<span className="font-weight-semi-bold ml-2">
											{name.en_US}
										</span>
									</>
								);
							},
							size: 'sm',
						},
						{
							id: '__marketplaceProduct',
							name: i18n.translate('version'),
							render: (marketplaceProduct: MarketplaceProduct) =>
								marketplaceProduct.appVersion || '',
						},
						{
							id: '__marketplaceProduct',
							name: i18n.translate('app-type'),
							render: (marketplaceProduct: MarketplaceProduct) =>
								marketplaceProduct.appType,
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
										{workflowStatusInfo.label}
									</OrderStatus>
								);
							},
						},
					],
					navigateTo: (item) => `/app/${item.productId}`,
				}}
			/>
		</Page>
	);
};

export default Apps;
