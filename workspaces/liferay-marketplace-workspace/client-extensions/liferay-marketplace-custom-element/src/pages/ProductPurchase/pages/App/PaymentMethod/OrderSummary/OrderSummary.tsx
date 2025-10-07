/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import {useSelector} from '@xstate/store/react';
import {useEffect} from 'react';
import {useNavigate} from 'react-router-dom';

import paypal from '../../../../../../assets/images/paypal.png';
import ProductPurchase from '../../../../../../components/ProductPurchase';
import {Section} from '../../../../../../components/Section/Section';
import i18n from '../../../../../../i18n';
import {Liferay} from '../../../../../../liferay/liferay';
import {formatCurrency} from '../../../../../../utils/currencies';
import {getProductPriceModel} from '../../../../../../utils/productUtils';
import {useProductPurchaseOutletContext} from '../../../../ProductPurchaseOutlet';
import ProductPurchaseApp from '../../../../services/ProductPurchaseApp';
import {productPurchaseStore} from '../../../../store/AppPurchaseStore';
import {PaymentMethodType} from '../../../../types';
import LicenseTermsCheckbox from '../../License/LicenseTermsCheckbox';

import './index.scss';

const PaymentMethods = {
	[PaymentMethodType.TRIAL]: {
		icon: (
			<ClayIcon
				className="mr-3"
				color=" #0B5FFF"
				fontSize={16}
				symbol="document-text"
			/>
		),
		subtitle: 'Online payments with PayPal',
		title: i18n.translate('start-trial'),
	},
	[PaymentMethodType.PAY_NOW]: {
		icon: (
			<img
				alt="paypal"
				className="mr-3"
				height={18}
				src={paypal}
				width={16}
			/>
		),
		subtitle: 'Online payments with PayPal',
		title: i18n.translate('pay-with-card'),
	},
	[PaymentMethodType.INVOICE]: {
		icon: (
			<ClayIcon
				className="mr-3"
				color=" #0B5FFF"
				fontSize={16}
				symbol="document-text"
			/>
		),
		subtitle: 'Online payments with PayPal',
		title: i18n.translate('pay-with-invoice'),
	},
};

const OrderSummary = () => {
	const navigate = useNavigate();

	const {
		actions: {previousStep},
		handlePurchase,
		isSingleAccount,
		marketplaceDeliveryProduct,
		product,
		productPurchaseCart,
	} = useProductPurchaseOutletContext();

	const {isFreeApp} = getProductPriceModel(product);

	const {cart, cartItems} = productPurchaseCart;

	const {licenseType, payment: paymentStore} = useSelector(
		productPurchaseStore,
		(state) => state.context
	);

	const summary = productPurchaseCart.cart.summary;
	const billingAddress = paymentStore.billingAddress;
	const currencyCode = Liferay.CommerceContext.currency.currencyCode;

	useEffect(() => {
		if (!isFreeApp && !licenseType) {

			// Force redirect to checkout homepage

			navigate('/');
		}
	}, [isFreeApp, licenseType, navigate]);

	const onClickPayNow = async () => {
		if (licenseType === 'TRIAL') {
			return handlePurchase(ProductPurchaseApp, undefined, {
				isTrialSKU: true,
			});
		}

		await handlePurchase(ProductPurchaseApp, {
			...cart,
			billingAddress: paymentStore.billingAddress,
			cartItems,
			paymentMethod:
				paymentStore.type === PaymentMethodType.PAY_NOW
					? 'paypal-integration'
					: 'money-order',
			shippingAddress: paymentStore.billingAddress,
		});
	};

	const valueFallBack = (value: string) => {
		if (!value) {
			return formatCurrency(0, currencyCode);
		}

		return value;
	};

	return (
		<ProductPurchase.Shell
			className="product-purchase-summary select-payment-step"
			footerProps={{
				backButtonProps: {
					disabled: isSingleAccount && isFreeApp,
					onClick: previousStep,
				},
				continueButtonProps: {
					children: i18n.translate(
						isFreeApp ? 'get-app' : 'purchase-app'
					),
					disabled: !paymentStore.eulaAgreement,
					onClick: () => {
						if (isFreeApp) {
							return handlePurchase(ProductPurchaseApp);
						}

						onClickPayNow();
					},
				},
			}}
			subtitle={
				<small className="text-black-50">
					Please review the order summary below and flag the checkbox
					to complete your purchase
				</small>
			}
			title={i18n.translate('summary')}
		>
			{!isFreeApp && (
				<>
					<Section label={i18n.translate('billing-address')}>
						<div className="align-items-center d-flex p-4 section-card">
							<div>
								<ClayIcon
									className="mr-3"
									color="#0B5FFF"
									fontSize={16}
									symbol="geolocation"
								/>
							</div>
							<div>
								<div className="font-weight-bold section-card-title">
									{paymentStore.billingAddress.name}
								</div>
								<div className="section-card-subtitle text-black-50">
									{`${billingAddress.street1}, ${billingAddress.city}, ${billingAddress.regionISOCode}, ${billingAddress.country}`}
								</div>
							</div>
						</div>
					</Section>

					<Section label={i18n.translate('payment-method')}>
						<div className="align-items-center d-flex p-4 section-card">
							<div>
								{
									PaymentMethods[
										paymentStore.type as keyof typeof PaymentMethods
									]?.icon
								}
							</div>
							<div>
								<div className="font-weight-bold section-card-title">
									{
										PaymentMethods[
											paymentStore.type as keyof typeof PaymentMethods
										].title
									}
								</div>
								<div className="section-card-subtitle text-black-50">
									{
										PaymentMethods[
											paymentStore.type as keyof typeof PaymentMethods
										].subtitle
									}
								</div>
							</div>
						</div>
					</Section>
				</>
			)}

			<Section label="Order Summary">
				<div className="d-flex mx-5">
					<div className="col-1 d-flex justify-content-end m-0 p-0 text-nowrap">
						{i18n.translate('net-price')}:
					</div>
					<span className="font-weight-bold ml-2">
						{valueFallBack(summary?.subtotalFormatted)}
					</span>
				</div>

				<div className="d-flex mx-5">
					<div className="col-1 d-flex justify-content-end m-0 p-0">
						{i18n.translate('vat')}:
					</div>
					<span className="font-weight-bold ml-2">
						{valueFallBack(summary?.taxValueFormatted)}
					</span>
				</div>

				<div className="d-flex mx-5">
					<div className="col-1 d-flex justify-content-end m-0 p-0">
						{i18n.translate('total')}:
					</div>
					<span className="font-weight-bold ml-2">
						{valueFallBack(summary?.totalFormatted)}
					</span>

					{!isFreeApp && (
						<div className="license-tag ml-3 px-2 py-1">
							{marketplaceDeliveryProduct.getLicenseTagText()}
						</div>
					)}
				</div>
			</Section>

			<LicenseTermsCheckbox />
		</ProductPurchase.Shell>
	);
};

export default OrderSummary;
