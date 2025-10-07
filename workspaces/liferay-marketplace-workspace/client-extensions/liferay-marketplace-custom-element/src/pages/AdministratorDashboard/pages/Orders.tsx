/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Button from '@clayui/button';
import Icon from '@clayui/icon';
import ClayLabel from '@clayui/label';
import {Status} from '@clayui/modal/lib/types';
import {ClayTooltipProvider} from '@clayui/tooltip';
import {formatDistance} from 'date-fns';
import {Fragment, useMemo} from 'react';

import ListView, {ListViewProps} from '../../../components/ListView';
import {ManagementToolbarProps} from '../../../components/ListView/components/ManagementToolbar';
import Page from '../../../components/Page';
import {
	OrderCustomFields,
	OrderTypes,
	orderTypeLabel,
	orderWorkflowDisplayType,
	paymentWorkflowDisplayType,
} from '../../../enums/Order';
import i18n from '../../../i18n';
import {Liferay} from '../../../liferay/liferay';
import {FilterSchemaOption} from '../../../schema/filters';
import marketplaceOAuth2 from '../../../services/oauth/Marketplace';
import CommerceSelectAccount from '../../../services/rest/CommerceSelectAccount';
import {safeJSONParse} from '../../../utils/util';
import InfoCard from '../components/InfoCard';
import useOrderMetrics from '../hooks/useOrderMetrics';

type AdministratorOrdersListViewProps = {
	isSortable?: boolean;
	listViewProps?: Partial<ListViewProps<Order>>;
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

function redirectTo(path: string) {
	return async function (order: Order) {
		await CommerceSelectAccount.selectAccount(order.accountId);

		Liferay.CommerceContext.account = {
			accountId: order.accountId,
			accountName: '',
		};

		Liferay.Util.navigate(
			Liferay.ThemeDisplay.getLayoutURL().replace(
				'/administrator-dashboard',
				path
			)
		);
	};
}

export function AdministratorOrdersListView({
	isSortable = false,
	listViewProps,
	managementToolbarProps,
}: AdministratorOrdersListViewProps) {
	return (
		<ListView<Order>
			emptyStateProps={{title: i18n.translate('no-orders-yet')}}
			id="administrator-orders"
			managementToolbarProps={{
				actionButton: (
					filter: {
						[key: string]: string;
					},
					filterSchema?: FilterSchemaOption
				) => {
					return (
						<Button
							className="align-items-center d-flex h-100 justify-content-center ml-3 mr-4"
							displayType="unstyled"
							onClick={() =>
								marketplaceOAuth2.downloadOrderReport(
									filter,
									filterSchema
								)
							}
						>
							<Icon className="mr-2" symbol="download" />
							<b>{i18n.translate('export')}</b>
						</Button>
					);
				},

				filterSchema: 'administratorOrders',
				...managementToolbarProps,
			}}
			resource={`/o/headless-commerce-admin-order/v1.0/orders?${new URLSearchParams(
				{
					nestedFields: 'account,orderItems',
					sort: 'createDate:desc',
				}
			)}`}
			tableProps={{
				actions: [
					{
						name: i18n.translate('customer-dashboard'),
						onClick: redirectTo('/customer-dashboard'),
					},
					{
						name: i18n.translate('publisher-dashboard'),
						onClick: redirectTo('/publisher-dashboard'),
					},
					{
						name: i18n.translate('order-details'),
						onClick: (order: Order) => {
							window.open(
								`/group/guest/~/control_panel/manage?p_p_id=com_liferay_commerce_order_web_internal_portlet_CommerceOrderPortlet&p_p_lifecycle=0&p_p_state=maximized&_com_liferay_commerce_order_web_internal_portlet_CommerceOrderPortlet_mvcRenderCommandName=%2Fcommerce_order%2Fedit_commerce_order&_com_liferay_commerce_order_web_internal_portlet_CommerceOrderPortlet_commerceOrderId=${order.id}`,
								'_blank'
							);
						},
					},
				],
				columns: [
					{
						id: 'id',
						name: i18n.translate('id'),
						render: (id) => (
							<span className="font-weight-bold">{id}</span>
						),
					},
					{
						id: 'orderItems',
						name: i18n.translate('app-name'),
						render: (orderItems) => (
							<span className="text-wrap">
								{orderItems[0]?.name?.en_US}
							</span>
						),
					},
					{
						id: 'account',
						name: i18n.translate('user-account'),
						render: (account) => account.name,
					},
					{
						id: 'orderTypeExternalReferenceCode',
						name: i18n.translate('app-type'),
						render: (orderTypeExternalReferenceCode) => (
							<span>
								{
									orderTypeLabel[
										orderTypeExternalReferenceCode as keyof typeof OrderTypes
									]
								}
							</span>
						),
					},
					{
						id: 'totalFormatted',
						name: i18n.translate('amount'),
					},
					{
						id: 'orderStatusInfo',
						name: i18n.translate('order-status'),
						render: (orderStatusInfo) => (
							<ClayLabel
								className="text-nowrap"
								displayType={
									orderWorkflowDisplayType[
										orderStatusInfo.code as keyof typeof orderWorkflowDisplayType
									] as Status
								}
							>
								{orderStatusInfo.label_i18n}
							</ClayLabel>
						),
					},
					{
						id: 'paymentStatusInfo',
						name: i18n.translate('payment-status'),
						render: (paymentStatusInfo) => (
							<ClayLabel
								className="text-nowrap"
								displayType={
									paymentWorkflowDisplayType[
										paymentStatusInfo?.code as keyof typeof paymentWorkflowDisplayType
									] as Status
								}
							>
								{paymentStatusInfo.label_i18n}
							</ClayLabel>
						),
					},
					{
						id: 'customFields',
						name: i18n.translate('customer-project'),
						render: (customFields) => {
							const projects = safeJSONParse(
								customFields![
									OrderCustomFields.KORONEIKI_PROJECT
								],
								[]
							);

							const Wrapper = projects.length
								? ClayTooltipProvider
								: Fragment;

							return (
								<Wrapper>
									<div
										data-tooltip-align="bottom"
										title={projects
											.map(
												({name}, index) =>
													`${projects.length > 1 ? `(${index + 1})` : ''} ${name}`
											)
											.join('\n')}
									>
										{projects.length ? 'Yes' : 'No'}
									</div>
								</Wrapper>
							);
						},
					},
					{
						id: 'createDate',
						name: i18n.translate('created-at'),
						render: (createDate) => (
							<span
								className="ml-2 text-capitalize text-nowrap"
								title={createDate}
							>
								{formatDistance(
									new Date(createDate ?? ''),
									Date.now(),
									{addSuffix: true}
								)}
							</span>
						),
						sortable: isSortable,
					},
				],
			}}
			{...listViewProps}
		/>
	);
}

export default function Orders() {
	const {data: metrics} = useOrderMetrics('week');

	const infoCard = useMemo(
		() => [
			{
				growth: metrics?.growth ?? 0,
				growthContext: `+${metrics?.lastPeriod ?? 0} this week `,
				title: 'Total Orders',
				value: metrics?.totalCount,
			},
			{
				title: 'Monthly Orders',
				value: metrics?.ordersThisMonth,
			},
			{
				title: 'Current Year Orders',
				value: metrics?.ordersThisYear,
			},
		],
		[
			metrics?.growth,
			metrics?.lastPeriod,
			metrics?.ordersThisMonth,
			metrics?.ordersThisYear,
			metrics?.totalCount,
		]
	);

	return (
		<>
			<div className="d-flex flex-column">
				<div className="d-flex flex-wrap info-container mb-4">
					{infoCard.map((card, index) => (
						<InfoCard
							{...card}
							expanded
							key={index}
							symbol="shopping-cart"
							title={card.title}
							value={card.value}
						/>
					))}
				</div>
			</div>
			<Page
				pageRendererProps={{className: 'border py-2'}}
				title={i18n.translate('orders')}
			>
				<AdministratorOrdersListView
					isSortable
					managementToolbarProps={{
						searchVisible: true,
						visible: true,
					}}
				/>
			</Page>
		</>
	);
}
