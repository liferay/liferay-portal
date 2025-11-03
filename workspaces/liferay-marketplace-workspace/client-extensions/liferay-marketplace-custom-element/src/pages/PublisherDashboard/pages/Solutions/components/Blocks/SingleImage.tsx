/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {DropzoneUpload} from '../../../../../../components/DropzoneUpload/DropzoneUpload';
import {ACCEPT_FILE_TYPES} from '../../../../../../enums/File';
import {MAX_SIZE_5MBS} from '../../constants';

const SingleImage = () => {
	const handleUpload = (_files: File[]) => null;

	return (
		<div className="d-flex p-4">
			<DropzoneUpload
				acceptFileTypes={ACCEPT_FILE_TYPES}
				buttonText="Select a file"
				description="Only gif, jpg, png are allowed. Max file size is 5MB "
				maxFiles={5}
				maxSize={MAX_SIZE_5MBS}
				multiple={true}
				onHandleUpload={handleUpload}
				title="Drag and drop to upload or"
			/>
		</div>
	);
};

export default SingleImage;
