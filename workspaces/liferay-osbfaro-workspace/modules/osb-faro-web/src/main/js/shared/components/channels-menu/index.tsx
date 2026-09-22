import ClayIcon from '@clayui/icon';
import getCN from 'classnames';
import PickerTriggerButton from '../PickerTriggerButton';
import React from 'react';
import {connect} from 'react-redux';
import {MAX_LABEL_LENGTH} from 'shared/util/constants';
import {Option, Picker} from '@clayui/core';
import {Routes, toRoute} from 'shared/util/router';
import {truncateText} from 'shared/util/util';
import {updateDefaultChannelId} from 'shared/actions/preferences';
import {useHistoryAdapter} from 'shared/hooks/useHistoryAdapter';

export type Channel = {
	createTime: number;
	groupIdCount: number;
	id: string;
	name: string;
	permissionType: number;
	tokenAuth: boolean;
};

interface IChannelsMenuProps extends React.HTMLAttributes<HTMLElement> {
	defaultChannelId?: string;
	groupId: string;
	channels: Channel[];
	updateDefaultChannelId: ({
		defaultChannelId,
		groupId,
	}: {
		defaultChannelId: string;
		groupId: string;
	}) => void;
}

export const getDefaultChannel = (
	defaultChannelId: string | undefined,
	channels: Channel[]
) => {
	if (channels && !!channels.length) {
		return channels.find(({id}) => id === defaultChannelId) || channels[0];
	}

	return null;
};

/**
 * Built on `Picker` (`@clayui/core`) with `searchable`, the same idiomatic
 * pattern `StageConditionRow.renderFieldPicker` uses for "pick one item from
 * a searchable list" — `PickerTriggerButton` as the trigger, carrying only
 * the utility classes that lay the label out against the caret.
 */

export const ChannelsMenu: React.FC<IChannelsMenuProps> = ({
	channels = [],
	className,
	defaultChannelId,
	groupId,
	updateDefaultChannelId,
}) => {
	const history = useHistoryAdapter();

	const channel = getDefaultChannel(defaultChannelId, channels);

	return (
		<div className={getCN('channels-menu-root', className)}>
			<div className="channels-menu-label">
				{Liferay.Language.get('property')}
			</div>

			<Picker
				aria-label={Liferay.Language.get('property')}
				as={PickerTriggerButton}
				block
				buttonClassName="d-flex justify-content-between align-items-center"
				disabled={!channel}
				items={channels}
				label={
					channel
						? channel.name
						: Liferay.Language.get('no-properties')
				}
				onSelectionChange={(key) => {
					const selected = channels.find(
						(item) => item.id === String(key)
					);

					if (!selected) {
						return;
					}

					updateDefaultChannelId({
						defaultChannelId: selected.id,
						groupId,
					});

					history.push(
						toRoute(Routes.SITES, {
							channelId: selected.id,
							groupId,
						})
					);
				}}
				searchable
				selectedKey={channel ? channel.id : undefined}
				size="regular"
			>
				{(item) => (
					<Option key={item.id} textValue={item.name}>
						{truncateText(item.name, MAX_LABEL_LENGTH, null)}
					</Option>
				)}
			</Picker>

			<div className="channels-menu-icon">
				<ClayIcon className="icon-root" symbol="sites" />
			</div>
		</div>
	);
};

export default connect(null, {updateDefaultChannelId})(ChannelsMenu);
