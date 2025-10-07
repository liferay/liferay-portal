/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Button as ClayButton} from '@clayui/core';
import ClayModal from '@clayui/modal';
import {Observer} from '@clayui/modal/lib/types';
import Button from '~/components/Button';
import i18n from '~/utils/I18n';

import './BusinessEventsModal.css';

import classNames from 'classnames';

interface IBusinessEventsModalProps {
	baseButtonDisabled?: boolean;
	children: React.ReactNode;
	handleSubmit: () => void;
	headerTitle: string;
	isLoadingSubmitButton?: boolean;
	modalType: string;
	observer: Observer;
	onClose: () => void;
	reason?: string;
	submitButton: string;
	title: string;
}

const BusinessEventsModal = ({
	baseButtonDisabled,
	children,
	handleSubmit,
	headerTitle,
	isLoadingSubmitButton,
	modalType,
	observer,
	onClose,
	reason,
	submitButton,
	title,
}: IBusinessEventsModalProps) => {
	const isCancelModal = modalType === 'cancelEvent';

	const handleDisabled = () => {
		if (isCancelModal) {
			return !reason?.trim() || isLoadingSubmitButton;
		}

		if (modalType === 'editEvent') {
			return !reason?.trim() || isLoadingSubmitButton;
		}

		if (modalType === 'goLiveEvent') {
			return baseButtonDisabled || isLoadingSubmitButton;
		}

		return false;
	};

	return (
		<ClayModal center observer={observer} size="lg">
			<div className="p-4">
				<div className="pb-4">
					<div className="font-weight-bold header-title mb-1">
						{headerTitle.toUpperCase()}
					</div>

					<div className="d-flex justify-content-between">
						<h3>{title}</h3>

						<Button
							appendIcon="times"
							aria-label="close"
							className="align-self-start edit-modal-close"
							displayType="unstyled"
							onClick={onClose}
						/>
					</div>
				</div>

				<div className="pb-3">{children}</div>

				<div>
					<div
						className={classNames('d-flex justify-content-end', {
							'cancel-event-modal': isCancelModal,
						})}
					>
						<ClayButton
							className="mr-3"
							displayType="secondary"
							onClick={onClose}
						>
							{i18n.translate('cancel')}
						</ClayButton>

						<ClayButton
							disabled={handleDisabled()}
							displayType="primary"
							onClick={handleSubmit}
						>
							{submitButton}
						</ClayButton>
					</div>
				</div>
			</div>
		</ClayModal>
	);
};

export default BusinessEventsModal;
