/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import React, {useState} from 'react';

import {Wizard, WizardStep} from '../../components/Wizard';
import {ContentSelection} from '../../components/forms/content_selector/ContentSelector';
import {postImportProcess} from '../../services/postImportProcess';
import {ImportPreview} from '../../types/exportImportPreview';
import {DataStrategy, UserIdStrategy} from '../../types/exportImportProcess';
import {Scope} from '../../types/scope';
import {
	toPortletDataHandlerSections,
	toProcessRequestFlags,
} from '../../utils/contentSelection';
import {toPortletDataHandlers} from '../../utils/toPortletDataHandlers';
import DataSelectionStep from './steps/DataSelectionStep';
import FileSelectionStep from './steps/FileSelectionStep';
import SettingsStep, {SETTINGS_STEP_INITIAL_VALUES} from './steps/SettingsStep';

export function NewImport({
	backURL,
	commentsAndRatingsEnabled = false,
	importPreviewAPIURL,
	importProcessAPIURL,
	scope,
	siteSelectionEnabled = false,
}: {
	backURL: string;
	commentsAndRatingsEnabled?: boolean;
	importPreviewAPIURL: string;
	importProcessAPIURL: string;
	scope: Scope;
	siteSelectionEnabled?: boolean;
}) {
	const [importPreview, setImportPreview] = useState<
		ImportPreview | undefined
	>();

	return (
		<Wizard backURL={backURL}>
			<WizardStep
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
					siteSelectionEnabled={siteSelectionEnabled}
				/>
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
							portletDataHandlers: toPortletDataHandlers(
								toPortletDataHandlerSections(
									importPreview.portletDataHandlers ?? []
								),
								contentSelection
							),
							...(siteSelectionEnabled && {
								sites: (
									values.siteExternalReferenceCodes as string[]
								).map((externalReferenceCode) => ({
									externalReferenceCode,
								})),
							}),
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
