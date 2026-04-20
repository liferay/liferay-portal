/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import React, {createContext, useContext} from 'react';

import {Wizard, WizardStep} from '../../components/Wizard';
import DataSelectionStep from './steps/DataSelectionStep';
import FileSelectionStep from './steps/FileSelectionStep';
import SettingsStep from './steps/SettingsStep';

export const WizardContext = createContext({
	isCompanyGroup: false,
});

export function useWizard() {
	return useContext(WizardContext);
}

export function NewImport({
	backURL,
	isCompanyGroup,
}: {
	backURL: string;
	isCompanyGroup: boolean;
}) {
	return (
		<WizardContext.Provider value={{isCompanyGroup}}>
			<Wizard backURL={backURL}>
				<WizardStep
					description={Liferay.Language.get(
						'name-your-import-process-and-upload-your-file'
					)}
					initialValues={{
						fileSelector: undefined,
						name: '',
					}}
					title={Liferay.Language.get('setup')}
				>
					<FileSelectionStep />
				</WizardStep>

				<WizardStep
					description={Liferay.Language.get(
						'select-the-data-from-your-file-that-you-would-like-to-import'
					)}
					title={Liferay.Language.get('data-selection')}
				>
					<DataSelectionStep />
				</WizardStep>

				<WizardStep
					actionButton={
						<ClayButton type="submit">
							<span className="inline-item inline-item-before">
								<ClayIcon className="mr-1" symbol="import" />
							</span>

							{Liferay.Language.get('import')}
						</ClayButton>
					}
					description={Liferay.Language.get(
						'set-up-your-import-configuration'
					)}
					onSubmit={async () => {
						alert('Import started!');
					}}
					title={Liferay.Language.get('settings')}
				>
					<SettingsStep />
				</WizardStep>
			</Wizard>
		</WizardContext.Provider>
	);
}
