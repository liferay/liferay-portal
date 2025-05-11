/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Icon from '@clayui/icon';
import {useOutletContext, useParams} from 'react-router-dom';

import QATable from '../../../../../components/QATable';
import i18n from '../../../../../i18n';
import {formatDate} from '../../../../../utils/date';
import formatLocaleCurrency from '../../../../../utils/formatLocaleCurrency';
import {
	getSpecificationByKey,
	isCloudProduct,
} from '../../../../../utils/productUtils';
import {safeJSONParse} from '../../../../../utils/util';
import getProductPriceModel from '../../../../GetApp/utils/getProductPriceModel';

import './App.scss';

type HeaderProps = {
	icon: string;
	title: string;
};

const Header = ({icon, title}: HeaderProps) => {
	return (
		<div className="detailed-card-header">
			<h2>{title}</h2>

			<div className="detailed-card-header-icon-container">
				<Icon
					className="detailed-card-header-clay-icon"
					symbol={icon}
				/>
			</div>
		</div>
	);
};

const getPriceList = (
	isCloud: boolean,
	isPaidApp: boolean,
	placedOrder: PlacedOrder
) => {
	if (isPaidApp) {
		return {
			title: 'License Price',
			value: (
				<table className="qa-table">
					<thead>
						<tr>
							<th {...{width: '40%'}}>
								{i18n.translate('type')}
							</th>

							<th {...{width: '30%'}}>{i18n.translate('qty')}</th>

							<th>{i18n.translate('total')}</th>
						</tr>
					</thead>
					<tbody>
						{placedOrder.placedOrderItems.map(
							(order: PlacedOrderItems, index: number) => {
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
											<td className="text-capitalize">
												{type}
											</td>

											<td>{order.quantity}</td>

											<td>
												{formatLocaleCurrency(
													order.quantity *
														order.price.price
												)}
											</td>
										</tr>
									);
								}
							}
						)}
					</tbody>
				</table>
			),
		};
	}

	return {
		title: 'license',
		value: placedOrder.placedOrderItems.map(
			(order: PlacedOrderItems, index: number) => {
				return (
					<p key={index}>
						{formatLocaleCurrency(
							order.quantity * order.price.price
						)}
					</p>
				);
			}
		),
	};
};

const App = () => {
	const {orderId} = useParams();
	const {placedOrder, product} = useOutletContext<any>();

	const licenseType = getSpecificationByKey('license-type', product);

	const projectNameField =
		Object.values(placedOrder.customFields).find((field) =>
			Object.keys(field === 'Project Name')
		) || '-';

	const isCloud = isCloudProduct(product);
	const {isPaidApp} = getProductPriceModel(product);

	return (
		<div className="app-details-page-container mt-6">
			<div className="app-details-body-container">
				<div className="detailed-card-container">
					<Header
						icon="order-form-tag"
						title={i18n.translate('details')}
					/>

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
								value: licenseType?.value || '-',
							},
						]}
					/>
				</div>

				<div className="detailed-card-container">
					<Header
						icon="shopping-cart"
						title={i18n.translate('summary')}
					/>

					<QATable
						items={[
							getPriceList(isCloud, isPaidApp, placedOrder),
							{
								title: i18n.translate('subtotal'),
								value:
									formatLocaleCurrency(
										placedOrder.summary.subtotal
									) || '',
							},
							{
								title: i18n.translate('subtotal-discount'),
								value:
									formatLocaleCurrency(
										placedOrder.summary.totalDiscountValue
									) || '',
							},
							{
								title: i18n.translate('coupon-code'),
								value: placedOrder.couponCode || '-',
							},
							{
								title: i18n.translate('tax-vat'),
								value:
									formatLocaleCurrency(
										placedOrder.summary.taxValue
									) || '',
							},
							{
								title: i18n.translate('total'),
								value:
									formatLocaleCurrency(
										placedOrder.summary.total
									) || '-',
							},
						]}
					/>
				</div>

				{placedOrder.placedOrderBillingAddress && (
					<div className="detailed-card-container">
						<Header
							icon="geolocation"
							title={i18n.translate('address')}
						/>

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
					</div>
				)}
			</div>
		</div>
	);
};

export default App;
