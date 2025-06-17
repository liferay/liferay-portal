/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import classNames from 'classnames';
import {useMemo, useState} from 'react';
import {useNavigate, useParams} from 'react-router-dom';
import useSWR, {KeyedMutator} from 'swr';

import {useMarketplaceContext} from '../../../../context/MarketplaceContext';
import {ProductWorkflowStatusCode} from '../../../../enums/Product';
import i18n from '../../../../i18n';
import {Liferay} from '../../../../liferay/liferay';
import koroneikiOAuth2 from '../../../../services/oauth/Koroneiki';
import HeadlessCommerceAdminCatalog from '../../../../services/rest/HeadlessCommerceAdminCatalog';
import {
	getProductVersionFromSpecifications,
	getThumbnailByProductAttachment,
	showAppImage,
} from '../../../../utils/util';
import {ReviewAndSubmitAppPage} from './AppCreationFlow/ReviewAndSubmitAppPage/ReviewAndSubmitAppPage';
import AppDetail from './AppDetail';

import './App.scss';

type AppProps = {
	isAdministratorDashboard?: boolean;
};

type AdministratorButtons = {
	mutate: KeyedMutator<any>;
	productId: number;
	selectedApp: any;
};

const AdministratorButtons: React.FC<AdministratorButtons> = ({
	mutate,
	productId,
	selectedApp,
}) => {
	const [loading, setLoading] = useState(false);

	const isDraft =
		selectedApp.workflowStatusInfo.code === ProductWorkflowStatusCode.DRAFT;

	const onUpdateRequestStatus = async (
		workflowStatus: ProductWorkflowStatusCode
	) => {
		try {
			await HeadlessCommerceAdminCatalog.updateProductByExternalReferenceCode(
				selectedApp.externalReferenceCode,
				{workflowStatusInfo: workflowStatus}
			);

			mutate((data: any) => data, {revalidate: true});

			Liferay.Util.openToast({
				message: i18n.translate('your-request-completed-successfully'),
				type: 'success',
			});
		}
		catch {
			Liferay.Util.openToast({
				message: i18n.translate('an-unexpected-error-occurred'),
				type: 'danger',
			});
		}
	};

	return (
		<>
			<ClayButton
				className="font-weight-bold mr-5"
				disabled={loading}
				displayType="unstyled"
				onClick={() => {
					setLoading(true);

					koroneikiOAuth2
						.syncProduct(productId)
						.then(() =>
							Liferay.Util.openToast({
								message: 'Koroneiki Sync Successfully',
								title: 'Success',
							})
						)
						.catch((error) => {
							console.error(error);

							Liferay.Util.openToast({
								message: 'Koroneiki Sync Failed',
								title: 'Error',
								type: 'danger',
							});
						})
						.finally(() => setLoading(false));
				}}
			>
				{loading ? 'Synchronizing...' : 'Sync to KR'}
			</ClayButton>

			{isDraft && (
				<ClayButton
					displayType="primary"
					onClick={() =>
						onUpdateRequestStatus(
							ProductWorkflowStatusCode.APPROVED
						)
					}
				>
					{i18n.translate('approve')}
				</ClayButton>
			)}
		</>
	);
};

const App: React.FC<AppProps> = ({isAdministratorDashboard}) => {
	const {productId} = useParams();
	const {myUserAccount, properties} = useMarketplaceContext();
	const navigate = useNavigate();

	const isNewAppEnabled = properties.featureFlags.includes('LPD-24546');

	const {
		data: selectedApp,
		isLoading,
		mutate,
	} = useSWR(`/published-app/${productId}`, () =>
		HeadlessCommerceAdminCatalog.getProduct(
			productId as unknown as number,
			new URLSearchParams({
				nestedFields: 'attachments,images,productSpecifications',
			})
		)
	);

	const appVersion = useMemo(
		() =>
			getProductVersionFromSpecifications(
				selectedApp?.productSpecifications ?? []
			),
		[selectedApp?.productSpecifications]
	);

	if (!selectedApp || isLoading) {
		return null;
	}

	const status = selectedApp?.workflowStatusInfo?.label?.replace(
		/(^\w|\s\w)/g,
		(m: string) => m.toUpperCase()
	);

	const thumbnail = getThumbnailByProductAttachment(selectedApp?.images);

	return (
		<div className="app-details-page-container">
			<ClayButton
				className="align-items-center d-flex"
				displayType="unstyled"
				onClick={() => navigate('..')}
			>
				<ClayIcon className="mr-2" symbol="order-arrow-left" />
				<span className="h5 mt-1">
					{i18n.translate('back-to-apps')}
				</span>
			</ClayButton>

			{status === 'Draft' && (
				<ClayAlert
					className="app-details-page-alert-container"
					displayType="info"
				>
					<span className="app-details-page-alert-text">
						This submission is currently under review by Liferay.
						Once the process is complete, we will publish it into
						Marketplace. Meanwhile, any information or data from
						this app submission cannot be updated.
					</span>
				</ClayAlert>
			)}

			<div className="app-details-page-app-info-main-container mt-4">
				<div className="app-details-page-app-info-left-container">
					<div>
						<img
							alt="App Logo"
							className="app-details-page-icon"
							src={showAppImage(thumbnail)}
						/>
					</div>

					<div>
						<span
							className="app-details-page-app-info-title d-block text-truncate"
							title={selectedApp.name?.en_US}
						>
							{selectedApp.name?.en_US}
						</span>

						<div className="app-details-page-app-info-subtitle-container">
							{appVersion && (
								<span className="app-details-page-app-info-subtitle-text">
									{appVersion}
								</span>
							)}

							<ClayIcon
								aria-label="status icon"
								className={classNames(
									'app-details-page-app-info-subtitle-icon',
									{
										'app-details-page-app-info-subtitle-icon-hidden':
											selectedApp.workflowStatusInfo
												.label === 'draft',
										'app-details-page-app-info-subtitle-icon-pending':
											selectedApp.workflowStatusInfo
												.label === 'pending',
										'app-details-page-app-info-subtitle-icon-published':
											selectedApp.workflowStatusInfo
												.label === 'approved',
									}
								)}
								symbol="circle"
							/>

							<span className="app-details-page-app-info-subtitle-text">
								{selectedApp.workflowStatusInfo.label_i18n}
							</span>
						</div>
					</div>
				</div>

				{isAdministratorDashboard &&
					myUserAccount?.roleBriefs.some(
						({name}) => name === 'Administrator'
					) && (
						<div className="app-details-page-app-info-buttons-container">
							<AdministratorButtons
								mutate={mutate}
								productId={productId as unknown as number}
								selectedApp={selectedApp}
							/>
						</div>
					)}
			</div>
			<div>
				{isNewAppEnabled ? (
					<AppDetail />
				) : (
					<ReviewAndSubmitAppPage
						onClickBack={() => {}}
						onClickContinue={() => {}}
						productERC={selectedApp.externalReferenceCode}
						productId={selectedApp.productId}
						readonly
					/>
				)}
			</div>
		</div>
	);
};

export default App;
