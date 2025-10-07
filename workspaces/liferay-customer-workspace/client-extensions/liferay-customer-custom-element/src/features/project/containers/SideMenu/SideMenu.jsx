/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import classNames from 'classnames';
import {useEffect, useMemo, useRef, useState} from 'react';
import {useAppPropertiesContext} from '~/contexts/AppPropertiesContext';
import i18n from '~/utils/I18n';
import {Button} from '~/components';
import getKebabCase from '~/utils/getKebabCase';
import {useAppContext} from '~/features/project/context';
import {MENU_TYPES, PRODUCT_TYPES} from '~/features/project/utils/constants';
import SideMenuSkeleton from './SideMenuSkeleton';
import MenuItem from './components/MenuItem';
import useCurrentKoroneikiAccount from '~/hooks/useCurrentKoroneikiAccount';
import useMyUserAccountByAccountExternalReferenceCode from '~/features/project/pages/Project/TeamMembers/components/TeamMembersTable/hooks/useMyUserAccountByAccountExternalReferenceCode';

import './SideMenu.css';

const ACTIVATION_PATH = 'activation';

const SideMenu = () => {
	const [{project, subscriptionGroups}] = useAppContext();
	const [isOpenedProductsMenu, setIsOpenedProductsMenu] = useState(false);
	const [menuItemActiveStatus, setMenuItemActiveStatus] = useState([]);
	const {featureFlags} = useAppPropertiesContext();

	const {data: koroneikiData, loading: koroneikiAccountLoading} =
		useCurrentKoroneikiAccount();
	const koroneikiAccount =
		koroneikiData?.koroneikiAccountByExternalReferenceCode;

	const {data: myUserAccountData} =
		useMyUserAccountByAccountExternalReferenceCode(
			koroneikiAccount?.accountKey,
			koroneikiAccountLoading
		);
	const loggedUserAccount = myUserAccountData?.myUserAccount;

	const activationMenuRef = useRef();

	const activationSubscriptionGroups = useMemo(
		() =>
			subscriptionGroups?.filter((subscriptionGroup) => {
				return (
					subscriptionGroup.hasActivation
				);
			}),
		[subscriptionGroups]
	);

	const hasSomeMenuItemActive = useMemo(
		() => menuItemActiveStatus.some((menuItemActive) => !!menuItemActive),
		[menuItemActiveStatus]
	);

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
		const expandedHeightProducts = isOpenedProductsMenu
			? activationSubscriptionGroups?.length * 48
			: 0;

		if (activationMenuRef?.current) {
			activationMenuRef.current.style.maxHeight = `${expandedHeightProducts}px`;
		}
	}, [
		activationSubscriptionGroups?.length,
		hasSomeMenuItemActive,
		isOpenedProductsMenu,
	]);

	const accountSubscriptionGroupsMenuItem = useMemo(
		() =>
			activationSubscriptionGroups?.sort(
				(a, b) => {
					const aDisplayName = a.activationProductName
						? a.activationProductName
						: a.name;

					const bDisplayName = b.activationProductName
						? b.activationProductName
						: b.name;

					return aDisplayName.localeCompare(bDisplayName);
				}
			).map(
				({activationProductName, name}, index) => {
					const displayName = activationProductName
						? activationProductName
						: name;

					const redirectPage = getKebabCase(displayName);

					const iconKey = name === PRODUCT_TYPES.dxpCloud
						? 'lxc'
						: name === PRODUCT_TYPES.liferayExperienceCloud
							? 'experienceCloud'
							: redirectPage.split('-')[0];

					const menuUpdateStatus = (isActive) =>
						setMenuItemActiveStatus(
							(previousMenuItemActiveStatus) => {
								const menuItemStatus = [
									...previousMenuItemActiveStatus,
								];
								menuItemStatus[index] = isActive;

								setIsOpenedProductsMenu(
									menuItemStatus.some(Boolean)
								);

								return menuItemStatus;
							}
						);

					return (
						<MenuItem
							iconKey={iconKey}
							key={`${displayName}-${index}`}
							setActive={menuUpdateStatus}
							to={`${ACTIVATION_PATH}/${redirectPage}`}
						>
							{displayName}
						</MenuItem>
					);
				}
			),
		[activationSubscriptionGroups]
	);

	if (!activationSubscriptionGroups) {
		return <SideMenuSkeleton />;
	}

	return (
		<div className="bg-neutral-1 cp-side-menu ml-4 pl-4 pt-4">
			<ul className="list-unstyled mr-2">
				<div className="d-flex">
					<MenuItem iconKey="overview" to="">
						{i18n.translate(getKebabCase(MENU_TYPES.overview))}
					</MenuItem>
				</div>

				{accountSubscriptionGroupsMenuItem.length > 0 && (
					<li>
						<div className="d-flex">
							<Button
								appendIcon={
									!!activationSubscriptionGroups.length &&
									'angle-right-small'
								}
								appendIconClassName="ml-auto"
								className={classNames(
									'align-items-center btn-borderless d-flex px-2 py-2 rounded w-100',
									{
										'cp-activation-active':
											isOpenedProductsMenu,
										'text-neutral-4':
											activationSubscriptionGroups.length <
											1,
										'text-neutral-10':
											!!activationSubscriptionGroups.length,
									}
								)}
								disabled={
									activationSubscriptionGroups.length < 1
								}
								iconKey="activation"
								onClick={() =>
									setIsOpenedProductsMenu(
										(previousIsOpenedProductsMenu) =>
											!previousIsOpenedProductsMenu
									)
								}
							>
								{i18n.translate(
									getKebabCase(MENU_TYPES.activation)
								)}
							</Button>
						</div>

						<ul
							className={classNames(
								'cp-products-list list-unstyled ml-3 overflow-hidden mb-1',
								{
									'cp-products-list-active':
										isOpenedProductsMenu,
								}
							)}
							ref={activationMenuRef}
						>
							{accountSubscriptionGroupsMenuItem}
						</ul>
					</li>
				)}

				{featureFlags.includes('ISSD-119') && (
					<div className="d-flex">
						<MenuItem
							iconKey="attachments"
							to={getKebabCase(MENU_TYPES.attachments)}
						>
							{i18n.translate(
								getKebabCase(MENU_TYPES.attachments)
							)}
						</MenuItem>
					</div>
				)}

				<div className="d-flex">
					<MenuItem
						iconKey="teamMembers"
						to={getKebabCase(MENU_TYPES.teamMembers)}
					>
						{i18n.translate(getKebabCase(MENU_TYPES.teamMembers))}
					</MenuItem>
				</div>

				{featureFlags.includes('LRSD-5119') && (
					<div className="d-flex">
						<MenuItem
							iconKey="businessEvents"
							to={getKebabCase(MENU_TYPES.businessEvents)}
						>
							{i18n.translate(getKebabCase(MENU_TYPES.businessEvents))}
						</MenuItem>
					</div>
				)}

				{((featureFlags.includes('LRSD-6322') && loggedUserAccount?.isLiferayStaff) ||
					(featureFlags.includes('LRSD-7805') && loggedUserAccount?.isPartner)) &&
						hasSaasSubscription && (
							<div className="d-flex">
								<MenuItem
									iconKey="projectUsage"
									to={getKebabCase(MENU_TYPES.projectUsage)}
								>
									{i18n.translate(
										getKebabCase(MENU_TYPES.projectUsage)
									)}
								</MenuItem>
							</div>
				)}
			</ul>
		</div>
	);
};

SideMenu.Skeleton = SideMenuSkeleton;
export default SideMenu;
