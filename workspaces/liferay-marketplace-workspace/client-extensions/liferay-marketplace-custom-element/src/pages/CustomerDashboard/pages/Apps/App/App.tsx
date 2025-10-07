/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useOutletContext, useParams} from 'react-router-dom';

import {DetailedCard} from '../../../../../components/DetailedCard/DetailedCard';
import QATable from '../../../../../components/QATable';
import {ProductSpecificationKey} from '../../../../../enums/Product';
import i18n from '../../../../../i18n';
import {formatDate} from '../../../../../utils/date';
import formatLocaleCurrency from '../../../../../utils/formatLocaleCurrency';
import {
	getProductPriceModel,
	getProductSpecificationValue,
	isCloudProduct,
} from '../../../../../utils/productUtils';
import {safeJSONParse} from '../../../../../utils/util';

import './App.scss';

const getPriceList = (
	isCloud: boolean,
	isPaidApp: boolean,
	placedOrder: PlacedOrder
) => {
	if (!isPaidApp) {
		return {
			title: i18n.translate('license'),
			value: placedOrder.placedOrderItems.map((order, index) => (
				<p key={index}>
					{formatLocaleCurrency(order.quantity * order.price.price)}
				</p>
			)),
		};
	}

	return {
		title: 'License Price',
		value: (
			<table className="qa-table">
				<thead>
					<tr>
						<th width="40%">{i18n.translate('type')}</th>
						<th width="30%">{i18n.translate('qty')}</th>
						<th>{i18n.translate('total')}</th>
					</tr>
				</thead>
				<tbody>
					{placedOrder.placedOrderItems.map((order, index) => {
						const optionName = safeJSONParse<any>(
							order.options,
							[]
						);

						const type = isCloud
							? 'Standard'
							: optionName[0]?.value || '';
						{
							return (
								<tr key={index}>
									<td className="text-capitalize">{type}</td>

									<td>{order.quantity}</td>

									<td>{order.price.priceFormatted}</td>
								</tr>
							);
						}
					})}
				</tbody>
			</table>
		),
	};
};

const App = () => {
	const {orderId} = useParams();
	const {placedOrder, product} = useOutletContext<any>();

	const licenseType = getProductSpecificationValue(
		ProductSpecificationKey.APP_LICENSING_TYPE,
		product
	);

	const projectNameField =
		Object.values(placedOrder.customFields).find((field) =>
			Object.keys(field === 'Project Name')
		) || '-';

	const isCloud = isCloudProduct(product);
	const {isPaidApp} = getProductPriceModel(product);

	return (
		<div className="app-details-page-container mt-6">
			<div className="app-details-body-container">
				<DetailedCard
					cardIconAltText="Profile Icon"
					cardTitle={i18n.translate('details')}
					clayIcon="order-form-tag"
				>
					<QATable
						items={[
							{
								title: i18n.translate('order-id'),
								value: orderId,
							},
							{
								title: i18n.translate('order-date'),
								value: formatDate(placedOrder.createDate),
							},
							{
								title: i18n.translate('customer-account'),
								value: placedOrder.account,
							},
							{
								title: i18n.translate('customer-project'),
								value: projectNameField,
							},
							{
								title: i18n.translate('purchased-by'),
								value: placedOrder.author,
							},
							{
								title: 'Purchase Order Number',
								value: placedOrder.purchaseOrderNumber || '-',
							},
							{
								title: i18n.translate('license-type'),
								value: licenseType || '-',
							},
						]}
					/>
				</DetailedCard>

				<DetailedCard
					cardIconAltText="Profile Icon"
					cardTitle={i18n.translate('summary')}
					clayIcon="shopping-cart"
				>
					<QATable
						items={[
							getPriceList(isCloud, isPaidApp, placedOrder),
							{
								title: i18n.translate('subtotal'),
								value: placedOrder.summary.subtotalFormatted,
							},
							{
								title: i18n.translate('subtotal-discount'),
								value: placedOrder.summary
									.subtotalDiscountValueFormatted,
							},
							{
								title: i18n.translate('coupon-code'),
								value: placedOrder.couponCode || '-',
							},
							{
								title: i18n.translate('tax-vat'),
								value: placedOrder.summary.taxValueFormatted,
							},
							{
								title: i18n.translate('total'),
								value: placedOrder.summary.totalFormatted,
							},
						]}
					/>
				</DetailedCard>

				{placedOrder.placedOrderBillingAddress && (
					<DetailedCard
						cardIconAltText="Profile Icon"
						cardTitle={i18n.translate('address')}
						clayIcon="geolocation"
					>
						<QATable
							items={[
								{
									title: i18n.translate('billing-address'),
									value:
										placedOrder.placedOrderBillingAddress
											.street1 || '',
								},
								{
									title: '',
									value: placedOrder.placedOrderBillingAddress
										.city,
								},
								{
									title: '',
									value: `${
										placedOrder.placedOrderBillingAddress
											.regionISOCode || ''
									}, ${
										placedOrder.placedOrderBillingAddress
											.zip || ''
									}`,
								},
								{
									title: '',
									value:
										placedOrder.placedOrderBillingAddress
											.countryISOCode || '',
								},
							]}
						/>
					</DetailedCard>
				)}
			</div>
		</div>
	);
};

export default App;
