/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayModal from '@clayui/modal';
import React from 'react';

import ExportFormModalBody from './ExportFormModalBody';

const ExportFormModal: React.FC<
	{children?: React.ReactNode | undefined} & IProps
> = ({
	csvExport,
	exportFormURL,
	fileExtensions,
	observer,
	onClose,
	portletNamespace,
}) => {
	return (
		<ClayModal observer={observer}>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{Liferay.Language.get('export')}
			</ClayModal.Header>

			<ClayModal.Body>
				<form
					action={exportFormURL}
					id="exportFormEntriesForm"
					method="post"
				>
					<ExportFormModalBody
						csvExport={csvExport}
						fileExtensions={fileExtensions}
						portletNamespace={portletNamespace}
					/>
				</form>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton displayType="secondary" onClick={onClose}>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							displayType="primary"
							form="exportFormEntriesForm"
							onClick={onClose}
							type="submit"
						>
							{Liferay.Language.get('ok')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</ClayModal>
	);
};

interface IProps {
	csvExport: string;
	exportFormURL: string;
	fileExtensions: string[];
	observer: any;
	onClose: () => void;
	portletNamespace: string;
}

export default ExportFormModal;
