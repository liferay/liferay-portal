/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {createStore} from '@xstate/store';

import {ConsoleUserProject} from '../../../services/oauth/types';
import {PaymentMethodType} from '../types';

export type LicenseType = 'TRIAL' | 'PAID';

type Invoice = {
	email: string;
	purchaseOrderNumber: string;
};

export const productPurchaseStore = createStore({
	context: {
		account: {
			taxId: '',
		},
		licenseType: null as unknown as LicenseType,
		payment: {
			billingAddress: {
				city: '',
				country: '',
				countryISOCode: '',
				name: '',
				phoneNumber: '',
				regionISOCode: '',
				street1: '',
				street2: '',
				zip: '',
			} as BillingAddress,
			eulaAgreement: false,
			invoice: {
				email: '',
				purchaseOrderNumber: '',
			} as Invoice,
			taxId: '',
			type: PaymentMethodType.PAY_NOW,
		},
		project: null as unknown as ConsoleUserProject,
	},
	on: {
		setAccountTaxId: {
			payment: (context, event: {taxId: string}) => ({
				...context.payment,
				taxId: event.taxId,
			}),
		},
		setBillingAddress: {
			payment: (
				context,
				event: {billingAddress: Partial<BillingAddress>}
			) => ({
				...context.payment,
				billingAddress: event.billingAddress as BillingAddress,
			}),
		},

		setInvoice: {
			payment: (context, event: {invoice: Invoice}) => ({
				...context.payment,
				invoice: event.invoice,
			}),
		},

		setLicenseType: {
			licenseType: (context, event: {licenseType: LicenseType}) => {
				if (event.licenseType === 'PAID') {
					context.payment.type = PaymentMethodType.PAY_NOW;
				}

				return event.licenseType;
			},
		},

		setPaymentMethodType: {
			payment: (
				context,
				event: {paymentMethodType: PaymentMethodType}
			) => ({
				...context.payment,
				type: event.paymentMethodType,
			}),
		},

		setProject: {
			project: (_, event: {project: ConsoleUserProject}) => event.project,
		},

		toggleEulaAgreement: {
			payment: (context) => {
				context.payment.eulaAgreement = !context.payment.eulaAgreement;

				return context.payment;
			},
		},
	},
});
