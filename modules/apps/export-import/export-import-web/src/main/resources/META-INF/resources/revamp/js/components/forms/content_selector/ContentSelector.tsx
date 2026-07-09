/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import React from 'react';

import {PageTreeModalConfiguration} from '../../../pages/export/components/PageTreeModal';
import {ExportImportProcess} from '../../../types/exportImportProcess';
import {PreviewPortletDataHandlerSection} from '../../../types/portletDataHandler';
import {
	getVisibleSections,
	updateSelection,
} from '../../../utils/contentSelection';
import ContentSection, {SectionSelection} from './ContentSection';

export type ContentSelection = Record<string, SectionSelection>;

interface ContentSelectorProps {
	'aria-labelledby'?: string;
	'commentsAndRatingsEnabled'?: boolean;
	'contentSelection': ContentSelection | undefined;
	'errorMessage'?: string;
	'lookAndFeelEnabled'?: boolean;
	'name': string;
	'onChange': (value: ContentSelection | undefined) => void;
	'pageTreeModalConfiguration'?: PageTreeModalConfiguration;
	'previewPortletDataHandlerSections': PreviewPortletDataHandlerSection[];
	'process'?: ExportImportProcess;
	'showDeletions'?: boolean;
}

export default function ContentSelector({
	'aria-labelledby': ariaLabelledby,
	commentsAndRatingsEnabled = false,
	contentSelection = {},
	errorMessage,
	lookAndFeelEnabled = false,
	name,
	onChange,
	pageTreeModalConfiguration,
	process = 'export',
	previewPortletDataHandlerSections,
	showDeletions,
}: ContentSelectorProps) {
	const errorId = errorMessage ? `${name}-error-message` : undefined;

	const visibleSections = getVisibleSections(
		previewPortletDataHandlerSections,
		{
			lookAndFeelEnabled,
			showDeletions,
		}
	);

	return (
		<div
			aria-describedby={errorId}
			aria-invalid={errorMessage ? true : undefined}
			aria-labelledby={ariaLabelledby}
			className="c-gap-4 d-flex flex-column mt-4"
			role="group"
		>
			{visibleSections.map(
				(
					previewPortletDataHandlerSection: PreviewPortletDataHandlerSection
				) => (
					<ContentSection
						commentsAndRatingsEnabled={commentsAndRatingsEnabled}
						key={previewPortletDataHandlerSection.name}
						lookAndFeelEnabled={lookAndFeelEnabled}
						onChange={(sectionSelection) =>
							onChange(
								updateSelection(
									contentSelection,
									previewPortletDataHandlerSection.name,
									sectionSelection
								)
							)
						}
						pageTreeModalConfiguration={pageTreeModalConfiguration}
						previewPortletDataHandlerSection={
							previewPortletDataHandlerSection
						}
						process={process}
						sectionSelection={
							contentSelection[
								previewPortletDataHandlerSection.name
							]
						}
						showDeletions={showDeletions}
					/>
				)
			)}

			{errorMessage && (
				<ClayAlert
					displayType="danger"
					id={errorId}
					title={Liferay.Language.get('error-colon')}
				>
					{errorMessage}
				</ClayAlert>
			)}
		</div>
	);
}
