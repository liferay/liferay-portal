import autobind from 'autobind-decorator';
import Card from 'shared/components/Card';
import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import ErrorDisplay from 'shared/components/ErrorDisplay';
import getCN from 'classnames';
import NoResultsDisplay from 'shared/components/NoResultsDisplay';
import React from 'react';
import Table from 'shared/components/table';
import URLConstants from 'shared/util/url-constants';
import {autoCancel, hasRequest} from 'shared/util/request-decorator';
import {individualsListColumns} from 'shared/util/table-columns';
import {PropTypes} from 'prop-types';
import {Routes, toRoute} from 'shared/util/router';

@hasRequest
export default class KnownIndividualsCard extends React.Component {
	static propTypes = {
		channelId: PropTypes.string,
		dataSourceFn: PropTypes.func.isRequired,
		groupId: PropTypes.string.isRequired,
		id: PropTypes.string.isRequired
	};

	state = {
		error: false,
		items: [],
		loading: true
	};

	componentDidMount() {
		this.handleFetchItems();
	}

	@autoCancel
	@autobind
	handleFetchItems() {
		const {channelId, dataSourceFn, groupId, id} = this.props;

		this.setState({
			error: false,
			loading: true
		});

		return dataSourceFn({channelId, groupId, id})
			.then(({items}) => {
				this.setState({
					items,
					loading: false
				});
			})
			.catch(err => {
				if (!err.IS_CANCELLATION_ERROR) {
					this.setState({
						error: true,
						loading: false
					});
				}
			});
	}

	renderTable() {
		const {
			props: {channelId, groupId},
			state: {error, items, loading}
		} = this;

		if (error) {
			return (
				<ErrorDisplay
					key='ERROR_DISPLAY'
					onReload={this.handleFetchItems}
					spacer
				/>
			);
		} else if (!loading && !items.length) {
			return (
				<NoResultsDisplay
					description={
						<>
							<span className='mr-1'>
								{Liferay.Language.get(
									'check-back-later-to-verify-if-data-has-been-received-from-your-data-sources'
								)}
							</span>

							<ClayLink
								href={
									URLConstants.IndividualsDashboardDocumentation
								}
								key='DOCUMENTATION'
								target='_blank'
							>
								{Liferay.Language.get(
									'learn-more-about-individuals'
								)}
							</ClayLink>
						</>
					}
					key='NO_RESULTS_DISPLAY'
					spacer
					title={Liferay.Language.get(
						'no-individuals-were-found'
					)}
				/>
			);
		} else {
			return (
				<Table
					columns={[
						individualsListColumns.getNameJobTitle({
							channelId,
							groupId
						})
					]}
					items={items}
					loading={loading}
					rowIdentifier='id'
				/>
			);
		}
	}

	render() {
		const {channelId, className, groupId, id} = this.props;

		return (
			<Card className={getCN('known-individuals-card-root', className)}>
				<Card.Header>
					<Card.Title>
						{Liferay.Language.get('known-individuals')}
					</Card.Title>
				</Card.Header>

				{this.renderTable()}

				<Card.Footer>
					<ClayLink
						borderless
						button
						className='button-root'
						displayType='secondary'
						href={toRoute(Routes.CONTACTS_ACCOUNT_INDIVIDUALS, {
							channelId,
							groupId,
							id
						})}
						small
					>
						{Liferay.Language.get('view-all-individuals')}

						<ClayIcon className='ml-2' symbol='angle-right-small' />
					</ClayLink>
				</Card.Footer>
			</Card>
		);
	}
}
