import autobind from 'autobind-decorator';
import ClayButton from '@clayui/button';
import ClayIcon from 'shared/components/Icon';
import Input from './Input';
import omitDefinedProps from 'shared/util/omitDefinedProps';
import React from 'react';
import {addContext} from 'shared/util/clay';
import {noop} from 'lodash';
import {PropTypes} from 'prop-types';
import {Stack} from 'immutable';

export const CONTEXT = 'search-input';

export default class SearchInput extends React.Component {
	static childContextTypes = {
		clay: PropTypes.instanceOf(Stack)
	};

	static defaultProps = {
		disabled: false,
		onSubmit: noop,
		placeholder: Liferay.Language.get('search'),
		value: ''
	};

	static propTypes = {
		disabled: PropTypes.bool,
		onChange: PropTypes.func,
		onSubmit: PropTypes.func,
		placeholder: PropTypes.string,
		value: PropTypes.string
	};

	constructor(props) {
		super(props);

		this._inputRef = React.createRef();
	}

	getChildContext() {
		return addContext(this, CONTEXT);
	}

	@autobind
	handleClearSearch() {
		const {onChange, onSubmit} = this.props;

		if (onChange) {
			onChange('');
		}

		if (onSubmit) {
			onSubmit('');
		}

		this._inputRef.current._elementRef.current.focus();
	}

	@autobind
	handleChange(event) {
		const {onChange} = this.props;

		if (onChange) {
			onChange(event.target.value);
		}
	}

	@autobind
	handleKeyDown(event) {
		if (event.key === 'Enter') {
			event.preventDefault();

			this.props.onSubmit(
				this._inputRef.current._elementRef.current.value
			);
		}
	}

	@autobind
	handleSubmit() {
		this.props.onSubmit(this._inputRef.current._elementRef.current.value);
	}

	render() {
		const {className, disabled, placeholder, value, ...otherProps} =
			this.props;

		return (
			<Input.Group className={className}>
				<Input.GroupItem>
					<Input
						{...omitDefinedProps(otherProps, SearchInput.propTypes)}
						disabled={disabled}
						inset='after'
						onChange={this.handleChange}
						onKeyDown={this.handleKeyDown}
						placeholder={placeholder}
						ref={this._inputRef}
						value={value}
					/>

					<Input.Inset position='after'>
						<ClayButton
							aria-label={
								value
									? Liferay.Language.get('clear')
									: Liferay.Language.get('search')
							}
							className='button-root'
							disabled={disabled}
							displayType='unstyled'
							onClick={
								value
									? this.handleClearSearch
									: this.handleSubmit
							}
						>
							{value ? (
								<ClayIcon
									className='icon-root'
									symbol='times'
								/>
							) : (
								<ClayIcon
									className='icon-root'
									symbol='search'
								/>
							)}
						</ClayButton>
					</Input.Inset>
				</Input.GroupItem>
			</Input.Group>
		);
	}
}
