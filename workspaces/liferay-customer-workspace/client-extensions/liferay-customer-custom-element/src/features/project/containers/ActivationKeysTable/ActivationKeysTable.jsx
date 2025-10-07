/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
import {ButtonWithIcon} from '@clayui/core';
import {useModal} from '@clayui/modal';
import {ClayTooltipProvider} from '@clayui/tooltip';
import {useCallback, useEffect, useMemo, useState} from 'react';
import {useLocation, useOutletContext} from 'react-router-dom';
import i18n from '~/utils/I18n';
import RoundedGroupButtons from '~/components/RoundedGroupButtons';
import ActionTable from '~/components/ActionTable';
import {useAppPropertiesContext} from '~/contexts/AppPropertiesContext';
import {ALERT_DOWNLOAD_TYPE} from '~/features/project/utils/constants/alertDownloadType';
import {getLicenseKeyPermanentStatus} from '../GenerateNewKey/utils/licenseKeyPermanentStatus';
import DownloadAlert from './components/DownloadAlert';
import ActivationKeysTableHeader from './components/Header';
import useFilters from './components/Header/hooks/useFilters';
import ModalKeyDetails from './components/ModalKeyDetails';
import useGetActivationKeysData from './hooks/useGetActivationKeysData';
import usePagination from './hooks/usePagination';
import useStatusCountNavigation from './hooks/useStatusCountNavigation';
import {ACTIVATE_COLUMNS} from './utils/constants';
import {ALERT_ACTIVATION_AGGREGATED_KEYS_DOWNLOAD_TEXT} from './utils/constants/alertAggregateKeysDownloadText';
import {
	EnvironmentTypeColumn,
	ExpirationDateColumn,
	KeyTypeColumn,
	StatusColumn,
} from './utils/constants/columns-definitions';
import {downloadActivationLicenseKey} from './utils/downloadActivationLicenseKey';
import {getActivationKeyDownload} from './utils/getActivationKeyDownload';
import {getTooltipContentRenderer} from './utils/getTooltipContentRenderer';

import './ActivationKeysTable.css';

const messageNewKeyGeneratedAlert = i18n.translate(
	'activation-key-was-generated-successfully'
);

const messageDeactivateKey = i18n.translate(
	'activation-keys-were-deactivated-successfully'
);

const ActivationKeysTable = ({
	hasComplimentaryKey,
	initialFilter,
	isRenewTable,
	oAuthToken,
	productName,
	project,
	setActivationKeysChecked,
	setKeysSelectedCount,
	setRenewKeysFilterChecked,
}) => {
	const {provisioningServerAPI} = useAppPropertiesContext();
	const [isVisibleModal, setIsVisibleModal] = useState(false);
	const [downloadStatus, setDownloadStatus] = useState('');
	const [
		activationKeysFilteredByRenewable,
		setActivationKeysFilteredByRenewable,
	] = useState([]);
	const {state} = useLocation();
	const {setHasSideMenu} = useOutletContext();

	const messageNewKeyGeneratedAlertForComplimentary = i18n.translate(
		state?.isMultipleKeys
			? 'complimentary-keys-were-generated-successfully'
			: 'complimentary-key-was-generated-successfully'
	);

	useEffect(() => {
		setHasSideMenu(true);
	}, [setHasSideMenu]);

	const [newKeyGeneratedAlertStatus, setNewKeyGeneratedAlertStatus] =
		useState(state?.newKeyGeneratedAlert ? 'success' : '');

	const [deactivatedKeyAlertStatus, setDeactivatedKeyAlertStatus] = useState(
		state?.deactivateKeyAlert ? 'success' : ''
	);

	const {
		activationKeysState: [activationKeys, setActivationKeys],
		loading,
		setFilterTerm,
	} = useGetActivationKeysData(project, initialFilter);

	useEffect(() => {
		if (activationKeys) {
			const filteredKeys = activationKeys.filter((activationKey) => {
				const isPermanentLicenseKey = getLicenseKeyPermanentStatus(
					activationKey?.startDate,
					activationKey?.expirationDate
				);

				return !isPermanentLicenseKey;
			});
			setActivationKeysFilteredByRenewable(filteredKeys);
		}
	}, [activationKeys]);

	const {
		navigationGroupButtons,
		statusfilterByTitle: [statusFilter, setStatusFilter],
	} = useStatusCountNavigation(activationKeys);

	const [allActivationKeys, setAllActivationKeys] = useState([]);
	const [hasRenewalSubscription, setHasRenewalSubscription] = useState('');
	const [filters, setFilters] = useFilters(
		setFilterTerm,
		productName,
		initialFilter
	);

	const {activationKeysByStatusPaginated, paginationConfig} = usePagination(
		isRenewTable ? activationKeysFilteredByRenewable : activationKeys,
		statusFilter,
		setAllActivationKeys
	);

	const [currentActivationKey, setCurrentActivationKey] = useState();
	const [activationKeysIdChecked, setActivationKeysIdChecked] = useState([]);

	const {observer, onClose} = useModal({
		onClose: () => setIsVisibleModal(false),
	});

	const activationKeysByStatusPaginatedChecked = useMemo(
		() =>
			activationKeys.filter(({id}) =>
				activationKeysIdChecked.includes(id)
			) || [],
		[activationKeys, activationKeysIdChecked]
	);

	useEffect(() => {
		const renewKeysSelected = () => {
			if (isRenewTable) {
				setActivationKeysChecked(
					activationKeysByStatusPaginatedChecked
				);

				setKeysSelectedCount(activationKeysIdChecked?.length);
			}
		};
		renewKeysSelected();
	}, [
		activationKeysByStatusPaginatedChecked,
		activationKeysIdChecked?.length,
		allActivationKeys,
		isRenewTable,
		setActivationKeysChecked,
		setKeysSelectedCount,
	]);

	useEffect(() => {
		const hasRenewSubscription = allActivationKeys.some(
			(item) =>
				!getLicenseKeyPermanentStatus(
					item?.startDate,
					item?.expirationDate
				)
		);

		setHasRenewalSubscription(hasRenewSubscription);
	}, [allActivationKeys]);

	const handleAlertStatus = useCallback((hasSuccessfullyDownloadedKeys) => {
		setDownloadStatus(
			hasSuccessfullyDownloadedKeys
				? ALERT_DOWNLOAD_TYPE.success
				: ALERT_DOWNLOAD_TYPE.danger
		);
	}, []);

	const getActivationKeysRows = useCallback(
		(activationKey) => ({
			customClickOnRow: () => {
				setCurrentActivationKey(activationKey);
				setIsVisibleModal(true);
			},
			download: (
				<ButtonWithIcon
					aria-label={i18n.translate('download-key')}
					className="text-dark"
    				displayType="unstyled"
					onClick={() =>
						getActivationKeyDownload(
							oAuthToken,
							provisioningServerAPI,
							handleAlertStatus,
							activationKey,
							project.name
						)
					}
					small
					spritemap={Liferay.Icons.spritemap}
					symbol="download"
				/>
			),
			envName: (
				<div title={[activationKey.name, activationKey.description]}>
					<p className="font-weight-bold m-0 text-neutral-10 text-truncate">
						{activationKey.name}
					</p>

					<p className="font-weight-normal m-0 text-neutral-7 text-paragraph-sm text-truncate">
						{activationKey.description}
					</p>
				</div>
			),
			envType: <EnvironmentTypeColumn activationKey={activationKey} />,
			expirationDate: (
				<ExpirationDateColumn activationKey={activationKey} />
			),
			id: activationKey.id,
			keyType: <KeyTypeColumn activationKey={activationKey} />,
			status: <StatusColumn activationKey={activationKey} />,
		}),
		[handleAlertStatus, oAuthToken, provisioningServerAPI, project.name]
	);

	return (
		<>
			{isVisibleModal && (
				<ModalKeyDetails
					currentActivationKey={currentActivationKey}
					downloadActivationLicenseKey={downloadActivationLicenseKey}
					isVisibleModal={isVisibleModal}
					oAuthToken={oAuthToken}
					observer={observer}
					onClose={onClose}
					project={project}
				/>
			)}
			<ClayTooltipProvider
				contentRenderer={({title}) => getTooltipContentRenderer(title)}
				delay={100}
			>
				<div>
					<div className="align-center cp-activation-key-container d-flex justify-content-between mb-2">
						<h3 className="m-0">
							{isRenewTable
								? i18n.sub(
										'renew-x-activation-key',
										productName
									)
								: i18n.translate('activation-keys')}
						</h3>

						{!isRenewTable && (
							<RoundedGroupButtons
								groupButtons={navigationGroupButtons}
								handleOnChange={(value) =>
									setStatusFilter(value)
								}
							/>
						)}
					</div>

					{isRenewTable && (
						<h6 className="text-neutral-6">
							{i18n.translate(
								'select-the-activation-key-you-wish-to-renew'
							)}
						</h6>
					)}

					<div className="mt-4 py-2">
						<ActivationKeysTableHeader
							activationKeysByStatusPaginatedChecked={
								activationKeysByStatusPaginatedChecked
							}
							activationKeysState={[
								isRenewTable
									? activationKeysFilteredByRenewable
									: activationKeys,
								setActivationKeys,
							]}
							filterState={[filters, setFilters]}
							hasRenewalSubscription={hasRenewalSubscription}
							isRenewTable={isRenewTable}
							loading={loading}
							oAuthToken={oAuthToken}
							productName={productName}
							project={project}
							setRenewKeysFilterChecked={
								setRenewKeysFilterChecked
							}
						/>
					</div>

					{!!activationKeysByStatusPaginated.length && (
						<ActionTable
							checkboxConfig={{
								checkboxesChecked: activationKeysIdChecked,
								setCheckboxesChecked:
									setActivationKeysIdChecked,
							}}
							className="border-0 cp-activation-key-table"
							columns={ACTIVATE_COLUMNS}
							hasCheckbox
							hasPagination
							isLoading={loading}
							paginationConfig={paginationConfig}
							rows={activationKeysByStatusPaginated.map(
								(activationKey) =>
									getActivationKeysRows(activationKey)
							)}
						/>
					)}

					{!activationKeysByStatusPaginated.length &&
						(filters.searchTerm || filters.hasValue) && (
							<div className="d-flex justify-content-center py-4">
								{i18n.translate(
									'no-activation-keys-found-with-this-search-criteria'
								)}
							</div>
						)}
				</div>
			</ClayTooltipProvider>

			{!!downloadStatus && (
				<DownloadAlert
					downloadStatus={downloadStatus}
					message={
						ALERT_ACTIVATION_AGGREGATED_KEYS_DOWNLOAD_TEXT[
							downloadStatus
						]
					}
					setDownloadStatus={setDownloadStatus}
				/>
			)}

			{!!newKeyGeneratedAlertStatus && (
				<DownloadAlert
					downloadStatus={newKeyGeneratedAlertStatus}
					message={
						!hasComplimentaryKey
							? messageNewKeyGeneratedAlert
							: messageNewKeyGeneratedAlertForComplimentary
					}
					setDownloadStatus={setNewKeyGeneratedAlertStatus}
				/>
			)}

			{!!deactivatedKeyAlertStatus && (
				<DownloadAlert
					downloadStatus={deactivatedKeyAlertStatus}
					message={messageDeactivateKey}
					setDownloadStatus={setDeactivatedKeyAlertStatus}
				/>
			)}
		</>
	);
};

export default ActivationKeysTable;
