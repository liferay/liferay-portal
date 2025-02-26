/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLoadingIndicator from '@clayui/loading-indicator';
import {useEffect, useMemo, useState} from 'react';
import {HashRouter, Route, Routes} from 'react-router-dom';
import {useAppPropertiesContext} from '~/contexts/AppPropertiesContext';
import getKebabCase from '~/utils/getKebabCase';
import DeactivateKeysTable from '~/features/project/containers/DeactivateKeysTable';
import GenerateNewKey from '~/features/project/containers/GenerateNewKey';
import {useCustomerPortal} from '~/features/project/context';
import {actionTypes} from '~/features/project/context/reducer';
import Layout from '~/features/project/layouts/BaseLayout';
import {PRODUCT_TYPES} from '~/features/project/utils/constants';
import {getWebContents} from '~/features/project/utils/getWebContents';
import Commerce from '../ActivationKeys/Commerce';
import EnterpriseSearch from '../ActivationKeys/EnterpriseSearch';
import AnalyticsCloud from '../AnalyticsCloud';
import Attachments from '../Attachments';
import DXP from '../DXP';
import DXPCloud from '../DXPCloud';
import LiferayExperienceCloud from '../LiferayExperienceCloud';
import Overview from '../Overview';
import Portal from '../Portal';
import RenewTable from '../RenewTable';
import TeamMembers from '../TeamMembers';
import ActivationOutlet from './Outlets/ActivationOutlet';
import ProductOutlet from './Outlets/ProductOutlet';
import ProjectUsage from '../ProjectUsage';
import useCurrentKoroneikiAccount from '~/hooks/useCurrentKoroneikiAccount';
import useMyUserAccountByAccountExternalReferenceCode from '~/features/project/pages/Project/TeamMembers/components/TeamMembersTable/hooks/useMyUserAccountByAccountExternalReferenceCode';
import BusinessEvents from '../BusinessEvent';
import BusinessEventsItem from '../BusinessEvent/pages/BusinessEventsItem/BusinessEventsItem';

const ProjectRoutes = () => {
	const [hasComplimentaryKey, setHasComplimentaryKey] = useState(false);

	const [{project, subscriptionGroups}, dispatch] = useCustomerPortal();
	const {featureFlags} = useAppPropertiesContext();

	const {data: koroneikiData, loading: koroneikiAccountLoading} =
		useCurrentKoroneikiAccount();
	const koroneikiAccount =
		koroneikiData?.koroneikiAccountByExternalReferenceCode;

	const {data: myUserAccountData} =
		useMyUserAccountByAccountExternalReferenceCode(
			koroneikiAccountLoading,
			koroneikiAccount?.accountKey
		);
	const loggedUserAccount = myUserAccountData?.myUserAccount;

	const hasSaasSubscription = useMemo(
		() =>
			subscriptionGroups?.some(
				(subscription) =>
					subscription.externalReferenceCode ===
					`${project?.externalReferenceCode}_liferay-saas`
			),
		[subscriptionGroups]
	);

	useEffect(() => {
		if (project && subscriptionGroups) {
			dispatch({
				payload: getWebContents(
					project.dxpVersion,
					project.slaCurrent,
					subscriptionGroups
				),
				type: actionTypes.UPDATE_QUICK_LINKS,
			});
		}
	}, [dispatch, project, subscriptionGroups]);

	return (
		<HashRouter>
			<Routes>
				<Route element={<ClayLoadingIndicator />} index />

				<Route element={<Layout />} path="/:accountKey">
					<Route element={<Overview />} index />

					{featureFlags.includes('LPS-153478') && (
						<Route
							element={
								<ProductOutlet
									product={
										PRODUCT_TYPES.liferayExperienceCloud
									}
								/>
							}
						>
							<Route
								element={<LiferayExperienceCloud />}
								path={getKebabCase(
									PRODUCT_TYPES.liferayExperienceCloud
								)}
							/>
						</Route>
					)}

					<Route
						element={
							<ProductOutlet product={PRODUCT_TYPES.dxpCloud} />
						}
					>
						<Route
							element={<DXPCloud />}
							path={getKebabCase(PRODUCT_TYPES.dxpCloud)}
						/>
					</Route>

					<Route element={<ActivationOutlet />} path="activation">
						<Route
							element={
								<ProductOutlet product={PRODUCT_TYPES.portal} />
							}
							path={getKebabCase(PRODUCT_TYPES.portal)}
						>
							<Route
								element={
									<Portal
										hasComplimentaryKey={
											hasComplimentaryKey
										}
									/>
								}
								index
							/>

							<Route
								element={
									<GenerateNewKey
										hasComplimentaryKey={
											hasComplimentaryKey
										}
										productGroupName={PRODUCT_TYPES.portal}
										setHasComplimentaryKey={
											setHasComplimentaryKey
										}
									/>
								}
								path="new"
							/>

							{featureFlags.includes('LPS-186175') && (
								<Route
									element={
										<DeactivateKeysTable
											initialFilter="startswith(productName,'Portal')"
											productName={PRODUCT_TYPES.portal}
										/>
									}
									path="deactivate"
								/>
							)}

							<Route
								element={
									<RenewTable
										hasComplimentaryKey={
											hasComplimentaryKey
										}
										isRenewTable
									/>
								}
								path="portal-renew"
							/>
						</Route>

						<Route
							element={
								<ProductOutlet product={PRODUCT_TYPES.dxp} />
							}
							path={getKebabCase(PRODUCT_TYPES.dxp)}
						>
							<Route
								element={
									<DXP
										hasComplimentaryKey={
											hasComplimentaryKey
										}
									/>
								}
								index
							/>

							<Route
								element={
									<GenerateNewKey
										hasComplimentaryKey={
											hasComplimentaryKey
										}
										productGroupName={PRODUCT_TYPES.dxp}
										setHasComplimentaryKey={
											setHasComplimentaryKey
										}
									/>
								}
								path="new"
							/>

							<Route
								element={
									<DeactivateKeysTable
										initialFilter="(startswith(productName,'DXP') or startswith(productName,'Digital'))"
										productName={PRODUCT_TYPES.dxp}
									/>
								}
								path="deactivate"
							/>

							<Route
								element={
									<RenewTable
										hasComplimentaryKey={
											hasComplimentaryKey
										}
										isDXPTable
										isRenewTable
									/>
								}
								path="dxp-renew"
							/>
						</Route>

						<Route
							element={
								<ProductOutlet
									product={PRODUCT_TYPES.analyticsCloud}
								/>
							}
							path={getKebabCase(PRODUCT_TYPES.analyticsCloud)}
						>
							<Route element={<AnalyticsCloud />} index />
						</Route>

						<Route
							element={
								<ProductOutlet
									product={PRODUCT_TYPES.commerce}
								/>
							}
							path={getKebabCase(PRODUCT_TYPES.commerce)}
						>
							<Route element={<Commerce />} index />
						</Route>

						<Route
							element={
								<ProductOutlet
									product={PRODUCT_TYPES.enterpriseSearch}
								/>
							}
							path={getKebabCase(PRODUCT_TYPES.enterpriseSearch)}
						>
							<Route element={<EnterpriseSearch />} index />
						</Route>
					</Route>

					{featureFlags.includes('ISSD-119') && (
						<Route element={<Attachments />} path="attachments" />
					)}

					<Route element={<TeamMembers />} path="team-members" />
					
					{featureFlags.includes('LRSD-5119') && (
						<Route path="business-events">
							<Route element={<BusinessEvents />} index />
							<Route element={<BusinessEventsItem />} path=":id"/>
						</Route>
					)}

					{((featureFlags.includes('LRSD-6322') && loggedUserAccount?.isLiferayStaff) ||
						(featureFlags.includes('LRSD-7805') && loggedUserAccount?.isPartner)) &&
							hasSaasSubscription && (
								<Route
									element={<ProjectUsage />}
									path="project-usage"
								/>
					)}

					<Route element={<h3>Page not found</h3>} path="*" />
				</Route>
			</Routes>
		</HashRouter>
	);
};

export default ProjectRoutes;
