/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {ClayCheckbox} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayLayout from '@clayui/layout';
import ClayMultiSelect from '@clayui/multi-select';
import {Form, Formik} from 'formik';
import {fetch, objectToFormData, sub} from 'frontend-js-web';
import React, {useState} from 'react';

import Footer from '../../components/Footer';
import PageTreeModal, {
	PageTreeModalConfiguration,
} from '../../components/PageTreeModal';
import SectionHeader from '../../components/SectionHeader';
import Setup from '../../components/Setup';

type Language = {
	label: string;
	value: string;
};

type StaticSiteExportFormValues = {
	exportAllPages: boolean;
	languageIds: string[];
	layoutIds?: number[];
	name: string;
};

export function NewStaticSiteExport({
	availableLanguages,
	backURL,
	defaultLanguageId,
	exportProcessAPIURL,
	pageTreeModalConfiguration,
	portletNamespace,
}: {
	availableLanguages: Language[];
	backURL: string;
	defaultLanguageId: string;
	exportProcessAPIURL: string;
	pageTreeModalConfiguration: PageTreeModalConfiguration;
	portletNamespace: string;
}) {
	const [languageInputValue, setLanguageInputValue] = useState('');
	const [showModal, setShowModal] = useState(false);

	const initialFormValues: StaticSiteExportFormValues = {
		exportAllPages: true,
		languageIds: [defaultLanguageId],
		name: '',
	};

	return (
		<Formik
			initialValues={initialFormValues}
			onSubmit={async (values) => {
				const response = await fetch(exportProcessAPIURL, {
					body: objectToFormData({
						[`${portletNamespace}languageIds`]: values.languageIds,
						[`${portletNamespace}name`]: values.name,
						...(values.layoutIds && {
							[`${portletNamespace}layoutIds`]: values.layoutIds,
						}),
					}),
					method: 'POST',
				});

				if (!response.ok) {
					Liferay.Util.openToast({
						message: Liferay.Language.get(
							'an-unexpected-error-occurred'
						),
						type: 'danger',
					});

					return;
				}

				Liferay.Util.navigate(backURL);
			}}
			validate={(values) => {
				const errors: Partial<
					Record<keyof StaticSiteExportFormValues, string>
				> = {};

				if (!values.languageIds.length) {
					errors.languageIds = Liferay.Language.get(
						'this-field-is-required'
					);
				}

				if (!values.name) {
					errors.name = Liferay.Language.get(
						'this-field-is-required'
					);
				}

				return errors;
			}}
			validateOnMount
		>
			{(formik) => (
				<Form noValidate>
					<Setup
						placeholder={Liferay.Language.get('add-an-export-name')}
						title={sub(
							Liferay.Language.get('x-details'),
							Liferay.Language.get('static-site')
						)}
					/>

					<SectionHeader title={Liferay.Language.get('languages')} />

					<ClayLayout.Sheet>
						<ClayMultiSelect
							allowsCustomLabel={false}
							aria-label={Liferay.Language.get('languages')}
							items={availableLanguages.filter((language) =>
								formik.values.languageIds.includes(
									language.value
								)
							)}
							onChange={setLanguageInputValue}
							onItemsChange={(languages: Language[]) =>
								formik.setFieldValue(
									'languageIds',
									languages.map((language) => language.value)
								)
							}
							placeholder={Liferay.Language.get(
								'select-languages'
							)}
							sourceItems={availableLanguages.filter(
								(language) =>
									!formik.values.languageIds.includes(
										language.value
									)
							)}
							value={languageInputValue}
						>
							{(language: Language) => (
								<ClayMultiSelect.Item
									key={language.value}
									textValue={language.label}
								>
									<ClayIcon
										className="inline-item inline-item-before"
										symbol={language.value
											.replace(/_/g, '-')
											.toLowerCase()}
									/>

									{language.label}
								</ClayMultiSelect.Item>
							)}
						</ClayMultiSelect>
					</ClayLayout.Sheet>

					<SectionHeader title={Liferay.Language.get('pages')} />

					<ClayLayout.Sheet>
						<ClayCheckbox
							checked={formik.values.exportAllPages}
							label={Liferay.Language.get('export-all-pages')}
							onChange={() =>
								formik.setValues({
									...formik.values,
									exportAllPages:
										!formik.values.exportAllPages,
									layoutIds: undefined,
								})
							}
						/>

						{!formik.values.exportAllPages && (
							<div className="align-items-center d-flex justify-content-between mt-3">
								<span className="small">
									{formik.values.layoutIds
										? sub(
												Liferay.Language.get(
													'x-selected'
												),
												formik.values.layoutIds.length
											)
										: Liferay.Language.get('all-pages')}
								</span>

								<ClayButton
									className="font-weight-semi-bold"
									displayType="link"
									onClick={() => setShowModal(true)}
									size="sm"
								>
									{Liferay.Language.get('select-layouts')}
								</ClayButton>
							</div>
						)}
					</ClayLayout.Sheet>

					{showModal && (
						<PageTreeModal
							groupId={pageTreeModalConfiguration.groupId}
							initialAll={!formik.values.layoutIds}
							initialSelectedIds={(
								formik.values.layoutIds ?? []
							).map(String)}
							onClose={() => setShowModal(false)}
							onSubmit={(result) => {
								setShowModal(false);

								if (result) {
									formik.setFieldValue(
										'layoutIds',
										result.layoutIds
									);
								}
							}}
							pageSize={pageTreeModalConfiguration.pageSize}
							privateLayout={false}
						/>
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
			)}
		</Formik>
	);
}
