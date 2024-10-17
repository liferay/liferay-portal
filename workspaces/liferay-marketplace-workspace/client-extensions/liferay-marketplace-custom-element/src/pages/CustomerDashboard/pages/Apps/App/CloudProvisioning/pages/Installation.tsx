/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import classNames from 'classnames';
import {useEffect, useMemo} from 'react';
import {useOutletContext} from 'react-router-dom';

import Loading from '../../../../../../../components/Loading';
import ProductPurchase from '../../../../../../../components/ProductPurchase';
import {useMarketplaceContext} from '../../../../../../../context/MarketplaceContext';
import i18n from '../../../../../../../i18n';
import {cloudConsoleURLs, openLink} from '../../../../../../../utils/link';
import {CloudProvisioningOutletContext} from './CloudProvisioningOutlet';

enum Statuses {
	FAILED,
	LOADING,
	SUCCESS,
}

const MARKETPLACE_ADMIN_EMAIL = 'marketplace-admin@liferay.com';

const statuses = {
	[Statuses.FAILED]: {
		bodyMessage: (
			<span>
				Sorry, was not possible to install your app, you can try again.
				If the problem persist, contact a{' '}
				<a href={`mailto:${MARKETPLACE_ADMIN_EMAIL}`}>
					{MARKETPLACE_ADMIN_EMAIL}
				</a>
			</span>
		),
		icon: (
			<ClayIcon color="red" fontSize="4rem" symbol="times-circle-full" />
		),
		title: i18n.translate('installation-failed'),
	},
	[Statuses.LOADING]: {
		bodyMessage: i18n.translate(
			'the-installation-process-is-ongoing-and-may-take-some-time-navigating-to-other-sections-will-not-cancel-the-process'
		),
		icon: <Loading displayType="primary" shape="squares" size="lg" />,
		title: i18n.translate('installation-in-progress'),
	},
	[Statuses.SUCCESS]: {
		bodyMessage: i18n.translate(
			'you-can-view-your-app-in-cloud-console-or-go-back-to-my-apps'
		),
		icon: (
			<ClayIcon
				color="#4AAB3B"
				fontSize="4rem"
				symbol="check-circle-full"
			/>
		),
		title: i18n.translate('installation-success'),
	},
};

const CloudProvisioningInstallation = () => {
	const {
		properties: {cloudConsoleURL},
	} = useMarketplaceContext();
	const {
		form: {
			formState: {isSubmitSuccessful, isSubmitted, isSubmitting},
			watch,
		},
		navigate,
		orderId,
	} = useOutletContext<CloudProvisioningOutletContext>();

	const environment = watch('environment');

	const isLoading = isSubmitting || !isSubmitted;

	const status = useMemo(() => {
		if (isLoading) {
			return statuses[Statuses.LOADING];
		}

		if (isSubmitSuccessful) {
			return statuses[Statuses.SUCCESS];
		}

		return statuses[Statuses.FAILED];
	}, [isLoading, isSubmitSuccessful]);

	useEffect(() => {
		if (!environment) {
			navigate('');
		}
	}, [navigate, environment]);

	return (
		<ProductPurchase.Shell
			className="align-items-center d-flex flex-column mt-5"
			footerProps={{
				backButtonProps: {
					children: i18n.translate('go-to-app-provisioning'),
					onClick: () =>
						navigate(`/order/${orderId}/cloud-provisioning`),
				},
				cancelButtonProps: {
					children: <></>,
				},
				continueButtonProps: {
					children: 'View App In Cloud',
					onClick: () =>
						openLink(
							cloudConsoleURLs.getProjectServices(
								cloudConsoleURL,
								environment.projectId
							)
						),
					...{
						className: classNames({
							'd-none': !isSubmitSuccessful,
							'ml-3': !isLoading || isSubmitSuccessful,
						}),
					},
				},
			}}
			title={status.title}
		>
			{status.icon}

			<span className="col-7 mt-6 text-center">{status.bodyMessage}</span>
		</ProductPurchase.Shell>
	);
};

export default CloudProvisioningInstallation;
