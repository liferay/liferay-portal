import autobind from 'autobind-decorator';
import ClayButton from '@clayui/button';
import ClayDropDown from '@clayui/drop-down';
import ClayIcon from 'shared/components/Icon';
import getCN from 'classnames';
import React from 'react';
import {get, noop} from 'lodash';
import {getDisplayRole} from 'shared/util/lang';
import {hasChanges} from 'shared/util/react';
import {PropTypes} from 'prop-types';
import {StopClickPropagation} from 'shared/components/table/cell-components';

export default class RoleRenderer extends React.Component {
	static defaultProps = {
		data: {},
		editing: false,
		onUpdateEdits: noop,
		options: []
	};

	static propTypes = {
		data: PropTypes.shape({
			roleName: PropTypes.string
		}),
		editing: PropTypes.bool,
		onUpdateEdits: PropTypes.func,
		options: PropTypes.array
	};

	state = {
		selectedKey: ''
	};

	constructor(props) {
		super(props);

		const {
			data: {roleName},
			options
		} = this.props;

		this.state = {
			...this.state,
			selectedKey: options.length ? roleName : ''
		};
	}

	componentDidMount() {
		const {
			data: {roleName},
			onUpdateEdits
		} = this.props;

		onUpdateEdits('roleName', roleName);
	}

	componentDidUpdate(prevProps) {
		const {
			data: {roleName},
			editing,
			onUpdateEdits
		} = this.props;

		if (editing && hasChanges(prevProps, this.props, 'roleName')) {
			onUpdateEdits('roleName', roleName);

			this.setState({
				selectedKey: roleName
			});
		}

		if (hasChanges(prevProps, this.props, 'data')) {
			this.setState({
				selectedKey: get(this.props, 'data.roleName', roleName)
			});
		}
	}

	@autobind
	handleOptionClick(selectedKey) {
		const {onUpdateEdits} = this.props;

		this.setState({
			selectedKey
		});

		onUpdateEdits('roleName', selectedKey);
	}

	render() {
		const {
			props: {className, data, editing, options},
			state: {selectedKey}
		} = this;

		return (
			<td className={className}>
				<StopClickPropagation>
					{editing && options.length > 0 ? (
						<ClayDropDown
							closeOnClick
							trigger={
								<ClayButton
									className={getCN(
										'd-flex',
										'justify-content-between',
										' align-items-center'
									)}
									displayType='secondary'
									small
								>
									<span className='text-truncate mr-2'>
										{getDisplayRole(selectedKey)}
									</span>

									<ClayIcon
										className='mt-0'
										symbol='caret-bottom'
									/>
								</ClayButton>
							}
						>
							{options.map(option => (
								<ClayDropDown.Item
									key={option.value}
									onClick={() =>
										this.handleOptionClick(option.value)
									}
								>
									{option.label}
								</ClayDropDown.Item>
							))}
						</ClayDropDown>
					) : (
						getDisplayRole(data.roleName)
					)}
				</StopClickPropagation>
			</td>
		);
	}
}
