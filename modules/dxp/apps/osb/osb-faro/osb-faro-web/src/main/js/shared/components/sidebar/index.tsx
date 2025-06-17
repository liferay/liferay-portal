import * as API from 'shared/api';
import ChannelsMenu, {Channel} from '../channels-menu';
import ClayIcon from '@clayui/icon';
import getCN from 'classnames';
import React from 'react';
import SidebarItem from './SidebarItem';
import UserDropdown, {Menus} from 'shared/components/user-dropdown';
import {ACCOUNTS, Routes, SEGMENTS, toRoute} from 'shared/util/router';
import {
	DEVELOPER_MODE,
	ENABLE_ACCOUNTS,
	LANGUAGES
} from 'shared/util/constants';
import {Link, matchPath} from 'react-router-dom';
import {User} from 'shared/util/records';

interface ISidebarProps {
	activePathname: string;
	channelId: string;
	channels: Channel[];
	className?: string;
	collapsed: boolean;
	currentUser: User;
	groupId: string;
	onToggle: () => void;
}

const Sidebar: React.FC<ISidebarProps> = ({
	activePathname,
	channelId,
	channels = [],
	className,
	collapsed = false,
	currentUser = new User(),
	groupId,
	onToggle
}) => {
	const sidebarSections = [
		{
			items: [
				{
					icon: 'ac_page',
					label: Liferay.Language.get('sites'),
					route: Routes.SITES,
					url: toRoute(Routes.SITES, {channelId, groupId})
				},
				{
					icon: 'ac_assets',
					label: Liferay.Language.get('assets'),
					route: Routes.ASSETS,
					url: toRoute(Routes.ASSETS, {channelId, groupId})
				},
				{
					icon: 'ac_event_analysis',
					label: Liferay.Language.get('events'),
					route: Routes.EVENT_ANALYSIS,
					url: toRoute(Routes.EVENT_ANALYSIS, {
						channelId,
						groupId
					})
				}
			],
			label: Liferay.Language.get('touchpoints')
		},
		{
			items: [
				{
					icon: 'ac_segment',
					label: Liferay.Language.get('segments'),
					route: Routes.CONTACTS_LIST_SEGMENT,
					url: toRoute(Routes.CONTACTS_LIST_ENTITY, {
						channelId,
						groupId,
						type: SEGMENTS
					})
				},
				ENABLE_ACCOUNTS && {
					icon: 'ac_account',
					label: Liferay.Language.get('accounts'),
					route: Routes.CONTACTS_LIST_ACCOUNT,
					url: toRoute(Routes.CONTACTS_LIST_ENTITY, {
						channelId,
						groupId,
						type: ACCOUNTS
					})
				},
				{
					icon: 'ac_individual',
					label: Liferay.Language.get('individuals'),
					route: Routes.CONTACTS_INDIVIDUALS,
					url: toRoute(Routes.CONTACTS_INDIVIDUALS, {
						channelId,
						groupId
					})
				}
			].filter(Boolean) as [],
			label: Liferay.Language.get('people')
		},
		{
			// LRAC-13187 - TODO Remove Feature flag after definition of the features that will be announced to commerce and AC connection.
			hide: !DEVELOPER_MODE,
			items: [
				{
					icon: 'ac_commerce',
					label: Liferay.Language.get('commerce'),
					route: Routes.COMMERCE,
					url: toRoute(Routes.COMMERCE, {channelId, groupId})
				}
			],
			label: Liferay.Language.get('commerce')
		},
		{
			items: [
				{
					icon: 'ac_test',
					label: Liferay.Language.get('tests'),
					route: Routes.TESTS,
					url: toRoute(Routes.TESTS, {channelId, groupId})
				}
			],
			label: Liferay.Language.get('optimize')
		}
	];

	const getUserMenus = (): Menus => {
		const {emailAddress, languageId} = currentUser;

		return {
			base: [
				{
					items: [
						{
							childMenuId: 'language',
							divider: true,
							label: Liferay.Language.get('language')
						},
						{
							label: Liferay.Language.get('switch-workspaces'),
							url: Routes.BASE
						},
						{
							externalLink: true,
							label: Liferay.Language.get('sign-out'),
							url: Routes.LOGOUT
						}
					],
					subheaderLabel: emailAddress
				}
			],
			language: [
				{
					items: LANGUAGES.map(({id, label}) => {
						const active = languageId === id;

						return {
							active,
							label,
							onClick: active
								? null
								: () =>
										API.user
											.updateLanguage({
												languageId: id
											})
											.then(() =>
												window.location.reload()
											)
						};
					})
				}
			]
		};
	};

	return (
		<div className={getCN('sidebar-root', className, {collapsed})}>
			<div className='sidebar-header'>
				<Link
					className='sidebar-header-logo'
					to={toRoute(Routes.SITES, {channelId, groupId})}
				>
					<ClayIcon
						className='icon-root icon-size-md logo'
						symbol='ac_logo'
					/>
				</Link>

				<ChannelsMenu
					channels={channels}
					defaultChannelId={channelId}
					groupId={groupId}
				/>
			</div>

			<div className='sidebar-body'>
				{sidebarSections.map(
					({hide = false, items, label}, sectionIndex) =>
						!hide && (
							<div className='section' key={sectionIndex}>
								<div className='h5 section-title'>{label}</div>

								<ul className='nav-list'>
									{items.map(
										(
											{icon, label, route, url},
											itemIndex
										) => (
											<SidebarItem
												active={
													!!matchPath(
														activePathname,
														{
															path: route
														}
													)
												}
												href={url}
												icon={icon}
												key={itemIndex}
												label={label}
											/>
										)
									)}
								</ul>
							</div>
						)
				)}
			</div>

			<div className='sidebar-footer'>
				<div className='divider' />

				<ul className='nav-list'>
					<UserDropdown
						className='user-dropdown-root'
						containerElement='li'
						initialActiveMenu='base'
						menus={getUserMenus()}
						userName={currentUser.name}
					/>

					<SidebarItem
						active={
							!!matchPath(activePathname, {
								path: Routes.SETTINGS
							})
						}
						href={toRoute(Routes.SETTINGS_DATA_SOURCE_LIST, {
							groupId
						})}
						icon='cog'
						label={Liferay.Language.get('settings')}
					/>

					{DEVELOPER_MODE && (
						<SidebarItem
							active={
								!!matchPath(activePathname, {
									path: Routes.UI_KIT
								})
							}
							href={toRoute(Routes.UI_KIT, {
								channelId,
								groupId
							})}
							icon='code'
							label='UI Kit'
						/>
					)}

					<SidebarItem
						icon={
							collapsed ? 'angle-right-small' : 'angle-left-small'
						}
						onClick={onToggle}
					/>
				</ul>
			</div>
		</div>
	);
};

export default Sidebar;
