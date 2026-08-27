import * as API from 'shared/api';
import AutocompleteInput from 'shared/components/AutocompleteInput';
import Form from 'shared/components/form';
import getCN from 'classnames';
import React from 'react';
import {ISegmentEditorCustomInputBase} from '../utils/types';
import {Map} from 'immutable';
import {getPropertyValue} from '../utils/custom-inputs';
import {isOfKnownType, isValid} from '../utils/utils';
import {Option, Picker} from '@clayui/core';
import {
	getCustomInputOperators,
	isKnown,
	isUnknown,
	RelationalOperators,
} from '../utils/constants';
import {
	DEFAULT_UTM_PARAMETER_OPTIONS,
	getUtmParameterLabel,
	IAcquisitionParameter,
} from '../utils/properties/session-properties';

interface IUtmParameterInputProps extends ISegmentEditorCustomInputBase {
	touched: {
		customInput: boolean;
	};
	valid: {
		customInput: boolean;
	};
}

interface IUtmParameterInputState {
	acquisitionParameters: IAcquisitionParameter[];
}

export default class UtmParameterInput extends React.Component<
	IUtmParameterInputProps,
	IUtmParameterInputState
> {
	constructor(props: IUtmParameterInputProps) {
		super(props);
		this.fieldValuesDataSourceFn = this.fieldValuesDataSourceFn.bind(this);
		this.handleBlur = this.handleBlur.bind(this);
		this.handleOperatorChange = this.handleOperatorChange.bind(this);
		this.handleParameterNameChange =
			this.handleParameterNameChange.bind(this);
		this.handleValueChange = this.handleValueChange.bind(this);

		this.state = {
			acquisitionParameters: DEFAULT_UTM_PARAMETER_OPTIONS,
		};
	}

	componentDidMount() {
		const {channelId, groupId} = this.props;

		this._mounted = true;

		API.session
			.fetchAcquisitionParameters({channelId, groupId: groupId!})
			.then(({items}) => {

				// A channel that has captured no acquisition parameter yet
				// answers with an empty list, which would leave the picker
				// with nothing to choose from and lock the user into the
				// seeded parameter. Keep the defaults for that, exactly as
				// the catch below keeps them for a failed call.

				if (this._mounted && items?.length) {
					this.setState({acquisitionParameters: items});
				}
			})
			.catch(() => {});
	}

	componentWillUnmount() {
		this._mounted = false;
	}

	_mounted = false;

	getOperators() {
		return getCustomInputOperators(this.props.property.type);
	}

	getParameterFieldName(): string {
		return getPropertyValue(this.props.value, 'propertyName', 0) ?? '';
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

	fieldValuesDataSourceFn() {
		const {channelId, groupId, value: valueIMap} = this.props;

		return API.session
			.fetchFieldValues({
				channelId,
				fieldName: this.getParameterFieldName(),
				groupId: groupId!,
				query: getPropertyValue(valueIMap, 'value', 0),
			})
			.then(({items}) => items);
	}

	handleBlur() {
		const {onChange, touched, valid, value: valueIMap} = this.props;

		onChange({
			touched: {...touched, customInput: true},
			valid: {
				...valid,
				customInput: isValid(getPropertyValue(valueIMap, 'value', 0)),
			},
			value: valueIMap,
		});
	}

	handleParameterNameChange(fieldName: React.Key) {
		const {onChange, touched, valid, value: valueIMap} = this.props;

		// "Is known" and "is unknown" are read back from a null value, so
		// clearing it to "" would silently turn them into "is not" and
		// "is". Only a criterion comparing against a real value is reset.

		const value = isOfKnownType(this.getSelectedOperatorKey() ?? '')
			? null
			: '';

		onChange({
			touched: {...touched, customInput: true},
			valid: {...valid, customInput: isValid(value)},
			value: valueIMap
				.setIn(
					['criterionGroup', 'items', 0, 'propertyName'],
					String(fieldName)
				)
				.setIn(['criterionGroup', 'items', 0, 'value'], value),
		});
	}

	handleOperatorChange(operator: React.Key) {
		const {onChange, touched, valid, value: valueIMap} = this.props;

		let newVal = valueIMap.setIn(
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
			touched: {...touched, customInput: true},
			valid: {
				...valid,
				customInput: isValid(
					newVal.getIn(['criterionGroup', 'items', 0, 'value'])
				),
			},
			value: newVal,
		});
	}

	handleValueChange(value: React.Key) {
		const {onChange, touched, valid, value: valueIMap} = this.props;

		onChange({
			touched: {...touched, customInput: true},
			valid: {...valid, customInput: isValid(value)},
			value: valueIMap.setIn(
				['criterionGroup', 'items', 0, 'value'],
				value
			),
		});
	}

	render() {
		const {
			className,
			displayValue,
			property: {entityName},
			touched,
			valid,
			value: valueIMap,
		} = this.props;
		const {acquisitionParameters} = this.state;

		const value = getPropertyValue(valueIMap, 'value', 0);
		const parameterFieldName = this.getParameterFieldName();

		const selectedOperatorKey = this.getSelectedOperatorKey();
		const knownType = isOfKnownType(selectedOperatorKey ?? '');

		const showError = !valid.customInput && touched.customInput;

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
							className="criterion-input"
							items={acquisitionParameters}
							onSelectionChange={this.handleParameterNameChange}
							selectedKey={parameterFieldName}
						>
							{({fieldName, name}) => (
								<Option key={fieldName}>
									{getUtmParameterLabel(name)}
								</Option>
							)}
						</Picker>
					</Form.GroupItem>

					<Form.GroupItem shrink>
						<Picker
							items={
								this.getOperators().map(({key, label}) => ({
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

					{!knownType && (
						<Form.GroupItem>
							<AutocompleteInput
								className={getCN(className, {
									'has-error': showError,
								})}
								dataSourceFn={this.fieldValuesDataSourceFn}
								dataSourceKey={parameterFieldName}
								onBlur={this.handleBlur}
								onChange={this.handleValueChange}
								value={value}
							/>
						</Form.GroupItem>
					)}
				</Form.Group>
			</div>
		);
	}
}
