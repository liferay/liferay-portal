import Form from 'shared/components/form';
import React from 'react';
import {getPropertyValue, setPropertyValue} from '../utils/custom-inputs';
import {ISegmentEditorCustomInputBase} from '../utils/types';
import {Option, Picker} from '@clayui/core';
import {SEARCH_TERM_BOOLEAN_OPTIONS} from '../utils/constants';

export default class SearchTermBooleanInput extends React.Component<ISegmentEditorCustomInputBase> {
	constructor(props: ISegmentEditorCustomInputBase) {
		super(props);
		this.handleChange = this.handleChange.bind(this);
	}

	handleChange(newValue: React.Key) {
		const {onChange, value} = this.props;

		onChange({
			value: setPropertyValue(value, 'value', 1, newValue),
		});
	}

	render() {
		const {
			property: {entityName},
			value,
		} = this.props;

		return (
			<div className="criteria-statement">
				<Form.Group autoFit>
					<Form.GroupItem className="entity-name" label shrink>
						{entityName}
					</Form.GroupItem>

					<Form.GroupItem shrink>
						<Picker
							className="criterion-input"
							items={SEARCH_TERM_BOOLEAN_OPTIONS}
							onSelectionChange={this.handleChange}
							selectedKey={getPropertyValue(value, 'value', 1)}
						>
							{({label, value}) => (
								<Option key={value}>{label}</Option>
							)}
						</Picker>
					</Form.GroupItem>

					<Form.GroupItem className="operator" label shrink>
						{Liferay.Language.get('searching').toLowerCase()}
					</Form.GroupItem>

					<Form.GroupItem className="display-value" label shrink>
						{getPropertyValue(value, 'value', 0)}
					</Form.GroupItem>
				</Form.Group>
			</div>
		);
	}
}
