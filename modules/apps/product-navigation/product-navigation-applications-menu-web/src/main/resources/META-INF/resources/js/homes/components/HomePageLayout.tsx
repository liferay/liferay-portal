/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLayout from '@clayui/layout';
import ClaySticker from '@clayui/sticker';
import classNames from 'classnames';
import React from 'react';

import {useWindowSize} from '../utils/useWindowSize';

type Props = {
	children: React.ReactNode;
	logo: string;
	title: string;
	titleContralized?: boolean;
};

const LARGE_BREAKPOINT = 992;

const HomePageLayout = ({
	children,
	logo,
	title,
	titleContralized = false,
}: Props) => {
	const {width} = useWindowSize();

	const isLargeBreakpoint = width >= LARGE_BREAKPOINT;

	return (
		<ClayLayout.ContainerFluid
			className="px-2 px-md-3 px-sm-2 px-xl-4"
			view
		>
			<ClayLayout.Row className="my-5">
				<ClayLayout.Col>
					<div
						className={classNames(
							'font-family-source-sans-pro font-weight-bold text-truncate',
							{
								'd-flex align-items-center text-left':
									!titleContralized,
								'text-center': titleContralized,
							}
						)}
					>
						<div
							className={classNames({
								'mb-3': titleContralized,
								'mr-3': !titleContralized,
							})}
						>
							<ClaySticker
								className="border-0"
								displayType="outline"
								size={isLargeBreakpoint ? 'xxl' : 'xl'}
							>
								<ClaySticker.Image
									alt={`${title} logo`}
									src={logo}
									style={{
										height: isLargeBreakpoint
											? '40px'
											: '22px',
										width: isLargeBreakpoint
											? '40px'
											: '22px',
									}}
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

			<ClayLayout.Row className="mb-7">{children}</ClayLayout.Row>
		</ClayLayout.ContainerFluid>
	);
};

export default HomePageLayout;
