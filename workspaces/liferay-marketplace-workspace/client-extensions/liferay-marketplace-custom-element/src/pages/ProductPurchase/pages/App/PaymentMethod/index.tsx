/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import {useSelector} from '@xstate/store/react';
import {useEffect, useMemo} from 'react';
import {useNavigate} from 'react-router-dom';

import ProductPurchase from '../../../../../components/ProductPurchase';
import useAccountAddresses from '../../../../../hooks/useAccountAddresses';
import i18n from '../../../../../i18n';
import zodSchema from '../../../../../schema/zod';
import marketplaceOAuth2 from '../../../../../services/oauth/Marketplace';
import HeadlessAdminUser from '../../../../../services/rest/HeadlessAdminUser';
import HeadlessCommerceDeliveryCart from '../../../../../services/rest/HeadlessCommerceDeliveryCart';
import {useProductPurchaseOutletContext} from '../../../ProductPurchaseOutlet';
import ProductPurchaseApp from '../../../services/ProductPurchaseApp';
import {cartStore} from '../../../store';
import {productPurchaseStore} from '../../../store/AppPurchaseStore';
import {PaymentMethodType} from '../../../types';
import BillingAddress from './BillingAddress/BillingAddress';
import {PaymentTypeSelector} from './PaymentTypeSelector';
import TaxIdDisplay from './TaxIdDisplay';
import {TrialMethod} from './TrialMethod/TrialMethod';

const PaymentMethodFlows = {
	[PaymentMethodType.TRIAL]: {
		actionMessage: i18n.translate('start-trial'),
	},
	[PaymentMethodType.PAY_NOW]: {
		actionMessage: i18n.translate('continue'),
	},
	[PaymentMethodType.INVOICE]: {
		actionMessage: i18n.translate('continue'),
	},
};

export default function PaymentMethod() {
	const navigate = useNavigate();

	const payment = useSelector(
		productPurchaseStore,
		(state) => state.context.payment
	);

	const isPrimaryButtonActive = useMemo(() => {
		const isAddressValid = zodSchema.billingAddress.safeParse(
			payment.billingAddress
		);

		if (payment.type === PaymentMethodType.TRIAL) {
			return isAddressValid.success;
		}

		if (payment.type === PaymentMethodType.PAY_NOW) {
			return isAddressValid.success;
		}

		if (payment.type === PaymentMethodType.INVOICE) {
			const invoiceValues = Object.values(payment.invoice);

			return !!invoiceValues.length && isAddressValid.success;
		}
	}, [payment]);

	const {type: paymentMethodType} = payment;

	const {licenseType, payment: paymentStore} = useSelector(
		productPurchaseStore,
		(state) => state.context
	);

	const {
		actions: {nextStep, previousStep},
		handlePurchase,
		productPurchaseCart,
		selectedAccount,
	} = useProductPurchaseOutletContext();

	useEffect(() => {
		if (!licenseType) {

			// Force redirect to checkout homepage

			navigate('/');
		}
	}, [licenseType, navigate]);

	const {
		data: addressResponse = {items: []},
		mutate: mutateUserAccoutAddress,
	} = useAccountAddresses(selectedAccount?.id);

	const {actionMessage} =
		PaymentMethodFlows[
			paymentStore.type as keyof typeof PaymentMethodFlows
		];

	const onClickContinue = async () => {
		if (licenseType === 'TRIAL') {
			return handlePurchase(ProductPurchaseApp, undefined, {
				isTrialSKU: true,
			});
		}

		await productPurchaseCart.updateCart(productPurchaseCart.cart.id, {
			billingAddress: payment.billingAddress,
		});

		if (licenseType === 'PAID') {
			await marketplaceOAuth2.taxCalculate(productPurchaseCart.cart.id);
		}

		if (payment.taxId && !selectedAccount.taxId) {
			await HeadlessAdminUser.updateAccount(selectedAccount.id, {
				taxId: payment.taxId,
			});
		}

		cartStore.send({
			cart: await HeadlessCommerceDeliveryCart.getCart(
				productPurchaseCart.cart.id
			),
			type: 'setCart',
		});

		nextStep();
	};

	return (
		<ProductPurchase.Shell
			className="select-payment-step"
			footerProps={{
				backButtonProps: {
					onClick: previousStep,
				},
				continueButtonProps: {
					children: actionMessage,
					disabled: !isPrimaryButtonActive,
					onClick: onClickContinue,
				},
				termsAndConditions: (
					<span className="text-2">
						<ClayIcon className="mr-2" symbol="info-panel-open" />
						{i18n.translate(
							'terms-privacy-returns-or-contact-support-all-costs-are-in-us-dollars'
						)}
					</span>
				),
			}}
			title={i18n.translate('payment-method')}
		>
			<BillingAddress
				addresses={addressResponse.items}
				mutateUserAccoutAddress={mutateUserAccoutAddress}
				setBillingAddress={(billingAddress) =>
					productPurchaseStore.send({
						billingAddress,
						type: 'setBillingAddress',
					})
				}
			/>

			{paymentMethodType === PaymentMethodType.TRIAL ? (
				<TrialMethod />
			) : (
				<>
					<TaxIdDisplay />
					<PaymentTypeSelector />
				</>
			)}
		</ProductPurchase.Shell>
	);
}
