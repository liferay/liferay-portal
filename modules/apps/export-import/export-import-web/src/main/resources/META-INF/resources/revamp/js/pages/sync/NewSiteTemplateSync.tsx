/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import {Form, Formik} from 'formik';
import {escapeHTML, sub} from 'frontend-js-web';
import React, {useState} from 'react';

import DataSelection from '../../components/DataSelection';
import Footer from '../../components/Footer';
import {PageTreeModalConfiguration} from '../../components/PageTreeModal';
import {ContentSelection} from '../../components/forms/content_selector/ContentSelector';
import {usePreview} from '../../hooks/usePreview';
import {postSyncProcess} from '../../services/postSyncProcess';
import {Preview} from '../../types/exportImportPreview';
import {
	toPortletDataHandlerSections,
	toProcessRequestFlags,
	withSelectedLayoutSetCount,
} from '../../utils/contentSelection';
import {navigateToProcesses} from '../../utils/navigateToProcesses';
import {toPortletDataHandlers} from '../../utils/toPortletDataHandlers';
import SyncConfirmationModal from './components/SyncConfirmationModal';

type SyncFormValues = {
	contentSelection: ContentSelection | null | undefined;
};

export function NewSiteTemplateSync({
	backURL,
	commentsAndRatingsEnabled = false,
	pageTreeModalConfiguration,
	siteTemplateName,
	syncPreview,
	syncPreviewAPIURL,
	syncProcessAPIURL,
	timeZoneId,
}: {
	backURL: string;
	commentsAndRatingsEnabled?: boolean;
	pageTreeModalConfiguration: PageTreeModalConfiguration;
	siteTemplateName: string;
	syncPreview?: Preview;
	syncPreviewAPIURL: string;
	syncProcessAPIURL: string;
	timeZoneId: string;
}) {
	const {error, loading, preview} = usePreview(
		syncPreviewAPIURL,
		timeZoneId,
		syncPreview
	);

	const [confirming, setConfirming] = useState(false);

	if (error) {
		return <ClayAlert displayType="danger">{error}</ClayAlert>;
	}

	const portletDataHandlerSections = toPortletDataHandlerSections(
		preview?.portletDataHandlers ?? []
	);

	const initialFormValues: SyncFormValues = {
		contentSelection: undefined,
	};

	return (
		<Formik
			initialValues={initialFormValues}
			onSubmit={async (values, {setStatus}) => {
				const result = await postSyncProcess({
					syncProcessRequest: {
						...toProcessRequestFlags(values.contentSelection),
						portletDataHandlers: toPortletDataHandlers(
							portletDataHandlerSections,
							values.contentSelection
						),
					},
					url: syncProcessAPIURL,
				});

				if (result.error) {
					Liferay.Util.openToast({
						message: escapeHTML(result.error),
						type: 'danger',
					});

					return;
				}

				navigateToProcesses(
					setStatus,
					sub(
						Liferay.Language.get(
							'the-sync-of-the-site-template-x-started-you-will-receive-a-notification-when-the-process-is-complete'
						),
						escapeHTML(siteTemplateName)
					),
					backURL
				);
			}}
			validate={(values) =>
				values.contentSelection
					? {}
					: {
							contentSelection: Liferay.Language.get(
								'please-select-at-least-one-entity-type-to-continue'
							),
						}
			}
		>
			{(formik) => {
				const contentSelection = formik.values.contentSelection;

				return (
					<Form
						noValidate
						onSubmit={(event) => {
							event.preventDefault();

							setConfirming(true);
						}}
					>
						<ClayAlert
							className="mt-4"
							displayType="info"
							title={Liferay.Language.get('info')}
						>
							{Liferay.Language.get(
								'site-template-sync-description'
							)}
						</ClayAlert>

						<DataSelection
							commentsAndRatingsEnabled={
								commentsAndRatingsEnabled
							}
							loading={loading}
							pageTreeModalConfiguration={{
								...pageTreeModalConfiguration,
								title: Liferay.Language.get('pages-to-sync'),
							}}
							portletDataHandlerSections={withSelectedLayoutSetCount(
								portletDataHandlerSections,
								contentSelection
							)}
							process="sync"
							subtitle={sub(
								Liferay.Language.get(
									'choose-what-to-sync-from-x'
								),
								siteTemplateName
							)}
							timeZoneId={timeZoneId}
							title={Liferay.Language.get('select-data-to-sync')}
						/>

						<Footer
							actionButtonLabel={Liferay.Language.get('sync')}
							actionButtonSymbol="reload"
							backURL={backURL}
							continueDisabled={!contentSelection}
						/>

						{confirming && (
							<SyncConfirmationModal
								onClose={() => setConfirming(false)}
								onConfirm={formik.submitForm}
								siteTemplateName={siteTemplateName}
							/>
						)}
					</Form>
				);
			}}
		</Formik>
	);
}
