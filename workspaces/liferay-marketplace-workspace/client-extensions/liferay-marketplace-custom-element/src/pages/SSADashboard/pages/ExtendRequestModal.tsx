/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import classNames from 'classnames';
import {add} from 'date-fns';
import {ReactElement, useState} from 'react';
import {KeyedMutator} from 'swr';

import {OrderCustomFields, OrderStatus as Status} from '../../../enums/Order';
import i18n from '../../../i18n';
import {Liferay} from '../../../liferay/liferay';
import trialOAuth2 from '../../../services/oauth/Trial';
import HeadlessTrialExtensionRequest from '../../../services/rest/HeadlessTrialExtensionRequest';
import {formatDate} from '../../../utils/date';
import {safeJSONParse} from '../../../utils/util';
import {TRIAL_STATUS_LABEL} from '../constants';
import {ExtendRequestStatus} from '../enums/SSATrials';

type ExtendSSATrialModalProps = {
	mutatePlacedOrderPage: KeyedMutator<any>;
	onClose: () => void;
	order: PlacedOrder;
	ssaTrialExtendMutate: KeyedMutator<any>;
	trialExtend: TrialExtend;
	trialExtendCount: number;
};

type DetailsProps = {
	children?: ReactElement | string;
	title: string;
};

const Details: React.FC<DetailsProps> = ({children, title}) => (
	<div className="d-flex flex-column mb-4">
		<p className="font-weight-bold m-0 text-black-50">{title}</p>
		<div className="d-inline-flex">{children}</div>
	</div>
);

const ExtendRequestModal: React.FC<ExtendSSATrialModalProps> = ({
	mutatePlacedOrderPage,
	onClose,
	order,
	ssaTrialExtendMutate,
	trialExtend,
	trialExtendCount,
}) => {
	const [submitting, setSubmitting] = useState<null | 'approve' | 'reject'>(
		null
	);

	const mutateTrialExtendRequest = (status: ExtendRequestStatus) => {
		ssaTrialExtendMutate(
			(data: any) => {
				const updatedItems = data.items.map((item: TrialExtend) => {
					if (item.id === trialExtend.id) {
						return {
							...item,
							dueStatus: {
								key: status,
							},
						};
					}

					return item;
				});

				return {
					...data,
					items: updatedItems,
				};
			},
			{revalidate: true}
		);
	};

	return (
		<>
			<div className="d-flex flex-column mb-9 provisioning-details">
				<div className="align-items-center d-flex justify-content-between">
					<span className="font-weight-bold text-primary">
						{i18n.translate('extension-request').toUpperCase()}
					</span>

					<span>
						<ClayButtonWithIcon
							aria-label="Close"
							borderless
							className="text-dark"
							onClick={onClose}
							symbol="times"
							title="Close"
						/>
					</span>
				</div>

				<div className="d-flex flex-column justify-content-start">
					<h2 className="m-0 text-weight-bold">
						{
							safeJSONParse(
								order.customFields[
									OrderCustomFields.TRIAL_SETTINGS
								],
								{projectId: 'N/A'}
							).projectId
						}
					</h2>
					<p>{order.orderTypeExternalReferenceCode}</p>
				</div>

				<div className="d-flex flex-row mt-5">
					<div className="col-6 p-0">
						<p className="font-weight-bold">
							{i18n.translate('details')}
						</p>

						<Details title={i18n.translate('start-date')}>
							<span className="extend-request-info">
								{formatDate(order.createDate)}
							</span>
						</Details>

						<Details title={i18n.translate('expiration-date')}>
							<span className="extend-request-info">
								{formatDate(
									order.customFields[
										OrderCustomFields.TRIAL_END_DATE
									],
									'DNE'
								)}
							</span>
						</Details>

						<Details title={i18n.translate('status')}>
							<span
								className={classNames('extension-status', {
									'extension-status-approved': [
										Status.IN_PROGRESS,
										Status.PROCESSING,
									].includes(
										order.orderStatusInfo.label as Status
									),
									'extension-status-expired': [
										Status.COMPLETED,
										Status.APPROVED,
									].includes(
										order.orderStatusInfo.label as Status
									),
									'extension-status-pending':
										order.orderStatusInfo.label ===
										Status.PENDING,
								})}
							>
								{
									TRIAL_STATUS_LABEL[
										order.orderStatusInfo
											.label as keyof typeof TRIAL_STATUS_LABEL
									]
								}
							</span>
						</Details>
					</div>

					<div className="col-6 p-0">
						<p className="font-weight-bold">
							{i18n.translate('extension')}
						</p>

						<Details
							title={i18n.translate('times-already-extended')}
						>
							<span className="extend-request-info">
								{trialExtendCount.toString()}
							</span>
						</Details>

						<Details
							title={i18n.translate('duration-of-the-extension')}
						>
							<span className="extend-request-info">
								{trialExtend.duration.toString()}
							</span>
						</Details>

						<Details
							title={i18n.translate(
								'new-potential-expiration-date'
							)}
						>
							<span className="extend-request-info">
								{formatDate(
									add(
										new Date(
											order.customFields[
												OrderCustomFields.TRIAL_END_DATE
											]
										),
										{days: trialExtend.duration}
									),
									'DNE'
								)}
							</span>
						</Details>
					</div>
				</div>
				<div className="d-flex flex-row mb-7">
					<div className="d-flex flex-column flex-grow-1 p-0">
						<p className="font-weight-bold m-0 text-black-50">
							{i18n.translate('reason')}
						</p>
						<p className="extend-request-info extend-request-reason">
							{trialExtend.reason}
						</p>
					</div>
				</div>
			</div>

			<div className="d-flex justify-content-end pt-8">
				<ClayButton
					className="mr-4"
					disabled={!!submitting}
					displayType="secondary"
					onClick={async () => {
						setSubmitting('reject');

						try {
							await HeadlessTrialExtensionRequest.updateTrialExtensionRequest(
								trialExtend.id,
								{dueStatus: {key: ExtendRequestStatus.REJECTED}}
							);

							mutateTrialExtendRequest(
								ExtendRequestStatus.REJECTED
							);

							setSubmitting(null);

							Liferay.Util.openToast({
								message: i18n.translate(
									'trial-extension-rejected-successfully'
								),
								title: i18n.translate('success'),
								type: 'success',
							});
						}
						catch (error) {
							console.error(error);

							Liferay.Util.openToast({
								message: i18n.translate(
									'failed-to-reject-trial-extension'
								),
								title: i18n.translate('failure'),
								type: 'danger',
							});
						}

						onClose();
					}}
				>
					<div className="align-items-center d-flex">
						{submitting === 'reject' && (
							<ClayLoadingIndicator className="mr-3 my-0" />
						)}
						{i18n.translate('reject-request')}
					</div>
				</ClayButton>

				<ClayButton
					disabled={!!submitting}
					onClick={async () => {
						setSubmitting('approve');

						try {
							await HeadlessTrialExtensionRequest.updateTrialExtensionRequest(
								trialExtend.id,
								{dueStatus: {key: ExtendRequestStatus.APPROVED}}
							);

							mutateTrialExtendRequest(
								ExtendRequestStatus.APPROVED
							);

							await trialOAuth2.extendTrial(trialExtend.id);

							mutatePlacedOrderPage((response: any) => response, {
								revalidate: true,
							});

							setSubmitting(null);

							Liferay.Util.openToast({
								message: i18n.translate(
									'trial-extension-approved-successfully'
								),
								title: i18n.translate('success'),
								type: 'success',
							});
						}
						catch (error) {
							console.error(error);

							Liferay.Util.openToast({
								message: i18n.translate(
									'failed-to-approve-trial-extension'
								),
								title: i18n.translate('failure'),
								type: 'danger',
							});
						}

						onClose();
					}}
				>
					<div className="align-items-center d-flex">
						{submitting === 'approve' && (
							<ClayLoadingIndicator className="mr-3 my-0" />
						)}

						{i18n.translate('approve-request')}
					</div>
				</ClayButton>
			</div>
		</>
	);
};

export default ExtendRequestModal;
