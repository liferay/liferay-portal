import PaginationBar from 'shared/components/PaginationBar';
import PropTypes from 'prop-types';
import React from 'react';
import {paginationDefaults} from 'shared/util/pagination';

const defaultOptions = {defaultDelta: paginationDefaults.delta};

export default (options = {}) =>
	WrappedComponent => {
		const {defaultDelta} = {...defaultOptions, ...options};

		class WithPaginationBar extends React.Component {
			static defaultProps = {
				delta: defaultDelta,
				page: paginationDefaults.page
			};

			static propTypes = {
				delta: PropTypes.oneOfType([
					PropTypes.string,
					PropTypes.number
				]),
				onDeltaChange: PropTypes.func,
				onPageChange: PropTypes.func,
				page: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
				resultsMessagePlural: PropTypes.string,
				resultsMessageSingular: PropTypes.string,
				showDeltaDropdown: PropTypes.bool,
				total: PropTypes.number
			};

			render() {
				const {
					delta,
					onDeltaChange,
					onPageChange,
					page,
					resultsMessagePlural,
					resultsMessageSingular,
					showDeltaDropdown,
					total
				} = this.props;

				return (
					<>
						<WrappedComponent {...this.props} />

						{!!total && (
							<PaginationBar
								href={window.location.href}
								key='PAGINATION_BAR'
								onDeltaChange={onDeltaChange}
								onPageChange={onPageChange}
								page={page}
								resultsMessagePlural={resultsMessagePlural}
								resultsMessageSingular={resultsMessageSingular}
								selectedDelta={delta}
								showDeltaDropdown={showDeltaDropdown}
								totalItems={total}
							/>
						)}
					</>
				);
			}
		}

		return WithPaginationBar;
	};
