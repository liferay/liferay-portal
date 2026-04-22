/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import {FormikValues} from 'formik';
import React from 'react';

import {Wizard, WizardStep} from '../../components/Wizard';
import {DateFilterValues} from '../../components/date_filter';
import DataSelectionStep from './steps/DataSelectionStep';
import SettingsStep from './steps/SettingsStep';
import SetupStep from './steps/SetupStep';

export function NewExport({backURL}: {backURL: string}) {
	const handleApplyFilter = (filterValues: DateFilterValues) => {

		// eslint-disable-next-line no-console
		console.log('Filtering by:', filterValues);
	};

	return (
		<Wizard backURL={backURL}>
			<WizardStep
				description={Liferay.Language.get(
					'name-your-export-and-make-an-initial-data-selection-to-narrow-down-in-the-next-step'
				)}
				initialValues={{
					name: '',
					selectedSectionIds: [],
				}}
				title={Liferay.Language.get('setup')}
				validate={(values: FormikValues) => {
					const errors: {[key: string]: string} = {};

					if (!values?.selectedSectionIds?.length) {
						errors.selectedSectionIds = Liferay.Language.get(
							'please-select-at-least-one-entity-type-to-continue'
						);
					}

					return errors;
				}}
			>
				<SetupStep />
			</WizardStep>

			<WizardStep
				description={Liferay.Language.get(
					'select-and-filter-the-data-you-want-to-include-in-your-export'
				)}
				title={Liferay.Language.get('data-selection')}
			>
				<DataSelectionStep
					itemsCount={0}
					onApplyFilter={handleApplyFilter}
				/>
			</WizardStep>

			<WizardStep
				actionButton={
					<ClayButton type="submit">
						<span className="inline-item inline-item-before">
							<ClayIcon className="mr-1" symbol="export" />
						</span>

						{Liferay.Language.get('export')}
					</ClayButton>
				}
				description={Liferay.Language.get(
					'configure-your-export-settings'
				)}
				onSubmit={async () => {
					alert('Export started!');
				}}
				title={Liferay.Language.get('settings')}
			>
				<SettingsStep />
			</WizardStep>
		</Wizard>
	);
}
