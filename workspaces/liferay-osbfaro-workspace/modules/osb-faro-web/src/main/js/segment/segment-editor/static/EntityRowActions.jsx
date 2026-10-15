import autobind from 'autobind-decorator';
import ClayButton from '@clayui/button';
import ClayLabel from '@clayui/label';
import React from 'react';
import {Map, Set} from 'immutable';
import {noop} from 'lodash';
import {PropTypes} from 'prop-types';

export default class EntityRowActions extends React.Component {
	static defaultProps = {
		addIdsISet: new Set(),
		data: {},
		itemsIMap: new Map(),
		itemsSelected: false,
		onRowDelete: noop,
		onUndoChanges: noop,
		removeIdsISet: new Set(),
		showAdded: false
	};

	static propTypes = {
		addIdsISet: PropTypes.instanceOf(Set),
		data: PropTypes.object,
		itemsIMap: PropTypes.instanceOf(Map),
		itemsSelected: PropTypes.bool,
		onRowDelete: PropTypes.func,
		onUndoChanges: PropTypes.func,
		removeIdsISet: PropTypes.instanceOf(Set),
		showAdded: PropTypes.bool
	};

	@autobind
	handleEntityRemoval() {
		const {
			data: {id},
			itemsIMap,
			onRowDelete
		} = this.props;

		onRowDelete([id], itemsIMap);
	}

	@autobind
	handleUndoChanges() {
		const {
			data: {id},
			onUndoChanges
		} = this.props;

		onUndoChanges([id]);
	}

	isAddStaged() {
		const {
			addIdsISet,
			data: {id}
		} = this.props;

		return addIdsISet.has(id);
	}

	isRemoveStaged() {
		const {
			data: {id},
			removeIdsISet
		} = this.props;

		return removeIdsISet.has(id);
	}

	renderActions() {
		const {itemsSelected, showAdded} = this.props;

		const addStaged = this.isAddStaged();

		const removeStaged = this.isRemoveStaged();

		if ((addStaged && showAdded) || removeStaged) {
			return (
				<>
					{!itemsSelected && (
						<ClayButton
							borderless
							className='button-root'
							displayType='primary'
							onClick={this.handleUndoChanges}
							outline
							size='sm'
						>
							{Liferay.Language.get('undo')}
						</ClayButton>
					)}

					<ClayLabel
						displayType={addStaged ? 'success' : 'warning'}
						inverse
						size='lg'
					>
						{addStaged
							? Liferay.Language.get('added')
							: Liferay.Language.get('removed')}
					</ClayLabel>
				</>
			);
		} else if (!itemsSelected) {
			return (
				<ClayButton
					className='button-root'
					displayType='secondary'
					onClick={this.handleEntityRemoval}
					outline
					size='sm'
				>
					{Liferay.Language.get('remove')}
				</ClayButton>
			);
		}

		return null;
	}

	render() {
		return this.renderActions();
	}
}
