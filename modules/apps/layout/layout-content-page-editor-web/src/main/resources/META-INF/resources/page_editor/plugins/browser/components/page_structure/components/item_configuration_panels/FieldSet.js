/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayPanel from '@clayui/panel';
import {isNullOrUndefined} from '@liferay/layout-js-components-web';
import classNames from 'classnames';
import PropTypes from 'prop-types';
import React, {useMemo} from 'react';

import {FRAGMENT_CONFIGURATION_FIELDS} from '../../../../../../app/components/fragment_configuration_fields/index';
import {CONTAINER_WIDTH_TYPES} from '../../../../../../app/config/constants/containerWidthTypes';
import {FRAGMENT_ENTRY_TYPES} from '../../../../../../app/config/constants/fragmentEntryTypes';
import {LAYOUT_DATA_ITEM_TYPES} from '../../../../../../app/config/constants/layoutDataItemTypes';
import {VIEWPORT_SIZES} from '../../../../../../app/config/constants/viewportSizes';
import {getEditableLocalizedValue} from '../../../../../../app/utils/getEditableLocalizedValue';
import CurrentLanguageFlag from '../../../../../../common/components/CurrentLanguageFlag';
import {ConfigurationFieldPropTypes} from '../../../../../../prop_types/index';

const DISPLAY_SIZES = {
	small: 'small',
};

export function fieldIsDisabled(item, field) {
	if (field.disabled) {
		return true;
	}

	return (
		item.type === LAYOUT_DATA_ITEM_TYPES.container &&
		item.config?.widthType === CONTAINER_WIDTH_TYPES.fixed &&
		(field.name === 'marginRight' || field.name === 'marginLeft')
	);
}

function getAvailableFields(item, fields, fragmentEntryLinks, viewportSize) {
	if (!fragmentEntryLinks && !viewportSize) {
		return fields;
	}

	let availableFields = fields;

	if (viewportSize !== VIEWPORT_SIZES.desktop) {
		availableFields = fields.filter(
			(field) => field.responsive || field.name === 'backgroundImage'
		);
	}

	if (item.type !== LAYOUT_DATA_ITEM_TYPES.fragment) {
		return availableFields;
	}

	const fragmentEntryLink =
		fragmentEntryLinks[item.config.fragmentEntryLinkId];

	if (fragmentEntryLink.fragmentEntryType === FRAGMENT_ENTRY_TYPES.input) {
		availableFields = availableFields.filter(
			(field) => field.name !== 'display'
		);
	}

	return availableFields;
}

export function FieldSet({
	description,
	embedInCollapsableSection = true,
	fragmentEntryLinks,
	selectedViewportSize,
	isCustomStylesFieldSet = false,
	fields,
	item = {},
	label,
	languageId,
	onValueSelect,
	values,
}) {
	const availableFields = getAvailableFields(
		item,
		fields,
		fragmentEntryLinks,
		selectedViewportSize
	);

	return !!availableFields.length && label && embedInCollapsableSection ? (
		<ClayPanel
			collapsable
			defaultExpanded
			displayTitle={label}
			displayType="unstyled"
			showCollapseIcon
		>
			<ClayPanel.Body>
				<FieldSetContent
					description={description}
					fields={availableFields}
					isCustomStylesFieldSet={isCustomStylesFieldSet}
					item={item}
					languageId={languageId}
					onValueSelect={onValueSelect}
					values={values}
				/>
			</ClayPanel.Body>
		</ClayPanel>
	) : (
		<FieldSetContent
			description={description}
			fields={availableFields}
			isCustomStylesFieldSet={isCustomStylesFieldSet}
			item={item}
			languageId={languageId}
			onValueSelect={onValueSelect}
			values={values}
		/>
	);
}

function FieldSetContent({
	description,
	fields,
	isCustomStylesFieldSet,
	item,
	languageId,
	onValueSelect,
	values,
}) {
	const fieldsByName = useMemo(() => {
		return fields.reduce((acc, field) => {
			acc[field.name] = field;

			return acc;
		}, {});
	}, [fields]);

	return (
		<div className="page-editor__sidebar__fieldset">
			{description && <p className="text-secondary">{description}</p>}

			{fields
				.filter(
					(field) =>
						!field.typeOptions?.dependency ||
						Object.entries(field.typeOptions.dependency).every(
							([key, {type, value}]) => {
								let dependencyValue = String(values[key]);

								if (!dependencyValue) {
									const dependencyField = fieldsByName[key];

									if (!dependencyField) {
										return true;
									}

									if (
										dependencyField.type === 'checkbox' &&
										!dependencyField.defaultValue
									) {
										dependencyValue = 'false';
									}
									else {
										dependencyValue =
											fieldsByName[key]?.defaultValue;
									}
								}

								if (type === 'equal') {
									return dependencyValue === value;
								}

								return dependencyValue !== value;
							}
						)
				)
				.map((field, index) => {
					const FieldComponent =
						field.type && FRAGMENT_CONFIGURATION_FIELDS[field.type];

					return (
						<div
							className={classNames(
								field.cssClass,
								'autofit-row page-editor__sidebar__fieldset__field align-items-end',
								{
									'page-editor__sidebar__fieldset__field-small':
										field.displaySize ===
										DISPLAY_SIZES.small,
								}
							)}
							key={index}
						>
							<div className="autofit-col autofit-col-expand">
								<FieldComponent
									disabled={fieldIsDisabled(item, field)}
									field={field}
									isCustomStyle={isCustomStylesFieldSet}
									item={item}
									onValueSelect={onValueSelect}
									value={getFieldValue({
										field,
										languageId,
										values,
									})}
								/>
							</div>

							{field.localizable && (
								<CurrentLanguageFlag className="ml-2" />
							)}
						</div>
					);
				})}
		</div>
	);
}

function getFieldValue({field, languageId, values}) {
	if (field.name === '') {
		return values;
	}

	const value = values[field.name];

	if (
		isNullOrUndefined(value) ||
		(field.type === 'colorPicker' && typeof value === 'object')
	) {
		return field.defaultValue;
	}

	if (!field.localizable || typeof value !== 'object') {
		return value;
	}

	return getEditableLocalizedValue(value, languageId, field.defaultValue);
}

FieldSet.propTypes = {
	fields: PropTypes.arrayOf(PropTypes.shape(ConfigurationFieldPropTypes)),
	fragmentEntryLinks: PropTypes.object,
	isCustomStylesFieldSet: PropTypes.bool,
	item: PropTypes.object,
	label: PropTypes.string,
	languageId: PropTypes.string.isRequired,
	onValueSelect: PropTypes.func.isRequired,
	selectedViewportSize: PropTypes.string,
	values: PropTypes.object,
};
