/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import useSWR from 'swr';

import checkCircleIcon from '../../assets/icons/check_circle_icon.svg';
import paymentPendingIcon from '../../assets/icons/payment_pending_icon.svg';
import timesCircleIcon from '../../assets/icons/times_circle_icon.svg';
import {AccountAndAppCard} from '../../components/Card/AccountAndAppCard';
import {Header} from '../../components/Header/Header';
import {PageRenderer} from '../../components/Page';
import {OrderTypes, PaymentStatus} from '../../enums/Order';
import withProviders from '../../hoc/withProviders';
import useGetProductByOrderId from '../../hooks/useGetProductByOrderId';
import i18n from '../../i18n';
import {Liferay} from '../../liferay/liferay';
import HeadlessAdminUser from '../../services/rest/HeadlessAdminUser';
import {getSiteURL} from '../../utils/site';
import {getAccountImage} from '../../utils/util';

import './NextSteps.scss';

import {Fragment} from 'react/jsx-runtime';

type NextStepsBodyProps = ReturnType<typeof useGetProductByOrderId>['data'];

export function NextStepsBody(props: NextStepsBodyProps) {
	const placedOrder = props!.placedOrder;
	const product = props!.product;

	const accountId = placedOrder.accountId;
	const orderId = placedOrder.id;
	const productName = product.name;

	const {data: accountCommerce} = useSWR(
		accountId ? `/next-steps/account-commerce/${accountId}` : null,
		() => HeadlessAdminUser.getAccount(accountId as unknown as string)
	);

	const paymentStatus = placedOrder?.paymentStatus;

	const isCloudApp =
		placedOrder?.orderTypeExternalReferenceCode === OrderTypes.CLOUDAPP;

	const continueButtonKey =
		paymentStatus === PaymentStatus.PAID
			? isCloudApp
				? 'continue-to-install'
				: 'continue-to-download'
			: 'go-to-the-catalog';

	const nextStepBody = {
		[PaymentStatus.FAILED]: (
			<Header
				description={
					<span>
						<p className="text-center">
							We&apos;re sorry, but your PayPal payment for{' '}
							<strong>{productName}</strong> could not be
							processed. Please try again or use a different
							PayPal account.
						</p>
						<p className="d-flex justify-content-center m-0 next-step-page-text-bold">
							Need help?&nbsp;{' '}
							<a href="mailto:marketplace-admin@liferay.com">
								marketplace-admin@liferay.com
							</a>
						</p>
					</span>
				}
				icon={
					<span className="d-flex justify-content-center">
						<img
							alt="payment pending icon"
							draggable="false"
							src={timesCircleIcon}
						/>
					</span>
				}
				title={
					<span className="d-flex justify-content-center mb-5 next-step-page-title">
						{i18n.translate('purchase-failed')}
					</span>
				}
			/>
		),

		[PaymentStatus.PAID]: (
			<Header
				description={
					<span>
						<p className="mb-4 text-center">
							Thank you for choosing{' '}
							<strong>{productName}</strong>. Your purchase has
							been successfully processed. To continue, please
							click the button below to download or install the
							app.
						</p>

						<p className="align-items-end d-flex justify-content-center mb-0">
							Your Order ID is: &nbsp;
							<a
								className="next-step-page-text-bold"
								href={`${getSiteURL()}/customer-dashboard#/order/${orderId}`}
							>
								<span className="mb-0 next-step-page-order next-step-page-text-bold span">
									{orderId}
								</span>
							</a>
						</p>
					</span>
				}
				icon={
					<span className="d-flex justify-content-center">
						<img
							alt="check circle icon"
							draggable="false"
							src={checkCircleIcon}
						/>
					</span>
				}
				title={
					<span className="d-flex justify-content-center mb-5 next-step-page-title">
						{i18n.translate('purchase-completed')}
					</span>
				}
			/>
		),
		[PaymentStatus.PENDING]: (
			<Header
				description={
					<span>
						<p className="text-center">
							Thank you for your order. We have registered your
							request and will send you the invoice by email with
							all the details to complete your payment. Check your
							Spam or Promotions folder if you don&apos;t see it
							in your inbox. Your order is currently{' '}
							<strong>pending payment</strong>.
						</p>
						<p className="d-flex justify-content-center m-0 next-step-page-text-bold">
							Need help?&nbsp;{' '}
							<a href="mailto:marketplace-admin@liferay.com">
								marketplace-admin@liferay.com
							</a>
						</p>
					</span>
				}
				icon={
					<span className="d-flex justify-content-center">
						<img
							alt="payment pending icon"
							draggable="false"
							src={paymentPendingIcon}
						/>
					</span>
				}
				title={
					<span className="d-flex justify-content-center mb-5 next-step-page-title">
						{i18n.translate('order-received')}
					</span>
				}
			/>
		),
	};

	return (
		<Fragment>
			<div className="next-step-page-cards">
				<AccountAndAppCard
					category="Application"
					logo={
						props!.marketplaceDeliveryOrder.productThumbnail ||
						'catalog'
					}
					title={productName}
				/>

				<ClayIcon
					className="m-0 next-step-page-icon"
					symbol="arrow-right-full"
				/>

				<AccountAndAppCard
					category="Account"
					logo={getAccountImage(accountCommerce?.logoURL as string)}
					title={accountCommerce?.name ?? ''}
				/>
			</div>

			<div className="next-step-page-text">
				<div className="next-step-page-text">
					{(nextStepBody as any)[String(paymentStatus) || '']}
				</div>
			</div>

			<div className="d-flex justify-content-center mt-4 next-step-page-footer-button-container">
				<ClayButton
					className="mr-3 next-step-page-footer-button-back"
					displayType="secondary"
					onClick={() => {
						Liferay.Util.navigate(
							`${getSiteURL()}/customer-dashboard`
						);
					}}
				>
					{i18n.translate('go-to-my-apps')}
				</ClayButton>

				<ClayButton
					className="next-step-page-footer-button-continue"
					displayType="primary"
					onClick={() => {
						const url =
							paymentStatus === PaymentStatus.PAID
								? isCloudApp
									? `${getSiteURL()}/customer-dashboard#/order/${orderId}/cloud-provisioning`
									: `${getSiteURL()}/customer-dashboard#/order/${orderId}/download`
								: getSiteURL();
						Liferay.Util.navigate(url);
					}}
				>
					{i18n.translate(continueButtonKey)}
				</ClayButton>
			</div>

			{paymentStatus === PaymentStatus.PAID && (
				<div className="d-flex justify-content-center next-step-page-learn-more">
					<a
						href="https://learn.liferay.com/w/dxp/development/marketplace"
						target="_blank"
					>
						Learn more about App Configuration
					</a>
				</div>
			)}
		</Fragment>
	);
}

export function NextSteps() {
	const urlParams = new URLSearchParams(window.location.search);
	const orderId = urlParams.get('orderId');

	const {data, error, isLoading} = useGetProductByOrderId(orderId as string);

	if (isLoading) {
		return <ClayLoadingIndicator />;
	}

	return (
		<PageRenderer
			className="next-step-page-container"
			error={error}
			isLoading={isLoading}
		>
			<div className="next-step-page-content">
				<NextStepsBody {...data!} />
			</div>
		</PageRenderer>
	);
}

export default withProviders(NextSteps);
