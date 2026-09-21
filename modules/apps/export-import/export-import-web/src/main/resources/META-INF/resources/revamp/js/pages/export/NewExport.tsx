/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import ClayLayout from '@clayui/layout';
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
import SiteSelection from './components/SiteSelection';

type ExportFormValues = {
	contentSelection: ContentSelection | undefined;
	dateFilter: DateFilterValues;
	deletions: boolean;
	name: string;
	permissions: boolean;
	siteExternalReferenceCodes: string[];
};

/**
 * The form's errors. `selection` belongs to no single field: it says that
 * neither an entity type nor a site was picked, so blaming either control for
 * it would mark the wrong one invalid.
 */
interface NewExportErrors {
	contentSelection?: string;
	name?: string;
	selection?: string;
}

export function NewExport({
	backURL,
	commentsAndRatingsEnabled = false,
	exportPreview,
	exportPreviewAPIURL,
	exportPreviewSitesAPIURL,
	exportProcessAPIURL,
	lookAndFeelEnabled = false,
	pageTreeModalConfiguration,
	sitesEnabled = false,
}: {
	backURL: string;
	commentsAndRatingsEnabled?: boolean;
	exportPreview?: Preview;
	exportPreviewAPIURL: string;
	exportPreviewSitesAPIURL?: string;
	exportProcessAPIURL: string;
	lookAndFeelEnabled?: boolean;
	pageTreeModalConfiguration: PageTreeModalConfiguration;
	sitesEnabled?: boolean;
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
		siteExternalReferenceCodes: [],
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
						siteExternalReferenceCodes:
							values.siteExternalReferenceCodes,
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
			validate={(values) => getProcessFormErrors(values, sitesEnabled)}
			validateOnMount
		>
			{(formik) => {
				const contentSelection = formik.values.contentSelection;

				const {selection: selectionError} =
					formik.errors as NewExportErrors;

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

						{sitesEnabled && exportPreviewSitesAPIURL && (
							<ClayLayout.Sheet className="mt-4 option-group">
								<SiteSelection
									exportPreviewSitesAPIURL={
										exportPreviewSitesAPIURL
									}
								/>
							</ClayLayout.Sheet>
						)}

						{formik.touched.contentSelection && selectionError && (
							<ClayAlert
								className="mt-4"
								displayType="danger"
								title={Liferay.Language.get('error-colon')}
							>
								{selectionError}
							</ClayAlert>
						)}

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
