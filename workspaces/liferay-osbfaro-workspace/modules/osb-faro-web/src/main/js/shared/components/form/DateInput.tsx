import DateInput, {OverlayAlignment} from '../DateInput';
import getCN from 'classnames';
import HelpBlock from './HelpBlock';
import Label from './Label';
import React from 'react';
import {FieldProps} from 'formik';
import {isNumber} from 'lodash';

interface IFormDateInputProps
	extends FieldProps,
		React.HTMLAttributes<HTMLElement> {
	inline: boolean;
	label: string;
	overlayAlignment?: OverlayAlignment;
	popover: {
		content: React.ReactNode;
		title: React.ReactNode;
	};
	required?: boolean;
	showRetentionPeriod?: boolean;
	usePortal?: boolean;
	width: number;
}

const FormDateInput: React.FC<IFormDateInputProps> = ({
	className,
	field,
	form,
	inline = false,
	label,
	overlayAlignment = 'bottomLeft',
	popover,
	required = false,
	showRetentionPeriod = true,
	usePortal = true,
	width,
}) => {
	const {name, value} = field;

	const handleChange = (value: any): void => {
		const {setFieldValue} = form;

		setFieldValue(name, value);
	};

	const error = form.errors[name];
	const touched = form.touched[name];

	const classes = getCN('form-date-input-root', className, {
		'form-inline-group': inline,
		'has-error': error && touched,
		'has-success': !error && touched,
	});

	const style = isNumber(width)
		? {flexBasis: `${width}%`, flexGrow: 0}
		: undefined;

	return (
		<div className={classes} style={style}>
			{label && (
				<Label htmlFor={name} popover={popover} required={required}>
					{label}
				</Label>
			)}

			<DateInput
				aria-required={required || undefined}
				id={name}
				name={name}
				onDateInputChange={handleChange}
				overlayAlignment={overlayAlignment}
				showRetentionPeriod={showRetentionPeriod}
				usePortal={usePortal}
				value={value}
			/>

			<HelpBlock name={name} />
		</div>
	);
};

export default FormDateInput;
