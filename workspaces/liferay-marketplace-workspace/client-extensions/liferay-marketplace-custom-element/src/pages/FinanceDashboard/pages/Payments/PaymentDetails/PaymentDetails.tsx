/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayTable from '@clayui/table';
import {useParams} from 'react-router-dom';

import {DetailedCard} from '../../../../../components/DetailedCard/DetailedCard';
import {PageRenderer} from '../../../../../components/Page';
import QATable, {Orientation} from '../../../../../components/QATable';
import Table from '../../../../../components/Table/Table';
import {PaymentStatus as PaymentStatusCode} from '../../../../../enums/Order';
import usePublisherSalesSummaryObject from '../../../../../hooks/usePublisherSalesSummaryObject';
import i18n from '../../../../../i18n';
import {Liferay} from '../../../../../liferay/liferay';
import PublisherSalesSummary from '../../../../../services/rest/PublisherSalesSummary';
import {safeJSONParse} from '../../../../../utils/util';
import DetailsHeader from '../../../components/DetailsHeader/DetailsHeader';
import PaymentStatus from '../../../components/PaymentStatus/PaymentStatus';
import {formatCurrency, getTotalByOrderKey} from '../../../util/finance';
import {formatDate, textWrapper} from '../../../util/util';
import {PublisherPayoutStatus} from '../Payments';

enum Payouts {
	MP_COMMISSION = 0.2,
	PUBLISHER_PAYOUT = 0.8,
}

export function formatPostalAddress(
	address: AccountPostalAddresses | undefined
) {
	if (!address || !Object.keys(address).length) {
		return '-';
	}

	return [
		address.streetAddressLine1,
		address.addressLocality,
		address.addressRegion,
		address.postalCode,
		address.addressCountry,
	]
		.filter(Boolean)
		.join(', ');
}

const PaymentDetails = () => {
	const {entryId} = useParams();

	const {data, error, isLoading, mutate} = usePublisherSalesSummaryObject(
		entryId as string
	);

	const {
		account,
		completeOrderItems,
		postalAddresses = {items: []},
		publisherSalesSummary,
	} = data || {};

	const postalAddress = postalAddresses.items.find(
		(address) => address.addressType === 'billing'
	);

	const emailAddress = account?.customFields?.find(
		(customField) => customField.name === 'Contact Email'
	)?.customValue?.data;

	const paymentStatus = publisherSalesSummary?.paymentStatus.key;

	const paymentStautsCode =
		paymentStatus === PublisherPayoutStatus.PAID
			? PaymentStatusCode.PAID
			: PaymentStatusCode.PENDING;

	return (
		<PageRenderer
			className="app-details-header d-flex flex-column w-100"
			error={error}
			isLoading={isLoading}
		>
			<DetailsHeader
				backLink="/payments"
				onClick={async () =>
					PublisherSalesSummary.patchPublisherSalesSummary(
						{
							paidBy: Liferay.ThemeDisplay.getUserName(),
							paidDate: new Date().toISOString(),
							paymentStatus: {
								key: PublisherPayoutStatus.PAID,
							},
						},
						entryId!
					).then((updatedPublisherSalesSummary) => {
						mutate(
							{
								...data!,
								publisherSalesSummary: {
									...data!.publisherSalesSummary,
									...updatedPublisherSalesSummary,
								},
							},
							{revalidate: false}
						);
					})
				}
				paymentStatusCode={paymentStautsCode}
				showButton={paymentStatus !== PublisherPayoutStatus.PAID}
				title={publisherSalesSummary?.publisherName as string}
			/>

			<div className="d-flex mt-5">
				<DetailedCard
					cardIconAltText="order-form-pencil"
					cardTitle={i18n.translate('publisher-details')}
					className="mr-5 w-100"
					clayIcon="order-form-pencil"
				>
					<QATable
						items={[
							{
								title: i18n.translate('publisher-name'),
								value: textWrapper(account?.name),
							},
							{
								title: i18n.translate('email'),
								value: textWrapper(emailAddress as string),
							},
							{
								title: i18n.translate('billing-address'),
								value: textWrapper(
									formatPostalAddress(postalAddress)
								),
							},
							{
								title: i18n.translate('vat-number'),
								value: textWrapper(account?.taxId),
							},
							{
								title: i18n.translate('quarter'),
								value: textWrapper(
									publisherSalesSummary?.quarter
								),
							},
						]}
						orientation={Orientation.VERTICAL}
					/>
				</DetailedCard>

				<DetailedCard
					cardIconAltText="paste-plaintext"
					cardTitle={i18n.translate('transaction-details')}
					className="w-100"
					clayIcon="paste-plaintext"
				>
					<QATable
						items={[
							{
								title: i18n.translate('apps-sold'),
								value: textWrapper(
									publisherSalesSummary
										?.publisherToCommerceOrder?.length
								),
							},
							{
								title: i18n.translate('net-price'),
								value: textWrapper(
									getTotalByOrderKey(
										'subtotalAmount',
										publisherSalesSummary?.publisherToCommerceOrder!
									)
								),
							},
							{
								title: i18n.translate('total'),
								value: textWrapper(
									getTotalByOrderKey(
										'totalAmount',
										publisherSalesSummary?.publisherToCommerceOrder!
									)
								),
							},
							{
								title: i18n.translate('mp-commission'),
								value: textWrapper(
									getTotalByOrderKey(
										'totalAmount',
										publisherSalesSummary?.publisherToCommerceOrder!,
										Payouts.MP_COMMISSION
									)
								),
							},
							{
								title: i18n.translate('publisher-payout'),
								value: textWrapper(
									getTotalByOrderKey(
										'totalAmount',
										publisherSalesSummary?.publisherToCommerceOrder!,
										Payouts.PUBLISHER_PAYOUT
									)
								),
							},
							{
								title: i18n.translate('status'),
								value: (
									<PaymentStatus
										paymentStatus={paymentStautsCode}
									/>
								),
							},
							{
								title: i18n.translate('paid-date'),
								value: textWrapper(
									formatDate(publisherSalesSummary?.paidDate)
								),
								visible:
									paymentStatus ===
									PublisherPayoutStatus.PAID,
							},
							{
								title: i18n.translate('paid-by'),
								value: textWrapper(
									publisherSalesSummary?.paidBy
								),
								visible:
									paymentStatus ===
									PublisherPayoutStatus.PAID,
							},
						]}
						orientation={Orientation.VERTICAL}
					/>
				</DetailedCard>
			</div>

			<DetailedCard
				cardIconAltText="order-form"
				cardTitle={i18n.translate('order-details')}
				className="mt-5 pb-0 w-100"
				clayIcon="price-tag"
			>
				<Table
					className="table-borderless"
					columns={[
						{
							key: 'placedOrderItem',
							render: (placedOrderItem) => {
								const [skuOption] = safeJSONParse(
									placedOrderItem.options,
									[
										{
											skuOptionValueKey: 'Standard',
											skuOptionValueName: 'Standard',
										},
									]
								);

								return (
									<div className="pt-2">
										<div className="d-flex">
											<img
												alt="App Icon"
												className="mr-2 order-details-app-icon rounded"
												draggable={false}
												src={placedOrderItem.thumbnail}
											/>
											<span>
												<strong>
													{placedOrderItem.name}
												</strong>

												<p className="finance-dashboard-secondary-text mb-0 pb-0 text-capitalize">
													{skuOption?.skuOptionValueKey ||
														skuOption?.skuOptionValueName}
												</p>
											</span>
										</div>
									</div>
								);
							},
							title: i18n.translate('app-name'),
						},
						{
							key: 'placedOrderItem',
							render: (placedOrderItem, order) => (
								<div className="d-flex flex-column justify-content-center">
									<p className="mb-0">
										{order.orderItem.account.name}
									</p>
									<p className="mb-0 text-muted">
										{placedOrderItem.author}
									</p>
								</div>
							),
							title: i18n.translate('account'),
						},
						{
							key: 'placedOrderItem',
							render: (placedOrderItem) =>
								placedOrderItem.quantity,
							title: i18n.translate('quantity'),
						},
						{
							key: 'orderItem',
							render: (orderItem) =>
								formatCurrency(
									orderItem.currencyCode,
									orderItem.finalPrice
								),

							title: i18n.translate('net-price'),
						},
						{
							key: 'orderItem',
							render: (orderItem) =>
								formatCurrency(
									orderItem.currencyCode,
									orderItem.finalPriceWithTaxAmount -
										orderItem.finalPrice
								),
							title: i18n.translate('vat'),
						},
						{
							key: 'orderItem',
							render: (orderItem) =>
								formatCurrency(
									orderItem.currencyCode,
									orderItem?.finalPriceWithTaxAmount
								),
							title: i18n.translate('total'),
						},
					]}
					hasHover={false}
					rows={completeOrderItems || []}
				>
					<ClayTable.Row className="publisher-payout-footer w-100">
						<ClayTable.Cell colSpan={3}>
							<b>{i18n.translate('total')}</b>
						</ClayTable.Cell>

						<ClayTable.Cell>
							{getTotalByOrderKey(
								'subtotalAmount',
								publisherSalesSummary?.publisherToCommerceOrder!
							)}
						</ClayTable.Cell>

						<ClayTable.Cell>
							{getTotalByOrderKey(
								'taxAmountValue',
								publisherSalesSummary?.publisherToCommerceOrder!
							)}
						</ClayTable.Cell>

						<ClayTable.Cell>
							{getTotalByOrderKey(
								'totalAmount',
								publisherSalesSummary?.publisherToCommerceOrder!
							)}
						</ClayTable.Cell>
					</ClayTable.Row>
				</Table>
			</DetailedCard>
		</PageRenderer>
	);
};

export default PaymentDetails;
