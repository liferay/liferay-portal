/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import useSWR from 'swr';

import SearchBuilder from '../../core/SearchBuilder';
import HeadlessTrialExtensionRequest from '../../services/rest/HeadlessTrialExtensionRequest';

const useSSATrialsExtend = (account: Account) =>
	useSWR(account?.id ? '/o/c/trialextensionrequests' : null, () =>
		HeadlessTrialExtensionRequest.getTrialExtensionRequest(
			new URLSearchParams({
				filter: SearchBuilder.eq(
					'r_accountToTrialExtensionRequest_accountEntryId',
					account.id
				),
				page: '1',
				pageSize: '-1',
				sort: 'dateCreated:desc',
			})
		)
	);

export {useSSATrialsExtend};
