import AutocompleteInput from 'shared/components/AutocompleteInput';
import Form from 'shared/components/form';
import getCN from 'classnames';
import Input from 'shared/components/Input';
import React from 'react';
import {getPropertyValue} from '../utils/custom-inputs';
import {ISegmentEditorCustomInputBase} from '../utils/types';
import {
	getCustomInputOperators,
	isKnown,
	isUnknown,
	RelationalOperators,
} from '../utils/constants';
import {isOfKnownType, isValid} from '../utils/utils';
import {Map} from 'immutable';
import {Option, Picker} from '@clayui/core';

export interface ICustomStringInputProps extends ISegmentEditorCustomInputBase {
	autocomplete?: boolean;
	fieldValuesDataSourceFn?: () => Promise<string[]>;
	touched: boolean;
	valid: boolean;
}

export default class CustomStringInput extends React.Component<ICustomStringInputProps> {
	static defaultProps = {
		autocomplete: true,
	};

	constructor(props: ICustomStringInputProps) {
		super(props);
		this.handleBlur = this.handleBlur.bind(this);
		this.handleOperatorChange = this.handleOperatorChange.bind(this);
		this.handleValueChange = this.handleValueChange.bind(this);
	}

	getOperators() {
		return getCustomInputOperators(this.props.property.type);
	}

	getSelectedOperatorKey() {
		const criterionIMap = this.props.value.getIn(
			['criterionGroup', 'items', 0],
			Map()
		);

		const operatorName = criterionIMap.get('operatorName');
		const value = criterionIMap.get('value');

		let operatorKey = operatorName;

		const valueNull = value === null;

		if (operatorName === RelationalOperators.EQ && valueNull) {
			operatorKey = isUnknown;
		}
		else if (operatorName === RelationalOperators.NE && valueNull) {
			operatorKey = isKnown;
		}

		return this.getOperators().find(({key}) => key === operatorKey)?.key;
	}

	handleBlur() {
		const {onChange, value: valueIMap} = this.props;

		onChange({
			touched: true,
			valid: isValid(getPropertyValue(valueIMap, 'value', 0)),
			value: valueIMap,
		});
	}

	handleOperatorChange(operator: React.Key) {
		const {onChange, value: valueIMap} = this.props;

		let newVal = null;

		newVal = valueIMap.setIn(
			['criterionGroup', 'items', 0, 'operatorName'],
			this.getOperators().find(({key}) => key === operator)?.name
		);

		if (isOfKnownType(String(operator))) {
			newVal = newVal.setIn(
				['criterionGroup', 'items', 0, 'value'],
				null
			);
		}
		else if (getPropertyValue(valueIMap, 'value', 0) === null) {
			newVal = newVal.setIn(['criterionGroup', 'items', 0, 'value'], '');
		}

		onChange({
			valid: isValid(
				newVal.getIn(['criterionGroup', 'items', 0, 'value'])
			),
			value: newVal,
		});
	}

	handleValueChange(value: React.Key) {
		const {onChange, value: valueIMap} = this.props;

		onChange({
			valid: isValid(value),
			value: valueIMap.setIn(
				['criterionGroup', 'items', 0, 'value'],
				value
			),
		});
	}

	render() {
		const {
			autocomplete,
			className,
			displayValue,
			fieldValuesDataSourceFn,
			property: {entityName, options = []},
			touched,
			valid,
			value: valueIMap,
		} = this.props;

		const value = getPropertyValue(valueIMap, 'value', 0);

		const operators = this.getOperators();
		const selectedOperatorKey = this.getSelectedOperatorKey();
		const knownType = isOfKnownType(selectedOperatorKey ?? '');

		const showError = !valid && touched;

		const sharedInputProps = {
			className: getCN(className, {
				'has-error': showError,
			}),
			onBlur: this.handleBlur,
			value,
		};

		return (
			<div className="criteria-statement">
				<Form.Group autoFit>
					<Form.GroupItem className="entity-name" label shrink>
						{entityName}
					</Form.GroupItem>

					<Form.GroupItem className="display-value" label shrink>
						{displayValue}
					</Form.GroupItem>

					<Form.GroupItem shrink>
						<Picker
							items={
								operators.map(({key, label}) => ({
									key,
									label,
								})) as {label: string; key: string}[]
							}
							onSelectionChange={this.handleOperatorChange}
							selectedKey={selectedOperatorKey}
						>
							{({key, label}) => (
								<Option key={key}>{label}</Option>
							)}
						</Picker>
					</Form.GroupItem>

					{!knownType && options.length === 0 && (
						<Form.GroupItem>
							{autocomplete ? (
								<AutocompleteInput
									{...sharedInputProps}
									dataSourceFn={fieldValuesDataSourceFn}
									onChange={this.handleValueChange}
								/>
							) : (
								<Input
									{...sharedInputProps}
									autoComplete="nope"
									onChange={(
										event: React.ChangeEvent<HTMLInputElement>
									) => {
										this.handleValueChange(
											event.target.value
										);
									}}
								/>
							)}
						</Form.GroupItem>
					)}

					{!knownType && options.length > 0 && (
						<Form.GroupItem shrink>
							<Picker
								className="criterion-input"
								items={
									options.map(({label, value}) => ({
										label,
										value,
									})) as {label: string; value: string}[]
								}
								onBlur={this.handleBlur}
								onSelectionChange={this.handleValueChange}
								selectedKey={value}
							>
								{({label, value}) => (
									<Option key={value}>{label}</Option>
								)}
							</Picker>
						</Form.GroupItem>
					)}
				</Form.Group>
			</div>
		);
	}
}
