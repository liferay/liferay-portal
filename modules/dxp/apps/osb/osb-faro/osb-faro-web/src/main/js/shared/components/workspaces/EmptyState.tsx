import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import getCN from 'classnames';
import React from 'react';
import {close, modalTypes, open} from 'shared/actions/modals';
import {connect} from 'react-redux';
import {DisplayType} from 'shared/types';
import {ENABLE_ADD_TRIAL_WORKSPACE, FaroEnv} from 'shared/util/constants';
import {Routes, toRoute} from 'shared/util/router';

interface IEmptyStateProps extends React.HTMLAttributes<HTMLElement> {
	close: () => void;
	open: (string, object) => void;
}

const EmptyState: React.FC<IEmptyStateProps> = ({
	className,
	close,
	open,
	...otherProps
}) => (
	<div className={getCN('empty-container row', className)} {...otherProps}>
		<div
			className={
				FARO_ENV === FaroEnv.Production ? 'col-xl-12' : 'col-xl-6'
			}
		>
			<CardEmpty
				buttonProps={{
					displayType: 'primary',
					label: Liferay.Language.get('contact-sales'),
					onClick: () =>
						open(modalTypes.CONTACT_SALES_MODAL, {
							onClose: close
						})
				}}
				description={Liferay.Language.get(
					'do-more-with-our-business-&-enterprise-plans'
				)}
				icon='ac_integration'
			/>
		</div>

		{ENABLE_ADD_TRIAL_WORKSPACE && (
			<div className='col-xl-6'>
				<CardEmpty
					buttonProps={{
						href: toRoute(Routes.WORKSPACE_ADD_TRIAL),
						label: Liferay.Language.get('start-free-trial')
					}}
					description={Liferay.Language.get(
						'90-day-full-feature-trial'
					)}
					icon='ac_page_analytics'
				/>
			</div>
		)}
	</div>
);

interface ICardItemProps extends React.HTMLAttributes<HTMLElement> {
	buttonProps: {
		displayType?: DisplayType;
		href?: string;
		label: string;
		onClick?: () => void;
	};
	description: string;
	icon: string;
}

export const CardEmpty: React.FC<ICardItemProps> = ({
	buttonProps,
	description,
	icon
}) => {
	const {
		displayType = 'secondary',
		href,
		label,
		...otherButtonProps
	} = buttonProps;

	return (
		<div className='empty-card'>
			<ClayIcon className='icon-root' symbol={icon} />
			<p>{description}</p>
			{href ? (
				<ClayLink
					button
					displayType='secondary'
					href={href}
					{...otherButtonProps}
				>
					{label}
				</ClayLink>
			) : (
				<ClayButton displayType={displayType} {...otherButtonProps}>
					{label}
				</ClayButton>
			)}
		</div>
	);
};

export default connect(null, {close, open})(EmptyState);
