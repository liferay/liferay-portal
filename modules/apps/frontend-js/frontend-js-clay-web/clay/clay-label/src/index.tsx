/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import classNames from 'classnames';
import React from 'react';

type WithOptional<T, P extends keyof T> = Omit<T, P> & Partial<Pick<T, P>>;

export type LabelDisplayType =
	| 'danger'
	| 'info'
	| 'secondary'
	| 'success'
	| 'unstyled'
	| 'warning';

export type ContentLabelDisplayType =
	| 'content-0'
	| 'content-1'
	| 'content-2'
	| 'content-3'
	| 'content-4'
	| 'content-5'
	| 'content-6'
	| 'content-7'
	| 'content-8';

const ItemAfter = React.forwardRef<
	HTMLSpanElement,
	React.HTMLAttributes<HTMLSpanElement>
>(({children, className, ...otherProps}, ref) => (
	<span
		{...otherProps}
		className={classNames(className, 'label-item label-item-after')}
		ref={ref}
	>
		{children}
	</span>
));

ItemAfter.displayName = 'ClayLabelItemAfter';

const ItemBefore = React.forwardRef<
	HTMLSpanElement,
	React.HTMLAttributes<HTMLSpanElement>
>(({children, className, ...otherProps}, ref) => (
	<span
		{...otherProps}
		className={classNames(className, 'label-item label-item-before')}
		ref={ref}
	>
		{children}
	</span>
));

ItemBefore.displayName = 'ClayLabelItemBefore';

const ItemExpand = React.forwardRef<
	HTMLAnchorElement | HTMLSpanElement,
	React.BaseHTMLAttributes<HTMLAnchorElement | HTMLSpanElement>
>(({children, className, href, ...otherProps}, ref) => {
	const TagName = href ? ClayLink : 'span';

	return (
		<TagName
			{...otherProps}
			className={classNames(className, 'label-item label-item-expand')}
			href={href}
			ref={ref as React.ComponentProps<typeof ClayLink>['ref']}
		>
			{children}
		</TagName>
	);
});

ItemExpand.displayName = 'ClayLabelItemExpand';

interface IBaseProps<T extends string = string>
	extends React.BaseHTMLAttributes<HTMLSpanElement> {

	/**
	 * Flag to indicate if `label-dismissible` class should be applied.
	 */
	dismissible?: boolean;

	/**
	 * Determines the style of the label.
	 */
	displayType: T;

	/**
	 * Flag to indicate if the label should be of the `inverse` variant.
	 */
	inverse?: boolean;

	/**
	 * Flag to indicate if the label should be of the `large` variant.
	 * @deprecated Use `size="lg"` instead.
	 */
	large?: boolean;

	/**
	 * Determines the size of the label.
	 */
	size?: 'lg' | 'sm';
}

const OldLabel = React.forwardRef<HTMLSpanElement, IBaseProps>(
	(
		{
			children,
			className,
			dismissible,
			displayType,
			inverse = false,
			large = false,
			size,
			...otherProps
		},
		ref
	) => {
		const useInverse = inverse && displayType !== 'unstyled';

		return (
			<span
				{...otherProps}
				className={classNames('label', className, {
					'label-dismissible': dismissible,
					'label-lg': !size && large,
					[`label-${displayType}`]: !useInverse && displayType,
					[`label-${size}`]: size,
					[`label-inverse-${displayType}`]: useInverse,
				})}
				ref={ref}
			>
				{children}
			</span>
		);
	}
);

OldLabel.displayName = 'ClayLabel';

interface IProps<T extends string = string> extends IBaseProps<T> {

	/**
	 * HTML properties that are applied to the 'x' button.
	 */
	closeButtonProps?: React.ButtonHTMLAttributes<HTMLButtonElement> & {
		ref?: (instance: HTMLButtonElement | null) => void;
	};

	/**
	 * Pros to add to the inner label item
	 */
	innerElementProps?: React.ComponentProps<typeof ItemExpand>;

	/**
	 * Path to the location of the spritemap resource used for Icon.
	 */
	spritemap?: string;

	/**
	 * Flag to indicate if component should include the close button
	 */
	withClose?: boolean;
}

const BaseLabel = React.forwardRef<HTMLAnchorElement | HTMLSpanElement, IProps>(
	(
		{
			children,
			closeButtonProps,
			href,
			innerElementProps = {},
			withClose = true,
			spritemap,
			...otherProps
		},
		ref
	) => {
		return (
			<OldLabel
				dismissible={withClose && !!closeButtonProps}
				{...otherProps}
				ref={ref}
			>
				{!withClose && children}

				{withClose && (
					<>
						<ItemExpand {...innerElementProps} href={href}>
							{children}
						</ItemExpand>

						{closeButtonProps && (
							<ItemAfter>
								<button
									{...closeButtonProps}
									className={classNames(
										closeButtonProps.className,
										'close'
									)}
									type="button"
								>
									<ClayIcon
										spritemap={spritemap}
										symbol="times-small"
									/>
								</button>
							</ItemAfter>
						)}
					</>
				)}
			</OldLabel>
		);
	}
);

BaseLabel.displayName = 'ClayBaseLabel';

const LabelComponent = React.forwardRef<
	HTMLAnchorElement | HTMLSpanElement,
	WithOptional<IProps<LabelDisplayType>, 'displayType'>
>(({displayType = 'secondary', ...props}, ref) => (
	<BaseLabel {...props} displayType={displayType} ref={ref} />
));

LabelComponent.displayName = 'ClayLabel';

const Label = Object.assign(LabelComponent, {
	ItemAfter,
	ItemBefore,
	ItemExpand,
});

const ContentLabelComponent = React.forwardRef<
	HTMLAnchorElement | HTMLSpanElement,
	Omit<IProps<ContentLabelDisplayType>, 'inverse'>
>((props, ref) => <BaseLabel {...props} inverse ref={ref} />);

ContentLabelComponent.displayName = 'ClayContentLabel';

const ContentLabel = Object.assign(ContentLabelComponent, {
	ItemAfter,
	ItemBefore,
	ItemExpand,
});

export {ContentLabel, ItemAfter, ItemBefore, ItemExpand};
export default Label;
