/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import classNames from 'classnames';
import {useMemo} from 'react';
import {useLocation, useNavigate, useParams} from 'react-router-dom';
import useSWR from 'swr';

import {useMarketplaceContext} from '../../../../context/MarketplaceContext';
import {ProductWorkflowStatusCode} from '../../../../enums/Product';
import i18n from '../../../../i18n';
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
	header?: any;
};

const App: React.FC<AppProps> = ({header}) => {
	const {productId} = useParams();
	const {properties} = useMarketplaceContext();
	const navigate = useNavigate();
	const {pathname} = useLocation();

	const {data: product, isLoading} = useSWR(
		`/published-app/${productId}`,
		() =>
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
				product?.productSpecifications ?? []
			),
		[product?.productSpecifications]
	);

	if (isLoading || !product) {
		return null;
	}

	const isNewAppEnabled = properties.featureFlags.includes('LPD-24546');

	const thumbnail = getThumbnailByProductAttachment(product?.images);

	return (
		<div className="app-details-page-container">
			<ClayButton
				className="align-items-center d-flex"
				displayType="unstyled"
				onClick={() => navigate('..')}
			>
				<ClayIcon className="mr-2" symbol="order-arrow-left" />
				<span className="h5 mt-1">
					{i18n.translate(
						pathname.includes('/solutions')
							? 'back-to-solutions'
							: 'back-to-apps'
					)}
				</span>
			</ClayButton>

			{product.workflowStatusInfo.code ===
				ProductWorkflowStatusCode.DRAFT && (
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
							title={product.name?.en_US}
						>
							{product.name?.en_US}
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
											product.workflowStatusInfo.label ===
											'draft',
										'app-details-page-app-info-subtitle-icon-pending':
											product.workflowStatusInfo.label ===
											'pending',
										'app-details-page-app-info-subtitle-icon-published':
											product.workflowStatusInfo.label ===
											'approved',
									}
								)}
								symbol="circle"
							/>

							<span className="app-details-page-app-info-subtitle-text">
								{product.workflowStatusInfo.label_i18n}
							</span>
						</div>
					</div>
				</div>

				{header}
			</div>
			<div>
				{isNewAppEnabled ? (
					<AppDetail />
				) : (
					<ReviewAndSubmitAppPage
						productERC={product.externalReferenceCode}
						productId={product.productId}
						readonly
					/>
				)}
			</div>
		</div>
	);
};

export default App;
