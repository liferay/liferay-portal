import ClayButton from '@clayui/button';
import ClayDropDown from '@clayui/drop-down';
import ClayIcon from 'shared/components/Icon';
import ClayLink from '@clayui/link';
import getCN from 'classnames';
import React from 'react';
import {MenuItem} from './types';

const UserMenuDropdownItem: React.FC<
	React.ComponentProps<typeof ClayDropDown.Item> & MenuItem
> = ({
	active,
	className,
	externalLink,
	icon,
	iconAlignment,
	label,
	onClick,
	url,
}) => {
	const Content = () => {
		if (iconAlignment === 'left') {
			return (
				<>
					{icon && (
						<ClayIcon className="icon-root  mr-2" symbol={icon} />
					)}

					{label}
				</>
			);
		}

		return (
			<>
				{label}

				{icon && <ClayIcon className="icon-root  ml-2" symbol={icon} />}
			</>
		);
	};

	return (
		<ClayDropDown.Item className={getCN(className, {active})}>
			{url ? (
				externalLink ? (
					<ClayLink
						className="button-root btn btn-block btn-unstyled"

						// @ts-ignore
						externalLink
						href={url}
					>
						<Content />
					</ClayLink>
				) : (
					<ClayLink
						block
						button
						className="button-root"
						displayType="unstyled"
						href={url}
					>
						<Content />
					</ClayLink>
				)
			) : (
				<ClayButton
					block
					className="button-root"
					displayType="unstyled"
					onClick={onClick}
				>
					<Content />
				</ClayButton>
			)}
		</ClayDropDown.Item>
	);
};

export default UserMenuDropdownItem;
