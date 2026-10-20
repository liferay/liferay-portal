/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useId} from 'react';

import ScopeSection from '../../../components/ScopeSection';
import SectionHeader from '../../../components/SectionHeader';
import {
	FormikFieldCheckbox,
	FormikFieldContentSelector,
} from '../../../components/forms/formik';
import {ImportPreview} from '../../../types/exportImportPreview';
import {toPortletDataHandlerSections} from '../../../utils/contentSelection';
import FileSummary from './FileSummary';
import SiteSelection from './SiteSelection';

export default function DataSelectionStep({
	commentsAndRatingsEnabled = false,
	importPreview,
	siteSelectionEnabled = false,
}: {
	commentsAndRatingsEnabled?: boolean;
	importPreview?: ImportPreview;
	siteSelectionEnabled?: boolean;
}) {
	const labelId = useId();

	if (!importPreview) {
		return null;
	}

	return (
		<>
			<FileSummary importPreview={importPreview} />

			{siteSelectionEnabled && !!importPreview.sites?.length && (
				<ScopeSection
					subtitle={Liferay.Language.get(
						'import-complete-sites-from-your-file,-including-all-their-pages,-content,-and-settings'
					)}
				>
					<SiteSelection sites={importPreview.sites} />
				</ScopeSection>
			)}

			<SectionHeader
				id={labelId}
				subtitle={Liferay.Language.get(
					'select-the-data-from-your-file-that-you-would-like-to-import'
				)}
				title={Liferay.Language.get('data-selection')}
			/>

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
					label={Liferay.Language.get('replicate-selected-deletions')}
					name="deletions"
				/>
			)}

			<div data-testid="data-selection-section">
				<FormikFieldContentSelector
					aria-labelledby={labelId}
					commentsAndRatingsEnabled={commentsAndRatingsEnabled}
					name="contentSelection"
					portletDataHandlerSections={toPortletDataHandlerSections(
						importPreview.portletDataHandlers
					)}
					process="import"
				/>
			</div>
		</>
	);
}
