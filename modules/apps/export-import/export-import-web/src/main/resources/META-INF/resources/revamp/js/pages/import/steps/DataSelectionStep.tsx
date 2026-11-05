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
	FormikFieldGroups,
} from '../../../components/forms/formik';
import {ImportPreview} from '../../../types/exportImportPreview';
import {toPortletDataHandlerSections} from '../../../utils/contentSelection';
import {getScopeSubtitle} from '../../../utils/getScopeSubtitle';
import FileSummary from './FileSummary';

export default function DataSelectionStep({
	commentsAndRatingsEnabled = false,
	importPreview,
	siteSelectionEnabled = false,
	spaceSelectionEnabled = false,
}: {
	commentsAndRatingsEnabled?: boolean;
	importPreview?: ImportPreview;
	siteSelectionEnabled?: boolean;
	spaceSelectionEnabled?: boolean;
}) {
	const labelId = useId();

	if (!importPreview) {
		return null;
	}

	const sites =
		siteSelectionEnabled && importPreview.sites?.length
			? importPreview.sites
			: undefined;

	const previewSpaces = importPreview.assetLibraries?.filter(
		({type}) => type === 'Space'
	);

	const spaces =
		spaceSelectionEnabled && previewSpaces?.length
			? previewSpaces
			: undefined;

	return (
		<>
			<FileSummary importPreview={importPreview} />

			{(sites || spaces) && (
				<ScopeSection
					subtitle={getScopeSubtitle('import', {
						siteSelectionEnabled: !!sites,
						spaceSelectionEnabled: !!spaces,
					})}
				>
					{sites && (
						<FormikFieldGroups
							groups={sites}
							name="siteExternalReferenceCodes"
							process="import"
							totalCount={sites.length}
							type="site"
						/>
					)}

					{spaces && (
						<FormikFieldGroups
							groups={spaces}
							name="spaceExternalReferenceCodes"
							process="import"
							totalCount={spaces.length}
							type="space"
						/>
					)}
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
