/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLoadingIndicator from '@clayui/loading-indicator';
import React from 'react';

import {ExportImportProcess} from '../types/exportImportProcess';
import {PortletDataHandlerSection} from '../types/portletDataHandler';
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
	onApplyFilter,
	pageTreeModalConfiguration,
	permissionsDescription,
	permissionsLabel,
	portletDataHandlerSections,
	process = 'export',
	subtitle,
	timeZoneId,
}: {
	commentsAndRatingsEnabled?: boolean;
	deletionCount?: number;
	deletionsDescription: string;
	deletionsLabel: string;
	itemsCount?: number;
	lastPublishDate?: string;
	loading?: boolean;
	onApplyFilter: (dateFilterValues: DateFilterValues) => void;
	pageTreeModalConfiguration: PageTreeModalConfiguration;
	permissionsDescription: string;
	permissionsLabel: string;
	portletDataHandlerSections: PortletDataHandlerSection[];
	process?: ExportImportProcess;
	subtitle: string;
	timeZoneId: string;
}) {
	return (
		<>
			<SectionHeader
				id={LABEL_ID}
				subtitle={subtitle}
				title={Liferay.Language.get('data-selection')}
			/>

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

			<FormikFieldDateFilter
				itemsCount={itemsCount}
				lastPublishDate={lastPublishDate}
				name="dateFilterValues"
				onApplyFilter={onApplyFilter}
				timeZoneId={timeZoneId}
			/>

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
						name="contentSelection"
						pageTreeModalConfiguration={pageTreeModalConfiguration}
						portletDataHandlerSections={portletDataHandlerSections}
						process={process}
					/>
				)}
			</div>
		</>
	);
}
