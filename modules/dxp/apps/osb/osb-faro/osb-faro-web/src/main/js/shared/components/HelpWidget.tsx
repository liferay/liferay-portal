import ClayButton from '@clayui/button';
import ClayDropDown, {Align} from '@clayui/drop-down';
import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import React from 'react';
import URLConstants from 'shared/util/url-constants';
import {close, modalTypes, open} from 'shared/actions/modals';
import {connect, ConnectedProps} from 'react-redux';
import {Map} from 'immutable';
import {Modal} from 'shared/types';
import {PLANS} from 'shared/util/subscriptions';
import {RootState} from 'shared/store';

const getDropdownItems = ({
	close,
	groupId,
	open,
	showModal
}: {
	close: Modal.close;
	groupId: string;
	open: Modal.open;
	showModal: boolean;
}): {href?: string; label: string; onClick?: () => void; target?: string}[] => [
	showModal
		? {
				label: Liferay.Language.get('report-an-issue'),
				onClick: () => {
					open(modalTypes.HELP_WIDGET_MODAL, {
						groupId,
						onClose: close
					});
				}
		  }
		: {
				href: URLConstants.TicketPageLink,
				label: Liferay.Language.get('report-an-issue'),
				target: '_blank'
		  },
	{
		href: URLConstants.DocumentationLink,
		label: Liferay.Language.get('help-center'),
		target: '_blank'
	}
];

const connector = connect(
	(store: RootState, {groupId}: {groupId: string}) => ({
		faroSubscriptionIMap: store.getIn(
			['projects', groupId, 'data', 'faroSubscription'],
			Map()
		)
	}),
	{close, open}
);

type PropsFromRedux = ConnectedProps<typeof connector>;

interface IHelpWidgetProps extends PropsFromRedux {
	groupId: string;
}

const HelpWidget: React.FC<IHelpWidgetProps> = ({
	close,
	faroSubscriptionIMap,
	groupId,
	open
}) => {
	const basicTier = faroSubscriptionIMap.get('name') === PLANS.basic.name;

	return (
		<div className='help-widget-root'>
			<ClayDropDown
				alignmentPosition={Align.TopLeft}
				menuElementAttrs={{
					className: 'help-dropdown-root'
				}}
				trigger={
					<ClayButton
						aria-label={Liferay.Language.get('help')}
						borderless
						className='button-root help-button'
						displayType='info'
						size='sm'
					>
						<ClayIcon
							className='icon-root'
							symbol='ac_question_mark'
						/>
					</ClayButton>
				}
			>
				{getDropdownItems({
					close,
					groupId,
					open,
					showModal: basicTier
				}).map(({href, label, onClick, target}, index) => (
					<ClayDropDown.Item key={index}>
						{href ? (
							<ClayLink
								className='btn btn-unstyled w-100'
								href={href}
								onClick={onClick}
								target={target}
							>
								{label}
							</ClayLink>
						) : (
							<ClayButton
								displayType='unstyled'
								onClick={onClick}
							>
								{label}
							</ClayButton>
						)}
					</ClayDropDown.Item>
				))}
			</ClayDropDown>
		</div>
	);
};

export default connector(HelpWidget);
