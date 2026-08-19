/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayForm, {ClayInput} from '@clayui/form';
import ClayPanel from '@clayui/panel';
import {useFormik} from 'formik';
import {openToast, useId} from 'frontend-js-components-web';
import {escapeHTML, navigate, sub} from 'frontend-js-web';
import React, {useEffect, useState} from 'react';

import DesignLibraryService from '../../services/DesignLibraryService';
import {DesignLibrary} from '../../types';
import {FieldText} from '../forms';
import {Errors, maxLength, required, validate} from '../forms/validations';
import focusInvalidElement from '../utils/focusInvalidElement';
interface DesignLibrarySettingsProps {
	backURL: string;
	externalReferenceCode: string;
	groupId: string;
	portletId: string;
}

export default function DesignLibrarySettings({
	backURL,
	externalReferenceCode,
	groupId,
	portletId,
}: DesignLibrarySettingsProps) {
	const id = useId();

	const [designLibrary, setDesignLibrary] = useState<DesignLibrary | null>(
		null
	);

	useEffect(() => {
		DesignLibraryService.get(externalReferenceCode).then(setDesignLibrary);
	}, [externalReferenceCode]);

	const {
		errors,
		handleBlur,
		handleChange,
		handleSubmit,
		isSubmitting,
		submitForm,
		touched,
		values,
	} = useFormik({
		enableReinitialize: true,
		initialValues: {
			description: designLibrary?.description ?? '',
			erc: designLibrary?.externalReferenceCode ?? '',
			name: designLibrary?.name ?? '',
		},
		onSubmit: async (values) => {
			const {description, erc, name} = values;
			try {
				const data = await DesignLibraryService.update(
					designLibrary?.externalReferenceCode as string,
					{
						description,
						externalReferenceCode: erc,
						name,
					}
				);

				openToast({
					message: sub(
						Liferay.Language.get('x-was-saved-successfully'),
						`<strong>${escapeHTML(name as string)}</strong>`
					),
					type: 'success',
				});

				const updatedDesignLibrary = data as DesignLibrary;

				setDesignLibrary(updatedDesignLibrary);

				if (name !== designLibrary?.name) {
					Liferay.Portlet.refresh(`#p_p_id_${portletId}_`);
				}
			}
			catch (error: any) {
				const errorMessage =
					error?.title ??
					error?.message ??
					Liferay.Language.get(
						'an-unexpected-error-occurred-while-saving-the-design-library'
					);

				openToast({
					message: errorMessage,
					type: 'danger',
				});
			}
		},
		validate: (values): Errors =>
			validate(
				{
					erc: [required],
					name: [required, maxLength(75)],
				},
				values,
				errors
			),
	});

	const shouldDisableSaveButton = isSubmitting || !values.name;

	const onSave = () => {
		if (Object.keys(errors).length) {
			focusInvalidElement();

			return;
		}

		submitForm();
	};

	const onCancel = () => {
		if (backURL) {
			navigate(backURL);
		}
		else {
			window.history.back();
		}
	};

	if (!designLibrary) {
		return null;
	}

	return (
		<form
			className="container-fluid container-fluid-max-md p-0 p-md-4"
			onSubmit={handleSubmit}
		>
			<ClayPanel
				aria-label={Liferay.Language.get('settings')}
				className="mb-4"
				collapsable
				defaultExpanded={true}
				displayTitle={
					<ClayPanel.Title>
						<h2 className="mb-0 py-2 text-6 text-dark">
							{Liferay.Language.get('settings')}
						</h2>
					</ClayPanel.Title>
				}
				displayType="secondary"
				role="group"
				showCollapseIcon
			>
				<div className="pt-4 px-4">
					<FieldText
						errorMessage={touched.name ? errors?.name : undefined}
						label={Liferay.Language.get('name')}
						name="name"
						onBlur={handleBlur}
						onChange={handleChange}
						placeholder={Liferay.Language.get(
							'enter-a-design-library-name'
						)}
						required
						value={values.name}
					/>

					<FieldText
						component="textarea"
						label={Liferay.Language.get('description')}
						name="description"
						onBlur={handleBlur}
						onChange={handleChange}
						value={values.description}
					/>

					<ClayForm.Group>
						<label htmlFor={`${id}groupId`}>
							{Liferay.Language.get('group-id')}
						</label>

						<ClayInput
							id={`${id}groupId`}
							readOnly
							value={groupId}
						/>
					</ClayForm.Group>

					<FieldText
						errorMessage={touched.erc ? errors?.erc : undefined}
						helpIcon={Liferay.Language.get(
							'unique-key-for-referencing-the-design-library-definition'
						)}
						label={Liferay.Language.get('erc')}
						name="erc"
						onBlur={handleBlur}
						onChange={handleChange}
						required
						value={values.erc}
					/>
				</div>
			</ClayPanel>

			<ClayButton.Group className="mt-2" spaced>
				<ClayButton disabled={shouldDisableSaveButton} onClick={onSave}>
					{Liferay.Language.get('save')}
				</ClayButton>

				<ClayButton displayType="secondary" onClick={onCancel}>
					{Liferay.Language.get('cancel')}
				</ClayButton>
			</ClayButton.Group>
		</form>
	);
}
