import AC_ICONS from './ac-icons';
import ClayIcon from '@clayui/icon';
import classNames from 'classnames';
import clayIconsSpritemap from '@clayui/css/lib/images/icons/icons.svg?url';
import React from 'react';

/**
 * The sprite the installed `@clayui/css` ships, so the Clay icons follow the
 * Clay version this app bundles rather than the one the portal runs.
 */

export const CLAY_ICONS_SPRITEMAP = clayIconsSpritemap;

type IconProps = React.ComponentProps<typeof ClayIcon>;

/**
 * Renders an icon by its symbol name. The Analytics Cloud icons are drawn
 * inline from their SVGR components, with the same markup `ClayIcon` gives a
 * sprite icon, and every other symbol is a Clay icon drawn from the sprite.
 */

const Icon = React.forwardRef<SVGSVGElement, IconProps>(
	({className, spritemap, symbol, ...otherProps}, ref) => {
		const ACIcon = symbol ? AC_ICONS[symbol] : undefined;

		if (!ACIcon) {
			return (
				<ClayIcon
					{...otherProps}
					className={className}
					ref={ref}
					spritemap={spritemap}
					symbol={symbol}
				/>
			);
		}

		return (
			<ACIcon
				{...otherProps}
				className={classNames(
					`lexicon-icon lexicon-icon-${symbol}`,
					className
				)}
				ref={ref}
				role="presentation"
			/>
		);
	}
);

Icon.displayName = 'Icon';

export default Icon;
