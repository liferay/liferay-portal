/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export default async function CommerceCheckoutStep() {
	const cartId = document.getElementById('payment-order-id').value;
	const commerceCheckoutStepContainer = document.getElementById(
		'_com_liferay_commerce_checkout_web_internal_portlet_CommerceCheckoutPortlet_commerceCheckoutStepContainer'
	);
	const heading = document.createElement('h1');

	commerceCheckoutStepContainer.appendChild(heading);
	heading.classList.add('mb-5', 'text-center');
	heading.innerText = 'Item-Specific Shipping Address';

	const cartItems = await fetch(
		`/o/headless-commerce-delivery-cart/v1.0/carts/${cartId}/items`,
		{
			headers: {
				'Accept': 'application/json',
				'Content-Type': 'application/json',
				'x-csrf-token': Liferay.authToken,
			},
			method: 'GET',
		}
	)
		.then((response) => {
			if (response.ok) {
				return response.json();
			}

			return [];
		})
		.then((response) => {
			return response.items ? response.items : [];
		});

	const shippingAddresses = await fetch(
		`/o/headless-admin-user/v1.0/accounts/${Liferay.CommerceContext.account.accountId}/postal-addresses`,
		{
			headers: {
				'Accept': 'application/json',
				'Content-Type': 'application/json',
				'x-csrf-token': Liferay.authToken,
			},
			method: 'GET',
		}
	)
		.then((response) => {
			if (response.ok) {
				return response.json();
			}

			return [];
		})
		.then((response) => {
			return response.items ? response.items : [];
		});

	cartItems.forEach((cartItem, index) => {
		const cartItemDivider = document.createElement('hr');
		const cartItemRow = document.createElement('div');
		const currentShippingAddress = shippingAddresses.find(
			(address) => address.id === cartItem.shippingAddressId
		);

		cartItemRow.classList.add('row', 'px-5');
		cartItemRow.style.minHeight = '400px';
		cartItemRow.style.minWidth = '300px';
		commerceCheckoutStepContainer.appendChild(cartItemDivider);
		commerceCheckoutStepContainer.appendChild(cartItemRow);
		const cartItemInfoCol = document.createElement('div');

		cartItemInfoCol.classList.add('col-8', 'pt-5');
		cartItemRow.appendChild(cartItemInfoCol);
		const cartItemName = document.createElement('h3');

		cartItemInfoCol.appendChild(cartItemName);
		cartItemName.classList.add('mb-3');
		cartItemName.innerText = `${index + 1}. ${cartItem.name}`;
		const shippingAddressInfoContainer = document.createElement('div');

		cartItemInfoCol.appendChild(shippingAddressInfoContainer);
		shippingAddressInfoContainer.classList.add('d-flex');
		const shippingAddressInfoMessage = document.createElement('p');

		shippingAddressInfoMessage.classList.add('mb-0', 'pt-1');
		shippingAddressInfoMessage.innerText = `Shipping to: `;
		shippingAddressInfoContainer.appendChild(shippingAddressInfoMessage);
		const addressesSelectList = document.createElement('select');

		addressesSelectList.classList.add('ml-3', 'form-control', 'w-50');
		addressesSelectList.setAttribute(
			'name',
			`_com_liferay_commerce_checkout_web_internal_portlet_CommerceCheckoutPortlet_item_${cartItem.id}`
		);
		addressesSelectList.setAttribute('value', shippingAddresses[0].id);
		shippingAddressInfoContainer.appendChild(addressesSelectList);

		shippingAddresses.forEach((shippingAddress) => {
			const shippingAddressOption = document.createElement('option');

			addressesSelectList.appendChild(shippingAddressOption);
			shippingAddressOption.innerText = `${shippingAddress.name}`;
			shippingAddressOption.setAttribute('value', shippingAddress.id);
		});

		if (currentShippingAddress) {
			const shippingAddressSummary = document.createElement('p');

			cartItemInfoCol.appendChild(shippingAddressSummary);
			shippingAddressSummary.innerText = `${currentShippingAddress.addressCountry} - ${currentShippingAddress.streetAddressLine1}`;
		}

		const cartItemImage = document.createElement('div');

		cartItemRow.appendChild(cartItemImage);
		cartItemImage.classList.add('col-4');
		cartItemImage.innerHTML = cartItem.adaptiveMediaImageHTMLTag;
	});
}
