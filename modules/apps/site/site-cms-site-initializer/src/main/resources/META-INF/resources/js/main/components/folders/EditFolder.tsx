/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import ClayForm from '@clayui/form';
import ClayMultiSelect from '@clayui/multi-select';
import {Item} from '@clayui/multi-select/lib/types';
import ClayToolbar from '@clayui/toolbar';
import {useFormik} from 'formik';
import {navigate} from 'frontend-js-web';
import React from 'react';

import {FieldText} from '../forms';
import FieldWrapper from '../forms/FieldWrapper';
import {required, validate} from '../forms/validations';

interface EditFolderProps {
	backURL: string;
	description?: string;
	name: string;
	space: string;
}

const EditFolder: React.FC<EditFolderProps> = ({
	backURL,
	description,
	name,
	space,
}) => {
	const spaceItems: Item[] = [{label: space, value: space}];

	const {errors, handleChange, handleSubmit, values} = useFormik({
		initialValues: {
			folderDescription: description || '',
			folderName: name,
			folderSpace: space,
		},
		onSubmit: (values) => {
			console.log('Form submitted:', values);
		},
		validate: (values) =>
			validate(
				{
					folderName: [required],
				},
				values
			),
	});

	const handleOnSaveClick = () => {
		handleSubmit();
	};

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
							{name}
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
							displayType="primary"
							onClick={handleOnSaveClick}
							size="sm"
						>
							{Liferay.Language.get('save')}
						</ClayButton>
					</ClayToolbar.Item>
				</ClayToolbar.Nav>
			</ClayToolbar>

			<div className="container-fluid container-fluid-max-md mt-4">
				<ClayForm>
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

					<FieldWrapper
						disabled
						fieldId="folderSpace"
						label={Liferay.Language.get('space')}
						required
					>
						<ClayMultiSelect
							aria-required={true}
							disabled
							id="folderSpace"
							items={spaceItems}
						/>
					</FieldWrapper>

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
