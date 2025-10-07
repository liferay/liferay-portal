/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayModal from '@clayui/modal';
import {memo} from 'react';

import {useAppPropertiesContext} from '~/contexts/AppPropertiesContext';
import i18n from '~/utils/I18n';

import {
	Button,
	Table,
} from '~/components';
import {useGetAccountSubscriptionUsage} from '~/services/liferay/graphql/account-subscription-usage';
import UsageChart from './components/UsageChart';
import useOrderItems from './hooks/useOrderItems';
import getColumns from './utils/getColumns';
import getRows from './utils/getRows';

const accountSubscriptionGroupNames = ['Liferay Self-Hosted', 'Portal'];

const AccountSubscriptionModal = ({
	IsPortalOrDXP,
	accountKey,
	accountSubscriptionGroup,
	accountSubscriptionProductKey,
	externalReferenceCode,
	observer,
	onClose,
	title,
}) => {
	const {
		activePage,
		data,
		setActivePage,
		loading,
		pageSize: itemsPerPage,
 	} = useOrderItems(externalReferenceCode);

	const {articleWhatIsMyInstanceSizingValueURL} = useAppPropertiesContext();

	const {
		data: accountSubscriptionUsageData,
		loading: accountSubscriptionUsageLoading,
	} = useGetAccountSubscriptionUsage(
		accountKey,
		accountSubscriptionProductKey,
		IsPortalOrDXP
	);

	const totalCount = data?.orderItems.totalCount;

	const accountSubscriptionTerms = data?.orderItems?.items ?? [];

	const accountSubscriptionTermsSort = [...accountSubscriptionTerms].sort(
		(a, b) =>
			new Date(b.options?.startDate) - new Date(a.options?.startDate)
	);

	return (
		<ClayModal center observer={observer} size="lg">
			<div className="pt-4 px-4">
				<div className="d-flex justify-content-between mb-4">
					<div className="flex-row mb-1">
						<div className="h6 text-brand-primary">
							{i18n.translate('subscription-terms').toUpperCase()}
						</div>

						<h2 className="text-neutral-10">{title}</h2>
					</div>

					<Button
						appendIcon="times"
						className="align-self-start"
						displayType="unstyled"
						onClick={onClose}
					/>
				</div>

				<h5 className="mb-4">
					{i18n.translate('active-subscriptions')}
				</h5>

				{accountSubscriptionGroupNames.includes(
					accountSubscriptionGroup?.name
				) && (
					<UsageChart
						data={
							accountSubscriptionUsageData?.getAccountSubscriptionUsage
						}
						loading={accountSubscriptionUsageLoading}
					/>
				)}

				<Table
					columns={getColumns(
						title,
						articleWhatIsMyInstanceSizingValueURL
					)}
					hasPagination
					isLoading={loading}
					paginationConfig={{
						activePage,
						itemsPerPage,
						setActivePage,
						totalCount,
					}}
					rows={getRows(accountSubscriptionTermsSort)}
					tableVerticalAlignment="middle"
				/>
			</div>
		</ClayModal>
	);
};

export default memo(AccountSubscriptionModal);
