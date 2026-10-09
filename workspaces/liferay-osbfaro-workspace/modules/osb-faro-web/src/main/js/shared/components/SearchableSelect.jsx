import autobind from 'autobind-decorator';
import ClayButton from '@clayui/button';
import ClayDropDown from '@clayui/drop-down';
import ClayIcon from 'shared/components/Icon';
import getCN from 'classnames';
import Loading from 'shared/components/Loading';
import NoResultsDisplay, {
	getFormattedTitle
} from 'shared/components/NoResultsDisplay';
import omitDefinedProps from 'shared/util/omitDefinedProps';
import React from 'react';
import SearchInput from './SearchInput';
import {noop} from 'lodash';
import {PropTypes} from 'prop-types';

class Item extends React.Component {
	static defaultProps = {
		onSelect: noop
	};

	static propTypes = {
		item: PropTypes.object.isRequired,
		onSelect: PropTypes.func
	};

	@autobind
	handleClick() {
		this.props.onSelect(this.props.item);
	}

	render() {
		const {item, ...otherProps} = this.props;

		return (
			<ClayDropDown.Item
				{...omitDefinedProps(otherProps, Item.propTypes)}
				closeOnClick
				onClick={this.handleClick}
			>
				{item.name}
			</ClayDropDown.Item>
		);
	}
}

/**
 * @deprecated Prefer building on BaseSelect.
 */
class SearchableSelect extends React.Component {
	static defaultProps = {
		buttonPlaceholder: '',
		inputPlaceholder: '',
		inputValue: '',
		onSearchChange: noop,
		onSelect: noop,
		showSearch: true
	};

	static propTypes = {
		buttonPlaceholder: PropTypes.string,
		footerButtonMessage: PropTypes.string,
		footerOnClick: PropTypes.func,
		inputPlaceholder: PropTypes.string,
		inputValue: PropTypes.string,
		items: PropTypes.array.isRequired,
		loading: PropTypes.bool,
		onChange: PropTypes.func,
		onSelect: PropTypes.func,
		readOnly: PropTypes.bool,
		selectedItem: PropTypes.shape({
			name: PropTypes.string,
			value: PropTypes.any
		}),
		showSearch: PropTypes.bool
	};

	@autobind
	handleSelect(item) {
		this.props.onSelect(item);
	}

	isActiveItem(selected, item) {
		const valuesEqual =
			selected.value && item.value && selected.value === item.value;

		const namesEqual =
			selected.name && item.name && selected.name === item.name;

		return !!(valuesEqual || namesEqual);
	}

	renderDropdownItems() {
		const {items, loading, selectedItem} = this.props;

		if (loading) {
			return <Loading />;
		} else if (!items.length) {
			return <NoResultsDisplay title={getFormattedTitle()} />;
		} else {
			return items.map((item, i) => {
				if (item.subheader) {
					return (
						<ClayDropDown.Section key={i}>
							{item.name}
						</ClayDropDown.Section>
					);
				} else {
					return (
						<Item
							active={
								selectedItem &&
								this.isActiveItem(selectedItem, item)
							}
							item={item}
							key={i}
							onSelect={this.handleSelect}
						/>
					);
				}
			});
		}
	}

	render() {
		const {
			buttonPlaceholder,
			className,
			disabled,
			footerButtonMessage,
			footerOnClick,
			inputPlaceholder,
			inputValue,
			onSearchChange,
			selectedItem,
			showSearch
		} = this.props;

		return (
			<ClayDropDown
				className={getCN('searchable-select-root', className)}
				closeOnClick
				trigger={
					<ClayButton
						className='button-root d-flex align-items-center justify-content-between w-100'
						disabled={disabled}
						displayType='unstyled'
					>
						{(selectedItem && selectedItem.name) ||
							buttonPlaceholder}

						<ClayIcon
							className='icon-root ml-2'
							symbol='caret-bottom'
						/>
					</ClayButton>
				}
			>
				{showSearch && (
					<ClayDropDown.Section>
						<SearchInput
							onChange={onSearchChange}
							placeholder={inputPlaceholder}
							value={inputValue}
						/>
					</ClayDropDown.Section>
				)}

				<ClayDropDown.Section className='items-wrapper'>
					{this.renderDropdownItems()}
				</ClayDropDown.Section>

				{footerOnClick && (
					<ClayDropDown.Section className='footer-action'>
						<ClayButton
							block
							className='button-root'
							displayType='primary'
							onClick={footerOnClick}
						>
							{footerButtonMessage}
						</ClayButton>
					</ClayDropDown.Section>
				)}
			</ClayDropDown>
		);
	}
}

export default SearchableSelect;
