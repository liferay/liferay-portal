/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import React from 'react';

import {ExportImportProcess} from '../../../types/exportImportProcess';
import {PortletDataHandlerSection} from '../../../types/portletDataHandler';
import {
	getVisibleSections,
	updateSelection,
} from '../../../utils/contentSelection';
import {PageTreeModalConfiguration} from '../../PageTreeModal';
import ContentSection, {SectionSelection} from './ContentSection';

export type ContentSelection = Record<string, SectionSelection>;

interface ContentSelectorProps {
	'aria-labelledby'?: string;
	'commentsAndRatingsEnabled'?: boolean;
	'contentSelection': ContentSelection | undefined;
	'errorMessage'?: string;
	'name': string;
	'onChange': (value: ContentSelection | undefined) => void;
	'pageTreeModalConfiguration'?: PageTreeModalConfiguration;
	'portletDataHandlerSections': PortletDataHandlerSection[];
	'process'?: ExportImportProcess;
	'showDeletions'?: boolean;
}

export default function ContentSelector({
	'aria-labelledby': ariaLabelledby,
	commentsAndRatingsEnabled = false,
	contentSelection = {},
	errorMessage,
	name,
	onChange,
	pageTreeModalConfiguration,
	process = 'export',
	portletDataHandlerSections,
	showDeletions,
}: ContentSelectorProps) {
	const errorId = errorMessage ? `${name}-error-message` : undefined;

	const visibleSections = getVisibleSections(portletDataHandlerSections, {
		showDeletions,
	});

	return (
		<div
			aria-describedby={errorId}
			aria-invalid={errorMessage ? true : undefined}
			aria-labelledby={ariaLabelledby}
			className="c-gap-4 d-flex flex-column mt-4"
			role="group"
		>
			{visibleSections.map(
				(portletDataHandlerSection: PortletDataHandlerSection) => (
					<ContentSection
						commentsAndRatingsEnabled={commentsAndRatingsEnabled}
						key={portletDataHandlerSection.key}
						onChange={(sectionSelection) =>
							onChange(
								updateSelection(
									contentSelection,
									portletDataHandlerSection.key,
									sectionSelection
								)
							)
						}
						pageTreeModalConfiguration={pageTreeModalConfiguration}
						portletDataHandlerSection={portletDataHandlerSection}
						process={process}
						sectionSelection={
							contentSelection[portletDataHandlerSection.key]
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
