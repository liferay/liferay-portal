/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm from '@clayui/form';
import ClayLayout from '@clayui/layout';
import Link from '@clayui/link';
import {sub} from 'frontend-js-web';
import React, {PropsWithChildren, useId} from 'react';
import {getImage} from '../../structure_builder/utils/getImage';

export interface NewSpaceFormSectionProps {
	description: string;
	linkLabel: string;
	linkUrl: string;
	onSubmit: (event: React.FormEvent<HTMLFormElement>) => void;
	step: 1 | 2;
	title: string;
}

export function NewSpaceFormSection({
	children,
	description,
	linkLabel,
	linkUrl,
	onSubmit,
	step,
	title,
}: PropsWithChildren<NewSpaceFormSectionProps>) {
	const logoDescriptioId = useId();

	return (
		<ClayLayout.Col className="mw-50 px-9 w-50">
			<ClayForm onSubmit={onSubmit}>
				<ClayLayout.Container className="mb-5 p-0">
					<ClayLayout.ContentRow className="align-items-center mb-6">
						<img src={getImage("cms_logo.svg")} aria-labelledby={logoDescriptioId}></img>

						<span
							className="font-weight-bold ms-3 text-7"
							id={logoDescriptioId}
						>
							{Liferay.Language.get('cms-product')}
						</span>
					</ClayLayout.ContentRow>

					<p className="mb-2 text-secondary">
						{sub(Liferay.Language.get('step-x-of-x'), [step, 2])}
					</p>

					<h1 className="font-semibold mb-4 text-7">{title}</h1>

					<p className="mb-2 text-5 text-secondary">{description}</p>

					<Link
						className="font-weight-bold text-4 text-underline"
						href={linkUrl}
					>
						{linkLabel}
					</Link>
				</ClayLayout.Container>

				{children}
			</ClayForm>
		</ClayLayout.Col>
	);
}
