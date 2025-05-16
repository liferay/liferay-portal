/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayModal from '@clayui/modal';
import {FormikHelpers, useFormik} from 'formik';
import {navigate, sub} from 'frontend-js-web';
import React from 'react';

import {AssetData} from '../../FDSPropsTransformer/actions/createAssetAction';
import {FolderData} from '../../FDSPropsTransformer/actions/createFolderAction';
import {FieldPicker, FieldText} from '../forms';
import {required, validate} from '../forms/validations';

export type AssetLibrary = {
	groupId: string;
	name: string;
};

type Props = {
	action: AssetData['action'] | FolderData['action'];
	assetLibraries: AssetLibrary[];
	closeModal: () => void;
	onSubmit?: (
		values: {
			groupId: string;
			name: string;
		},
		formikHelpers: FormikHelpers<{
			groupId: string;
			name: string;
		}>
	) => Promise<any> | void;
	redirect?: string;
	title: string;
};

export default function CreationModalContent({
	action,
	assetLibraries,
	closeModal,
	onSubmit,
	redirect,
	title,
}: Props) {
	const {
		errors,
		handleChange,
		handleSubmit,
		isSubmitting,
		setFieldValue,
		touched,
		values,
	} = useFormik({
		initialValues: {
			groupId:
				assetLibraries.length === 1 ? assetLibraries[0].groupId : '',
			name: '',
		},
		onSubmit: async (values, formikHelpers) => {
			if (redirect) {
				const {groupId, name} = values;

				const url = new URL(redirect);

				url.searchParams.set('name', name);
				url.searchParams.set('groupId', groupId);

				navigate(url.pathname + url.search);

				return;
			}

			if (onSubmit) {
				await onSubmit(values, formikHelpers);
			}
		},
		validate: (values) =>
			validate(
				{
					groupId: [required],
					name: action === 'createFolder' ? [required] : [],
				},
				values
			),
	});

	return (
		<form onSubmit={handleSubmit}>
			<ClayModal.Header>{title}</ClayModal.Header>

			<ClayModal.Body>
				{action === 'createFolder' ? (
					<FieldText
						errorMessage={touched.name ? errors.name : undefined}
						label={Liferay.Language.get('name')}
						name="name"
						onChange={handleChange}
						required
						value={values.name}
					/>
				) : null}

				{assetLibraries.length > 1 && (
					<FieldPicker
						errorMessage={
							touched.groupId ? errors.groupId : undefined
						}
						helpMessage={sub(
							Liferay.Language.get('choose-the-space-for-the-x'),
							title
						)}
						items={assetLibraries.map(({groupId, name}) => ({
							label: name,
							value: groupId,
						}))}
						label={Liferay.Language.get('space')}
						name="groupId"
						onSelectionChange={(value: string) => {
							setFieldValue('groupId', value);
						}}
						placeholder={Liferay.Language.get('select-a-space')}
						required
						selectedKey={values.groupId}
					/>
				)}
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							displayType="secondary"
							onClick={closeModal}
							type="button"
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							disabled={isSubmitting}
							displayType="primary"
							type="submit"
						>
							{Liferay.Language.get('save')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</form>
	);
}
