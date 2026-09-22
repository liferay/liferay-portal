/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayCardWithNavigation} from '@clayui/card';
import ClayIcon from '@clayui/icon';
import ClayLayout from '@clayui/layout';
import ClaySticker from '@clayui/sticker';
import classNames from 'classnames';
import React from 'react';

import {CategoryItemGrouped} from '../types';

type Props = {
	displayType?: 'horizontal' | 'vertical';
	icon: string;
	items: CategoryItemGrouped[];
	title: string;
};

const HomePageLayout = ({
	displayType = 'vertical',
	icon,
	items,
	title,
}: Props) => {
	const isHorizontal = displayType === 'horizontal';

	return (
		<ClayLayout.ContainerFluid
			className={`px-2 px-md-3 px-sm-2 px-xl-4 home-${displayType}`}
			size="lg"
		>
			<ClayLayout.Row
				className={classNames({
					'c-mb-3 c-mb-lg-4 c-mt-4 c-mt-lg-5': !isHorizontal,
					'c-mt-4 c-mt-lg-5 c-mb-4 c-mb-lg-5': isHorizontal,
				})}
			>
				<ClayLayout.Col>
					<div
						className={classNames('font-weight-bold', {
							'd-flex align-items-center text-left': isHorizontal,
							'text-center': !isHorizontal,
						})}
					>
						<div
							className={classNames({
								'c-mb-3 c-mb-lg-2': !isHorizontal,
							})}
						>
							<ClaySticker
								borderless
								displayType="outline"
								size="xl"
							>
								<ClaySticker.Image alt="" src={icon} />
							</ClaySticker>
						</div>

						<h2
							className={classNames('font-weight-bold text-11', {
								'c-ml-3 c-ml-lg-4': isHorizontal,
							})}
						>
							{title}
						</h2>
					</div>
				</ClayLayout.Col>
			</ClayLayout.Row>

			<ClayLayout.Row
				className={classNames({
					'c-pt-1': isHorizontal,
					'c-pt-lg-4': !isHorizontal,
				})}
			>
				{items.map((group) => {
					if (!isHorizontal) {
						return group.items.map((item) => (
							<ClayLayout.Col key={item.id} lg={3} md={4} sm={6}>
								<ClayCardWithNavigation
									cardType="navigation"
									href={item.href}
									title={item.label}
								>
									<ClayIcon symbol={item.leadingIcon} />
								</ClayCardWithNavigation>
							</ClayLayout.Col>
						));
					}

					const hasLabel = Boolean(group.label);

					return (
						<ClayLayout.ContainerFluid
							aria-label={hasLabel ? group.label : undefined}
							className="c-mb-3 c-mb-lg-4"
							key={group.id}
							role={hasLabel ? 'group' : undefined}
						>
							{hasLabel && (
								<ClayLayout.ContentRow className="c-mb-1 c-mb-lg-2 c-pb-2">
									<ClayLayout.ContentCol expand>
										<p className="font-weight-semi-bold mb-0 text-2 text-secondary text-uppercase">
											{group.label}
										</p>
									</ClayLayout.ContentCol>
								</ClayLayout.ContentRow>
							)}

							<ClayLayout.Row>
								{group.items.map((item) => (
									<ClayLayout.Col key={item.id} md={4} sm={6}>
										<ClayCardWithNavigation
											cardType="navigation"
											className="c-mb-2 c-mb-lg-3"
											horizontal
											horizontalSymbol={item.leadingIcon}
											href={item.href}
											title={item.label}
										/>
									</ClayLayout.Col>
								))}
							</ClayLayout.Row>
						</ClayLayout.ContainerFluid>
					);
				})}
			</ClayLayout.Row>
		</ClayLayout.ContainerFluid>
	);
};

export default HomePageLayout;
