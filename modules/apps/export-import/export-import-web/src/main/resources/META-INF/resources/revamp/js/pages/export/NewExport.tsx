/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import {Form, Formik} from 'formik';
import {sub} from 'frontend-js-web';
import React from 'react';

import DataSelection from '../../components/DataSelection';
import Footer from '../../components/Footer';
import {PageTreeModalConfiguration} from '../../components/PageTreeModal';
import ScopeSection from '../../components/ScopeSection';
import Setup from '../../components/Setup';
import {DateFilterValues, Range} from '../../components/date_filter';
import {ContentSelection} from '../../components/forms/content_selector/ContentSelector';
import {usePreview} from '../../hooks/usePreview';
import {postExportProcess} from '../../services/postExportProcess';
import {Preview} from '../../types/exportImportPreview';
import {
	getSelectedDeletionCount,
	getSelectedItemsCount,
	toPortletDataHandlerSections,
	toProcessRequestFlags,
	withSelectedLayoutSetCount,
} from '../../utils/contentSelection';
import {getProcessFormErrors} from '../../utils/getProcessFormErrors';
import {navigateToProcesses} from '../../utils/navigateToProcesses';
import {toPortletDataHandlers} from '../../utils/toPortletDataHandlers';
import SiteSelection from './components/SiteSelection';

type ExportFormValues = {
	contentSelection: ContentSelection | null | undefined;
	dateFilterValues: DateFilterValues;
	deletions: boolean;
	name: string;
	permissions: boolean;
	siteExternalReferenceCodes: string[];
};

export function NewExport({
	backURL,
	commentsAndRatingsEnabled = false,
	exportPreview,
	exportPreviewAPIURL,
	exportPreviewSitesAPIURL,
	exportProcessAPIURL,
	pageTreeModalConfiguration,
	siteSelectionEnabled = false,
	timeZoneId,
}: {
	backURL: string;
	commentsAndRatingsEnabled?: boolean;
	exportPreview?: Preview;
	exportPreviewAPIURL: string;
	exportPreviewSitesAPIURL?: string;
	exportProcessAPIURL: string;
	pageTreeModalConfiguration: PageTreeModalConfiguration;
	siteSelectionEnabled?: boolean;
	timeZoneId: string;
}) {
	const {appliedDateFilterRef, error, handleApplyFilter, loading, preview} =
		usePreview(exportPreviewAPIURL, timeZoneId, exportPreview);

	if (error) {
		return <ClayAlert displayType="danger">{error}</ClayAlert>;
	}

	const portletDataHandlerSections = toPortletDataHandlerSections(
		preview?.portletDataHandlers ?? []
	);

	const initialFormValues: ExportFormValues = {
		contentSelection: undefined,
		dateFilterValues: {range: Range.All},
		deletions: false,
		name: '',
		permissions: false,
		siteExternalReferenceCodes: [],
	};

	return (
		<Formik
			initialValues={initialFormValues}
			onSubmit={async (values, formikHelpers) => {
				const result = await postExportProcess({
					exportProcessRequest: {
						...toProcessRequestFlags(values.contentSelection),
						dateFilter: appliedDateFilterRef.current,
						deletions: values.deletions,
						name: values.name,
						permissions: values.permissions,
						portletDataHandlers: toPortletDataHandlers(
							portletDataHandlerSections,
							values.contentSelection
						),
						...(siteSelectionEnabled && {
							sites: values.siteExternalReferenceCodes.map(
								(externalReferenceCode) => ({
									externalReferenceCode,
								})
							),
						}),
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

				navigateToProcesses(
					formikHelpers,
					Liferay.Language.get('the-process-has-started'),
					backURL
				);
			}}
			validate={(values) =>
				getProcessFormErrors(values, siteSelectionEnabled)
			}
			validateOnMount
		>
			{(formik) => {
				const contentSelection = formik.values.contentSelection;

				const {selection: selectionError} = formik.errors as {
					selection?: string;
				};

				return (
					<Form noValidate>
						<Setup
							placeholder={Liferay.Language.get(
								'add-an-export-name'
							)}
							title={sub(
								Liferay.Language.get('x-details'),
								Liferay.Language.get('export[noun]')
							)}
						/>

						{siteSelectionEnabled && exportPreviewSitesAPIURL && (
							<ScopeSection
								subtitle={Liferay.Language.get(
									'export-complete-sites,-including-all-their-pages,-content,-and-settings'
								)}
							>
								<SiteSelection
									exportPreviewSitesAPIURL={
										exportPreviewSitesAPIURL
									}
								/>
							</ScopeSection>
						)}

						<DataSelection
							commentsAndRatingsEnabled={
								commentsAndRatingsEnabled
							}
							deletionCount={getSelectedDeletionCount(
								preview?.deletionCount,
								portletDataHandlerSections,
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
								portletDataHandlerSections,
								contentSelection
							)}
							loading={loading}
							onApplyFilter={handleApplyFilter}
							pageTreeModalConfiguration={{
								...pageTreeModalConfiguration,
								title: Liferay.Language.get('pages-to-export'),
							}}
							permissionsDescription={Liferay.Language.get(
								'export-import-permissions-help'
							)}
							permissionsLabel={Liferay.Language.get(
								'export-permissions'
							)}
							portletDataHandlerSections={withSelectedLayoutSetCount(
								portletDataHandlerSections,
								contentSelection
							)}
							subtitle={Liferay.Language.get(
								'select-and-filter-the-data-you-want-to-include-in-your-export'
							)}
							timeZoneId={timeZoneId}
						/>

						{(formik.touched.contentSelection ||
							formik.touched.siteExternalReferenceCodes) &&
							selectionError && (
								<ClayAlert
									className="mt-4"
									displayType="danger"
									title={Liferay.Language.get('error-colon')}
								>
									{selectionError}
								</ClayAlert>
							)}

						<Footer
							actionButtonLabel={Liferay.Language.get('export')}
							actionButtonSymbol="export"
							backURL={backURL}
							continueDisabled={
								formik.isSubmitting ||
								formik.status?.submitted ||
								!formik.isValid
							}
						/>
					</Form>
				);
			}}
		</Formik>
	);
}
