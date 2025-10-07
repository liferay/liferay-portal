/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

const salesCardConfigurations = {
	nonTrainer: {
		class: 'sales-resources-card-icon',
		description:
			'Find the latest presentation decks, battle cards, reports, and other essential resources to effectively position and sell Liferay solutions.',
		goToText: 'Go to the Sales Enablement Hub',
		href: '/web/sales-enablement/sales-resources',
		title: 'Sales Resources',
	},
	trainer: {
		class: 'sales-enablement-hub-card-icon',
		description:
			'Access exclusive assets, tools, and materials designed to help our partners and staff succeed in their roles, from sales to certified training.',
		goToText: 'Go to the Enablement Hub',
		href: '/web/sales-enablement/home',
		title: 'Enablement Hub',
	},
};

const salesClasses = {
	salesHubCard: 'sales-hub-card',
	salesHubCardLink: 'sales-hub-card-link',
	salesPageCard: 'sales-page-card',
	salesPageCardCTA: 'sales-page-card-cta',
	salesPageCardDescription: 'sales-page-card-description',
	salesPageCardTitle: 'sales-page-card-title',
};

const salesElements = Object.fromEntries(
	Object.entries(salesClasses).map(([key, className]) => [
		key,
		document.querySelector(`.${className}`),
	])
);

const getUserAccount = async () => {
	const response = await Liferay.Util.fetch(
		`/o/headless-admin-user/v1.0/user-accounts/${Liferay.ThemeDisplay.getUserId()}`
	);

	return response.json();
};

const hasUserGroup = (userAccount, targetGroups = []) => {
	if (!Array.isArray(userAccount?.userGroupBriefs)) {
		return false;
	}

	return userAccount.userGroupBriefs.some((group) =>
		targetGroups.includes(group.name)
	);
};

const renderCardByRole = (isTrainer) => {
	const {salesPageCard} = salesElements;

	salesPageCard.classList.remove(
		salesCardConfigurations.nonTrainer.class,
		salesCardConfigurations.trainer.class
	);

	const config = isTrainer
		? salesCardConfigurations.trainer
		: salesCardConfigurations.nonTrainer;

	salesPageCard.classList.add(config.class);

	setCardInfo(config);
};

const setCardInfo = ({description, goToText, href, title}) => {
	const {
		salesHubCardLink,
		salesPageCardCTA,
		salesPageCardDescription,
		salesPageCardTitle,
	} = salesElements;

	salesHubCardLink.href = href;
	salesPageCardDescription.textContent = description;
	salesPageCardCTA.textContent = goToText;
	salesPageCardTitle.textContent = title;
};

document.addEventListener('DOMContentLoaded', async () => {
	const userAccount = await getUserAccount();

	if (hasUserGroup(userAccount, ['Employees', 'Partners'])) {
		salesElements.salesHubCard.classList.remove('hide');

		renderCardByRole(
			userAccount.roleBriefs?.some((role) =>
				[
					'TRAINERS-LOUNGE-CONTENT-ADMIN',
					'TRAINERS-LOUNGE-USER',
				].includes(role.externalReferenceCode)
			)
		);
	}
});
