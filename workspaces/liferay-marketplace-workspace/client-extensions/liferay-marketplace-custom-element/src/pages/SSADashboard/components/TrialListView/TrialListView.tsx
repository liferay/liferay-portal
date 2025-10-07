/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useMemo} from 'react';
import {Link} from 'react-router-dom';

import ListView, {ListViewProps} from '../../../../components/ListView';
import {ManagementToolbarProps} from '../../../../components/ListView/components/ManagementToolbar';
import {useMarketplaceContext} from '../../../../context/MarketplaceContext';
import SearchBuilder from '../../../../core/SearchBuilder';
import {
	OrderCustomFields,
	OrderStatus,
	OrderTypes,
} from '../../../../enums/Order';
import i18n from '../../../../i18n';
import {Liferay} from '../../../../liferay/liferay';
import {Action} from '../../../../utils/constants';
import {formatDate, formatDateTime} from '../../../../utils/date';
import {safeJSONParse} from '../../../../utils/util';
import {useSSADashboardOutlet} from '../../SSADashboardOutlet';
import {EXTEND_TRIAL_STATUS_LABEL} from '../../constants';
import CreateTrialModalForm from '../../pages/CreateTrialModalform';
import ExtensionStatus from '../ExtensionStatus/ExtensionStatus';
import TrialStatus from '../TrialStatus/TrialStatus';

type TrialsListViewProps = {
	actions: Action[];
	authorOnlyTrials?: boolean;
	createTrialFormModal: any;
	isSortable?: boolean;
	listViewProps?: Partial<ListViewProps<PlacedOrder>>;
	managementToolbarProps?: {
		visible?: boolean;
	} & Omit<
		ManagementToolbarProps,
		| 'actions'
		| 'onSelectAllRows'
		| 'rowSelectable'
		| 'tableProps'
		| 'totalItems'
	>;
};

// Refresh the table every 60 seconds

const refreshInterval = 60 * 1000;

const getResourceURL = (accountId: number) =>
	`/o/headless-commerce-delivery-order/v1.0/channels/${Liferay.CommerceContext.commerceChannelId}/accounts/${accountId}/placed-orders?${new URLSearchParams(
		{
			nestedFields: 'placedOrderItems',
			sort: 'createDate:desc',
		}
	)}`;

export default function TrialListView({
	actions,
	authorOnlyTrials,
	createTrialFormModal,
	listViewProps,
	managementToolbarProps,
}: TrialsListViewProps) {
	const {ssaAccount, ssaTrialExtend} = useSSADashboardOutlet();
	const {myUserAccount} = useMarketplaceContext();

	const author = myUserAccount?.name;

	const searchBuilder = useMemo(() => {
		const searchBuilder = new SearchBuilder().eq(
			'orderTypeExternalReferenceCode',
			OrderTypes.SSA_SAAS
		);

		if (authorOnlyTrials) {
			searchBuilder.and().eq('author', author);
		}

		return searchBuilder;
	}, [author, authorOnlyTrials]);

	return (
		<>
			<ListView<PlacedOrder>
				defaultFilters={{filter: searchBuilder.build()}}
				emptyStateProps={{title: i18n.translate('no-trials-yet')}}
				id="ssa-trials"
				managementToolbarProps={{
					filterSchema: 'administratorSSATrials',
					...managementToolbarProps,
				}}
				refreshInterval={refreshInterval}
				resource={getResourceURL(ssaAccount.id)}
				tableProps={{
					actions,
					columns: [
						{
							id: 'placedOrderItems',
							name: 'Project ID',
							render: (_, {customFields, id}) => (
								<Link
									className="font-weight-semi-bold ml-2"
									to={`/details/${id}`}
								>
									{customFields &&
										safeJSONParse(
											customFields[
												OrderCustomFields.TRIAL_SETTINGS
											],
											{projectId: id}
										).projectId}
								</Link>
							),
						},
						{
							id: 'author',
							name: 'Created By',
							render: (author, {createDate}) => (
								<div className="d-flex flex-column">
									<span className="dashboard-table-row-text">
										{author}
									</span>

									<span className="dashboard-table-row-purchased-date">
										{formatDate(createDate)}
									</span>
								</div>
							),
							sortable: true,
						},
						{
							id: 'customFields',
							name: 'Solution Type',
							render: (customFields) =>
								safeJSONParse(
									customFields[
										OrderCustomFields.TRIAL_SETTINGS
									],
									{siteInitializerKey: 'Blank Site'}
								).siteInitializerKey,
						},
						{
							id: 'createDate',
							name: 'End Date',
							render: (_, {customFields}) =>
								formatDateTime(
									customFields[
										OrderCustomFields.TRIAL_END_DATE
									],
									'DNE'
								),
							sortable: true,
						},
						{
							id: 'orderStatusInfo',
							name: 'Trial Status',
							render: (orderStatusInfo) => (
								<TrialStatus
									trialStatus={orderStatusInfo?.label}
								/>
							),
						},
						{
							id: 'id',
							name: 'Extension Status',
							render: (orderId, placedOrder) => {
								const ssaTrialsExtendRequests =
									ssaTrialExtend.items;

								const extendRequests =
									ssaTrialsExtendRequests?.filter(
										(extend: TrialExtend) => {
											return (
												extend.r_orderToTrialExtensionRequest_commerceOrderId ===
												Number(orderId)
											);
										}
									) as TrialExtend[];

								if (
									!extendRequests ||
									extendRequests?.length === 0
								) {
									return (
										<ExtensionStatus extensionStatus="not-requested" />
									);
								}

								return (
									<ExtensionStatus
										extensionStatus={
											placedOrder.orderStatusInfo
												.label === OrderStatus.COMPLETED
												? 'extension-expired'
												: (extendRequests[0]?.dueStatus
														.key as keyof typeof EXTEND_TRIAL_STATUS_LABEL)
										}
									/>
								);
							},
						},
					],
				}}
				{...listViewProps}
			>
				{(_, {mutate}) => (
					<CreateTrialModalForm
						modal={createTrialFormModal}
						mutate={mutate}
					/>
				)}
			</ListView>
		</>
	);
}
