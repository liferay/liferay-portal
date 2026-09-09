import ClayIcon from '@clayui/icon';
import ClayLabel from '@clayui/label';
import ClayLink from '@clayui/link';
import ClaySticker from '@clayui/sticker';
import EventCountPill from 'shared/components/EventCountPill';
import getCN from 'classnames';
import React, {FC, useState} from 'react';
import {CampaignTouch, CampaignTouchMember} from 'shared/util/activities';
import {Text} from '@clayui/core';

const TouchRow: FC<{
	individualUrl?: string;
	member: CampaignTouchMember;
}> = ({individualUrl, member: {individualName, jobTitle, status}}) => (
	<li className="timeline-row touch-row bg-white w-100">
		<div className="row-content flex-fill d-flex align-items-center">
			<ClaySticker className="touch-sticker" shape="user-icon">
				<ClayIcon color="gray" symbol="user" />
			</ClaySticker>

			<div className="touch-info">
				{individualUrl ? (
					<ClayLink className="touch-name" href={individualUrl}>
						<Text size={3} weight="semi-bold">
							{individualName}
						</Text>
					</ClayLink>
				) : (
					<span className="touch-name">
						<Text size={3} weight="semi-bold">
							{individualName}
						</Text>
					</span>
				)}

				{jobTitle && (
					<div className="touch-job-title">
						<Text color="secondary" size={3} weight="normal">
							{jobTitle}
						</Text>
					</div>
				)}
			</div>

			<ClayLabel
				className="touch-status ml-auto flex-shrink-0 m-0"
				displayType="info"
				withClose={false}
			>
				{status}
			</ClayLabel>
		</div>
	</li>
);

const CampaignRow: FC<{
	campaign: CampaignTouch;
	individualUrls?: Record<string, string>;
}> = ({
	campaign: {campaignName, dataSourceType, touches},
	individualUrls = {},
}) => {
	const [expanded, setExpanded] = useState<boolean>(false);

	return (
		<li
			className={getCN(
				'timeline-row',
				'campaign-row',
				'bg-white',
				'w-100',
				{expanded}
			)}
		>
			<div
				className="row-main clickable d-flex align-items-center"
				onClick={() => setExpanded(!expanded)}
				onKeyPress={() => setExpanded(!expanded)}
				role="button"
				tabIndex={0}
			>
				<ClaySticker className="campaign-sticker flex-shrink-0">
					<ClayIcon
						className="row-icon icon-root text-secondary"
						symbol="megaphone"
					/>
				</ClaySticker>

				<span className="title text-dark">{campaignName}</span>

				<div className="row-details ml-auto pl-3 d-flex align-items-center">
					<ClayLabel
						className="data-source-label m-0"
						displayType="success"
						inverse
						withClose={false}
					>
						<strong>{dataSourceType.toUpperCase()}</strong>
					</ClayLabel>

					<EventCountPill
						symbol="comments"
						totalEvents={touches.length}
					/>
				</div>

				<ClayIcon
					className="angle-icon icon-root ml-3 flex-shrink-0 text-secondary"
					symbol={expanded ? 'angle-up' : 'angle-down'}
				/>
			</div>

			{expanded && (
				<ul className="timeline-rows nested">
					{touches.map((member, index) => (
						<TouchRow
							individualUrl={
								member.individualId
									? individualUrls[member.individualId]
									: undefined
							}
							key={index}
							member={member}
						/>
					))}
				</ul>
			)}
		</li>
	);
};

export default CampaignRow;
