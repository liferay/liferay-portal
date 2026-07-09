/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLayout from '@clayui/layout';
import React from 'react';

import {
	FormikFieldCheckbox,
	FormikFieldContentSelector,
} from '../../../components/forms/formik';
import {ImportPreview} from '../../../types/exportImportPreview';
import FileSummary from './FileSummary';

export default function DataSelectionStep({
	commentsAndRatingsEnabled = false,
	importPreview,
	lookAndFeelEnabled = false,
}: {
	commentsAndRatingsEnabled?: boolean;
	importPreview?: ImportPreview;
	lookAndFeelEnabled?: boolean;
}) {
	if (!importPreview) {
		return null;
	}

	return (
		<>
			<FileSummary importPreview={importPreview} />

			<ClayLayout.Sheet className="mt-4 option-group">
				<FormikFieldCheckbox
					description={Liferay.Language.get(
						'export-import-permissions-help'
					)}
					label={Liferay.Language.get('import-permissions')}
					name="permissions"
				/>

				{importPreview.deletionCount > 0 && (
					<FormikFieldCheckbox
						description={Liferay.Language.get('deletions-help')}
						label={Liferay.Language.get(
							'replicate-selected-deletions'
						)}
						name="deletions"
					/>
				)}
			</ClayLayout.Sheet>

			<FormikFieldContentSelector
				commentsAndRatingsEnabled={commentsAndRatingsEnabled}
				lookAndFeelEnabled={lookAndFeelEnabled}
				name="contentSelection"
				previewPortletDataHandlerSections={
					importPreview.previewPortletDataHandlerSections
				}
				process="import"
			/>
		</>
	);
}
