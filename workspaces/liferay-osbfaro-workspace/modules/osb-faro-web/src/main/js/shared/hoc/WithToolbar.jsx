import autobind from 'autobind-decorator';
import FaroConstants, {RangeKeyTimeRanges} from 'shared/util/constants';
import PropTypes from 'prop-types';
import React from 'react';
import SearchToolbar from 'shared/components/search-toolbar';
import withHistory from './WithHistory';
import {DropdownRangeKey} from 'shared/components/dropdown-range-key/DropdownRangeKey';
import {get} from 'lodash';
import {hasChanges} from 'shared/util/react';
import {paginationDefaults} from 'shared/util/pagination';
import {pickBy} from 'lodash';
import {removeUriQueryParam, setUriQueryValues} from 'shared/util/router';

const {
	pagination: {cur: DEFAULT_CUR}
} = FaroConstants;

export default configs => WrappedComponent => {
	class WithToolbarBar extends React.Component {
		static defaultProps = {
			disableSearch: false,
			query: paginationDefaults.query,
			rangeSelectors: {
				rangeEnd: '',
				rangeKey: RangeKeyTimeRanges.Last30Days,
				rangeStart: ''
			},
			showCheckbox: false
		};

		static propTypes = {
			alwaysShowSearch: PropTypes.bool,
			disableSearch: PropTypes.bool,
			history: PropTypes.object,
			onRangeSelectorsChange: PropTypes.func,
			onSearchValueChange: PropTypes.func,
			onSelectEntirePage: PropTypes.func,
			orderByOptions: PropTypes.array,
			orderIOMap: PropTypes.object,
			query: PropTypes.string,
			rangeKeys: PropTypes.array,
			rangeSelectors: PropTypes.object,
			renderNav: PropTypes.func,
			renderViewSelectedToggle: PropTypes.func,
			selectEntirePage: PropTypes.bool,
			selectEntirePageIndeterminate: PropTypes.bool,
			showCheckbox: PropTypes.bool,
			showFilterAndOrder: PropTypes.bool,
			total: PropTypes.number
		};

		constructor(props) {
			super(props);

			this.state = {
				searchValue: props.query
			};
		}

		componentDidUpdate(prevProps) {
			const {
				props: {query},
				state: {searchValue}
			} = this;

			if (
				hasChanges(prevProps, this.props, 'query') &&
				query !== searchValue
			) {
				this.setState({searchValue: query});
			}
		}

		@autobind
		handleRangeSelectorsChange(rangeSelectors) {
			const {history, onRangeSelectorsChange} = this.props;

			const {rangeEnd, rangeKey, rangeStart} = rangeSelectors;

			onRangeSelectorsChange
				? onRangeSelectorsChange(rangeSelectors)
				: history.push(
						setUriQueryValues(
							pickBy({
								page: DEFAULT_CUR,
								rangeEnd,
								rangeKey,
								rangeStart
							}),
							removeUriQueryParam(
								window.location.href,
								'rangeEnd',
								'rangeStart'
							)
						)
				  );
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

		render() {
			const {
				props: {
					alwaysShowSearch,
					disableSearch,
					onFilterByChange,
					onOrderIOMapChange,
					onQueryChange,
					onSelectEntirePage,
					orderByOptions,
					orderIOMap,
					query,
					rangeKeys,
					rangeSelectors,
					renderNav,
					renderViewSelectedToggle,
					selectEntirePage,
					selectEntirePageIndeterminate,
					showCheckbox,
					showDropdownRangeKey,
					showFilterAndOrder,
					total,
					...otherProps
				},
				state: {searchValue}
			} = this;

			return (
				<>
					<SearchToolbar
						{...otherProps}
						alwaysShowSearch={alwaysShowSearch}
						disableSearch={get(
							configs,
							'disableSearch',
							disableSearch
						)}
						onFilterByChange={onFilterByChange}
						onOrderIOMapChange={onOrderIOMapChange}
						onQueryChange={onQueryChange}
						onSearchValueChange={this.handleSearchValueChange}
						onSelectEntirePage={onSelectEntirePage}
						orderByOptions={orderByOptions}
						orderIOMap={orderIOMap}
						query={query}
						renderViewSelectedToggle={renderViewSelectedToggle}
						searchValue={searchValue}
						selectEntirePage={selectEntirePage}
						selectEntirePageIndeterminate={
							selectEntirePageIndeterminate
						}
						showCheckbox={showCheckbox}
						showFilterAndOrder={get(
							configs,
							'showFilterAndOrder',
							showFilterAndOrder
						)}
						showSearch
						total={total}
					>
						{get(
							configs,
							'showDropdownRangeKey',
							showDropdownRangeKey
						) && (
							<DropdownRangeKey
								legacy={get(
									configs,
									'legacyDropdownRangeKey',
									true
								)}
								onRangeSelectorChange={
									this.handleRangeSelectorsChange
								}
								rangeKeys={rangeKeys}
								rangeSelectors={rangeSelectors}
							/>
						)}

						{renderNav && renderNav(this.props)}
					</SearchToolbar>

					<WrappedComponent {...this.props} />
				</>
			);
		}
	}

	return withHistory(WithToolbarBar);
};
