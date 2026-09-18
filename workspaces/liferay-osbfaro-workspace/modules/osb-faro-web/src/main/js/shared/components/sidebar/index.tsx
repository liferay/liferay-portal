import * as API from 'shared/api';
import ChannelsMenu, {Channel} from '../channels-menu';
import ClayIcon from '@clayui/icon';
import getCN from 'classnames';
import Panel from '@clayui/panel';
import React from 'react';
import SidebarItem from './SidebarItem';
import UserDropdown, {Menus} from 'shared/components/user-dropdown';
import {ACCOUNTS, Routes, SEGMENTS, toRoute} from 'shared/util/router';
import {DEVELOPER_MODE, LANGUAGES} from 'shared/util/constants';
import {Link, matchPath} from 'react-router-dom';
import {Map} from 'immutable';
import {useLDPEnabled} from 'shared/hooks/useLDPEnabled';
import {User} from 'shared/util/records';

interface ISidebarProps {
	activePathname: string;
	channelId: string;
	channels: Channel[];
	className?: string;
	collapsed: boolean;
	currentUser: User;
	expandedSections: Map<string, boolean>;
	groupId: string;
	onSectionExpandedChange: (sectionKey: string, expanded: boolean) => void;
	onToggle: () => void;
}

const Sidebar: React.FC<ISidebarProps> = ({
	activePathname,
	channelId,
	channels = [],
	className,
	collapsed = false,
	currentUser = new User(),
	expandedSections = Map(),
	groupId,
	onSectionExpandedChange,
	onToggle,
}) => {
	const LDPEnabled = useLDPEnabled({groupId});

	const sidebarSections = [
		{
			items: [
				LDPEnabled && {
					icon: 'plant',
					label: Liferay.Language.get('lifecycles'),
					route: Routes.LIFECYCLE,
					url: toRoute(Routes.LIFECYCLE, {channelId, groupId}),
				},
				LDPEnabled && {
					icon: 'megaphone',
					label: Liferay.Language.get('campaigns'),
					route: Routes.CAMPAIGNS,
					url: toRoute(Routes.CAMPAIGNS, {channelId, groupId}),
				},
				{
					icon: 'sites',
					label: Liferay.Language.get('sites'),
					route: Routes.SITES,
					url: toRoute(Routes.SITES, {channelId, groupId}),
				},
				{
					icon: 'sheets',
					label: Liferay.Language.get('assets'),
					route: Routes.ASSETS,
					url: toRoute(Routes.ASSETS, {
						channelId,
						groupId,
					}),
				},
				{
					icon: 'click',
					label: Liferay.Language.get('events'),
					route: Routes.EVENT_ANALYSIS,
					url: toRoute(Routes.EVENT_ANALYSIS, {
						channelId,
						groupId,
					}),
				},
			].filter(Boolean) as [],
			key: 'touchpoints',
			label: Liferay.Language.get('touchpoints'),
		},
		{
			items: [
				{
					icon: 'box-squared',
					label: Liferay.Language.get('segments'),
					route: `${Routes.CONTACTS}/${SEGMENTS}`,
					url: toRoute(Routes.CONTACTS_LIST_ENTITY, {
						channelId,
						groupId,
						type: SEGMENTS,
					}),
				},
				LDPEnabled && {
					icon: 'briefcase',
					label: Liferay.Language.get('accounts'),
					route: `${Routes.CONTACTS}/${ACCOUNTS}`,
					url: toRoute(Routes.CONTACTS_LIST_ENTITY, {
						channelId,
						groupId,
						type: ACCOUNTS,
					}),
				},
				{
					icon: 'users',
					label: Liferay.Language.get('individuals'),
					route: Routes.CONTACTS_INDIVIDUALS,
					url: toRoute(Routes.CONTACTS_INDIVIDUALS, {
						channelId,
						groupId,
					}),
				},
			].filter(Boolean) as [],
			key: 'people',
			label: Liferay.Language.get('people'),
		},
		{
			items: [
				{
					icon: 'test',
					label: Liferay.Language.get('tests'),
					route: Routes.TESTS,
					url: toRoute(Routes.TESTS, {channelId, groupId}),
				},
			],
			key: 'optimize',
			label: Liferay.Language.get('optimize'),
		},
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
							label: Liferay.Language.get('language'),
						},
						{
							label: Liferay.Language.get('switch-workspaces'),
							url: Routes.BASE,
						},
						{
							externalLink: true,
							label: Liferay.Language.get('sign-out'),
							url: Routes.LOGOUT,
						},
					],
					subheaderLabel: emailAddress,
				},
			],
			language: [
				{
					items: LANGUAGES.map(({id, label}) => {
						const active = languageId === id;

						return {
							active,
							label,
							onClick: active
								? undefined
								: () => {
										API.user
											.updateLanguage({
												languageId: id,
											})
											.then(() =>
												window.location.reload()
											);
									},
						};
					}),
				},
			],
		};
	};

	return (
		<div className={getCN('sidebar-root', className, {collapsed})}>
			<div className="sidebar-header">
				<Link
					className="sidebar-header-logo"
					to={toRoute(Routes.SITES, {channelId, groupId})}
				>
					<ClayIcon
						className="icon-root icon-size-md logo"
						symbol={LDPEnabled ? 'ldp_logo' : 'ac_logo'}
					/>
				</Link>

				<ChannelsMenu
					channels={channels}
					defaultChannelId={channelId}
					groupId={groupId}
				/>
			</div>

			<div className="sidebar-body">
				{sidebarSections.map(({items, key, label}) => (
					<Panel
						collapsable
						displayTitle={
							<div className="section-title">
								<span>{label}</span>

								<ClayIcon
									className="icon-root"
									symbol={
										expandedSections.get(key, true)
											? 'angle-down'
											: 'angle-right'
									}
								/>
							</div>
						}
						expanded={expandedSections.get(key, true)}
						key={key}
						onExpandedChange={(expanded) =>
							onSectionExpandedChange(key, expanded)
						}
						showCollapseIcon={false}
					>
						<Panel.Body>
							<ul className="nav-list">
								{items.map(
									({icon, label, route, url}, itemIndex) => (
										<SidebarItem
											active={
												!!matchPath(
													{
														end: false,
														path: route,
													},
													activePathname
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
						</Panel.Body>
					</Panel>
				))}
			</div>

			<div className="sidebar-footer">
				<div className="divider" />

				<ul className="nav-list">
					<UserDropdown
						className="user-dropdown-root"
						containerElement="li"
						initialActiveMenu="base"
						menus={getUserMenus()}
						userName={currentUser.name}
					/>

					<SidebarItem
						active={
							!!matchPath(
								{
									end: false,
									path: Routes.SETTINGS,
								},
								activePathname
							)
						}
						href={toRoute(Routes.SETTINGS_DATA_SOURCE_LIST, {
							groupId,
						})}
						icon="cog"
						label={Liferay.Language.get('settings')}
					/>

					{DEVELOPER_MODE && (
						<SidebarItem
							active={
								!!matchPath(
									{
										end: false,
										path: Routes.UI_KIT,
									},
									activePathname
								)
							}
							href={toRoute(Routes.UI_KIT, {
								channelId,
								groupId,
							})}
							icon="code"
							label="UI Kit"
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
