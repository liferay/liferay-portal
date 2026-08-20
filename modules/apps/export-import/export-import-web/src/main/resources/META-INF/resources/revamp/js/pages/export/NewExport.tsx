/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import {Form, Formik} from 'formik';
import {sub} from 'frontend-js-web';
import React from 'react';

import DataSelection from '../../components/DataSelection';
import Footer from '../../components/Footer';
import {PageTreeModalConfiguration} from '../../components/PageTreeModal';
import Setup from '../../components/Setup';
import {DateFilterValues, Range} from '../../components/date_filter';
import {ContentSelection} from '../../components/forms/content_selector/ContentSelector';
import {usePreview} from '../../hooks/usePreview';
import {postExportProcess} from '../../services/postExportProcess';
import {Preview} from '../../types/exportImportPreview';
import {
	getSelectedDeletionCount,
	getSelectedItemsCount,
	toProcessRequestFlags,
	withSelectedLayoutSetCount,
} from '../../utils/contentSelection';
import {getProcessFormErrors} from '../../utils/getProcessFormErrors';
import {toRequestPortletDataHandlers} from '../../utils/toRequestPortletDataHandlers';

type ExportFormValues = {
	contentSelection: ContentSelection | undefined;
	dateFilter: DateFilterValues;
	deletions: boolean;
	name: string;
	permissions: boolean;
};

export function NewExport({
	backURL,
	commentsAndRatingsEnabled = false,
	exportPreview,
	exportPreviewAPIURL,
	exportProcessAPIURL,
	lookAndFeelEnabled = false,
	pageTreeModalConfiguration,
}: {
	backURL: string;
	commentsAndRatingsEnabled?: boolean;
	exportPreview?: Preview;
	exportPreviewAPIURL: string;
	exportProcessAPIURL: string;
	lookAndFeelEnabled?: boolean;
	pageTreeModalConfiguration: PageTreeModalConfiguration;
}) {
	const {appliedDateFilterRef, error, handleApplyFilter, loading, preview} =
		usePreview(exportPreviewAPIURL, exportPreview);

	if (error) {
		return <ClayAlert displayType="danger">{error}</ClayAlert>;
	}

	const previewPortletDataHandlerSections =
		preview?.previewPortletDataHandlerSections ?? [];

	const initialFormValues: ExportFormValues = {
		contentSelection: undefined,
		dateFilter: {range: Range.All},
		deletions: false,
		name: '',
		permissions: false,
	};

	return (
		<Formik
			initialValues={initialFormValues}
			onSubmit={async (values) => {
				const result = await postExportProcess({
					exportProcessRequest: {
						...appliedDateFilterRef.current,
						...toProcessRequestFlags(values.contentSelection),
						deletions: values.deletions,
						name: values.name,
						permissions: values.permissions,
						requestPortletDataHandlers:
							toRequestPortletDataHandlers(
								previewPortletDataHandlerSections,
								values.contentSelection
							),
					},
					url: exportProcessAPIURL,
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
			validate={getProcessFormErrors}
			validateOnMount
		>
			{(formik) => {
				const contentSelection = formik.values.contentSelection;

				return (
					<Form noValidate>
						<Setup
							placeholder={Liferay.Language.get(
								'add-an-export-name'
							)}
							subtitle={Liferay.Language.get(
								'provide-a-descriptive-name-for-your-file'
							)}
							title={sub(
								Liferay.Language.get('x-details'),
								Liferay.Language.get('export')
							)}
						/>

						<DataSelection
							commentsAndRatingsEnabled={
								commentsAndRatingsEnabled
							}
							deletionCount={getSelectedDeletionCount(
								preview?.deletionCount,
								previewPortletDataHandlerSections,
								contentSelection
							)}
							deletionsDescription={Liferay.Language.get(
								'deletions-help-export'
							)}
							deletionsLabel={Liferay.Language.get(
								'export-individual-deletions'
							)}
							itemsCount={getSelectedItemsCount(
								preview?.additionCount,
								previewPortletDataHandlerSections,
								contentSelection
							)}
							loading={loading}
							lookAndFeelEnabled={lookAndFeelEnabled}
							onApplyFilter={handleApplyFilter}
							pageTreeModalConfiguration={
								pageTreeModalConfiguration
							}
							permissionsDescription={Liferay.Language.get(
								'export-import-permissions-help'
							)}
							permissionsLabel={Liferay.Language.get(
								'export-permissions'
							)}
							previewPortletDataHandlerSections={withSelectedLayoutSetCount(
								previewPortletDataHandlerSections,
								contentSelection
							)}
							subtitle={Liferay.Language.get(
								'select-and-filter-the-data-you-want-to-include-in-your-export'
							)}
						/>

						<Footer
							actionButton={
								<ClayButton
									disabled={
										formik.isSubmitting || !formik.isValid
									}
									type="submit"
								>
									<span className="inline-item inline-item-before">
										<ClayIcon
											className="mr-1"
											symbol="export"
										/>
									</span>

									{Liferay.Language.get('export')}
								</ClayButton>
							}
							backURL={backURL}
						/>
					</Form>
				);
			}}
		</Formik>
	);
}
