/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayModal from '@clayui/modal';
import PropTypes from 'prop-types';
import React from 'react';

import {exportStatus, fetchExportedFile} from '../BatchPlannerExport';
import Poller from '../Poller';
import ExportModalBody from './ExportModalBody';

const ExportModal = ({
	closeModal,
	formDataQuerySelector,
	formSubmitURL,
	observer,
}) => {
	const {downloadFile, errorMessage, loading, percentage, ready} = Poller(
		formDataQuerySelector,
		formSubmitURL,
		exportStatus,
		fetchExportedFile
	);

	let modalStatus;

	if (ready) {
		modalStatus = 'success';
	}
	else if (errorMessage) {
		modalStatus = 'danger';
	}
	else {
		modalStatus = 'info';
	}

	return (
		<ClayModal observer={observer} size="md" status={modalStatus}>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{Liferay.Language.get('export-file')}
			</ClayModal.Header>

			<ExportModalBody
				errorMessage={errorMessage}
				percentage={percentage}
				readyToDownload={ready}
			/>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							disabled={loading}
							displayType={null}
							onClick={closeModal}
						>
							{Liferay.Language.get('back-to-the-list')}
						</ClayButton>

						<ClayButton
							disabled={!ready}
							displayType={modalStatus}
							onClick={downloadFile}
						>
							{loading && (
								<span className="inline-item inline-item-before">
									<span
										aria-hidden="true"
										className="loading-animation"
									></span>
								</span>
							)}

							{Liferay.Language.get('download')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</ClayModal>
	);
};

ExportModal.propTypes = {
	closeModal: PropTypes.func.isRequired,
	formDataQuerySelector: PropTypes.string.isRequired,
	formSubmitURL: PropTypes.string.isRequired,
	namespace: PropTypes.string.isRequired,
	observer: PropTypes.object.isRequired,
};

export default ExportModal;
