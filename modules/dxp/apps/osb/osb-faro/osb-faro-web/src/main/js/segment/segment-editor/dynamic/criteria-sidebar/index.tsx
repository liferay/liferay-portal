import autobind from 'autobind-decorator';
import ClayButton from '@clayui/button';
import ClayDropdown from '@clayui/drop-down';
import ClayIcon from '@clayui/icon';
import CriteriaSidebarCollapse from './CriteriaSidebarCollapse';
import CriteriaSidebarSearchBar from './CriteriaSidebarSearchBar';
import getCN from 'classnames';
import React from 'react';
import {List} from 'immutable';
import {Property, PropertyGroup} from 'shared/util/records';

interface ICriteriaSidebarProps {
	propertyGroupsIList: List<PropertyGroup>;
}

interface ICriteriaSidebarState {
	searchValue: string;
	selectedPropertyKey: string;
}

export default class CriteriaSidebar extends React.Component<
	ICriteriaSidebarProps,
	ICriteriaSidebarState
> {
	state = {
		searchValue: '',
		selectedPropertyKey: null
	};

	constructor(props) {
		super(props);

		const {propertyGroupsIList = List<Property>()} = props;

		this.state = {
			...this.state,
			selectedPropertyKey: propertyGroupsIList.getIn([0, 'propertyKey'])
		};
	}

	@autobind
	handlePropertyGroupSelect(selectedPropertyKey) {
		this.setState({
			selectedPropertyKey
		});
	}

	@autobind
	handleOnSearchChange(value) {
		this.setState({searchValue: value});
	}

	render() {
		const {
			props: {propertyGroupsIList},
			state: {searchValue, selectedPropertyKey}
		} = this;

		const activePropertyGroup = propertyGroupsIList.find(
			({propertyKey}) => propertyKey === selectedPropertyKey
		);

		return (
			<div className='criteria-sidebar-root'>
				<div className='sidebar-header'>
					{activePropertyGroup ? (
						<ClayDropdown
							closeOnClick
							trigger={
								<ClayButton
									block
									borderless
									className={getCN(
										'd-flex',
										'justify-content-between',
										' align-items-center'
									)}
									displayType='secondary'
									outline
								>
									<span className='text-truncate'>
										{activePropertyGroup.label}
									</span>

									<ClayIcon symbol='caret-bottom' />
								</ClayButton>
							}
						>
							{propertyGroupsIList.map(({label, propertyKey}) => (
								<ClayDropdown.Item
									active={propertyKey === selectedPropertyKey}
									key={propertyKey}
									onClick={() =>
										this.handlePropertyGroupSelect(
											propertyKey
										)
									}
								>
									{label}
								</ClayDropdown.Item>
							))}
						</ClayDropdown>
					) : (
						Liferay.Language.get('properties')
					)}
				</div>

				<div className='sidebar-search'>
					<CriteriaSidebarSearchBar
						onChange={this.handleOnSearchChange}
						searchValue={searchValue}
					/>
				</div>

				<div className='sidebar-collapse'>
					<CriteriaSidebarCollapse
						propertyGroupsIList={propertyGroupsIList}
						propertyKey={selectedPropertyKey}
						searchValue={searchValue}
					/>
				</div>
			</div>
		);
	}
}
