/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ApiHelpers} from '../../../../helpers/ApiHelpers';
import {userData} from '../../../../utils/performLogin';

export default async function addSiteUser({
	apiHelpers,
	roleName = 'Site Owner',
	siteId,
	userAccount,
}: {
	apiHelpers: ApiHelpers;
	roleName?: string;
	siteId: string;
	userAccount?: TUserAccount;
}) {
	const user =
		await apiHelpers.headlessAdminUser.postUserAccount(userAccount);

	userData[user.alternateName] = {
		name: user.givenName,
		password: 'test',
		surname: user.familyName,
	};

	await apiHelpers.jsonWebServicesUser.agreeToTermsOfUse(user.id);

	await apiHelpers.jsonWebServicesUser.answerReminderQuery(user.id);

	const company =
		await apiHelpers.jsonWebServicesCompany.getCompanyByWebId(
			'liferay.com'
		);

	const role = await apiHelpers.jsonWebServicesRole.getRole(
		String(company.companyId),
		roleName
	);

	await apiHelpers.headlessAdminUser.assignUserToSite(
		role.roleId,
		siteId,
		user.id
	);

	return user;
}
