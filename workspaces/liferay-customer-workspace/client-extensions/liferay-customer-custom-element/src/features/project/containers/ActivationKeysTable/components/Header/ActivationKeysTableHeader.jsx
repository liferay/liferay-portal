/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import {useCallback, useEffect, useMemo, useState} from 'react';
import {useAppPropertiesContext} from '~/contexts/AppPropertiesContext';
import {useGetMyUserAccount} from '~/services/liferay/graphql/user-accounts';
import i18n from '~/utils/I18n';
import {ROLE_TYPES} from '~/utils/constants';
import {ALERT_DOWNLOAD_TYPE} from '~/features/project/utils/constants/alertDownloadType';
import {ALERT_ACTIVATION_AGGREGATED_KEYS_DOWNLOAD_TEXT} from '../../utils/constants/alertAggregateKeysDownloadText';
import {ALERT_ACTIVATION_MULTIPLE_KEYS_DOWNLOAD_TEXT} from '../../utils/constants/alertMultipleKeysDownloadText';
import {DOWNLOADABLE_LICENSE_KEYS} from '../../utils/constants/downlodableLicenseKeys';
import {hasAdminUserAccount} from '../../utils/hasAdminUserAccount';
import {isBulkRenewAvailable} from '../../utils/isBulkRenewAvailable';
import ActionButton from '../ActionButton';
import BadgeFilter from '../BadgeFilter';
import DeactivateButton from '../DeactivateButton';
import DownloadAlert from '../DownloadAlert';
import Filter from '../Filter';
import RenewButton from '../RenewButton';
import useGetAccountUserAccount from './hooks/useGetAccountUserAccount';

import './ActivationKeysTableHeader.css';

const ActivationKeysTableHeader = ({
	activationKeysByStatusPaginatedChecked,
	activationKeysState,
	filterState: [filters, setFilters],
	hasRenewalSubscription,
	isRenewTable,
	loading,
	oAuthToken,
	productName,
	project,
	setRenewKeysFilterChecked,
}) => {
	const [activationKeys, setActivationKeys] = activationKeysState;

	const {
		userAccountsState: [userAccounts],
	} = useGetAccountUserAccount(project);

	const {data: myAccount} = useGetMyUserAccount();

	const isAdminUserAccount = hasAdminUserAccount(myAccount);

	const isAdminOrPartnerManager = useMemo(() => {
		const currentUser = userAccounts?.find(
			({id}) => id === Number(Liferay.ThemeDisplay.getUserId())
		);

		if (currentUser) {
			const hasAdminRoles = currentUser?.roles?.some(
				(role) =>
					role === ROLE_TYPES.admin.key ||
					role === ROLE_TYPES.partnerManager.key
			);

			return hasAdminRoles;
		}
	}, [userAccounts]);

	const {featureFlags} = useAppPropertiesContext();

	const [status, setStatus] = useState({
		deactivate: '',
		downloadAggregated: '',
		downloadMultiple: '',
	});

	const filterCheckedActivationKeys = useMemo(
		() =>
			activationKeysByStatusPaginatedChecked.reduce(
				(
					filterCheckedActivationKeysAccumulator,
					activationKeyChecked,
					index
				) =>
					`${filterCheckedActivationKeysAccumulator}${
						index > 0 ? '&' : ''
					}licenseKeyIds=${activationKeyChecked.id}`,
				''
			),
		[activationKeysByStatusPaginatedChecked]
	);

	const isAbleToDownloadAggregateKeys = useMemo(() => {
		const [
			firstActivationKeyChecked,
			...restActivationKeysChecked
		] = activationKeysByStatusPaginatedChecked;

		return restActivationKeysChecked.every(
			(activationKeyChecked) =>
				DOWNLOADABLE_LICENSE_KEYS.above71DXPVersion(
					firstActivationKeyChecked,
					activationKeyChecked
				) ||
				DOWNLOADABLE_LICENSE_KEYS.below71DXPVersion(
					firstActivationKeyChecked,
					activationKeyChecked
				)
		);
	}, [activationKeysByStatusPaginatedChecked]);

	const handleDeactivate = useCallback(
		() =>
			setActivationKeys((previousActivationKeys) =>
				previousActivationKeys.filter(
					(activationKey) =>
						!activationKeysByStatusPaginatedChecked.find(
							({id}) => activationKey.id === id
						)
				)
			),
		[activationKeysByStatusPaginatedChecked, setActivationKeys]
	);

	const allowSelfProvisioning = project.allowSelfProvisioning;

	const bulkRenewAvailable = isBulkRenewAvailable(
		activationKeysByStatusPaginatedChecked
	);

	const complimentaryKeyValidation = (activationKey) => activationKey;

	const handleComplimentaryKey = activationKeysByStatusPaginatedChecked?.map(
		(activationKey) => activationKey.complimentary
	);

	const isComplimentaryKey = handleComplimentaryKey.some(
		complimentaryKeyValidation
	);

	useEffect(() => {
		if (isRenewTable) {
			setRenewKeysFilterChecked(filterCheckedActivationKeys);
		}
	}, [filterCheckedActivationKeys, isRenewTable, setRenewKeysFilterChecked]);

	return (
		<>
			<div className="bg-neutral-1 d-flex flex-column pb-1 pt-3 px-3 rounded">
				<div className="d-flex">
					<Filter
						activationKeys={activationKeys}
						filtersState={[filters, setFilters]}
					/>

					<div className="align-items-center d-flex ml-auto">
						{!!activationKeysByStatusPaginatedChecked.length &&
							!isRenewTable && (
								<>
									<p className="font-weight-semi-bold m-0 ml-auto pr-2 text-neutral-10">
										{i18n.sub('x-of-x-keys-selected', [
											activationKeysByStatusPaginatedChecked.length,
											activationKeys.length,
										])}
									</p>

									{(isAdminUserAccount ||
										isAdminOrPartnerManager) &&
										allowSelfProvisioning && (
											<DeactivateButton
												deactivateKeysStatus={
													status.deactivate
												}
												filterCheckedActivationKeys={
													filterCheckedActivationKeys
												}
												handleDeactivate={
													handleDeactivate
												}
												oAuthToken={oAuthToken}
												setDeactivateKeysStatus={(
													value
												) =>
													setStatus(
														(previousStatus) => ({
															...previousStatus,
															deactivate: value,
														})
													)
												}
											/>
										)}
								</>
							)}

						{featureFlags.includes('ISSD-78') &&
							(isAdminUserAccount || isAdminOrPartnerManager) &&
							allowSelfProvisioning &&
							activationKeysByStatusPaginatedChecked.length >=
								2 &&
							bulkRenewAvailable &&
							!isRenewTable && (
								<RenewButton
									activationKeysByStatusPaginatedChecked={
										activationKeysByStatusPaginatedChecked
									}
									filterCheckedActivationKeys={
										filterCheckedActivationKeys
									}
									identifier="renew"
									isComplimentaryKey={isComplimentaryKey}
								>
									{i18n.translate('renew')}
								</RenewButton>
							)}

						{!isRenewTable && (
							<ActionButton
								activationKeysByStatusPaginatedChecked={
									activationKeysByStatusPaginatedChecked
								}
								filterCheckedActivationKeys={
									filterCheckedActivationKeys
								}
								hasRenewalSubscription={hasRenewalSubscription}
								identifier="action"
								isAbleToDownloadAggregateKeys={
									isAbleToDownloadAggregateKeys
								}
								isAdminOrPartnerManager={
									isAdminOrPartnerManager
								}
								isAdminUserAccount={isAdminUserAccount}
								oAuthToken={oAuthToken}
								productName={productName}
								project={project}
								setStatus={setStatus}
							/>
						)}
					</div>
				</div>

				<BadgeFilter
					activationKeysLength={activationKeys?.length}
					filtersState={[filters, setFilters]}
					loading={loading}
				/>
			</div>

			{status.downloadAggregated && (
				<DownloadAlert
					downloadStatus={status.downloadAggregated}
					message={
						ALERT_ACTIVATION_AGGREGATED_KEYS_DOWNLOAD_TEXT[
							status.downloadAggregated
						]
					}
					setDownloadStatus={(value) =>
						setStatus((previousStatus) => ({
							...previousStatus,
							downloadAggregated: value,
						}))
					}
				/>
			)}

			{status.downloadMultiple && (
				<DownloadAlert
					downloadStatus={status.downloadMultiple}
					message={
						ALERT_ACTIVATION_MULTIPLE_KEYS_DOWNLOAD_TEXT[
							status.downloadMultiple
						]
					}
					setDownloadStatus={(value) =>
						setStatus((previousStatus) => ({
							...previousStatus,
							downloadMultiple: value,
						}))
					}
				/>
			)}

			{status.deactivate === ALERT_DOWNLOAD_TYPE.success && (
				<DownloadAlert
					downloadStatus="success"
					message={i18n.translate(
						'activation-keys-were-deactivated-successfully'
					)}
					setDownloadStatus={(value) =>
						setStatus((previousStatus) => ({
							...previousStatus,
							deactivate: value,
						}))
					}
				/>
			)}

			{!isAbleToDownloadAggregateKeys && (
				<ClayAlert className="my-2" displayType="info">
					<div
						dangerouslySetInnerHTML={{
							__html: i18n.sub(
								'to-download-an-aggregate-key-select-keys-for-a-valid-liferay-version-with-identical-type-start-date-end-date-and-instance-size-to-learn-more-click-x-here-x',
								['<a href="https://support.liferay.com/w/how-do-i-download-my-liferay-dxp-portal-activation-keys" target="_blank">', '</a>']
							)
						}}
					/>
				</ClayAlert>
			)}
		</>
	);
};

export default ActivationKeysTableHeader;
