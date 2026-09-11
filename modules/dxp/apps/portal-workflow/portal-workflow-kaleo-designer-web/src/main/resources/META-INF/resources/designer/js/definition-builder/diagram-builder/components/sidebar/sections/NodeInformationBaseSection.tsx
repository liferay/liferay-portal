/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm, {ClayInput} from '@clayui/form';
import React from 'react';

import {defaultLanguageId} from '../../../../constants';
import HelpIcon from './shared-components/HelpIcon';
import {checkIdErrors, checkLabelErrors, getUpdatedLabelItem} from './utils';

import type {Elements, Node} from 'react-flow-renderer';

import type {NodeInformationError} from './utils';

interface NodeInformationBaseSection {
	elements: Elements;
	errors: NodeInformationError;
	selectedItem: Node;
	selectedItemNewId: string | null;
	selectedLanguageId: Liferay.Language.Locale | '';
	setErrors: (value: NodeInformationError) => void;
	setSelectedItem: (value: Node) => void;
	setSelectedItemNewId: (value: string | null) => void;
}

export function NodeInformationBaseSection({
	elements,
	errors,
	selectedItem,
	selectedItemNewId,
	selectedLanguageId,
	setErrors,
	setSelectedItem,
	setSelectedItemNewId,
}: NodeInformationBaseSection) {
	return (
		<>
			<ClayForm.Group className={errors.label ? 'has-error' : ''}>
				<label htmlFor="workflowDefinitionBaseNodeLabel">
					{Liferay.Language.get('label')}

					<span className="ml-1 mr-1 text-warning">*</span>

					<HelpIcon message={Liferay.Language.get('label-name')} />
				</label>

				<ClayInput
					id="workflowDefinitionBaseNodeLabel"
					onChange={({target}) => {
						setErrors(checkLabelErrors(errors, target));

						const key =
							selectedLanguageId !== ''
								? selectedLanguageId
								: defaultLanguageId;

						setSelectedItem(
							getUpdatedLabelItem(key, selectedItem, target)
						);
					}}
					type="text"
					value={
						(selectedLanguageId
							? selectedItem?.data.label[selectedLanguageId]
							: selectedItem?.data.label[defaultLanguageId]) || ''
					}
				/>

				<ClayForm.FeedbackItem>
					{errors.label && (
						<>
							<ClayForm.FeedbackIndicator symbol="exclamation-full" />

							{Liferay.Language.get('this-field-is-required')}
						</>
					)}
				</ClayForm.FeedbackItem>
			</ClayForm.Group>

			<ClayForm.Group
				className={
					errors.id instanceof Object &&
					(errors?.id?.duplicated || errors?.id?.empty)
						? 'has-error'
						: ''
				}
			>
				<label htmlFor="workflowDefinitionBaseNodeName">
					<span>
						{`${Liferay.Language.get(
							'node'
						)} ${Liferay.Language.get('name')}`}
					</span>

					<span className="ml-1 mr-1 text-warning">*</span>

					<HelpIcon
						message={Liferay.Language.get(
							'name-is-the-node-identifier'
						)}
					/>
				</label>

				<ClayInput
					id="workflowDefinitionBaseNodeName"
					onChange={({target}) => {
						const filteredElements = elements.slice();

						filteredElements.splice(
							elements.findIndex(
								(element) => element.id === selectedItem.id
							),
							1
						);

						setErrors(
							checkIdErrors(filteredElements, errors, target)
						);

						setSelectedItemNewId(target.value);
					}}
					type="text"
					value={(selectedItemNewId ?? selectedItem?.id) || ''}
				/>

				<ClayForm.FeedbackItem>
					{errors.id instanceof Object &&
						(errors?.id?.duplicated || errors?.id?.empty) && (
							<>
								<ClayForm.FeedbackIndicator symbol="exclamation-full" />

								{errors.id.duplicated
									? Liferay.Language.get(
											'a-node-with-that-name-already-exists'
										)
									: Liferay.Language.get(
											'this-field-is-required'
										)}
							</>
						)}
				</ClayForm.FeedbackItem>
			</ClayForm.Group>

			<ClayForm.Group>
				<label htmlFor="workflowDefinitionBaseNodeDescription">
					{Liferay.Language.get('description')}
				</label>

				<ClayInput
					component="textarea"
					id="workflowDefinitionBaseNodeDescription"
					onChange={({target}) =>
						setSelectedItem({
							...selectedItem,
							data: {
								...selectedItem.data,
								description: target.value,
							},
						})
					}
					type="text"
					value={selectedItem?.data.description || ''}
				/>
			</ClayForm.Group>
		</>
	);
}
