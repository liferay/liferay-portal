/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '../../../css/components/Toolbar.scss';

import ClayToolbar from '@clayui/toolbar';
import classNames from 'classnames';
import React from 'react';

type Props = {
	backURL?: string;
	children?: React.ReactNode;
	title: string;
};

function Toolbar({backURL, children, title}: Props) {
	return (
		<ClayToolbar className="bg-white cms-control-menu px-4 sticky-top top-bar">
			<div className="container-fluid">
				<ClayToolbar.Nav>
					{backURL ? (
						<ClayToolbar.Item>
							<ClayToolbar.Action
								aria-label={Liferay.Language.get('back')}
								href={backURL}
								symbol="angle-left"
								title={Liferay.Language.get('back')}
							/>
						</ClayToolbar.Item>
					) : null}

					<ClayToolbar.Item className="text-left" expand>
						<ClayToolbar.Section>
							<h1 className="font-weight-semi-bold m-0 tbar-section text-5 text-dark">
								{title}
							</h1>
						</ClayToolbar.Section>
					</ClayToolbar.Item>

					{children}
				</ClayToolbar.Nav>
			</div>
		</ClayToolbar>
	);
}

export function Item({
	children,
	className,
	expand,
}: {
	children: React.ReactNode;
	className?: string;
	expand?: boolean;
}) {
	return (
		<ClayToolbar.Item
			className={classNames('text-left', className)}
			expand={expand}
		>
			<ClayToolbar.Section>{children}</ClayToolbar.Section>
		</ClayToolbar.Item>
	);
}

Toolbar.Item = Item;

export default Toolbar;
