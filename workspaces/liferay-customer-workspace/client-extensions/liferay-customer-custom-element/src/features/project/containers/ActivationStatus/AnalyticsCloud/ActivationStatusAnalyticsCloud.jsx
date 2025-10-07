/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import {ButtonWithIcon} from '@clayui/core';
import {Align} from '@clayui/drop-down';
import {useModal} from '@clayui/modal';
import {useEffect, useState} from 'react';
import SearchBuilder from '~/lib/SearchBuilder';
import i18n from '~/utils/I18n';
import {Button, ButtonDropDown} from '~/components';
import {useAppPropertiesContext} from '~/contexts/AppPropertiesContext';
import {AnalyticsIcon} from '~/assets/NavigationMenu';
import {
	getAccountSubscriptionGroups,
	getCommerceOrderItems,
	updateAccountSubscriptionGroups,
} from '~/services/liferay/graphql/queries';
import getActivationStatusDateRange from '~/utils/getActivationStatusDateRange';
import {ALERT_UPDATE_ANALYTICS_CLOUD_STATUS} from '~/features/project/containers/ActivationKeysTable/utils/constants/alertUpdateAnalyticsCloudStatus';
import {useAppContext} from '~/features/project/context';
import {actionTypes} from '~/features/project/context/reducer';
import {
	AUTO_CLOSE_ALERT_TIME,
	PRODUCT_TYPES,
	STATUS_TAG_TYPES,
	STATUS_TAG_TYPE_NAMES,
} from '~/features/project/utils/constants';
import AnalyticsCloudModal from '../../AnalyticsCloudModal';
import ActivationStatusLayout from '../Layout';
import AnalyticsCloudStatusModal from './AnalyticsCloudStatusModal';
import ActivationCardLink from '../ActivationCardLink';

const ActivationStatusAnalyticsCloud = () => {
	const [{project, subscriptionGroups, userAccount}, dispatch] =
		useAppContext();

	const {client} = useAppPropertiesContext();
	const [activationStatusDate, setActivationStatusDate] = useState('');
	const [isVisible, setIsVisible] = useState(false);
	const [visible, setVisible] = useState(false);
	const {observer: observerSetupModal, onClose} = useModal({
		onClose: () => setIsVisible(false),
	});
	const {observer: observerStatusModal, onClose: onCloseStatusModal} =
		useModal({
			onClose: () => setVisible(false),
		});

	const subscriptionGroupAnalyticsCloud = subscriptionGroups.find(
		(subscriptionGroup) =>
			subscriptionGroup.name === PRODUCT_TYPES.analyticsCloud
	);

	const [
		subscriptionGroupActivationStatus,
		setSubscriptionGroupActivationStatus,
	] = useState(subscriptionGroupAnalyticsCloud?.activationStatus);

	const onCloseSetupModal = async (isSuccess) => {
		onClose();

		if (isSuccess) {
			const searchBuilder = new SearchBuilder();
			const getSubscriptionGroups = async (accountKey) => {
				const {data: dataSubscriptionGroups} = await client.query({
					query: getAccountSubscriptionGroups,
					variables: {
						filter: searchBuilder
							.eq('accountKey', accountKey)
							.and()
							.eq('hasActivation', true)
							.build(),
					},
				});

				if (dataSubscriptionGroups) {
					dispatch({
						payload:
							dataSubscriptionGroups?.c?.accountSubscriptionGroups
								?.items,
						type: actionTypes.UPDATE_SUBSCRIPTION_GROUPS,
					});

					setSubscriptionGroupActivationStatus(
						STATUS_TAG_TYPE_NAMES.inProgress
					);
				}
			};

			getSubscriptionGroups(project.accountKey);
		}
	};

	const [hasFinishedUpdate, setHasFinishedUpdate] = useState(false);

	const currentActivationStatus = {
		[STATUS_TAG_TYPE_NAMES.active]: {
			buttonLink: project?.acWorkspaceGroupId && (
				<ActivationCardLink
					linkText={i18n.translate('go-to-workspace')}
					url={`https://analytics.liferay.com/workspace/${project?.acWorkspaceGroupId}/sites`}
				/>
			),
			id: STATUS_TAG_TYPES.active,
			subtitle: i18n.translate(
				'your-analytics-cloud-environments-are-ready-visit-the-workspace-to-view-analytics-cloud-details'
			),
			title: i18n.translate('analytics-cloud-activation'),
		},
		[STATUS_TAG_TYPE_NAMES.inProgress]: {
			dropdownIcon: (userAccount.isStaff &&
				userAccount.isProvisioning) && (
				<ButtonDropDown
					align={Align.BottomRight}
					customDropDownButton={
						<ButtonWithIcon
							aria-label={i18n.translate('set-to-active')}
							className="text-secondary"
    						displayType="unstyled"
							small
							spritemap={Liferay.Icons.spritemap}
							symbol="caret-bottom"
						/>
					}
					items={[
						{
							label: i18n.translate('set-to-active'),
							onClick: () => setVisible(true),
						},
					]}
					menuElementAttrs={{
						className: 'p-0 cp-activation-key-icon rounded-xs',
					}}
				/>
			),
			id: STATUS_TAG_TYPES.inProgress,
			subtitle: i18n.translate(
				'your-analytics-cloud-workspace-is-being-set-up-and-will-be-available-soon'
			),
			title: i18n.translate('analytics-cloud-activation'),
		},
		[STATUS_TAG_TYPE_NAMES.notActivated]: {
			buttonLink: userAccount.isAccountAdmin && (
				<Button
					appendIcon="order-arrow-right"
					aria-label={i18n.translate('finish-activation')}
					className="btn btn-link font-weight-semi-bold p-0 text-brand-primary text-paragraph"
					displayType="link"
					onClick={() => setIsVisible(true)}
				>
					{i18n.translate('finish-activation')}
				</Button>
			),
			id: STATUS_TAG_TYPES.notActivated,
			subtitle: i18n.translate(
				'almost-there-setup-analytics-cloud-by-finishing-the-activation-form'
			),
			title: i18n.translate('analytics-cloud-activation'),
		},
	};

	const activationStatus =
		currentActivationStatus[
			subscriptionGroupActivationStatus ||
				STATUS_TAG_TYPE_NAMES.notActivated
		];

	useEffect(() => {
		const fetchCommerceOrderItems = async () => {
			const {data} = await client.query({
				query: getCommerceOrderItems,
				variables: {
					filter: SearchBuilder.eq(
						'customFields/accountSubscriptionGroupERC',
						`${project.accountKey}_analytics-cloud`
					),
				},
			});

			if (data) {
				setActivationStatusDate(
					getActivationStatusDateRange(data?.orderItems?.items)
				);
			}
		};

		fetchCommerceOrderItems();
	}, [client, project]);

	const updateGroupId = async () => {
		await client.mutate({
			context: {
				displaySuccess: false,
				type: 'liferay-rest',
			},
			mutation: updateAccountSubscriptionGroups,
			variables: {
				accountSubscriptionGroup: {
					accountKey: project.accountKey,
					activationStatus: STATUS_TAG_TYPE_NAMES.active,
					r_accountEntryToAccountSubscriptionGroup_accountEntryId:
						project.id,
				},
				id: subscriptionGroupAnalyticsCloud?.accountSubscriptionGroupId,
			},
		});

		setSubscriptionGroupActivationStatus(STATUS_TAG_TYPE_NAMES.active);
		setVisible(false);
		setHasFinishedUpdate(true);
	};

	return (
		<>
			{isVisible && (
				<AnalyticsCloudModal
					observer={observerSetupModal}
					onClose={onCloseSetupModal}
					project={project}
					subscriptionGroupId={
						subscriptionGroupAnalyticsCloud.accountSubscriptionGroupId
					}
				/>
			)}

			<ActivationStatusLayout
				activationStatus={activationStatus}
				activationStatusDate={activationStatusDate}
				iconPath={AnalyticsIcon}
				project={project}
				subscriptionGroupActivationStatus={
					subscriptionGroupActivationStatus
				}
			/>

			{visible && (
				<AnalyticsCloudStatusModal
					groupIdValue={project?.acWorkspaceGroupId}
					observer={observerStatusModal}
					onClose={onCloseStatusModal}
					updateCardStatus={updateGroupId}
				/>
			)}

			{hasFinishedUpdate && (
				<ClayAlert.ToastContainer>
					<ClayAlert
						autoClose={AUTO_CLOSE_ALERT_TIME.success}
						displayType="success"
						onClose={() => setHasFinishedUpdate(false)}
					>
						{ALERT_UPDATE_ANALYTICS_CLOUD_STATUS.success}
					</ClayAlert>
				</ClayAlert.ToastContainer>
			)}
		</>
	);
};

export default ActivationStatusAnalyticsCloud;
