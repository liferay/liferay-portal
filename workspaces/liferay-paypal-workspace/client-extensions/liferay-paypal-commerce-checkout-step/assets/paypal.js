/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export async function onPaypalLoaded() {
	const payPalOAuth = Liferay.OAuth2Client.FromUserAgentApplication(
		'liferay-paypal-commerce-payment-integration-oaua'
	);

	const orderId = document.getElementById('payment-order-id').value;

	const paypal = window.paypal;

	const payPalButton = paypal.Buttons({
		async createOrder(data) {
			try {
				const cartPaymentResource = await payPalOAuth.fetch('/render', {
					body: JSON.stringify({
						fundingSource: data.paymentSource,
						orderId,
						redirect: false,
					}),
					method: 'POST',
				});

				const cartPaymentURLJson = await cartPaymentResource.json();
				const cartPaymentURL = cartPaymentURLJson.url;
				const resource = await fetch(cartPaymentURL);

				if (resource.ok) {
					const orderData = await payPalOAuth.fetch(
						'/set-up-payment/get-paypal-order/' + orderId
					);

					if (orderData) {
						const orderDataJson = await orderData.json();

						return orderDataJson.id;
					}
				}
			}
			catch (error) {
				resultMessage(
					`Could not initiate PayPal Checkout...<br><br>${error}`
				);
			}
		},
		async onApprove(data) {
			try {
				const cartPaymentResource = await payPalOAuth.fetch('/render', {
					body: JSON.stringify({
						orderId,
						transactionCode: data.orderID,
					}),
					method: 'POST',
				});

				const cartPaymentResourceJSON =
					await cartPaymentResource.json();
				const cartPaymentURL = cartPaymentResourceJSON.url;
				const response = await fetch(cartPaymentURL);

				if (response.ok) {
					window.location.href = response.url;
				}
			}
			catch (error) {
				resultMessage(
					`Sorry, your transaction could not be processed...<br><br>${error}`
				);
			}
		},
		async onCancel(data) {
			const cartPaymentResource = await payPalOAuth.fetch('/render', {
				body: JSON.stringify({
					cancel: true,
					orderId,
					redirect: false,
					transactionCode: data.orderID,
				}),
				method: 'POST',
			});

			const cartPaymentResourceJSON = await cartPaymentResource.json();
			const cartPaymentURL = cartPaymentResourceJSON.url;
			await fetch(cartPaymentURL);
		},
		async onError(error) {
			resultMessage(
				`Could not initiate PayPal Checkout...<br><br>${error}`
			);
		},
	});

	if (payPalButton.isEligible()) {
		payPalButton.render('#paypal-button-container');
	}
}

function resultMessage(message) {
	const container = document.querySelector('#result-message');
	container.innerHTML = message;
}
