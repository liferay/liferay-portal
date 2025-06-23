/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayCardWithInfo} from '@clayui/card';
import ClayForm, {ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import {useModal} from '@clayui/modal';
import classNames from 'classnames';
import {useId} from 'frontend-js-components-web';
import PropTypes from 'prop-types';
import React, {useState} from 'react';

import {MultiStepFormModal, MultiStepFormModalStep} from './MultiStepFormModal';
import {FieldTypeSelector} from './fragment-editor/FieldTypeSelector';

function getFieldName(namespace, fieldName) {
	return namespace.concat(fieldName);
}

export default function AddFragmentModal({
	addFragmentEntryURL,
	fieldTypes: initialFieldTypes,
	fragmentTypes,
	namespace,
}) {
	const [name, setName] = useState('');
	const [type, setType] = useState(fragmentTypes[0]);
	const [nameError, setNameError] = useState(null);
	const [selectedFieldTypes, setSelectedFieldTypes] = useState([]);
	const selectFragmentText = useId();

	const [visible, setVisible] = useState(true);
	const {observer, onClose} = useModal({
		onClose: () => setVisible(false),
	});

	const fieldTypes = initialFieldTypes.filter(
		(fieldType) => fieldType.key !== 'localizationSelect'
	);

	return (
		visible && (
			<MultiStepFormModal
				className="add-fragment-modal"
				observer={observer}
				onClose={onClose}
				onFormError={setNameError}
				submitLabel={Liferay.Language.get('add')}
				submitURL={addFragmentEntryURL}
				title={Liferay.Language.get('add-fragment')}
			>
				<MultiStepFormModalStep>
					<p
						className="font-weight-semi-bold mb-4 text-secondary"
						id={selectFragmentText}
					>
						{Liferay.Language.get('select-fragment-type')}
					</p>

					<div
						aria-labelledby={selectFragmentText}
						className="d-flex fragment-type-cards"
						role="group"
					>
						{fragmentTypes.map((fragmentType) => (
							<FragmentTypeCard
								active={type.key === fragmentType.key}
								fragmentType={fragmentType}
								key={fragmentType.key}
								onSelect={setType}
							/>
						))}
					</div>

					<input
						name={getFieldName(namespace, 'type')}
						readOnly
						required
						type="hidden"
						value={type.key}
					/>
				</MultiStepFormModalStep>

				<MultiStepFormModalStep>
					<ClayForm.Group
						className={classNames({'has-error': nameError})}
					>
						<label htmlFor={getFieldName(namespace, 'name')}>
							{Liferay.Language.get('name')}

							<ClayIcon
								className="reference-mark"
								symbol="asterisk"
							/>
						</label>

						<ClayInput
							id={getFieldName(namespace, 'name')}
							name={getFieldName(namespace, 'name')}
							onChange={(event) => setName(event.target.value)}
							required
							type="text"
							value={name}
						/>

						{nameError && (
							<ClayForm.FeedbackGroup>
								<ClayForm.FeedbackItem>
									{nameError}
								</ClayForm.FeedbackItem>
							</ClayForm.FeedbackGroup>
						)}
					</ClayForm.Group>

					{type.name === 'form' && (
						<FieldTypeSelector
							availableFieldTypes={fieldTypes}
							description={Liferay.Language.get(
								'specify-which-field-types-this-fragment-will-support.-you-can-change-this-configuration-later'
							)}
							fieldTypes={selectedFieldTypes}
							onChangeFieldTypes={setSelectedFieldTypes}
							portletNamespace={namespace}
							showFragmentConfigurationLink={false}
							small
							title={Liferay.Language.get(
								'select-supported-field-types'
							)}
						/>
					)}
				</MultiStepFormModalStep>
			</MultiStepFormModal>
		)
	);
}

AddFragmentModal.propTypes = {
	addFragmentEntryURL: PropTypes.string.isRequired,
	fieldTypes: PropTypes.array.isRequired,
	fragmentTypes: PropTypes.array.isRequired,
	namespace: PropTypes.string.isRequired,
};

function FragmentTypeCard({active, fragmentType, onSelect}) {
	const {description, name, symbol, title} = fragmentType;

	return (
		<ClayCardWithInfo
			className={`fragment-type-card mb-0 fragment-type-card-${name}`}
			description={description}
			onSelectChange={() => onSelect(fragmentType)}
			radioProps={{
				'aria-label': title,
				'name': 'fragments',
				'value': name,
			}}
			selectableType="radio"
			selected={active}
			stickerProps={null}
			symbol={symbol}
			title={title}
			truncate={false}
		/>
	);
}

FragmentTypeCard.propTypes = {
	active: PropTypes.bool.isRequired,
	fragmentType: PropTypes.object.isRequired,
	onSelect: PropTypes.func.isRequired,
};
