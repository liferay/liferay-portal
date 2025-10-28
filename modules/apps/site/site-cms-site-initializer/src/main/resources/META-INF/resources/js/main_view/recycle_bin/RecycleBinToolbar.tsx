/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import {Text} from '@clayui/core';
import {ClayDropDownWithItems} from '@clayui/drop-down';
import ClayToolbar from '@clayui/toolbar';
import {openModal} from 'frontend-js-components-web';
import React from 'react';

import Breadcrumb from '../../common/components/Breadcrumb';
import isRecycleBinRootPage from '../../common/utils/isRecycleBinRootPage';
import EmptyRecycleBinModalContent from '../modal/EmptyRecycleBinModalContent';

interface Props {
	breadcrumbItems: any[];
}

export default function RecycleBinToolbar({breadcrumbItems}: Props) {
	return isRecycleBinRootPage(breadcrumbItems) ? (
		<div>
			<ClayToolbar
				aria-label={Liferay.Language.get('recycle-bin')}
				className="border-0"
				light
				style={{height: '72px'}}
			>
				<div className="container-fluid px-4">
					<ClayToolbar.Nav>
						<ClayToolbar.Item>
							<ClayToolbar.Section>
								<div className="text-dark">
									<Text as="span" size={7} weight="semi-bold">
										{Liferay.Language.get('recycle-bin')}
									</Text>
								</div>
							</ClayToolbar.Section>
						</ClayToolbar.Item>

						<ClayToolbar.Item>
							<ClayDropDownWithItems
								items={[
									{
										label: Liferay.Language.get(
											'empty-recycle-bin'
										),
										onClick: () => {
											openModal({
												center: true,
												contentComponent: ({
													closeModal,
												}: {
													closeModal: () => void;
												}) => (
													<EmptyRecycleBinModalContent
														closeModal={closeModal}
													/>
												),
												size: 'md',
												status: 'danger',
											});
										},
										symbolLeft: 'trash',
									},
								]}
								menuWidth="shrink"
								trigger={
									<ClayButtonWithIcon
										aria-label={Liferay.Language.get(
											'more-actions'
										)}
										displayType="unstyled"
										size="xs"
										symbol="ellipsis-v"
									/>
								}
							/>
						</ClayToolbar.Item>
					</ClayToolbar.Nav>
				</div>
			</ClayToolbar>
		</div>
	) : (
		<Breadcrumb breadcrumbItems={breadcrumbItems} hideSpace={true} />
	);
}
