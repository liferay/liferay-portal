/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayForm, {ClayInput, ClayToggle} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import PropTypes from 'prop-types';
import React, {useContext, useEffect, useState} from 'react';
import {isEdge} from 'react-flow-renderer';

import {DefinitionBuilderContext} from '../../../../DefinitionBuilderContext';
import {defaultLanguageId} from '../../../../constants';
import {DiagramBuilderContext} from '../../../DiagramBuilderContext';
import SidebarPanel from '../SidebarPanel';
import {isTransitionNameDuplicated} from '../utils';
import {checkLabelErrors, getUpdatedLabelItem} from './utils';

export default function EdgeInformation({errors, setErrors}) {
	const {elements, selectedLanguageId, setElements} = useContext(
		DefinitionBuilderContext
	);
	const {selectedItem, setSelectedItem, setSelectedTransitionNewName} =
		useContext(DiagramBuilderContext);

	const [transitionName, setTransitionName] = useState(
		selectedItem?.data.name ?? ''
	);
	const [showWarningAlert, setShowWarningAlert] = useState(false);

	const onToggleDefault = (defaultEdge) => {
		if (defaultEdge) {
			setShowWarningAlert(
				elements.find(
					(element) =>
						isEdge(element) &&
						element.source === selectedItem.source &&
						element.data.defaultEdge
				)
			);

			setElements((previousElements) =>
				previousElements.map((element) => {
					if (
						isEdge(element) &&
						element.source === selectedItem.source
					) {
						element.data.defaultEdge = false;
					}

					return element;
				})
			);
		}

		setSelectedItem({
			...selectedItem,
			data: {
				...selectedItem.data,
				defaultEdge,
			},
		});
	};

	const handleTransitionNameChange = (value) => {
		setTransitionName(value);

		const duplicated = isTransitionNameDuplicated(
			elements,
			selectedItem,
			value.trim()
		);

		const empty = value.trim() === '';

		setErrors((previous) => ({
			...previous,
			id: {
				duplicated,
				empty,
			},
		}));

		if (!empty && !duplicated) {
			setSelectedTransitionNewName(value);
		}
	};

	useEffect(() => {
		setTransitionName(selectedItem?.data.name ?? '');
	}, [selectedItem]);

	return (
		<>
			<SidebarPanel panelTitle={Liferay.Language.get('information')}>
				<ClayForm.Group className={errors.label ? 'has-error' : ''}>
					<label htmlFor="nodeLabel">
						{Liferay.Language.get('label')}

						<span className="ml-1 mr-1 text-warning">*</span>

						<span title={Liferay.Language.get('label-name')}>
							<ClayIcon
								className="text-muted"
								symbol="question-circle-full"
							/>
						</span>
					</label>

					<ClayInput
						id="edgeLabel"
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
								: selectedItem?.data.label[
										defaultLanguageId
									]) || ''
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
						errors.id.duplicated || errors.id.empty
							? 'has-error'
							: ''
					}
				>
					<label htmlFor="transitionName">
						<span>
							{`${Liferay.Language.get(
								'transition'
							)} ${Liferay.Language.get('name')}`}
						</span>

						<span className="ml-1 mr-1 text-warning">*</span>

						<span
							title={Liferay.Language.get(
								'name-is-the-transition-identifier'
							)}
						>
							<ClayIcon
								className="text-muted"
								symbol="question-circle-full"
							/>
						</span>
					</label>

					<ClayInput
						id="transitionName"
						onChange={({target}) => {
							handleTransitionNameChange(target.value);
						}}
						type="text"
						value={transitionName}
					/>

					<ClayForm.FeedbackItem>
						{(errors.id.duplicated || errors.id.empty) && (
							<>
								<ClayForm.FeedbackIndicator symbol="exclamation-full" />

								{errors.id.duplicated
									? Liferay.Language.get(
											'a-transition-with-that-name-already-exists'
										)
									: Liferay.Language.get(
											'this-field-is-required'
										)}
							</>
						)}
					</ClayForm.FeedbackItem>
				</ClayForm.Group>

				<ClayForm.Group>
					<label htmlFor="toggleDefault">
						{Liferay.Language.get('default')}

						<span className="ml-1 mr-1 text-warning">*</span>
					</label>

					<div>
						<ClayToggle
							id="toggleDefault"
							label={
								selectedItem?.data?.defaultEdge
									? Liferay.Language.get('true')
									: Liferay.Language.get('false')
							}
							onToggle={onToggleDefault}
							toggled={selectedItem?.data?.defaultEdge}
						/>
					</div>
				</ClayForm.Group>
			</SidebarPanel>
			{showWarningAlert && (
				<ClayAlert.ToastContainer>
					<ClayAlert
						autoClose={5000}
						closeButtonAriaLabel={Liferay.Language.get('close')}
						displayType="warning"
						onClose={() => setShowWarningAlert(false)}
						title={`${Liferay.Language.get(
							'default-was-changed'
						)}:`}
					>
						{Liferay.Language.get(
							'your-workflow-may-have-changed-because-the-default-was-changed'
						)}
					</ClayAlert>
				</ClayAlert.ToastContainer>
			)}
		</>
	);
}

EdgeInformation.propTypes = {
	errors: PropTypes.object.isRequired,
	setErrors: PropTypes.func.isRequired,
};
