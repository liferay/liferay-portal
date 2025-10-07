/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import classNames from 'classnames';
import React from 'react';

// @ts-ignore

import TooltipTextRenderer from './TooltipTextRenderer';

interface DefaultRendererOptions {
	truncate?: boolean;
}

type DefaultRendererValue =
	| string
	| number
	| boolean
	| null
	| undefined
	| {
			icon?: string;
			iconSymbol?: string;
			label?: string;
			label_i18n?: string;
			text?: string;
	  };

const Wrapper = ({
	children,
	options,
}: {
	children: React.ReactNode;
	options?: DefaultRendererOptions;
}) => {
	return options?.truncate ? (
		<span
			className={classNames(
				'default-renderer__text-truncate',
				'text-truncate'
			)}
		>
			{children}
		</span>
	) : (
		<>{children}</>
	);
};

const DefaultRenderer: React.FC<
	{children?: React.ReactNode | undefined} & {
		options?: DefaultRendererOptions;
		value: DefaultRendererValue;
	}
> = ({options, value}) => {
	if (
		typeof value === 'number' ||
		typeof value === 'string' ||
		React.isValidElement(value)
	) {
		return <Wrapper options={options}>{value}</Wrapper>;
	}

	if (typeof value === 'boolean') {
		return (
			<Wrapper options={options}>
				{value
					? Liferay.Language.get('yes')
					: Liferay.Language.get('no')}
			</Wrapper>
		);
	}

	if (value === null || typeof value !== 'object') {
		return null;
	}

	if (value.icon) {
		return <ClayIcon symbol={value.icon} />;
	}

	if (!!value.iconSymbol && !!value.text) {
		return <TooltipTextRenderer value={value} />;
	}

	if (value.label_i18n) {
		return <Wrapper options={options}>{value.label_i18n}</Wrapper>;
	}

	if (value.label) {
		return <Wrapper options={options}>{value.label}</Wrapper>;
	}

	if (
		typeof value === 'object' &&
		Array.isArray(value) &&
		typeof value[0] !== 'object'
	) {
		return <Wrapper options={options}>{value.join(', ')}</Wrapper>;
	}

	if (typeof value === 'object') {
		return <Wrapper options={options}>{JSON.stringify(value)}</Wrapper>;
	}

	return null;
};

export default DefaultRenderer;
