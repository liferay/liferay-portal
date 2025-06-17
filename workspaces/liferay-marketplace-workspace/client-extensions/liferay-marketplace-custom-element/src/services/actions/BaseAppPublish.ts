/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {UploadedFile} from '../../components/FileList/FileList';
import {ProductSpecificationKey} from '../../enums/Product';
import {base64ToText, fileToBase64} from '../../utils/file';
import HeadlessCommerceAdminCatalogImpl from '../rest/HeadlessCommerceAdminCatalog';

export default class BaseAppPublish {
	public static addOrUpdateImages = async (
		images: UploadedFile[],
		tag: string | null,
		product: Product,
		priorityInitialValue: number
	) => {
		let priority = priorityInitialValue;

		for (const image of images) {
			priority++;

			if (!image.changed && image.uploaded) {
				continue;
			}

			const uploadedProductImage = product?.images?.find(
				(uploadedImage) =>
					uploadedImage.externalReferenceCode === image.id
			);

			const imageMetadata = {
				...(uploadedProductImage && {
					fileEntryId: uploadedProductImage.fileEntryId,
					id: uploadedProductImage.id,
				}),
				...(image?.file && {
					attachment: base64ToText(
						(await fileToBase64(image.file)) as string
					),
				}),
				externalReferenceCode: image.id,
				galleryEnabled: true,
				neverExpire: true,
				priority,
				tags: tag ? [tag] : [],
				title: {
					en_US: image.imageDescription || image.file.name,
				},
			};

			await HeadlessCommerceAdminCatalogImpl.createProductImageByExternalReferenceCodeAxios(
				product?.externalReferenceCode,
				imageMetadata,
				(progress) => {
					image.changed = false;
					image.progress = progress;
					image.uploaded = progress === 100;
				}
			);
		}
	};

	public static async deleteReferences(externalReferenceCodes: string[]) {
		try {
			for (const externalReferenceCode of externalReferenceCodes) {
				await HeadlessCommerceAdminCatalogImpl.deleteAttachmentByExternalReferenceCode(
					externalReferenceCode
				);
			}
		}
		catch (error) {
			console.error(error);
		}
	}

	public static updateSpecification = async (
		product: Product,
		specificationKey: ProductSpecificationKey,
		value: string
	) => {
		const {productId, productSpecifications = []} = product;

		let specification: any = {};
		if (specificationKey === 'liferay-version') {
			specification = productSpecifications.find(
				(productSpecification) =>
					productSpecification.specificationKey ===
						specificationKey &&
					productSpecification.value.en_US === value
			);
		}
		else {
			specification = productSpecifications.find(
				(productSpecification) =>
					productSpecification.specificationKey === specificationKey
			);
		}

		if (
			!value?.trim() ||
			(specification && specification.value.en_US === value)
		) {

			// No need to update the specification if the value is equal
			// the previous value or empty.

			return;
		}

		const fn = specification
			? HeadlessCommerceAdminCatalogImpl.updateProductSpecification
			: HeadlessCommerceAdminCatalogImpl.createProductSpecification;

		const result = await fn(
			(specification ? specification.id : productId) as number,
			{
				specificationKey,
				value: {en_US: value},
			}
		);

		if (specification) {
			specification.value.en_US = value;

			return;
		}

		productSpecifications.push(result);
	};

	public static updateSpecifications = (
		product: Product,
		specifications: {key: ProductSpecificationKey; value: string}[]
	) =>
		Promise.allSettled(
			specifications.map((specification) =>
				this.updateSpecification(
					product,
					specification.key,
					specification.value
				)
			)
		);
}
