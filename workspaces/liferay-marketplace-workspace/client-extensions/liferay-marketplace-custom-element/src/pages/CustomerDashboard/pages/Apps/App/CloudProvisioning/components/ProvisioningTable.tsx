/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import ClayDropDown from '@clayui/drop-down';
import {useOutletContext} from 'react-router-dom';

import Loading from '../../../../../../../components/Loading';
import Table from '../../../../../../../components/Table/Table';
import i18n from '../../../../../../../i18n';
import useProvisioningActions from '../hooks/useProvisioningActions';
import useProvisioningData from '../hooks/useProvisioningData';
import {InstallStatus} from '../types';
import InstallAlertModal from './InstallAlertModal';
import InstallationStatus from './InstallStatus';
import UninstallModal from './UninstallModal';

type ProvisioningTableProps = ReturnType<typeof useProvisioningData>;

const ProvisioningTable: React.FC<ProvisioningTableProps> = ({
	mutateOrder,
	order,
	provisioningTableData,
	resourceRequirements,
}) => {
	const {selectedAccount} = useOutletContext<{selectedAccount: Account}>();
	const {
		actions,
		installAlertModal,
		loading,
		onOpenDetailsModal,
		selectedProvisioningRow,
		uninstall,
		uninstallModal,
	} = useProvisioningActions({
		mutateOrder,
		order,
		resourceRequirements,
		selectedAccount,
	});

	return (
		<>
			<Table
				className="mt-4"
				columns={[
					{
						key: 'type',
						render: (type, provisioningRow) => (
							<>
								<div className="dashboard-table-row-type font-weight-bold">
									{type}
								</div>

								<div className="dashboard-table-row-type">
									{provisioningRow.host}
								</div>
							</>
						),
						title: (
							<>
								<div className="text-dark">
									{i18n.translate('type')}
								</div>

								<div className="text-black-50">
									{i18n.translate('host-name')}
								</div>
							</>
						),
					},
					{
						key: 'startDate',
						render: (startDate, provisioningRow) => (
							<>
								<div className="dashboard-table-row-type">
									{startDate}
								</div>

								<div className="dashboard-table-row-type">
									{provisioningRow.expirationDate}
								</div>
							</>
						),
						title: (
							<>
								<div className="text-dark">
									{i18n.translate('start-date')}
								</div>

								<div className="text-dark">
									{i18n.translate('exp-date')}
								</div>
							</>
						),
					},
					{
						key: 'status',
						render: (status: string, provisioningRow) => (
							<div className="align-items-center d-flex">
								<InstallationStatus status={status}>
									{status}
								</InstallationStatus>

								{provisioningRow.status ===
									InstallStatus.IN_PROGRESS && (
									<Loading
										displayType="primary"
										shape="circle"
										size="sm"
									/>
								)}
							</div>
						),
						title: (
							<div className="text-dark">
								{i18n.translate('status')}
							</div>
						),
					},
					{
						key: 'project',
						render: (project, provisioningRow) => {
							const environment = provisioningRow.environment;

							return (
								<>
									<div className="dashboard-table-row-type font-weight-bold">
										{project || 'Not Installed'}
									</div>

									<div className="dashboard-table-row-type">
										{environment || 'Not Installed'}
									</div>
								</>
							);
						},
						title: (
							<>
								<div className="text-dark">
									{i18n.translate('project')}
								</div>

								<div className="text-black-50">
									{i18n.translate('environment')}
								</div>
							</>
						),
					},
					{
						key: 'dropdown',
						render: (_, provisioningRow) => (
							<div
								className="d-flex justify-content-end"
								onClick={(event) => event.stopPropagation()}
							>
								<ClayDropDown
									trigger={
										<ClayButtonWithIcon
											aria-label="Kebab Button"
											displayType={null}
											symbol="ellipsis-v"
											title="Kebab Button"
										/>
									}
								>
									<ClayDropDown.ItemList>
										{actions
											.filter((action) =>
												action.show(provisioningRow)
											)
											.map((action, index) => (
												<ClayDropDown.Item
													disabled={false}
													key={index}
													onClick={() =>
														action.action(
															provisioningRow
														)
													}
												>
													{action?.title}
												</ClayDropDown.Item>
											))}
									</ClayDropDown.ItemList>
								</ClayDropDown>
							</div>
						),
					},
				]}
				onClickRow={(row) => onOpenDetailsModal(row)}
				rows={provisioningTableData}
			/>
			{selectedProvisioningRow && (
				<InstallAlertModal modal={installAlertModal} />
			)}
			{selectedProvisioningRow && (
				<UninstallModal
					loading={loading}
					modal={uninstallModal}
					provisioningRow={selectedProvisioningRow}
					uninstall={uninstall}
				/>
			)}
		</>
	);
};

export default ProvisioningTable;
