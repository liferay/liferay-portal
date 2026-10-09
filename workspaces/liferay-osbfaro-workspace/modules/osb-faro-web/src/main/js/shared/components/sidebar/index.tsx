import ChannelsMenu, {Channel} from '../channels-menu';
import ClayIcon from 'shared/components/Icon';
import getCN from 'classnames';
import React, {useLayoutEffect} from 'react';
import {ACCOUNTS, Routes, SEGMENTS, toRoute} from 'shared/util/router';
import {Map} from 'immutable';
import {matchPath} from 'react-router-dom';
import {useIsMobile} from 'shared/hooks/useIsMobile';
import {useLDPEnabled} from 'shared/hooks/useLDPEnabled';
import {SidePanel, VerticalNav} from '@clayui/core';

interface ISidebarNavItem {
	icon: string;
	id: string;
	label: string;
	route: string;
	url: string;
}

interface ISidebarNavSection {
	id: string;
	items: ISidebarNavItem[];
	label: string;
}

type SidebarNavEntry = ISidebarNavItem | ISidebarNavSection;

interface ISidebarProps {
	activePathname: string;
	channelId?: string;
	channels: Channel[];
	className?: string;
	collapsed: boolean;
	collapsedSections: Map<string, boolean>;
	containerRef: React.RefObject<HTMLElement>;
	groupId: string;
	onCollapsedChange: (collapsed: boolean) => void;
	onSectionToggle: (sectionKey: string, collapsed: boolean) => void;
}

/**
 * `VerticalNav` renders a nested level only through the `items` property, and
 * reuses the root render function for every depth, so one function has to
 * handle both a section and one of its items.
 */
const isSection = (entry: SidebarNavEntry): entry is ISidebarNavSection =>
	'items' in entry;

const renderNavItem = (entry: SidebarNavEntry) =>
	isSection(entry) ? (
		<VerticalNav.Item
			className="mb-4"
			items={entry.items}
			textValue={entry.label}
		>
			<span className="font-weight-semi-bold section-title text-2 text-uppercase">
				{entry.label}
			</span>
		</VerticalNav.Item>
	) : (
		<VerticalNav.Item href={entry.url} textValue={entry.label}>
			<span className="mr-2 sticker">
				<ClayIcon className="icon-root" symbol={entry.icon} />
			</span>

			<span className="item-label">{entry.label}</span>
		</VerticalNav.Item>
	);

const Sidebar: React.FC<ISidebarProps> = ({
	activePathname,
	channelId,
	channels = [],
	className,
	collapsed = false,
	collapsedSections = Map(),
	containerRef,
	groupId,
	onCollapsedChange,
	onSectionToggle,
}) => {
	const isMobile = useIsMobile();

	const LDPEnabled = useLDPEnabled({groupId});

	/**
	 * On mobile the panel overlays the page and traps focus while it is open,
	 * so it starts closed and closes again after every navigation.
	 */
	useLayoutEffect(() => {
		if (isMobile) {
			onCollapsedChange(true);
		}
	}, [activePathname, isMobile]);

	const sidebarSections: ISidebarNavSection[] = [
		{
			id: 'touchpoints',
			items: [
				LDPEnabled && {
					icon: 'plant',
					id: 'lifecycles',
					label: Liferay.Language.get('lifecycles'),
					route: Routes.LIFECYCLE,
					url: toRoute(Routes.LIFECYCLE, {channelId, groupId}),
				},
				LDPEnabled && {
					icon: 'megaphone',
					id: 'campaigns',
					label: Liferay.Language.get('campaigns'),
					route: Routes.CAMPAIGNS,
					url: toRoute(Routes.CAMPAIGNS, {channelId, groupId}),
				},
				{
					icon: 'sites',
					id: 'sites',
					label: Liferay.Language.get('sites'),
					route: Routes.SITES,
					url: toRoute(Routes.SITES, {channelId, groupId}),
				},
				{
					icon: 'sheets',
					id: 'assets',
					label: Liferay.Language.get('assets'),
					route: Routes.ASSETS,
					url: toRoute(Routes.ASSETS, {
						channelId,
						groupId,
					}),
				},
				{
					icon: 'click',
					id: 'events',
					label: Liferay.Language.get('events'),
					route: Routes.EVENT_ANALYSIS,
					url: toRoute(Routes.EVENT_ANALYSIS, {
						channelId,
						groupId,
					}),
				},
			].filter(Boolean) as ISidebarNavItem[],
			label: Liferay.Language.get('touchpoints'),
		},
		{
			id: 'people',
			items: [
				{
					icon: 'box-squared',
					id: 'segments',
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
					id: 'accounts',
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
					id: 'individuals',
					label: Liferay.Language.get('individuals'),
					route: Routes.CONTACTS_INDIVIDUALS,
					url: toRoute(Routes.CONTACTS_INDIVIDUALS, {
						channelId,
						groupId,
					}),
				},
			].filter(Boolean) as ISidebarNavItem[],
			label: Liferay.Language.get('people'),
		},
		{
			id: 'optimize',
			items: [
				{
					icon: 'test',
					id: 'tests',
					label: Liferay.Language.get('tests'),
					route: Routes.TESTS,
					url: toRoute(Routes.TESTS, {channelId, groupId}),
				},
			],
			label: Liferay.Language.get('optimize'),
		},
	];

	const isActive = ({route}: ISidebarNavItem) =>
		Boolean(matchPath({end: false, path: route}, activePathname));

	const activeItem = sidebarSections
		.flatMap(({items}) => items)
		.find(isActive);

	const expandedKeys = new Set<React.Key>(
		sidebarSections
			.filter(({id}) => !collapsedSections.get(id, false))
			.map(({id}) => id)
	);

	/**
	 * `VerticalNav` reports the whole expanded set, while the stored preference
	 * is one entry per section. Exactly one section changes per event, so it is
	 * the one whose state differs from the set that was rendered.
	 */
	const handleExpandedChange = (nextExpandedKeys: Set<React.Key>) => {
		const toggledSection = sidebarSections.find(
			({id}) => expandedKeys.has(id) !== nextExpandedKeys.has(id)
		);

		if (toggledSection) {
			onSectionToggle(
				toggledSection.id,
				!nextExpandedKeys.has(toggledSection.id)
			);
		}
	};

	const channelsMenu = (
		<ChannelsMenu
			channels={channels}
			defaultChannelId={channelId}
			groupId={groupId}
		/>
	);

	return (
		<SidePanel
			aria-label={Liferay.Language.get('menu')}
			className={getCN('shadow-none sidebar-root', className)}
			closeOnEscape={false}
			containerRef={containerRef}
			direction="left"
			onOpenChange={(open) => onCollapsedChange(!open)}
			open={!collapsed}
			panelWidth={280}
			position="fixed"
		>
			{isMobile ? (
				<SidePanel.Header
					className="my-4 px-3 py-0"
					messages={{closeAriaLabel: Liferay.Language.get('close')}}
				>
					{channelsMenu}
				</SidePanel.Header>
			) : (
				<div className="my-4 px-3 py-0 sidebar-header">
					{channelsMenu}
				</div>
			)}

			<SidePanel.Body className="p-0">
				<VerticalNav<SidebarNavEntry>
					active={activeItem?.id}
					aria-label={Liferay.Language.get('menu')}
					displayType="primary"
					expandedKeys={expandedKeys}
					items={sidebarSections}
					onExpandedChange={handleExpandedChange}
					stacked
				>
					{renderNavItem}
				</VerticalNav>
			</SidePanel.Body>
		</SidePanel>
	);
};

export default Sidebar;
