import Cell from './Cell';
import Checkbox from 'shared/components/Checkbox';
import getCN from 'classnames';
import React, {useState} from 'react';
import {get, noop} from 'lodash';
import DataValue from 'shared/components/DataValue';
import {StopClickPropagation} from './cell-components';

export type Column = {
	accessor: string;
	cellRenderer?: (params: {[key: string]: any}) => React.ReactNode;
	cellRendererProps?: {[key: string]: any};
	className?: string;
	dataFormatter?: (dataValue: any, data?: object) => React.ReactNode;
	editable?: boolean;
	headProps?: {[key: string]: any};
	label?: React.ReactNode;
	sortable?: boolean;
	title?: boolean;
};

interface IRowProps {
	className?: string;
	clickable?: boolean;
	columns: Column[];
	data?: {[key: string]: any};
	disabled?: boolean;
	items?: Array<any>;
	itemsSelected?: boolean;
	onClick?: (data: any) => void;
	onRowDelete: (data: any) => void;
	onRowSave: (data: any) => void;
	renderInlineRowActions?: (params: any) => React.ReactNode; // Can we just remove this?  doesn't seem to be that useful... we can just use it in the columns
	renderRowActions?: (params: any) => React.ReactNode;
	rowIndex: number;
	selected?: boolean;
	showCheckbox?: boolean;
}

const Row: React.FC<IRowProps> = ({
	clickable = false,
	columns = [],
	data = {},
	disabled = false,
	items = [],
	itemsSelected = false,
	onClick = noop,
	renderInlineRowActions,
	renderRowActions,
	rowIndex,
	selected = false,
	showCheckbox = false,
}) => {
	const [state, setState] = useState({
		editing: false,
		edits: {},
	});

	const handleCheckboxChange = () => onClick(data);

	const handleEventPropagation = (event: React.MouseEvent) =>
		event.stopPropagation();

	const handleEdit = () =>
		setState((prevState) => ({
			...prevState,
			editing: true,
		}));

	const handleResetEdits = () =>
		setState((prevState) => ({
			...prevState,
			editing: false,
			edits: {},
		}));

	const handleUpdateEdits = (attr: string, value: any) =>
		setState((prevState) => ({
			...prevState,
			edits: {...prevState.edits, [attr]: value},
		}));

	const renderActionColumn = () => {
		const {editing, edits} = state;

		if (renderRowActions) {
			return (
				<Cell className="row-actions" key="ROW_ACTIONS">
					<StopClickPropagation>
						{renderRowActions({
							data,
							items,
						})}
					</StopClickPropagation>
				</Cell>
			);
		}
		else if (renderInlineRowActions) {
			return (
				<Cell className="row-inline-actions" key="INLINE_ACTIONS">
					<StopClickPropagation>
						{renderInlineRowActions({
							data,
							editing,
							edits,
							items,
							itemsSelected,
							rowEvents: {
								onRowCancel: handleResetEdits,
								onRowEdit: handleEdit,
								onRowSave: handleResetEdits,
							},
						})}
					</StopClickPropagation>
				</Cell>
			);
		}
	};

	const {editing} = state;

	const classes = getCN({
		clickable,
		disabled,
		'table-active': selected,
	});

	return (
		<tr className={classes} onClick={handleCheckboxChange}>
			{showCheckbox && (
				<Cell>
					<Checkbox
						checked={selected}
						disabled={disabled}
						onChange={handleCheckboxChange}
					/>
				</Cell>
			)}

			{columns.map((column, i) => {
				const {
					accessor,
					cellRenderer: CellRenderer,
					cellRendererProps,
					className,
					dataFormatter = (val) => val,
					editable = false,
					title,
				} = column;

				if (CellRenderer && editable) {
					return (
						<CellRenderer
							{...cellRendererProps}
							className={className}
							data={data}
							disabled={disabled}
							editing={editing && !selected}
							key={i}
							onClick={handleEventPropagation}
							onUpdateEdits={handleUpdateEdits}
						/>
					);
				}
				else if (CellRenderer) {
					return (
						<CellRenderer
							{...cellRendererProps}
							className={className}
							data={data}
							disabled={disabled}
							key={i}
							onClick={handleEventPropagation}
							rowIndex={rowIndex}
						/>
					);
				}
				else {
					const dataValue = get(data, accessor);

					return (
						<Cell className={className} key={i} title={title}>
							<DataValue
								format={(value) => dataFormatter(value, data)}
								value={dataValue}
							/>
						</Cell>
					);
				}
			})}

			{(renderRowActions || renderInlineRowActions) &&
				renderActionColumn()}
		</tr>
	);
};

export default Row;
