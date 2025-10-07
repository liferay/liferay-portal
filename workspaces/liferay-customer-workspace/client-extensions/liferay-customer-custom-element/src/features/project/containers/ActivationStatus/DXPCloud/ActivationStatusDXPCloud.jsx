/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import {ButtonWithIcon} from '@clayui/core';
import {Align} from '@clayui/drop-down';
import ClayModal, {useModal} from '@clayui/modal';
import {useEffect, useState} from 'react';

import i18n from '~/utils/I18n';

import {Button, ButtonDropDown} from '~/components';
import SetupDXPCloudForm from '~/features/project/containers/SetupDXPCloudForm';
import {useAppPropertiesContext} from '~/contexts/AppPropertiesContext';
import {DXPIcon} from '~/assets/NavigationMenu';
import {
	getAccountSubscriptionGroups,
	getCommerceOrderItems,
} from '~/services/liferay/graphql/queries';
import getActivationStatusDateRange from '~/utils/getActivationStatusDateRange';
import {ALERT_UPDATE_DXP_CLOUD_STATUS} from '~/features/project/containers/ActivationKeysTable/utils/constants';
import {useAppContext} from '~/features/project/context';
import {actionTypes} from '~/features/project/context/reducer';
import {
	AUTO_CLOSE_ALERT_TIME,
	LIST_TYPES,
	STATUS_TAG_TYPES,
	STATUS_TAG_TYPE_NAMES,
} from '~/features/project/utils/constants';
import ModalDXPCActivationStatus from '../../ModalDXPCActivationStatus';
import AlreadySubmittedModal from '../AlreadySubmittedModal';
import ActivationStatusLayout from '../Layout';
import PopoverIcon from './components/PopoverIcon';
import ActivationCardLink from '../ActivationCardLink'; 

const submittedModalTexts = {
	paragraph: i18n.translate(
		'return-to-the-product-activation-page-to-view-the-current-activation-status'
	),
	subtitle: i18n.translate(
		'we-ll-need-a-few-details-to-finish-building-your-liferay-paas-environment'
	),
	text: i18n.translate(
		'another-user-already-submitted-the-liferay-paas-activation-request'
	),
	title: i18n.translate('set-up-liferay-paas'),
};

const SetupDXPCloudModal = ({
	observer,
	onClose,
	project,
	subscriptionGroupId,
}) => {
	const [formAlreadySubmitted, setFormAlreadySubmitted] = useState(false);
	const {client} = useAppPropertiesContext();

	return (
		<ClayModal center observer={observer}>
			{formAlreadySubmitted ? (
				<AlreadySubmittedModal
					onClose={onClose}
					submittedModalTexts={submittedModalTexts}
				/>
			) : (
				<SetupDXPCloudForm
					client={client}
					dxpVersion={project.dxpVersion}
					handlePage={onClose}
					leftButton={i18n.translate('cancel')}
					listType={LIST_TYPES.dxpMajorVersion}
					project={project}
					setFormAlreadySubmitted={setFormAlreadySubmitted}
					subscriptionGroupId={subscriptionGroupId}
				/>
			)}
		</ClayModal>
	);
};

const ActivationStatusDXPCloud = ({
	dxpCloudEnvironment,
	project,
	subscriptionGroupDXPCloud,
	userAccount,
}) => {
	const [projectIdValue, setProjectIdValue] = useState('');
	const [
		subscriptionGroupActivationStatus,
		setSubscriptionGroupActivationStatus,
	] = useState(subscriptionGroupDXPCloud?.activationStatus);
	const [, dispatch] = useAppContext();
	const {client} = useAppPropertiesContext();
	const [hasFinishedUpdate, setHasFinishedUpdate] = useState(false);
	const [activationStatusDate, setActivationStatusDate] = useState('');
	const [visibleSetup, setVisibleSetup] = useState(false);
	const setupModalProps = useModal({
		onClose: () => setVisibleSetup(false),
	});

	const [visibleStatus, setVisibleStatus] = useState(false);
	const activationStatusModalProps = useModal({
		onClose: () => setVisibleStatus(false),
	});
	const projectID = dxpCloudEnvironment?.projectId;

	const onCloseSetupModal = async (isSuccess) => {
		setVisibleSetup(false);

		if (isSuccess) {
			const getSubscriptionGroups = async (accountKey) => {
				const {data: dataSubscriptionGroups} = await client.query({
					query: getAccountSubscriptionGroups,
					variables: {
						filter: `accountKey eq '${accountKey}' and hasActivation eq true`,
					},
				});

				if (dataSubscriptionGroups) {
					const items =
						dataSubscriptionGroups?.c?.accountSubscriptionGroups
							?.items;
					dispatch({
						payload: items,
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
	const currentActivationStatus = {
		[STATUS_TAG_TYPE_NAMES.active]: {
			buttonLink: (
				<>
					<ActivationCardLink
						linkText={i18n.translate('go-to-product-console')}
						url="https://console.liferay.cloud"
					/>
				</>
			),
			id: STATUS_TAG_TYPES.active,
			subtitle: (
				<>
					{i18n.translate('your-liferay-paas')}
					<PopoverIcon />
					{i18n.translate(
						'environments-are-ready-go-to-the-product-console-to-view-liferay-paas-details'
					)}
				</>
			),
			title: i18n.translate('activation-status'),
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
							onClick: () => setVisibleStatus(true),
						},
					]}
					menuElementAttrs={{
						className: 'p-0 cp-activation-key-icon rounded-xs',
					}}
				/>
			),
			id: STATUS_TAG_TYPES.inProgress,
			subtitle: (
				<>
					{i18n.translate('your-liferay-paas')}
					<PopoverIcon />
					{i18n.translate(
						'environments-are-being-set-up-and-will-be-available-soon'
					)}
				</>
			),
			title: i18n.translate('activation-status'),
		},
		[STATUS_TAG_TYPE_NAMES.notActivated]: {
			buttonLink: userAccount.isAccountAdmin && (
				<Button
					appendIcon="order-arrow-right"
					className="btn btn-link font-weight-semi-bold p-0 text-brand-primary text-paragraph"
					displayType="link"
					onClick={() => setVisibleSetup(true)}
				>
					{i18n.translate('finish-activation')}
				</Button>
			),
			id: STATUS_TAG_TYPES.notActivated,
			subtitle: (
				<>
					{i18n.translate('almost-there-setup-liferay-paas')}
					<PopoverIcon />
					{i18n.translate('by-finishing-the-activation-form')}
				</>
			),
			title: i18n.translate('activation-status'),
		},
	};

	const activationStatus =
		currentActivationStatus[
			subscriptionGroupActivationStatus ||
				STATUS_TAG_TYPE_NAMES.notActivated
		];

	useEffect(() => {
		const fetchCommerceOrderItems = async () => {
			const filterAccountSubscriptionERC = `customFields/accountSubscriptionGroupERC eq '${project.accountKey}_liferay-paas'`;
			const {data} = await client.query({
				query: getCommerceOrderItems,
				variables: {
					filter: filterAccountSubscriptionERC,
				},
			});

			if (data) {
				const activationStatusDateRange = getActivationStatusDateRange(
					data?.orderItems?.items
				);
				setActivationStatusDate(activationStatusDateRange);
			}
		};

		fetchCommerceOrderItems();
	}, [client, project]);

	return (
		<>
			{visibleSetup && (
				<SetupDXPCloudModal
					{...setupModalProps}
					dxpVersion={project.dxpVersion}
					listType={LIST_TYPES.dxpMajorVersion}
					onClose={onCloseSetupModal}
					project={project}
					subscriptionGroupId={
						subscriptionGroupDXPCloud?.accountSubscriptionGroupId
					}
				/>
			)}

			<ActivationStatusLayout
				activationStatus={activationStatus}
				activationStatusDate={activationStatusDate}
				iconPath={DXPIcon}
				project={project}
				subscriptionGroupActivationStatus={
					subscriptionGroupActivationStatus
				}
			/>

			{visibleStatus && (
				<ModalDXPCActivationStatus
					{...activationStatusModalProps}
					accountKey={project.accountKey}
					projectID={projectID}
					projectIdValue={projectIdValue}
					setHasFinishedUpdate={setHasFinishedUpdate}
					setProjectIdValue={setProjectIdValue}
					setSubscriptionGroupActivationStatus={
						setSubscriptionGroupActivationStatus
					}
				/>
			)}

			{hasFinishedUpdate && (
				<ClayAlert.ToastContainer>
					<ClayAlert
						autoClose={AUTO_CLOSE_ALERT_TIME.success}
						displayType="success"
						onClose={() => setHasFinishedUpdate(false)}
					>
						{ALERT_UPDATE_DXP_CLOUD_STATUS.success}
					</ClayAlert>
				</ClayAlert.ToastContainer>
			)}
		</>
	);
};

export default ActivationStatusDXPCloud;
