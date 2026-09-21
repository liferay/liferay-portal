/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import ClayLayout from '@clayui/layout';
import React, {useState} from 'react';

import {Wizard, WizardStep} from '../../components/Wizard';
import {ContentSelection} from '../../components/forms/content_selector/ContentSelector';
import {postImportProcess} from '../../services/postImportProcess';
import {ImportPreview} from '../../types/exportImportPreview';
import {DataStrategy, UserIdStrategy} from '../../types/exportImportProcess';
import {Scope} from '../../types/scope';
import {toProcessRequestFlags} from '../../utils/contentSelection';
import {toRequestPortletDataHandlers} from '../../utils/toRequestPortletDataHandlers';
import DataSelectionStep from './steps/DataSelectionStep';
import FileSelectionStep from './steps/FileSelectionStep';
import SettingsStep, {SETTINGS_STEP_INITIAL_VALUES} from './steps/SettingsStep';
import SiteSelection from './steps/SiteSelection';

export function NewImport({
	backURL,
	commentsAndRatingsEnabled = false,
	importPreviewAPIURL,
	importProcessAPIURL,
	lookAndFeelEnabled = false,
	scope,
	sitesEnabled = false,
}: {
	backURL: string;
	commentsAndRatingsEnabled?: boolean;
	importPreviewAPIURL: string;
	importProcessAPIURL: string;
	lookAndFeelEnabled?: boolean;
	scope: Scope;
	sitesEnabled?: boolean;
}) {
	const [importPreview, setImportPreview] = useState<
		ImportPreview | undefined
	>();

	return (
		<Wizard backURL={backURL}>
			<WizardStep
				description={Liferay.Language.get(
					'name-your-import-process-and-upload-your-file'
				)}
				initialValues={{
					fileSelector: undefined,
					name: '',
				}}
				isStepValid={(values) =>
					values.fileSelector instanceof File &&
					!!values.name.trim() &&
					!!importPreview
				}
				title={Liferay.Language.get('setup')}
			>
				<FileSelectionStep
					importPreviewAPIURL={importPreviewAPIURL}
					setImportPreview={setImportPreview}
				/>
			</WizardStep>

			<WizardStep
				description={Liferay.Language.get(
					'select-the-data-from-your-file-that-you-would-like-to-import'
				)}
				initialValues={{
					contentSelection: undefined,
					deletions: false,
					permissions: false,
					siteExternalReferenceCodes: [],
				}}
				isStepValid={(values) =>
					!!values.contentSelection ||
					!!values.siteExternalReferenceCodes?.length
				}
				title={Liferay.Language.get('data-selection')}
			>
				<DataSelectionStep
					commentsAndRatingsEnabled={commentsAndRatingsEnabled}
					importPreview={importPreview}
					lookAndFeelEnabled={lookAndFeelEnabled}
				/>

				{sitesEnabled && (
					<ClayLayout.Sheet className="mt-4 option-group">
						<SiteSelection
							previewSites={importPreview?.previewSites ?? []}
						/>
					</ClayLayout.Sheet>
				)}
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
				initialValues={SETTINGS_STEP_INITIAL_VALUES}
				onSubmit={async (values) => {
					if (!importPreview) {
						Liferay.Util.openToast({
							message: Liferay.Language.get(
								'an-unexpected-error-occurred'
							),
							type: 'danger',
						});

						return;
					}

					const contentSelection = values.contentSelection as
						| ContentSelection
						| undefined;

					const result = await postImportProcess({
						importProcessRequest: {
							...toProcessRequestFlags(contentSelection),
							dataStrategy: values.dataStrategy as DataStrategy,
							deletions: !!values.deletions,
							name: values.name,
							permissions: !!values.permissions,
							requestPortletDataHandlers:
								toRequestPortletDataHandlers(
									importPreview.previewPortletDataHandlerSections ??
										[],
									values.contentSelection
								),
							siteExternalReferenceCodes:
								values.siteExternalReferenceCodes,
							userIdStrategy:
								values.userIdStrategy as UserIdStrategy,
						},
						url: importProcessAPIURL,
					});

					if (result.error) {
						Liferay.Util.openToast({
							message: result.error,
							type: 'danger',
						});

						return;
					}

					Liferay.Util.navigate(backURL);
				}}
				title={Liferay.Language.get('settings')}
			>
				<SettingsStep scope={scope} />
			</WizardStep>
		</Wizard>
	);
}
