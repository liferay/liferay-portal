/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import {ClayTooltipProvider} from '@clayui/tooltip';
import React from 'react';

interface IProps {
	children: any;
	className?: string;
	helpMessage?: string;
	icon?: string;
	title?: string;
	url?: string;
}

const AnalyticsFrame = React.forwardRef(
	(
		{children, className = '', helpMessage, icon, title, url}: IProps,
		ref: React.ForwardedRef<HTMLElement>
	) => {
		return (
			<section
				className={`analytics-frame rounded-lg ${className}`.trim()}
				ref={ref}
			>
				{helpMessage || icon || title || url ? (
					<header className="align-items-center border-bottom d-flex justify-content-between p-3">
						<span className="align-items-center d-flex">
							{icon && (
								<span className="analytics-frame-icon btn-monospaced rounded-lg">
									<ClayIcon symbol={icon} />
								</span>
							)}

							<span className="font-weight-semi-bold px-2">
								{title}
							</span>

							{helpMessage && (
								<ClayTooltipProvider>
									<ClayButtonWithIcon
										data-tooltip-align="top"
										displayType="unstyled"
										symbol="question-circle"
										title={helpMessage}
									/>
								</ClayTooltipProvider>
							)}
						</span>

						{url && (
							<ClayLink
								className="btn btn-link font-weight-semi-bold"
								href={url}
								title={Liferay.Language.get('view-all')}
							>
								{Liferay.Language.get('view-all')}
							</ClayLink>
						)}
					</header>
				) : null}

				<div>{children}</div>
			</section>
		);
	}
);

export default AnalyticsFrame;
