/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import ClayModal, {useModal} from '@clayui/modal';

import i18n from '../../../../i18n';

import './DeactivateKeysModal.scss';

import ClayIcon from '@clayui/icon';
import {useState} from 'react';

type DeactivateKeysModalProps = ReturnType<typeof useModal> & {
	onConfirm: () => Promise<void>;
};

const DeactivateKeysModal: React.FC<DeactivateKeysModalProps> = ({
	observer,
	onConfirm,
	onOpenChange,
}) => {
	const [deactivationFailed, setDeactivationFailed] = useState(false);
	const [isDeactivating, setDeactivating] = useState(false);

	const onDeactivateClick = async () => {
		setDeactivating(true);

		await onConfirm().catch(() => setDeactivationFailed(true));

		setDeactivating(false);
	};

	return (
		<ClayModal center observer={observer}>
			<div className="deactivate-key-modal-container pt-4 px-4">
				<div className="flex-row mb-1">
					<div className="d-flex justify-content-between">
						<h2 className="deactivate-key-modal-h2">
							{i18n.translate('confirm-deactivation-terms')}
						</h2>

						<ClayButtonWithIcon
							aria-labelledby="close icon"
							className="align-self-start"
							displayType="unstyled"
							onClick={() => onOpenChange(false)}
							symbol="times"
						/>
					</div>

					<p className="mb-2 mt-5">
						{i18n.translate(
							'i-certify-that-the-instances-activated-with-the-selected-activation-keys-have-been-shut-down-and-that-there-is-no-liferay-software-installed-deployed-used-or-executed-that-is-activated-with-the-selected-activation-key'
						)}
					</p>

					<p className="mb-6 mt-5">
						{i18n.translate(
							'a-request-will-be-sent-to-deactivate-the-selected-activation-key-from-now-on-it-will-be-hidden-and-no-longer-visible'
						)}
					</p>
				</div>

				<div className="d-flex justify-content-end my-4">
					<ClayButton
						className="deactivate-key-modal-cancel-btn mr-3"
						displayType="secondary"
						onClick={() => onOpenChange(false)}
					>
						{i18n.translate('cancel')}
					</ClayButton>

					<ClayButton
						className="deactivate-key-modal-confirm-btn"
						disabled={isDeactivating}
						onClick={onDeactivateClick}
					>
						{i18n.translate('confirm-deactivate-keys')}
					</ClayButton>
				</div>
			</div>

			{deactivationFailed && (
				<div className="d-flex deactivate-key-modal-error-alert px-4 py-4">
					<ClayIcon
						className="mr-2 mt-1 text-danger"
						symbol="warning-full"
					/>

					<p className="m-0 text-danger text-paragraph">
						{i18n.translate(
							'there-was-an-unexpected-error-while-attempting-to-deactivate-the-key-please-try-again-in-a-few-moments'
						)}
					</p>
				</div>
			)}
		</ClayModal>
	);
};

export default DeactivateKeysModal;
