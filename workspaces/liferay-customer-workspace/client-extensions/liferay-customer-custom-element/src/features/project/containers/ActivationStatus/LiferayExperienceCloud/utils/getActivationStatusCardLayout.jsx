/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ButtonWithIcon} from '@clayui/core';
import {Align} from '@clayui/drop-down';

import i18n from '~/utils/I18n';
import {Button, ButtonDropDown} from '~/components';

import {
	STATUS_TAG_TYPES,
	STATUS_TAG_TYPE_NAMES,
} from '~/features/project/utils/constants';
import ActivationCardLink from '../../ActivationCardLink';
import {useGetAccountSubscriptions} from '~/services/liferay/graphql/account-subscriptions';

const ACCEPTED_SUBSCRIPTIONS = ['Business Plan', 'Enterprise Plan'];

const formatedSubscriptions = () =>
	[...ACCEPTED_SUBSCRIPTIONS]
		.map((projectName) => `'${projectName}'`)
		.join(',');

export default function getActivationStatusCardLayout(
	lxcEnvironment,
	project,
	onNotActivatedClick,
	onInProgressClick,
	userAccount
) {
	const {data: subscriptionsData} = useGetAccountSubscriptions({
		filter: `name in (${formatedSubscriptions()}) and accountSubscriptionGroupERC eq '${
			project?.accountKey
		}_liferay-saas'`,
	});

	const hasDevInstance =
		!!subscriptionsData?.c.accountSubscriptions.items.length;

	return {
		[STATUS_TAG_TYPE_NAMES.active]: {
			buttonLink: (
				<div className="d-flex flex-column">
					{lxcEnvironment?.projectId && (
						<ActivationCardLink
							linkText={i18n.translate('go-to-liferay-saas')}
							url={`https://${lxcEnvironment?.projectId}.lxc.liferay.com`}
						/>
					)}

					{lxcEnvironment?.projectId && (
						<ActivationCardLink
							linkText={i18n.translate('go-to-uat')}
							url={`https://${lxcEnvironment?.projectId}-uat.lxc.liferay.com`}
						/>
					)}

					{hasDevInstance && lxcEnvironment?.projectId && (
						<ActivationCardLink
							linkText={i18n.translate('go-to-dev')}
							url={`https://${lxcEnvironment?.projectId}-dev.lxc.liferay.com`}
						/>
					)}

					{project?.acWorkspaceGroupId && (
						<ActivationCardLink
							linkText={i18n.translate(
								'go-to-analytics-cloud-workspace'
							)}
							url={`https://analytics.liferay.com/workspace/${project.acWorkspaceGroupId}/sites`}
						/>
					)}
				</div>
			),
			id: STATUS_TAG_TYPES.active,
			subtitle: i18n.translate(
				'your-liferay-saas-project-is-set-up-if-you-need-to-submit-additional-workspaces-please-create-a-support-ticket'
			),
			title: i18n.translate('liferay-saas-activation'),
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
							onClick: () => onInProgressClick(),
						},
					]}
					menuElementAttrs={{
						className: 'p-0 cp-activation-key-icon rounded-xs',
					}}
				/>
			),
			id: STATUS_TAG_TYPES.inProgress,
			subtitle: i18n.translate(
				'your-liferay-saas-project-is-being-set-up-and-will-be-available-soon'
			),
			title: i18n.translate('liferay-saas-activation'),
		},
		[STATUS_TAG_TYPE_NAMES.notActivated]: {
			buttonLink: userAccount.isAccountAdmin && (
				<Button
					appendIcon="order-arrow-right"
					className="btn btn-link font-weight-semi-bold p-0 text-brand-primary text-paragraph"
					displayType="link"
					onClick={() => onNotActivatedClick()}
				>
					{i18n.translate('finish-activation')}
				</Button>
			),
			id: STATUS_TAG_TYPES.notActivated,
			subtitle: i18n.translate(
				'almost-there-setup-liferay-saas-by-finishing-the-activation-form'
			),
			title: i18n.translate('liferay-saas-activation'),
		},
	};
}
