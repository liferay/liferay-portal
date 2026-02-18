/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLayout from '@clayui/layout';
import ClaySticker from '@clayui/sticker';
import classNames from 'classnames';
import React from 'react';

import '../../../css/Home.scss';
import {CategoryItemGrouped} from '../types';
import {useWindowSize} from '../utils/useWindowSize';
import CategoryCard from './CategoryCard';
import CategoryCardHorizontal from './CategoryCardHorizontal';

type Props = {
	displayType?: 'horizontal' | 'vertical';
	icon: string;
	items: CategoryItemGrouped[];
	title: string;
};

const LARGE_BREAKPOINT = 992;

const HomePageLayout = ({
	displayType = 'vertical',
	icon,
	items,
	title,
}: Props) => {
	const {width} = useWindowSize();

	const isLargeBreakpoint = width >= LARGE_BREAKPOINT;

	const imageStyle = React.useMemo(
		() => ({
			height: isLargeBreakpoint ? '40px' : '22px',
			width: isLargeBreakpoint ? '40px' : '22px',
		}),
		[isLargeBreakpoint]
	);
	const isHorizontal = displayType === 'horizontal';

	return (
		<ClayLayout.ContainerFluid
			className={`px-2 px-md-3 px-sm-2 px-xl-4 home-${displayType}`}
			size="lg"
		>
			<ClayLayout.Row className="my-5">
				<ClayLayout.Col>
					<div
						className={classNames(
							'font-family-source-sans-pro font-weight-bold text-truncate',
							{
								'd-flex align-items-center text-left':
									isHorizontal,
								'text-center': !isHorizontal,
							}
						)}
					>
						<div
							className={classNames({
								'c-mb-2': !isHorizontal,
							})}
						>
							<ClaySticker
								className="border-0"
								displayType="outline"
								size={isLargeBreakpoint ? 'xxl' : 'xl'}
							>
								<ClaySticker.Image
									alt={`${title} logo`}
									src={icon}
									style={imageStyle}
								/>
							</ClaySticker>
						</div>

						<span
							className={classNames(
								'font-family-source-sans-pro font-weight-bold text-truncate text-dark',
								{
									'text-7': !isLargeBreakpoint,
									'text-11': isLargeBreakpoint,
								}
							)}
						>
							{title}
						</span>
					</div>
				</ClayLayout.Col>
			</ClayLayout.Row>

			<ClayLayout.Row className="mb-7">
				{items.map((group) =>
					isHorizontal ? (
						<ClayLayout.ContainerFluid
							className="mb-4"
							key={group.label}
						>
							<ClayLayout.ContentRow className="mb-2 pb-2">
								<ClayLayout.ContentCol expand>
									<p className="font-weight-semi-bold mb-0 text-3 text-secondary text-uppercase">
										{group.label}
									</p>
								</ClayLayout.ContentCol>
							</ClayLayout.ContentRow>

							<ClayLayout.Row>
								{group.items.map((app) => (
									<ClayLayout.Col key={app.id} md={4} sm={6}>
										<CategoryCardHorizontal item={app} />
									</ClayLayout.Col>
								))}
							</ClayLayout.Row>
						</ClayLayout.ContainerFluid>
					) : (
						group.items.map((item) => (
							<ClayLayout.Col key={item.id} lg={3} md={4} sm={6}>
								<CategoryCard item={item} />
							</ClayLayout.Col>
						))
					)
				)}
			</ClayLayout.Row>
		</ClayLayout.ContainerFluid>
	);
};

export default HomePageLayout;
