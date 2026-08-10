import DateInput from './DateInput';
import Form from 'shared/components/form';
import React from 'react';
import {Criterion, ISegmentEditorCustomInputBase} from '../utils/types';
import {getCustomDateFormat} from 'shared/util/date';
import {
	getCompleteDate,
	getOperator,
	setCompleteDate,
	setOperator,
} from '../utils/custom-inputs';
import {PropertyTypes, SUPPORTED_OPERATORS_MAP} from '../utils/constants';
import {Option, Picker} from '@clayui/core';

const DATE_OPERATORS = SUPPORTED_OPERATORS_MAP[PropertyTypes.Date];

export default class CustomDateInput extends React.Component<ISegmentEditorCustomInputBase> {
	constructor(props: ISegmentEditorCustomInputBase) {
		super(props);
		this.handleDateChange = this.handleDateChange.bind(this);
		this.handleOperatorChange = this.handleOperatorChange.bind(this);
		this.renderOperatorDropdown = this.renderOperatorDropdown.bind(this);
	}

	handleDateChange(newDate: Criterion | Criterion[]) {
		const {onChange, value} = this.props;

		const newValue = Array.isArray(newDate)
			? newDate[0]?.value
			: newDate.value;

		onChange({value: setCompleteDate(value, newValue)});
	}

	handleOperatorChange(newValue: React.Key) {
		const {onChange, value} = this.props;

		onChange({value: setOperator(value, 0, newValue)});
	}

	renderOperatorDropdown() {
		const {value} = this.props;

		return (
			<Form.GroupItem className="operator" shrink>
				<Picker
					className="criterion-input operator-input"
					items={DATE_OPERATORS.map(({key, label}) => ({
						label,
						value: key,
					}))}
					onSelectionChange={this.handleOperatorChange}
					selectedKey={getOperator(value, 0)}
				>
					{({label, value}) => <Option key={value}>{label}</Option>}
				</Picker>
			</Form.GroupItem>
		);
	}

	render() {

		// eslint-disable-next-line @typescript-eslint/no-unused-vars
		const {onChange: _onChange, value, ...otherProps} = this.props;

		return (
			<DateInput
				{...otherProps}
				displayFormat={getCustomDateFormat()}
				onChange={this.handleDateChange}
				operatorRenderer={this.renderOperatorDropdown}
				value={getCompleteDate(value)}
			/>
		);
	}
}
