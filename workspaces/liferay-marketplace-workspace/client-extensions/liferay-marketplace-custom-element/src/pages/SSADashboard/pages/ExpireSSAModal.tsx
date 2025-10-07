/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import {useState} from 'react';
import {KeyedMutator} from 'swr';

import i18n from '../../../i18n';
import {Liferay} from '../../../liferay/liferay';
import trialOAuth2 from '../../../services/oauth/Trial';

type ExpireSSAModalProps = {
	accountId: number;
	mutate: KeyedMutator<Order | PlacedOrder>;
	onClose: () => void;
	order: Order | PlacedOrder;
};

const ExpireSSAModal: React.FC<ExpireSSAModalProps> = ({
	mutate,
	onClose,
	order,
}) => {
	const [isSubmitting, setIsSubmitting] = useState(false);

	return (
		<div>
			<ClayAlert displayType="warning" role={null}>
				{i18n.translate('this-action-cannot-be-undone')}
			</ClayAlert>

			<p>
				{i18n.translate(
					'are-you-sure-you-want-to-expire-this-trial-this-action-implies-the-permanent-end-of-the-test-environment'
				)}
			</p>

			<div className="d-flex justify-content-end" key="footer-buttons">
				<ClayButton
					aria-label="cancel"
					disabled={isSubmitting}
					displayType="secondary"
					key={0}
					onClick={onClose}
				>
					{i18n.translate('cancel')}
				</ClayButton>

				<ClayButton
					aria-label="close"
					className="ml-4"
					disabled={isSubmitting}
					displayType="warning"
					key={2}
					onClick={async () => {
						setIsSubmitting(true);
						try {
							await trialOAuth2.expireTrial(order.id);

							mutate((orders) => orders);

							Liferay.Util.openToast({
								message: i18n.translate(
									'trial-expired-successfully'
								),
								type: 'success',
							});

							setIsSubmitting(false);
						}
						catch {
							Liferay.Util.openToast({
								message: i18n.translate(
									'failed-to-expire-the-trial'
								),
								type: 'danger',
							});

							setIsSubmitting(false);
						}

						onClose();
					}}
				>
					<div className="align-items-center d-flex">
						{isSubmitting && (
							<ClayLoadingIndicator className="mr-3 my-0" />
						)}
						{i18n.translate('expire')}
					</div>
				</ClayButton>
			</div>
		</div>
	);
};

export default ExpireSSAModal;
