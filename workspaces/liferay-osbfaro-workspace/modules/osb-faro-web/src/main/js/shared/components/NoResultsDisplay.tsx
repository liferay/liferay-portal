import ClayIcon from '@clayui/icon';
import getCN from 'classnames';
import React from 'react';
import {Sizes} from 'shared/util/constants';
import {sub} from 'shared/util/lang';

interface IIconProps {
	border?: boolean;
	size?: Sizes;
	symbol: string;
}

export interface INoResultsDisplayProps
	extends React.HTMLAttributes<HTMLElement> {
	children?: React.ReactElement;
	description?: string | React.ReactNode;
	displayCard?: boolean;
	flexGrow?: boolean;
	icon?: IIconProps;
	primary?: boolean;
	spacer?: boolean;
	title?: string;
}

interface INoResultsDisplayIcon
	extends React.HTMLAttributes<HTMLDivElement>,
		IIconProps {}

const NoResultsDisplayIcon: React.FC<INoResultsDisplayIcon> = ({
	border = true,
	size = Sizes.XXXLarge,
	symbol,
}) => {
	const classes = getCN('no-results-icon', {
		'no-results-icon-border': border,
	});

	return (
		<div className={classes}>
			<ClayIcon
				className={getCN('icon-root', {[`icon-size-${size}`]: size})}
				symbol={symbol}
			/>
		</div>
	);
};

const NoResultsDisplay: React.FC<INoResultsDisplayProps> = ({
	children,
	className,
	description,
	displayCard = false,
	flexGrow = true,
	icon,
	primary = false,
	spacer = false,
	title = getFormattedTitle(),
	...otherProps
}) => {
	const classes = getCN(className, 'no-results-root', {
		'display-card': displayCard,
		'flex-grow-0': !flexGrow,
		'flex-grow-1': flexGrow,
		'no-results-primary': primary,
	});

	return (
		<div {...otherProps} className={classes}>
			<div className={getCN('no-results-content', {spacer})}>
				{icon && <NoResultsDisplayIcon {...icon} />}

				{title && <div className="h4 no-results-title">{title}</div>}

				{description && (
					<div className="no-results-description">{description}</div>
				)}

				{children}
			</div>
		</div>
	);
};

type GetFormattedTitle = (name?: string, title?: string) => string;

export const getFormattedTitle: GetFormattedTitle = (
	name = Liferay.Language.get('items').toLowerCase(),
	title = Liferay.Language.get('no-x-were-found')
) => sub(title, [name]) as string;

export default NoResultsDisplay;
