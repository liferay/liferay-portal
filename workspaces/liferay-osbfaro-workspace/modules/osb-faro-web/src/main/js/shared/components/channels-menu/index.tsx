import ClayIcon from '@clayui/icon';
import getCN from 'classnames';
import PickerTriggerButton from '../PickerTriggerButton';
import React, {useState} from 'react';
import {ClayDropDownWithItems} from '@clayui/drop-down';
import {connect} from 'react-redux';
import {Routes, toRoute} from 'shared/util/router';
import {truncateText} from 'shared/util/util';
import {updateDefaultChannelId} from 'shared/actions/preferences';

const MAX_LABEL_LENGTH = 35;

const MENU_WIDTH = 280;

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
 * Built on `ClayDropDownWithItems` (`@clayui/drop-down`), Clay's own
 * ready-made searchable dropdown, rather than assembling `DropDown` +
 * `DropDown.Item` + a search input by hand.
 */

export const ChannelsMenu: React.FC<IChannelsMenuProps> = ({
	channels,
	className,
	defaultChannelId,
	groupId,
	updateDefaultChannelId,
}) => {
	const [active, setActive] = useState(false);
	const [search, setSearch] = useState('');

	const channel = getDefaultChannel(defaultChannelId, channels);

	const options = channels.filter(
		({name}) => !search || name.toLowerCase().includes(search.toLowerCase())
	);

	const items = options.map((item) => {
		const selected = item.id === defaultChannelId;

		return {
			active: selected,
			href: toRoute(Routes.SITES, {channelId: item.id, groupId}),
			onClick: () => {
				updateDefaultChannelId({
					defaultChannelId: item.id,
					groupId,
				});
			},
			symbolLeft: selected ? 'check-small' : undefined,
			title: truncateText(item.name, MAX_LABEL_LENGTH, null),
		};
	});

	return (
		<div className={getCN('channels-menu-root', className)}>
			<div className="channels-menu-label">
				{Liferay.Language.get('property')}
			</div>

			{channel ? (
				<ClayDropDownWithItems
					active={active}
					alignmentByViewport
					closeOnClickOutside
					items={items}
					menuElementAttrs={{
						className:
							'dropdown-menu-select channels-menu-dropdown',
						style: {maxWidth: 'none', width: MENU_WIDTH},
					}}
					menuWidth="shrink"
					onActiveChange={setActive}
					onSearchValueChange={setSearch}
					renderMenuOnClick
					searchable
					searchValue={search}
					trigger={
						<PickerTriggerButton
							aria-label={Liferay.Language.get('property')}
							buttonClassName="channels-menu-trigger"
							label={channel.name}
							role="combobox"
						/>
					}
				/>
			) : (
				<PickerTriggerButton
					buttonClassName="channels-menu-trigger"
					disabled
					label={Liferay.Language.get('no-properties')}
				/>
			)}

			<div className="channels-menu-icon">
				<ClayIcon className="icon-root" symbol="sites" />
			</div>
		</div>
	);
};

export default connect(null, {updateDefaultChannelId})(ChannelsMenu);
