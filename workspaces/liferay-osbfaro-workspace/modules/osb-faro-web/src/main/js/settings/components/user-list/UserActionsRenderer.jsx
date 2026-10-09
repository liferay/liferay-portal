import autobind from 'autobind-decorator';
import ClayButton from '@clayui/button';
import ClayIcon from 'shared/components/Icon';
import getCN from 'classnames';
import React from 'react';
import {noop} from 'lodash';
import {PropTypes} from 'prop-types';
import {User} from 'shared/util/records';

export default class UserActionsRenderer extends React.Component {
	static defaultProps = {
		editing: false,
		edits: {},
		itemsSelected: false,
		onRowCancel: noop,
		onRowEdit: noop,
		onRowSave: noop,
		onUserDelete: noop,
		onUserSave: noop
	};

	static propTypes = {
		currentUserId: PropTypes.oneOfType([
			PropTypes.number,
			PropTypes.string
		]),
		data: PropTypes.instanceOf(User).isRequired,
		editing: PropTypes.bool,
		edits: PropTypes.object,
		itemsSelected: PropTypes.bool,
		onRowCancel: PropTypes.func,
		onRowEdit: PropTypes.func,
		onRowSave: PropTypes.func,
		onUserDelete: PropTypes.func,
		onUserSave: PropTypes.func
	};

	@autobind
	handleEdit() {
		this.props.onRowEdit();
	}

	@autobind
	handleDelete() {
		const {
			data: {id},
			onUserDelete
		} = this.props;

		onUserDelete([id]);
	}

	@autobind
	handleSave() {
		const {
			data: {id},
			edits,
			onRowSave,
			onUserSave
		} = this.props;

		onRowSave();

		onUserSave({edits, ids: [id]});
	}

	render() {
		const {
			className,
			currentUserId,
			data,
			editing,
			itemsSelected,
			onRowCancel
		} = this.props;

		const classes = getCN(className, {
			hidden: itemsSelected
		});

		return (
			<span className={classes}>
				{!data.isOwner() &&
					data.id !== currentUserId &&
					(editing ? (
						<>
							<ClayButton
								className='button-root'
								disabled={itemsSelected}
								displayType='primary'
								onClick={this.handleSave}
								size='sm'
							>
								{Liferay.Language.get('save')}
							</ClayButton>

							<ClayButton
								className='button-root'
								disabled={itemsSelected}
								displayType='secondary'
								onClick={onRowCancel}
								size='sm'
							>
								{Liferay.Language.get('cancel')}
							</ClayButton>
						</>
					) : (
						<>
							<ClayButton
								className='button-root'
								disabled={itemsSelected}
								displayType='secondary'
								onClick={this.handleEdit}
								size='sm'
							>
								{Liferay.Language.get('edit')}
							</ClayButton>

							<ClayButton
								aria-label={Liferay.Language.get('delete')}
								borderless
								className='button-root'
								disabled={itemsSelected}
								displayType='secondary'
								onClick={this.handleDelete}
								size='sm'
							>
								<ClayIcon
									className='icon-root'
									symbol='trash'
								/>
							</ClayButton>
						</>
					))}
			</span>
		);
	}
}
