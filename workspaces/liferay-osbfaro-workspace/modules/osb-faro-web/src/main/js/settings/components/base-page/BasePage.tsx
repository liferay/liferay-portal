import Breadcrumbs from 'shared/components/Breadcrumbs';
import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import DocumentTitle from 'shared/components/DocumentTitle';
import getCN from 'classnames';
import MaintenanceAlert from 'shared/components/MaintenanceAlert';
import NotificationAlertList, {
	useNotificationsAPI,
} from 'shared/components/NotificationAlertList';
import React from 'react';
import TextTruncate from 'shared/components/TextTruncate';
import Toolbar from './Toolbar';
import {DEVELOPER_MODE} from 'shared/util/constants';
import {IBreadcrumbArgs} from 'shared/util/breadcrumbs';
import {matchPath, useLocation, useParams} from 'react-router-dom';
import {PageActions} from 'shared/components/base-page/Header';
import {Routes, toRoute} from 'shared/util/router';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';
import {User} from 'shared/util/records';
import {useStore} from 'react-redux';

function getSidebarSections({
	currentUser,
	groupId,
	recommendationsEnabled,
}: {
	currentUser: User;
	groupId: string;
	recommendationsEnabled: boolean;
}) {
	return [
		{
			items: [
				currentUser.isAdmin() && {
					icon: 'ac_api',
					label: Liferay.Language.get('apis'),
					route: Routes.SETTINGS_APIS_TOKEN_LIST,
					url: toRoute(Routes.SETTINGS_APIS_TOKEN_LIST, {
						groupId,
					}),
				},
				{
					icon: 'definitions',
					label: Liferay.Language.get('definitions'),
					route: Routes.SETTINGS_DEFINITIONS,
					url: toRoute(Routes.SETTINGS_DEFINITIONS, {groupId}),
				},
				{
					icon: 'data_privacy_lock',
					label: Liferay.Language.get('data-control-&-privacy'),
					route: Routes.SETTINGS_DATA_PRIVACY,
					url: toRoute(Routes.SETTINGS_DATA_PRIVACY, {groupId}),
				},
				{
					icon: 'faro_data_source',
					label: Liferay.Language.get('data-sources'),
					route: Routes.SETTINGS_DATA_SOURCE_LIST,
					url: toRoute(Routes.SETTINGS_DATA_SOURCE_LIST, {
						groupId,
					}),
				},
				recommendationsEnabled && {
					icon: 'ac_star',
					label: Liferay.Language.get('recommendations'),
					route: Routes.SETTINGS_RECOMMENDATIONS,
					url: toRoute(Routes.SETTINGS_RECOMMENDATIONS, {
						groupId,
					}),
				},
			].filter(Boolean) as Array<{
				icon: string;
				label: string;
				route: string;
				url: string;
			}>,
			label: Liferay.Language.get('workspace-data'),
		},
		{
			items: [
				{
					icon: 'user_management',
					label: Liferay.Language.get('user-management'),
					route: Routes.SETTINGS_USERS,
					url: toRoute(Routes.SETTINGS_USERS, {
						groupId,
					}),
				},
				{
					icon: 'ac_page',
					label: Liferay.Language.get('properties'),
					route: Routes.SETTINGS_CHANNELS,
					url: toRoute(Routes.SETTINGS_CHANNELS, {
						groupId,
					}),
				},
				{
					icon: 'usage',
					label: Liferay.Language.get('subscription-&-usage'),
					route: Routes.SETTINGS_USAGE,
					url: toRoute(Routes.SETTINGS_USAGE, {groupId}),
				},
				{
					icon: 'cog',
					label: Liferay.Language.get('workspace'),
					route: Routes.SETTINGS_WORKSPACE,
					url: toRoute(Routes.SETTINGS_WORKSPACE, {groupId}),
				},
				DEVELOPER_MODE && {
					icon: 'flag-full',
					label: 'Feature Flags',
					route: Routes.SETTINGS_FEATURE_FLAGS,
					url: toRoute(Routes.SETTINGS_FEATURE_FLAGS, {groupId}),
				},
			].filter(Boolean) as Array<{
				icon: string;
				label: string;
				route: string;
				url: string;
			}>,
			label: Liferay.Language.get('workspace-settings'),
		},
	];
}

interface ISettingsBasePageProps {
	breadcrumbItems?: Array<IBreadcrumbArgs>;
	children?: React.ReactNode;
	className?: string;
	documentTitle?: string;
	pageActions?: Array<any>;
	pageActionsDisplayLimit?: number;
	pageDescription?: React.ReactNode;
	pageTitle?: React.ReactNode;
	subTitle?: React.ReactNode;
}

const SettingsBasePage: React.FC<ISettingsBasePageProps> = ({
	breadcrumbItems,
	children,
	className,
	documentTitle,
	pageActions = [],
	pageActionsDisplayLimit,
	pageDescription,
	pageTitle,
	subTitle,
}) => {
	const {groupId = ''} = useParams<{groupId: string}>();
	const location = useLocation();
	const currentUser = useCurrentUser();
	const notificationResponse = useNotificationsAPI(groupId);

	const store = useStore();
	const recommendationsEnabled = store
		.getState()
		.getIn(['projects', groupId, 'data', 'recommendationsEnabled'], false);

	return (
		<div className="settings-root">
			<Toolbar
				backURL={{
					label: Liferay.Language.get('settings'),
					url: toRoute(Routes.WORKSPACE_WITH_ID, {
						groupId,
					}),
				}}
			/>

			<div className="content-wrapper">
				<nav className="section-side settings-side-bar">
					{getSidebarSections({
						currentUser,
						groupId,
						recommendationsEnabled,
					}).map(({items, label}, sectionIndex) => (
						<div key={sectionIndex}>
							{label && (
								<h5 className="section-title">{label}</h5>
							)}

							<ul className="nav">
								{items.map(({icon, label, route, url}) => (
									<li
										className={getCN('item', {
											active: !!matchPath(
												{end: false, path: route},
												location.pathname
											),
										})}
										key={url}
									>
										<ClayLink
											className="button-root"
											href={url}
										>
											<span className="icon-wrapper">
												<ClayIcon
													className="icon-root"
													symbol={icon}
												/>
											</span>

											{label}
										</ClayLink>
									</li>
								))}
							</ul>
						</div>
					))}
				</nav>

				<div className="content section-main">
					<div
						className={getCN('settings-base-page-root', className)}
					>
						<DocumentTitle
							title={`${
								documentTitle || pageTitle
							} - ${Liferay.Language.get('settings')}`}
						/>

						<NotificationAlertList
							{...notificationResponse}
							groupId={groupId}
						/>

						<MaintenanceAlert />

						{breadcrumbItems && (
							<Breadcrumbs items={breadcrumbItems} />
						)}

						{(!!pageTitle ||
							!!pageDescription ||
							!!pageActions.length) && (
							<div
								className={getCN('content-header', {
									['has-page-actions']: !!pageActions.length,
								})}
							>
								<div className="header-text">
									{pageTitle && (
										<div className="d-flex">
											<h3 className="title-text text-truncate">
												<TextTruncate
													title={pageTitle}
												/>
											</h3>

											{subTitle && (
												<TextTruncate
													className="subtitle-text ml-2"
													title={subTitle}
												/>
											)}
										</div>
									)}

									{pageDescription && (
										<div className="description">
											{pageDescription}
										</div>
									)}
								</div>

								<div className="page-actions-container">
									<PageActions
										actions={pageActions}
										actionsDisplayLimit={
											pageActionsDisplayLimit
										}
									/>
								</div>
							</div>
						)}

						<div>{children}</div>
					</div>
				</div>
			</div>
		</div>
	);
};

export default SettingsBasePage;
