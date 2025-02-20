/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {useModal} from '@clayui/modal';
import {openToast} from 'frontend-js-components-web';
import PropTypes from 'prop-types';
import React, {useCallback, useEffect, useState} from 'react';

import {
	DISALLOWED_CSV_ENTITY_TYPES,
	EXPORT_FILE_FORMAT_SELECTED_EVENT,
	SCHEMA_SELECTED_EVENT,
} from '../constants';
import ExportModal from './ExportModal';

function Export({
	formExportDataQuerySelector,
	formExportURL,
	portletNamespace,
}) {
	const [disable, setDisable] = useState(true);
	const [visible, setVisible] = useState(false);
	const {observer, onClose} = useModal({
		onClose: () => setVisible(false),
	});

	const onButtonClick = useCallback(
		(event) => {
			event.preventDefault();

			const isFieldChecked = document.querySelectorAll(
				'#fieldsTableBody input[type=checkbox]:checked'
			);

			if (!isFieldChecked?.length) {
				openToast({
					message: Liferay.Language.get(
						'please-select-at-least-one-field'
					),
					type: 'danger',
				});

				return;
			}

			setVisible(true);
		},
		[setVisible]
	);

	useEffect(() => {
		const handleExportFileFormatUpdated = ({
			selectedExportFileFormat,
			selectedSchema,
		}) => {
			if (
				selectedExportFileFormat === 'CSV' &&
				DISALLOWED_CSV_ENTITY_TYPES.includes(selectedSchema)
			) {
				setDisable(true);
			}
		};

		function handleSchemaChange(event) {
			if (event.schema) {
				setDisable(false);
			}
		}

		Liferay.on(
			EXPORT_FILE_FORMAT_SELECTED_EVENT,
			handleExportFileFormatUpdated
		);
		Liferay.on(SCHEMA_SELECTED_EVENT, handleSchemaChange);

		return () => {
			Liferay.detach(
				EXPORT_FILE_FORMAT_SELECTED_EVENT,
				handleExportFileFormatUpdated
			);
			Liferay.detach(SCHEMA_SELECTED_EVENT, handleSchemaChange);
		};
	}, [portletNamespace]);

	return (
		<>
			<ClayButton
				disabled={disable}
				displayType="primary"
				id={`${portletNamespace}saveTemplate`}
				onClick={onButtonClick}
				type="submit"
			>
				{Liferay.Language.get('export')}
			</ClayButton>

			{visible && (
				<ExportModal
					closeModal={onClose}
					formDataQuerySelector={formExportDataQuerySelector}
					formSubmitURL={formExportURL}
					namespace={portletNamespace}
					observer={observer}
				/>
			)}
		</>
	);
}

Export.propTypes = {
	formExportDataQuerySelector: PropTypes.string.isRequired,
	formExportURL: PropTypes.string.isRequired,
	portletNamespace: PropTypes.string.isRequired,
};

export default Export;
