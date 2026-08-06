import * as API from 'shared/api';
import autobind from 'autobind-decorator';
import ClayButton from '@clayui/button';
import ClayLink from '@clayui/link';
import Form, {validateInputMessage} from 'shared/components/form';
import getCN from 'classnames';
import React from 'react';
import Sheet from 'shared/components/Sheet';
import TextTruncate from 'shared/components/TextTruncate';
import {close, modalTypes, open} from 'shared/actions/modals';
import {connect} from 'react-redux';
import {DataSource} from 'shared/util/records';
import {EntityTypes} from 'shared/util/constants';
import {getRouteName} from 'shared/util/router';
import {getTypeLangKey, sub} from 'shared/util/lang';
import {
	individualsListColumns,
	segmentsListColumns
} from 'shared/util/table-columns';
import {noop} from 'lodash/fp';
import {PropTypes} from 'prop-types';
import {Routes, toRoute} from 'shared/util/router';
import {toLocale} from 'shared/util/numbers';

/**
 * Get the API for the specific entityType.
 * @param {number} entityType
 * @returns {function} - API function.
 */
function getEntityApi(entityType) {
	switch (entityType) {
		case EntityTypes.Individual:
			return API.individuals.search;
		case EntityTypes.IndividualsSegment:
		default:
			return API.individualSegment.search;
	}
}

/**
 * Get the data source function for entity's modal.
 * @param {number} entityType
 * @returns {function} - Data source function.
 */
function getDataSourceFn(entityType) {
	const entityApi = getEntityApi(entityType);

	return ({dataSourceId, delta, groupId, orderIOMap, page, query}) =>
		entityApi({
			dataSourceId,
			delta,
			groupId,
			orderIOMap,
			page,
			query
		});
}

/**
 * Get the table columns for entity's modal.
 * @param {number} entityType
 * @param {string} timeZoneId
 * @returns {array}
 */
function getEntityColumns(entityType, timeZoneId) {
	switch (entityType) {
		case EntityTypes.Individual:
			return [
				individualsListColumns.name,
				individualsListColumns.jobTitle,
				individualsListColumns.activitiesCount,
				individualsListColumns.getLastActivityDate(timeZoneId),
				individualsListColumns.willBeRemoved
			];
		case EntityTypes.IndividualsSegment:
		default:
			return [
				segmentsListColumns.name,
				segmentsListColumns.activitiesCount,
				segmentsListColumns.getOwnerName(timeZoneId)
			];
	}
}

/**
 * Get the title for the entity's modal.
 * @param {number} entityType
 * @param {string} dataSourceName
 * @returns {string}
 */
function getEntityTitle(entityType, dataSourceName) {
	const TruncatedName = () => (
		<TextTruncate inline maxCharLength={50} title={dataSourceName} />
	);

	switch (entityType) {
		case EntityTypes.Individual:
			return sub(
				Liferay.Language.get('x-s-individuals'),
				[<TruncatedName key='NAME' />],
				false
			);
		case EntityTypes.IndividualsSegment:
		default:
			return Liferay.Language.get('related-segments');
	}
}

class DataSourceItem extends React.Component {
	static defaultProps = {
		onClick: noop
	};

	static propTypes = {
		entityType: PropTypes.number.isRequired,
		onClick: PropTypes.func,
		secondaryInfo: PropTypes.string.isRequired,
		title: PropTypes.string.isRequired
	};

	@autobind
	handleClick() {
		const {entityType, onClick} = this.props;

		onClick(entityType);
	}

	render() {
		const {secondaryInfo, title} = this.props;

		return (
			<div
				className={`entity-item${
					this.props.className ? ` ${this.props.className}` : ''
				}`}
			>
				<ClayButton
					className='button-root title'
					displayType='unstyled'
					onClick={this.handleClick}
				>
					<h5>{title}</h5>
				</ClayButton>

				<div className='secondary-info'>{secondaryInfo}</div>
			</div>
		);
	}
}

export class DeleteDataSource extends React.Component {
	static propTypes = {
		actionRequestFn: PropTypes.func.isRequired,
		close: PropTypes.func.isRequired,
		dataSource: PropTypes.instanceOf(DataSource).isRequired,
		deleteMessage: PropTypes.string.isRequired,
		deletePhrase: PropTypes.string.isRequired,
		entitiesCount: PropTypes.object,
		groupId: PropTypes.string.isRequired,
		id: PropTypes.string.isRequired,
		open: PropTypes.func.isRequired,
		pageActionConfirmationText: PropTypes.string.isRequired,
		pageActionText: PropTypes.string.isRequired,
		timeZoneId: PropTypes.string
	};

	state = {
		valid: false
	};

	@autobind
	handleDeleteDataSource(_, {setSubmitting}) {
		const {
			actionRequestFn,
			close,
			open,
			pageActionConfirmationText,
			pageActionText
		} = this.props;

		open(modalTypes.CONFIRMATION_MODAL, {
			message: (
				<div>
					<div className='h4 text-secondary'>
						{pageActionConfirmationText}
					</div>

					<b>
						{Liferay.Language.get(
							'you-will-permanently-lose-all-contacts-and-analytics-data-collected-from-this-data-source.-you-will-not-be-able-to-undo-this-action'
						)}
					</b>
				</div>
			),
			modalVariant: 'modal-warning',
			onClose: close,
			onSubmit: actionRequestFn,
			submitButtonDisplay: 'warning',
			submitMessage: pageActionText,
			title: pageActionText,
			titleIcon: 'warning'
		});

		setSubmitting(false);
	}

	@autobind
	handleEntityModal(entityType) {
		const {
			close,
			dataSource: {name},
			groupId,
			id,
			open,
			timeZoneId
		} = this.props;

		open(modalTypes.SEARCHABLE_ENTITIES_TABLE_MODAL, {
			columns: getEntityColumns(entityType, timeZoneId),
			dataSourceFn: getDataSourceFn(entityType),
			dataSourceParams: {dataSourceId: id, groupId},
			entityLabel: getTypeLangKey(entityType),
			entityType: getRouteName(entityType),
			onClose: close,
			rowIdentifier: 'id',
			title: getEntityTitle(entityType, name)
		});
	}

	renderDataSourceItems() {
		const {entitiesCount} = this.props;

		const items = [
			{
				entityType: EntityTypes.IndividualsSegment,
				secondaryInfo: Liferay.Language.get(
					'segments-with-criteria-related-to-this-data-source-will-be-disabled-until-the-criteria-is-updated'
				),
				title: sub(Liferay.Language.get('x-segments'), [
					toLocale(entitiesCount[EntityTypes.IndividualsSegment])
				])
			},
			{
				entityType: EntityTypes.Individual,
				secondaryInfo: sub(
					Liferay.Language.get(
						'all-attributes-related-to-an-x-from-this-data-source-will-be-removed,-which-may-result-in-the-removal-of-the-x'
					),
					[Liferay.Language.get('individual')]
				),
				title: sub(Liferay.Language.get('x-individuals'), [
					toLocale(entitiesCount[EntityTypes.Individual])
				])
			}
		];

		return items.map((params, i) => (
			<DataSourceItem
				{...params}
				key={i}
				onClick={this.handleEntityModal}
			/>
		));
	}

	render() {
		const {
			props: {
				className,
				dataSource: {name},
				deleteMessage,
				deletePhrase,
				groupId,
				id,
				pageActionText
			}
		} = this;

		return (
			<div className={getCN('delete-data-source-root', className)}>
				<div>{this.renderDataSourceItems()}</div>

				<Form
					initialValues={{delete: ''}}
					onSubmit={this.handleDeleteDataSource}
				>
					{({handleSubmit, isSubmitting, isValid}) => (
						<Form.Form data-testid='form' onSubmit={handleSubmit}>
							<Sheet.Body>
								<div>{deleteMessage}</div>

								<div className='copy-container'>
									<div className='h4'>
										{sub(
											Liferay.Language.get(
												'copy-the-following-x'
											),
											[
												<span
													className='copy-text'
													key='COPY_TEXT'
												>
													{sub(deletePhrase, [name])}
												</span>
											],
											false
										)}
									</div>
								</div>

								<Form.Input
									data-testid='confirmation-input'
									name='delete'
									validate={validateInputMessage(
										sub(deletePhrase, [name])
									)}
								/>
							</Sheet.Body>

							<Sheet.Footer divider={false}>
								<ClayButton
									className='button-root delete-button'
									disabled={!isValid || isSubmitting}
									displayType='warning'
									type='submit'
								>
									{pageActionText}
								</ClayButton>

								<ClayLink
									button
									className='button-root'
									displayType='secondary'
									href={toRoute(Routes.SETTINGS_DATA_SOURCE, {
										groupId,
										id
									})}
								>
									{Liferay.Language.get('cancel')}
								</ClayLink>
							</Sheet.Footer>
						</Form.Form>
					)}
				</Form>
			</div>
		);
	}
}

export default connect(
	(store, {groupId}) => ({
		timeZoneId: store.getIn([
			'projects',
			groupId,
			'data',
			'timeZone',
			'timeZoneId'
		])
	}),
	{
		close,
		open
	}
)(DeleteDataSource);
