import Breadcrumbs from 'shared/components/Breadcrumbs';
import classNames from 'classnames';
import ClayBadge from '@clayui/badge';
import ClayButton from '@clayui/button';
import ClayDropDown, {Align} from '@clayui/drop-down';
import ClayIcon from '@clayui/icon';
import ClayLabel from '@clayui/label';
import ClayLink from '@clayui/link';
import ClayNavigationBar from '@clayui/navigation-bar';
import ClayToolbar from '@clayui/toolbar';
import getCN from 'classnames';
import Label from 'shared/components/Label';
import NotificationAlertList, {
	useNotificationsAPI,
} from '../NotificationAlertList';
import React, {useState} from 'react';
import Row from './Row';
import TextTruncate from 'shared/components/TextTruncate';
import {getMatchedRoute, setUriQueryValues, toRoute} from 'shared/util/router';
import {IBreadcrumbArgs} from 'shared/util/breadcrumbs';
import {pickBy} from 'lodash';

type NavBarItem = {
	deprecated?: boolean;
	exact: boolean;
	label: string;
	route: string;
};

interface INavBarProps extends React.HTMLAttributes<HTMLDivElement> {
	items: NavBarItem[];
	routeParams?: object;
	routeQueries?: object;
}

const NavBar: React.FC<INavBarProps> = ({
	items,
	routeParams = {},
	routeQueries = {},
}) => {
	const matchedRoute = getMatchedRoute(items);

	const initialItem =
		items.find((item) => item.route === matchedRoute) ?? items[0];

	const [activeLabel, setActiveLabel] = useState(initialItem.label);

	return (
		<div className="row">
			<ClayNavigationBar triggerLabel={activeLabel}>
				{items.map(({deprecated, label, route}) => (
					<ClayNavigationBar.Item
						active={matchedRoute === route}
						key={label}
					>
						<ClayLink
							href={setUriQueryValues(
								pickBy(routeQueries),
								toRoute(route, routeParams)
							)}
							onClick={() => setActiveLabel(label)}
						>
							{label}

							{deprecated && (
								<ClayBadge
									className="ml-1"
									displayType="warning"
									label={Liferay.Language.get(
										'deprecated'
									).toUpperCase()}
									translucent
								/>
							)}
						</ClayLink>
					</ClayNavigationBar.Item>
				))}
			</ClayNavigationBar>
		</div>
	);
};

interface Action extends React.HTMLAttributes<HTMLElement> {
	deprecated?: boolean;
	disabled?: boolean;
	label: string;
	href?: string;
	icon?: {
		symbol: string;
	};
	external?: boolean;

	/**
	 * Wraps the rendered dropdown item, so an action can drive its own
	 * trigger-and-modal component (e.g. a download action that opens a
	 * confirmation modal) instead of a plain click handler.
	 */
	renderItem?: (item: React.ReactElement) => React.ReactElement;
}

interface IPageActionsProps {
	actions?: Action[];
	actionsDisplayLimit?: number;
	disabled?: boolean;
	label?: string;
}

const renderActionItems = (actions: Action[]) =>
	actions.map(({deprecated, icon, label, renderItem, ...props}) => {
		const item = (
			<ClayDropDown.Item {...props}>
				{icon && (
					<ClayIcon className="icon-root mr-2" symbol={icon.symbol} />
				)}

				{label}

				{deprecated && (
					<ClayBadge
						className="ml-1"
						displayType="warning"
						label={Liferay.Language.get('deprecated').toUpperCase()}
						translucent
					/>
				)}
			</ClayDropDown.Item>
		);

		return (
			<React.Fragment key={label}>
				{renderItem ? renderItem(item) : item}
			</React.Fragment>
		);
	});

const PageActions: React.FC<IPageActionsProps> = ({
	actions = [],
	actionsDisplayLimit = 1,
	disabled = false,
	label = '',
}) => (
	<>
		{actions.length <= actionsDisplayLimit &&
			actions.map(({icon, label, ...props}) => {
				const Button = props.href ? ClayLink : ClayButton;

				return (
					<Button
						button
						className={classNames(
							getCN('button-root', {
								disabled: props.disabled,
							})
						)}
						displayType="secondary"
						key={label}
						{...props}
					>
						{icon && (
							<ClayIcon className="mr-2" symbol={icon.symbol} />
						)}

						{label}
					</Button>
				);
			})}

		{actions.length > actionsDisplayLimit && (
			<ClayDropDown
				alignmentPosition={Align.BottomRight}
				closeOnClick
				trigger={
					<ClayButton
						aria-label={
							label ? undefined : Liferay.Language.get('menu')
						}
						borderless={!label}
						disabled={disabled}
						displayType={label.length ? 'primary' : 'secondary'}
						size={label ? undefined : 'sm'}
					>
						{label ? (
							<>
								<span>{label}</span>

								<ClayIcon
									className="icon-root ml-2"
									symbol="caret-bottom"
								/>
							</>
						) : (
							<ClayIcon
								className="icon-root"
								symbol="ellipsis-v"
							/>
						)}
					</ClayButton>
				}
			>
				{renderActionItems(actions)}
			</ClayDropDown>
		)}
	</>
);

interface IPageActionsToolbarProps {
	actions?: Action[];
	disabled?: boolean;
}

/**
 * Renders the page actions behind a toolbar action button, so the actions
 * sit in the same toolbar chrome the rest of the product uses while still
 * collapsing into a single trigger next to the page title.
 */
const PageActionsToolbar: React.FC<IPageActionsToolbarProps> = ({
	actions = [],
	disabled = false,
}) => (
	<ClayToolbar className="page-actions-toolbar">
		<ClayToolbar.Nav>
			<ClayToolbar.Item>
				<ClayDropDown
					alignmentPosition={Align.BottomRight}
					closeOnClick
					trigger={
						<ClayButton
							aria-label={Liferay.Language.get('menu')}
							className="component-action"
							disabled={disabled}
							displayType="unstyled"
						>
							<ClayIcon
								className="icon-root"
								symbol="ellipsis-v"
							/>
						</ClayButton>
					}
				>
					{renderActionItems(actions)}
				</ClayDropDown>
			</ClayToolbar.Item>
		</ClayToolbar.Nav>
	</ClayToolbar>
);

const Section: React.FC<React.HTMLAttributes<HTMLDivElement>> = ({
	children,
	className,
}) => <div className={getCN('header-section', className)}>{children}</div>;

interface ITitleSectionProps extends React.HTMLAttributes<HTMLDivElement> {
	label?: boolean;
	subtitle?: React.ReactNode | string;
	title?: string;
	topLabel?: string;
}

export interface IActionProps extends React.HTMLAttributes<HTMLDivElement> {
	displayType: string;
	label: string;
	redirectURL?: string;
	onClick?: () => void;
}

interface IActionsProps extends React.HTMLAttributes<HTMLDivElement> {
	actions: IActionProps[];
}

const TitleSection: React.FC<ITitleSectionProps> = ({
	children,
	className,
	label = false,
	subtitle,
	title,
	topLabel,
}) => (
	<Section className={getCN('title-section', className, {subtitle})}>
		{topLabel && (
			<div className="mb-1">
				<Label display="secondary" size="lg" uppercase>
					{topLabel}
				</Label>
			</div>
		)}

		<span className="align-items-center d-flex">
			<h1 className="title text-truncate">
				<TextTruncate title={title} />
			</h1>

			{children}
		</span>

		{subtitle &&
			(label ? (
				<ClayLabel className="mb-4" displayType="info">
					{subtitle}
				</ClayLabel>
			) : (
				<div className="subtitle">{subtitle}</div>
			))}
	</Section>
);

const Actions: React.FC<IActionsProps> = ({actions = []}) => (
	<div className="header-actions">
		{actions.map(({displayType, label, onClick, redirectURL}, index) =>
			redirectURL ? (
				<ClayLink
					className={getCN(`btn btn-${displayType}`, 'ml-2')}
					href={redirectURL}
					key={index}
					target="_blank"
				>
					<ClayIcon className="mr-2" symbol="shortcut" />

					{label}
				</ClayLink>
			) : (
				<ClayButton
					className="ml-2"
					displayType={displayType as any}
					key={index}
					onClick={onClick}
				>
					{label}
				</ClayButton>
			)
		)}
	</div>
);

interface IHeaderProps extends React.HTMLAttributes<HTMLDivElement> {
	breadcrumbs: IBreadcrumbArgs[];
	fluid?: boolean;
	groupId: string;
}

const Header: React.FC<IHeaderProps> & {
	NavBar: typeof NavBar;
	PageActions: typeof PageActions;
	PageActionsToolbar: typeof PageActionsToolbar;
	Actions: typeof Actions;
	Section: typeof Section;
	TitleSection: typeof TitleSection;
} = ({breadcrumbs, children, fluid, groupId}) => {
	const notificationResponse = useNotificationsAPI(groupId);

	if (fluid) {
		return (
			<header className="header-root">
				<div className="mx-5">
					{breadcrumbs && (
						<Row>
							<Breadcrumbs items={breadcrumbs} />
						</Row>
					)}

					{children}
				</div>

				<NotificationAlertList
					{...notificationResponse}
					groupId={groupId}
					stripe
				/>
			</header>
		);
	}

	return (
		<header className="header-root">
			<div className="header-container">
				{breadcrumbs && (
					<Row>
						<Breadcrumbs items={breadcrumbs} />
					</Row>
				)}

				{children}
			</div>

			<NotificationAlertList
				{...notificationResponse}
				groupId={groupId}
				stripe
			/>
		</header>
	);
};

Header.Actions = Actions;
Header.NavBar = NavBar;
Header.PageActions = PageActions;
Header.PageActionsToolbar = PageActionsToolbar;
Header.Section = Section;
Header.TitleSection = TitleSection;

export default Header;

export {NavBar, PageActions, PageActionsToolbar, Section, TitleSection};
