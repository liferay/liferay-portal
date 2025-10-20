/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

interface IAccountSelectorButton {
	account?: commerceTypes.TAccount;
	currentAccountPostURL: string;
	hasManageAccountsPermission?: boolean | false;
	order?: commerceTypes.TOrder;
}

interface IAccountSelectorButtonConfiguration {
	configuration: {
		label: string;
		showAccountImage: boolean;
		showAccountInfo: boolean;
		showButtonIcon: boolean;
		showOrderInfo: boolean;
	};
}

interface ICreateAccountButton {
	currentAccountPostURL: string;
	hasAddAccountsPermission: boolean;
}

interface ICreateAccountButtonConfiguration {
	configuration: {
		label: string;
	};
}

interface ICreateOrderButton {
	addCommerceOrderURL: string;
	commerceChannelId: number;
	currencyCode: string;
	currentCommerceAccountId: number;
	hasAddCommerceOrderPermission: boolean;
}

interface ICreateOrderButtonConfiguration {
	configuration: {
		label: string;
	};
}

type TAccountSelectorButtonProps = IAccountSelectorButton &
	IAccountSelectorButtonConfiguration;

type TCreateAccountButtonProps = ICreateAccountButton &
	ICreateAccountButtonConfiguration;

type TCreateOrderButtonProps = ICreateOrderButton &
	ICreateOrderButtonConfiguration;
