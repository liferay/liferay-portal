import autobind from 'autobind-decorator';
import ClayButton from '@clayui/button';
import Constants, {Sizes} from 'shared/util/constants';
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
import {setUriFilterValues, setUriQueryValues} from 'shared/util/router';
import {withHistory} from 'shared/hoc';

const {cur: defaultPage} = Constants.pagination;

/**
 * Memoized so typing in the search input — which only mutates the parent's
 * `searchValue` state — does not force the results subtree (e.g. a large
 * paginated table with custom cell renderers) to reconcile on every keystroke.
 */
const ResultsContent = React.memo(function ResultsContent({
	enableClearSearch,
	error,
	filterBy,
	items,
	loading,
	noResultsRenderer,
	onClearAllFilters,
	onItemsChange,
	onRetry,
	query,
	resultsRenderer,
	selectedItemsIOMap,
	showCheckbox,
	total
}) {
	if (error) {
		return (
			<div className='error-info flex-grow-1'>
				<div>
					{Liferay.Language.get('an-unexpected-error-occurred')}
				</div>

				<ClayButton
					className='button-root'
					displayType='secondary'
					onClick={onRetry}
				>
					{Liferay.Language.get('reload')}
				</ClayButton>
			</div>
		);
	}

	if (!loading && !items.length && !total) {
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
					title={Liferay.Language.get('no-results-were-found')}
				>
					{enableClearSearch && (
						<ClayButton
							className='button-root'
							displayType='secondary'
							onClick={onClearAllFilters}
						>
							{Liferay.Language.get('clear-search')}
						</ClayButton>
					)}
				</NoResultsDisplay>
			);
		}

		if (noResultsRenderer) {
			const activeFilters = filterBy.some(values => values.some(Boolean));

			return noResultsRenderer(query, activeFilters);
		}
	}

	return resultsRenderer({
		items,
		loading,
		onSelectItemsChange: showCheckbox ? onItemsChange : null,
		selectedItemsIOMap,
		total
	});
});

@withHistory
@hasRequest
export default class BaseResults extends React.Component {
	static contextType = SelectionContext;

	static defaultProps = {
		...paginationDefaults,
		checkDisabled: () => false,
		crossPageSelect: false,
		enableClearSearch: false,
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
		enableClearSearch: PropTypes.bool,
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
	handleClearAllFilters() {
		const {
			filterBy,
			history,
			onFilterByChange,
			onQueryChange,
			onSearchValueChange
		} = this.props;

		const emptyFilterBy = filterBy.map(() => Set([]));

		if (onSearchValueChange) {
			onSearchValueChange('');
		}

		if (onQueryChange || onFilterByChange) {
			onQueryChange && onQueryChange('');
			onFilterByChange && onFilterByChange(emptyFilterBy);
		} else {
			history.push(
				setUriFilterValues(
					emptyFilterBy,
					setUriQueryValues({page: defaultPage, query: ''})
				)
			);
		}
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

	render() {
		const {
			context: {selectedItems: selectedItemsIOMap},
			props: {
				autoFocusSearch,
				className,
				crossPageSelect,
				delta,
				enableClearSearch,
				filterBy,
				filterByOptions,
				maxLength,
				navRenderer,
				noResultsRenderer,
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
				resultsRenderer,
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

				<ResultsContent
					enableClearSearch={enableClearSearch}
					error={error}
					filterBy={filterBy}
					items={items}
					loading={loading}
					noResultsRenderer={noResultsRenderer}
					onClearAllFilters={this.handleClearAllFilters}
					onItemsChange={this.handleItemsChange}
					onRetry={this.handleFetchResults}
					query={query}
					resultsRenderer={resultsRenderer}
					selectedItemsIOMap={selectedItemsIOMap}
					showCheckbox={showCheckbox}
					total={total}
				/>

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
