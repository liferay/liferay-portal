/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Button, {ButtonProps} from '@clayui/button';
import {ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import classNames from 'classnames';
import {useRef} from 'react';

import LiferayFile from '../../../../../interfaces/liferayFile';
import WrapperInput from '../common/components/WrapperInput';
import PRMFormFieldProps from '../common/interfaces/prmFormFieldProps';
import PRMFormFieldStateProps from '../common/interfaces/prmFormFieldStateProps';

interface IProps {
	displayType: ButtonProps['displayType'];
	onAccept: (liferayFile: LiferayFile) => void;
	outline?: boolean;
	small?: boolean;
}

const InputFile = ({
	description,
	displayType,
	field,
	label,
	meta,
	onAccept,
	outline,
	required,
	small,
	value,
}: PRMFormFieldProps & PRMFormFieldStateProps<LiferayFile> & IProps) => {
	const inputFileRef = useRef<HTMLInputElement>(null);

	const handleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
		const file = event.target.files?.[0];

		return file && onAccept(file);
	};

	return (
		<WrapperInput
			{...meta}
			description={description}
			label={label}
			required={required}
		>
			<div>
				<Button
					className={classNames('bg-neutral-0', {
						'border-danger': meta.touched && meta.error,
						'border-success': meta.touched && !meta.error,
					})}
					displayType={displayType}
					name={field.name}
					onBlur={field.onBlur}
					onClick={() => inputFileRef.current?.click()}
					outline={outline}
					small={small}
				>
					<span className="inline-item inline-item-before">
						<ClayIcon symbol="upload" />
					</span>

					{value?.name || field.value?.name || 'Upload file'}
				</Button>
			</div>

			<ClayInput
				hidden
				name={field.name}
				onChange={handleChange}
				ref={inputFileRef}
				type="file"
				value=""
			/>
		</WrapperInput>
	);
};

export default InputFile;
