/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLayout from '@clayui/layout';
import ClaySticker from '@clayui/sticker';
import React from 'react';

import '../../css/ApplicationsHome.scss';
import AppIcon from './components/AppIcon';
import {HomeProps} from './types';

const ApplicationsHome = ({icon, items, title}: HomeProps) => {
	return (
		<ClayLayout.ContainerFluid
			className="px-2 px-md-3 px-sm-2 px-xl-4"
			size="md"
		>
			<ClayLayout.Row className="c-mb-4 c-mb-lg-6 c-mt-4 c-mt-lg-5">
				<ClayLayout.Col>
					<div className="text-center">
						<div className="c-mb-3 c-mb-lg-2">
							<ClaySticker
								borderless
								displayType="outline"
								size="xxl"
							>
								<ClaySticker.Image alt="" src={icon} />
							</ClaySticker>
						</div>

						<h2 className="font-weight-bold text-11">{title}</h2>
					</div>
				</ClayLayout.Col>
			</ClayLayout.Row>

			{items.map((group) => {
				const hasLabel = Boolean(group.label);

				return (
					<ClayLayout.ContainerFluid
						aria-label={hasLabel ? group.label : undefined}
						className="c-mb-5"
						key={group.id}
						role={hasLabel ? 'group' : undefined}
					>
						{hasLabel && (
							<ClayLayout.ContentRow className="c-mb-1 c-mb-lg-2 c-mt-lg-2 c-pb-2 c-px-5">
								<ClayLayout.ContentCol expand>
									<p className="font-weight-semi-bold mb-0 text-2 text-secondary text-uppercase">
										{group.label}
									</p>
								</ClayLayout.ContentCol>
							</ClayLayout.ContentRow>
						)}

						<div className="applications-home-app-grid">
							{group.items.map(({id, ...item}) => (
								<AppIcon key={id} {...item} />
							))}
						</div>
					</ClayLayout.ContainerFluid>
				);
			})}
		</ClayLayout.ContainerFluid>
	);
};

export default ApplicationsHome;
