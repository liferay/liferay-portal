/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm from '@clayui/form';
import {
	FormError,
	SingleSelect,
	Toggle,
} from '@liferay/object-js-components-web';
import {sub} from 'frontend-js-web';
import React, {useEffect, useState} from 'react';

import {defaultLanguageId} from '../../utils/constants';

interface AccountRestrictionContainerProps {
	errors: FormError<ObjectDefinition>;
	isApproved: boolean;
	isLinkedObjectDefinition?: boolean;
	objectFields: ObjectField[];
	onSubmit?: (editedObjectDefinition?: Partial<ObjectDefinition>) => void;
	setValues: (values: Partial<ObjectDefinition>) => void;
	values: Partial<ObjectDefinition>;
}

export function AccountRestrictionContainer({
	errors,
	isApproved,
	isLinkedObjectDefinition,
	objectFields,
	onSubmit,
	setValues,
	values,
}: AccountRestrictionContainerProps) {
	const [accountRelationshipFields, setAccountRelationshipFields] = useState<
		LabelValueObject[]
	>([]);

	const [disableAccountToggle, setDisableAccountToggle] =
		useState<boolean>(false);
	const [disableAccountSelect, setDisableAccountSelect] =
		useState<boolean>(false);

	useEffect(() => {
		const accountRelationshipFieldsResponse = objectFields.filter(
			(field) => {
				if (values.storageType && values.storageType !== 'default') {
					return (
						field.businessType === 'Integer' ||
						field.businessType === 'LongInteger' ||
						field.businessType === 'Text'
					);
				}

				return (
					field.businessType === 'Relationship' &&
					field.objectFieldSettings?.find(
						(fieldSetting) => fieldSetting.value === 'AccountEntry'
					)
				);
			}
		);

		if (accountRelationshipFieldsResponse.length) {
			setAccountRelationshipFields(
				accountRelationshipFieldsResponse.map(
					(accountRelationshipField) => {
						return {
							label: accountRelationshipField.label[
								defaultLanguageId
							] as string,
							value: accountRelationshipField.name,
						};
					}
				)
			);

			if (isApproved && values.accountEntryRestricted) {
				setDisableAccountToggle(true);
			}

			if (isApproved && values.accountEntryRestrictedObjectFieldName) {
				setDisableAccountSelect(true);
			}
		}

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [objectFields]);

	return (
		<>
			<ClayForm.Group>
				<Toggle
					disabled={
						!accountRelationshipFields.length ||
						disableAccountToggle ||
						isLinkedObjectDefinition
					}
					label={sub(
						Liferay.Language.get('enable-x'),
						Liferay.Language.get('account-restriction')
					)}
					name="accountEntryRestricted"
					onBlur={(event) => {
						event.stopPropagation();

						if (onSubmit) {
							onSubmit();
						}
					}}
					onToggle={() =>
						setValues({
							accountEntryRestricted:
								!values.accountEntryRestricted,
							accountEntryRestrictedObjectFieldName:
								!values.accountEntryRestricted === false
									? ''
									: values.accountEntryRestrictedObjectFieldName,
						})
					}
					toggled={values.accountEntryRestricted}
				/>
			</ClayForm.Group>

			<SingleSelect<LabelValueObject>
				disabled={
					!accountRelationshipFields.length ||
					!values.accountEntryRestricted ||
					disableAccountSelect ||
					isLinkedObjectDefinition
				}
				error={errors.accountEntryRestrictedObjectFieldName}
				items={accountRelationshipFields}
				label={Liferay.Language.get(
					'account-entry-restricted-object-field-id'
				)}
				onSelectionChange={(value) => {
					setValues({
						accountEntryRestrictedObjectFieldName: value as string,
					});

					if (onSubmit) {
						onSubmit({
							...values,
							accountEntryRestrictedObjectFieldName:
								value as string,
						});
					}
				}}
				required={
					!!accountRelationshipFields.length &&
					values.accountEntryRestricted &&
					!disableAccountSelect
				}
				selectedKey={values.accountEntryRestrictedObjectFieldName}
			/>
		</>
	);
}
