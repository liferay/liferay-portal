/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import {ClayDropDownWithItems} from '@clayui/drop-down';
import ClayForm, {ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import classNames from 'classnames';
import {FieldFeedback, useId} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import React, {useState} from 'react';

import {useCache} from '../contexts/CacheContext';
import {useSelector, useStateDispatch} from '../contexts/StateContext';
import selectPublishedFields from '../selectors/selectPublishedFields';
import selectValidationErrors from '../selectors/selectValidationErrors';
import {Field, MultiselectField, SingleSelectField} from '../utils/field';
import AsyncPicker from './AsyncPicker';

export default function PicklistPicker({
	disabled,
	field,
}: {
	disabled?: boolean;
	field: Field;
}) {
	const selectField = field as SingleSelectField | MultiselectField;

	const [selectedKey, setSelectedKey] = useState<React.Key>(
		selectField.picklistId
	);

	const dispatch = useStateDispatch();
	const publishedFields = useSelector(selectPublishedFields);
	const validationErrors = useSelector(selectValidationErrors(field.uuid));

	const {data: picklists, load: loadPicklist, status} = useCache('picklists');

	const feedbackId = useId();
	const pickerId = useId();

	const hasError = validationErrors.has('no-picklist');
	const isPublished = publishedFields.has(field.uuid);

	return (
		<ClayForm.Group className={classNames('mb-2', {'has-error': hasError})}>
			<ClayInput.Group className="align-items-end">
				<ClayInput.GroupItem>
					<label htmlFor={pickerId}>
						{Liferay.Language.get('picklist')}

						<ClayIcon
							className="ml-1 reference-mark"
							symbol="asterisk"
						/>
					</label>

					<AsyncPicker
						aria-describedby={feedbackId}
						disabled={isPublished || !picklists.length || disabled}
						id={pickerId}
						items={picklists}
						loader={loadPicklist}
						onBlur={(
							event: React.FocusEvent<HTMLButtonElement>
						) => {
							const noOptionSelected = !picklists.some(
								(picklist) =>
									picklist.id ===
									Number(event.relatedTarget?.id)
							);

							if (!selectedKey && noOptionSelected) {
								dispatch({
									error: 'no-picklist',
									type: 'add-validation-error',
									uuid: field.uuid,
								});
							}
						}}
						onSelectionChange={(selectedKey: React.Key) => {
							dispatch({
								picklistId: Number(selectedKey),
								type: 'update-field',
								uuid: field.uuid,
							});

							setSelectedKey(selectedKey);
						}}
						placeholder={sub(
							Liferay.Language.get('select-x'),
							Liferay.Language.get('picklist')
						)}
						selectedKey={selectedKey}
						status={status}
					/>
				</ClayInput.GroupItem>

				<ClayInput.GroupItem shrink>
					{selectedKey ? (
						<ClayDropDownWithItems
							items={[
								{
									href: `picklist-builder?listTypeDefinitionId=${selectedKey}`,
									label: Liferay.Language.get('edit'),
									symbolLeft: 'pencil',
									target: '_blank',
								},
								{type: 'divider'},
								{
									href: 'picklist-builder',
									label: Liferay.Language.get('new-picklist'),
									symbolRight: 'shortcut',
									target: '_blank',
								},
							]}
							trigger={
								<ClayButtonWithIcon
									aria-label={Liferay.Language.get(
										'more-actions'
									)}
									displayType="secondary"
									symbol="ellipsis-v"
									title={Liferay.Language.get('more-actions')}
								/>
							}
						/>
					) : (
						<ClayLink
							button
							displayType="secondary"
							href="picklist-builder"
							target="_blank"
						>
							{Liferay.Language.get('new-picklist')}

							<ClayIcon className="ml-2" symbol="shortcut" />
						</ClayLink>
					)}
				</ClayInput.GroupItem>
			</ClayInput.Group>

			{hasError ? (
				<FieldFeedback
					errorMessage={Liferay.Language.get(
						'this-field-is-required'
					)}
					id={feedbackId}
				/>
			) : null}
		</ClayForm.Group>
	);
}
