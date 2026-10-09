import ClayIcon from 'shared/components/Icon';
import getCN from 'classnames';
import Label from 'shared/components/Label';
import React, {FC, useState} from 'react';
import {BACKSPACE, COMMA, ENTER, SPACE} from 'shared/util/key-constants';
import {noop, partition} from 'lodash';
import {Size} from 'shared/util/colors-size';

const KEYS = [COMMA, ENTER, SPACE];

enum Display {
	Error = 'info-circle',
	Warning = 'warning-full',
}

interface IErrorProps {
	className: string;
	icon: {display: Display; size?: Size};
}

export interface IInputListProps {
	className?: string;
	disabled?: boolean;
	errorAttr?: IErrorProps;
	errorMessage?: string;
	inputValue: string;
	items: string[];
	keyCodesToSplit?: typeof KEYS;
	onInputChange?: (value: string) => void;
	onItemsChange?: (value: any) => void;
	onValidationFail?: (field: string, message: string) => void;
	placeholder?: string;
	validateOnBlur: boolean;
	validationFn: (value?: string) => boolean | void;
}

const InputList: FC<IInputListProps> = ({
	className,
	disabled = false,
	errorAttr = {
		className: 'has-error',
		icon: {display: Display.Error, size: 'sm'},
	},
	errorMessage = Liferay.Language.get('error'),
	inputValue = '',
	items = [],
	keyCodesToSplit = KEYS,
	onInputChange = noop,
	onItemsChange = noop,
	onValidationFail = noop,
	placeholder = '',
	validateOnBlur = false,
	validationFn = () => true,
}) => {
	const [focused, setFocused] = useState(false);
	const [valid, setValid] = useState(true);

	const getCharCode = (keyCode: number) => {
		if (keyCode >= 96) {
			return keyCode - 48 * Math.floor(keyCode / 48);
		}

		return keyCode;
	};

	const getStringFromKeyCode = (keycode: number) =>
		String.fromCharCode(getCharCode(keycode));

	const handleBlur = (event: React.FocusEvent<HTMLInputElement>) => {
		setFocused(false);

		if (event.target.value && validateOnBlur) {
			if (validationFn(event.target.value)) {
				onItemsChange([...items, event.target.value]);

				event.target.value = '';

				onInputChange('');
			}
			else {
				setValid(false);

				onValidationFail();
			}
		}
	};

	const handleFocus = () => {
		setFocused(true);
	};

	const handleKeyDown = (event: React.KeyboardEvent<HTMLInputElement>) => {
		const {
			keyCode,
			target: {value},
		} = event as any;

		if (value && keyCodesToSplit.includes(keyCode)) {
			event.preventDefault();

			if (validationFn(value)) {
				onItemsChange([...items, value]);

				onInputChange('');
			}
			else {
				setValid(false);

				onValidationFail();
			}
		}
		else if (!value && keyCode === BACKSPACE && items.length) {
			event.preventDefault();

			onItemsChange(items.slice(0, items.length - 1));
		}
		else {
			setValid(true);
		}
	};

	const handleInputChange = ({
		target: {value},
	}: React.ChangeEvent<HTMLInputElement>) => {
		onInputChange(value);
	};

	const handleRemoveItem = (index?: number) => {
		onItemsChange([
			...items.slice(0, index),
			...items.slice((index as number) + 1),
		]);
	};

	const handlePaste = (event: React.ClipboardEvent<HTMLInputElement>) => {
		const pastedText = event.clipboardData.getData('Text');

		const keysToSplit = keyCodesToSplit.map((keycode: number) =>
			getStringFromKeyCode(keycode)
		);

		const pastedItems = pastedText
			.split(new RegExp(`\\n|\\t|[${keysToSplit.join('')}]`))
			.filter((item) => item.length);

		if (pastedItems.length) {
			event.preventDefault();

			const [valid, invalid] = partition(
				pastedItems,
				(item) => item && validationFn(item)
			);

			if (valid.length) {
				onItemsChange([...items, ...valid]);
			}

			if (invalid.length) {
				onInputChange(invalid.join(''));

				setValid(false);

				onValidationFail();
			}
		}
	};

	const {
		className: errorClassName,
		icon: {display, size},
	} = errorAttr;

	const classes = getCN('input-group-item input-list-root', className, {
		disabled,
		[errorClassName]: !valid,
		focus: focused && valid,
	});

	return (
		<div className={classes}>
			<div className="form-control form-control-tag-group">
				{items &&
					items.length > 0 &&
					items.map((item: string, i: number) => (
						<Label
							display="secondary"
							index={i}
							key={i}
							onRemove={!disabled ? handleRemoveItem : undefined}
						>
							{item}
						</Label>
					))}

				<input
					className="form-control-inset"
					disabled={disabled}
					onBlur={handleBlur}
					onChange={handleInputChange}
					onFocus={handleFocus}
					onKeyDown={handleKeyDown}
					onPaste={handlePaste}
					placeholder={placeholder}
					type="text"
					value={inputValue}
				/>
			</div>

			{!valid && errorMessage && (
				<div className="form-feedback-group">
					<div className="form-feedback-item">
						<span className="form-feedback-indicator">
							<ClayIcon
								className={`icon-root icon-size-${size}`}
								symbol={display}
							/>
						</span>

						{errorMessage}
					</div>
				</div>
			)}
		</div>
	);
};

export {Display};
export default InputList;
