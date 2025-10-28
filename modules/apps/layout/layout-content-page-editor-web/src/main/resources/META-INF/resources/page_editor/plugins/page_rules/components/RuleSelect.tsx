/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {Option, Picker} from '@clayui/core';
import {ClayInput} from '@clayui/form';
import {usePrevious} from '@liferay/frontend-js-react-web';
import classNames from 'classnames';
import React, {MutableRefObject, useEffect} from 'react';

import {getSelectOptions} from '../../../common/getSelectOptions';

const TriggerLabel = React.forwardRef<HTMLButtonElement, any>(
	(
		{children, className: _className, onClick, triggerRef, ...otherProps},
		ref
	) => {
		useEffect(() => {
			if (ref && triggerRef) {

				// @ts-ignore
				// False positive - react-compiler/react-compiler
				// eslint-disable-next-line react-compiler/react-compiler
				triggerRef.current = ref.current;
			}
		});

		return (
			<ClayButton
				className={classNames(
					'page-editor__rule-builder-select form-control form-control-select form-control-sm'
				)}
				displayType="secondary"
				onClick={onClick}
				ref={ref}
				size="sm"
				{...otherProps}
			>
				{children}
			</ClayButton>
		);
	}
);

interface RuleSelectProps<T> {
	'aria-label'?: string;
	'items': ReadonlyArray<{label: string; value: T}>;
	'onSelectionChange': (selection: T) => void;
	'selectedKey'?: string;
	'triggerRef'?: MutableRefObject<HTMLButtonElement | undefined>;
}

export default function RuleSelect<T extends string>({
	items,
	onSelectionChange,
	selectedKey,
	triggerRef,
	...otherProps
}: RuleSelectProps<T>) {
	const previousSelectedKey = usePrevious(selectedKey);

	if (!items.length) {
		return (
			<ClayInput
				className="w-auto"
				readOnly
				sizing="sm"
				value={Liferay.Language.get('no-options-available')}
			/>
		);
	}

	return (
		<Picker
			as={TriggerLabel}
			items={getSelectOptions(items)}
			key={selectedKey === undefined && previousSelectedKey ? 0 : 1}
			messages={{
				itemDescribedby: Liferay.Language.get(
					'you-are-currently-on-a-text-element,-inside-of-a-list-box'
				),
				itemSelected: Liferay.Language.get('x-selected'),
				scrollToBottomAriaLabel:
					Liferay.Language.get('scroll-to-bottom'),
				scrollToTopAriaLabel: Liferay.Language.get('scroll-to-top'),
			}}
			onSelectionChange={(selection: React.Key) =>
				onSelectionChange(selection as T)
			}
			placeholder={Liferay.Language.get('select')}
			selectedKey={selectedKey}
			triggerRef={triggerRef}
			{...otherProps}
		>
			{(item) => <Option key={item.value}>{item.label}</Option>}
		</Picker>
	);
}
