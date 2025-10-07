/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useEffect, useRef, useState} from 'react';
import {Outlet, useLocation, useParams} from 'react-router-dom';
import InformationBanner from '~/components/InformationBanner';
import {useAppPropertiesContext} from '~/contexts/AppPropertiesContext';
import {useAppContext} from '~/features/project/context';
import i18n from '~/utils/I18n';

import ProjectBreadcrumb from '../../components/ProjectBreadcrumb/ProjectBreadcrumb';
import ProjectErrorMessage from '../../components/ProjectErrorMessage';
import SideMenu from '../../containers/SideMenu';

import './Layout.css';

import {FORMAT_DATE_TYPES} from '~/utils/constants';
import getDateCustomFormat from '~/utils/getDateCustomFormat';

import useHasAllEventsPermissions from '../../pages/Project/BusinessEvents/hooks/useHasAllEventsPermissions';

const Layout = () => {
	const {featureFlags} = useAppPropertiesContext();
	const [{businessEvents, subscriptions, userProjectAccess}] =
		useAppContext();

	const [hasSideMenu, setHasSideMenu] = useState(true);
	const [showBanner, setShowBanner] = useState(true);

	const [dismissedBanners, setDismissedBanners] = useState(() => {
		const stored = sessionStorage.getItem(
			'@liferayCP:dismissedOverdueBanners'
		);

		return stored ? JSON.parse(stored) : [];
	});
	const {accountKey} = useParams();
	const firstAccountKeyRef = useRef(accountKey);

	const location = useLocation();
	const routeParams = location.pathname;

	const {hasAllEventsPermissions} = useHasAllEventsPermissions();

	const isRenewOrDeactivatePage =
		routeParams?.endsWith('dxp-renew') ||
		routeParams?.endsWith('portal-renew') ||
		routeParams?.endsWith('deactivate');

	useEffect(() => {
		if (accountKey !== firstAccountKeyRef.current) {
			window.location.reload();
		}
	}, [accountKey]);

	const hasBusinessEnterpriseOrProSubscription = subscriptions?.some(
		(subscription) =>
			subscription.accountSubscriptionGroupERC?.includes('saas') &&
			(subscription.name?.includes('Business Plan') ||
				subscription.name?.includes('Enterprise Plan') ||
				subscription.name?.includes('Pro Plan'))
	);

	const handleOverdueBannerDismiss = (businessId) => {
		setDismissedBanners((prev) => {
			const updated = [...prev, businessId];

			sessionStorage.setItem(
				'@liferayCP:dismissedOverdueBanners',
				JSON.stringify(updated)
			);

			return updated;
		});
	};

	const handleBannerDismiss = () => {
		sessionStorage.setItem('@liferayCP:showSaaSProjectBanner', 'false');

		setShowBanner(false);
	};

	useEffect(() => {
		const bannerState = !sessionStorage.getItem(
			'@liferayCP:showSaaSProjectBanner'
		);

		setShowBanner(bannerState);
	}, []);

	if (userProjectAccess) {
		if (
			userProjectAccess.denyAccess ||
			!userProjectAccess.hasProjectAccess
		) {
			return <ProjectErrorMessage />;
		}
	}

	const overdueBusinessEvents = businessEvents?.filter(
		(businessEvent) => businessEvent.eventStatus.key === 'overdue'
	);

	return (
		<div className="position-relative w-100">
			<div className="mb-4">
				{hasAllEventsPermissions &&
					overdueBusinessEvents?.length > 0 &&
					overdueBusinessEvents
						.filter(
							(businessEvent) =>
								!dismissedBanners.includes(businessEvent.id)
						)
						?.map((businessEvent, businessEventIndex) => (
							<InformationBanner
								content={i18n.sub(
									'the-target-go-Live-date-of-x-has-passed-please-close-this-business-event-or-update-event-details',
									[
										getDateCustomFormat(
											FORMAT_DATE_TYPES.day2DMonthSYearN,
											businessEvent.targetGoLiveDateTime
										),
										`<a href="${Liferay.currentURL}#/${accountKey}/business-events/${businessEvent.id}?openModal=goLiveEvent">`,
										'</a>',
										`<a href="${Liferay.currentURL}#/${accountKey}/business-events/${businessEvent.id}/edit">`,
										'</a>',
									]
								)}
								icon="exclamation-full"
								key={businessEventIndex}
								onDismiss={() =>
									handleOverdueBannerDismiss(businessEvent.id)
								}
								title={`${i18n.translate('business-event')}: ${businessEvent.name}`}
							/>
						))}

				{showBanner &&
					featureFlags.includes('LRSD-8459') &&
					hasBusinessEnterpriseOrProSubscription && (
						<InformationBanner
							content={i18n.sub(
								'visit-the-new-project-usage-page-to-see-your-project-consumption-for-liferay-saas-for-more-information-please-feel-free-to-visit-this-page',
								[
									`<a href="${Liferay.currentURL}#/${accountKey}/project-usage">`,
									'</a>',
									'<a href="https://support.liferay.com/w/liferay-saas-plans">',
									'</a>',
								]
							)}
							icon="exclamation-circle"
							onDismiss={handleBannerDismiss}
						/>
					)}
			</div>

			<div className="d-flex">
				{!isRenewOrDeactivatePage && (
					<div>
						<div className="align-items-center cp-layout-header d-flex justify-content-between ml-4 mt-4">
							<ProjectBreadcrumb />
						</div>

						{hasSideMenu && <SideMenu />}
					</div>
				)}

				<div className="mx-4 px-2 w-100">
					<div className="mx-4 px-2 w-100">
						<Outlet
							context={{
								setHasSideMenu,
							}}
						/>
					</div>
				</div>
			</div>
		</div>
	);
};

export default Layout;
