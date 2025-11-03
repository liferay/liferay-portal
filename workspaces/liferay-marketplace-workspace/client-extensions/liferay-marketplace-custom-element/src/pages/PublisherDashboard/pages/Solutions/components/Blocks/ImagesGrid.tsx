/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {DropzoneUpload} from '../../../../../../components/DropzoneUpload/DropzoneUpload';
import Form from '../../../../../../components/MarketplaceForm';
import {ACCEPT_FILE_TYPES} from '../../../../../../enums/File';

const ImagesGrid = () => {
	const handleUpload = (_files: File[]) => null;

	return (
		<>
			<Form.Label className="mt-1 p-4" htmlFor="description">
				Add up to 6 images
			</Form.Label>

			<div className="d-flex mb-4 px-4">
				<DropzoneUpload
					acceptFileTypes={ACCEPT_FILE_TYPES}
					buttonText="Select a file"
					description="Only gif, jpg, png are allowed. Max file size is 5MB "
					maxFiles={5}
					maxSize={5000000}
					multiple={true}
					onHandleUpload={handleUpload}
					title="Drag and drop to upload or"
				/>
			</div>
		</>
	);
};

export default ImagesGrid;
