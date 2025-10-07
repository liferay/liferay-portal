/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

const COOKIE_KEY = 'com.liferay.commerce.currency.model.CommerceCurrency#';

function getCookie(name) {
	const cookies = document.cookie.split(';');

	for (const cookie of cookies) {
		const [key, value] = cookie.trim().split('=');

		if (key === name) {
			return value;
		}
	}

	return null;
}

function setCookie(name, value, days = 365) {
	const date = new Date();
	date.setTime(date.getTime() + days * 864e5);

	document.cookie = `${name}=${value}; expires=${date.toUTCString()}; path=/;`;
}

function closeDropdown(dropdownToggle, menuContainer) {
	menuContainer.style.display = 'none';
	dropdownToggle.setAttribute('aria-expanded', 'false');
}

function setupDropdownListeners(dropdownToggle, menuContainer) {
	dropdownToggle.addEventListener('click', (event) => {
		event.stopPropagation();

		const expanded =
			dropdownToggle.getAttribute('aria-expanded') === 'true';

		dropdownToggle.setAttribute('aria-expanded', String(!expanded));
		menuContainer.style.display = expanded ? 'none' : 'block';
	});

	document.addEventListener('click', (event) => {
		if (
			!menuContainer.contains(event.target) &&
			!dropdownToggle.contains(event.target)
		) {
			closeDropdown(dropdownToggle, menuContainer);
		}
	});
}

function currencyChangeModal(newCurrencyCode, closeDropdownFn) {
	Liferay.Util.openModal({
		bodyHTML:
			'<p>Changing the currency will remove your current active order and create a new one using the selected currency.</p>',
		buttons: [
			{
				displayType: 'secondary',
				label: 'Cancel',
				onClick: ({processClose}) => {
					processClose();
					closeDropdownFn();
				},
			},
			{
				displayType: 'primary',
				label: 'Confirm',
				onClick: ({processClose}) => {
					processClose();

					return Liferay.Util.fetch(
						`/o/headless-commerce-delivery-cart/v1.0/carts/${Liferay.CommerceContext?.order?.orderId}`,
						{
							method: 'DELETE',
						}
					)
						.then(() => {
							setCookie(
								`${COOKIE_KEY}${Liferay.CommerceContext?.commerceChannelGroupId}`,
								newCurrencyCode
							);

							window.location.reload();
						})
						.catch((error) => {
							console.error('DELETE order error:', error);

							Liferay.Util.openToast({
								message: 'Error deleting the active order.',
								title: 'Error',
								type: 'danger',
							});
						});
				},
			},
		],
		center: true,
		status: 'info',
		title: 'Change Currency',
	});
}

function renderCurrencyOptions({
	currencies,
	currencyOptionsList,
	currentCode,
	dropdownToggle,
	menuContainer,
}) {
	currencies.forEach((currency) => {
		const currencyItem = document.createElement('a');

		currencyItem.className = 'dropdown-item';
		currencyItem.href = '#';
		currencyItem.textContent = `${currency.symbol} ${currency.code}`;

		if (currency.code === currentCode) {
			currencyItem.classList.add('active');
		}

		currencyItem.addEventListener('click', (event) => {
			event.preventDefault();

			if (currency.code === currentCode) {
				closeDropdown(dropdownToggle, menuContainer);

				return;
			}

			if (Liferay.CommerceContext?.order?.orderId) {
				currencyChangeModal(currency.code, () =>
					closeDropdown(dropdownToggle, menuContainer)
				);
			}
			else {
				setCookie(
					`${COOKIE_KEY}${Liferay.CommerceContext?.commerceChannelGroupId}`,
					currency.code
				);

				window.location.reload();
			}
		});

		const currencyListItem = document.createElement('li');

		currencyListItem.appendChild(currencyItem);
		currencyOptionsList.appendChild(currencyListItem);
	});
}

function main() {
	const currentCurrencyCode = document.getElementById('currentCurrencyCode');
	const currencyOptionsList = document.getElementById('currencyOptionsList');
	const dropdownToggle = document.getElementById('currencyDropdownToggle');
	const menuContainer = dropdownToggle?.nextElementSibling;

	const storedCurrencyCode = getCookie(
		`${COOKIE_KEY}${Liferay.CommerceContext?.commerceChannelGroupId}`
	);

	return Liferay.Util.fetch(
		`/o/headless-commerce-delivery-catalog/v1.0/channels/${Liferay.CommerceContext?.commerceChannelId}/currencies`
	)
		.then((response) => response.json())
		.then(({items = []}) => {
			const currencies = items.filter((currency) => currency.active);

			const currentCode =
				storedCurrencyCode ||
				Liferay.CommerceContext?.currency?.currencyCode;

			const currentCurrency = currencies.find(
				(currency) => currency.code === currentCode
			);

			if (currentCurrency) {
				currentCurrencyCode.innerText = `${currentCurrency.symbol} ${currentCurrency.code}`;
			}

			renderCurrencyOptions({
				currencies,
				currencyOptionsList,
				currentCode,
				dropdownToggle,
				menuContainer,
			});
		})
		.finally(() => {
			setupDropdownListeners(dropdownToggle, menuContainer);
		});
}

if (Liferay.ThemeDisplay.isSignedIn()) {
	main();
}
