/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayCard from '@clayui/card';
import classNames from 'classnames';
import {memo, useMemo} from 'react';
import {Skeleton, StatusTag} from '~/components';
import {useAppPropertiesContext} from '~/contexts/AppPropertiesContext';
import PopoverIconButton from '~/features/project/components/PopoverIconButton';
import {getLicenseKeyPermanentStatus} from '~/features/project/containers/GenerateNewKey/utils/licenseKeyPermanentStatus';
import {getPerpetualValidStartDate} from '~/features/project/containers/GenerateNewKey/utils/perpetualValidStartDate';
import {getSubscriptionStatus} from '~/features/project/utils/getSubscriptionStatus';
import {useGetAccountSubscriptionUsage} from '~/services/liferay/graphql/account-subscription-usage';
import i18n from '~/utils/I18n';
import {FORMAT_DATE_TYPES} from '~/utils/constants';
import {
	PRODUCT_DISPLAY_EXCEPTION,
	PRODUCT_DISPLAY_EXCEPTION_INSTANCE_SIZE,
	SUBSCRIPTION_TYPES,
} from '~/utils/constants/subscriptionCardsCount';
import getDateCustomFormat from '~/utils/getDateCustomFormat';

import useOrderItems from '../AccountSubscriptionModal/hooks/useOrderItems';

import './AccountSubscriptionCard.css';

const AccountSubscriptionCard = ({
	IsPortalOrDXP,
	loading,
	logoPath: IconSVG,
	onClick,
	selectedAccountSubscriptionGroup,
	...accountSubscription
}) => {
	const {theOverviewPageURL} = useAppPropertiesContext();

	const {data: accountSubscriptionUsageData} = useGetAccountSubscriptionUsage(
		accountSubscription?.accountKey,
		accountSubscription?.productKey,
		IsPortalOrDXP
	);

	const currentConsumption = useMemo(
		() =>
			accountSubscriptionUsageData?.getAccountSubscriptionUsage
				?.currentConsumption,
		[accountSubscriptionUsageData]
	);

	let quantity = 0;

	const now = new Date();

	const {
		data
	} = useOrderItems(accountSubscription.externalReferenceCode, 1000);

	data?.orderItems?.items?.map((item) => {
		if (
			now > new Date(item.options?.startDate) &&
			now < new Date(item.options?.endDate)
		) {
			quantity += item.quantity;
		}
	});

	const DisplayOnCard = {
		Blank: null,
		Purchased: (
			<>
				{quantity && (
					<span className="align-items-center d-flex justify-content-start m-0">
						{quantity}
					</span>
				)}
			</>
		),
		PurchasedAndProvisioned: (
			<span className="d-flex justify-content-start m-0">
				{currentConsumption !== undefined
					? `${currentConsumption} ${i18n.translate('of')} ${quantity}`
					: `0 ${i18n.translate('of')} ${quantity}`}
			</span>
		),
	};

	const displayQuantityOnCard = (subscriptionType, productName) => {
		const isPurchasedAndProvisioned =
			SUBSCRIPTION_TYPES.PurchasedAndProvisioned.includes(
				subscriptionType
			);
		const isPurchased =
			SUBSCRIPTION_TYPES.Purchased.includes(subscriptionType);

		if (isPurchasedAndProvisioned) {
			if (PRODUCT_DISPLAY_EXCEPTION.blankProducts.includes(productName)) {
				return DisplayOnCard.Blank;
			}

			return PRODUCT_DISPLAY_EXCEPTION.purchasedProduct.includes(
				productName
			)
				? DisplayOnCard.Purchased
				: DisplayOnCard.PurchasedAndProvisioned;
		}

		if (isPurchased) {
			if (
				subscriptionType === 'Liferay Experience Cloud' ||
				subscriptionType === 'Other'
			) {
				return PRODUCT_DISPLAY_EXCEPTION.blankProducts.includes(
					productName
				)
					? DisplayOnCard.Blank
					: DisplayOnCard.Purchased;
			}

			return DisplayOnCard.Purchased;
		}

		return PRODUCT_DISPLAY_EXCEPTION.nonBlankProducts.includes(productName)
			? DisplayOnCard.Purchased
			: DisplayOnCard.Blank;
	};

	const keysProvisionedContent = displayQuantityOnCard(
		selectedAccountSubscriptionGroup?.name,
		accountSubscription?.name
	);

	const DisplayOnCardInstanceSize = {
		Blank: null,
		PurchasedAndProvisioned: accountSubscription.instanceSize > 0 && (
			<span className="align-items-center d-flex justify-content-start m-0">
				{accountSubscription.instanceSize}
			</span>
		),
	};

	const displayInstanceSizeOnCard = (subscriptionType, productName) => {
		const isPurchasedAndProvisioned =
			SUBSCRIPTION_TYPES.PurchasedAndProvisioned.includes(
				subscriptionType
			);

		if (isPurchasedAndProvisioned) {
			return PRODUCT_DISPLAY_EXCEPTION_INSTANCE_SIZE.purchasedProductInstanceSize.includes(
				productName
			)
				? DisplayOnCardInstanceSize.Blank
				: DisplayOnCardInstanceSize.PurchasedAndProvisioned;
		}
	};

	const keysProvisionedContentInstanceSize = displayInstanceSizeOnCard(
		selectedAccountSubscriptionGroup?.name,
		accountSubscription?.name
	);

	const isPurchased = SUBSCRIPTION_TYPES.Purchased.includes(
		selectedAccountSubscriptionGroup?.name
	);

	const accountSubscriptionGroupName =
		accountSubscription?.name === 'Designated Contact' || isPurchased;

	const isPermanentLicenseKey = getLicenseKeyPermanentStatus(
		accountSubscription?.startDate,
		accountSubscription?.endDate
	);

	const isValidPerpetualStartDate = getPerpetualValidStartDate(
		accountSubscription?.startDate
	);

	return (
		<ClayCard
			className={classNames(
				' cp-account-subscription-card-loading d-flex flex-column mb-4 shadow-none  w-100 ',
				{
					'card-interactive': !loading,
				}
			)}
			onClick={onClick}
		>
			<ClayCard.Body className="cp-account-subscription-card p-3 w-100">
				<div className="d-flex">
					{loading ? (
						<Skeleton
							className="mb-3 py-1"
							height={45}
							width={48}
						/>
					) : (
						IconSVG && (
							<div className="align-items-center d-flex">
								<IconSVG height={45} width={45} />
							</div>
						)
					)}

					{loading ? (
						<Skeleton
							className="cp-account-subscription-card-name"
							height={20}
							width={90}
						/>
					) : (
						<h5 className="cp-account-subscription-card-name p-2">
							<p className="cp-account-subscription-card-name">
								{accountSubscription.name}
							</p>
						</h5>
					)}

					{loading ? (
						<Skeleton height={20} width={38} />
					) : (
						<div className="cp-account-subscription-card-icon-info ml-auto">
							<StatusTag
								currentStatus={getSubscriptionStatus(
									new Date(accountSubscription.startDate),
									new Date(accountSubscription.endDate)
								)}
							/>
						</div>
					)}

					<div className="align-items-center cp-account-subscription-card-icon d-flex">
						<PopoverIconButton
							popoverLink={{
								textLink: i18n.translate(
									'learn-more-about-your-liferay-subscriptions-data'
								),
								url: theOverviewPageURL,
							}}
							symbol="question-circle"
						/>
					</div>
				</div>

				<div className="cp-account-subscription-card-info d-flex margin-left-container margin-right-container mt-3 mw-100">
					{loading ? (
						<Skeleton className="mb-1" height={13} width={80} />
					) : (
						keysProvisionedContentInstanceSize && (
							<div className="cp-account-subscription-card-info-bottom mb-0">
								<p className="title-info-bottom">{`${i18n.translate(
									'instance-size'
								)}`}</p>

								<p className="description-info-bottom">
									{keysProvisionedContentInstanceSize}
								</p>
							</div>
						)
					)}

					{keysProvisionedContent && (
						<div className="cp-account-subscription-card-info-bottom mb-0">
							<p className="title-info-bottom">{`${i18n.translate(
								accountSubscriptionGroupName
									? 'purchased'
									: 'keys-provisioned'
							)}`}</p>

							<p className="description-info-bottom">
								{keysProvisionedContent}
							</p>
						</div>
					)}

					{loading ? (
						<Skeleton className="mb-3" height={24} width={160} />
					) : (
						accountSubscription.startDate && (
							<div className="cp-account-subscription-card-info-bottom mb-0">
								<p className="title-info-bottom">{`${i18n.translate(
									'start-date'
								)}`}</p>

								<p className="description-info-bottom">
									{isPermanentLicenseKey &&
									isValidPerpetualStartDate
										? i18n.translate('not-applicable')
										: getDateCustomFormat(
												FORMAT_DATE_TYPES.day2DMonthSYearN,
												accountSubscription.startDate
											)}
								</p>
							</div>
						)
					)}

					{loading ? (
						<Skeleton className="mb-3" height={24} width={160} />
					) : (
						accountSubscription.endDate && (
							<div className="cp-account-subscription-card-info-bottom mb-0">
								<p className="title-info-bottom">{`${i18n.translate(
									'expiration-date'
								)}`}</p>

								<p className="description-info-bottom">
									{isPermanentLicenseKey &&
									isValidPerpetualStartDate
										? i18n.translate('not-applicable')
										: getDateCustomFormat(
												FORMAT_DATE_TYPES.day2DMonthSYearN,
												accountSubscription.endDate
											)}
								</p>
							</div>
						)
					)}
				</div>
			</ClayCard.Body>
		</ClayCard>
	);
};

export default memo(AccountSubscriptionCard);
