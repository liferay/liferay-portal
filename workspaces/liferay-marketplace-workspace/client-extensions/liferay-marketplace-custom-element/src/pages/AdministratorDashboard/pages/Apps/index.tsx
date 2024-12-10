/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import useSWR from 'swr';

import Page from '../../../../components/Page';
import i18n from '../../../../i18n';
import HeadlessCommerceAdminCatalogImpl from '../../../../services/rest/HeadlessCommerceAdminCatalog';
import AppAdministratorTable from './AppAdministratorTable';

const AppAdministrator = () => {
	const {
		data: apps,
		error,
		isLoading,
	} = useSWR<APIResponse<PublisherRequestInfo>>(
		'administrator-dashboard/apps',
		() =>
			HeadlessCommerceAdminCatalogImpl.getProducts(
				new URLSearchParams({
					'productSpecifications.pageSize': '-1',
					'nestedFields': 'productSpecifications',
					'sort': 'createDate:desc',
				})
			)
	);

	return (
		<Page
			description={i18n.translate('list-with-latest-published-apps')}
			pageRendererProps={{error, isLoading}}
			title={i18n.translate('recent-published-apps')}
		>
			<AppAdministratorTable items={apps?.items || []} />
		</Page>
	);
};

export default AppAdministrator;
