/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import ClayForm from '@clayui/form';
import {Item} from '@clayui/multi-select/lib/types';
import ClayToolbar from '@clayui/toolbar';
import {useFormik} from 'formik';
import {openToast} from 'frontend-js-components-web';
import {navigate, sub} from 'frontend-js-web';
import React, {useEffect, useState} from 'react';

import FolderService, {TFolder} from '../../../services/FolderService';
import {FieldPicker, FieldText} from '../forms';
import {required, validate} from '../forms/validations';

interface EditFolderProps {
	backURL: string;
	folderId: string;
}

const EditFolder: React.FC<EditFolderProps> = ({backURL, folderId}) => {
	const [folderData, setFolderData] = useState<
		Pick<TFolder, 'description' | 'scopeKey' | 'title'>
	>({description: '', scopeKey: '', title: ''});
	const [isLoading, setIsLoading] = useState<boolean>(true);

	useEffect(() => {
		const fetchFolderData = async () => {
			setIsLoading(true);
			try {
				const response = await FolderService.getFolder(folderId);

				setFolderData(response);
			}
			catch (error: any) {
				throw new Error(
					error.message || Liferay.Language.get('an-error-occurred')
				);
			}
			finally {
				setIsLoading(false);
			}
		};

		fetchFolderData();
	}, [folderId]);

	const spaceItems: Item[] = folderData
		? [{label: folderData.scopeKey, value: folderData.scopeKey}]
		: [];

	const {
		errors,
		handleChange,
		handleSubmit,
		isSubmitting,
		setValues,
		values,
	} = useFormik({
		initialValues: {
			folderDescription: folderData.description,
			folderName: folderData.title,
			folderSpace: folderData.scopeKey,
		},
		onSubmit: async (formValues) => {
			const newFolderValues: TFolder = {
				description: formValues.folderDescription,
				id: parseInt(folderId, 10),
				title: formValues.folderName,
			};

			const {error} = await FolderService.updateFolder(newFolderValues);

			if (!error) {
				navigate(backURL);

				openToast({
					message: sub(
						Liferay.Language.get('x-was-updated-successfully'),
						`<strong>${formValues.folderName}</strong>`
					),
					type: 'success',
				});
			}
			else {
				openToast({
					message: error,
					type: 'danger',
				});
			}
		},
		validate: (values) =>
			validate(
				{
					folderName: [required],
				},
				values
			),
	});

	useEffect(() => {
		if (folderData) {
			setValues({
				folderDescription: folderData.description,
				folderName: folderData.title,
				folderSpace: folderData.scopeKey,
			});
		}
	}, [folderData, setValues]);

	return (
		<div className="edit-folder">
			<ClayToolbar className="container-fluid" light>
				<ClayToolbar.Nav>
					<ClayToolbar.Item>
						<ClayButtonWithIcon
							aria-label={Liferay.Language.get('back')}
							borderless
							displayType="secondary"
							monospaced
							onClick={() => navigate(backURL)}
							size="sm"
							symbol="angle-left"
							title={Liferay.Language.get('back')}
						/>
					</ClayToolbar.Item>

					<ClayToolbar.Item className="text-left" expand>
						<h2 className="font-weight-semi-bold m-0 text-5">
							{isLoading ? (
								<span className="loading-animation"></span>
							) : (
								folderData?.title
							)}
						</h2>
					</ClayToolbar.Item>

					<ClayToolbar.Item>
						<ClayButton
							borderless
							displayType="secondary"
							onClick={() => navigate(backURL)}
							size="sm"
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>
					</ClayToolbar.Item>

					<ClayToolbar.Item>
						<ClayButton
							disabled={isSubmitting}
							displayType="primary"
							form="formEditFolder"
							size="sm"
							type="submit"
						>
							{isSubmitting && (
								<span className="inline-item inline-item-before">
									<span
										aria-hidden="true"
										className="loading-animation"
									></span>
								</span>
							)}

							{Liferay.Language.get('save')}
						</ClayButton>
					</ClayToolbar.Item>
				</ClayToolbar.Nav>
			</ClayToolbar>

			<div className="container-fluid container-fluid-max-md mt-4">
				<ClayForm id="formEditFolder" onSubmit={handleSubmit}>
					<h3 className="font-weight-semi-bold mb-4 text-6">
						{Liferay.Language.get('basic-info')}
					</h3>

					<FieldText
						errorMessage={errors.folderName}
						label={Liferay.Language.get('name')}
						name="folderName"
						onChange={handleChange}
						required
						type="input"
						value={values.folderName}
					/>

					<FieldPicker
						disabled
						items={spaceItems}
						label={Liferay.Language.get('space')}
						name="folderSpace"
						required
						selectedKey={values.folderSpace}
					/>

					<FieldText
						label={Liferay.Language.get('description')}
						name="folderDescription"
						onChange={handleChange}
						type="textarea"
						value={values.folderDescription}
					/>
				</ClayForm>
			</div>
		</div>
	);
};

export default EditFolder;
