/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import ClayForm, {
	ClayDualListBox,
	ClayRadio,
	ClayRadioGroup,
} from '@clayui/form';
import ClayPanel from '@clayui/panel';
import {useFormik} from 'formik';
import {openToast} from 'frontend-js-components-web';
import {navigate} from 'frontend-js-web';
import React, {useState} from 'react';

import {FieldPicker} from '../../common/components/forms';
import {Errors} from '../../common/components/forms/validations';
import SpaceService from '../../common/services/SpaceService';
import {LabelValueObject, Space} from '../../common/types/Space';
import SpacePanel from './SpacePanel';

export default function SpaceLanguageSettings({
	backURL,
	companyAvailableLanguages,
	setSpace,
	space,
}: {
	backURL?: string;
	companyAvailableLanguages: LabelValueObject[];
	setSpace?: React.Dispatch<React.SetStateAction<any>>;
	space: Space;
}) {
	const [defaultLanguageWarning, setDefaultLanguageWarning] =
		useState<boolean>(false);

	const {
		errors,
		handleSubmit,
		resetForm,
		setFieldValue,
		setValues,
		touched,
		values,
	} = useFormik({
		initialValues: {
			availableLanguageIds: space.settings?.availableLanguageIds ?? [],
			availableLanguages:
				companyAvailableLanguages.filter(
					(availableLanguage) =>
						!space.settings?.availableLanguageIds?.includes(
							availableLanguage.value
						)
				) || [],
			defaultLanguageId: space.settings?.defaultLanguageId ?? '',
			selectedLanguages:
				companyAvailableLanguages.filter((availableLanguage) =>
					space.settings?.availableLanguageIds?.includes(
						availableLanguage.value
					)
				) || [],
			useCustomLanguages: !!space.settings?.useCustomLanguages,
		},
		onSubmit: async (values) => {
			const {
				availableLanguageIds,
				defaultLanguageId,
				useCustomLanguages,
			} = values;

			const {data, error} = await SpaceService.updateSpace(
				space.externalReferenceCode,
				{
					externalReferenceCode: space.externalReferenceCode,
					settings: {
						availableLanguageIds,
						defaultLanguageId,
						useCustomLanguages,
					},
				}
			);

			if (error) {
				openToast({
					message: Liferay.Language.get(
						'an-unexpected-error-occurred-while-saving-the-space'
					),
					type: 'danger',
				});
			}
			else if (data) {
				openToast({
					message: Liferay.Util.sub(
						Liferay.Language.get('x-was-saved-successfully'),
						space.name
					),
					type: 'success',
				});

				if (setSpace) {
					setSpace(data);
				}
			}
		},
		validate: (values): Errors => {
			const errors: any = {};

			if (values.useCustomLanguages && !values.defaultLanguageId) {
				errors.defaultLanguageId = Liferay.Language.get(
					'default-language-is-required'
				);
			}

			return errors;
		},
	});

	const onCancel = () => {
		if (backURL) {
			navigate(backURL);
		}
		else {
			window.history.back();
		}
	};

	const handleItemsChange = (items: LabelValueObject[][]) => {
		const [nextAvailableLanguages, nextSelectedLanguages] = items;

		const removingDefaultLanguage = nextAvailableLanguages.some(
			(language) => language.value === values.defaultLanguageId
		);

		if (removingDefaultLanguage) {
			setDefaultLanguageWarning(true);
		}
		else {
			setValues({
				...values,
				availableLanguageIds: nextSelectedLanguages.map(
					(language) => language.value
				),
				availableLanguages: nextAvailableLanguages,
				selectedLanguages: nextSelectedLanguages,
			});

			setDefaultLanguageWarning(false);
		}
	};

	return (
		<form
			className="container-fluid container-fluid-max-md p-0 p-md-4"
			onSubmit={handleSubmit}
		>
			<SpacePanel title={Liferay.Language.get('languages')}>
				<p>
					{Liferay.Language.get(
						'select-the-language-configuration-for-the-space'
					)}
				</p>

				<ClayForm.Group>
					<ClayRadioGroup
						name="useCustomLanguages"
						onChange={(value: any) => {
							if (value === 'false') {
								resetForm();
							}

							setFieldValue(
								'useCustomLanguages',
								JSON.parse(value)
							);
						}}
						value={String(values.useCustomLanguages)}
					>
						<ClayRadio
							label={Liferay.Language.get(
								'use-the-default-language-options'
							)}
							value="false"
						/>

						<ClayRadio
							label={Liferay.Language.get(
								'define-a-custom-default-language-and-additional-active-languages-for-this-space'
							)}
							value="true"
						/>
					</ClayRadioGroup>
				</ClayForm.Group>

				{values.useCustomLanguages && (
					<ClayPanel
						aria-label={Liferay.Language.get(
							'custom-default-language'
						)}
						collapsable
						defaultExpanded={true}
						displayTitle={Liferay.Language.get(
							'custom-default-language'
						)}
						displayType="default"
						role="group"
						showCollapseIcon
					>
						<ClayPanel.Body>
							{defaultLanguageWarning && (
								<ClayAlert
									autoClose
									displayType="danger"
									onClose={() => {
										setDefaultLanguageWarning(false);
									}}
									title={Liferay.Language.get('error')}
								>
									{Liferay.Language.get(
										'you-cannot-remove-a-language-that-is-the-current-default-language'
									)}
								</ClayAlert>
							)}

							<FieldPicker
								errorMessage={
									touched.defaultLanguageId
										? errors.defaultLanguageId
										: undefined
								}
								items={values.selectedLanguages}
								label={Liferay.Language.get('default-language')}
								name="defaultLanguageId"
								onSelectionChange={(value: string) =>
									setFieldValue('defaultLanguageId', value)
								}
								selectedKey={values.defaultLanguageId}
							/>

							<ClayDualListBox
								disableLTR={!values.availableLanguages.length}
								disableRTL={
									values.selectedLanguages.length === 1
								}
								items={[
									values.availableLanguages,
									values.selectedLanguages,
								]}
								left={{
									id: 'availableLanguages',
									label: Liferay.Language.get('available'),
								}}
								onItemsChange={handleItemsChange}
								right={{
									id: 'selectedLanguages',
									label: Liferay.Language.get('selected'),
								}}
								size={10}
							/>
						</ClayPanel.Body>
					</ClayPanel>
				)}
			</SpacePanel>

			<ClayButton.Group className="mt-2" spaced>
				<ClayButton type="submit">
					{Liferay.Language.get('save')}
				</ClayButton>

				<ClayButton displayType="secondary" onClick={onCancel}>
					{Liferay.Language.get('cancel')}
				</ClayButton>
			</ClayButton.Group>
		</form>
	);
}
