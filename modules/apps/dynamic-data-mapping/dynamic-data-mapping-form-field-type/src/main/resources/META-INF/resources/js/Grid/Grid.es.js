/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayRadio} from '@clayui/form';
import ClayTable from '@clayui/table';
import {ReactFieldBase as FieldBase} from 'dynamic-data-mapping-form-field-type/api';
import React from 'react';

import {useSyncValue} from '../hooks/useSyncValue.es';

const TableHead = ({columns}) => (
	<ClayTable.Head>
		<ClayTable.Row>
			<ClayTable.Cell headingCell />

			{columns.map((column, colIndex) => {
				return (
					<ClayTable.Cell
						headingCell
						key={`column-${column.value}-${colIndex}`}
					>
						{column.label}
					</ClayTable.Cell>
				);
			})}
		</ClayTable.Row>
	</ClayTable.Head>
);

const TableBodyColumns = ({
	columns,
	disabled,
	name,
	onBlur,
	onChange,
	onFocus,
	row,
	value,
}) => {
	const columnLabel = Liferay.Language.get('column');
	const rowLabel = Liferay.Language.get('row');

	return columns.map((column, colIndex) => {
		return (
			<ClayTable.Cell key={`cell-${column.value}-${colIndex}`}>
				<ClayRadio
					aria-label={`${rowLabel}: ${row.label}, ${columnLabel}: ${column.label}`}
					checked={column.value === value[row.value]}
					className="form-builder-grid-field"
					data-name={row.value}
					data-option-reference-column={column.reference}
					data-option-reference-row={row.reference}
					disabled={disabled}
					name={name}
					onBlur={onBlur}
					onChange={onChange}
					onFocus={onFocus}
					value={column.value}
				/>
			</ClayTable.Cell>
		);
	});
};

const Grid = ({
	accessibleProps,
	columns = [{label: 'col1', value: 'fieldId'}],
	disabled,
	name,
	onBlur,
	onChange,
	onFocus,
	rows = [{label: 'row', value: 'jehf'}],
	value,
	...otherProps
}) => (
	<div className="table-responsive" {...otherProps}>
		{rows.map((row, rowIndex) => {
			const inputValue = value[row.value]
				? `${row.value};${value[row.value]}`
				: '';

			return (
				<input
					key={`row-${row.value}-${rowIndex}`}
					name={name}
					type="hidden"
					value={inputValue}
				/>
			);
		})}

		<ClayTable {...accessibleProps} striped>
			<TableHead columns={columns} />

			<ClayTable.Body>
				{rows.map((row, rowIndex) => {
					return (
						<ClayTable.Row
							key={`row-${row.value}-${rowIndex}`}
							name={row.value}
						>
							<ClayTable.Cell>{row.label}</ClayTable.Cell>

							<TableBodyColumns
								columns={columns}
								disabled={disabled}
								name={`${name}_${row.value}`}
								onBlur={onBlur}
								onChange={onChange}
								onFocus={onFocus}
								row={row}
								value={value}
							/>
						</ClayTable.Row>
					);
				})}
			</ClayTable.Body>
		</ClayTable>
	</div>
);

const Main = ({
	columns,
	name,
	readOnly,
	rows,
	onChange,
	onFocus,
	onBlur,
	value = {},
	...otherProps
}) => {
	const [state, setState] = useSyncValue(value, false);

	return (
		<FieldBase name={name} readOnly={readOnly} {...otherProps}>
			<Grid
				accessibleProps={{
					'aria-required': otherProps.required,
				}}
				columns={columns}
				disabled={readOnly}
				name={name}
				onBlur={onBlur}
				onChange={(event) => {
					const {target} = event;
					const value = {
						[target.dataset.name]: target.value,
					};

					const newState = {...state, ...value};

					setState(newState);

					onChange(event, newState);
				}}
				onFocus={onFocus}
				rows={rows}
				value={state}
			/>
		</FieldBase>
	);
};

Main.displayName = 'Grid';

export default Main;
