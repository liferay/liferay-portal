/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import {zodResolver} from '@hookform/resolvers/zod';
import {useState} from 'react';
import {useForm} from 'react-hook-form';
import {KeyedMutator} from 'swr';

import FormInput from '../../../components/Input/formInput';
import {OrderCustomFields} from '../../../enums/Order';
import i18n from '../../../i18n';
import {Liferay} from '../../../liferay/liferay';
import zodSchema, {z} from '../../../schema/zod';
import trialOAuth2 from '../../../services/oauth/Trial';
import HeadlessTrialExtensionRequest from '../../../services/rest/HeadlessTrialExtensionRequest';
import {EXTEND_OPTIONS, EXTEND_TYPES} from '../constants';
import {ExtendRequestStatus} from '../enums/SSATrials';

type ExtendSSATrialModalProps = {
	accountId: number;
	firstExtendRequest: boolean;
	mutatePlacedOrder?: KeyedMutator<any>;
	onClose: () => void;
	order: PlacedOrder;
	orderMutate: KeyedMutator<any>;
	ssaTrialExtendMutate: KeyedMutator<any>;
};

const ExtendSSATrialModal: React.FC<ExtendSSATrialModalProps> = ({
	accountId,
	firstExtendRequest,
	mutatePlacedOrder,
	onClose,
	order,
	orderMutate,
	ssaTrialExtendMutate,
}) => {
	const [submitting, setSubmitting] = useState<boolean>(false);

	const {
		formState: {errors, isLoading},
		handleSubmit,
		register,
	} = useForm({
		defaultValues: {
			duration: '' as unknown as number,
			reason: '',
		},
		mode: 'onSubmit',
		resolver: zodResolver(zodSchema.extendSSATrial),
	});

	const inputProps = {
		errors,
		register,
		required: true,
	};

	const extendType = firstExtendRequest
		? EXTEND_TYPES.AUTO_EXTEND
		: EXTEND_TYPES.ADMIN_REQUEST;

	const extendOptions = EXTEND_OPTIONS.find(
		(option) => option.extendType === extendType
	);

	const onSubmit = async (form: z.infer<typeof zodSchema.extendSSATrial>) => {
		setSubmitting(true);

		const trialSettings =
			order.customFields?.[OrderCustomFields.TRIAL_SETTINGS];
		const projectId = JSON.parse(trialSettings)?.projectId;

		try {
			const extendTrial = {
				dueStatus: {
					key:
						extendType === EXTEND_TYPES.AUTO_EXTEND
							? ExtendRequestStatus.AUTO_APPROVED
							: ExtendRequestStatus.PENDING,
				},
				duration: form.duration,
				projectId,
				r_accountToTrialExtensionRequest_accountEntryId: accountId,
				r_orderToTrialExtensionRequest_commerceOrderId: order.id,
				reason: form.reason,
			};

			const newExtensionRequest: TrialExtend =
				await HeadlessTrialExtensionRequest.createTrialExtensionRequest(
					extendTrial
				);

			if (extendType === EXTEND_TYPES.AUTO_EXTEND) {
				await trialOAuth2.extendTrial(newExtensionRequest.id);
			}

			ssaTrialExtendMutate(
				(data: any) => {
					return {
						...data,
						items: [newExtensionRequest, ...data.items],
					};
				},
				{revalidate: false}
			);

			if (extendType === EXTEND_TYPES.AUTO_EXTEND) {
				if (mutatePlacedOrder) {
					mutatePlacedOrder((response: any) => response, {
						revalidate: true,
					});
				}

				orderMutate((response: any) => response, {
					revalidate: true,
				});
			}

			Liferay.Util.openToast({
				message: i18n.translate('trial-extended-successfully'),
				title: i18n.translate('success'),
				type: 'success',
			});

			setSubmitting(false);

			onClose();
		}
		catch (error) {
			console.error(error);

			Liferay.Util.openToast({
				message: i18n.translate('failed-to-extend-trial'),
				title: i18n.translate('failure'),
				type: 'danger',
			});
			setSubmitting(false);
		}
	};

	return (
		<div>
			<ClayAlert displayType={extendOptions?.alertType}>
				{extendOptions?.alertText}
			</ClayAlert>

			<FormInput
				{...inputProps}
				boldLabel
				label="Duration"
				name="duration"
				placeholder="Value between 1 and 60"
				required={true}
				type="number"
			/>
			<FormInput
				{...inputProps}
				boldLabel
				label={i18n.translate('reason')}
				name="reason"
				placeholder="Tell why you need to extend the trial"
				required={true}
				type="textarea"
			/>
			<div className="d-flex justify-content-end">
				<ClayButton
					className="mr-4"
					disabled={!!submitting}
					displayType="secondary"
					onClick={onClose}
				>
					{i18n.translate('cancel')}
				</ClayButton>
				<ClayButton
					disabled={isLoading || submitting}
					onClick={handleSubmit(onSubmit)}
				>
					<div className="align-items-center d-flex">
						{submitting && (
							<ClayLoadingIndicator className="mr-3 my-0" />
						)}
						{extendOptions?.actionText}
					</div>
				</ClayButton>
			</div>
		</div>
	);
};

export default ExtendSSATrialModal;
