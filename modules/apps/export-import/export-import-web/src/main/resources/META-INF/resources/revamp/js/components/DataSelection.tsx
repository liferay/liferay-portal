/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLayout from '@clayui/layout';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import React from 'react';

import {ExportImportProcess} from '../types/exportImportProcess';
import {PreviewPortletDataHandlerSection} from '../types/portletDataHandler';
import {PageTreeModalConfiguration} from './PageTreeModal';
import SectionHeader from './SectionHeader';
import {DateFilterValues} from './date_filter';
import {
	FormikFieldCheckbox,
	FormikFieldContentSelector,
	FormikFieldDateFilter,
} from './forms/formik';

const LABEL_ID = 'dataSelection-label';

export default function DataSelection({
	commentsAndRatingsEnabled = false,
	deletionCount = 0,
	deletionsDescription,
	deletionsLabel,
	itemsCount,
	lastPublishDate,
	loading = false,
	lookAndFeelEnabled = false,
	onApplyFilter,
	pageTreeModalConfiguration,
	permissionsDescription,
	permissionsLabel,
	previewPortletDataHandlerSections,
	process = 'export',
	subtitle,
}: {
	commentsAndRatingsEnabled?: boolean;
	deletionCount?: number;
	deletionsDescription: string;
	deletionsLabel: string;
	itemsCount?: number;
	lastPublishDate?: string;
	loading?: boolean;
	lookAndFeelEnabled?: boolean;
	onApplyFilter: (dateFilterValues: DateFilterValues) => void;
	pageTreeModalConfiguration: PageTreeModalConfiguration;
	permissionsDescription: string;
	permissionsLabel: string;
	previewPortletDataHandlerSections: PreviewPortletDataHandlerSection[];
	process?: ExportImportProcess;
	subtitle: string;
}) {
	return (
		<>
			<SectionHeader
				className="mt-4"
				id={LABEL_ID}
				subtitle={subtitle}
				title={Liferay.Language.get('data-selection')}
			/>

			<ClayLayout.Sheet className="option-group">
				<FormikFieldCheckbox
					description={permissionsDescription}
					label={permissionsLabel}
					name="permissions"
				/>

				{deletionCount > 0 && (
					<FormikFieldCheckbox
						description={deletionsDescription}
						label={deletionsLabel}
						name="deletions"
					/>
				)}
			</ClayLayout.Sheet>

			<ClayLayout.Sheet className="mt-4">
				<FormikFieldDateFilter
					itemsCount={itemsCount}
					lastPublishDate={lastPublishDate}
					name="dateFilter"
					onApplyFilter={onApplyFilter}
				/>
			</ClayLayout.Sheet>

			<div className="sr-only" role="status">
				{loading
					? Liferay.Language.get('loading')
					: Liferay.Language.get('loaded')}
			</div>

			<div aria-busy={loading} data-testid="data-selection-section">
				{loading ? (
					<ClayLoadingIndicator className="mb-9 mt-8" />
				) : (
					<FormikFieldContentSelector
						aria-labelledby={LABEL_ID}
						commentsAndRatingsEnabled={commentsAndRatingsEnabled}
						lookAndFeelEnabled={lookAndFeelEnabled}
						name="contentSelection"
						pageTreeModalConfiguration={pageTreeModalConfiguration}
						previewPortletDataHandlerSections={
							previewPortletDataHandlerSections
						}
						process={process}
					/>
				)}
			</div>
		</>
	);
}
