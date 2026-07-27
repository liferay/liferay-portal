import autobind from 'autobind-decorator';
import Card from 'shared/components/Card';
import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import debounce from 'shared/util/debounce-decorator';
import EntityList from 'shared/components/EntityList';
import ErrorDisplay from 'shared/components/ErrorDisplay';
import getCN from 'classnames';
import Loading from 'shared/components/Loading';
import NoResultsDisplay from 'shared/components/NoResultsDisplay';
import React from 'react';
import SearchInput from 'shared/components/SearchInput';
import {autoCancel, hasRequest} from 'shared/util/request-decorator';
import {hasChanges} from 'shared/util/react';
import {PropTypes} from 'prop-types';

@hasRequest
export default class AssociatedSegmentsCard extends React.Component {
	static propTypes = {
		channelId: PropTypes.string,
		dataSourceFn: PropTypes.func.isRequired,
		groupId: PropTypes.string.isRequired,
		id: PropTypes.string.isRequired,
		noResultsRenderer: PropTypes.func.isRequired,
		pageUrl: PropTypes.string.isRequired
	};

	state = {
		error: false,
		items: [],
		loading: true,
		searchValue: ''
	};

	componentDidMount() {
		this.handleFetchSegments();
	}

	componentDidUpdate(prevProps, prevState) {
		if (hasChanges(prevState, this.state, 'searchValue')) {
			this.handleFetchSegments();
		}
	}

	componentWillUnmount() {
		this.handleFetchSegments.cancel();
	}

	@debounce(250)
	@autoCancel
	handleFetchSegments() {
		const {
			props: {channelId, dataSourceFn, groupId, id},
			state: {searchValue}
		} = this;

		this.setState({
			loading: true
		});

		return dataSourceFn({channelId, groupId, id, searchValue})
			.then(({items}) => {
				this.setState({
					error: false,
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

	@autobind
	handleSearch(value) {
		this.setState({
			searchValue: value
		});
	}

	renderList() {
		const {
			props: {channelId, groupId, noResultsRenderer},
			state: {error, items, loading, searchValue}
		} = this;

		if (error) {
			return (
				<ErrorDisplay
					key='ERROR_DISPLAY'
					onReload={this.handleFetchSegments}
					spacer
				/>
			);
		} else if (loading) {
			return <Loading overlay />;
		} else if (!items.length) {
			if (searchValue) {
				return (
					<NoResultsDisplay
						description={Liferay.Language.get(
							'please-try-a-different-search-term'
						)}
						title={Liferay.Language.get(
							'no-results-were-found'
						)}
					/>
				);
			}

			return noResultsRenderer();
		}

		return (
			<EntityList channelId={channelId} groupId={groupId} items={items} />
		);
	}

	render() {
		const {
			props: {className, pageUrl},
			state: {searchValue}
		} = this;

		return (
			<Card
				className={getCN('associated-segments-card-root', className)}
				minHeight={452}
			>
				<Card.Header>
					<Card.Title>
						{Liferay.Language.get('associated-segments')}
					</Card.Title>
				</Card.Header>

				<Card.Body>
					<SearchInput
						onChange={this.handleSearch}
						value={searchValue}
					/>

					{this.renderList()}
				</Card.Body>

				<Card.Footer>
					<ClayLink
						borderless
						button
						className='button-root'
						displayType='secondary'
						href={pageUrl}
						small
					>
						{Liferay.Language.get('view-all-segments')}

						<ClayIcon
							className='icon-root ml-2'
							symbol='angle-right-small'
						/>
					</ClayLink>
				</Card.Footer>
			</Card>
		);
	}
}
