/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React, {useState} from 'react';

import FieldMappingsSection from '../../../src/main/resources/META-INF/resources/js/edit_field_mappings/FieldMappingsSection';
import {IFieldMapping} from '../../../src/main/resources/META-INF/resources/js/edit_field_mappings/types';

jest.mock('@liferay/site-cms-site-initializer', () => ({
	RequiredMark: () => <span>required</span>,
}));

const BASE_SKU_CLASS_NAME = 'com.liferay.object.model.ObjectDefinition#P4R4';

const OBJECT_DEFINITIONS = [
	{
		className: BASE_SKU_CLASS_NAME,
		label: 'PIM Base SKU',
		objectFields: [{label: 'Code', name: 'code'}],
	},
	{
		className: 'com.liferay.object.model.ObjectDefinition#S1Z3',
		label: 'PIM Shirt',
		objectFields: [{label: 'Name', name: 'name'}],
	},
];

let key = 0;

function dynamicValue(sourceFieldName = '', sourceClassName = '') {
	return {
		key: `row-${++key}`,
		sourceClassName,
		sourceFieldName,
		type: 'dynamicValue',
		value: '',
	};
}

function fixedValue(value = '') {
	return {
		key: `row-${++key}`,
		sourceClassName: '',
		sourceFieldName: '',
		type: 'fixedValue',
		value,
	};
}

function FieldMappingsSectionWrapper({
	initialFieldMappings,
}: {
	initialFieldMappings: IFieldMapping[];
}) {
	const [fieldMappings, setFieldMappings] =
		useState<IFieldMapping[]>(initialFieldMappings);

	return (
		<FieldMappingsSection
			fieldMappings={fieldMappings}
			objectDefinitions={OBJECT_DEFINITIONS}
			setFieldMappings={setFieldMappings}
			spritemap="/icons.svg"
		/>
	);
}

describe('FieldMappingsSection', () => {
	it('offers every structure field when the source is all structures', () => {
		render(
			<FieldMappingsSectionWrapper
				initialFieldMappings={[dynamicValue()]}
			/>
		);

		expect(
			screen.getByRole('option', {name: 'Code (code)'})
		).toBeInTheDocument();
		expect(
			screen.getByRole('option', {name: 'Name (name)'})
		).toBeInTheDocument();
	});

	it('narrows the source attributes to the selected structure', async () => {
		render(
			<FieldMappingsSectionWrapper
				initialFieldMappings={[dynamicValue()]}
			/>
		);

		await userEvent.selectOptions(
			screen.getByLabelText('source'),
			BASE_SKU_CLASS_NAME
		);

		expect(
			screen.getByRole('option', {name: 'Code (code)'})
		).toBeInTheDocument();
		expect(
			screen.queryByRole('option', {name: 'Name (name)'})
		).not.toBeInTheDocument();
	});

	it('asks for a value when the source attribute is a fixed one', async () => {
		render(
			<FieldMappingsSectionWrapper
				initialFieldMappings={[dynamicValue()]}
			/>
		);

		expect(screen.queryByLabelText('value')).not.toBeInTheDocument();

		await userEvent.selectOptions(
			screen.getByLabelText('source-attribute'),
			'fixedValue'
		);

		expect(screen.getByLabelText('value')).toBeInTheDocument();
		expect(screen.getByLabelText('source')).toBeDisabled();
	});

	it('keeps the typed fixed value', async () => {
		render(
			<FieldMappingsSectionWrapper
				initialFieldMappings={[fixedValue()]}
			/>
		);

		await userEvent.type(screen.getByLabelText('value'), '12345');

		expect(screen.getByLabelText('value')).toHaveValue('12345');
	});

	it('keeps a row on its own inputs when an earlier row is deleted', async () => {
		render(
			<FieldMappingsSectionWrapper
				initialFieldMappings={[dynamicValue('code'), fixedValue()]}
			/>
		);

		const value = screen.getByLabelText('value');

		await userEvent.type(value, 'ABC-1');

		await userEvent.click(screen.getAllByLabelText('delete-row')[0]);

		expect(screen.getAllByLabelText('source')).toHaveLength(1);
		expect(screen.getByLabelText('value')).toBe(value);
		expect(screen.getByLabelText('value')).toHaveValue('ABC-1');
	});

	it('adds a field mapping row', async () => {
		render(
			<FieldMappingsSectionWrapper
				initialFieldMappings={[dynamicValue()]}
			/>
		);

		await userEvent.click(screen.getByLabelText('add-row'));

		expect(screen.getAllByLabelText('source')).toHaveLength(2);
	});

	it('keeps the last row undeletable', () => {
		render(
			<FieldMappingsSectionWrapper
				initialFieldMappings={[dynamicValue('code')]}
			/>
		);

		expect(screen.getByLabelText('delete-row')).toBeDisabled();
	});

	it('deletes a field mapping row', async () => {
		render(
			<FieldMappingsSectionWrapper
				initialFieldMappings={[
					dynamicValue('code'),
					dynamicValue('name'),
				]}
			/>
		);

		await userEvent.click(screen.getAllByLabelText('delete-row')[0]);

		expect(screen.getAllByLabelText('source')).toHaveLength(1);
	});
});
