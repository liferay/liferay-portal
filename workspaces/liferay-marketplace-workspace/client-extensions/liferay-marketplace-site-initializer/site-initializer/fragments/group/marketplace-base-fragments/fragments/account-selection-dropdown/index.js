/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

const MIN_ACCOUNTS_FOR_SEARCH = 5;
const PAGE_SIZE = 20;
const SEARCH_DELAY = 500;

let divider;
let dropdownList;
let menu;
let profileAccountImage;
let profileAccountName;
let searchInput;
let trigger;
let accountSelectionDropdown;
let accountList = [];
let lastRenderedAccounts = [];

function initializeElements() {
	accountList = [];
	accountSelectionDropdown = fragmentElement.querySelector(
		'.account-selection-dropdown'
	);
	divider = fragmentElement.querySelector('#divider');
	dropdownList = fragmentElement.querySelector('#dropdownList');
	menu = fragmentElement.querySelector('.dropdown-menu-container');
	profileAccountImage = document.getElementById('profile-account-image');
	profileAccountName = document.getElementById('profile-account-name');
	searchInput = fragmentElement.querySelector('.search-input');
	trigger = fragmentElement.querySelector('.dropdown-trigger');
}

async function setCommerceContextAccount(accountId) {
	try {
		const body = new FormData();

		body.append('accountId', accountId);

		await fetch(
			`/o/commerce-ui/set-current-account?groupId=${Liferay.ThemeDisplay.getScopeGroupId()}&p_auth=${
				Liferay.authToken
			}`,
			{
				body,
				headers: {'x-csrf-token': Liferay.authToken},
				method: 'POST',
			}
		);

		window.location.reload();
	}
	catch (error) {
		console.error('Failed to set account:', error);
	}
}

async function getAccount(accountId) {
	try {
		const response = await Liferay.Util.fetch(
			`/o/headless-admin-user/v1.0/accounts/${accountId}`
		);

		return response.json();
	}
	catch (error) {
		console.error('Failed to get account:', error);

		return null;
	}
}

async function getAccounts(searchParams = new URLSearchParams()) {
	try {
		const response = await Liferay.Util.fetch(
			`/o/headless-admin-user/v1.0/accounts?${searchParams.toString()}`
		);

		return response.json();
	}
	catch (error) {
		console.error('Failed to get accounts:', error);

		return {items: []};
	}
}

function setAccountName(name) {
	if (profileAccountName) {
		profileAccountName.innerText = name;
	}
}

function setAccountImage(logoURL) {
	if (profileAccountImage) {
		profileAccountImage.setAttribute('src', logoURL);
	}
}

function showSearchElements(show) {
	divider.style.display = show ? 'block' : 'none';
	searchInput.style.display = show ? 'block' : 'none';
}

function renderAccounts(accounts, currentAccountId, search) {
	if (!dropdownList) {
		return;
	}

	dropdownList.innerHTML = '';

	showSearchElements(accounts.length >= MIN_ACCOUNTS_FOR_SEARCH || !!search);

	if (!accounts.length) {
		const noResultItem = document.createElement('li');

		noResultItem.setAttribute('class', 'mx-3 no-results');
		noResultItem.textContent = 'No results found';

		dropdownList.appendChild(noResultItem);

		return;
	}

	lastRenderedAccounts = accounts;

	accounts.forEach((account) => {
		const accountImage = document.createElement('img');
		const accountName = document.createTextNode(account.name);
		const profileContainer = document.createElement('div');
		const row = document.createElement('li');

		row.setAttribute('class', 'mx-3 item-list');
		profileContainer.setAttribute(
			'class',
			'd-flex text-nowrap m-2 account-name font-weight-bold w-100'
		);

		if (account.id !== currentAccountId) {
			profileContainer.addEventListener('click', async () => {
				await setCommerceContextAccount(account.id);

				menu.classList.remove('active');
			});
		}

		if (account.id === currentAccountId) {
			row.classList.add('disabled');
		}

		accountImage.setAttribute('class', 'avatar mr-2');
		accountImage.setAttribute('src', account.logoURL);

		profileContainer.appendChild(accountImage);
		profileContainer.appendChild(accountName);
		row.appendChild(profileContainer);
		dropdownList.appendChild(row);
	});
}

async function loadDropdownAccounts() {
	try {
		const response = await getAccounts(
			new URLSearchParams({
				fields: 'id,name,logoURL',
				page: 2,
				pageSize: PAGE_SIZE.toString(),
				sort: 'name:asc',
			})
		);

		accountList = response?.items || [];
		lastRenderedAccounts = [...accountList];

		renderAccounts(accountList, Liferay.CommerceContext.account.accountId);
	}
	catch (error) {
		console.error('Failed to load accounts:', error);
	}
}

async function loadSelectedAccount() {
	try {
		const account = await getAccount(
			Liferay.CommerceContext.account.accountId
		);

		if (account) {
			setAccountImage(account.logoURL);
			setAccountName(account.name);
		}
	}
	catch (error) {
		console.error('Failed to load selected account:', error);
	}
}

async function fetchAccounts(search) {
	try {
		const response = await getAccounts(
			new URLSearchParams({
				fields: 'id,name,logoURL',
				filter: `contains(name, '${search}')`,
				page: 1,
				pageSize: PAGE_SIZE.toString(),
				sort: 'name:asc',
			})
		);

		renderAccounts(
			response.items,
			Liferay.CommerceContext.account.accountId,
			search
		);
	}
	catch (error) {
		console.error('Search failed:', error);
	}
}

function setupEventListeners() {
	trigger?.addEventListener('click', () => {
		menu.classList.toggle('active');
		if (menu.classList.contains('active')) {
			renderAccounts(
				lastRenderedAccounts.length
					? lastRenderedAccounts
					: accountList,
				Liferay.CommerceContext.account.accountId
			);
		}
	});

	let searchTimeout = null;

	searchInput?.addEventListener('input', () => {
		clearTimeout(searchTimeout);

		searchTimeout = setTimeout(
			() => fetchAccounts(searchInput.value.trim()),
			SEARCH_DELAY
		);
	});

	document.addEventListener('click', (event) => {
		if (!fragmentElement.contains(event.target)) {
			searchInput.value = '';

			menu.classList.remove('active');
		}
	});
}

async function main() {
	try {
		setAccountName(Liferay.CommerceContext.account.accountName);

		accountSelectionDropdown?.classList.remove('d-none');

		setupEventListeners();

		await Promise.all([loadDropdownAccounts(), loadSelectedAccount()]);
	}
	catch (error) {
		console.error('Failed to initialize:', error);
	}
}

if (Liferay.ThemeDisplay.isSignedIn()) {
	initializeElements();
	main();
}
