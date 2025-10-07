/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrontendDataSet, TOnFileDrop} from '@liferay/frontend-data-set-web';
import {openModal} from 'frontend-js-components-web';
import React from 'react';

const dummyUploader: TOnFileDrop = (droppedFiles: File[], dropTarget: any) => {
	const ModalBody = () => {
		return (
			<>
				<FrontendDataSet
					id=""
					items={droppedFiles.map((file: File) => ({
						name: file.name,
						size: file.size,
						type: file.type,
					}))}
					views={[
						{
							contentRenderer: 'table',
							default: true,
							label: 'Table',
							name: 'table',
							schema: {
								fields: [
									{
										contentRendererClientExtension: false,
										fieldName: 'name',
										label: 'Name',
									},
									{
										contentRendererClientExtension: false,
										fieldName: 'size',
										label: 'Size',
									},
									{
										contentRendererClientExtension: false,
										fieldName: 'type',
										label: 'Type',
									},
								],
							},
							thumbnail: 'table',
						},
					]}
				/>

				<div>
					{dropTarget ? (
						<span>Dropped on item {dropTarget.id}</span>
					) : (
						<span>Dropped on the FDS, no specific drop target</span>
					)}
				</div>
			</>
		);
	};

	openModal({
		bodyComponent: ModalBody,
		size: 'lg',
		title: 'Custom dummy file uploader',
	});
};

export default dummyUploader;
