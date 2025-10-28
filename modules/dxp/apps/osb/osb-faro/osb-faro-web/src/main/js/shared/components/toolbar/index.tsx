import Checkbox from 'shared/components/Checkbox';
import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import Constants from 'shared/util/constants';
import FilterAndOrder, {
	getFilterAndOrderLabel
} from 'shared/components/FilterAndOrder';
import FilterTags from './FilterTags';
import getCN from 'classnames';
import Nav from 'shared/components/Nav';
import NavBar from 'shared/components/NavBar';
import React from 'react';
import SearchInput from 'shared/components/SearchInput';
import SubnavTbar from 'shared/components/SubnavTbar';
import {FilterByType} from 'shared/types';
import {getDefaultSortOrder} from 'shared/util/pagination';
import {getPluralMessage} from 'shared/util/lang';
import {Map, OrderedMap, Set} from 'immutable';
import {noop} from 'lodash';
import {OrderParams} from 'shared/util/records';
import {setUriFilterValues, setUriQueryValues} from 'shared/util/router';
import {useHistory} from 'react-router-dom';

const {cur: defaultPage} = Constants.pagination;

function getFilterLabel(fieldName, fieldValue, filterByOptions) {
	const filterOption = filterByOptions.find(
		option => option.key === fieldName
	);

	const filterValueObject = filterOption.values.find(
		valueItem => valueItem.value === fieldValue
	);

	return filterValueObject.label;
}

interface IToolbarProps extends React.HTMLAttributes<HTMLElement> {
	alwaysShowSearch?: boolean;
	autoFocus?: boolean;
	disabled?: boolean;
	disableSearch?: boolean;
	filterBy?: FilterByType;
	filterByOptions?: [];
	flatFilter?: boolean;
	loading?: boolean;
	maxLength?: number;
	onFilterByChange?: (value: FilterByType) => void;
	onOrderIOMapChange?: (order: OrderedMap<string, OrderParams>) => void;
	onQueryChange?: (query: string) => void;
	onSearchValueChange?: (value: string) => void;
	onSelectAll?: () => void;
	onSelectEntirePage?: (event: Event) => void;
	order?: 'asc' | 'desc'; // TODO: Remove old orders
	orderByOptions?: [];
	orderIOMap?: any;
	placeholder?: string;
	query?: string;
	renderViewSelectedToggle?: () => React.ReactNode;
	searchValue?: string;
	selectEntirePage?: boolean;
	selectEntirePageIndeterminate?: boolean;
	showCheckbox?: boolean;
	showFilterAndOrder?: boolean;
	showSearch?: boolean;
	total?: number;
}

const Toolbar: React.FC<IToolbarProps> = ({
	alwaysShowSearch = false,
	autoFocus = false,
	children,
	className,
	disabled = false,
	disableSearch = false,
	filterBy = Map(),
	filterByOptions = [],
	flatFilter = false,
	loading,
	maxLength,
	onFilterByChange,
	onOrderIOMapChange,
	onQueryChange,
	onSearchValueChange = noop,
	onSelectAll,
	onSelectEntirePage,
	orderByOptions = [],
	orderIOMap,
	placeholder = Liferay.Language.get('search'),
	query = '',
	renderViewSelectedToggle,
	searchValue = '',
	selectEntirePage = false,
	selectEntirePageIndeterminate = false,
	showCheckbox = true,
	showFilterAndOrder = true,
	showSearch = true,
	total = 0
}) => {
	const history = useHistory();

	const itemsSelected = selectEntirePage || selectEntirePageIndeterminate;

	const activeFilters = filterBy.some(values => values.some(Boolean));

	const classes = getCN('my-2', {
		disabled,
		'items-selected': itemsSelected
	});

	const handleCheckboxChange = event => {
		onSelectEntirePage(event.currentTarget.checked);
	};

	const handleClearAllFilters = () => {
		const emptyFilterBy = filterBy.map(() => Set([]));

		onSearchValueChange('');

		if (onQueryChange || onFilterByChange) {
			onQueryChange && onQueryChange('');

			onFilterByChange && onFilterByChange(emptyFilterBy as FilterByType);
		} else {
			history.push(
				setUriFilterValues(
					emptyFilterBy,
					setUriQueryValues({page: defaultPage, query: ''})
				)
			);
		}
	};

	const handleFilterByChange = value => {
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
	};

	const handleFilterRemove = (field, value) => {
		if (onFilterByChange) {
			onFilterByChange(
				filterBy.update(field, (values = Set()) =>
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
	};

	const handleOrderFieldChange = field => {
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
	};

	const handleSearchSubmit = query => {
		onQueryChange
			? onQueryChange(query)
			: history.push(
					setUriQueryValues({
						page: defaultPage,
						query
					})
			  );
	};

	const renderFilterAndOrder = () => {
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
			const {field} = orderIOMap.first();

			return (
				<>
					{!!(filterByOptions.length || orderByOptions.length) && (
						<Nav.Item>
							<FilterAndOrder
								disabled={disabled}
								filterBy={filterBy}
								filterByOptions={filterByOptions}
								flat={flatFilter}
								onFilterByChange={handleFilterByChange}
								onOrderFieldChange={handleOrderFieldChange}
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
				</>
			);
		}
	};

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
									onChange={handleCheckboxChange}
								/>
							</Nav.Item>
						)}

						{renderFilterAndOrder()}
					</Nav>

					{(!itemsSelected || alwaysShowSearch) && showSearch && (
						<div className='navbar-form navbar-form-autofit mx-2'>
							<SearchInput
								autoFocus={autoFocus}
								className={getCN('search', {
									disabled: disabled || disableSearch
								})}
								disabled={disabled || disableSearch}
								maxLength={maxLength}
								onChange={onSearchValueChange}
								onSubmit={handleSearchSubmit}
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
										Liferay.Language.get('x-result-for-x'),
										Liferay.Language.get('x-results-for-x'),
										total,
										false,
										[
											total.toLocaleString(),
											<b key='QUERY_TERM'>{query}</b>
										]
								  )
								: getPluralMessage(
										Liferay.Language.get('x-result-for'),
										Liferay.Language.get('x-results-for'),
										total
								  )}
						</SubnavTbar.Item>
					)}

					<FilterTags
						onRemove={handleFilterRemove}
						tags={filterBy
							.map((valuesISet, field) =>
								valuesISet.filter(Boolean).map(fieldValue => ({
									field,
									label: getFilterLabel(
										field,
										fieldValue,
										filterByOptions
									),
									value: fieldValue
								}))
							)
							.flatten()
							.toArray()}
					/>

					{(query || activeFilters) && (
						<SubnavTbar.Item>
							<ClayButton
								className='button-root'
								displayType='link'
								key='FILTER_CLEAR'
								onClick={handleClearAllFilters}
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
};

export default Toolbar;
