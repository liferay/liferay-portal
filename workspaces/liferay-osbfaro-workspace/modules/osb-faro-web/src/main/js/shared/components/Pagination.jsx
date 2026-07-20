import autobind from 'autobind-decorator';
import ClayButton from '@clayui/button';
import ClayDropDown, {Align} from '@clayui/drop-down';
import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import FaroConstants from 'shared/util/constants';
import getCN from 'classnames';
import React from 'react';
import {isFunction, times} from 'lodash';
import {PropTypes} from 'prop-types';
import {setUriQueryValue} from 'shared/util/router';

const DEFAULT_PAGE = FaroConstants.pagination.cur;

const PAGE_BUFFER = 2;

class PaginationEllipsisItem extends React.Component {
	static propTypes = {
		onChange: PropTypes.func,
		page: PropTypes.number
	};

	@autobind
	handleChange() {
		const {onChange, page} = this.props;

		if (onChange) {
			onChange(page);
		}
	}

	render() {
		return (
			<ClayDropDown.Item {...this.props} onClick={this.handleChange}>
				{this.props.children}
			</ClayDropDown.Item>
		);
	}
}

class PaginationEllipsis extends React.Component {
	static propTypes = {
		href: PropTypes.string,
		items: PropTypes.array,
		onChange: PropTypes.func
	};

	render() {
		const {className, href, items, onChange} = this.props;

		return (
			<ClayDropDown
				alignmentPosition={Align.TopCenter}
				className={getCN('dropdown-root', className)}
				closeOnClick
				trigger={
					<ClayButton
						aria-label={Liferay.Language.get('see-more')}
						className='page-link'
						displayType='unstyled'
					>
						{'...'}
					</ClayButton>
				}
			>
				{items.map(item => (
					<PaginationEllipsisItem
						href={
							onChange
								? null
								: setUriQueryValue(href, 'page', item)
						}
						key={item}
						onChange={onChange}
						page={item}
					>
						{item}
					</PaginationEllipsisItem>
				))}
			</ClayDropDown>
		);
	}
}

class PaginationItem extends React.Component {
	static defaultProps = {
		active: false,
		disabled: false
	};

	static propTypes = {
		active: PropTypes.bool,
		ariaLabel: PropTypes.string,
		disabled: PropTypes.bool,
		href: PropTypes.string,
		onChange: PropTypes.func,
		page: PropTypes.oneOfType([PropTypes.number, PropTypes.element])
			.isRequired
	};

	@autobind
	handleChange() {
		const {onChange, page} = this.props;

		if (onChange) {
			onChange(page);
		}
	}

	render() {
		const {
			active,
			ariaLabel,
			children,
			className,
			disabled,
			href,
			onChange,
			page
		} = this.props;

		const classes = getCN('page-item', className, {active, disabled});
		const Button = onChange ? ClayButton : ClayLink;

		return (
			<li className={classes}>
				{page >= 0 ? (
					<Button
						aria-label={ariaLabel}
						className='button-root btn btn-unstyled page-link'
						disabled={disabled}
						href={onChange ? '' : href}
						onClick={this.handleChange}
					>
						{children}
					</Button>
				) : (
					children
				)}
			</li>
		);
	}
}

class Pagination extends React.Component {
	static defaultProps = {
		page: DEFAULT_PAGE
	};

	static propTypes = {
		href: PropTypes.string,
		onChange: PropTypes.func,
		page: PropTypes.number,
		total: PropTypes.number
	};

	getPages() {
		const {href, onChange, page, total} = this.props;

		const pages = times(total, i => i + 1);

		const frontBuffer = page - PAGE_BUFFER;

		if (frontBuffer > 1) {
			const start = 1;
			const numOfItems = frontBuffer - 2;

			const removedItems = pages.slice(start, start + numOfItems);

			if (removedItems.length) {
				pages.splice(
					start,
					numOfItems,
					<PaginationEllipsis
						href={href}
						items={removedItems}
						key='paginationEllipsis1'
						onChange={onChange}
					/>
				);
			}
		}

		const backBuffer = page + PAGE_BUFFER;

		if (backBuffer < total) {
			const start = pages.indexOf(backBuffer + 1);
			const numOfItems = total - backBuffer - 1;

			const removedItems = pages.slice(start, start + numOfItems);

			if (removedItems.length) {
				pages.splice(
					start,
					numOfItems,
					<PaginationEllipsis
						href={href}
						items={removedItems}
						key='paginationEllipsis2'
						onChange={onChange}
					/>
				);
			}
		}

		return pages;
	}

	render() {
		const {className, href, onChange, page, total} = this.props;

		return (
			<ul className={getCN('pagination pagination-root', className)}>
				<PaginationItem
					ariaLabel={Liferay.Language.get('previous')}
					disabled={page === 1}
					href={setUriQueryValue(href, 'page', page - 1)}
					onChange={onChange}
					page={page - 1}
				>
					<ClayIcon className='icon-root' symbol='angle-left-small' />
				</PaginationItem>

				{this.getPages().map((item, index) => (
					<PaginationItem
						active={page === item}
						href={setUriQueryValue(href, 'page', item)}
						key={index}
						onChange={onChange}
						page={isFunction(item) ? -1 : item}
					>
						{item}
					</PaginationItem>
				))}

				<PaginationItem
					ariaLabel={Liferay.Language.get('next')}
					disabled={page === total}
					href={setUriQueryValue(href, 'page', page + 1)}
					onChange={onChange}
					page={page + 1}
				>
					<ClayIcon
						className='icon-root'
						symbol='angle-right-small'
					/>
				</PaginationItem>
			</ul>
		);
	}
}

export default Pagination;
