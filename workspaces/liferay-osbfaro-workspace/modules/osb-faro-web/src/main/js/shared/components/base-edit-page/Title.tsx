import ClayForm, {ClayInput} from '@clayui/form';
import ClayIcon from 'shared/components/Icon';
import getCN from 'classnames';
import React from 'react';

interface ITitleProps {
	errorMessage?: string;
	id: string;
	label: string;
	name?: string;
	onBlur?: (event: React.FocusEvent<HTMLInputElement>) => void;
	onChange: (event: React.ChangeEvent<HTMLInputElement>) => void;
	placeholder?: string;
	required?: boolean;
	value: string;
}

/**
 * Enter only confirms the title, so it does not submit the editor's form.
 */
const handleKeyDown = (event: React.KeyboardEvent<HTMLInputElement>) => {
	if (event.key === 'Enter') {
		event.preventDefault();

		event.currentTarget.blur();
	}
};

const Title: React.FC<ITitleProps> = ({
	errorMessage,
	id,
	label,
	name,
	onBlur,
	onChange,
	placeholder,
	required = false,
	value,
}) => (
	<ClayForm.Group className={getCN('mb-0', {'has-error': !!errorMessage})}>
		<label className="mb-0 text-3" htmlFor={id}>
			{label}

			{required && (
				<ClayIcon className="reference-mark" symbol="asterisk" />
			)}
		</label>

		<ClayInput
			aria-describedby={errorMessage ? `${id}Error` : undefined}
			className="form-control-inline"
			id={id}
			name={name}
			onBlur={onBlur}
			onChange={onChange}
			onKeyDown={handleKeyDown}
			placeholder={placeholder}
			required={required}
			type="text"
			value={value}
		/>

		{errorMessage && (
			<p
				className="font-weight-semi-bold mb-0 mt-1 text-danger"
				id={`${id}Error`}
				role="alert"
			>
				<ClayIcon className="mr-1" symbol="info-circle" />

				{errorMessage}
			</p>
		)}
	</ClayForm.Group>
);

export default Title;
