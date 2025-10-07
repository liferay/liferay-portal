/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useNavigate} from 'react-router-dom';
import {KeyedMutator} from 'swr';

import ListView from '../../../../components/ListView';
import Page from '../../../../components/Page';
import {PaymentStatus as PaymentStatusCode} from '../../../../enums/Order';
import i18n from '../../../../i18n';
import {Liferay} from '../../../../liferay/liferay';
import HeadlessCommerceAdminOrder from '../../../../services/rest/HeadlessCommerceAdminOrder';
import PaymentStatus from '../../components/PaymentStatus/PaymentStatus';

async function onClickMarkAsPaid(order: Order, mutate: KeyedMutator<any>) {
	try {
		await HeadlessCommerceAdminOrder.patchOrder(order.id, {
			paymentStatus: PaymentStatusCode.PAID,
		});

		mutate((response: Order) => response, {
			revalidate: true,
		});

		Liferay.Util.openToast({
			message: i18n.translate('order-marked-as-paid'),
			type: 'success',
		});
	}
	catch {
		Liferay.Util.openToast({
			message: i18n.translate('oops-something-went-wrong'),
			type: 'danger',
		});
	}
}

const Orders = () => {
	const navigate = useNavigate();

	return (
		<Page
			description={i18n.translate(
				'section-that-shows-the-latest-sales-made'
			)}
			pageRendererProps={{
				className: 'border py-2 rounded-lg mb-8',
			}}
			title={i18n.translate('last-orders')}
		>
			<ListView<Order>
				emptyStateProps={{title: i18n.translate('no-orders-yet')}}
				id="finance-dashboard-orders"
				managementToolbarProps={{
					filterSchema: 'financeDashboardOrders',
					searchVisible: true,
					visible: true,
				}}
				resource="o/headless-commerce-admin-order/v1.0/orders?nestedFields=account,orderItems&sort=createDate:desc"
				tableProps={{
					actions: [
						{
							disabled(order) {
								return [
									PaymentStatusCode.CANCELED,
									PaymentStatusCode.PAID,
								].includes(order.paymentStatus);
							},
							name: i18n.translate('mark-as-paid'),
							onClick: onClickMarkAsPaid,
						},
						{
							name: i18n.translate('view-details'),
							onClick: (order) => navigate('/order/' + order.id),
						},
					],
					columns: [
						{
							clickable: true,
							id: 'id',
							name: i18n.translate('id'),
							render: (id) => <b>{id}</b>,
						},
						{
							id: 'orderDate',
							name: 'Date',
							render: (orderDate) => {
								const date = new Date(orderDate as string);

								return (
									<div className="d-flex flex-column justify-content-center">
										<p className="mb-0 pt-1">
											{date.toLocaleDateString('en-US', {
												day: 'numeric',
												month: 'short',
												year: 'numeric',
											})}
										</p>
										<p className="mb-0 text-muted">
											{date.toLocaleTimeString('en-US', {
												hour: 'numeric',
												minute: '2-digit',
											})}
										</p>
									</div>
								);
							},
						},
						{
							id: 'account',
							name: i18n.translate('account'),
							render: (account) => account?.name,
						},
						{
							id: 'orderItems',
							name: i18n.translate('app'),
							render: (orderItems) => orderItems[0].name?.en_US,
						},
						{
							id: 'paymentStatusInfo',
							name: i18n.translate('payment-status'),
							render: (paymentStatusInfo) => (
								<PaymentStatus
									paymentStatus={paymentStatusInfo.code}
								/>
							),
						},
						{
							id: 'totalFormatted',
							name: i18n.translate('total'),
						},
					],
					navigateTo: (order) => `/order/${order.id}`,
				}}
			/>
		</Page>
	);
};
export default Orders;
