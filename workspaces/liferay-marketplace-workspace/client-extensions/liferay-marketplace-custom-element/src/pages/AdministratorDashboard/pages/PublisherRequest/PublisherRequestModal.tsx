/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {useModal} from '@clayui/modal';
import {Status} from '@clayui/modal/lib/types';
import {KeyedMutator} from 'swr';

import Modal from '../../../../components/Modal';
import i18n from '../../../../i18n';
import {Liferay} from '../../../../liferay/liferay';
import fetcher from '../../../../services/fetcher';
import PublisherSummaryContent from '../../../PublisherGate/components/PublisherSummaryContent';

export const PublisherStatusDisplayType = {
	completed: 'success',
	inProgress: 'info',
	open: 'secondary',
	rejected: 'danger',
};

type PublisherRequestModalProps = ReturnType<typeof useModal> & {
	mutate: KeyedMutator<APIResponse>;
	selectedRequest?: PublisherRequestInfo;
};

const PublisherRequestModal: React.FC<PublisherRequestModalProps> = ({
	mutate,
	observer,
	onOpenChange,
	open,
	selectedRequest,
}) => {
	const showModalButtons =
		(selectedRequest?.requestStatus?.key ?? 'open') === 'open';

	const selectedRequestStatus = PublisherStatusDisplayType[
		selectedRequest?.requestStatus
			?.key as keyof typeof PublisherStatusDisplayType
	] as Status;

	const onUpdateRequestStatus = async (status: 'completed' | 'rejected') => {
		await fetcher.patch(
			`o/c/requestpublisheraccounts/${selectedRequest?.id}`,
			{
				requestStatus: status,
			}
		);

		mutate((items) => items, {revalidate: true});

		Liferay.Util.openToast({
			message: i18n.translate('your-request-completed-successfully'),
			type: 'success',
		});

		onOpenChange(false);
	};

	return (
		<Modal
			last={
				showModalButtons ? (
					<div>
						<ClayButton
							className="mr-3"
							displayType="danger"
							onClick={() => onUpdateRequestStatus('rejected')}
						>
							{i18n.translate('decline')}
						</ClayButton>

						<ClayButton
							displayType="primary"
							onClick={() => onUpdateRequestStatus('completed')}
						>
							{i18n.translate('approve')}
						</ClayButton>
					</div>
				) : undefined
			}
			observer={observer}
			size="lg"
			status={selectedRequestStatus}
			title={`Review Request - ${
				selectedRequest?.requestStatus?.name ?? 'Open'
			}`}
			visible={open}
		>
			<PublisherSummaryContent
				userInfo={
					{
						...selectedRequest,
						publisherType: Array.isArray(
							selectedRequest?.publisherType
						)
							? (selectedRequest?.publisherType as any[])?.map(
									({name}) => name
								)
							: ['App Publisher'],
					} as any
				}
			/>
		</Modal>
	);
};

export default PublisherRequestModal;
