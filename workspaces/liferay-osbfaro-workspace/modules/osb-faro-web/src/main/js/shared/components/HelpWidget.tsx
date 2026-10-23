import ClayButton from '@clayui/button';
import ClayDropDown, {Align} from '@clayui/drop-down';
import ClayIcon from 'shared/components/Icon';
import ClayLink from '@clayui/link';
import HelpWidgetModal from 'shared/components/modals/help-widget-modal';
import React from 'react';
import URLConstants from 'shared/util/url-constants';
import {connect, ConnectedProps} from 'react-redux';
import {Map} from 'immutable';
import {PLANS} from 'shared/util/subscriptions';
import {RootState} from 'shared/store';
import {useModal} from '@clayui/modal';

const getDropdownItems = ({
	onReportIssue,
	showModal,
}: {
	onReportIssue: () => void;
	showModal: boolean;
}): {href?: string; label: string; onClick?: () => void; target?: string}[] => [
	showModal
		? {
				label: Liferay.Language.get('report-an-issue'),
				onClick: onReportIssue,
			}
		: {
				href: URLConstants.TicketPageLink,
				label: Liferay.Language.get('report-an-issue'),
				target: '_blank',
			},
	{
		href: URLConstants.DocumentationLink,
		label: Liferay.Language.get('help-center'),
		target: '_blank',
	},
];

const connector = connect((store: RootState, {groupId}: {groupId: string}) => ({
	faroSubscriptionIMap: store.getIn(
		['projects', groupId, 'data', 'faroSubscription'],
		Map()
	),
}));

type PropsFromRedux = ConnectedProps<typeof connector>;

interface IHelpWidgetProps extends PropsFromRedux {
	groupId: string;
}

const HelpWidget: React.FC<IHelpWidgetProps> = ({
	faroSubscriptionIMap,
	groupId,
}) => {
	const helpModal = useModal();

	const basicTier =
		faroSubscriptionIMap.get('name') === (PLANS as any).basic.name;

	return (
		<div className="help-widget-root">
			<ClayDropDown
				alignmentPosition={Align.TopLeft}
				menuElementAttrs={{
					className: 'help-dropdown-root',
				}}
				trigger={
					<ClayButton
						aria-label={Liferay.Language.get('help')}
						borderless
						className="button-root help-button position-relative"
						displayType="info"
						size="sm"
					>
						<ClayIcon
							className="icon-root"
							symbol="ac_question_mark"
						/>
					</ClayButton>
				}
			>
				{getDropdownItems({
					onReportIssue: () => helpModal.onOpenChange(true),
					showModal: basicTier,
				}).map(({href, label, onClick, target}, index) => (
					<ClayDropDown.Item key={index}>
						{href ? (
							<ClayLink
								className="btn btn-unstyled w-100"
								href={href}
								onClick={onClick}
								target={target}
							>
								{label}
							</ClayLink>
						) : (
							<ClayButton
								displayType="unstyled"
								onClick={onClick}
							>
								{label}
							</ClayButton>
						)}
					</ClayDropDown.Item>
				))}
			</ClayDropDown>

			{helpModal.open && (
				<HelpWidgetModal
					groupId={groupId}
					observer={helpModal.observer}
					onClose={() => helpModal.onOpenChange(false)}
				/>
			)}
		</div>
	);
};

export default connector(HelpWidget);
