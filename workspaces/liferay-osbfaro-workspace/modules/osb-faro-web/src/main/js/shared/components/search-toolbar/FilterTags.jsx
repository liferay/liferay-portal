import autobind from 'autobind-decorator';
import Label from 'shared/components/Label';
import React from 'react';
import SubnavTbar from 'shared/components/SubnavTbar';
import {PropTypes} from 'prop-types';

class FilterTagItem extends React.Component {
	static propTypes = {
		field: PropTypes.string.isRequired,
		onRemove: PropTypes.func.isRequired,
		value: PropTypes.string.isRequired
	};

	@autobind
	handleRemove() {
		const {field, onRemove, value} = this.props;

		onRemove(field, value);
	}

	render() {
		const {label} = this.props;

		return (
			<Label
				className={
					this.props.className ? ` ${this.props.className}` : ''
				}
				display='info'
				onRemove={this.handleRemove}
				size='lg'
			>
				{label}
			</Label>
		);
	}
}

export default class FilterTags extends React.Component {
	static defaultProps = {
		tags: []
	};

	static propTypes = {
		tags: PropTypes.array
	};

	render() {
		const {tags, ...otherProps} = this.props;

		return (
			<>
				{tags.map(({field, label, value}, i) => (
					<SubnavTbar.Item expand={i === tags.length - 1} key={value}>
						<FilterTagItem
							{...otherProps}
							field={field}
							label={label}
							value={value}
						/>
					</SubnavTbar.Item>
				))}
			</>
		);
	}
}
