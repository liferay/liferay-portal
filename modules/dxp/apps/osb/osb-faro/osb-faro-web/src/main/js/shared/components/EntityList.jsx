import autobind from 'autobind-decorator';
import Avatar from './Avatar';
import getCN from 'classnames';
import ListGroup from './list-group';
import ListView from './ListView';
import Loading from 'shared/components/Loading';
import omitDefinedProps from 'shared/util/omitDefinedProps';
import React from 'react';
import Sticker from './Sticker';
import TextTruncate from './TextTruncate';
import {EntityTypes} from '../util/constants';
import {getDataSourceLangKey} from 'shared/util/lang';
import {getRouteName, Routes, toRoute} from 'shared/util/router';
import {LIFERAY_SITE_TYPE} from 'shared/util/data-sources';
import {Link} from 'react-router-dom';
import {PropTypes} from 'prop-types';
import {Set} from 'immutable';
import {sub} from '../util/lang';

class EntityListItem extends React.Component {
	static propTypes = {
		item: PropTypes.object.isRequired
	};

	state = {
		checked: false
	};

	getEntityUrl() {
		const {
			channelId,
			groupId,
			item: {id, type, url}
		} = this.props;

		switch (type) {
			case LIFERAY_SITE_TYPE:
				return url ? url : '#';
			case EntityTypes.DataSource:
				return toRoute(Routes.SETTINGS_DATA_SOURCE, {
					groupId,
					id
				});
			default:
				return toRoute(Routes.CONTACTS_ENTITY, {
					channelId,
					groupId,
					id,
					type: getRouteName(type)
				});
		}
	}

	getMessage() {
		const {friendlyURL, properties, providerType, type} = this.props.item;

		switch (type) {
			case EntityTypes.Individual:
				return sub(Liferay.Language.get('x-at-x'), [
					properties.jobTitle ||
						Liferay.Language.get('not-available'),
					properties.worksFor || Liferay.Language.get('not-available')
				]);
			case EntityTypes.DataSource:
				return getDataSourceLangKey(providerType);
			case LIFERAY_SITE_TYPE:
				return friendlyURL;
			default:
				return;
		}
	}

	handleLinkClick(event) {
		event.stopPropagation();
	}

	render() {
		const {item} = this.props;

		const {name, type} = item;

		return (
			<>
				<ListGroup.ItemField>
					{type === EntityTypes.IndividualsSegment ? (
						<Sticker
							display='primary'
							symbol='individual_dynamic_segment'
						/>
					) : (
						<Avatar
							circle={type === EntityTypes.Individual}
							entity={item}
						/>
					)}
				</ListGroup.ItemField>

				<ListGroup.ItemField className='justify-content-center' expand>
					<TextTruncate title={name}>
						<strong>
							<Link
								className='list-group-link'
								onClick={this.handleLinkClick}
								to={this.getEntityUrl()}
							>
								{name ? name : Liferay.Language.get('unknown')}
							</Link>
						</strong>
					</TextTruncate>

					<div className='secondary-info'>{this.getMessage()}</div>
				</ListGroup.ItemField>
			</>
		);
	}
}

class EntityList extends React.Component {
	static defaultProps = {
		disabledItemsISet: new Set(),
		loading: false,
		selectedItemsISet: new Set(),
		selectMultiple: true,
		showBorder: true,
		showHeader: true
	};

	static propTypes = {
		accentColor: PropTypes.string,
		channelId: PropTypes.string,
		disabledItemsISet: PropTypes.instanceOf(Set),
		entityType: PropTypes.number,
		groupId: PropTypes.string.isRequired,
		header: PropTypes.string,
		items: PropTypes.array,
		loading: PropTypes.bool,
		noItemsContent: PropTypes.any,
		noItemsHeader: PropTypes.string,
		onSelectItemsChange: PropTypes.func,
		selectedItemsISet: PropTypes.instanceOf(Set),
		selectMultiple: PropTypes.bool,
		showBorder: PropTypes.bool,
		showHeader: PropTypes.bool,
		total: PropTypes.number
	};

	@autobind
	getItemRenderer() {
		return ({item}) => {
			const {channelId, groupId} = this.props;

			return (
				<EntityListItem
					channelId={channelId}
					groupId={groupId}
					item={item}
				/>
			);
		};
	}

	render() {
		const {
			className,
			disabledItemsISet,
			items,
			loading,
			noItemsContent,
			onSelectItemsChange,
			selectedItemsISet,
			selectMultiple,
			showBorder,
			total,
			...otherProps
		} = this.props;

		const noItems = total === 0;

		const classes = getCN('entity-list-root', className, {
			'no-items': noItems
		});

		return (
			<div className={classes}>
				<ListView
					{...omitDefinedProps(otherProps, EntityList.propTypes)}
					disabledItemsISet={disabledItemsISet}
					itemRenderer={this.getItemRenderer()}
					items={items}
					noBorder={!showBorder}
					onSelectItemsChange={onSelectItemsChange}
					selectedItemsISet={selectedItemsISet}
					selectMultiple={selectMultiple}
				/>

				{loading && <Loading overlay />}

				{!loading && noItems && (
					<div className='status-overlay'>{noItemsContent}</div>
				)}
			</div>
		);
	}
}

export default EntityList;
