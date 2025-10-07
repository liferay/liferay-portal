/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayInput} from '@clayui/form';
import {Observer} from '@clayui/modal/lib/types';
import i18n from '~/utils/I18n';

import BusinessEventsModal from '../../../../../components/BusinessEventsModal/BusinessEventsModal';

import './BusinessEventsConfirmationPage.css';

interface IBusinessEventsConfirmationPageProps {
	handleSubmit: () => void;
	headerTitle: string;
	isLoadingSubmitButton?: boolean;
	message: string;
	observer: Observer;
	onClose: () => void;
	reason: string;
	setReason: React.Dispatch<React.SetStateAction<string>>;
}

const BusinessEventsConfirmationPage = ({
	handleSubmit,
	headerTitle,
	isLoadingSubmitButton,
	message,
	observer,
	onClose,
	reason,
	setReason,
}: IBusinessEventsConfirmationPageProps) => {
	const handleInputChange = (event: {target: {value: string}}) => {
		setReason(event.target.value);
	};

	return (
		<BusinessEventsModal
			handleSubmit={handleSubmit}
			headerTitle={headerTitle}
			isLoadingSubmitButton={isLoadingSubmitButton}
			modalType="editEvent"
			observer={observer}
			onClose={onClose}
			reason={reason}
			submitButton={i18n.translate('save-changes')}
			title={i18n.translate('change-target-go-live')}
		>
			<p className="mb-3">{message}</p>

			<div>
				<div className="font-weight-bold pb-2">
					{i18n.translate('reason-for-change')}

					<span className="edit-modal-asterisk"> *</span>
				</div>

				<ClayInput
					component="textarea"
					onChange={handleInputChange}
					required
					type="text"
					value={reason}
				/>
			</div>
		</BusinessEventsModal>
	);
};

export default BusinessEventsConfirmationPage;
