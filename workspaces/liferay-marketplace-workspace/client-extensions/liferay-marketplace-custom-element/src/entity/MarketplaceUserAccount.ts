/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {AccountRoleType} from '../enums/Account';
import {Liferay} from '../liferay/liferay';

export class MarketplaceUserAccount {
	constructor(protected userAccount: UserAccount) {}

	get accountBriefs() {
		return this.userAccount?.accountBriefs ?? [];
	}

	get accountName() {
		return this.userAccount.name;
	}

	get accountType() {
		return this.userAccount.type;
	}

	get isAdmin() {
		return this.hasRegularRole(AccountRoleType.ADMINISTRATOR);
	}

	get isSolutionPublisher() {
		return this.hasAccountRole(AccountRoleType.SOLUTION_PUBLISHER);
	}

	get isSSAAdmin() {
		return this.hasAccountRole(AccountRoleType.SSA_ADMIN) || this.isAdmin;
	}

	get isSSAUser() {
		return this.hasAccountRole(AccountRoleType.SSA);
	}

	private hasAccountRole(roleName: AccountRoleType) {
		return this.accountBriefs.some(
			(accountBrief) =>
				accountBrief.id ===
					Liferay.CommerceContext.account?.accountId &&
				accountBrief.roleBriefs.some(
					(roleBrief) => roleBrief.name === roleName
				)
		);
	}

	private hasRegularRole(roleName: AccountRoleType) {
		return this.userAccount?.roleBriefs.some(
			(role) => role?.name === roleName
		);
	}
}
