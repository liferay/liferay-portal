/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import {Form, Formik, FormikValues} from 'formik';
import React, {useCallback, useEffect, useRef, useState} from 'react';

import Footer from '../../components/Footer';
import {
	DateFilterValues,
	NormalizedDateFilter,
	Range,
	normalizeDateFilter,
} from '../../components/date_filter';
import {ContentSelection} from '../../components/forms/content_selector/ContentSelector';
import {
	ExportPreviewParams,
	getExportPreview,
} from '../../services/getExportPreview';
import {postExportProcess} from '../../services/postExportProcess';
import {ExportPreview} from '../../types/exportImportPreview';
import {
	getSelectedDeletionCount,
	getSelectedItemsCount,
	toProcessRequestFlags,
	withSelectedLayoutSetCount,
} from '../../utils/contentSelection';
import {toRequestPortletDataHandlers} from '../../utils/toRequestPortletDataHandlers';
import DataSelection from './components/DataSelection';
import {PageTreeModalConfiguration} from './components/PageTreeModal';
import Setup from './components/Setup';

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
	exportPreview?: ExportPreview;
	exportPreviewAPIURL: string;
	exportProcessAPIURL: string;
	lookAndFeelEnabled?: boolean;
	pageTreeModalConfiguration: PageTreeModalConfiguration;
}) {
	const [preview, setPreview] = useState<ExportPreview | undefined>(
		exportPreview
	);
	const [error, setError] = useState<string | null>(null);
	const [loading, setLoading] = useState(!exportPreview);
	const initialPreviewRef = useRef<ExportPreview | undefined>(exportPreview);
	const appliedDateFilterRef = useRef<NormalizedDateFilter>({});

	const getPreview = useCallback(
		(exportPreviewParams: ExportPreviewParams) => {
			setLoading(true);
			setError(null);

			getExportPreview(exportPreviewParams).then((result) => {
				if (result.error !== null) {
					setError(result.error);
				}
				else {
					setPreview(result.data);

					if (!initialPreviewRef.current) {
						initialPreviewRef.current = result.data;
					}
				}

				setLoading(false);
			});
		},
		[]
	);

	useEffect(() => {
		if (exportPreview) {
			return;
		}

		getPreview({url: exportPreviewAPIURL});
	}, [exportPreview, exportPreviewAPIURL, getPreview]);

	if (error) {
		return <ClayAlert displayType="danger">{error}</ClayAlert>;
	}

	const sections = preview?.previewPortletDataHandlerSections ?? [];

	const handleApplyFilter = (filterValues: DateFilterValues) => {
		appliedDateFilterRef.current = normalizeDateFilter(filterValues);

		if (filterValues.range === Range.All && initialPreviewRef.current) {
			setPreview(initialPreviewRef.current);

			return;
		}

		getPreview({
			query: appliedDateFilterRef.current,
			url: exportPreviewAPIURL,
		});
	};

	return (
		<Formik
			initialValues={{
				contentSelection: undefined,
				dateFilter: {range: Range.All} as DateFilterValues,
				deletions: false,
				name: '',
				permissions: false,
			}}
			onSubmit={async (values) => {
				const contentSelection = values.contentSelection as
					| ContentSelection
					| undefined;

				const result = await postExportProcess({
					exportProcessRequest: {
						...appliedDateFilterRef.current,
						...toProcessRequestFlags(contentSelection),
						deletions: !!values.deletions,
						name: values.name,
						permissions: !!values.permissions,
						requestPortletDataHandlers:
							toRequestPortletDataHandlers(
								sections,
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
			validate={(values: FormikValues) => {
				const errors: {[key: string]: string} = {};

				if (!values?.name) {
					errors.name = Liferay.Language.get(
						'this-field-is-required'
					);
				}

				if (!values?.contentSelection) {
					errors.contentSelection = Liferay.Language.get(
						'please-select-at-least-one-entity-type-to-continue'
					);
				}

				return errors;
			}}
			validateOnMount
		>
			{(formik) => {
				const contentSelection = formik.values.contentSelection as
					| ContentSelection
					| undefined;

				return (
					<Form noValidate>
						<Setup />

						<DataSelection
							commentsAndRatingsEnabled={
								commentsAndRatingsEnabled
							}
							deletionCount={getSelectedDeletionCount(
								preview?.deletionCount,
								sections,
								contentSelection
							)}
							itemsCount={getSelectedItemsCount(
								preview?.additionCount,
								sections,
								contentSelection
							)}
							loading={loading}
							lookAndFeelEnabled={lookAndFeelEnabled}
							onApplyFilter={handleApplyFilter}
							pageTreeModalConfiguration={
								pageTreeModalConfiguration
							}
							sections={withSelectedLayoutSetCount(
								sections,
								contentSelection
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
