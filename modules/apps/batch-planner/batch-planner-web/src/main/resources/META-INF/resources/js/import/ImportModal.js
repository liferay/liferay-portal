/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayForm from '@clayui/form';
import ClayLabel from '@clayui/label';
import ClayModal, {useModal} from '@clayui/modal';
import ClayProgressBar from '@clayui/progress-bar';
import PropTypes from 'prop-types';
import React from 'react';

import {fetchErrorReportFile, importStatus} from '../BatchPlannerImport';
import Poller from '../Poller';

const ImportModal = ({closeModal, formDataQuerySelector, formImportURL}) => {
	const {
		downloadFile,
		errorMessage,
		externalReferenceCode,
		loading,
		percentage,
		ready,
	} = Poller(
		formDataQuerySelector,
		formImportURL,
		importStatus,
		fetchErrorReportFile
	);
	const {observer, onClose} = useModal({
		onClose: () => {
			closeModal();
		},
	});

	let modalStatus;
	let title;
	let labelType;
	let label;

	if (ready) {
		modalStatus = 'success';
		title = Liferay.Language.get(
			'the-import-process-completed-successfully'
		);
		labelType = 'success';
		label = Liferay.Language.get('completed');
	}
	else if (errorMessage) {
		modalStatus = 'danger';
		title = errorMessage;
		labelType = 'danger';
		label = Liferay.Language.get('failed');
	}
	else {
		modalStatus = 'info';
		title = Liferay.Language.get(
			'data-is-importing.-you-can-safely-close-the-dialog'
		);
		labelType = 'warning';
		label = Liferay.Language.get('running');
	}

	return (
		<ClayModal observer={observer} size="md" status={modalStatus}>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{Liferay.Language.get('import-file')}
			</ClayModal.Header>

			<ClayModal.Body>
				<ClayForm.Group>
					<ClayForm.FeedbackGroup>
						<ClayForm.FeedbackItem>{title}</ClayForm.FeedbackItem>

						<ClayLabel displayType={labelType}>{label}</ClayLabel>
					</ClayForm.FeedbackGroup>

					<ClayProgressBar value={percentage} warn={!!errorMessage} />
				</ClayForm.Group>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton displayType="secondary" onClick={onClose}>
							{Liferay.Language.get('close')}
						</ClayButton>

						{modalStatus === 'danger' &&
							!!externalReferenceCode && (
								<ClayButton
									disabled={loading}
									displayType="danger"
									onClick={downloadFile}
									type="submit"
								>
									{Liferay.Language.get(
										'download-error-report'
									)}
								</ClayButton>
							)}
					</ClayButton.Group>
				}
			/>
		</ClayModal>
	);
};

ImportModal.propTypes = {
	formImportURL: PropTypes.string.isRequired,
};

export default ImportModal;
