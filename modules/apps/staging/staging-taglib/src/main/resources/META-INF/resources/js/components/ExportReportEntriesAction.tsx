/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayModal, {useModal} from '@clayui/modal';
import React from 'react';

import {ExportReportEntriesModal} from './ExportReportEntriesModal';

export function ExportReportEntriesAction({
	backgroundTaskId,
	filename,
}: {
	backgroundTaskId: string;
	filename: string;
}) {
	const {observer, onOpenChange, open} = useModal();

	return (
		<>
			<ClayButton
				className="dropdown-item"
				displayType="unstyled"
				onClick={() => onOpenChange(true)}
				role="menuitem"
			>
				{Liferay.Language.get('export-report-entries')}
			</ClayButton>

			{open && (
				<ClayModal disableAutoClose observer={observer}>
					<ExportReportEntriesModal
						closeModal={() => onOpenChange(false)}
						filename={filename}
						importProcessId={backgroundTaskId}
					/>
				</ClayModal>
			)}
		</>
	);
}
