/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {Option} from '@clayui/core';
import ClayForm from '@clayui/form';
import ClayLabel from '@clayui/label';
import ClayModal from '@clayui/modal';
import {Observer} from '@clayui/modal/lib/types';
import {
	FormError,
	SingleSelect,
	constantsUtils,
	stringUtils,
	useForm,
} from '@liferay/object-js-components-web';
import classNames from 'classnames';
import React, {useMemo, useState} from 'react';

import {TYPES, useLayoutContext} from '../objectLayoutContext';

import './ModalAddObjectLayoutField.scss';

const objectFieldSizes = [1, 2, 3];

type TInitialValues = {
	objectFieldName: string;
	objectFieldSize: number;
};

interface IBoxBtnColumnsProps {
	setValues: (values: Partial<TInitialValues>) => void;
}

function BoxBtnColumns({setValues}: IBoxBtnColumnsProps) {
	const [activeIndex, setActiveIndex] = useState<number>(0);

	return (
		<div className="box-btn-columns">
			{objectFieldSizes.map((objectFieldSize, objectFieldSizeIndex) => {
				const columns = [];

				for (let index = 0; index < objectFieldSize; index++) {
					columns.push(
						<div className="box-btn-columns__item" key={index} />
					);
				}

				return (
					<button
						className={classNames('box-btn-columns__btn', {
							active: activeIndex === objectFieldSizeIndex,
						})}
						key={objectFieldSizeIndex}
						name="objectFieldSize"
						onClick={() => {
							setActiveIndex(objectFieldSizeIndex);
							setValues({objectFieldSize});
						}}
						type="button"
						value={objectFieldSize}
					>
						{columns}
					</button>
				);
			})}
		</div>
	);
}

interface IProps extends React.HTMLAttributes<HTMLElement> {
	boxIndex: number;
	observer: Observer;
	onClose: () => void;
	tabIndex: number;
}

interface ObjectFieldItem {
	businessType: ObjectFieldBusinessTypeName;
	label: string;
	readOnly: string;
	required: boolean;
	value: number;
}

export default function ModalAddObjectLayoutField({
	boxIndex,
	observer,
	onClose,
	tabIndex,
}: IProps) {
	const [{objectFields}, dispatch] = useLayoutContext();
	const [selectedObjectFieldId, setSelectedObjectFieldId] =
		useState<string>();

	const objectFieldItems = useMemo(() => {
		const availableObjectFields: ObjectFieldItem[] = [];

		objectFields.map(
			({businessType, id, inLayout, label, name, readOnly, required}) => {
				if (!inLayout) {
					availableObjectFields.push({
						businessType,
						label: stringUtils.getLocalizableLabel({
							fallbackLabel: name,
							labels: label,
						}),
						readOnly,
						required,
						value: id,
					});
				}
			}
		);

		return availableObjectFields;
	}, [objectFields]);

	const onSubmit = (values: TInitialValues) => {
		dispatch({
			payload: {
				boxIndex,
				objectFieldName: values.objectFieldName,
				objectFieldSize: 12 / Number(values.objectFieldSize),
				tabIndex,
			},
			type: TYPES.ADD_OBJECT_LAYOUT_FIELD,
		});

		onClose();
	};

	const onValidate = (values: TInitialValues) => {
		const errors: FormError<TInitialValues> = {};

		if (!values.objectFieldName) {
			errors.objectFieldName = constantsUtils.REQUIRED_MSG;
		}

		return errors;
	};

	const initialValues: TInitialValues = {
		objectFieldName: '',
		objectFieldSize: 1,
	};

	const {errors, handleSubmit, setValues} = useForm({
		initialValues,
		onSubmit,
		validate: onValidate,
	});

	return (
		<ClayModal observer={observer}>
			<ClayForm onSubmit={handleSubmit}>
				<ClayModal.Header
					closeButtonAriaLabel={Liferay.Language.get('close')}
				>
					{Liferay.Language.get('add-field')}
				</ClayModal.Header>

				<ClayModal.Body>
					<SingleSelect
						error={errors.objectFieldName}
						id="modalAddObjectLayoutField"
						items={objectFieldItems}
						label={Liferay.Language.get('field')}
						onSelectionChange={(value) => {
							const selectedObjectField = objectFields.find(
								({id}) => id.toString() === value
							);

							setSelectedObjectFieldId(
								selectedObjectField?.id.toString()
							);

							setValues({
								objectFieldName: selectedObjectField?.name,
							});
						}}
						required
						selectedKey={selectedObjectFieldId}
					>
						{({businessType, label, readOnly, required, value}) => (
							<Option key={value} textValue={label}>
								<div className="lfr__object-web-layout-modal-add-field-option">
									{label}

									<div>
										<ClayLabel
											className="label-inside-custom-select"
											displayType={
												required ? 'warning' : 'success'
											}
										>
											{required
												? Liferay.Language.get(
														'mandatory'
													)
												: Liferay.Language.get(
														'optional'
													)}
										</ClayLabel>

										{(businessType === 'AutoIncrement' ||
											readOnly === 'conditional' ||
											readOnly === 'true') && (
											<ClayLabel
												className="label-inside-custom-select"
												displayType="secondary"
											>
												{Liferay.Language.get(
													'read-only'
												)}
											</ClayLabel>
										)}
									</div>
								</div>
							</Option>
						)}
					</SingleSelect>

					<BoxBtnColumns setValues={setValues} />
				</ClayModal.Body>

				<ClayModal.Footer
					last={
						<ClayButton.Group spaced>
							<ClayButton
								displayType="secondary"
								onClick={onClose}
							>
								{Liferay.Language.get('cancel')}
							</ClayButton>

							<ClayButton type="submit">
								{Liferay.Language.get('save')}
							</ClayButton>
						</ClayButton.Group>
					}
				/>
			</ClayForm>
		</ClayModal>
	);
}
