/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLoadingIndicator from '@clayui/loading-indicator';
import React from 'react';

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

type Props = {
	commentsAndRatingsEnabled?: boolean;
	loading?: boolean;
	pageTreeModalConfiguration: PageTreeModalConfiguration;
	portletDataHandlerSections: PortletDataHandlerSection[];
	subtitle: string;
	timeZoneId: string;
	title?: string;
} & (
	| {
			deletionCount?: number;
			deletionsDescription: string;
			deletionsLabel: string;
			itemsCount?: number;
			onApplyFilter: (dateFilterValues: DateFilterValues) => void;
			permissionsDescription: string;
			permissionsLabel: string;
			process: 'export' | 'publish';
	  }
	| {
			deletionCount?: never;
			deletionsDescription?: never;
			deletionsLabel?: never;
			itemsCount?: never;
			onApplyFilter?: never;
			permissionsDescription?: never;
			permissionsLabel?: never;
			process: 'sync';
	  }
);

export default function DataSelection(props: Props) {
	const {
		commentsAndRatingsEnabled = false,
		loading = false,
		pageTreeModalConfiguration,
		portletDataHandlerSections,
		process,
		subtitle,
		timeZoneId,
		title = Liferay.Language.get('data-selection'),
	} = props;

	return (
		<>
			<SectionHeader id={LABEL_ID} subtitle={subtitle} title={title} />

			{props.process !== 'sync' && (
				<>
					<FormikFieldCheckbox
						description={props.permissionsDescription}
						label={props.permissionsLabel}
						name="permissions"
					/>

					{!!props.deletionCount && (
						<FormikFieldCheckbox
							description={props.deletionsDescription}
							label={props.deletionsLabel}
							name="deletions"
						/>
					)}

					<FormikFieldDateFilter
						itemsCount={props.itemsCount}
						name="dateFilterValues"
						onApplyFilter={props.onApplyFilter}
						process={process}
						timeZoneId={timeZoneId}
					/>
				</>
			)}

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
