/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLayout from '@clayui/layout';
import classnames from 'classnames';
import React, {forwardRef, useRef} from 'react';
import {useDrop} from 'react-dnd';

import {EVENT_TYPES} from '../../../custom/form/eventTypes';
import {useForm, useFormState} from '../../hooks/useForm.es';
import FieldRepeatableDND from '../FieldRepeatableDND';

import './WebContentVariant.scss';

const DDM_FORM_ADMIN_PORTLET_NAMESPACE =
	'com_liferay_dynamic_data_mapping_form_web_portlet_DDMFormAdminPortlet';

export const Column = forwardRef(
	(
		{
			children,
			className,
			column,
			columnClassName,
			index,
			onClick,
			onMouseLeave,
			onMouseOver,
			pageIndex,
			rowIndex,
			viewMode,
		},
		ref
	) => {
		const {portletId} = useFormState();

		const addr = {
			'data-ddm-field-column': index,
			'data-ddm-field-page': pageIndex,
			'data-ddm-field-row': rowIndex,
		};

		const firstField = column.fields[0];
		const isFieldSetOrGroup = firstField?.type === 'fieldset';
		const isFieldSet = firstField?.ddmStructureId && isFieldSetOrGroup;

		return (
			<ClayLayout.Col
				{...addr}
				className={classnames('col-ddm', columnClassName)}
				key={index}
				md={column.size}
				onClick={onClick}
				onMouseLeave={onMouseLeave}
				onMouseOver={onMouseOver}
				ref={ref}
			>
				{!!column.fields.length && (
					<div
						className={classnames(
							'ddm-field-container ddm-target h-100',
							{
								'ddm-fieldset': !!isFieldSet,
								'fields-group': !!isFieldSetOrGroup,
							},
							className
						)}
						data-field-name={firstField.fieldName}
					>
						{column.fields.map((field, index) => {
							if (
								viewMode &&
								portletId.includes(
									DDM_FORM_ADMIN_PORTLET_NAMESPACE
								)
							) {
								field.predefinedValue = '';
							}

							if (field.repeatable) {
								return (
									<React.Fragment key={index}>
										{index === 0 && (
											<Placeholder
												field={field}
												index={index}
												nestedFieldIndex={
													field.nestedFieldIndex
												}
											/>
										)}

										<FieldRepeatableDND
											field={field}
											index={index}
											nestedFieldIndex={
												field.nestedFieldIndex
											}
										>
											{children}
										</FieldRepeatableDND>

										<Placeholder
											field={field}
											index={index + 1}
											nestedFieldIndex={
												typeof field.nestedFieldIndex ===
												'number'
													? field.nestedFieldIndex + 1
													: field.nestedFieldIndex
											}
										/>
									</React.Fragment>
								);
							}

							return typeof children === 'function'
								? children({field, index})
								: children;
						})}
					</div>
				)}
			</ClayLayout.Col>
		);
	}
);

Column.displayName = 'WebContentVariant.Column';

function Placeholder({field, index, nestedFieldIndex}) {
	const ref = useRef(null);
	const dispatch = useForm();

	const [{canDrop, overTarget}, dropRef] = useDrop({
		accept: field.fieldName,
		canDrop: () => {
			return true;
		},
		collect: (monitor) => {
			return {
				canDrop: monitor.canDrop(),
				overTarget: monitor.isOver({shallow: true}),
			};
		},
		drop: (item) => {
			if (!ref.current) {
				return;
			}

			const draggedIndex = item.index;
			const targetIndex = index;
			const sourceFieldName = item.id;
			const sourceNestedFieldIndex = item.nestedFieldIndex;
			const targetNestedFieldIndex = nestedFieldIndex;

			dispatch({
				payload: {
					draggedIndex,
					sourceFieldName,
					sourceNestedFieldIndex,
					targetIndex,
					targetNestedFieldIndex,
				},
				type: EVENT_TYPES.FORM_VIEW.REPEATABLE_FIELD.CHANGE_ORDER,
			});

			Liferay.fire('journal:storeState', {
				fieldName: Liferay.Language.get('move-repeatable-field'),
			});

			item.index = targetIndex;
		},
	});

	return (
		<div
			className={classnames('lfr-forms__form-view-ddm-target', {
				'lfr-forms__form-view-ddm-target-over': overTarget && canDrop,
			})}
			data-droppable="true"
			ref={(element) => {
				dropRef(element);
				ref.current = element;
			}}
		>
			<span data-droppable="true" />
		</div>
	);
}
