/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Option, Picker} from '@clayui/core';
import classNames from 'classnames';
import {FieldBase} from 'frontend-js-components-web';
import React, {Key, ReactElement} from 'react';

import './SingleSelect.scss';

type SingleSelectOption = {
	label?: string;
	value?: string | number;
};

interface SingleSelectProps<T extends SingleSelectOption> {
	as?:
		| 'button'
		| React.ForwardRefExoticComponent<any>
		| ((props: React.HTMLAttributes<HTMLElement>) => JSX.Element);
	children?: (item: T) => ReactElement;
	className?: string;
	defaultSelectedKey?: Key;
	disabled?: boolean;
	error?: string;
	feedbackMessage?: string;
	id?: string;
	items: T[];
	label?: string;
	onSelectionChange?: (itemValue: React.Key) => void;
	placeholder?: string;
	required?: boolean;
	selectedKey?: string | number;
	tooltip?: string;
}

export function SingleSelect<T extends SingleSelectOption>({
	as,
	children,
	className,
	defaultSelectedKey,
	disabled,
	error,
	feedbackMessage,
	id,
	items,
	label,
	onSelectionChange,
	placeholder = Liferay.Language.get('select-an-option'),
	required,
	selectedKey,
	tooltip,
}: SingleSelectProps<T>) {
	return (
		<FieldBase
			className={className}
			disabled={disabled}
			errorMessage={error}
			helpMessage={feedbackMessage}
			id={id}
			label={label}
			required={required}
			tooltip={tooltip}
		>
			<Picker<T>
				UNSAFE_menuClassName={classNames('lfr__object-single-select', {
					className,
				})}
				as={as}
				defaultSelectedKey={defaultSelectedKey}
				disabled={disabled}
				id={id}
				items={items}
				messages={{
					itemDescribedby: Liferay.Language.get(
						'you-are-currently-on-a-text-element,-inside-of-a-list-box'
					),
					itemSelected: Liferay.Language.get('x-selected'),
					scrollToBottomAriaLabel:
						Liferay.Language.get('scroll-to-bottom'),
					scrollToTopAriaLabel: Liferay.Language.get('scroll-to-top'),
				}}
				name={id}
				onSelectionChange={onSelectionChange}
				placeholder={placeholder}
				selectedKey={selectedKey}
			>
				{(item) =>
					children ? (
						children(item)
					) : (
						<Option key={item.value}>{item.label}</Option>
					)
				}
			</Picker>
		</FieldBase>
	);
}
