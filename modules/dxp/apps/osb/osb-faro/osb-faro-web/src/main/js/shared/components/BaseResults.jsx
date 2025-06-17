import autobind from 'autobind-decorator';
import ClayButton from '@clayui/button';
import debounce from 'shared/util/debounce-decorator';
import getCN from 'classnames';
import NoResultsDisplay from 'shared/components/NoResultsDisplay';
import PaginationBar from 'shared/components/PaginationBar';
import PropTypes from 'prop-types';
import React from 'react';
import Toolbar from 'shared/components/toolbar';
import {ACTION_TYPES, SelectionContext} from 'shared/context/selection';
import {autoCancel, hasRequest} from 'shared/util/request-decorator';
import {hasChanges} from 'shared/util/react';
import {paginationConfig, paginationDefaults} from 'shared/util/pagination';
import {Sizes} from 'shared/util/constants';

@hasRequest
export default class BaseResults extends React.Component {
	static contextType = SelectionContext;

	static defaultProps = {
		...paginationDefaults,
		checkDisabled: () => false,
		crossPageSelect: false,
		filterByOptions: [],
		orderByOptions: [],
		placeholder: Liferay.Language.get('search'),
		query: '',
		showCheckbox: false,
		showFilterAndOrder: true,
		showPagination: true,
		showSearch: true
	};

	static propTypes = {
		...paginationConfig,
		autoFocusSearch: PropTypes.bool,
		checkDisabled: PropTypes.func,
		crossPageSelect: PropTypes.bool,
		dataSourceFn: PropTypes.func.isRequired,
		dataSourceParams: PropTypes.object,
		entityLabel: PropTypes.string,
		filterByOptions: PropTypes.array,
		maxLength: PropTypes.number,
		navRenderer: PropTypes.func,
		noResultsDescription: PropTypes.string,
		noResultsIcon: PropTypes.string,
		noResultsRenderer: PropTypes.func,
		onDeltaChange: PropTypes.func,
		onOrderIOMapChange: PropTypes.func,
		onPageChange: PropTypes.func,
		onQueryChange: PropTypes.func, // This is the value for the query
		onSearchValueChange: PropTypes.func, // This is when the value itself changes on typing
		onSelectItemsChange: PropTypes.func,
		orderByOptions: PropTypes.array,
		orderIOMap: PropTypes.object,
		placeholder: PropTypes.string,
		renderSubNav: PropTypes.func,
		resultsRenderer: PropTypes.func.isRequired,
		showCheckbox: PropTypes.bool,
		showFilterAndOrder: PropTypes.bool,
		showPagination: PropTypes.bool,
		showSearch: PropTypes.bool
	};

	state = {
		disableSearch: false,
		error: false,
		items: [],
		loading: true,
		searchValue: '',
		total: 0
	};

	constructor(props) {
		super(props);

		const {maxLength, query} = this.props;

		this.state = {
			...this.state,
			searchValue: this.getSearchValue(maxLength, query)
		};
	}

	componentDidMount() {
		this.handleFetchResults();
	}

	componentDidUpdate(prevProps) {
		if (hasChanges(prevProps, this.props, 'query')) {
			const {maxLength, query} = this.props;

			this.setState(
				{
					searchValue: this.getSearchValue(maxLength, query)
				},
				this.handleFetchResults
			);
		} else if (
			hasChanges(
				prevProps,
				this.props,
				'dataSourceFn',
				'dataSourceParams',
				'delta',
				'filterBy',
				'orderIOMap',
				'page'
			)
		) {
			this.handleFetchResults();
		}
	}

	componentWillUnmount() {
		this.fetchResults.cancel();
	}

	allChecked() {
		const {
			context: {selectedItems: selectedItemsIOMap},
			props: {checkDisabled},
			state: {items}
		} = this;

		return (
			!selectedItemsIOMap.isEmpty() &&
			items.every(
				item => selectedItemsIOMap.has(item.id) || checkDisabled(item)
			)
		);
	}

	@autobind
	clearChecked() {
		const {
			context: {selectionDispatch},
			state: {items}
		} = this;

		selectionDispatch({
			payload: {items},
			type: ACTION_TYPES.remove
		});
	}

	@debounce(250)
	@autoCancel
	fetchResults() {
		const {
			context: {selectionDispatch},
			props: {
				crossPageSelect,
				dataSourceFn,
				dataSourceParams,
				delta,
				filterBy,
				orderIOMap,
				page,
				showCheckbox
			},
			state: {searchValue: query}
		} = this;

		return dataSourceFn({
			...dataSourceParams,
			delta,
			filterBy,
			orderIOMap,
			page,
			query
		})
			.then(({disableSearch, items, total = 0}) => {
				this.setState({
					disableSearch,
					items,
					loading: false,
					total
				});

				!crossPageSelect &&
					showCheckbox &&
					selectionDispatch({type: ACTION_TYPES.clearAll});
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

	getSearchValue(maxLength, query) {
		let searchValue = query;

		if (maxLength && maxLength < searchValue.length) {
			searchValue = searchValue.slice(0, maxLength);
		}

		return searchValue;
	}

	@autobind
	handleCheckAll(checked) {
		const {
			context: {selectionDispatch},
			props: {checkDisabled},
			state: {items}
		} = this;

		selectionDispatch({
			payload: {items: items.filter(item => !checkDisabled(item))},
			type: checked ? ACTION_TYPES.add : ACTION_TYPES.remove
		});
	}

	@autobind
	handleFetchResults() {
		this.setState({
			error: false,
			loading: true
		});

		this.fetchResults();
	}

	@autobind
	handleItemsChange(item) {
		const {selectionDispatch} = this.context;

		selectionDispatch({payload: {item}, type: ACTION_TYPES.toggle});
	}

	@autobind
	handleSearchValueChange(value) {
		const {onSearchValueChange} = this.props;

		if (onSearchValueChange) {
			onSearchValueChange(value);
		}

		this.setState({
			searchValue: value
		});
	}

	/**
	 * Public method for refreshing data
	 */
	reload() {
		this.handleFetchResults();
	}

	renderContent() {
		const {
			context: {selectedItems: selectedItemsIOMap},
			props: {
				filterBy,
				noResultsRenderer,
				query,
				resultsRenderer,
				showCheckbox
			},
			state: {error, items, loading, total}
		} = this;

		const activeFilters = filterBy.some(values => values.some(Boolean));

		if (error) {
			return (
				<div className='error-info flex-grow-1'>
					<div>
						{Liferay.Language.get('an-unexpected-error-occurred')}
					</div>

					<ClayButton
						className='button-root'
						displayType='secondary'
						onClick={this.handleFetchResults}
					>
						{Liferay.Language.get('reload')}
					</ClayButton>
				</div>
			);
		} else if (!loading && !items.length && !total) {
			if (query) {
				return (
					<NoResultsDisplay
						description={Liferay.Language.get(
							'please-try-a-different-search-term'
						)}
						icon={{
							border: false,
							size: Sizes.XXXLarge,
							symbol: 'ac_no_results_found'
						}}
						spacer
						title={Liferay.Language.get(
							'there-are-no-results-found'
						)}
					/>
				);
			} else if (noResultsRenderer) {
				return noResultsRenderer(query, activeFilters);
			}
		}

		return resultsRenderer({
			items,
			loading,
			onSelectItemsChange: showCheckbox ? this.handleItemsChange : null,
			selectedItemsIOMap,
			total
		});
	}

	render() {
		const {
			context: {selectedItems: selectedItemsIOMap},
			props: {
				autoFocusSearch,
				className,
				crossPageSelect,
				delta,
				filterBy,
				filterByOptions,
				maxLength,
				navRenderer,
				onDeltaChange,
				onFilterByChange,
				onOrderIOMapChange,
				onPageChange,
				onQueryChange,
				orderByOptions,
				orderIOMap,
				page,
				placeholder,
				query,
				renderSubnav,
				showCheckbox,
				showFilterAndOrder,
				showPagination,
				showSearch
			},
			state: {disableSearch, error, items, loading, searchValue, total}
		} = this;

		const allChecked = this.allChecked();

		return (
			<div
				className={getCN(
					'base-results-root d-flex flex-column flex-grow-1',
					className
				)}
			>
				<Toolbar
					alwaysShowSearch={crossPageSelect}
					autoFocus={autoFocusSearch}
					disabled={error}
					disableSearch={disableSearch}
					filterBy={filterBy}
					filterByOptions={filterByOptions}
					loading={loading}
					maxLength={maxLength}
					onFilterByChange={onFilterByChange}
					onOrderIOMapChange={onOrderIOMapChange}
					onQueryChange={onQueryChange}
					onSearchValueChange={this.handleSearchValueChange}
					onSelectEntirePage={this.handleCheckAll}
					orderByOptions={orderByOptions}
					orderIOMap={orderIOMap}
					placeholder={placeholder}
					query={query}
					searchValue={searchValue}
					selectEntirePage={allChecked && !error}
					selectEntirePageIndeterminate={
						!allChecked && !selectedItemsIOMap.isEmpty()
					}
					showCheckbox={showCheckbox}
					showFilterAndOrder={showFilterAndOrder}
					showSearch={showSearch}
					total={total}
				>
					{navRenderer && navRenderer(selectedItemsIOMap, items)}
				</Toolbar>

				{renderSubnav &&
					!error &&
					renderSubnav({handleClearChecked: this.clearChecked})}

				{this.renderContent()}

				{showPagination && !!total && !!items.length && (
					<PaginationBar
						href={window.location.href}
						key='PAGINATION_BAR'
						onDeltaChange={onDeltaChange}
						onPageChange={onPageChange}
						page={parseInt(page)}
						selectedDelta={parseInt(delta)}
						totalItems={total}
					/>
				)}
			</div>
		);
	}
}
