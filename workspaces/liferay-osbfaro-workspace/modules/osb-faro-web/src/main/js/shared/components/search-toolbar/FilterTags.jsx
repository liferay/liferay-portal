import autobind from 'autobind-decorator';
import ClayLabel from '@clayui/label';
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
			<ClayLabel
				className={
					this.props.className ? ` ${this.props.className}` : ''
				}
				closeButtonProps={{
					'aria-label': Liferay.Language.get('close'),
					onClick: this.handleRemove
				}}
				displayType='info'
				inverse
				size='lg'
			>
				{label}
			</ClayLabel>
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
