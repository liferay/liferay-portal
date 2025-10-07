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

import EmptyRecycleBinModalContent from '../modal/EmptyRecycleBinModalContent';

export default function RecycleBinToolbar() {
	return (
		<div>
			<ClayToolbar
				aria-label={Liferay.Language.get('recycle-bin')}
				className="border-0"
				light
			>
				<div className="container-fluid">
					<ClayToolbar.Nav>
						<ClayToolbar.Item className="text-left">
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
	);
}
