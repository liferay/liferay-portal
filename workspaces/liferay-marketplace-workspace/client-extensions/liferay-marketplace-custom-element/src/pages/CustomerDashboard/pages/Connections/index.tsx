/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import EmptyState from '@clayui/empty-state';
import ClayIcon from '@clayui/icon';
import {format} from 'date-fns';
import useSWR from 'swr';

import Loading from '../../../../components/Loading';
import Table from '../../../../components/Table/Table';
import SearchBuilder from '../../../../core/SearchBuilder';
import {useCustomerDashboardOutletContext} from '../../CustomerDashboardOutlet';

const ConnectionsTable = () => {
	const {selectedAccount} = useCustomerDashboardOutletContext();
	const {
		data: response,
		isLoading,
		isValidating,
		mutate,
	} = useSWR<APIResponse<{connectionSource: string; creator: Creator}>>(
		`/o/c/oauth2dxpauthorizations?filter=${SearchBuilder.eq('r_accountToOAuth2DxpAuthorization_accountEntryId', selectedAccount.id)}&sort=dateCreated:desc`
	);

	const items = response?.items || [];

	if (isLoading) {
		return <Loading className="my-6" shape="circle" size="md" />;
	}

	if (!items.length) {
		return (
			<EmptyState
				className="dxp-connections-table-empty-state"
				description=""
				title="No results found"
			>
				<div className="dxp-connections-table-no-connection">
					<p>
						No connection was found, to learn how to create the connection please check this

						<a
							className="ml-1"
							href="https://learn.liferay.com/w/dxp/liferay-development/marketplace/connecting-liferay-dxp-to-marketplace"
						>
							help page
						</a>
					</p>
				</div>
				<ClayButton
					disabled={isValidating}
					displayType="secondary"
					onClick={() => mutate((data) => data, {revalidate: true})}
				>
					Refresh
				</ClayButton>
			</EmptyState>
		);
	}

	return (
		<Table
			className="table-borderless"
			columns={[
				{
					key: 'creator',
					render: (creator) => creator.name,
					title: 'User Name',
				},
				{
					key: 'connectionSource',
					title: 'Source',
				},
				{
					key: 'dateCreated',
					render: (dateCreated) =>
						format(
							new Date(dateCreated),
							"dd MMM, yyyy 'at' h:mm a"
						),
					title: 'Connection Date',
				},
			]}
			rows={items}
		/>
	);
};

const Connections = () => {
	return (
		<div className="border dxp-connections-table">
			<div className="align-items-center d-flex justify-content-between">
				<h3 className="lh-1 mb-0">Connections</h3>
				<span
					className="align-items-center bg-light d-flex dxp-connections-table-icon justify-content-center rounded-pill"
					style={{height: '40px', width: '40px'}}
				>
					<ClayIcon symbol="device-check" />
				</span>
			</div>

			<ConnectionsTable />
		</div>
	);
};

export default Connections;
