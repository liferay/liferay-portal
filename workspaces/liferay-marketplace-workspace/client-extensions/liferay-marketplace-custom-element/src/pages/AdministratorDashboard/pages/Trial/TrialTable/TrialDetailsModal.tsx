/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLabel from '@clayui/label';
import {Status} from '@clayui/modal/lib/types';
import {formatDistance} from 'date-fns';
import {z} from 'zod';

import QATable from '../../../../../components/QATable';
import {OrderCustomFields} from '../../../../../enums/Order';
import i18n from '../../../../../i18n';
import zodSchema from '../../../../../schema/zod';
import {safeJSONParse} from '../../../../../utils/util';

type TrialDetailsProps = {
	order: Order;
};

export const ORDER_STATUS_LABEL = {
	completed: 'success',
	pending: 'info',
	processing: 'secondary',
};

const getDateOrDefault = (customField: string) =>
	customField
		? formatDistance(new Date(customField), Date.now(), {addSuffix: true})
		: 'N/A';

type TrialFormSchema = z.infer<typeof zodSchema.trialForm>;

const TrialDetails: React.FC<TrialDetailsProps> = ({order}) => {
	const customFields = order?.customFields || {};

	const trialSettings = safeJSONParse<
		Pick<
			TrialFormSchema,
			'consoleInviteEmailAddresses' | 'sendNotificationEmail'
		>
	>(customFields[OrderCustomFields.TRIAL_SETTINGS], {
		consoleInviteEmailAddresses: [],
		sendNotificationEmail: true,
	});

	const trialError = customFields[OrderCustomFields.TRIAL_ERROR];

	return (
		<QATable
			items={[
				{
					title: i18n.translate('order-id'),
					value: order?.id,
				},
				{
					title: i18n.translate('app-name'),
					value: order?.orderItems?.[0].name?.en_US,
				},
				{
					title: i18n.translate('trial-url'),
					value:
						customFields[OrderCustomFields.TRIAL_VIRTUAL_HOST] ||
						'N/A',
				},
				{
					title: i18n.translate('trial-status'),
					value: (
						<ClayLabel
							className="text-nowrap"
							displayType={
								ORDER_STATUS_LABEL[
									order.orderStatusInfo
										?.label as keyof typeof ORDER_STATUS_LABEL
								] as Status
							}
						>
							{order.orderStatusInfo?.label_i18n}
						</ClayLabel>
					),
				},
				{
					title: i18n.translate('created-at'),
					value: getDateOrDefault(order?.createDate as string),
				},
				{
					title: i18n.translate('start-date'),
					value: getDateOrDefault(
						customFields[OrderCustomFields.TRIAL_START_DATE]
					),
				},
				{
					title: i18n.translate('trial-end-date'),
					value: getDateOrDefault(
						customFields[OrderCustomFields.TRIAL_END_DATE]
					),
				},
				{
					title: 'Console Invited Email Addresses',
					value: trialSettings.consoleInviteEmailAddresses?.join(
						', \n'
					),
				},
				{
					title: 'Send Notification Email',
					value: i18n.translate(
						trialSettings.sendNotificationEmail ? 'yes' : 'no'
					),
				},
				{
					title: 'Error',
					value: (
						<span
							className="cursor-pointer text-secondary"
							onClick={() =>
								alert(
									JSON.stringify(
										safeJSONParse(trialError, {}),
										null,
										2
									)
								)
							}
						>
							{i18n.translate('details')}
						</span>
					),
					visible: !!trialError,
				},
			]}
		/>
	);
};

export default TrialDetails;
