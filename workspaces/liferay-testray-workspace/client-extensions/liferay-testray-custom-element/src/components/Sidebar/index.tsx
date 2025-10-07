/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import {ClayTooltipProvider} from '@clayui/tooltip';
import classNames from 'classnames';
import {useRef, useState} from 'react';
import {Link, useLocation} from 'react-router-dom';
import {STORAGE_KEYS} from '~/core/Storage';
import {CONSENT_TYPE} from '~/util/enum';

import useStorage from '../../hooks/useStorage';
import i18n from '../../i18n';
import {TestrayIcon, TestrayIconBrand} from '../../images';
import AutofillBuildsPopover from '../AutofillPopover';
import CompareRunsPopover from '../ComparePopover';
import TestrayIcons from '../Icons/TestrayIcon';
import Tooltip from '../Tooltip';
import SidebarFooter from './SidebarFooter';
import SidebarItem from './SidebarItem';
import TaskSidebar from './TasksSidebar';

const Sidebar = () => {
	const [expanded, setExpanded] = useStorage(STORAGE_KEYS.SIDEBAR, {
		consentType: CONSENT_TYPE.PERSONALIZATION,
		initialValue: true,
		storageType: 'persisted',
	});
	const [type, setType] = useState<'autofill' | 'compareRuns'>('compareRuns');
	const [visible, setVisible] = useState(false);
	const {pathname} = useLocation();

	const relevantPaths = [
		['case-result', 'project'],
		['issues'],
		['testflow'],
	];

	const CompareRunsContent = (
		<div
			className={classNames(
				'tr-sidebar__content__list__item tr-sidebar__content__list__item'
			)}
		>
			<TestrayIcons
				className="tr-sidebar__content__list__item__icon"
				fill="#8b8db2"
				size={35}
				symbol="drop"
			/>

			<span
				className={classNames('tr-sidebar__content__list__item__text', {
					'tr-sidebar__content__list__item__text--expanded': expanded,
				})}
			>
				{i18n.sub('compare-x', 'runs')}
			</span>
		</div>
	);

	const AutofillContent = (
		<div
			className={classNames(
				'tr-sidebar__content__list__item tr-sidebar__content__list__item'
			)}
		>
			<ClayIcon
				className="tr-sidebar__content__list__item__clayicon"
				fill="#8b8db2"
				symbol="change-list"
			/>

			<span
				className={classNames('tr-sidebar__content__list__item__text', {
					'tr-sidebar__content__list__item__text--expanded': expanded,
				})}
			>
				{i18n.sub('auto-fill-x', 'builds')}
			</span>
		</div>
	);

	const CompareRunsRef = useRef<HTMLDivElement>(null);
	const AutofillRef = useRef<HTMLDivElement>(null);

	const sidebarItems = [
		{
			icon: 'polls',
			label: i18n.translate('results'),
			path: '/',
		},
		{
			icon: 'box-container',
			label: i18n.translate('Issues'),
			path: '/issues',
		},
		{
			icon: 'merge',
			label: i18n.translate('testflow'),
			path: '/testflow?filter=%7B"dueStatus"%3A%5B"INANALYSIS"%5D%7D&filterSchema=testflow',
		},
		{
			element: (
				<div
					onClick={() => {
						setType('autofill');
						setVisible((show) => !show);
					}}
					ref={AutofillRef}
				>
					<Tooltip
						position="right"
						title={
							expanded
								? undefined
								: i18n.sub('auto-fill-x', 'builds')
						}
					>
						{AutofillContent}
					</Tooltip>
				</div>
			),
		},
		{
			element: (
				<div
					onClick={() => {
						setType('compareRuns');

						setVisible((show) => !show);
					}}
					ref={CompareRunsRef}
				>
					<Tooltip
						position="right"
						title={
							expanded
								? undefined
								: i18n.translate('compare-runs')
						}
					>
						{CompareRunsContent}
					</Tooltip>
				</div>
			),
		},
	];

	return (
		<ClayTooltipProvider>
			<div
				className={classNames('tr-sidebar', {
					'tr-sidebar--expanded': expanded,
				})}
			>
				<div className="tr-sidebar__content">
					<div className="mb-4">
						<Link className="tr-sidebar__content__title" to="/">
							<TestrayIcon />

							<TestrayIconBrand
								className={classNames(
									'tr-sidebar__content__title__brand',
									{
										'tr-sidebar__content__title__brand--expanded':
											expanded,
									}
								)}
							/>
						</Link>

						<div className="tr-sidebar__content__list">
							{sidebarItems.map(
								({element, icon, label, path}, index) => {
									if (path && index <= 2) {
										return (
											<SidebarItem
												active={relevantPaths[
													index
												].some((relevantPath) =>
													pathname.includes(
														relevantPath
													)
												)}
												expanded={expanded}
												icon={icon}
												key={index}
												label={label}
												path={path}
											/>
										);
									}

									return (
										<div
											className={classNames(
												'tr-sidebar__content_list__item"',
												{
													'tr-sidebar__content__list__item--active':
														index === 4 &&
														pathname.includes(
															'compare-runs'
														),
												}
											)}
											key={index}
										>
											{element}
										</div>
									);
								}
							)}
						</div>

						{type === 'compareRuns' ? (
							<CompareRunsPopover
								expanded={expanded}
								setVisible={setVisible}
								triggedRef={CompareRunsRef}
								visible={visible}
							/>
						) : (
							<AutofillBuildsPopover
								expanded={expanded}
								setType={setType}
								setVisible={setVisible}
								triggedRef={AutofillRef}
								visible={visible}
							/>
						)}
						<div className="tr-sidebar__content__divider" />
					</div>

					<TaskSidebar expanded={expanded} />

					<div className="pb-1">
						<SidebarFooter
							expanded={expanded}
							onClick={() => setExpanded(!expanded)}
						/>
					</div>
				</div>
			</div>
		</ClayTooltipProvider>
	);
};

export default Sidebar;
