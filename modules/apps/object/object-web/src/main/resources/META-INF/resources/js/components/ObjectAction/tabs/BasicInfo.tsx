/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm from '@clayui/form';
import {
	Card,
	FormError,
	Input,
	Toggle,
} from '@liferay/object-js-components-web';
import {InputLocalized} from 'frontend-js-components-web';
import React from 'react';

import {defaultLanguageId} from '../../../utils/constants';
import {toCamelCase} from '../../../utils/string';
import {DisabledGroovyScriptAlert} from '../../DisabledGroovyScriptAlert';

import './ActionBuilder.scss';

interface BasicInfoProps {
	disableGroovyAction: boolean;
	errors: FormError<ObjectAction & ObjectActionParameters>;
	handleChange: React.ChangeEventHandler<HTMLInputElement>;
	isApproved: boolean;
	readOnly?: boolean;
	scriptManagementConfigurationPortletURL: string;
	setValues: (values: Partial<ObjectAction>) => void;
	values: Partial<ObjectAction>;
}

export default function BasicInfo({
	disableGroovyAction,
	errors,
	handleChange,
	isApproved,
	readOnly,
	scriptManagementConfigurationPortletURL,
	setValues,
	values,
}: BasicInfoProps) {
	return (
		<>
			{disableGroovyAction && (
				<DisabledGroovyScriptAlert
					scriptManagementConfigurationPortletURL={
						scriptManagementConfigurationPortletURL
					}
				/>
			)}

			<Card title={Liferay.Language.get('basic-info')}>
				<InputLocalized
					error={errors.label}
					label={Liferay.Language.get('action-label')}
					name="label"
					onChange={(label) =>
						setValues({
							...values,
							...(!isApproved &&
								!values.system && {
									name: toCamelCase(
										label[defaultLanguageId] ?? ''
									),
								}),
							label,
						})
					}
					required
					translations={values.label ?? {[defaultLanguageId]: ''}}
				/>

				<Input
					disabled={isApproved || values.system}
					error={errors.name}
					id="actionNameInput"
					label={Liferay.Language.get('action-name')}
					name="name"
					onChange={handleChange}
					required
					value={values.name}
				/>

				<InputLocalized
					component="textarea"
					disabled={values.system}
					error={errors.description}
					helpMessage={Liferay.Language.get(
						'provide-descriptive-text-used-only-by-ai-agents-and-api-consumers'
					)}
					id="actionDescriptionInput"
					label={Liferay.Language.get('description')}
					onChange={(description) => setValues({description})}
					placeholder=""
					translations={
						(values.description ?? {}) as LocalizedValue<string>
					}
				/>

				<ClayForm.Group>
					<Toggle
						disabled={
							readOnly || values.system || disableGroovyAction
						}
						label={Liferay.Language.get('active')}
						name="indexed"
						onToggle={(active) => setValues({active})}
						toggled={values.active}
					/>
				</ClayForm.Group>
			</Card>
		</>
	);
}
