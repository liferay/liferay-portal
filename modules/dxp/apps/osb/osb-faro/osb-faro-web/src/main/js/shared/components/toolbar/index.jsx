import autobind from 'autobind-decorator';
import Checkbox from 'shared/components/Checkbox';
import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import Constants, {OrderByDirections} from 'shared/util/constants';
import FilterAndOrder, {
	getFilterAndOrderLabel
} from 'shared/components/FilterAndOrder';
import FilterTags from './FilterTags';
import getCN from 'classnames';
import Nav from 'shared/components/Nav';
import NavBar from 'shared/components/NavBar';
import PropTypes from 'prop-types';
import React from 'react';
import SearchInput from 'shared/components/SearchInput';
import SubnavTbar from 'shared/components/SubnavTbar';
import {getDefaultSortOrder, invertSortOrder} from 'shared/util/pagination';
import {getPluralMessage} from 'shared/util/lang';
import {Map, OrderedMap, Set} from 'immutable';
import {noop} from 'lodash';
import {OrderParams} from 'shared/util/records';
import {setUriFilterValues, setUriQueryValues} from 'shared/util/router';
import {withHistory} from 'shared/hoc';

const {
	cur: defaultPage,
	orderAscending,
	orderDescending
} = Constants.pagination;

/**
 * Get the filter label from filterOptions.
 * @param {string} fieldName
 * @param {string} fieldValue
 * @param {Array.<Object>} filterByOptions
 * @returns {string} - The display label for the filter.
 */
function getFilterLabel(fieldName, fieldValue, filterByOptions) {
	const filterOption = filterByOptions.find(
		option => option.key === fieldName
	);

	const filterValueObject = filterOption.values.find(
		valueItem => valueItem.value === fieldValue
	);

	return filterValueObject.label;
}

@withHistory
export default class Toolbar extends React.Component {
	static defaultProps = {
		alwaysShowSearch: false,
		autoFocus: false,
		disabled: false,
		disableSearch: false,
		filterBy: new Map(),
		filterByOptions: [],
		flatFilter: false,
		onSearchValueChange: noop,
		orderByOptions: [],
		placeholder: Liferay.Language.get('search'),
		query: '',
		searchValue: '',
		selectEntirePage: false,
		selectEntirePageIndeterminate: false,
		showCheckbox: true,
		showFilterAndOrder: true,
		showSearch: true,
		total: 0
	};

	static propTypes = {
		alwaysShowSearch: PropTypes.bool,
		autoFocus: PropTypes.bool,
		disabled: PropTypes.bool,
		disableSearch: PropTypes.bool,
		filterBy: PropTypes.instanceOf(Map),
		filterByOptions: PropTypes.array,
		flatFilter: PropTypes.bool,
		history: PropTypes.object.isRequired,
		loading: PropTypes.bool,
		maxLength: PropTypes.number,
		onFilterByChange: PropTypes.func,
		onOrderIOMapChange: PropTypes.func,
		onQueryChange: PropTypes.func,
		onSearchValueChange: PropTypes.func,
		onSelectAll: PropTypes.func,
		onSelectEntirePage: PropTypes.func,
		order: PropTypes.oneOf([orderAscending, orderDescending]), // TODO: Remove old orders
		orderByOptions: PropTypes.array,
		orderIOMap: PropTypes.object,
		placeholder: PropTypes.string,
		query: PropTypes.string,
		renderViewSelectedToggle: PropTypes.func,
		searchValue: PropTypes.string,
		selectEntirePage: PropTypes.bool,
		selectEntirePageIndeterminate: PropTypes.bool,
		showCheckbox: PropTypes.bool,
		showFilterAndOrder: PropTypes.bool,
		showSearch: PropTypes.bool,
		total: PropTypes.number
	};

	getActiveFilterTags() {
		const {filterBy, filterByOptions} = this.props;

		return filterBy
			.map((valuesISet, field) =>
				valuesISet.filter(Boolean).map(fieldValue => ({
					field,
					label: getFilterLabel(field, fieldValue, filterByOptions),
					value: fieldValue
				}))
			)
			.flatten()
			.toArray();
	}

	@autobind
	handleCheckboxChange(event) {
		this.props.onSelectEntirePage(event.currentTarget.checked);
	}

	@autobind
	handleClearAllFilters() {
		const {filterBy, history, onFilterByChange, onQueryChange} = this.props;

		const emptyFilterBy = filterBy.map(() => new Set([]));

		this.props.onSearchValueChange('');

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
	handleFilterByChange(value) {
		const {history, onFilterByChange} = this.props;

		if (onFilterByChange) {
			onFilterByChange(value);
		} else {
			history.push(
				setUriFilterValues(
					value,
					setUriQueryValues({page: defaultPage})
				)
			);
		}
	}

	@autobind
	handleFilterRemove(field, value) {
		const {filterBy, history, onFilterByChange} = this.props;

		if (onFilterByChange) {
			onFilterByChange(
				filterBy.update(field, (values = new Set()) =>
					values.has(value) ? values.delete(value) : values
				)
			);
		} else {
			history.push(
				setUriQueryValues(
					{
						[field]: filterBy.get(field).delete(value).toArray(),
						page: defaultPage
					},
					window.location.href
				)
			);
		}
	}

	@autobind
	handleOrderFieldChange(field) {
		const {history, onOrderIOMapChange} = this.props;

		const sortOrder = getDefaultSortOrder(field);

		if (onOrderIOMapChange) {
			onOrderIOMapChange(
				OrderedMap({
					[field]: new OrderParams({
						field,
						sortOrder
					})
				})
			);
		} else {
			history.push(
				setUriQueryValues(
					{
						field,
						page: defaultPage,
						sortOrder
					},
					window.location.href
				)
			);
		}
	}

	@autobind
	handleSearchSubmit(query) {
		const {history, onQueryChange} = this.props;

		onQueryChange
			? onQueryChange(query)
			: history.push(
					setUriQueryValues({
						page: defaultPage,
						query
					})
			  );
	}

	renderFilterAndOrder() {
		const {
			disabled,
			filterBy,
			filterByOptions,
			flatFilter,
			itemsSelected,
			onOrderIOMapChange,
			onSelectAll,
			orderByOptions,
			orderIOMap,
			showFilterAndOrder
		} = this.props;

		if (itemsSelected) {
			return (
				onSelectAll && (
					<Nav.Item key='SELECT_ALL'>
						<ClayButton
							className='button-root nav-btn'
							displayType='unstyled'
							onClick={onSelectAll}
						>
							{Liferay.Language.get('select-all')}
						</ClayButton>
					</Nav.Item>
				)
			);
		} else if (showFilterAndOrder && orderIOMap) {
			const {field, sortOrder} = orderIOMap.first();

			const invertedSortOrder = invertSortOrder(sortOrder);

			const ascending = sortOrder === OrderByDirections.Ascending;

			const uri = setUriQueryValues({
				field,
				page: defaultPage,
				sortOrder: invertedSortOrder
			});

			return (
				<>
					{!!(filterByOptions.length || orderByOptions.length) && (
						<Nav.Item>
							<FilterAndOrder
								disabled={disabled}
								filterBy={filterBy}
								filterByOptions={filterByOptions}
								flat={flatFilter}
								onFilterByChange={this.handleFilterByChange}
								onOrderFieldChange={this.handleOrderFieldChange}
								orderByOptions={orderByOptions}
								orderField={field}
								trigger={
									<ClayButton
										borderless
										className='d-flex align-items-center'
										displayType='secondary'
										size='sm'
									>
										{getFilterAndOrderLabel({
											filterByOptions,
											orderByOptions
										})}

										<ClayIcon
											className='ml-2 mt-0'
											symbol='caret-bottom'
										/>
									</ClayButton>
								}
							/>
						</Nav.Item>
					)}

					<Nav.Item>
						{onOrderIOMapChange ? (
							<ClayButton
								aria-label={
									ascending
										? Liferay.Language.get('ascending')
										: Liferay.Language.get('descending')
								}
								borderless
								className='btn-root nav-link nav-link-monospaced'
								disabled={disabled}
								displayType='unstyled'
								onClick={() => {
									onOrderIOMapChange(
										orderIOMap.setIn(
											[field, 'sortOrder'],
											invertedSortOrder
										)
									);
								}}
							>
								<ClayIcon
									className='icon-root'
									symbol={
										ascending
											? 'order_arrow_ascending'
											: 'order_arrow_descending'
									}
								/>
							</ClayButton>
						) : (
							<ClayLink
								borderless
								button
								className='btn-root nav-link nav-link-monospaced'
								displayType='unstyled'
								href={uri.toString()}
							>
								<ClayIcon
									className='icon-root'
									symbol={
										ascending
											? 'order_arrow_ascending'
											: 'order_arrow_descending'
									}
								/>
							</ClayLink>
						)}
					</Nav.Item>
				</>
			);
		}
	}

	render() {
		const {
			alwaysShowSearch,
			autoFocus,
			children,
			className,
			disabled,
			disableSearch,
			filterBy,
			loading,
			maxLength,
			onSearchValueChange,
			placeholder,
			query,
			renderViewSelectedToggle,
			searchValue,
			selectEntirePage,
			selectEntirePageIndeterminate,
			showCheckbox,
			showSearch,
			total
		} = this.props;

		const itemsSelected = selectEntirePage || selectEntirePageIndeterminate;

		const activeFilters = filterBy.some(values => values.some(Boolean));

		const classes = getCN({
			disabled,
			'items-selected': itemsSelected
		});

		return (
			<div className={getCN('toolbar-root', className)}>
				{(showCheckbox || showSearch) && (
					<NavBar
						className={classes}
						display={itemsSelected ? 'primary' : 'light'}
						expand
						managementBar
					>
						<Nav className='front-nav'>
							{showCheckbox && (
								<Nav.Item>
									<Checkbox
										checked={selectEntirePage}
										data-testid='select-all-checkbox'
										disabled={disabled || loading || !total}
										indeterminate={
											selectEntirePageIndeterminate
										}
										onChange={this.handleCheckboxChange}
									/>
								</Nav.Item>
							)}

							{this.renderFilterAndOrder()}
						</Nav>

						{(!itemsSelected || alwaysShowSearch) && showSearch && (
							<div className='navbar-form navbar-form-autofit'>
								<SearchInput
									autoFocus={autoFocus}
									className={getCN('search', {
										disabled: disabled || disableSearch
									})}
									disabled={disabled || disableSearch}
									maxLength={maxLength}
									onChange={onSearchValueChange}
									onSubmit={this.handleSearchSubmit}
									placeholder={placeholder}
									value={searchValue}
								/>
							</div>
						)}

						{children}
					</NavBar>
				)}

				{(query ||
					activeFilters ||
					(itemsSelected && renderViewSelectedToggle)) && (
					<SubnavTbar>
						{renderViewSelectedToggle && itemsSelected && (
							<SubnavTbar.Item className='view-selected-link-container'>
								{renderViewSelectedToggle()}
							</SubnavTbar.Item>
						)}

						{(query || activeFilters) && (
							<SubnavTbar.Item expand={!activeFilters}>
								{query
									? getPluralMessage(
											Liferay.Language.get(
												'x-result-for-x'
											),
											Liferay.Language.get(
												'x-results-for-x'
											),
											total,
											false,
											[
												total.toLocaleString(),
												<b key='QUERY_TERM'>{query}</b>
											]
									  )
									: getPluralMessage(
											Liferay.Language.get(
												'x-result-for'
											),
											Liferay.Language.get(
												'x-results-for'
											),
											total
									  )}
							</SubnavTbar.Item>
						)}

						<FilterTags
							onRemove={this.handleFilterRemove}
							tags={this.getActiveFilterTags()}
						/>

						{(query || activeFilters) && (
							<SubnavTbar.Item>
								<ClayButton
									className='button-root'
									displayType='link'
									key='FILTER_CLEAR'
									onClick={this.handleClearAllFilters}
									size='sm'
								>
									{Liferay.Language.get('clear')}
								</ClayButton>
							</SubnavTbar.Item>
						)}
					</SubnavTbar>
				)}
			</div>
		);
	}
}
